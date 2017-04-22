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

import com.io7m.jtensors.core.parameterized.matrices.PMatrix3x3D;
import com.io7m.r2.spaces.R2SpaceTextureType;

import java.util.function.BiFunction;

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
   * @param x   A context value passed to {@code f}
   * @param <A> The type of consumed values
   * @param <B> The type of returned values
   *
   * @return A value of {@code B}
   *
   * @throws R2Exception If {@code f} raises {@link R2Exception}
   */

  <A, B> B withTransform(
    R2TransformReadableType t,
    PMatrix3x3D<R2SpaceTextureType, R2SpaceTextureType> uv,
    A x,
    BiFunction<R2MatricesInstanceSingleType, A, B> f)
    throws R2Exception;

  /**
   * Evaluate matrices for a given projective light, relative to the current
   * observer.
   *
   * @param t   The light
   * @param f   The function that will receive matrices
   * @param x   A context value passed to {@code f}
   * @param <A> The type of consumed values
   * @param <B> The type of returned values
   *
   * @return A value of {@code B}
   *
   * @throws R2Exception If {@code f} raises {@link R2Exception}
   */

  <A, B> B withProjectiveLight(
    R2LightProjectiveReadableType t,
    A x,
    BiFunction<R2MatricesProjectiveLightType, A, B> f)
    throws R2Exception;

  /**
   * Evaluate matrices for a given volume light, relative to the current
   * observer.
   *
   * @param t   The light
   * @param f   The function that will receive matrices
   * @param x   A context value passed to {@code f}
   * @param <A> The type of consumed values
   * @param <B> The type of returned values
   *
   * @return A value of {@code B}
   *
   * @throws R2Exception If {@code f} raises {@link R2Exception}
   */

  <A, B> B withVolumeLight(
    R2LightVolumeSingleReadableType t,
    A x,
    BiFunction<R2MatricesVolumeLightType, A, B> f)
    throws R2Exception;
}
