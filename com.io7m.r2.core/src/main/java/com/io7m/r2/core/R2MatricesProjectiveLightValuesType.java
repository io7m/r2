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

import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceLightClipType;
import com.io7m.r2.spaces.R2SpaceLightEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * Access to the matrices for a given projective light.
 */

public interface R2MatricesProjectiveLightValuesType extends
  R2MatricesVolumeLightValuesType
{
  /**
   * @return The current projective light matrix for transforming positions from
   * eye-space to light-eye-space
   */

  PMatrix4x4D<R2SpaceEyeType, R2SpaceLightEyeType>
  matrixProjectiveEyeToLightEye();

  /**
   * @return The current projection matrix for the projective light
   */

  PMatrix4x4D<R2SpaceLightEyeType, R2SpaceLightClipType>
  matrixProjectiveProjection();

  /**
   * @return The current view matrix for the projective light
   */

  PMatrix4x4D<R2SpaceWorldType, R2SpaceLightEyeType> matrixProjectiveView();

  /**
   * @return The projection for the current projective light.
   */

  R2ProjectionType projectiveProjection();
}
