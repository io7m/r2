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

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jfsm.core.FSMEnumMutable;
import com.io7m.jfsm.core.FSMEnumMutableBuilderType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2LightProjectiveReadableType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2MatricesProjectiveLightValuesType;
import com.io7m.r2.core.R2MatricesVolumeLightValuesType;

/**
 * A verifier for projective light shaders; a type that verifies that a renderer
 * has called all of the required methods in the correct order.
 *
 * @param <M> See {@link R2ShaderLightProjectiveType}
 */

public final class R2ShaderLightProjectiveVerifier<
  M extends R2LightProjectiveReadableType> implements
  R2ShaderLightProjectiveType<M>
{
  private final R2ShaderLightProjectiveType<M> shader;
  private final FSMEnumMutable<State> state;

  private R2ShaderLightProjectiveVerifier(
    final R2ShaderLightProjectiveType<M> in_shader)
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
    sb.addTransition(
      State.STATE_VALUES_RECEIVED, State.STATE_VOLUME_RECEIVED);
    sb.addTransition(
      State.STATE_VOLUME_RECEIVED, State.STATE_PROJECTIVE_RECEIVED);
    sb.addTransition(
      State.STATE_PROJECTIVE_RECEIVED, State.STATE_VALIDATED);

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

  public static <M extends R2LightProjectiveReadableType>
  R2ShaderLightProjectiveType<M>
  newVerifier(
    final R2ShaderLightProjectiveType<M> s)
  {
    return new R2ShaderLightProjectiveVerifier<>(s);
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
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final JCGLTextureUnitContextMutableType tc,
    final AreaInclusiveUnsignedLType area,
    final M values,
    final R2MatricesObserverValuesType m)
  {
    this.state.transition(State.STATE_VALUES_RECEIVED);
    this.shader.onReceiveValues(g_tex, g_sh, tc, area, values, m);
  }

  @Override
  public void onReceiveVolumeLightTransform(
    final JCGLInterfaceGL33Type g,
    final R2MatricesVolumeLightValuesType m)
  {
    this.state.transition(State.STATE_VOLUME_RECEIVED);
    this.shader.onReceiveVolumeLightTransform(g, m);
  }

  @Override
  public void onReceiveProjectiveLight(
    final JCGLInterfaceGL33Type g,
    final R2MatricesProjectiveLightValuesType m)
  {
    this.state.transition(State.STATE_PROJECTIVE_RECEIVED);
    this.shader.onReceiveProjectiveLight(g, m);
  }

  private enum State
  {
    STATE_DEACTIVATED,
    STATE_ACTIVATED,
    STATE_GEOMETRY_BUFFER_RECEIVED,
    STATE_VALUES_RECEIVED,
    STATE_VOLUME_RECEIVED,
    STATE_PROJECTIVE_RECEIVED,
    STATE_VALIDATED
  }
}
