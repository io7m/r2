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

package com.io7m.r2.core.debug;

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureUnitContextParentType;

/**
 * The type of filters that display fullscreen quads textured with the given
 * texture.
 */

public interface R2FilterTextureShowType extends R2FilterType
{
  /**
   * Run the filter. The texture will be written to the first draw buffer of
   * whatever is the currently bound framebuffer.
   *
   * @param g    A GL interface
   * @param uc   A texture unit context
   * @param area The current framebuffer viewport
   * @param t    A texture
   */

  void runShowWithBoundBuffer(
    JCGLInterfaceGL33Type g,
    R2TextureUnitContextParentType uc,
    AreaInclusiveUnsignedLType area,
    R2Texture2DUsableType t);
}
