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

import com.io7m.jcanephora.core.JCGLFaceSelection;

/**
 * The set of depth instances in a scene.
 */

public interface R2DepthInstancesType
{
  /**
   * Set which faces will be culled for the rendered instances. The default if
   * none are specified is {@link JCGLFaceSelection#FACE_BACK}.
   *
   * @param f The faces to be culled
   */

  void depthsSetFaceCulling(JCGLFaceSelection f);

  /**
   * @return The faces to be culled during rendering
   *
   * @see #depthsSetFaceCulling(JCGLFaceSelection)
   */

  JCGLFaceSelection depthsGetFaceCulling();

  /**
   * Remove all instances from the scene.
   */

  void depthsReset();

  /**
   * Add a single instance to the scene using the given material, in group
   * {@code 1}.
   *
   * @param i   The instance
   * @param m   The material
   * @param <M> The type of shader parameters
   */

  <M> void depthsAddSingleInstance(
    final R2InstanceSingleType i,
    final R2MaterialOpaqueSingleType<M> m);

  /**
   * Add a batched instance to the scene using the given material, in group
   * {@code 1}.
   *
   * @param i   The instance
   * @param m   The material
   * @param <M> The type of shader parameters
   */

  <M> void depthsAddBatchedInstance(
    final R2InstanceBatchedType i,
    final R2MaterialOpaqueBatchedType<M> m);

  /**
   * Batch the instances and pass them to the given consumer for rendering.
   *
   * @param c The consumer
   */

  void depthsExecute(
    R2DepthInstancesConsumerType c);

  /**
   * @return The number of instances in the scene
   */

  long depthsCount();
}
