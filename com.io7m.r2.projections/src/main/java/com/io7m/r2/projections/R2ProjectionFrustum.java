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

package com.io7m.r2.projections;

import com.io7m.jcanephora.core.JCGLProjectionMatrices;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.unparameterized.matrices.Matrix4x4D;
import com.io7m.r2.core.api.watchable.R2Watchable;
import com.io7m.r2.core.api.watchable.R2WatchableType;
import com.io7m.r2.spaces.R2SpaceClipType;
import com.io7m.r2.spaces.R2SpaceEyeType;

/**
 * A perspective projection based on an explicit frustum.
 */

public final class R2ProjectionFrustum implements R2ProjectionType
{
  private final R2WatchableType<R2ProjectionReadableType> watchable;
  private double far_x_min;
  private double far_x_max;
  private double far_y_min;
  private double far_y_max;
  private double far_z;
  private double near_x_max;
  private double near_x_min;
  private double near_y_max;
  private double near_y_min;
  private double near_z;

  private R2ProjectionFrustum(
    final double in_x_min,
    final double in_x_max,
    final double in_y_min,
    final double in_y_max,
    final double in_z_near,
    final double in_z_far)
  {
    this.near_x_max = in_x_max;
    this.near_x_min = in_x_min;
    this.near_y_max = in_y_max;
    this.near_y_min = in_y_min;
    this.near_z = in_z_near;
    this.far_z = in_z_far;

    this.watchable = R2Watchable.newWatchable(this);
    this.update();
  }

  /**
   * Construct a new frustum projection.
   *
   * @return A new projection
   */

  public static R2ProjectionFrustum create()
  {
    return new R2ProjectionFrustum(
      -1.0,
      1.0,
      -1.0,
      1.0,
      0.01,
      1000.0);
  }

  /**
   * Construct a new frustum projection.
   *
   * @param in_x_min  The minimum X value on the near plane
   * @param in_x_max  The maximum X value on the near plane
   * @param in_y_min  The minimum Y value on the near plane
   * @param in_y_max  The maximum Y value on the near plane
   * @param in_z_near The distance to the near plane
   * @param in_z_far  The distance to the far plane
   *
   * @return A new projection.
   */

  public static R2ProjectionFrustum createWith(
    final double in_x_min,
    final double in_x_max,
    final double in_y_min,
    final double in_y_max,
    final double in_z_near,
    final double in_z_far)
  {
    return new R2ProjectionFrustum(
      in_x_min,
      in_x_max,
      in_y_min,
      in_y_max,
      in_z_near,
      in_z_far);
  }

  private void update()
  {
    this.far_x_min = this.near_x_min * (this.far_z / this.near_z);
    this.far_x_max = this.near_x_max * (this.far_z / this.near_z);
    this.far_y_min = this.near_y_min * (this.far_z / this.near_z);
    this.far_y_max = this.near_y_max * (this.far_z / this.near_z);
    this.watchable.watchableChanged();
  }

  /**
   * Set the maximum X value on the near plane.
   *
   * @param x The X value
   */

  public void projectionSetXMaximum(final double x)
  {
    this.near_x_max = x;
    this.update();
  }

  /**
   * Set the minimum X value on the near plane.
   *
   * @param x The X value
   */

  public void projectionSetXMinimum(final double x)
  {
    this.near_x_min = x;
    this.update();
  }

  /**
   * Set the maximum Y value on the near plane.
   *
   * @param y The Y value
   */

  public void projectionSetYMaximum(final double y)
  {
    this.near_y_max = y;
    this.update();
  }

  /**
   * Set the minimum Y value on the near plane.
   *
   * @param y The Y value
   */

  public void projectionSetYMinimum(final double y)
  {
    this.near_y_min = y;
    this.update();
  }

  /**
   * Set the distance to the far plane.
   *
   * @param z The Z distance
   */

  public void projectionSetZFar(final double z)
  {
    this.far_z = z;
    this.update();
  }

  /**
   * Set the distance to the near plane.
   *
   * @param z The Z distance
   */

  public void projectionSetZNear(final double z)
  {
    this.near_z = z;
    this.update();
  }

  @Override
  public PMatrix4x4D<R2SpaceEyeType, R2SpaceClipType> projectionMakeMatrix()
  {
    return PMatrices4x4D.toParameterized(
      JCGLProjectionMatrices.frustumProjectionRH(
        this.near_x_min,
        this.near_x_max,
        this.near_y_min,
        this.near_y_max,
        this.near_z,
        this.far_z));
  }

  @Override
  public Matrix4x4D projectionMakeMatrixUntyped()
  {
    return JCGLProjectionMatrices.frustumProjectionRH(
      this.near_x_min,
      this.near_x_max,
      this.near_y_min,
      this.near_y_max,
      this.near_z,
      this.far_z);
  }

  @Override
  public double projectionGetNearXMaximum()
  {
    return this.near_x_max;
  }

  @Override
  public double projectionGetNearXMinimum()
  {
    return this.near_x_min;
  }

  @Override
  public double projectionGetNearYMaximum()
  {
    return this.near_y_max;
  }

  @Override
  public double projectionGetNearYMinimum()
  {
    return this.near_y_min;
  }

  @Override
  public double projectionGetZFar()
  {
    return this.far_z;
  }

  @Override
  public double projectionGetZNear()
  {
    return this.near_z;
  }

  @Override
  public double projectionGetFarXMaximum()
  {
    return this.far_x_max;
  }

  @Override
  public double projectionGetFarXMinimum()
  {
    return this.far_x_min;
  }

  @Override
  public double projectionGetFarYMaximum()
  {
    return this.far_y_max;
  }

  @Override
  public double projectionGetFarYMinimum()
  {
    return this.far_y_min;
  }

  @Override
  public R2WatchableType<R2ProjectionReadableType> projectionGetWatchable()
  {
    return this.watchable;
  }
}
