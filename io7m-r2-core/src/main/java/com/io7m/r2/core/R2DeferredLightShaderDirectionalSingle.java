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

package com.io7m.r2.core;

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import org.valid4j.Assertive;

import java.util.Map;
import java.util.Optional;

/**
 * Directional light shader for single lights.
 */

public final class R2DeferredLightShaderDirectionalSingle extends
  R2AbstractShader<R2LightDirectionalSingle>
  implements R2ShaderScreenType<R2LightDirectionalSingle>
{
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_view_rays_origin_x0y0;
  private final JCGLProgramUniformType u_view_rays_origin_x1y0;
  private final JCGLProgramUniformType u_view_rays_origin_x0y1;
  private final JCGLProgramUniformType u_view_rays_origin_x1y1;
  private final JCGLProgramUniformType u_view_rays_ray_x0y0;
  private final JCGLProgramUniformType u_view_rays_ray_x1y0;
  private final JCGLProgramUniformType u_view_rays_ray_x0y1;
  private final JCGLProgramUniformType u_view_rays_ray_x1y1;
  private final JCGLProgramUniformType u_gbuffer_albedo;
  private final JCGLProgramUniformType u_gbuffer_normal;
  private final JCGLProgramUniformType u_gbuffer_specular;
  private final JCGLProgramUniformType u_gbuffer_depth;
  private final JCGLProgramUniformType u_viewport_inverse_width;
  private final JCGLProgramUniformType u_viewport_inverse_height;
  private final JCGLProgramUniformType u_light_directional_color;
  private final JCGLProgramUniformType u_light_directional_direction;
  private final JCGLProgramUniformType u_light_directional_intensity;

  private R2DeferredLightShaderDirectionalSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_sources,
      in_pool,
      "R2DeferredLightDirectionalSpecularSingle",
      "R2DeferredLightDirectionalSpecularSingle.vert",
      Optional.empty(),
      "R2DeferredLightDirectionalSpecularSingle.frag");

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    final Map<String, JCGLProgramUniformType> us = p.getUniforms();
    Assertive.ensure(
      us.size() == 18,
      "Expected number of parameters is 18 (got %d)",
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

    this.u_viewport_inverse_width =
      R2ShaderParameters.getUniformChecked(
        p, "R2_deferred_light_viewport.inverse_width");
    this.u_viewport_inverse_height =
      R2ShaderParameters.getUniformChecked(
        p, "R2_deferred_light_viewport.inverse_height");

    this.u_gbuffer_albedo =
      R2ShaderParameters.getUniformChecked(
        p, "R2_deferred_light_gbuffer.albedo");
    this.u_gbuffer_normal =
      R2ShaderParameters.getUniformChecked(
        p, "R2_deferred_light_gbuffer.normal");
    this.u_gbuffer_specular =
      R2ShaderParameters.getUniformChecked(
        p, "R2_deferred_light_gbuffer.specular");
    this.u_gbuffer_depth =
      R2ShaderParameters.getUniformChecked(
        p, "R2_deferred_light_gbuffer.depth");

    this.u_depth_coefficient =
      R2ShaderParameters.getUniformChecked(
        p, "R2_deferred_light_depth_coefficient");

    this.u_view_rays_origin_x0y0 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_deferred_light_view_rays.origin_x0y0");
    this.u_view_rays_origin_x1y0 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_deferred_light_view_rays.origin_x1y0");
    this.u_view_rays_origin_x0y1 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_deferred_light_view_rays.origin_x0y1");
    this.u_view_rays_origin_x1y1 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_deferred_light_view_rays.origin_x1y1");

    this.u_view_rays_ray_x0y0 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_deferred_light_view_rays.ray_x0y0");
    this.u_view_rays_ray_x1y0 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_deferred_light_view_rays.ray_x1y0");
    this.u_view_rays_ray_x0y1 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_deferred_light_view_rays.ray_x0y1");
    this.u_view_rays_ray_x1y1 =
      R2ShaderParameters.getUniformChecked(
        p, "R2_deferred_light_view_rays.ray_x1y1");
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

  public static R2ShaderScreenType<R2LightDirectionalSingle>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return new R2DeferredLightShaderDirectionalSingle(
      in_shaders, in_sources, in_pool);
  }

  @Override
  public Class<R2LightDirectionalSingle>
  getShaderParametersType()
  {
    return R2LightDirectionalSingle.class;
  }
}
