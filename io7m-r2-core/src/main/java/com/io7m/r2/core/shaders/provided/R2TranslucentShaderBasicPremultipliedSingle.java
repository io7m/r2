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
import com.io7m.jcanephora.core.JCGLBlendEquation;
import com.io7m.jcanephora.core.JCGLBlendFunction;
import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.renderstate.JCGLBlendState;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.core.shaders.types.R2ShaderTranslucentInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderTranslucentInstanceSingleVerifier;

import java.util.Optional;

/**
 * Simple translucent shader for single instances.
 */

public final class R2TranslucentShaderBasicPremultipliedSingle
  extends R2AbstractShader<R2TranslucentShaderBasicParameters>
  implements R2ShaderTranslucentInstanceSingleType<R2TranslucentShaderBasicParameters>
{
  private final JCGLProgramUniformType u_transform_uv;
  private final JCGLProgramUniformType u_transform_normal;
  private final JCGLProgramUniformType u_transform_modelview;
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_transform_view;
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_texture_albedo;
  private final JCGLProgramUniformType u_color;
  private final JCGLProgramUniformType u_fade_positive_eye_z_near;
  private final JCGLProgramUniformType u_fade_positive_eye_z_far;

  private R2TranslucentShaderBasicPremultipliedSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2TranslucentBasicPremultiplied",
      "com.io7m.r2.shaders.core/R2SurfaceSingle.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2TranslucentBasicPremultiplied.frag");

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_transform_projection = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_projection", JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_view = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_view", JCGLType.TYPE_FLOAT_MATRIX_4);

    this.u_transform_modelview =
      R2ShaderParameters.getUniformChecked(
        p, "R2_surface_matrices_instance.transform_modelview",
        JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_normal =
      R2ShaderParameters.getUniformChecked(
        p, "R2_surface_matrices_instance.transform_normal",
        JCGLType.TYPE_FLOAT_MATRIX_3);
    this.u_transform_uv =
      R2ShaderParameters.getUniformChecked(
        p, "R2_surface_matrices_instance.transform_uv",
        JCGLType.TYPE_FLOAT_MATRIX_3);

    this.u_depth_coefficient = R2ShaderParameters.getUniformChecked(
      p, "R2_view.depth_coefficient", JCGLType.TYPE_FLOAT);

    this.u_texture_albedo = R2ShaderParameters.getUniformChecked(
      p, "R2_texture_albedo", JCGLType.TYPE_SAMPLER_2D);
    this.u_color = R2ShaderParameters.getUniformChecked(
      p, "R2_color", JCGLType.TYPE_FLOAT_VECTOR_4);
    this.u_fade_positive_eye_z_near = R2ShaderParameters.getUniformChecked(
      p, "R2_fade_positive_eye_z_near", JCGLType.TYPE_FLOAT);
    this.u_fade_positive_eye_z_far = R2ShaderParameters.getUniformChecked(
      p, "R2_fade_positive_eye_z_far", JCGLType.TYPE_FLOAT);

    R2ShaderParameters.checkUniformParameterCount(p, 10);
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

  public static R2ShaderTranslucentInstanceSingleType<R2TranslucentShaderBasicParameters>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return R2ShaderTranslucentInstanceSingleVerifier.newVerifier(
      new R2TranslucentShaderBasicPremultipliedSingle(
        in_shaders, in_shader_env, in_pool));
  }

  @Override
  public Class<R2TranslucentShaderBasicParameters> shaderParametersType()
  {
    return R2TranslucentShaderBasicParameters.class;
  }

  @Override
  public void onReceiveViewValues(
    final JCGLShadersType g_sh,
    final R2MatricesObserverValuesType m,
    final AreaInclusiveUnsignedLType viewport)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);
    NullCheck.notNull(viewport);

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(m.projection()));
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_view, m.matrixView());
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection, m.matrixProjection());
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {

  }

  @Override
  public void onReceiveMaterialValues(
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final JCGLTextureUnitContextMutableType tc,
    final R2TranslucentShaderBasicParameters values)
  {
    NullCheck.notNull(g_tex);
    NullCheck.notNull(g_sh);
    NullCheck.notNull(tc);
    NullCheck.notNull(values);

    final JCGLTextureUnitType unit_albedo =
      tc.unitContextBindTexture2D(g_tex, values.albedoTexture().texture());

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_albedo, unit_albedo);

    g_sh.shaderUniformPutVector4f(
      this.u_color, values.albedoColor());

    g_sh.shaderUniformPutFloat(
      this.u_fade_positive_eye_z_far, values.fadeZFar());
    g_sh.shaderUniformPutFloat(
      this.u_fade_positive_eye_z_near, values.fadeZNear());
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
    g_sh.shaderUniformPutMatrix3x3f(
      this.u_transform_normal, m.matrixNormal());
    g_sh.shaderUniformPutMatrix3x3f(
      this.u_transform_uv, m.matrixUV());
  }

  @Override
  public Optional<JCGLBlendState> suggestedBlendState()
  {
    return Optional.of(JCGLBlendState.of(
      JCGLBlendFunction.BLEND_ONE,
      JCGLBlendFunction.BLEND_ONE,
      JCGLBlendFunction.BLEND_ONE_MINUS_SOURCE_ALPHA,
      JCGLBlendFunction.BLEND_ONE_MINUS_SOURCE_ALPHA,
      JCGLBlendEquation.BLEND_EQUATION_ADD,
      JCGLBlendEquation.BLEND_EQUATION_ADD));
  }
}
