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
import com.io7m.jcanephora.core.JCGLTextureFormats;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.renderstate.JCGLColorBufferMaskingState;
import com.io7m.jcanephora.renderstate.JCGLRenderState;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Pair;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jtensors.VectorI4F;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * Default implementation of the {@link R2MaskBufferType} interface.
 */

public final class R2MaskBuffer implements R2MaskBufferType
{
  private static final JCGLRenderState CLEAR_STATE;
  private static final JCGLClearSpecification CLEAR_SPEC;

  static {
    CLEAR_STATE =
      JCGLRenderState.builder()
        .setColorBufferMaskingState(
          JCGLColorBufferMaskingState.of(true, true, true, true))
        .build();

    CLEAR_SPEC = JCGLClearSpecification.of(
      Optional.of(new VectorI4F(1.0f, 1.0f, 1.0f, 1.0f)),
      OptionalDouble.empty(),
      OptionalInt.empty(),
      true);
  }

  private final R2Texture2DType t_rgba;
  private final JCGLFramebufferType framebuffer;
  private final UnsignedRangeInclusiveL range;
  private final R2MaskBufferDescriptionType desc;
  private final @Nullable R2Texture2DType t_depth;

  private R2MaskBuffer(
    final JCGLFramebufferType in_framebuffer,
    final R2MaskBufferDescriptionType in_desc,
    final R2Texture2DType in_t_rgba,
    final @Nullable R2Texture2DType in_t_depth,
    final @Nullable R2Texture2DUsableType in_t_depth_shared)
  {
    this.framebuffer = NullCheck.notNull(in_framebuffer);
    this.desc = NullCheck.notNull(in_desc);
    this.t_rgba = NullCheck.notNull(in_t_rgba);

    this.t_depth = in_t_depth;

    long size = 0L;
    size += this.t_rgba.texture().getRange().getInterval();

    if (this.t_depth != null) {
      size += this.t_depth.texture().getRange().getInterval();
    }
    if (in_t_depth_shared != null) {
      size += in_t_depth_shared.texture().getRange().getInterval();
    }

    this.range = new UnsignedRangeInclusiveL(0L, size - 1L);
  }

  /**
   * Construct a new mask buffer.
   *
   * @param g_fb A framebuffer interface
   * @param g_t  A texture interface
   * @param tc   A texture unit context
   * @param desc The mask buffer description
   *
   * @return A new mask buffer
   */

