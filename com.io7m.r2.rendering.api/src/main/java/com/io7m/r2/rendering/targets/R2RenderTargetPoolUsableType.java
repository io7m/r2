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

package com.io7m.r2.rendering.targets;

import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;

/**
 * The type of render target pools.
 *
 * @param <D> The type of render target descriptions
 * @param <T> The type of render targets
 */

public interface R2RenderTargetPoolUsableType<
  D extends R2RenderTargetDescriptionType,
  T extends R2RenderTargetUsableType<D>>
{
  /**
   * <p>Get a render target from the pool, allocating a new one if necessary.
   * The returned render target is guaranteed to be different from any other
   * that has been returned from this call and not yet returned with {@link
   * #returnValue(JCGLTextureUnitContextParentType, R2RenderTargetUsableType)}
   * .</p>
   *
   * @param tc   A texture unit context
   * @param desc A render target description
   *
   * @return A (possibly new) render target that must be returned after use
   *
   * @see #returnValue(JCGLTextureUnitContextParentType, R2RenderTargetUsableType)
   */

  T get(
    JCGLTextureUnitContextParentType tc,
    D desc);

  /**
   * Return a render target that has previously been fetched via {@link
   * #get(JCGLTextureUnitContextParentType, R2RenderTargetDescriptionType)}.
   *
   * @param tc     A texture unit context
   * @param target A render target
   */

  void returnValue(
    JCGLTextureUnitContextParentType tc,
    T target);
}
