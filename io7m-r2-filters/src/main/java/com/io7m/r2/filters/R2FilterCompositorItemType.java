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

package com.io7m.r2.filters;

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2Texture2DUsableType;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * A single texture that will be rendered to some subset of the current
 * viewport.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2FilterCompositorItemType
{
  /**
   * @return The texture that will be rendered
   */

  @Value.Parameter
  R2Texture2DUsableType texture();

  /**
   * @return The area of the viewport to which the texture will be rendered
   */

  @Value.Parameter
  AreaInclusiveUnsignedLType outputViewport();

  /**
   * @return The value by which to scale each sample of the texture when
   * compositing
   */

  @Value.Parameter
  @Value.Default
  default float intensity()
  {
    return 1.0f;
  }

  /**
   * @return The blending parameters for this item
   */

  @Value.Parameter
  Optional<R2FilterCompositorBlendingType> blending();
}
