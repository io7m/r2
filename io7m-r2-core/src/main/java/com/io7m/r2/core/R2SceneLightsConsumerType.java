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

/**
 * <p>The type of consumers of scene lights.</p>
 *
 * <p>Lights are batched in a manner that is intended to require the minimum
 * number of state changes for rendering, based upon the relative cost of state
 * changes.</p>
 *
 * @see R2SceneOpaquesConsumerType
 */

public interface R2SceneLightsConsumerType
{
  /**
   * Called when rendering of lights begins.
   */

  void onStart();

  /**
   * Called when a clip group should be rendered.
   *
   * @param i     The clipping volume
   * @param group The group number
   *
   * @return A clip group consumer that will receive the contents of the group
   */

  R2SceneLightsClipGroupConsumerType onStartClipGroup(
    R2InstanceSingleType i,
    int group);

  /**
   * Called when a group should be rendered.
   *
   * @param group The group number
   *
   * @return A group consumer that will receive the contents of the group
   */

  R2SceneLightsGroupConsumerType onStartGroup(
    int group);

  /**
   * Called when rendering of lights is finished.
   */

  void onFinish();
}
