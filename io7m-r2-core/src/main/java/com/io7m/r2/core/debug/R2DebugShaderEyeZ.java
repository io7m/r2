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
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2AbstractShader;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2ShaderGBufferConsumerType;
import com.io7m.r2.core.R2ShaderParameters;
import com.io7m.r2.core.R2ShaderScreenType;
import com.io7m.r2.core.R2ShaderSourcesType;
import org.valid4j.Assertive;

import java.util.Map;
import java.util.Optional;

/**
 * A shader that recovers the eye-space Z value of the g-buffer surface.
 */

public final class R2DebugShaderEyeZ extends
  R2AbstractShader<Unit>
  implements R2ShaderScreenType<Unit>, R2ShaderGBufferConsumerType
{
  private final JCGLProgramUniformType u_gbuffer_albedo;
  private final JCGLProgramUniformType u_gbuffer_normal;
  private final JCGLProgramUniformType u_gbuffer_specular;
  private final JCGLProgramUniformType u_gbuffer_depth;
  private final JCGLProgramUniformType u_viewport_inverse_width;
  private final JCGLProgramUniformType u_viewport_inverse_height;
  private final JCGLProgramUniformType u_depth_coefficient;

  private R2DebugShaderEyeZ(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_sources,
      in_pool,
      "R2DebugEyeZReconstruction",
      "R2DebugEyeZReconstruction.vert",
      Optional.empty(),
      "R2DebugEyeZReconstruction.frag");

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    final Map<String, JCGLProgramUniformType> us = p.getUniforms();
    Assertive.ensure(
      us.size() == 7,
      "Expected number of parameters is 7 (got %d)",
      Integer.valueOf(us.size()));

    this.u_gbuffer_albedo =
      R2ShaderParameters.getUniformChecked(
        p, "R2_gbuffer.albedo");
    this.u_gbuffer_normal =
      R2ShaderParameters.getUniformChecked(
        p, "R2_gbuffer.normal");
    this.u_gbuffer_specular =
      R2ShaderParameters.getUniformChecked(
        p, "R2_gbuffer.specular");
    this.u_gbuffer_depth =
      R2ShaderParameters.getUniformChecked(
        p, "R2_gbuffer.depth");

    this.u_viewport_inverse_width =
      R2ShaderParameters.getUniformChecked(
        p, "R2_viewport.inverse_width");
    this.u_viewport_inverse_height =
      R2ShaderParameters.getUniformChecked(
        p, "R2_viewport.inverse_height");

    this.u_depth_coefficient =
      R2ShaderParameters.getUniformChecked(
        p, "R2_depth_coefficient");
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

  public static R2DebugShaderEyeZ newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return new R2DebugShaderEyeZ(in_shaders, in_sources, in_pool);
  }

  @Override
  public void setGBuffer(
    final JCGLShadersType g_sh,
    final JCGLTexturesType g_tex,
    final R2GeometryBufferUsableType g,
    final JCGLTextureUnitType unit_albedo,
    final JCGLTextureUnitType unit_specular,
    final JCGLTextureUnitType unit_depth,
    final JCGLTextureUnitType unit_normals)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(g_tex);
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

    /**
     * Upload the viewport.
     */

    final AreaInclusiveUnsignedLType area = g.getArea();
    final UnsignedRangeInclusiveL range_x = area.getRangeX();
    final UnsignedRangeInclusiveL range_y = area.getRangeY();
    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_width,
      (float) (1.0 / (double) range_x.getInterval()));
    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_height,
      (float) (1.0 / (double) range_y.getInterval()));
  }

  @Override
  public Class<Unit>
  getShaderParametersType()
  {
    return Unit.class;
  }

  /**
   * Set any view-dependent values.
   *
   * @param g_sh A shader interface
   * @param m    View matrices
   */

  public void setViewDependentValues(
    final JCGLShadersType g_sh,
    final R2MatricesObserverType m)
  {
    /**
     * Upload the scene's depth coefficient.
     */

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(m.getProjection()));
  }
}
