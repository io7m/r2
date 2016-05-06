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
import com.io7m.jtensors.Vector3FType;
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
import com.io7m.r2.core.R2TransformReadableType;
import com.io7m.r2.core.R2TransformSiOT;
import com.io7m.r2.core.R2WatchableType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public final class R2TransformOSiTTest
{
  @Test
  public void testIdentity()
  {
    final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m =
      PMatrixHeapArrayM4x4F.newMatrix();
    final R2TransformSiOT r = R2TransformSiOT.newTransform();
    final PVector3FType<R2SpaceWorldType> t = r.getTranslation();
    final Quaternion4FType o = r.getOrientation();
    final Vector3FType s = r.getScale();

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
    final R2TransformSiOT r = R2TransformSiOT.newTransform();
    final PVector3FType<R2SpaceWorldType> t = r.getTranslation();
    final Quaternion4FType o = r.getOrientation();
    final Vector3FType s = r.getScale();

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

  @Test
  public void testHasChangedTranslation()
  {
    final AtomicBoolean changed = new AtomicBoolean(true);
    final R2TransformContextType c = R2TransformContext.newContext();
    final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m =
      PMatrixHeapArrayM4x4F.newMatrix();
    final R2TransformSiOT r =
      R2TransformSiOT.newTransform();
    final R2WatchableType<R2TransformReadableType> w =
      r.transformGetWatchable();
    w.watchableAdd(ww -> changed.set(true));
    final PVector3FType<R2SpaceWorldType> t = r.getTranslation();

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    t.setXF(1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    t.setYF(1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    t.setZF(1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    t.set2F(1.0f, 1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    t.set3F(1.0f, 1.0f, 1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    t.copyFrom2F(new VectorI2F(1.0f, 1.0f));

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    t.copyFrom3F(new VectorI3F(1.0f, 1.0f, 1.0f));

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    t.copyFromTyped2F(new PVectorI2F<>(1.0f, 1.0f));

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    t.copyFromTyped3F(new PVectorI3F<>(1.0f, 1.0f, 1.0f));

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);
  }

  @Test
  public void testHasChangedScale()
  {
    final AtomicBoolean changed = new AtomicBoolean(true);
    final R2TransformContextType c = R2TransformContext.newContext();
    final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m =
      PMatrixHeapArrayM4x4F.newMatrix();
    final R2TransformSiOT r =
      R2TransformSiOT.newTransform();
    final R2WatchableType<R2TransformReadableType> w =
      r.transformGetWatchable();
    w.watchableAdd(ww -> changed.set(true));
    final Vector3FType s = r.getScale();

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    s.setXF(1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    s.setYF(1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    s.setZF(1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    s.set2F(1.0f, 1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    s.set3F(1.0f, 1.0f, 1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    s.copyFrom2F(new VectorI2F(1.0f, 1.0f));

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    s.copyFrom3F(new VectorI3F(1.0f, 1.0f, 1.0f));

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);
  }

  @Test
  public void testHasChangedOrientation()
  {
    final AtomicBoolean changed = new AtomicBoolean(true);
    final R2TransformContextType c = R2TransformContext.newContext();
    final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m =
      PMatrixHeapArrayM4x4F.newMatrix();
    final R2TransformSiOT r =
      R2TransformSiOT.newTransform();
    final R2WatchableType<R2TransformReadableType> w =
      r.transformGetWatchable();
    w.watchableAdd(ww -> changed.set(true));
    final Quaternion4FType o = r.getOrientation();

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    o.setXF(1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    o.setYF(1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    o.setZF(1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    o.setWF(1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    o.set2F(1.0f, 1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    o.set3F(1.0f, 1.0f, 1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    o.set4F(1.0f, 1.0f, 1.0f, 1.0f);

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    o.copyFrom2F(new VectorI2F(1.0f, 1.0f));

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    o.copyFrom3F(new VectorI3F(1.0f, 1.0f, 1.0f));

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);

    o.copyFrom4F(new VectorI4F(1.0f, 1.0f, 1.0f, 1.0f));

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F(c, m);
    changed.set(false);
  }
}
