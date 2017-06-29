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

package com.io7m.r2.rendering.depth.variance.api;

import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.r2.core.api.deletable.R2DeletableType;
import com.io7m.r2.matrices.R2MatricesObserverType;
import com.io7m.r2.rendering.depth.api.R2DepthInstancesType;

/**
 * The type of renderers that populate depth variance buffers.
 */

public interface R2DepthVarianceRendererType extends R2DeletableType
{
  /**
   * Render the given opaque instances into the given depth buffer.
   *
   * @param gbuffer The depth buffer
   * @param uc      A texture unit context
   * @param m       A matrix context
   * @param s       The depth instances
   */

  void renderDepthVariance(
    R2DepthVarianceBufferUsableType gbuffer,
    JCGLTextureUnitContextParentType uc,
    R2MatricesObserverType m,
    R2DepthInstancesType s);

  /**
   * Render the given opaque instances into the currently bound depth
   * buffer.
   *
   * @param area The current viewport
   * @param uc   A texture unit context
   * @param m    A matrix context
   * @param s    The depth instances
   */

  void renderDepthVarianceWithBoundBuffer(
    AreaL area,
    JCGLTextureUnitContextParentType uc,
    R2MatricesObserverType m,
    R2DepthInstancesType s);
}
