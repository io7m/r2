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

import com.io7m.jaffirm.core.Invariants;
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jcanephora.core.JCGLArrayBufferType;
import com.io7m.jcanephora.core.JCGLArrayObjectBuilderType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLBufferUpdateType;
import com.io7m.jcanephora.core.JCGLBufferUpdates;
import com.io7m.jcanephora.core.JCGLException;
import com.io7m.jcanephora.core.JCGLScalarType;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLArrayBuffersType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.r2.spaces.R2SpaceWorldType;

import java.nio.FloatBuffer;

/**
 * <p>The default implementation of the {@link R2InstanceBatchedDynamicType}
 * interface.</p>
 *
 * <p>This implementation manages a fixed size set of at most {@code N}
 * instances. It stores per-instance positions/scales/orientations in a vertex
 * buffer object, and uses an "orphaning" strategy where the entire buffer
 * storage is reallocated and populated on each call to {@link
 * #update(JCGLInterfaceGL33Type)}.</p>
 */

public final class R2InstanceBillboardedDynamic
  implements R2InstanceBillboardedDynamicType
{
  private final long instance_id;
  private final int max_size;
  private final JCGLArrayBufferType data_vbo;
  private final JCGLArrayObjectType data_vao;
  private final JCGLBufferUpdateType<JCGLArrayBufferType> update_vbo;
  private int used;

  private R2InstanceBillboardedDynamic(
    final JCGLArrayBuffersType g_ab,
    final JCGLArrayObjectsType g_ao,
    final long in_id,
    final int count)
  {
    NullCheck.notNull(g_ab, "Array buffers");
    NullCheck.notNull(g_ao, "Array objects");

    Preconditions.checkPreconditionI(
      count,
      count > 0,
      c -> "Count " + c + " must be positive");

    this.instance_id = in_id;
    this.max_size = count;
    this.used = 0;

    JCGLArrayBufferType vbo = null;
    JCGLArrayObjectType vao = null;

    try {

      /*
       * Allocate a buffer to store one world position, a scalar scale,
       * and a scalar rotation per instance.
       */

      long vertex_size = 3L * 4L;
      vertex_size += 4L;
      vertex_size += 4L;

      vbo = g_ab.arrayBufferAllocate(
        vertex_size * (long) count, JCGLUsageHint.USAGE_DYNAMIC_DRAW);
      g_ab.arrayBufferUnbind();

      final JCGLArrayObjectBuilderType aob = g_ao.arrayObjectNewBuilder();
      aob.setStrictChecking(true);

      long offset = 0L;
      aob.setAttributeFloatingPoint(
        R2AttributeConventions.POSITION_ATTRIBUTE_INDEX,
        vbo,
        3,
        JCGLScalarType.TYPE_FLOAT,
        (int) vertex_size,
        offset,
        false);
      offset += 3L * 4L;
      aob.setAttributeFloatingPoint(
        R2AttributeConventions.BILLBOARDED_ATTRIBUTE_SCALE_INDEX,
        vbo,
        1,
        JCGLScalarType.TYPE_FLOAT,
        (int) vertex_size,
        offset,
        false);
      offset += 4L;
      aob.setAttributeFloatingPoint(
        R2AttributeConventions.BILLBOARDED_ATTRIBUTE_ROTATION_INDEX,
        vbo,
        1,
        JCGLScalarType.TYPE_FLOAT,
        (int) vertex_size,
        offset,
        false);
      offset += 4L;

      Invariants.checkInvariantL(
        offset, vertex_size == offset, x -> "Final offset must be correct");

      vao = g_ao.arrayObjectAllocate(aob);
      g_ao.arrayObjectUnbind();

    } catch (final JCGLException e) {
      if (vbo != null) {
        g_ab.arrayBufferDelete(vbo);
      }
      if (vao != null) {
        g_ao.arrayObjectDelete(vao);
      }
      throw e;
    }

    this.data_vbo = vbo;
    this.data_vao = vao;
    this.update_vbo = JCGLBufferUpdates.newUpdateReplacingAll(this.data_vbo);
  }

  /**
   * Construct a new batch of instances.
   *
   * @param pool  The ID pool
   * @param g_ab  An array buffer interface
   * @param g_ao  An array object interface
   * @param count The maximum number of instances in the batch
   *
   * @return A new batch
   */

  public static R2InstanceBillboardedDynamic create(
    final R2IDPoolType pool,
    final JCGLArrayBuffersType g_ab,
    final JCGLArrayObjectsType g_ao,
    final int count)
  {
    NullCheck.notNull(pool, "Pool");
    return new R2InstanceBillboardedDynamic(g_ab, g_ao, pool.freshID(), count);
  }

  @Override
  public int maximumSize()
  {
    return this.max_size;
  }

  @Override
  public void clear()
  {
    this.used = 0;
  }

  @Override
  public int addInstance(
    final PVector3D<R2SpaceWorldType> position,
    final double scale,
    final double rotation)
    throws R2ExceptionBatchIsFull
  {
    NullCheck.notNull(position, "Position");

    if (this.used >= this.max_size) {
      final StringBuilder sb = new StringBuilder(64);
      sb.append("Batch is full (capacity is ");
      sb.append(this.max_size);
      sb.append(")");
      throw new R2ExceptionBatchIsFull(sb.toString());
    }

    final FloatBuffer floats =
      this.update_vbo.data().asFloatBuffer();

    final int index = this.used;
    final int offset = Math.multiplyExact(index, 5);
    floats.put(offset + 0, (float) position.x());
    floats.put(offset + 1, (float) position.y());
    floats.put(offset + 2, (float) position.z());
    floats.put(offset + 3, (float) scale);
    floats.put(offset + 4, (float) rotation);
    this.used = Math.addExact(this.used, 1);
    return index;
  }

  @Override
  public void delete(
    final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    NullCheck.notNull(g, "G33");

    if (!this.isDeleted()) {
      final JCGLArrayObjectsType g_ao = g.arrayObjects();
      final JCGLArrayBuffersType g_ab = g.arrayBuffers();
      g_ab.arrayBufferDelete(this.data_vbo);
      g_ao.arrayObjectDelete(this.data_vao);
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.data_vao.isDeleted();
  }

  @Override
  public long instanceID()
  {
    return this.instance_id;
  }

  @Override
  public JCGLArrayObjectType arrayObject()
  {
    return this.data_vao;
  }

  @Override
  public void update(
    final JCGLInterfaceGL33Type g)
  {
    NullCheck.notNull(g, "G33");

    final JCGLArrayBuffersType g_ab = g.arrayBuffers();
    final JCGLArrayObjectsType g_ao = g.arrayObjects();

    final FloatBuffer floats =
      this.update_vbo.data().asFloatBuffer();

    for (int index = this.used; index < this.max_size; ++index) {
      final int offset = Math.multiplyExact(index, 5);
      floats.put(offset + 0, 0.0f);
      floats.put(offset + 1, 0.0f);
      floats.put(offset + 2, 0.0f);
      floats.put(offset + 3, 0.0f);
      floats.put(offset + 4, 0.0f);
    }

    g_ao.arrayObjectBind(this.data_vao);
    g_ab.arrayBufferBind(this.data_vbo);
    g_ab.arrayBufferReallocate(this.data_vbo);
    g_ab.arrayBufferUpdate(this.update_vbo);
    g_ao.arrayObjectUnbind();
  }

  @Override
  public int enabledCount()
  {
    return this.used;
  }
}
