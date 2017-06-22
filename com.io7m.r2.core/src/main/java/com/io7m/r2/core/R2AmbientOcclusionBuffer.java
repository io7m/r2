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
import com.io7m.jtensors.core.unparameterized.vectors.Vector4D;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import static com.io7m.jcanephora.core.JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR;
import static com.io7m.jcanephora.core.JCGLTextureFormat.TEXTURE_FORMAT_R_8_1BPP;
import static com.io7m.jcanephora.core.JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE;

/**
 * Default implementation of the {@link R2AmbientOcclusionBufferType}
 * interface.
 */

public final class R2AmbientOcclusionBuffer
  implements R2AmbientOcclusionBufferType
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
        JCGLColorBufferMaskingState.of(true, false, false, false))
      .build();

    CLEAR_SPEC = JCGLClearSpecification.of(
      Optional.of(Vector4D.of(1.0, 0.0, 0.0, 0.0)),
      OptionalDouble.of(1.0),
      OptionalInt.empty(),
      true);
  }

  private final R2Texture2DType t_occ;
  private final JCGLFramebufferType framebuffer;
  private final UnsignedRangeInclusiveL range;
  private final R2AmbientOcclusionBufferDescription desc;

  private R2AmbientOcclusionBuffer(
    final JCGLFramebufferType in_framebuffer,
    final R2Texture2DType in_t_occ,
    final R2AmbientOcclusionBufferDescription in_desc)
  {
    this.framebuffer =
      NullCheck.notNull(in_framebuffer, "Framebuffer");
    this.t_occ =
      NullCheck.notNull(in_t_occ, "Occlusion texture");
    this.desc =
      NullCheck.notNull(in_desc, "Buffer description");

    long size = 0L;
    size += this.t_occ.texture().byteRange().getInterval();
    this.range = new UnsignedRangeInclusiveL(0L, size - 1L);
  }

  /**
   * Construct a new ambient occlusion buffer.
   *
   * @param g_fb        A framebuffer interface
   * @param g_t         A texture interface
   * @param tc          A texture unit context
   * @param description The buffer description
   *
   * @return A new ambient occlusion buffer
   */

  public static R2AmbientOcclusionBuffer create(
    final JCGLFramebuffersType g_fb,
    final JCGLTexturesType g_t,
    final JCGLTextureUnitContextParentType tc,
    final R2AmbientOcclusionBufferDescription description)
  {
    final List<JCGLFramebufferColorAttachmentPointType> points =
      g_fb.framebufferGetColorAttachments();
    final List<JCGLFramebufferDrawBufferType> buffers =
      g_fb.framebufferGetDrawBuffers();

    final JCGLTextureUnitContextType cc = tc.unitContextNewWithReserved(1);
    try {
      final AreaSizeL area = description.area();
      final Pair<JCGLTextureUnitType, JCGLTexture2DType> p_occ =
        cc.unitContextAllocateTexture2D(
          g_t,
          area.sizeX(),
          area.sizeY(),
          TEXTURE_FORMAT_R_8_1BPP,
          TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
          TEXTURE_FILTER_LINEAR,
          JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

      final R2Texture2DType rt_occ = R2Texture2DStatic.of(p_occ.getRight());
      final JCGLFramebufferBuilderType fbb = g_fb.framebufferNewBuilder();
      fbb.attachColorTexture2DAt(
        points.get(0),
        buffers.get(0),
        rt_occ.texture());

      final R2AmbientOcclusionBufferDescription.Builder ab =
        R2AmbientOcclusionBufferDescription.builder();
      ab.from(description);

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
  public AreaSizeL size()
  {
    return this.t_occ.texture().size();
  }

  @Override
  public R2AmbientOcclusionBufferDescription description()
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
      this.t_occ.delete(g);

      final JCGLFramebuffersType g_fb = g.framebuffers();
      g_fb.framebufferDelete(this.framebuffer);
    }
  }
}
