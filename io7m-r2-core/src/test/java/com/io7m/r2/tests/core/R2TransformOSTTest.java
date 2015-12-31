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

import com.io7m.jtensors.QuaternionM4F;
import com.io7m.jtensors.VectorM3F;
import com.io7m.jtensors.parameterized.PMatrix4x4FType;
import com.io7m.jtensors.parameterized.PMatrixHeapArrayM4x4F;
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.r2.core.R2TransformContext;
import com.io7m.r2.core.R2TransformContextType;
import com.io7m.r2.core.R2TransformOST;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import org.junit.Assert;
import org.junit.Test;

public final class R2TransformOSTTest
{
  @Test
  public void testIdentity()
  {
    final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m =
      PMatrixHeapArrayM4x4F.newMatrix();
    final R2TransformOST r = R2TransformOST.newTransform();
    final PVectorM3F<R2SpaceWorldType> t = r.getTranslation();
    final QuaternionM4F o = r.getOrientation();
    final VectorM3F s = r.getScale();

    Assert.assertEquals(0.0f, t.getXF(), 0.0f);
    Assert.assertEquals(0.0f, t.getYF(), 0.0f);
    Assert.assertEquals(0.0f, t.getZF(), 0.0f);

    Assert.assertEquals(0.0f, o.getXF(), 0.0f);
    Assert.assertEquals(0.0f, o.getYF(), 0.0f);
    Assert.assertEquals(0.0f, o.getZF(), 0.0f);
    Assert.assertEquals(1.0f, o.getWF(), 0.0f);

    Assert.assertEquals(1.0f, s.getXF(), 0.0f);
    Assert.assertEquals(1.0f, s.getYF(), 0.0f);
    Assert.assertEquals(1.0f, s.getZF(), 0.0f);

    final R2TransformContextType c = R2TransformContext.newContext();
    r.transformMakeMatrix4x4F(c, m);

    for (int mr = 0; mr < 4; ++mr) {
      for (int mc = 0; mc < 4; ++mc) {
        if (mr == mc) {
          Assert.assertEquals(1.0f, m.getRowColumnF(mr, mc), 0.0f);
        } else {
          Assert.assertEquals(0.0f, m.getRowColumnF(mr, mc), 0.0f);
        }
      }
    }
  }
}
