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

package com.io7m.r2.core;

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
import com.io7m.r2.core.cursors.R2VertexCursorP32UNT16;
import com.io7m.r2.core.cursors.R2VertexP32UNT16ByteBuffered;
import com.io7m.r2.core.cursors.R2VertexP32UNT16Type;

import java.nio.ByteBuffer;

import static com.io7m.r2.meshes.api.R2MeshAttributeConventions.NORMAL_ATTRIBUTE_INDEX;
import static com.io7m.r2.meshes.api.R2MeshAttributeConventions.POSITION_ATTRIBUTE_INDEX;
import static com.io7m.r2.meshes.api.R2MeshAttributeConventions.TANGENT4_ATTRIBUTE_INDEX;
import static com.io7m.r2.meshes.api.R2MeshAttributeConventions.UV_ATTRIBUTE_INDEX;

/**
 * A mesh representing the convex hull for a given projection.
 */

public final class R2ProjectionMesh implements R2ProjectionMeshType
{
  private final JCGLArrayObjectType array_object;
  private final JCGLArrayBufferType array_buffer;
  private final JCGLIndexBufferType index_buffer;
  private final JCGLBufferUpdateType<JCGLArrayBufferType> array_update;
  private final JPRACursor1DType<R2VertexP32UNT16Type> array_cursor;
  private final R2ProjectionType projection;
  private boolean mesh_needs_update;

  private R2ProjectionMesh(
    final R2ProjectionType in_p,
    final JCGLArrayObjectType in_array_object,
    final JCGLArrayBufferType in_array_buffer,
    final JCGLBufferUpdateType<JCGLArrayBufferType> in_array_update,
    final JPRACursor1DType<R2VertexP32UNT16Type> in_array_cursor,
    final JCGLIndexBufferType in_index_buffer)
  {
    this.projection =
      NullCheck.notNull(in_p, "Projection");
    this.array_object =
      NullCheck.notNull(in_array_object, "Array object");
    this.array_buffer =
      NullCheck.notNull(in_array_buffer, "Array buffer");
    this.index_buffer =
      NullCheck.notNull(in_index_buffer, "Index buffer");
    this.array_update =
      NullCheck.notNull(in_array_update, "Array update");
    this.array_cursor =
      NullCheck.notNull(in_array_cursor, "Array cursor");

    this.projection.projectionGetWatchable()
      .watchableAdd(p -> this.mesh_needs_update = true);
  }

  /**
   * Construct a mesh based on the given projection.
   *
   * @param g          A GL interface
   * @param p          The initial projection
   * @param array_hint The usage hint for the array buffer that backs the mesh
   * @param index_hint The usage hint for the index buffer that backs the mesh
   *                   indices
   *
   * @return A new mesh
   */

  public static R2ProjectionMesh create(
    final JCGLInterfaceGL33Type g,
    final R2ProjectionType p,
    final JCGLUsageHint array_hint,
    final JCGLUsageHint index_hint)
  {
    NullCheck.notNull(g, "G33");
    NullCheck.notNull(p, "Projection");
    NullCheck.notNull(array_hint, "Array hint");
    NullCheck.notNull(index_hint, "Indices hint");

    final JCGLArrayBuffersType ga = g.arrayBuffers();
    final JCGLArrayObjectsType go = g.arrayObjects();
    final JCGLIndexBuffersType gi = g.indexBuffers();

    final JCGLArrayBufferType ab;
    final JCGLIndexBufferType ib;
    final JCGLArrayObjectType ao;
    final JCGLBufferUpdateType<JCGLArrayBufferType> array_update;
    final JPRACursor1DType<R2VertexP32UNT16Type> array_cursor;

    go.arrayObjectUnbind();

    /*
     * Allocate array buffer.
     */

    {
      final R2VertexCursorP32UNT16 vci =
        R2VertexCursorP32UNT16.getInstance();

      final long array_size = vci.vertexSize() * 8L;
      ab = ga.arrayBufferAllocate(array_size, array_hint);
      array_update =
        JCGLBufferUpdates.newUpdateReplacingAll(ab);
      array_cursor =
        JPRACursor1DByteBufferedChecked.newCursor(
          array_update.data(),
          R2VertexP32UNT16ByteBuffered::newValueWithOffset);
    }

    /*
     * Allocate index buffer.
     */

    {
      final long face_count = 6L;
      final long tri_count = face_count * 2L;
      final long ind_count = tri_count * 3L;

      ib = gi.indexBufferAllocate(
        ind_count, JCGLUnsignedType.TYPE_UNSIGNED_SHORT, index_hint);
      final JCGLBufferUpdateType<JCGLIndexBufferType> index_update =
        JCGLBufferUpdates.newUpdateReplacingAll(ib);
      final ByteBuffer d =
        index_update.data();

      /*
        Front side triangles.
       */

      triangle(d, 1, 0, 3);
      triangle(d, 1, 3, 2);

      /*
        Left side triangles.
       */

      triangle(d, 5, 4, 0);
      triangle(d, 5, 0, 1);

      /*
        Right side triangles.
       */

      triangle(d, 6, 3, 7);
      triangle(d, 6, 2, 3);

      /*
        Top side triangles.
       */

      triangle(d, 5, 1, 2);
      triangle(d, 5, 2, 6);

      /*
        Bottom side triangles.
       */

      triangle(d, 4, 3, 0);
      triangle(d, 4, 7, 3);

      /*
        Back side triangles.
       */

      triangle(d, 5, 7, 4);
      triangle(d, 6, 7, 5);

      d.rewind();
      gi.indexBufferUpdate(index_update);
      gi.indexBufferUnbind();
    }

    /*
      Allocate and configure array object.
     */

    {
      final JCGLArrayObjectBuilderType aob = go.arrayObjectNewBuilder();
      aob.setIndexBuffer(ib);
      aob.setAttributeFloatingPoint(
        POSITION_ATTRIBUTE_INDEX,
        ab,
        3,
        JCGLScalarType.TYPE_FLOAT,
        R2VertexP32UNT16ByteBuffered.sizeInOctets(),
        (long) R2VertexP32UNT16ByteBuffered.metaPositionStaticOffsetFromType(),
        false);
      aob.setAttributeFloatingPoint(
        UV_ATTRIBUTE_INDEX,
        ab,
        2,
        JCGLScalarType.TYPE_HALF_FLOAT,
        R2VertexP32UNT16ByteBuffered.sizeInOctets(),
        (long) R2VertexP32UNT16ByteBuffered.metaUvStaticOffsetFromType(),
        false);
      aob.setAttributeFloatingPoint(
        NORMAL_ATTRIBUTE_INDEX,
        ab,
        3,
        JCGLScalarType.TYPE_HALF_FLOAT,
        R2VertexP32UNT16ByteBuffered.sizeInOctets(),
        (long) R2VertexP32UNT16ByteBuffered.metaNormalStaticOffsetFromType(),
        false);
      aob.setAttributeFloatingPoint(
        TANGENT4_ATTRIBUTE_INDEX,
        ab,
        4,
        JCGLScalarType.TYPE_HALF_FLOAT,
        R2VertexP32UNT16ByteBuffered.sizeInOctets(),
        (long) R2VertexP32UNT16ByteBuffered.metaTangentStaticOffsetFromType(),
        false);

      ao = go.arrayObjectAllocate(aob);
      go.arrayObjectUnbind();
    }

    final R2ProjectionMesh m =
      new R2ProjectionMesh(p, ao, ab, array_update, array_cursor, ib);

    m.update(ga);
    return m;
  }

