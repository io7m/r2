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

import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.Vector4FType;
import com.io7m.jtensors.VectorM4F;
import com.io7m.jtensors.VectorReadable4FType;
import com.io7m.jtensors.parameterized.PMatrixI4x4F;
import com.io7m.jtensors.parameterized.PMatrixM4x4F;
import com.io7m.jtensors.parameterized.PMatrixReadable4x4FType;
import com.io7m.jtensors.parameterized.PVector4FType;
import com.io7m.jtensors.parameterized.PVectorI4F;
import com.io7m.jtensors.parameterized.PVectorM4F;
import com.io7m.r2.spaces.R2SpaceClipType;
import com.io7m.r2.spaces.R2SpaceEyeType;

/**
 * The default implementation of the {@link R2ViewRaysType} interface.
 */

public final class R2ViewRays implements R2ViewRaysType
{
  private static final PVectorI4F<R2SpaceClipType> FAR_X0Y0;
  private static final PVectorI4F<R2SpaceClipType> FAR_X0Y1;
  private static final PVectorI4F<R2SpaceClipType> FAR_X1Y0;
  private static final PVectorI4F<R2SpaceClipType> FAR_X1Y1;
  private static final PVectorI4F<R2SpaceClipType> NEAR_X0Y0;
  private static final PVectorI4F<R2SpaceClipType> NEAR_X0Y1;
  private static final PVectorI4F<R2SpaceClipType> NEAR_X1Y0;
  private static final PVectorI4F<R2SpaceClipType> NEAR_X1Y1;

  static {
    NEAR_X0Y0 = new PVectorI4F<>(-1.0f, -1.0f, -1.0f, 1.0f);
    NEAR_X1Y0 = new PVectorI4F<>(1.0f, -1.0f, -1.0f, 1.0f);
    NEAR_X0Y1 = new PVectorI4F<>(-1.0f, 1.0f, -1.0f, 1.0f);
    NEAR_X1Y1 = new PVectorI4F<>(1.0f, 1.0f, -1.0f, 1.0f);

    FAR_X0Y0 = new PVectorI4F<>(-1.0f, -1.0f, 1.0f, 1.0f);
    FAR_X1Y0 = new PVectorI4F<>(1.0f, -1.0f, 1.0f, 1.0f);
    FAR_X0Y1 = new PVectorI4F<>(-1.0f, 1.0f, 1.0f, 1.0f);
    FAR_X1Y1 = new PVectorI4F<>(1.0f, 1.0f, 1.0f, 1.0f);
  }

  private final Vector4FType origin_x0y0;
  private final Vector4FType origin_x0y1;
  private final Vector4FType origin_x1y0;
  private final Vector4FType origin_x1y1;
  private final Vector4FType ray_x0y0;
  private final Vector4FType ray_x0y1;
  private final Vector4FType ray_x1y0;
  private final Vector4FType ray_x1y1;
  private final PVector4FType<R2SpaceEyeType> temp_near;
  private final PVector4FType<R2SpaceEyeType> temp_far;

  private R2ViewRays(
    final Vector4FType in_origin_x0y0,
    final Vector4FType in_origin_x1y0,
    final Vector4FType in_origin_x0y1,
    final Vector4FType in_origin_x1y1,
    final Vector4FType in_ray_x0y0,
    final Vector4FType in_ray_x1y0,
    final Vector4FType in_ray_x0y1,
    final Vector4FType in_ray_x1y1)
  {
    this.origin_x0y0 = NullCheck.notNull(in_origin_x0y0, "Origin");
    this.origin_x1y0 = NullCheck.notNull(in_origin_x1y0, "Origin");
    this.origin_x0y1 = NullCheck.notNull(in_origin_x0y1, "Origin");
    this.origin_x1y1 = NullCheck.notNull(in_origin_x1y1, "Origin");
    this.ray_x0y0 = NullCheck.notNull(in_ray_x0y0, "Ray");
    this.ray_x1y0 = NullCheck.notNull(in_ray_x1y0, "Ray");
    this.ray_x0y1 = NullCheck.notNull(in_ray_x0y1, "Ray");
    this.ray_x1y1 = NullCheck.notNull(in_ray_x1y1, "Ray");

    this.temp_near = new PVectorM4F<>();
    this.temp_far = new PVectorM4F<>();
  }

  /**
   * Construct a new set of view rays.
   *
   * @param c Preallocated storage for matrix operations
   *
   * @return A new set of view rays
   */

