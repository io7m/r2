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

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.VectorM2F;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.shaders.provided.R2AbstractShader;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderParametersFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

/**
 * A bilateral depth-aware blur vertical RGBA shader.
 */

public final class R2ShaderFilterBilateralBlurDepthAwareVertical4f extends
  R2AbstractShader<R2ShaderFilterBilateralBlurDepthAwareParameters>
  implements
  R2ShaderFilterType<R2ShaderFilterBilateralBlurDepthAwareParameters>
{
  private final JCGLProgramUniformType u_texture_image;
  private final JCGLProgramUniformType u_texture_depth;
  private final JCGLProgramUniformType u_blur_depth_coefficient;
  private final JCGLProgramUniformType u_blur_radius;
  private final JCGLProgramUniformType u_blur_sharpness;
  private final JCGLProgramUniformType u_blur_falloff;
  private final JCGLProgramUniformType u_blur_output_image_size_inverse;
  private final VectorM2F inverse_size;

  private R2ShaderFilterBilateralBlurDepthAwareVertical4f(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2ShaderFilterBilateralBlurDepthAwareVertical4f",
      "com.io7m.r2.shaders.core/R2Filter.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2FilterBilateralBlurDepthAwareVertical4f.frag");

    final JCGLProgramShaderUsableType p = this.shaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 7);

    this.u_texture_image =
      R2ShaderParameters.getUniformChecked(
        p,
        "R2_texture_image",
        JCGLType.TYPE_SAMPLER_2D);
    this.u_texture_depth =
      R2ShaderParameters.getUniformChecked(
        p,
        "R2_texture_depth",
        JCGLType.TYPE_SAMPLER_2D);

    this.u_blur_depth_coefficient =
      R2ShaderParameters.getUniformChecked(
        p, "R2_blur.depth_coefficient", JCGLType.TYPE_FLOAT);
    this.u_blur_radius =
      R2ShaderParameters.getUniformChecked(
        p, "R2_blur.radius", JCGLType.TYPE_FLOAT);
    this.u_blur_sharpness =
      R2ShaderParameters.getUniformChecked(
        p, "R2_blur.sharpness", JCGLType.TYPE_FLOAT);
    this.u_blur_falloff =
      R2ShaderParameters.getUniformChecked(
        p, "R2_blur.falloff", JCGLType.TYPE_FLOAT);
    this.u_blur_output_image_size_inverse =
      R2ShaderParameters.getUniformChecked(
        p, "R2_blur.output_image_size_inverse", JCGLType.TYPE_FLOAT_VECTOR_2);

    this.inverse_size = new VectorM2F();
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

  public static R2ShaderFilterType<R2ShaderFilterBilateralBlurDepthAwareParameters>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return R2ShaderFilterVerifier.newVerifier(
      new R2ShaderFilterBilateralBlurDepthAwareVertical4f(
        in_shaders, in_shader_env, in_pool));
  }

  @Override
  public Class<R2ShaderFilterBilateralBlurDepthAwareParameters>
  shaderParametersType()
  {
    return R2ShaderFilterBilateralBlurDepthAwareParameters.class;
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {
    // Nothing
  }

  @Override
  public void onReceiveFilterValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersFilterType<R2ShaderFilterBilateralBlurDepthAwareParameters> parameters)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(parameters);

    final R2ShaderFilterBilateralBlurDepthAwareParameters values =
      parameters.values();
    final JCGLTextureUnitContextMutableType tc =
      parameters.textureUnitContext();

    final JCGLShadersType g_sh = g.getShaders();
    final JCGLTexturesType g_tex = g.getTextures();

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
      this.u_blur_falloff, values.blurFalloff());
    g_sh.shaderUniformPutFloat(
      this.u_blur_radius, values.blurRadius());
    g_sh.shaderUniformPutFloat(
      this.u_blur_sharpness, values.blurSharpness());

    this.inverse_size.set2F(
      values.blurOutputInverseWidth(), values.blurOutputInverseHeight());
    g_sh.shaderUniformPutVector2f(
      this.u_blur_output_image_size_inverse, this.inverse_size);
  }
}
