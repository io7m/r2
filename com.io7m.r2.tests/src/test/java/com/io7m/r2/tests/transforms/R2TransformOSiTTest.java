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

package com.io7m.r2.tests.transforms;

import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.quaternions.Quaternion4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.r2.core.api.watchable.R2WatchableType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import com.io7m.r2.transforms.R2TransformReadableType;
import com.io7m.r2.transforms.R2TransformSiOT;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public final class R2TransformOSiTTest
{
  @Test
  public void testIdentity()
  {
    final R2TransformSiOT r = R2TransformSiOT.create();
    final PVector3D<R2SpaceWorldType> t = r.translation();
    final Quaternion4D o = r.orientation();
    final Vector3D s = r.scale();

    Assert.assertEquals(0.0, t.x(), 0.0);
    Assert.assertEquals(0.0, t.y(), 0.0);
    Assert.assertEquals(0.0, t.z(), 0.0);

    Assert.assertEquals(0.0, o.x(), 0.0);
    Assert.assertEquals(0.0, o.y(), 0.0);
    Assert.assertEquals(0.0, o.z(), 0.0);
    Assert.assertEquals(1.0, o.w(), 0.0);

    Assert.assertEquals(1.0, s.x(), 0.0);
    Assert.assertEquals(1.0, s.y(), 0.0);
    Assert.assertEquals(1.0, s.z(), 0.0);

    final PMatrix4x4D<R2SpaceObjectType, R2SpaceWorldType> m =
      r.transformMakeMatrix4x4F();

    Assert.assertEquals(1.0, m.r0c0(), 0.0);
    Assert.assertEquals(0.0, m.r1c0(), 0.0);
    Assert.assertEquals(0.0, m.r2c0(), 0.0);
    Assert.assertEquals(0.0, m.r3c0(), 0.0);

    Assert.assertEquals(0.0, m.r0c1(), 0.0);
    Assert.assertEquals(1.0, m.r1c1(), 0.0);
    Assert.assertEquals(0.0, m.r2c1(), 0.0);
    Assert.assertEquals(0.0, m.r3c1(), 0.0);

    Assert.assertEquals(0.0, m.r0c2(), 0.0);
    Assert.assertEquals(0.0, m.r1c2(), 0.0);
    Assert.assertEquals(1.0, m.r2c2(), 0.0);
    Assert.assertEquals(0.0, m.r3c2(), 0.0);

    Assert.assertEquals(0.0, m.r0c3(), 0.0);
    Assert.assertEquals(0.0, m.r1c3(), 0.0);
    Assert.assertEquals(0.0, m.r2c3(), 0.0);
    Assert.assertEquals(1.0, m.r3c3(), 0.0);
  }

  @Test
  public void testTranslate()
  {
    final R2TransformSiOT r = R2TransformSiOT.create();

    r.setTranslation(PVector3D.of(5.0, 0.0, 0.0));

    final PVector3D<R2SpaceWorldType> t = r.translation();
    final Quaternion4D o = r.orientation();
    final Vector3D s = r.scale();
    Assert.assertEquals(5.0, t.x(), 0.0);
    Assert.assertEquals(0.0, t.y(), 0.0);
    Assert.assertEquals(0.0, t.z(), 0.0);

    Assert.assertEquals(0.0, o.x(), 0.0);
    Assert.assertEquals(0.0, o.y(), 0.0);
    Assert.assertEquals(0.0, o.z(), 0.0);
    Assert.assertEquals(1.0, o.w(), 0.0);

    Assert.assertEquals(1.0, s.x(), 0.0);
    Assert.assertEquals(1.0, s.y(), 0.0);
    Assert.assertEquals(1.0, s.z(), 0.0);

    final PMatrix4x4D<R2SpaceObjectType, R2SpaceWorldType> m =
      r.transformMakeMatrix4x4F();

    System.out.println(m);

    Assert.assertEquals(1.0, m.r0c0(), 0.0);
    Assert.assertEquals(0.0, m.r1c0(), 0.0);
    Assert.assertEquals(0.0, m.r2c0(), 0.0);
    Assert.assertEquals(0.0, m.r3c0(), 0.0);

    Assert.assertEquals(0.0, m.r0c1(), 0.0);
    Assert.assertEquals(1.0, m.r1c1(), 0.0);
    Assert.assertEquals(0.0, m.r2c1(), 0.0);
    Assert.assertEquals(0.0, m.r3c1(), 0.0);

    Assert.assertEquals(0.0, m.r0c2(), 0.0);
    Assert.assertEquals(0.0, m.r1c2(), 0.0);
    Assert.assertEquals(1.0, m.r2c2(), 0.0);
    Assert.assertEquals(0.0, m.r3c2(), 0.0);

    Assert.assertEquals(5.0, m.r0c3(), 0.0);
    Assert.assertEquals(0.0, m.r1c3(), 0.0);
    Assert.assertEquals(0.0, m.r2c3(), 0.0);
    Assert.assertEquals(1.0, m.r3c3(), 0.0);
  }

  @Test
  public void testHasChangedTranslation()
  {
    final AtomicBoolean changed = new AtomicBoolean(true);

    final R2TransformSiOT r =
      R2TransformSiOT.create();
    final R2WatchableType<R2TransformReadableType> w =
      r.transformGetWatchable();
    w.watchableAdd(ww -> changed.set(true));

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F();
    changed.set(false);

    r.setTranslation(PVector3D.of(1.0, 2.0, 3.0));
    Assert.assertTrue(changed.get());

    r.transformMakeMatrix4x4F();
    changed.set(false);

    Assert.assertEquals(PVector3D.of(1.0, 2.0, 3.0), r.translation());
  }

  @Test
  public void testHasChangedScale()
  {
    final AtomicBoolean changed = new AtomicBoolean(true);

    final R2TransformSiOT r =
      R2TransformSiOT.create();
    final R2WatchableType<R2TransformReadableType> w =
      r.transformGetWatchable();
    w.watchableAdd(ww -> changed.set(true));

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F();
    changed.set(false);

    r.setScaleAxes(Vector3D.of(2.0, 3.0, 4.0));
    Assert.assertTrue(changed.get());

    r.transformMakeMatrix4x4F();
    changed.set(false);

    Assert.assertEquals(Vector3D.of(2.0, 3.0, 4.0), r.scaleAxes());
  }

  @Test
  public void testHasChangedOrientation()
  {
    final AtomicBoolean changed = new AtomicBoolean(true);

    final R2TransformSiOT r =
      R2TransformSiOT.create();
    final R2WatchableType<R2TransformReadableType> w =
      r.transformGetWatchable();
    w.watchableAdd(ww -> changed.set(true));

    Assert.assertTrue(changed.get());
    r.transformMakeMatrix4x4F();
    changed.set(false);

    r.setOrientation(Quaternion4D.of(1.0, 2.0, 3.0, 4.0));
    Assert.assertTrue(changed.get());

    r.transformMakeMatrix4x4F();
    changed.set(false);

    Assert.assertEquals(Quaternion4D.of(1.0, 2.0, 3.0, 4.0), r.orientation());
  }
}
