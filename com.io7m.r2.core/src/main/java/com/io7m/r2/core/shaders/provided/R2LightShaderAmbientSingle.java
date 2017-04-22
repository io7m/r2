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

package com.io7m.r2.core.shaders.provided;

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightAmbientScreenSingle;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.shaders.types.R2ShaderLightScreenSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightScreenSingleVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderLightSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.core.shaders.types.R2ShaderParametersLightType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

/**
 * Ambient light shader for single lights.
 */

public final class R2LightShaderAmbientSingle extends
  R2AbstractShader<R2LightAmbientScreenSingle>
  implements R2ShaderLightScreenSingleType<R2LightAmbientScreenSingle>
{
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_transform_projection_inverse;
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_viewport_inverse_width;
  private final JCGLProgramUniformType u_viewport_inverse_height;
  private final JCGLProgramUniformType u_light_color;
  private final JCGLProgramUniformType u_light_intensity;
  private final JCGLProgramUniformType u_light_occlusion;

  private R2LightShaderAmbientSingle(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.core.R2LightShaderAmbientSingle",
      "com.io7m.r2.shaders.core/R2LightAmbientSingle.vert",
      Optional.empty(),
      "com.io7m.r2.shaders.core/R2LightAmbientSingle.frag");

    final JCGLProgramShaderUsableType p = this.shaderProgram();
    R2ShaderParameters.checkUniformParameterCount(p, 9);

    this.u_light_color =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_ambient.color", JCGLType.TYPE_FLOAT_VECTOR_3);
    this.u_light_intensity =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_ambient.intensity", JCGLType.TYPE_FLOAT);
    this.u_light_occlusion =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_ambient.occlusion", JCGLType.TYPE_SAMPLER_2D);

    final JCGLProgramUniformType u_transform_volume_modelview = R2ShaderParameters.getUniformChecked(
      p,
      "R2_light_matrices.transform_volume_modelview",
      JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_projection =
      R2ShaderParameters.getUniformChecked(
        p,
        "R2_light_matrices.transform_projection",
        JCGLType.TYPE_FLOAT_MATRIX_4);
    this.u_transform_projection_inverse =
      R2ShaderParameters.getUniformChecked(
        p,
        "R2_light_matrices.transform_projection_inverse",
        JCGLType.TYPE_FLOAT_MATRIX_4);

    this.u_viewport_inverse_width =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_viewport.inverse_width", JCGLType.TYPE_FLOAT);
    this.u_viewport_inverse_height =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_viewport.inverse_height", JCGLType.TYPE_FLOAT);

    this.u_depth_coefficient =
      R2ShaderParameters.getUniformChecked(
        p, "R2_light_depth_coefficient", JCGLType.TYPE_FLOAT);
  }

  /**
   * Construct a new shader.
   *
   * @param in_shaders    A shader interface
   * @param in_shader_env The shader preprocessing environment
   * @param in_pool       The ID pool
   *
   * @return A new shader
   */

  public static R2ShaderLightSingleType<R2LightAmbientScreenSingle>
  newShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return R2ShaderLightScreenSingleVerifier.newVerifier(
      new R2LightShaderAmbientSingle(in_shaders, in_shader_env, in_pool));
  }

  @Override
  public Class<R2LightAmbientScreenSingle>
  shaderParametersType()
  {
    return R2LightAmbientScreenSingle.class;
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {
    // Nothing
  }

  @Override
  public void onReceiveBoundGeometryBufferTextures(
    final JCGLInterfaceGL33Type g,
    final R2GeometryBufferUsableType gbuffer,
    final JCGLTextureUnitType unit_albedo,
    final JCGLTextureUnitType unit_specular,
    final JCGLTextureUnitType unit_depth,
    final JCGLTextureUnitType unit_normals)
  {
    NullCheck.notNull(g, "G33");
    NullCheck.notNull(gbuffer, "G-Buffer");
    NullCheck.notNull(unit_albedo, "Albedo");
    NullCheck.notNull(unit_depth, "Depth");
    NullCheck.notNull(unit_normals, "Normals");
    NullCheck.notNull(unit_specular, "Specular");
  }

  @Override
  public void onReceiveValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersLightType<R2LightAmbientScreenSingle> light_parameters)
  {
    NullCheck.notNull(g, "G33");
    NullCheck.notNull(light_parameters, "Light parameters");

    final JCGLShadersType g_sh = g.shaders();
    final JCGLTexturesType g_tex = g.textures();

    final R2MatricesObserverValuesType m =
      light_parameters.observerMatrices();
    final AreaL viewport =
      light_parameters.viewport();
    final R2LightAmbientScreenSingle light =
      light_parameters.values();
    final JCGLTextureUnitContextMutableType tc =
      light_parameters.textureUnitContext();

    /*
     * Upload the projections for the light volume.
     */

    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_projection, m.matrixProjection());
    g_sh.shaderUniformPutPMatrix4x4f(
      this.u_transform_projection_inverse, m.matrixProjectionInverse());

    /*
     * Upload the viewport.
     */

    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_width,
      (float) (1.0 / (double) viewport.width()));
    g_sh.shaderUniformPutFloat(
      this.u_viewport_inverse_height,
      (float) (1.0 / (double) viewport.height()));

    /*
     * Upload the scene's depth coefficient.
     */

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(m.projection()));

    /*
     * Upload the occlusion texture and light values.
     */

    final JCGLTextureUnitType unit_ao =
      tc.unitContextBindTexture2D(g_tex, light.occlusionMap().texture());
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_light_occlusion, unit_ao);

    g_sh.shaderUniformPutPVector3f(
      this.u_light_color, light.color());
    g_sh.shaderUniformPutFloat(
      this.u_light_intensity, (float) light.intensity());
  }
}
