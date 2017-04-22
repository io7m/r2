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

import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jregions.core.unparameterized.areas.AreaL;

/**
 * The type of stencil renderers.
 */

public interface R2StencilRendererType extends R2DeletableType
{
  /**
   * Render the given stencil instances into the currently bound framebuffer.
   *
   * @param m    A matrix context
   * @param pc   A profiling context
   * @param uc   A texture unit context
   * @param area The current viewport
   * @param s    The stencil instances
   */

  void renderStencilsWithBoundBuffer(
    R2MatricesObserverType m,
    JCGLProfilingContextType pc,
    JCGLTextureUnitContextParentType uc,
    AreaL area,
    R2SceneStencilsType s);
}