  public static R2MaskBufferType newMaskBuffer(
    final JCGLFramebuffersType g_fb,
    final JCGLTexturesType g_t,
    final JCGLTextureUnitContextParentType tc,
    final R2MaskBufferDescriptionType desc)
  {
    NullCheck.notNull(g_fb);
    NullCheck.notNull(g_t);
    NullCheck.notNull(tc);
    NullCheck.notNull(desc);

    final List<JCGLFramebufferColorAttachmentPointType> points =
      g_fb.framebufferGetColorAttachments();
    final List<JCGLFramebufferDrawBufferType> buffers =
      g_fb.framebufferGetDrawBuffers();

    final AreaInclusiveUnsignedLType area = desc.area();
    final UnsignedRangeInclusiveL range_x = area.getRangeX();
    final UnsignedRangeInclusiveL range_y = area.getRangeY();

    final JCGLTextureUnitContextType cc = tc.unitContextNew();
    try {
      final Pair<JCGLTextureUnitType, JCGLTexture2DType> p =
        cc.unitContextAllocateTexture2D(
          g_t,
          range_x.getInterval(),
          range_y.getInterval(),
          JCGLTextureFormat.TEXTURE_FORMAT_R_8_1BPP,
          JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
          JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

      final JCGLFramebufferBuilderType fbb = g_fb.framebufferNewBuilder();
      final R2Texture2DType rt = R2Texture2DStatic.of(p.getRight());
      fbb.attachColorTexture2DAt(points.get(0), buffers.get(0), rt.texture());

      final Optional<R2DepthAttachmentSpecificationType> spec_opt =
        desc.depthAttachment();

      if (spec_opt.isPresent()) {
        final R2DepthAttachmentSpecificationType spec = spec_opt.get();
        return spec.matchDepthAttachment(
          Unit.unit(),
          (ignored, share) -> createMaskBufferWithShared(
            g_fb, desc, area, fbb, rt, share),
          (ignored, create) -> createMaskBufferWithCreated(
            g_fb, g_t, desc, range_x, range_y, cc, fbb, rt, create));
      }

      final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
      return new R2MaskBuffer(fb, desc, rt, null, null);
    } finally {
      cc.unitContextFinish(g_t);
    }
  }

  private static R2MaskBuffer createMaskBufferWithCreated(
    final JCGLFramebuffersType g_fb,
    final JCGLTexturesType g_t,
    final R2MaskBufferDescriptionType desc,
    final UnsignedRangeInclusiveL range_x,
    final UnsignedRangeInclusiveL range_y,
    final JCGLTextureUnitContextMutableType cc,
    final JCGLFramebufferBuilderType fbb,
    final R2Texture2DType rt,
    final R2DepthAttachmentCreateType create)
  {
    final JCGLTextureFormat format =
      depthFormatForPrecision(create.precision());
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
    return new R2MaskBuffer(
      fb, desc, rt, R2Texture2DStatic.of(td), null);
  }

  private static R2MaskBuffer createMaskBufferWithShared(
    final JCGLFramebuffersType g_fb,
    final R2MaskBufferDescriptionType desc,
    final AreaInclusiveUnsignedLType area,
    final JCGLFramebufferBuilderType fbb,
    final R2Texture2DType rt,
    final R2DepthAttachmentShareType share)
  {
    final R2Texture2DUsableType dt = share.texture();

    final AreaInclusiveUnsignedLType depth_area = dt.texture().textureGetArea();
    if (!Objects.equals(depth_area, area)) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append(
        "The size of a shared depth attachment must match the size of the color attachment.");
      sb.append(System.lineSeparator());
      sb.append("Color size: ");
      sb.append(area.getRangeX().getInterval());
      sb.append("x");
      sb.append(area.getRangeY().getInterval());
      sb.append(System.lineSeparator());
      sb.append("Depth size: ");
      sb.append(depth_area.getRangeX().getInterval());
      sb.append("x");
      sb.append(depth_area.getRangeY().getInterval());
      sb.append(System.lineSeparator());
      throw new R2ExceptionBadTextureSize(sb.toString());
    }

    final JCGLTextureFormat format = dt.texture().textureGetFormat();
    if (JCGLTextureFormats.isDepthRenderable(format)) {
      if (JCGLTextureFormats.isStencilRenderable(format)) {
        fbb.attachDepthStencilTexture2D(dt.texture());
      } else {
        fbb.attachDepthTexture2D(dt.texture());
      }
    } else {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Expected a depth or depth-stencil format texture.");
      sb.append(System.lineSeparator());
      sb.append("Format: ");
      sb.append(format);
      sb.append(System.lineSeparator());
      throw new R2ExceptionBadTextureSize(sb.toString());
    }

    final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
    return new R2MaskBuffer(fb, desc, rt, null, dt);
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
  public R2Texture2DUsableType maskTexture()
  {
    return this.t_rgba;
  }

  @Override
  public JCGLFramebufferUsableType primaryFramebuffer()
  {
    return this.framebuffer;
  }

  @Override
  public AreaInclusiveUnsignedLType area()
  {
    return this.desc.area();
  }

  @Override
  public R2MaskBufferDescriptionType description()
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

      if (this.t_depth != null) {
        this.t_depth.delete(g);
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

    JCGLRenderStates.activate(g, CLEAR_STATE);
    g.getClear().clear(CLEAR_SPEC);
  }
}