  private static void triangle(
    final ByteBuffer d,
    final int v0,
    final int v1,
    final int v2)
  {
    d.putShort((short) (v0 & 0xffff));
    d.putShort((short) (v1 & 0xffff));
    d.putShort((short) (v2 & 0xffff));
  }

  /**
   * @return The array object for the mesh
   */

  @Override
  public JCGLArrayObjectUsableType arrayObject()
  {
    return this.array_object;
  }

  @Override
  public R2ProjectionReadableType projectionReadable()
  {
    return this.projection;
  }

  @Override
  public boolean isUpdateRequired()
  {
    return this.mesh_needs_update;
  }

  private void update(
    final JCGLArrayBuffersType ga)
  {
    NullCheck.notNull(ga, "Array buffers");

    final R2VertexP32UNT16Type v = this.array_cursor.getElementView();
    final VectorStorageFloating3Type pc = v.getPositionWritable();
    final VectorStorageFloating2Type uc = v.getUvWritable();

    final R2ProjectionType p = this.projection;
    final double near_x_min = p.projectionGetNearXMinimum();
    final double near_x_max = p.projectionGetNearXMaximum();
    final double near_y_min = p.projectionGetNearYMinimum();
    final double near_y_max = p.projectionGetNearYMaximum();

    final double far_x_min = p.projectionGetFarXMinimum();
    final double far_x_max = p.projectionGetFarXMaximum();
    final double far_y_min = p.projectionGetFarYMinimum();
    final double far_y_max = p.projectionGetFarYMaximum();

    final double near_z = -p.projectionGetZNear();
    final double far_z = -p.projectionGetZFar();

    this.array_cursor.setElementIndex(0);
    uc.setXY(0.0, 0.0);
    pc.setXYZ(near_x_min, near_y_min, near_z);
    this.array_cursor.setElementIndex(1);
    uc.setXY(0.0, 1.0);
    pc.setXYZ(near_x_min, near_y_max, near_z);
    this.array_cursor.setElementIndex(2);
    uc.setXY(1.0, 1.0);
    pc.setXYZ(near_x_max, near_y_max, near_z);
    this.array_cursor.setElementIndex(3);
    uc.setXY(1.0, 0.0);
    pc.setXYZ(near_x_max, near_y_min, near_z);

    this.array_cursor.setElementIndex(4);
    uc.setXY(0.0, 0.0);
    pc.setXYZ(far_x_min, far_y_min, far_z);
    this.array_cursor.setElementIndex(5);
    uc.setXY(0.0, 1.0);
    pc.setXYZ(far_x_min, far_y_max, far_z);
    this.array_cursor.setElementIndex(6);
    uc.setXY(1.0, 1.0);
    pc.setXYZ(far_x_max, far_y_max, far_z);
    this.array_cursor.setElementIndex(7);
    uc.setXY(1.0, 0.0);
    pc.setXYZ(far_x_max, far_y_min, far_z);

    ga.arrayBufferBind(this.array_buffer);
    ga.arrayBufferUpdate(this.array_update);
    ga.arrayBufferUnbind();
    this.mesh_needs_update = false;
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    NullCheck.notNull(g, "G33");

    if (!this.isDeleted()) {
      final JCGLArrayBuffersType ga = g.arrayBuffers();
      final JCGLArrayObjectsType go = g.arrayObjects();
      final JCGLIndexBuffersType gi = g.indexBuffers();
      ga.arrayBufferDelete(this.array_buffer);
      gi.indexBufferDelete(this.index_buffer);
      go.arrayObjectDelete(this.array_object);
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.array_buffer.isDeleted();
  }

  @Override
  public void updateProjection(
    final JCGLArrayBuffersType ga)
  {
    NullCheck.notNull(ga, "Array buffers");
    this.update(ga);
  }

  @Override
  public R2ProjectionType projectionWritable()
  {
    return this.projection;
  }
}
