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

package com.io7m.r2.rendering.depth.api;

// @formatter:off

import com.io7m.r2.instances.R2InstanceBatchedType;
import com.io7m.r2.instances.R2InstanceSingleType;
import com.io7m.r2.shaders.depth.api.R2ShaderDepthBatchedUsableType;
import com.io7m.r2.shaders.depth.api.R2ShaderDepthSingleUsableType;

/**
 * <p>The type of consumers of depth instances.</p>
 *
 * <p>Instances are batched in a manner that is intended to require the minimum
 * number of state changes for rendering, based upon the relative cost of state
 * changes.</p>
 */

// @formatter:on

public interface R2DepthInstancesConsumerType
{
  /**
   * Called when rendering of instances begins.
   */

  void onStart();

  /**
   * Called when a batched instance should upload batch data to the GPU.
   *
   * @param i The batched instance
   */

  void onInstanceBatchedUpdate(
    R2InstanceBatchedType i);

  /**
   * Called when a new shader should be activated in order to start rendering
   * batched instances.
   *
   * @param s   The shader
   * @param <M> The type of shader parameters
   */

  <M> void onInstanceBatchedShaderStart(
    R2ShaderDepthBatchedUsableType<M> s);

  /**
   * Called when new material settings should be assigned, for batched
   * instances.
   *
   * @param material The current material
   * @param <M>      The type of shader parameters
   */

  <M> void onInstanceBatchedMaterialStart(
    R2MaterialDepthBatchedType<M> material);

  /**
   * Called when a batched instance should be rendered.
   *
   * @param material The current material
   * @param i        The current instance
   * @param <M>      The type of shader parameters
   */

  <M> void onInstanceBatched(
    R2MaterialDepthBatchedType<M> material,
    R2InstanceBatchedType i);

  /**
   * Called after the current set of batched instances have finished rendering
   * with the current material.
   *
   * @param material The current material
   * @param <M>      The type of shader parameters
   */

  <M> void onInstanceBatchedMaterialFinish(
    R2MaterialDepthBatchedType<M> material);

  /**
   * Called when the current shader should be deactivated.
   *
   * @param s   The shader
   * @param <M> The type of shader parameters
   */

  <M> void onInstanceBatchedShaderFinish(
    R2ShaderDepthBatchedUsableType<M> s);

  /**
   * Called when a new shader should be activated in order to start rendering
   * single instances.
   *
   * @param s   The shader
   * @param <M> The type of shader parameters
   */

  <M> void onInstanceSingleShaderStart(
    R2ShaderDepthSingleUsableType<M> s);

  /**
   * Called when new material settings should be assigned, for single
   * instances.
   *
   * @param material The current material
   * @param <M>      The type of shader parameters
   */

  <M> void onInstanceSingleMaterialStart(
    R2MaterialDepthSingleType<M> material);

  /**
   * Called when a new array object should be bound, for single instances.
   *
   * @param i The current instance
   */

  void onInstanceSingleArrayStart(
    R2InstanceSingleType i);

  /**
   * Called when a single instance should be rendered.
   *
   * @param material The current material
   * @param i        The current instance
   * @param <M>      The type of shader parameters
   */

  <M> void onInstanceSingle(
    R2MaterialDepthSingleType<M> material,
    R2InstanceSingleType i);

  /**
   * Called after the current set of single instances have finished rendering
   * with the current material.
   *
   * @param material The current material
   * @param <M>      The type of shader parameters
   */

  <M> void onInstanceSingleMaterialFinish(
    R2MaterialDepthSingleType<M> material);

  /**
   * Called when the current shader should be deactivated.
   *
   * @param s   The shader
   * @param <M> The type of shader parameters
   */

  <M> void onInstanceSingleShaderFinish(
    R2ShaderDepthSingleUsableType<M> s);

  /**
   * Called when rendering of instances is finished.
   */

  void onFinish();
}
