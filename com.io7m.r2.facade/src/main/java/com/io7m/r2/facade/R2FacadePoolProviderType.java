/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.facade;

import com.io7m.r2.core.R2AmbientOcclusionBufferDescription;
import com.io7m.r2.core.R2AmbientOcclusionBufferPool;
import com.io7m.r2.core.R2AmbientOcclusionBufferUsableType;
import com.io7m.r2.core.R2ImageBufferDescription;
import com.io7m.r2.core.R2ImageBufferPool;
import com.io7m.r2.core.R2ImageBufferUsableType;
import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2RenderTargetPoolType;
import org.immutables.value.Value;

/**
 * The type of convenient pool providers.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2FacadePoolProviderType
{
  /**
   * @return The current facade
   */

  @Value.Auxiliary
  @Value.Parameter
  R2FacadeType main();

  /**
   * Create a new pool of RGBA images.
   *
   * @param soft_size The soft size of the pool; the size at which the
   *                  implementation will attempt to keep the pool
   * @param hard_size The hard size of the pool; the maximum size of the pool -
   *                  exceeding this size will raise errors
   *
   * @return A new image pool
   */

  default R2RenderTargetPoolType<R2ImageBufferDescription, R2ImageBufferUsableType> createRGBAPool(
    final long soft_size,
    final long hard_size)
  {
    return R2ImageBufferPool.newPool(this.main().gl33(), soft_size, hard_size);
  }

  /**
   * Create a new pool of ambient occlusion images.
   *
   * @param soft_size The soft size of the pool; the size at which the
   *                  implementation will attempt to keep the pool
   * @param hard_size The hard size of the pool; the maximum size of the pool -
   *                  exceeding this size will raise errors
   *
   * @return A new image pool
   */

  default R2RenderTargetPoolType<R2AmbientOcclusionBufferDescription, R2AmbientOcclusionBufferUsableType> createAmbientOcclusionPool(
    final long soft_size,
    final long hard_size)
  {
    return R2AmbientOcclusionBufferPool.newPool(
      this.main().gl33(),
      soft_size,
      hard_size);
  }
}
