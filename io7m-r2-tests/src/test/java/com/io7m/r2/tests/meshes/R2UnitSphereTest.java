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

package com.io7m.r2.tests.meshes;

import com.io7m.r2.meshes.defaults.R2UnitSphere;
import org.junit.Assert;
import org.junit.Test;

public final class R2UnitSphereTest
{
  @Test
  public void testCircleArea()
  {
    Assert.assertEquals(Math.PI, R2UnitSphere.getCircleArea(1.0), 0.0);
    Assert.assertEquals(Math.PI * 100.0, R2UnitSphere.getCircleArea(10.0), 0.0);
    Assert.assertEquals(12.5664, R2UnitSphere.getCircleArea(2.0), 0.0001);
  }

  @Test
  public void testApproximationTriangleArea()
  {
    Assert.assertEquals(
      1.4142,
      R2UnitSphere.getUVSphereApproximationTriangleArea(2.0, 8),
      0.0001);
  }

  @Test
  public void testApproximationArea()
  {
    final double ta =
      R2UnitSphere.getUVSphereApproximationTriangleArea(2.0, 8);
    Assert.assertEquals(
      8.0 * ta, R2UnitSphere.getUVSphereApproximationArea(2.0, 8), 0.0001);
  }

  @Test
  public void testApproximationScaleFactor()
  {
    final double ca = R2UnitSphere.getCircleArea(2.0);
    final double aa = R2UnitSphere.getUVSphereApproximationArea(2.0, 8);

    Assert.assertEquals(
      ca / aa,
      R2UnitSphere.getUVSphereApproximationScaleFactor(2.0, 8), 0.0001);
    Assert.assertEquals(
      1.1107,
      R2UnitSphere.getUVSphereApproximationScaleFactor(2.0, 8), 0.0001);
  }

  @Test
  public void testApproximationTriangleInteriorAngle()
  {
    Assert.assertEquals(
      2.0 * Math.PI, R2UnitSphere.getUVSphereTriangleInteriorAngle(1), 0.0001);
    Assert.assertEquals(
      Math.PI, R2UnitSphere.getUVSphereTriangleInteriorAngle(2), 0.0001);
    Assert.assertEquals(
      Math.PI / 2.0, R2UnitSphere.getUVSphereTriangleInteriorAngle(4), 0.0001);
    Assert.assertEquals(
      Math.PI / 4.0, R2UnitSphere.getUVSphereTriangleInteriorAngle(8), 0.0001);
  }
}
