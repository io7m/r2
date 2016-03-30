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

/**
 * The type of render target pools.
 *
 * @param <D> The type of render target descriptions
 * @param <T> The type of render targets
 */

public interface R2RenderTargetPoolType<
  D extends R2RenderTargetDescriptionType,
  T extends R2RenderTargetUsableType<D>>
  extends R2RenderTargetPoolUsableType<D, T>
{
  /**
   * Delete the pool.
   *
   * @param c A texture unit context
   */

  void delete(R2TextureUnitContextParentType c);

  /**
   * @return {@code true} iff the pool is deleted
   */

  boolean isDeleted();
}