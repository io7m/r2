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

package com.io7m.r2.core.debug;

import com.io7m.jtensors.VectorReadable4FType;
import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2SceneLightsType;
import com.io7m.r2.core.R2SceneOpaquesReadableType;
import com.io7m.r2.core.R2UnitSphereUsableType;
import it.unimi.dsi.fastutil.ints.Int2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import org.immutables.value.Value;

/**
 * Parameters for debug-visualizing renderers.
 */

@Value.Immutable
@Value.Modifiable
@R2ImmutableStyleType
public interface R2DebugVisualizerRendererParametersType
{
  /**
   * @return A unit sphere used to render various pieces of debug geometry
   */

  @Value.Parameter
  R2UnitSphereUsableType unitSphere();

  /**
   * @return A debug cube used to render various pieces of debug geometry
   */

  @Value.Parameter
  R2DebugCubeUsableType debugCube();

  /**
   * @return The opaque instances in the scene.
   */

  @Value.Parameter
  R2SceneOpaquesReadableType opaqueInstances();

  /**
   * @return The lights in the scene.
   */

  @Value.Parameter
  R2SceneLightsType lights();

  /**
   * @return The extra debugging instances
   */

  @Value.Parameter
  R2DebugInstancesType debugInstances();

  /**
   * @return {@code true} iff opaque instances should be rendered
   */

  @Value.Parameter
  @Value.Default
  default boolean showOpaqueInstances()
  {
    return true;
  }

  /**
   * @return {@code true} iff lights should be rendered
   */

  @Value.Parameter
  @Value.Default
  default boolean showLights()
  {
    return true;
  }

  /**
   * The mapping from groups to colors. Each instance will be rendered a
   * specific color based on the group to which it belongs. If no mapping exists
   * from the group number to a color, the default color will be used instead.
   *
   * @return A mapping from groups to colors
   *
   * @see #geometryDefaultColor()
   */

  @Value.Parameter
  @Value.Default
  default Int2ReferenceMap<VectorReadable4FType> geometryGroupColors()
  {
    return new Int2ReferenceLinkedOpenHashMap<>();
  }

  /**
   * @return The default color with which to render instances, if no specific
   * color has been set
   *
   * @see #geometryGroupColors()
   */

  @Value.Parameter
  @Value.Default
  default VectorReadable4FType geometryDefaultColor()
  {
    return R2DebugVisualizerDefaults.DEFAULT_GROUP_COLOR;
  }
}
