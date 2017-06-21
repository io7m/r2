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
import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2Texture2DStatic;
import com.io7m.r2.filters.R2SSAONoiseTexture;
import org.immutables.value.Value;

/**
 * The type of convenient texture providers.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2FacadeTextureProviderType
{
  /**
   * @return The current facade
   */

  @Value.Auxiliary
  @Value.Parameter
  R2FacadeType main();

  /**
   * Create a new SSAO noise texture.
   *
   * @param ctx The current texture unit context
   *
   * @return A new SSAO noise texture
   */

  default R2Texture2DStatic createSSAONoiseTexture(
    final JCGLTextureUnitContextParentType ctx)
  {
    return R2SSAONoiseTexture.create(
      this.main().rendererGL33().textures(),
      ctx);
  }

  /**
   * Create a new SSAO noise texture using the root texture unit context.
   *
   * @return A new SSAO noise texture
   */

  default R2Texture2DStatic createSSAONoiseTexture()
  {
    return this.createSSAONoiseTexture(
      this.main().textureUnitAllocator().rootContext());
  }
}
