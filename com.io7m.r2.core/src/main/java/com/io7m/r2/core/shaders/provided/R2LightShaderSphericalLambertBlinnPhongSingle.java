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
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightSphericalSingleReadableType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2MatricesVolumeLightValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2ViewRaysReadableType;
import com.io7m.r2.core.shaders.abstracts.R2AbstractLightVolumeShaderSingle;
import com.io7m.r2.core.shaders.abstracts.R2ShaderStateChecking;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderParametersLightType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

import java.util.Optional;

/**
 * Spherical light shader for single lights.
 */

public final class R2LightShaderSphericalLambertBlinnPhongSingle extends
  R2AbstractLightVolumeShaderSingle<R2LightSphericalSingleReadableType>
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
  private final JCGLProgramUniformType u_light_spherical_color;
  private final JCGLProgramUniformType u_light_spherical_intensity;
  private final JCGLProgramUniformType u_light_spherical_position;
  private final JCGLProgramUniformType u_light_spherical_inverse_range;
  private final JCGLProgramUniformType u_light_spherical_inverse_falloff;

  private R2LightShaderSphericalLambertBlinnPhongSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2LightShaderSphericalLambertBlinnPhongSingle",
      "com.io7m.r2.shaders.core/R2LightPositionalSingle.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2LightSphericalLambertBlinnPhongSingle.frag",
      in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_light_spherical_color =
      R2ShaderParameters.uniform(
        p, "R2_light_spherical.color", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_light_spherical_intensity =
      R2ShaderParameters.uniform(
        p, "R2_light_spherical.intensity", JCGLType.TYPE_FLOAT);
    this.u_light_spherical_position =
      R2ShaderParameters.uniform(
        p, "R2_light_spherical.position", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_light_spherical_inverse_range =
      R2ShaderParameters.uniform(
        p, "R2_light_spherical.inverse_range", JCGLType.TYPE_FLOAT);
    this.u_light_spherical_inverse_falloff =
      R2ShaderParameters.uniform(
        p, "R2_light_spherical.inverse_falloff", JCGLType.TYPE_FLOAT);

    this.u_transform_volume_modelview =
      R2ShaderParameters.uniform(
        p,
        "R2_light_matrices.transform_volume_modelview",
        JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_projection =
      R2ShaderParameters.uniform(
        p,
        "R2_light_matrices.transform_projection",
        JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_projection_inverse =
      R2ShaderParameters.uniform(
        p,
        "R2_light_matrices.transform_projection_inverse",
        JCGLType.TYPE_FLOAT_MATRIX_4);

    this.u_gbuffer_albedo =
      R2ShaderParameters.uniform(
        p, "R2_light_gbuffer.albedo", JCGLType.TYPE_SAMPLER_2D);
    this.u_gbuffer_normal =
      R2ShaderParameters.uniform(
        p, "R2_light_gbuffer.normal", JCGLType.TYPE_SAMPLER_2D);
    this.u_gbuffer_specular =
      R2ShaderParameters.uniform(
        p, "R2_light_gbuffer.specular", JCGLType.TYPE_SAMPLER_2D);
    this.u_gbuffer_depth =
      R2ShaderParameters.uniform(
        p, "R2_light_gbuffer.depth", JCGLType.TYPE_SAMPLER_2D);

    this.u_viewport_inverse_width =
      R2ShaderParameters.uniform(
        p, "R2_light_viewport.inverse_width", JCGLType.TYPE_FLOAT);
    this.u_viewport_inverse_height =
      R2ShaderParameters.uniform(
        p, "R2_light_viewport.inverse_height", JCGLType.TYPE_FLOAT);

    this.u_depth_coefficient =
      R2ShaderParameters.uniform(
        p, "R2_light_depth_coefficient", JCGLType.TYPE_FLOAT);

    this.u_view_rays_origin_x0y0 =
      R2ShaderParameters.uniform(
        p, "R2_light_view_rays.origin_x0y0", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_origin_x1y0 =
      R2ShaderParameters.uniform(
        p, "R2_light_view_rays.origin_x1y0", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_origin_x0y1 =
      R2ShaderParameters.uniform(
        p, "R2_light_view_rays.origin_x0y1", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_origin_x1y1 =
      R2ShaderParameters.uniform(
        p, "R2_light_view_rays.origin_x1y1", JCGLType.TYPE_FLOAT_VECTOR_3);

    this.u_view_rays_ray_x0y0 =
      R2ShaderParameters.uniform(
        p, "R2_light_view_rays.ray_x0y0", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_ray_x1y0 =
      R2ShaderParameters.uniform(
        p, "R2_light_view_rays.ray_x1y0", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_ray_x0y1 =
      R2ShaderParameters.uniform(
        p, "R2_light_view_rays.ray_x0y1", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_ray_x1y1 =
      R2ShaderParameters.uniform(
        p, "R2_light_view_rays.ray_x1y1", JCGLType.TYPE_FLOAT_VECTOR_3);

    R2ShaderParameters.checkUniformParameterCount(p, 23);
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

  public static R2LightShaderSphericalLambertBlinnPhongSingle
  create(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2LightShaderSphericalLambertBlinnPhongSingle(
      in_shaders, in_shader_env, in_pool, R2ShaderStateChecking.STATE_CHECK);
  }

  @Override
  public Class<R2LightSphericalSingleReadableType>
  shaderParametersType()
  {
    return R2LightSphericalSingleReadableType.class;
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
    final R2ShaderParametersLightType<R2LightSphericalSingleReadableType> light_parameters)
  {
    final JCGLShadersType g_sh = g.shaders();

    final R2MatricesObserverValuesType m =
      light_parameters.observerMatrices();
    final AreaL viewport =
      light_parameters.viewport();
    final R2LightSphericalSingleReadableType light =
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

    final PVector3D<R2SpaceWorldType> position = light.originPosition();
    final PVector4D<R2SpaceWorldType> position4 =
      PVector4D.of(position.x(), position.y(), position.z(), 1.0);
    final PVector4D<R2SpaceEyeType> position_eye4 =
      PMatrices4x4D.multiplyVectorPost(m.matrixView(), position4);
    final PVector3D<R2SpaceEyeType> position_eye =
      PVector3D.of(position_eye4.x(), position_eye4.y(), position_eye4.z());

    g_sh.shaderUniformPutPVector3f(
      this.u_light_spherical_position, position_eye);

    /*
     * Upload the light values.
     */

    g_sh.shaderUniformPutPVector3f(
      this.u_light_spherical_color, light.color());
    g_sh.shaderUniformPutFloat(
      this.u_light_spherical_intensity, (float) light.intensity());
    g_sh.shaderUniformPutFloat(
      this.u_light_spherical_inverse_falloff, (float) (1.0 / light.falloff()));
    g_sh.shaderUniformPutFloat(
      this.u_light_spherical_inverse_range, (float) (1.0 / light.radius()));
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
}
