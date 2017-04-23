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
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.shaders.abstracts.R2AbstractFilterShader;
import com.io7m.r2.core.shaders.abstracts.R2ShaderStateChecking;
import com.io7m.r2.core.shaders.types.R2ShaderParametersFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_VECTOR_3;
import static com.io7m.jcanephora.core.JCGLType.TYPE_SAMPLER_2D;
import static com.io7m.r2.core.shaders.types.R2ShaderParameters.checkUniformParameterCount;
import static com.io7m.r2.core.shaders.types.R2ShaderParameters.uniform;

/**
 * A shader that renders fog based on depth.
 */

public abstract class R2ShaderFilterFogDepth
  extends R2AbstractFilterShader<R2ShaderFilterFogParameters>
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
    final String type,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2ShaderFilterFogDepth" + type,
      "com.io7m.r2.shaders.core/R2Filter.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2FilterFogDepth" + type + ".frag", in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_image_texture =
      uniform(p, "R2_image", TYPE_SAMPLER_2D);
    this.u_image_depth_texture =
      uniform(p, "R2_image_depth", TYPE_SAMPLER_2D);

    this.u_depth_coefficient =
      uniform(p, "R2_depth_coefficient", TYPE_FLOAT);

    this.u_fog_near_z =
      uniform(p, "R2_fog.z_near", TYPE_FLOAT);
    this.u_fog_far_z =
      uniform(p, "R2_fog.z_far", TYPE_FLOAT);
    this.u_fog_color =
      uniform(p, "R2_fog.color", TYPE_FLOAT_VECTOR_3);

    checkUniformParameterCount(p, 6);
  }

  @Override
  public final Class<R2ShaderFilterFogParameters>
  shaderParametersType()
  {
    return R2ShaderFilterFogParameters.class;
  }

  @Override
  protected final void onActualReceiveFilterValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersFilterType<R2ShaderFilterFogParameters> parameters)
  {
    final R2ShaderFilterFogParameters values =
      parameters.values();
    final JCGLTextureUnitContextMutableType tc =
      parameters.textureUnitContext();

    final JCGLShadersType g_sh = g.shaders();
    final JCGLTexturesType g_tex = g.textures();

    final JCGLTextureUnitType unit_image_texture =
      tc.unitContextBindTexture2D(g_tex, values.imageTexture().texture());
    final JCGLTextureUnitType unit_depth_texture =
      tc.unitContextBindTexture2D(g_tex, values.imageDepthTexture().texture());

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_image_texture, unit_image_texture);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_image_depth_texture, unit_depth_texture);

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(
        values.observerValues().projection()));

    g_sh.shaderUniformPutPVector3f(
      this.u_fog_color, values.fogColor());
    g_sh.shaderUniformPutFloat(
      this.u_fog_far_z, (float) values.fogFarPositiveZ());
    g_sh.shaderUniformPutFloat(
      this.u_fog_near_z, (float) values.fogNearPositiveZ());
  }
}
