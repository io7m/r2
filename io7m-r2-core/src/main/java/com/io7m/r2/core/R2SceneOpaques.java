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
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
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

  private final Long2LongOpenHashMap instance_to_material;

  private final Long2ReferenceOpenHashMap<LongSet> material_to_instances;
  private final Long2ReferenceOpenHashMap<LongSet> shader_to_materials;

  private final Long2ReferenceOpenHashMap<R2MaterialOpaqueSingleType<?>>
    instance_materials;
  private final Long2ReferenceOpenHashMap<R2ShaderSingleUsableType<?>>
    instance_shaders;
  private final Long2ReferenceOpenHashMap<R2InstanceSingleType>
    instances;

  private final StringBuilder text;
  private final ObjectArrayList<R2InstanceSingleType> instances_sorted;

  private R2SceneOpaques()
  {
    this.text = new StringBuilder(128);
    this.instance_to_material = new Long2LongOpenHashMap(1024);
    this.material_to_instances = new Long2ReferenceOpenHashMap<>(1024);
    this.shader_to_materials = new Long2ReferenceOpenHashMap<>(1024);
    this.instance_materials = new Long2ReferenceOpenHashMap<>(1024);
    this.instance_shaders = new Long2ReferenceOpenHashMap<>(1024);
    this.instances = new Long2ReferenceOpenHashMap<>(1024);
    this.instances_sorted = new ObjectArrayList<>();
  }

  /**
   * @return A new empty set of opaques
   */

  public static R2SceneOpaquesType newOpaques()
  {
    return new R2SceneOpaques();
  }

  @Override
  public void opaquesReset()
  {
    this.instance_to_material.clear();
    this.material_to_instances.clear();
    this.shader_to_materials.clear();
    this.instance_materials.clear();
    this.instance_shaders.clear();
    this.instances.clear();
    this.instances_sorted.clear();

    R2SceneOpaques.LOG.trace("reset");
  }

  @Override
  public <M> void opaquesAddSingleInstance(
    final R2InstanceSingleType i,
    final R2MaterialOpaqueSingleType<M> m)
  {
    NullCheck.notNull(i);
    NullCheck.notNull(m);

    final long i_id = i.getInstanceID();
    final long m_id = m.getMaterialID();
    final R2ShaderSingleUsableType<?> shader = m.getShader();
    final long s_id = shader.getShaderID();

    if (this.instance_to_material.containsKey(i_id)) {
      final long im_id = this.instance_to_material.get(i_id);
      if (im_id != m_id) {
        throw this.errorInstanceAlreadyVisible(i, m_id, im_id);
      }
    }

    this.instance_to_material.put(i_id, m_id);

    final LongSet m_instances;
    if (this.material_to_instances.containsKey(m_id)) {
      m_instances = this.material_to_instances.get(m_id);
    } else {
      m_instances = new LongOpenHashSet();
    }
    m_instances.add(i_id);
    this.material_to_instances.put(m_id, m_instances);

    final LongSet s_materials;
    if (this.shader_to_materials.containsKey(s_id)) {
      s_materials = this.shader_to_materials.get(s_id);
    } else {
      s_materials = new LongOpenHashSet();
    }
    s_materials.add(m_id);
    this.shader_to_materials.put(s_id, s_materials);

    this.instances.put(i_id, i);
    this.instance_materials.put(m_id, m);
    this.instance_shaders.put(s_id, shader);

    if (R2SceneOpaques.LOG.isTraceEnabled()) {
      R2SceneOpaques.LOG.trace(
        "opaque add single-mesh (instance {}, material {}, shader {})",
        Long.valueOf(i_id), Long.valueOf(m_id), Long.valueOf(s_id));
    }
  }

  @Override
  public void opaquesExecute(
    final R2SceneOpaquesConsumerType c)
  {
    final ObjectIterator<R2ShaderSingleUsableType<?>> s_iter =
      this.instance_shaders.values().iterator();

    c.onStart();
    this.opaquesExecuteSingleInstances(c, s_iter);
    c.onFinish();
  }

  @SuppressWarnings("unchecked")
  private void opaquesExecuteSingleInstances(
    final R2SceneOpaquesConsumerType c,
    final ObjectIterator<R2ShaderSingleUsableType<?>> s_iter)
  {
    /**
     * For each single instance shader {@code s}...
     */

    while (s_iter.hasNext()) {
      final R2ShaderSingleUsableType<Object> s =
        (R2ShaderSingleUsableType<Object>) s_iter.next();
      c.onInstanceSingleShaderStart(s);

      /**
       * For each material {@code m} using the shader {@code s}...
       */

      final LongSet s_materials = this.shader_to_materials.get(s.getShaderID());
      for (final long m_id : s_materials) {
        final R2MaterialOpaqueSingleType<Object> material =
          (R2MaterialOpaqueSingleType<Object>)
            this.instance_materials.get(m_id);
        c.onInstanceSingleMaterialStart(material);

        /**
         * Sort the instances by their array object instances, to allow
         * for rendering with the fewest number of array object binds.
         */

        final LongSet m_instances = this.material_to_instances.get(m_id);
        this.instances_sorted.clear();
        for (final long i_id : m_instances) {
          final R2InstanceSingleType i = this.instances.get(i_id);
          this.instances_sorted.add(i);
        }

        this.instances_sorted.sort((a, b) -> {
          final JCGLArrayObjectUsableType ao = a.getArrayObject();
          final JCGLArrayObjectUsableType bo = b.getArrayObject();
          return Integer.compare(ao.getGLName(), bo.getGLName());
        });

        int current_array = -1;
        for (int index = 0; index < this.instances_sorted.size(); ++index) {
          final R2InstanceSingleType i = this.instances_sorted.get(index);
          final JCGLArrayObjectUsableType array_object = i.getArrayObject();
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

  @Override
  public long opaquesCount()
  {
    return (long) this.instances.size();
  }

  private R2RendererExceptionInstanceAlreadyVisible errorInstanceAlreadyVisible(
    final R2InstanceSingleType i,
    final long m_id,
    final long im_id)
  {
    this.text.setLength(0);
    this.text.append(
      "Instance is already visible with different material.\n");
    this.text.append("Instance:         ");
    this.text.append(i.getInstanceID());
    this.text.append("Current material: ");
    this.text.append(im_id);
    this.text.append("New material:     ");
    this.text.append(m_id);
    return new R2RendererExceptionInstanceAlreadyVisible(
      this.text.toString());
  }
}
