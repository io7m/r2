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

package com.io7m.r2.tests.core.debug;

import com.io7m.jcanephora.core.JCGLArrayVertexAttributeFloatingPointType;
import com.io7m.jcanephora.core.JCGLScalarType;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.r2.core.cursors.R2VertexP16ByteBuffered;
import com.io7m.r2.core.debug.R2DebugCube;
import com.io7m.r2.core.debug.R2DebugCubeType;
import com.io7m.r2.tests.core.R2JCGLContract;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2DebugCubeContract extends R2JCGLContract
{
  @Test
  public final void testDelete()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2DebugCubeType uq = R2DebugCube.create(g);
    Assert.assertFalse(uq.isDeleted());

    uq.delete(g);
    Assert.assertTrue(uq.isDeleted());
    Assert.assertTrue(uq.arrayObject().isDeleted());

    uq.delete(g);
    Assert.assertTrue(uq.isDeleted());
    Assert.assertTrue(uq.arrayObject().isDeleted());
  }

  @Test
  public final void testNew()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final R2DebugCubeType uq = R2DebugCube.create(g);
    Assert.assertFalse(uq.isDeleted());

    long size = 0L;
    size += 8L * (long) R2VertexP16ByteBuffered.sizeInOctets();
    size += 2L * 24L; /* 24 vertices in index buffer */
    Assert.assertEquals(size, uq.byteRange().getInterval());

    {
      final JCGLArrayVertexAttributeFloatingPointType a =
        (JCGLArrayVertexAttributeFloatingPointType)
          uq.arrayObject().attributeAt(0).get();
      Assert.assertEquals(3L, (long) a.elementCount());
      Assert.assertEquals(0L, (long) a.index());
      Assert.assertEquals(
        (long) R2VertexP16ByteBuffered.metaPositionStaticOffsetFromType(),
        a.offsetOctets());
      Assert.assertEquals(
        (long) R2VertexP16ByteBuffered.sizeInOctets(),
        (long) a.strideOctets());
      Assert.assertEquals(JCGLScalarType.TYPE_HALF_FLOAT, a.type());
      Assert.assertFalse(a.isNormalized());
    }
  }
}
