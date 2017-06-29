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

package com.io7m.r2.transforms;

import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.r2.core.api.watchable.R2Watchable;
import com.io7m.r2.core.api.watchable.R2WatchableType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * <p>A simple 4x4 matrix transform.</p>
 */

public final class R2TransformMatrix4x4
  implements R2TransformNonOrthogonalReadableType, R2TransformType
{
  private final R2WatchableType<R2TransformNonOrthogonalReadableType> watchable;
  private PMatrix4x4D<R2SpaceObjectType, R2SpaceWorldType> model;

  private R2TransformMatrix4x4()
  {
    this.model = PMatrices4x4D.identity();
    this.watchable = R2Watchable.newWatchable(this);
  }

  /**
   * @return A new identity transform
   */

  public static R2TransformMatrix4x4 create()
  {
    return new R2TransformMatrix4x4();
  }

  /**
   * Set the transform matrix.
   *
   * @param m The matrix
   */

  public void setMatrix4x4D(
    final PMatrix4x4D<R2SpaceObjectType, R2SpaceWorldType> m)
  {
    this.model = NullCheck.notNull(m, "Matrix");
    this.watchable.watchableChanged();
  }

  @Override
  public PMatrix4x4D<R2SpaceObjectType, R2SpaceWorldType> transformMakeMatrix4x4F()
  {
    return this.model;
  }

  @Override
  @SuppressWarnings("unchecked")
  public R2WatchableType<R2TransformReadableType> transformGetWatchable()
  {
    return (R2WatchableType<R2TransformReadableType>) (Object) this.watchable;
  }

  @Override
  public R2WatchableType<R2TransformNonOrthogonalReadableType>
  transformNonOrthogonalGetWatchable()
  {
    return this.watchable;
  }
}
