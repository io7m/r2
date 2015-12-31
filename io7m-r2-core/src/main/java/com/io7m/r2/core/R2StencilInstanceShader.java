/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.core;

import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import org.valid4j.Assertive;

import java.util.Map;
import java.util.Optional;

/**
 * A stencil-only shader implementation.
 */

public final class R2StencilInstanceShader extends R2AbstractShader<Unit>
  implements
  R2ShaderInstanceType<Unit>
{
  private final JCGLProgramUniformType u_transform_modelview;
  private final JCGLProgramUniformType u_transform_projection;

  private R2StencilInstanceShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_sources,
      in_pool,
      "R2StencilInstance",
      "R2StencilInstance.vert",
      Optional.empty(),
      "R2StencilInstance.frag");

    final Map<String, JCGLProgramUniformType> us =
      this.getShaderProgram().getUniforms();
    Assertive.ensure(2 == us.size());

    this.u_transform_modelview = NullCheck.notNull(
      us.get("R2_stencil_parameters.transform_modelview"));
    this.u_transform_projection = NullCheck.notNull(
      us.get("R2_stencil_parameters.transform_projection"));
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

  public static R2ShaderInstanceType<Unit> newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return new R2StencilInstanceShader(in_shaders, in_sources, in_pool);
  }

  @Override
  public Class<Unit> getShaderParametersType()
  {
    return Unit.class;
  }

  @Override
  public void setMatricesView(
    final JCGLShadersType g_sh,
    final R2MatricesObserverValuesType m)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);

    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection, m.getMatrixProjection());
  }

  @Override
  public void setMatricesInstance(
    final JCGLShadersType g_sh,
    final R2MatricesInstanceValuesType m)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);

    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_modelview, m.getMatrixModelView());
  }
}
