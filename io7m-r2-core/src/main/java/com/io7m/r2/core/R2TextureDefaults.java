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
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.cursors.JCGLRGB8ByteBuffered;
import com.io7m.jcanephora.cursors.JCGLRGB8Type;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.runtime.java.JPRACursor2DByteBufferedUnchecked;
import com.io7m.jpra.runtime.java.JPRACursor2DType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * The default implementation of the {@link R2TextureDefaultsType} interface.
 */

public final class R2TextureDefaults implements R2TextureDefaultsType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2TextureDefaults.class);
  }

  private final R2Texture2DType normal;
  private final R2Texture2DType white;
  private final R2Texture2DType black;
  private final R2Texture2DType projective;

  private R2TextureDefaults(
    final R2Texture2DType in_white,
    final R2Texture2DType in_normal,
    final R2Texture2DType in_black,
    final R2Texture2DType in_projective)
  {
    this.white = NullCheck.notNull(in_white);
    this.normal = NullCheck.notNull(in_normal);
    this.black = NullCheck.notNull(in_black);
    this.projective = NullCheck.notNull(in_projective);
  }

  /**
   * Allocate the default textures.
   *
   * @param g_t A texture interface
   * @param tc  A texture unit allocator
   *
   * @return A set of default textures
   */

  public static R2TextureDefaultsType newDefaults(
    final JCGLTexturesType g_t,
    final R2TextureUnitContextParentType tc)
  {
    NullCheck.notNull(g_t);
    NullCheck.notNull(tc);

    R2TextureDefaults.LOG.debug("allocating default textures");

    final R2TextureUnitContextType cc = tc.unitContextNewWithReserved(3);
    try {
      final R2Texture2DType t_n =
        R2TextureDefaults.newTextureRGB(g_t, cc, 0.5, 0.5, 1.0);
      final R2Texture2DType t_w =
        R2TextureDefaults.newTextureRGB(g_t, cc, 1.0, 1.0, 1.0);
      final R2Texture2DType t_b =
        R2TextureDefaults.newTextureRGB(g_t, cc, 0.0, 0.0, 0.0);
      final R2Texture2DType t_proj =
        R2TextureDefaults.newTextureProjectiveRGB(g_t, cc, 1.0, 1.0, 1.0);

      return new R2TextureDefaults(t_w, t_n, t_b, t_proj);
    } finally {
      cc.unitContextFinish(g_t);
    }
  }

  private static R2Texture2DType newTextureProjectiveRGB(
    final JCGLTexturesType g_tx,
    final R2TextureUnitContextType cc,
    final double r,
    final double g,
    final double b)
  {
    final int size = 128;

    final Pair<JCGLTextureUnitType, R2Texture2DType> p =
      cc.unitContextAllocateTexture2D(
        g_tx,
        (long) size,
        (long) size,
        JCGLTextureFormat.TEXTURE_FORMAT_RGB_8_3BPP,
        JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
        JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

    final JCGLTexture2DType t = p.getRight().getReal();
    final JCGLTexture2DUpdateType up =
      JCGLTextureUpdates.newUpdateReplacingAll2D(t);
    final ByteBuffer d = up.getData();
    final JPRACursor2DType<JCGLRGB8Type> c =
      JPRACursor2DByteBufferedUnchecked.newCursor(
        d, size, size, JCGLRGB8ByteBuffered::newValueWithOffset);
    final JCGLRGB8Type v = c.getElementView();

    for (int y = 0; y < size; ++y) {
      for (int x = 0; x < size; ++x) {
        c.setElementPosition(x, y);

        final boolean edge =
          x == 0 || x == (size - 1) || y == 0 || y == (size - 1);

        if (edge) {
          v.setR(0.0);
          v.setG(0.0);
          v.setB(0.0);
        } else {
          v.setR(r);
          v.setG(g);
          v.setB(b);
        }
      }
    }

    g_tx.texture2DUpdate(p.getLeft(), up);
    return R2Texture2DStatic.of(t);
  }

  private static R2Texture2DType newTextureRGB(
    final JCGLTexturesType g_tx,
    final R2TextureUnitContextType cc,
    final double r,
    final double g,
    final double b)
  {
    final Pair<JCGLTextureUnitType, R2Texture2DType> p =
      cc.unitContextAllocateTexture2D(
        g_tx,
        2L,
        2L,
        JCGLTextureFormat.TEXTURE_FORMAT_RGB_8_3BPP,
        JCGLTextureWrapS.TEXTURE_WRAP_REPEAT,
        JCGLTextureWrapT.TEXTURE_WRAP_REPEAT,
        JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST,
        JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);

    final JCGLTexture2DType t = p.getRight().getReal();
    final JCGLTexture2DUpdateType up =
      JCGLTextureUpdates.newUpdateReplacingAll2D(t);
    final ByteBuffer d = up.getData();
    final JPRACursor2DType<JCGLRGB8Type> c =
      JPRACursor2DByteBufferedUnchecked.newCursor(
        d, 2, 2, JCGLRGB8ByteBuffered::newValueWithOffset);
    final JCGLRGB8Type v = c.getElementView();

    for (int y = 0; y < 2; ++y) {
      for (int x = 0; x < 2; ++x) {
        c.setElementPosition(x, y);
        v.setR(r);
        v.setG(g);
        v.setB(b);
      }
    }

    g_tx.texture2DUpdate(p.getLeft(), up);
    return R2Texture2DStatic.of(t);
  }

  @Override
  public R2Texture2DUsableType getNormalTexture()
  {
    return this.normal;
  }

  @Override
  public R2Texture2DUsableType getWhiteTexture()
  {
    return this.white;
  }

  @Override
  public R2Texture2DUsableType getBlackTexture()
  {
    return this.black;
  }

  @Override
  public R2Texture2DUsableType getWhiteProjectiveTexture()
  {
    return this.projective;
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    if (!this.normal.isDeleted()) {
      R2TextureDefaults.LOG.debug("delete");
      this.normal.delete(g);
      this.white.delete(g);
      this.black.delete(g);
      this.projective.delete(g);
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.normal.isDeleted();
  }
}
