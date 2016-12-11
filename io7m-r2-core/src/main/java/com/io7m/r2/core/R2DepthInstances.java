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

import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.JCGLFaceSelection;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.shaders.types.R2ShaderDepthBatchedUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderDepthSingleUsableType;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the {@link R2DepthInstancesType} interface.
 */

public final class R2DepthInstances implements R2DepthInstancesType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2DepthInstances.class);
  }

  private final Singles singles;
  private final Batches batches;
  private final StringBuilder text;
  private JCGLFaceSelection culling;

  private R2DepthInstances()
  {
    this.text = new StringBuilder(128);
    this.singles = new Singles();
    this.batches = new Batches();
    this.culling = JCGLFaceSelection.FACE_BACK;
  }

  /**
   * @return A new empty set of depth instances
   */

  public static R2DepthInstancesType newDepthInstances()
  {
    return new R2DepthInstances();
  }

  @Override
  public void depthsSetFaceCulling(final JCGLFaceSelection f)
  {
    this.culling = NullCheck.notNull(f);
  }

  @Override
  public JCGLFaceSelection depthsGetFaceCulling()
  {
    return this.culling;
  }

  @Override
  public void depthsReset()
  {
    LOG.trace("reset");
    this.singles.clear();
    this.batches.clear();
  }

  @Override
  public <M> void depthsAddSingleInstance(
    final R2InstanceSingleType i,
    final R2MaterialDepthSingleType<M> m)
  {
    NullCheck.notNull(i);
    NullCheck.notNull(m);

    final long i_id = i.instanceID();
    final long m_id = m.materialID();
    final R2ShaderDepthSingleUsableType<?> shader = m.shader();
    final long s_id = shader.getShaderID();

    /**
     * Insert the instance into the set of all single instances. Instances
     * that are already visible are rejected.
     */

    if (this.singles.instances.containsKey(i_id)) {
      throw this.errorInstanceAlreadyVisible(i);
    }

    this.singles.instances.put(i_id, i);
    this.singles.instance_materials.put(m_id, m);
    this.singles.instance_shaders.put(s_id, shader);
    this.singles.instance_to_material.put(i_id, m_id);

    /**
     * Update the set of mappings from materials to instances for the group.
     */

    final LongSet m_instances;
    if (this.singles.material_to_instances.containsKey(m_id)) {
      m_instances = this.singles.material_to_instances.get(m_id);
    } else {
      m_instances = new LongOpenHashSet();
    }
    m_instances.add(i_id);
    this.singles.material_to_instances.put(m_id, m_instances);

    /**
     * Update the set of mappings from shaders to materials for the group.
     */

    final LongSet s_materials;
    if (this.singles.shader_to_materials.containsKey(s_id)) {
      s_materials = this.singles.shader_to_materials.get(s_id);
    } else {
      s_materials = new LongOpenHashSet();
    }
    s_materials.add(m_id);
    this.singles.shader_to_materials.put(s_id, s_materials);

    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "add single (instance {}, material {}, shader {})",
        Long.valueOf(i_id),
        Long.valueOf(m_id),
        Long.valueOf(s_id));
    }
  }

  private R2RendererExceptionInstanceAlreadyVisible errorInstanceAlreadyVisible(
    final R2InstanceType i)
  {
    this.text.setLength(0);
    this.text.append("Instance is already visible.\n");
    this.text.append("Instance:         ");
    this.text.append(i.instanceID());
    return new R2RendererExceptionInstanceAlreadyVisible(this.text.toString());
  }

  @Override
  public <M> void depthsAddBatchedInstance(
    final R2InstanceBatchedType i,
    final R2MaterialDepthBatchedType<M> m)
  {
    NullCheck.notNull(i);
    NullCheck.notNull(m);

    final long i_id = i.instanceID();
    final long m_id = m.materialID();
    final R2ShaderDepthBatchedUsableType<?> shader = m.shader();
    final long s_id = shader.getShaderID();

    /**
     * Insert the instance into the set of all batched instances. Instances
     * that are already visible are rejected.
     */

    if (this.batches.instances.containsKey(i_id)) {
      throw this.errorInstanceAlreadyVisible(i);
    }
    this.batches.instances.put(i_id, i);

    /**
     * Insert the instance, material, and shader into the group. Add a
     * mapping from the instance to the material.
     */

    this.batches.instances.put(i_id, i);
    this.batches.instance_materials.put(m_id, m);
    this.batches.instance_shaders.put(s_id, shader);
    this.batches.instance_to_material.put(i_id, m_id);

    /**
     * Update the set of mappings from materials to instances for the group.
     */

    final LongSet m_instances;
    if (this.batches.material_to_instances.containsKey(m_id)) {
      m_instances = this.batches.material_to_instances.get(m_id);
    } else {
      m_instances = new LongOpenHashSet();
    }
    m_instances.add(i_id);
    this.batches.material_to_instances.put(m_id, m_instances);

    /**
     * Update the set of mappings from shaders to materials for the group.
     */

    final LongSet s_materials;
    if (this.batches.shader_to_materials.containsKey(s_id)) {
      s_materials = this.batches.shader_to_materials.get(s_id);
    } else {
      s_materials = new LongOpenHashSet();
    }
    s_materials.add(m_id);
    this.batches.shader_to_materials.put(s_id, s_materials);

    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "add batched (instance {}, material {}, shader {})",
        Long.valueOf(i_id),
        Long.valueOf(m_id),
        Long.valueOf(s_id));
    }
  }

  @Override
  public void depthsExecute(
    final R2DepthInstancesConsumerType c)
  {
    NullCheck.notNull(c);

    c.onStart();
    this.depthsExecuteBatchedInstancesUpdate(c);
    this.depthsExecuteBatched(c);
    this.depthsExecuteSingles(c);
    c.onFinish();
  }

  @SuppressWarnings("unchecked")
  private void depthsExecuteSingles(
    final R2DepthInstancesConsumerType c)
  {
    /**
     * For each single instance shader {@code s}...
     */

    final ObjectIterator<R2ShaderDepthSingleUsableType<?>> bs_iter =
      this.singles.instance_shaders.values().iterator();

    while (bs_iter.hasNext()) {
      final R2ShaderDepthSingleUsableType<Object> s =
        (R2ShaderDepthSingleUsableType<Object>) bs_iter.next();

      c.onInstanceSingleShaderStart(s);

      /**
       * For each material {@code m} using the shader {@code s}...
       */

      final LongSet s_materials =
        this.singles.shader_to_materials.get(s.getShaderID());

      for (final long m_id : s_materials) {
        final R2MaterialDepthSingleType<Object> material =
          (R2MaterialDepthSingleType<Object>)
            this.singles.instance_materials.get(m_id);
        c.onInstanceSingleMaterialStart(material);

        /**
         * Sort the instances by their array object instances, to allow
         * for rendering with the fewest number of array object binds.
         */

        final LongSet m_instances =
          this.singles.material_to_instances.get(m_id);

        this.singles.instances_sorted.clear();
        for (final long i_id : m_instances) {
          final R2InstanceSingleType i = this.singles.instances.get(i_id);
          this.singles.instances_sorted.add(i);
        }

        this.singles.instances_sorted.sort((a, b) -> {
          final JCGLArrayObjectUsableType ao = a.arrayObject();
          final JCGLArrayObjectUsableType bo = b.arrayObject();
          return Integer.compare(ao.getGLName(), bo.getGLName());
        });

        /**
         * Render all instances with the minimum number of array object
         * bindings.
         */

        int current_array = -1;
        final int sorted_size = this.singles.instances_sorted.size();
        for (int index = 0; index < sorted_size; ++index) {
          final R2InstanceSingleType i =
            this.singles.instances_sorted.get(index);
          final JCGLArrayObjectUsableType array_object = i.arrayObject();
          final int next_array = array_object.getGLName();
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

  private void depthsExecuteBatchedInstancesUpdate(
    final R2DepthInstancesConsumerType c)
  {
    final LongIterator b_iter =
      this.batches.instances.keySet().iterator();

    while (b_iter.hasNext()) {
      final long b_id = b_iter.nextLong();
      c.onInstanceBatchedUpdate(this.batches.instances.get(b_id));
    }
  }

  @SuppressWarnings("unchecked")
  private void depthsExecuteBatched(
    final R2DepthInstancesConsumerType c)
  {
    /**
     * For each shader {@code s}...
     */

    final ObjectIterator<R2ShaderDepthBatchedUsableType<?>> bs_iter =
      this.batches.instance_shaders.values().iterator();

    while (bs_iter.hasNext()) {
      final R2ShaderDepthBatchedUsableType<Object> s =
        (R2ShaderDepthBatchedUsableType<Object>) bs_iter.next();

      c.onInstanceBatchedShaderStart(s);

      /**
       * For each material {@code m} using the shader {@code s}...
       */

      final LongSet s_materials =
        this.batches.shader_to_materials.get(s.getShaderID());

      for (final long m_id : s_materials) {
        final R2MaterialDepthBatchedType<Object> material =
          (R2MaterialDepthBatchedType<Object>)
            this.batches.instance_materials.get(m_id);
        c.onInstanceBatchedMaterialStart(material);

        /**
         * Render all instances.
         *
         * Batched instances can be rendered in any order, because each
         * batched instance is expected to have its own vertex array object.
         * There are no efficiency gains to be made by imposing any particular
         * order.
         */

        final LongSet m_instances =
          this.batches.material_to_instances.get(m_id);

        for (final long id : m_instances) {
          final R2InstanceBatchedType i = this.batches.instances.get(id);
          c.onInstanceBatched(material, i);
        }

        c.onInstanceBatchedMaterialFinish(material);
      }

      c.onInstanceBatchedShaderFinish(s);
    }
  }

  @Override
  public long depthsCount()
  {
    final long sc = (long) this.singles.instances.size();
    final long bc = (long) this.batches.instances.size();
    return sc + bc;
  }

  private static final class Singles
  {
    private final Long2LongOpenHashMap
      instance_to_material;
    private final Long2ReferenceOpenHashMap<LongSet>
      material_to_instances;
    private final Long2ReferenceOpenHashMap<LongSet>
      shader_to_materials;
    private final Long2ReferenceOpenHashMap<R2MaterialDepthSingleType<?>>
      instance_materials;
    private final Long2ReferenceOpenHashMap<R2ShaderDepthSingleUsableType<?>>
      instance_shaders;
    private final Long2ReferenceOpenHashMap<R2InstanceSingleType>
      instances;
    private final ObjectArrayList<R2InstanceSingleType>
      instances_sorted;

    Singles()
    {
      this.instances = new Long2ReferenceOpenHashMap<>(1024);
      this.instance_to_material = new Long2LongOpenHashMap(1024);
      this.material_to_instances = new Long2ReferenceOpenHashMap<>(1024);
      this.shader_to_materials = new Long2ReferenceOpenHashMap<>(1024);
      this.instance_materials = new Long2ReferenceOpenHashMap<>(1024);
      this.instance_shaders = new Long2ReferenceOpenHashMap<>(1024);
      this.instances_sorted = new ObjectArrayList<>();
    }

    void clear()
    {
      this.instance_to_material.clear();
      this.material_to_instances.clear();
      this.shader_to_materials.clear();
      this.instance_materials.clear();
      this.instance_shaders.clear();
      this.instances.clear();
    }
  }

  private static final class Batches
  {
    private final Long2LongOpenHashMap instance_to_material;
    private final Long2ReferenceOpenHashMap<LongSet>
      material_to_instances;
    private final Long2ReferenceOpenHashMap<LongSet>
      shader_to_materials;
    private final Long2ReferenceOpenHashMap<R2MaterialDepthBatchedType<?>>
      instance_materials;
    private final Long2ReferenceOpenHashMap<R2ShaderDepthBatchedUsableType<?>>
      instance_shaders;
    private final Long2ReferenceOpenHashMap<R2InstanceBatchedType>
      instances;

    Batches()
    {
      this.instance_to_material = new Long2LongOpenHashMap(1024);
      this.material_to_instances = new Long2ReferenceOpenHashMap<>(1024);
      this.shader_to_materials = new Long2ReferenceOpenHashMap<>(1024);
      this.instance_materials = new Long2ReferenceOpenHashMap<>(1024);
      this.instance_shaders = new Long2ReferenceOpenHashMap<>(1024);
      this.instances = new Long2ReferenceOpenHashMap<>(1024);
    }

    void clear()
    {
      this.instance_to_material.clear();
      this.shader_to_materials.clear();
      this.instance_materials.clear();
      this.instance_shaders.clear();
      this.instances.clear();
      this.material_to_instances.clear();
    }
  }

}
