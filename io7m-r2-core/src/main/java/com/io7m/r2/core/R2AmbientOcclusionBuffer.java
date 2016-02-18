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
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;

import java.util.List;

/**
 * Default implementation of the {@link R2AmbientOcclusionBufferType}
 * interface.
 */

public final class R2AmbientOcclusionBuffer implements
  R2AmbientOcclusionBufferType
{
  private final R2Texture2DType                         t_occ;
  private final JCGLFramebufferType                     framebuffer;
  private final UnsignedRangeInclusiveL                 range;
  private final R2AmbientOcclusionBufferDescriptionType desc;

  private R2AmbientOcclusionBuffer(
    final JCGLFramebufferType in_framebuffer,
    final R2Texture2DType in_t_occ,
    final R2AmbientOcclusionBufferDescriptionType in_desc)
  {
    this.framebuffer = NullCheck.notNull(in_framebuffer);
    this.t_occ = NullCheck.notNull(in_t_occ);
    this.desc = NullCheck.notNull(in_desc);

    long size = 0L;
    size += this.t_occ.get().getRange().getInterval();
    this.range = new UnsignedRangeInclusiveL(0L, size - 1L);
  }

  /**
   * Construct a new ambient occlusion buffer.
   *
   * @param g_fb A framebuffer interface
   * @param g_t  A texture interface
   * @param tc   A texture unit context
   * @param d    The buffer description
   *
   * @return A new ambient occlusion buffer
   */

  public static R2AmbientOcclusionBufferType newAmbientOcclusionBuffer(
    final JCGLFramebuffersType g_fb,
    final JCGLTexturesType g_t,
    final R2TextureUnitContextParentType tc,
    final R2AmbientOcclusionBufferDescriptionType d)
  {
    final List<JCGLFramebufferColorAttachmentPointType> points =
      g_fb.framebufferGetColorAttachments();
    final List<JCGLFramebufferDrawBufferType> buffers =
      g_fb.framebufferGetDrawBuffers();

    final AreaInclusiveUnsignedLType area = d.getArea();
    final UnsignedRangeInclusiveL range_x = area.getRangeX();
    final UnsignedRangeInclusiveL range_y = area.getRangeY();

    final R2TextureUnitContextType cc = tc.unitContextNewWithReserved(1);
    try {
      final Pair<JCGLTextureUnitType, R2Texture2DType> p_occ =
        cc.unitContextAllocateTexture2D(
          g_t,
          range_x.getInterval(),
          range_y.getInterval(),
          JCGLTextureFormat.TEXTURE_FORMAT_R_8_1BPP,
          JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
          JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

      final R2Texture2DType rt_occ = p_occ.getRight();
      final JCGLFramebufferBuilderType fbb = g_fb.framebufferNewBuilder();
      fbb.attachColorTexture2DAt(points.get(0), buffers.get(0), rt_occ.get());

      final com.io7m.r2.core.R2AmbientOcclusionBufferDescription.Builder ab =
        com.io7m.r2.core.R2AmbientOcclusionBufferDescription.builder();
      ab.from(d);

      final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
      return new R2AmbientOcclusionBuffer(fb, rt_occ, ab.build());
    } finally {
      cc.unitContextFinish(g_t);
    }
  }

  @Override
  public R2Texture2DUsableType getAmbientOcclusionTexture()
  {
    return this.t_occ;
  }

  @Override
  public JCGLFramebufferUsableType getPrimaryFramebuffer()
  {
    return this.framebuffer;
  }

  @Override
  public AreaInclusiveUnsignedLType getArea()
  {
    return this.t_occ.get().textureGetArea();
  }

  @Override
  public R2AmbientOcclusionBufferDescriptionType getDescription()
  {
    return this.desc;
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
      this.t_occ.delete(g);

      final JCGLFramebuffersType g_fb = g.getFramebuffers();
      g_fb.framebufferDelete(this.framebuffer);
    }
  }
}
