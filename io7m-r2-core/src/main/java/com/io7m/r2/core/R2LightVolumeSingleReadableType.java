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
 * The type of readable single-instance lights that operate using light
 * volumes.
 */

public interface R2LightVolumeSingleReadableType extends
  R2LightSingleReadableType
{
  /**
   * Get access to the readable transform for the light volume geometry.
   *
   * @return The readable transform for the light volume
   */

  R2TransformReadableType getTransform();

  /**
   * Match on the type of volume light.
   *
   * @param context       A context value
   * @param on_projective Evaluated for projective lights
   * @param on_spherical  Evaluated for spherical lights
   * @param <A>           The type of context values
   * @param <B>           The type of returned values
   * @param <E>           The type of raised exceptions
   *
   * @return A value of type {@code B}
   *
   * @throws E If any of the given functions raise {@code E}
   */

  <A, B, E extends Throwable>
  B matchLightVolumeSingleReadable(
    A context,
    PartialBiFunctionType<A, R2LightProjectiveReadableType, B, E> on_projective,
    PartialBiFunctionType<A, R2LightSphericalSingleReadableType, B, E> on_spherical)
    throws E;
}
