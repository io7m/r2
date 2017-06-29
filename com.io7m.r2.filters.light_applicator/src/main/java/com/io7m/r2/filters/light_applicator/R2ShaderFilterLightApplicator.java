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

package com.io7m.r2.filters.light_applicator;

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

import static com.io7m.jcanephora.core.JCGLType.TYPE_SAMPLER_2D;
import static com.io7m.r2.shaders.api.R2ShaderParameters.checkUniformParameterCount;
import static com.io7m.r2.shaders.api.R2ShaderParameters.uniform;
import static com.io7m.r2.shaders.api.R2ShaderStateChecking.STATE_CHECK;

/**
 * A light applicator shader.
 */

public final class R2ShaderFilterLightApplicator extends
  R2AbstractFilterShader<R2ShaderFilterLightApplicatorParameters>
{
  private final JCGLProgramUniformType u_texture_albedo;
  private final JCGLProgramUniformType u_texture_specular;
  private final JCGLProgramUniformType u_texture_diffuse;

  private R2ShaderFilterLightApplicator(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.light_applicator.R2ShaderFilterLightApplicator",
      "com.io7m.r2.shaders.filter.api/R2Filter.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.light_applicator/R2FilterLightApplicator.frag",
      in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();
    this.u_texture_albedo =
      uniform(p, "R2_textures_albedo", TYPE_SAMPLER_2D);
    this.u_texture_specular =
      uniform(p, "R2_textures_specular", TYPE_SAMPLER_2D);
    this.u_texture_diffuse =
      uniform(p, "R2_textures_diffuse", TYPE_SAMPLER_2D);

    checkUniformParameterCount(p, 3);
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

  public static R2ShaderFilterLightApplicator
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2ShaderFilterLightApplicator(
      in_shaders, in_shader_env, in_pool, STATE_CHECK);
  }

  @Override
  public Class<R2ShaderFilterLightApplicatorParameters>
  shaderParametersType()
  {
    return R2ShaderFilterLightApplicatorParameters.class;
  }

  @Override
  protected void onActualReceiveFilterValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersFilterType<R2ShaderFilterLightApplicatorParameters> parameters)
  {
    final R2ShaderFilterLightApplicatorParameters values =
      parameters.values();
    final JCGLTextureUnitContextMutableType tc =
      parameters.textureUnitContext();

    final JCGLShadersType g_sh = g.shaders();
    final JCGLTexturesType g_tex = g.textures();

    final JCGLTextureUnitType unit_albedo =
      tc.unitContextBindTexture2D(g_tex, values.albedoTexture().texture());
    final JCGLTextureUnitType unit_diffuse =
      tc.unitContextBindTexture2D(g_tex, values.diffuseTexture().texture());
    final JCGLTextureUnitType unit_specular =
      tc.unitContextBindTexture2D(g_tex, values.specularTexture().texture());

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_albedo, unit_albedo);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_diffuse, unit_diffuse);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_specular, unit_specular);
  }
}
