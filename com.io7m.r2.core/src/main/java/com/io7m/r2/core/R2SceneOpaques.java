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

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBillboardedUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleUsableType;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the {@link R2SceneOpaquesType} interface.
 */

public final class R2SceneOpaques implements R2SceneOpaquesType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2SceneOpaques.class);
  }

  private final Singles singles;
  private final Batches batches;
  private final Billboards billboards;

  private R2SceneOpaques()
  {
    this.singles = new Singles();
    this.batches = new Batches();
    this.billboards = new Billboards();
  }

  /**
   * @return A new empty set of opaques
   */

  public static R2SceneOpaques create()
  {
    return new R2SceneOpaques();
  }

  private static R2RendererExceptionInstanceAlreadyVisible
  errorInstanceAlreadyVisible(
    final R2InstanceType i)
  {
    final StringBuilder text = new StringBuilder(128);
    text.append("Instance is already visible.\n");
    text.append("Instance:         ");
    text.append(i.instanceID());
    return new R2RendererExceptionInstanceAlreadyVisible(text.toString());
  }

  @Override
  public void opaquesReset()
  {
    LOG.trace("reset");
    this.singles.clear();
    this.batches.clear();
    this.billboards.clear();
  }

  @Override
  public <M> void opaquesAddSingleInstanceInGroup(
    final R2InstanceSingleType i,
    final R2MaterialOpaqueSingleType<M> m,
    final int group)
  {
    NullCheck.notNull(i, "Instance");
    NullCheck.notNull(m, "Material");
    R2Stencils.checkValidGroup(group);

    final long i_id = i.instanceID();
    final long m_id = m.materialID();
    final R2ShaderInstanceSingleUsableType<?> shader = m.shader();
    final long s_id = shader.shaderID();

    /*
     * Insert the instance into the set of all single instances. Instances
     * that are already visible are rejected.
     */

    if (this.singles.instances.containsKey(i_id)) {
      throw errorInstanceAlreadyVisible(i);
    }
    this.singles.instances.put(i_id, i);

    /*
     * Insert the instance, material, and shader into the group. Add a
     * mapping from the instance to the material.
     */

    final Singles.Group g = this.singles.groups[group];

    Preconditions.checkPrecondition(
      !g.instances.containsKey(i_id),
      "Group must not contain instance");

    this.singles.group_max = Math.max(this.singles.group_max, group + 1);
    g.instances.put(i_id, i);
    g.instance_materials.put(m_id, m);
    g.instance_shaders.put(s_id, shader);

    /*
     * Update the set of mappings from materials to instances for the group.
     */

    final LongSet m_instances;
    if (g.material_to_instances.containsKey(m_id)) {
      m_instances = g.material_to_instances.get(m_id);
    } else {
      m_instances = new LongOpenHashSet();
    }
    m_instances.add(i_id);
    g.material_to_instances.put(m_id, m_instances);

    /*
     * Update the set of mappings from shaders to materials for the group.
     */

    final LongSet s_materials;
    if (g.shader_to_materials.containsKey(s_id)) {
      s_materials = g.shader_to_materials.get(s_id);
    } else {
      s_materials = new LongOpenHashSet();
    }
    s_materials.add(m_id);
    g.shader_to_materials.put(s_id, s_materials);

    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "opaque add single (instance {}, group {}, material {}, shader {})",
        Long.valueOf(i_id),
        Integer.valueOf(group),
        Long.valueOf(m_id),
        Long.valueOf(s_id));
    }
  }

  @Override
  public <M> void opaquesAddBatchedInstanceInGroup(
    final R2InstanceBatchedType i,
    final R2MaterialOpaqueBatchedType<M> m,
    final int group)
  {
    NullCheck.notNull(i, "Instance");
    NullCheck.notNull(m, "Material");
    R2Stencils.checkValidGroup(group);

    final long i_id = i.instanceID();
    final long m_id = m.materialID();
    final R2ShaderInstanceBatchedUsableType<?> shader = m.shader();
    final long s_id = shader.shaderID();

    /*
     * Insert the instance into the set of all batched instances. Instances
     * that are already visible are rejected.
     */

    if (this.batches.instances.containsKey(i_id)) {
      throw errorInstanceAlreadyVisible(i);
    }
    this.batches.instances.put(i_id, i);

    /*
     * Insert the instance, material, and shader into the group. Add a
     * mapping from the instance to the material.
     */

    final Batches.Group g = this.batches.groups[group];

    Preconditions.checkPrecondition(
      !g.instances.containsKey(i_id),
      "Group must not contain instance");

    this.batches.group_max = Math.max(this.batches.group_max, group + 1);
    g.instances.put(i_id, i);
    g.instance_materials.put(m_id, m);
    g.instance_shaders.put(s_id, shader);

    /*
     * Update the set of mappings from materials to instances for the group.
     */

    final LongSet m_instances;
    if (g.material_to_instances.containsKey(m_id)) {
      m_instances = g.material_to_instances.get(m_id);
    } else {
      m_instances = new LongOpenHashSet();
    }
    m_instances.add(i_id);
    g.material_to_instances.put(m_id, m_instances);

    /*
     * Update the set of mappings from shaders to materials for the group.
     */

    final LongSet s_materials;
    if (g.shader_to_materials.containsKey(s_id)) {
      s_materials = g.shader_to_materials.get(s_id);
    } else {
      s_materials = new LongOpenHashSet();
    }
    s_materials.add(m_id);
    g.shader_to_materials.put(s_id, s_materials);

    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "opaque add batched (instance {}, group {}, material {}, shader {})",
        Long.valueOf(i_id),
        Integer.valueOf(group),
        Long.valueOf(m_id),
        Long.valueOf(s_id));
    }
  }

  @Override
  public <M> void opaquesAddBillboardedInstanceInGroup(
    final R2InstanceBillboardedType i,
    final R2MaterialOpaqueBillboardedType<M> m,
    final int group)
  {
    NullCheck.notNull(i, "Instance");
    NullCheck.notNull(m, "Material");
    R2Stencils.checkValidGroup(group);

    final long i_id = i.instanceID();
    final long m_id = m.materialID();
    final R2ShaderInstanceBillboardedUsableType<?> shader = m.shader();
    final long s_id = shader.shaderID();

    /*
     * Insert the instance into the set of all billboarded instances. Instances
     * that are already visible are rejected.
     */

    if (this.billboards.instances.containsKey(i_id)) {
      throw errorInstanceAlreadyVisible(i);
    }
    this.billboards.instances.put(i_id, i);

    /*
     * Insert the instance, material, and shader into the group. Add a
     * mapping from the instance to the material.
     */

    final Billboards.Group g = this.billboards.groups[group];

    Preconditions.checkPrecondition(
      !g.instances.containsKey(i_id),
      "Group must not contain instance");

    this.billboards.group_max = Math.max(this.billboards.group_max, group + 1);
    g.instances.put(i_id, i);
    g.instance_materials.put(m_id, m);
    g.instance_shaders.put(s_id, shader);

    /*
     * Update the set of mappings from materials to instances for the group.
     */

    final LongSet m_instances;
    if (g.material_to_instances.containsKey(m_id)) {
      m_instances = g.material_to_instances.get(m_id);
    } else {
      m_instances = new LongOpenHashSet();
    }
    m_instances.add(i_id);
    g.material_to_instances.put(m_id, m_instances);

    /*
     * Update the set of mappings from shaders to materials for the group.
     */

    final LongSet s_materials;
    if (g.shader_to_materials.containsKey(s_id)) {
      s_materials = g.shader_to_materials.get(s_id);
    } else {
      s_materials = new LongOpenHashSet();
    }
    s_materials.add(m_id);
    g.shader_to_materials.put(s_id, s_materials);

    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "opaque add billboarded (instance {}, group {}, material {}, shader {})",
        Long.valueOf(i_id),
        Integer.valueOf(group),
        Long.valueOf(m_id),
        Long.valueOf(s_id));
    }
  }

  @Override
  public void opaquesExecute(
    final R2SceneOpaquesConsumerType c)
  {
    c.onStart();

    /*
     * Update all the batched instances.
     */

    this.opaquesExecuteBatchedInstancesUpdate(c);

    /*
     * Update all the billboarded instances.
     */

    this.opaquesExecuteBillboardedInstancesUpdate(c);

    /*
     * Then for each group, execute:
     *
     * 1. The batched instances
     * 2. The billboarded instances
     * 3. The single instances
     */

    int max = 0;
    max = Math.max(max, this.batches.group_max);
    max = Math.max(max, this.singles.group_max);
    max = Math.max(max, this.billboards.group_max);

    for (int index = 1; index < max; ++index) {
      final Batches.Group g_batch = this.batches.groups[index];
      final Billboards.Group g_bill = this.billboards.groups[index];
      final Singles.Group g_single = this.singles.groups[index];

      if (g_batch.instances.isEmpty()
        && g_single.instances.isEmpty()
        && g_bill.instances.isEmpty()) {
        continue;
      }

      c.onStartGroup(index);

      if (!g_batch.instances.isEmpty()) {
        this.opaquesExecuteGroupBatched(c, g_batch);
      }

      if (!g_bill.instances.isEmpty()) {
        this.opaquesExecuteGroupBillboarded(c, g_bill);
      }

      if (!g_single.instances.isEmpty()) {
        this.opaquesExecuteGroupSingle(c, g_single);
      }

      c.onFinishGroup(index);
    }

    c.onFinish();
  }

  private void opaquesExecuteBillboardedInstancesUpdate(
    final R2SceneOpaquesConsumerType c)
  {
    final LongIterator b_iter =
      this.billboards.instances.keySet().iterator();

    while (b_iter.hasNext()) {
      final long b_id = b_iter.nextLong();
      c.onInstanceBillboardedUpdate(this.billboards.instances.get(b_id));
    }
  }

  private void opaquesExecuteBatchedInstancesUpdate(
    final R2SceneOpaquesConsumerType c)
  {
    final LongIterator b_iter =
      this.batches.instances.keySet().iterator();

    while (b_iter.hasNext()) {
      final long b_id = b_iter.nextLong();
      c.onInstanceBatchedUpdate(this.batches.instances.get(b_id));
    }
  }

  @SuppressWarnings("unchecked")
  private void opaquesExecuteGroupSingle(
    final R2SceneOpaquesConsumerType c,
    final Singles.Group g)
  {
    /*
     * For each single instance shader {@code s}...
     */

    final ObjectIterator<R2ShaderInstanceSingleUsableType<?>> bs_iter =
      g.instance_shaders.values().iterator();

    while (bs_iter.hasNext()) {
      final R2ShaderInstanceSingleUsableType<Object> s =
        (R2ShaderInstanceSingleUsableType<Object>) bs_iter.next();

      c.onInstanceSingleShaderStart(s);

      /*
       * For each material {@code m} using the shader {@code s}...
       */

      final LongSet s_materials =
        g.shader_to_materials.get(s.shaderID());

      for (final long m_id : s_materials) {
        final R2MaterialOpaqueSingleType<Object> material =
          (R2MaterialOpaqueSingleType<Object>) g.instance_materials.get(m_id);
        c.onInstanceSingleMaterialStart(material);

        /*
         * Sort the instances by their array object instances, to allow
         * for rendering with the fewest number of array object binds.
         */

        final LongSet m_instances =
          g.material_to_instances.get(m_id);

        this.singles.instances_sorted.clear();
        for (final long i_id : m_instances) {
          final R2InstanceSingleType i = g.instances.get(i_id);
          this.singles.instances_sorted.add(i);
        }

        this.singles.instances_sorted.sort((a, b) -> {
          final JCGLArrayObjectUsableType ao = a.arrayObject();
          final JCGLArrayObjectUsableType bo = b.arrayObject();
          return Integer.compare(ao.glName(), bo.glName());
        });

        /*
         * Render all instances with the minimum number of array object
         * bindings.
         */

        int current_array = -1;
        final int sorted_size = this.singles.instances_sorted.size();
        for (int index = 0; index < sorted_size; ++index) {
          final R2InstanceSingleType i =
            this.singles.instances_sorted.get(index);
          final JCGLArrayObjectUsableType array_object = i.arrayObject();
          final int next_array = array_object.glName();
          if (next_array != current_array) {
            c.onInstanceSingleArrayStart(i);
          }
          current_array = next_array;
          c.onInstanceSingle(material, i);
        }

        c.onInstanceSingleMaterialFinish(material);
      }

      c.onInstanceSingleShaderFinish(s);
    }
  }

  @SuppressWarnings("unchecked")
  private void opaquesExecuteGroupBatched(
    final R2SceneOpaquesConsumerType c,
    final Batches.Group g)
  {
    /*
     * For each shader {@code s}...
     */

    final ObjectIterator<R2ShaderInstanceBatchedUsableType<?>> bs_iter =
      g.instance_shaders.values().iterator();

    while (bs_iter.hasNext()) {
      final R2ShaderInstanceBatchedUsableType<Object> s =
        (R2ShaderInstanceBatchedUsableType<Object>) bs_iter.next();

      c.onInstanceBatchedShaderStart(s);

      /*
       * For each material {@code m} using the shader {@code s}...
       */

      final LongSet s_materials =
        g.shader_to_materials.get(s.shaderID());

      for (final long m_id : s_materials) {
        final R2MaterialOpaqueBatchedType<Object> material =
          (R2MaterialOpaqueBatchedType<Object>)
            g.instance_materials.get(m_id);
        c.onInstanceBatchedMaterialStart(material);

        /*
         * Render all instances.
         *
         * Batched instances can be rendered in any order, because each
         * batched instance is expected to have its own vertex array object.
         * There are no efficiency gains to be made by imposing any particular
         * order.
         */

        final LongSet m_instances =
          g.material_to_instances.get(m_id);

        for (final long id : m_instances) {
          final R2InstanceBatchedType i = this.batches.instances.get(id);
          c.onInstanceBatched(material, i);
        }

        c.onInstanceBatchedMaterialFinish(material);
      }

      c.onInstanceBatchedShaderFinish(s);
    }
  }

  @SuppressWarnings("unchecked")
  private void opaquesExecuteGroupBillboarded(
    final R2SceneOpaquesConsumerType c,
    final Billboards.Group g)
  {
    /*
     * For each shader {@code s}...
     */

    final ObjectIterator<R2ShaderInstanceBillboardedUsableType<?>> bs_iter =
      g.instance_shaders.values().iterator();

    while (bs_iter.hasNext()) {
      final R2ShaderInstanceBillboardedUsableType<Object> s =
        (R2ShaderInstanceBillboardedUsableType<Object>) bs_iter.next();

      c.onInstanceBillboardedShaderStart(s);

      /*
       * For each material {@code m} using the shader {@code s}...
       */

      final LongSet s_materials =
        g.shader_to_materials.get(s.shaderID());

      for (final long m_id : s_materials) {
        final R2MaterialOpaqueBillboardedType<Object> material =
          (R2MaterialOpaqueBillboardedType<Object>)
            g.instance_materials.get(m_id);
        c.onInstanceBillboardedMaterialStart(material);

        /*
         * Render all instances.
         *
         * Billboarded instances can be rendered in any order, because each
         * billboarded instance is expected to have its own vertex array object.
         * There are no efficiency gains to be made by imposing any particular
         * order.
         */

        final LongSet m_instances =
          g.material_to_instances.get(m_id);

        for (final long id : m_instances) {
          final R2InstanceBillboardedType i = this.billboards.instances.get(id);
          c.onInstanceBillboarded(material, i);
        }

        c.onInstanceBillboardedMaterialFinish(material);
      }

      c.onInstanceBillboardedShaderFinish(s);
    }
  }

  @Override
  public long opaquesCount()
  {
    final long size_c = (long) this.singles.instances.size();
    final long batch_c = (long) this.batches.instances.size();
    final long bill_c = (long) this.billboards.instances.size();
    return Math.addExact(size_c, Math.addExact(batch_c, bill_c));
  }

  private static final class Singles
  {
    private final Group[] groups;
    private final Long2ReferenceOpenHashMap<R2InstanceSingleType> instances;
    private final ObjectArrayList<R2InstanceSingleType> instances_sorted;
    private int group_max;

    Singles()
    {
      final int size = R2Stencils.MAXIMUM_GROUPS;
      this.groups = new Group[size];
      for (int index = 1; index < size; ++index) {
        this.groups[index] = new Group();
      }
      this.group_max = 1;
      this.instances = new Long2ReferenceOpenHashMap<>(1024 * size);
      this.instances_sorted = new ObjectArrayList<>();
    }

    void clear()
    {
      for (int index = 1; index < this.group_max; ++index) {
        this.groups[index].clear();
      }
      this.group_max = 1;
      this.instances.clear();
      this.instances_sorted.clear();
    }

    private static final class Group
    {
      private final Long2ReferenceOpenHashMap<LongSet>
        material_to_instances;
      private final Long2ReferenceOpenHashMap<LongSet>
        shader_to_materials;
      private final Long2ReferenceOpenHashMap<R2MaterialOpaqueSingleType<?>>
        instance_materials;
      private final Long2ReferenceOpenHashMap<R2ShaderInstanceSingleUsableType<?>>
        instance_shaders;
      private final Long2ReferenceOpenHashMap<R2InstanceSingleType>
        instances;

      Group()
      {
        this.material_to_instances = new Long2ReferenceOpenHashMap<>(1024);
        this.shader_to_materials = new Long2ReferenceOpenHashMap<>(1024);
        this.instance_materials = new Long2ReferenceOpenHashMap<>(1024);
        this.instance_shaders = new Long2ReferenceOpenHashMap<>(1024);
        this.instances = new Long2ReferenceOpenHashMap<>(1024);
      }

      void clear()
      {
        this.material_to_instances.clear();
        this.shader_to_materials.clear();
        this.instance_materials.clear();
        this.instance_shaders.clear();
        this.instances.clear();
      }
    }
  }

  private static final class Batches
  {
    private final Group[] groups;
    private final Long2ReferenceOpenHashMap<R2InstanceBatchedType> instances;
    private int group_max;

    Batches()
    {
      final int size = R2Stencils.MAXIMUM_GROUPS;
      this.groups = new Group[size];
      for (int index = 1; index < size; ++index) {
        this.groups[index] = new Group();
      }
      this.group_max = 1;
      this.instances = new Long2ReferenceOpenHashMap<>(1024 * size);
    }

    void clear()
    {
      for (int index = 1; index < this.group_max; ++index) {
        this.groups[index].clear();
      }
      this.group_max = 1;
      this.instances.clear();
    }

    private static final class Group
    {
      private final Long2ReferenceOpenHashMap<LongSet>
        material_to_instances;
      private final Long2ReferenceOpenHashMap<LongSet>
        shader_to_materials;
      private final Long2ReferenceOpenHashMap<R2MaterialOpaqueBatchedType<?>>
        instance_materials;
      private final Long2ReferenceOpenHashMap<R2ShaderInstanceBatchedUsableType<?>>
        instance_shaders;
      private final Long2ReferenceOpenHashMap<R2InstanceBatchedType>
        instances;

      Group()
      {
        this.material_to_instances = new Long2ReferenceOpenHashMap<>(1024);
        this.shader_to_materials = new Long2ReferenceOpenHashMap<>(1024);
        this.instance_materials = new Long2ReferenceOpenHashMap<>(1024);
        this.instance_shaders = new Long2ReferenceOpenHashMap<>(1024);
        this.instances = new Long2ReferenceOpenHashMap<>(1024);
      }

      void clear()
      {
        this.shader_to_materials.clear();
        this.instance_materials.clear();
        this.instance_shaders.clear();
        this.instances.clear();
        this.material_to_instances.clear();
      }
    }
  }

  private static final class Billboards
  {
    private final Group[] groups;
    private final Long2ReferenceOpenHashMap<R2InstanceBillboardedType> instances;
    private int group_max;

    Billboards()
    {
      final int size = R2Stencils.MAXIMUM_GROUPS;
      this.groups = new Group[size];
      for (int index = 1; index < size; ++index) {
        this.groups[index] = new Group();
      }
      this.group_max = 1;
      this.instances = new Long2ReferenceOpenHashMap<>(1024 * size);
    }

    void clear()
    {
      for (int index = 1; index < this.group_max; ++index) {
        this.groups[index].clear();
      }
      this.group_max = 1;
      this.instances.clear();
    }

    private static final class Group
    {
      private final Long2ReferenceOpenHashMap<LongSet>
        material_to_instances;
      private final Long2ReferenceOpenHashMap<LongSet>
        shader_to_materials;
      private final Long2ReferenceOpenHashMap<R2MaterialOpaqueBillboardedType<?>>
        instance_materials;
      private final Long2ReferenceOpenHashMap<R2ShaderInstanceBillboardedUsableType<?>>
        instance_shaders;
      private final Long2ReferenceOpenHashMap<R2InstanceBillboardedType>
        instances;

      Group()
      {
        this.material_to_instances = new Long2ReferenceOpenHashMap<>(1024);
        this.shader_to_materials = new Long2ReferenceOpenHashMap<>(1024);
        this.instance_materials = new Long2ReferenceOpenHashMap<>(1024);
        this.instance_shaders = new Long2ReferenceOpenHashMap<>(1024);
        this.instances = new Long2ReferenceOpenHashMap<>(1024);
      }

      void clear()
      {
        this.shader_to_materials.clear();
        this.instance_materials.clear();
        this.instance_shaders.clear();
        this.instances.clear();
        this.material_to_instances.clear();
      }
    }
  }
}
