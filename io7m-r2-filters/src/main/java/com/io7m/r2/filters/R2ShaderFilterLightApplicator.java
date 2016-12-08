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
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;

import java.util.Optional;

/**
 * A light applicator shader.
 */

public final class R2ShaderFilterLightApplicator extends
  R2AbstractShader<R2ShaderFilterLightApplicatorParametersType>
  implements R2ShaderFilterType<R2ShaderFilterLightApplicatorParametersType>
{
  private final JCGLProgramUniformType u_texture_albedo;
  private final JCGLProgramUniformType u_texture_specular;
  private final JCGLProgramUniformType u_texture_diffuse;
  private       JCGLTextureUnitType    unit_albedo;
  private       JCGLTextureUnitType    unit_diffuse;
  private       JCGLTextureUnitType    unit_specular;

  private R2ShaderFilterLightApplicator(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_sources,
      in_pool,
      "R2FilterLightApplicator",
      "R2FilterLightApplicator.vert",
      Optional.empty(),
      "R2FilterLightApplicator.frag");

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 3);

    this.u_texture_albedo =
      R2ShaderParameters.getUniformChecked(p, "R2_textures_albedo", JCGLType.TYPE_SAMPLER_2D);
    this.u_texture_specular =
      R2ShaderParameters.getUniformChecked(p, "R2_textures_specular", JCGLType.TYPE_SAMPLER_2D);
    this.u_texture_diffuse =
      R2ShaderParameters.getUniformChecked(p, "R2_textures_diffuse", JCGLType.TYPE_SAMPLER_2D);
  }

  /**
   * Construct a new shader.
   *
   * @param in_shaders A shader interface
   * @param in_sources Shader sources
   * @param in_pool    The ID pool
   *
   * @return A new shader
   */

  public static R2ShaderFilterType<R2ShaderFilterLightApplicatorParametersType>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return R2ShaderFilterVerifier.newVerifier(
      new R2ShaderFilterLightApplicator(in_shaders, in_sources, in_pool));
  }

  @Override
  public Class<R2ShaderFilterLightApplicatorParametersType>
  getShaderParametersType()
  {
    return R2ShaderFilterLightApplicatorParametersType.class;
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
    final R2ShaderFilterLightApplicatorParametersType values)
  {
    NullCheck.notNull(g_tex);
    NullCheck.notNull(tc);
    NullCheck.notNull(g_sh);
    NullCheck.notNull(values);

    this.unit_albedo =
      tc.unitContextBindTexture2D(g_tex, values.getAlbedoTexture().get());
    this.unit_diffuse =
      tc.unitContextBindTexture2D(g_tex, values.getDiffuseTexture().get());
    this.unit_specular =
      tc.unitContextBindTexture2D(g_tex, values.getSpecularTexture().get());

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_albedo, this.unit_albedo);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_diffuse, this.unit_diffuse);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_specular, this.unit_specular);
  }
}
