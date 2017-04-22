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

import com.io7m.jcanephora.core.JCGLTexture2DType;
import com.io7m.jcanephora.core.JCGLTexture2DUpdateType;
import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLTextureUpdates;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.cursors.JCGLR8ByteBuffered;
import com.io7m.jcanephora.cursors.JCGLR8Type;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.runtime.java.JPRACursor2DByteBufferedUnchecked;
import com.io7m.jpra.runtime.java.JPRACursor2DType;
import com.io7m.junreachable.UnimplementedCodeException;

/**
 * Functions for allocating noise textures.
 */

public final class R2NoiseTexture
{
  private R2NoiseTexture()
  {
    throw new UnimplementedCodeException();
  }

  /**
   * Construct a new square noise texture.
   *
   * @param gt   A texture interface
   * @param tc   A texture unit allocator
   * @param size The length of the edge of the noise texture
   *
   * @return A new texture
   */

  public static R2Texture2DStatic newR8(
    final JCGLTexturesType gt,
    final JCGLTextureUnitContextParentType tc,
    final long size)
  {
    NullCheck.notNull(gt, "Textures");
    NullCheck.notNull(tc, "Texture context");

    final JCGLTextureUnitContextType cc = tc.unitContextNew();
    try {
      final Pair<JCGLTextureUnitType, JCGLTexture2DType> p =
        cc.unitContextAllocateTexture2D(
          gt,
          size,
          size,
          JCGLTextureFormat.TEXTURE_FORMAT_R_8_1BPP,
          JCGLTextureWrapS.TEXTURE_WRAP_REPEAT,
          JCGLTextureWrapT.TEXTURE_WRAP_REPEAT,
          JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
          JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);
      final R2Texture2DStatic rt = R2Texture2DStatic.of(p.getRight());

      final JCGLTexture2DUpdateType tu =
        JCGLTextureUpdates.newUpdateReplacingAll2D(rt.texture());
      final JPRACursor2DType<JCGLR8Type> c =
        JPRACursor2DByteBufferedUnchecked.newCursor(
          tu.data(),
          (int) size,
          (int) size,
          JCGLR8ByteBuffered::newValueWithOffset);

      final JCGLR8Type uv = c.getElementView();
      for (int y = 0; y < (int) size; ++y) {
        for (int x = 0; x < (int) size; ++x) {
          c.setElementPosition(x, y);
          uv.setR(Math.random());
        }
      }

      gt.texture2DUpdate(p.getLeft(), tu);
      return rt;
    } finally {
      cc.unitContextFinish(gt);
    }
  }
}
