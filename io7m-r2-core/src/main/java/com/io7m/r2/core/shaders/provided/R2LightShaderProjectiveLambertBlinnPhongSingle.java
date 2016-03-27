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
import com.io7m.r2.core.R2LightProjectiveReadableType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2MatricesProjectiveLightValuesType;
import com.io7m.r2.core.R2MatricesVolumeLightValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2TextureUnitContextMutableType;
import com.io7m.r2.core.R2TransformContextType;
import com.io7m.r2.core.R2ViewRaysReadableType;
import com.io7m.r2.core.shaders.types.R2ShaderLightProjectiveType;
import com.io7m.r2.core.shaders.types.R2ShaderLightProjectiveVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

import java.util.Optional;

/**
 * Projective light shader for single lights.
 */

public final class R2LightShaderProjectiveLambertBlinnPhongSingle extends
  R2AbstractShader<R2LightProjectiveReadableType>
  implements R2ShaderLightProjectiveType<R2LightProjectiveReadableType>
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

  private final PVector4FType<R2SpaceEyeType>   position_eye;
  private final PVector3FType<R2SpaceEyeType>   position_eye3;
  private final PVector4FType<R2SpaceWorldType> position_world;
  private JCGLTextureUnitType unit_image;

  private R2LightShaderProjectiveLambertBlinnPhongSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_sources,
      in_pool,
      "R2LightProjectiveLambertBlinnPhongSingle",
      "R2LightProjectiveLambertBlinnPhongSingle.vert",
      Optional.empty(),
      "R2LightProjectiveLambertBlinnPhongSingle.frag");

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 26);

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

    this.u_transform_volume_modelview =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_matrices.transform_volume_modelview");
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

  public static R2ShaderLightProjectiveType<R2LightProjectiveReadableType>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return R2ShaderLightProjectiveVerifier.newVerifier(
      new R2LightShaderProjectiveLambertBlinnPhongSingle(
        in_shaders, in_sources, in_pool));
  }

  @Override
  public Class<R2LightProjectiveReadableType>
  getShaderParametersType()
  {
    return R2LightProjectiveReadableType.class;
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {
    // Nothing
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
  public void onReceiveVolumeLightTransform(
    final JCGLShadersType g_sh,
    final R2MatricesVolumeLightValuesType m)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);

    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_volume_modelview, m.getMatrixLightVolumeModelView());
  }

  @Override
  public void onReceiveValues(
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final R2TextureUnitContextMutableType tc,
    final AreaInclusiveUnsignedLType viewport,
    final R2LightProjectiveReadableType values,
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
     * Upload the viewport.
     */

    final UnsignedRangeInclusiveL range_x = viewport.getRangeX();
    final UnsignedRangeInclusiveL range_y = viewport.getRangeY();
    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_width,
      (float) (1.0 / (double) range_x.getInterval()));
    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_height,
      (float) (1.0 / (double) range_y.getInterval()));

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

    final PVectorReadable3FType<R2SpaceWorldType> position =
      values.getPosition();

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
  }
}
