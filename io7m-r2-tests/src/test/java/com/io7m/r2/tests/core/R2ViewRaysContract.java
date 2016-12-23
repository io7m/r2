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

import com.io7m.jcanephora.core.JCGLProjectionMatrices;
import com.io7m.jcanephora.core.JCGLProjectionMatricesType;
import com.io7m.jtensors.VectorM3F;
import com.io7m.jtensors.parameterized.PMatrix4x4FType;
import com.io7m.jtensors.parameterized.PMatrixHeapArrayM4x4F;
import com.io7m.jtensors.parameterized.PMatrixM4x4F;
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
    R2ViewRaysContract.LOG.debug("origin_X0Y0: {} ", vr.originX0Y0());
    R2ViewRaysContract.LOG.debug("origin_X1Y0: {}", vr.originX1Y0());
    R2ViewRaysContract.LOG.debug("origin_X0Y1: {}", vr.originX0Y1());
    R2ViewRaysContract.LOG.debug("origin_X1Y1: {}", vr.originX1Y1());
    R2ViewRaysContract.LOG.debug("ray_X0Y0: {}", vr.rayX0Y0());
    R2ViewRaysContract.LOG.debug("ray_X1Y0: {}", vr.rayX1Y0());
    R2ViewRaysContract.LOG.debug("ray_X0Y1: {}", vr.rayX0Y1());
    R2ViewRaysContract.LOG.debug("ray_X1Y1: {}", vr.rayX1Y1());
  }

  protected abstract R2ViewRaysType getViewRays(
    final PMatrixM4x4F.ContextPM4F c);

  @Test
  public final void testIdentity()
  {
    final PMatrixM4x4F.ContextPM4F c = new PMatrixM4x4F.ContextPM4F();
    final R2ViewRaysType vr = this.getViewRays(c);
    R2ViewRaysContract.dumpViewRays(vr);
  }

  @Test
  public final void testOrthographic0()
  {
    final PMatrixM4x4F.ContextPM4F c = new PMatrixM4x4F.ContextPM4F();
    final JCGLProjectionMatricesType pc = JCGLProjectionMatrices.newMatrices();
    final R2ProjectionOrthographic p = R2ProjectionOrthographic.newFrustumWith(
      pc,
      -320.0f,
      320.0f,
      -240.0f,
      240.0f,
      1.0f,
      100.0f);

    final PMatrix4x4FType<R2SpaceClipType, R2SpaceEyeType> ipm =
      PMatrixHeapArrayM4x4F.newMatrix();
    final PMatrix4x4FType<R2SpaceEyeType, R2SpaceClipType> ipm_tmp =
      PMatrixHeapArrayM4x4F.newMatrix();

    p.projectionMakeMatrix(ipm_tmp);
    PMatrixM4x4F.invert(c, ipm_tmp, ipm);

    final R2ViewRaysType vr = R2ViewRays.newViewRays(c);
    vr.recalculate(c, ipm);
    R2ViewRaysContract.dumpViewRays(vr);

    final float delta = 0.0001f;
    Assert.assertEquals(0.0f, vr.rayX0Y0().getXF(), delta);
    Assert.assertEquals(0.0f, vr.rayX0Y0().getYF(), delta);
    Assert.assertEquals(1.0f, vr.rayX0Y0().getZF(), delta);
    Assert.assertEquals(0.0f, vr.rayX0Y0().getWF(), delta);

    Assert.assertEquals(0.0f, vr.rayX1Y0().getXF(), delta);
    Assert.assertEquals(0.0f, vr.rayX1Y0().getYF(), delta);
    Assert.assertEquals(1.0f, vr.rayX1Y0().getZF(), delta);
    Assert.assertEquals(0.0f, vr.rayX1Y0().getWF(), delta);

    Assert.assertEquals(0.0f, vr.rayX0Y1().getXF(), delta);
    Assert.assertEquals(0.0f, vr.rayX0Y1().getYF(), delta);
    Assert.assertEquals(1.0f, vr.rayX0Y1().getZF(), delta);
    Assert.assertEquals(0.0f, vr.rayX0Y1().getWF(), delta);

    Assert.assertEquals(0.0f, vr.rayX1Y1().getXF(), delta);
    Assert.assertEquals(0.0f, vr.rayX1Y1().getYF(), delta);
    Assert.assertEquals(1.0f, vr.rayX1Y1().getZF(), delta);
    Assert.assertEquals(0.0f, vr.rayX1Y1().getWF(), delta);

    Assert.assertEquals(-320.0f, vr.originX0Y0().getXF(), delta);
    Assert.assertEquals(-240.0f, vr.originX0Y0().getYF(), delta);
    Assert.assertEquals(0.0f, vr.originX0Y0().getZF(), delta);
    Assert.assertEquals(1.0f, vr.originX0Y0().getWF(), delta);

    Assert.assertEquals(320.0f, vr.originX1Y0().getXF(), delta);
    Assert.assertEquals(-240.0f, vr.originX1Y0().getYF(), delta);
    Assert.assertEquals(0.0f, vr.originX1Y0().getZF(), delta);
    Assert.assertEquals(1.0f, vr.originX1Y0().getWF(), delta);

    Assert.assertEquals(-320.0f, vr.originX0Y1().getXF(), delta);
    Assert.assertEquals(240.0f, vr.originX0Y1().getYF(), delta);
    Assert.assertEquals(0.0f, vr.originX0Y1().getZF(), delta);
    Assert.assertEquals(1.0f, vr.originX0Y1().getWF(), delta);

    Assert.assertEquals(320.0f, vr.originX1Y1().getXF(), delta);
    Assert.assertEquals(240.0f, vr.originX1Y1().getYF(), delta);
    Assert.assertEquals(0.0f, vr.originX1Y1().getZF(), delta);
    Assert.assertEquals(1.0f, vr.originX1Y1().getWF(), delta);
  }

  @Test
  public final void testPerspective0()
  {
    final PMatrixM4x4F.ContextPM4F c = new PMatrixM4x4F.ContextPM4F();
    final JCGLProjectionMatricesType pc = JCGLProjectionMatrices.newMatrices();
    final R2ProjectionFOV p = R2ProjectionFOV.newFrustumWith(
      pc, (float) Math.toRadians(90.0f), 1.0f, 1.0f, 100.0f);

    final PMatrix4x4FType<R2SpaceClipType, R2SpaceEyeType> ipm =
      PMatrixHeapArrayM4x4F.newMatrix();
    final PMatrix4x4FType<R2SpaceEyeType, R2SpaceClipType> ipm_tmp =
      PMatrixHeapArrayM4x4F.newMatrix();

    p.projectionMakeMatrix(ipm_tmp);
    PMatrixM4x4F.invert(c, ipm_tmp, ipm);

    final R2ViewRaysType vr = R2ViewRays.newViewRays(c);
    vr.recalculate(c, ipm);
    R2ViewRaysContract.dumpViewRays(vr);

    {
      final VectorM3F.ContextVM3F vc = new VectorM3F.ContextVM3F();
      final VectorM3F temp_0 = new VectorM3F();
      final VectorM3F temp_1 = new VectorM3F();
      final VectorM3F out = new VectorM3F();

      R2Bilinear.bilinear3F(
        vc,
        vr.rayX0Y0(),
        vr.rayX1Y0(),
        vr.rayX0Y1(),
        vr.rayX1Y1(),
        0.5f,
        0.5f, temp_0, temp_1, out);
      System.out.printf("q: %s\n", out);
    }

    final float delta = 0.0001f;
    Assert.assertEquals(1.0f, vr.rayX0Y0().getXF(), delta);
    Assert.assertEquals(1.0f, vr.rayX0Y0().getYF(), delta);
    Assert.assertEquals(1.0f, vr.rayX0Y0().getZF(), delta);
    Assert.assertEquals(0.0f, vr.rayX0Y0().getWF(), delta);

    Assert.assertEquals(-1.0f, vr.rayX1Y0().getXF(), delta);
    Assert.assertEquals(1.0f, vr.rayX1Y0().getYF(), delta);
    Assert.assertEquals(1.0f, vr.rayX1Y0().getZF(), delta);
    Assert.assertEquals(0.0f, vr.rayX1Y0().getWF(), delta);

    Assert.assertEquals(1.0f, vr.rayX0Y1().getXF(), delta);
    Assert.assertEquals(-1.0f, vr.rayX0Y1().getYF(), delta);
    Assert.assertEquals(1.0f, vr.rayX0Y1().getZF(), delta);
    Assert.assertEquals(0.0f, vr.rayX0Y1().getWF(), delta);

    Assert.assertEquals(-1.0f, vr.rayX1Y1().getXF(), delta);
    Assert.assertEquals(-1.0f, vr.rayX1Y1().getYF(), delta);
    Assert.assertEquals(1.0f, vr.rayX1Y1().getZF(), delta);
    Assert.assertEquals(0.0f, vr.rayX1Y1().getWF(), delta);

    Assert.assertEquals(0.0f, vr.originX0Y0().getXF(), delta);
    Assert.assertEquals(0.0f, vr.originX0Y0().getYF(), delta);
    Assert.assertEquals(0.0f, vr.originX0Y0().getZF(), delta);
    Assert.assertEquals(1.0f, vr.originX0Y0().getWF(), delta);

    Assert.assertEquals(0.0f, vr.originX1Y0().getXF(), delta);
    Assert.assertEquals(0.0f, vr.originX1Y0().getYF(), delta);
    Assert.assertEquals(0.0f, vr.originX1Y0().getZF(), delta);
    Assert.assertEquals(1.0f, vr.originX1Y0().getWF(), delta);

    Assert.assertEquals(0.0f, vr.originX0Y1().getXF(), delta);
    Assert.assertEquals(0.0f, vr.originX0Y1().getYF(), delta);
    Assert.assertEquals(0.0f, vr.originX0Y1().getZF(), delta);
    Assert.assertEquals(1.0f, vr.originX0Y1().getWF(), delta);

    Assert.assertEquals(0.0f, vr.originX1Y1().getXF(), delta);
    Assert.assertEquals(0.0f, vr.originX1Y1().getYF(), delta);
    Assert.assertEquals(0.0f, vr.originX1Y1().getZF(), delta);
    Assert.assertEquals(1.0f, vr.originX1Y1().getWF(), delta);
  }
}
