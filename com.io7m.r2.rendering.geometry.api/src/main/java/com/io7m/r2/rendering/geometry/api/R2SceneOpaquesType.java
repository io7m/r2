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

package com.io7m.r2.rendering.geometry.api;

import com.io7m.r2.instances.R2ExceptionInstanceAlreadyVisible;
import com.io7m.r2.instances.R2InstanceBatchedType;
import com.io7m.r2.instances.R2InstanceBillboardedType;
import com.io7m.r2.instances.R2InstanceSingleType;
import com.io7m.r2.rendering.stencil.api.R2ExceptionStencilInvalidGroup;

/**
 * The set of opaque instances in a scene.
 */

public interface R2SceneOpaquesType extends R2SceneOpaquesReadableType
{
  /**
   * Remove all instances from the scene.
   */

  void opaquesReset();

  /**
   * Add a single instance to the scene using the given material, in group
   * {@code 1}.
   *
   * @param i   The instance
   * @param m   The material
   * @param <M> The type of shader parameters
   *
   * @throws R2ExceptionInstanceAlreadyVisible If the given instance has already
   *                                           been submitted for rendering
   * @throws R2ExceptionStencilInvalidGroup    If {@code group} does not refer
   *                                           to a valid group
   */

  default <M> void opaquesAddSingleInstance(
    final R2InstanceSingleType i,
    final R2MaterialOpaqueSingleType<M> m)
    throws R2ExceptionInstanceAlreadyVisible,
    R2ExceptionStencilInvalidGroup
  {
    this.opaquesAddSingleInstanceInGroup(i, m, 1);
  }

  /**
   * Add a single instance to the scene using the given material. The instance
   * is placed into the specified {@code group}, which must be a value in the
   * range {@code [1, {@link com.io7m.r2.rendering.stencil.api.R2Stencils#MAXIMUM_GROUPS}]}.
   *
   * @param i     The instance
   * @param m     The material
   * @param group The group
   * @param <M>   The type of shader parameters
   *
   * @throws R2ExceptionInstanceAlreadyVisible If the given instance has already
   *                                           been submitted for rendering
   * @throws R2ExceptionStencilInvalidGroup    If {@code group} does not refer
   *                                           to a valid group
   */

  <M> void opaquesAddSingleInstanceInGroup(
    R2InstanceSingleType i,
    R2MaterialOpaqueSingleType<M> m,
    int group)
    throws R2ExceptionInstanceAlreadyVisible,
    R2ExceptionStencilInvalidGroup;

  /**
   * Add a batched instance to the scene using the given material, in group
   * {@code 1}.
   *
   * @param i   The instance
   * @param m   The material
   * @param <M> The type of shader parameters
   *
   * @throws R2ExceptionInstanceAlreadyVisible If the given instance has already
   *                                           been submitted for rendering
   * @throws R2ExceptionStencilInvalidGroup    If {@code group} does not refer
   *                                           to a valid group
   */

  default <M> void opaquesAddBatchedInstance(
    final R2InstanceBatchedType i,
    final R2MaterialOpaqueBatchedType<M> m)
    throws R2ExceptionInstanceAlreadyVisible,
    R2ExceptionStencilInvalidGroup
  {
    this.opaquesAddBatchedInstanceInGroup(i, m, 1);
  }

  /**
   * Add a batched instance to the scene using the given material. The instance
   * is placed into the specified {@code group}, which must be a value in the
   * range {@code [1, 127]}.
   *
   * @param i     The instance
   * @param m     The material
   * @param group The group
   * @param <M>   The type of shader parameters
   *
   * @throws R2ExceptionInstanceAlreadyVisible If the given instance has already
   *                                           been submitted for rendering
   * @throws R2ExceptionStencilInvalidGroup    If {@code group} does not refer
   *                                           to a valid group
   */

  <M> void opaquesAddBatchedInstanceInGroup(
    R2InstanceBatchedType i,
    R2MaterialOpaqueBatchedType<M> m,
    int group)
    throws R2ExceptionInstanceAlreadyVisible,
    R2ExceptionStencilInvalidGroup;

  /**
   * Add a billboarded instance to the scene using the given material, in group
   * {@code 1}.
   *
   * @param i   The instance
   * @param m   The material
   * @param <M> The type of shader parameters
   *
   * @throws R2ExceptionInstanceAlreadyVisible If the given instance has already
   *                                           been submitted for rendering
   * @throws R2ExceptionStencilInvalidGroup    If {@code group} does not refer
   *                                           to a valid group
   */

  default <M> void opaquesAddBillboardedInstance(
    final R2InstanceBillboardedType i,
    final R2MaterialOpaqueBillboardedType<M> m)
    throws R2ExceptionInstanceAlreadyVisible,
    R2ExceptionStencilInvalidGroup
  {
    this.opaquesAddBillboardedInstanceInGroup(i, m, 1);
  }

  /**
   * Add a billboarded instance to the scene using the given material. The
   * instance is placed into the specified {@code group}, which must be a value
   * in the range {@code [1, 127]}.
   *
   * @param i     The instance
   * @param m     The material
   * @param group The group
   * @param <M>   The type of shader parameters
   *
   * @throws R2ExceptionInstanceAlreadyVisible If the given instance has already
   *                                           been submitted for rendering
   * @throws R2ExceptionStencilInvalidGroup    If {@code group} does not refer
   *                                           to a valid group
   */

  <M> void opaquesAddBillboardedInstanceInGroup(
    R2InstanceBillboardedType i,
    R2MaterialOpaqueBillboardedType<M> m,
    int group)
    throws R2ExceptionInstanceAlreadyVisible,
    R2ExceptionStencilInvalidGroup;
}
