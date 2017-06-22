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
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Pair;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jtensors.core.unparameterized.vectors.Vector4D;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import static com.io7m.jcanephora.core.JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR;
import static com.io7m.jcanephora.core.JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST;
import static com.io7m.jcanephora.core.JCGLTextureFormat.TEXTURE_FORMAT_DEPTH_24_STENCIL_8_4BPP;
import static com.io7m.jcanephora.core.JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP;
import static com.io7m.jcanephora.core.JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE;

/**
 * Default implementation of the {@link R2ImageBufferType} interface.
 */

public final class R2ImageBuffer implements R2ImageBufferType
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
      Optional.of(Vector4D.of(0.0, 0.0, 0.0, 0.0)),
      OptionalDouble.empty(),
      OptionalInt.empty(),
      true);
  }

  private final R2Texture2DType t_rgba;
  private final JCGLFramebufferType framebuffer;
  private final UnsignedRangeInclusiveL range;
  private final R2ImageBufferDescription desc;
  private final @Nullable R2Texture2DType t_depth;

  private R2ImageBuffer(
    final JCGLFramebufferType in_framebuffer,
    final R2ImageBufferDescription in_desc,
    final R2Texture2DType in_t_rgba,
    final @Nullable R2Texture2DType in_t_depth,
    final @Nullable R2Texture2DUsableType in_t_depth_shared)
  {
    this.framebuffer =
      NullCheck.notNull(in_framebuffer, "Framebuffer");
    this.desc =
      NullCheck.notNull(in_desc, "Description");
    this.t_rgba =
      NullCheck.notNull(in_t_rgba, "RGBA");

    this.t_depth = in_t_depth;

    long size = 0L;
    size += this.t_rgba.texture().byteRange().getInterval();

    if (this.t_depth != null) {
      size += this.t_depth.texture().byteRange().getInterval();
    }
    if (in_t_depth_shared != null) {
      size += in_t_depth_shared.texture().byteRange().getInterval();
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

  public static R2ImageBuffer create(
    final JCGLFramebuffersType g_fb,
    final JCGLTexturesType g_t,
    final JCGLTextureUnitContextParentType tc,
    final R2ImageBufferDescription desc)
  {
    NullCheck.notNull(g_fb, "Framebuffer");
    NullCheck.notNull(g_t, "Textures");
    NullCheck.notNull(tc, "Texture context");
    NullCheck.notNull(desc, "Description");

    final List<JCGLFramebufferColorAttachmentPointType> points =
      g_fb.framebufferGetColorAttachments();
    final List<JCGLFramebufferDrawBufferType> buffers =
      g_fb.framebufferGetDrawBuffers();

    final AreaSizeL area = desc.area();

    final JCGLTextureUnitContextType cc = tc.unitContextNew();
    try {
      final Pair<JCGLTextureUnitType, JCGLTexture2DType> p =
        cc.unitContextAllocateTexture2D(
          g_t,
          area.sizeX(),
          area.sizeY(),
          TEXTURE_FORMAT_RGBA_8_4BPP,
          TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
          TEXTURE_FILTER_LINEAR,
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
          (ignored, share) -> createImageBufferWithShared(
            g_fb, desc, area, fbb, rt, share),
          (ignored, create) -> createImageBufferWithCreated(
            g_fb, g_t, desc, area, cc, fbb, rt, create),
          (ignored, create) -> createImageBufferWithCreatedStencil(
            g_fb, g_t, desc, area, cc, fbb, rt, create));
      }

      final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
      return new R2ImageBuffer(fb, desc, rt, null, null);
    } finally {
      cc.unitContextFinish(g_t);
    }
  }

  private static R2ImageBuffer createImageBufferWithCreatedStencil(
    final JCGLFramebuffersType g_fb,
    final JCGLTexturesType g_t,
    final R2ImageBufferDescription desc,
    final AreaSizeL area,
    final JCGLTextureUnitContextType cc,
    final JCGLFramebufferBuilderType fbb,
    final R2Texture2DType rt,
    final R2DepthAttachmentCreateWithStencilType create)
  {
    final Pair<JCGLTextureUnitType, JCGLTexture2DType> pd =
      cc.unitContextAllocateTexture2D(
        g_t,
        area.sizeX(),
        area.sizeY(),
        TEXTURE_FORMAT_DEPTH_24_STENCIL_8_4BPP,
        TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
        TEXTURE_FILTER_NEAREST,
        JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    final JCGLTexture2DType td = pd.getRight();
    fbb.attachDepthStencilTexture2D(td);
    final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
    return new R2ImageBuffer(
      fb, desc, rt, R2Texture2DStatic.of(td), null);
  }

  private static R2ImageBuffer createImageBufferWithCreated(
    final JCGLFramebuffersType g_fb,
    final JCGLTexturesType g_t,
    final R2ImageBufferDescription desc,
    final AreaSizeL area,
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
        area.sizeX(),
        area.sizeY(),
        format,
        TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
        TEXTURE_FILTER_NEAREST,
        JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    final JCGLTexture2DType td = pd.getRight();
    fbb.attachDepthTexture2D(td);
    final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
    return new R2ImageBuffer(
      fb, desc, rt, R2Texture2DStatic.of(td), null);
  }

  private static R2ImageBuffer createImageBufferWithShared(
    final JCGLFramebuffersType g_fb,
    final R2ImageBufferDescription desc,
    final AreaSizeL area,
    final JCGLFramebufferBuilderType fbb,
    final R2Texture2DType rt,
    final R2DepthAttachmentShareType share)
  {
    final R2Texture2DUsableType dt = share.texture();

    final AreaSizeL depth_area = dt.texture().size();
    if (!Objects.equals(depth_area, area)) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append(
        "The size of a shared depth attachment must match the size of the color attachment.");
      sb.append(System.lineSeparator());
      sb.append("Color size: ");
      sb.append(area.sizeX());
      sb.append("x");
      sb.append(area.sizeY());
      sb.append(System.lineSeparator());
      sb.append("Depth size: ");
      sb.append(depth_area.sizeX());
      sb.append("x");
      sb.append(depth_area.sizeY());
      sb.append(System.lineSeparator());
      throw new R2ExceptionBadTextureSize(sb.toString());
    }

    final JCGLTextureFormat format = dt.texture().format();
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
    return new R2ImageBuffer(fb, desc, rt, null, dt);
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
  public R2Texture2DUsableType imageTexture()
  {
    return this.t_rgba;
  }

  @Override
  public JCGLFramebufferUsableType primaryFramebuffer()
  {
    return this.framebuffer;
  }

  @Override
  public AreaSizeL size()
  {
    return this.desc.area();
  }

  @Override
  public R2ImageBufferDescription description()
  {
    return this.desc;
  }

  @Override
  public UnsignedRangeInclusiveL byteRange()
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

      final JCGLFramebuffersType g_fb = g.framebuffers();
      g_fb.framebufferDelete(this.framebuffer);
    }
  }

  @Override
  public void clearBoundPrimaryFramebuffer(
    final JCGLInterfaceGL33Type g)
    throws R2RendererExceptionFramebufferNotBound
  {
    final JCGLFramebuffersType g_fb = g.framebuffers();

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
    g.clearing().clear(CLEAR_SPEC);
  }
}
