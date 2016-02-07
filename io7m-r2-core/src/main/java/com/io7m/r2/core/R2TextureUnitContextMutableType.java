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

import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jfunctional.Pair;

/**
 * The type of texture unit contexts that can have new textures bound.
 */

public interface R2TextureUnitContextMutableType
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
   * Allocate a 2D texture using the given parameters, binding it to a freshly
   * allocated texture unit.
   *
   * @param g          A texture interface
   * @param width      The width of the texture
   * @param height     The height of the texture
   * @param format     The texture format
   * @param wrap_s     The wrapping mode on the S axis
   * @param wrap_t     The wrapping mode on the T axis
   * @param min_filter The magnification filter
   * @param mag_filter The minification filter
   *
   * @return A pair consisting of the allocated texture and texture unit
   */

  Pair<JCGLTextureUnitType, R2Texture2DType> unitContextAllocateTexture2D(
    JCGLTexturesType g,
    long width,
    long height,
    JCGLTextureFormat format,
    JCGLTextureWrapS wrap_s,
    JCGLTextureWrapT wrap_t,
    JCGLTextureFilterMinification min_filter,
    JCGLTextureFilterMagnification mag_filter);
}
