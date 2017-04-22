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

import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors3D;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Bilinear functions.
 */

public final class R2Bilinear
{
  private R2Bilinear()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Interpolate between the four given points based on {@code px} and {@code
   * py}.
   *
   * @param x0y0 A corner
   * @param x1y0 A corner
   * @param x0y1 A corner
   * @param x1y1 A corner
   * @param px   An interpolation value for the X axis
   * @param py   An interpolation value for the Y axis
   *
   * @return An interpolated vector
   */

  public static Vector3D bilinear3F(
    final Vector3D x0y0,
    final Vector3D x1y0,
    final Vector3D x0y1,
    final Vector3D x1y1,
    final double px,
    final double py)
  {
    NullCheck.notNull(x0y0, "x0y0");
    NullCheck.notNull(x1y0, "x1y0");
    NullCheck.notNull(x0y1, "x0y1");
    NullCheck.notNull(x1y1, "x1y1");

    final Vector3D temp_0 = Vectors3D.interpolateLinear(x0y0, x1y0, px);
    final Vector3D temp_1 = Vectors3D.interpolateLinear(x0y1, x1y1, px);
    return Vectors3D.interpolateLinear(temp_0, temp_1, py);
  }
}
