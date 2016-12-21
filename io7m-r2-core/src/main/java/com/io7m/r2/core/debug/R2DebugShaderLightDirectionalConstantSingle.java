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
import com.io7m.r2.core.R2AbstractShader;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightDirectionalScreenSingle;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2TransformContextType;
import com.io7m.r2.core.shaders.types.R2ShaderLightScreenSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightScreenSingleVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderLightSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

import java.util.Optional;

/**
 * Directional light shader that ignores the surface and applies a constant
 * color.
 */

public final class R2DebugShaderLightDirectionalConstantSingle extends
  R2AbstractShader<R2LightDirectionalScreenSingle>
  implements R2ShaderLightScreenSingleType<R2LightDirectionalScreenSingle>
{
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_transform_projection_inverse;
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_light_directional_color;
  private final JCGLProgramUniformType u_light_directional_direction;
  private final JCGLProgramUniformType u_light_directional_intensity;
  private final PVector4FType<R2SpaceEyeType> direction_eye;
  private final PVector3FType<R2SpaceEyeType> direction_eye3;
  private final PVector4FType<R2SpaceWorldType> direction_world;

  private R2DebugShaderLightDirectionalConstantSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2DebugShaderLightDirectionalConstantSingle",
      "com.io7m.r2.shaders.core/R2LightDirectionalSingle.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2LightDirectionalDebugConstantSingle.frag");

    this.direction_eye = new PVectorM4F<>();
    this.direction_eye3 = new PVectorM3F<>();
    this.direction_world = new PVectorM4F<>();

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 7);

    this.u_light_directional_color =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_directional.color", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_light_directional_direction =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_directional.direction", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_light_directional_intensity =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_directional.intensity", JCGLType.TYPE_FLOAT);

    final JCGLProgramUniformType u_transform_volume_modelview = R2ShaderParameters.getUniformChecked(
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

  public static R2ShaderLightSingleType<R2LightDirectionalScreenSingle>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return R2ShaderLightScreenSingleVerifier.newVerifier(
      new R2DebugShaderLightDirectionalConstantSingle(
        in_shaders, in_shader_env, in_pool));
  }

  @Override
  public Class<R2LightDirectionalScreenSingle>
  getShaderParametersType()
  {
    return R2LightDirectionalScreenSingle.class;
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
  }

  @Override
  public void onReceiveValues(
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final JCGLTextureUnitContextMutableType tc,
    final AreaInclusiveUnsignedLType viewport,
    final R2LightDirectionalScreenSingle values,
    final R2MatricesObserverValuesType m)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);
    NullCheck.notNull(values);

    /*
      Upload the projections for the light volume.
     */

    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection, m.matrixProjection());
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection_inverse, m.matrixProjectionInverse());

    /*
      Upload the scene's depth coefficient.
     */

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(m.projection()));

    /*
      Transform the light's direction to eye-space and upload it.
     */

    final PVector3FType<R2SpaceWorldType> direction = values.getDirection();
    this.direction_world.copyFrom3F(direction);
    this.direction_world.setWF(0.0f);

    final R2TransformContextType trc = m.transformContext();
    PMatrixM4x4F.multiplyVector4F(
      trc.contextPM4F(),
      m.matrixView(),
      this.direction_world,
      this.direction_eye);

    this.direction_eye3.copyFrom3F(this.direction_eye);

    g_sh.shaderUniformPutVector3f(
      this.u_light_directional_direction, this.direction_eye3);

    /*
      Upload the light values.
     */

    g_sh.shaderUniformPutVector3f(
      this.u_light_directional_color, values.color());
    g_sh.shaderUniformPutFloat(
      this.u_light_directional_intensity, values.intensity());
  }
}
