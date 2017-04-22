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

import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.r2.core.R2ProjectionFOV;
import com.io7m.r2.core.R2ProjectionFrustum;
import com.io7m.r2.core.R2ProjectionMesh;
import com.io7m.r2.core.R2ProjectionMeshType;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2ProjectionMeshContract extends R2JCGLContract
{
  @Test
  public final void testUsage()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final R2ProjectionFOV p = R2ProjectionFOV.createWith(
      (float) Math.toRadians(90.0),
      1.0f,
      0.01f,
      1000.0f);

    final R2ProjectionMeshType m =
      R2ProjectionMesh.create(
        g,
        p,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW);

    Assert.assertFalse(m.isDeleted());
    Assert.assertFalse(m.arrayObject().isDeleted());
    Assert.assertFalse(m.isUpdateRequired());
    Assert.assertEquals(p, m.projectionReadable());
    Assert.assertEquals(p, m.projectionWritable());

    p.projectionSetZFar(2.0f);
    Assert.assertTrue(m.isUpdateRequired());

    m.updateProjection(g.arrayBuffers());
    Assert.assertFalse(m.isUpdateRequired());

    m.delete(g);
    Assert.assertTrue(m.isDeleted());
  }

  @Test
  public final void testFrustumValues()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final R2ProjectionFrustum p =
      R2ProjectionFrustum.createWith(
        -2.0f,
        2.0f,
        -1.0f,
        1.0f,
        1.0f,
        5.0f);

    final R2ProjectionMeshType m =
      R2ProjectionMesh.create(
        g,
        p,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW);

    Assert.assertFalse(m.isDeleted());
    Assert.assertFalse(m.arrayObject().isDeleted());
    Assert.assertFalse(m.isUpdateRequired());
    Assert.assertEquals(p, m.projectionReadable());
    Assert.assertEquals(p, m.projectionWritable());

    p.projectionSetZFar(2.0f);
    Assert.assertTrue(m.isUpdateRequired());

    m.updateProjection(g.arrayBuffers());
    Assert.assertFalse(m.isUpdateRequired());

    m.delete(g);
    Assert.assertTrue(m.isDeleted());
  }
}
