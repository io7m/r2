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

package com.io7m.r2.rendering.translucent.api;

import com.io7m.jcanephora.renderstate.JCGLBlendState;
import com.io7m.jcanephora.renderstate.JCGLCullingState;
import com.io7m.jfunctional.PartialBiFunctionType;

import java.util.Optional;

/**
 * The type of translucents.
 *
 * @param <M> The type of shader parameters
 */

public interface R2TranslucentType<M>
{
  /**
   * Match on the type of translucent.
   *
   * @param context        A context value
   * @param on_single      Evaluated for single instances
   * @param on_batched     Evaluated for batched instances
   * @param on_billboarded Evaluated for billboarded instances
   * @param <A>            The type of context values
   * @param <B>            The type of returned values
   * @param <E>            The type of raised exceptions
   *
   * @return A value of type {@code B}
   *
   * @throws E If any of the given functions raise {@code E}
   */

  <A, B, E extends Throwable>
  B matchTranslucent(
    A context,
    PartialBiFunctionType<A, R2TranslucentSingleType<M>, B, E> on_single,
    PartialBiFunctionType<A, R2TranslucentBatchedType<M>, B, E> on_batched,
    PartialBiFunctionType<A, R2TranslucentBillboardedType<M>, B, E> on_billboarded)
    throws E;

  /**
   * @return The type of shader parameters
   */

  default Class<M> shaderParametersType()
  {
    @SuppressWarnings("unchecked") final Class<M> c = (Class<M>) this.shaderParameters().getClass();
    return c;
  }

  /**
   * @return The shader parameters
   */

  M shaderParameters();

  /**
   * @return The blending state for the translucent
   */

  Optional<JCGLBlendState> blending();

  /**
   * @return The culling state for the translucent
   */

  JCGLCullingState culling();
}
