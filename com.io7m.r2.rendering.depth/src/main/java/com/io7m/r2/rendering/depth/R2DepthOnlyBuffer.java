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

package com.io7m.r2.rendering.depth;

import com.io7m.jcanephora.core.JCGLClearSpecification;
import com.io7m.jcanephora.core.JCGLFramebufferBuilderType;
import com.io7m.jcanephora.core.JCGLFramebufferType;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLTexture2DType;
import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.renderstate.JCGLColorBufferMaskingState;
import com.io7m.jcanephora.renderstate.JCGLDepthClamping;
import com.io7m.jcanephora.renderstate.JCGLDepthState;
import com.io7m.jcanephora.renderstate.JCGLDepthStrict;
import com.io7m.jcanephora.renderstate.JCGLDepthWriting;
import com.io7m.jcanephora.renderstate.JCGLRenderState;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.api.R2Exception;
import com.io7m.r2.rendering.api.R2RendererExceptionFramebufferNotBound;
import com.io7m.r2.rendering.depth.api.R2DepthOnlyBufferDescription;
import com.io7m.r2.rendering.depth.api.R2DepthOnlyBufferType;
import com.io7m.r2.rendering.depth.api.R2DepthPrecision;
import com.io7m.r2.textures.R2Texture2DStatic;
import com.io7m.r2.textures.R2Texture2DType;
import com.io7m.r2.textures.R2Texture2DUsableType;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import static com.io7m.jcanephora.core.JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST;
import static com.io7m.jcanephora.core.JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE;

/**
 * Default implementation of the {@link R2DepthOnlyBufferType} interface.
 */

public final class R2DepthOnlyBuffer implements R2DepthOnlyBufferType
{
  private static final JCGLRenderState CLEAR_STATE;
  private static final JCGLClearSpecification CLEAR_SPEC;

  static {
    CLEAR_STATE = JCGLRenderState.builder()
      .setDepthState(JCGLDepthState.of(
        JCGLDepthStrict.DEPTH_STRICT_ENABLED,
        Optional.empty(),
        JCGLDepthWriting.DEPTH_WRITE_ENABLED,
        JCGLDepthClamping.DEPTH_CLAMP_ENABLED))
      .setColorBufferMaskingState(
        JCGLColorBufferMaskingState.of(false, false, false, false))
      .build();

    CLEAR_SPEC = JCGLClearSpecification.of(
      Optional.empty(),
      OptionalDouble.of(1.0),
      OptionalInt.empty(),
      true);
  }

  private final R2Texture2DType t_depth;
  private final JCGLFramebufferType framebuffer;
  private final UnsignedRangeInclusiveL range;
  private final R2DepthOnlyBufferDescription desc;

  private R2DepthOnlyBuffer(
    final JCGLFramebufferType in_framebuffer,
    final R2DepthOnlyBufferDescription in_desc,
    final R2Texture2DType in_t_depth)
  {
    this.framebuffer =
      NullCheck.notNull(in_framebuffer, "Framebuffer");
    this.desc =
      NullCheck.notNull(in_desc, "Description");
    this.t_depth =
      NullCheck.notNull(in_t_depth, "Depth texture");

    long size = 0L;
    size += this.t_depth.texture().byteRange().getInterval();
    this.range = new UnsignedRangeInclusiveL(0L, size - 1L);
  }

  /**
   * Construct a new depth-only buffer.
   *
   * @param g_fb A framebuffer interface
   * @param g_t  A texture interface
   * @param tc   A texture unit context
   * @param desc The geometry buffer description
   *
   * @return A new geometry buffer
   */

  public static R2DepthOnlyBuffer create(
    final JCGLFramebuffersType g_fb,
    final JCGLTexturesType g_t,
    final JCGLTextureUnitContextParentType tc,
    final R2DepthOnlyBufferDescription desc)
  {
    NullCheck.notNull(g_fb, "Framebuffers");
    NullCheck.notNull(g_t, "Textures");
    NullCheck.notNull(tc, "Texture context");
    NullCheck.notNull(desc, "Description");

    final JCGLTextureUnitContextType cc = tc.unitContextNewWithReserved(4);
    try {
      final AreaSizeL area = desc.area();
      final Pair<JCGLTextureUnitType, JCGLTexture2DType> p_depth =
        cc.unitContextAllocateTexture2D(
          g_t,
          area.sizeX(),
          area.sizeY(),
          formatForPrecision(desc.depthPrecision()),
          TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
          TEXTURE_FILTER_NEAREST,
          JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);

      final R2Texture2DType rt_depth = R2Texture2DStatic.of(p_depth.getRight());

      final JCGLFramebufferBuilderType fbb = g_fb.framebufferNewBuilder();
      fbb.attachDepthTexture2D(rt_depth.texture());

      final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
      return new R2DepthOnlyBuffer(fb, desc, rt_depth);
    } finally {
      cc.unitContextFinish(g_t);
    }
  }

  private static JCGLTextureFormat formatForPrecision(
    final R2DepthPrecision p)
  {
    switch (p) {
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
  public R2Texture2DUsableType depthTexture()
  {
    return this.t_depth;
  }

  @Override
  public JCGLFramebufferUsableType primaryFramebuffer()
  {
    return this.framebuffer;
  }

  @Override
  public AreaSizeL size()
  {
    return this.t_depth.texture().size();
  }

  @Override
  public R2DepthOnlyBufferDescription description()
  {
    return this.desc;
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
      this.t_depth.delete(g);

      final JCGLFramebuffersType g_fb = g.framebuffers();
      g_fb.framebufferDelete(this.framebuffer);
    }
  }
}
