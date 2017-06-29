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

package com.io7m.r2.tests.core;

import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.r2.matrices.R2MatricesObserverValuesType;
import com.io7m.r2.projections.R2ProjectionReadableType;
import com.io7m.r2.spaces.R2SpaceClipType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import com.io7m.r2.view_rays.R2ViewRays;
import com.io7m.r2.view_rays.R2ViewRaysReadableType;

public final class R2EmptyObserverValues implements
  R2MatricesObserverValuesType
{
  private final R2ProjectionReadableType proj;

  public R2EmptyObserverValues(
    final R2ProjectionReadableType p)
  {
    this.proj = NullCheck.notNull(p);
  }

  @Override
  public R2ProjectionReadableType projection()
  {
    return this.proj;
  }

  @Override
  public PMatrix4x4D<R2SpaceEyeType, R2SpaceClipType> matrixProjection()
  {
    return PMatrices4x4D.identity();
  }

  @Override
  public PMatrix4x4D<R2SpaceClipType, R2SpaceEyeType> matrixProjectionInverse()
  {
    return PMatrices4x4D.identity();
  }

  @Override
  public PMatrix4x4D<R2SpaceWorldType, R2SpaceEyeType> matrixView()
  {
    return PMatrices4x4D.identity();
  }

  @Override
  public PMatrix4x4D<R2SpaceEyeType, R2SpaceWorldType> matrixViewInverse()
  {
    return PMatrices4x4D.identity();
  }

  @Override
  public R2ViewRaysReadableType viewRays()
  {
    return R2ViewRays.newViewRays();
  }
}
