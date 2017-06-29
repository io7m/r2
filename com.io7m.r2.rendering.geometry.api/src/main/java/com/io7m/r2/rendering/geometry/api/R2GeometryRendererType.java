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

package com.io7m.r2.rendering.geometry.api;

import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.r2.core.api.deletable.R2DeletableType;
import com.io7m.r2.matrices.R2MatricesObserverType;

import java.util.Optional;

/**
 * The type of renderers that populate geometry buffers.
 */

public interface R2GeometryRendererType extends R2DeletableType
{
  /**
   * <p>Render the given opaque instances into the given geometry buffer.</p>
   *
   * <p>If a geometry buffer is provided, it will be bound before evaluation and
   * left bound after the method returns.</p>
   *
   * @param area    The output viewport
   * @param gbuffer The optional geometry buffer
   * @param pc      A profiling context
   * @param uc      A texture unit context
   * @param m       A matrix context
   * @param s       The opaque instances
   */

  void renderGeometry(
    AreaL area,
    Optional<R2GeometryBufferUsableType> gbuffer,
    JCGLProfilingContextType pc,
    JCGLTextureUnitContextParentType uc,
    R2MatricesObserverType m,
    R2SceneOpaquesReadableType s);
}
