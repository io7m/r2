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

package com.io7m.r2.core.filters;

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2AbstractShader;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2ShaderParameters;
import com.io7m.r2.core.R2ShaderScreenType;
import com.io7m.r2.core.R2ShaderSourcesType;
import org.valid4j.Assertive;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A light applicator shader.
 */

public final class R2ShaderFilterLightApplicator extends
  R2AbstractShader<R2ShaderFilterLightApplicatorParameters>
  implements R2ShaderScreenType<R2ShaderFilterLightApplicatorParameters>
{
  private final JCGLProgramUniformType u_texture_albedo;
  private final JCGLProgramUniformType u_texture_specular;
  private final JCGLProgramUniformType u_texture_diffuse;

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
    final Map<String, JCGLProgramUniformType> us = p.getUniforms();
    Assertive.ensure(
      us.size() == 3,
      "Expected number of parameters is 3 (got %d)",
      Integer.valueOf(us.size()));

    this.u_texture_albedo =
      R2ShaderParameters.getUniformChecked(
        p, "R2_textures_albedo");
    this.u_texture_specular =
      R2ShaderParameters.getUniformChecked(
        p, "R2_textures_specular");
    this.u_texture_diffuse =
      R2ShaderParameters.getUniformChecked(
        p, "R2_textures_diffuse");
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

  public static R2ShaderFilterLightApplicator
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return new R2ShaderFilterLightApplicator(in_shaders, in_sources, in_pool);
  }

  @Override
  public Class<R2ShaderFilterLightApplicatorParameters>
  getShaderParametersType()
  {
    return R2ShaderFilterLightApplicatorParameters.class;
  }

  /**
   * Bind any textures needed for execution.
   *
   * @param g_tex  A texture interface
   * @param values The parameters
   */

  public void setTextures(
    final JCGLTexturesType g_tex,
    final R2ShaderFilterLightApplicatorParameters values)
  {
    NullCheck.notNull(g_tex);
    NullCheck.notNull(values);

    final List<JCGLTextureUnitType> units = g_tex.textureGetUnits();
    g_tex.texture2DBind(units.get(0), values.getAlbedoTexture().get());
    g_tex.texture2DBind(units.get(1), values.getDiffuseTexture().get());
    g_tex.texture2DBind(units.get(2), values.getSpecularTexture().get());
  }

  /**
   * Set any shader parameters needed for execution.
   *
   * @param g_sh   A shader interface
   * @param g_tex  A texture interface
   * @param values The parameters
   */

  public void setValues(
    final JCGLShadersType g_sh,
    final JCGLTexturesType g_tex,
    final R2ShaderFilterLightApplicatorParameters values)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(values);

    final List<JCGLTextureUnitType> units = g_tex.textureGetUnits();
    g_sh.shaderUniformPutTexture2DUnit(this.u_texture_albedo, units.get(0));
    g_sh.shaderUniformPutTexture2DUnit(this.u_texture_diffuse, units.get(1));
    g_sh.shaderUniformPutTexture2DUnit(this.u_texture_specular, units.get(2));
  }
}
