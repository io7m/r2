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
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.renderstate.JCGLBlendState;
import com.io7m.jfsm.core.FSMEnumMutable;
import com.io7m.jfsm.core.FSMEnumMutableBuilderType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;

import java.util.Optional;

/**
 * A verifier for billboarded translucent instance shaders; a type that verifies
 * that a renderer has called all of the required methods in the correct order.
 *
 * @param <M> See {@link R2ShaderInstanceSingleType}
 */

public final class R2ShaderTranslucentInstanceBillboardedVerifier<M> implements
  R2ShaderTranslucentInstanceBillboardedType<M>
{
  private final R2ShaderTranslucentInstanceBillboardedType<M> shader;
  private final FSMEnumMutable<State> state;

  private R2ShaderTranslucentInstanceBillboardedVerifier(
    final R2ShaderTranslucentInstanceBillboardedType<M> in_shader)
  {
    this.shader = NullCheck.notNull(in_shader);

    final FSMEnumMutableBuilderType<State> sb =
      FSMEnumMutable.builder(State.STATE_DEACTIVATED);

    sb.addTransition(
      State.STATE_DEACTIVATED, State.STATE_ACTIVATED);
    sb.addTransition(
      State.STATE_ACTIVATED, State.STATE_VIEW_RECEIVED);
    sb.addTransition(
      State.STATE_VIEW_RECEIVED, State.STATE_MATERIAL_RECEIVED);
    sb.addTransition(
      State.STATE_MATERIAL_RECEIVED, State.STATE_MATERIAL_RECEIVED);
    sb.addTransition(
      State.STATE_MATERIAL_RECEIVED, State.STATE_VALIDATED);
    sb.addTransition(
      State.STATE_VALIDATED, State.STATE_MATERIAL_RECEIVED);

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
   * @param <M> See {@link R2ShaderInstanceBillboardedType}
   *
   * @return A new verifier
   */

  public static <M> R2ShaderTranslucentInstanceBillboardedType<M>
  newVerifier(final R2ShaderTranslucentInstanceBillboardedType<M> s)
  {
    return new R2ShaderTranslucentInstanceBillboardedVerifier<>(s);
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
    this.shader.onActivate(g);
    this.state.transition(State.STATE_ACTIVATED);
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
  public void onReceiveViewValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersViewType view_parameters)
  {
    this.state.transition(State.STATE_VIEW_RECEIVED);
    this.shader.onReceiveViewValues(g, view_parameters);
  }

  @Override
  public void onReceiveMaterialValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersMaterialType<M> mat_parameters)
  {
    this.state.transition(State.STATE_MATERIAL_RECEIVED);
    this.shader.onReceiveMaterialValues(g, mat_parameters);
  }

  @Override
  public Optional<JCGLBlendState> suggestedBlendState()
  {
    return this.shader.suggestedBlendState();
  }

  private enum State
  {
    STATE_DEACTIVATED,
    STATE_ACTIVATED,
    STATE_MATERIAL_RECEIVED,
    STATE_VIEW_RECEIVED,
    STATE_VALIDATED
  }
}
