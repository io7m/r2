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

package com.io7m.r2.instances;

import com.io7m.jaffirm.core.Preconditions;
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
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.storage.bytebuffered.PMatrixByteBuffered4x4Type;
import com.io7m.jtensors.storage.bytebuffered.PMatrixByteBuffered4x4s32;
import com.io7m.mutable.numbers.core.MutableLong;
import com.io7m.r2.core.api.R2Exception;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.meshes.api.R2MeshAttributeConventions;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import com.io7m.r2.transforms.R2TransformOrthogonalReadableType;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;

import java.util.function.Consumer;

/**
 * <p>The default implementation of the {@link R2InstanceBatchedDynamicType}
 * interface.</p>
 *
 * <p>This implementation manages a fixed size batch of {@code N} instances. It
 * stores per-instance matrices in a vertex buffer object, and uses an
 * "orphaning" strategy where the entire buffer storage is reallocated and
 * populated on each call to {@link #update(JCGLInterfaceGL33Type)}.</p>
 */

public final class R2InstanceBatchedDynamic
  implements R2InstanceBatchedDynamicType
{
  private final long instance_id;
  private final R2TransformOrthogonalReadableType[] members;
  private final IntSortedSet free;
  private final int max_size;
  private final JCGLArrayBufferType matrix_vbo;
  private final JCGLArrayObjectType matrix_vao;
  private final JCGLBufferUpdateType<JCGLArrayBufferType> update_vbo;
  private final MutableLong index;
  private final PMatrixByteBuffered4x4Type<R2SpaceObjectType, R2SpaceWorldType> matrix_pointer;
  private final Consumer<R2TransformOrthogonalReadableType> update_consumer;
  private boolean update_required;

  private R2InstanceBatchedDynamic(
    final JCGLArrayBuffersType g_ab,
    final JCGLArrayObjectsType g_ao,
    final JCGLArrayObjectUsableType o,
    final long in_id,
    final int count)
  {
    NullCheck.notNull(g_ab, "Array buffers");
    NullCheck.notNull(g_ao, "Array objects");
    NullCheck.notNull(o, "Array object");

    Preconditions.checkPreconditionI(
      count,
      count > 0,
      c -> "Count " + c + " must be positive");

    this.instance_id = in_id;
    this.max_size = count;
    this.free = new IntAVLTreeSet();
    this.members = new R2TransformOrthogonalReadableType[count];

    for (int i = 0; i < count; ++i) {
      this.free.add(i);
    }

    JCGLArrayBufferType vbo = null;
    JCGLArrayObjectType vao = null;

    try {

      /*
        Allocate a buffer to store one model matrix per instance.
       */

      vbo = g_ab.arrayBufferAllocate(
        16L * 4L * (long) count, JCGLUsageHint.USAGE_DYNAMIC_DRAW);
      g_ab.arrayBufferUnbind();

      final JCGLArrayObjectBuilderType aob =
        g_ao.arrayObjectNewBuilderFromObject(o);

      o.indexBufferBound().ifPresent(aob::setIndexBuffer);
      aob.setStrictChecking(true);

      final int stride = 16 * 4;
      long offset = 0L;
      aob.setAttributeFloatingPointWithDivisor(
        R2MeshAttributeConventions.batchedModelMatrixColumn0AttributeIndex(),
        vbo,
        4,
        JCGLScalarType.TYPE_FLOAT,
        stride,
        offset,
        false,
        1);
      offset += 4L * 4L;
      aob.setAttributeFloatingPointWithDivisor(
        R2MeshAttributeConventions.batchedModelMatrixColumn1AttributeIndex(),
        vbo,
        4,
        JCGLScalarType.TYPE_FLOAT,
        stride,
        offset,
        false,
        1);
      offset += 4L * 4L;
      aob.setAttributeFloatingPointWithDivisor(
        R2MeshAttributeConventions.batchedModelMatrixColumn2AttributeIndex(),
        vbo,
        4,
        JCGLScalarType.TYPE_FLOAT,
        stride,
        offset,
        false,
        1);
      offset += 4L * 4L;
      aob.setAttributeFloatingPointWithDivisor(
        R2MeshAttributeConventions.batchedModelMatrixColumn3AttributeIndex(),
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
    this.index = MutableLong.create();
    this.update_required = true;
    this.update_consumer = transform -> this.update_required = true;

    this.matrix_pointer =
      PMatrixByteBuffered4x4s32.createWithBase(
        this.update_vbo.data(), this.index, 0);
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

  public static R2InstanceBatchedDynamic create(
    final R2IDPoolType pool,
    final JCGLArrayBuffersType g_ab,
    final JCGLArrayObjectsType g_ao,
    final JCGLArrayObjectUsableType o,
    final int count)
  {
    NullCheck.notNull(pool, "Pool");
    return new R2InstanceBatchedDynamic(g_ab, g_ao, o, pool.freshID(), count);
  }

  @Override
  public int maximumSize()
  {
    return this.max_size;
  }

  @Override
  public int enabledCount()
  {
    return this.max_size - this.free.size();
  }

  @Override
  public int renderCount()
  {
    return this.max_size;
  }

  @Override
  public void disableAll()
  {
    for (int i = 0; i < this.max_size; ++i) {
      this.remove(i);
    }
  }

  private void remove(final int i)
  {
    this.members[i].transformOrthogonalGetWatchable().watchableRemove(this.update_consumer);
    this.members[i] = null;
    this.free.add(i);
    this.update_required = true;
  }

  @Override
  public int enableInstance(
    final R2TransformOrthogonalReadableType t)
  {
    NullCheck.notNull(t, "Transform");

    if (this.enabledCount() == this.max_size) {
      final StringBuilder sb = new StringBuilder(64);
      sb.append("Batch is full (capacity is ");
      sb.append(this.max_size);
      sb.append(")");
      throw new R2ExceptionInstanceBatchIsFull(sb.toString());
    }

    Preconditions.checkPrecondition(
      !this.free.isEmpty(), "Free set must not be empty");

    final int next = this.free.firstInt();
    this.members[next] = t;
    t.transformOrthogonalGetWatchable().watchableAdd(this.update_consumer);
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
    final JCGLInterfaceGL33Type g)
  {
    NullCheck.notNull(g, "G33");

    final JCGLArrayBuffersType g_ab = g.arrayBuffers();
    final JCGLArrayObjectsType g_ao = g.arrayObjects();

    long offset = 0L;
    for (int i = 0; i < this.max_size; ++i) {
      this.index.setValue(offset);
      final R2TransformOrthogonalReadableType trans = this.members[i];

      final PMatrix4x4D<R2SpaceObjectType, R2SpaceWorldType> m;
      if (trans != null) {
        m = trans.transformMakeMatrix4x4F();
      } else {

        /*
         * "Disable" the instance by specifying that it be drawn with
         * a scale of zero.
         */

        m = PMatrices4x4D.zero();
      }

      this.matrix_pointer.setPMatrix4x4D(m);
      offset += 16L * 4L;
    }

    g_ao.arrayObjectBind(this.matrix_vao);
    g_ab.arrayBufferBind(this.matrix_vbo);
    g_ab.arrayBufferReallocate(this.matrix_vbo);
    g_ab.arrayBufferUpdate(this.update_vbo);
    g_ao.arrayObjectUnbind();

    this.update_required = false;
  }

  @Override
  public boolean updateRequired()
  {
    return this.update_required;
  }

  @Override
  public JCGLArrayObjectType arrayObject()
  {
    return this.matrix_vao;
  }

  @Override
  public long instanceID()
  {
    return this.instance_id;
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    NullCheck.notNull(g, "G33");

    if (!this.isDeleted()) {
      final JCGLArrayObjectsType g_ao = g.arrayObjects();
      final JCGLArrayBuffersType g_ab = g.arrayBuffers();
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
