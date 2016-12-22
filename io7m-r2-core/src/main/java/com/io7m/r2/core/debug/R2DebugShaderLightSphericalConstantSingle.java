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

package com.io7m.r2.core.debug;

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.parameterized.PMatrixM4x4F;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.jtensors.parameterized.PVector4FType;
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.jtensors.parameterized.PVectorM4F;
import com.io7m.jtensors.parameterized.PVectorReadable3FType;
import com.io7m.r2.core.shaders.provided.R2AbstractShader;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightSphericalSingleReadableType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2MatricesVolumeLightValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2TransformContextType;
import com.io7m.r2.core.shaders.types.R2ShaderLightVolumeSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightVolumeSingleVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

import java.util.Optional;

/**
 * Spherical light shader that ignores the surface and applies a constant
 * color.
 */

public final class R2DebugShaderLightSphericalConstantSingle extends
  R2AbstractShader<R2LightSphericalSingleReadableType>
  implements R2ShaderLightVolumeSingleType<R2LightSphericalSingleReadableType>
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

  private final PVector4FType<R2SpaceEyeType> position_eye;
  private final PVector3FType<R2SpaceEyeType> position_eye3;
  private final PVector4FType<R2SpaceWorldType> position_world;

  private R2DebugShaderLightSphericalConstantSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2DebugShaderLightSphericalConstantSingle",
      "com.io7m.r2.shaders.core/R2LightPositionalSingle.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2LightSphericalDebugConstantSingle.frag");

    final JCGLProgramShaderUsableType p = this.shaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 9);

    this.u_light_spherical_color =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_spherical.color", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_light_spherical_intensity =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_spherical.intensity", JCGLType.TYPE_FLOAT);
    this.u_light_spherical_position =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_spherical.position", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_light_spherical_inverse_range =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_spherical.inverse_range", JCGLType.TYPE_FLOAT);
    this.u_light_spherical_inverse_falloff =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_spherical.inverse_falloff", JCGLType.TYPE_FLOAT);

    this.u_transform_volume_modelview =
      R2ShaderParameters.getUniformChecked(
        p,
        "R2_light_matrices.transform_volume_modelview",
        JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_projection =
      R2ShaderParameters.getUniformChecked(
        p,
        "R2_light_matrices.transform_projection",
        JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_projection_inverse =
      R2ShaderParameters.getUniformChecked(
        p,
        "R2_light_matrices.transform_projection_inverse",
        JCGLType.TYPE_FLOAT_MATRIX_4);

    this.u_depth_coefficient =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_depth_coefficient", JCGLType.TYPE_FLOAT);

    this.position_eye = new PVectorM4F<>();
    this.position_eye3 = new PVectorM3F<>();
    this.position_world = new PVectorM4F<>();
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

  public static R2ShaderLightVolumeSingleType<R2LightSphericalSingleReadableType>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return R2ShaderLightVolumeSingleVerifier.newVerifier(
      new R2DebugShaderLightSphericalConstantSingle(
        in_shaders, in_shader_env, in_pool));
  }

  @Override
  public Class<R2LightSphericalSingleReadableType>
  shaderParametersType()
  {
    return R2LightSphericalSingleReadableType.class;
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
  }

  @Override
  public void onReceiveValues(
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final JCGLTextureUnitContextMutableType tc,
    final AreaInclusiveUnsignedLType viewport,
    final R2LightSphericalSingleReadableType values,
    final R2MatricesObserverValuesType m)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(g_tex);
    NullCheck.notNull(tc);
    NullCheck.notNull(values);
    NullCheck.notNull(m);

    /*
      Upload the scene's depth coefficient.
     */

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(m.projection()));

    /*
      Upload the projection for the light volume.
     */

    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection, m.matrixProjection());
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection_inverse, m.matrixProjectionInverse());

    /*
      Transform the light's position to eye-space and upload it.
     */

    final PVectorReadable3FType<R2SpaceWorldType> position =
      values.originPosition();
    this.position_world.copyFrom3F(position);
    this.position_world.setWF(0.0f);

    final R2TransformContextType trc = m.transformContext();
    PMatrixM4x4F.multiplyVector4F(
      trc.contextPM4F(),
      m.matrixView(),
      this.position_world,
      this.position_eye);

    this.position_eye3.copyFrom3F(this.position_eye);

    g_sh.shaderUniformPutVector3f(
      this.u_light_spherical_position, this.position_eye3);

    /*
      Upload light values.
     */

    g_sh.shaderUniformPutVector3f(
      this.u_light_spherical_color, values.color());
    g_sh.shaderUniformPutFloat(
      this.u_light_spherical_intensity, values.intensity());
    g_sh.shaderUniformPutFloat(
      this.u_light_spherical_inverse_falloff, 1.0f / values.falloff());
    g_sh.shaderUniformPutFloat(
      this.u_light_spherical_inverse_range, 1.0f / values.radius());
  }

  @Override
  public void onReceiveVolumeLightTransform(
    final JCGLShadersType g_sh,
    final R2MatricesVolumeLightValuesType m)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);

    /*
      Upload the light volume modelview matrix.
     */

    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_volume_modelview, m.matrixLightModelView());
  }
}
