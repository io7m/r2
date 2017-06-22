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

package com.io7m.r2.filters;

import com.io7m.jaffirm.core.Preconditions;
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
import com.io7m.jregions.core.unparameterized.sizes.AreaSizesL;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2RenderTargetDescriptionType;
import com.io7m.r2.core.R2RenderTargetDescriptions;
import com.io7m.r2.core.R2RenderTargetPoolUsableType;
import com.io7m.r2.core.R2RenderTargetUsableType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2UnitQuadUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersFilterMutable;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.EnumSet;
import java.util.Objects;
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

public final class R2FilterBoxBlur<
  SD extends R2RenderTargetDescriptionType,
  S extends R2RenderTargetUsableType<SD>,
  DD extends R2RenderTargetDescriptionType,
  D extends R2RenderTargetUsableType<DD>>
  implements R2FilterBoxBlurType<SD, S, DD, D>
{
  private static final Set<JCGLFramebufferBlitBuffer> BLIT_BUFFERS;

  static {
    BLIT_BUFFERS = EnumSet.of(
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_COLOR);
  }

  private final R2ShaderFilterType<R2ShaderFilterBoxBlurParameters> shader_blur_h;
  private final R2ShaderFilterType<R2ShaderFilterBoxBlurParameters> shader_blur_v;
  private final JCGLInterfaceGL33Type g;
  private final R2RenderTargetPoolUsableType<DD, D> render_target_pool;
  private final R2UnitQuadUsableType quad;
  private final JCGLRenderState render_state;
  private final R2ShaderParametersFilterMutable<R2ShaderFilterBoxBlurParameters> blur_values;

  private R2FilterBoxBlur(
    final JCGLInterfaceGL33Type in_g,
    final R2RenderTargetPoolUsableType<DD, D> in_rtp_pool,
    final R2UnitQuadUsableType in_quad,
    final R2ShaderFilterType<R2ShaderFilterBoxBlurParameters> in_shader_blur_h,
    final R2ShaderFilterType<R2ShaderFilterBoxBlurParameters> in_shader_blur_v)
  {
    this.g =
      NullCheck.notNull(in_g, "G33");
    this.shader_blur_h =
      NullCheck.notNull(in_shader_blur_h, "Shader Blur H");
    this.shader_blur_v =
      NullCheck.notNull(in_shader_blur_v, "Shader Blur V");
    this.render_target_pool =
      NullCheck.notNull(in_rtp_pool, "Pool");
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
  R2FilterBoxBlurType<SD, S, DD, D>
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
    final R2ShaderFilterType<R2ShaderFilterBoxBlurParameters> s_blur_h =
      R2ShaderFilterBoxBlurHorizontal4f.create(
        g_sh, in_shader_env, in_id_pool);
    final R2ShaderFilterType<R2ShaderFilterBoxBlurParameters> s_blur_v =
      R2ShaderFilterBoxBlurVertical4f.create(
        g_sh, in_shader_env, in_id_pool);

    return new R2FilterBoxBlur<>(
      in_g, in_rtp_pool, in_quad, s_blur_h, s_blur_v);
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

  private void runSimpleScale(
    final JCGLProfilingContextType pc_base,
    final JCGLTextureUnitContextParentType uc,
    final R2FilterBoxBlurParameters<SD, S, DD, D> parameters)
  {
    NullCheck.notNull(pc_base, "Profiling");
    NullCheck.notNull(uc, "Texture context");
    NullCheck.notNull(parameters, "Filter parameters");

    final JCGLProfilingContextType pc_scale = pc_base.childContext("scale");

    pc_scale.startMeasuringIfEnabled();
    try {
      final JCGLFramebuffersType g_fb = this.g.framebuffers();

      final S source = parameters.sourceRenderTarget();
      final D destination = parameters.outputRenderTarget();

      final DD desc_orig = destination.description();
      final R2BlurParameters blur_params = parameters.blurParameters();

      final DD desc_scaled =
        R2RenderTargetDescriptions.scale(
          desc_orig,
          parameters.outputDescriptionScaler(),
          blur_params.blurScale());

      final D destination_scaled =
        this.render_target_pool.get(uc, desc_scaled);

      try {
        g_fb.framebufferReadBind(source.primaryFramebuffer());
        g_fb.framebufferDrawBind(destination_scaled.primaryFramebuffer());
        g_fb.framebufferBlit(
          source.sizeAsViewport(),
          destination_scaled.sizeAsViewport(),
          BLIT_BUFFERS,
          blur_params.blurScaleFilter());

        g_fb.framebufferReadBind(destination_scaled.primaryFramebuffer());
        g_fb.framebufferDrawBind(destination.primaryFramebuffer());
        g_fb.framebufferBlit(
          destination_scaled.sizeAsViewport(),
          destination.sizeAsViewport(),
          BLIT_BUFFERS,
          blur_params.blurScaleFilter());

      } finally {
        this.render_target_pool.returnValue(uc, destination_scaled);
        g_fb.framebufferReadUnbind();
      }
    } finally {
      pc_scale.stopMeasuringIfEnabled();
    }
  }

  private void runSimpleCopy(
    final JCGLProfilingContextType pc_base,
    final R2FilterBoxBlurParameters<SD, S, DD, D> parameters)
  {
    NullCheck.notNull(pc_base, "Profiling");
    NullCheck.notNull(parameters, "Parameters");

    final JCGLFramebuffersType g_fb = this.g.framebuffers();
    final JCGLProfilingContextType pc_copy = pc_base.childContext("copy");

    pc_copy.startMeasuringIfEnabled();
    try {
      final S source =
        parameters.sourceRenderTarget();
      final D destination =
        parameters.outputRenderTarget();

      /*
       * The output framebuffer is bound here even though it may not be
       * necessary. The reason for this is that filters are supposed to have
       * consistent binding semantics: If a framebuffer is specified as an output,
       * that framebuffer will be bound when the filter has finished executing.
       */

      g_fb.framebufferDrawBind(destination.primaryFramebuffer());

      /*
       * No point copying something to itself...
       */

      if (Objects.equals(source, destination)) {
        return;
      }

      try {
        g_fb.framebufferReadBind(source.primaryFramebuffer());
        g_fb.framebufferBlit(
          source.sizeAsViewport(),
          destination.sizeAsViewport(),
          BLIT_BUFFERS,
          parameters.blurParameters().blurScaleFilter());
      } finally {
        g_fb.framebufferReadUnbind();
      }
    } finally {
      pc_copy.stopMeasuringIfEnabled();
    }
  }

  @Override
  public void runFilter(
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType uc,
    final R2FilterBoxBlurParameters<SD, S, DD, D> parameters)
  {
    NullCheck.notNull(pc, "Profiling");
    NullCheck.notNull(uc, "Texture context");
    NullCheck.notNull(parameters, "Filter parameters");

    Preconditions.checkPrecondition(
      !this.isDeleted(), "Filter must not be deleted");

    final JCGLProfilingContextType pc_base = pc.childContext("box-blur");

    final R2BlurParameters blur_params = parameters.blurParameters();
    final int blur_passes = blur_params.blurPasses();
    if (blur_passes == 0) {
      this.runSimpleCopy(pc_base, parameters);
      return;
    }

    Preconditions.checkPreconditionI(
      blur_passes,
      blur_passes > 0,
      p -> "Blur passes must be positive");

    final double blur_size = blur_params.blurSize();
    if (blur_size == 0.0) {
      this.runSimpleScale(pc_base, uc, parameters);
      return;
    }

    Preconditions.checkPreconditionI(
      blur_passes,
      blur_passes > 0,
      p -> "Blur passes must be positive");

    Preconditions.checkPreconditionD(
      blur_size,
      blur_size > 0.0,
      p -> "Blur size must be positive");

    this.runBlur(pc_base, uc, parameters);
  }

  private void runBlur(
    final JCGLProfilingContextType pc_base,
    final JCGLTextureUnitContextParentType uc,
    final R2FilterBoxBlurParameters<SD, S, DD, D> parameters)
  {
    NullCheck.notNull(pc_base, "Profiling");
    NullCheck.notNull(uc, "Texture context");
    NullCheck.notNull(parameters, "Filter parameters");

    final JCGLProfilingContextType pc_blur = pc_base.childContext("blur");
    pc_blur.startMeasuringIfEnabled();

    try {
      final S source =
        parameters.sourceRenderTarget();
      final D destination =
        parameters.outputRenderTarget();

      final DD desc_scaled =
        R2RenderTargetDescriptions.scale(
          destination.description(),
          parameters.outputDescriptionScaler(),
          parameters.blurParameters().blurScale());

      final D temporary_a =
        this.render_target_pool.get(uc, desc_scaled);

      try {

        final D temporary_b =
          this.render_target_pool.get(uc, desc_scaled);

        try {

          /*
           * Copy the contents of the source to TA.
           */

          final JCGLFramebuffersType g_fb = this.g.framebuffers();
          g_fb.framebufferReadBind(source.primaryFramebuffer());
          g_fb.framebufferDrawBind(temporary_a.primaryFramebuffer());
          final R2BlurParameters blur_params = parameters.blurParameters();
          g_fb.framebufferBlit(
            source.sizeAsViewport(),
            temporary_a.sizeAsViewport(),
            BLIT_BUFFERS,
            blur_params.blurScaleFilter());

          /*
           * Now repeatedly blur TA → TB, TB → TA.
           */

          for (int pass = 0; pass < blur_params.blurPasses(); ++pass) {
            this.evaluateBlurH(
              uc,
              blur_params,
              parameters.outputTextureSelector().apply(temporary_a),
              temporary_b.sizeAsViewport(),
              temporary_b.primaryFramebuffer());
            this.evaluateBlurV(
              uc,
              blur_params,
              parameters.outputTextureSelector().apply(temporary_b),
              temporary_a.sizeAsViewport(),
              temporary_a.primaryFramebuffer());
          }

          /*
           * Now, copy TA → Output.
           */

          g_fb.framebufferReadBind(temporary_a.primaryFramebuffer());
          g_fb.framebufferDrawBind(destination.primaryFramebuffer());
          g_fb.framebufferBlit(
            temporary_a.sizeAsViewport(),
            destination.sizeAsViewport(),
            BLIT_BUFFERS,
            blur_params.blurScaleFilter());
          g_fb.framebufferReadUnbind();

        } finally {
          this.render_target_pool.returnValue(uc, temporary_b);
        }
      } finally {
        this.render_target_pool.returnValue(uc, temporary_a);
      }
    } finally {
      pc_blur.stopMeasuringIfEnabled();
    }
  }

  private void evaluateBlurH(
    final JCGLTextureUnitContextParentType uc,
    final R2BlurParameters parameters,
    final R2Texture2DUsableType source_texture,
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
        this.shader_blur_h.onActivate(this.g);

        final AreaL source_area =
          AreaSizesL.area(source_texture.texture().size());

        this.blur_values.setTextureUnitContext(tc);
        this.blur_values.setValues(
          R2ShaderFilterBoxBlurParameters.builder()
            .setTexture(source_texture)
            .setBlurRadius((double) source_area.sizeX() / parameters.blurSize())
            .build());

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
    final R2BlurParameters parameters,
    final R2Texture2DUsableType source_texture,
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
        this.shader_blur_v.onActivate(this.g);

        final AreaL source_area =
          AreaSizesL.area(source_texture.texture().size());

        this.blur_values.setTextureUnitContext(tc);
        this.blur_values.setValues(
          R2ShaderFilterBoxBlurParameters.builder()
            .setTexture(source_texture)
            .setBlurRadius((double) source_area.sizeY() / parameters.blurSize())
            .build());

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
