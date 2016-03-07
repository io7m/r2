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
import com.io7m.jtensors.parameterized.PMatrixDirectM4x4F;
import com.io7m.jtensors.parameterized.PMatrixDirectReadable4x4FType;
import com.io7m.jtensors.parameterized.PMatrixM4x4F;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2ProjectionReadableType;
import com.io7m.r2.core.R2TransformContext;
import com.io7m.r2.core.R2TransformContextType;
import com.io7m.r2.core.R2ViewRays;
import com.io7m.r2.core.R2ViewRaysReadableType;
import com.io7m.r2.spaces.R2SpaceClipType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

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
  public R2ProjectionReadableType getProjection()
  {
    return this.proj;
  }

  @Override
  public PMatrixDirectReadable4x4FType<R2SpaceEyeType, R2SpaceClipType>
  getMatrixProjection()
  {
    return PMatrixDirectM4x4F.newMatrix();
  }

  @Override
  public PMatrixDirectReadable4x4FType<R2SpaceClipType, R2SpaceEyeType>
  getMatrixProjectionInverse()
  {
    return PMatrixDirectM4x4F.newMatrix();
  }

  @Override
  public PMatrixDirectReadable4x4FType<R2SpaceWorldType,
    R2SpaceEyeType> getMatrixView()
  {
    return PMatrixDirectM4x4F.newMatrix();
  }

  @Override
  public PMatrixDirectReadable4x4FType<R2SpaceEyeType,
    R2SpaceWorldType> getMatrixViewInverse()
  {
    return PMatrixDirectM4x4F.newMatrix();
  }

  @Override
  public R2ViewRaysReadableType getViewRays()
  {
    return R2ViewRays.newViewRays(new PMatrixM4x4F.ContextPM4F());
  }

  @Override
  public R2TransformContextType getTransformContext()
  {
    return R2TransformContext.newContext();
  }
}
