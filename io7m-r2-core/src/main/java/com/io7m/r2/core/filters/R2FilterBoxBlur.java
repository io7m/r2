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

package com.io7m.r2.core.filters;

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.JCGLFramebufferBlitBuffer;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLBlendingType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.core.api.JCGLViewportsType;
import com.io7m.jcanephora.renderstate.JCGLRenderStateMutable;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jnull.NullCheck;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2RenderTargetDescriptionType;
import com.io7m.r2.core.R2RenderTargetDescriptions;
import com.io7m.r2.core.R2RenderTargetPoolUsableType;
import com.io7m.r2.core.R2RenderTargetUsableType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2TextureUnitContextType;
import com.io7m.r2.core.R2UnitQuadUsableType;
import com.io7m.r2.core.shaders.provided.R2ShaderFilterBoxBlurHorizontal4f;
import com.io7m.r2.core.shaders.provided.R2ShaderFilterBoxBlurParametersMutable;
import com.io7m.r2.core.shaders.provided.R2ShaderFilterBoxBlurVertical4f;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;
import org.valid4j.Assertive;

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
  implements R2FilterType<R2FilterBoxBlurParameters<SD, S, DD, D>>
{
  private static final Set<JCGLFramebufferBlitBuffer> BLIT_BUFFERS;

  static {
    BLIT_BUFFERS = EnumSet.of(
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_COLOR);
  }

  private final R2ShaderFilterBoxBlurHorizontal4f      shader_blur_h;
  private final R2ShaderFilterBoxBlurVertical4f        shader_blur_v;
  private final JCGLInterfaceGL33Type                  g;
  private final R2RenderTargetPoolUsableType<DD, D>    render_target_pool;
  private final R2UnitQuadUsableType                   quad;
  private final R2ShaderFilterBoxBlurParametersMutable shader_params;
  private final JCGLRenderStateMutable                 render_state;

  private R2FilterBoxBlur(
    final JCGLInterfaceGL33Type in_g,
    final R2RenderTargetPoolUsableType<DD, D> in_rtp_pool,
    final R2UnitQuadUsableType in_quad,
    final R2ShaderFilterBoxBlurHorizontal4f in_shader_blur_h,
    final R2ShaderFilterBoxBlurVertical4f in_shader_blur_v)
  {
    this.g =
      NullCheck.notNull(in_g);
    this.shader_blur_h =
      NullCheck.notNull(in_shader_blur_h);
    this.shader_blur_v =
      NullCheck.notNull(in_shader_blur_v);
    this.render_target_pool =
      NullCheck.notNull(in_rtp_pool);
    this.quad =
      NullCheck.notNull(in_quad);
    this.shader_params =
      R2ShaderFilterBoxBlurParametersMutable.create();
    this.render_state =
      JCGLRenderStateMutable.create();
  }

  /**
   * Construct a new filter.
   *
   * @param in_sources      Shader sources
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
  R2FilterType<R2FilterBoxBlurParameters<SD, S, DD, D>>
  newFilter(
    final R2ShaderSourcesType in_sources,
    final JCGLInterfaceGL33Type in_g,
    final R2TextureDefaultsType in_tex_defaults,
    final R2RenderTargetPoolUsableType<DD, D> in_rtp_pool,
    final R2IDPoolType in_id_pool,
    final R2UnitQuadUsableType in_quad)
  {
    NullCheck.notNull(in_sources);
    NullCheck.notNull(in_g);
    NullCheck.notNull(in_tex_defaults);
    NullCheck.notNull(in_id_pool);
    NullCheck.notNull(in_rtp_pool);
    NullCheck.notNull(in_quad);

    final JCGLShadersType g_sh = in_g.getShaders();
    final R2ShaderFilterBoxBlurHorizontal4f s_blur_h =
      R2ShaderFilterBoxBlurHorizontal4f.newShader(g_sh, in_sources, in_id_pool);
    final R2ShaderFilterBoxBlurVertical4f s_blur_v =
      R2ShaderFilterBoxBlurVertical4f.newShader(g_sh, in_sources, in_id_pool);

    return new R2FilterBoxBlur<>(
      in_g,
      in_rtp_pool,
      in_quad,
      s_blur_h,
      s_blur_v);
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
    final R2TextureUnitContextParentType uc,
    final R2FilterBoxBlurParameters<SD, S, DD, D> parameters)
  {
    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();

    final S source =
      parameters.getSourceRenderTarget();
    final D destination =
      parameters.getOutputRenderTarget();

    final DD desc_orig =
      destination.getDescription();
    final DD desc_scaled =
      R2RenderTargetDescriptions.scale(
        desc_orig, (double) parameters.getBlurScale());

    final D destination_scaled =
      this.render_target_pool.get(uc, desc_scaled);

    try {
      g_fb.framebufferReadBind(source.getPrimaryFramebuffer());
      g_fb.framebufferDrawBind(destination_scaled.getPrimaryFramebuffer());
      g_fb.framebufferBlit(
        source.getArea(),
        destination_scaled.getArea(),
        R2FilterBoxBlur.BLIT_BUFFERS,
        parameters.getBlurScaleFilter());
      g_fb.framebufferReadUnbind();
      g_fb.framebufferDrawUnbind();

      g_fb.framebufferReadBind(destination_scaled.getPrimaryFramebuffer());
      g_fb.framebufferDrawBind(destination.getPrimaryFramebuffer());
      g_fb.framebufferBlit(
        destination_scaled.getArea(),
        destination.getArea(),
        R2FilterBoxBlur.BLIT_BUFFERS,
        parameters.getBlurScaleFilter());

    } finally {
      this.render_target_pool.returnValue(uc, destination_scaled);
      g_fb.framebufferReadUnbind();
      g_fb.framebufferDrawUnbind();
    }
  }

  private void runSimpleCopy(
    final R2TextureUnitContextParentType uc,
    final R2FilterBoxBlurParameters<SD, S, DD, D> parameters)
  {
    final S source =
      parameters.getSourceRenderTarget();
    final D destination =
      parameters.getOutputRenderTarget();

    /**
     * No point copying something to itself...
     */

    if (Objects.equals(source, destination)) {
      return;
    }

    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();

    try {
      g_fb.framebufferReadBind(source.getPrimaryFramebuffer());
      g_fb.framebufferDrawBind(destination.getPrimaryFramebuffer());
      g_fb.framebufferBlit(
        source.getArea(),
        destination.getArea(),
        R2FilterBoxBlur.BLIT_BUFFERS,
        parameters.getBlurScaleFilter());
    } finally {
      g_fb.framebufferDrawUnbind();
      g_fb.framebufferReadUnbind();
    }
  }

  @Override
  public void runFilter(
    final R2TextureUnitContextParentType uc,
    final R2FilterBoxBlurParameters<SD, S, DD, D> parameters)
  {
    NullCheck.notNull(uc);
    NullCheck.notNull(parameters);

    final int blur_passes = parameters.getBlurPasses();
    if (blur_passes == 0) {
      this.runSimpleCopy(uc, parameters);
      return;
    }

    Assertive.ensure(blur_passes > 0);

    final float blur_size = parameters.getBlurSize();
    if (blur_size == 0.0f) {
      this.runSimpleScale(uc, parameters);
      return;
    }

    Assertive.ensure(blur_passes > 0);
    Assertive.ensure(blur_size > 0.0f);

    this.runBlur(uc, parameters);
  }

  private void runBlur(
    final R2TextureUnitContextParentType uc,
    final R2FilterBoxBlurParameters<SD, S, DD, D> parameters)
  {
    NullCheck.notNull(uc);
    NullCheck.notNull(parameters);

    final S source =
      parameters.getSourceRenderTarget();
    final D destination =
      parameters.getOutputRenderTarget();

    final DD desc_scaled;
    if (parameters.getBlurScale() == 1.0) {
      desc_scaled = destination.getDescription();
    } else {
      desc_scaled = R2RenderTargetDescriptions.scale(
        destination.getDescription(),
        parameters.getBlurScale());
    }

    final D temporary_a =
      this.render_target_pool.get(uc, desc_scaled);

    try {

      final D temporary_b =
        this.render_target_pool.get(uc, desc_scaled);

      try {

        /**
         * Copy the contents of the source to TA.
         */

        final JCGLFramebuffersType g_fb = this.g.getFramebuffers();
        g_fb.framebufferReadUnbind();
        g_fb.framebufferDrawUnbind();
        g_fb.framebufferReadBind(source.getPrimaryFramebuffer());
        g_fb.framebufferDrawBind(temporary_a.getPrimaryFramebuffer());
        g_fb.framebufferBlit(
          source.getArea(),
          temporary_a.getArea(),
          R2FilterBoxBlur.BLIT_BUFFERS,
          parameters.getBlurScaleFilter());
        g_fb.framebufferReadUnbind();
        g_fb.framebufferDrawUnbind();

        /**
         * Now repeatedly blur TA → TB, TB → TA.
         */

        for (int pass = 0; pass < parameters.getBlurPasses(); ++pass) {
          this.evaluateBlurH(
            uc,
            parameters,
            parameters.getOutputTextureSelector().apply(temporary_a),
            temporary_b.getArea(),
            temporary_b.getPrimaryFramebuffer());
          this.evaluateBlurV(
            uc,
            parameters,
            parameters.getOutputTextureSelector().apply(temporary_b),
            temporary_a.getArea(),
            temporary_a.getPrimaryFramebuffer());
        }

        /**
         * Now, copy TA → Output.
         */

        g_fb.framebufferReadUnbind();
        g_fb.framebufferDrawUnbind();
        g_fb.framebufferReadBind(temporary_a.getPrimaryFramebuffer());
        g_fb.framebufferDrawBind(destination.getPrimaryFramebuffer());
        g_fb.framebufferBlit(
          temporary_a.getArea(),
          destination.getArea(),
          R2FilterBoxBlur.BLIT_BUFFERS,
          parameters.getBlurScaleFilter());
        g_fb.framebufferReadUnbind();
        g_fb.framebufferDrawUnbind();

      } finally {
        this.render_target_pool.returnValue(uc, temporary_b);
      }
    } finally {
      this.render_target_pool.returnValue(uc, temporary_a);
    }
  }

  private void evaluateBlurH(
    final R2TextureUnitContextParentType uc,
    final R2FilterBoxBlurParameters<SD, S, DD, D> parameters,
    final R2Texture2DUsableType source_texture,
    final AreaInclusiveUnsignedLType target_area,
    final JCGLFramebufferUsableType target_fb)
  {
    final JCGLTexturesType g_tex = this.g.getTextures();
    final JCGLShadersType g_sh = this.g.getShaders();
    final JCGLDrawType g_dr = this.g.getDraw();
    final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
    final JCGLViewportsType g_v = this.g.getViewports();
    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();

    final R2TextureUnitContextType tc = uc.unitContextNew();

    try {
      g_fb.framebufferDrawBind(target_fb);

      JCGLRenderStates.activate(this.g, this.render_state);
      g_v.viewportSet(target_area);

      try {
        g_sh.shaderActivateProgram(this.shader_blur_h.getShaderProgram());

        final AreaInclusiveUnsignedLType source_area =
          source_texture.get().textureGetArea();
        final UnsignedRangeInclusiveL range_x =
          source_area.getRangeX();

        this.shader_params.setBlurRadius(
          (float) range_x.getInterval() / parameters.getBlurSize());
        this.shader_params.setTexture(source_texture);

        this.shader_blur_h.setTextures(g_tex, tc, this.shader_params);
        this.shader_blur_h.setValues(g_sh, this.shader_params);

        g_ao.arrayObjectBind(this.quad.getArrayObject());
        g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
      } finally {
        g_ao.arrayObjectUnbind();
        g_sh.shaderDeactivateProgram();
      }

    } finally {
      g_fb.framebufferDrawUnbind();
      tc.unitContextFinish(g_tex);
    }
  }

  private void evaluateBlurV(
    final R2TextureUnitContextParentType uc,
    final R2FilterBoxBlurParameters<SD, S, DD, D> parameters,
    final R2Texture2DUsableType source_texture,
    final AreaInclusiveUnsignedLType target_area,
    final JCGLFramebufferUsableType target_fb)
  {
    final JCGLTexturesType g_tex = this.g.getTextures();
    final JCGLBlendingType g_b = this.g.getBlending();
    final JCGLShadersType g_sh = this.g.getShaders();
    final JCGLDrawType g_dr = this.g.getDraw();
    final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
    final JCGLViewportsType g_v = this.g.getViewports();
    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();

    final R2TextureUnitContextType tc = uc.unitContextNew();

    try {
      g_fb.framebufferDrawBind(target_fb);

      JCGLRenderStates.activate(this.g, this.render_state);
      g_v.viewportSet(target_area);

      try {
        g_sh.shaderActivateProgram(this.shader_blur_v.getShaderProgram());

        final AreaInclusiveUnsignedLType source_area =
          source_texture.get().textureGetArea();
        final UnsignedRangeInclusiveL range_y =
          source_area.getRangeY();

        this.shader_params.setBlurRadius(
          (float) range_y.getInterval() / parameters.getBlurSize());
        this.shader_params.setTexture(source_texture);

        this.shader_blur_v.setTextures(g_tex, tc, this.shader_params);
        this.shader_blur_v.setValues(g_sh, this.shader_params);

        g_ao.arrayObjectBind(this.quad.getArrayObject());
        g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
      } finally {
        g_ao.arrayObjectUnbind();
        g_sh.shaderDeactivateProgram();
      }

    } finally {
      g_fb.framebufferDrawUnbind();
      tc.unitContextFinish(g_tex);
    }
  }

}
