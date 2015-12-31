/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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

import com.io7m.jtensors.parameterized.PMatrixReadable4x4FType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * Matrices.
 */

public interface R2MatricesType
{
  /**
   * Evaluate matrices for the given observer.
   *
   * @param view       The observer's view matrix
   * @param projection The observer's projection
   * @param f          The function that will receive the evaluated matrices
   * @param <T>        The type of returned values
   *
   * @return A value of {@code T}
   */

  <T> T withObserver(
    final PMatrixReadable4x4FType<R2SpaceWorldType, R2SpaceEyeType> view,
    final R2ProjectionReadableType projection,
    final R2MatricesObserverFunctionType<T> f);
}
