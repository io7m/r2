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
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2AbstractShader;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2TextureUnitContextMutableType;
import com.io7m.r2.core.shaders.types.R2ShaderDepthBatchedType;
import com.io7m.r2.core.shaders.types.R2ShaderDepthBatchedVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;

import java.util.Optional;

/**
 * Basic depth shader for single instances.
 */

public final class R2DepthShaderBasicBatched extends
  R2AbstractShader<R2DepthShaderBasicParametersType>
  implements R2ShaderDepthBatchedType<R2DepthShaderBasicParametersType>
{
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_transform_view;
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_texture_albedo;
  private final JCGLProgramUniformType u_alpha_discard_threshold;
  private       JCGLTextureUnitType    unit_albedo;

  private R2DepthShaderBasicBatched(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_sources,
      in_pool,
      "R2DepthBasicBatched",
      "R2DepthBasicBatched.vert",
      Optional.empty(),
      "R2DepthBasicBatched.frag");

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 5);

    this.u_transform_projection = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_projection");
    this.u_transform_view = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_view");
    this.u_depth_coefficient = R2ShaderParameters.getUniformChecked(
      p, "R2_view.depth_coefficient");

    this.u_alpha_discard_threshold = R2ShaderParameters.getUniformChecked(
      p, "R2_alpha_discard_threshold");
    this.u_texture_albedo = R2ShaderParameters.getUniformChecked(
      p, "R2_texture_albedo");
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

  public static R2ShaderDepthBatchedType<R2DepthShaderBasicParametersType>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    final R2DepthShaderBasicBatched s =
      new R2DepthShaderBasicBatched(in_shaders, in_sources, in_pool);
    final R2ShaderDepthBatchedType<R2DepthShaderBasicParametersType> v =
      R2ShaderDepthBatchedVerifier.newVerifier(s);
    return v;
  }

  @Override
  public Class<R2DepthShaderBasicParametersType> getShaderParametersType()
  {
    return R2DepthShaderBasicParametersType.class;
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {
    // Nothing
  }

  @Override
  public void onReceiveViewValues(
    final JCGLShadersType g_sh,
    final R2MatricesObserverValuesType m)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(m.getProjection()));
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_view, m.getMatrixView());
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection, m.getMatrixProjection());
  }

  @Override
  public void onReceiveMaterialValues(
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final R2TextureUnitContextMutableType tc,
    final R2DepthShaderBasicParametersType values)
  {
    NullCheck.notNull(g_tex);
    NullCheck.notNull(g_sh);
    NullCheck.notNull(tc);
    NullCheck.notNull(values);

    this.unit_albedo =
      tc.unitContextBindTexture2D(g_tex, values.getAlbedoTexture());

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_albedo, this.unit_albedo);
    g_sh.shaderUniformPutFloat(
      this.u_alpha_discard_threshold, values.getAlphaDiscardThreshold());
  }
}
