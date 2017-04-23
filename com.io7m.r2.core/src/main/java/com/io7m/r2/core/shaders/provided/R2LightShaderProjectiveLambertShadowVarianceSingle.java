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
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightProjectiveWithShadowVarianceType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2MatricesProjectiveLightValuesType;
import com.io7m.r2.core.R2MatricesVolumeLightValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2ShadowDepthVarianceType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2ViewRaysReadableType;
import com.io7m.r2.core.shaders.abstracts.R2AbstractLightProjectiveWithShadowShader;
import com.io7m.r2.core.shaders.abstracts.R2ShaderStateChecking;
import com.io7m.r2.core.shaders.types.R2ShaderParametersLightType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_MATRIX_4;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_VECTOR_3;
import static com.io7m.jcanephora.core.JCGLType.TYPE_SAMPLER_2D;
import static com.io7m.r2.core.shaders.types.R2ShaderParameters.checkUniformParameterCount;
import static com.io7m.r2.core.shaders.types.R2ShaderParameters.uniform;

/**
 * Projective light shader for single lights.
 */

public final class R2LightShaderProjectiveLambertShadowVarianceSingle extends
  R2AbstractLightProjectiveWithShadowShader<R2LightProjectiveWithShadowVarianceType>
{
  private final JCGLProgramUniformType u_transform_projection_inverse;
  private final JCGLProgramUniformType u_transform_volume_modelview;
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_view_rays_origin_x0y0;
  private final JCGLProgramUniformType u_view_rays_origin_x1y0;
  private final JCGLProgramUniformType u_view_rays_origin_x0y1;
  private final JCGLProgramUniformType u_view_rays_origin_x1y1;
  private final JCGLProgramUniformType u_view_rays_ray_x0y0;
  private final JCGLProgramUniformType u_view_rays_ray_x1y0;
  private final JCGLProgramUniformType u_view_rays_ray_x0y1;
  private final JCGLProgramUniformType u_view_rays_ray_x1y1;
  private final JCGLProgramUniformType u_gbuffer_albedo;
  private final JCGLProgramUniformType u_gbuffer_normal;
  private final JCGLProgramUniformType u_gbuffer_specular;
  private final JCGLProgramUniformType u_gbuffer_depth;
  private final JCGLProgramUniformType u_viewport_inverse_width;
  private final JCGLProgramUniformType u_viewport_inverse_height;
  private final JCGLProgramUniformType u_light_projective_color;
  private final JCGLProgramUniformType u_light_projective_intensity;
  private final JCGLProgramUniformType u_light_projective_position;
  private final JCGLProgramUniformType u_light_projective_inverse_range;
  private final JCGLProgramUniformType u_light_projective_inverse_falloff;
  private final JCGLProgramUniformType u_transform_light_projection;
  private final JCGLProgramUniformType u_transform_eye_to_light_eye;
  private final JCGLProgramUniformType u_light_projective_image;
  private final JCGLProgramUniformType u_shadow_factor_minimum;
  private final JCGLProgramUniformType u_shadow_variance_minimum;
  private final JCGLProgramUniformType u_shadow_bleed_reduction;
  private final JCGLProgramUniformType u_shadow_depth_coefficient;
  private final JCGLProgramUniformType u_shadow_map;

  private R2LightShaderProjectiveLambertShadowVarianceSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2LightShaderProjectiveLambertShadowVarianceSingle",
      "com.io7m.r2.shaders.core/R2LightPositionalSingle.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2LightProjectiveLambertShadowVarianceSingle.frag",
      in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_shadow_factor_minimum =
      uniform(p, "R2_shadow_variance.factor_minimum", TYPE_FLOAT);
    this.u_shadow_variance_minimum =
      uniform(p, "R2_shadow_variance.variance_minimum", TYPE_FLOAT);
    this.u_shadow_bleed_reduction =
      uniform(p, "R2_shadow_variance.bleed_reduction", TYPE_FLOAT);
    this.u_shadow_depth_coefficient =
      uniform(p, "R2_shadow_variance.depth_coefficient", TYPE_FLOAT);
    this.u_shadow_map =
      uniform(p, "R2_shadow_variance.map", TYPE_SAMPLER_2D);

    this.u_light_projective_color =
      uniform(p, "R2_light_projective.color", TYPE_FLOAT_VECTOR_3);
    this.u_light_projective_intensity =
      uniform(p, "R2_light_projective.intensity", TYPE_FLOAT);
    this.u_light_projective_position =
      uniform(p, "R2_light_projective.position", TYPE_FLOAT_VECTOR_3);
    this.u_light_projective_inverse_range =
      uniform(p, "R2_light_projective.inverse_range", TYPE_FLOAT);
    this.u_light_projective_inverse_falloff =
      uniform(p, "R2_light_projective.inverse_falloff", TYPE_FLOAT);
    this.u_light_projective_image =
      uniform(p, "R2_light_projective_image", TYPE_SAMPLER_2D);

    this.u_transform_volume_modelview =
      uniform(
        p,
        "R2_light_matrices.transform_volume_modelview",
        TYPE_FLOAT_MATRIX_4);
    this.u_transform_projection =
      uniform(p, "R2_light_matrices.transform_projection", TYPE_FLOAT_MATRIX_4);
    this.u_transform_projection_inverse =
      uniform(
        p,
        "R2_light_matrices.transform_projection_inverse",
        TYPE_FLOAT_MATRIX_4);

    this.u_transform_eye_to_light_eye =
      uniform(p, "R2_transform_eye_to_light_eye", TYPE_FLOAT_MATRIX_4);
    this.u_transform_light_projection =
      uniform(p, "R2_transform_light_projection", TYPE_FLOAT_MATRIX_4);

    this.u_gbuffer_albedo =
      uniform(p, "R2_light_gbuffer.albedo", TYPE_SAMPLER_2D);
    this.u_gbuffer_normal =
      uniform(p, "R2_light_gbuffer.normal", TYPE_SAMPLER_2D);
    this.u_gbuffer_specular =
      uniform(p, "R2_light_gbuffer.specular", TYPE_SAMPLER_2D);
    this.u_gbuffer_depth =
      uniform(p, "R2_light_gbuffer.depth", TYPE_SAMPLER_2D);

    this.u_viewport_inverse_width =
      uniform(p, "R2_light_viewport.inverse_width", TYPE_FLOAT);
    this.u_viewport_inverse_height =
      uniform(p, "R2_light_viewport.inverse_height", TYPE_FLOAT);

    this.u_depth_coefficient =
      uniform(p, "R2_light_depth_coefficient", TYPE_FLOAT);

    this.u_view_rays_origin_x0y0 =
      uniform(p, "R2_light_view_rays.origin_x0y0", TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_origin_x1y0 =
      uniform(p, "R2_light_view_rays.origin_x1y0", TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_origin_x0y1 =
      uniform(p, "R2_light_view_rays.origin_x0y1", TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_origin_x1y1 =
      uniform(p, "R2_light_view_rays.origin_x1y1", TYPE_FLOAT_VECTOR_3);

    this.u_view_rays_ray_x0y0 =
      uniform(p, "R2_light_view_rays.ray_x0y0", TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_ray_x1y0 =
      uniform(p, "R2_light_view_rays.ray_x1y0", TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_ray_x0y1 =
      uniform(p, "R2_light_view_rays.ray_x0y1", TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_ray_x1y1 =
      uniform(p, "R2_light_view_rays.ray_x1y1", TYPE_FLOAT_VECTOR_3);

    checkUniformParameterCount(p, 31);
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

  public static R2LightShaderProjectiveLambertShadowVarianceSingle create(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2LightShaderProjectiveLambertShadowVarianceSingle(
      in_shaders, in_shader_env, in_pool, R2ShaderStateChecking.STATE_CHECK);
  }

  @Override
  public Class<R2LightProjectiveWithShadowVarianceType>
  shaderParametersType()
  {
    return R2LightProjectiveWithShadowVarianceType.class;
  }

  @Override
  protected void onActualReceiveBoundGeometryBufferTextures(
    final JCGLInterfaceGL33Type g,
    final R2GeometryBufferUsableType gbuffer,
    final JCGLTextureUnitType unit_albedo,
    final JCGLTextureUnitType unit_specular,
    final JCGLTextureUnitType unit_depth,
    final JCGLTextureUnitType unit_normals)
  {
    /*
     * Set each of the required G-Buffer textures.
     */

    final JCGLShadersType g_sh = g.shaders();
    g_sh.shaderUniformPutTexture2DUnit(this.u_gbuffer_albedo, unit_albedo);
    g_sh.shaderUniformPutTexture2DUnit(this.u_gbuffer_normal, unit_normals);
    g_sh.shaderUniformPutTexture2DUnit(this.u_gbuffer_specular, unit_specular);
    g_sh.shaderUniformPutTexture2DUnit(this.u_gbuffer_depth, unit_depth);
  }

  @Override
  protected void onActualReceiveValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersLightType<R2LightProjectiveWithShadowVarianceType> light_parameters)
  {
    final JCGLShadersType g_sh = g.shaders();
    final JCGLTexturesType g_tex = g.textures();

    final R2MatricesObserverValuesType m =
      light_parameters.observerMatrices();
    final AreaL viewport =
      light_parameters.viewport();
    final R2LightProjectiveWithShadowVarianceType light =
      light_parameters.values();
    final JCGLTextureUnitContextMutableType tc =
      light_parameters.textureUnitContext();

    /*
     * Upload the current view rays.
     */

    final R2ViewRaysReadableType view_rays = m.viewRays();
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_origin_x0y0, view_rays.originX0Y0());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_origin_x1y0, view_rays.originX1Y0());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_origin_x0y1, view_rays.originX0Y1());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_origin_x1y1, view_rays.originX1Y1());

    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_ray_x0y0, view_rays.rayX0Y0());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_ray_x1y0, view_rays.rayX1Y0());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_ray_x0y1, view_rays.rayX0Y1());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_ray_x1y1, view_rays.rayX1Y1());

    /*
     * Upload the viewport.
     */

    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_width,
      (float) (1.0 / (double) viewport.width()));
    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_height,
      (float) (1.0 / (double) viewport.height()));

    /*
     * Upload the scene's depth coefficient.
     */

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(m.projection()));

    /*
     * Upload the projection for the light volume.
     */

    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_projection, m.matrixProjection());
    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_projection_inverse, m.matrixProjectionInverse());

    /*
     * Transform the light's position to eye-space and upload it.
     */

    final PVector3D<R2SpaceWorldType> position = light.position();
    final PVector4D<R2SpaceWorldType> position4 =
      PVector4D.of(position.x(), position.y(), position.z(), 1.0);
    final PVector4D<R2SpaceEyeType> position_eye4 =
      PMatrices4x4D.multiplyVectorPost(m.matrixView(), position4);
    final PVector3D<R2SpaceEyeType> position_eye =
      PVector3D.of(position_eye4.x(), position_eye4.y(), position_eye4.z());

    g_sh.shaderUniformPutPVector3f(
      this.u_light_projective_position, position_eye);

    /*
     * Upload the projected image.
     */

    final JCGLTextureUnitType unit_image =
      tc.unitContextBindTexture2D(g_tex, light.image().texture());
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_light_projective_image,
      unit_image);

    /*
     * Upload the light values.
     */

    g_sh.shaderUniformPutPVector3f(
      this.u_light_projective_color, light.color());
    g_sh.shaderUniformPutFloat(
      this.u_light_projective_intensity, (float) light.intensity());
    g_sh.shaderUniformPutFloat(
      this.u_light_projective_inverse_falloff, (float) (1.0 / light.falloff()));
    g_sh.shaderUniformPutFloat(
      this.u_light_projective_inverse_range, (float) (1.0 / light.radius()));
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_light_projective_image, unit_image);

    /*
     * Upload the shadow values.
     */

    final R2ShadowDepthVarianceType shadow = light.shadow();
    g_sh.shaderUniformPutFloat(
      this.u_shadow_bleed_reduction, shadow.lightBleedReduction());
    g_sh.shaderUniformPutFloat(
      this.u_shadow_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(light.projection()));
    g_sh.shaderUniformPutFloat(
      this.u_shadow_factor_minimum, shadow.minimumFactor());
    g_sh.shaderUniformPutFloat(
      this.u_shadow_variance_minimum, shadow.minimumVariance());
  }

  @Override
  protected void onActualReceiveVolumeLightTransform(
    final JCGLInterfaceGL33Type g,
    final R2MatricesVolumeLightValuesType m)
  {
    final JCGLShadersType g_sh = g.shaders();

    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_volume_modelview, m.matrixLightModelView());
  }

  @Override
  protected void onActualReceiveProjectiveLight(
    final JCGLInterfaceGL33Type g,
    final R2MatricesProjectiveLightValuesType m)
  {
    final JCGLShadersType g_sh = g.shaders();

    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_eye_to_light_eye, m.matrixProjectiveEyeToLightEye());
    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_light_projection, m.matrixProjectiveProjection());
  }

  @Override
  protected void onActualReceiveShadowMap(
    final JCGLInterfaceGL33Type g,
    final JCGLTextureUnitContextMutableType tc,
    final R2Texture2DUsableType map)
  {
    final JCGLShadersType g_sh = g.shaders();
    final JCGLTexturesType g_tex = g.textures();

    final JCGLTextureUnitType unit_shadow =
      tc.unitContextBindTexture2D(g_tex, map.texture());
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_shadow_map, unit_shadow);
  }
}
