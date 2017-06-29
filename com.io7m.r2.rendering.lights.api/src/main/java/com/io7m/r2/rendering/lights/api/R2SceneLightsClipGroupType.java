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

package com.io7m.r2.rendering.lights.api;

import com.io7m.r2.lights.R2LightSingleReadableType;
import com.io7m.r2.shaders.light.api.R2ShaderLightSingleUsableType;

/**
 * A clip group that will clip the bounds of all lights within it.
 */

public interface R2SceneLightsClipGroupType
{
  /**
   * @return The light group to which this clip group belongs
   */

  R2SceneLightsGroupType getGroup();

  /**
   * Add a light to the clip group.
   *
   * @param light  The light
   * @param shader The light shader
   * @param <L>    The precise type of light
   */

  <L extends R2LightSingleReadableType>
  void clipGroupAddSingle(
    L light,
    R2ShaderLightSingleUsableType<L> shader);
}
