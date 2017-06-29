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

package com.io7m.r2.filters.emission;

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
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jnull.NullCheck;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jregions.core.unparameterized.areas.AreasL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizesL;
import com.io7m.r2.core.api.R2Exception;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.filters.api.R2FilterUsableType;
import com.io7m.r2.filters.box_blur.api.R2BlurParameters;
import com.io7m.r2.filters.box_blur.api.R2FilterBoxBlurParameters;
import com.io7m.r2.filters.emission.api.R2FilterEmissionParameters;
import com.io7m.r2.filters.emission.api.R2FilterEmissionType;
import com.io7m.r2.images.api.R2ImageBufferDescription;
import com.io7m.r2.images.api.R2ImageBufferUsableType;
import com.io7m.r2.rendering.targets.R2RenderTargetDescriptions;
import com.io7m.r2.rendering.targets.R2RenderTargetPoolUsableType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.filter.api.R2ShaderFilterType;
import com.io7m.r2.shaders.filter.api.R2ShaderParametersFilterMutable;
import com.io7m.r2.textures.R2Texture2DUsableType;
import com.io7m.r2.unit_quads.R2UnitQuadUsableType;

import java.util.Optional;

/**
 * The default implementation of the emission/glow filter.
 */

public final class R2FilterEmission implements R2FilterEmissionType
{
  private final R2FilterUsableType<R2FilterBoxBlurParameters<
    R2ImageBufferDescription,
    R2ImageBufferUsableType,
    R2ImageBufferDescription,
    R2ImageBufferUsableType>> filter_blur;
  private final R2ShaderFilterType<R2ShaderFilterEmissionParameters> shader_emission;
  private final JCGLInterfaceGL33Type g;
  private final R2RenderTargetPoolUsableType<
    R2ImageBufferDescription, R2ImageBufferUsableType> render_target_pool;
  private final R2UnitQuadUsableType quad;
  private final JCGLRenderState render_state_additive;
  private final JCGLRenderState render_state;
  private final R2ShaderParametersFilterMutable<R2ShaderFilterEmissionParameters> values;

