/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.facade;

import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.r2.core.R2AmbientOcclusionBuffer;
import com.io7m.r2.core.R2AmbientOcclusionBufferDescription;
import com.io7m.r2.core.R2GeometryBuffer;
import com.io7m.r2.core.R2GeometryBufferDescription;
import com.io7m.r2.core.R2ImageBuffer;
import com.io7m.r2.core.R2ImageBufferDescription;
import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2LightBufferDescription;
import com.io7m.r2.core.R2LightBufferType;
import com.io7m.r2.core.R2LightBuffers;
import com.io7m.r2.core.R2MaskBuffer;
import com.io7m.r2.core.R2MaskBufferDescription;
import org.immutables.value.Value;

/**
 * The type of convenient buffer providers.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2FacadeBufferProviderType
{
  /**
   * @return The current facade
   */

  @Value.Auxiliary
  @Value.Parameter
  R2FacadeType main();

  /**
   * Create a light buffer using the current root texture unit context.
   *
   * @param description The light buffer description
   *
   * @return A light buffer
   */

  default R2LightBufferType createLightBuffer(
    final R2LightBufferDescription description)
  {
    return this.createLightBuffer(
      this.main().textureUnitAllocator().rootContext(),
      description);
  }

  /**
   * Create a light buffer.
   *
   * @param ctx         The current texture unit context
   * @param description The light buffer description
   *
   * @return A light buffer
   */

  default R2LightBufferType createLightBuffer(
    final JCGLTextureUnitContextParentType ctx,
    final R2LightBufferDescription description)
  {
    return R2LightBuffers.newLightBuffer(
      this.main().gl33().framebuffers(),
      this.main().gl33().textures(),
      ctx,
      description);
  }

  /**
   * Create a geometry buffer using the current root texture unit context.
   *
   * @param description The geometry buffer description
   *
   * @return A light buffer
   */

  default R2GeometryBuffer createGeometryBuffer(
    final R2GeometryBufferDescription description)
  {
    return this.createGeometryBuffer(
      this.main().textureUnitAllocator().rootContext(),
      description);
  }

  /**
   * Create a geometry buffer.
   *
   * @param ctx         The current texture unit context
   * @param description The geometry buffer description
   *
   * @return A geometry buffer
   */

  default R2GeometryBuffer createGeometryBuffer(
    final JCGLTextureUnitContextParentType ctx,
    final R2GeometryBufferDescription description)
  {
    return R2GeometryBuffer.create(
      this.main().gl33().framebuffers(),
      this.main().gl33().textures(),
      ctx, description);
  }

  /**
   * Create an image buffer using the current root texture unit context.
   *
   * @param description The image buffer description
   *
   * @return An image buffer
   */

  default R2ImageBuffer createImageBuffer(
    final R2ImageBufferDescription description)
  {
    return this.createImageBuffer(
      this.main().textureUnitAllocator().rootContext(),
      description);
  }

  /**
   * Create an image buffer.
   *
   * @param ctx         The current texture unit context
   * @param description The image buffer description
   *
   * @return An image buffer
   */

  default R2ImageBuffer createImageBuffer(
    final JCGLTextureUnitContextParentType ctx,
    final R2ImageBufferDescription description)
  {
    return R2ImageBuffer.create(
      this.main().gl33().framebuffers(),
      this.main().gl33().textures(),
      ctx,
      description);
  }

  /**
   * Create an ambient occlusion buffer.
   *
   * @param ctx         The current texture unit context
   * @param description The ambient occlusion buffer description
   *
   * @return An ambient occlusion buffer
   */

  default R2AmbientOcclusionBuffer createAmbientOcclusionBuffer(
    final JCGLTextureUnitContextParentType ctx,
    final R2AmbientOcclusionBufferDescription description)
  {
    return R2AmbientOcclusionBuffer.create(
      this.main().gl33().framebuffers(),
      this.main().gl33().textures(), ctx,
      description);
  }

  /**
   * Create an ambient occlusion buffer using the current root texture unit
   * context.
   *
   * @param description The ambient occlusion buffer description
   *
   * @return An ambient occlusion buffer
   */

  default R2AmbientOcclusionBuffer createAmbientOcclusionBuffer(
    final R2AmbientOcclusionBufferDescription description)
  {
    return this.createAmbientOcclusionBuffer(
      this.main().textureUnitAllocator().rootContext(),
      description);
  }

  /**
   * Create a mask buffer.
   *
   * @param ctx         The current texture unit context
   * @param description The mask buffer description
   *
   * @return A mask buffer
   */

  default R2MaskBuffer createMaskBuffer(
    final JCGLTextureUnitContextParentType ctx,
    final R2MaskBufferDescription description)
  {
    return R2MaskBuffer.create(
      this.main().gl33().framebuffers(),
      this.main().gl33().textures(),
      ctx,
      description);
  }

  /**
   * Create a mask buffer using the current root texture unit context.
   *
   * @param description The mask buffer description
   *
   * @return A mask buffer
   */

  default R2MaskBuffer createMaskBuffer(
    final R2MaskBufferDescription description)
  {
    return this.createMaskBuffer(
      this.main().textureUnitAllocator().rootContext(),
      description);
  }
}
