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

package com.io7m.r2.lights;

import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * The type of writable single-instance spherical lights.
 */

public interface R2LightSphericalSingleWritableType extends
  R2LightSphericalWritableType,
  R2LightVolumeSingleWritableType
{
  /**
   * Set the origin position of the light.
   *
   * @param p The position of the origin of the light in world-space
   */

  void setOriginPosition(
    PVector3D<R2SpaceWorldType> p);

  /**
   * Set the geometry scale factor.
   *
   * The final rendered scale of the light volume
   * geometry is given by {@link R2LightSphericalSingleReadableType#radius()}
   * * {@code f}. This is used to artificially enlarge the light volume to
   * account for, for example, the fact that a low-polygon approximation of a
   * sphere will typically have a smaller volume than a real sphere would.
   *
   * @param f The scale factor
   */

  void setGeometryScaleFactor(
    double f);
}
