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
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jfunctional.Unit;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2ImageBuffer;
import com.io7m.r2.core.R2ImageBufferDescription;
import com.io7m.r2.core.R2ImageBufferDescriptionType;
import com.io7m.r2.core.R2ImageBufferType;
import com.io7m.r2.core.R2RenderTargetAllocatorFunctionType;
import com.io7m.r2.core.R2RenderTargetStackAllocationException;
import com.io7m.r2.core.R2RenderTargetStackDeletedException;
import com.io7m.r2.core.R2RenderTargetStackEmptyException;
import com.io7m.r2.core.R2RenderTargetStackInconsistentException;
import com.io7m.r2.core.R2RenderTargetStackType;
import com.io7m.r2.core.R2RenderTargetStackWrongTargetException;
import com.io7m.r2.core.R2TextureUnitAllocator;
import com.io7m.r2.core.R2TextureUnitAllocatorType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class R2RenderTargetStackContract extends R2JCGLContract
{
  @Rule public ExpectedException expected = ExpectedException.none();

  private static R2ImageBufferType newRenderTarget(
    final JCGLInterfaceGL33Type g33)
  {
    final R2TextureUnitAllocatorType tc =
      R2TextureUnitAllocator.newAllocatorWithStack(
        8, g33.getTextures().textureGetUnits());
    final R2ImageBufferDescriptionType desc = R2ImageBufferDescription.of(
      AreaInclusiveUnsignedL.of(
        new UnsignedRangeInclusiveL(0L, 1L),
        new UnsignedRangeInclusiveL(0L, 1L)));
    return R2ImageBuffer.newImageBuffer(
      g33.getFramebuffers(),
      g33.getTextures(),
      tc.getRootContext(),
      desc);
  }

  protected abstract R2RenderTargetStackType newStack(JCGLInterfaceGL33Type f);

  @Test
  public final void testStackUnbindDrawNothingBound()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final JCGLFramebuffersType g_fb = g33.getFramebuffers();
    final R2RenderTargetStackType st = this.newStack(g33);

    final R2ImageBufferType r0 =
      R2RenderTargetStackContract.newRenderTarget(g33);

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    this.expected.expect(R2RenderTargetStackEmptyException.class);
    st.renderTargetUnbindDraw(r0);
  }

  @Test
  public final void testStackUnbindDrawWrongBound()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final JCGLFramebuffersType g_fb = g33.getFramebuffers();
    final R2RenderTargetStackType st = this.newStack(g33);

    final R2ImageBufferType r0 =
      R2RenderTargetStackContract.newRenderTarget(g33);
    final R2ImageBufferType r1 =
      R2RenderTargetStackContract.newRenderTarget(g33);

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    st.renderTargetBindDraw(r0);
    Assert.assertTrue(g_fb.framebufferDrawIsBound(r0.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());

    this.expected.expect(R2RenderTargetStackWrongTargetException.class);
    st.renderTargetUnbindDraw(r1);
  }

  @Test
  public final void testStackBindDrawCorrect()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final JCGLFramebuffersType g_fb = g33.getFramebuffers();
    final R2RenderTargetStackType st = this.newStack(g33);

    final R2ImageBufferType r0 =
      R2RenderTargetStackContract.newRenderTarget(g33);
    final R2ImageBufferType r1 =
      R2RenderTargetStackContract.newRenderTarget(g33);
    final R2ImageBufferType r2 =
      R2RenderTargetStackContract.newRenderTarget(g33);

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    st.renderTargetBindDraw(r0);
    Assert.assertTrue(g_fb.framebufferDrawIsBound(r0.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertEquals(1L, (long) st.getDrawStackSize());
    Assert.assertEquals(0L, (long) st.getReadStackSize());

    st.renderTargetBindDraw(r1);
    Assert.assertTrue(g_fb.framebufferDrawIsBound(r1.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertEquals(2L, (long) st.getDrawStackSize());
    Assert.assertEquals(0L, (long) st.getReadStackSize());

    st.renderTargetBindDraw(r2);
    Assert.assertTrue(g_fb.framebufferDrawIsBound(r2.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertEquals(3L, (long) st.getDrawStackSize());
    Assert.assertEquals(0L, (long) st.getReadStackSize());

    st.renderTargetUnbindDraw(r2);
    Assert.assertTrue(g_fb.framebufferDrawIsBound(r1.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertEquals(2L, (long) st.getDrawStackSize());
    Assert.assertEquals(0L, (long) st.getReadStackSize());

    st.renderTargetUnbindDraw(r1);
    Assert.assertTrue(g_fb.framebufferDrawIsBound(r0.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertEquals(1L, (long) st.getDrawStackSize());
    Assert.assertEquals(0L, (long) st.getReadStackSize());

    st.renderTargetUnbindDraw(r0);
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());
    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertEquals(0L, (long) st.getDrawStackSize());
    Assert.assertEquals(0L, (long) st.getReadStackSize());
  }

  @Test
  public final void testStackBindDrawDeleted()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final JCGLFramebuffersType g_fb = g33.getFramebuffers();
    final R2RenderTargetStackType st = this.newStack(g33);

    final R2ImageBufferType r0 =
      R2RenderTargetStackContract.newRenderTarget(g33);

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    r0.delete(g33);

    this.expected.expect(R2RenderTargetStackDeletedException.class);
    st.renderTargetBindDraw(r0);
  }

  @Test
  public final void testStackBindReadDeleted()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final JCGLFramebuffersType g_fb = g33.getFramebuffers();
    final R2RenderTargetStackType st = this.newStack(g33);

    final R2ImageBufferType r0 =
      R2RenderTargetStackContract.newRenderTarget(g33);

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    r0.delete(g33);

    this.expected.expect(R2RenderTargetStackDeletedException.class);
    st.renderTargetBindRead(r0);
  }

  @Test
  public final void testStackBindDrawRestoreDeleted()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final JCGLFramebuffersType g_fb = g33.getFramebuffers();
    final R2RenderTargetStackType st = this.newStack(g33);

    final AtomicInteger deleted = new AtomicInteger(0);

    final R2ImageBufferType r0 =
      R2RenderTargetStackContract.newRenderTarget(g33);
    final R2ImageBufferType r1 =
      R2RenderTargetStackContract.newRenderTarget(g33);
    final R2ImageBufferType r2 =
      R2RenderTargetStackContract.newRenderTarget(g33);

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    st.renderTargetBindDraw(r0);
    Assert.assertTrue(g_fb.framebufferDrawIsBound(r0.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());

    st.renderTargetBindDraw(r1);
    Assert.assertTrue(g_fb.framebufferDrawIsBound(r1.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());

    st.renderTargetBindDraw(r2);
    Assert.assertTrue(g_fb.framebufferDrawIsBound(r2.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());

    r1.delete(g33);
    r0.delete(g33);

    try {
      st.renderTargetUnbindDraw(r2);
    } catch (final R2RenderTargetStackDeletedException e) {
      deleted.incrementAndGet();
    }

    Assert.assertEquals(1L, (long) deleted.get());
    Assert.assertEquals(2L, (long) st.getDrawStackSize());

    try {
      st.renderTargetUnbindDraw(r1);
    } catch (final R2RenderTargetStackDeletedException e) {
      deleted.incrementAndGet();
    }

    Assert.assertEquals(2L, (long) deleted.get());
    Assert.assertEquals(1L, (long) st.getDrawStackSize());

    try {
      st.renderTargetUnbindDraw(r0);
    } catch (final R2RenderTargetStackDeletedException e) {
      deleted.incrementAndGet();
    }

    Assert.assertEquals(3L, (long) deleted.get());
    Assert.assertEquals(0L, (long) st.getDrawStackSize());
  }

  @Test
  public final void testStackBindReadRestoreDeleted()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final JCGLFramebuffersType g_fb = g33.getFramebuffers();
    final R2RenderTargetStackType st = this.newStack(g33);

    final AtomicInteger deleted = new AtomicInteger(0);

    final R2ImageBufferType r0 =
      R2RenderTargetStackContract.newRenderTarget(g33);
    final R2ImageBufferType r1 =
      R2RenderTargetStackContract.newRenderTarget(g33);
    final R2ImageBufferType r2 =
      R2RenderTargetStackContract.newRenderTarget(g33);

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    st.renderTargetBindRead(r0);
    Assert.assertTrue(g_fb.framebufferReadIsBound(r0.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    st.renderTargetBindRead(r1);
    Assert.assertTrue(g_fb.framebufferReadIsBound(r1.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    st.renderTargetBindRead(r2);
    Assert.assertTrue(g_fb.framebufferReadIsBound(r2.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    r1.delete(g33);
    r0.delete(g33);

    try {
      st.renderTargetUnbindRead(r2);
    } catch (final R2RenderTargetStackDeletedException e) {
      deleted.incrementAndGet();
    }

    Assert.assertEquals(1L, (long) deleted.get());
    Assert.assertEquals(2L, (long) st.getReadStackSize());

    try {
      st.renderTargetUnbindRead(r1);
    } catch (final R2RenderTargetStackDeletedException e) {
      deleted.incrementAndGet();
    }

    Assert.assertEquals(2L, (long) deleted.get());
    Assert.assertEquals(1L, (long) st.getReadStackSize());

    try {
      st.renderTargetUnbindRead(r0);
    } catch (final R2RenderTargetStackDeletedException e) {
      deleted.incrementAndGet();
    }

    Assert.assertEquals(3L, (long) deleted.get());
    Assert.assertEquals(0L, (long) st.getReadStackSize());
  }

  @Test
  public final void testStackAllocateIncorrect()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final R2RenderTargetStackType st = this.newStack(g33);

    final R2TextureUnitAllocatorType tc =
      R2TextureUnitAllocator.newAllocatorWithStack(
        8, g33.getTextures().textureGetUnits());

    final R2ImageBufferDescriptionType desc = R2ImageBufferDescription.of(
      AreaInclusiveUnsignedL.of(
        new UnsignedRangeInclusiveL(0L, 1L),
        new UnsignedRangeInclusiveL(0L, 1L)));

    final R2RenderTargetAllocatorFunctionType<
      R2ImageBufferDescriptionType, R2ImageBufferType, Unit> f =
      (g, tc1, context, description) -> {
        final JCGLFramebuffersType gg = g.getFramebuffers();
        final R2ImageBufferType r =
          R2ImageBuffer.newImageBuffer(gg, g.getTextures(), tc1, description);
        gg.framebufferDrawUnbind();
        return r;
      };

    this.expected.expect(R2RenderTargetStackAllocationException.class);
    st.renderTargetAllocateDraw(tc.getRootContext(), Unit.unit(), desc, f);
  }

  @Test
  public final void testStackAllocateCorrect()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final JCGLFramebuffersType g_fb = g33.getFramebuffers();
    final R2RenderTargetStackType st = this.newStack(g33);

    final R2TextureUnitAllocatorType tc =
      R2TextureUnitAllocator.newAllocatorWithStack(
        8, g33.getTextures().textureGetUnits());

    final R2ImageBufferDescriptionType desc = R2ImageBufferDescription.of(
      AreaInclusiveUnsignedL.of(
        new UnsignedRangeInclusiveL(0L, 1L),
        new UnsignedRangeInclusiveL(0L, 1L)));

    final R2RenderTargetAllocatorFunctionType<
      R2ImageBufferDescriptionType, R2ImageBufferType, Unit> f =
      (g, tc1, context, description) -> R2ImageBuffer.newImageBuffer(
        g.getFramebuffers(), g.getTextures(), tc1, description);

    final R2ImageBufferType rt = st.renderTargetAllocateDraw(
      tc.getRootContext(), Unit.unit(), desc, f);

    Assert.assertEquals(rt.getDescription(), desc);
    Assert.assertTrue(g_fb.framebufferDrawIsBound(rt.getPrimaryFramebuffer()));
    Assert.assertEquals(1L, (long) st.getDrawStackSize());
    Assert.assertEquals(0L, (long) st.getReadStackSize());
  }

  @Test
  public final void testStackUnbindReadWrongBound()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final JCGLFramebuffersType g_fb = g33.getFramebuffers();
    final R2RenderTargetStackType st = this.newStack(g33);

    final R2ImageBufferType r0 =
      R2RenderTargetStackContract.newRenderTarget(g33);
    final R2ImageBufferType r1 =
      R2RenderTargetStackContract.newRenderTarget(g33);

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    st.renderTargetBindRead(r0);
    Assert.assertTrue(g_fb.framebufferReadIsBound(r0.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    this.expected.expect(R2RenderTargetStackWrongTargetException.class);
    st.renderTargetUnbindRead(r1);
  }

  @Test
  public final void testStackBindDrawInconsistent()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final JCGLFramebuffersType g_fb = g33.getFramebuffers();
    final R2RenderTargetStackType st = this.newStack(g33);

    final R2ImageBufferType r0 =
      R2RenderTargetStackContract.newRenderTarget(g33);

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    st.renderTargetBindDraw(r0);
    Assert.assertTrue(g_fb.framebufferDrawIsBound(r0.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());

    g_fb.framebufferDrawUnbind();

    this.expected.expect(R2RenderTargetStackInconsistentException.class);
    st.renderTargetBindDraw(r0);
  }

  @Test
  public final void testStackUnbindDrawInconsistent()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final JCGLFramebuffersType g_fb = g33.getFramebuffers();
    final R2RenderTargetStackType st = this.newStack(g33);

    final R2ImageBufferType r0 =
      R2RenderTargetStackContract.newRenderTarget(g33);

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    st.renderTargetBindDraw(r0);
    Assert.assertTrue(g_fb.framebufferDrawIsBound(r0.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());

    g_fb.framebufferDrawUnbind();

    this.expected.expect(R2RenderTargetStackInconsistentException.class);
    st.renderTargetUnbindDraw(r0);
  }

  @Test
  public final void testStackUnbindReadNothingBound()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final JCGLFramebuffersType g_fb = g33.getFramebuffers();
    final R2RenderTargetStackType st = this.newStack(g33);

    final R2ImageBufferType r0 =
      R2RenderTargetStackContract.newRenderTarget(g33);

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    this.expected.expect(R2RenderTargetStackEmptyException.class);
    st.renderTargetUnbindRead(r0);
  }

  @Test
  public final void testStackBindReadCorrect()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final JCGLFramebuffersType g_fb = g33.getFramebuffers();
    final R2RenderTargetStackType st = this.newStack(g33);

    final R2ImageBufferType r0 =
      R2RenderTargetStackContract.newRenderTarget(g33);
    final R2ImageBufferType r1 =
      R2RenderTargetStackContract.newRenderTarget(g33);
    final R2ImageBufferType r2 =
      R2RenderTargetStackContract.newRenderTarget(g33);

    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    st.renderTargetBindRead(r0);
    Assert.assertTrue(g_fb.framebufferReadIsBound(r0.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());
    Assert.assertEquals(0L, (long) st.getDrawStackSize());
    Assert.assertEquals(1L, (long) st.getReadStackSize());

    st.renderTargetBindRead(r1);
    Assert.assertTrue(g_fb.framebufferReadIsBound(r1.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());
    Assert.assertEquals(0L, (long) st.getDrawStackSize());
    Assert.assertEquals(2L, (long) st.getReadStackSize());

    st.renderTargetBindRead(r2);
    Assert.assertTrue(g_fb.framebufferReadIsBound(r2.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());
    Assert.assertEquals(0L, (long) st.getDrawStackSize());
    Assert.assertEquals(3L, (long) st.getReadStackSize());

    st.renderTargetUnbindRead(r2);
    Assert.assertTrue(g_fb.framebufferReadIsBound(r1.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());
    Assert.assertEquals(0L, (long) st.getDrawStackSize());
    Assert.assertEquals(2L, (long) st.getReadStackSize());

    st.renderTargetUnbindRead(r1);
    Assert.assertTrue(g_fb.framebufferReadIsBound(r0.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());
    Assert.assertEquals(0L, (long) st.getDrawStackSize());
    Assert.assertEquals(1L, (long) st.getReadStackSize());

    st.renderTargetUnbindRead(r0);
    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());
    Assert.assertEquals(0L, (long) st.getDrawStackSize());
    Assert.assertEquals(0L, (long) st.getReadStackSize());
  }

  @Test
  public final void testStackBindReadInconsistent()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final JCGLFramebuffersType g_fb = g33.getFramebuffers();
    final R2RenderTargetStackType st = this.newStack(g33);

    final R2ImageBufferType r0 =
      R2RenderTargetStackContract.newRenderTarget(g33);

    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    st.renderTargetBindRead(r0);
    Assert.assertTrue(g_fb.framebufferReadIsBound(r0.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    g_fb.framebufferReadUnbind();

    this.expected.expect(R2RenderTargetStackInconsistentException.class);
    st.renderTargetBindRead(r0);
  }

  @Test
  public final void testStackUnbindReadInconsistent()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = gc.contextGetGL33();
    final JCGLFramebuffersType g_fb = g33.getFramebuffers();
    final R2RenderTargetStackType st = this.newStack(g33);

    final R2ImageBufferType r0 =
      R2RenderTargetStackContract.newRenderTarget(g33);

    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    st.renderTargetBindRead(r0);
    Assert.assertTrue(g_fb.framebufferReadIsBound(r0.getPrimaryFramebuffer()));
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    g_fb.framebufferReadUnbind();

    this.expected.expect(R2RenderTargetStackInconsistentException.class);
    st.renderTargetUnbindRead(r0);
  }
}
