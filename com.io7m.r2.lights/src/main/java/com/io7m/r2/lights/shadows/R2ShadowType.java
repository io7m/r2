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

package com.io7m.r2.lights.shadows;

import com.io7m.jfunctional.PartialBiFunctionType;
import org.immutables.value.Value;

/**
 * The type of shadows.
 */

public interface R2ShadowType
{
  /**
   * @return The unique shadow ID
   */

  @Value.Parameter
  long shadowID();

  /**
   * The level to which the shadow is allowed to attenuate light. A value of
   * {@code n} means that if a point is in shadow, the light intensity will be
   * attenuated down to {@code n}.
   *
   * @return The minimum shadow factor
   */

  @Value.Parameter
  default float minimumFactor()
  {
    return 0.2f;
  }

  /**
   * Match on the type of light.
   *
   * @param context     A context value
   * @param on_variance Evaluated for variance shadows
   * @param <A>         The type of context values
   * @param <B>         The type of returned values
   * @param <E>         The type of raised exceptions
   *
   * @return A value of type {@code B}
   *
   * @throws E If any of the given functions raise {@code E}
   */

  <A, B, E extends Throwable>
  B matchShadow(
    A context,
    PartialBiFunctionType<A, R2ShadowDepthVarianceType, B, E> on_variance)
    throws E;
}
