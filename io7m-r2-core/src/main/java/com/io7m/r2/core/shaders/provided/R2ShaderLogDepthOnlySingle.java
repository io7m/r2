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
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderParametersMaterialType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersViewType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Map;
import java.util.Optional;

/**
 * A shader that does not output any color data, but does write a logarithmic
 * depth value.
 */

public final class R2ShaderLogDepthOnlySingle extends R2AbstractShader<Unit>
  implements R2ShaderInstanceSingleType<Unit>
{
  private final JCGLProgramUniformType u_transform_modelview;
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_transform_view;
  private final JCGLProgramUniformType u_depth_coefficient;

  private R2ShaderLogDepthOnlySingle(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2ShaderLogDepthOnlySingle",
      "com.io7m.r2.shaders.core/R2LogDepthOnlySingle.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2LogDepthOnlySingle.frag");

    final JCGLProgramShaderUsableType p = this.shaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 4);

    final Map<String, JCGLProgramUniformType> us = p.getUniforms();
    this.u_transform_modelview = NullCheck.notNull(
      us.get("R2_transform_modelview"));
    this.u_transform_projection = NullCheck.notNull(
      us.get("R2_view.transform_projection"));
    this.u_transform_view = NullCheck.notNull(
      us.get("R2_view.transform_view"));
    this.u_depth_coefficient = NullCheck.notNull(
      us.get("R2_view.depth_coefficient"));
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

  public static R2ShaderInstanceSingleType<Unit> newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return R2ShaderInstanceSingleVerifier.newVerifier(
      new R2ShaderLogDepthOnlySingle(in_shaders, in_shader_env, in_pool));
  }

  @Override
  public Class<Unit> shaderParametersType()
  {
    return Unit.class;
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {
    // Nothing
  }

  @Override
  public void onReceiveViewValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersViewType view_parameters)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(view_parameters);

    final JCGLShadersType g_sh = g.getShaders();
    final R2MatricesObserverValuesType matrices =
      view_parameters.observerMatrices();

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(matrices.projection()));
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_view, matrices.matrixView());
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection, matrices.matrixProjection());
  }

  @Override
  public void onReceiveMaterialValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersMaterialType<Unit> mat_parameters)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(mat_parameters);
  }

  @Override
  public void onReceiveInstanceTransformValues(
    final JCGLShadersType g_sh,
    final R2MatricesInstanceSingleValuesType m)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);

    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_modelview, m.matrixModelView());
  }
}
