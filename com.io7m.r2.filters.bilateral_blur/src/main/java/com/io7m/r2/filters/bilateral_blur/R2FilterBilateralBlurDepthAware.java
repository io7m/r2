/*
 * Copyright © 2016 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.r2.filters.bilateral_blur;

import com.io7m.jcanephora.core.JCGLFramebufferBlitBuffer;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.core.api.JCGLViewportsType;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.renderstate.JCGLRenderState;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jnull.NullCheck;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.r2.core.api.R2Exception;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.filters.api.R2FilterType;
import com.io7m.r2.filters.bilateral_blur.api.R2BilateralBlurParameters;
import com.io7m.r2.filters.bilateral_blur.api.R2FilterBilateralBlurDepthAwareParameters;
import com.io7m.r2.matrices.R2MatricesObserverValuesType;
import com.io7m.r2.projections.R2Projections;
import com.io7m.r2.rendering.targets.R2RenderTargetDescriptionType;
import com.io7m.r2.rendering.targets.R2RenderTargetDescriptions;
import com.io7m.r2.rendering.targets.R2RenderTargetPoolUsableType;
import com.io7m.r2.rendering.targets.R2RenderTargetUsableType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.bilateral_blur.R2ShaderFilterBilateralBlurDepthAwareHorizontal4f;
import com.io7m.r2.shaders.bilateral_blur.R2ShaderFilterBilateralBlurDepthAwareParameters;
import com.io7m.r2.shaders.bilateral_blur.R2ShaderFilterBilateralBlurDepthAwareVertical4f;
import com.io7m.r2.shaders.filter.api.R2ShaderFilterType;
import com.io7m.r2.shaders.filter.api.R2ShaderParametersFilterMutable;
import com.io7m.r2.textures.R2Texture2DUsableType;
import com.io7m.r2.textures.R2TextureDefaultsType;
import com.io7m.r2.unit_quads.R2UnitQuadUsableType;

import java.util.EnumSet;
import java.util.Set;

/**
 * <p>A generic box blur filter.</p>
 *
 * <p>The filter blurs render targets of type {@code S}, writing the blurred
 * results to render targets of type {@code T}.</p>
 *
 * @param <SD> The type of source render target descriptions
 * @param <S>  The type of source render targets
 * @param <DD> The type of destination render target descriptions
 * @param <D>  The type of destination render targets
 */

