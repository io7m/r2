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

import com.io7m.jtensors.parameterized.PMatrixReadable3x3FType;
import com.io7m.r2.spaces.R2SpaceTextureType;

/**
 * Access to the matrices for a given observer.
 */

public interface R2MatricesObserverType extends R2MatricesObserverValuesType
{
  /**
   * Evaluate matrices for a given transform, relative to the current observer.
   *
   * @param t   The transform
   * @param uv  The UV matrix
   * @param f   The function that will receive matrices
   * @param <T> The type of returned values
   *
   * @return A value of {@code T}
   *
   * @throws R2Exception If {@code f} raises {@link R2Exception}
   */

  <T> T withTransform(
    R2TransformReadableType t,
    PMatrixReadable3x3FType<R2SpaceTextureType, R2SpaceTextureType> uv,
    R2MatricesInstanceSingleFunctionType<T> f)
    throws R2Exception;
}
