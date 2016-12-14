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

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.JCGLBlendEquation;
import com.io7m.jcanephora.core.JCGLBlendFunction;
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
import com.io7m.jcanephora.renderstate.JCGLBlendState;
import com.io7m.jcanephora.renderstate.JCGLRenderState;
import com.io7m.jcanephora.renderstate.JCGLRenderStateType;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2FilterUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2ImageBufferDescription;
import com.io7m.r2.core.R2ImageBufferDescriptionType;
import com.io7m.r2.core.R2ImageBufferUsableType;
import com.io7m.r2.core.R2RenderTargetDescriptions;
import com.io7m.r2.core.R2RenderTargetPoolUsableType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2UnitQuadUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

/**
 * The default implementation of the emission/glow filter.
 */

public final class R2FilterEmission implements R2FilterType<R2FilterEmissionParameters>
{
  private final R2FilterUsableType<R2FilterBoxBlurParameters<
    R2ImageBufferDescriptionType,
    R2ImageBufferUsableType,
    R2ImageBufferDescriptionType,
    R2ImageBufferUsableType>> filter_blur;
  private final R2ShaderFilterType<R2ShaderFilterEmissionParameters> shader_emission;
  private final JCGLInterfaceGL33Type g;
  private final R2RenderTargetPoolUsableType<
    R2ImageBufferDescriptionType, R2ImageBufferUsableType> render_target_pool;
  private final R2UnitQuadUsableType quad;
  private final JCGLRenderState render_state_additive;
  private final JCGLRenderState render_state;
  private final R2ShaderFilterType<R2ShaderFilterTextureShowParameters> shader_copy;

  private R2FilterEmission(
    final R2FilterUsableType<R2FilterBoxBlurParameters<R2ImageBufferDescriptionType, R2ImageBufferUsableType, R2ImageBufferDescriptionType, R2ImageBufferUsableType>> in_blur,
    final R2ShaderFilterType<R2ShaderFilterEmissionParameters> in_shader_emission,
    final R2ShaderFilterType<R2ShaderFilterTextureShowParameters> in_shader_copy,
    final JCGLInterfaceGL33Type in_g,
    final R2RenderTargetPoolUsableType<R2ImageBufferDescriptionType, R2ImageBufferUsableType> in_render_target_pool,
    final R2UnitQuadUsableType in_quad)
  {
    this.filter_blur = NullCheck.notNull(in_blur);
    this.shader_emission = NullCheck.notNull(in_shader_emission);
    this.shader_copy = NullCheck.notNull(in_shader_copy);
    this.g = NullCheck.notNull(in_g);
    this.render_target_pool = NullCheck.notNull(in_render_target_pool);
    this.quad = NullCheck.notNull(in_quad);

    this.render_state_additive =
      JCGLRenderState
        .builder()
        .setBlendState(Optional.of(JCGLBlendState.of(
          JCGLBlendFunction.BLEND_ONE,
          JCGLBlendFunction.BLEND_ONE,
          JCGLBlendFunction.BLEND_ONE,
          JCGLBlendFunction.BLEND_ONE,
          JCGLBlendEquation.BLEND_EQUATION_ADD,
          JCGLBlendEquation.BLEND_EQUATION_ADD)))
        .build();

    this.render_state =
      JCGLRenderState.builder().build();
  }

  /**
   * Construct a new emission filter.
   *
   * @param in_g                  A GL interface
   * @param in_shader_env         Access to shader sources
   * @param in_id_pool            An ID pool
   * @param in_blur               A blur filter
   * @param in_render_target_pool A pool of render targets for the blur filter
   * @param in_quad               A unit quad
   *
   * @return A new filter
   */

