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
 * A vertical RGBA box blur shader.
 */

public final class R2ShaderFilterBoxBlurVertical4f extends
  R2AbstractShader<R2ShaderFilterBoxBlurParametersType>
  implements R2ShaderFilterType<R2ShaderFilterBoxBlurParametersType>
{
  private final JCGLProgramUniformType u_texture;
  private final JCGLProgramUniformType u_blur_size;
  private       JCGLTextureUnitType    unit_texture;

  private R2ShaderFilterBoxBlurVertical4f(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_sources,
      in_pool,
      "R2FilterBoxBlurVertical4f",
      "R2FilterBoxBlurVertical4f.vert",
      Optional.empty(),
      "R2FilterBoxBlurVertical4f.frag");

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 2);

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

  public static R2ShaderFilterType<R2ShaderFilterBoxBlurParametersType>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return R2ShaderFilterVerifier.newVerifier(
      new R2ShaderFilterBoxBlurVertical4f(in_shaders, in_sources, in_pool));
  }

  @Override
  public Class<R2ShaderFilterBoxBlurParametersType>
  getShaderParametersType()
  {
    return R2ShaderFilterBoxBlurParametersType.class;
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
    final R2ShaderFilterBoxBlurParametersType values)
  {
    NullCheck.notNull(g_tex);
    NullCheck.notNull(tc);
    NullCheck.notNull(g_sh);
    NullCheck.notNull(values);

    this.unit_texture =
      tc.unitContextBindTexture2D(g_tex, values.getTexture().get());

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture, this.unit_texture);
    g_sh.shaderUniformPutFloat(
      this.u_blur_size, values.getBlurRadius());
  }
}
