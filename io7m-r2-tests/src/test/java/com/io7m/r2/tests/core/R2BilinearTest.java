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

import com.io7m.jtensors.VectorI3F;
import com.io7m.jtensors.VectorM3F;
import com.io7m.r2.core.R2Bilinear;
import org.junit.Assert;
import org.junit.Test;

public final class R2BilinearTest
{
  @Test
  public void testBilinear0()
  {
    final VectorI3F x0y0 = new VectorI3F(0.0f, 0.0f, 0.0f);
    final VectorI3F x1y0 = new VectorI3F(1.0f, 0.0f, 0.0f);
    final VectorI3F x0y1 = new VectorI3F(0.0f, 1.0f, 0.0f);
    final VectorI3F x1y1 = new VectorI3F(1.0f, 1.0f, 0.0f);
    final float px = 0.0f;
    final float py = 0.0f;

    final VectorM3F.ContextVM3F c = new VectorM3F.ContextVM3F();
    final VectorM3F temp_0 = new VectorM3F();
    final VectorM3F temp_1 = new VectorM3F();
    final VectorM3F out = new VectorM3F();

    R2Bilinear.bilinear3F(
      c, x0y0, x1y0, x0y1, x1y1, px, py, temp_0, temp_1, out);

    Assert.assertEquals(0.0f, out.getXF(), 0.0f);
    Assert.assertEquals(0.0f, out.getYF(), 0.0f);
    Assert.assertEquals(0.0f, out.getZF(), 0.0f);
  }

  @Test public void testBilinear1()
  {
    final VectorI3F x0y0 = new VectorI3F(0.0f, 0.0f, 0.0f);
    final VectorI3F x1y0 = new VectorI3F(1.0f, 0.0f, 0.0f);
    final VectorI3F x0y1 = new VectorI3F(0.0f, 1.0f, 0.0f);
    final VectorI3F x1y1 = new VectorI3F(1.0f, 1.0f, 0.0f);
    final float px = 0.5f;
    final float py = 0.5f;

    final VectorM3F.ContextVM3F c = new VectorM3F.ContextVM3F();
    final VectorM3F temp_0 = new VectorM3F();
    final VectorM3F temp_1 = new VectorM3F();
    final VectorM3F out = new VectorM3F();

    R2Bilinear.bilinear3F(
      c, x0y0, x1y0, x0y1, x1y1, px, py, temp_0, temp_1, out);

    Assert.assertEquals(0.5f, out.getXF(), 0.0f);
    Assert.assertEquals(0.5f, out.getYF(), 0.0f);
    Assert.assertEquals(0.0f, out.getZF(), 0.0f);
  }

  @Test public void testBilinear2()
  {
    final VectorI3F x0y0 = new VectorI3F(0.0f, 0.0f, 0.0f);
    final VectorI3F x1y0 = new VectorI3F(1.0f, 0.0f, 0.0f);
    final VectorI3F x0y1 = new VectorI3F(0.0f, 1.0f, 0.0f);
    final VectorI3F x1y1 = new VectorI3F(1.0f, 1.0f, 0.0f);
    final float px = 1.0f;
    final float py = 0.0f;

    final VectorM3F.ContextVM3F c = new VectorM3F.ContextVM3F();
    final VectorM3F temp_0 = new VectorM3F();
    final VectorM3F temp_1 = new VectorM3F();
    final VectorM3F out = new VectorM3F();

    R2Bilinear.bilinear3F(
      c, x0y0, x1y0, x0y1, x1y1, px, py, temp_0, temp_1, out);

    Assert.assertEquals(1.0f, out.getXF(), 0.0f);
    Assert.assertEquals(0.0f, out.getYF(), 0.0f);
    Assert.assertEquals(0.0f, out.getZF(), 0.0f);
  }
}
