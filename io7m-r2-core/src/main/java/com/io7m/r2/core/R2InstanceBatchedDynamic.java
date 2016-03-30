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
import com.io7m.jcanephora.core.JCGLException;
import com.io7m.jcanephora.core.JCGLScalarType;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLArrayBuffersType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.bytebuffered.parameterized.PMatrixByteBuffered4x4FType;
import com.io7m.jtensors.bytebuffered.parameterized.PMatrixByteBufferedM4x4F;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import org.valid4j.Assertive;

/**
 * <p>The default implementation of the {@link R2InstanceBatchedDynamicType}
 * interface.</p>
 *
 * <p>This implementation manages a fixed size batch of {@code N} instances. It
 * stores per-instance matrices in a vertex buffer object, and uses an
 * "orphaning" strategy where the entire buffer storage is reallocated and
 * populated on each call to {@link #update(JCGLInterfaceGL33Type,
 * R2TransformContextType)}.</p>
 */

public final class R2InstanceBatchedDynamic implements
  R2InstanceBatchedDynamicType
{
  private final long                                      instance_id;
  private final R2TransformOrthogonalReadableType[]       members;
  private final IntSortedSet                              free;
  private final int                                       max_size;
  private final JCGLArrayBufferType                       matrix_vbo;
  private final JCGLArrayObjectType                       matrix_vao;
  private final JCGLBufferUpdateType<JCGLArrayBufferType> update_vbo;

  private final PMatrixByteBuffered4x4FType<R2SpaceObjectType,
    R2SpaceWorldType> matrix_pointer;

  private R2InstanceBatchedDynamic(
    final JCGLArrayBuffersType g_ab,
    final JCGLArrayObjectsType g_ao,
    final JCGLArrayObjectUsableType o,
    final long in_id,
    final int count)
  {
    NullCheck.notNull(g_ab);
    NullCheck.notNull(g_ao);
    NullCheck.notNull(o);
    Assertive.require(count > 0, "Count must be positive");

    this.instance_id = in_id;
    this.max_size = count;
    this.free = new IntAVLTreeSet();
    this.members = new R2TransformOrthogonalReadableType[count];

    for (int index = 0; index < count; ++index) {
      this.free.add(index);
    }

    JCGLArrayBufferType vbo = null;
    JCGLArrayObjectType vao = null;

    try {

      /**
       * Allocate a buffer to store one model matrix per instance.
       */

      vbo = g_ab.arrayBufferAllocate(
        16L * 4L * (long) count, JCGLUsageHint.USAGE_DYNAMIC_DRAW);
      g_ab.arrayBufferUnbind();

      final JCGLArrayObjectBuilderType aob =
        g_ao.arrayObjectNewBuilderFromObject(o);

      o.getIndexBufferBound().ifPresent(aob::setIndexBuffer);
      aob.setStrictChecking(true);

      final int stride = 16 * 4;
      long offset = 0L;
      aob.setAttributeFloatingPointWithDivisor(
        R2AttributeConventions.BATCHED_MODEL_MATRIX_COLUMN_0_ATTRIBUTE_INDEX,
        vbo,
        4,
        JCGLScalarType.TYPE_FLOAT,
        stride,
        offset,
        false,
        1);
      offset += 4L * 4L;
      aob.setAttributeFloatingPointWithDivisor(
        R2AttributeConventions.BATCHED_MODEL_MATRIX_COLUMN_1_ATTRIBUTE_INDEX,
        vbo,
        4,
        JCGLScalarType.TYPE_FLOAT,
        stride,
        offset,
        false,
        1);
      offset += 4L * 4L;
      aob.setAttributeFloatingPointWithDivisor(
        R2AttributeConventions.BATCHED_MODEL_MATRIX_COLUMN_2_ATTRIBUTE_INDEX,
        vbo,
        4,
        JCGLScalarType.TYPE_FLOAT,
        stride,
        offset,
        false,
        1);
      offset += 4L * 4L;
      aob.setAttributeFloatingPointWithDivisor(
        R2AttributeConventions.BATCHED_MODEL_MATRIX_COLUMN_3_ATTRIBUTE_INDEX,
        vbo,
        4,
        JCGLScalarType.TYPE_FLOAT,
        stride,
        offset,
        false,
        1);

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

    this.matrix_vbo = vbo;
    this.matrix_vao = vao;
    this.update_vbo = JCGLBufferUpdates.newUpdateReplacingAll(this.matrix_vbo);
    this.matrix_pointer =
      PMatrixByteBufferedM4x4F.newMatrixFromByteBuffer(
        this.update_vbo.getData(), 0L);
  }

  /**
   * Construct a new batch of instances.
   *
   * @param pool  The ID pool
   * @param g_ab  An array buffer interface
   * @param g_ao  An array object interface
   * @param o     An existing array object
   * @param count The maximum number of instances in the batch
   *
   * @return A new batch
   */

  public static R2InstanceBatchedDynamicType newBatch(
    final R2IDPoolType pool,
    final JCGLArrayBuffersType g_ab,
    final JCGLArrayObjectsType g_ao,
    final JCGLArrayObjectUsableType o,
    final int count)
  {
    NullCheck.notNull(pool);
    return new R2InstanceBatchedDynamic(
      g_ab, g_ao, o, pool.getFreshID(), count);
  }

  @Override
  public int getMaximumSize()
  {
    return this.max_size;
  }

  @Override
  public int getEnabledCount()
  {
    return this.max_size - this.free.size();
  }

  @Override
  public int getRenderCount()
  {
    return this.max_size;
  }

  @Override
  public void disableAll()
  {
    for (int index = 0; index < this.max_size; ++index) {
      this.remove(index);
    }
  }

  private void remove(final int index)
  {
    this.members[index] = null;
    this.free.add(index);
  }

  @Override
  public int enableInstance(
    final R2TransformOrthogonalReadableType t)
  {
    NullCheck.notNull(t);

    if (this.getEnabledCount() == this.max_size) {
      final StringBuilder sb = new StringBuilder(64);
      sb.append("Batch is full (capacity is ");
      sb.append(this.max_size);
      sb.append(")");
      throw new R2ExceptionBatchIsFull(sb.toString());
    }

    Assertive.require(!this.free.isEmpty());
    final int next = this.free.firstInt();
    this.members[next] = t;
    this.free.remove(next);
    return next;
  }

  @Override
  public void disableInstance(final int id)
  {
    this.remove(id);
  }

  @Override
  public void update(
    final JCGLInterfaceGL33Type g,
    final R2TransformContextType context)
  {
    NullCheck.notNull(context);
    NullCheck.notNull(g);

    final JCGLArrayBuffersType g_ab = g.getArrayBuffers();
    final JCGLArrayObjectsType g_ao = g.getArrayObjects();

    long offset = 0L;
    for (int index = 0; index < this.max_size; ++index) {
      this.matrix_pointer.setByteOffset(offset);
      final R2TransformOrthogonalReadableType trans = this.members[index];

      if (trans != null) {
        trans.transformMakeMatrix4x4F(context, this.matrix_pointer);
      } else {

        /**
         * "Disable" the instance by specifying that it be drawn with
         * a scale of zero.
         */

        this.matrix_pointer.setR0C0F(0.0f);
        this.matrix_pointer.setR1C1F(0.0f);
        this.matrix_pointer.setR2C2F(0.0f);
        this.matrix_pointer.setR3C3F(1.0f);
      }

      offset += 16L * 4L;
    }

    g_ao.arrayObjectBind(this.matrix_vao);
    g_ab.arrayBufferBind(this.matrix_vbo);
    g_ab.arrayBufferReallocate(this.matrix_vbo);
    g_ab.arrayBufferUpdate(this.update_vbo);
    g_ao.arrayObjectUnbind();
  }

  @Override
  public JCGLArrayObjectType getArrayObject()
  {
    return this.matrix_vao;
  }

  @Override
  public long getInstanceID()
  {
    return this.instance_id;
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    NullCheck.notNull(g);

    if (!this.isDeleted()) {
      final JCGLArrayObjectsType g_ao = g.getArrayObjects();
      final JCGLArrayBuffersType g_ab = g.getArrayBuffers();
      g_ab.arrayBufferDelete(this.matrix_vbo);
      g_ao.arrayObjectDelete(this.matrix_vao);
      this.disableAll();
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.matrix_vao.isDeleted();
  }
}
