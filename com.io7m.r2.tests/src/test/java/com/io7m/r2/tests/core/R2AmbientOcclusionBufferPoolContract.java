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

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jpuddle.core.JPPoolHardLimitExceededException;
import com.io7m.jpuddle.core.JPPoolObjectReturnException;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.r2.core.R2AmbientOcclusionBufferDescription;
import com.io7m.r2.core.R2AmbientOcclusionBufferPool;
import com.io7m.r2.core.R2AmbientOcclusionBufferType;
import com.io7m.r2.core.R2AmbientOcclusionBufferUsableType;
import com.io7m.r2.core.R2RenderTargetPoolType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator.newAllocatorWithStack;
import static com.io7m.jregions.core.unparameterized.sizes.AreaSizeL.of;
import static com.io7m.r2.core.R2AmbientOcclusionBuffer.create;
import static com.io7m.r2.core.R2AmbientOcclusionBufferDescription.Builder;
import static com.io7m.r2.core.R2AmbientOcclusionBufferDescription.builder;
import static com.io7m.r2.core.R2AmbientOcclusionBufferPool.newPool;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
        8, g.textures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc_root =
      tc.rootContext();

    final R2RenderTargetPoolType<
      R2AmbientOcclusionBufferDescription,
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
      newAllocatorWithStack(
        8, g.textures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc_root =
      tc.rootContext();

    final R2RenderTargetPoolType<
      R2AmbientOcclusionBufferDescription,
      R2AmbientOcclusionBufferUsableType> p =
      newPool(g, 8L * 8L, 8L * 8L);

    assertFalse(p.isDeleted());

    final AreaSizeL area = of(8L, 8L);

    final R2AmbientOcclusionBufferDescription desc =
      R2AmbientOcclusionBufferDescription.of(area);

    final R2AmbientOcclusionBufferUsableType b0 = p.get(tc_root, desc);
    assertFalse(b0.isDeleted());
    assertEquals(area, b0.size());

    p.returnValue(tc_root, b0);

    final R2AmbientOcclusionBufferUsableType b1 = p.get(tc_root, desc);
    assertFalse(b1.isDeleted());
    assertEquals(area, b1.size());
    assertEquals(b0, b1);

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
      newAllocatorWithStack(
        8, g.textures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc_root =
      tc.rootContext();
    final JCGLTextureUnitContextType tc_alloc =
      tc_root.unitContextNew();

    final R2RenderTargetPoolType<
      R2AmbientOcclusionBufferDescription,
      R2AmbientOcclusionBufferUsableType> p =
      newPool(
        g,
        0L,
        8L * 8L);

    final AreaSizeL area = AreaSizeL.of(640L, 480L);

    final Builder db =
      builder();
    db.setArea(area);

    final R2AmbientOcclusionBufferType ab =
      create(
        g.framebuffers(),
        g.textures(),
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
      newAllocatorWithStack(
        8, g.textures().textureGetUnits());

    final JCGLTextureUnitContextParentType tc_root = tc.rootContext();
    final JCGLTextureUnitContextType tc_alloc = tc_root.unitContextNew();

    final R2RenderTargetPoolType<
      R2AmbientOcclusionBufferDescription,
      R2AmbientOcclusionBufferUsableType> p =
      newPool(
        g,
        0L,
        8L * 8L);

    final AreaSizeL area = of(16L, 16L);

    final Builder db =
      builder();
    db.setArea(area);

    this.expected.expect(JPPoolHardLimitExceededException.class);
    p.get(tc_root, db.build());
  }
}
