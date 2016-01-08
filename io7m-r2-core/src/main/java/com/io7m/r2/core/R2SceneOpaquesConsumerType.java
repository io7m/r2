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

// @formatter:off

/**
 * <p>The type of consumers of opaque scene instances.</p>
 *
 * <p>Instances are batched in a manner that is intended to require the minimum
 * number of state changes for rendering, based upon the relative cost of state
 * changes.</p>
 *
 * <p>In NVIDIA's <i>Beyond Porting</i> slides, the relative cost of rendering
 * state changes are given:</p>
 *
 * <ul><li>Render target changes - 60,000/second</li><li>Program bindings -
 * 300,000/second</li><li>ROP</li><li>Texture bindings - 1,500,000/second</li>
 * <li>Vertex format</li><li>UBO bindings</li><li>Vertex bindings</li>
 * <li>Uniform updates - 10,000,000/second</li></ul>
 *
 * <p>It is beneficial, for example, to organize instances such that shaders are
 * changed less frequently than array objects, etc.</p>
 */

// @formatter:on

public interface R2SceneOpaquesConsumerType
{
  /**
   * Called when rendering of instances begins.
   */

  void onStart();

  /**
   * Called when a new shader should be activated in order to start rendering
   * single instances.
   *
   * @param s   The shader
   * @param <M> The type of shader parameters
   */

  <M> void onInstanceSingleShaderStart(
    R2ShaderSingleUsableType<M> s);

  /**
   * Called when new material settings should be assigned, for single
   * instances.
   *
   * @param material The current material
   * @param <M>      The type of shader parameters
   */

  <M> void onInstanceSingleMaterialStart(
    R2MaterialOpaqueSingleType<M> material);

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
    R2MaterialOpaqueSingleType<M> material,
    R2InstanceSingleType i);

  /**
   * Called after the current set of single instances have finished rendering
   * with the current material.
   *
   * @param material The current material
   * @param <M>      The type of shader parameters
   */

  <M> void onInstanceSingleMaterialFinish(
    R2MaterialOpaqueSingleType<M> material);

  /**
   * Called when the current shader should be deactivated.
   *
   * @param s   The shader
   * @param <M> The type of shader parameters
   */

  <M> void onInstanceSingleShaderFinish(
    R2ShaderSingleUsableType<M> s);

  /**
   * Called when rendering of instances is finished.
   */

  void onFinish();

}
