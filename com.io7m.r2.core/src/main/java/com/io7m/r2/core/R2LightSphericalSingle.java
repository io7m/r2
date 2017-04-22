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
 * Parameters for simple spherical lights that operate by rendering scaled unit
 * spheres.
 */

public final class R2LightSphericalSingle implements R2LightSphericalSingleType
{
  private final long id;
  private final JCGLArrayObjectUsableType volume;
  private final R2TransformST transform;
  private PVector3D<R2SpaceRGBType> color;
  private double falloff;
  private double intensity;
  private double radius;
  private double scale_factor;

  private R2LightSphericalSingle(
    final JCGLArrayObjectUsableType in_volume,
    final long in_id)
  {
    this.id = in_id;
    this.volume = NullCheck.notNull(in_volume, "Volume");
    this.transform = R2TransformST.create();
    this.radius = 1.0;
    this.color = PVector3D.of(1.0, 1.0, 1.0);
    this.intensity = 1.0;
    this.falloff = 1.0;
    this.scale_factor = 1.0;
  }

  /**
   * Construct a new light.
   *
   * @param in_sphere The light volume
   * @param in_pool   The ID pool
   *
   * @return A new light
   */

  public static R2LightSphericalSingle newLight(
    final R2UnitSphereUsableType in_sphere,
    final R2IDPoolType in_pool)
  {
    NullCheck.notNull(in_pool, "Pool");
    NullCheck.notNull(in_sphere, "Sphere");
    return new R2LightSphericalSingle(
      in_sphere.arrayObject(), in_pool.freshID());
  }

  @Override
  public double radius()
  {
    return this.radius;
  }

  @Override
  public void setRadius(
    final double r)
  {
    this.radius = Math.max(0.001, r);
    this.transform.setScale(r * this.scale_factor);
  }

  @Override
  public double falloff()
  {
    return this.falloff;
  }

  @Override
  public void setFalloff(final double f)
  {
    this.falloff = f;
  }

  @Override
  public PVector3D<R2SpaceRGBType> color()
  {
    return this.color;
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
  public PVector3D<R2SpaceWorldType> originPosition()
  {
    return this.transform.getTranslation();
  }

  @Override
  public JCGLArrayObjectUsableType arrayObject()
  {
    return this.volume;
  }

  @Override
  public long lightID()
  {
    return this.id;
  }

  @Override
  public R2TransformReadableType transform()
  {
    return this.transform;
  }

  @Override
  public double geometryScaleFactor()
  {
    return this.scale_factor;
  }

  @Override
  public void setOriginPosition(
    final PVector3D<R2SpaceWorldType> p)
  {
    this.transform.setTranslation(NullCheck.notNull(p, "Position"));
  }

  @Override
  public void setGeometryScaleFactor(final double f)
  {
    this.scale_factor = Math.max(0.001, f);
    this.transform.setScale(this.radius * this.scale_factor);
  }
}
