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

package com.io7m.r2.shaders.refraction;

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.renderstate.JCGLBlendState;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.vectors.PVectors3D;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.matrices.R2MatricesObserverValuesType;
import com.io7m.r2.projections.R2Projections;
import com.io7m.r2.shaders.api.R2ShaderParametersMaterialType;
import com.io7m.r2.shaders.api.R2ShaderParametersViewType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.api.R2ShaderStateChecking;
import com.io7m.r2.shaders.translucent.api.R2AbstractTranslucentInstanceShaderBillboarded;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_MATRIX_4;
import static com.io7m.jcanephora.core.JCGLType.TYPE_FLOAT_VECTOR_3;
import static com.io7m.jcanephora.core.JCGLType.TYPE_SAMPLER_2D;
import static com.io7m.r2.shaders.api.R2ShaderParameters.checkUniformParameterCount;
import static com.io7m.r2.shaders.api.R2ShaderParameters.uniform;

/**
 * Shader for refracting billboarded instances.
 */

public final class R2RefractionMaskedDeltaShaderBillboarded
  extends R2AbstractTranslucentInstanceShaderBillboarded<R2RefractionMaskedDeltaParameters>
{
  private final JCGLProgramUniformType u_transform_projection;
  private final JCGLProgramUniformType u_transform_view;
  private final JCGLProgramUniformType u_depth_coefficient;
  private final JCGLProgramUniformType u_refraction_scale;
  private final JCGLProgramUniformType u_refraction_color;
  private final JCGLProgramUniformType u_refraction_scene;
  private final JCGLProgramUniformType u_refraction_mask;
  private final JCGLProgramUniformType u_refraction_delta;

  private R2RefractionMaskedDeltaShaderBillboarded(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2ShaderStateChecking in_check)
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      "com.io7m.r2.shaders.refraction.R2RefractionMaskedDelta",
      "com.io7m.r2.shaders.core/R2Billboarded.vert",
      Optional.of("com.io7m.r2.shaders.core/R2Billboarded.geom"),
      "com.io7m.r2.shaders.refraction/R2RefractionMaskedDelta.frag",
      in_check);

    final JCGLProgramShaderUsableType p = this.shaderProgram();

    this.u_transform_projection =
      uniform(p, "R2_view.transform_projection", TYPE_FLOAT_MATRIX_4);
    this.u_transform_view =
      uniform(p, "R2_view.transform_view", TYPE_FLOAT_MATRIX_4);

    this.u_depth_coefficient =
      uniform(p, "R2_view.depth_coefficient", TYPE_FLOAT);

    this.u_refraction_scale =
      uniform(p, "R2_refraction.scale", TYPE_FLOAT);
    this.u_refraction_color =
      uniform(p, "R2_refraction.color", TYPE_FLOAT_VECTOR_3);
    this.u_refraction_scene =
      uniform(p, "R2_refraction.scene", TYPE_SAMPLER_2D);
    this.u_refraction_mask =
      uniform(p, "R2_refraction.mask", TYPE_SAMPLER_2D);
    this.u_refraction_delta =
      uniform(p, "R2_refraction_delta", TYPE_SAMPLER_2D);

    checkUniformParameterCount(p, 8);
  }

  /**
   * Construct a new shader.
   *
   * @param in_shaders    A shader interface
   * @param in_shader_env A shader preprocessing environment
   * @param in_pool       The ID pool
   *
   * @return A new shader
   */

  public static R2RefractionMaskedDeltaShaderBillboarded create(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2RefractionMaskedDeltaShaderBillboarded(
      in_shaders, in_shader_env, in_pool, R2ShaderStateChecking.STATE_CHECK);
  }

  @Override
  public Class<R2RefractionMaskedDeltaParameters> shaderParametersType()
  {
    return R2RefractionMaskedDeltaParameters.class;
  }

  @Override
  protected void onActualReceiveViewValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersViewType view_parameters)
  {
    final JCGLShadersType g_sh = g.shaders();
    final R2MatricesObserverValuesType matrices =
      view_parameters.observerMatrices();

    g_sh.shaderUniformPutFloat(
      this.u_depth_coefficient,
      (float) R2Projections.getDepthCoefficient(matrices.projection()));
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_view,
      PMatrices4x4D.toUnparameterized(matrices.matrixView()));
    g_sh.shaderUniformPutMatrix4x4f(
      this.u_transform_projection,
      PMatrices4x4D.toUnparameterized(matrices.matrixProjection()));
  }

  @Override
  protected void onActualReceiveMaterialValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersMaterialType<R2RefractionMaskedDeltaParameters> mat_parameters)
  {
    final JCGLTexturesType g_tex = g.textures();
    final JCGLShadersType g_sh = g.shaders();

    final JCGLTextureUnitContextMutableType tc =
      mat_parameters.textureUnitContext();
    final R2RefractionMaskedDeltaParameters values =
      mat_parameters.values();

    final JCGLTextureUnitType unit_mask =
      tc.unitContextBindTexture2D(g_tex, values.maskTexture().texture());
    final JCGLTextureUnitType unit_scene =
      tc.unitContextBindTexture2D(g_tex, values.sceneTexture().texture());
    final JCGLTextureUnitType unit_delta =
      tc.unitContextBindTexture2D(g_tex, values.deltaTexture().texture());

    g_sh.shaderUniformPutFloat(
      this.u_refraction_scale, (float) values.scale());
    g_sh.shaderUniformPutVector3f(
      this.u_refraction_color, PVectors3D.toUnparameterized(values.color()));
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_refraction_mask, unit_mask);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_refraction_scene, unit_scene);
    g_sh.shaderUniformPutTexture2DUnit(
      this.u_refraction_delta, unit_delta);
  }

  @Override
  public Optional<JCGLBlendState> suggestedBlendState()
  {
    return Optional.empty();
  }
}
