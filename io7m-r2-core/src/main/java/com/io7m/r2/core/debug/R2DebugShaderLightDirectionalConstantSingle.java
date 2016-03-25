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
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
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
import com.io7m.r2.core.R2LightDirectionalSingle;
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2MatricesProjectiveLightValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2TextureUnitContextMutableType;
import com.io7m.r2.core.R2TransformContextType;
import com.io7m.r2.core.shaders.types.R2ShaderLightScreenSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderProjectiveRequired;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import org.valid4j.Assertive;

import java.util.Map;
import java.util.Optional;

/**
 * Directional light shader that ignores the surface and applies a constant
 * color.
 */

public final class R2DebugShaderLightDirectionalConstantSingle extends
  R2AbstractShader<R2LightDirectionalSingle>
  implements R2ShaderLightScreenSingleType<R2LightDirectionalSingle>
{
  private final JCGLProgramUniformType          u_transform_modelview;
  private final JCGLProgramUniformType          u_transform_projection;
  private final JCGLProgramUniformType          u_transform_projection_inverse;
  private final JCGLProgramUniformType          u_depth_coefficient;
  private final JCGLProgramUniformType          u_light_directional_color;
  private final JCGLProgramUniformType          u_light_directional_direction;
  private final JCGLProgramUniformType          u_light_directional_intensity;
  private final PVector4FType<R2SpaceEyeType>   direction_eye;
  private final PVector3FType<R2SpaceEyeType>   direction_eye3;
  private final PVector4FType<R2SpaceWorldType> direction_world;

  private R2DebugShaderLightDirectionalConstantSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_sources,
      in_pool,
      "R2LightDirectionalDebugConstantSingle",
      "R2LightDirectionalDebugConstantSingle.vert",
      Optional.empty(),
      "R2LightDirectionalDebugConstantSingle.frag");

    this.direction_eye = new PVectorM4F<>();
    this.direction_eye3 = new PVectorM3F<>();
    this.direction_world = new PVectorM4F<>();

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    final Map<String, JCGLProgramUniformType> us = p.getUniforms();
    Assertive.ensure(
      us.size() == 7,
      "Expected number of parameters is 7 (got %d)",
      Integer.valueOf(us.size()));

    this.u_light_directional_color =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_directional.color");
    this.u_light_directional_direction =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_directional.direction");
    this.u_light_directional_intensity =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_directional.intensity");

    this.u_transform_modelview =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_matrices.transform_modelview");
    this.u_transform_projection =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_matrices.transform_projection");
    this.u_transform_projection_inverse =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_matrices.transform_projection_inverse");

    this.u_depth_coefficient =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_depth_coefficient");
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

  public static R2ShaderLightSingleType<R2LightDirectionalSingle>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return R2ShaderLightVerifier.newVerifier(
      new R2DebugShaderLightDirectionalConstantSingle(
        in_shaders, in_sources, in_pool),
      R2ShaderProjectiveRequired.R2_SHADER_PROJECTIVE_NOT_REQUIRED);
  }

  @Override
  public Class<R2LightDirectionalSingle>
  getShaderParametersType()
  {
    return R2LightDirectionalSingle.class;
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
  public void onReceiveProjectiveLight(
    final JCGLShadersType g_sh,
    final R2MatricesProjectiveLightValuesType m)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);
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
    final AreaInclusiveUnsignedLType viewport,
    final R2LightDirectionalSingle values,
    final R2MatricesObserverValuesType m)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);
    NullCheck.notNull(values);

    /**
     * Upload the projections for the light volume.
     */

    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection, m.getMatrixProjection());
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection_inverse, m.getMatrixProjectionInverse());

    /**
     * Upload the scene's depth coefficient.
     */

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(m.getProjection()));

    /**
     * Transform the light's direction to eye-space and upload it.
     */

    final PVector3FType<R2SpaceWorldType> direction = values.getDirection();
    this.direction_world.copyFrom3F(direction);
    this.direction_world.setWF(0.0f);

    final R2TransformContextType trc = m.getTransformContext();
    PMatrixM4x4F.multiplyVector4F(
      trc.getContextPM4F(),
      m.getMatrixView(),
      this.direction_world,
      this.direction_eye);

    this.direction_eye3.copyFrom3F(this.direction_eye);

    g_sh.shaderUniformPutVector3f(
      this.u_light_directional_direction, this.direction_eye3);

    /**
     * Upload the light values.
     */

    g_sh.shaderUniformPutVector3f(
      this.u_light_directional_color, values.getColor());
    g_sh.shaderUniformPutFloat(
      this.u_light_directional_intensity, values.getIntensity());
  }
}
