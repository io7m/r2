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
import com.io7m.r2.core.R2AmbientOcclusionBufferUsableType;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2TextureUnitContextParentType;

/**
 * The type of filters that consume geometry buffers and populate ambient
 * occlusion buffers.
 *
 * @see R2GeometryBufferUsableType
 * @see com.io7m.r2.core.R2AmbientOcclusionBufferType
 */

public interface R2FilterSSAOType extends R2FilterType
{
  /**
   * Run the filter on the given geometry buffer. The occlusion term will be
   * written to {@code abuffer}.
   *
   * @param g       A GL interface
   * @param p       The SSAO parameters
   * @param uc      A texture unit context
   * @param m       The view matrices used to construct the scene
   * @param gbuffer A geometry buffer
   * @param abuffer An ambient occlusion buffer
   */

  void runSSAO(
    JCGLInterfaceGL33Type g,
    R2SSAOParameters p,
    R2TextureUnitContextParentType uc,
    R2MatricesObserverValuesType m,
    R2GeometryBufferUsableType gbuffer,
    R2AmbientOcclusionBufferUsableType abuffer);

  /**
   * Run the filter on the given geometry buffer. The occlusion term will be
   * written to the first draw buffer of whatever is the currently bound
   * framebuffer.
   *
   * @param g            A GL interface
   * @param p            The SSAO parameters
   * @param uc           A texture unit context
   * @param m            The view matrices used to construct the scene
   * @param gbuffer      A geometry buffer
   * @param abuffer_area The size of the current viewport
   */

  void runSSAOWithBoundBuffer(
    JCGLInterfaceGL33Type g,
    R2SSAOParameters p,
    R2TextureUnitContextParentType uc,
    R2MatricesObserverValuesType m,
    R2GeometryBufferUsableType gbuffer,
    AreaInclusiveUnsignedLType abuffer_area);
}
