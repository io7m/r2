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
import com.io7m.jnull.NullCheck;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jtensors.core.unparameterized.vectors.Vector2D;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.shaders.abstracts.R2AbstractFilterShader;
import com.io7m.r2.core.shaders.abstracts.R2ShaderStateChecking;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_VECTOR_2;
import static com.io7m.jcanephora.core.JCGLType.TYPE_SAMPLER_2D;
import static com.io7m.r2.core.shaders.types.R2ShaderParameters.checkUniformParameterCount;
import static com.io7m.r2.core.shaders.types.R2ShaderParameters.uniform;

/**
 * An FXAA shader.
 */

public final class R2ShaderFilterFXAAA
  extends R2AbstractFilterShader<R2ShaderFilterFXAAParameters>
{
  private final JCGLProgramUniformType u_image;
  private final JCGLProgramUniformType u_screen_inverse;
  private final JCGLProgramUniformType u_subpixel_aliasing_removal;
  private final JCGLProgramUniformType u_edge_threshold;
  private final JCGLProgramUniformType u_edge_threshold_minimum;

  private R2ShaderFilterFXAAA(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final String in_name,
    final String in_fragment,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core." + in_name,
      "com.io7m.r2.shaders.core/R2Filter.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/" + in_fragment,
      in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_image =
      uniform(p, "R2_fxaa.image", TYPE_SAMPLER_2D);
    this.u_screen_inverse =
      uniform(p, "R2_fxaa.screen_inverse", TYPE_FLOAT_VECTOR_2);
    this.u_subpixel_aliasing_removal =
      uniform(p, "R2_fxaa.subpixel_aliasing_removal", TYPE_FLOAT);
    this.u_edge_threshold =
      uniform(p, "R2_fxaa.edge_threshold", TYPE_FLOAT);
    this.u_edge_threshold_minimum =
      uniform(p, "R2_fxaa.edge_threshold_minimum", TYPE_FLOAT);

    checkUniformParameterCount(p, 5);
  }

  /**
   * Construct a new shader.
   *
   * @param in_shaders    A shader interface
   * @param in_shader_env Shader sources
   * @param in_pool       The ID pool
   * @param q             The quality preset
   *
   * @return A new shader
   */

  public static R2ShaderFilterType<R2ShaderFilterFXAAParameters> create(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2FilterFXAAQuality q)
  {
    NullCheck.notNull(q, "Quality");

    final String n = qualityToShaderName(q);
    final String f = n + ".frag";

    return new R2ShaderFilterFXAAA(
      in_shaders,
      in_shader_env,
      in_pool,
      n,
      f,
      R2ShaderStateChecking.STATE_CHECK);
  }

  private static String qualityToShaderName(
    final R2FilterFXAAQuality q)
  {
    switch (q) {
      case R2_FXAA_QUALITY_10:
        return "R2FXAA_10";
      case R2_FXAA_QUALITY_15:
        return "R2FXAA_15";
      case R2_FXAA_QUALITY_20:
        return "R2FXAA_20";
      case R2_FXAA_QUALITY_25:
        return "R2FXAA_25";
      case R2_FXAA_QUALITY_29:
        return "R2FXAA_29";
      case R2_FXAA_QUALITY_39:
        return "R2FXAA_39";
    }

    throw new UnreachableCodeException();
  }

  @Override
  protected void onActualReceiveFilterValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersFilterType<R2ShaderFilterFXAAParameters> parameters)
  {
    final R2ShaderFilterFXAAParameters values =
      parameters.values();
    final JCGLTextureUnitContextMutableType tc =
      parameters.textureUnitContext();

    final JCGLShadersType g_sh = g.shaders();
    final JCGLTexturesType g_tex = g.textures();

    /*
     * Upload the texture to be filtered.
     */

    final JCGLTextureUnitType u =
      tc.unitContextBindTexture2D(g_tex, values.texture().texture());
    g_sh.shaderUniformPutTexture2DUnit(this.u_image, u);

    /*
     * Upload the viewport.
     */

    final R2Texture2DUsableType texture = values.texture();
    final AreaSizeL area = texture.texture().size();

    final Vector2D screen_inverse =
      Vector2D.of(
        1.0 / (double) area.width(),
        1.0 / (double) area.height());

    g_sh.shaderUniformPutVector2f(this.u_screen_inverse, screen_inverse);

    /*
     * Upload the various algorithm parameters.
     */

    g_sh.shaderUniformPutFloat(
      this.u_edge_threshold,
      (float) values.edgeThreshold());
    g_sh.shaderUniformPutFloat(
      this.u_edge_threshold_minimum,
      (float) values.edgeThresholdMinimum());
    g_sh.shaderUniformPutFloat(
      this.u_subpixel_aliasing_removal,
      (float) values.subPixelAliasingRemoval());
  }

  @Override
  public Class<R2ShaderFilterFXAAParameters> shaderParametersType()
  {
    return R2ShaderFilterFXAAParameters.class;
  }
}
