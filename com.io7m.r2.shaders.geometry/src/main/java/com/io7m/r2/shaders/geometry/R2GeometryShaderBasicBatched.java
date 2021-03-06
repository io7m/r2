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

package com.io7m.r2.shaders.geometry;

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.vectors.PVectors3D;
import com.io7m.jtensors.core.parameterized.vectors.PVectors4D;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.matrices.R2MatricesObserverValuesType;
import com.io7m.r2.projections.R2Projections;
import com.io7m.r2.shaders.api.R2ShaderParametersMaterialType;
import com.io7m.r2.shaders.api.R2ShaderParametersViewType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.api.R2ShaderStateChecking;
import com.io7m.r2.shaders.geometry.api.R2AbstractGeometryShaderBatched;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_MATRIX_4;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_VECTOR_3;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_VECTOR_4;
import static com.io7m.jcanephora.core.JCGLType.TYPE_SAMPLER_2D;
import static com.io7m.r2.shaders.api.R2ShaderParameters.checkUniformParameterCount;
import static com.io7m.r2.shaders.api.R2ShaderParameters.uniform;

/**
 * Basic geometry shader for batched instances.
 */

public final class R2GeometryShaderBasicBatched
  extends R2AbstractGeometryShaderBatched<R2GeometryShaderBasicParameters>
{
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_transform_view;
  private final JCGLProgramUniformType u_transform_projection;
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

  private R2GeometryShaderBasicBatched(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.geometry.R2GeometryShaderBasicBatched",
      "com.io7m.r2.shaders.geometry.api/R2GeometryBatched.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.geometry/R2GeometryBasicBatched.frag",
      in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_transform_projection =
      uniform(p, "R2_view.transform_projection", TYPE_FLOAT_MATRIX_4);
    this.u_transform_view =
      uniform(p, "R2_view.transform_view", TYPE_FLOAT_MATRIX_4);
    this.u_depth_coefficient =
      uniform(p, "R2_view.depth_coefficient", TYPE_FLOAT);

    this.u_emission_amount =
      uniform(p, "R2_basic_surface_parameters.emission_amount", TYPE_FLOAT);
    this.u_albedo_color =
      uniform(
        p,
        "R2_basic_surface_parameters.albedo_color",
        TYPE_FLOAT_VECTOR_4);
    this.u_albedo_mix =
      uniform(p, "R2_basic_surface_parameters.albedo_mix", TYPE_FLOAT);
    this.u_specular_color =
      uniform(
        p,
        "R2_basic_surface_parameters.specular_color",
        TYPE_FLOAT_VECTOR_3);
    this.u_specular_exponent =
      uniform(p, "R2_basic_surface_parameters.specular_exponent", TYPE_FLOAT);
    this.u_alpha_discard_threshold =
      uniform(
        p,
        "R2_basic_surface_parameters.alpha_discard_threshold",
        TYPE_FLOAT);

    this.u_texture_normal =
      uniform(p, "R2_surface_textures.normal", TYPE_SAMPLER_2D);

    this.u_texture_albedo =
      uniform(p, "R2_basic_surface_textures.albedo", TYPE_SAMPLER_2D);
    this.u_texture_specular =
      uniform(p, "R2_basic_surface_textures.specular", TYPE_SAMPLER_2D);
    this.u_texture_emission =
      uniform(p, "R2_basic_surface_textures.emission", TYPE_SAMPLER_2D);

    checkUniformParameterCount(p, 13);
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

  public static R2GeometryShaderBasicBatched create(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2GeometryShaderBasicBatched(
      in_shaders, in_shader_env, in_pool, R2ShaderStateChecking.STATE_CHECK);
  }

  @Override
  public Class<R2GeometryShaderBasicParameters> shaderParametersType()
  {
    return R2GeometryShaderBasicParameters.class;
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
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_view,
      PMatrices4x4D.toUnparameterized(matrices.matrixView()));
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection,
      PMatrices4x4D.toUnparameterized(matrices.matrixProjection()));
  }

  @Override
  protected void onActualReceiveMaterialValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersMaterialType<R2GeometryShaderBasicParameters> mat_parameters)
  {
    final JCGLTexturesType g_tex = g.textures();
    final JCGLShadersType g_sh = g.shaders();

    final JCGLTextureUnitContextMutableType tc =
      mat_parameters.textureUnitContext();
    final R2GeometryShaderBasicParameters values =
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

    g_sh.shaderUniformPutVector4f(
      this.u_albedo_color,
      PVectors4D.toUnparameterized(values.albedoColor()));
    g_sh.shaderUniformPutFloat(
      this.u_albedo_mix,
      (float) values.albedoMix());

    g_sh.shaderUniformPutFloat(
      this.u_emission_amount,
      (float) values.emission());

    g_sh.shaderUniformPutVector3f(
      this.u_specular_color,
      PVectors3D.toUnparameterized(values.specularColor()));
    g_sh.shaderUniformPutFloat(
      this.u_specular_exponent,
      (float) values.specularExponent());

    g_sh.shaderUniformPutFloat(
      this.u_alpha_discard_threshold,
      (float) values.alphaDiscardThreshold());
  }
}
