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

import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.shaders.types.R2ShaderDepthSingleUsableType;

/**
 * Default implementation of the {@link R2MaterialDepthSingleType}
 * interface.
 *
 * @param <M> The type of shader parameters
 */

public final class R2MaterialDepthSingle<M> implements
  R2MaterialDepthSingleType<M>
{
  private final long                                id;
  private final M                                   params;
  private final R2ShaderDepthSingleUsableType<M> shader;

  private R2MaterialDepthSingle(
    final long in_id,
    final R2ShaderDepthSingleUsableType<M> in_shader,
    final M in_params)
  {
    this.params = NullCheck.notNull(in_params);
    this.shader = NullCheck.notNull(in_shader);
    this.id = in_id;
  }

  /**
   * Construct a new material.
   *
   * @param in_pool   The ID pool
   * @param in_shader The shader
   * @param in_params The parameters
   * @param <M>       The precise type of shader parameters
   *
   * @return A new material
   */

  public static <M> R2MaterialDepthSingleType<M> newMaterial(
    final R2IDPoolType in_pool,
    final R2ShaderDepthSingleUsableType<M> in_shader,
    final M in_params)
  {
    NullCheck.notNull(in_pool);
    return new R2MaterialDepthSingle<>(
      in_pool.getFreshID(), in_shader, in_params);
  }

  @Override
  public long getMaterialID()
  {
    return this.id;
  }

  @Override
  public M getShaderParameters()
  {
    return this.params;
  }

  @Override
  public R2ShaderDepthSingleUsableType<M> getShader()
  {
    return this.shader;
  }
}
