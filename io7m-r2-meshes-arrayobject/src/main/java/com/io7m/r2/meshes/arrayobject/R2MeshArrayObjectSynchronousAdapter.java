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

package com.io7m.r2.meshes.arrayobject;

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jcanephora.core.JCGLArrayBufferType;
import com.io7m.jcanephora.core.JCGLArrayObjectBuilderType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLBufferUpdateType;
import com.io7m.jcanephora.core.JCGLBufferUpdates;
import com.io7m.jcanephora.core.JCGLIndexBufferType;
import com.io7m.jcanephora.core.JCGLUnsignedType;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLArrayBuffersType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLIndexBuffersType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2AttributeConventions;
import com.io7m.r2.core.R2IndexBuffers;
import com.io7m.r2.core.cursors.R2VertexCursorProducerInfoType;
import com.io7m.r2.core.cursors.R2VertexCursorProducerType;
import com.io7m.r2.core.cursors.R2VertexCursorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.OptionalLong;

/**
 * The default implementation of the
 * {@link R2MeshArrayObjectSynchronousAdapterType}
 * interface.
 */

public final class R2MeshArrayObjectSynchronousAdapter implements
  R2MeshArrayObjectSynchronousAdapterType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MeshArrayObjectSynchronousAdapter.class);
  }

  private final JCGLArrayObjectsType g_ao;
  private final JCGLArrayBuffersType g_ab;
  private final JCGLIndexBuffersType g_ib;
  private final JCGLUsageHint array_usage;
  private JCGLUnsignedType index_type_actual;
  private final JCGLUsageHint index_usage;
  private final R2VertexCursorProducerType<ByteBuffer> cursor_producer;
  private final R2VertexCursorProducerInfoType cursor_info;
  private final JCGLUnsignedType index_type_minimum;
  private boolean failed;
  private JCGLArrayBufferType array_buffer;
  private JCGLIndexBufferType index_buffer;
  private JCGLArrayObjectType array_object;
  private JCGLBufferUpdateType<JCGLArrayBufferType> array_update;
  private JCGLBufferUpdateType<JCGLIndexBufferType> index_update;
  private R2VertexCursorType array_cursor;
  private Optional<Throwable> error_ex;
  private String error_message;
  private OptionalLong vertex_count;

  private R2MeshArrayObjectSynchronousAdapter(
    final JCGLArrayObjectsType in_g_ao,
    final JCGLArrayBuffersType in_g_ab,
    final JCGLIndexBuffersType in_g_ib,
    final JCGLUsageHint in_array_usage,
    final JCGLUnsignedType in_index_type,
    final JCGLUsageHint in_index_usage,
    final R2VertexCursorProducerInfoType in_cursor_info,
    final R2VertexCursorProducerType<ByteBuffer> in_cursor_producer)
  {
    this.g_ao = NullCheck.notNull(in_g_ao);
    this.g_ab = NullCheck.notNull(in_g_ab);
    this.g_ib = NullCheck.notNull(in_g_ib);
    this.array_usage = NullCheck.notNull(in_array_usage);
    this.index_type_minimum = NullCheck.notNull(in_index_type);
    this.index_usage = NullCheck.notNull(in_index_usage);
    this.cursor_info = NullCheck.notNull(in_cursor_info);
    this.cursor_producer = NullCheck.notNull(in_cursor_producer);
    this.vertex_count = OptionalLong.empty();
    this.failed = false;
  }

  /**
   * Construct a new adapter.
   *
   * @param in_ao              An array objects interface
   * @param in_ab              An array buffers interface
   * @param in_ib              An index buffers interface
   * @param in_array_usage     The usage hint for the created array buffer
   * @param in_index_type      The smallest allowed type for index buffer
   *                           indices
   * @param in_index_usage     The usage hint for the created index buffer
   * @param in_cursor_info     Information for created cursors
   * @param in_cursor_producer A cursor producer
   *
   * @return A new adapter
   *
   * @see com.io7m.r2.core.R2IndexBuffers#getTypeForCount(JCGLUnsignedType,
   * long)
   */

  public static R2MeshArrayObjectSynchronousAdapterType newAdapter(
    final JCGLArrayObjectsType in_ao,
    final JCGLArrayBuffersType in_ab,
    final JCGLIndexBuffersType in_ib,
    final JCGLUsageHint in_array_usage,
    final JCGLUnsignedType in_index_type,
    final JCGLUsageHint in_index_usage,
    final R2VertexCursorProducerInfoType in_cursor_info,
    final R2VertexCursorProducerType<ByteBuffer> in_cursor_producer)
  {
    return new R2MeshArrayObjectSynchronousAdapter(
      in_ao,
      in_ab,
      in_ib,
      in_array_usage,
      in_index_type,
      in_index_usage,
      in_cursor_info,
      in_cursor_producer);
  }

  @Override
  public void onEventStart()
  {

  }

  @Override
  public void onEventVertexCount(final long count)
  {
    Preconditions.checkPrecondition(
      this.array_buffer == null, "Array buffer must be null");
    Preconditions.checkPrecondition(
      this.array_object == null, "Array object must be null");
    Preconditions.checkPrecondition(
      this.index_buffer == null, "Index buffer must be null");

    this.vertex_count = OptionalLong.of(count);

    this.array_buffer =
      this.g_ab.arrayBufferAllocate(
        count * this.cursor_info.vertexSize(),
        this.array_usage);
    this.g_ab.arrayBufferUnbind();

    this.array_update =
      JCGLBufferUpdates.newUpdateReplacingAll(this.array_buffer);
    this.array_cursor =
      this.cursor_producer.newCursor(this.array_update.getData());
  }

  @Override
  public void onEventTriangleCount(final long count)
  {
    NullCheck.notNull(
      this.array_buffer, "Array buffer");
    Preconditions.checkPrecondition(
      this.array_object == null, "Array object must be null");
    Preconditions.checkPrecondition(
      this.index_buffer == null, "Index buffer must be null");
    Preconditions.checkPrecondition(
      this.vertex_count.isPresent(), "Vertex count must have been received");

    this.index_type_actual =
      R2IndexBuffers.getTypeForCount(
        this.index_type_minimum, this.vertex_count.getAsLong());

    this.index_buffer =
      this.g_ib.indexBufferAllocate(
        count * 3L, this.index_type_actual, this.index_usage);
    this.g_ib.indexBufferUnbind();

    final JCGLArrayObjectBuilderType aob = this.g_ao.arrayObjectNewBuilder();
    aob.setAttributeFloatingPoint(
      R2AttributeConventions.POSITION_ATTRIBUTE_INDEX,
      this.array_buffer,
      3,
      this.cursor_info.positionElementType(),
      (int) this.cursor_info.vertexSize(),
      this.cursor_info.positionOffset(),
      false);
    aob.setAttributeFloatingPoint(
      R2AttributeConventions.UV_ATTRIBUTE_INDEX,
      this.array_buffer,
      2,
      this.cursor_info.uvElementType(),
      (int) this.cursor_info.vertexSize(),
      this.cursor_info.uvOffset(),
      false);
    aob.setAttributeFloatingPoint(
      R2AttributeConventions.NORMAL_ATTRIBUTE_INDEX,
      this.array_buffer,
      3,
      this.cursor_info.normalElementType(),
      (int) this.cursor_info.vertexSize(),
      this.cursor_info.normalOffset(),
      false);
    aob.setAttributeFloatingPoint(
      R2AttributeConventions.TANGENT4_ATTRIBUTE_INDEX,
      this.array_buffer,
      4,
      this.cursor_info.tangent4ElementType(),
      (int) this.cursor_info.vertexSize(),
      this.cursor_info.tangent4Offset(),
      false);
    aob.setIndexBuffer(this.index_buffer);
    this.array_object = this.g_ao.arrayObjectAllocate(aob);

    this.index_update =
      JCGLBufferUpdates.newUpdateReplacingAll(this.index_buffer);
  }

  @Override
  public void onEventVertexStarted(
    final long index)
  {

  }

  @Override
  public void onEventVertexPosition(
    final long index,
    final double x,
    final double y,
    final double z)
  {
    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "[{}] position: {} {} {}",
        Long.valueOf(index),
        Double.valueOf(x),
        Double.valueOf(y),
        Double.valueOf(z));
    }

    this.array_cursor.setPosition(index, x, y, z);
  }

  @Override
  public void onEventVertexNormal(
    final long index,
    final double x,
    final double y,
    final double z)
  {
    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "[{}] normal: {} {} {}",
        Long.valueOf(index),
        Double.valueOf(x),
        Double.valueOf(y),
        Double.valueOf(z));
    }

    this.array_cursor.setNormal(index, x, y, z);
  }

  @Override
  public void onEventVertexTangent(
    final long index,
    final double x,
    final double y,
    final double z,
    final double w)
  {
    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "[{}] tangent: {} {} {}",
        Long.valueOf(index),
        Double.valueOf(x),
        Double.valueOf(y),
        Double.valueOf(z));
    }

    this.array_cursor.setTangent4(index, x, y, z, w);
  }

  @Override
  public void onEventVertexUV(
    final long index,
    final double x,
    final double y)
  {
    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "[{}] uv: {} {}",
        Long.valueOf(index),
        Double.valueOf(x),
        Double.valueOf(y));
    }

    this.array_cursor.setUV(index, x, y);
  }

  @Override
  public void onEventVertexFinished(
    final long index)
  {

  }

  @Override
  public void onEventVerticesFinished()
  {
    NullCheck.notNull(this.array_buffer, "Array buffer");
    NullCheck.notNull(this.index_buffer, "Index buffer");
    NullCheck.notNull(this.array_object, "Array object");

    this.g_ab.arrayBufferBind(this.array_buffer);
    this.g_ab.arrayBufferUpdate(this.array_update);
    this.g_ab.arrayBufferUnbind();
  }

  @Override
  public void onEventTriangle(
    final long index,
    final long v0,
    final long v1,
    final long v2)
  {
    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "[{}] triangle: {} {} {}",
        Long.valueOf(index),
        Long.valueOf(v0),
        Long.valueOf(v1),
        Long.valueOf(v2));
    }

    final ByteBuffer data = this.index_update.getData();
    switch (this.index_type_actual) {
      case TYPE_UNSIGNED_BYTE: {
        final long offset = index * 3L;
        final byte v0i = (byte) (v0 & 0xffL);
        final byte v1i = (byte) (v1 & 0xffL);
        final byte v2i = (byte) (v2 & 0xffL);
        data.put((int) offset, v0i);
        data.put((int) offset + 1, v1i);
        data.put((int) offset + 2, v2i);
        break;
      }
      case TYPE_UNSIGNED_INT: {
        final long offset = index * (3L * 4L);
        final int v0i = (int) (v0 & 0xffffffffL);
        final int v1i = (int) (v1 & 0xffffffffL);
        final int v2i = (int) (v2 & 0xffffffffL);
        data.putInt((int) offset, v0i);
        data.putInt((int) offset + 4, v1i);
        data.putInt((int) offset + 8, v2i);
        break;
      }
      case TYPE_UNSIGNED_SHORT: {
        final long offset = index * (3L * 2L);
        final short v0i = (short) (v0 & 0xffffL);
        final short v1i = (short) (v1 & 0xffffL);
        final short v2i = (short) (v2 & 0xffffL);
        data.putShort((int) offset, v0i);
        data.putShort((int) offset + 2, v1i);
        data.putShort((int) offset + 4, v2i);
        break;
      }
    }
  }

  @Override
  public void onEventTrianglesFinished()
  {
    NullCheck.notNull(this.array_buffer, "Array buffer");
    NullCheck.notNull(this.index_buffer, "Index buffer");
    NullCheck.notNull(this.array_object, "Array object");

    this.g_ao.arrayObjectBind(this.array_object);
    this.g_ib.indexBufferUpdate(this.index_update);
  }

  @Override
  public void onEventFinished()
  {
    if (this.array_object != null) {
      this.g_ao.arrayObjectUnbind();
    }
  }

  @Override
  public boolean hasFailed()
  {
    return this.failed;
  }

  @Override
  public JCGLArrayBufferType arrayBuffer()
  {
    if (this.failed) {
      throw new IllegalStateException("Parsing failed");
    }
    return this.array_buffer;
  }

  @Override
  public JCGLIndexBufferType indexBuffer()
  {
    if (this.failed) {
      throw new IllegalStateException("Parsing failed");
    }
    return this.index_buffer;
  }

  @Override
  public JCGLArrayObjectType arrayObject()
  {
    if (this.failed) {
      throw new IllegalStateException("Parsing failed");
    }
    return this.array_object;
  }

  @Override
  public Optional<Throwable> errorException()
  {
    if (!this.failed) {
      throw new IllegalStateException("Parsing did not fail");
    }
    return this.error_ex;
  }

  @Override
  public String errorMessage()
  {
    if (!this.failed) {
      throw new IllegalStateException("Parsing did not fail");
    }
    return this.error_message;
  }

  @Override
  public void onError(
    final Optional<Throwable> e,
    final String message)
  {
    this.failed = true;
    this.error_ex = e;
    this.error_message = message;

    try {
      if ((this.array_buffer != null) && (!this.array_buffer.isDeleted())) {
        this.g_ab.arrayBufferDelete(this.array_buffer);
      }
      if ((this.index_buffer != null) && (!this.index_buffer.isDeleted())) {
        this.g_ib.indexBufferDelete(this.index_buffer);
      }
      if ((this.array_object != null) && (!this.array_object.isDeleted())) {
        this.g_ao.arrayObjectDelete(this.array_object);
      }
    } finally {
      this.array_buffer = null;
      this.index_buffer = null;
      this.array_object = null;
    }
  }
}
