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
import com.io7m.jtensors.Matrix4x4FType;
import com.io7m.jtensors.MatrixM4x4F;
import com.io7m.jtensors.Quaternion4FType;
import com.io7m.jtensors.QuaternionM4F;
import com.io7m.jtensors.QuaternionReadable4FType;
import com.io7m.jtensors.Vector3FType;
import com.io7m.jtensors.parameterized.PMatrixWritable4x4FType;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.jtensors.parameterized.PVectorReadable3FType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * <p>A transform represented by an orientation, followed by a translation.</p>
 *
 * <p>The transform does not allow independent scaling on each axis and will
 * therefore produce matrices that are guaranteed to be orthogonal.</p>
 */

public final class R2TransformOT implements R2TransformOTType
{
  private final Quaternion4FType orientation;
  private final PVector3FType<R2SpaceWorldType> translation;
  private final R2WatchableType<R2TransformOTType> watchable;

  private R2TransformOT(
    final Quaternion4FType in_orientation,
    final PVector3FType<R2SpaceWorldType> in_translation)
  {
    NullCheck.notNull(in_orientation);
    NullCheck.notNull(in_translation);

    this.watchable =
      R2Watchable.newWatchable(this);
    this.orientation =
      new R2TransformNotifyingTensors.R2QuaternionM4F(
        this.watchable::watchableChanged, in_orientation);
    this.translation =
      new R2TransformNotifyingTensors.R2PVectorM3F<>(
        this.watchable::watchableChanged, in_translation);
  }

  /**
   * Construct a transform using the given initial values. The given vectors are
   * not copied; any modifications made to them will be reflected in the
   * transform.
   *
   * @param in_orientation The orientation
   * @param in_translation The translation
   *
   * @return A new transform
   */

  public static R2TransformOT newTransformWithValues(
    final Quaternion4FType in_orientation,
    final PVector3FType<R2SpaceWorldType> in_translation)
  {
    return new R2TransformOT(in_orientation, in_translation);
  }

  /**
   * Construct a transform using the default values: The identity quaternion
   * {@code (0, 0, 0, 1)} for orientation and the translation {@code (0, 0,
   * 0)}.
   *
   * @return A new transform
   */

  public static R2TransformOT newTransform()
  {
    return new R2TransformOT(
      new QuaternionM4F(),
      new PVectorM3F<>(0.0f, 0.0f, 0.0f)
    );
  }

  /**
   * <p>Construct a transform using the default values: The identity quaternion
   * {@code (0, 0, 0, 1)} for orientation and the translation {@code (0, 0,
   * 0)}.</p>
   *
   * @param in_changed The procedure that will be executed every time the value
   *                   of this transform is changed
   *
   * @return A new transform
   */

  public static R2TransformOT newTransformWithNotifier(
    final Runnable in_changed)
  {
    return new R2TransformOT(
      new QuaternionM4F(),
      new PVectorM3F<>(0.0f, 0.0f, 0.0f)
    );
  }

  @Override
  public Quaternion4FType orientation()
  {
    return this.orientation;
  }

  @Override
  public PVector3FType<R2SpaceWorldType> translation()
  {
    return this.translation;
  }

  @Override
  public void transformMakeMatrix4x4F(
    final R2TransformContextType context,
    final PMatrixWritable4x4FType<R2SpaceObjectType, R2SpaceWorldType> m)
  {
    NullCheck.notNull(context);
    NullCheck.notNull(m);

    final Matrix4x4FType accum = context.temporaryMatrix4x4_0();
    MatrixM4x4F.setIdentity(accum);

    {
      MatrixM4x4F.makeTranslation3F(this.translation, accum);
    }

    {
      final Matrix4x4FType temporary = context.temporaryMatrix4x4_1();
      QuaternionM4F.makeRotationMatrix4x4(this.orientation, temporary);
      MatrixM4x4F.multiply(accum, temporary, m);
    }
  }

  @Override
  public <T extends R2SpaceType, U extends R2SpaceType> void
  transformMakeViewMatrix4x4F(
    final R2TransformContextType context,
    final PMatrixWritable4x4FType<T, U> m)
  {
    NullCheck.notNull(context);
    NullCheck.notNull(m);

    final Matrix4x4FType m_tmp0 = context.temporaryMatrix4x4_0();
    final Matrix4x4FType m_tmp1 = context.temporaryMatrix4x4_1();
    final Vector3FType v_tmp = context.temporaryVector3();

    final Quaternion4FType o_inv = context.temporaryQuaternion();
    QuaternionM4F.conjugate(this.orientation, o_inv);
    QuaternionM4F.makeRotationMatrix4x4(o_inv, m_tmp0);

    v_tmp.set3F(
      -this.translation.getXF(),
      -this.translation.getYF(),
      -this.translation.getZF());
    MatrixM4x4F.makeTranslation3F(v_tmp, m_tmp1);
    MatrixM4x4F.multiply(m_tmp0, m_tmp1, m);
  }

  @Override
  public QuaternionReadable4FType orientationReadable()
  {
    return this.orientation;
  }

  @Override
  public PVectorReadable3FType<R2SpaceWorldType> translationReadable()
  {
    return this.translation;
  }

  @Override
  @SuppressWarnings("unchecked")
  public R2WatchableType<R2TransformReadableType> transformGetWatchable()
  {
    final Object o = this.watchable;
    return (R2WatchableType<R2TransformReadableType>) o;
  }

  @Override
  @SuppressWarnings("unchecked")
  public R2WatchableType<R2TransformOrthogonalReadableType>
  transformOrthogonalGetWatchable()
  {
    final Object o = this.watchable;
    return (R2WatchableType<R2TransformOrthogonalReadableType>) o;
  }
}
