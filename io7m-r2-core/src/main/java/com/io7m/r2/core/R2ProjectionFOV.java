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

import com.io7m.jcanephora.core.JCGLProjectionMatricesType;
import com.io7m.jnull.NullCheck;
import com.io7m.jranges.RangeCheck;
import com.io7m.jtensors.parameterized.PMatrixWritable4x4FType;
import com.io7m.r2.spaces.R2SpaceClipType;
import com.io7m.r2.spaces.R2SpaceEyeType;

/**
 * A perspective projection based on a given field of view and aspect ratio.
 */

public final class R2ProjectionFOV implements R2ProjectionType
{
  private final JCGLProjectionMatricesType context;
  private       float                      z_far;
  private       float                      z_near;
  private       float                      x_max;
  private       float                      x_min;
  private       float                      y_max;
  private       float                      y_min;
  private       float                      horizontal_fov;
  private       float                      aspect;

  private R2ProjectionFOV(
    final JCGLProjectionMatricesType in_context,
    final float in_fov,
    final float in_aspect,
    final float in_z_near,
    final float in_z_far)
  {
    this.context = NullCheck.notNull(in_context);
    this.x_max = 1.0f;
    this.x_min = -1.0f;
    this.y_max = 1.0f;
    this.y_min = -1.0f;
    this.z_far = in_z_far;
    this.z_near = in_z_near;

    RangeCheck.checkGreaterDouble(
      (double) in_aspect, "Aspect ratio", 0.0, "Minimum ratio");

    this.horizontal_fov = in_fov;
    this.aspect = in_aspect;
    this.update();
  }

  /**
   * Construct a new FOV projection.
   *
   * @param in_fov    The full horizontal field of view (the angle at the base
   *                  of the triangle formed by the frustum on the {@code x/z}
   *                  plane, in radians).
   * @param in_aspect The non-zero aspect ratio
   * @param in_z_near The distance to the near plane
   * @param in_z_far  The distance to the far plane
   * @param c         A matrices interface
   *
   * @return A new projection.
   */

  public static R2ProjectionFOV newFrustumWith(
    final JCGLProjectionMatricesType c,
    final float in_fov,
    final float in_aspect,
    final float in_z_near,
    final float in_z_far)
  {
    return new R2ProjectionFOV(c, in_fov, in_aspect, in_z_near, in_z_far);
  }

  private void update()
  {
    this.x_max = (float) (this.z_near * Math.tan(this.horizontal_fov / 2.0));
    this.x_min = -this.x_max;
    this.y_max = this.x_max / this.aspect;
    this.y_min = -this.y_max;
  }

  /**
   * @return The aspect ratio.
   */

  public float getAspectRatio()
  {
    return this.aspect;
  }

  /**
   * Set the aspect ratio. The given value must be {@code > 0.0}.
   *
   * @param r The new ratio
   */

  public void setAspectRatio(final float r)
  {
    RangeCheck.checkGreaterDouble(
      (double) r, "Aspect ratio", 0.0, "Minimum ratio");
    this.aspect = r;
    this.update();
  }

  /**
   * @return The horizontal field of view.
   */

  public float getHorizontalFOV()
  {
    return this.horizontal_fov;
  }

  /**
   * Set the horizontal field of view.
   *
   * @param f The full horizontal field of view (the angle at the base of the
   *          triangle formed by the frustum on the {@code x/z} plane, in
   *          radians).
   */

  public void setHorizontalFOV(final float f)
  {
    this.horizontal_fov = f;
    this.update();
  }

  /**
   * Set the distance to the far plane.
   *
   * @param z The Z distance
   */

  public void projectionSetZFar(final float z)
  {
    this.z_far = z;
  }

  /**
   * Set the distance to the near plane.
   *
   * @param z The Z distance
   */

  public void projectionSetZNear(final float z)
  {
    this.z_near = z;
  }

  @Override
  public void projectionMakeMatrix(
    final PMatrixWritable4x4FType<R2SpaceEyeType, R2SpaceClipType> m)
  {
    this.context.makeFrustumProjection(
      m,
      (double) this.x_min,
      (double) this.x_max,
      (double) this.y_min,
      (double) this.y_max,
      (double) this.z_near,
      (double) this.z_far);
  }

  @Override
  public float projectionGetXMaximum()
  {
    return this.x_max;
  }

  @Override
  public float projectionGetXMinimum()
  {
    return this.x_min;
  }

  @Override
  public float projectionGetYMaximum()
  {
    return this.y_max;
  }

  @Override
  public float projectionGetYMinimum()
  {
    return this.y_min;
  }

  @Override
  public float projectionGetZFar()
  {
    return this.z_far;
  }

  @Override
  public float projectionGetZNear()
  {
    return this.z_near;
  }
}
