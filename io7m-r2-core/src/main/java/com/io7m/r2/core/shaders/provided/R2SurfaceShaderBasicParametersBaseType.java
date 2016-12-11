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

package com.io7m.r2.core.shaders.provided;

import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.jtensors.parameterized.PVector4FType;
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.jtensors.parameterized.PVectorM4F;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.spaces.R2SpaceRGBAType;
import com.io7m.r2.spaces.R2SpaceRGBType;
import org.immutables.value.Value;

/**
 * Parameters for basic surfaces.
 */

public interface R2SurfaceShaderBasicParametersBaseType
{
  /**
   * @return A reference to the default textures
   */

  @Value.Parameter
  R2TextureDefaultsType textureDefaults();

  /**
   * <p>The base albedo color for the surface. This value is mixed with values
   * sampled from {@link #albedoTexture()} based on {@link #albedoMix()}.</p>
   *
   * @return The base albedo color of the surface
   *
   * @see #albedoTexture()
   * @see #albedoMix()
   */

  @Value.Default
  default PVector4FType<R2SpaceRGBAType> albedoColor()
  {
    return new PVectorM4F<>(1.0f, 1.0f, 1.0f, 1.0f);
  }

  /**
   * <p>The base specular color for the surface. This essentially encodes both
   * the intensity and color of specular highlights for the surface.</p>
   *
   * @return The base specular color of the surface
   */

  @Value.Default
  default PVector3FType<R2SpaceRGBType> specularColor()
  {
    return new PVectorM3F<>(0.0f, 0.0f, 0.0f);
  }

  /**
   * <p>The emission value for the surface, in the range {@code [0.0, 1.0]}.
   * The value is typically processed by filters to give surfaces the appearance
   * of emitting light.</p>
   *
   * @return The emission value of the surface
   */

  @Value.Default
  default float emission()
  {
    return 0.0f;
  }

  /**
   * <p>An emission map which is used to modulate the value of {@link
   * #emission()}. Specifically, values sampled from the map are multiplied
   * by {@link #emission()} to give per-pixel control of the emission value.</p>
   *
   * @return The emission map for the surface
   */

  @Value.Default
  default R2Texture2DUsableType emissionTexture()
  {
    return this.textureDefaults().texture2DWhite();
  }

  /**
   * <p>An albedo map which is used to modulate the value of {@link
   * #albedoColor()}. Specifically, values sampled from the map are linearly
   * mixed with {@link #albedoColor()} based on {@link #albedoMix()}. The alpha
   * channel of the map is also used so that a texture with transparent sections
   * will show the underlying {@link #albedoColor()} even when the texture is
   * fully mixed (i.e. {@link #albedoMix()} == {@code 1.0}).</p>
   *
   * @return The albedo map for the surface
   */

  @Value.Default
  default R2Texture2DUsableType albedoTexture()
  {
    return this.textureDefaults().texture2DWhite();
  }

  /**
   * <p>A specification of the mix value between the albedo color and texture.
   * If {@code 0.0}, the albedo of the surface will be {@link #albedoColor()}.
   * If {@code 1.0}, the albedo of the surface will be a value sampled from
   * {@link #albedoTexture()}. Other values in the range {@code [0.0, 1.0]} will
   * linearly mix between the two extremes.</p>
   *
   * @return The mix between the albedo color and texture
   */

  @Value.Default
  default float albedoMix()
  {
    return 0.0f;
  }

  /**
   * <p>A specular map which is used to modulate the value of {@link
   * #specularColor()}. Specifically, values sampled from the map are multiplied
   * by {@link #specularColor()} to give per-pixel control of specular intensity
   * and color.</p>
   *
   * @return The specular map for the surface
   */

  @Value.Default
  default R2Texture2DUsableType specularTexture()
  {
    return this.textureDefaults().texture2DWhite();
  }

  /**
   * <p>The specular exponent. This is a value in the range {@code [0.0, 256.0]}
   * and is used directly as the exponent term.</p>
   *
   * @return The specular exponent
   */

  @Value.Default
  default float specularExponent()
  {
    return 64.0f;
  }

  /**
   * <p>A normal map used to peturb normal vectors for the surface. The texture
   * is expected to contain RGB-encoded vectors such that {@code [0.5, 0.5,
   * 1.0]} represents the "neutral" tangent-space {@code [0.0, 0.0, 1.0]}
   * vector.</p>
   *
   * <p>A map containing entirely {@code [0.5, 0.5, 1.0]} values will have no
   * visible effect on the surface.</p>
   *
   * @return The normal map
   */

  @Value.Default
  default R2Texture2DUsableType normalTexture()
  {
    return this.textureDefaults().texture2DNormal();
  }

  /**
   * <p>The alpha discard threshold specifies a lower bound on the opacity of
   * surface pixels. For any given surface pixel, if the opacity of the
   * calculated <i>albedo</i> is less than the {@link #alphaDiscardThreshold()},
   * then the pixel is simply discarded; the pixel is not written to the
   * current <i>geometry buffer</i>. This allows for otherwise opaque surfaces
   * to have fully transparent sections.</p>
   *
   * @return A discard threshold in the range {@code [0.0, 1.0]}
   */

  @Value.Default
  default float alphaDiscardThreshold()
  {
    return 0.0f;
  }
}
