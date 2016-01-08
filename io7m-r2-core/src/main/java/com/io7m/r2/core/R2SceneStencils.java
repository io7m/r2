/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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

import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jnull.NullCheck;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of the {@link R2SceneStencilsType} interface.
 */

public final class R2SceneStencils implements R2SceneStencilsType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2SceneStencils.class);
  }

  private final Long2ReferenceOpenHashMap<R2InstanceSingleType> instances;
  private final ObjectArrayList<R2InstanceSingleType>
    instances_sorted;

  private R2SceneStencilsMode mode;

  private R2SceneStencils()
  {
    this.instances = new Long2ReferenceOpenHashMap<>(1024);
    this.instances_sorted = new ObjectArrayList<>();
    this.mode = R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_NEGATIVE;
  }

  /**
   * @return A new set of masking instances
   */

  public static R2SceneStencilsType newMasks()
  {
    return new R2SceneStencils();
  }

  @Override
  public void stencilsReset()
  {
    this.instances.clear();
  }

  @Override
  public void stencilsSetMode(final R2SceneStencilsMode m)
  {
    this.mode = NullCheck.notNull(m);
  }

  @Override
  public R2SceneStencilsMode stencilsGetMode()
  {
    return this.mode;
  }

  @Override
  public long stencilsCount()
  {
    return (long) this.instances.size();
  }

  @Override
  public void stencilsAddSingle(
    final R2InstanceSingleType i)
  {
    NullCheck.notNull(i);

    final long i_id = i.getInstanceID();

    this.instances.put(i_id, i);
    if (R2SceneStencils.LOG.isTraceEnabled()) {
      R2SceneStencils.LOG.trace(
        "stencil add single-mesh (instance {})",
        Long.valueOf(i_id));
    }
  }

  @Override
  public void stencilsExecute(
    final R2SceneStencilsConsumerType c)
  {
    c.onStart();

    /**
     * Sort the instances by their array object instances, to allow
     * for rendering with the fewest number of array object binds.
     */

    this.instances_sorted.clear();
    for (final long i_id : this.instances.keySet()) {
      final R2InstanceSingleType i = this.instances.get(i_id);
      this.instances_sorted.add(i);
    }

    this.instances_sorted.sort((a, b) -> {
      final JCGLArrayObjectUsableType ao = a.getArrayObject();
      final JCGLArrayObjectUsableType bo = b.getArrayObject();
      final int ac = Integer.compare(ao.getGLName(), bo.getGLName());
      if (ac == 0) {
        return Long.compare(a.getInstanceID(), b.getInstanceID());
      }
      return ac;
    });

    int current_array = -1;
    for (int index = 0; index < this.instances_sorted.size(); ++index) {
      final R2InstanceSingleType i = this.instances_sorted.get(index);
      final JCGLArrayObjectUsableType array_object = i.getArrayObject();
      final int next_array = array_object.getGLName();
      if (next_array != current_array) {
        c.onInstanceSingleStartArray(i);
      }
      current_array = next_array;
      c.onInstanceSingle(i);
    }

    c.onFinish();
  }
}