  private R2FilterEmission(
    final R2FilterUsableType<R2FilterBoxBlurParameters<R2ImageBufferDescription, R2ImageBufferUsableType, R2ImageBufferDescription, R2ImageBufferUsableType>> in_blur,
    final R2ShaderFilterType<R2ShaderFilterEmissionParameters> in_shader_emission,
    final JCGLInterfaceGL33Type in_g,
    final R2RenderTargetPoolUsableType<R2ImageBufferDescription, R2ImageBufferUsableType> in_render_target_pool,
    final R2UnitQuadUsableType in_quad)
  {
    this.filter_blur =
      NullCheck.notNull(in_blur, "Blur");
    this.shader_emission =
      NullCheck.notNull(in_shader_emission, "Emission");
    this.g =
      NullCheck.notNull(in_g, "G33");
    this.render_target_pool =
      NullCheck.notNull(in_render_target_pool, "Render target pool");
    this.quad =
      NullCheck.notNull(in_quad, "Quad");

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

    this.values = R2ShaderParametersFilterMutable.create();
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

  public static R2FilterEmissionType newFilter(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_id_pool,
    final R2FilterUsableType<
      R2FilterBoxBlurParameters<
        R2ImageBufferDescription,
        R2ImageBufferUsableType,
        R2ImageBufferDescription,
        R2ImageBufferUsableType>> in_blur,
    final R2RenderTargetPoolUsableType<
      R2ImageBufferDescription,
      R2ImageBufferUsableType> in_render_target_pool,
    final R2UnitQuadUsableType in_quad)
  {
    final JCGLShadersType g_sh = in_g.shaders();
    final R2ShaderFilterEmission in_shader_emission =
      R2ShaderFilterEmission.newShader(g_sh, in_shader_env, in_id_pool);

    return new R2FilterEmission(
      in_blur,
      in_shader_emission,
      in_g,
      in_render_target_pool,
      in_quad);
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type gx)
    throws R2Exception
  {
    NullCheck.notNull(gx, "G33");

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
    NullCheck.notNull(pc, "Profiling");
    NullCheck.notNull(uc, "Texture context");
    NullCheck.notNull(parameters, "Filter parameters");

    final JCGLProfilingContextType pc_base =
      pc.childContext("emission");

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
      pc_base.childContext("unblurred");

    final JCGLFramebuffersType g_fb = this.g.framebuffers();
    final Optional<JCGLFramebufferUsableType> fb_opt =
      parameters.outputFramebuffer();
    fb_opt.ifPresent(g_fb::framebufferDrawBind);

    this.renderEmission(
      uc,
      pc_unblurred.childContext("render"),
      parameters.outputViewport(),
      parameters.albedoEmissionMap(),
      1.0,
      parameters.textureDefaults().black2D(),
      0.0,
      this.render_state_additive);
  }

  private void runFilterBlurred(
    final JCGLProfilingContextType pc_base,
    final JCGLTextureUnitContextParentType uc,
    final R2FilterEmissionParameters parameters,
    final R2BlurParameters blur_parameters)
  {
    final JCGLProfilingContextType pc_emission =
      pc_base.childContext("blurred");
    final JCGLProfilingContextType pc_extract =
      pc_emission.childContext("extract");
    final JCGLProfilingContextType pc_merge =
      pc_emission.childContext("render");

    final JCGLFramebuffersType g_fb =
      this.g.framebuffers();

    final AreaSizeL area_scaled =
      R2RenderTargetDescriptions.scaleAreaInclusive(
        AreasL.size(parameters.outputViewport()),
        parameters.scale());
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
      this.renderEmission(
        uc,
        pc_extract,
        AreaSizesL.area(area_scaled),
        parameters.albedoEmissionMap(),
        1.0,
        parameters.textureDefaults().black2D(),
        0.0,
        this.render_state);

      /*
       * Blur the image.
       */

      final R2FilterBoxBlurParameters<
        R2ImageBufferDescription,
        R2ImageBufferUsableType,
        R2ImageBufferDescription,
        R2ImageBufferUsableType> fbp =
        R2FilterBoxBlurParameters.of(
          temp,
          R2ImageBufferUsableType::imageTexture,
          temp,
          R2ImageBufferUsableType::imageTexture,
          this.render_target_pool,
          blur_parameters,
          (i, a) -> R2ImageBufferDescription.of(a, Optional.empty()));

      this.filter_blur.runFilter(pc_emission, uc, fbp);

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

      this.renderEmission(
        uc,
        pc_merge,
        parameters.outputViewport(),
        parameters.albedoEmissionMap(),
        parameters.emissionIntensity(),
        temp.imageTexture(),
        parameters.glowIntensity(),
        this.render_state_additive);

    } finally {
      this.render_target_pool.returnValue(uc, temp);
    }
  }

  private void renderEmission(
    final JCGLTextureUnitContextParentType uc,
    final JCGLProfilingContextType pc_draw,
    final AreaL output_viewport,
    final R2Texture2DUsableType in_albedo_emission,
    final double in_emission_intensity,
    final R2Texture2DUsableType in_glow_texture,
    final double in_glow_intensity,
    final JCGLRenderStateType r_state)
  {
    pc_draw.startMeasuringIfEnabled();
    try {
      final JCGLTexturesType g_tex = this.g.textures();
      final JCGLShadersType g_sh = this.g.shaders();
      final JCGLDrawType g_dr = this.g.drawing();
      final JCGLArrayObjectsType g_ao = this.g.arrayObjects();
      final JCGLViewportsType g_v = this.g.viewports();

      final JCGLTextureUnitContextType tc = uc.unitContextNew();
      try {
        g_v.viewportSet(output_viewport);
        JCGLRenderStates.activate(this.g, r_state);
        try {
          this.values.setTextureUnitContext(tc);
          this.values.setValues(
            R2ShaderFilterEmissionParameters.builder()
              .setAlbedoEmissionTexture(in_albedo_emission)
              .setEmissionIntensity(in_emission_intensity)
              .setGlowTexture(in_glow_texture)
              .setGlowIntensity(in_glow_intensity)
              .build());

          this.shader_emission.onActivate(this.g);
          this.shader_emission.onReceiveFilterValues(this.g, this.values);
          this.shader_emission.onValidate();
          g_ao.arrayObjectBind(this.quad.arrayObject());
          g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
        } finally {
          g_ao.arrayObjectUnbind();
          this.shader_emission.onDeactivate(this.g);
        }
      } finally {
        tc.unitContextFinish(g_tex);
      }
    } finally {
      pc_draw.stopMeasuringIfEnabled();
    }
  }
}
