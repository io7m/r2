/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.core.shaders.abstracts;

import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jfsm.core.FSMEnumMutable;
import com.io7m.jfsm.core.FSMEnumMutableBuilderType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2ExceptionShaderPreprocessingFailed;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

/**
 * An abstract shader implementation that checks state transitions for
 * correctness.
 *
 * @param <M> The type of shader parameters
 */

public abstract class R2AbstractFilterShader<M>
  extends R2AbstractShader<R2AbstractFilterShader.State, M>
  implements R2ShaderFilterType<M>
{
  private final FSMEnumMutable<State> state;
  private final R2ShaderStateChecking check;

  protected R2AbstractFilterShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final String in_name,
    final String in_vertex,
    final Optional<String> in_geometry,
    final String in_fragment,
    final R2ShaderStateChecking in_check)
    throws R2ExceptionShaderPreprocessingFailed
  {
    super(
      in_shaders,
      in_shader_env,
      in_pool,
      in_name,
      in_vertex,
      in_geometry,
      in_fragment);

    this.check = NullCheck.notNull(in_check, "Check");

    final FSMEnumMutableBuilderType<State> sb =
      FSMEnumMutable.builder(State.STATE_DEACTIVATED);

    sb.addTransition(
      State.STATE_DEACTIVATED, State.STATE_ACTIVATED);
    sb.addTransition(
      State.STATE_ACTIVATED, State.STATE_VALUES_RECEIVED);
    sb.addTransition(
      State.STATE_VALUES_RECEIVED, State.STATE_VALIDATED);
    sb.addTransition(
      State.STATE_VALIDATED, State.STATE_VALUES_RECEIVED);

    for (final State target : State.values()) {
      if (target != State.STATE_DEACTIVATED) {
        sb.addTransition(target, State.STATE_DEACTIVATED);
      }
    }

    this.state = sb.build();
  }

  @Override
  public final void onReceiveFilterValues(
    final JCGLInterfaceGL33Type g,
    final R2ShaderParametersFilterType<M> parameters)
  {
    NullCheck.notNull(g, "G33");
    NullCheck.notNull(parameters, "Parameters");

    this.state.transition(State.STATE_VALUES_RECEIVED);
    this.onActualReceiveFilterValues(g, parameters);
  }

  protected abstract void onActualReceiveFilterValues(
    JCGLInterfaceGL33Type g,
    R2ShaderParametersFilterType<M> parameters);

  @Override
  protected final FSMEnumMutable<State> onCheckGetFSM()
  {
    return this.state;
  }

  @Override
  protected final void onCheckActivated()
  {
    this.state.transition(State.STATE_ACTIVATED);
  }

  @Override
  protected final void onCheckDeactivated()
  {
    this.state.transition(State.STATE_DEACTIVATED);
  }

  @Override
  protected final void onCheckValidated()
  {
    this.state.transition(State.STATE_VALIDATED);
  }

  protected enum State
  {
    STATE_DEACTIVATED,
    STATE_ACTIVATED,
    STATE_VALUES_RECEIVED,
    STATE_VALIDATED
  }

}