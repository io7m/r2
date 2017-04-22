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

import com.io7m.jranges.RangeCheckException;
import com.io7m.r2.core.R2ProjectionFOV;
import com.io7m.r2.core.R2ProjectionReadableType;
import com.io7m.r2.core.R2WatchableType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.atomic.AtomicInteger;

public final class R2ProjectionFOVTest
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test
  public void testValues()
  {
    final R2ProjectionFOV p =
      R2ProjectionFOV.createWith(
        1.5707963267948966,
        1.0,
        1.0,
        100.0);

    p.projectionSetZFar(100.0);
    p.projectionSetZNear(1.0);

    final double delta = 0.00000001;
    Assert.assertEquals(1.0, p.projectionGetNearXMaximum(), delta);
    Assert.assertEquals(-1.0, p.projectionGetNearXMinimum(), delta);
    Assert.assertEquals(1.0, p.projectionGetNearYMaximum(), delta);
    Assert.assertEquals(-1.0, p.projectionGetNearYMinimum(), delta);
    Assert.assertEquals(1.0, p.projectionGetZNear(), delta);

    Assert.assertEquals(100.0, p.projectionGetFarXMaximum(), delta);
    Assert.assertEquals(-100.0, p.projectionGetFarXMinimum(), delta);
    Assert.assertEquals(100.0, p.projectionGetFarYMaximum(), delta);
    Assert.assertEquals(-100.0, p.projectionGetFarYMinimum(), delta);
    Assert.assertEquals(100.0, p.projectionGetZFar(), delta);

    p.projectionSetZFar(200.0);

    Assert.assertEquals(200.0, p.projectionGetFarXMaximum(), delta);
    Assert.assertEquals(-200.0, p.projectionGetFarXMinimum(), delta);
    Assert.assertEquals(200.0, p.projectionGetFarYMaximum(), delta);
    Assert.assertEquals(-200.0, p.projectionGetFarYMinimum(), delta);
    Assert.assertEquals(200.0, p.projectionGetZFar(), delta);

    p.setAspectRatio(2.0);
    Assert.assertEquals(2.0, p.aspectRatio(), delta);
    p.setHorizontalFOV(0.43633);
    Assert.assertEquals(0.43633, (double) p.horizontalFOV(), delta);
  }

  @Test
  public void testAspectNonzero()
  {
    final R2ProjectionFOV p =
      R2ProjectionFOV.createWith(
        1.5707963267948966,
        1.0,
        1.0,
        100.0);

    this.expected.expect(RangeCheckException.class);
    p.setAspectRatio(0.0);
  }

  @Test
  public void testWatchable()
  {
    final R2ProjectionFOV p =
      R2ProjectionFOV.createWith(
        1.5707963267948966,
        1.0,
        1.0,
        100.0);

    final AtomicInteger called = new AtomicInteger(0);
    final R2WatchableType<R2ProjectionReadableType> w =
      p.projectionGetWatchable();
    w.watchableAdd(ww -> called.incrementAndGet());

    Assert.assertEquals(1L, (long) called.get());

    p.projectionSetZFar(100.0);
    p.projectionSetZNear(1.0);
    p.setAspectRatio(1.0);
    p.setHorizontalFOV(1.0);

    Assert.assertEquals(5L, (long) called.get());
  }
}
