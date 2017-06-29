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

package com.io7m.r2.shaders.depth;

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.matrices.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.matrices.R2MatricesObserverValuesType;
import com.io7m.r2.projections.R2Projections;
import com.io7m.r2.shaders.api.R2ShaderParametersMaterialType;
import com.io7m.r2.shaders.api.R2ShaderParametersViewType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.api.R2ShaderStateChecking;
import com.io7m.r2.shaders.depth.api.R2AbstractDepthShaderSingle;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_MATRIX_3;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_MATRIX_4;
import static com.io7m.jcanephora.core.JCGLType.TYPE_SAMPLER_2D;
import static com.io7m.r2.shaders.api.R2ShaderParameters.checkUniformParameterCount;
import static com.io7m.r2.shaders.api.R2ShaderParameters.uniform;

/**
 * Basic depth shader for single instances.
 */

public final class R2DepthShaderBasicSingle
  extends R2AbstractDepthShaderSingle<R2DepthShaderBasicParameters>
{
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_transform_normal;
  private final JCGLProgramUniformType u_transform_modelview;
  private final JCGLProgramUniformType u_transform_view;
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_transform_uv;
  private final JCGLProgramUniformType u_texture_albedo;
  private final JCGLProgramUniformType u_alpha_discard_threshold;

  private R2DepthShaderBasicSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.depth.R2DepthBasicSingle",
      "com.io7m.r2.shaders.depth.api/R2DepthSingle.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.depth/R2DepthBasicSingle.frag",
      in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_transform_projection = uniform(
      p, "R2_view.transform_projection", TYPE_FLOAT_MATRIX_4);
    this.u_transform_view = uniform(
      p, "R2_view.transform_view", TYPE_FLOAT_MATRIX_4);
    this.u_depth_coefficient = uniform(
      p, "R2_view.depth_coefficient", TYPE_FLOAT);

    this.u_transform_normal = uniform(
      p, "R2_matrices_instance.transform_normal", TYPE_FLOAT_MATRIX_3);
    this.u_transform_modelview = uniform(
      p, "R2_matrices_instance.transform_modelview", TYPE_FLOAT_MATRIX_4);
    this.u_transform_uv = uniform(
      p, "R2_matrices_instance.transform_uv", TYPE_FLOAT_MATRIX_3);

    this.u_alpha_discard_threshold = uniform(
      p, "R2_alpha_discard_threshold", TYPE_FLOAT);
    this.u_texture_albedo = uniform(
      p, "R2_texture_albedo", TYPE_SAMPLER_2D);

    checkUniformParameterCount(p, 8);
  }

  /**
   * Construct a new shader.
   *
   * @param in_shaders    A shader interface
   * @param in_shader_env The shader preprocessing environment
   * @param in_pool       The ID pool
   *
   * @return A new shader
   */

  public static R2DepthShaderBasicSingle create(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2DepthShaderBasicSingle(
      in_shaders, in_shader_env, in_pool, R2ShaderStateChecking.STATE_CHECK);
  }

  @Override
  public Class<R2DepthShaderBasicParameters> shaderParametersType()
  {
    return R2DepthShaderBasicParameters.class;
  }

  @Override
  protected void onActualReceiveViewValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersViewType view_parameters)
  {
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
  protected void onActualReceiveMaterialValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersMaterialType<R2DepthShaderBasicParameters> mat_parameters)
  {
    final JCGLTexturesType g_tex = g.textures();
    final JCGLShadersType g_sh = g.shaders();

    final JCGLTextureUnitContextMutableType tc =
      mat_parameters.textureUnitContext();
    final R2DepthShaderBasicParameters values =
      mat_parameters.values();

    final JCGLTextureUnitType unit_albedo =
      tc.unitContextBindTexture2D(g_tex, values.albedoTexture().texture());

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_albedo, unit_albedo);
    g_sh.shaderUniformPutFloat(
      this.u_alpha_discard_threshold,
      (float) values.alphaDiscardThreshold());
  }

  @Override
  protected void onActualReceiveInstanceTransformValues(
    final JCGLInterfaceGL33Type g,
    final R2MatricesInstanceSingleValuesType m)
  {
    final JCGLShadersType g_sh = g.shaders();

    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_modelview, m.matrixModelView());
    g_sh.shaderUniformPutPMatrix3x3f(
      this.u_transform_normal, m.matrixNormal());
    g_sh.shaderUniformPutPMatrix3x3f(
      this.u_transform_uv, m.matrixUV());
  }
}
