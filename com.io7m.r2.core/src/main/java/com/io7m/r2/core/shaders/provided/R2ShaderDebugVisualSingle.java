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
import com.io7m.jcanephora.core.JCGLType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
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
import com.io7m.r2.spaces.R2SpaceRGBAType;

import java.util.Optional;

/**
 * Debug visualization shader for single instances.
 */

public final class R2ShaderDebugVisualSingle extends
  R2AbstractShader<PVector4D<R2SpaceRGBAType>>
  implements R2ShaderInstanceSingleType<PVector4D<R2SpaceRGBAType>>
{
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_transform_normal;
  private final JCGLProgramUniformType u_transform_modelview;
  private final JCGLProgramUniformType u_transform_view;
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_transform_uv;
  private final JCGLProgramUniformType u_color;

  private R2ShaderDebugVisualSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2ShaderDebugVisualSingle",
      "com.io7m.r2.shaders.core/R2SurfaceSingle.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2DebugVisualConstant.frag");

    final JCGLProgramShaderUsableType p = this.shaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 7);

    this.u_transform_projection = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_projection", JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_view = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_view", JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_depth_coefficient = R2ShaderParameters.getUniformChecked(
      p, "R2_view.depth_coefficient", JCGLType.TYPE_FLOAT);

    this.u_transform_normal = R2ShaderParameters.getUniformChecked(
      p,
      "R2_surface_matrices_instance.transform_normal",
      JCGLType.TYPE_FLOAT_MATRIX_3);
    this.u_transform_modelview = R2ShaderParameters.getUniformChecked(
      p,
      "R2_surface_matrices_instance.transform_modelview",
      JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_uv = R2ShaderParameters.getUniformChecked(
      p,
      "R2_surface_matrices_instance.transform_uv",
      JCGLType.TYPE_FLOAT_MATRIX_3);

    this.u_color = R2ShaderParameters.getUniformChecked(
      p, "R2_color", JCGLType.TYPE_FLOAT_VECTOR_4);
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

  public static R2ShaderInstanceSingleType<PVector4D<R2SpaceRGBAType>> newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return R2ShaderInstanceSingleVerifier.newVerifier(
      new R2ShaderDebugVisualSingle(in_shaders, in_shader_env, in_pool));
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<PVector4D<R2SpaceRGBAType>> shaderParametersType()
  {
    final Class<?> c = PVector4D.class;
    return (Class<PVector4D<R2SpaceRGBAType>>) c;
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
    NullCheck.notNull(g, "G33");
    NullCheck.notNull(view_parameters, "View parameters");

    final JCGLShadersType g_sh = g.shaders();
    final R2MatricesObserverValuesType matrices =
      view_parameters.observerMatrices();

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(matrices.projection()));
    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_view, matrices.matrixView());
    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_projection, matrices.matrixProjection());
  }

  @Override
  public void onReceiveMaterialValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersMaterialType<PVector4D<R2SpaceRGBAType>> mat_parameters)
  {
    NullCheck.notNull(g, "G33");
    NullCheck.notNull(mat_parameters, "Material parameters");

    final JCGLShadersType g_sh = g.shaders();
    g_sh.shaderUniformPutPVector4f(this.u_color, mat_parameters.values());
  }

  @Override
  public void onReceiveInstanceTransformValues(
    final JCGLInterfaceGL33Type g,
    final R2MatricesInstanceSingleValuesType m)
  {
    NullCheck.notNull(g, "G33");
    NullCheck.notNull(m, "Instance matrices");

    final JCGLShadersType g_sh = g.shaders();

    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_modelview, m.matrixModelView());
    g_sh.shaderUniformPutPMatrix3x3f(
      this.u_transform_normal, m.matrixNormal());
    g_sh.shaderUniformPutPMatrix3x3f(
      this.u_transform_uv, m.matrixUV());
  }
}
