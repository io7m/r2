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

import com.io7m.r2.core.shaders.types.R2ShaderLightSingleUsableType;

/**
 * The set of lights that affect opaque instances in a scene.
 */

public interface R2SceneOpaqueLightsType
{
  /**
   * Remove all lights from the scene.
   */

  void opaqueLightsReset();

  /**
   * Add a light to the scene, lighting instances that were in group {@code
   * group}.
   *
   * @param light  The light
   * @param shader The light shader
   * @param group  The group
   * @param <L>    The precise type of light
   */

  <L extends R2LightSingleReadableType>
  void opaqueLightsAddSingleWithGroup(
    L light,
    R2ShaderLightSingleUsableType<L> shader,
    int group);

  /**
   * Add a light to the scene, lighting instances that were in group {@code 1}.
   *
   * @param light  The light
   * @param shader The light shader
   * @param <L>    The precise type of light
   */

  default <L extends R2LightSingleReadableType>
  void opaqueLightsAddSingle(
    final L light,
    final R2ShaderLightSingleUsableType<L> shader)
  {
    this.opaqueLightsAddSingleWithGroup(light, shader, 1);
  }

  /**
   * Batch the lights and pass them to the given consumer for rendering.
   *
   * @param c The consumer
   */

  void opaqueLightsExecute(
    R2SceneOpaqueLightsConsumerType c);

  /**
   * @return The number of lights in the scene
   */

  long opaqueLightsCount();
}
