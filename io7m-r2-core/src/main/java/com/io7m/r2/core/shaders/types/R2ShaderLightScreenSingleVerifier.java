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
import com.io7m.jfsm.core.FSMEnumMutable;
import com.io7m.jfsm.core.FSMEnumMutableBuilderType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2LightScreenSingleType;

/**
 * A verifier for single-instance volume light shaders; a type that verifies
 * that a renderer has called all of the required methods in the correct order.
 *
 * @param <M> See {@link R2ShaderLightSingleType}
 */

public final class R2ShaderLightScreenSingleVerifier<
  M extends R2LightScreenSingleType> implements
  R2ShaderLightScreenSingleType<M>
{
  private final R2ShaderLightScreenSingleType<M> shader;
  private final FSMEnumMutable<State> state;

  private R2ShaderLightScreenSingleVerifier(
    final R2ShaderLightScreenSingleType<M> in_shader)
  {
    this.shader = NullCheck.notNull(in_shader);

    final FSMEnumMutableBuilderType<State> sb =
      FSMEnumMutable.builder(State.STATE_DEACTIVATED);

    sb.addTransition(
      State.STATE_DEACTIVATED, State.STATE_ACTIVATED);
    sb.addTransition(
      State.STATE_ACTIVATED, State.STATE_GEOMETRY_BUFFER_RECEIVED);
    sb.addTransition(
      State.STATE_GEOMETRY_BUFFER_RECEIVED, State.STATE_VALUES_RECEIVED);

    for (final State target : State.values()) {
      if (target != State.STATE_DEACTIVATED) {
        sb.addTransition(target, State.STATE_DEACTIVATED);
      }
    }

    this.state = sb.build();
  }

  /**
   * Construct a new verifier for the given shader.
   *
   * @param s   The shader
   * @param <M> See {@link R2ShaderLightVolumeSingleType}
   *
   * @return A new verifier
   */

  public static <M extends R2LightScreenSingleType>
  R2ShaderLightScreenSingleType<M>
  newVerifier(
    final R2ShaderLightScreenSingleType<M> s)
  {
    return new R2ShaderLightScreenSingleVerifier<>(s);
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
  public long shaderID()
  {
    return this.shader.shaderID();
  }

  @Override
  public Class<M> shaderParametersType()
  {
    return this.shader.shaderParametersType();
  }

  @Override
  public JCGLProgramShaderUsableType shaderProgram()
  {
    return this.shader.shaderProgram();
  }

  @Override
  public void onActivate(final JCGLInterfaceGL33Type g)
  {
    this.state.transition(State.STATE_ACTIVATED);
    this.shader.onActivate(g);
  }

  @Override
  public void onValidate()
    throws R2ExceptionShaderValidationFailed
  {
    this.state.transition(State.STATE_VALIDATED);
    this.shader.onValidate();
  }

  @Override
  public void onDeactivate(final JCGLInterfaceGL33Type g)
  {
    this.state.transition(State.STATE_DEACTIVATED);
    this.shader.onDeactivate(g);
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
    this.state.transition(State.STATE_GEOMETRY_BUFFER_RECEIVED);
    this.shader.onReceiveBoundGeometryBufferTextures(
      g_sh,
      g,
      unit_albedo,
      unit_specular,
      unit_depth,
      unit_normals);
  }

  @Override
  public void onReceiveValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersLightType<M> light_parameters)
  {
    this.state.transition(State.STATE_VALUES_RECEIVED);
    this.shader.onReceiveValues(g, light_parameters);
  }

  private enum State
  {
    STATE_DEACTIVATED,
    STATE_ACTIVATED,
    STATE_GEOMETRY_BUFFER_RECEIVED,
    STATE_VALUES_RECEIVED,
    STATE_VALIDATED
  }
}
