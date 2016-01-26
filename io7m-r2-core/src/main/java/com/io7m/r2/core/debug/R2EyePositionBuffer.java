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

import com.io7m.jareas.core.AreaInclusiveUnsignedIType;
import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.JCGLFramebufferBuilderType;
import com.io7m.jcanephora.core.JCGLFramebufferColorAttachmentPointType;
import com.io7m.jcanephora.core.JCGLFramebufferDrawBufferType;
import com.io7m.jcanephora.core.JCGLFramebufferType;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLTexture2DType;
import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jnull.NullCheck;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveI;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2Texture2DStatic;
import com.io7m.r2.core.R2Texture2DType;
import com.io7m.r2.core.R2Texture2DUsableType;

import java.util.List;

/**
 * Default implementation of the {@link R2EyePositionBuffer} interface.
 */

public final class R2EyePositionBuffer implements R2EyePositionBufferType
{
  private final R2Texture2DType            t_eye;
  private final R2Texture2DType            t_depth;
  private final JCGLFramebufferType        framebuffer;
  private final UnsignedRangeInclusiveL    range;
  private final AreaInclusiveUnsignedLType area;

  private R2EyePositionBuffer(
    final JCGLFramebufferType in_framebuffer,
    final R2Texture2DType in_t_eye,
    final R2Texture2DType in_t_depth)
  {
    this.framebuffer = NullCheck.notNull(in_framebuffer);
    this.t_eye = NullCheck.notNull(in_t_eye);
    this.t_depth = NullCheck.notNull(in_t_depth);

    long size = 0L;
    size += this.t_eye.get().getRange().getInterval();
    size += this.t_depth.get().getRange().getInterval();
    this.range = new UnsignedRangeInclusiveL(0L, size - 1L);
    this.area = in_t_eye.get().textureGetArea();
  }

  /**
   * Construct a new eye-space position buffer.
   *
   * @param g    A graphics interface
   * @param area The inclusive area of the buffer
   *
   * @return A new eye-space position buffer
   */

  public static R2EyePositionBufferType newEyePositionBuffer(
    final JCGLInterfaceGL33Type g,
    final AreaInclusiveUnsignedIType area)
  {
    final JCGLFramebuffersType g_fb = g.getFramebuffers();
    final JCGLTexturesType g_t = g.getTextures();
    final List<JCGLFramebufferColorAttachmentPointType> points =
      g_fb.framebufferGetColorAttachments();
    final List<JCGLFramebufferDrawBufferType> buffers =
      g_fb.framebufferGetDrawBuffers();
    final List<JCGLTextureUnitType> units =
      g_t.textureGetUnits();
    final JCGLTextureUnitType u_0 = units.get(0);

    final UnsignedRangeInclusiveI range_x = area.getRangeX();
    final UnsignedRangeInclusiveI range_y = area.getRangeY();

    final JCGLTexture2DType t_eye =
      g_t.texture2DAllocate(
        u_0,
        (long) range_x.getInterval(),
        (long) range_y.getInterval(),
        JCGLTextureFormat.TEXTURE_FORMAT_RGBA_32F_16BPP,
        JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
        JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

    final JCGLTexture2DType t_depth =
      g_t.texture2DAllocate(
        u_0,
        (long) range_x.getInterval(),
        (long) range_y.getInterval(),
        JCGLTextureFormat.TEXTURE_FORMAT_DEPTH_24_STENCIL_8_4BPP,
        JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
        JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

    g_t.textureUnitUnbind(u_0);

    final JCGLFramebufferBuilderType fbb = g_fb.framebufferNewBuilder();
    fbb.attachColorTexture2DAt(points.get(0), buffers.get(0), t_eye);
    fbb.attachDepthStencilTexture2D(t_depth);

    final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
    return new R2EyePositionBuffer(
      fb,
      R2Texture2DStatic.of(t_eye),
      R2Texture2DStatic.of(t_depth));
  }

  @Override
  public R2Texture2DUsableType getEyeZTexture()
  {
    return this.t_eye;
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
      this.t_eye.delete(g);
      this.t_depth.delete(g);

      final JCGLFramebuffersType g_fb = g.getFramebuffers();
      g_fb.framebufferDelete(this.framebuffer);
    }
  }
}