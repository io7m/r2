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
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;

/**
 * A verifier for filter shaders; a type that verifies that a renderer has
 * called all of the required methods in the correct order.
 *
 * @param <M> See {@link R2ShaderFilterType}
 */

public final class R2ShaderFilterVerifier<M> implements R2ShaderFilterType<M>
{
  private static final State[] VALUES_RECEIVED_OR_ACTIVATED = {
    State.STATE_ACTIVATED,
    State.STATE_VALUES_RECEIVED,
  };

  private final R2ShaderFilterType<M> shader;
  private final StringBuilder text;
  private State state;

  private R2ShaderFilterVerifier(
    final R2ShaderFilterType<M> in_shader)
  {
    this.shader = NullCheck.notNull(in_shader);
    this.text = new StringBuilder(128);
    this.state = State.STATE_DEACTIVATED;
  }

  /**
   * Construct a new verifier for the given shader.
   *
   * @param s   The shader
   * @param <M> See {@link R2ShaderLightSingleType}
   *
   * @return A new verifier
   */

  public static <M> R2ShaderFilterType<M> newVerifier(
    final R2ShaderFilterType<M> s)
  {
    return new R2ShaderFilterVerifier<>(s);
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
      this.shader.getShaderProgram().getName(),
      State.STATE_VALUES_RECEIVED,
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
  public void onReceiveFilterValues(
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final JCGLTextureUnitContextMutableType tc,
    final M values)
  {
    R2ShaderVerifiers.checkStates(
      this.text,
      this.shader.getShaderProgram().getName(),
      R2ShaderFilterVerifier.VALUES_RECEIVED_OR_ACTIVATED,
      this.state);

    this.shader.onReceiveFilterValues(g_tex, g_sh, tc, values);
    this.state = State.STATE_VALUES_RECEIVED;
  }

  private enum State
  {
    STATE_DEACTIVATED,
    STATE_ACTIVATED,
    STATE_VALUES_RECEIVED
  }
}
