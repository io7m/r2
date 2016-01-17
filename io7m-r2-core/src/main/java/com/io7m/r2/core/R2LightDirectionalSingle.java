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

import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.r2.spaces.R2SpaceRGBType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * Parameters for single directional lights.
 */

public final class R2LightDirectionalSingle implements R2LightScreenSingleType
{
  private final PVector3FType<R2SpaceRGBType>   color;
  private final PVector3FType<R2SpaceWorldType> direction;
  private final long                            id;
  private final R2UnitQuadUsableType            quad;
  private       float                           intensity;

  private R2LightDirectionalSingle(
    final R2UnitQuadUsableType in_quad,
    final long in_id)
  {
    this.quad = NullCheck.notNull(in_quad);
    this.id = in_id;
    this.color = new PVectorM3F<>(1.0f, 1.0f, 1.0f);
    this.direction = new PVectorM3F<>(0.0f, 0.0f, -1.0f);
    this.intensity = 1.0f;
  }

  /**
   * Construct a new light.
   *
   * @param q    A unit quad
   * @param pool The ID pool
   *
   * @return A new light
   */

  public static R2LightDirectionalSingle newLight(
    final R2UnitQuadUsableType q,
    final R2IDPoolType pool)
  {
    NullCheck.notNull(pool);
    return new R2LightDirectionalSingle(q, pool.getFreshID());
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

  public PVector3FType<R2SpaceWorldType> getDirection()
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

  public void setIntensity(
    final float i)
  {
    this.intensity = i;
  }

  @Override
  public long getLightID()
  {
    return this.id;
  }

  @Override
  public JCGLArrayObjectUsableType getArrayObject()
  {
    return this.quad.getArrayObject();
  }
}
