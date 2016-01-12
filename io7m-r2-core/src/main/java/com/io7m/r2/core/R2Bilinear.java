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
import com.io7m.jtensors.Vector3FType;
import com.io7m.jtensors.VectorM3F;
import com.io7m.jtensors.VectorReadable3FType;
import com.io7m.jtensors.VectorWritable3FType;
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
   * @param c      Preallocated storage for vector operations
   * @param x0y0   A corner
   * @param x1y0   A corner
   * @param x0y1   A corner
   * @param x1y1   A corner
   * @param px     An interpolation value for the X axis
   * @param py     An interpolation value for the Y axis
   * @param temp_0 A temporary vector
   * @param temp_1 A temporary vector
   * @param out    The output vector
   */

  public static void bilinear3F(
    final VectorM3F.ContextVM3F c,
    final VectorReadable3FType x0y0,
    final VectorReadable3FType x1y0,
    final VectorReadable3FType x0y1,
    final VectorReadable3FType x1y1,
    final float px,
    final float py,
    final Vector3FType temp_0,
    final Vector3FType temp_1,
    final VectorWritable3FType out)
  {
    NullCheck.notNull(c, "Context");
    NullCheck.notNull(x0y0, "x0y0");
    NullCheck.notNull(x1y0, "x1y0");
    NullCheck.notNull(x0y1, "x0y1");
    NullCheck.notNull(x1y1, "x1y1");
    NullCheck.notNull(out, "Output vector");
    NullCheck.notNull(temp_0, "Temporary vector");
    NullCheck.notNull(temp_1, "Temporary vector");

    VectorM3F.interpolateLinear(c, x0y0, x1y0, px, temp_0);
    VectorM3F.interpolateLinear(c, x0y1, x1y1, px, temp_1);
    VectorM3F.interpolateLinear(c, temp_0, temp_1, py, out);
  }
}
