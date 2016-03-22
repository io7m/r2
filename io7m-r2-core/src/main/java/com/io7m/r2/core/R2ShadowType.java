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
  long getShadowID();

  /**
   * The level to which the shadow is allowed to attenuate light. A value of
   * {@code n} means that if a point is in shadow, the light intensity will be
   * attenuated down to {@code n}.
   *
   * @return The minimum shadow factor
   */

  @Value.Parameter
  default float getFactorMinimum()
  {
    return 0.2f;
  }
}
