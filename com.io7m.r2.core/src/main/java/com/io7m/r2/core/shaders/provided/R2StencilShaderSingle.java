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

package com.io7m.r2.core.shaders.provided;

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jfunctional.Unit;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.shaders.abstracts.R2AbstractInstanceShaderSingle;
import com.io7m.r2.core.shaders.abstracts.R2ShaderStateChecking;
import com.io7m.r2.core.shaders.types.R2ShaderParametersMaterialType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersViewType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_MATRIX_4;
import static com.io7m.r2.core.shaders.types.R2ShaderParameters.checkUniformParameterCount;
import static com.io7m.r2.core.shaders.types.R2ShaderParameters.uniform;

/**
 * A stencil-only shader implementation.
 */

public final class R2StencilShaderSingle
  extends R2AbstractInstanceShaderSingle<Unit>
{
  private final JCGLProgramUniformType u_transform_modelview;
  private final JCGLProgramUniformType u_transform_projection;

  private R2StencilShaderSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2StencilShaderSingle",
      "com.io7m.r2.shaders.core/R2StencilSingle.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2Nothing.frag",
      in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();
    this.u_transform_modelview =
      uniform(
        p,
        "R2_stencil_parameters.transform_modelview",
        TYPE_FLOAT_MATRIX_4);
    this.u_transform_projection =
      uniform(
        p,
        "R2_stencil_parameters.transform_projection",
        TYPE_FLOAT_MATRIX_4);

    checkUniformParameterCount(p, 2);
  }

  /**
   * Construct a new shader.
   *
   * @param in_shaders    A shader interface
   * @param in_shader_env A shader preprocessing environment
   * @param in_pool       The ID pool
   *
   * @return A new shader
   */

  public static R2StencilShaderSingle create(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2StencilShaderSingle(
      in_shaders, in_shader_env, in_pool, R2ShaderStateChecking.STATE_CHECK);
  }

  @Override
  public Class<Unit> shaderParametersType()
  {
    return Unit.class;
  }

  @Override
  protected void onActualReceiveViewValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersViewType view_parameters)
  {
    final JCGLShadersType g_sh = g.shaders();
    final R2MatricesObserverValuesType matrices =
      view_parameters.observerMatrices();

    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_projection, matrices.matrixProjection());
  }

  @Override
  protected void onActualReceiveMaterialValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersMaterialType<Unit> mat_parameters)
  {

  }

  @Override
  protected void onActualReceiveInstanceTransformValues(
    final JCGLInterfaceGL33Type g,
    final R2MatricesInstanceSingleValuesType m)
  {
    final JCGLShadersType g_sh = g.shaders();

    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_modelview, m.matrixModelView());
  }

}
