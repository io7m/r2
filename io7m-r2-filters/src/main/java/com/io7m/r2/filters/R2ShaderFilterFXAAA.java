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
import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.VectorM2F;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2AbstractShader;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

/**
 * An FXAA shader.
 */

public final class R2ShaderFilterFXAAA extends
  R2AbstractShader<R2ShaderFilterFXAAParametersType>
  implements R2ShaderFilterType<R2ShaderFilterFXAAParametersType>
{
  private final JCGLProgramUniformType u_image;
  private final JCGLProgramUniformType u_screen_inverse;
  private final JCGLProgramUniformType u_subpixel_aliasing_removal;
  private final JCGLProgramUniformType u_edge_threshold;
  private final JCGLProgramUniformType u_edge_threshold_minimum;
  private final VectorM2F screen_inverse;

  private R2ShaderFilterFXAAA(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final String in_name,
    final String in_fragment)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core." + in_name,
      "com.io7m.r2.shaders.core/R2Filter.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/" + in_fragment);

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 5);

    this.u_image =
      R2ShaderParameters.getUniformChecked(
        p, "R2_fxaa.image", JCGLType.TYPE_SAMPLER_2D);
    this.u_screen_inverse =
      R2ShaderParameters.getUniformChecked(
        p, "R2_fxaa.screen_inverse", JCGLType.TYPE_FLOAT_VECTOR_2);
    this.u_subpixel_aliasing_removal =
      R2ShaderParameters.getUniformChecked(
        p, "R2_fxaa.subpixel_aliasing_removal", JCGLType.TYPE_FLOAT);
    this.u_edge_threshold =
      R2ShaderParameters.getUniformChecked(
        p, "R2_fxaa.edge_threshold", JCGLType.TYPE_FLOAT);
    this.u_edge_threshold_minimum =
      R2ShaderParameters.getUniformChecked(
        p, "R2_fxaa.edge_threshold_minimum", JCGLType.TYPE_FLOAT);

    this.screen_inverse = new VectorM2F();
  }

  /**
   * Construct a new shader.
   *
   * @param in_shaders    A shader interface
   * @param in_shader_env Shader sources
   * @param in_pool       The ID pool
   * @param q             The quality preset
   *
   * @return A new shader
   */

  public static R2ShaderFilterType<R2ShaderFilterFXAAParametersType>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2FilterFXAAQuality q)
  {
    NullCheck.notNull(q);

    final String n = R2ShaderFilterFXAAA.qualityToShaderName(q);
    final String f = n + ".frag";

    return R2ShaderFilterVerifier.newVerifier(
      new R2ShaderFilterFXAAA(in_shaders, in_shader_env, in_pool, n, f));
  }

  private static String qualityToShaderName(
    final R2FilterFXAAQuality q)
  {
    switch (q) {
      case R2_FXAA_QUALITY_10:
        return "R2FXAA_10";
      case R2_FXAA_QUALITY_15:
        return "R2FXAA_15";
      case R2_FXAA_QUALITY_20:
        return "R2FXAA_20";
      case R2_FXAA_QUALITY_25:
        return "R2FXAA_25";
      case R2_FXAA_QUALITY_29:
        return "R2FXAA_29";
      case R2_FXAA_QUALITY_39:
        return "R2FXAA_39";
    }

    throw new UnreachableCodeException();
  }

  @Override
  public void onReceiveFilterValues(
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final JCGLTextureUnitContextMutableType tc,
    final R2ShaderFilterFXAAParametersType values)
  {
    /**
     * Upload the texture to be filtered.
     */

    final JCGLTextureUnitType u =
      tc.unitContextBindTexture2D(g_tex, values.getTexture().get());
    g_sh.shaderUniformPutTexture2DUnit(this.u_image, u);

    /**
     * Upload the viewport.
     */

    final R2Texture2DUsableType texture = values.getTexture();
    final AreaInclusiveUnsignedLType area = texture.get().textureGetArea();
    final UnsignedRangeInclusiveL range_x = area.getRangeX();
    final UnsignedRangeInclusiveL range_y = area.getRangeY();

    this.screen_inverse.set2F(
      (float) (1.0 / (double) range_x.getInterval()),
      (float) (1.0 / (double) range_y.getInterval()));

    g_sh.shaderUniformPutVector2f(this.u_screen_inverse, this.screen_inverse);

    /**
     * Upload the various algorithm parameters.
     */

    g_sh.shaderUniformPutFloat(
      this.u_edge_threshold, values.getEdgeThreshold());
    g_sh.shaderUniformPutFloat(
      this.u_edge_threshold_minimum, values.getEdgeThresholdMinimum());
    g_sh.shaderUniformPutFloat(
      this.u_subpixel_aliasing_removal, values.getSubPixelAliasingRemoval());
  }

  @Override
  public Class<R2ShaderFilterFXAAParametersType> getShaderParametersType()
  {
    return R2ShaderFilterFXAAParametersType.class;
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {
    // Nothing
  }
}
