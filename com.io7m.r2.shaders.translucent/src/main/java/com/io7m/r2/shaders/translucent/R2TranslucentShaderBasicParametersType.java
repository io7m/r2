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

package com.io7m.r2.shaders.translucent;

import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.textures.R2Texture2DUsableType;
import com.io7m.r2.textures.R2TextureDefaultsType;
import com.io7m.r2.spaces.R2SpaceRGBAType;
import org.immutables.value.Value;

/**
 * The type of parameters for the basic translucent shader.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2TranslucentShaderBasicParametersType
{
  /**
   * @return A reference to the default textures
   */

  @Value.Parameter
  R2TextureDefaultsType textureDefaults();

  /**
   * <p>An albedo map which is used primarily used to determine the opacity
   * of the shaded surface.</p>
   *
   * @return The albedo texture for the surface
   */

  @Value.Parameter
  @Value.Default
  default R2Texture2DUsableType albedoTexture()
  {
    return this.textureDefaults().white2D();
  }

  /**
   * @return A color value by which to multiply the final refracted image
   */

  @Value.Parameter
  @Value.Default
  default PVector4D<R2SpaceRGBAType> albedoColor()
  {
    return PVector4D.of(1.0, 1.0, 1.0, 1.0);
  }

  /**
   * <p>A specification of the distance at which to begin fading in instances.
   * This allows instances to quickly fade out when they get too close to the
   * view plane.</p>
   *
   * @return The positive eye-space Z distance at which to start fading in
   * instances
   *
   * @see #fadeZFar()
   */

  @Value.Parameter
  @Value.Default
  default double fadeZNear()
  {
    return 0.0;
  }

  /**
   * A specification of the distance at which instances will be fully faded in.
   *
   * @return The positive eye-space Z distance at which instances are fully
   * faded in
   *
   * @see #fadeZNear()
   */

  @Value.Parameter
  @Value.Default
  default double fadeZFar()
  {
    return 0.0;
  }
}
