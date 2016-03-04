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
import com.io7m.r2.core.shaders.R2ShaderSingleUsableType;

/**
 * Default implementation of the {@link R2MaterialOpaqueSingleType}
 * interface.
 *
 * @param <M> The type of shader parameters
 */

public final class R2MaterialOpaqueSingle<M> implements
  R2MaterialOpaqueSingleType<M>
{
  private final long                        id;
  private final M                           params;
  private final R2ShaderSingleUsableType<M> shader;

  private R2MaterialOpaqueSingle(
    final long in_id,
    final R2ShaderSingleUsableType<M> in_shader,
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

  public static <M> R2MaterialOpaqueSingleType<M> newMaterial(
    final R2IDPoolType in_pool,
    final R2ShaderSingleUsableType<M> in_shader,
    final M in_params)
  {
    NullCheck.notNull(in_pool);
    return new R2MaterialOpaqueSingle<>(
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
  public R2ShaderSingleUsableType<M> getShader()
  {
    return this.shader;
  }
}
