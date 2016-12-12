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

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.spaces.R2SpaceRGBType;
import org.immutables.value.Value;

/**
 * Parameters for the fog filter.
 */

@Value.Immutable
@Value.Modifiable
@R2ImmutableStyleType
public interface R2FilterFogParametersType
{
  /**
   * A specification of fog progression. This specifies the curve that fog
   * intensity follows at distances greater than {@link #fogNearPositiveZ()}.
   *
   * @return The fog progression
   */

  @Value.Parameter
  @Value.Default
  default R2FilterFogProgression progression()
  {
    return R2FilterFogProgression.FOG_LINEAR;
  }

  /**
   * @return The output viewport
   */

  @Value.Parameter
  AreaInclusiveUnsignedLType viewport();

  /**
   * @return The input texture that will have fog applied
   */

  @Value.Parameter
  R2Texture2DUsableType imageTexture();

  /**
   * @return The depth texture that contains logarithmic depth values
   */

  @Value.Parameter
  R2Texture2DUsableType imageDepthTexture();

  /**
   * The positive eye-space Z value at which fog starts. Objects closer
   * than this value have no fog applied.
   *
   * @return The starting fog distance
   */

  @Value.Parameter
  @Value.Default
  default float fogNearPositiveZ()
  {
    return 3.0f;
  }

  /**
   * The positive eye-space Z value at which fog ends. Objects further
   * than this value are fully obscured by fog.
   *
   * @return The ending fog distance
   */

  @Value.Parameter
  @Value.Default
  default float fogFarPositiveZ()
  {
    return 20.0f;
  }

  /**
   * @return The color of the fog
   */

  @Value.Parameter
  PVector3FType<R2SpaceRGBType> fogColor();

  /**
   * @return The observer matrix values that were used to produce the scene
   */

  @Value.Parameter
  R2MatricesObserverValuesType observerValues();
}