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
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVectors3D;
import com.io7m.jtensors.core.quaternions.Quaternion4D;
import com.io7m.jtensors.core.quaternions.Quaternions4D;
import com.io7m.r2.core.api.watchable.R2Watchable;
import com.io7m.r2.core.api.watchable.R2WatchableType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * <p>A transform represented by an scale, followed by an orientation, followed
 * by a translation.</p>
 *
 * <p>The transform does not allow independent scaling on each axis and will
 * therefore produce matrices that are guaranteed to be orthogonal.</p>
 */

public final class R2TransformSOT implements R2TransformSOTType
{
  private final R2WatchableType<R2TransformOrthogonalReadableType> watchable;
  private Quaternion4D orientation;
  private PVector3D<R2SpaceWorldType> translation;
  private double scale;

  private R2TransformSOT(
    final Quaternion4D in_orientation,
    final double in_scale,
    final PVector3D<R2SpaceWorldType> in_translation)
  {
    this.orientation =
      NullCheck.notNull(in_orientation, "Orientation");
    this.translation =
      NullCheck.notNull(in_translation, "Scale");
    this.scale = in_scale;
    this.watchable = R2Watchable.newWatchable(this);
  }

  /**
   * Construct a transform using the given initial values. The given vectors are
   * not copied; any modifications made to them will be reflected in the
   * transform.
   *
   * @param in_orientation The orientation
   * @param in_scale       The scale
   * @param in_translation The translation
   *
   * @return A new transform
   */

  public static R2TransformSOT createWith(
    final Quaternion4D in_orientation,
    final double in_scale,
    final PVector3D<R2SpaceWorldType> in_translation)
  {
    return new R2TransformSOT(in_orientation, in_scale, in_translation);
  }

  /**
   * Construct a transform using the default values: The identity quaternion
   * {@code (0, 0, 0, 1)} for orientation, the scale vector {@code (1, 1, 1)},
   * and the translation {@code (0, 0, 0)}.
   *
   * @return A new transform
   */

  public static R2TransformSOT create()
  {
    return new R2TransformSOT(
      Quaternions4D.identity(),
      1.0,
      PVectors3D.zero()
    );
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
   * @return A value representing scale
   */

  @Override
  public double scale()
  {
    return this.scale;
  }

  /**
   * Set the uniform scale.
   *
   * @param x The scale value
   */

  @Override
  public void setScale(final double x)
  {
    this.scale = x;
    this.watchable.watchableChanged();
  }

  @Override
  public void setOrientation(final Quaternion4D q)
  {
    this.orientation = NullCheck.notNull(q, "Orientation");
    this.watchable.watchableChanged();
  }

  @Override
  public void setTranslation(final PVector3D<R2SpaceWorldType> t)
  {
    this.translation = NullCheck.notNull(t, "Translation");
    this.watchable.watchableChanged();
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
      PMatrices4x4D.ofScale(this.scale, this.scale, this.scale);
    return (PMatrix4x4D<R2SpaceObjectType, R2SpaceWorldType>) (Object)
      PMatrices4x4D.multiply(
        PMatrices4x4D.multiply(m_trans, m_orient),
        m_scale);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends R2SpaceType, U extends R2SpaceType> PMatrix4x4D<T, U>
  transformMakeViewMatrix4x4F()
  {
    final PMatrix4x4D<Object, Object> m_trans =
      PMatrices4x4D.ofTranslation(
        -this.translation.x(), -this.translation.y(), -this.translation.z());
    final PMatrix4x4D<Object, Object> m_orient =
      Quaternions4D.toPMatrix4x4(Quaternions4D.conjugate(this.orientation));
    return (PMatrix4x4D<T, U>) (Object) PMatrices4x4D.multiply(
      m_orient,
      m_trans);
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
}
