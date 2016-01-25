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

import com.io7m.jcanephora.core.JCGLArrayVertexAttributeFloatingPointType;
import com.io7m.jcanephora.core.JCGLScalarType;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.r2.core.R2UnitQuad;
import com.io7m.r2.core.R2UnitQuadType;
import com.io7m.r2.core.cursors.R2VertexPUNT16ByteBuffered;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2UnitQuadContract extends R2JCGLContract
{
  @Test
  public final void testDelete()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2UnitQuadType uq = R2UnitQuad.newUnitQuad(g);
    Assert.assertFalse(uq.isDeleted());

    uq.delete(g);
    Assert.assertTrue(uq.isDeleted());
    Assert.assertTrue(uq.getArrayObject().isDeleted());

    uq.delete(g);
    Assert.assertTrue(uq.isDeleted());
    Assert.assertTrue(uq.getArrayObject().isDeleted());
  }

  @Test
  public final void testNew()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final R2UnitQuadType uq = R2UnitQuad.newUnitQuad(g);
    Assert.assertFalse(uq.isDeleted());

    long size = 0L;
    size += (long) R2VertexPUNT16ByteBuffered.sizeInOctets() * 4L;
    size += 6L * 2L;

    Assert.assertEquals(size, uq.getRange().getInterval());

    {
      final JCGLArrayVertexAttributeFloatingPointType a =
        (JCGLArrayVertexAttributeFloatingPointType)
          uq.getArrayObject().getAttributeAt(0).get();
      Assert.assertEquals(3L, (long) a.getElements());
      Assert.assertEquals(0L, (long) a.getIndex());
      Assert.assertEquals(
        (long) R2VertexPUNT16ByteBuffered.metaPositionStaticOffsetFromType(),
        a.getOffset());
      Assert.assertEquals(
        (long) R2VertexPUNT16ByteBuffered.sizeInOctets(),
        (long) a.getStride());
      Assert.assertEquals(JCGLScalarType.TYPE_HALF_FLOAT, a.getType());
      Assert.assertFalse(a.isNormalized());
    }

    {
      final JCGLArrayVertexAttributeFloatingPointType a =
        (JCGLArrayVertexAttributeFloatingPointType)
          uq.getArrayObject().getAttributeAt(1).get();
      Assert.assertEquals(2L, (long) a.getElements());
      Assert.assertEquals(1L, (long) a.getIndex());
      Assert.assertEquals(
        (long) R2VertexPUNT16ByteBuffered.metaUvStaticOffsetFromType(),
        a.getOffset());
      Assert.assertEquals(
        (long) R2VertexPUNT16ByteBuffered.sizeInOctets(),
        (long) a.getStride());
      Assert.assertEquals(JCGLScalarType.TYPE_HALF_FLOAT, a.getType());
      Assert.assertFalse(a.isNormalized());
    }

    {
      final JCGLArrayVertexAttributeFloatingPointType a =
        (JCGLArrayVertexAttributeFloatingPointType)
          uq.getArrayObject().getAttributeAt(2).get();
      Assert.assertEquals(3L, (long) a.getElements());
      Assert.assertEquals(2L, (long) a.getIndex());
      Assert.assertEquals(
        (long) R2VertexPUNT16ByteBuffered.metaNormalStaticOffsetFromType(),
        a.getOffset());
      Assert.assertEquals(
        (long) R2VertexPUNT16ByteBuffered.sizeInOctets(),
        (long) a.getStride());
      Assert.assertEquals(JCGLScalarType.TYPE_HALF_FLOAT, a.getType());
      Assert.assertFalse(a.isNormalized());
    }

    {
      final JCGLArrayVertexAttributeFloatingPointType a =
        (JCGLArrayVertexAttributeFloatingPointType)
          uq.getArrayObject().getAttributeAt(3).get();
      Assert.assertEquals(4L, (long) a.getElements());
      Assert.assertEquals(3L, (long) a.getIndex());
      Assert.assertEquals(
        (long) R2VertexPUNT16ByteBuffered.metaTangentStaticOffsetFromType(),
        a.getOffset());
      Assert.assertEquals(
        (long) R2VertexPUNT16ByteBuffered.sizeInOctets(),
        (long) a.getStride());
      Assert.assertEquals(JCGLScalarType.TYPE_HALF_FLOAT, a.getType());
      Assert.assertFalse(a.isNormalized());
    }
  }
}
