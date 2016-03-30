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
import com.io7m.jtensors.parameterized.PVectorReadable3FType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * The type of readable projective lights.
 */

public interface R2LightProjectiveReadableType extends
  R2LightVolumeSingleReadableType
{
  /**
   * @return The light radius
   */

  float getRadius();

  /**
   * @return The light falloff exponent
   */

  float getFalloff();

  /**
   * @return The light's projection
   */

  R2ProjectionReadableType getProjection();

  /**
   * @return The image that will be projected from the light
   */

  R2Texture2DUsableType getImage();

  /**
   * @return The transform for the light origin
   */

  @Override
  R2TransformViewReadableType getTransform();

  /**
   * @return The position of the origin of the light in world-space
   */

  PVectorReadable3FType<R2SpaceWorldType> getPosition();

  @Override
  default <A, B, E extends Throwable> B matchLightVolumeSingleReadable(
    final A context,
    final PartialBiFunctionType<A, R2LightProjectiveReadableType, B, E> on_projective,
    final PartialBiFunctionType<A, R2LightSphericalSingleReadableType, B, E> on_spherical)
    throws E
  {
    return on_projective.call(context, this);
  }

  /**
   * Match on the type of projective light.
   *
   * @param context   A context value
   * @param on_shadowless Evaluated for projective lights without shadows
   * @param on_shadowed Evaluated for projective lights with shadows
   * @param <A>       The type of context values
   * @param <B>       The type of returned values
   * @param <E>       The type of raised exceptions
   *
   * @return A value of type {@code B}
   *
   * @throws E If any of the given functions raise {@code E}
   */

  <A, B, E extends Throwable>
  B matchProjectiveReadable(
    A context,
    PartialBiFunctionType<A, R2LightProjectiveWithoutShadowReadableType, B, E> on_shadowless,
    PartialBiFunctionType<A, R2LightProjectiveWithShadowReadableType, B, E> on_shadowed)
    throws E;
}
