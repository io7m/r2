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
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;

/**
 * The type of renderers that populate light buffers.
 */

public interface R2LightRendererType extends R2DeletableType
{
  /**
   * Render the given lights into the given light buffer.
   *
   * @param g       A GL interface
   * @param gbuffer The populated geometry buffer
   * @param lbuffer The light buffer
   * @param m       A matrix context
   * @param s       The opaque lights
   */

  void renderLights(
    JCGLInterfaceGL33Type g,
    R2GeometryBufferUsableType gbuffer,
    R2LightBufferUsableType lbuffer,
    R2MatricesObserverType m,
    R2SceneOpaqueLightsType s);

  /**
   * Render the given lights into the currently bound light buffer.
   *
   * @param g            A GL interface
   * @param gbuffer      The populated geometry buffer
   * @param lbuffer_area The area of the currently bound framebuffer
   * @param m            A matrix context
   * @param s            The opaque lights
   */

  void renderLightsWithBoundBuffer(
    JCGLInterfaceGL33Type g,
    R2GeometryBufferUsableType gbuffer,
    AreaInclusiveUnsignedLType lbuffer_area,
    R2MatricesObserverType m,
    R2SceneOpaqueLightsType s);
}