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
import com.io7m.jtensors.parameterized.PMatrixWritable4x4FType;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * <p>A transform represented by a scale, followed by a translation.</p>
 *
 * <p>The transform does not allow independent scaling on each axis and will
 * therefore produce matrices that are guaranteed to be orthogonal.</p>
 */

public final class R2TransformST implements
  R2TransformOrthogonalReadableType, R2TransformType
{
  private final PVector3FType<R2SpaceWorldType> translation;
  private final R2WatchableType<R2TransformOrthogonalReadableType> watchable;
  private float scale;

  private R2TransformST(
    final float in_scale,
    final PVector3FType<R2SpaceWorldType> in_translation)
  {
    NullCheck.notNull(in_translation);

    this.watchable =
      R2Watchable.newWatchable(this);

    this.scale = in_scale;
    this.translation =
      new R2TransformNotifyingTensors.R2PVectorM3F<>(
        this.watchable::watchableChanged, in_translation);
  }

  /**
   * Construct a transform using the given initial values. The given vectors are
   * not copied; any modifications made to them will be reflected in the
   * transform.
   *
   * @param in_scale       The scale
   * @param in_translation The translation
   *
   * @return A new transform
   */

  public static R2TransformST newTransformWithValues(
    final float in_scale,
    final PVector3FType<R2SpaceWorldType> in_translation)
  {
    return new R2TransformST(in_scale, in_translation);
  }

  /**
   * Construct a transform using the default values: The scale {@code 1.0}, and
   * the translation {@code (0, 0, 0)}.
   *
   * @return A new transform
   */

  public static R2TransformST newTransform()
  {
    return new R2TransformST(
      1.0f,
      new PVectorM3F<>(0.0f, 0.0f, 0.0f)
    );
  }

  /**
   * @return A value representing scale
   */

  public float getScale()
  {
    return this.scale;
  }

  /**
   * Set the uniform scale.
   *
   * @param x The scale value
   */

  public void setScale(final float x)
  {
    this.scale = x;
    this.watchable.watchableChanged();
  }

  /**
   * @return A translation in world-space
   */

  public PVector3FType<R2SpaceWorldType> getTranslation()
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

    {
      MatrixM4x4F.makeTranslation3F(this.translation, accum);
    }

    {
      final Matrix4x4FType temporary = context.temporaryMatrix4x4_1();
      MatrixM4x4F.setIdentity(temporary);
      temporary.setR0C0F(this.scale);
      temporary.setR1C1F(this.scale);
      temporary.setR2C2F(this.scale);
      MatrixM4x4F.multiply(accum, temporary, m);
    }
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
  void transformMakeViewMatrix4x4F(
    final R2TransformContextType context,
    final PMatrixWritable4x4FType<T, U> m)
  {
    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }
}
