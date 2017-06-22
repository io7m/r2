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

import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Texture2DUsableType;
import org.immutables.value.Value;

/**
 * The type of parameters for bilateral depth-aware blur shaders.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2ShaderFilterBilateralBlurDepthAwareParametersType
{
  /**
   * @return The matrices that were used to produce the scene being blurred
   */

  @Value.Parameter
  R2MatricesObserverValuesType viewMatrices();

  /**
   * @return The image texture that will be blurred
   */

  @Value.Parameter
  R2Texture2DUsableType imageTexture();

  /**
   * @return The depth texture that will be sampled to determine the
   * contribution of each corresponding pixel in the image texture
   */

  @Value.Parameter
  R2Texture2DUsableType depthTexture();

  /**
   * @return The depth coefficient used to produce the scene
   */

  @Value.Parameter
  double depthCoefficient();

  /**
   * @return The blur falloff
   */

  @Value.Parameter
  double blurFalloff();

  /**
   * @return The radius of the blur effect in texels
   */

  @Value.Parameter
  @Value.Default
  default double blurRadius()
  {
    return 4.0;
  }

  /**
   * @return The blur sharpness
   */

  @Value.Parameter
  @Value.Default
  default double blurSharpness()
  {
    return 16.0;
  }

  /**
   * @return The inverse width of the output image: (1 / width)
   */

  @Value.Parameter
  double blurOutputInverseWidth();

  /**
   * @return The inverse height of the output image: (1 / height)
   */

  @Value.Parameter
  double blurOutputInverseHeight();
}
