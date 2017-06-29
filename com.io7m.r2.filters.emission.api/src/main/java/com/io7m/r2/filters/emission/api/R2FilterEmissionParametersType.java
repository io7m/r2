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

package com.io7m.r2.filters.emission.api;

import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.filters.box_blur.api.R2BlurParameters;
import com.io7m.r2.textures.R2Texture2DUsableType;
import com.io7m.r2.textures.R2TextureDefaultsType;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * The type of parameters for emission filters.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2FilterEmissionParametersType
{
  /**
   * @return A reference to the default textures
   */

  @Value.Parameter
  R2TextureDefaultsType textureDefaults();

  /**
   * @return The framebuffer to which the output will be written. If no
   * framebuffer is specified, the output will go to the default framebuffer.
   */

  @Value.Parameter
  Optional<JCGLFramebufferUsableType> outputFramebuffer();

  /**
   * @return The viewport for the output framebuffer
   */

  @Value.Parameter
  AreaL outputViewport();

  /**
   * @return A texture with the surface albedo in the {@code (r, g, b)}
   * components and the emission level in the alpha component.
   */

  @Value.Parameter
  R2Texture2DUsableType albedoEmissionMap();

  /**
   * A specification of an intensity value by which values sampled from the
   * {@link #albedoEmissionMap()} are multiplied. This effectively allows
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
   * A specification of an intensity value by which values sampled from
   * any produced glow maps are multiplied. This effectively allows control over
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

  /**
   * @return The blur parameters, if blurring is to be used
   */

  @Value.Parameter
  Optional<R2BlurParameters> blurParameters();

  /**
   * If blurring is to be used, the value returned here specifies the scale at
   * which the intermediate emissive step is rendered. If blurring is not going
   * to be used, the emission image is simply blended into the existing
   * framebuffer without any intermediate framebuffers, and therefore the scale
   * value given here is ignored.
   *
   * @return The scale to use for unblurred intermediate framebuffers
   */

  @Value.Parameter
  @Value.Default
  default double scale()
  {
    return 0.5;
  }
}
