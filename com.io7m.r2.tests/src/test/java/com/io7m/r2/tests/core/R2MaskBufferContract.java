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

package com.io7m.r2.tests.core;

import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLTexture2DType;
import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Pair;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.r2.core.R2DepthAttachmentCreate;
import com.io7m.r2.core.R2DepthAttachmentShare;
import com.io7m.r2.core.R2DepthPrecision;
import com.io7m.r2.core.R2ExceptionBadTextureSize;
import com.io7m.r2.core.R2MaskBufferDescription;
import com.io7m.r2.core.R2MaskBufferType;
import com.io7m.r2.core.R2Texture2DStatic;
import com.io7m.r2.core.R2Texture2DUsableType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST;
import static com.io7m.jcanephora.core.JCGLTextureFormat.TEXTURE_FORMAT_DEPTH_16_2BPP;
import static com.io7m.jcanephora.core.JCGLTextureFormat.TEXTURE_FORMAT_DEPTH_24_4BPP;
import static com.io7m.jcanephora.core.JCGLTextureFormat.TEXTURE_FORMAT_DEPTH_24_STENCIL_8_4BPP;
import static com.io7m.jcanephora.core.JCGLTextureFormat.TEXTURE_FORMAT_R_8_1BPP;
import static com.io7m.jcanephora.core.JCGLTextureWrapS.TEXTURE_WRAP_REPEAT;
import static com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator.newAllocatorWithStack;
import static com.io7m.r2.core.R2MaskBuffer.create;
import static com.io7m.r2.core.R2MaskBufferDescription.of;
import static java.util.Optional.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class R2MaskBufferContract extends R2JCGLContract
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test
  public final void testIdentities()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final JCGLTextureUnitAllocatorType a =
      newAllocatorWithStack(
        4, g.textures().textureGetUnits());

    final AreaSizeL area = AreaSizeL.of(640L, 480L);
    final R2MaskBufferDescription desc = of(area, empty());

    final R2MaskBufferType gb =
      create(
        g.framebuffers(),
        g.textures(),
        a.rootContext(),
        desc);

    Assert.assertEquals(640L * 480L * 1L, gb.byteRange().getInterval());
    assertFalse(gb.isDeleted());

    final R2Texture2DUsableType t_r =
      gb.maskTexture();
    final JCGLFramebufferUsableType fb =
      gb.primaryFramebuffer();

    assertEquals(desc, gb.description());
    Assert.assertEquals(area, gb.size());

    Assert.assertEquals(
      TEXTURE_FORMAT_R_8_1BPP,
      t_r.texture().format());

    gb.delete(g);
    assertTrue(fb.isDeleted());
    assertTrue(gb.isDeleted());
  }

  @Test
  public final void testIdentitiesWithDepthCreate()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final JCGLTextureUnitAllocatorType a =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        4, g.textures().textureGetUnits());

    for (final R2DepthPrecision dp : R2DepthPrecision.values()) {
      final AreaSizeL area = AreaSizeL.of(640L, 480L);
      final R2MaskBufferDescription desc =
        of(area, Optional.of(R2DepthAttachmentCreate.of(dp)));

      final R2MaskBufferType gb =
        create(
          g.framebuffers(),
          g.textures(),
          a.rootContext(),
          desc);

      long pixel_size = 1L;
      switch (dp) {
        case R2_DEPTH_PRECISION_16:
          pixel_size += 2L;
          break;
        case R2_DEPTH_PRECISION_24:
          pixel_size += 4L;
          break;
        case R2_DEPTH_PRECISION_32F:
          pixel_size += 4L;
          break;
      }

      Assert.assertEquals(
        640L * 480L * pixel_size,
        gb.byteRange().getInterval());
      assertFalse(gb.isDeleted());

      final R2Texture2DUsableType t_r =
        gb.maskTexture();
      final JCGLFramebufferUsableType fb =
        gb.primaryFramebuffer();

      assertEquals(desc, gb.description());
      Assert.assertEquals(area, gb.size());

      Assert.assertEquals(
        TEXTURE_FORMAT_R_8_1BPP,
        t_r.texture().format());

      gb.delete(g);
      assertTrue(fb.isDeleted());
      assertTrue(gb.isDeleted());
    }
  }

  @Test
  public final void testIdentitiesWithDepthShare()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final JCGLTextureUnitAllocatorType a =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        4, g.textures().textureGetUnits());

    for (final R2DepthPrecision dp : R2DepthPrecision.values()) {
      final AreaSizeL area = AreaSizeL.of(640L, 480L);
      final JCGLTextureUnitContextParentType tc = a.rootContext();
      final JCGLTextureUnitContextType tcc = tc.unitContextNew();

      JCGLTextureFormat format = null;
      switch (dp) {
        case R2_DEPTH_PRECISION_16:
          format = TEXTURE_FORMAT_DEPTH_16_2BPP;
          break;
        case R2_DEPTH_PRECISION_24:
          format = TEXTURE_FORMAT_DEPTH_24_4BPP;
          break;
        case R2_DEPTH_PRECISION_32F:
          format = TEXTURE_FORMAT_DEPTH_24_STENCIL_8_4BPP;
          break;
      }

      final Pair<JCGLTextureUnitType, JCGLTexture2DType> t_depth =
        tcc.unitContextAllocateTexture2D(
          g.textures(),
          area.sizeX(),
          area.sizeY(),
          format,
          TEXTURE_WRAP_REPEAT,
          JCGLTextureWrapT.TEXTURE_WRAP_REPEAT,
          TEXTURE_FILTER_NEAREST,
          JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);

      tcc.unitContextFinish(g.textures());

      final R2MaskBufferDescription desc =
        of(
          area, Optional.of(R2DepthAttachmentShare.of(
            R2Texture2DStatic.of(t_depth.getRight()))));

      final R2MaskBufferType gb =
        create(
          g.framebuffers(),
          g.textures(),
          a.rootContext(),
          desc);

      long pixel_size = 1L;
      switch (dp) {
        case R2_DEPTH_PRECISION_16:
          pixel_size += 2L;
          break;
        case R2_DEPTH_PRECISION_24:
          pixel_size += 4L;
          break;
        case R2_DEPTH_PRECISION_32F:
          pixel_size += 4L;
          break;
      }

      Assert.assertEquals(
        640L * 480L * pixel_size,
        gb.byteRange().getInterval());
      assertFalse(gb.isDeleted());

      final R2Texture2DUsableType t_r =
        gb.maskTexture();
      final JCGLFramebufferUsableType fb =
        gb.primaryFramebuffer();

      assertEquals(desc, gb.description());
      Assert.assertEquals(area, gb.size());

      Assert.assertEquals(
        TEXTURE_FORMAT_R_8_1BPP,
        t_r.texture().format());

      gb.delete(g);
      assertTrue(fb.isDeleted());
      assertTrue(gb.isDeleted());
    }
  }

  @Test
  public final void testIdentitiesWithDepthShareBadSize()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final JCGLTextureUnitAllocatorType a =
      newAllocatorWithStack(
        4, g.textures().textureGetUnits());

    final AreaSizeL area = AreaSizeL.of(640L, 480L);

    final AreaSizeL area_shared = AreaSizeL.of(16L, 16L);

    final JCGLTextureUnitContextParentType tc = a.rootContext();
    final JCGLTextureUnitContextType tcc = tc.unitContextNew();

    final Pair<JCGLTextureUnitType, JCGLTexture2DType> t_depth =
      tcc.unitContextAllocateTexture2D(
        g.textures(),
        area_shared.sizeX(),
        area_shared.sizeY(),
        TEXTURE_FORMAT_DEPTH_24_4BPP,
        TEXTURE_WRAP_REPEAT,
        JCGLTextureWrapT.TEXTURE_WRAP_REPEAT,
        TEXTURE_FILTER_NEAREST,
        JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);

    tcc.unitContextFinish(g.textures());

    final R2MaskBufferDescription desc =
      of(
        area, Optional.of(R2DepthAttachmentShare.of(
          R2Texture2DStatic.of(t_depth.getRight()))));

    this.expected.expect(R2ExceptionBadTextureSize.class);
    create(
      g.framebuffers(),
      g.textures(),
      a.rootContext(),
      desc);
  }

  @Test
  public final void testIdentitiesWithDepthShareBadFormat()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final JCGLTextureUnitAllocatorType a =
      newAllocatorWithStack(
        4, g.textures().textureGetUnits());

    final AreaSizeL area = AreaSizeL.of(640L, 480L);
    final AreaSizeL area_shared = AreaSizeL.of(16L, 16L);

    final JCGLTextureUnitContextParentType tc = a.rootContext();
    final JCGLTextureUnitContextType tcc = tc.unitContextNew();

    final Pair<JCGLTextureUnitType, JCGLTexture2DType> t_depth =
      tcc.unitContextAllocateTexture2D(
        g.textures(),
        area_shared.sizeX(),
        area_shared.sizeY(),
        TEXTURE_FORMAT_R_8_1BPP,
        TEXTURE_WRAP_REPEAT,
        JCGLTextureWrapT.TEXTURE_WRAP_REPEAT,
        TEXTURE_FILTER_NEAREST,
        JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);

    tcc.unitContextFinish(g.textures());

    final R2MaskBufferDescription desc =
      of(
        area, Optional.of(R2DepthAttachmentShare.of(
          R2Texture2DStatic.of(t_depth.getRight()))));

    this.expected.expect(R2ExceptionBadTextureSize.class);
    create(
      g.framebuffers(),
      g.textures(),
      a.rootContext(),
      desc);
  }
}
