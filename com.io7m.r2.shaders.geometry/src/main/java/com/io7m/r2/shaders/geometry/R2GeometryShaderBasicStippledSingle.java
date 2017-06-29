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
import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jtensors.core.unparameterized.vectors.Vector2D;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.matrices.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.matrices.R2MatricesObserverValuesType;
import com.io7m.r2.projections.R2Projections;
import com.io7m.r2.shaders.api.R2ShaderParametersMaterialType;
import com.io7m.r2.shaders.api.R2ShaderParametersViewType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.api.R2ShaderStateChecking;
import com.io7m.r2.shaders.geometry.api.R2AbstractGeometryShaderSingle;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_MATRIX_3;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_MATRIX_4;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_VECTOR_2;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_VECTOR_3;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_VECTOR_4;
import static com.io7m.jcanephora.core.JCGLType.TYPE_SAMPLER_2D;
import static com.io7m.r2.shaders.api.R2ShaderParameters.checkUniformParameterCount;
import static com.io7m.r2.shaders.api.R2ShaderParameters.uniform;

/**
 * Basic deferred surface shader for single instances with alpha stippling.
 */

public final class R2GeometryShaderBasicStippledSingle
  extends R2AbstractGeometryShaderSingle<R2GeometryShaderBasicStippledParameters>
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
  private final JCGLProgramUniformType u_texture_stipple;
  private final JCGLProgramUniformType u_stipple_noise_uv_scale;
  private final JCGLProgramUniformType u_stipple_threshold;
  private final JCGLProgramUniformType u_viewport_inverse_width;
  private final JCGLProgramUniformType u_viewport_inverse_height;
  private long viewport_w;
  private long viewport_h;

  private R2GeometryShaderBasicStippledSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.geometry.R2GeometryShaderBasicStippledSingle",
      "com.io7m.r2.shaders.geometry.api/R2GeometrySingle.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.geometry/R2GeometryBasicStippledSingle.frag",
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

    this.u_emission_amount = uniform(
      p, "R2_basic_surface_parameters.emission_amount", TYPE_FLOAT);
    this.u_albedo_color = uniform(
      p, "R2_basic_surface_parameters.albedo_color", TYPE_FLOAT_VECTOR_4);
    this.u_albedo_mix = uniform(
      p, "R2_basic_surface_parameters.albedo_mix", TYPE_FLOAT);
    this.u_specular_color = uniform(
      p, "R2_basic_surface_parameters.specular_color", TYPE_FLOAT_VECTOR_3);
    this.u_specular_exponent = uniform(
      p, "R2_basic_surface_parameters.specular_exponent", TYPE_FLOAT);
    this.u_alpha_discard_threshold = uniform(
      p, "R2_basic_surface_parameters.alpha_discard_threshold", TYPE_FLOAT);

    this.u_texture_normal =
      uniform(p, "R2_surface_textures.normal", TYPE_SAMPLER_2D);
    this.u_texture_albedo =
      uniform(p, "R2_basic_surface_textures.albedo", TYPE_SAMPLER_2D);
    this.u_texture_specular =
      uniform(p, "R2_basic_surface_textures.specular", TYPE_SAMPLER_2D);
    this.u_texture_emission =
      uniform(p, "R2_basic_surface_textures.emission", TYPE_SAMPLER_2D);

    this.u_texture_stipple =
      uniform(p, "R2_stipple.pattern", TYPE_SAMPLER_2D);
    this.u_stipple_noise_uv_scale =
      uniform(p, "R2_stipple.pattern_uv_scale", TYPE_FLOAT_VECTOR_2);
    this.u_stipple_threshold =
      uniform(p, "R2_stipple.threshold", TYPE_FLOAT);

    this.u_viewport_inverse_width =
      uniform(p, "R2_viewport.inverse_width", TYPE_FLOAT);
    this.u_viewport_inverse_height =
      uniform(p, "R2_viewport.inverse_height", TYPE_FLOAT);

    checkUniformParameterCount(p, 21);
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

  public static R2GeometryShaderBasicStippledSingle create(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2GeometryShaderBasicStippledSingle(
      in_shaders, in_shader_env, in_pool, R2ShaderStateChecking.STATE_CHECK);
  }

  @Override
  public Class<R2GeometryShaderBasicStippledParameters> shaderParametersType()
  {
    return R2GeometryShaderBasicStippledParameters.class;
  }

  @Override
  protected void onActualReceiveViewValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersViewType view_parameters)
  {
    final JCGLShadersType g_sh = g.shaders();
    final R2MatricesObserverValuesType matrices =
      view_parameters.observerMatrices();
    final AreaL viewport =
      view_parameters.viewport();

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(matrices.projection()));
    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_view, matrices.matrixView());
    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_projection, matrices.matrixProjection());

    /*
     * Upload the viewport.
     */

    this.viewport_w = viewport.sizeX();
    this.viewport_h = viewport.sizeY();

    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_width,
      (float) (1.0 / (double) this.viewport_w));
    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_height,
      (float) (1.0 / (double) this.viewport_h));
  }

  @Override
  protected void onActualReceiveMaterialValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersMaterialType<R2GeometryShaderBasicStippledParameters> mat_parameters)
  {
    final JCGLTexturesType g_tex = g.textures();
    final JCGLShadersType g_sh = g.shaders();

    final JCGLTextureUnitContextMutableType tc =
      mat_parameters.textureUnitContext();
    final R2GeometryShaderBasicStippledParameters values =
      mat_parameters.values();

    final JCGLTexture2DUsableType noise =
      values.stippleNoiseTexture().texture();

    final Vector2D noise_uv_scale = Vector2D.of(
      (double) (this.viewport_w / noise.width()),
      (double) (this.viewport_h / noise.height()));

    g_sh.shaderUniformPutVector2f(
      this.u_stipple_noise_uv_scale, noise_uv_scale);

    final JCGLTextureUnitType unit_albedo =
      tc.unitContextBindTexture2D(g_tex, values.albedoTexture().texture());
    final JCGLTextureUnitType unit_emission =
      tc.unitContextBindTexture2D(g_tex, values.emissionTexture().texture());
    final JCGLTextureUnitType unit_normal =
      tc.unitContextBindTexture2D(g_tex, values.normalTexture().texture());
    final JCGLTextureUnitType unit_specular =
      tc.unitContextBindTexture2D(g_tex, values.specularTexture().texture());
    final JCGLTextureUnitType unit_stipple =
      tc.unitContextBindTexture2D(
        g_tex,
        values.stippleNoiseTexture().texture());

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_albedo, unit_albedo);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_emission, unit_emission);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_normal, unit_normal);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_specular, unit_specular);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_stipple, unit_stipple);

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

    g_sh.shaderUniformPutFloat(
      this.u_stipple_threshold, (float) values.stippleThreshold());
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
