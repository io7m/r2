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

package com.io7m.r2.filters.debug.eye_z;

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.filters.debug.eye_z.api.R2FilterDebugEyeZParameters;
import com.io7m.r2.matrices.R2MatricesObserverValuesType;
import com.io7m.r2.projections.R2Projections;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferUsableType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.api.R2ShaderStateChecking;
import com.io7m.r2.shaders.filter.api.R2AbstractFilterShader;
import com.io7m.r2.shaders.filter.api.R2ShaderParametersFilterType;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT;
import static com.io7m.jcanephora.core.JCGLType.TYPE_SAMPLER_2D;
import static com.io7m.r2.shaders.api.R2ShaderParameters.checkUniformParameterCount;
import static com.io7m.r2.shaders.api.R2ShaderParameters.uniform;

/**
 * A shader that recovers the eye-space Z value of the g-buffer surface.
 */

public final class R2ShaderFilterDebugEyeZ
  extends R2AbstractFilterShader<R2FilterDebugEyeZParameters>
{
  private final JCGLProgramUniformType u_gbuffer_albedo;
  private final JCGLProgramUniformType u_gbuffer_normal;
  private final JCGLProgramUniformType u_gbuffer_specular;
  private final JCGLProgramUniformType u_gbuffer_depth;
  private final JCGLProgramUniformType u_viewport_inverse_width;
  private final JCGLProgramUniformType u_viewport_inverse_height;
  private final JCGLProgramUniformType u_depth_coefficient;

  private R2ShaderFilterDebugEyeZ(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2ShaderFilterDebugEyeZ",
      "com.io7m.r2.shaders.core/R2DebugPositionOnly.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2DebugEyeZReconstruction.frag",
      in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_gbuffer_albedo =
      uniform(p, "R2_gbuffer.albedo", TYPE_SAMPLER_2D);
    this.u_gbuffer_normal =
      uniform(p, "R2_gbuffer.normal", TYPE_SAMPLER_2D);
    this.u_gbuffer_specular =
      uniform(p, "R2_gbuffer.specular", TYPE_SAMPLER_2D);
    this.u_gbuffer_depth =
      uniform(p, "R2_gbuffer.depth", TYPE_SAMPLER_2D);

    this.u_viewport_inverse_width =
      uniform(p, "R2_viewport.inverse_width", TYPE_FLOAT);
    this.u_viewport_inverse_height =
      uniform(p, "R2_viewport.inverse_height", TYPE_FLOAT);

    this.u_depth_coefficient =
      uniform(p, "R2_depth_coefficient", TYPE_FLOAT);

    checkUniformParameterCount(p, 7);
  }

  /**
   * Construct a new shader.
   *
   * @param in_shaders    A shader interface
   * @param in_shader_env Shader sources
   * @param in_pool       The ID pool
   *
   * @return A new shader
   */

  public static R2ShaderFilterDebugEyeZ newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2ShaderFilterDebugEyeZ(
      in_shaders, in_shader_env, in_pool, R2ShaderStateChecking.STATE_CHECK);
  }

  @Override
  public Class<R2FilterDebugEyeZParameters>
  shaderParametersType()
  {
    return R2FilterDebugEyeZParameters.class;
  }

  @Override
  protected void onActualReceiveFilterValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersFilterType<R2FilterDebugEyeZParameters> parameters)
  {
    final R2FilterDebugEyeZParameters values =
      parameters.values();
    final JCGLTextureUnitContextMutableType tc =
      parameters.textureUnitContext();

    final JCGLShadersType g_sh = g.shaders();
    final JCGLTexturesType g_tex = g.textures();

    /*
     * Set each of the required G-Buffer textures.
     */

    final R2GeometryBufferUsableType gbuffer = values.geometryBuffer();
    final JCGLTextureUnitType unit_albedo =
      tc.unitContextBindTexture2D(
        g_tex,
        gbuffer.albedoEmissiveTexture().texture());
    final JCGLTextureUnitType unit_normals =
      tc.unitContextBindTexture2D(
        g_tex,
        gbuffer.normalTexture().texture());
    final JCGLTextureUnitType unit_specular =
      tc.unitContextBindTexture2D(
        g_tex,
        gbuffer.specularTextureOrDefault(values.textureDefaults()).texture());
    final JCGLTextureUnitType unit_depth =
      tc.unitContextBindTexture2D(
        g_tex,
        gbuffer.depthTexture().texture());

    g_sh.shaderUniformPutTexture2DUnit(this.u_gbuffer_albedo, unit_albedo);
    g_sh.shaderUniformPutTexture2DUnit(this.u_gbuffer_normal, unit_normals);
    g_sh.shaderUniformPutTexture2DUnit(this.u_gbuffer_specular, unit_specular);
    g_sh.shaderUniformPutTexture2DUnit(this.u_gbuffer_depth, unit_depth);

    /*
     * Upload the viewport.
     */

    final AreaSizeL area = gbuffer.description().area();
    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_width,
      (float) (1.0 / (double) area.sizeX()));
    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_height,
      (float) (1.0 / (double) area.sizeY()));

    /*
     * Upload the scene's depth coefficient.
     */

    final R2MatricesObserverValuesType m = values.observerValues();
    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(m.projection()));
  }
}
