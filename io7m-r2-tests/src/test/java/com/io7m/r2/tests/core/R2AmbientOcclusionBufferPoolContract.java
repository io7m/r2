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

import com.io7m.jareas.core.AreaInclusiveUnsignedL;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jpuddle.core.JPPoolHardLimitExceededException;
import com.io7m.jpuddle.core.JPPoolObjectReturnException;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2AmbientOcclusionBuffer;
import com.io7m.r2.core.R2AmbientOcclusionBufferDescription;
import com.io7m.r2.core.R2AmbientOcclusionBufferDescriptionType;
import com.io7m.r2.core.R2AmbientOcclusionBufferPool;
import com.io7m.r2.core.R2AmbientOcclusionBufferType;
import com.io7m.r2.core.R2AmbientOcclusionBufferUsableType;
import com.io7m.r2.core.R2RenderTargetPoolType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public abstract class R2AmbientOcclusionBufferPoolContract extends
  R2JCGLContract
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test
  public final void testIdentities()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

    final JCGLTextureUnitAllocatorType tc =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g.getTextures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc_root =
      tc.getRootContext();

    final R2RenderTargetPoolType<
      R2AmbientOcclusionBufferDescriptionType,
      R2AmbientOcclusionBufferUsableType> p =
      R2AmbientOcclusionBufferPool.newPool(g, 0L, 8L * 8L);

    Assert.assertFalse(p.isDeleted());
    p.delete(tc_root);
    Assert.assertTrue(p.isDeleted());
  }

  @Test
  public final void testGetReuseReturn()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

    final JCGLTextureUnitAllocatorType tc =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g.getTextures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc_root =
      tc.getRootContext();

    final R2RenderTargetPoolType<
      R2AmbientOcclusionBufferDescriptionType,
      R2AmbientOcclusionBufferUsableType> p =
      R2AmbientOcclusionBufferPool.newPool(g, 8L * 8L, 8L * 8L);

    Assert.assertFalse(p.isDeleted());

    final AreaInclusiveUnsignedL area =
      AreaInclusiveUnsignedL.of(
        new UnsignedRangeInclusiveL(0L, 7L),
        new UnsignedRangeInclusiveL(0L, 7L));

    final R2AmbientOcclusionBufferDescription desc =
      R2AmbientOcclusionBufferDescription.of(area);

    final R2AmbientOcclusionBufferUsableType b0 = p.get(tc_root, desc);
    Assert.assertFalse(b0.isDeleted());
    Assert.assertEquals(area, b0.area());

    p.returnValue(tc_root, b0);

    final R2AmbientOcclusionBufferUsableType b1 = p.get(tc_root, desc);
    Assert.assertFalse(b1.isDeleted());
    Assert.assertEquals(area, b1.area());
    Assert.assertEquals(b0, b1);

    p.returnValue(tc_root, b1);
  }

  @Test
  public final void testReturnInvalid()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

    final JCGLTextureUnitAllocatorType tc =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g.getTextures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc_root =
      tc.getRootContext();
    final JCGLTextureUnitContextType tc_alloc =
      tc_root.unitContextNew();

    final R2RenderTargetPoolType<
      R2AmbientOcclusionBufferDescriptionType,
      R2AmbientOcclusionBufferUsableType> p =
      R2AmbientOcclusionBufferPool.newPool(
        g,
        0L,
        8L * 8L);

    final AreaInclusiveUnsignedL area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 639L),
      new UnsignedRangeInclusiveL(0L, 479L));

    final R2AmbientOcclusionBufferDescription.Builder db =
      R2AmbientOcclusionBufferDescription.builder();
    db.setArea(area);

    final R2AmbientOcclusionBufferType ab =
      R2AmbientOcclusionBuffer.newAmbientOcclusionBuffer(
        g.getFramebuffers(),
        g.getTextures(),
        tc_alloc,
        db.build());

    this.expected.expect(JPPoolObjectReturnException.class);
    p.returnValue(tc_alloc, ab);
  }

  @Test
  public final void testOverflow()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

    final JCGLTextureUnitAllocatorType tc =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g.getTextures().textureGetUnits());

    final JCGLTextureUnitContextParentType tc_root = tc.getRootContext();
    final JCGLTextureUnitContextType tc_alloc = tc_root.unitContextNew();

    final R2RenderTargetPoolType<
      R2AmbientOcclusionBufferDescriptionType,
      R2AmbientOcclusionBufferUsableType> p = R2AmbientOcclusionBufferPool
      .newPool(
        g,
        0L,
        8L * 8L);

    final AreaInclusiveUnsignedL area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 15L),
      new UnsignedRangeInclusiveL(0L, 15L));

    final R2AmbientOcclusionBufferDescription.Builder db =
      R2AmbientOcclusionBufferDescription.builder();
    db.setArea(area);

    this.expected.expect(JPPoolHardLimitExceededException.class);
    p.get(tc_root, db.build());
  }
}
