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

package com.io7m.r2.rendering.targets;

import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLResourceSizedType;
import com.io7m.jcanephora.core.JCGLResourceUsableType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizesL;
import com.io7m.r2.rendering.api.R2RendererExceptionFramebufferNotBound;

/**
 * The type of usable render targets.
 *
 * @param <D> The precise type of render target description used to produce
 *            render targets of this type
 */

public interface R2RenderTargetUsableType<D extends R2RenderTargetDescriptionType>
  extends JCGLResourceUsableType, JCGLResourceSizedType
{
  /**
   * @return The framebuffer
   */

  JCGLFramebufferUsableType primaryFramebuffer();

  /**
   * @return The image size
   */

  AreaSizeL size();

  /**
   * @return The image size as a viewport
   */

  default AreaL sizeAsViewport()
  {
    return AreaSizesL.area(this.size());
  }

  /**
   * @return The description used to create the render target
   */

  D description();

  /**
   * Clear the primary framebuffer to render target specific default values,
   * changing any current render state necessary to achieve this.
   *
   * @param g A GL interface
   *
   * @throws R2RendererExceptionFramebufferNotBound If the framebuffer is not
   *                                                bound
   */

  void clearBoundPrimaryFramebuffer(JCGLInterfaceGL33Type g)
    throws R2RendererExceptionFramebufferNotBound;
}
