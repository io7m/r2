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
import com.io7m.jtensors.Quaternion4FType;
import com.io7m.jtensors.Vector3FType;
import com.io7m.jtensors.VectorReadable2FType;
import com.io7m.jtensors.VectorReadable3FType;
import com.io7m.jtensors.VectorReadable4FType;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.jtensors.parameterized.PVectorReadable2FType;
import com.io7m.jtensors.parameterized.PVectorReadable3FType;
import com.io7m.junreachable.UnreachableCodeException;

import java.util.concurrent.atomic.AtomicBoolean;

final class R2TransformDelegatingTensors
{
  private R2TransformDelegatingTensors()
  {
    throw new UnreachableCodeException();
  }

  static final class R2PVectorM3F<T> implements PVector3FType<T>
  {
    private final PVector3FType<T> v;
    private final AtomicBoolean    changed;

    R2PVectorM3F(
      final AtomicBoolean in_changed,
      final PVector3FType<T> in_v)
    {
      this.changed = NullCheck.notNull(in_changed);
      this.v = NullCheck.notNull(in_v);
    }

    @Override
    public float getZF()
    {
      return this.v.getZF();
    }

    @Override
    public void setZF(final float z)
    {
      this.v.setZF(z);
      this.changed.set(true);
    }

    @Override
    public float getXF()
    {
      return this.v.getXF();
    }

    @Override
    public void setXF(final float x)
    {
      this.v.setXF(x);
      this.changed.set(true);
    }

    @Override
    public float getYF()
    {
      return this.v.getYF();
    }

    @Override
    public void setYF(final float y)
    {
      this.v.setYF(y);
      this.changed.set(true);
    }

    @Override
    public void copyFrom3F(final VectorReadable3FType in_v)
    {
      this.v.copyFrom3F(in_v);
      this.changed.set(true);
    }

    @Override
    public void set3F(
      final float x,
      final float y,
      final float z)
    {
      this.v.set3F(x, y, z);
      this.changed.set(true);
    }

    @Override
    public void copyFrom2F(final VectorReadable2FType in_v)
    {
      this.v.copyFrom2F(in_v);
      this.changed.set(true);
    }

    @Override
    public boolean equals(final Object obj)
    {
      return this.v.equals(obj);
    }

    @Override
    public int hashCode()
    {
      return this.v.hashCode();
    }

    @Override
    public void set2F(
      final float x,
      final float y)
    {
      this.v.set2F(x, y);
      this.changed.set(true);
    }

    @Override
    public void copyFromTyped3F(final PVectorReadable3FType<T> in_v)
    {
      this.v.copyFromTyped3F(in_v);
      this.changed.set(true);
    }

    @Override
    public void copyFromTyped2F(final PVectorReadable2FType<T> in_v)
    {
      this.v.copyFromTyped2F(in_v);
      this.changed.set(true);
    }

    @Override
    public String toString()
    {
      return this.v.toString();
    }
  }

  static final class R2VectorM3F implements Vector3FType
  {
    private final Vector3FType  v;
    private final AtomicBoolean changed;

    R2VectorM3F(
      final AtomicBoolean in_changed,
      final Vector3FType in_v)
    {
      this.changed = NullCheck.notNull(in_changed);
      this.v = NullCheck.notNull(in_v);
    }

    @Override
    public float getZF()
    {
      return this.v.getZF();
    }

    @Override
    public boolean equals(final Object obj)
    {
      return this.v.equals(obj);
    }

    @Override
    public int hashCode()
    {
      return this.v.hashCode();
    }

    @Override
    public void setZF(final float z)
    {
      this.v.setZF(z);
      this.changed.set(true);
    }

    @Override
    public float getXF()
    {
      return this.v.getXF();
    }

    @Override
    public void setXF(final float x)
    {
      this.v.setXF(x);
      this.changed.set(true);
    }

    @Override
    public float getYF()
    {
      return this.v.getYF();
    }

    @Override
    public void setYF(final float y)
    {
      this.v.setYF(y);
      this.changed.set(true);
    }

    @Override
    public void copyFrom3F(final VectorReadable3FType in_v)
    {
      this.v.copyFrom3F(in_v);
      this.changed.set(true);
    }

    @Override
    public void set3F(
      final float x,
      final float y,
      final float z)
    {
      this.v.set3F(x, y, z);
      this.changed.set(true);
    }

    @Override
    public void copyFrom2F(final VectorReadable2FType in_v)
    {
      this.v.copyFrom2F(in_v);
      this.changed.set(true);
    }

    @Override
    public void set2F(
      final float x,
      final float y)
    {
      this.v.set2F(x, y);
      this.changed.set(true);
    }

    @Override
    public String toString()
    {
      return this.v.toString();
    }
  }

  static final class R2QuaternionM4F implements Quaternion4FType
  {
    private final Quaternion4FType q;
    private final AtomicBoolean    changed;

    R2QuaternionM4F(
      final AtomicBoolean in_changed,
      final Quaternion4FType in_q)
    {
      this.changed = NullCheck.notNull(in_changed);
      this.q = NullCheck.notNull(in_q);
    }

    @Override
    public boolean equals(final Object obj)
    {
      return this.q.equals(obj);
    }

    @Override
    public String toString()
    {
      return this.q.toString();
    }

    @Override
    public int hashCode()
    {
      return this.q.hashCode();
    }

    @Override
    public float getWF()
    {
      return this.q.getWF();
    }

    @Override
    public void setWF(final float w)
    {
      this.q.setWF(w);
      this.changed.set(true);
    }

    @Override
    public float getZF()
    {
      return this.q.getZF();
    }

    @Override
    public void setZF(final float z)
    {
      this.q.setZF(z);
      this.changed.set(true);
    }

    @Override
    public float getXF()
    {
      return this.q.getXF();
    }

    @Override
    public void setXF(final float x)
    {
      this.q.setXF(x);
      this.changed.set(true);
    }

    @Override
    public float getYF()
    {
      return this.q.getYF();
    }

    @Override
    public void setYF(final float y)
    {
      this.q.setYF(y);
      this.changed.set(true);
    }

    @Override
    public void copyFrom4F(
      final VectorReadable4FType in_v)
    {
      this.q.copyFrom4F(in_v);
      this.changed.set(true);
    }

    @Override
    public void set4F(
      final float x,
      final float y,
      final float z,
      final float w)
    {
      this.q.set4F(x, y, z, w);
      this.changed.set(true);
    }

    @Override
    public void copyFrom3F(final VectorReadable3FType in_v)
    {
      this.q.copyFrom3F(in_v);
      this.changed.set(true);
    }

    @Override
    public void set3F(
      final float x,
      final float y,
      final float z)
    {
      this.q.set3F(x, y, z);
      this.changed.set(true);
    }

    @Override
    public void copyFrom2F(final VectorReadable2FType in_v)
    {
      this.q.copyFrom2F(in_v);
      this.changed.set(true);
    }

    @Override
    public void set2F(
      final float x,
      final float y)
    {
      this.q.set2F(x, y);
      this.changed.set(true);
    }
  }
}
