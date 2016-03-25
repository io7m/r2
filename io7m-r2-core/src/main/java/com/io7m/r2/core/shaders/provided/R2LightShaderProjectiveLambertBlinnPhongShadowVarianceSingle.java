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
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.parameterized.PMatrixM4x4F;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.jtensors.parameterized.PVector4FType;
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.jtensors.parameterized.PVectorM4F;
import com.io7m.jtensors.parameterized.PVectorReadable3FType;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2AbstractShader;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightProjectiveWithShadowVariance;
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2MatricesProjectiveLightValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2ShadowDepthVarianceType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureUnitContextMutableType;
import com.io7m.r2.core.R2TransformContextType;
import com.io7m.r2.core.R2TransformOTReadableType;
import com.io7m.r2.core.R2ViewRaysReadableType;
import com.io7m.r2.core.shaders.types.R2ShaderLightWithShadowSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightWithShadowVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderProjectiveRequired;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

import java.util.Optional;

/**
 * Projective light shader for single lights.
 */

public final class R2LightShaderProjectiveLambertBlinnPhongShadowVarianceSingle extends
  R2AbstractShader<R2LightProjectiveWithShadowVariance>
  implements R2ShaderLightWithShadowSingleType
  <R2LightProjectiveWithShadowVariance>
{
  private final JCGLProgramUniformType u_transform_projection_inverse;
  private final JCGLProgramUniformType u_transform_modelview;
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

  private final PVector4FType<R2SpaceEyeType>   position_eye;
  private final PVector3FType<R2SpaceEyeType>   position_eye3;
  private final PVector4FType<R2SpaceWorldType> position_world;
  private       JCGLTextureUnitType             unit_image;
  private       JCGLTextureUnitType             unit_shadow;

  private R2LightShaderProjectiveLambertBlinnPhongShadowVarianceSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_sources,
      in_pool,
      "R2LightProjectiveLambertBlinnPhongShadowVarianceSingle",
      "R2LightProjectiveLambertBlinnPhongShadowVarianceSingle.vert",
      Optional.empty(),
      "R2LightProjectiveLambertBlinnPhongShadowVarianceSingle.frag");

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 31);

    this.u_shadow_factor_minimum =
      R2ShaderParameters.getUniformChecked(
        p, "R2_shadow_variance.factor_minimum");
    this.u_shadow_variance_minimum =
      R2ShaderParameters.getUniformChecked(
        p, "R2_shadow_variance.variance_minimum");
    this.u_shadow_bleed_reduction =
      R2ShaderParameters.getUniformChecked(
        p, "R2_shadow_variance.bleed_reduction");
    this.u_shadow_depth_coefficient =
      R2ShaderParameters.getUniformChecked(
        p, "R2_shadow_variance.depth_coefficient");
    this.u_shadow_map =
      R2ShaderParameters.getUniformChecked(
        p, "R2_shadow_variance.map");

    this.u_light_projective_color =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_projective.color");
    this.u_light_projective_intensity =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_projective.intensity");
    this.u_light_projective_position =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_projective.position");
    this.u_light_projective_inverse_range =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_projective.inverse_range");
    this.u_light_projective_inverse_falloff =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_projective.inverse_falloff");
    this.u_light_projective_image =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_projective_image");

    this.u_transform_modelview =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_matrices.transform_modelview");
    this.u_transform_projection =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_matrices.transform_projection");
    this.u_transform_projection_inverse =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_matrices.transform_projection_inverse");

    this.u_transform_eye_to_light_eye =
      R2ShaderParameters.getUniformChecked(
        p, "R2_transform_eye_to_light_eye");
    this.u_transform_light_projection =
      R2ShaderParameters.getUniformChecked(
        p, "R2_transform_light_projection");

    this.u_gbuffer_albedo =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_gbuffer.albedo");
    this.u_gbuffer_normal =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_gbuffer.normal");
    this.u_gbuffer_specular =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_gbuffer.specular");
    this.u_gbuffer_depth =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_gbuffer.depth");

    this.u_viewport_inverse_width =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_viewport.inverse_width");
    this.u_viewport_inverse_height =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_viewport.inverse_height");

    this.u_depth_coefficient =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_depth_coefficient");

    this.u_view_rays_origin_x0y0 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_view_rays.origin_x0y0");
    this.u_view_rays_origin_x1y0 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_view_rays.origin_x1y0");
    this.u_view_rays_origin_x0y1 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_view_rays.origin_x0y1");
    this.u_view_rays_origin_x1y1 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_view_rays.origin_x1y1");

    this.u_view_rays_ray_x0y0 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_view_rays.ray_x0y0");
    this.u_view_rays_ray_x1y0 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_view_rays.ray_x1y0");
    this.u_view_rays_ray_x0y1 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_view_rays.ray_x0y1");
    this.u_view_rays_ray_x1y1 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_view_rays.ray_x1y1");

    this.position_eye = new PVectorM4F<>();
    this.position_eye3 = new PVectorM3F<>();
    this.position_world = new PVectorM4F<>();
  }

  /**
   * Construct a new shader.
   *
   * @param in_shaders A shader interface
   * @param in_sources Shader sources
   * @param in_pool    The ID pool
   *
   * @return A new shader
   */

  public static R2ShaderLightWithShadowSingleType
    <R2LightProjectiveWithShadowVariance>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return R2ShaderLightWithShadowVerifier.newVerifier(
      new R2LightShaderProjectiveLambertBlinnPhongShadowVarianceSingle(
        in_shaders,
        in_sources,
        in_pool),
      R2ShaderProjectiveRequired.R2_SHADER_PROJECTIVE_REQUIRED);
  }

  @Override
  public Class<R2LightProjectiveWithShadowVariance>
  getShaderParametersType()
  {
    return R2LightProjectiveWithShadowVariance.class;
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {

  }

  @Override
  public void onReceiveBoundGeometryBufferTextures(
    final JCGLShadersType g_sh,
    final R2GeometryBufferUsableType g,
    final JCGLTextureUnitType unit_albedo,
    final JCGLTextureUnitType unit_specular,
    final JCGLTextureUnitType unit_depth,
    final JCGLTextureUnitType unit_normals)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(g);
    NullCheck.notNull(unit_albedo);
    NullCheck.notNull(unit_depth);
    NullCheck.notNull(unit_normals);
    NullCheck.notNull(unit_specular);

    /**
     * Upload the viewport.
     */

    final AreaInclusiveUnsignedLType viewport = g.getDescription().getArea();
    final UnsignedRangeInclusiveL range_x = viewport.getRangeX();
    final UnsignedRangeInclusiveL range_y = viewport.getRangeY();
    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_width,
      (float) (1.0 / (double) range_x.getInterval()));
    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_height,
      (float) (1.0 / (double) range_y.getInterval()));

    /**
     * Set each of the required G-Buffer textures.
     */

    g_sh.shaderUniformPutTexture2DUnit(this.u_gbuffer_albedo, unit_albedo);
    g_sh.shaderUniformPutTexture2DUnit(this.u_gbuffer_normal, unit_normals);
    g_sh.shaderUniformPutTexture2DUnit(this.u_gbuffer_specular, unit_specular);
    g_sh.shaderUniformPutTexture2DUnit(this.u_gbuffer_depth, unit_depth);
  }

  @Override
  public void onReceiveProjectiveLight(
    final JCGLShadersType g_sh,
    final R2MatricesProjectiveLightValuesType m)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);

    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_eye_to_light_eye, m.getMatrixProjectiveEyeToLightEye());
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_light_projection, m.getMatrixProjectiveProjection());
  }

  @Override
  public void onReceiveInstanceTransformValues(
    final JCGLShadersType g_sh,
    final R2MatricesInstanceSingleValuesType m)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);

    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_modelview, m.getMatrixModelView());
  }

  @Override
  public void onReceiveValues(
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final R2TextureUnitContextMutableType tc,
    final R2LightProjectiveWithShadowVariance values,
    final R2MatricesObserverValuesType m)
  {
    NullCheck.notNull(g_tex);
    NullCheck.notNull(g_sh);
    NullCheck.notNull(tc);
    NullCheck.notNull(values);
    NullCheck.notNull(m);

    /**
     * Upload the current view rays.
     */

    final R2ViewRaysReadableType view_rays = m.getViewRays();
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_origin_x0y0, view_rays.getOriginX0Y0());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_origin_x1y0, view_rays.getOriginX1Y0());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_origin_x0y1, view_rays.getOriginX0Y1());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_origin_x1y1, view_rays.getOriginX1Y1());

    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_ray_x0y0, view_rays.getRayX0Y0());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_ray_x1y0, view_rays.getRayX1Y0());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_ray_x0y1, view_rays.getRayX0Y1());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_ray_x1y1, view_rays.getRayX1Y1());

    /**
     * Upload the scene's depth coefficient.
     */

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(m.getProjection()));

    /**
     * Upload the projection for the light volume.
     */

    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection, m.getMatrixProjection());
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection_inverse, m.getMatrixProjectionInverse());

    /**
     * Transform the light's position to eye-space and upload it.
     */

    final R2TransformOTReadableType transform = values.getTransform();
    final PVectorReadable3FType<R2SpaceWorldType> position =
      transform.getTranslationReadable();

    this.position_world.copyFrom3F(position);
    this.position_world.setWF(1.0f);

    final R2TransformContextType trc = m.getTransformContext();
    PMatrixM4x4F.multiplyVector4F(
      trc.getContextPM4F(),
      m.getMatrixView(),
      this.position_world,
      this.position_eye);

    this.position_eye3.copyFrom3F(this.position_eye);

    g_sh.shaderUniformPutVector3f(
      this.u_light_projective_position, this.position_eye3);

    /**
     * Upload the projected image.
     */

    this.unit_image =
      tc.unitContextBindTexture2D(g_tex, values.getImage());
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_light_projective_image,
      this.unit_image);

    /**
     * Upload the light values.
     */

    g_sh.shaderUniformPutVector3f(
      this.u_light_projective_color, values.getColor());
    g_sh.shaderUniformPutFloat(
      this.u_light_projective_intensity, values.getIntensity());
    g_sh.shaderUniformPutFloat(
      this.u_light_projective_inverse_falloff, 1.0f / values.getFalloff());
    g_sh.shaderUniformPutFloat(
      this.u_light_projective_inverse_range, 1.0f / values.getRadius());
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_light_projective_image, this.unit_image);

    /**
     * Upload the shadow values.
     */

    final R2ShadowDepthVarianceType shadow = values.getShadow();
    g_sh.shaderUniformPutFloat(
      this.u_shadow_bleed_reduction, shadow.getLightBleedReduction());
    g_sh.shaderUniformPutFloat(
      this.u_shadow_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(values.getProjection()));
    g_sh.shaderUniformPutFloat(
      this.u_shadow_factor_minimum, shadow.getMinimumFactor());
    g_sh.shaderUniformPutFloat(
      this.u_shadow_variance_minimum, shadow.getMinimumVariance());
  }

  @Override
  public void onReceiveShadowMap(
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final R2TextureUnitContextMutableType tc,
    final R2LightProjectiveWithShadowVariance values,
    final R2Texture2DUsableType map)
  {
    NullCheck.notNull(g_tex);
    NullCheck.notNull(g_sh);
    NullCheck.notNull(tc);
    NullCheck.notNull(values);
    NullCheck.notNull(map);

    this.unit_shadow =
      tc.unitContextBindTexture2D(g_tex, map);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_shadow_map, this.unit_shadow);
  }
}
