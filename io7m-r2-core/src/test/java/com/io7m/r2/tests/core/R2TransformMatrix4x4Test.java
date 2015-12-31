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

package com.io7m.r2.tests.core;

import com.io7m.jtensors.MatrixM4x4F;
import com.io7m.jtensors.MatrixReadable4x4FType;
import com.io7m.jtensors.parameterized.PMatrix4x4FType;
import com.io7m.jtensors.parameterized.PMatrixHeapArrayM4x4F;
import com.io7m.jtensors.tests.Matrix4x4FContract;
import com.io7m.r2.core.R2TransformContext;
import com.io7m.r2.core.R2TransformContextType;
import com.io7m.r2.core.R2TransformMatrix4x4;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import org.junit.Assert;
import org.junit.Test;

public final class R2TransformMatrix4x4Test extends
  Matrix4x4FContract<R2TransformMatrix4x4>
{
  @Test
  public void testRandom()
  {
    final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m =
      PMatrixHeapArrayM4x4F.newMatrix();
    final R2TransformMatrix4x4 r = new R2TransformMatrix4x4();

    final float[][] data = new float[4][4];

    for (int mr = 0; mr < 4; ++mr) {
      for (int mc = 0; mc < 4; ++mc) {
        final float k = (float) Math.random();
        data[mr][mc] = k;
        r.setRowColumnF(mr, mc, k);
      }
    }

    final R2TransformContextType c = R2TransformContext.newContext();
    r.transformMakeMatrix4x4F(c, m);

    for (int mr = 0; mr < 4; ++mr) {
      for (int mc = 0; mc < 4; ++mc) {
        final float k = data[mr][mc];
        Assert.assertEquals(k, m.getRowColumnF(mr, mc), 0.0f);
      }
    }
  }

  @Override
  protected R2TransformMatrix4x4 newMatrix()
  {
    return new R2TransformMatrix4x4();
  }

  @Override
  protected R2TransformMatrix4x4 newMatrixFrom(
    final MatrixReadable4x4FType m)
  {
    final R2TransformMatrix4x4 rm = new R2TransformMatrix4x4();
    MatrixM4x4F.copy(m, rm);
    return rm;
  }

  @Override
  protected void checkDirectBufferInvariants(
    final R2TransformMatrix4x4 m0)
  {

  }
}
