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
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * Default implementation of the {@link R2LightBufferType} interface.
 */

public final class R2LightBuffers
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
      JCGLColorBufferMaskingState.of(true, true, true, true));
    CLEAR_STATE = JCGLRenderState.builder().from(k).build();

    CLEAR_SPEC = JCGLClearSpecification.of(
      Optional.of(new VectorI4F(0.0f, 0.0f, 0.0f, 0.0f)),
      OptionalDouble.of(1.0),
      OptionalInt.empty(),
      true);
  }

  private R2LightBuffers()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Construct a new light buffer.
   *
   * @param g_fb A framebuffer interface
   * @param g_t  A texture interface
   * @param tc   A texture unit context
   * @param desc The light buffer description
   *
   * @return A new light buffer
   */

  public static R2LightBufferType newLightBuffer(
    final JCGLFramebuffersType g_fb,
    final JCGLTexturesType g_t,
    final JCGLTextureUnitContextParentType tc,
    final R2LightBufferDescriptionType desc)
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

    final JCGLTextureUnitContextType cc = tc.unitContextNewWithReserved(3);
    try {
      final Pair<JCGLTextureUnitType, JCGLTexture2DType> p_depth =
        cc.unitContextAllocateTexture2D(
          g_t,
          range_x.getInterval(),
          range_y.getInterval(),
          JCGLTextureFormat.TEXTURE_FORMAT_DEPTH_24_STENCIL_8_4BPP,
          JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
          JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

      switch (desc.components()) {
        case R2_LIGHT_BUFFER_DIFFUSE_ONLY: {
          final Pair<JCGLTextureUnitType, JCGLTexture2DType> p_diff =
            cc.unitContextAllocateTexture2D(
              g_t,
              range_x.getInterval(),
              range_y.getInterval(),
              JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
              JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
              JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
              JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
              JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

          return R2LightBuffers.newDiffuseOnly(
            g_fb,
            desc,
            points,
            buffers,
            p_depth,
            p_diff);
        }
        case R2_LIGHT_BUFFER_SPECULAR_ONLY: {
          final Pair<JCGLTextureUnitType, JCGLTexture2DType> p_spec =
            cc.unitContextAllocateTexture2D(
              g_t,
              range_x.getInterval(),
              range_y.getInterval(),
              JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
              JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
              JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
              JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
              JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

          return R2LightBuffers.newSpecularOnly(
            g_fb,
            desc,
            points,
            buffers,
            p_depth,
            p_spec);
        }
        case R2_LIGHT_BUFFER_DIFFUSE_AND_SPECULAR: {
          final Pair<JCGLTextureUnitType, JCGLTexture2DType> p_diff =
            cc.unitContextAllocateTexture2D(
              g_t,
              range_x.getInterval(),
              range_y.getInterval(),
              JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
              JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
              JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
              JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
              JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

          final Pair<JCGLTextureUnitType, JCGLTexture2DType> p_spec =
            cc.unitContextAllocateTexture2D(
              g_t,
              range_x.getInterval(),
              range_y.getInterval(),
              JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
              JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
              JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
              JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
              JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

          return R2LightBuffers.newDiffuseSpecular(
            g_fb,
            desc,
            points,
            buffers,
            p_depth,
            p_diff,
            p_spec);
        }
      }

      throw new UnreachableCodeException();
    } finally {
      cc.unitContextFinish(g_t);
    }
  }

  private static R2LightBufferType newDiffuseSpecular(
    final JCGLFramebuffersType g_fb,
    final R2LightBufferDescriptionType desc,
    final List<JCGLFramebufferColorAttachmentPointType> points,
    final List<JCGLFramebufferDrawBufferType> buffers,
    final Pair<JCGLTextureUnitType, JCGLTexture2DType> p_depth,
    final Pair<JCGLTextureUnitType, JCGLTexture2DType> p_diff,
    final Pair<JCGLTextureUnitType, JCGLTexture2DType> p_spec)
  {
    final R2Texture2DType rt_depth = R2Texture2DStatic.of(p_depth.getRight());
    final R2Texture2DType rt_diff = R2Texture2DStatic.of(p_diff.getRight());
    final R2Texture2DType rt_spec = R2Texture2DStatic.of(p_spec.getRight());

    final JCGLFramebufferBuilderType fbb = g_fb.framebufferNewBuilder();
    fbb.attachColorTexture2DAt(
      points.get(0),
      buffers.get(0),
      rt_diff.texture());
    fbb.attachColorTexture2DAt(
      points.get(1),
      buffers.get(1),
      rt_spec.texture());
    fbb.attachDepthStencilTexture2D(rt_depth.texture());

    final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
    return new R2LightBuffers.LightBufferDiffuseSpecular(
      fb, desc, rt_depth, rt_diff, rt_spec);
  }

  private static R2LightBufferType newSpecularOnly(
    final JCGLFramebuffersType g_fb,
    final R2LightBufferDescriptionType desc,
    final List<JCGLFramebufferColorAttachmentPointType> points,
    final List<JCGLFramebufferDrawBufferType> buffers,
    final Pair<JCGLTextureUnitType, JCGLTexture2DType> p_depth,
    final Pair<JCGLTextureUnitType, JCGLTexture2DType> p_spec)
  {
    final R2Texture2DType rt_depth = R2Texture2DStatic.of(p_depth.getRight());
    final R2Texture2DType rt_spec = R2Texture2DStatic.of(p_spec.getRight());

    final JCGLFramebufferBuilderType fbb = g_fb.framebufferNewBuilder();
    fbb.attachColorTexture2DAt(
      points.get(1),
      buffers.get(1),
      rt_spec.texture());
    fbb.attachDepthStencilTexture2D(rt_depth.texture());

    final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
    return new R2LightBuffers.LightBufferSpecularOnly(
      fb, desc, rt_depth, rt_spec);
  }

  private static R2LightBufferType newDiffuseOnly(
    final JCGLFramebuffersType g_fb,
    final R2LightBufferDescriptionType desc,
    final List<JCGLFramebufferColorAttachmentPointType> points,
    final List<JCGLFramebufferDrawBufferType> buffers,
    final Pair<JCGLTextureUnitType, JCGLTexture2DType> p_depth,
    final Pair<JCGLTextureUnitType, JCGLTexture2DType> p_diff)
  {
    final R2Texture2DType rt_depth = R2Texture2DStatic.of(p_depth.getRight());
    final R2Texture2DType rt_diff = R2Texture2DStatic.of(p_diff.getRight());

    final JCGLFramebufferBuilderType fbb = g_fb.framebufferNewBuilder();
    fbb.attachColorTexture2DAt(
      points.get(0),
      buffers.get(0),
      rt_diff.texture());
    fbb.attachDepthStencilTexture2D(rt_depth.texture());

    final JCGLFramebufferType fb = g_fb.framebufferAllocate(fbb);
    return new R2LightBuffers.LightBufferDiffuseOnly(
      fb, desc, rt_depth, rt_diff);
  }

  private static void clearFramebuffer(
    final JCGLInterfaceGL33Type g,
    final JCGLFramebufferUsableType framebuffer)
  {
    final JCGLFramebuffersType g_fb = g.getFramebuffers();

    if (!g_fb.framebufferDrawIsBound(framebuffer)) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Expected a framebuffer to be bound.");
      sb.append(System.lineSeparator());
      sb.append("Framebuffer: ");
      sb.append(framebuffer);
      sb.append(System.lineSeparator());
      throw new R2RendererExceptionFramebufferNotBound(sb.toString());
    }

    JCGLRenderStates.activate(g, R2LightBuffers.CLEAR_STATE);
    g.getClear().clear(R2LightBuffers.CLEAR_SPEC);
  }

  private static final class LightBufferDiffuseSpecular
    implements R2LightBufferType, R2LightBufferDiffuseSpecularUsableType
  {
    private final JCGLFramebufferType framebuffer;
    private final R2Texture2DType t_diffuse;
    private final R2Texture2DType t_specular;
    private final R2Texture2DType t_depth;
    private final R2LightBufferDescriptionType description;
    private final UnsignedRangeInclusiveL range;

    LightBufferDiffuseSpecular(
      final JCGLFramebufferType in_framebuffer,
      final R2LightBufferDescriptionType in_desc,
      final R2Texture2DType in_t_depth,
      final R2Texture2DType in_t_diffuse,
      final R2Texture2DType in_t_specular)
    {
      this.framebuffer = NullCheck.notNull(in_framebuffer);
      this.t_diffuse = NullCheck.notNull(in_t_diffuse);
      this.t_specular = NullCheck.notNull(in_t_specular);
      this.t_depth = NullCheck.notNull(in_t_depth);
      this.description = NullCheck.notNull(in_desc);

      long size = 0L;
      size += this.t_diffuse.texture().getRange().getInterval();
      size += this.t_specular.texture().getRange().getInterval();
      size += this.t_depth.texture().getRange().getInterval();
      this.range = new UnsignedRangeInclusiveL(0L, size - 1L);
    }

    @Override
    public R2Texture2DUsableType diffuseTexture()
    {
      return this.t_diffuse;
    }

    @Override
    public R2Texture2DUsableType specularTexture()
    {
      return this.t_specular;
    }

    @Override
    public JCGLFramebufferUsableType primaryFramebuffer()
    {
      return this.framebuffer;
    }

    @Override
    public AreaInclusiveUnsignedLType area()
    {
      return this.description.area();
    }

    @Override
    public R2LightBufferDescriptionType description()
    {
      return this.description;
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
        this.t_diffuse.delete(g);
        this.t_depth.delete(g);
        this.t_specular.delete(g);

        final JCGLFramebuffersType g_fb = g.getFramebuffers();
        g_fb.framebufferDelete(this.framebuffer);
      }
    }

    @Override
    public void clearBoundPrimaryFramebuffer(
      final JCGLInterfaceGL33Type g)
      throws R2RendererExceptionFramebufferNotBound
    {
      R2LightBuffers.clearFramebuffer(g, this.framebuffer);
    }
  }

  private static final class LightBufferSpecularOnly implements
    R2LightBufferType,
    R2LightBufferSpecularOnlyUsableType
  {
    private final JCGLFramebufferType framebuffer;
    private final R2Texture2DType t_specular;
    private final R2Texture2DType t_depth;
    private final R2LightBufferDescriptionType description;
    private final UnsignedRangeInclusiveL range;

    LightBufferSpecularOnly(
      final JCGLFramebufferType in_framebuffer,
      final R2LightBufferDescriptionType in_desc,
      final R2Texture2DType in_t_depth,
      final R2Texture2DType in_t_specular)
    {
      this.framebuffer = NullCheck.notNull(in_framebuffer);
      this.t_specular = NullCheck.notNull(in_t_specular);
      this.t_depth = NullCheck.notNull(in_t_depth);
      this.description = NullCheck.notNull(in_desc);

      long size = 0L;
      size += this.t_specular.texture().getRange().getInterval();
      size += this.t_depth.texture().getRange().getInterval();
      this.range = new UnsignedRangeInclusiveL(0L, size - 1L);
    }

    @Override
    public R2Texture2DUsableType specularTexture()
    {
      return this.t_specular;
    }

    @Override
    public JCGLFramebufferUsableType primaryFramebuffer()
    {
      return this.framebuffer;
    }

    @Override
    public AreaInclusiveUnsignedLType area()
    {
      return this.description.area();
    }

    @Override
    public R2LightBufferDescriptionType description()
    {
      return this.description;
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
        this.t_depth.delete(g);
        this.t_specular.delete(g);

        final JCGLFramebuffersType g_fb = g.getFramebuffers();
        g_fb.framebufferDelete(this.framebuffer);
      }
    }

    @Override
    public void clearBoundPrimaryFramebuffer(
      final JCGLInterfaceGL33Type g)
      throws R2RendererExceptionFramebufferNotBound
    {
      R2LightBuffers.clearFramebuffer(g, this.framebuffer);
    }
  }

  private static final class LightBufferDiffuseOnly implements
    R2LightBufferType,
    R2LightBufferDiffuseOnlyUsableType
  {
    private final JCGLFramebufferType framebuffer;
    private final R2Texture2DType t_diffuse;
    private final R2Texture2DType t_depth;
    private final R2LightBufferDescriptionType description;
    private final UnsignedRangeInclusiveL range;

    LightBufferDiffuseOnly(
      final JCGLFramebufferType in_framebuffer,
      final R2LightBufferDescriptionType in_desc,
      final R2Texture2DType in_t_depth,
      final R2Texture2DType in_t_diffuse)
    {
      this.framebuffer = NullCheck.notNull(in_framebuffer);
      this.t_diffuse = NullCheck.notNull(in_t_diffuse);
      this.t_depth = NullCheck.notNull(in_t_depth);
      this.description = NullCheck.notNull(in_desc);

      long size = 0L;
      size += this.t_diffuse.texture().getRange().getInterval();
      size += this.t_depth.texture().getRange().getInterval();
      this.range = new UnsignedRangeInclusiveL(0L, size - 1L);
    }

    @Override
    public R2Texture2DUsableType diffuseTexture()
    {
      return this.t_diffuse;
    }

    @Override
    public JCGLFramebufferUsableType primaryFramebuffer()
    {
      return this.framebuffer;
    }

    @Override
    public AreaInclusiveUnsignedLType area()
    {
      return this.description.area();
    }

    @Override
    public R2LightBufferDescriptionType description()
    {
      return this.description;
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
        this.t_diffuse.delete(g);
        this.t_depth.delete(g);

        final JCGLFramebuffersType g_fb = g.getFramebuffers();
        g_fb.framebufferDelete(this.framebuffer);
      }
    }

    @Override
    public void clearBoundPrimaryFramebuffer(
      final JCGLInterfaceGL33Type g)
      throws R2RendererExceptionFramebufferNotBound
    {
      R2LightBuffers.clearFramebuffer(g, this.framebuffer);
    }
  }
}
