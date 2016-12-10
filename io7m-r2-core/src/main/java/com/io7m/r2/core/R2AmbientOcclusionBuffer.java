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
import com.io7m.jcanephora.renderstate.JCGLDepthClamping;
import com.io7m.jcanephora.renderstate.JCGLDepthState;
import com.io7m.jcanephora.renderstate.JCGLDepthStrict;
import com.io7m.jcanephora.renderstate.JCGLDepthWriting;
import com.io7m.jcanephora.renderstate.JCGLRenderState;
import com.io7m.jcanephora.renderstate.JCGLRenderStateMutable;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.VectorI4F;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * Default implementation of the {@link R2AmbientOcclusionBufferType}
 * interface.
 */

public final class R2AmbientOcclusionBuffer implements
  R2AmbientOcclusionBufferType
{
  private static final JCGLRenderState CLEAR_STATE;
  private static final JCGLClearSpecification CLEAR_SPEC;

  static {
    final JCGLRenderStateMutable k = JCGLRenderStateMutable.create();
    k.setDepthState(JCGLDepthState.of(
      JCGLDepthStrict.DEPTH_STRICT_ENABLED,
      Optional.empty(),
      JCGLDepthWriting.DEPTH_WRITE_ENABLED,
      JCGLDepthClamping.DEPTH_CLAMP_ENABLED
    ));
    k.setColorBufferMaskingState(
      JCGLColorBufferMaskingState.of(true, false, false, false));
    CLEAR_STATE = JCGLRenderState.builder().from(k).build();

    CLEAR_SPEC = JCGLClearSpecification.of(
      Optional.of(new VectorI4F(1.0f, 0.0f, 0.0f, 0.0f)),
      OptionalDouble.of(1.0),
      OptionalInt.empty(),
      true);
  }

  private final R2Texture2DType t_occ;
  private final JCGLFramebufferType framebuffer;
  private final UnsignedRangeInclusiveL range;
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
    size += this.t_occ.texture().getRange().getInterval();
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
    final JCGLTextureUnitContextParentType tc,
    final R2AmbientOcclusionBufferDescriptionType d)
  {
    final List<JCGLFramebufferColorAttachmentPointType> points =
      g_fb.framebufferGetColorAttachments();
    final List<JCGLFramebufferDrawBufferType> buffers =
      g_fb.framebufferGetDrawBuffers();

    final AreaInclusiveUnsignedLType area = d.area();
    final UnsignedRangeInclusiveL range_x = area.getRangeX();
    final UnsignedRangeInclusiveL range_y = area.getRangeY();

    final JCGLTextureUnitContextType cc = tc.unitContextNewWithReserved(1);
    try {
      final Pair<JCGLTextureUnitType, JCGLTexture2DType> p_occ =
        cc.unitContextAllocateTexture2D(
          g_t,
          range_x.getInterval(),
          range_y.getInterval(),
          JCGLTextureFormat.TEXTURE_FORMAT_R_8_1BPP,
          JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
          JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

      final R2Texture2DType rt_occ = R2Texture2DStatic.of(p_occ.getRight());
      final JCGLFramebufferBuilderType fbb = g_fb.framebufferNewBuilder();
      fbb.attachColorTexture2DAt(points.get(0), buffers.get(0), rt_occ.texture());

      final R2AmbientOcclusionBufferDescription.Builder ab =
        R2AmbientOcclusionBufferDescription.builder();
      ab.from(d);

      final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
      return new R2AmbientOcclusionBuffer(fb, rt_occ, ab.build());
    } finally {
      cc.unitContextFinish(g_t);
    }
  }

  @Override
  public R2Texture2DUsableType ambientOcclusionTexture()
  {
    return this.t_occ;
  }

  @Override
  public JCGLFramebufferUsableType primaryFramebuffer()
  {
    return this.framebuffer;
  }

  @Override
  public AreaInclusiveUnsignedLType area()
  {
    return this.t_occ.texture().textureGetArea();
  }

  @Override
  public R2AmbientOcclusionBufferDescriptionType description()
  {
    return this.desc;
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

    JCGLRenderStates.activate(g, R2AmbientOcclusionBuffer.CLEAR_STATE);
    g.getClear().clear(R2AmbientOcclusionBuffer.CLEAR_SPEC);
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
