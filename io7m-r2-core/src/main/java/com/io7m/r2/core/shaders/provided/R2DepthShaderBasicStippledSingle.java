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
import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.VectorM2F;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.shaders.types.R2ShaderDepthSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderDepthSingleVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderParametersMaterialType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersViewType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

/**
 * Basic depth shader for single instances.
 */

public final class R2DepthShaderBasicStippledSingle extends
  R2AbstractShader<R2DepthShaderBasicStippledParameters>
  implements R2ShaderDepthSingleType<R2DepthShaderBasicStippledParameters>
{
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_transform_normal;
  private final JCGLProgramUniformType u_transform_modelview;
  private final JCGLProgramUniformType u_transform_view;
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_transform_uv;
  private final JCGLProgramUniformType u_texture_albedo;
  private final JCGLProgramUniformType u_alpha_discard_threshold;
  private final JCGLProgramUniformType u_texture_stipple;
  private final JCGLProgramUniformType u_stipple_noise_uv_scale;
  private final JCGLProgramUniformType u_stipple_threshold;
  private final JCGLProgramUniformType u_viewport_inverse_width;
  private final JCGLProgramUniformType u_viewport_inverse_height;
  private final VectorM2F noise_uv_scale;
  private long viewport_w;
  private long viewport_h;

  private R2DepthShaderBasicStippledSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2DepthBasicStippledSingle",
      "com.io7m.r2.shaders.core/R2DepthSingle.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2DepthBasicStippledSingle.frag");

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_transform_projection = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_projection", JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_view = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_view", JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_depth_coefficient = R2ShaderParameters.getUniformChecked(
      p, "R2_view.depth_coefficient", JCGLType.TYPE_FLOAT);

    this.u_transform_normal = R2ShaderParameters.getUniformChecked(
      p,
      "R2_surface_matrices_instance.transform_normal",
      JCGLType.TYPE_FLOAT_MATRIX_3);
    this.u_transform_modelview = R2ShaderParameters.getUniformChecked(
      p,
      "R2_surface_matrices_instance.transform_modelview",
      JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_uv = R2ShaderParameters.getUniformChecked(
      p,
      "R2_surface_matrices_instance.transform_uv",
      JCGLType.TYPE_FLOAT_MATRIX_3);

    this.u_alpha_discard_threshold = R2ShaderParameters.getUniformChecked(
      p, "R2_alpha_discard_threshold", JCGLType.TYPE_FLOAT);
    this.u_texture_albedo = R2ShaderParameters.getUniformChecked(
      p, "R2_texture_albedo", JCGLType.TYPE_SAMPLER_2D);

    this.u_texture_stipple = R2ShaderParameters.getUniformChecked(
      p, "R2_stipple.pattern", JCGLType.TYPE_SAMPLER_2D);
    this.u_stipple_noise_uv_scale = R2ShaderParameters.getUniformChecked(
      p, "R2_stipple.pattern_uv_scale", JCGLType.TYPE_FLOAT_VECTOR_2);
    this.u_stipple_threshold = R2ShaderParameters.getUniformChecked(
      p, "R2_stipple.threshold", JCGLType.TYPE_FLOAT);

    this.u_viewport_inverse_width =
      R2ShaderParameters.getUniformChecked(
        p, "R2_viewport.inverse_width", JCGLType.TYPE_FLOAT);
    this.u_viewport_inverse_height =
      R2ShaderParameters.getUniformChecked(
        p, "R2_viewport.inverse_height", JCGLType.TYPE_FLOAT);

    R2ShaderParameters.checkUniformParameterCount(p, 13);
    this.noise_uv_scale = new VectorM2F();
  }

  /**
   * Construct a new shader.
   *
   * @param in_shaders    A shader interface
   * @param in_shader_env The shader preprocessing environment
   * @param in_pool       The ID pool
   *
   * @return A new shader
   */

  public static R2ShaderDepthSingleType<R2DepthShaderBasicStippledParameters>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return R2ShaderDepthSingleVerifier.newVerifier(
      new R2DepthShaderBasicStippledSingle(in_shaders, in_shader_env, in_pool));
  }

  @Override
  public Class<R2DepthShaderBasicStippledParameters> shaderParametersType()
  {
    return R2DepthShaderBasicStippledParameters.class;
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {
    // Nothing
  }

  @Override
  public void onReceiveInstanceTransformValues(
    final JCGLInterfaceGL33Type g,
    final R2MatricesInstanceSingleValuesType m)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(m);

    final JCGLShadersType g_sh = g.getShaders();

    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_modelview, m.matrixModelView());
    g_sh.shaderUniformPutMatrix3x3f(
      this.u_transform_normal, m.matrixNormal());
    g_sh.shaderUniformPutMatrix3x3f(
      this.u_transform_uv, m.matrixUV());
  }

  @Override
  public void onReceiveViewValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersViewType view_parameters)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(view_parameters);

    final JCGLShadersType g_sh = g.getShaders();
    final R2MatricesObserverValuesType matrices =
      view_parameters.observerMatrices();
    final AreaInclusiveUnsignedLType viewport =
      view_parameters.viewport();

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(matrices.projection()));
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_view, matrices.matrixView());
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection, matrices.matrixProjection());

    /*
     * Upload the viewport.
     */

    this.viewport_w = viewport.getRangeX().getInterval();
    this.viewport_h = viewport.getRangeY().getInterval();

    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_width,
      (float) (1.0 / (double) this.viewport_w));
    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_height,
      (float) (1.0 / (double) this.viewport_h));
  }

  @Override
  public void onReceiveMaterialValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersMaterialType<R2DepthShaderBasicStippledParameters> mat_parameters)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(mat_parameters);

    final JCGLTexturesType g_tex = g.getTextures();
    final JCGLShadersType g_sh = g.getShaders();

    final JCGLTextureUnitContextMutableType tc =
      mat_parameters.textureUnitContext();
    final R2DepthShaderBasicStippledParameters values =
      mat_parameters.values();

    final JCGLTexture2DUsableType noise =
      values.stippleNoiseTexture().texture();
    this.noise_uv_scale.set2F(
      (float) (this.viewport_w / noise.textureGetWidth()),
      (float) (this.viewport_h / noise.textureGetHeight()));

    g_sh.shaderUniformPutVector2f(
      this.u_stipple_noise_uv_scale, this.noise_uv_scale);

    final JCGLTextureUnitType unit_albedo =
      tc.unitContextBindTexture2D(g_tex, values.albedoTexture().texture());
    final JCGLTextureUnitType unit_stipple =
      tc.unitContextBindTexture2D(
        g_tex, values.stippleNoiseTexture().texture());

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_albedo, unit_albedo);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_stipple, unit_stipple);

    g_sh.shaderUniformPutFloat(
      this.u_alpha_discard_threshold, values.alphaDiscardThreshold());

    g_sh.shaderUniformPutFloat(
      this.u_stipple_threshold, values.stippleThreshold());
  }
}
