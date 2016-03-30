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

import com.io7m.jfunctional.PartialBiFunctionType;

/**
 * The type of readable projective lights.
 */

public interface R2LightProjectiveWithShadowReadableType extends
  R2LightProjectiveReadableType,
  R2LightWithShadowSingleType
{
  @Override
  default <A, B, E extends Throwable> B matchProjectiveReadable(
    final A context,
    final PartialBiFunctionType<A, R2LightProjectiveWithoutShadowReadableType, B, E> on_shadowless,
    final PartialBiFunctionType<A, R2LightProjectiveWithShadowReadableType, B, E> on_shadowed)
    throws E
  {
    return on_shadowed.call(context, this);
  }
}