public final class R2FilterBilateralBlurDepthAware<
  SD extends R2RenderTargetDescriptionType,
  S extends R2RenderTargetUsableType<SD>,
  DD extends R2RenderTargetDescriptionType,
  D extends R2RenderTargetUsableType<DD>>
  implements R2FilterType<R2FilterBilateralBlurDepthAwareParameters<SD, S, DD, D>>
{
  private static final Set<JCGLFramebufferBlitBuffer> BLIT_BUFFERS;

  static {
    BLIT_BUFFERS = EnumSet.of(
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_COLOR);
  }

  private final R2ShaderFilterType<R2ShaderFilterBilateralBlurDepthAwareParameters> shader_blur_h;
  private final R2ShaderFilterType<R2ShaderFilterBilateralBlurDepthAwareParameters> shader_blur_v;
  private final JCGLInterfaceGL33Type g;
  private final R2RenderTargetPoolUsableType<DD, D> render_target_pool;
  private final R2UnitQuadUsableType quad;
  private final JCGLRenderState render_state;
  private final R2ShaderParametersFilterMutable<R2ShaderFilterBilateralBlurDepthAwareParameters> blur_values;

  private R2FilterBilateralBlurDepthAware(
    final JCGLInterfaceGL33Type in_g,
    final R2RenderTargetPoolUsableType<DD, D> in_rtp_pool,
    final R2UnitQuadUsableType in_quad,
    final R2ShaderFilterType<R2ShaderFilterBilateralBlurDepthAwareParameters> in_shader_blur_h,
    final R2ShaderFilterType<R2ShaderFilterBilateralBlurDepthAwareParameters> in_shader_blur_v)
  {
    this.g =
      NullCheck.notNull(in_g, "G33");
    this.shader_blur_h =
      NullCheck.notNull(in_shader_blur_h, "Shader Blur H");
    this.shader_blur_v =
      NullCheck.notNull(in_shader_blur_v, "Shader Blur V");
    this.render_target_pool =
      NullCheck.notNull(in_rtp_pool, "Render target pool");
    this.quad =
      NullCheck.notNull(in_quad, "Quad");
    this.render_state = JCGLRenderState.builder().build();
    this.blur_values = R2ShaderParametersFilterMutable.create();
  }

  /**
   * Construct a new filter.
   *
   * @param in_shader_env   Shader sources
   * @param in_g            A GL interface
   * @param in_tex_defaults The set of default textures
   * @param in_rtp_pool     A render target pool
   * @param in_id_pool      An ID pool
   * @param in_quad         A unit quad
   * @param <SD>            The type of source render target descriptions
   * @param <S>             The type of source render targets
   * @param <DD>            The type of destination render target descriptions
   * @param <D>             The type of destination render targets
   *
   * @return A new filter
   */

  public static <
    SD extends R2RenderTargetDescriptionType,
    S extends R2RenderTargetUsableType<SD>,
    DD extends R2RenderTargetDescriptionType,
    D extends R2RenderTargetUsableType<DD>>
  R2FilterType<R2FilterBilateralBlurDepthAwareParameters<SD, S, DD, D>>
  newFilter(
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final JCGLInterfaceGL33Type in_g,
    final R2TextureDefaultsType in_tex_defaults,
    final R2RenderTargetPoolUsableType<DD, D> in_rtp_pool,
    final R2IDPoolType in_id_pool,
    final R2UnitQuadUsableType in_quad)
  {
    NullCheck.notNull(in_shader_env, "Shader environment");
    NullCheck.notNull(in_g, "G33");
    NullCheck.notNull(in_tex_defaults, "Texture defaults");
    NullCheck.notNull(in_id_pool, "ID Pool");
    NullCheck.notNull(in_rtp_pool, "Render target pool");
    NullCheck.notNull(in_quad, "Quad");

    final JCGLShadersType g_sh = in_g.shaders();
    final R2ShaderFilterBilateralBlurDepthAwareHorizontal4f s_blur_h =
      R2ShaderFilterBilateralBlurDepthAwareHorizontal4f.check(
        g_sh, in_shader_env, in_id_pool);
    final R2ShaderFilterBilateralBlurDepthAwareVertical4f s_blur_v =
      R2ShaderFilterBilateralBlurDepthAwareVertical4f.create(
        g_sh, in_shader_env, in_id_pool);

    return new R2FilterBilateralBlurDepthAware<>(
      in_g,
      in_rtp_pool,
      in_quad,
      s_blur_h,
      s_blur_v);
  }

  private static <
    SD extends R2RenderTargetDescriptionType,
    S extends R2RenderTargetUsableType<SD>,
    DD extends R2RenderTargetDescriptionType,
    D extends R2RenderTargetUsableType<DD>>
  R2ShaderFilterBilateralBlurDepthAwareParameters createShaderParams(
    final R2FilterBilateralBlurDepthAwareParameters<SD, S, DD, D> parameters,
    final R2Texture2DUsableType source_value_texture,
    final R2Texture2DUsableType source_depth_texture)
  {
    final AreaSizeL source_area =
      source_value_texture.texture().size();
    final R2MatricesObserverValuesType m_observer =
      parameters.sceneObserverValues();
    final R2BilateralBlurParameters blur_params =
      parameters.blurParameters();

    final R2ShaderFilterBilateralBlurDepthAwareParameters.Builder pb =
      R2ShaderFilterBilateralBlurDepthAwareParameters.builder();

    {
      final double radius = blur_params.blurSize();
      final double sigma = (radius + 1.0) / 2.0;
      final double inv_sigma2 = 1.0 / (2.0 * sigma * sigma);
      pb.setBlurFalloff(inv_sigma2);
    }

    pb.setBlurOutputInverseWidth(1.0 / (double) source_area.sizeX());
    pb.setBlurOutputInverseHeight(1.0 / (double) source_area.sizeY());
    pb.setBlurRadius(blur_params.blurSize());
    pb.setBlurSharpness(blur_params.blurSharpness());
    pb.setDepthCoefficient(
      R2Projections.getDepthCoefficient(m_observer.projection()));
    pb.setDepthTexture(source_depth_texture);
    pb.setImageTexture(source_value_texture);
    pb.setViewMatrices(m_observer);
    return pb.build();
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type gx)
    throws R2Exception
  {
    if (!this.isDeleted()) {
      this.shader_blur_h.delete(gx);
      this.shader_blur_v.delete(gx);
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.shader_blur_h.isDeleted();
  }

  @Override
  public void runFilter(
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType uc,
    final R2FilterBilateralBlurDepthAwareParameters<SD, S, DD, D> parameters)
  {
    NullCheck.notNull(pc, "Profiling");
    NullCheck.notNull(uc, "Texture context");
    NullCheck.notNull(parameters, "Filter parameters");

    final JCGLProfilingContextType pc_base =
      pc.childContext("bilateral-depth-aware-blur");
    final JCGLProfilingContextType pc_copy_in =
      pc_base.childContext("copy-in");
    final JCGLProfilingContextType pc_copy_out =
      pc_base.childContext("copy-out");
    final JCGLProfilingContextType pc_blur =
      pc_base.childContext("blur");

    final S source =
      parameters.sourceRenderTarget();
    final D destination =
      parameters.outputRenderTarget();

    final R2BilateralBlurParameters blur_params =
      parameters.blurParameters();

    final DD desc_scaled = R2RenderTargetDescriptions.scale(
      destination.description(),
      parameters.outputDescriptionScaler(),
      blur_params.blurScale());

    final D temporary_a =
      this.render_target_pool.get(uc, desc_scaled);

    try {

      final D temporary_b =
        this.render_target_pool.get(uc, desc_scaled);

      try {
        final JCGLFramebuffersType g_fb = this.g.framebuffers();

        /*
         * Copy the contents of the source to TA.
         */

        pc_copy_in.startMeasuringIfEnabled();
        try {
          g_fb.framebufferReadBind(source.primaryFramebuffer());
          g_fb.framebufferDrawBind(temporary_a.primaryFramebuffer());
          g_fb.framebufferBlit(
            source.sizeAsViewport(),
            temporary_a.sizeAsViewport(),
            BLIT_BUFFERS,
            blur_params.blurScaleFilter());
          g_fb.framebufferReadUnbind();
        } finally {
          pc_copy_in.stopMeasuringIfEnabled();
        }

        /*
         * Now repeatedly blur TA → TB, TB → TA.
         */

        pc_blur.startMeasuringIfEnabled();
        try {
          final R2Texture2DUsableType depth = parameters.depthTexture();
          for (int pass = 0; pass < blur_params.blurPasses(); ++pass) {
            this.evaluateBlurH(
              uc,
              parameters,
              parameters.outputTextureSelector().apply(temporary_a),
              depth,
              temporary_b.sizeAsViewport(),
              temporary_b.primaryFramebuffer());
            this.evaluateBlurV(
              uc,
              parameters,
              parameters.outputTextureSelector().apply(temporary_b),
              depth,
              temporary_a.sizeAsViewport(),
              temporary_a.primaryFramebuffer());
          }
        } finally {
          pc_blur.stopMeasuringIfEnabled();
        }

        /*
         * Now, copy TA → Output.
         */

        pc_copy_out.startMeasuringIfEnabled();
        try {
          g_fb.framebufferReadBind(temporary_a.primaryFramebuffer());
          g_fb.framebufferDrawBind(destination.primaryFramebuffer());
          g_fb.framebufferBlit(
            temporary_a.sizeAsViewport(),
            destination.sizeAsViewport(),
            BLIT_BUFFERS,
            blur_params.blurScaleFilter());
          g_fb.framebufferReadUnbind();
        } finally {
          pc_copy_out.stopMeasuringIfEnabled();
        }

      } finally {
        this.render_target_pool.returnValue(uc, temporary_b);
      }
    } finally {
      this.render_target_pool.returnValue(uc, temporary_a);
    }
  }

  private void evaluateBlurH(
    final JCGLTextureUnitContextParentType uc,
    final R2FilterBilateralBlurDepthAwareParameters<SD, S, DD, D> parameters,
    final R2Texture2DUsableType source_value_texture,
    final R2Texture2DUsableType source_depth_texture,
    final AreaL target_area,
    final JCGLFramebufferUsableType target_fb)
  {
    final JCGLTexturesType g_tex = this.g.textures();
    final JCGLDrawType g_dr = this.g.drawing();
    final JCGLArrayObjectsType g_ao = this.g.arrayObjects();
    final JCGLViewportsType g_v = this.g.viewports();
    final JCGLFramebuffersType g_fb = this.g.framebuffers();

    final JCGLTextureUnitContextType tc = uc.unitContextNew();

    try {
      g_fb.framebufferDrawBind(target_fb);
      JCGLRenderStates.activate(this.g, this.render_state);
      g_v.viewportSet(target_area);

      try {
        this.blur_values.setValues(createShaderParams(
          parameters, source_value_texture, source_depth_texture));
        this.blur_values.setTextureUnitContext(tc);

        this.shader_blur_h.onActivate(this.g);
        this.shader_blur_h.onReceiveFilterValues(this.g, this.blur_values);
        this.shader_blur_h.onValidate();

        g_ao.arrayObjectBind(this.quad.arrayObject());
        g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
      } finally {
        g_ao.arrayObjectUnbind();
        this.shader_blur_h.onDeactivate(this.g);
      }
    } finally {
      tc.unitContextFinish(g_tex);
    }
  }

  private void evaluateBlurV(
    final JCGLTextureUnitContextParentType uc,
    final R2FilterBilateralBlurDepthAwareParameters<SD, S, DD, D> parameters,
    final R2Texture2DUsableType source_value_texture,
    final R2Texture2DUsableType source_depth_texture,
    final AreaL target_area,
    final JCGLFramebufferUsableType target_fb)
  {
    final JCGLTexturesType g_tex = this.g.textures();
    final JCGLDrawType g_dr = this.g.drawing();
    final JCGLArrayObjectsType g_ao = this.g.arrayObjects();
    final JCGLViewportsType g_v = this.g.viewports();
    final JCGLFramebuffersType g_fb = this.g.framebuffers();

    final JCGLTextureUnitContextType tc = uc.unitContextNew();

    try {
      g_fb.framebufferDrawBind(target_fb);
      JCGLRenderStates.activate(this.g, this.render_state);
      g_v.viewportSet(target_area);

      try {
        this.blur_values.setValues(createShaderParams(
          parameters, source_value_texture, source_depth_texture));
        this.blur_values.setTextureUnitContext(tc);

        this.shader_blur_v.onActivate(this.g);
        this.shader_blur_v.onReceiveFilterValues(this.g, this.blur_values);
        this.shader_blur_v.onValidate();

        g_ao.arrayObjectBind(this.quad.arrayObject());
        g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
      } finally {
        g_ao.arrayObjectUnbind();
        this.shader_blur_v.onDeactivate(this.g);
      }
    } finally {
      tc.unitContextFinish(g_tex);
    }
  }

}
