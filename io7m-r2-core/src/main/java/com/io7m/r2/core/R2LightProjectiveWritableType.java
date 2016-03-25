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

/**
 * The type of writable projective lights.
 */

public interface R2LightProjectiveWritableType
{
  /**
   * @return The writable transform for the light
   */

  R2TransformOTType getTransformWritable();

  /**
   * Set the light radius in world-space units.
   *
   * @param r The light radius
   */

  void setRadius(
    float r);

  /**
   * Set the light falloff exponent
   *
   * @param r The light falloff
   */

  void setFalloff(
    float r);

  /**
   * Set the light intensity.
   *
   * @param i The intensity
   */

  void setIntensity(
    float i);
}
