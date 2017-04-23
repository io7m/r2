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

import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVectors3D;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * <p>A transform represented by a translation.</p>
 *
 * <p>The transform does not allow independent scaling on each axis and will
 * therefore produce matrices that are guaranteed to be orthogonal.</p>
 */

public final class R2TransformT implements R2TransformTType
{
  private final R2WatchableType<R2TransformOrthogonalReadableType> watchable;
  private PVector3D<R2SpaceWorldType> translation;

  private R2TransformT(
    final PVector3D<R2SpaceWorldType> in_translation)
  {
    this.translation =
      NullCheck.notNull(in_translation, "Translation");
    this.watchable =
      R2Watchable.newWatchable(this);
  }

  /**
   * Construct a transform using the given initial values. The given vectors are
   * not copied; any modifications made to them will be reflected in the
   * transform.
   *
   * @param in_translation The translation
   *
   * @return A new transform
   */

  public static R2TransformT createWith(
    final PVector3D<R2SpaceWorldType> in_translation)
  {
    return new R2TransformT(in_translation);
  }

  /**
   * Construct a transform using the default values: The translation {@code (0,
   * 0, 0)}.
   *
   * @return A new transform
   */

  public static R2TransformT create()
  {
    return new R2TransformT(PVectors3D.zero());
  }

  /**
   * @return A translation in world-space
   */

  @Override
  public PVector3D<R2SpaceWorldType> translation()
  {
    return this.translation;
  }

  @Override
  public PMatrix4x4D<R2SpaceObjectType, R2SpaceWorldType> transformMakeMatrix4x4F()
  {
    return PMatrices4x4D.ofTranslation(
      this.translation.x(),
      this.translation.y(),
      this.translation.z());
  }

  @Override
  @SuppressWarnings("unchecked")
  public R2WatchableType<R2TransformReadableType> transformGetWatchable()
  {
    return (R2WatchableType<R2TransformReadableType>) (Object) this.watchable;
  }

  @Override
  public R2WatchableType<R2TransformOrthogonalReadableType>
  transformOrthogonalGetWatchable()
  {
    return this.watchable;
  }

  @Override
  public <T extends R2SpaceType, U extends R2SpaceType>
  PMatrix4x4D<T, U> transformMakeViewMatrix4x4F()
  {
    final PVector3D<R2SpaceWorldType> inv = PVectors3D.negate(this.translation);
    return PMatrices4x4D.ofTranslation(inv.x(), inv.y(), inv.z());
  }

  @Override
  public void setTranslation(
    final PVector3D<R2SpaceWorldType> t)
  {
    this.translation = NullCheck.notNull(t, "Translation");
    this.watchable.watchableChanged();
  }
}