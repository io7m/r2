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
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
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
 * A vertical RGBA box blur shader.
 */

public final class R2ShaderFilterBoxBlurVertical4f extends
  R2AbstractShader<R2ShaderFilterBoxBlurParameters>
  implements R2ShaderFilterType<R2ShaderFilterBoxBlurParameters>
{
  private final JCGLProgramUniformType u_texture;
  private final JCGLProgramUniformType u_blur_size;

  private R2ShaderFilterBoxBlurVertical4f(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2ShaderFilterBoxBlurVertical4f",
      "com.io7m.r2.shaders.core/R2Filter.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2FilterBoxBlurVertical4f.frag");

    final JCGLProgramShaderUsableType p = this.shaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 2);

    this.u_texture =
      R2ShaderParameters.getUniformChecked(
        p,
        "R2_texture",
        JCGLType.TYPE_SAMPLER_2D);
    this.u_blur_size =
      R2ShaderParameters.getUniformChecked(
        p,
        "R2_blur_size",
        JCGLType.TYPE_FLOAT);
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

  public static R2ShaderFilterType<R2ShaderFilterBoxBlurParameters>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return R2ShaderFilterVerifier.newVerifier(
      new R2ShaderFilterBoxBlurVertical4f(in_shaders, in_shader_env, in_pool));
  }

  @Override
  public Class<R2ShaderFilterBoxBlurParameters>
  shaderParametersType()
  {
    return R2ShaderFilterBoxBlurParameters.class;
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
    final R2ShaderParametersFilterType<R2ShaderFilterBoxBlurParameters> parameters)
  {
    NullCheck.notNull(g, "G33");
    NullCheck.notNull(parameters, "Filter parameters");

    final R2ShaderFilterBoxBlurParameters values =
      parameters.values();
    final JCGLTextureUnitContextMutableType tc =
      parameters.textureUnitContext();

    final JCGLShadersType g_sh = g.shaders();
    final JCGLTexturesType g_tex = g.textures();

    final JCGLTextureUnitType unit_texture =
      tc.unitContextBindTexture2D(g_tex, values.texture().texture());

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture, unit_texture);
    g_sh.shaderUniformPutFloat(
      this.u_blur_size, (float) values.blurRadius());
  }
}
