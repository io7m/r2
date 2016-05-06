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

import com.io7m.jtensors.MatrixWritable4x4FType;
import com.io7m.jtensors.parameterized.PMatrixWritable4x4FType;
import com.io7m.r2.spaces.R2SpaceClipType;
import com.io7m.r2.spaces.R2SpaceEyeType;

/**
 * The type of readable projections.
 */

public interface R2ProjectionReadableType
{
  /**
   * Write the projection to the given matrix.
   *
   * @param m The output matrix
   */

  void projectionMakeMatrix(
    PMatrixWritable4x4FType<R2SpaceEyeType, R2SpaceClipType> m);

  /**
   * Write the projection to the given matrix.
   *
   * @param m The output matrix
   */

  void projectionMakeMatrixUntyped(
    MatrixWritable4x4FType m);

  /**
   * @return The rightmost edge of the frustum's near plane.
   */

  float projectionGetNearXMaximum();

  /**
   * @return The leftmost edge of the frustum's near plane.
   */

  float projectionGetNearXMinimum();

  /**
   * @return The topmost edge of the frustum's near plane.
   */

  float projectionGetNearYMaximum();

  /**
   * @return The bottommost edge of the frustum's near plane.
   */

  float projectionGetNearYMinimum();

  /**
   * @return The value of the projection's far plane.
   */

  float projectionGetZFar();

  /**
   * @return The value of the projection's near plane.
   */

  float projectionGetZNear();

  /**
   * @return The rightmost edge of the frustum's far plane.
   */

  float projectionGetFarXMaximum();

  /**
   * @return The leftmost edge of the frustum's far plane.
   */

  float projectionGetFarXMinimum();

  /**
   * @return The topmost edge of the frustum's far plane.
   */

  float projectionGetFarYMaximum();

  /**
   * @return The bottommost edge of the frustum's far plane.
   */

  float projectionGetFarYMinimum();

  /**
   * @return The watchable value for this projection
   */

  R2WatchableType<R2ProjectionReadableType> projectionGetWatchable();
}
