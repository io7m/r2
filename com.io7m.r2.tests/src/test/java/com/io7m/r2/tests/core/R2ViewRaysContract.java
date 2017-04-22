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

import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.r2.core.R2Bilinear;
import com.io7m.r2.core.R2ProjectionFOV;
import com.io7m.r2.core.R2ProjectionOrthographic;
import com.io7m.r2.core.R2ViewRays;
import com.io7m.r2.core.R2ViewRaysType;
import com.io7m.r2.spaces.R2SpaceClipType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class R2ViewRaysContract
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2ViewRaysContract.class);
  }

  private static void dumpViewRays(
    final R2ViewRaysType vr)
  {
    LOG.debug("origin_X0Y0: {} ", vr.originX0Y0());
    LOG.debug("origin_X1Y0: {}", vr.originX1Y0());
    LOG.debug("origin_X0Y1: {}", vr.originX0Y1());
    LOG.debug("origin_X1Y1: {}", vr.originX1Y1());
    LOG.debug("ray_X0Y0: {}", vr.rayX0Y0());
    LOG.debug("ray_X1Y0: {}", vr.rayX1Y0());
    LOG.debug("ray_X0Y1: {}", vr.rayX0Y1());
    LOG.debug("ray_X1Y1: {}", vr.rayX1Y1());
  }

  protected abstract R2ViewRaysType getViewRays();

  @Test
  public final void testIdentity()
  {
    final R2ViewRaysType vr = this.getViewRays();
    dumpViewRays(vr);
  }

  @Test
  public final void testOrthographic0()
  {
    final R2ProjectionOrthographic p =
      R2ProjectionOrthographic.createWith(
        -320.0,
        320.0,
        -240.0,
        240.0,
        1.0,
        100.0);

    final PMatrix4x4D<R2SpaceClipType, R2SpaceEyeType> ipm =
      PMatrices4x4D.invert(p.projectionMakeMatrix()).get();

    final R2ViewRaysType vr = R2ViewRays.newViewRays();
    vr.recalculate(ipm);
    dumpViewRays(vr);

    final double delta = 0.0001;
    Assert.assertEquals(0.0, vr.rayX0Y0().x(), delta);
    Assert.assertEquals(0.0, vr.rayX0Y0().y(), delta);
    Assert.assertEquals(1.0, vr.rayX0Y0().z(), delta);

    Assert.assertEquals(0.0, vr.rayX1Y0().x(), delta);
    Assert.assertEquals(0.0, vr.rayX1Y0().y(), delta);
    Assert.assertEquals(1.0, vr.rayX1Y0().z(), delta);

    Assert.assertEquals(0.0, vr.rayX0Y1().x(), delta);
    Assert.assertEquals(0.0, vr.rayX0Y1().y(), delta);
    Assert.assertEquals(1.0, vr.rayX0Y1().z(), delta);

    Assert.assertEquals(0.0, vr.rayX1Y1().x(), delta);
    Assert.assertEquals(0.0, vr.rayX1Y1().y(), delta);
    Assert.assertEquals(1.0, vr.rayX1Y1().z(), delta);

    Assert.assertEquals(-320.0, vr.originX0Y0().x(), delta);
    Assert.assertEquals(-240.0, vr.originX0Y0().y(), delta);
    Assert.assertEquals(0.0, vr.originX0Y0().z(), delta);

    Assert.assertEquals(320.0, vr.originX1Y0().x(), delta);
    Assert.assertEquals(-240.0, vr.originX1Y0().y(), delta);
    Assert.assertEquals(0.0, vr.originX1Y0().z(), delta);

    Assert.assertEquals(-320.0, vr.originX0Y1().x(), delta);
    Assert.assertEquals(240.0, vr.originX0Y1().y(), delta);
    Assert.assertEquals(0.0, vr.originX0Y1().z(), delta);

    Assert.assertEquals(320.0, vr.originX1Y1().x(), delta);
    Assert.assertEquals(240.0, vr.originX1Y1().y(), delta);
    Assert.assertEquals(0.0, vr.originX1Y1().z(), delta);
  }

  @Test
  public final void testPerspective0()
  {
    final R2ProjectionFOV p = R2ProjectionFOV.createWith(
      (double) (float) Math.toRadians(90.0), 1.0, 1.0, 100.0);

    final PMatrix4x4D<R2SpaceClipType, R2SpaceEyeType> ipm =
      PMatrices4x4D.invert(p.projectionMakeMatrix()).get();

    final R2ViewRaysType vr = R2ViewRays.newViewRays();
    vr.recalculate(ipm);
    dumpViewRays(vr);

    {
      final Vector3D out =
        R2Bilinear.bilinear3F(
          vr.rayX0Y0(),
          vr.rayX1Y0(),
          vr.rayX0Y1(),
          vr.rayX1Y1(),
          0.5,
          0.5);
      System.out.printf("q: %s\n", out);
    }

    final float delta = 0.0001f;
    Assert.assertEquals(1.0, vr.rayX0Y0().x(), (double) delta);
    Assert.assertEquals(1.0, vr.rayX0Y0().y(), (double) delta);
    Assert.assertEquals(1.0, vr.rayX0Y0().z(), (double) delta);

    Assert.assertEquals(-1.0, vr.rayX1Y0().x(), (double) delta);
    Assert.assertEquals(1.0, vr.rayX1Y0().y(), (double) delta);
    Assert.assertEquals(1.0, vr.rayX1Y0().z(), (double) delta);

    Assert.assertEquals(1.0, vr.rayX0Y1().x(), (double) delta);
    Assert.assertEquals(-1.0, vr.rayX0Y1().y(), (double) delta);
    Assert.assertEquals(1.0, vr.rayX0Y1().z(), (double) delta);

    Assert.assertEquals(-1.0, vr.rayX1Y1().x(), (double) delta);
    Assert.assertEquals(-1.0, vr.rayX1Y1().y(), (double) delta);
    Assert.assertEquals(1.0, vr.rayX1Y1().z(), (double) delta);

    Assert.assertEquals(0.0, vr.originX0Y0().x(), (double) delta);
    Assert.assertEquals(0.0, vr.originX0Y0().y(), (double) delta);
    Assert.assertEquals(0.0, vr.originX0Y0().z(), (double) delta);

    Assert.assertEquals(0.0, vr.originX1Y0().x(), (double) delta);
    Assert.assertEquals(0.0, vr.originX1Y0().y(), (double) delta);
    Assert.assertEquals(0.0, vr.originX1Y0().z(), (double) delta);

    Assert.assertEquals(0.0, vr.originX0Y1().x(), (double) delta);
    Assert.assertEquals(0.0, vr.originX0Y1().y(), (double) delta);
    Assert.assertEquals(0.0, vr.originX0Y1().z(), (double) delta);

    Assert.assertEquals(0.0, vr.originX1Y1().x(), (double) delta);
    Assert.assertEquals(0.0, vr.originX1Y1().y(), (double) delta);
    Assert.assertEquals(0.0, vr.originX1Y1().z(), (double) delta);
  }
}
