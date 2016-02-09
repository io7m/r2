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

package com.io7m.r2.core.debug;

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.r2.core.R2DeletableType;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2MatricesObserverType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2UnitQuadUsableType;

/**
 * The type of renderers that recover the eye-space position value from
 * populated G-buffers.
 */

public interface R2EyePositionRendererType extends R2DeletableType
{
  /**
   * Reconstruct the eye-space position of every fragment in the given
   * G-Buffer.
   *
   * @param gbuffer The populated geometry buffer
   * @param zbuffer The eye-Z buffer
   * @param uc      A texture unit context
   * @param m       A matrix context
   * @param q       The unit quad used for the full-screen pass
   */

  void renderEyePosition(
    R2GeometryBufferUsableType gbuffer,
    R2EyeZBufferUsableType zbuffer,
    R2TextureUnitContextParentType uc,
    R2MatricesObserverType m,
    R2UnitQuadUsableType q);

  /**
   * Reconstruct the eye-space position of every fragment in the given
   * G-Buffer.

   * @param gbuffer      The populated geometry buffer
   * @param zbuffer_area The area of the currently bound framebuffer
   * @param uc           A texture unit context
   * @param m            A matrix context
   * @param q            The unit quad used for the full-screen pass
   */

  void renderEyePositionWithBoundBuffer(
    R2GeometryBufferUsableType gbuffer,
    AreaInclusiveUnsignedLType zbuffer_area,
    R2TextureUnitContextParentType uc,
    R2MatricesObserverType m,
    R2UnitQuadUsableType q);
}
