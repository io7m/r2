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

import java.util.Optional;

/**
 * The type of renderers that populate light buffers.
 */

public interface R2LightRendererType extends R2DeletableType
{
  /**
   * <p>Render the given lights into the given light buffer.</p>
   *
   * <p>If a light buffer is provided, it will be bound before evaluation and
   * left bound after the method returns.</p>
   *
   * @param gbuffer The populated geometry buffer
   * @param area    The output viewport
   * @param lbuffer The output light buffer
   * @param pc      A profiling context
   * @param uc      A texture unit context
   * @param shadows A set of rendered shadow maps
   * @param m       A matrix context
   * @param s       The opaque lights
   */

  void renderLightsToLightBuffer(
    R2GeometryBufferUsableType gbuffer,
    AreaL area,
    Optional<R2LightBufferUsableType> lbuffer,
    JCGLProfilingContextType pc,
    JCGLTextureUnitContextParentType uc,
    R2ShadowMapContextUsableType shadows,
    R2MatricesObserverType m,
    R2SceneLightsType s);

  /**
   * <p>Render the given lights into the given image buffer.</p>
   *
   * <p>If an image buffer is provided, it will be bound before evaluation and
   * left bound after the method returns.</p>
   *
   * @param gbuffer The populated geometry buffer
   * @param area    The output viewport
   * @param ibuffer The output image buffer
   * @param pc      A profiling context
   * @param uc      A texture unit context
   * @param shadows A set of rendered shadow maps
   * @param m       A matrix context
   * @param s       The opaque lights
   */

  void renderLightsToImageBuffer(
    R2GeometryBufferUsableType gbuffer,
    AreaL area,
    Optional<R2ImageBufferUsableType> ibuffer,
    JCGLProfilingContextType pc,
    JCGLTextureUnitContextParentType uc,
    R2ShadowMapContextUsableType shadows,
    R2MatricesObserverType m,
    R2SceneLightsType s);
}
