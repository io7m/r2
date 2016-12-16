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
import com.io7m.jcanephora.core.JCGLType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2AbstractShader;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

/**
 * Shader for refracting single instances.
 */

public final class R2RefractionMaskedDeltaShaderBatched
  extends R2AbstractShader<R2RefractionMaskedDeltaParameters>
  implements R2ShaderInstanceBatchedType<R2RefractionMaskedDeltaParameters>
{
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_transform_view;
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_refraction_scale;
  private final JCGLProgramUniformType u_refraction_color;
  private final JCGLProgramUniformType u_refraction_scene;
  private final JCGLProgramUniformType u_refraction_mask;
  private final JCGLProgramUniformType u_refraction_delta;

  private R2RefractionMaskedDeltaShaderBatched(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2RefractionMaskedDelta",
      "com.io7m.r2.shaders.core/R2RefractionMaskedDeltaBatched.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2RefractionMaskedDelta.frag");

    final JCGLProgramShaderUsableType p = this.getShaderProgram();

    this.u_transform_projection = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_projection", JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_view = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_view", JCGLType.TYPE_FLOAT_MATRIX_4);

    this.u_depth_coefficient = R2ShaderParameters.getUniformChecked(
      p, "R2_view.depth_coefficient", JCGLType.TYPE_FLOAT);

    this.u_refraction_scale = R2ShaderParameters.getUniformChecked(
      p, "R2_refraction.scale", JCGLType.TYPE_FLOAT);
    this.u_refraction_color = R2ShaderParameters.getUniformChecked(
      p, "R2_refraction.color", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_refraction_scene = R2ShaderParameters.getUniformChecked(
      p, "R2_refraction.scene", JCGLType.TYPE_SAMPLER_2D);
    this.u_refraction_mask = R2ShaderParameters.getUniformChecked(
      p, "R2_refraction.mask", JCGLType.TYPE_SAMPLER_2D);
    this.u_refraction_delta = R2ShaderParameters.getUniformChecked(
      p, "R2_refraction_delta", JCGLType.TYPE_SAMPLER_2D);

    R2ShaderParameters.checkUniformParameterCount(p, 8);
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

  public static R2ShaderInstanceBatchedType<R2RefractionMaskedDeltaParameters>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return R2ShaderInstanceBatchedVerifier.newVerifier(
      new R2RefractionMaskedDeltaShaderBatched(
        in_shaders, in_shader_env, in_pool));
  }

  @Override
  public Class<R2RefractionMaskedDeltaParameters> getShaderParametersType()
  {
    return R2RefractionMaskedDeltaParameters.class;
  }

  @Override
  public void onReceiveViewValues(
    final JCGLShadersType g_sh,
    final R2MatricesObserverValuesType m,
    final AreaInclusiveUnsignedLType viewport)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);
    NullCheck.notNull(viewport);

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(m.projection()));
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_view, m.matrixView());
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection, m.matrixProjection());
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {

  }

  @Override
  public void onReceiveMaterialValues(
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final JCGLTextureUnitContextMutableType tc,
    final R2RefractionMaskedDeltaParameters values)
  {
    NullCheck.notNull(g_tex);
    NullCheck.notNull(g_sh);
    NullCheck.notNull(tc);
    NullCheck.notNull(values);

    final JCGLTextureUnitType unit_mask =
      tc.unitContextBindTexture2D(g_tex, values.maskTexture().texture());
    final JCGLTextureUnitType unit_scene =
      tc.unitContextBindTexture2D(g_tex, values.sceneTexture().texture());
    final JCGLTextureUnitType unit_delta =
      tc.unitContextBindTexture2D(g_tex, values.deltaTexture().texture());

    g_sh.shaderUniformPutFloat(this.u_refraction_scale, values.scale());
    g_sh.shaderUniformPutVector3f(this.u_refraction_color, values.color());
    g_sh.shaderUniformPutTexture2DUnit(this.u_refraction_mask, unit_mask);
    g_sh.shaderUniformPutTexture2DUnit(this.u_refraction_scene, unit_scene);
    g_sh.shaderUniformPutTexture2DUnit(this.u_refraction_delta, unit_delta);
  }
}
