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

import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.unparameterized.matrices.Matrix4x4D;
import com.io7m.r2.core.api.watchable.R2WatchableType;
import com.io7m.r2.spaces.R2SpaceClipType;
import com.io7m.r2.spaces.R2SpaceEyeType;

/**
 * The type of readable projections.
 */

public interface R2ProjectionReadableType
{
  /**
   * @return The 4x4 matrix for the projection
   */

  PMatrix4x4D<R2SpaceEyeType, R2SpaceClipType> projectionMakeMatrix();

  /**
   * @return The 4x4 matrix for the projection
   */

  Matrix4x4D projectionMakeMatrixUntyped();

  /**
   * @return The rightmost edge of the frustum's near plane.
   */

  double projectionGetNearXMaximum();

  /**
   * @return The leftmost edge of the frustum's near plane.
   */

  double projectionGetNearXMinimum();

  /**
   * @return The topmost edge of the frustum's near plane.
   */

  double projectionGetNearYMaximum();

  /**
   * @return The bottommost edge of the frustum's near plane.
   */

  double projectionGetNearYMinimum();

  /**
   * @return The value of the projection's far plane.
   */

  double projectionGetZFar();

  /**
   * @return The value of the projection's near plane.
   */

  double projectionGetZNear();

  /**
   * @return The rightmost edge of the frustum's far plane.
   */

  double projectionGetFarXMaximum();

  /**
   * @return The leftmost edge of the frustum's far plane.
   */

  double projectionGetFarXMinimum();

  /**
   * @return The topmost edge of the frustum's far plane.
   */

  double projectionGetFarYMaximum();

  /**
   * @return The bottommost edge of the frustum's far plane.
   */

  double projectionGetFarYMinimum();

  /**
   * @return The watchable value for this projection
   */

  R2WatchableType<R2ProjectionReadableType> projectionGetWatchable();
}
