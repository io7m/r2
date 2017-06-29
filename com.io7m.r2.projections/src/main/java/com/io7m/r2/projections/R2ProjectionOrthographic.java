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
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.unparameterized.matrices.Matrix4x4D;
import com.io7m.r2.core.api.watchable.R2Watchable;
import com.io7m.r2.core.api.watchable.R2WatchableType;
import com.io7m.r2.spaces.R2SpaceClipType;
import com.io7m.r2.spaces.R2SpaceEyeType;

/**
 * An orthographic projection.
 */

public final class R2ProjectionOrthographic implements R2ProjectionType
{
  private final R2WatchableType<R2ProjectionReadableType> watchable;
  private double x_max;
  private double x_min;
  private double y_max;
  private double y_min;
  private double z_far;
  private double z_near;

  private R2ProjectionOrthographic(
    final double in_x_min,
    final double in_x_max,
    final double in_y_min,
    final double in_y_max,
    final double in_z_near,
    final double in_z_far)
  {
    this.x_max = in_x_max;
    this.x_min = in_x_min;
    this.y_max = in_y_max;
    this.y_min = in_y_min;
    this.z_far = in_z_far;
    this.z_near = in_z_near;
    this.watchable = R2Watchable.newWatchable(this);
  }

  /**
   * Construct a new frustum projection.
   *
   * @return A new projection
   */

  public static R2ProjectionOrthographic create()
  {
    return new R2ProjectionOrthographic(
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

  public static R2ProjectionOrthographic createWith(
    final double in_x_min,
    final double in_x_max,
    final double in_y_min,
    final double in_y_max,
    final double in_z_near,
    final double in_z_far)
  {
    return new R2ProjectionOrthographic(
      in_x_min,
      in_x_max,
      in_y_min,
      in_y_max,
      in_z_near,
      in_z_far);
  }

  /**
   * Set the maximum X value on the near plane.
   *
   * @param x The X value
   */

  public void projectionSetXMaximum(final double x)
  {
    this.x_max = x;
    this.watchable.watchableChanged();
  }

  /**
   * Set the minimum X value on the near plane.
   *
   * @param x The X value
   */

  public void projectionSetXMinimum(final double x)
  {
    this.x_min = x;
    this.watchable.watchableChanged();
  }

  /**
   * Set the maximum Y value on the near plane.
   *
   * @param y The Y value
   */

  public void projectionSetYMaximum(final double y)
  {
    this.y_max = y;
    this.watchable.watchableChanged();
  }

  /**
   * Set the minimum Y value on the near plane.
   *
   * @param y The Y value
   */

  public void projectionSetYMinimum(final double y)
  {
    this.y_min = y;
    this.watchable.watchableChanged();
  }

  /**
   * Set the distance to the far plane.
   *
   * @param z The Z distance
   */

  public void projectionSetZFar(final double z)
  {
    this.z_far = z;
    this.watchable.watchableChanged();
  }

  /**
   * Set the distance to the near plane.
   *
   * @param z The Z distance
   */

  public void projectionSetZNear(final double z)
  {
    this.z_near = z;
    this.watchable.watchableChanged();
  }

  @Override
  public PMatrix4x4D<R2SpaceEyeType, R2SpaceClipType> projectionMakeMatrix()
  {
    return JCGLProjectionMatrices.orthographicProjectionRHP(
      this.x_min, this.x_max, this.y_min, this.y_max, this.z_near, this.z_far);
  }

  @Override
  public Matrix4x4D projectionMakeMatrixUntyped()
  {
    return JCGLProjectionMatrices.orthographicProjectionRH(
      this.x_min, this.x_max, this.y_min, this.y_max, this.z_near, this.z_far);
  }

  @Override
  public double projectionGetNearXMaximum()
  {
    return this.x_max;
  }

  @Override
  public double projectionGetNearXMinimum()
  {
    return this.x_min;
  }

  @Override
  public double projectionGetNearYMaximum()
  {
    return this.y_max;
  }

  @Override
  public double projectionGetNearYMinimum()
  {
    return this.y_min;
  }

  @Override
  public double projectionGetZFar()
  {
    return this.z_far;
  }

  @Override
  public double projectionGetZNear()
  {
    return this.z_near;
  }

  @Override
  public double projectionGetFarXMaximum()
  {
    return this.x_max;
  }

  @Override
  public double projectionGetFarXMinimum()
  {
    return this.x_min;
  }

  @Override
  public double projectionGetFarYMaximum()
  {
    return this.y_max;
  }

  @Override
  public double projectionGetFarYMinimum()
  {
    return this.y_min;
  }

  @Override
  public R2WatchableType<R2ProjectionReadableType> projectionGetWatchable()
  {
    return this.watchable;
  }
}
