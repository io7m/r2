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
import com.io7m.jtensors.QuaternionM4F;
import com.io7m.jtensors.VectorM3F;
import com.io7m.jtensors.parameterized.PMatrixWritable4x4FType;
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * <p>A transform represented by an orientation, a scale, and a
 * translation.</p>
 *
 * <p>The transform allows independent scaling on each axis and may therefore
 * produce matrices that are not orthogonal.</p>
 */

public final class R2TransformOSiT implements
  R2TransformNonOrthogonalReadableType
{
  private final QuaternionM4F                orientation;
  private final VectorM3F                    scale;
  private final PVectorM3F<R2SpaceWorldType> translation;

  private R2TransformOSiT(
    final QuaternionM4F in_orientation,
    final VectorM3F in_scale,
    final PVectorM3F<R2SpaceWorldType> in_translation)
  {
    this.orientation = NullCheck.notNull(in_orientation);
    this.scale = NullCheck.notNull(in_scale);
    this.translation = NullCheck.notNull(in_translation);
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

  public static R2TransformOSiT newTransformWith(
    final QuaternionM4F in_orientation,
    final VectorM3F in_scale,
    final PVectorM3F<R2SpaceWorldType> in_translation)
  {
    return new R2TransformOSiT(in_orientation, in_scale, in_translation);
  }

  /**
   * Construct a transform using the default values: The identity quaternion
   * {@code (0, 0, 0, 1)} for orientation, the scale vector {@code (1, 1, 1)},
   * and the translation {@code (0, 0, 0)}.
   *
   * @return A new transform
   */

  public static R2TransformOSiT newTransform()
  {
    return new R2TransformOSiT(
      new QuaternionM4F(),
      new VectorM3F(1.0f, 1.0f, 1.0f),
      new PVectorM3F<>(0.0f, 0.0f, 0.0f)
    );
  }

  /**
   * @return A quaternion representing the current orientation
   */

  public QuaternionM4F getOrientation()
  {
    return this.orientation;
  }

  /**
   * @return A vector representing scale in three dimensions
   */

  public VectorM3F getScale()
  {
    return this.scale;
  }

  /**
   * @return A translation in world-space
   */

  public PVectorM3F<R2SpaceWorldType> getTranslation()
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

    final Matrix4x4FType accum = context.getTemporaryMatrix4x4_0();
    MatrixM4x4F.setIdentity(accum);

    {
      MatrixM4x4F.makeTranslation3F(this.translation, accum);
    }

    {
      final Matrix4x4FType temporary = context.getTemporaryMatrix4x4_1();
      QuaternionM4F.makeRotationMatrix4x4(this.orientation, temporary);
      MatrixM4x4F.multiply(accum, temporary, accum);
    }

    {
      final Matrix4x4FType temporary = context.getTemporaryMatrix4x4_1();
      MatrixM4x4F.setIdentity(temporary);
      temporary.setR0C0F(this.scale.getXF());
      temporary.setR1C1F(this.scale.getYF());
      temporary.setR2C2F(this.scale.getZF());
      MatrixM4x4F.multiply(accum, temporary, m);
    }
  }
}
