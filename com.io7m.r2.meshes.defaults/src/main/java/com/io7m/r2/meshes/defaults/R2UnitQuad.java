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

package com.io7m.r2.meshes.defaults;

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
import com.io7m.jtensors.storage.api.unparameterized.vectors.VectorStorageFloating2Type;
import com.io7m.jtensors.storage.api.unparameterized.vectors.VectorStorageFloating3Type;
import com.io7m.jtensors.storage.api.unparameterized.vectors.VectorStorageFloating4Type;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.api.R2Exception;
import com.io7m.r2.cursors.vertex.R2VertexPUNT16ByteBuffered;
import com.io7m.r2.cursors.vertex.R2VertexPUNT16Type;
import com.io7m.r2.meshes.api.R2MeshAttributeConventions;
import com.io7m.r2.unit_quads.R2UnitQuadType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * The default implementation of the {@link R2UnitQuadType} interface.
 */

public final class R2UnitQuad implements R2UnitQuadType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2UnitQuad.class);
  }

  private final JCGLArrayObjectType array_object;
  private final JCGLArrayBufferType array;
  private final JCGLIndexBufferType index;
  private final UnsignedRangeInclusiveL range;
  private boolean deleted;

  private R2UnitQuad(
    final JCGLArrayObjectType in_array_object,
    final JCGLArrayBufferType in_array,
    final JCGLIndexBufferType in_index)
  {
    this.array = NullCheck.notNull(in_array, "Array buffer");
    this.array_object = NullCheck.notNull(in_array_object, "Array object");
    this.index = NullCheck.notNull(in_index, "Index buffer");
    this.deleted = false;

    long size = 0L;
    size += this.array.byteRange().getInterval();
    size += this.index.byteRange().getInterval();
    this.range = new UnsignedRangeInclusiveL(0L, size - 1L);
  }

  /**
   * Allocate a new unit quad.
   *
   * @param g An OpenGL interface
   *
   * @return A new unit quad
   */

  public static R2UnitQuadType newUnitQuad(
    final JCGLInterfaceGL33Type g)
  {
    LOG.debug("allocating unit quad");

    final JCGLArrayBuffersType g_ab = g.arrayBuffers();
    final JCGLIndexBuffersType g_ib = g.indexBuffers();
    final JCGLArrayObjectsType g_ao = g.arrayObjects();

    final JCGLArrayBufferType a;
    final JCGLIndexBufferType i;
    final JCGLArrayObjectType ao;

    g_ao.arrayObjectUnbind();

    /*
     * Allocate and populate array buffer.
     */

    {
      a =
        g_ab.arrayBufferAllocate(
          4L * (long) R2VertexPUNT16ByteBuffered.sizeInOctets(),
          JCGLUsageHint.USAGE_STATIC_DRAW);

      final JCGLBufferUpdateType<JCGLArrayBufferType> u =
        JCGLBufferUpdates.newUpdateReplacingAll(a);
      final ByteBuffer d = u.data();

      final JPRACursor1DType<R2VertexPUNT16Type> c =
        JPRACursor1DByteBufferedChecked.newCursor(
          d,
          R2VertexPUNT16ByteBuffered::newValueWithOffset);
      final R2VertexPUNT16Type v = c.getElementView();
      final VectorStorageFloating3Type pc = v.getPositionWritable();
      final VectorStorageFloating3Type nc = v.getNormalWritable();
      final VectorStorageFloating4Type tc = v.getTangentWritable();
      final VectorStorageFloating2Type uc = v.getUvWritable();

      c.setElementIndex(0);
      pc.setXYZ(-1.0, 1.0, 0.0);
      nc.setXYZ(0.0, 0.0, 1.0);
      uc.setXY(0.0, 1.0);
      tc.setXYZW(1.0, 0.0, 0.0, 1.0);

      c.setElementIndex(1);
      pc.setXYZ(-1.0, -1.0, 0.0);
      nc.setXYZ(0.0, 0.0, 1.0);
      uc.setXY(0.0, 0.0);
      tc.setXYZW(1.0, 0.0, 0.0, 1.0);

      c.setElementIndex(2);
      pc.setXYZ(1.0, -1.0, 0.0);
      nc.setXYZ(0.0, 0.0, 1.0);
      uc.setXY(1.0, 0.0);
      tc.setXYZW(1.0, 0.0, 0.0, 1.0);

      c.setElementIndex(3);
      pc.setXYZ(1.0, 1.0, 0.0);
      nc.setXYZ(0.0, 0.0, 1.0);
      uc.setXY(1.0, 1.0);
      tc.setXYZW(1.0, 0.0, 0.0, 1.0);

      g_ab.arrayBufferUpdate(u);
      g_ab.arrayBufferUnbind();
    }

    /*
     * Allocate and populate index buffer.
     */

    {
      i =
        g_ib.indexBufferAllocate(
          6L,
          JCGLUnsignedType.TYPE_UNSIGNED_SHORT,
          JCGLUsageHint.USAGE_STATIC_DRAW);

      final JCGLBufferUpdateType<JCGLIndexBufferType> u =
        JCGLBufferUpdates.newUpdateReplacingAll(i);
      final ShortBuffer d = u.data().asShortBuffer();
      d.put(0, (short) 0);
      d.put(1, (short) 1);
      d.put(2, (short) 2);
      d.put(3, (short) 0);
      d.put(4, (short) 2);
      d.put(5, (short) 3);

      g_ib.indexBufferUpdate(u);
      g_ib.indexBufferUnbind();
    }

    /*
      Allocate and configure array object.
     */

    {
      final JCGLArrayObjectBuilderType aob = g_ao.arrayObjectNewBuilder();
      aob.setIndexBuffer(i);
      aob.setAttributeFloatingPoint(
        R2MeshAttributeConventions.positionAttributeIndex(),
        a,
        3,
        JCGLScalarType.TYPE_HALF_FLOAT,
        R2VertexPUNT16ByteBuffered.sizeInOctets(),
        (long) R2VertexPUNT16ByteBuffered.metaPositionStaticOffsetFromType(),
        false);
      aob.setAttributeFloatingPoint(
        R2MeshAttributeConventions.uvAttributeIndex(),
        a,
        2,
        JCGLScalarType.TYPE_HALF_FLOAT,
        R2VertexPUNT16ByteBuffered.sizeInOctets(),
        (long) R2VertexPUNT16ByteBuffered.metaUvStaticOffsetFromType(),
        false);
      aob.setAttributeFloatingPoint(
        R2MeshAttributeConventions.normalAttributeIndex(),
        a,
        3,
        JCGLScalarType.TYPE_HALF_FLOAT,
        R2VertexPUNT16ByteBuffered.sizeInOctets(),
        (long) R2VertexPUNT16ByteBuffered.metaNormalStaticOffsetFromType(),
        false);
      aob.setAttributeFloatingPoint(
        R2MeshAttributeConventions.tangent4AttributeIndex(),
        a,
        4,
        JCGLScalarType.TYPE_HALF_FLOAT,
        R2VertexPUNT16ByteBuffered.sizeInOctets(),
        (long) R2VertexPUNT16ByteBuffered.metaTangentStaticOffsetFromType(),
        false);

      ao = g_ao.arrayObjectAllocate(aob);
      g_ao.arrayObjectUnbind();
    }

    return new R2UnitQuad(ao, a, i);
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    if (!this.isDeleted()) {
      try {
        LOG.debug("delete");
        final JCGLArrayBuffersType g_ab = g.arrayBuffers();
        final JCGLIndexBuffersType g_ib = g.indexBuffers();
        final JCGLArrayObjectsType g_ao = g.arrayObjects();
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
  public UnsignedRangeInclusiveL byteRange()
  {
    return this.range;
  }

  @Override
  public boolean isDeleted()
  {
    return this.deleted;
  }
}
