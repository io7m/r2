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
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valid4j.Assertive;

/**
 * The default implementation of the {@link R2SceneLightsType} interface.
 */

public final class R2SceneLights implements R2SceneLightsType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2SceneLights.class);
  }

  private final Group[] groups;
  private final ObjectArrayList<R2LightSingleReadableType> lights_sorted;
  private int group_max;

  private R2SceneLights()
  {
    final int size = R2Stencils.MAXIMUM_GROUPS;
    this.groups = new Group[size];
    for (int index = 1; index < size; ++index) {
      this.groups[index] = new Group(index);
    }
    this.group_max = 1;
    this.lights_sorted = new ObjectArrayList<>();
  }

  /**
   * @return A new empty set of lights
   */

  public static R2SceneLightsType newLights()
  {
    return new R2SceneLights();
  }

  private void clear()
  {
    for (int index = 1; index < this.group_max; ++index) {
      this.groups[index].clear();
    }
    this.group_max = 1;
    this.lights_sorted.clear();
  }

  @Override
  public void lightsReset()
  {
    R2SceneLights.LOG.trace("reset");
    this.clear();
  }

  @Override
  public R2SceneLightsGroupType lightsGetGroup(
    final int group)
  {
    return this.groups[R2Stencils.checkValidGroup(group)];
  }

  @Override
  public void lightsExecute(
    final R2SceneLightsConsumerType c)
  {
    NullCheck.notNull(c);

    try {
      c.onStart();
      this.lightsExecuteClipGroups(c);
      this.lightsExecuteGroups(c);
    } finally {
      c.onFinish();
    }
  }

  private void lightsExecuteGroups(
    final R2SceneLightsConsumerType c)
  {
    /**
     * For each non-empty group...
     */

    for (int index = 1; index < this.group_max; ++index) {
      final Group g = this.groups[index];
      if (g.lights_unclipped.isEmpty()) {
        continue;
      }

      this.lightsExecuteGroup(c, g);
    }
  }

  @SuppressWarnings("unchecked")
  private void lightsExecuteGroup(
    final R2SceneLightsConsumerType c,
    final Group g)
  {
    final R2SceneLightsGroupConsumerType gv = c.onStartGroup(g.id);

    try {

      /**
       * For each single instance shader {@code s}...
       */

      final ObjectIterator<R2ShaderLightSingleUsableType<?>> bs_iter =
        g.light_shaders.values().iterator();

      while (bs_iter.hasNext()) {
        final R2ShaderLightSingleUsableType<R2LightSingleReadableType> s =
          (R2ShaderLightSingleUsableType<R2LightSingleReadableType>)
            bs_iter.next();

        /**
         * If any lights are present that do not belong to a clip group, then
         * render them.
         */

        final long s_id = s.getShaderID();
        if (g.shader_to_lights.containsKey(s_id)) {
          gv.onLightSingleShaderStart(s);

          /**
           * Sort the lights by their array object lights, to allow
           * for rendering with the fewest number of array object binds.
           */

          Assertive.require(g.shader_to_lights.containsKey(s_id));
          final LongSet s_lights =
            g.shader_to_lights.get(s_id);
          Assertive.require(s_lights != null);

          this.sortLights(g, s_lights, this.lights_sorted);

          /**
           * Render all lights with the minimum number of array object
           * bindings.
           */

          int current_array = -1;
          final int sorted_size = this.lights_sorted.size();
          for (int index = 0; index < sorted_size; ++index) {
            final R2LightSingleReadableType i =
              this.lights_sorted.get(index);
            final JCGLArrayObjectUsableType array_object =
              i.getArrayObject();
            final int next_array = array_object.getGLName();
            if (next_array != current_array) {
              gv.onLightSingleArrayStart(i);
            }
            current_array = next_array;
            gv.onLightSingle(s, i);
          }

          gv.onLightSingleShaderFinish(s);
        }
      }

    } finally {
      gv.onFinish();
    }
  }

  private void lightsExecuteClipGroups(
    final R2SceneLightsConsumerType c)
  {
    /**
     * For each group that contains a non-empty clip group...
     */

    for (int index = 1; index < this.group_max; ++index) {
      final Group g = this.groups[index];
      if (g.clip_groups.isEmpty()) {
        continue;
      }

      /**
       * Execute each non-empty clip group...
       */

      final LongIterator k_iter = g.clip_groups.keySet().iterator();
      while (k_iter.hasNext()) {
        final long cg_id = k_iter.nextLong();
        final Group.ClipGroup cg = g.clip_groups.get(cg_id);
        if (cg.shader_to_lights.isEmpty()) {
          continue;
        }

        this.lightsExecuteClipGroup(c, cg);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void lightsExecuteClipGroup(
    final R2SceneLightsConsumerType c,
    final Group.ClipGroup cg)
  {
    final Group g =
      (Group) cg.getGroup();
    final R2SceneLightsClipGroupConsumerType cgc =
      c.onStartClipGroup(cg.volume, g.getID());

    try {

      /**
       * For each shader...
       */

      final LongIterator s_iter =
        cg.shader_to_lights.keySet().iterator();

      while (s_iter.hasNext()) {
        final long s_id = s_iter.nextLong();
        final R2ShaderLightSingleUsableType<R2LightSingleReadableType> shader =
          (R2ShaderLightSingleUsableType<R2LightSingleReadableType>)
            g.light_shaders.get(s_id);

        cgc.onLightSingleShaderStart(shader);

        /**
         * Sort the lights by their array object instances, to allow
         * for rendering with the fewest number of array object binds.
         */

        final LongSet s_lights =
          cg.shader_to_lights.get(s_id);

        this.sortLights(g, s_lights, this.lights_sorted);

        /**
         * Render all lights with the minimum number of array object
         * bindings.
         */

        int current_array = -1;
        final int sorted_size = this.lights_sorted.size();
        for (int index = 0; index < sorted_size; ++index) {
          final R2LightSingleReadableType i =
            this.lights_sorted.get(index);
          final JCGLArrayObjectUsableType array_object =
            i.getArrayObject();
          final int next_array = array_object.getGLName();
          if (next_array != current_array) {
            cgc.onLightSingleArrayStart(i);
          }
          current_array = next_array;
          cgc.onLightSingle(shader, i);
        }

        cgc.onLightSingleShaderFinish(shader);
      }
    } finally {
      cgc.onFinish();
    }
  }

  private void sortLights(
    final Group g,
    final LongSet s_lights,
    final ObjectArrayList<R2LightSingleReadableType> ls)
  {
    ls.clear();
    for (final long i_id : s_lights) {
      final R2LightSingleReadableType i = g.lights.get(i_id);
      ls.add(i);
    }

    /**
     * Sort lights first by their array objects, and then by their light
     * identifiers.
     */

    ls.sort((a, b) -> {
      final JCGLArrayObjectUsableType ao = a.getArrayObject();
      final JCGLArrayObjectUsableType bo = b.getArrayObject();

      final int ac = Integer.compareUnsigned(ao.getGLName(), bo.getGLName());
      if (ac == 0) {
        return Long.compareUnsigned(a.getLightID(), b.getLightID());
      }

      return ac;
    });
  }

  @Override
  public long lightsCount()
  {
    long size = 0L;

    for (int index = 1; index < this.groups.length; ++index) {
      size += (long) this.groups[index].lights.size();
    }

    return size;
  }

  private final class Group implements R2SceneLightsGroupType
  {
    private final int id;
    private final Long2ReferenceOpenHashMap<LongSet> shader_to_lights;
    private final Long2ReferenceOpenHashMap<R2ShaderLightSingleUsableType<?>> light_shaders;
    private final Long2ReferenceOpenHashMap<R2LightSingleReadableType> lights;
    private final LongSet lights_unclipped;
    private final Long2ReferenceOpenHashMap<ClipGroup> clip_groups;

    Group(final int in_id)
    {
      this.id = in_id;
      this.shader_to_lights = new Long2ReferenceOpenHashMap<>();
      this.light_shaders = new Long2ReferenceOpenHashMap<>();
      this.lights = new Long2ReferenceOpenHashMap<>();
      this.lights_unclipped = new LongOpenHashSet();
      this.clip_groups = new Long2ReferenceOpenHashMap<>();
    }

    @Override
    public int getID()
    {
      return this.id;
    }

    @Override
    public <L extends R2LightSingleReadableType> void lightGroupAddSingle(
      final L light,
      final R2ShaderLightSingleUsableType<L> shader)
    {
      NullCheck.notNull(light);
      NullCheck.notNull(shader);

      final R2SceneLights ls = R2SceneLights.this;
      final long l_id = light.getLightID();
      final long s_id = shader.getShaderID();

      /**
       * Insert the light and shader into the group. Add a mapping from the
       * light to the shader.
       */

      if (this.lights.containsKey(l_id)) {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("Light already visible in group.");
        sb.append(System.lineSeparator());
        sb.append("Light: ");
        sb.append(l_id);
        sb.append(System.lineSeparator());
        sb.append("Group: ");
        sb.append(this.getID());
        sb.append(System.lineSeparator());
        throw new R2RendererExceptionLightAlreadyVisible(sb.toString());
      }

      Assertive.require(!this.lights.containsKey(l_id));

      ls.group_max = Math.max(ls.group_max, this.id + 1);
      this.lights.put(l_id, light);
      this.light_shaders.put(s_id, shader);
      this.lights_unclipped.add(l_id);

      /**
       * Update the set of mappings from shaders to lights for the group.
       */

      final LongSet m_lights;
      if (this.shader_to_lights.containsKey(s_id)) {
        m_lights = this.shader_to_lights.get(s_id);
      } else {
        m_lights = new LongOpenHashSet();
      }
      m_lights.add(l_id);
      this.shader_to_lights.put(s_id, m_lights);

      if (R2SceneLights.LOG.isTraceEnabled()) {
        R2SceneLights.LOG.trace(
          "light add single (light {}, group {}, shader {})",
          Long.valueOf(l_id),
          Integer.valueOf(this.id),
          Long.valueOf(s_id));
      }
    }

    @Override
    public R2SceneLightsClipGroupType lightGroupNewClipGroup(
      final R2InstanceSingleType i)
    {
      NullCheck.notNull(i);

      final long iid = i.getInstanceID();
      Assertive.require(!this.clip_groups.containsKey(iid));

      final R2SceneLights ls = R2SceneLights.this;
      ls.group_max = Math.max(ls.group_max, this.id + 1);

      final ClipGroup cg = new ClipGroup(i);
      this.clip_groups.put(iid, cg);
      return cg;
    }

    private void clear()
    {
      this.shader_to_lights.clear();
      this.light_shaders.clear();
      this.lights.clear();
      this.lights_unclipped.clear();

      final LongIterator iter = this.clip_groups.keySet().iterator();
      while (iter.hasNext()) {
        final ClipGroup cg = this.clip_groups.get(iter.nextLong());
        cg.shader_to_lights.clear();
        cg.deleted = true;
      }
      this.clip_groups.clear();;
    }

    private final class ClipGroup implements R2SceneLightsClipGroupType
    {
      private final R2InstanceSingleType volume;
      private final Long2ReferenceOpenHashMap<LongSet> shader_to_lights;
      private boolean deleted;

      private ClipGroup(
        final R2InstanceSingleType v)
      {
        this.volume = NullCheck.notNull(v);
        this.shader_to_lights = new Long2ReferenceOpenHashMap<>();
        this.deleted = false;
      }

      @Override
      public R2SceneLightsGroupType getGroup()
      {
        return Group.this;
      }

      @Override
      public <L extends R2LightSingleReadableType> void clipGroupAddSingle(
        final L light,
        final R2ShaderLightSingleUsableType<L> shader)
      {
        NullCheck.notNull(light);
        NullCheck.notNull(shader);

        if (this.deleted) {
          throw new R2RendererExceptionClipGroupDeleted(
            "Clip group has been deleted");
        }

        final long l_id = light.getLightID();
        final long s_id = shader.getShaderID();

        /**
         * Insert the light and shader into the group. Add a mapping from the light
         * to the shader.
         */

        if (Group.this.lights.containsKey(l_id)) {
          final StringBuilder sb = new StringBuilder(128);
          sb.append("Light already visible in group.");
          sb.append(System.lineSeparator());
          sb.append("Light: ");
          sb.append(l_id);
          sb.append(System.lineSeparator());
          sb.append("Group: ");
          sb.append(Group.this.getID());
          sb.append(System.lineSeparator());
          throw new R2RendererExceptionLightAlreadyVisible(sb.toString());
        }

        Assertive.require(!Group.this.lights.containsKey(l_id));
        Group.this.lights.put(l_id, light);
        Group.this.light_shaders.put(s_id, shader);

        /**
         * Update the set of mappings from shaders to lights for the clip group.
         */

        final LongSet m_lights;
        if (this.shader_to_lights.containsKey(s_id)) {
          m_lights = this.shader_to_lights.get(s_id);
        } else {
          m_lights = new LongOpenHashSet();
        }
        m_lights.add(l_id);
        this.shader_to_lights.put(s_id, m_lights);

        if (R2SceneLights.LOG.isTraceEnabled()) {
          R2SceneLights.LOG.trace(
            "light add single (light {}, shader {}, clip group {})",
            Long.valueOf(l_id),
            Long.valueOf(this.volume.getInstanceID()),
            Long.valueOf(s_id));
        }
      }
    }
  }
}
