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
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2AbstractShader;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

/**
 * An emission shader that copies the emissive parts of an image.
 */

public final class R2ShaderFilterEmission extends
  R2AbstractShader<R2ShaderFilterEmissionParameters>
  implements R2ShaderFilterType<R2ShaderFilterEmissionParameters>
{
  private final JCGLProgramUniformType u_albedo_emission_texture;
  private final JCGLProgramUniformType u_albedo_emission_intensity;
  private final JCGLProgramUniformType u_glow_texture;
  private final JCGLProgramUniformType u_glow_intensity;

  private R2ShaderFilterEmission(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2ShaderFilterEmission",
      "com.io7m.r2.shaders.core/R2Filter.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2FilterEmission.frag");

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_albedo_emission_texture =
      R2ShaderParameters.getUniformChecked(
        p, "R2_albedo_emission", JCGLType.TYPE_SAMPLER_2D);
    this.u_glow_texture =
      R2ShaderParameters.getUniformChecked(
        p, "R2_glow", JCGLType.TYPE_SAMPLER_2D);

    this.u_albedo_emission_intensity =
      R2ShaderParameters.getUniformChecked(
        p, "R2_emission_intensity", JCGLType.TYPE_FLOAT);
    this.u_glow_intensity =
      R2ShaderParameters.getUniformChecked(
        p, "R2_glow_intensity", JCGLType.TYPE_FLOAT);

    R2ShaderParameters.checkUniformParameterCount(p, 4);
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

  public static R2ShaderFilterType<R2ShaderFilterEmissionParameters>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return R2ShaderFilterVerifier.newVerifier(
      new R2ShaderFilterEmission(in_shaders, in_shader_env, in_pool));
  }

  @Override
  public Class<R2ShaderFilterEmissionParameters>
  shaderParametersType()
  {
    return R2ShaderFilterEmissionParameters.class;
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {
    // Nothing
  }

  @Override
  public void onReceiveFilterValues(
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final JCGLTextureUnitContextMutableType tc,
    final R2ShaderFilterEmissionParameters values)
  {
    NullCheck.notNull(g_tex);
    NullCheck.notNull(tc);
    NullCheck.notNull(g_sh);
    NullCheck.notNull(values);

    final JCGLTextureUnitType unit_emission_texture =
      tc.unitContextBindTexture2D(
        g_tex, values.albedoEmissionTexture().texture());
    final JCGLTextureUnitType unit_glow_texture =
      tc.unitContextBindTexture2D(
        g_tex, values.glowTexture().texture());

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_albedo_emission_texture, unit_emission_texture);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_glow_texture, unit_glow_texture);

    g_sh.shaderUniformPutFloat(
      this.u_albedo_emission_intensity, values.emissionIntensity());
    g_sh.shaderUniformPutFloat(
      this.u_glow_intensity, values.glowIntensity());
  }
}
