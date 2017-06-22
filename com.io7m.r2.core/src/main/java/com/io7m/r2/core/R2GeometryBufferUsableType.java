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

import java.util.Optional;

/**
 * The type of usable geometry buffers.
 */

public interface R2GeometryBufferUsableType
  extends R2RenderTargetUsableType<R2GeometryBufferDescription>
{
  /**
   * @return The albedo/emissive texture
   */

  R2Texture2DUsableType albedoEmissiveTexture();

  /**
   * @return The normal texture
   */

  R2Texture2DUsableType normalTexture();

  /**
   * @return The specular texture
   */

  Optional<R2Texture2DUsableType> specularTexture();

  /**
   * Return either the allocated specular texture, or a suitable default
   * replacement that behaves as if the geometry buffer contains no specular
   * components.
   *
   * @param td The texture defaults
   *
   * @return A specular texture, or a default replacement
   */

  default R2Texture2DUsableType specularTextureOrDefault(
    final R2TextureDefaultsType td)
  {
    // Checkstyle doesn't understand the final keyword in interfaces.
    // CHECKSTYLE:OFF
    final Optional<R2Texture2DUsableType> s_opt = this.specularTexture();
    return s_opt.orElseGet(td::black2D);
    // CHECKSTYLE:ON
  }

  /**
   * @return The depth/stencil texture
   */

  R2Texture2DUsableType depthTexture();
}
