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
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.r2.spaces.R2SpaceRGBType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * Parameters for single directional lights.
 */

public final class R2LightDirectionalScreenSingle
  implements R2LightScreenSingleType
{
  private final PVector3D<R2SpaceWorldType> direction;
  private final long id;
  private final R2UnitQuadUsableType quad;
  private PVector3D<R2SpaceRGBType> color;
  private double intensity;

  private R2LightDirectionalScreenSingle(
    final R2UnitQuadUsableType in_quad,
    final long in_id)
  {
    this.quad = NullCheck.notNull(in_quad, "Quad");
    this.id = in_id;
    this.color = PVector3D.of(1.0, 1.0, 1.0);
    this.direction = PVector3D.of(0.0, 0.0, -1.0);
    this.intensity = 1.0;
  }

  /**
   * Construct a new light.
   *
   * @param q    A unit quad
   * @param pool The ID pool
   *
   * @return A new light
   */

  public static R2LightDirectionalScreenSingle create(
    final R2UnitQuadUsableType q,
    final R2IDPoolType pool)
  {
    NullCheck.notNull(pool, "Pool");
    return new R2LightDirectionalScreenSingle(q, pool.freshID());
  }

  /**
   * @return The readable/writable light color
   */

  @Override
  public PVector3D<R2SpaceRGBType> color()
  {
    return this.color;
  }

  /**
   * @return The readable/writable light direction
   */

  public PVector3D<R2SpaceWorldType> direction()
  {
    return this.direction;
  }

  @Override
  public double intensity()
  {
    return this.intensity;
  }

  @Override
  public void setColor(final PVector3D<R2SpaceRGBType> in_color)
  {
    this.color = NullCheck.notNull(in_color, "Color");
  }

  @Override
  public void setIntensity(
    final double i)
  {
    this.intensity = Math.max(0.0, i);
  }

  @Override
  public long lightID()
  {
    return this.id;
  }

  @Override
  public JCGLArrayObjectUsableType arrayObject()
  {
    return this.quad.arrayObject();
  }

  @Override
  public R2TransformReadableType transform()
  {
    return R2TransformIdentity.get();
  }
}
