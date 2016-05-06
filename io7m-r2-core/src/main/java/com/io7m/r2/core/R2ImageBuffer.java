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
import com.io7m.jcanephora.core.JCGLClearSpecification;
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
import com.io7m.jcanephora.renderstate.JCGLColorBufferMaskingState;
import com.io7m.jcanephora.renderstate.JCGLRenderState;
import com.io7m.jcanephora.renderstate.JCGLRenderStateMutable;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.VectorI4F;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * Default implementation of the {@link R2ImageBufferType} interface.
 */

public final class R2ImageBuffer implements R2ImageBufferType
{
  private static final JCGLRenderState CLEAR_STATE;
  private static final JCGLClearSpecification CLEAR_SPEC;

  static {
    final JCGLRenderStateMutable k = JCGLRenderStateMutable.create();

    k.setColorBufferMaskingState(
      JCGLColorBufferMaskingState.of(true, true, true, true));
    CLEAR_STATE = JCGLRenderState.builder().from(k).build();

    CLEAR_SPEC = JCGLClearSpecification.of(
      Optional.of(new VectorI4F(1.0f, 1.0f, 1.0f, 1.0f)),
      OptionalDouble.empty(),
      OptionalInt.empty(),
      true);
  }

  private final R2Texture2DType t_rgba;
  private final JCGLFramebufferType framebuffer;
  private final UnsignedRangeInclusiveL range;
  private final R2ImageBufferDescriptionType desc;
  private final Optional<R2Texture2DType> t_depth;

  private R2ImageBuffer(
    final JCGLFramebufferType in_framebuffer,
    final R2ImageBufferDescriptionType in_desc,
    final R2Texture2DType in_t_rgba,
    final Optional<R2Texture2DType> in_t_depth)
  {
    this.framebuffer = NullCheck.notNull(in_framebuffer);
    this.desc = NullCheck.notNull(in_desc);
    this.t_rgba = NullCheck.notNull(in_t_rgba);
    this.t_depth = NullCheck.notNull(in_t_depth);

    long size = 0L;
    size += this.t_rgba.get().getRange().getInterval();

    if (this.t_depth.isPresent()) {
      final R2Texture2DType td = this.t_depth.get();
      size += td.getReal().getRange().getInterval();
    }

    this.range = new UnsignedRangeInclusiveL(0L, size - 1L);
  }

  /**
   * Construct a new image buffer.
   *
   * @param g_fb A framebuffer interface
   * @param g_t  A texture interface
   * @param tc   A texture unit context
   * @param desc The image buffer description
   *
   * @return A new image buffer
   */

  public static R2ImageBufferType newImageBuffer(
    final JCGLFramebuffersType g_fb,
    final JCGLTexturesType g_t,
    final JCGLTextureUnitContextParentType tc,
    final R2ImageBufferDescriptionType desc)
  {
    NullCheck.notNull(g_fb);
    NullCheck.notNull(g_t);
    NullCheck.notNull(tc);
    NullCheck.notNull(desc);

    final List<JCGLFramebufferColorAttachmentPointType> points =
      g_fb.framebufferGetColorAttachments();
    final List<JCGLFramebufferDrawBufferType> buffers =
      g_fb.framebufferGetDrawBuffers();

    final AreaInclusiveUnsignedLType area = desc.getArea();
    final UnsignedRangeInclusiveL range_x = area.getRangeX();
    final UnsignedRangeInclusiveL range_y = area.getRangeY();

    final JCGLTextureUnitContextType cc = tc.unitContextNew();
    try {
      final Pair<JCGLTextureUnitType, JCGLTexture2DType> p =
        cc.unitContextAllocateTexture2D(
          g_t,
          range_x.getInterval(),
          range_y.getInterval(),
          JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
          JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
          JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

      final JCGLFramebufferBuilderType fbb = g_fb.framebufferNewBuilder();
      final R2Texture2DType rt = R2Texture2DStatic.of(p.getRight());
      fbb.attachColorTexture2DAt(points.get(0), buffers.get(0), rt.get());

      final Optional<R2DepthPrecision> prec_opt = desc.getDepthPrecision();
      if (prec_opt.isPresent()) {
        final R2DepthPrecision prec = prec_opt.get();
        final JCGLTextureFormat format =
          R2ImageBuffer.depthFormatForPrecision(prec);
        final Pair<JCGLTextureUnitType, JCGLTexture2DType> pd =
          cc.unitContextAllocateTexture2D(
            g_t,
            range_x.getInterval(),
            range_y.getInterval(),
            format,
            JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
            JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
            JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST,
            JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);
        final JCGLTexture2DType td = pd.getRight();
        fbb.attachDepthTexture2D(td);
        final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
        return new R2ImageBuffer(
          fb, desc, rt, Optional.of(R2Texture2DStatic.of(td)));
      }

      final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
      return new R2ImageBuffer(fb, desc, rt, Optional.empty());

    } finally {
      cc.unitContextFinish(g_t);
    }
  }

  private static JCGLTextureFormat depthFormatForPrecision(
    final R2DepthPrecision prec)
  {
    switch (prec) {
      case R2_DEPTH_PRECISION_16:
        return JCGLTextureFormat.TEXTURE_FORMAT_DEPTH_16_2BPP;
      case R2_DEPTH_PRECISION_24:
        return JCGLTextureFormat.TEXTURE_FORMAT_DEPTH_24_4BPP;
      case R2_DEPTH_PRECISION_32F:
        return JCGLTextureFormat.TEXTURE_FORMAT_DEPTH_32F_4BPP;
    }
    throw new UnreachableCodeException();
  }

  @Override
  public R2Texture2DUsableType getRGBATexture()
  {
    return this.t_rgba;
  }

  @Override
  public JCGLFramebufferUsableType getPrimaryFramebuffer()
  {
    return this.framebuffer;
  }

  @Override
  public AreaInclusiveUnsignedLType getArea()
  {
    return this.desc.getArea();
  }

  @Override
  public R2ImageBufferDescriptionType getDescription()
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
      this.t_rgba.delete(g);

      if (this.t_depth.isPresent()) {
        this.t_depth.get().delete(g);
      }

      final JCGLFramebuffersType g_fb = g.getFramebuffers();
      g_fb.framebufferDelete(this.framebuffer);
    }
  }

  @Override
  public void clearBoundPrimaryFramebuffer(
    final JCGLInterfaceGL33Type g)
    throws R2RendererExceptionFramebufferNotBound
  {
    final JCGLFramebuffersType g_fb = g.getFramebuffers();

    if (!g_fb.framebufferDrawIsBound(this.framebuffer)) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Expected a framebuffer to be bound.");
      sb.append(System.lineSeparator());
      sb.append("Framebuffer: ");
      sb.append(this.framebuffer);
      sb.append(System.lineSeparator());
      throw new R2RendererExceptionFramebufferNotBound(sb.toString());
    }

    JCGLRenderStates.activate(g, R2ImageBuffer.CLEAR_STATE);
    g.getClear().clear(R2ImageBuffer.CLEAR_SPEC);
  }
}
