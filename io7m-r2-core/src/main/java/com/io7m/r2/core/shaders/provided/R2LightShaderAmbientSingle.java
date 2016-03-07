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
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2AbstractShader;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightAmbientSingle;
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2MatricesProjectiveLightValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2TextureUnitContextMutableType;
import com.io7m.r2.core.shaders.types.R2ShaderLightScreenSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderProjectiveRequired;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;

import java.util.Optional;

/**
 * Ambient light shader for single lights.
 */

public final class R2LightShaderAmbientSingle extends
  R2AbstractShader<R2LightAmbientSingle>
  implements R2ShaderLightScreenSingleType<R2LightAmbientSingle>
{
  private final JCGLProgramUniformType u_transform_modelview;
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_transform_projection_inverse;
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_viewport_inverse_width;
  private final JCGLProgramUniformType u_viewport_inverse_height;
  private final JCGLProgramUniformType u_light_color;
  private final JCGLProgramUniformType u_light_intensity;
  private final JCGLProgramUniformType u_light_occlusion;
  private       JCGLTextureUnitType    unit_ao;

  private R2LightShaderAmbientSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_sources,
      in_pool,
      "R2LightAmbientSingle",
      "R2LightAmbientSingle.vert",
      Optional.empty(),
      "R2LightAmbientSingle.frag");

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 9);

    this.u_light_color =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_ambient.color");
    this.u_light_intensity =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_ambient.intensity");
    this.u_light_occlusion =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_ambient.occlusion");

    this.u_transform_modelview =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_matrices.transform_modelview");
    this.u_transform_projection =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_matrices.transform_projection");
    this.u_transform_projection_inverse =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_matrices.transform_projection_inverse");

    this.u_viewport_inverse_width =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_viewport.inverse_width");
    this.u_viewport_inverse_height =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_viewport.inverse_height");

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

  public static R2ShaderLightSingleType<R2LightAmbientSingle>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return R2ShaderLightVerifier.newVerifier(
      new R2LightShaderAmbientSingle(in_shaders, in_sources, in_pool),
      R2ShaderProjectiveRequired.R2_SHADER_PROJECTIVE_NOT_REQUIRED);
  }

  @Override
  public Class<R2LightAmbientSingle>
  getShaderParametersType()
  {
    return R2LightAmbientSingle.class;
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
  }

  @Override
  public void onReceiveProjectiveLight(
    final JCGLShadersType g_sh,
    final R2MatricesProjectiveLightValuesType m)
  {
    // Nothing
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
    final R2LightAmbientSingle values,
    final R2MatricesObserverValuesType m)
  {
    NullCheck.notNull(g_tex);
    NullCheck.notNull(g_sh);
    NullCheck.notNull(tc);
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
     * Upload the occlusion texture and light values.
     */

    this.unit_ao =
      tc.unitContextBindTexture2D(g_tex, values.getOcclusionMap());
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_light_occlusion, this.unit_ao);

    g_sh.shaderUniformPutVector3f(
      this.u_light_color, values.getColor());
    g_sh.shaderUniformPutFloat(
      this.u_light_intensity, values.getIntensity());
  }
}
