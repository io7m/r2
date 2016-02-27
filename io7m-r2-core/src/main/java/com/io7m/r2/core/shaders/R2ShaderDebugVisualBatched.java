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

package com.io7m.r2.core.shaders;

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.VectorReadable4FType;
import com.io7m.r2.core.R2AbstractShader;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2ShaderBatchedType;
import com.io7m.r2.core.R2ShaderParameters;
import com.io7m.r2.core.R2ShaderSourcesType;
import com.io7m.r2.core.R2TextureUnitContextMutableType;
import org.valid4j.Assertive;

import java.util.Map;
import java.util.Optional;

/**
 * Debug visualization shader for batched instances.
 */

public final class R2ShaderDebugVisualBatched extends
  R2AbstractShader<VectorReadable4FType>
  implements R2ShaderBatchedType<VectorReadable4FType>
{
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_transform_view;
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_color;

  private R2ShaderDebugVisualBatched(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_sources,
      in_pool,
      "R2DebugVisualConstantBatched",
      "R2DebugVisualConstantBatched.vert",
      Optional.empty(),
      "R2DebugVisualConstantBatched.frag");

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    final Map<String, JCGLProgramUniformType> us = p.getUniforms();
    Assertive.ensure(us.size() == 4, "Expected number of parameters is 4");

    this.u_transform_projection = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_projection");
    this.u_transform_view = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_view");
    this.u_depth_coefficient = R2ShaderParameters.getUniformChecked(
      p, "R2_view.depth_coefficient");

    this.u_color = R2ShaderParameters.getUniformChecked(
      p, "R2_color");
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

  public static R2ShaderBatchedType<VectorReadable4FType> newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return new R2ShaderDebugVisualBatched(in_shaders, in_sources, in_pool);
  }

  @Override
  public void setMaterialTextures(
    final JCGLTexturesType g_tex,
    final R2TextureUnitContextMutableType tc,
    final VectorReadable4FType values)
  {
    NullCheck.notNull(tc);
    NullCheck.notNull(values);
  }

  @Override
  public void setMaterialValues(
    final JCGLShadersType g_sh,
    final VectorReadable4FType values)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(values);

    g_sh.shaderUniformPutVector4f(this.u_color, values);
  }

  @Override
  public void setMatricesView(
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
  public Class<VectorReadable4FType> getShaderParametersType()
  {
    return VectorReadable4FType.class;
  }
}
