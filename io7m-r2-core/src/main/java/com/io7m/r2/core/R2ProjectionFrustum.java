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
import com.io7m.jtensors.MatrixWritable4x4FType;
import com.io7m.jtensors.parameterized.PMatrixWritable4x4FType;
import com.io7m.r2.spaces.R2SpaceClipType;
import com.io7m.r2.spaces.R2SpaceEyeType;

/**
 * A perspective projection based on an explicit frustum.
 */

public final class R2ProjectionFrustum implements R2ProjectionType
{
  private final JCGLProjectionMatricesType                context;
  private final R2WatchableType<R2ProjectionReadableType> watchable;
  private       float                                     far_x_min;
  private       float                                     far_x_max;
  private       float                                     far_y_min;
  private       float                                     far_y_max;
  private       float                                     far_z;
  private       float                                     near_x_max;
  private       float                                     near_x_min;
  private       float                                     near_y_max;
  private       float                                     near_y_min;
  private       float                                     near_z;

  private R2ProjectionFrustum(
    final JCGLProjectionMatricesType in_context,
    final float in_x_min,
    final float in_x_max,
    final float in_y_min,
    final float in_y_max,
    final float in_z_near,
    final float in_z_far)
  {
    this.context = NullCheck.notNull(in_context);

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
   * @param c A matrices interface
   *
   * @return A new projection
   */

  public static R2ProjectionFrustum newFrustum(
    final JCGLProjectionMatricesType c)
  {
    return new R2ProjectionFrustum(
      c,
      -1.0f,
      1.0f,
      -1.0f,
      1.0f,
      0.01f,
      1000.0f);
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
   * @param c         A matrices interface
   *
   * @return A new projection.
   */

  public static R2ProjectionFrustum newFrustumWith(
    final JCGLProjectionMatricesType c,
    final float in_x_min,
    final float in_x_max,
    final float in_y_min,
    final float in_y_max,
    final float in_z_near,
    final float in_z_far)
  {
    return new R2ProjectionFrustum(
      c,
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

  public void projectionSetXMaximum(final float x)
  {
    this.near_x_max = x;
    this.update();
  }

  /**
   * Set the minimum X value on the near plane.
   *
   * @param x The X value
   */

  public void projectionSetXMinimum(final float x)
  {
    this.near_x_min = x;
    this.update();
  }

  /**
   * Set the maximum Y value on the near plane.
   *
   * @param y The Y value
   */

  public void projectionSetYMaximum(final float y)
  {
    this.near_y_max = y;
    this.update();
  }

  /**
   * Set the minimum Y value on the near plane.
   *
   * @param y The Y value
   */

  public void projectionSetYMinimum(final float y)
  {
    this.near_y_min = y;
    this.update();
  }

  /**
   * Set the distance to the far plane.
   *
   * @param z The Z distance
   */

  public void projectionSetZFar(final float z)
  {
    this.far_z = z;
    this.update();
  }

  /**
   * Set the distance to the near plane.
   *
   * @param z The Z distance
   */

  public void projectionSetZNear(final float z)
  {
    this.near_z = z;
    this.update();
  }

  @Override
  public void projectionMakeMatrix(
    final PMatrixWritable4x4FType<R2SpaceEyeType, R2SpaceClipType> m)
  {
    this.projectionMakeMatrixUntyped(m);
  }

  @Override
  public void projectionMakeMatrixUntyped(final MatrixWritable4x4FType m)
  {
    this.context.makeFrustumProjection(
      m,
      (double) this.near_x_min,
      (double) this.near_x_max,
      (double) this.near_y_min,
      (double) this.near_y_max,
      (double) this.near_z,
      (double) this.far_z);
  }

  @Override
  public float projectionGetNearXMaximum()
  {
    return this.near_x_max;
  }

  @Override
  public float projectionGetNearXMinimum()
  {
    return this.near_x_min;
  }

  @Override
  public float projectionGetNearYMaximum()
  {
    return this.near_y_max;
  }

  @Override
  public float projectionGetNearYMinimum()
  {
    return this.near_y_min;
  }

  @Override
  public float projectionGetZFar()
  {
    return this.far_z;
  }

  @Override
  public float projectionGetZNear()
  {
    return this.near_z;
  }

  @Override
  public float projectionGetFarXMaximum()
  {
    return this.far_x_max;
  }

  @Override
  public float projectionGetFarXMinimum()
  {
    return this.far_x_min;
  }

  @Override
  public float projectionGetFarYMaximum()
  {
    return this.far_y_max;
  }

  @Override
  public float projectionGetFarYMinimum()
  {
    return this.far_y_min;
  }

  @Override
  public R2WatchableType<R2ProjectionReadableType> projectionGetWatchable()
  {
    return this.watchable;
  }
}
