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

package com.io7m.r2.tests.core;

import com.io7m.jtensors.QuaternionM4F;
import com.io7m.jtensors.VectorM3F;
import com.io7m.jtensors.parameterized.PMatrix4x4FType;
import com.io7m.jtensors.parameterized.PMatrixHeapArrayM4x4F;
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.r2.core.R2TransformContext;
import com.io7m.r2.core.R2TransformContextType;
import com.io7m.r2.core.R2TransformOSiT;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import org.junit.Assert;
import org.junit.Test;

public final class R2TransformOSiTTest
{
  @Test
  public void testIdentity()
  {
    final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m =
      PMatrixHeapArrayM4x4F.newMatrix();
    final R2TransformOSiT r = R2TransformOSiT.newTransform();
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

    Assert.assertEquals(1.0f, m.getR0C0F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR1C0F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR2C0F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR3C0F(), 0.0f);

    Assert.assertEquals(0.0f, m.getR0C1F(), 0.0f);
    Assert.assertEquals(1.0f, m.getR1C1F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR2C1F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR3C1F(), 0.0f);

    Assert.assertEquals(0.0f, m.getR0C2F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR1C2F(), 0.0f);
    Assert.assertEquals(1.0f, m.getR2C2F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR3C2F(), 0.0f);

    Assert.assertEquals(0.0f, m.getR0C3F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR1C3F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR2C3F(), 0.0f);
    Assert.assertEquals(1.0f, m.getR3C3F(), 0.0f);
  }

  @Test
  public void testTranslate()
  {
    final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m =
      PMatrixHeapArrayM4x4F.newMatrix();
    final R2TransformOSiT r = R2TransformOSiT.newTransform();
    final PVectorM3F<R2SpaceWorldType> t = r.getTranslation();
    final QuaternionM4F o = r.getOrientation();
    final VectorM3F s = r.getScale();

    t.set3F(5.0f, 0.0f, 0.0f);

    Assert.assertEquals(5.0f, t.getXF(), 0.0f);
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

    System.out.println(m);

    Assert.assertEquals(1.0f, m.getR0C0F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR1C0F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR2C0F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR3C0F(), 0.0f);

    Assert.assertEquals(0.0f, m.getR0C1F(), 0.0f);
    Assert.assertEquals(1.0f, m.getR1C1F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR2C1F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR3C1F(), 0.0f);

    Assert.assertEquals(0.0f, m.getR0C2F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR1C2F(), 0.0f);
    Assert.assertEquals(1.0f, m.getR2C2F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR3C2F(), 0.0f);

    Assert.assertEquals(5.0f, m.getR0C3F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR1C3F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR2C3F(), 0.0f);
    Assert.assertEquals(1.0f, m.getR3C3F(), 0.0f);
  }
}
