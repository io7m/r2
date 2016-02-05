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

import com.io7m.jareas.core.AreaInclusiveUnsignedIType;
import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.JCGLFramebufferBuilderType;
import com.io7m.jcanephora.core.JCGLFramebufferColorAttachmentPointType;
import com.io7m.jcanephora.core.JCGLFramebufferDrawBufferType;
import com.io7m.jcanephora.core.JCGLFramebufferType;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveI;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;

import java.util.List;

/**
 * Default implementation of the {@link R2ImageBufferType} interface.
 */

public final class R2ImageBuffer implements R2ImageBufferType
{
  private final R2Texture2DType            t_rgba;
  private final JCGLFramebufferType        framebuffer;
  private final UnsignedRangeInclusiveL    range;
  private final AreaInclusiveUnsignedLType area;

  private R2ImageBuffer(
    final JCGLFramebufferType in_framebuffer,
    final R2Texture2DType in_t_rgba)
  {
    this.framebuffer = NullCheck.notNull(in_framebuffer);
    this.t_rgba = NullCheck.notNull(in_t_rgba);

    long size = 0L;
    size += this.t_rgba.get().getRange().getInterval();
    this.range = new UnsignedRangeInclusiveL(0L, size - 1L);
    this.area = in_t_rgba.get().textureGetArea();
  }

  /**
   * Construct a new image buffer.
   *
   * @param g_fb A framebuffer interface
   * @param g_t  A texture interface
   * @param tc   A texture unit context
   * @param area The inclusive area of the buffer
   *
   * @return A new image buffer
   */

  public static R2ImageBufferType newImageBuffer(
    final JCGLFramebuffersType g_fb,
    final JCGLTexturesType g_t,
    final R2TextureUnitContextParentType tc,
    final AreaInclusiveUnsignedIType area)
  {
    final List<JCGLFramebufferColorAttachmentPointType> points =
      g_fb.framebufferGetColorAttachments();
    final List<JCGLFramebufferDrawBufferType> buffers =
      g_fb.framebufferGetDrawBuffers();

    final UnsignedRangeInclusiveI range_x = area.getRangeX();
    final UnsignedRangeInclusiveI range_y = area.getRangeY();

    final R2TextureUnitContextType cc = tc.unitContextNew();
    try {
      final Pair<JCGLTextureUnitType, R2Texture2DType> p =
        cc.unitContextAllocateTexture2D(
          g_t,
          (long) range_x.getInterval(),
          (long) range_y.getInterval(),
          JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
          JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
          JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

      final JCGLFramebufferBuilderType fbb = g_fb.framebufferNewBuilder();
      final R2Texture2DType rt = p.getRight();
      fbb.attachColorTexture2DAt(points.get(0), buffers.get(0), rt.get());

      final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
      return new R2ImageBuffer(fb, rt);
    } finally {
      cc.unitContextFinish(g_t);
    }
  }


  @Override
  public R2Texture2DUsableType getRGBATexture()
  {
    return this.t_rgba;
  }

  @Override
  public JCGLFramebufferUsableType getFramebuffer()
  {
    return this.framebuffer;
  }

  @Override
  public AreaInclusiveUnsignedLType getArea()
  {
    return this.area;
  }

  @Override
  public UnsignedRangeInclusiveL getRange()
  {
    return this.range;
  }

  @Override
  public boolean isDeleted()
  {
    return this.framebuffer.isDeleted();
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    if (!this.isDeleted()) {
      this.t_rgba.delete(g);

      final JCGLFramebuffersType g_fb = g.getFramebuffers();
      g_fb.framebufferDelete(this.framebuffer);
    }
  }
}
