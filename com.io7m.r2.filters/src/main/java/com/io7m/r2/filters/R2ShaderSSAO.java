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
import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jtensors.core.unparameterized.vectors.Vector2D;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2ViewRaysReadableType;
import com.io7m.r2.core.shaders.abstracts.R2AbstractFilterShader;
import com.io7m.r2.core.shaders.abstracts.R2ShaderStateChecking;
import com.io7m.r2.core.shaders.types.R2ShaderParametersFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_MATRIX_4;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_VECTOR_2;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_VECTOR_3;
import static com.io7m.jcanephora.core.JCGLType.TYPE_INTEGER;
import static com.io7m.jcanephora.core.JCGLType.TYPE_SAMPLER_2D;
import static com.io7m.r2.core.shaders.types.R2ShaderParameters.checkUniformParameterCount;
import static com.io7m.r2.core.shaders.types.R2ShaderParameters.uniform;

/**
 * An SSAO shader.
 */

public final class R2ShaderSSAO
  extends R2AbstractFilterShader<R2ShaderSSAOParameters>
{
  private final JCGLProgramUniformType u_ssao_noise_uv_scale;
  private final JCGLProgramUniformType u_ssao_kernel;
  private final JCGLProgramUniformType u_ssao_kernel_size;
  private final JCGLProgramUniformType u_ssao_sample_radius;
  private final JCGLProgramUniformType u_ssao_texture_noise;
  private final JCGLProgramUniformType u_ssao_transform_projection;
  private final JCGLProgramUniformType u_ssao_power;
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_view_rays_origin_x0y0;
  private final JCGLProgramUniformType u_view_rays_origin_x1y0;
  private final JCGLProgramUniformType u_view_rays_origin_x0y1;
  private final JCGLProgramUniformType u_view_rays_origin_x1y1;
  private final JCGLProgramUniformType u_view_rays_ray_x0y0;
  private final JCGLProgramUniformType u_view_rays_ray_x1y0;
  private final JCGLProgramUniformType u_view_rays_ray_x0y1;
  private final JCGLProgramUniformType u_view_rays_ray_x1y1;
  private final JCGLProgramUniformType u_gbuffer_normal;
  private final JCGLProgramUniformType u_gbuffer_depth;

  private long kernel_version;
  private R2SSAOKernelReadableType kernel_last;

  private R2ShaderSSAO(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2SSAO",
      "com.io7m.r2.shaders.core/R2Filter.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2SSAO.frag",
      in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    final JCGLProgramUniformType u_gbuffer_albedo =
      uniform(p, "R2_gbuffer.albedo", TYPE_SAMPLER_2D);
    this.u_gbuffer_normal =
      uniform(p, "R2_gbuffer.normal", TYPE_SAMPLER_2D);
    final JCGLProgramUniformType u_gbuffer_specular =
      uniform(p, "R2_gbuffer.specular", TYPE_SAMPLER_2D);
    this.u_gbuffer_depth =
      uniform(p, "R2_gbuffer.depth", TYPE_SAMPLER_2D);

    this.u_ssao_noise_uv_scale =
      uniform(p, "R2_ssao_noise_uv_scale", TYPE_FLOAT_VECTOR_2);
    this.u_ssao_kernel =
      uniform(p, "R2_ssao_kernel[0]", TYPE_FLOAT_VECTOR_3);
    this.u_ssao_kernel_size =
      uniform(p, "R2_ssao_kernel_size", TYPE_INTEGER);
    this.u_ssao_texture_noise =
      uniform(p, "R2_ssao_noise", TYPE_SAMPLER_2D);
    this.u_ssao_sample_radius =
      uniform(p, "R2_ssao_sample_radius", TYPE_FLOAT);
    this.u_ssao_power =
      uniform(p, "R2_ssao_power", TYPE_FLOAT);
    this.u_ssao_transform_projection =
      uniform(p, "R2_ssao_transform_projection", TYPE_FLOAT_MATRIX_4);

    this.u_depth_coefficient =
      uniform(p, "R2_depth_coefficient", TYPE_FLOAT);

    this.u_view_rays_origin_x0y0 =
      uniform(p, "R2_view_rays.origin_x0y0", TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_origin_x1y0 =
      uniform(p, "R2_view_rays.origin_x1y0", TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_origin_x0y1 =
      uniform(p, "R2_view_rays.origin_x0y1", TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_origin_x1y1 =
      uniform(p, "R2_view_rays.origin_x1y1", TYPE_FLOAT_VECTOR_3);

    this.u_view_rays_ray_x0y0 =
      uniform(p, "R2_view_rays.ray_x0y0", TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_ray_x1y0 =
      uniform(p, "R2_view_rays.ray_x1y0", TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_ray_x0y1 =
      uniform(p, "R2_view_rays.ray_x0y1", TYPE_FLOAT_VECTOR_3);
    this.u_view_rays_ray_x1y1 =
      uniform(p, "R2_view_rays.ray_x1y1", TYPE_FLOAT_VECTOR_3);

    checkUniformParameterCount(p, 20);
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

  public static R2ShaderSSAO create(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2ShaderSSAO(
      in_shaders, in_shader_env, in_pool, R2ShaderStateChecking.STATE_CHECK);
  }

  @Override
  public Class<R2ShaderSSAOParameters>
  shaderParametersType()
  {
    return R2ShaderSSAOParameters.class;
  }

  private boolean shouldSetKernel(
    final R2SSAOKernelReadableType k)
  {
    return this.kernel_last == null || this.kernel_version != k.version();
  }

  @Override
  protected void onActualReceiveFilterValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersFilterType<R2ShaderSSAOParameters> parameters)
  {
    final R2ShaderSSAOParameters values =
      parameters.values();
    final JCGLTextureUnitContextMutableType tc =
      parameters.textureUnitContext();

    final JCGLShadersType g_sh = g.shaders();
    final JCGLTexturesType g_tex = g.textures();

    /*
     * Upload the current view rays.
     */

    final R2MatricesObserverValuesType view = values.viewMatrices();
    final R2ViewRaysReadableType view_rays = view.viewRays();
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

    /*
     * Upload the projections for the light volume.
     */

    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_ssao_transform_projection, view.matrixProjection());

    /*
     * Upload the scene's depth coefficient.
     */

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(view.projection()));

    final JCGLTextureUnitType unit_noise = tc.unitContextBindTexture2D(
      g_tex,
      values.noiseTexture().texture());

    /*
     * Upload the geometry buffer.
     */

    final R2GeometryBufferUsableType gbuffer = values.geometryBuffer();
    final JCGLTextureUnitType unit_depth =
      tc.unitContextBindTexture2D(g_tex, gbuffer.depthTexture().texture());
    final JCGLTextureUnitType unit_normals =
      tc.unitContextBindTexture2D(g_tex, gbuffer.normalTexture().texture());
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_gbuffer_normal, unit_normals);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_gbuffer_depth, unit_depth);

    /*
     * Upload the SSAO-specific parameters.
     */

    final AreaL viewport_area = values.viewport();
    final JCGLTexture2DUsableType noise = values.noiseTexture().texture();
    final Vector2D noise_uv_scale = Vector2D.of(
      (double) viewport_area.sizeX() / (double) noise.sizeX(),
      (double) viewport_area.sizeY() / (double) noise.sizeY());

    g_sh.shaderUniformPutVector2f(
      this.u_ssao_noise_uv_scale, noise_uv_scale);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_ssao_texture_noise, unit_noise);
    g_sh.shaderUniformPutFloat(
      this.u_ssao_sample_radius, (float) values.sampleRadius());
    g_sh.shaderUniformPutFloat(
      this.u_ssao_power, (float) values.exponent());

    final R2SSAOKernelReadableType k = values.kernel();
    if (this.shouldSetKernel(k)) {
      g_sh.shaderUniformPutVectorf(this.u_ssao_kernel, k.floatBuffer());
      g_sh.shaderUniformPutInteger(this.u_ssao_kernel_size, k.size());
      this.kernel_last = k;
      this.kernel_version = k.version();
    }
  }
}
