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

/**
 * <p>A stack representing the current and previously bound render targets.</p>
 *
 * <p>The render target stack abstraction enforces a stack discipline for the
 * binding and unbinding of render targets, and implementations are expected to
 * perform checks to ensure that the stack remains consistent with the state of
 * the currently bound render targets.</p>
 */

public interface R2RenderTargetStackType
{
  /**
   * Bind the given render target for reading.
   *
   * @param r   The render target
   * @param <D> The type of render target descriptions
   *
   * @throws R2RenderTargetStackException             On errors
   * @throws R2RenderTargetStackInconsistentException If the stack has become
   *                                                  inconsistent because the
   *                                                  programmer has changed
   *                                                  render targets without
   *                                                  doing it via this stack
   */

  <D extends R2RenderTargetDescriptionType>
  void renderTargetBindRead(R2RenderTargetUsableType<D> r)
    throws R2RenderTargetStackException,
    R2RenderTargetStackInconsistentException;

  /**
   * Unbind the given render target for reading. The previously bound render
   * target, if any, will be re-bound for reading. If there was no previously
   * bound render target, the default framebuffer is used.
   *
   * @param r   The render target
   * @param <D> The type of render target descriptions
   *
   * @throws R2RenderTargetStackException            On errors
   * @throws R2RenderTargetStackWrongTargetException If the given render target
   *                                                 is not bound for reading
   */

  <D extends R2RenderTargetDescriptionType>
  void renderTargetUnbindRead(R2RenderTargetUsableType<D> r)
    throws R2RenderTargetStackException,
    R2RenderTargetStackWrongTargetException;

  /**
   * Bind the given render target for drawing.
   *
   * @param r   The render target
   * @param <D> The type of render target descriptions
   *
   * @throws R2RenderTargetStackException             On errors
   * @throws R2RenderTargetStackInconsistentException If the stack has become
   *                                                  inconsistent because the
   *                                                  programmer has changed
   *                                                  render targets without
   *                                                  doing it via this stack
   */

  <D extends R2RenderTargetDescriptionType>
  void renderTargetBindDraw(R2RenderTargetUsableType<D> r)
    throws R2RenderTargetStackException,
    R2RenderTargetStackInconsistentException;

  /**
   * Unbind the given render target for drawing.  The previously bound render
   * target, if any, will be re-bound for drawing. If there was no previously
   * bound render target, the default framebuffer is used.
   *
   * @param r   The render target
   * @param <D> The type of render target descriptions
   *
   * @throws R2RenderTargetStackException            On errors
   * @throws R2RenderTargetStackWrongTargetException If the given render target
   *                                                 is not bound for drawing
   */

  <D extends R2RenderTargetDescriptionType>
  void renderTargetUnbindDraw(R2RenderTargetUsableType<D> r)
    throws R2RenderTargetStackException,
    R2RenderTargetStackWrongTargetException;

  /**
   * <p>Allocate a new render target, leaving it bound as the current draw
   * buffer.</p>
   *
   * <p>The render target is actually allocated by the function {@code f}, which
   * is required to leave the render target bound as the current draw buffer
   * prior to returning.</p>
   *
   * @param tc          A texture unit allocator
   * @param context     A context value
   * @param description A render target description
   * @param f           An allocation function
   * @param <D>         The type of render target descriptions
   * @param <T>         The type of render targets
   * @param <C>         The type of context values
   *
   * @return A new render target
   */

  <D extends R2RenderTargetDescriptionType, T extends R2RenderTargetType<D>, C>
  T renderTargetAllocateDraw(
    R2TextureUnitContextParentType tc,
    C context,
    D description,
    R2RenderTargetAllocatorFunctionType<D, T, C> f);
}
