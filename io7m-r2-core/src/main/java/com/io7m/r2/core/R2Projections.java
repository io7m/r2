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
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions over projections.
 */

public final class R2Projections
{
  private R2Projections()
  {
    throw new UnreachableCodeException();
  }

  private static double log2(
    final double x)
  {
    return (StrictMath.log(x) / StrictMath.log(2.0));
  }

  /**
   * Calculate a depth coefficient for the given projection.
   *
   * @param p The projection
   *
   * @return A depth coefficient
   */

  public static double getDepthCoefficient(
    final R2ProjectionReadableType p)
  {
    NullCheck.notNull(p);
    final double far = (double) p.projectionGetZFar();
    return 2.0 / R2Projections.log2(far + 1.0);
  }
}
