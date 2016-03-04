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

package com.io7m.r2.core.shaders;

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2AbstractShader;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2TextureUnitContextMutableType;
import org.valid4j.Assertive;

import java.util.Map;
import java.util.Optional;

/**
 * A horizontal RGBA box blur shader.
 */

public final class R2ShaderFilterBoxBlurHorizontal4f extends
  R2AbstractShader<R2ShaderFilterBoxBlurParametersType>
  implements R2ShaderScreenType<R2ShaderFilterBoxBlurParametersType>
{
  private final JCGLProgramUniformType u_texture;
  private final JCGLProgramUniformType u_blur_size;
  private       JCGLTextureUnitType    unit_texture;

  private R2ShaderFilterBoxBlurHorizontal4f(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_sources,
      in_pool,
      "R2FilterBoxBlurHorizontal4f",
      "R2FilterBoxBlurHorizontal4f.vert",
      Optional.empty(),
      "R2FilterBoxBlurHorizontal4f.frag");

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    final Map<String, JCGLProgramUniformType> us = p.getUniforms();
    Assertive.ensure(
      us.size() == 2,
      "Expected number of parameters is 2 (got %d)",
      Integer.valueOf(us.size()));

    this.u_texture =
      R2ShaderParameters.getUniformChecked(p, "R2_texture");
    this.u_blur_size =
      R2ShaderParameters.getUniformChecked(p, "R2_blur_size");
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

  public static R2ShaderFilterBoxBlurHorizontal4f
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return new R2ShaderFilterBoxBlurHorizontal4f(
      in_shaders, in_sources, in_pool);
  }

  @Override
  public Class<R2ShaderFilterBoxBlurParametersType>
  getShaderParametersType()
  {
    return R2ShaderFilterBoxBlurParametersType.class;
  }

  /**
   * Bind any textures needed for execution.
   *
   * @param g_tex  A texture interface
   * @param uc     A texture interface
   * @param values The parameters
   */

  public void setTextures(
    final JCGLTexturesType g_tex,
    final R2TextureUnitContextMutableType uc,
    final R2ShaderFilterBoxBlurParametersType values)
  {
    NullCheck.notNull(uc);
    NullCheck.notNull(values);

    this.unit_texture =
      uc.unitContextBindTexture2D(g_tex, values.getTexture());
  }

  /**
   * Set any shader parameters needed for execution.
   *
   * @param g_sh   A shader interface
   * @param values The parameters
   */

  public void setValues(
    final JCGLShadersType g_sh,
    final R2ShaderFilterBoxBlurParametersType values)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(values);

    g_sh.shaderUniformPutTexture2DUnit(this.u_texture, this.unit_texture);
    g_sh.shaderUniformPutFloat(this.u_blur_size, values.getBlurRadius());
  }
}
