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
import com.io7m.jtensors.parameterized.PVectorReadable3FType;
import com.io7m.r2.spaces.R2SpaceRGBType;

/**
 * Parameters for single ambient lights.
 */

public final class R2LightAmbientScreenSingle implements R2LightScreenSingleType
{
  private final PVector3FType<R2SpaceRGBType> color;
  private final long id;
  private final R2UnitQuadUsableType quad;
  private float intensity;
  private R2Texture2DUsableType occlusion_map;

  private R2LightAmbientScreenSingle(
    final R2UnitQuadUsableType in_quad,
    final long in_id,
    final R2Texture2DUsableType in_occ)
  {
    this.quad = NullCheck.notNull(in_quad);
    this.id = in_id;
    this.color = new PVectorM3F<>(1.0f, 1.0f, 1.0f);
    this.intensity = 1.0f;
    this.occlusion_map = NullCheck.notNull(in_occ);
  }

  /**
   * Construct a new light.
   *
   * @param q           A unit quad
   * @param pool        The ID pool
   * @param in_defaults A set of default textures
   *
   * @return A new light
   */

  public static R2LightAmbientScreenSingle newLight(
    final R2UnitQuadUsableType q,
    final R2IDPoolType pool,
    final R2TextureDefaultsType in_defaults)
  {
    NullCheck.notNull(pool);
    NullCheck.notNull(in_defaults);

    return new R2LightAmbientScreenSingle(
      q, pool.getFreshID(), in_defaults.texture2DWhite());
  }

  @Override
  public PVectorReadable3FType<R2SpaceRGBType> getColor()
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
  public PVector3FType<R2SpaceRGBType> getColorWritable()
  {
    return this.color;
  }

  /**
   * @return The occlusion map for the light
   */

  public R2Texture2DUsableType getOcclusionMap()
  {
    return this.occlusion_map;
  }

  /**
   * Set the occlusion map for the light.
   *
   * @param m The occlusion map
   */

  public void setOcclusionMap(
    final R2Texture2DUsableType m)
  {
    this.occlusion_map = NullCheck.notNull(m);
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

  @Override
  public R2TransformReadableType getTransform()
  {
    return R2TransformIdentity.getInstance();
  }
}
