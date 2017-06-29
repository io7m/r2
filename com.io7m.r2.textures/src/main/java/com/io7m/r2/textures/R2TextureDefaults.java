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

package com.io7m.r2.textures;

import com.io7m.jcanephora.core.JCGLCubeMapFaceRH;
import com.io7m.jcanephora.core.JCGLTexture2DType;
import com.io7m.jcanephora.core.JCGLTexture2DUpdateType;
import com.io7m.jcanephora.core.JCGLTextureCubeType;
import com.io7m.jcanephora.core.JCGLTextureCubeUpdateType;
import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLTextureUpdates;
import com.io7m.jcanephora.core.JCGLTextureWrapR;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.cursors.JCGLRGB8ByteBuffered;
import com.io7m.jcanephora.cursors.JCGLRGB8Type;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.runtime.java.JPRACursor2DByteBufferedUnchecked;
import com.io7m.jpra.runtime.java.JPRACursor2DType;
import com.io7m.r2.core.api.R2Exception;
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

  private final R2Texture2DType t2d_normal;
  private final R2Texture2DType t2d_white;
  private final R2Texture2DType t2d_black;
  private final R2Texture2DType t2d_projective;
  private final R2TextureCubeType cube_black;

  private R2TextureDefaults(
    final R2Texture2DType in_t2d_white,
    final R2Texture2DType in_t2d_normal,
    final R2Texture2DType in_t2d_black,
    final R2Texture2DType in_t2d_projective,
    final R2TextureCubeType in_cube_black)
  {
    this.t2d_white = NullCheck.notNull(in_t2d_white, "White");
    this.t2d_normal = NullCheck.notNull(in_t2d_normal, "Normal");
    this.t2d_black = NullCheck.notNull(in_t2d_black, "Black");
    this.t2d_projective = NullCheck.notNull(in_t2d_projective, "Projective");
    this.cube_black = NullCheck.notNull(in_cube_black, "Black cube");
  }

  /**
   * Allocate the default textures.
   *
   * @param g_t A texture interface
   * @param tc  A texture unit allocator
   *
   * @return A set of default textures
   */

  public static R2TextureDefaultsType create(
    final JCGLTexturesType g_t,
    final JCGLTextureUnitContextParentType tc)
  {
    NullCheck.notNull(g_t, "Textures");
    NullCheck.notNull(tc, "Texture context");

    LOG.debug("allocating default textures");

    final JCGLTextureUnitContextType cc = tc.unitContextNewWithReserved(3);
    try {
      final R2Texture2DType t2d_n =
        newTextureRGB(g_t, cc, 0.5, 0.5, 1.0);
      final R2Texture2DType t2d_w =
        newTextureRGB(g_t, cc, 1.0, 1.0, 1.0);
      final R2Texture2DType t2d_b =
        newTextureRGB(g_t, cc, 0.0, 0.0, 0.0);
      final R2Texture2DType t2d_proj =
        newTextureProjectiveRGB(g_t, cc, 1.0, 1.0, 1.0);
      final R2TextureCubeType tc_b =
        newCubeRGB(g_t, cc, 0.0, 0.0, 0.0);

      return new R2TextureDefaults(t2d_w, t2d_n, t2d_b, t2d_proj, tc_b);
    } finally {
      cc.unitContextFinish(g_t);
    }
  }

  private static R2Texture2DType newTextureProjectiveRGB(
    final JCGLTexturesType g_tx,
    final JCGLTextureUnitContextType cc,
    final double r,
    final double g,
    final double b)
  {
    final int size = 128;

    final Pair<JCGLTextureUnitType, JCGLTexture2DType> p =
      cc.unitContextAllocateTexture2D(
        g_tx,
        (long) size,
        (long) size,
        JCGLTextureFormat.TEXTURE_FORMAT_RGB_8_3BPP,
        JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
        JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

    final JCGLTexture2DType t = p.getRight();
    final JCGLTexture2DUpdateType up =
      JCGLTextureUpdates.newUpdateReplacingAll2D(t);
    final ByteBuffer d = up.data();
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

  private static R2TextureCubeType newCubeRGB(
    final JCGLTexturesType g_tx,
    final JCGLTextureUnitContextType cc,
    final double r,
    final double g,
    final double b)
  {
    final Pair<JCGLTextureUnitType, JCGLTextureCubeType> p =
      cc.unitContextAllocateTextureCube(
        g_tx,
        2L,
        JCGLTextureFormat.TEXTURE_FORMAT_RGB_8_3BPP,
        JCGLTextureWrapR.TEXTURE_WRAP_REPEAT,
        JCGLTextureWrapS.TEXTURE_WRAP_REPEAT,
        JCGLTextureWrapT.TEXTURE_WRAP_REPEAT,
        JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST,
        JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);

    final JCGLTextureCubeType t = p.getRight();
    final JCGLTextureCubeUpdateType up =
      JCGLTextureUpdates.newUpdateReplacingAllCube(t);
    final ByteBuffer d = up.data();
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

    for (final JCGLCubeMapFaceRH face : JCGLCubeMapFaceRH.values()) {
      g_tx.textureCubeUpdateRH(p.getLeft(), face, up);
    }

    return R2TextureCubeStatic.of(t);
  }

  private static R2Texture2DType newTextureRGB(
    final JCGLTexturesType g_tx,
    final JCGLTextureUnitContextType cc,
    final double r,
    final double g,
    final double b)
  {
    final Pair<JCGLTextureUnitType, JCGLTexture2DType> p =
      cc.unitContextAllocateTexture2D(
        g_tx,
        2L,
        2L,
        JCGLTextureFormat.TEXTURE_FORMAT_RGB_8_3BPP,
        JCGLTextureWrapS.TEXTURE_WRAP_REPEAT,
        JCGLTextureWrapT.TEXTURE_WRAP_REPEAT,
        JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST,
        JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);

    final JCGLTexture2DType t = p.getRight();
    final JCGLTexture2DUpdateType up =
      JCGLTextureUpdates.newUpdateReplacingAll2D(t);
    final ByteBuffer d = up.data();
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
  public R2Texture2DUsableType normal2D()
  {
    return this.t2d_normal;
  }

  @Override
  public R2Texture2DUsableType white2D()
  {
    return this.t2d_white;
  }

  @Override
  public R2Texture2DUsableType black2D()
  {
    return this.t2d_black;
  }

  @Override
  public R2Texture2DUsableType whiteProjective2D()
  {
    return this.t2d_projective;
  }

  @Override
  public R2TextureCubeUsableType blackCube()
  {
    return this.cube_black;
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    if (!this.t2d_normal.isDeleted()) {
      LOG.debug("delete");
      this.t2d_normal.delete(g);
      this.t2d_white.delete(g);
      this.t2d_black.delete(g);
      this.t2d_projective.delete(g);
      this.cube_black.delete(g);
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.t2d_normal.isDeleted();
  }
}
