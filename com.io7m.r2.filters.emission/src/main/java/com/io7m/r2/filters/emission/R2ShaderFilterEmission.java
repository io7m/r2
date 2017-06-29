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

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.api.R2ShaderStateChecking;
import com.io7m.r2.shaders.filter.api.R2AbstractFilterShader;
import com.io7m.r2.shaders.filter.api.R2ShaderParametersFilterType;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT;
import static com.io7m.jcanephora.core.JCGLType.TYPE_SAMPLER_2D;
import static com.io7m.r2.shaders.api.R2ShaderParameters.checkUniformParameterCount;
import static com.io7m.r2.shaders.api.R2ShaderParameters.uniform;

/**
 * An emission shader that copies the emissive parts of an image.
 */

public final class R2ShaderFilterEmission
  extends R2AbstractFilterShader<R2ShaderFilterEmissionParameters>
{
  private final JCGLProgramUniformType u_albedo_emission_texture;
  private final JCGLProgramUniformType u_albedo_emission_intensity;
  private final JCGLProgramUniformType u_glow_texture;
  private final JCGLProgramUniformType u_glow_intensity;

  private R2ShaderFilterEmission(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.emission.R2ShaderFilterEmission",
      "com.io7m.r2.shaders.filter.api/R2Filter.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.emission/R2FilterEmission.frag",
      in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_albedo_emission_texture =
      uniform(p, "R2_albedo_emission", TYPE_SAMPLER_2D);
    this.u_glow_texture =
      uniform(p, "R2_glow", TYPE_SAMPLER_2D);

    this.u_albedo_emission_intensity =
      uniform(p, "R2_emission_intensity", TYPE_FLOAT);
    this.u_glow_intensity =
      uniform(p, "R2_glow_intensity", TYPE_FLOAT);

    checkUniformParameterCount(p, 4);
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

  public static R2ShaderFilterEmission
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2ShaderFilterEmission(
      in_shaders, in_shader_env, in_pool, R2ShaderStateChecking.STATE_CHECK);
  }

  @Override
  public Class<R2ShaderFilterEmissionParameters>
  shaderParametersType()
  {
    return R2ShaderFilterEmissionParameters.class;
  }

  @Override
  protected void onActualReceiveFilterValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersFilterType<R2ShaderFilterEmissionParameters> parameters)
  {
    final R2ShaderFilterEmissionParameters values =
      parameters.values();
    final JCGLTextureUnitContextMutableType tc =
      parameters.textureUnitContext();

    final JCGLShadersType g_sh = g.shaders();
    final JCGLTexturesType g_tex = g.textures();

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
      this.u_albedo_emission_intensity, (float) values.emissionIntensity());
    g_sh.shaderUniformPutFloat(
      this.u_glow_intensity, (float) values.glowIntensity());
  }
}
