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

package com.io7m.r2.core.debug;

import com.io7m.jcanephora.core.JCGLArrayBufferType;
import com.io7m.jcanephora.core.JCGLArrayObjectBuilderType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.JCGLBufferUpdateType;
import com.io7m.jcanephora.core.JCGLBufferUpdates;
import com.io7m.jcanephora.core.JCGLIndexBufferType;
import com.io7m.jcanephora.core.JCGLScalarType;
import com.io7m.jcanephora.core.JCGLUnsignedType;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLArrayBuffersType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLIndexBuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedChecked;
import com.io7m.jpra.runtime.java.JPRACursor1DType;
import com.io7m.jtensors.ieee754b16.Vector3Db16Type;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2AttributeConventions;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.cursors.R2VertexP16ByteBuffered;
import com.io7m.r2.core.cursors.R2VertexP16Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * The default implementation of the {@link R2DebugCubeType} interface.
 */

public final class R2DebugCube implements R2DebugCubeType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2DebugCube.class);
  }

  private final JCGLArrayObjectType array_object;
  private final JCGLArrayBufferType array;
  private final JCGLIndexBufferType index;
  private final UnsignedRangeInclusiveL range;
  private boolean deleted;

  private R2DebugCube(
    final JCGLArrayObjectType in_array_object,
    final JCGLArrayBufferType in_array,
    final JCGLIndexBufferType in_index)
  {
    this.array = NullCheck.notNull(in_array);
    this.array_object = NullCheck.notNull(in_array_object);
    this.index = NullCheck.notNull(in_index);
    this.deleted = false;

    long size = 0L;
    size += this.array.getRange().getInterval();
    size += this.index.getRange().getInterval();
    this.range = new UnsignedRangeInclusiveL(0L, size - 1L);
  }

  /**
   * Allocate a new debug cube.
   *
   * @param g An OpenGL interface
   *
   * @return A new debug cube
   */

  public static R2DebugCubeType newDebugCube(
    final JCGLInterfaceGL33Type g)
  {
    LOG.debug("allocating debug cube");

    final JCGLArrayBuffersType g_ab = g.getArrayBuffers();
    final JCGLIndexBuffersType g_ib = g.getIndexBuffers();
    final JCGLArrayObjectsType g_ao = g.getArrayObjects();

    final JCGLArrayBufferType a;
    final JCGLIndexBufferType i;
    final JCGLArrayObjectType ao;

    /*
     * Allocate and populate array buffer.
     */

    {
      a =
        g_ab.arrayBufferAllocate(
          8L * (long) R2VertexP16ByteBuffered.sizeInOctets(),
          JCGLUsageHint.USAGE_STATIC_DRAW);

      final JCGLBufferUpdateType<JCGLArrayBufferType> u =
        JCGLBufferUpdates.newUpdateReplacingAll(a);
      final ByteBuffer d = u.getData();

      final JPRACursor1DType<R2VertexP16Type> c =
        JPRACursor1DByteBufferedChecked.newCursor(
          d,
          R2VertexP16ByteBuffered::newValueWithOffset);
      final R2VertexP16Type v = c.getElementView();
      final Vector3Db16Type pc = v.getPositionWritable();

      double y = 0.5;
      c.setElementIndex(0);
      pc.set3D(-0.5, y, -0.5);
      c.setElementIndex(1);
      pc.set3D(-0.5, y, 0.5);
      c.setElementIndex(2);
      pc.set3D(0.5, y, 0.5);
      c.setElementIndex(3);
      pc.set3D(0.5, y, -0.5);

      y = -0.5;
      c.setElementIndex(4);
      pc.set3D(-0.5, y, -0.5);
      c.setElementIndex(5);
      pc.set3D(-0.5, y, 0.5);
      c.setElementIndex(6);
      pc.set3D(0.5, y, 0.5);
      c.setElementIndex(7);
      pc.set3D(0.5, y, -0.5);

      g_ab.arrayBufferUpdate(u);
      g_ab.arrayBufferUnbind();
    }

    /*
     * Allocate and populate index buffer.
     */

    {
      i =
        g_ib.indexBufferAllocate(
          24L,
          JCGLUnsignedType.TYPE_UNSIGNED_SHORT,
          JCGLUsageHint.USAGE_STATIC_DRAW);

      final JCGLBufferUpdateType<JCGLIndexBufferType> u =
        JCGLBufferUpdates.newUpdateReplacingAll(i);
      final ShortBuffer d = u.getData().asShortBuffer();

      /* Top ring */

      d.put(0, (short) 0);
      d.put(1, (short) 1);

      d.put(2, (short) 1);
      d.put(3, (short) 2);

      d.put(4, (short) 2);
      d.put(5, (short) 3);

      d.put(6, (short) 3);
      d.put(7, (short) 0);

      /* Bottom ring */

      d.put(8, (short) 4);
      d.put(9, (short) 5);

      d.put(10, (short) 5);
      d.put(11, (short) 6);

      d.put(12, (short) 6);
      d.put(13, (short) 7);

      d.put(14, (short) 7);
      d.put(15, (short) 4);

      /* Struts */

      d.put(16, (short) 0);
      d.put(17, (short) 4);

      d.put(18, (short) 1);
      d.put(19, (short) 5);

      d.put(20, (short) 2);
      d.put(21, (short) 6);

      d.put(22, (short) 3);
      d.put(23, (short) 7);

      g_ib.indexBufferUpdate(u);
      g_ib.indexBufferUnbind();
    }

    /*
     * Allocate and configure array object.
     */

    {
      final JCGLArrayObjectBuilderType aob = g_ao.arrayObjectNewBuilder();
      aob.setIndexBuffer(i);
      aob.setAttributeFloatingPoint(
        R2AttributeConventions.POSITION_ATTRIBUTE_INDEX,
        a,
        3,
        JCGLScalarType.TYPE_HALF_FLOAT,
        R2VertexP16ByteBuffered.sizeInOctets(),
        (long) R2VertexP16ByteBuffered.metaPositionStaticOffsetFromType(),
        false);

      ao = g_ao.arrayObjectAllocate(aob);
      g_ao.arrayObjectUnbind();
    }

    return new R2DebugCube(ao, a, i);
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    if (!this.isDeleted()) {
      try {
        LOG.debug("delete");
        final JCGLArrayBuffersType g_ab = g.getArrayBuffers();
        final JCGLIndexBuffersType g_ib = g.getIndexBuffers();
        final JCGLArrayObjectsType g_ao = g.getArrayObjects();
        g_ao.arrayObjectDelete(this.array_object);
        g_ab.arrayBufferDelete(this.array);
        g_ib.indexBufferDelete(this.index);
      } finally {
        this.deleted = true;
      }
    }
  }

  @Override
  public JCGLArrayObjectUsableType arrayObject()
  {
    return this.array_object;
  }

  @Override
  public UnsignedRangeInclusiveL getRange()
  {
    return this.range;
  }

  @Override
  public boolean isDeleted()
  {
    return this.deleted;
  }
}
