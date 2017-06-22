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

import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleUsableType;
import org.immutables.value.Value;

/**
 * The type of opaque materials that can be applied to single instances.
 *
 * @param <M> The type of shader parameters
 */

@R2ImmutableStyleType
@Value.Immutable
public interface R2MaterialOpaqueSingleType<M> extends R2MaterialType<M>
{
  @Override
  @Value.Parameter
  long materialID();

  /**
   * @return The material shader
   */

  @Override
  @Value.Auxiliary
  @Value.Parameter
  R2ShaderInstanceSingleUsableType<M> shader();

  @Override
  @Value.Auxiliary
  @Value.Parameter
  M shaderParameters();
}
