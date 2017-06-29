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

package com.io7m.r2.filters.occlusion_applicator.api;

import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.rendering.lights.api.R2LightBufferUsableType;
import com.io7m.r2.textures.R2Texture2DUsableType;
import org.immutables.value.Value;

/**
 * The type of occlusion applicator filter parameters.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2FilterOcclusionApplicatorParametersType
{
  /**
   * @return The light buffer that will have occlusion applied
   */

  @Value.Parameter
  R2LightBufferUsableType outputLightBuffer();

  /**
   * @return A texture containing occlusion values
   */

  @Value.Parameter
  R2Texture2DUsableType occlusionTexture();

  /**
   * @return The intensity of the occlusion
   */

  @Value.Parameter
  @Value.Default
  default double intensity()
  {
    return 1.0;
  }
}
