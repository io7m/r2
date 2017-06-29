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

package com.io7m.r2.shaders.debug;

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.lights.R2LightSphericalSingleReadableType;
import com.io7m.r2.matrices.R2MatricesObserverValuesType;
import com.io7m.r2.matrices.R2MatricesVolumeLightValuesType;
import com.io7m.r2.projections.R2Projections;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferUsableType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.api.R2ShaderStateChecking;
import com.io7m.r2.shaders.light.api.R2AbstractLightVolumeShaderSingle;
import com.io7m.r2.shaders.light.api.R2ShaderParametersLightType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_MATRIX_4;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_VECTOR_3;
import static com.io7m.jcanephora.core.JCGLType.TYPE_SAMPLER_2D;
import static com.io7m.r2.shaders.api.R2ShaderParameters.checkUniformParameterCount;
import static com.io7m.r2.shaders.api.R2ShaderParameters.uniform;
import static com.io7m.r2.shaders.light.api.R2LightShaderParameters.lightShaderTargetIsImageBuffer;

/**
 * Spherical light shader that ignores the surface and applies a constant
 * color.
 */

public final class R2DebugShaderLightSphericalConstantSingle extends
  R2AbstractLightVolumeShaderSingle<R2LightSphericalSingleReadableType>
{
  private final JCGLProgramUniformType u_transform_volume_modelview;
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_transform_projection_inverse;
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_light_spherical_color;
  private final JCGLProgramUniformType u_light_spherical_intensity;
  private final JCGLProgramUniformType u_light_spherical_position;
  private final JCGLProgramUniformType u_light_spherical_inverse_range;
  private final JCGLProgramUniformType u_light_spherical_inverse_falloff;
  private final JCGLProgramUniformType u_gbuffer_albedo;
  private final JCGLProgramUniformType u_gbuffer_normal;
  private final JCGLProgramUniformType u_gbuffer_specular;
  private final JCGLProgramUniformType u_gbuffer_depth;

  private R2DebugShaderLightSphericalConstantSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.debug.R2DebugShaderLightSphericalConstantSingle",
      "com.io7m.r2.shaders.light.api/R2LightPositionalSingle.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.debug/R2LightSphericalDebugConstantSingle.frag",
      in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_light_spherical_color =
      uniform(p, "R2_light_spherical.color", TYPE_FLOAT_VECTOR_3);
    this.u_light_spherical_intensity =
      uniform(p, "R2_light_spherical.intensity", TYPE_FLOAT);
    this.u_light_spherical_position =
      uniform(p, "R2_light_spherical.position", TYPE_FLOAT_VECTOR_3);
    this.u_light_spherical_inverse_range =
      uniform(p, "R2_light_spherical.inverse_range", TYPE_FLOAT);
    this.u_light_spherical_inverse_falloff =
      uniform(p, "R2_light_spherical.inverse_falloff", TYPE_FLOAT);

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

    this.u_depth_coefficient =
      uniform(p, "R2_light_depth_coefficient", TYPE_FLOAT);

    final int count;
    if (lightShaderTargetIsImageBuffer(this.environment())) {
      this.u_gbuffer_albedo =
        uniform(p, "R2_light_gbuffer.albedo", TYPE_SAMPLER_2D);
      this.u_gbuffer_normal =
        uniform(p, "R2_light_gbuffer.normal", TYPE_SAMPLER_2D);
      this.u_gbuffer_specular =
        uniform(p, "R2_light_gbuffer.specular", TYPE_SAMPLER_2D);
      this.u_gbuffer_depth =
        uniform(p, "R2_light_gbuffer.depth", TYPE_SAMPLER_2D);
      count = 15;
    } else {
      this.u_gbuffer_albedo = null;
      this.u_gbuffer_normal = null;
      this.u_gbuffer_specular = null;
      this.u_gbuffer_depth = null;
      count = 9;
    }

    checkUniformParameterCount(p, count);
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

  public static R2DebugShaderLightSphericalConstantSingle create(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2DebugShaderLightSphericalConstantSingle(
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
    if (this.u_gbuffer_albedo != null) {
      /*
       * Upload the geometry buffer textures.
       */

      final JCGLShadersType g_sh = g.shaders();
      g_sh.shaderUniformPutTexture2DUnit(this.u_gbuffer_albedo, unit_albedo);
      g_sh.shaderUniformPutTexture2DUnit(this.u_gbuffer_normal, unit_normals);
      g_sh.shaderUniformPutTexture2DUnit(
        this.u_gbuffer_specular,
        unit_specular);
      g_sh.shaderUniformPutTexture2DUnit(this.u_gbuffer_depth, unit_depth);
    }
  }

  @Override
  protected void onActualReceiveValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersLightType<R2LightSphericalSingleReadableType> light_parameters)
  {
    final JCGLShadersType g_sh = g.shaders();

    final R2MatricesObserverValuesType m =
      light_parameters.observerMatrices();
    final R2LightSphericalSingleReadableType light =
      light_parameters.values();

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
      this.u_transform_projection,
      m.matrixProjection());
    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_projection_inverse,
      m.matrixProjectionInverse());

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
     * Upload light values.
     */

    g_sh.shaderUniformPutPVector3f(
      this.u_light_spherical_color,
      light.color());
    g_sh.shaderUniformPutFloat(
      this.u_light_spherical_intensity,
      (float) light.intensity());
    g_sh.shaderUniformPutFloat(
      this.u_light_spherical_inverse_falloff,
      (float) (1.0 / light.falloff()));
    g_sh.shaderUniformPutFloat(
      this.u_light_spherical_inverse_range,
      (float) (1.0 / light.radius()));
  }

  @Override
  protected void onActualReceiveVolumeLightTransform(
    final JCGLInterfaceGL33Type g,
    final R2MatricesVolumeLightValuesType m)
  {
    /*
     * Upload the light volume modelview matrix.
     */

    final JCGLShadersType g_sh = g.shaders();

    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_volume_modelview,
      m.matrixLightModelView());
  }

}
