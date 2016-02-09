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

/**
 * The type of renderers that populate geometry buffers.
 */

public interface R2GeometryRendererType extends R2DeletableType
{
  /**
   * Render the given opaque instances into the given geometry buffer.
   *
   * @param gbuffer The geometry buffer
   * @param uc      A texture unit context
   * @param m       A matrix context
   * @param s       The opaque instances
   */

  void renderGeometry(
    R2GeometryBufferUsableType gbuffer,
    R2TextureUnitContextParentType uc,
    R2MatricesObserverType m,
    R2SceneOpaquesType s);

  /**
   * Render the given opaque instances into the currently bound geometry
   * buffer.
   *
   * @param area The current viewport
   * @param uc   A texture unit context
   * @param m    A matrix context
   * @param s    The opaque instances
   */

  void renderGeometryWithBoundBuffer(
    AreaInclusiveUnsignedLType area,
    R2TextureUnitContextParentType uc,
    R2MatricesObserverType m,
    R2SceneOpaquesType s);
}
