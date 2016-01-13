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
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceRGBType;

/**
 * Parameters for single directional lights.
 */

public final class R2LightDirectionalSingle
{
  private final PVector3FType<R2SpaceRGBType> color;
  private final PVector3FType<R2SpaceEyeType> direction;
  private       float                         intensity;

  /**
   * Construct a directional light.
   */

  public R2LightDirectionalSingle()
  {
    this.color = new PVectorM3F<>(1.0f, 1.0f, 1.0f);
    this.direction = new PVectorM3F<>(0.0f, 0.0f, -1.0f);
    this.intensity = 1.0f;
  }

  /**
   * @return The readable/writable light color
   */

  public PVector3FType<R2SpaceRGBType> getColor()
  {
    return this.color;
  }

  /**
   * @return The readable/writable light direction
   */

  public PVector3FType<R2SpaceEyeType> getDirection()
  {
    return this.direction;
  }

  /**
   * @return The current light intensity
   */

  public float getIntensity()
  {
    return this.intensity;
  }

  /**
   * Set the light intensity.
   *
   * @param i The intensity
   */

  public void setIntensity(final float i)
  {
    this.intensity = i;
  }
}