  public static R2ViewRaysType newViewRays(
    final PMatrixM4x4F.ContextPM4F c)
  {
    final R2ViewRays vr = new R2ViewRays(
      new VectorM4F(),
      new VectorM4F(),
      new VectorM4F(),
      new VectorM4F(),
      new VectorM4F(),
      new VectorM4F(),
      new VectorM4F(),
      new VectorM4F());
    vr.recalculate(c, PMatrixI4x4F.identity());
    return vr;
  }

  private static void calculateRayAndOrigin(
    final PMatrixM4x4F.ContextPM4F c,
    final PMatrixReadable4x4FType<R2SpaceClipType, R2SpaceEyeType> m,
    final PVectorI4F<R2SpaceClipType> near,
    final PVectorI4F<R2SpaceClipType> far,
    final PVector4FType<R2SpaceEyeType> temp_near,
    final PVector4FType<R2SpaceEyeType> temp_far,
    final Vector4FType out_ray,
    final Vector4FType out_origin)
  {
    /**
     * Transform NDC → Clip.
     */

    PMatrixM4x4F.multiplyVector4F(c, m, near, temp_near);
    PMatrixM4x4F.multiplyVector4F(c, m, far, temp_far);

    /**
     * Normalize in terms of w: NDC coordinates would usually be lacking a w
     * component, but the w component included in the original frustum corners
     * is necessary in order to multiply by a 4x4 matrix.
     */

    PVectorM4F.scaleInPlace(temp_near, 1.0 / temp_near.getWF());
    PVectorM4F.scaleInPlace(temp_far, 1.0 / temp_far.getWF());

    /**
     * Subtract the near corner from the far corner to produce a ray.
     */

    VectorM4F.subtract(temp_far, temp_near, out_ray);

    /**
     * Normalize the ray in terms of z.
     */

    VectorM4F.scaleInPlace(out_ray, 1.0 / out_ray.getZF());

    /**
     * Subtract the scaled ray from the near corner to produce an origin.
     */

    VectorM4F.scale(out_ray, temp_near.getZF(), out_origin);
    VectorM4F.subtract(temp_near, out_origin, out_origin);
  }

  @Override
  public void recalculate(
    final PMatrixM4x4F.ContextPM4F c,
    final PMatrixReadable4x4FType<R2SpaceClipType, R2SpaceEyeType> m)
  {
    NullCheck.notNull(c);
    NullCheck.notNull(m);

    R2ViewRays.calculateRayAndOrigin(
      c,
      m,
      R2ViewRays.NEAR_X0Y0,
      R2ViewRays.FAR_X0Y0,
      this.temp_near,
      this.temp_far,
      this.ray_x0y0,
      this.origin_x0y0);

    R2ViewRays.calculateRayAndOrigin(
      c,
      m,
      R2ViewRays.NEAR_X1Y0,
      R2ViewRays.FAR_X1Y0,
      this.temp_near,
      this.temp_far,
      this.ray_x1y0,
      this.origin_x1y0);

    R2ViewRays.calculateRayAndOrigin(
      c,
      m,
      R2ViewRays.NEAR_X0Y1,
      R2ViewRays.FAR_X0Y1,
      this.temp_near,
      this.temp_far,
      this.ray_x0y1,
      this.origin_x0y1);

    R2ViewRays.calculateRayAndOrigin(
      c,
      m,
      R2ViewRays.NEAR_X1Y1,
      R2ViewRays.FAR_X1Y1,
      this.temp_near,
      this.temp_far,
      this.ray_x1y1,
      this.origin_x1y1);
  }

  /**
   * @return The x0y0 origin
   */

  @Override
  public VectorReadable4FType originX0Y0()
  {
    return this.origin_x0y0;
  }

  /**
   * @return The x0y1 origin
   */

  @Override
  public VectorReadable4FType originX0Y1()
  {
    return this.origin_x0y1;
  }

  /**
   * @return The x1y0 origin
   */

  @Override
  public VectorReadable4FType originX1Y0()
  {
    return this.origin_x1y0;
  }

  /**
   * @return The x1y1 origin
   */

  @Override
  public VectorReadable4FType originX1Y1()
  {
    return this.origin_x1y1;
  }

  /**
   * @return The x0y0 view ray
   */

  @Override
  public VectorReadable4FType rayX0Y0()
  {
    return this.ray_x0y0;
  }

  /**
   * @return The x0y1 view ray
   */

  @Override
  public VectorReadable4FType rayX0Y1()
  {
    return this.ray_x0y1;
  }

  /**
   * @return The x1y0 view ray
   */

  @Override
  public VectorReadable4FType rayX1Y0()
  {
    return this.ray_x1y0;
  }

  /**
   * @return The x1y1 view ray
   */

  @Override
  public VectorReadable4FType rayX1Y1()
  {
    return this.ray_x1y1;
  }
}
