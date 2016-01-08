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
import com.io7m.jtensors.MatrixM4x4F;
import com.io7m.jtensors.VectorReadable2FType;
import com.io7m.jtensors.VectorReadable3FType;
import com.io7m.jtensors.VectorReadable4FType;
import com.io7m.jtensors.VectorWritable2FType;
import com.io7m.jtensors.VectorWritable3FType;
import com.io7m.jtensors.VectorWritable4FType;
import com.io7m.jtensors.parameterized.PMatrix4x4FType;
import com.io7m.jtensors.parameterized.PMatrixHeapArrayM4x4F;
import com.io7m.jtensors.parameterized.PMatrixWritable4x4FType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * <p>A simple 4x4 matrix transform.</p>
 */

public final class R2TransformMatrix4x4 implements
  R2TransformNonOrthogonalReadableType,
  PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType>
{
  private final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> model;
  private       boolean                                              changed;

  /**
   * Construct a new identity transform.
   */

  public R2TransformMatrix4x4()
  {
    this.model = PMatrixHeapArrayM4x4F.newMatrix();
    this.changed = true;
  }

  @Override
  public void setRow0With4F(final VectorReadable4FType v)
  {
    this.changed = true;
    this.model.setRow0With4F(v);
  }

  @Override
  public void setRow1With4F(final VectorReadable4FType v)
  {
    this.changed = true;
    this.model.setRow1With4F(v);
  }

  @Override
  public void setRow2With4F(final VectorReadable4FType v)
  {
    this.changed = true;
    this.model.setRow2With4F(v);
  }

  @Override
  public void setRow3With4F(final VectorReadable4FType v)
  {
    this.changed = true;
    this.model.setRow3With4F(v);
  }

  @Override
  public void setRowWith4F(
    final int row,
    final VectorReadable4FType v)
  {
    this.changed = true;
    this.model.setRowWith4F(row, v);
  }

  @Override
  public void setRowWith4FUnsafe(
    final int row,
    final VectorReadable4FType v)
  {
    this.changed = true;
    this.model.setRowWith4FUnsafe(row, v);
  }

  @Override
  public void setRowWith3F(
    final int row,
    final VectorReadable3FType v)
  {
    this.changed = true;
    this.model.setRowWith3F(row, v);
  }

  @Override
  public void setRowWith3FUnsafe(
    final int row,
    final VectorReadable3FType v)
  {
    this.changed = true;
    this.model.setRowWith3FUnsafe(row, v);
  }

  @Override
  public void setRowWith2F(
    final int row,
    final VectorReadable2FType v)
  {
    this.changed = true;
    this.model.setRowWith2F(row, v);
  }

  @Override
  public void setRowWith2FUnsafe(
    final int row,
    final VectorReadable2FType v)
  {
    this.changed = true;
    this.model.setRowWith2FUnsafe(row, v);
  }

  @Override
  public void setRowColumnF(
    final int row,
    final int column,
    final float value)
  {
    this.changed = true;
    this.model.setRowColumnF(row, column, value);
  }

  @Override
  public float getRowColumnF(
    final int row,
    final int column)
  {
    return this.model.getRowColumnF(row, column);
  }

  @Override
  public float getR0C3F()
  {
    return this.model.getR0C3F();
  }

  @Override
  public void setR0C3F(final float x)
  {
    this.changed = true;
    this.model.setR0C3F(x);
  }

  @Override
  public float getR1C3F()
  {
    return this.model.getR1C3F();
  }

  @Override
  public void setR1C3F(final float x)
  {
    this.changed = true;
    this.model.setR1C3F(x);
  }

  @Override
  public float getR2C3F()
  {
    return this.model.getR2C3F();
  }

  @Override
  public void setR2C3F(final float x)
  {
    this.changed = true;
    this.model.setR2C3F(x);
  }

  @Override
  public float getR3C0F()
  {
    return this.model.getR3C0F();
  }

  @Override
  public void setR3C0F(final float x)
  {
    this.changed = true;
    this.model.setR3C0F(x);
  }

  @Override
  public float getR3C1F()
  {
    return this.model.getR3C1F();
  }

  @Override
  public void setR3C1F(final float x)
  {
    this.changed = true;
    this.model.setR3C1F(x);
  }

  @Override
  public float getR3C2F()
  {
    return this.model.getR3C2F();
  }

  @Override
  public void setR3C2F(final float x)
  {
    this.changed = true;
    this.model.setR3C2F(x);
  }

  @Override
  public float getR3C3F()
  {
    return this.model.getR3C3F();
  }

  @Override
  public void setR3C3F(final float x)
  {
    this.changed = true;
    this.model.setR3C3F(x);
  }

  @Override
  public <V extends VectorWritable4FType> void getRow4F(
    final int row,
    final V out)
  {
    this.model.getRow4F(row, out);
  }

  @Override
  public <V extends VectorWritable4FType> void getRow4FUnsafe(
    final int row,
    final V out)
  {
    this.model.getRow4FUnsafe(row, out);
  }

  @Override
  public float getR0C2F()
  {
    return this.model.getR0C2F();
  }

  @Override
  public void setR0C2F(final float x)
  {
    this.changed = true;
    this.model.setR0C2F(x);
  }

  @Override
  public float getR1C2F()
  {
    return this.model.getR1C2F();
  }

  @Override
  public void setR1C2F(final float x)
  {
    this.changed = true;
    this.model.setR1C2F(x);
  }

  @Override
  public float getR2C0F()
  {
    return this.model.getR2C0F();
  }

  @Override
  public void setR2C0F(final float x)
  {
    this.changed = true;
    this.model.setR2C0F(x);
  }

  @Override
  public float getR2C1F()
  {
    return this.model.getR2C1F();
  }

  @Override
  public void setR2C1F(final float x)
  {
    this.changed = true;
    this.model.setR2C1F(x);
  }

  @Override
  public float getR2C2F()
  {
    return this.model.getR2C2F();
  }

  @Override
  public void setR2C2F(final float x)
  {
    this.changed = true;
    this.model.setR2C2F(x);
  }

  @Override
  public <V extends VectorWritable3FType> void getRow3F(
    final int row,
    final V out)
  {
    this.model.getRow3F(row, out);
  }

  @Override
  public <V extends VectorWritable3FType> void getRow3FUnsafe(
    final int row,
    final V out)
  {
    this.model.getRow3FUnsafe(row, out);
  }

  @Override
  public float getR0C0F()
  {
    return this.model.getR0C0F();
  }

  @Override
  public void setR0C0F(final float x)
  {
    this.changed = true;
    this.model.setR0C0F(x);
  }

  @Override
  public float getR0C1F()
  {
    return this.model.getR0C1F();
  }

  @Override
  public void setR0C1F(final float x)
  {
    this.changed = true;
    this.model.setR0C1F(x);
  }

  @Override
  public float getR1C0F()
  {
    return this.model.getR1C0F();
  }

  @Override
  public void setR1C0F(final float x)
  {
    this.changed = true;
    this.model.setR1C0F(x);
  }

  @Override
  public float getR1C1F()
  {
    return this.model.getR1C1F();
  }

  @Override
  public void setR1C1F(final float x)
  {
    this.changed = true;
    this.model.setR1C1F(x);
  }

  @Override
  public <V extends VectorWritable2FType> void getRow2F(
    final int row,
    final V out)
  {
    this.model.getRow2F(row, out);
  }

  @Override
  public <V extends VectorWritable2FType> void getRow2FUnsafe(
    final int row,
    final V out)
  {
    this.model.getRow2FUnsafe(row, out);
  }

  @Override
  public boolean transformHasChanged()
  {
    return this.changed;
  }

  @Override
  public void transformMakeMatrix4x4F(
    final R2TransformContextType c,
    final PMatrixWritable4x4FType<R2SpaceObjectType, R2SpaceWorldType> m)
  {
    NullCheck.notNull(c);
    NullCheck.notNull(m);
    MatrixM4x4F.copy(this.model, m);
    this.changed = false;
  }

  @Override
  public String toString()
  {
    return this.model.toString();
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    final R2TransformMatrix4x4 that = (R2TransformMatrix4x4) o;
    return this.model.equals(that.model);

  }

  @Override
  public int hashCode()
  {
    return this.model.hashCode();
  }
}
