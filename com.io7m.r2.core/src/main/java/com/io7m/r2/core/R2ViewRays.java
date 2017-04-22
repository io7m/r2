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

import com.io7m.jfunctional.Pair;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.jtensors.core.parameterized.vectors.PVectors4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors3D;
import com.io7m.r2.spaces.R2SpaceClipType;
import com.io7m.r2.spaces.R2SpaceEyeType;

/**
 * The default implementation of the {@link R2ViewRaysType} interface.
 */

public final class R2ViewRays implements R2ViewRaysType
{
  private static final PVector4D<R2SpaceClipType> FAR_X0Y0;
  private static final PVector4D<R2SpaceClipType> FAR_X0Y1;
  private static final PVector4D<R2SpaceClipType> FAR_X1Y0;
  private static final PVector4D<R2SpaceClipType> FAR_X1Y1;
  private static final PVector4D<R2SpaceClipType> NEAR_X0Y0;
  private static final PVector4D<R2SpaceClipType> NEAR_X0Y1;
  private static final PVector4D<R2SpaceClipType> NEAR_X1Y0;
  private static final PVector4D<R2SpaceClipType> NEAR_X1Y1;

  static {
    NEAR_X0Y0 = PVector4D.of(-1.0, -1.0, -1.0, 1.0);
    NEAR_X1Y0 = PVector4D.of(1.0, -1.0, -1.0, 1.0);
    NEAR_X0Y1 = PVector4D.of(-1.0, 1.0, -1.0, 1.0);
    NEAR_X1Y1 = PVector4D.of(1.0, 1.0, -1.0, 1.0);

    FAR_X0Y0 = PVector4D.of(-1.0, -1.0, 1.0, 1.0);
    FAR_X1Y0 = PVector4D.of(1.0, -1.0, 1.0, 1.0);
    FAR_X0Y1 = PVector4D.of(-1.0, 1.0, 1.0, 1.0);
    FAR_X1Y1 = PVector4D.of(1.0, 1.0, 1.0, 1.0);
  }

  private Vector3D origin_x0y0;
  private Vector3D origin_x0y1;
  private Vector3D origin_x1y0;
  private Vector3D origin_x1y1;
  private Vector3D ray_x0y0;
  private Vector3D ray_x0y1;
  private Vector3D ray_x1y0;
  private Vector3D ray_x1y1;

  private R2ViewRays(
    final Vector3D in_origin_x0y0,
    final Vector3D in_origin_x1y0,
    final Vector3D in_origin_x0y1,
    final Vector3D in_origin_x1y1,
    final Vector3D in_ray_x0y0,
    final Vector3D in_ray_x1y0,
    final Vector3D in_ray_x0y1,
    final Vector3D in_ray_x1y1)
  {
    this.origin_x0y0 = NullCheck.notNull(in_origin_x0y0, "Origin");
    this.origin_x1y0 = NullCheck.notNull(in_origin_x1y0, "Origin");
    this.origin_x0y1 = NullCheck.notNull(in_origin_x0y1, "Origin");
    this.origin_x1y1 = NullCheck.notNull(in_origin_x1y1, "Origin");
    this.ray_x0y0 = NullCheck.notNull(in_ray_x0y0, "Ray");
    this.ray_x1y0 = NullCheck.notNull(in_ray_x1y0, "Ray");
    this.ray_x0y1 = NullCheck.notNull(in_ray_x0y1, "Ray");
    this.ray_x1y1 = NullCheck.notNull(in_ray_x1y1, "Ray");
  }

  /**
   * Construct a new set of view rays.
   *
   * @return A new set of view rays
   */

  public static R2ViewRays newViewRays()
  {
    final R2ViewRays vr =
      new R2ViewRays(
        Vectors3D.zero(), Vectors3D.zero(), Vectors3D.zero(), Vectors3D.zero(),
        Vectors3D.zero(), Vectors3D.zero(), Vectors3D.zero(), Vectors3D.zero());
    vr.recalculate(PMatrices4x4D.identity());
    return vr;
  }

