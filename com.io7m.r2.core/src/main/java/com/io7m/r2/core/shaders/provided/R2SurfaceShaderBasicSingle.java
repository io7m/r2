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
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.shaders.abstracts.R2AbstractInstanceShaderSingle;
import com.io7m.r2.core.shaders.abstracts.R2ShaderStateChecking;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderParametersMaterialType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersViewType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

/**
 * Basic deferred surface shader for single instances.
 */

public final class R2SurfaceShaderBasicSingle
  extends R2AbstractInstanceShaderSingle<R2SurfaceShaderBasicParameters>
{
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_transform_normal;
  private final JCGLProgramUniformType u_transform_modelview;
  private final JCGLProgramUniformType u_transform_view;
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_transform_uv;
  private final JCGLProgramUniformType u_emission_amount;
  private final JCGLProgramUniformType u_albedo_color;
  private final JCGLProgramUniformType u_albedo_mix;
  private final JCGLProgramUniformType u_specular_color;
  private final JCGLProgramUniformType u_specular_exponent;
  private final JCGLProgramUniformType u_texture_albedo;
  private final JCGLProgramUniformType u_texture_normal;
  private final JCGLProgramUniformType u_texture_specular;
  private final JCGLProgramUniformType u_texture_emission;
  private final JCGLProgramUniformType u_alpha_discard_threshold;

  private R2SurfaceShaderBasicSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2SurfaceShaderBasicSingle",
      "com.io7m.r2.shaders.core/R2SurfaceSingle.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2SurfaceBasicSingle.frag",
      in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 16);

    this.u_transform_projection = R2ShaderParameters.uniform(
      p, "R2_view.transform_projection", JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_view = R2ShaderParameters.uniform(
      p, "R2_view.transform_view", JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_depth_coefficient = R2ShaderParameters.uniform(
      p, "R2_view.depth_coefficient", JCGLType.TYPE_FLOAT);

    this.u_transform_normal = R2ShaderParameters.uniform(
      p,
      "R2_surface_matrices_instance.transform_normal",
      JCGLType.TYPE_FLOAT_MATRIX_3);
    this.u_transform_modelview = R2ShaderParameters.uniform(
      p,
      "R2_surface_matrices_instance.transform_modelview",
      JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_uv = R2ShaderParameters.uniform(
      p,
      "R2_surface_matrices_instance.transform_uv",
      JCGLType.TYPE_FLOAT_MATRIX_3);

    this.u_emission_amount = R2ShaderParameters.uniform(
      p, "R2_basic_surface_parameters.emission_amount", JCGLType.TYPE_FLOAT);
    this.u_albedo_color = R2ShaderParameters.uniform(
      p,
      "R2_basic_surface_parameters.albedo_color",
      JCGLType.TYPE_FLOAT_VECTOR_4);
    this.u_albedo_mix = R2ShaderParameters.uniform(
      p, "R2_basic_surface_parameters.albedo_mix", JCGLType.TYPE_FLOAT);
    this.u_specular_color = R2ShaderParameters.uniform(
      p,
      "R2_basic_surface_parameters.specular_color",
      JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_specular_exponent = R2ShaderParameters.uniform(
      p, "R2_basic_surface_parameters.specular_exponent", JCGLType.TYPE_FLOAT);
    this.u_alpha_discard_threshold = R2ShaderParameters.uniform(
      p,
      "R2_basic_surface_parameters.alpha_discard_threshold",
      JCGLType.TYPE_FLOAT);

    this.u_texture_normal = R2ShaderParameters.uniform(
      p, "R2_surface_textures.normal", JCGLType.TYPE_SAMPLER_2D);
    this.u_texture_albedo = R2ShaderParameters.uniform(
      p, "R2_basic_surface_textures.albedo", JCGLType.TYPE_SAMPLER_2D);
    this.u_texture_specular = R2ShaderParameters.uniform(
      p, "R2_basic_surface_textures.specular", JCGLType.TYPE_SAMPLER_2D);
    this.u_texture_emission = R2ShaderParameters.uniform(
      p, "R2_basic_surface_textures.emission", JCGLType.TYPE_SAMPLER_2D);
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

  public static R2SurfaceShaderBasicSingle create(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2SurfaceShaderBasicSingle(
      in_shaders, in_shader_env, in_pool, R2ShaderStateChecking.STATE_CHECK);
  }

  @Override
  public Class<R2SurfaceShaderBasicParameters> shaderParametersType()
  {
    return R2SurfaceShaderBasicParameters.class;
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
    final R2ShaderParametersMaterialType<R2SurfaceShaderBasicParameters> mat_parameters)
  {
    final JCGLTexturesType g_tex = g.textures();
    final JCGLShadersType g_sh = g.shaders();

    final JCGLTextureUnitContextMutableType tc =
      mat_parameters.textureUnitContext();
    final R2SurfaceShaderBasicParameters values =
      mat_parameters.values();

    final JCGLTextureUnitType unit_albedo =
      tc.unitContextBindTexture2D(g_tex, values.albedoTexture().texture());
    final JCGLTextureUnitType unit_emission =
      tc.unitContextBindTexture2D(g_tex, values.emissionTexture().texture());
    final JCGLTextureUnitType unit_normal =
      tc.unitContextBindTexture2D(g_tex, values.normalTexture().texture());
    final JCGLTextureUnitType unit_specular =
      tc.unitContextBindTexture2D(g_tex, values.specularTexture().texture());

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_albedo, unit_albedo);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_emission, unit_emission);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_normal, unit_normal);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_specular, unit_specular);

    g_sh.shaderUniformPutPVector4f(
      this.u_albedo_color, values.albedoColor());
    g_sh.shaderUniformPutFloat(
      this.u_albedo_mix, (float) values.albedoMix());

    g_sh.shaderUniformPutFloat(
      this.u_emission_amount, (float) values.emission());

    g_sh.shaderUniformPutPVector3f(
      this.u_specular_color, values.specularColor());
    g_sh.shaderUniformPutFloat(
      this.u_specular_exponent, (float) values.specularExponent());

    g_sh.shaderUniformPutFloat(
      this.u_alpha_discard_threshold, (float) values.alphaDiscardThreshold());
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
