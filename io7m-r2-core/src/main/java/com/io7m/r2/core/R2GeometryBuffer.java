/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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
import com.io7m.jcanephora.core.JCGLFramebufferBuilderType;
import com.io7m.jcanephora.core.JCGLFramebufferColorAttachmentPointType;
import com.io7m.jcanephora.core.JCGLFramebufferDrawBufferType;
import com.io7m.jcanephora.core.JCGLFramebufferType;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLTexture2DType;
import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
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

import java.util.List;

/**
 * Default implementation of the {@link R2GeometryBufferType} interface.
 */

public final class R2GeometryBuffer implements R2GeometryBufferType
{
  private final JCGLTexture2DType       t_rgba;
  private final JCGLTexture2DType       t_norm;
  private final JCGLTexture2DType       t_spec;
  private final JCGLTexture2DType       t_depth;
  private final JCGLFramebufferType     framebuffer;
  private final UnsignedRangeInclusiveL range;

  private R2GeometryBuffer(
    final JCGLFramebufferType in_framebuffer,
    final JCGLTexture2DType in_t_rgba,
    final JCGLTexture2DType in_t_norm,
    final JCGLTexture2DType in_t_spec,
    final JCGLTexture2DType in_t_depth)
  {
    this.framebuffer = NullCheck.notNull(in_framebuffer);
    this.t_rgba = NullCheck.notNull(in_t_rgba);
    this.t_norm = NullCheck.notNull(in_t_norm);
    this.t_spec = NullCheck.notNull(in_t_spec);
    this.t_depth = NullCheck.notNull(in_t_depth);

    long size = 0L;
    size += this.t_rgba.getRange().getInterval();
    size += this.t_norm.getRange().getInterval();
    size += this.t_spec.getRange().getInterval();
    size += this.t_depth.getRange().getInterval();
    this.range = new UnsignedRangeInclusiveL(0L, size - 1L);
  }

  /**
   * Construct a new geometry buffer.
   *
   * @param g    A graphics interface
   * @param area The inclusive area of the buffer
   *
   * @return A new geometry buffer
   */

  public static R2GeometryBufferType newGeometryBuffer(
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

    final JCGLTexture2DType t_rgba =
      g_t.texture2DAllocate(
        u_0,
        (long) range_x.getInterval(),
        (long) range_y.getInterval(),
        JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
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

    final JCGLTexture2DType t_norm =
      g_t.texture2DAllocate(
        u_0,
        (long) range_x.getInterval(),
        (long) range_y.getInterval(),
        JCGLTextureFormat.TEXTURE_FORMAT_RG_16F_4BPP,
        JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
        JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

    final JCGLTexture2DType t_spec =
      g_t.texture2DAllocate(
        u_0,
        (long) range_x.getInterval(),
        (long) range_y.getInterval(),
        JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
        JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
        JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

    g_t.textureUnitUnbind(u_0);

    final JCGLFramebufferBuilderType fbb = g_fb.framebufferNewBuilder();
    fbb.attachColorTexture2DAt(points.get(0), buffers.get(0), t_rgba);
    fbb.attachColorTexture2DAt(points.get(1), buffers.get(1), t_norm);
    fbb.attachColorTexture2DAt(points.get(2), buffers.get(2), t_spec);
    fbb.attachDepthStencilTexture2D(t_depth);

    final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
    return new R2GeometryBuffer(fb, t_rgba, t_norm, t_spec, t_depth);
  }

  @Override public JCGLTexture2DUsableType getAlbedoEmissiveTexture()
  {
    return this.t_rgba;
  }

  @Override public JCGLTexture2DUsableType getNormalTexture()
  {
    return this.t_norm;
  }

  @Override public JCGLTexture2DUsableType getSpecularTexture()
  {
    return this.t_spec;
  }

  @Override public JCGLTexture2DUsableType getDepthTexture()
  {
    return this.t_depth;
  }

  @Override public JCGLFramebufferUsableType getFramebuffer()
  {
    return this.framebuffer;
  }

  @Override public UnsignedRangeInclusiveL getRange()
  {
    return this.range;
  }

  @Override public boolean isDeleted()
  {
    return this.framebuffer.isDeleted();
  }
}
