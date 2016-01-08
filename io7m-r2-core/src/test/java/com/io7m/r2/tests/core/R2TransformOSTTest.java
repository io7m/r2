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

import com.io7m.jtensors.Quaternion4FType;
import com.io7m.jtensors.VectorI2F;
import com.io7m.jtensors.VectorI3F;
import com.io7m.jtensors.VectorI4F;
import com.io7m.jtensors.parameterized.PMatrix4x4FType;
import com.io7m.jtensors.parameterized.PMatrixHeapArrayM4x4F;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.jtensors.parameterized.PVectorI2F;
import com.io7m.jtensors.parameterized.PVectorI3F;
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
    final PVector3FType<R2SpaceWorldType> t = r.getTranslation();
    final Quaternion4FType o = r.getOrientation();
    final float s = r.getScale();

    Assert.assertTrue(r.transformHasChanged());

    Assert.assertEquals(0.0f, t.getXF(), 0.0f);
    Assert.assertEquals(0.0f, t.getYF(), 0.0f);
    Assert.assertEquals(0.0f, t.getZF(), 0.0f);

    Assert.assertEquals(0.0f, o.getXF(), 0.0f);
    Assert.assertEquals(0.0f, o.getYF(), 0.0f);
    Assert.assertEquals(0.0f, o.getZF(), 0.0f);
    Assert.assertEquals(1.0f, o.getWF(), 0.0f);

    Assert.assertEquals(1.0f, s, 0.0f);

    final R2TransformContextType c = R2TransformContext.newContext();
    r.transformMakeMatrix4x4F(c, m);

    Assert.assertFalse(r.transformHasChanged());

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
  public void testScale()
  {
    final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m =
      PMatrixHeapArrayM4x4F.newMatrix();
    final R2TransformOST r = R2TransformOST.newTransform();
    final PVector3FType<R2SpaceWorldType> t = r.getTranslation();
    final Quaternion4FType o = r.getOrientation();
    r.setScale(2.0f);

    Assert.assertTrue(r.transformHasChanged());

    Assert.assertEquals(0.0f, t.getXF(), 0.0f);
    Assert.assertEquals(0.0f, t.getYF(), 0.0f);
    Assert.assertEquals(0.0f, t.getZF(), 0.0f);

    Assert.assertEquals(0.0f, o.getXF(), 0.0f);
    Assert.assertEquals(0.0f, o.getYF(), 0.0f);
    Assert.assertEquals(0.0f, o.getZF(), 0.0f);
    Assert.assertEquals(1.0f, o.getWF(), 0.0f);

    final R2TransformContextType c = R2TransformContext.newContext();
    r.transformMakeMatrix4x4F(c, m);

    Assert.assertFalse(r.transformHasChanged());

    Assert.assertEquals(2.0f, m.getR0C0F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR1C0F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR2C0F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR3C0F(), 0.0f);

    Assert.assertEquals(0.0f, m.getR0C1F(), 0.0f);
    Assert.assertEquals(2.0f, m.getR1C1F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR2C1F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR3C1F(), 0.0f);

    Assert.assertEquals(0.0f, m.getR0C2F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR1C2F(), 0.0f);
    Assert.assertEquals(2.0f, m.getR2C2F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR3C2F(), 0.0f);

    Assert.assertEquals(0.0f, m.getR0C3F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR1C3F(), 0.0f);
    Assert.assertEquals(0.0f, m.getR2C3F(), 0.0f);
    Assert.assertEquals(1.0f, m.getR3C3F(), 0.0f);
  }

  @Test
  public void testHasChangedTranslation()
  {
    final R2TransformContextType c = R2TransformContext.newContext();
    final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m =
      PMatrixHeapArrayM4x4F.newMatrix();
    final R2TransformOST r = R2TransformOST.newTransform();
    final PVector3FType<R2SpaceWorldType> t = r.getTranslation();

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    t.setXF(1.0f);

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    t.setYF(1.0f);

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    t.setZF(1.0f);

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    t.set2F(1.0f, 1.0f);

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    t.set3F(1.0f, 1.0f, 1.0f);

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    t.copyFrom2F(new VectorI2F(1.0f, 1.0f));

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    t.copyFrom3F(new VectorI3F(1.0f, 1.0f, 1.0f));

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    t.copyFromTyped2F(new PVectorI2F<>(1.0f, 1.0f));

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    t.copyFromTyped3F(new PVectorI3F<>(1.0f, 1.0f, 1.0f));

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());
  }

  @Test
  public void testHasChangedScale()
  {
    final R2TransformContextType c = R2TransformContext.newContext();
    final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m =
      PMatrixHeapArrayM4x4F.newMatrix();
    final R2TransformOST r = R2TransformOST.newTransform();

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    r.setScale(1.0f);

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());
  }

  @Test
  public void testHasChangedOrientation()
  {
    final R2TransformContextType c = R2TransformContext.newContext();
    final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m =
      PMatrixHeapArrayM4x4F.newMatrix();
    final R2TransformOST r = R2TransformOST.newTransform();
    final Quaternion4FType o = r.getOrientation();

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    o.setXF(1.0f);

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    o.setYF(1.0f);

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    o.setZF(1.0f);

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    o.setWF(1.0f);

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    o.set2F(1.0f, 1.0f);

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    o.set3F(1.0f, 1.0f, 1.0f);

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    o.set4F(1.0f, 1.0f, 1.0f, 1.0f);

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    o.copyFrom2F(new VectorI2F(1.0f, 1.0f));

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    o.copyFrom3F(new VectorI3F(1.0f, 1.0f, 1.0f));

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());

    o.copyFrom4F(new VectorI4F(1.0f, 1.0f, 1.0f, 1.0f));

    Assert.assertTrue(r.transformHasChanged());
    r.transformMakeMatrix4x4F(c, m);
    Assert.assertFalse(r.transformHasChanged());
  }

  @Test
  public void testTranslate()
  {
    final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m =
      PMatrixHeapArrayM4x4F.newMatrix();
    final R2TransformOST r = R2TransformOST.newTransform();
    final PVector3FType<R2SpaceWorldType> t = r.getTranslation();
    final Quaternion4FType o = r.getOrientation();
    final float s = r.getScale();

    Assert.assertTrue(r.transformHasChanged());

    t.set3F(5.0f, 0.0f, 0.0f);

    Assert.assertEquals(5.0f, t.getXF(), 0.0f);
    Assert.assertEquals(0.0f, t.getYF(), 0.0f);
    Assert.assertEquals(0.0f, t.getZF(), 0.0f);

    Assert.assertEquals(0.0f, o.getXF(), 0.0f);
    Assert.assertEquals(0.0f, o.getYF(), 0.0f);
    Assert.assertEquals(0.0f, o.getZF(), 0.0f);
    Assert.assertEquals(1.0f, o.getWF(), 0.0f);

    Assert.assertEquals(1.0f, s, 0.0f);

    final R2TransformContextType c = R2TransformContext.newContext();
    r.transformMakeMatrix4x4F(c, m);

    Assert.assertFalse(r.transformHasChanged());

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
