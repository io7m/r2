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

package com.io7m.r2.rendering.stencil.api;

import com.io7m.r2.instances.R2InstanceSingleType;

/**
 * The set of stencil instances in a scene.
 */

public interface R2SceneStencilsType
{
  /**
   * Remove all stencil instances from the scene.
   */

  void stencilsReset();

  /**
   * Set the default stencil mode.
   *
   * @param m The mode
   */

  void stencilsSetMode(
    R2SceneStencilsMode m);

  /**
   * @return The default stencil mode
   */

  R2SceneStencilsMode stencilsGetMode();

  /**
   * @return The number of stencil instances in the scene
   */

  long stencilsCount();

  /**
   * Add a single instance to the scene.
   *
   * @param i The instance
   */

  void stencilsAddSingle(
    R2InstanceSingleType i);

  /**
   * Batch the instances and pass them to the given consumer for rendering.
   *
   * @param c The consumer
   */

  void stencilsExecute(
    R2SceneStencilsConsumerType c);
}