  public static R2FilterType<R2FilterEmissionParameters> newFilter(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_id_pool,
    final R2FilterUsableType<
      R2FilterBoxBlurParameters<
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType,
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType>> in_blur,
    final R2RenderTargetPoolUsableType<
      R2ImageBufferDescriptionType,
      R2ImageBufferUsableType> in_render_target_pool,
    final R2UnitQuadUsableType in_quad)
  {
    final JCGLShadersType g_sh = in_g.getShaders();

    final R2ShaderFilterType<
      R2ShaderFilterEmissionParameters> in_shader_emission =
      R2ShaderFilterEmission.newShader(g_sh, in_shader_env, in_id_pool);
    final R2ShaderFilterType<
      R2ShaderFilterTextureShowParameters> in_shader_copy =
      R2ShaderFilterTextureShow.newShader(g_sh, in_shader_env, in_id_pool);

    return new R2FilterEmission(
      in_blur,
      in_shader_emission,
      in_shader_copy,
      in_g,
      in_render_target_pool,
      in_quad);
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type gx)
    throws R2Exception
  {
    NullCheck.notNull(gx);

    if (!this.isDeleted()) {
      this.shader_emission.delete(gx);
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.shader_emission.isDeleted();
  }

  @Override
  public void runFilter(
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType uc,
    final R2FilterEmissionParameters parameters)
  {
    NullCheck.notNull(pc);
    NullCheck.notNull(uc);
    NullCheck.notNull(parameters);

    final JCGLProfilingContextType pc_base =
      pc.getChildContext("emission");

    final Optional<R2BlurParameters> blur_opt =
      parameters.blurParameters();

    if (blur_opt.isPresent()) {
      this.runFilterBlurred(pc_base, uc, parameters, blur_opt.get());
      return;
    }

    this.runFilterUnblurred(pc_base, uc, parameters);
  }

  private void runFilterUnblurred(
    final JCGLProfilingContextType pc_base,
    final JCGLTextureUnitContextParentType uc,
    final R2FilterEmissionParameters parameters)
  {
    final JCGLProfilingContextType pc_unblurred =
      pc_base.getChildContext("unblurred");

    final JCGLFramebuffersType g_fb =
      this.g.getFramebuffers();
    final Optional<JCGLFramebufferUsableType> fb_opt =
      parameters.outputFramebuffer();
    if (fb_opt.isPresent()) {
      g_fb.framebufferDrawBind(fb_opt.get());
    }

    this.copyEmissive(
      uc,
      pc_unblurred.getChildContext("draw"),
      parameters.outputViewport(),
      parameters.albedoEmissionMap(),
      this.render_state_additive);
  }

  private void runFilterBlurred(
    final JCGLProfilingContextType pc_base,
    final JCGLTextureUnitContextParentType uc,
    final R2FilterEmissionParameters parameters,
    final R2BlurParameters blur_parameters)
  {
    final JCGLProfilingContextType pc_blurred =
      pc_base.getChildContext("blurred");
    final JCGLProfilingContextType pc_draw =
      pc_blurred.getChildContext("draw");
    final JCGLProfilingContextType pc_copy_out =
      pc_blurred.getChildContext("copy-out");

    final JCGLFramebuffersType g_fb =
      this.g.getFramebuffers();

    final AreaInclusiveUnsignedLType area_scaled =
      R2RenderTargetDescriptions.scaleAreaInclusive(
        parameters.outputViewport(), parameters.scale());
    final R2ImageBufferDescription temp_desc =
      R2ImageBufferDescription.of(area_scaled, Optional.empty());

    final R2ImageBufferUsableType temp =
      this.render_target_pool.get(uc, temp_desc);

    try {

      /*
       * Render the albedo components of the texture, multiplied by the
       * emission level, to the output.
       */

      g_fb.framebufferDrawBind(temp.primaryFramebuffer());
      this.copyEmissive(
        uc,
        pc_draw,
        area_scaled,
        parameters.albedoEmissionMap(),
        this.render_state);

      /*
       * Blur the image.
       */

      final R2FilterBoxBlurParameters<
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType,
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType> fbp =
        R2FilterBoxBlurParameters.of(
          temp,
          R2ImageBufferUsableType::imageTexture,
          temp,
          R2ImageBufferUsableType::imageTexture,
          this.render_target_pool,
          blur_parameters,
          (i, a) -> R2ImageBufferDescription.of(a, Optional.empty()));

      this.filter_blur.runFilter(pc_blurred, uc, fbp);

      /*
       * Blend the blurred emissive image into the output framebuffer.
       */

      final Optional<JCGLFramebufferUsableType> fb_opt =
        parameters.outputFramebuffer();
      if (fb_opt.isPresent()) {
        g_fb.framebufferDrawBind(fb_opt.get());
      } else {
        g_fb.framebufferDrawUnbind();
      }

      this.copyOut(
        uc,
        pc_copy_out,
        parameters.outputViewport(),
        temp.imageTexture(),
        this.render_state_additive);

    } finally {
      this.render_target_pool.returnValue(uc, temp);
    }
  }

  private void copyOut(
    final JCGLTextureUnitContextParentType uc,
    final JCGLProfilingContextType pc_copy_out,
    final AreaInclusiveUnsignedLType output_viewport,
    final R2Texture2DUsableType in_texture,
    final JCGLRenderStateType r_state)
  {
    pc_copy_out.startMeasuringIfEnabled();
    try {
      final JCGLTexturesType g_tex = this.g.getTextures();
      final JCGLShadersType g_sh = this.g.getShaders();
      final JCGLDrawType g_dr = this.g.getDraw();
      final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
      final JCGLViewportsType g_v = this.g.getViewports();

      final JCGLTextureUnitContextType tc = uc.unitContextNew();
      try {
        g_v.viewportSet(output_viewport);
        JCGLRenderStates.activate(this.g, r_state);

        try {
          final R2ShaderFilterTextureShowParameters cp =
            R2ShaderFilterTextureShowParameters.builder()
              .setTexture(in_texture)
              .setIntensity(1.0f)
              .build();

          this.shader_copy.onActivate(g_sh);
          this.shader_copy.onReceiveFilterValues(g_tex, g_sh, tc, cp);
          this.shader_copy.onValidate();
          g_ao.arrayObjectBind(this.quad.arrayObject());
          g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
        } finally {
          g_ao.arrayObjectUnbind();
          this.shader_copy.onDeactivate(g_sh);
        }
      } finally {
        tc.unitContextFinish(g_tex);
      }
    } finally {
      pc_copy_out.stopMeasuringIfEnabled();
    }
  }

  private void copyEmissive(
    final JCGLTextureUnitContextParentType uc,
    final JCGLProfilingContextType pc_draw,
    final AreaInclusiveUnsignedLType output_viewport,
    final R2Texture2DUsableType in_texture,
    final JCGLRenderStateType r_state)
  {
    pc_draw.startMeasuringIfEnabled();
    try {
      final JCGLTexturesType g_tex = this.g.getTextures();
      final JCGLShadersType g_sh = this.g.getShaders();
      final JCGLDrawType g_dr = this.g.getDraw();
      final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
      final JCGLViewportsType g_v = this.g.getViewports();

      final JCGLTextureUnitContextType tc = uc.unitContextNew();
      try {
        g_v.viewportSet(output_viewport);
        JCGLRenderStates.activate(this.g, r_state);
        try {
          final R2ShaderFilterEmissionParameters sp =
            R2ShaderFilterEmissionParameters.of(in_texture);

          this.shader_emission.onActivate(g_sh);
          this.shader_emission.onReceiveFilterValues(g_tex, g_sh, tc, sp);
          this.shader_emission.onValidate();
          g_ao.arrayObjectBind(this.quad.arrayObject());
          g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
        } finally {
          g_ao.arrayObjectUnbind();
          this.shader_emission.onDeactivate(g_sh);
        }
      } finally {
        tc.unitContextFinish(g_tex);
      }
    } finally {
      pc_draw.stopMeasuringIfEnabled();
    }
  }
}