  private static Pair<Vector3D, Vector3D> calculateRayAndOrigin(
    final PMatrix4x4D<R2SpaceClipType, R2SpaceEyeType> m,
    final PVector4D<R2SpaceClipType> near,
    final PVector4D<R2SpaceClipType> far)
  {
    /*
     * Transform NDC → Clip.
     */

    PVector4D<R2SpaceEyeType> temp_near =
      PMatrices4x4D.multiplyVectorPost(m, near);
    PVector4D<R2SpaceEyeType> temp_far =
      PMatrices4x4D.multiplyVectorPost(m, far);

    /*
     * Normalize in terms of w: NDC coordinates would usually be lacking a w
     * component, but the w component included in the original frustum corners
     * is necessary in order to multiply by a 4x4 matrix.
     */

    temp_near = PVectors4D.scale(temp_near, 1.0 / temp_near.w());
    temp_far = PVectors4D.scale(temp_far, 1.0 / temp_far.w());

    /*
     * Subtract the near corner from the far corner to produce a ray.
     */

    PVector4D<R2SpaceEyeType> out_ray =
      PVectors4D.subtract(temp_far, temp_near);

    /*
     * Normalize the ray in terms of z.
     */

    out_ray = PVectors4D.scale(out_ray, 1.0 / out_ray.z());

    /*
     * Subtract the scaled ray from the near corner to produce an origin.
     */

    PVector4D<R2SpaceEyeType> out_origin =
      PVectors4D.scale(out_ray, temp_near.z());
    out_origin = PVectors4D.subtract(temp_near, out_origin);

    return Pair.pair(
      Vector3D.of(out_ray.x(), out_ray.y(), out_ray.z()),
      Vector3D.of(out_origin.x(), out_origin.y(), out_origin.z()));
  }

  @Override
  public void recalculate(
    final PMatrix4x4D<R2SpaceClipType, R2SpaceEyeType> m)
  {
    NullCheck.notNull(m, "Matrix");

    {
      final Pair<Vector3D, Vector3D> p =
        calculateRayAndOrigin(m, NEAR_X0Y0, FAR_X0Y0);
      this.ray_x0y0 = p.getLeft();
      this.origin_x0y0 = p.getRight();
    }

    {
      final Pair<Vector3D, Vector3D> p =
        calculateRayAndOrigin(m, NEAR_X1Y0, FAR_X1Y0);
      this.ray_x1y0 = p.getLeft();
      this.origin_x1y0 = p.getRight();
    }

    {
      final Pair<Vector3D, Vector3D> p =
        calculateRayAndOrigin(m, NEAR_X0Y1, FAR_X0Y1);
      this.ray_x0y1 = p.getLeft();
      this.origin_x0y1 = p.getRight();
    }

    {
      final Pair<Vector3D, Vector3D> p =
        calculateRayAndOrigin(m, NEAR_X1Y1, FAR_X1Y1);
      this.ray_x1y1 = p.getLeft();
      this.origin_x1y1 = p.getRight();
    }
  }

  /**
   * @return The x0y0 origin
   */

  @Override
  public Vector3D originX0Y0()
  {
    return this.origin_x0y0;
  }

  /**
   * @return The x0y1 origin
   */

  @Override
  public Vector3D originX0Y1()
  {
    return this.origin_x0y1;
  }

  /**
   * @return The x1y0 origin
   */

  @Override
  public Vector3D originX1Y0()
  {
    return this.origin_x1y0;
  }

  /**
   * @return The x1y1 origin
   */

  @Override
  public Vector3D originX1Y1()
  {
    return this.origin_x1y1;
  }

  /**
   * @return The x0y0 view ray
   */

  @Override
  public Vector3D rayX0Y0()
  {
    return this.ray_x0y0;
  }

  /**
   * @return The x0y1 view ray
   */

  @Override
  public Vector3D rayX0Y1()
  {
    return this.ray_x0y1;
  }

  /**
   * @return The x1y0 view ray
   */

  @Override
  public Vector3D rayX1Y0()
  {
    return this.ray_x1y0;
  }

  /**
   * @return The x1y1 view ray
   */

  @Override
  public Vector3D rayX1Y1()
  {
    return this.ray_x1y1;
  }
}
