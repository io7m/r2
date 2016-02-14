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
import com.io7m.r2.core.R2ShaderParameters;
import com.io7m.r2.core.R2ShaderScreenType;
import com.io7m.r2.core.R2ShaderSourcesType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureUnitContextMutableType;
import org.valid4j.Assertive;

import java.util.Map;
import java.util.Optional;

/**
 * An image sink shader that copies a texture to the first output.
 */

public final class R2ShaderTextureShow extends
  R2AbstractShader<R2Texture2DUsableType>
  implements R2ShaderScreenType<R2Texture2DUsableType>
{
  private final JCGLProgramUniformType u_texture;
  private       JCGLTextureUnitType    unit_texture;

  private R2ShaderTextureShow(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_sources,
      in_pool,
      "R2FilterTextureShow",
      "R2FilterTextureShow.vert",
      Optional.empty(),
      "R2FilterTextureShow.frag");

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    final Map<String, JCGLProgramUniformType> us = p.getUniforms();
    Assertive.ensure(
      us.size() == 1,
      "Expected number of parameters is 1 (got %d)",
      Integer.valueOf(us.size()));

    this.u_texture =
      R2ShaderParameters.getUniformChecked(p, "R2_texture");
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

  public static R2ShaderTextureShow
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return new R2ShaderTextureShow(in_shaders, in_sources, in_pool);
  }

  @Override
  public Class<R2Texture2DUsableType>
  getShaderParametersType()
  {
    return R2Texture2DUsableType.class;
  }

  /**
   * Bind any textures needed for execution.
   *
   * @param g_tex A texture interface
   * @param uc    A texture interface
   * @param c     The texture
   */

  public void setTextures(
    final JCGLTexturesType g_tex,
    final R2TextureUnitContextMutableType uc,
    final R2Texture2DUsableType c)
  {
    NullCheck.notNull(uc);
    NullCheck.notNull(c);

    this.unit_texture =
      uc.unitContextBindTexture2D(g_tex, c);
  }

  /**
   * Set any shader parameters needed for execution.
   *
   * @param g_sh   A shader interface
   * @param values The parameters
   */

  public void setValues(
    final JCGLShadersType g_sh,
    final R2Texture2DUsableType values)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(values);

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture, this.unit_texture);
  }
}
