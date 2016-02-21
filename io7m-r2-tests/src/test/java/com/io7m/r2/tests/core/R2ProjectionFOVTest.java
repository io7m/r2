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
import com.io7m.r2.core.R2ProjectionFOV;
import org.junit.Assert;
import org.junit.Test;

public final class R2ProjectionFOVTest
{
  @Test
  public void testValues()
  {
    final JCGLProjectionMatricesType pm =
      JCGLProjectionMatrices.newMatrices();
    final R2ProjectionFOV p =
      R2ProjectionFOV.newFrustumWith(
        pm,
        (float) Math.toRadians(90.0f),
        1.0f,
        1.0f,
        100.0f);

    p.projectionSetZFar(100.0f);
    p.projectionSetZNear(1.0f);

    Assert.assertEquals(1.0f, p.projectionGetNearXMaximum(), 0.0f);
    Assert.assertEquals(-1.0f, p.projectionGetNearXMinimum(), 0.0f);
    Assert.assertEquals(1.0f, p.projectionGetNearYMaximum(), 0.0f);
    Assert.assertEquals(-1.0f, p.projectionGetNearYMinimum(), 0.0f);
    Assert.assertEquals(1.0f, p.projectionGetZNear(), 0.0f);

    Assert.assertEquals(100.0f, p.projectionGetFarXMaximum(), 0.0f);
    Assert.assertEquals(-100.0f, p.projectionGetFarXMinimum(), 0.0f);
    Assert.assertEquals(100.0f, p.projectionGetFarYMaximum(), 0.0f);
    Assert.assertEquals(-100.0f, p.projectionGetFarYMinimum(), 0.0f);
    Assert.assertEquals(100.0f, p.projectionGetZFar(), 0.0f);

    p.projectionSetZFar(200.0f);

    Assert.assertEquals(200.0f, p.projectionGetFarXMaximum(), 0.0f);
    Assert.assertEquals(-200.0f, p.projectionGetFarXMinimum(), 0.0f);
    Assert.assertEquals(200.0f, p.projectionGetFarYMaximum(), 0.0f);
    Assert.assertEquals(-200.0f, p.projectionGetFarYMinimum(), 0.0f);
    Assert.assertEquals(200.0f, p.projectionGetZFar(), 0.0f);
  }
}
