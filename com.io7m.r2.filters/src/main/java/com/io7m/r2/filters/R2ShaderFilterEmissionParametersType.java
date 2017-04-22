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

package com.io7m.r2.filters;

import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2Texture2DUsableType;
import org.immutables.value.Value;

/**
 * Parameters for the emission shader.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2ShaderFilterEmissionParametersType
{
  /**
   * @return The texture that contains the emission and albedo values
   */

  @Value.Parameter
  R2Texture2DUsableType albedoEmissionTexture();

  /**
   * A specification of an intensity value by which values sampled from the
   * {@link #albedoEmissionTexture()} are multiplied. This effectively allows
   * control over the intensity of the emission effect in the final image.
   *
   * @return The intensity of the emissive sections of the image
   */

  @Value.Parameter
  @Value.Default
  default double emissionIntensity()
  {
    return 1.0;
  }

  /**
   * @return The texture that contains a blurred emission
   */

  @Value.Parameter
  R2Texture2DUsableType glowTexture();

  /**
   * A specification of an intensity value by which values sampled from the
   * {@link #glowTexture()} are multiplied. This effectively allows control over
   * the intensity of the glow effect in the final image.
   *
   * @return The intensity of the glow sections of the image
   */

  @Value.Parameter
  @Value.Default
  default double glowIntensity()
  {
    return 1.0;
  }
}
