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

package com.io7m.r2.shaders.bilateral_blur;

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jtensors.core.unparameterized.vectors.Vector2D;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.matrices.R2MatricesObserverValuesType;
import com.io7m.r2.projections.R2Projections;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.api.R2ShaderStateChecking;
import com.io7m.r2.shaders.filter.api.R2AbstractFilterShader;
import com.io7m.r2.shaders.filter.api.R2ShaderParametersFilterType;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_VECTOR_2;
import static com.io7m.jcanephora.core.JCGLType.TYPE_SAMPLER_2D;
import static com.io7m.r2.shaders.api.R2ShaderParameters.checkUniformParameterCount;
import static com.io7m.r2.shaders.api.R2ShaderParameters.uniform;

/**
 * A bilateral depth-aware blur vertical RGBA shader.
 */

public final class R2ShaderFilterBilateralBlurDepthAwareVertical4f
  extends R2AbstractFilterShader<R2ShaderFilterBilateralBlurDepthAwareParameters>
{
  private final JCGLProgramUniformType u_texture_image;
  private final JCGLProgramUniformType u_texture_depth;
  private final JCGLProgramUniformType u_blur_depth_coefficient;
  private final JCGLProgramUniformType u_blur_radius;
  private final JCGLProgramUniformType u_blur_sharpness;
  private final JCGLProgramUniformType u_blur_falloff;
  private final JCGLProgramUniformType u_blur_output_image_size_inverse;

  private R2ShaderFilterBilateralBlurDepthAwareVertical4f(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.bilateral_blur.R2ShaderFilterBilateralBlurDepthAwareVertical4f",
      "com.io7m.r2.shaders.filter.api/R2Filter.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.bilateral_blur/R2FilterBilateralBlurDepthAwareVertical4f.frag",
      in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_texture_image =
      uniform(p, "R2_texture_image", TYPE_SAMPLER_2D);
    this.u_texture_depth =
      uniform(p, "R2_texture_depth", TYPE_SAMPLER_2D);

    this.u_blur_depth_coefficient =
      uniform(p, "R2_blur.depth_coefficient", TYPE_FLOAT);
    this.u_blur_radius =
      uniform(p, "R2_blur.radius", TYPE_FLOAT);
    this.u_blur_sharpness =
      uniform(p, "R2_blur.sharpness", TYPE_FLOAT);
    this.u_blur_falloff =
      uniform(p, "R2_blur.falloff", TYPE_FLOAT);
    this.u_blur_output_image_size_inverse =
      uniform(p, "R2_blur.output_image_size_inverse", TYPE_FLOAT_VECTOR_2);

    checkUniformParameterCount(p, 7);
  }

  /**
   * Construct a new shader.
   *
   * @param in_shaders    A shader interface
   * @param in_shader_env Shader sources
   * @param in_pool       The ID pool
   *
   * @return A new shader
   */

  public static R2ShaderFilterBilateralBlurDepthAwareVertical4f
  create(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2ShaderFilterBilateralBlurDepthAwareVertical4f(
      in_shaders, in_shader_env, in_pool, R2ShaderStateChecking.STATE_CHECK);
  }

  @Override
  public Class<R2ShaderFilterBilateralBlurDepthAwareParameters>
  shaderParametersType()
  {
    return R2ShaderFilterBilateralBlurDepthAwareParameters.class;
  }

  @Override
  protected void onActualReceiveFilterValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersFilterType<R2ShaderFilterBilateralBlurDepthAwareParameters> parameters)
  {
    final R2ShaderFilterBilateralBlurDepthAwareParameters values =
      parameters.values();
    final JCGLTextureUnitContextMutableType tc =
      parameters.textureUnitContext();

    final JCGLShadersType g_sh = g.shaders();
    final JCGLTexturesType g_tex = g.textures();

    /*
     * Upload the scene's depth coefficient.
     */

    final R2MatricesObserverValuesType view_matrices =
      values.viewMatrices();

    g_sh.shaderUniformPutFloat(
      this.u_blur_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(view_matrices.projection()));

    /*
     * Upload textures and parameters.
     */

    final JCGLTextureUnitType unit_texture_image =
      tc.unitContextBindTexture2D(g_tex, values.imageTexture().texture());
    final JCGLTextureUnitType unit_texture_depth =
      tc.unitContextBindTexture2D(g_tex, values.depthTexture().texture());

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_image, unit_texture_image);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_depth, unit_texture_depth);

    g_sh.shaderUniformPutFloat(
      this.u_blur_falloff, (float) values.blurFalloff());
    g_sh.shaderUniformPutFloat(
      this.u_blur_radius, (float) values.blurRadius());
    g_sh.shaderUniformPutFloat(
      this.u_blur_sharpness, (float) values.blurSharpness());

    g_sh.shaderUniformPutVector2f(
      this.u_blur_output_image_size_inverse,
      Vector2D.of(
        values.blurOutputInverseWidth(),
        values.blurOutputInverseHeight()));
  }
}
