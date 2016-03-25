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
import com.io7m.jfunctional.PartialBiFunctionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.r2.spaces.R2SpaceRGBType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * Parameters for simple spherical lights that operate by rendering scaled unit
 * spheres.
 */

public final class R2LightSphericalSimpleSingle implements
  R2LightSphericalSingleType
{
  private final PVector3FType<R2SpaceRGBType> color;
  private final long                          id;
  private final R2UnitSphereUsableType        sphere;
  private final R2TransformST                 transform;
  private       float                         falloff;
  private       float                         intensity;
  private       float                         radius;

  private R2LightSphericalSimpleSingle(
    final R2UnitSphereUsableType in_sphere,
    final long in_id)
  {
    this.id = in_id;
    this.sphere = NullCheck.notNull(in_sphere);
    this.transform = R2TransformST.newTransform();
    this.radius = 1.0f;
    this.color = new PVectorM3F<>(1.0f, 1.0f, 1.0f);
    this.intensity = 1.0f;
    this.falloff = 1.0f;
  }

  /**
   * Construct a new light.
   *
   * @param in_sphere The light volume sphere
   * @param in_pool   The ID pool
   *
   * @return A new light
   */

  public static R2LightSphericalSingleType newLight(
    final R2UnitSphereUsableType in_sphere,
    final R2IDPoolType in_pool)
  {
    NullCheck.notNull(in_pool);
    NullCheck.notNull(in_sphere);
    return new R2LightSphericalSimpleSingle(in_sphere, in_pool.getFreshID());
  }

  @Override
  public float getRadius()
  {
    return this.radius;
  }

  @Override
  public void setRadius(
    final float r)
  {
    this.radius = Math.max(0.001f, r);
    this.transform.setScale(this.radius);
  }

  @Override
  public float getFalloff()
  {
    return this.falloff;
  }

  @Override
  public void setFalloff(final float f)
  {
    this.falloff = f;
  }

  @Override
  public PVector3FType<R2SpaceRGBType> getColor()
  {
    return this.color;
  }

  @Override
  public float getIntensity()
  {
    return this.intensity;
  }

  @Override
  public void setIntensity(
    final float i)
  {
    this.intensity = Math.max(0.0f, i);
  }

  @Override
  public PVector3FType<R2SpaceWorldType> getPosition()
  {
    return this.transform.getTranslation();
  }

  @Override
  public JCGLArrayObjectUsableType getArrayObject()
  {
    return this.sphere.getArrayObject();
  }

  @Override
  public long getLightID()
  {
    return this.id;
  }

  @Override
  public R2TransformReadableType getTransform()
  {
    return this.transform;
  }

  @Override
  public <A, B, E extends Throwable> B matchLightSingle(
    final A context,
    final PartialBiFunctionType<A, R2LightVolumeSingleType, B, E> on_volume,
    final PartialBiFunctionType<A, R2LightScreenSingleType, B, E> on_screen)
    throws E
  {
    return on_volume.call(context, this);
  }
}
