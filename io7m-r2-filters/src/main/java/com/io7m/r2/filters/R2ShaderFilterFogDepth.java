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
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2AbstractShader;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

/**
 * A shader that renders fog based on depth.
 */

public abstract class R2ShaderFilterFogDepth extends
  R2AbstractShader<R2ShaderFilterFogParameters>
  implements R2ShaderFilterType<R2ShaderFilterFogParameters>
{
  private final JCGLProgramUniformType u_image_texture;
  private final JCGLProgramUniformType u_image_depth_texture;
  private final JCGLProgramUniformType u_fog_near_z;
  private final JCGLProgramUniformType u_fog_far_z;
  private final JCGLProgramUniformType u_fog_color;
  private final JCGLProgramUniformType u_depth_coefficient;

  protected R2ShaderFilterFogDepth(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final String type)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2ShaderFilterFogDepth" + type,
      "com.io7m.r2.shaders.core/R2Filter.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2FilterFogDepth" + type + ".frag");

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_image_texture =
      R2ShaderParameters.getUniformChecked(
        p, "R2_image", JCGLType.TYPE_SAMPLER_2D);
    this.u_image_depth_texture =
      R2ShaderParameters.getUniformChecked(
        p, "R2_image_depth", JCGLType.TYPE_SAMPLER_2D);

    this.u_depth_coefficient =
      R2ShaderParameters.getUniformChecked(
        p, "R2_depth_coefficient", JCGLType.TYPE_FLOAT);

    this.u_fog_near_z =
      R2ShaderParameters.getUniformChecked(
        p, "R2_fog.z_near", JCGLType.TYPE_FLOAT);
    this.u_fog_far_z =
      R2ShaderParameters.getUniformChecked(
        p, "R2_fog.z_far", JCGLType.TYPE_FLOAT);
    this.u_fog_color =
      R2ShaderParameters.getUniformChecked(
        p, "R2_fog.color", JCGLType.TYPE_FLOAT_VECTOR_3);

    R2ShaderParameters.checkUniformParameterCount(p, 6);
  }

  @Override
  public final Class<R2ShaderFilterFogParameters>
  shaderParametersType()
  {
    return R2ShaderFilterFogParameters.class;
  }

  @Override
  public final void onValidate()
    throws R2ExceptionShaderValidationFailed
  {
    // Nothing
  }

  @Override
  public final void onReceiveFilterValues(
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final JCGLTextureUnitContextMutableType tc,
    final R2ShaderFilterFogParameters values)
  {
    NullCheck.notNull(g_tex);
    NullCheck.notNull(tc);
    NullCheck.notNull(g_sh);
    NullCheck.notNull(values);

    final JCGLTextureUnitType unit_image_texture = tc.unitContextBindTexture2D(
      g_tex,
      values.imageTexture().texture());
    final JCGLTextureUnitType unit_depth_texture = tc.unitContextBindTexture2D(
      g_tex,
      values.imageDepthTexture().texture());

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_image_texture, unit_image_texture);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_image_depth_texture, unit_depth_texture);

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(
        values.observerValues().projection()));

    g_sh.shaderUniformPutVector3f(
      this.u_fog_color, values.fogColor());
    g_sh.shaderUniformPutFloat(
      this.u_fog_far_z, values.fogFarPositiveZ());
    g_sh.shaderUniformPutFloat(
      this.u_fog_near_z, values.fogNearPositiveZ());
  }
}
