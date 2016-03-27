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
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.shaders.types.R2ShaderLightSingleUsableType;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valid4j.Assertive;

/**
 * Default implementation of the {@link R2SceneOpaqueLightsType} interface.
 */

public final class R2SceneOpaqueLights implements R2SceneOpaqueLightsType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2SceneOpaqueLights.class);
  }

  private final Singles singles;
  private final StringBuilder text;

  private R2SceneOpaqueLights()
  {
    this.singles = new Singles();
    this.text = new StringBuilder(256);
  }

  /**
   * @return An empty set of opaque lights
   */

  public static R2SceneOpaqueLightsType newLights()
  {
    return new R2SceneOpaqueLights();
  }

  @Override
  public void opaqueLightsReset()
  {
    R2SceneOpaqueLights.LOG.trace("reset");
    this.singles.clear();
  }

  @Override
  public <L extends R2LightSingleReadableType>
  void opaqueLightsAddSingleWithGroup(
    final L light,
    final R2ShaderLightSingleUsableType<L> shader,
    final int group)
  {
    NullCheck.notNull(light);
    NullCheck.notNull(shader);
    R2Stencils.checkValidGroup(group);

    final long l_id = light.getLightID();
    final long s_id = shader.getShaderID();

    /**
     * Insert the instance into the set of all single instances. Instances
     * that are already visible are rejected.
     */

    if (this.singles.lights.containsKey(l_id)) {
      throw this.errorLightAlreadyVisible(light);
    }
    this.singles.lights.put(l_id, light);

    /**
     * Insert the light and shader into the group. Add a mapping from the light
     * to the shader.
     */

    final Singles.Group g = this.singles.groups[group];
    Assertive.ensure(!g.lights.containsKey(l_id));
    this.singles.group_max = Math.max(this.singles.group_max, group + 1);
    g.lights.put(l_id, light);
    g.light_shaders.put(s_id, shader);

    /**
     * Update the set of mappings from shaders to lights for the group.
     */

    final LongSet m_lights;
    if (g.shader_to_lights.containsKey(s_id)) {
      m_lights = g.shader_to_lights.get(s_id);
    } else {
      m_lights = new LongOpenHashSet();
    }
    m_lights.add(l_id);
    g.shader_to_lights.put(s_id, m_lights);

    if (R2SceneOpaqueLights.LOG.isTraceEnabled()) {
      R2SceneOpaqueLights.LOG.trace(
        "light add single (light {}, group {}, shader {})",
        Long.valueOf(l_id),
        Integer.valueOf(group),
        Long.valueOf(s_id));
    }
  }

  private R2RendererExceptionLightAlreadyVisible errorLightAlreadyVisible(
    final R2LightSingleReadableType light)
  {
    this.text.setLength(0);
    this.text.append("Light is already visible.\n");
    this.text.append("Light:         ");
    this.text.append(light.getLightID());
    return new R2RendererExceptionLightAlreadyVisible(this.text.toString());
  }

  @Override
  public void opaqueLightsExecute(
    final R2SceneOpaqueLightsConsumerType c)
  {
    NullCheck.notNull(c);

    c.onStart();
    this.opaqueLightsExecuteSingles(c);
    c.onFinish();
  }

  @SuppressWarnings("unchecked")
  private void opaqueLightsExecuteSingles(
    final R2SceneOpaqueLightsConsumerType c)
  {
    /**
     * For each group {@code g}...
     */

    for (int group = 1; group < this.singles.group_max; ++group) {
      final Singles.Group g = this.singles.groups[group];

      if (g.lights.isEmpty()) {
        continue;
      }

      c.onStartGroup(group);

      /**
       * For each single instance shader {@code s}...
       */

      final ObjectIterator<R2ShaderLightSingleUsableType<?>> bs_iter =
        g.light_shaders.values().iterator();

      while (bs_iter.hasNext()) {
        final R2ShaderLightSingleUsableType<R2LightSingleReadableType> s =
          (R2ShaderLightSingleUsableType<R2LightSingleReadableType>) bs_iter.next();

        c.onLightSingleShaderStart(s);

        final long s_id = s.getShaderID();

        /**
         * Sort the lights by their array object lights, to allow
         * for rendering with the fewest number of array object binds.
         */

        final LongSet s_lights =
          g.shader_to_lights.get(s_id);

        this.singles.lights_sorted.clear();
        for (final long i_id : s_lights) {
          final R2LightSingleReadableType i = g.lights.get(i_id);
          this.singles.lights_sorted.add(i);
        }

        this.singles.lights_sorted.sort((a, b) -> {
          final JCGLArrayObjectUsableType ao = a.getArrayObject();
          final JCGLArrayObjectUsableType bo = b.getArrayObject();
          return Integer.compare(ao.getGLName(), bo.getGLName());
        });

        /**
         * Render all lights with the minimum number of array object
         * bindings.
         */

        int current_array = -1;
        final int sorted_size = this.singles.lights_sorted.size();
        for (int index = 0; index < sorted_size; ++index) {
          final R2LightSingleReadableType i =
            this.singles.lights_sorted.get(index);
          final JCGLArrayObjectUsableType array_object =
            i.getArrayObject();
          final int next_array = array_object.getGLName();
          if (next_array != current_array) {
            c.onLightSingleArrayStart(i);
          }
          current_array = next_array;
          c.onLightSingle(s, i);
        }

        c.onLightSingleShaderFinish(s);
      }

      c.onFinishGroup(group);
    }
  }

  @Override
  public long opaqueLightsCount()
  {
    final long sc = (long) this.singles.lights.size();
    return sc;
  }

  private static final class Singles
  {
    private final Group[] groups;
    private final Long2ReferenceOpenHashMap<R2LightSingleReadableType> lights;
    private final ObjectArrayList<R2LightSingleReadableType> lights_sorted;
    private int group_max;

    Singles()
    {
      final int size = R2Stencils.MAXIMUM_GROUPS;
      this.groups = new Group[size];
      for (int index = 1; index < size; ++index) {
        this.groups[index] = new Group();
      }
      this.group_max = 1;
      this.lights = new Long2ReferenceOpenHashMap<>(128 * size);
      this.lights_sorted = new ObjectArrayList<>();
    }

    public void clear()
    {
      for (int index = 1; index < this.group_max; ++index) {
        this.groups[index].clear();
      }
      this.group_max = 1;
      this.lights.clear();
      this.lights_sorted.clear();
    }

    private static final class Group
    {
      private final Long2ReferenceOpenHashMap<LongSet>
        shader_to_lights;
      private final Long2ReferenceOpenHashMap<R2ShaderLightSingleUsableType<?>>
        light_shaders;
      private final Long2ReferenceOpenHashMap<R2LightSingleReadableType>
        lights;

      Group()
      {
        this.light_shaders = new Long2ReferenceOpenHashMap<>(1024);
        this.lights = new Long2ReferenceOpenHashMap<>(1024);
        this.shader_to_lights = new Long2ReferenceOpenHashMap<>(1024);
      }

      void clear()
      {
        this.shader_to_lights.clear();
        this.light_shaders.clear();
        this.lights.clear();
      }
    }
  }
}
