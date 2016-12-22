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

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.VectorI4F;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderParametersMaterialType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersViewType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

/**
 * Shader for masking single instances.
 */

public final class R2MaskShaderBatched extends R2AbstractShader<Unit>
  implements R2ShaderInstanceBatchedType<Unit>
{
  private static final VectorI4F WHITE = new VectorI4F(1.0f, 1.0f, 1.0f, 1.0f);

  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_transform_view;
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_frag_color;

  private R2MaskShaderBatched(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2MaskBatched",
      "com.io7m.r2.shaders.core/R2MaskBatched.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2Mask.frag");

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_transform_projection = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_projection", JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_view = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_view", JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_depth_coefficient = R2ShaderParameters.getUniformChecked(
      p, "R2_view.depth_coefficient", JCGLType.TYPE_FLOAT);
    this.u_frag_color = R2ShaderParameters.getUniformChecked(
      p, "R2_frag_color", JCGLType.TYPE_FLOAT_VECTOR_4);

    R2ShaderParameters.checkUniformParameterCount(p, 4);
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

  public static R2ShaderInstanceBatchedType<Unit>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return R2ShaderInstanceBatchedVerifier.newVerifier(
      new R2MaskShaderBatched(in_shaders, in_shader_env, in_pool));
  }

  @Override
  public Class<Unit> shaderParametersType()
  {
    return Unit.class;
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
    final AreaInclusiveUnsignedLType viewport =
      view_parameters.viewport();

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(matrices.projection()));
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_view, matrices.matrixView());
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection, matrices.matrixProjection());
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {

  }

  @Override
  public void onReceiveMaterialValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersMaterialType<Unit> mat_parameters)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(mat_parameters);

    final JCGLShadersType g_sh = g.getShaders();
    g_sh.shaderUniformPutVector4f(this.u_frag_color, WHITE);
  }
}
