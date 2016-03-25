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

package com.io7m.r2.core.shaders.types;

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2LightWithShadowSingleType;
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2MatricesProjectiveLightValuesType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureUnitContextMutableType;

/**
 * A verifier for light shaders; a type that verifies that a renderer has called
 * all of the required methods in the correct order.
 *
 * @param <M> See {@link R2ShaderLightWithShadowSingleType}
 */

public final class R2ShaderLightWithShadowVerifier<M extends
  R2LightWithShadowSingleType> implements
  R2ShaderLightWithShadowSingleType<M>
{
  private final R2ShaderLightWithShadowSingleType<M> shader;
  private final StringBuilder                        text;
  private final R2ShaderProjectiveRequired           projective_required;
  private       State                                state;

  private R2ShaderLightWithShadowVerifier(
    final R2ShaderLightWithShadowSingleType<M> in_shader,
    final R2ShaderProjectiveRequired in_projective_required)
  {
    this.shader = NullCheck.notNull(in_shader);
    this.text = new StringBuilder(128);
    this.projective_required = NullCheck.notNull(in_projective_required);
    this.state = State.STATE_DEACTIVATED;
  }

  /**
   * Construct a new verifier for the given shader.
   *
   * @param s   The shader
   * @param r   A specification of whether or not projective light values are
   *            required
   * @param <M> See {@link R2ShaderLightSingleType}
   *
   * @return A new verifier
   */

  public static <M extends R2LightWithShadowSingleType>
  R2ShaderLightWithShadowSingleType<M>
  newVerifier(
    final R2ShaderLightWithShadowSingleType<M> s,
    final R2ShaderProjectiveRequired r)
  {
    return new R2ShaderLightWithShadowVerifier<>(s, r);
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    this.shader.delete(g);
  }

  @Override
  public boolean isDeleted()
  {
    return this.shader.isDeleted();
  }

  @Override
  public long getShaderID()
  {
    return this.shader.getShaderID();
  }

  @Override
  public Class<M> getShaderParametersType()
  {
    return this.shader.getShaderParametersType();
  }

  @Override
  public JCGLProgramShaderUsableType getShaderProgram()
  {
    return this.shader.getShaderProgram();
  }

  @Override
  public void onActivate(final JCGLShadersType g_sh)
  {
    this.shader.onActivate(g_sh);
    this.state = State.STATE_ACTIVATED;
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {
    R2ShaderVerifiers.checkState(
      this.text,
      this.getShaderProgram().getName(),
      State.STATE_INSTANCE_RECEIVED,
      this.state);

    this.shader.onValidate();
  }

  @Override
  public void onDeactivate(final JCGLShadersType g_sh)
  {
    this.shader.onDeactivate(g_sh);
    this.state = State.STATE_DEACTIVATED;
  }

  @Override
  public void onReceiveBoundGeometryBufferTextures(
    final JCGLShadersType g_sh,
    final R2GeometryBufferUsableType g,
    final JCGLTextureUnitType unit_albedo,
    final JCGLTextureUnitType unit_specular,
    final JCGLTextureUnitType unit_depth,
    final JCGLTextureUnitType unit_normals)
  {
    R2ShaderVerifiers.checkState(
      this.text,
      this.getShaderProgram().getName(),
      State.STATE_ACTIVATED,
      this.state);

    this.shader.onReceiveBoundGeometryBufferTextures(
      g_sh,
      g,
      unit_albedo,
      unit_specular,
      unit_depth,
      unit_normals);
    this.state = State.STATE_GEOMETRY_BUFFER_RECEIVED;
  }

  @Override
  public void onReceiveProjectiveLight(
    final JCGLShadersType g_sh,
    final R2MatricesProjectiveLightValuesType m)
  {
    R2ShaderVerifiers.checkState(
      this.text,
      this.getShaderProgram().getName(),
      State.STATE_SHADOW_RECEIVED,
      this.state);

    this.shader.onReceiveProjectiveLight(g_sh, m);
    this.state = State.STATE_PROJECTIVE_RECEIVED;
  }

  @Override
  public void onReceiveInstanceTransformValues(
    final JCGLShadersType g_sh,
    final R2MatricesInstanceSingleValuesType m)
  {
    switch (this.projective_required) {
      case R2_SHADER_PROJECTIVE_REQUIRED:
        R2ShaderVerifiers.checkState(
          this.text,
          this.getShaderProgram().getName(),
          State.STATE_PROJECTIVE_RECEIVED,
          this.state);
        break;
      case R2_SHADER_PROJECTIVE_NOT_REQUIRED:
        R2ShaderVerifiers.checkState(
          this.text,
          this.getShaderProgram().getName(),
          State.STATE_SHADOW_RECEIVED,
          this.state);
        break;
    }

    this.shader.onReceiveInstanceTransformValues(g_sh, m);
    this.state = State.STATE_INSTANCE_RECEIVED;
  }

  @Override
  public void onReceiveValues(
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final R2TextureUnitContextMutableType tc,
    final M values,
    final R2MatricesObserverValuesType m)
  {
    R2ShaderVerifiers.checkState(
      this.text,
      this.getShaderProgram().getName(),
      State.STATE_GEOMETRY_BUFFER_RECEIVED,
      this.state);

    this.shader.onReceiveValues(g_tex, g_sh, tc, values, m);
    this.state = State.STATE_VALUES_RECEIVED;
  }

  @Override
  public void onReceiveShadowMap(
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final R2TextureUnitContextMutableType tc,
    final M values,
    final R2Texture2DUsableType map)
  {
    R2ShaderVerifiers.checkState(
      this.text,
      this.getShaderProgram().getName(),
      State.STATE_VALUES_RECEIVED,
      this.state);

    this.shader.onReceiveShadowMap(g_tex, g_sh, tc, values, map);
    this.state = State.STATE_SHADOW_RECEIVED;
  }

  private enum State
  {
    STATE_DEACTIVATED,
    STATE_ACTIVATED,
    STATE_GEOMETRY_BUFFER_RECEIVED,
    STATE_VALUES_RECEIVED,
    STATE_SHADOW_RECEIVED,
    STATE_PROJECTIVE_RECEIVED,
    STATE_INSTANCE_RECEIVED
  }
}
