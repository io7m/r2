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
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.core.R2MatricesObserverValuesType;

/**
 * A delegating verifier for single-instance depth shaders; a type that verifies
 * that a renderer has called all of the required methods in the correct order.
 *
 * @param <M> See {@link R2ShaderInstanceSingleType}
 */

public final class R2ShaderDepthSingleVerifier<M> implements
  R2ShaderDepthSingleType<M>
{
  private static final State[] VIEW_MATERIAL_OR_INSTANCE_RECEIVED = {
    State.STATE_MATERIAL_RECEIVED,
    State.STATE_VIEW_RECEIVED,
    State.STATE_INSTANCE_RECEIVED,
  };

  private static final State[] MATERIAL_OR_INSTANCE_RECEIVED = {
    State.STATE_MATERIAL_RECEIVED,
    State.STATE_INSTANCE_RECEIVED,
  };

  private final R2ShaderDepthSingleType<M> shader;
  private final StringBuilder text;
  private State state;

  private R2ShaderDepthSingleVerifier(
    final R2ShaderDepthSingleType<M> in_shader)
  {
    this.shader = NullCheck.notNull(in_shader);
    this.text = new StringBuilder(128);
    this.state = State.STATE_DEACTIVATED;
  }

  /**
   * Construct a new verifier for the given shader.
   *
   * @param s   The shader
   * @param <M> See {@link R2ShaderDepthSingleType}
   *
   * @return A new verifier
   */

  public static <M> R2ShaderDepthSingleType<M>
  newVerifier(final R2ShaderDepthSingleType<M> s)
  {
    return new R2ShaderDepthSingleVerifier<>(s);
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
    this.state = State.STATE_DEACTIVATED;
    this.shader.onDeactivate(g_sh);
  }

  @Override
  public void onReceiveViewValues(
    final JCGLShadersType g_sh,
    final R2MatricesObserverValuesType m)
  {
    R2ShaderVerifiers.checkState(
      this.text,
      this.getShaderProgram().getName(),
      State.STATE_ACTIVATED,
      this.state);

    this.state = State.STATE_VIEW_RECEIVED;
    this.shader.onReceiveViewValues(g_sh, m);
  }

  @Override
  public void onReceiveMaterialValues(
    final JCGLTexturesType g_tex,
    final JCGLShadersType g_sh,
    final JCGLTextureUnitContextMutableType tc,
    final M values)
  {
    R2ShaderVerifiers.checkStates(
      this.text,
      this.getShaderProgram().getName(),
      R2ShaderDepthSingleVerifier.VIEW_MATERIAL_OR_INSTANCE_RECEIVED,
      this.state);

    this.state = State.STATE_MATERIAL_RECEIVED;
    this.shader.onReceiveMaterialValues(g_tex, g_sh, tc, values);
  }

  @Override
  public void onReceiveInstanceTransformValues(
    final JCGLShadersType g_sh,
    final R2MatricesInstanceSingleValuesType m)
  {
    R2ShaderVerifiers.checkStates(
      this.text,
      this.getShaderProgram().getName(),
      R2ShaderDepthSingleVerifier.MATERIAL_OR_INSTANCE_RECEIVED,
      this.state);

    this.state = State.STATE_INSTANCE_RECEIVED;
    this.shader.onReceiveInstanceTransformValues(g_sh, m);
  }

  private enum State
  {
    STATE_DEACTIVATED,
    STATE_ACTIVATED,
    STATE_MATERIAL_RECEIVED,
    STATE_INSTANCE_RECEIVED,
    STATE_VIEW_RECEIVED
  }
}
