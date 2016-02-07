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

package com.io7m.r2.core.filters;

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2ImageBufferUsableType;
import com.io7m.r2.core.R2LightBufferUsableType;
import com.io7m.r2.core.R2TextureUnitContextParentType;

/**
 * The type of filters that combine geometry buffers and light buffers into lit
 * images.
 *
 * @see R2GeometryBufferUsableType
 * @see R2LightBufferUsableType
 */

public interface R2FilterLightApplicatorType extends R2FilterType
{
  /**
   * Run the filter on the given geometry and light buffers. The lit image will
   * be written to {@code ibuffer}.
   *
   * @param g       A GL interface
   * @param uc      A texture unit context
   * @param gbuffer A geometry buffer
   * @param lbuffer A light buffer
   * @param ibuffer An image buffer
   */

  void runLightApplicator(
    JCGLInterfaceGL33Type g,
    R2TextureUnitContextParentType uc,
    R2GeometryBufferUsableType gbuffer,
    R2LightBufferUsableType lbuffer,
    R2ImageBufferUsableType ibuffer);

  /**
   * Run the filter on the given geometry and light buffers. The lit image will
   * be written to the first draw buffer of whatever is the currently bound
   * framebuffer.
   *
   * @param g       A GL interface
   * @param uc      A texture unit context
   * @param gbuffer A geometry buffer
   * @param lbuffer A light buffer
   * @param area    The current framebuffer viewport
   */

  void runLightApplicatorWithBoundBuffer(
    JCGLInterfaceGL33Type g,
    R2TextureUnitContextParentType uc,
    R2GeometryBufferUsableType gbuffer,
    R2LightBufferUsableType lbuffer,
    AreaInclusiveUnsignedLType area);
}
