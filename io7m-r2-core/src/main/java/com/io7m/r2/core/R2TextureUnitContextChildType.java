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

import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;

/**
 * The type of child texture unit contexts.
 */

public interface R2TextureUnitContextChildType
{
  /**
   * Allocate a texture unit and bind the given texture to it.
   *
   * @param g A texture interface
   * @param t A texture
   *
   * @return A texture unit
   */

  JCGLTextureUnitType unitContextBindTexture2D(
    JCGLTexturesType g,
    R2Texture2DUsableType t);

  /**
   * Finish the texture unit context. Subsequent calls to any methods on this
   * context will raise {@link R2ExceptionTextureUnitContextNotActive}.
   *
   * @param g A texture interface
   */

  void unitContextFinish(JCGLTexturesType g);
}
