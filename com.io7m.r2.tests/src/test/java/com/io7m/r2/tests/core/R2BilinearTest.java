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

import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.r2.core.R2Bilinear;
import org.junit.Assert;
import org.junit.Test;

public final class R2BilinearTest
{
  @Test
  public void testBilinear0()
  {
    final Vector3D x0y0 = Vector3D.of(0.0, 0.0, 0.0);
    final Vector3D x1y0 = Vector3D.of(1.0, 0.0, 0.0);
    final Vector3D x0y1 = Vector3D.of(0.0, 1.0, 0.0);
    final Vector3D x1y1 = Vector3D.of(1.0, 1.0, 0.0);
    final float px = 0.0f;
    final float py = 0.0f;

    final Vector3D out = R2Bilinear.bilinear3F(
      x0y0, x1y0, x0y1, x1y1, (double) px, (double) py);

    Assert.assertEquals(0.0, out.x(), 0.0);
    Assert.assertEquals(0.0, out.y(), 0.0);
    Assert.assertEquals(0.0, out.z(), 0.0);
  }

  @Test
  public void testBilinear1()
  {
    final Vector3D x0y0 = Vector3D.of(0.0, 0.0, 0.0);
    final Vector3D x1y0 = Vector3D.of(1.0, 0.0, 0.0);
    final Vector3D x0y1 = Vector3D.of(0.0, 1.0, 0.0);
    final Vector3D x1y1 = Vector3D.of(1.0, 1.0, 0.0);
    final float px = 0.5f;
    final float py = 0.5f;

    final Vector3D out = R2Bilinear.bilinear3F(
      x0y0, x1y0, x0y1, x1y1, (double) px, (double) py);

    Assert.assertEquals(0.5, out.x(), 0.0);
    Assert.assertEquals(0.5, out.y(), 0.0);
    Assert.assertEquals(0.0, out.z(), 0.0);
  }

  @Test
  public void testBilinear2()
  {
    final Vector3D x0y0 = Vector3D.of(0.0, 0.0, 0.0);
    final Vector3D x1y0 = Vector3D.of(1.0, 0.0, 0.0);
    final Vector3D x0y1 = Vector3D.of(0.0, 1.0, 0.0);
    final Vector3D x1y1 = Vector3D.of(1.0, 1.0, 0.0);
    final float px = 1.0f;
    final float py = 0.0f;


    final Vector3D out = R2Bilinear.bilinear3F(
      x0y0, x1y0, x0y1, x1y1, (double) px, (double) py);

    Assert.assertEquals(1.0, out.x(), 0.0);
    Assert.assertEquals(0.0, out.y(), 0.0);
    Assert.assertEquals(0.0, out.z(), 0.0);
  }
}
