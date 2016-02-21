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

import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.r2.spaces.R2SpaceRGBType;

/**
 * The type of projective lights.
 */

public interface R2LightProjectiveType extends R2LightSingleType
{
  /**
   * @return The light radius
   */

  float getRadius();

  /**
   * Set the light radius in world-space units.
   *
   * @param r The light radius
   */

  void setRadius(
    float r);

  /**
   * @return The light falloff exponent
   */

  float getFalloff();

  /**
   * Set the light falloff exponent
   *
   * @param r The light falloff
   */

  void setFalloff(
    float r);

  /**
   * @return The readable/writable light color
   */

  PVector3FType<R2SpaceRGBType> getColor();

  /**
   * @return The current light intensity
   */

  float getIntensity();

  /**
   * Set the light intensity.
   *
   * @param i The intensity
   */

  void setIntensity(
    float i);

  /**
   * @return The readable light transform
   */

  @Override
  R2TransformOTReadableType getTransform();

  /**
   * @return The light's projection
   */

  R2ProjectionReadableType getProjection();
}
