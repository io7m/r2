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
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2ViewRaysReadableType;
import com.io7m.r2.core.shaders.provided.R2AbstractShader;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderParametersFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

/**
 * An SSAO shader.
 */

public final class R2ShaderSSAO extends
  R2AbstractShader<R2ShaderSSAOParameters>
  implements R2ShaderFilterType<R2ShaderSSAOParameters>
{
  private final VectorM2F noise_uv_scale;
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
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2SSAO",
      "com.io7m.r2.shaders.core/R2Filter.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2SSAO.frag");

    this.noise_uv_scale = new VectorM2F();

    final JCGLProgramShaderUsableType p = this.shaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 20);

    final JCGLProgramUniformType u_gbuffer_albedo =
      R2ShaderParameters.getUniformChecked(
        p, "R2_gbuffer.albedo", JCGLType.TYPE_SAMPLER_2D);
    this.u_gbuffer_normal =
      R2ShaderParameters.getUniformChecked(
        p, "R2_gbuffer.normal", JCGLType.TYPE_SAMPLER_2D);
    final JCGLProgramUniformType u_gbuffer_specular =
      R2ShaderParameters.getUniformChecked(
        p, "R2_gbuffer.specular", JCGLType.TYPE_SAMPLER_2D);
    this.u_gbuffer_depth =
      R2ShaderParameters.getUniformChecked(
        p, "R2_gbuffer.depth", JCGLType.TYPE_SAMPLER_2D);

    this.u_ssao_noise_uv_scale =
      R2ShaderParameters.getUniformChecked(
        p, "R2_ssao_noise_uv_scale", JCGLType.TYPE_FLOAT_VECTOR_2);
    this.u_ssao_kernel =
      R2ShaderParameters.getUniformChecked(
        p, "R2_ssao_kernel[0]", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_ssao_kernel_size =
      R2ShaderParameters.getUniformChecked(
        p, "R2_ssao_kernel_size", JCGLType.TYPE_INTEGER);
    this.u_ssao_texture_noise =
      R2ShaderParameters.getUniformChecked(
        p, "R2_ssao_noise", JCGLType.TYPE_SAMPLER_2D);
    this.u_ssao_sample_radius =
      R2ShaderParameters.getUniformChecked(
        p, "R2_ssao_sample_radius", JCGLType.TYPE_FLOAT);
    this.u_ssao_power =
      R2ShaderParameters.getUniformChecked(
        p, "R2_ssao_power", JCGLType.TYPE_FLOAT);
    this.u_ssao_transform_projection =
      R2ShaderParameters.getUniformChecked(
        p, "R2_ssao_transform_projection", JCGLType.TYPE_FLOAT_MATRIX_4);

    this.u_depth_coefficient =
      R2ShaderParameters.getUniformChecked(
        p, "R2_depth_coefficient", JCGLType.TYPE_FLOAT);

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

  public static R2ShaderFilterType<R2ShaderSSAOParameters>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return R2ShaderFilterVerifier.newVerifier(
      new R2ShaderSSAO(in_shaders, in_shader_env, in_pool));
  }

  @Override
  public Class<R2ShaderSSAOParameters>
  shaderParametersType()
  {
    return R2ShaderSSAOParameters.class;
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {
    // Nothing
  }

  private boolean shouldSetKernel(
    final R2SSAOKernelReadableType k)
  {
    return this.kernel_last == null || this.kernel_version != k.version();
  }

  @Override
  public void onReceiveFilterValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersFilterType<R2ShaderSSAOParameters> parameters)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(parameters);

    final R2ShaderSSAOParameters values =
      parameters.values();
    final JCGLTextureUnitContextMutableType tc =
      parameters.textureUnitContext();

    final JCGLShadersType g_sh = g.getShaders();
    final JCGLTexturesType g_tex = g.getTextures();

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

    g_sh.shaderUniformPutMatrix4x4f(
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

    final AreaInclusiveUnsignedLType viewport_area = values.viewport();
    final UnsignedRangeInclusiveL range_x = viewport_area.getRangeX();
    final UnsignedRangeInclusiveL range_y = viewport_area.getRangeY();

    final JCGLTexture2DUsableType noise = values.noiseTexture().texture();
    this.noise_uv_scale.set2F(
      (float) (range_x.getInterval() / noise.textureGetWidth()),
      (float) (range_y.getInterval() / noise.textureGetHeight())
    );

    g_sh.shaderUniformPutVector2f(
      this.u_ssao_noise_uv_scale, this.noise_uv_scale);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_ssao_texture_noise, unit_noise);
    g_sh.shaderUniformPutFloat(
      this.u_ssao_sample_radius, values.sampleRadius());
    g_sh.shaderUniformPutFloat(
      this.u_ssao_power, values.exponent());

    final R2SSAOKernelReadableType k = values.kernel();
    if (this.shouldSetKernel(k)) {
      g_sh.shaderUniformPutVectorf(this.u_ssao_kernel, k.floatBuffer());
      g_sh.shaderUniformPutInteger(this.u_ssao_kernel_size, k.size());
      this.kernel_last = k;
      this.kernel_version = k.version();
    }
  }
}
