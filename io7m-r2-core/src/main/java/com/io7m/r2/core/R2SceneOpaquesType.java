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

/**
 * The set of opaque instances in a scene.
 */

public interface R2SceneOpaquesType
{
  /**
   * Remove all instances from the scene.
   */

  void opaquesReset();

  /**
   * Add a single mesh instance to the scene using the given material.
   *
   * @param i   The instance
   * @param m   The material
   * @param <M> The type of shader parameters
   */

  <M> void opaquesAddSingleMesh(
    R2InstanceSingleMeshType i,
    R2MaterialOpaqueSingleMeshType<M> m);

  /**
   * Batch the instances and pass them to the given consumer for rendering.
   *
   * @param c The consume
   */

  void opaquesExecute(
    R2SceneOpaquesConsumerType c);
}
