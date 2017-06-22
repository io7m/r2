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

package com.io7m.r2.core;

import com.io7m.jcanephora.renderstate.JCGLBlendState;
import com.io7m.jcanephora.renderstate.JCGLCullingState;
import com.io7m.jfunctional.PartialBiFunctionType;
import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.core.shaders.types.R2ShaderTranslucentInstanceBillboardedType;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * The type of batched instance translucents.
 *
 * @param <M> The type of shader parameters
 */

@R2ImmutableStyleType
@Value.Immutable
public interface R2TranslucentBillboardedType<M> extends R2TranslucentType<M>
{
  @Override
  default <A, B, E extends Throwable> B matchTranslucent(
    final A context,
    final PartialBiFunctionType<A, R2TranslucentSingleType<M>, B, E> on_single,
    final PartialBiFunctionType<A, R2TranslucentBatchedType<M>, B, E> on_batched,
    final PartialBiFunctionType<A, R2TranslucentBillboardedType<M>, B, E> on_billboarded)
    throws E
  {
    return on_billboarded.call(context, this);
  }

  /**
   * @return The instance to be rendered
   */

  @Value.Parameter
  R2InstanceBillboardedType instance();

  /**
   * @return The shader
   */

  @Value.Parameter
  R2ShaderTranslucentInstanceBillboardedType<M> shader();

  @Override
  @Value.Parameter
  M shaderParameters();

  @Override
  @Value.Parameter
  Optional<JCGLBlendState> blending();

  @Override
  @Value.Parameter
  JCGLCullingState culling();
}
