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
import com.io7m.r2.core.R2AbstractShader;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2ShaderSingleType;
import com.io7m.r2.core.R2ShaderParameters;
import com.io7m.r2.core.R2ShaderSourcesType;
import com.io7m.r2.core.R2TextureUnitContextMutableType;
import org.valid4j.Assertive;

import java.util.Map;
import java.util.Optional;

/**
 * Basic deferred surface shader for single instances.
 */

public final class R2SurfaceShaderBasicSingle extends
  R2AbstractShader<R2SurfaceShaderBasicParameters>
  implements R2ShaderSingleType<R2SurfaceShaderBasicParameters>
{
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_transform_normal;
  private final JCGLProgramUniformType u_transform_modelview;
  private final JCGLProgramUniformType u_transform_view;
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_transform_uv;
  private final JCGLProgramUniformType u_emission_amount;
  private final JCGLProgramUniformType u_albedo_color;
  private final JCGLProgramUniformType u_albedo_mix;
  private final JCGLProgramUniformType u_specular_color;
  private final JCGLProgramUniformType u_specular_exponent;
  private final JCGLProgramUniformType u_texture_albedo;
  private final JCGLProgramUniformType u_texture_normal;
  private final JCGLProgramUniformType u_texture_specular;
  private final JCGLProgramUniformType u_texture_emission;
  private JCGLTextureUnitType unit_albedo;
  private JCGLTextureUnitType unit_emission;
  private JCGLTextureUnitType unit_normal;
  private JCGLTextureUnitType unit_specular;

  private R2SurfaceShaderBasicSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_sources,
      in_pool,
      "R2SurfaceBasicSingle",
      "R2SurfaceBasicSingle.vert",
      Optional.empty(),
      "R2SurfaceBasicSingle.frag");

    final JCGLProgramShaderUsableType p = this.getShaderProgram();
    final Map<String, JCGLProgramUniformType> us = p.getUniforms();
    Assertive.ensure(us.size() == 15, "Expected number of parameters is 15");

    this.u_transform_projection = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_projection");
    this.u_transform_view = R2ShaderParameters.getUniformChecked(
      p, "R2_view.transform_view");
    this.u_depth_coefficient = R2ShaderParameters.getUniformChecked(
      p, "R2_view.depth_coefficient");

    this.u_transform_normal = R2ShaderParameters.getUniformChecked(
      p, "R2_surface_matrices_instance.transform_normal");
    this.u_transform_modelview = R2ShaderParameters.getUniformChecked(
      p, "R2_surface_matrices_instance.transform_modelview");
    this.u_transform_uv = R2ShaderParameters.getUniformChecked(
      p, "R2_surface_matrices_instance.transform_uv");

    this.u_emission_amount = R2ShaderParameters.getUniformChecked(
      p, "R2_basic_surface_parameters.emission_amount");
    this.u_albedo_color = R2ShaderParameters.getUniformChecked(
      p, "R2_basic_surface_parameters.albedo_color");
    this.u_albedo_mix = R2ShaderParameters.getUniformChecked(
      p, "R2_basic_surface_parameters.albedo_mix");
    this.u_specular_color = R2ShaderParameters.getUniformChecked(
      p, "R2_basic_surface_parameters.specular_color");
    this.u_specular_exponent = R2ShaderParameters.getUniformChecked(
      p, "R2_basic_surface_parameters.specular_exponent");

    this.u_texture_normal = R2ShaderParameters.getUniformChecked(
      p, "R2_surface_textures.normal");

    this.u_texture_albedo = R2ShaderParameters.getUniformChecked(
      p, "R2_basic_surface_textures.albedo");
    this.u_texture_specular = R2ShaderParameters.getUniformChecked(
      p, "R2_basic_surface_textures.specular");
    this.u_texture_emission = R2ShaderParameters.getUniformChecked(
      p, "R2_basic_surface_textures.emission");
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

  public static R2ShaderSingleType<R2SurfaceShaderBasicParameters> newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return new R2SurfaceShaderBasicSingle(in_shaders, in_sources, in_pool);
  }

  @Override
  public void setMaterialTextures(
    final JCGLTexturesType g_tex,
    final R2TextureUnitContextMutableType tc,
    final R2SurfaceShaderBasicParameters values)
  {
    NullCheck.notNull(tc);
    NullCheck.notNull(values);

    this.unit_albedo =
      tc.unitContextBindTexture2D(g_tex, values.getAlbedoTexture());
    this.unit_emission =
      tc.unitContextBindTexture2D(g_tex, values.getEmissionTexture());
    this.unit_normal =
      tc.unitContextBindTexture2D(g_tex, values.getNormalTexture());
    this.unit_specular =
      tc.unitContextBindTexture2D(g_tex, values.getSpecularTexture());
  }

  @Override
  public void setMaterialValues(
    final JCGLShadersType g_sh,
    final R2SurfaceShaderBasicParameters values)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(values);

    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_albedo, this.unit_albedo);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_emission, this.unit_emission);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_normal, this.unit_normal);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_texture_specular, this.unit_specular);

    g_sh.shaderUniformPutVector4f(
      this.u_albedo_color, values.getAlbedoColor());
    g_sh.shaderUniformPutFloat(
      this.u_albedo_mix, values.getAlbedoMix());

    g_sh.shaderUniformPutFloat(
      this.u_emission_amount, values.getEmission());

    g_sh.shaderUniformPutVector3f(
      this.u_specular_color, values.getSpecularColor());
    g_sh.shaderUniformPutFloat(
      this.u_specular_exponent, values.getSpecularExponent());
  }

  @Override
  public void setMatricesView(
    final JCGLShadersType g_sh,
    final R2MatricesObserverValuesType m)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(m.getProjection()));
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_view, m.getMatrixView());
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection, m.getMatrixProjection());
  }

  @Override
  public void setMatricesInstance(
    final JCGLShadersType g_sh,
    final R2MatricesInstanceSingleValuesType m)
  {
    NullCheck.notNull(g_sh);
    NullCheck.notNull(m);

    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_modelview, m.getMatrixModelView());
    g_sh.shaderUniformPutMatrix3x3f(
      this.u_transform_normal, m.getMatrixNormal());
  }

  @Override
  public Class<R2SurfaceShaderBasicParameters> getShaderParametersType()
  {
    return R2SurfaceShaderBasicParameters.class;
  }
}
