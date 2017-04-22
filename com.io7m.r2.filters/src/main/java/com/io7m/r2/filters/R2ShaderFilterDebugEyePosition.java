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

package com.io7m.r2.filters;

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2ViewRaysReadableType;
import com.io7m.r2.core.shaders.provided.R2AbstractShader;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderParametersFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

/**
 * A shader that recovers the eye-space position of the g-buffer surface.
 */

public final class R2ShaderFilterDebugEyePosition extends
  R2AbstractShader<R2FilterDebugEyePositionParameters>
  implements R2ShaderFilterType<R2FilterDebugEyePositionParameters>
{
  private final JCGLProgramUniformType u_gbuffer_albedo;
  private final JCGLProgramUniformType u_gbuffer_normal;
  private final JCGLProgramUniformType u_gbuffer_specular;
  private final JCGLProgramUniformType u_gbuffer_depth;
  private final JCGLProgramUniformType u_viewport_inverse_width;
  private final JCGLProgramUniformType u_viewport_inverse_height;
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_view_rays_origin_x0y0;
  private final JCGLProgramUniformType u_view_rays_origin_x1y0;
  private final JCGLProgramUniformType u_view_rays_origin_x0y1;
  private final JCGLProgramUniformType u_view_rays_origin_x1y1;
  private final JCGLProgramUniformType u_view_rays_ray_x0y0;
  private final JCGLProgramUniformType u_view_rays_ray_x1y0;
  private final JCGLProgramUniformType u_view_rays_ray_x0y1;
  private final JCGLProgramUniformType u_view_rays_ray_x1y1;

  private R2ShaderFilterDebugEyePosition(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2ShaderFilterDebugEyePosition",
      "com.io7m.r2.shaders.core/R2DebugPositionOnly.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2DebugEyePositionReconstruction.frag");

    final JCGLProgramShaderUsableType p = this.shaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 15);

    this.u_gbuffer_albedo =
      R2ShaderParameters.getUniformChecked(
        p, "R2_gbuffer.albedo", JCGLType.TYPE_SAMPLER_2D);
    this.u_gbuffer_normal =
      R2ShaderParameters.getUniformChecked(
        p, "R2_gbuffer.normal", JCGLType.TYPE_SAMPLER_2D);
    this.u_gbuffer_specular =
      R2ShaderParameters.getUniformChecked(
        p, "R2_gbuffer.specular", JCGLType.TYPE_SAMPLER_2D);
    this.u_gbuffer_depth =
      R2ShaderParameters.getUniformChecked(
        p, "R2_gbuffer.depth", JCGLType.TYPE_SAMPLER_2D);

    this.u_viewport_inverse_width =
      R2ShaderParameters.getUniformChecked(
        p, "R2_viewport.inverse_width", JCGLType.TYPE_FLOAT);
    this.u_viewport_inverse_height =
      R2ShaderParameters.getUniformChecked(
        p, "R2_viewport.inverse_height", JCGLType.TYPE_FLOAT);

    this.u_view_rays_origin_x0y0 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_view_rays.origin_x0y0", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_origin_x1y0 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_view_rays.origin_x1y0", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_origin_x0y1 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_view_rays.origin_x0y1", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_origin_x1y1 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_view_rays.origin_x1y1", JCGLType.TYPE_FLOAT_VECTOR_3);

    this.u_view_rays_ray_x0y0 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_view_rays.ray_x0y0", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_ray_x1y0 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_view_rays.ray_x1y0", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_ray_x0y1 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_view_rays.ray_x0y1", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_ray_x1y1 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_view_rays.ray_x1y1", JCGLType.TYPE_FLOAT_VECTOR_3);

    this.u_depth_coefficient =
      R2ShaderParameters.getUniformChecked(
        p, "R2_depth_coefficient", JCGLType.TYPE_FLOAT);
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

  public static R2ShaderFilterDebugEyePosition newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2ShaderFilterDebugEyePosition(
      in_shaders,
      in_shader_env,
      in_pool);
  }

  @Override
  public Class<R2FilterDebugEyePositionParameters>
  shaderParametersType()
  {
    return R2FilterDebugEyePositionParameters.class;
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {
    // Nothing
  }

  @Override
  public void onReceiveFilterValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersFilterType<R2FilterDebugEyePositionParameters> parameters)
  {
    NullCheck.notNull(g, "G33");
    NullCheck.notNull(parameters, "Filter parameters");

    final R2FilterDebugEyePositionParameters values =
      parameters.values();
    final JCGLTextureUnitContextMutableType tc =
      parameters.textureUnitContext();

    final JCGLShadersType g_sh = g.shaders();
    final JCGLTexturesType g_tex = g.textures();

    /*
     * Set each of the required G-Buffer textures.
     */

    final R2GeometryBufferUsableType gbuffer = values.geometryBuffer();
    final JCGLTextureUnitType unit_normals =
      tc.unitContextBindTexture2D(g_tex, gbuffer.normalTexture().texture());
    final JCGLTextureUnitType unit_depth =
      tc.unitContextBindTexture2D(g_tex, gbuffer.depthTexture().texture());

    final JCGLTextureUnitType unit_albedo =
      tc.unitContextBindTexture2D(
        g_tex,
        gbuffer.albedoEmissiveTexture().texture());
    final JCGLTextureUnitType unit_specular =
      tc.unitContextBindTexture2D(
        g_tex,
        gbuffer.specularTextureOrDefault(values.textureDefaults()).texture());

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
      (float) (1.0 / (double) area.width()));
    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_height,
      (float) (1.0 / (double) area.height()));

    /*
     * Upload the scene's depth coefficient.
     */

    final R2MatricesObserverValuesType m = values.observerValues();
    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(m.projection()));

    /*
     * Upload the current view rays.
     */

    final R2ViewRaysReadableType view_rays = m.viewRays();
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_origin_x0y0, view_rays.originX0Y0());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_origin_x1y0, view_rays.originX1Y0());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_origin_x0y1, view_rays.originX0Y1());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_origin_x1y1, view_rays.originX1Y1());

    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_ray_x0y0, view_rays.rayX0Y0());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_ray_x1y0, view_rays.rayX1Y0());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_ray_x0y1, view_rays.rayX0Y1());
    g_sh.shaderUniformPutVector3f(
      this.u_view_rays_ray_x1y1, view_rays.rayX1Y1());
  }
}
