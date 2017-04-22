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
import com.io7m.jtensors.core.quaternions.Quaternion4D;
import com.io7m.jtensors.core.quaternions.Quaternions4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * <p>A transform represented by a scale, followed by an orientation, followed
 * by a translation.</p>
 *
 * <p>The transform allows independent scaling on each axis and may therefore
 * produce matrices that are not orthogonal.</p>
 */

public final class R2TransformSiOT implements R2TransformSiOTType
{
  private final R2WatchableType<R2TransformNonOrthogonalReadableType> watchable;
  private Quaternion4D orientation;
  private Vector3D scale;
  private PVector3D<R2SpaceWorldType> translation;

  private R2TransformSiOT(
    final Quaternion4D in_orientation,
    final Vector3D in_scale,
    final PVector3D<R2SpaceWorldType> in_translation)
  {
    this.orientation =
      NullCheck.notNull(in_orientation, "Orientation");
    this.scale =
      NullCheck.notNull(in_scale, "Scale");
    this.translation =
      NullCheck.notNull(in_translation, "Translation");
    this.watchable =
      R2Watchable.newWatchable(this);
  }

  /**
   * <p>Construct a transform using the given initial values. The given vectors
   * are not copied; any modifications made to them will be reflected in the
   * transform.</p>
   *
   * @param in_orientation The orientation
   * @param in_scale       The scale
   * @param in_translation The translation
   *
   * @return A new transform
   */

  public static R2TransformSiOT createWith(
    final Quaternion4D in_orientation,
    final Vector3D in_scale,
    final PVector3D<R2SpaceWorldType> in_translation)
  {
    return new R2TransformSiOT(in_orientation, in_scale, in_translation);
  }

  /**
   * <p>Construct a transform using the default values: The identity quaternion
   * {@code (0, 0, 0, 1)} for orientation, the scale vector {@code (1, 1, 1)},
   * and the translation {@code (0, 0, 0)}.</p>
   *
   * @return A new transform
   */

  public static R2TransformSiOT create()
  {
    return new R2TransformSiOT(
      Quaternions4D.identity(),
      Vector3D.of(1.0, 1.0, 1.0),
      PVector3D.of(0.0, 0.0, 0.0));
  }

  /**
   * @return A quaternion representing the current orientation
   */

  @Override
  public Quaternion4D orientation()
  {
    return this.orientation;
  }

  /**
   * @return A vector representing scale in three dimensions
   */

  public Vector3D scale()
  {
    return this.scale;
  }

  /**
   * @return A translation in world-space
   */

  @Override
  public PVector3D<R2SpaceWorldType> translation()
  {
    return this.translation;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PMatrix4x4D<R2SpaceObjectType, R2SpaceWorldType>
  transformMakeMatrix4x4F()
  {
    final PMatrix4x4D<Object, Object> m_trans =
      PMatrices4x4D.ofTranslation(
        this.translation.x(), this.translation.y(), this.translation.z());
    final PMatrix4x4D<Object, Object> m_orient =
      Quaternions4D.toPMatrix4x4(this.orientation);
    final PMatrix4x4D<Object, Object> m_scale =
      PMatrices4x4D.ofScale(this.scale.x(), this.scale.y(), this.scale.z());
    return (PMatrix4x4D<R2SpaceObjectType, R2SpaceWorldType>) (Object)
      PMatrices4x4D.multiply(
        PMatrices4x4D.multiply(m_trans, m_orient),
        m_scale);
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

  @Override
  public Vector3D scaleAxes()
  {
    return this.scale;
  }

  @Override
  public void setOrientation(
    final Quaternion4D q)
  {
    this.orientation = NullCheck.notNull(q, "Orientation");
    this.watchable.watchableChanged();
  }

  @Override
  public void setScaleAxes(
    final Vector3D s)
  {
    this.scale = NullCheck.notNull(s, "Scale");
    this.watchable.watchableChanged();
  }

  @Override
  public void setTranslation(
    final PVector3D<R2SpaceWorldType> t)
  {
    this.translation = NullCheck.notNull(t, "Translation");
    this.watchable.watchableChanged();
  }
}
