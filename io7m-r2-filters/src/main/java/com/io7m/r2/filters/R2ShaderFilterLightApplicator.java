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
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.shaders.provided.R2AbstractShader;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderParametersFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

/**
 * A light applicator shader.
 */

public final class R2ShaderFilterLightApplicator extends
  R2AbstractShader<R2ShaderFilterLightApplicatorParameters>
  implements R2ShaderFilterType<R2ShaderFilterLightApplicatorParameters>
{
  private final JCGLProgramUniformType u_texture_albedo;
  private final JCGLProgramUniformType u_texture_specular;
  private final JCGLProgramUniformType u_texture_diffuse;

  private R2ShaderFilterLightApplicator(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2ShaderFilterLightApplicator",
      "com.io7m.r2.shaders.core/R2Filter.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2FilterLightApplicator.frag");

    final JCGLProgramShaderUsableType p = this.shaderProgram();
    this.u_texture_albedo =
      R2ShaderParameters.getUniformChecked(
        p, "R2_textures_albedo", JCGLType.TYPE_SAMPLER_2D);
    this.u_texture_specular =
      R2ShaderParameters.getUniformChecked(
        p, "R2_textures_specular", JCGLType.TYPE_SAMPLER_2D);
    this.u_texture_diffuse =
      R2ShaderParameters.getUniformChecked(
        p, "R2_textures_diffuse", JCGLType.TYPE_SAMPLER_2D);
    R2ShaderParameters.checkUniformParameterCount(p, 3);
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

  public static R2ShaderFilterType<R2ShaderFilterLightApplicatorParameters>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return R2ShaderFilterVerifier.newVerifier(
      new R2ShaderFilterLightApplicator(in_shaders, in_shader_env, in_pool));
  }

  @Override
  public Class<R2ShaderFilterLightApplicatorParameters>
  shaderParametersType()
  {
    return R2ShaderFilterLightApplicatorParameters.class;
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
    final R2ShaderParametersFilterType<R2ShaderFilterLightApplicatorParameters> parameters)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(parameters);

    final R2ShaderFilterLightApplicatorParameters values =
      parameters.values();
    final JCGLTextureUnitContextMutableType tc =
      parameters.textureUnitContext();

    final JCGLShadersType g_sh = g.getShaders();
    final JCGLTexturesType g_tex = g.getTextures();

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
