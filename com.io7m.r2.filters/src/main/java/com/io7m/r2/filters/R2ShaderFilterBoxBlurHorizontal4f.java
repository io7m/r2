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
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.shaders.abstracts.R2AbstractFilterShader;
import com.io7m.r2.core.shaders.abstracts.R2ShaderStateChecking;
import com.io7m.r2.core.shaders.types.R2ShaderParametersFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT;
import static com.io7m.jcanephora.core.JCGLType.TYPE_SAMPLER_2D;
import static com.io7m.r2.core.shaders.types.R2ShaderParameters.checkUniformParameterCount;
import static com.io7m.r2.core.shaders.types.R2ShaderParameters.uniform;

/**
 * A horizontal RGBA box blur shader.
 */

public final class R2ShaderFilterBoxBlurHorizontal4f
  extends R2AbstractFilterShader<R2ShaderFilterBoxBlurParameters>
{
  private final JCGLProgramUniformType u_texture;
  private final JCGLProgramUniformType u_blur_size;

  private R2ShaderFilterBoxBlurHorizontal4f(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2ShaderFilterBoxBlurHorizontal4f",
      "com.io7m.r2.shaders.core/R2Filter.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2FilterBoxBlurHorizontal4f.frag",
      in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_texture = uniform(p, "R2_texture", TYPE_SAMPLER_2D);
    this.u_blur_size = uniform(p, "R2_blur_size", TYPE_FLOAT);

    checkUniformParameterCount(p, 2);
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

  public static R2ShaderFilterBoxBlurHorizontal4f
  create(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2ShaderFilterBoxBlurHorizontal4f(
      in_shaders, in_shader_env, in_pool, R2ShaderStateChecking.STATE_CHECK);
  }

  @Override
  public Class<R2ShaderFilterBoxBlurParameters>
  shaderParametersType()
  {
    return R2ShaderFilterBoxBlurParameters.class;
  }

  @Override
  protected void onActualReceiveFilterValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersFilterType<R2ShaderFilterBoxBlurParameters> parameters)
  {
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
