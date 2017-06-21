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

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jcanephora.core.JCGLArrayBufferType;
import com.io7m.jcanephora.core.JCGLArrayObjectBuilderType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.JCGLBufferUpdateType;
import com.io7m.jcanephora.core.JCGLBufferUpdates;
import com.io7m.jcanephora.core.JCGLScalarType;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLArrayBuffersType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedChecked;
import com.io7m.jpra.runtime.java.JPRACursor1DType;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.jtensors.storage.api.unparameterized.vectors.VectorStorageFloating3Type;
import com.io7m.r2.core.R2DeletableType;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.cursors.R2RGBA8WritableType;
import com.io7m.r2.core.cursors.R2VertexP32RGBA8ByteBuffered;
import com.io7m.r2.core.cursors.R2VertexP32RGBA8Type;
import com.io7m.r2.spaces.R2SpaceRGBAType;
import com.io7m.r2.spaces.R2SpaceWorldType;

import java.nio.ByteBuffer;
import java.util.List;

import static com.io7m.r2.meshes.api.R2MeshAttributeConventions.POSITION_ATTRIBUTE_INDEX;

/**
 * A batcher for line segments.
 */

public final class R2DebugLineSegmentsBatch implements R2DeletableType
{
  private final JCGLArrayBuffersType g33_array_buffers;
  private final JCGLArrayObjectsType g33_array_objects;
  private long space_available;
  private JCGLArrayBufferType array;
  private JCGLArrayObjectType array_object;
  private boolean deleted;

  /**
   * Construct a new empty batch.
   *
   * @param in_g A GL interface
   */

  public R2DebugLineSegmentsBatch(
    final JCGLInterfaceGL33Type in_g)
  {
    final JCGLInterfaceGL33Type g33 = NullCheck.notNull(in_g, "GL33");
    this.g33_array_buffers = g33.arrayBuffers();
    this.g33_array_objects = g33.arrayObjects();
    this.space_available = 0L;
  }

  /**
   * @return The usable array object for the batch
   */

  public JCGLArrayObjectUsableType arrayObject()
  {
    Preconditions.checkPrecondition(
      this.array_object != null,
      "Line segments must have been provided");
    return this.array_object;
  }

  /**
   * Set the line segments and create any necessary batches on the GPU.
   *
   * @param segments The line segments
   */

  public void setLineSegments(
    final List<R2DebugLineSegment> segments)
  {
    NullCheck.notNull(segments, "Segments");

    final long space_required = Math.multiplyExact(
      (long) segments.size() * 2L,
      (long) R2VertexP32RGBA8ByteBuffered.sizeInOctets());

    if (this.space_available < space_required) {
      this.deleteActual();

      /*
       * Allocate array buffer.
       */

      this.array = this.g33_array_buffers.arrayBufferAllocate(
        space_required,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW);
      this.space_available = space_required;

      /*
       * Allocate and configure array object.
       */

      {
        final JCGLArrayObjectBuilderType aob =
          this.g33_array_objects.arrayObjectNewBuilder();
        aob.setAttributeFloatingPoint(
          POSITION_ATTRIBUTE_INDEX,
          this.array,
          3,
          JCGLScalarType.TYPE_FLOAT,
          R2VertexP32RGBA8ByteBuffered.sizeInOctets(),
          (long) R2VertexP32RGBA8ByteBuffered.metaPositionStaticOffsetFromType(),
          false);
        aob.setAttributeFloatingPoint(
          POSITION_ATTRIBUTE_INDEX + 1,
          this.array,
          4,
          JCGLScalarType.TYPE_UNSIGNED_BYTE,
          R2VertexP32RGBA8ByteBuffered.sizeInOctets(),
          (long) R2VertexP32RGBA8ByteBuffered.metaColorStaticOffsetFromType(),
          true);

        this.array_object = this.g33_array_objects.arrayObjectAllocate(aob);
        this.g33_array_objects.arrayObjectUnbind();
      }
    }

    if (space_required == 0L) {
      return;
    }

    /*
     * Populate array buffer.
     */

    {
      final JCGLBufferUpdateType<JCGLArrayBufferType> u =
        JCGLBufferUpdates.newUpdateReplacingAll(this.array);
      final ByteBuffer d = u.data();

      final JPRACursor1DType<R2VertexP32RGBA8Type> c =
        JPRACursor1DByteBufferedChecked.newCursor(
          d,
          R2VertexP32RGBA8ByteBuffered::newValueWithOffset);
      final R2VertexP32RGBA8Type v = c.getElementView();
      final VectorStorageFloating3Type pc = v.getPositionWritable();
      final R2RGBA8WritableType cc = v.getColorWritable();

      int element = 0;
      for (int index = 0; index < segments.size(); ++index) {
        final R2DebugLineSegment segment = segments.get(index);
        final PVector3D<R2SpaceWorldType> from = segment.from();
        final PVector4D<R2SpaceRGBAType> from_color = segment.fromColor();
        final PVector3D<R2SpaceWorldType> to = segment.to();
        final PVector4D<R2SpaceRGBAType> to_color = segment.toColor();

        c.setElementIndex(element);
        pc.setXYZ(from.x(), from.y(), from.z());

        {
          final int r = (int) (from_color.x() * 255.0);
          final int g = (int) (from_color.y() * 255.0);
          final int b = (int) (from_color.z() * 255.0);
          final int a = (int) (from_color.w() * 255.0);
          cc.setColorR((byte) (r & 0xff));
          cc.setColorG((byte) (g & 0xff));
          cc.setColorB((byte) (b & 0xff));
          cc.setColorA((byte) (a & 0xff));
        }

        c.setElementIndex(element + 1);
        pc.setXYZ(to.x(), to.y(), to.z());

        {
          final int r = (int) (to_color.x() * 255.0);
          final int g = (int) (to_color.y() * 255.0);
          final int b = (int) (to_color.z() * 255.0);
          final int a = (int) (to_color.w() * 255.0);
          cc.setColorR((byte) (r & 0xff));
          cc.setColorG((byte) (g & 0xff));
          cc.setColorB((byte) (b & 0xff));
          cc.setColorA((byte) (a & 0xff));
        }

        element = Math.addExact(element, 2);
      }

      this.g33_array_buffers.arrayBufferBind(this.array);
      this.g33_array_buffers.arrayBufferUpdate(u);
      this.g33_array_buffers.arrayBufferUnbind();
    }
  }

  private void deleteActual()
  {
    if (this.array != null) {
      this.g33_array_buffers.arrayBufferDelete(this.array);
      this.array = null;
    }
    if (this.array_object != null) {
      this.g33_array_objects.arrayObjectDelete(this.array_object);
      this.array_object = null;
    }
  }

  @Override
  public void delete(
    final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    NullCheck.notNull(g, "GL33");

    try {
      this.deleteActual();
    } finally {
      this.deleted = true;
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.deleted;
  }
}
