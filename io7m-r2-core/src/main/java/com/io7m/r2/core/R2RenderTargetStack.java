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

import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLResourceUsableType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jnull.NullCheck;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valid4j.Assertive;

import java.util.Objects;

/**
 * The default implementation of the {@link R2RenderTargetStackType} interface.
 */

public final class R2RenderTargetStack implements R2RenderTargetStackType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2RenderTargetStack.class);
  }

  private final ReferenceArrayList<R2RenderTargetUsableType<?>> stack_read;
  private final JCGLFramebuffersType framebuffers;
  private final ReferenceArrayList<R2RenderTargetUsableType<?>> stack_draw;
  private final JCGLInterfaceGL33Type g;

  private R2RenderTargetStack(
    final JCGLInterfaceGL33Type in_g)
  {
    this.g = NullCheck.notNull(in_g);
    this.framebuffers = this.g.getFramebuffers();
    this.stack_read = new ReferenceArrayList<>(32);
    this.stack_draw = new ReferenceArrayList<>(32);
  }

  /**
   * @param in_g A GL interface
   *
   * @return A new render target stack
   */

  public static R2RenderTargetStackType newStack(
    final JCGLInterfaceGL33Type in_g)
  {
    return new R2RenderTargetStack(in_g);
  }

  private static <D extends R2RenderTargetDescriptionType>
  void checkNotDeleted(final JCGLResourceUsableType r)
  {
    if (r.isDeleted()) {
      throw R2RenderTargetStack.isDeleted(r);
    }
  }

  private static R2RenderTargetStackDeletedException isDeleted(
    final JCGLResourceUsableType r)
  {
    return new R2RenderTargetStackDeletedException(r.toString());
  }

  @Override
  public int getDrawStackSize()
  {
    return this.stack_draw.size();
  }

  @Override
  public int getReadStackSize()
  {
    return this.stack_read.size();
  }

  @Override
  public <D extends R2RenderTargetDescriptionType> void renderTargetBindRead(
    final R2RenderTargetUsableType<D> r)
  {
    NullCheck.notNull(r);
    R2RenderTargetStack.checkNotDeleted(r);

    boolean already_bound = false;

    if (!this.stack_read.isEmpty()) {
      final R2RenderTargetUsableType<?> p =
        this.stack_read.get(this.stack_read.size() - 1);
      final JCGLFramebufferUsableType p_fb = p.getPrimaryFramebuffer();
      this.checkBoundRead(p_fb);

      already_bound =
        Objects.equals(p_fb, r.getPrimaryFramebuffer());
    }

    if (!already_bound) {
      this.framebuffers.framebufferReadBind(r.getPrimaryFramebuffer());
      this.stack_read.add(r);
    } else {
      if (R2RenderTargetStack.LOG.isTraceEnabled()) {
        R2RenderTargetStack.LOG.trace("redundant read bind ignored: {}", r);
      }
    }

    Assertive.ensure(
      this.framebuffers.framebufferReadIsBound(r.getPrimaryFramebuffer()),
      "Framebuffer read buffer is bound");
  }

  private void checkBoundRead(
    final JCGLFramebufferUsableType p_fb)
  {
    if (!this.framebuffers.framebufferReadIsBound(p_fb)) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Render target stack is inconsistent.\n");
      sb.append("Top of read stack: ");
      sb.append(p_fb);
      sb.append("\n");
      sb.append("Actually bound: ");
      sb.append(this.framebuffers.framebufferReadGetBound());
      sb.append("\n");
      throw new R2RenderTargetStackInconsistentException(
        sb.toString());
    }
  }

  @Override
  public <D extends R2RenderTargetDescriptionType> void renderTargetUnbindRead(
    final R2RenderTargetUsableType<D> r)
  {
    NullCheck.notNull(r);

    if (this.stack_read.isEmpty()) {
      throw new R2RenderTargetStackEmptyException(
        "Render target read stack is empty; cannot unbind!");
    }

    final R2RenderTargetUsableType<?> current =
      this.stack_read.get(this.stack_read.size() - 1);

    /**
     * Check that the stack is consistent; the top of the stack must be
     * the currently bound framebuffer (if the framebuffer on the top of the
     * stack hasn't been deleted).
     */

    final JCGLFramebufferUsableType c_fb = current.getPrimaryFramebuffer();
    if (!current.isDeleted()) {
      this.checkBoundRead(c_fb);
    }

    /**
     * Check that the given render target matches that of the top of the stack.
     */

    if (!Objects.equals(current, r)) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Render target stack is inconsistent.\n");
      sb.append("Top of read stack: ");
      sb.append(c_fb);
      sb.append("\n");
      sb.append("Given render target: ");
      sb.append(r.getPrimaryFramebuffer());
      sb.append("\n");
      throw new R2RenderTargetStackWrongTargetException(sb.toString());
    }

    /**
     * If the stack has more than one element, re-bind the previous
     * framebuffer.
     */

    if (this.stack_read.size() > 1) {
      final R2RenderTargetUsableType<?> previous =
        this.stack_read.get(this.stack_read.size() - 2);

      /**
       * If the previous framebuffer was deleted, pop the current framebuffer
       * from the stack and ignore it (nothing further needs to be done with it).
       * Then signal the fact that there's a deleted framebuffer on the stack.
       */

      if (previous.isDeleted()) {
        this.stack_read.remove(this.stack_read.size() - 1);
        throw R2RenderTargetStack.isDeleted(previous);
      }

      this.framebuffers.framebufferReadBind(previous.getPrimaryFramebuffer());
    } else {

      /**
       * Otherwise, the default framebuffer is bound.
       */

      this.framebuffers.framebufferReadUnbind();
    }

    /**
     * Remove the render target from the stack, and then check if it was
     * deleted. The reason that the check is deferred is that it results in
     * consistent stack handling without hiding the fact that the user left
     * a deleted framebuffer on the stack.
     */

    this.stack_read.remove(this.stack_read.size() - 1);
    R2RenderTargetStack.checkNotDeleted(r);

    Assertive.ensure(
      !this.framebuffers.framebufferReadIsBound(r.getPrimaryFramebuffer()),
      "Given framebuffer is no longer bound");
  }

  @Override
  public <D extends R2RenderTargetDescriptionType> void renderTargetBindDraw(
    final R2RenderTargetUsableType<D> r)
  {
    NullCheck.notNull(r);
    R2RenderTargetStack.checkNotDeleted(r);

    boolean already_bound = false;

    if (!this.stack_draw.isEmpty()) {
      final R2RenderTargetUsableType<?> p =
        this.stack_draw.get(this.stack_draw.size() - 1);

      final JCGLFramebufferUsableType p_fb = p.getPrimaryFramebuffer();
      this.checkBoundDraw(p_fb);
      already_bound = Objects.equals(p_fb, r.getPrimaryFramebuffer());
    }

    if (!already_bound) {
      this.framebuffers.framebufferDrawBind(r.getPrimaryFramebuffer());
      this.stack_draw.add(r);
    } else {
      if (R2RenderTargetStack.LOG.isTraceEnabled()) {
        R2RenderTargetStack.LOG.trace("redundant draw bind ignored: {}", r);
      }
    }

    Assertive.ensure(
      this.framebuffers.framebufferDrawIsBound(r.getPrimaryFramebuffer()),
      "Framebuffer draw buffer is bound");
  }

  @Override
  public <D extends R2RenderTargetDescriptionType> void renderTargetUnbindDraw(
    final R2RenderTargetUsableType<D> r)
  {
    NullCheck.notNull(r);

    if (this.stack_draw.isEmpty()) {
      throw new R2RenderTargetStackEmptyException(
        "Render target draw stack is empty; cannot unbind!");
    }

    final R2RenderTargetUsableType<?> current =
      this.stack_draw.get(this.stack_draw.size() - 1);

    /**
     * Check that the stack is consistent; the top of the stack must be
     * the currently bound framebuffer (if the framebuffer on the top of the
     * stack hasn't been deleted).
     */

    final JCGLFramebufferUsableType c_fb = current.getPrimaryFramebuffer();
    if (!current.isDeleted()) {
      this.checkBoundDraw(c_fb);
    }

    /**
     * Check that the given render target matches that of the top of the stack.
     */

    if (!Objects.equals(current, r)) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Render target stack is inconsistent.\n");
      sb.append("Top of draw stack: ");
      sb.append(c_fb);
      sb.append("\n");
      sb.append("Given render target: ");
      sb.append(r.getPrimaryFramebuffer());
      sb.append("\n");
      throw new R2RenderTargetStackWrongTargetException(sb.toString());
    }

    /**
     * If the stack has more than one element, re-bind the previous
     * framebuffer.
     */

    if (this.stack_draw.size() > 1) {
      final R2RenderTargetUsableType<?> previous =
        this.stack_draw.get(this.stack_draw.size() - 2);

      /**
       * If the previous framebuffer was deleted, pop the current framebuffer
       * from the stack and ignore it (nothing further needs to be done with it).
       * Then signal the fact that there's a deleted framebuffer on the stack.
       */

      if (previous.isDeleted()) {
        this.stack_draw.remove(this.stack_draw.size() - 1);
        throw R2RenderTargetStack.isDeleted(previous);
      }

      this.framebuffers.framebufferDrawBind(previous.getPrimaryFramebuffer());
    } else {

      /**
       * Otherwise, the default framebuffer is bound.
       */

      this.framebuffers.framebufferDrawUnbind();
    }

    /**
     * Remove the render target from the stack, and then check if it was
     * deleted. The reason that the check is deferred is that it results in
     * consistent stack handling without hiding the fact that the user left
     * a deleted framebuffer on the stack.
     */

    this.stack_draw.remove(this.stack_draw.size() - 1);
    R2RenderTargetStack.checkNotDeleted(r);

    Assertive.ensure(
      !this.framebuffers.framebufferDrawIsBound(r.getPrimaryFramebuffer()),
      "Given framebuffer is no longer bound");
  }

  @Override
  public <D extends R2RenderTargetDescriptionType,
    T extends R2RenderTargetType<D>, C> T renderTargetAllocateDraw(
    final R2TextureUnitContextParentType tc,
    final C context,
    final D description,
    final R2RenderTargetAllocatorFunctionType<D, T, C> f)
  {
    final T r = f.call(this.g, tc, context, description);

    final JCGLFramebufferUsableType new_fb = r.getPrimaryFramebuffer();
    if (!this.framebuffers.framebufferDrawIsBound(new_fb)) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append(
        "Render target allocator function failed to leave the primary framebuffer bound.\n");
      sb.append("Currently bound: ");
      sb.append(this.framebuffers.framebufferDrawGetBound());
      sb.append("\n");
      r.delete(this.g);
      throw new R2RenderTargetStackAllocationException(sb.toString());
    }

    R2RenderTargetStack.checkNotDeleted(r);
    this.stack_draw.add(r);
    return r;
  }

  private void checkBoundDraw(
    final JCGLFramebufferUsableType current)
  {
    if (!this.framebuffers.framebufferDrawIsBound(current)) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Render target stack is inconsistent.\n");
      sb.append("Top of draw stack: ");
      sb.append(current);
      sb.append("\n");
      sb.append("Actually bound: ");
      sb.append(this.framebuffers.framebufferDrawGetBound());
      sb.append("\n");
      throw new R2RenderTargetStackInconsistentException(
        sb.toString());
    }
  }
}
