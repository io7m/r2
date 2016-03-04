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
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.VectorM2F;
import com.io7m.r2.core.R2AbstractShader;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2TextureUnitContextMutableType;
import org.valid4j.Assertive;

import java.util.Map;
import java.util.Optional;

/**
 * A bilateral depth-aware blur vertical RGBA shader.
 */

public final class R2ShaderFilterBilateralBlurDepthAwareVertical4f extends
  R2AbstractShader<R2ShaderFilterBilateralBlurDepthAwareParametersType>
  implements
  R2ShaderScreenType<R2ShaderFilterBilateralBlurDepthAwareParametersType>
{
  private final JCGLProgramUniformType u_texture_image;
  private final JCGLProgramUniformType u_texture_depth;
  private final JCGLProgramUniformType u_blur_depth_coefficient;
  private final JCGLProgramUniformType u_blur_radius;
  private final JCGLProgramUniformType u_blur_sharpness;
  private final JCGLProgramUniformType u_blur_falloff;
  private final JCGLProgramUniformType u_blur_output_image_size_inverse;
  private final VectorM2F              inverse_size;
  private       JCGLTextureUnitType    unit_texture_image;
  private       JCGLTextureUnitType    unit_texture_depth;

  private R2ShaderFilterBilateralBlurDepthAwareVertical4f(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_sources,
      in_pool,
      "R2FilterBilateralBlurDepthAwareVertical4f",
      "R2FilterBilateralBlurDepthAwareVertical4f.vert",
      Optional.empty(),
      "R2FilterBilateralBlurDepthAwareVertical4f.frag");

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    final Map<String, JCGLProgramUniformType> us = p.getUniforms();
    Assertive.ensure(
      us.size() == 7,
      "Expected number of parameters is 7 (got %d)",
      Integer.valueOf(us.size()));

    this.u_texture_image =
      R2ShaderParameters.getUniformChecked(p, "R2_texture_image");
    this.u_texture_depth =
      R2ShaderParameters.getUniformChecked(p, "R2_texture_depth");

    this.u_blur_depth_coefficient =
      R2ShaderParameters.getUniformChecked(
        p, "R2_blur.depth_coefficient");
    this.u_blur_radius =
      R2ShaderParameters.getUniformChecked(
        p, "R2_blur.radius");
    this.u_blur_sharpness =
      R2ShaderParameters.getUniformChecked(
        p, "R2_blur.sharpness");
    this.u_blur_falloff =
      R2ShaderParameters.getUniformChecked(
        p, "R2_blur.falloff");
    this.u_blur_output_image_size_inverse =
      R2ShaderParameters.getUniformChecked(
        p, "R2_blur.output_image_size_inverse");

    this.inverse_size = new VectorM2F();
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

  public static R2ShaderFilterBilateralBlurDepthAwareVertical4f
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return new R2ShaderFilterBilateralBlurDepthAwareVertical4f(
      in_shaders, in_sources, in_pool);
  }

  @Override
  public Class<R2ShaderFilterBilateralBlurDepthAwareParametersType>
  getShaderParametersType()
  {
    return R2ShaderFilterBilateralBlurDepthAwareParametersType.class;
  }

  /**
   * Bind any textures needed for execution.
   *
   * @param g_tex  A texture interface
   * @param uc     A texture interface
   * @param values The parameters
   */

  public void setTextures(
    final JCGLTexturesType g_tex,
    final R2TextureUnitContextMutableType uc,
    final R2ShaderFilterBilateralBlurDepthAwareParametersType values)
  {
    NullCheck.notNull(uc);
    NullCheck.notNull(values);

    this.unit_texture_image =
      uc.unitContextBindTexture2D(g_tex, values.getImageTexture());
    this.unit_texture_depth =
      uc.unitContextBindTexture2D(g_tex, values.getDepthTexture());
  }

  /**
   * Set any shader parameters needed for execution.
   *
   * @param g_sh   A shader interface
   * @param values The parameters
   */

  public void setValues(
    final JCGLShadersType g_sh,
    final R2ShaderFilterBilateralBlurDepthAwareParametersType values)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(values);

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_image, this.unit_texture_image);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_depth, this.unit_texture_depth);

    g_sh.shaderUniformPutFloat(
      this.u_blur_falloff, values.getBlurFalloff());
    g_sh.shaderUniformPutFloat(
      this.u_blur_radius, values.getBlurRadius());
    g_sh.shaderUniformPutFloat(
      this.u_blur_sharpness, values.getBlurSharpness());

    this.inverse_size.set2F(
      values.getBlurOutputInverseWidth(), values.getBlurOutputInverseHeight());
    g_sh.shaderUniformPutVector2f(
      this.u_blur_output_image_size_inverse, this.inverse_size);
  }

  /**
   * Set the view-dependent values for the shader.
   *
   * @param g_sh A shader interface
   * @param m    The view matrices
   */

  public void setViewDependentValues(
    final JCGLShadersType g_sh,
    final R2MatricesObserverValuesType m)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);

    /**
     * Upload the scene's depth coefficient.
     */

    g_sh.shaderUniformPutFloat(
      this.u_blur_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(m.getProjection()));
  }
}
