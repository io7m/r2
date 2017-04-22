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

import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * A transform that always yields an identity matrix.
 */

public final class R2TransformIdentity implements
  R2TransformOrthogonalReadableType, R2TransformType
{
  private static final R2TransformIdentity INSTANCE = new R2TransformIdentity();

  private final R2WatchableType<R2TransformReadableType> watchable;

  private R2TransformIdentity()
  {
    this.watchable = R2Watchable.newWatchable(this);
  }

  /**
   * @return The identity transform
   */

  public static R2TransformIdentity get()
  {
    return INSTANCE;
  }

  @Override
  public PMatrix4x4D<R2SpaceObjectType, R2SpaceWorldType>
  transformMakeMatrix4x4F()
  {
    return PMatrices4x4D.identity();
  }

  @Override
  public <T extends R2SpaceType, U extends R2SpaceType> PMatrix4x4D<T, U>
  transformMakeViewMatrix4x4F()
  {
    return PMatrices4x4D.identity();
  }

  @Override
  public R2WatchableType<R2TransformReadableType> transformGetWatchable()
  {
    return this.watchable;
  }

  @Override
  @SuppressWarnings("unchecked")
  public R2WatchableType<R2TransformOrthogonalReadableType>
  transformOrthogonalGetWatchable()
  {
    return (R2WatchableType<R2TransformOrthogonalReadableType>) (Object) this.watchable;
  }
}
