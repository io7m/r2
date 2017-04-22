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

package com.io7m.r2.filters;

import com.io7m.jaffirm.core.Invariants;
import com.io7m.jnull.NullCheck;
import com.io7m.jranges.RangeCheck;
import com.io7m.jranges.RangeInclusiveI;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors3D;
import com.io7m.jtensors.storage.bytebuffered.VectorByteBufferedFloating3s32;
import com.io7m.mutable.numbers.core.MutableLong;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.io7m.jinterp.InterpolationD.interpolateExponential;
import static java.lang.Math.multiplyExact;
import static java.lang.Math.random;

/**
 * The default implementation of the {@link R2SSAOKernelType} interface.
 */

public final class R2SSAOKernel implements R2SSAOKernelType
{
  /**
   * The range of valid kernel sizes.
   */

  public static final RangeInclusiveI VALID_SIZES;

  static {
    VALID_SIZES = new RangeInclusiveI(1, 128);
  }

  private final FloatBuffer data;
  private final VectorByteBufferedFloating3s32 data_cursor;
  private final MutableLong pointer;
  private int size;
  private long version;

  private R2SSAOKernel(
    final ByteBuffer in_data,
    final int in_size)
  {
    this.data =
      NullCheck.notNull(in_data, "Data").asFloatBuffer();
    this.pointer = MutableLong.create();
    this.data_cursor =
      VectorByteBufferedFloating3s32.createWithBase(in_data, this.pointer, 0);
    this.size =
      RangeCheck.checkIncludedInInteger(
        in_size, "Size", VALID_SIZES, "Valid sizes");
    this.version = 0L;
  }

  /**
   * Generate a new kernel.
   *
   * @param in_size The kernel size
   *
   * @return A new kernel
   */

  public static R2SSAOKernel newKernel(final int in_size)
  {
    RangeCheck.checkIncludedInInteger(
      in_size, "Size", VALID_SIZES, "Valid sizes");

    final ByteBuffer bb =
      ByteBuffer.allocateDirect(VALID_SIZES.getUpper() * 3 * 4)
        .order(ByteOrder.nativeOrder());

    final R2SSAOKernel k = new R2SSAOKernel(bb, in_size);
    k.regenerate(in_size);
    return k;
  }

  @Override
  public void regenerate(final int new_size)
  {
    RangeCheck.checkIncludedInInteger(
      new_size, "Size", VALID_SIZES, "Valid sizes");

    /*
     * Generate a hemisphere of random sample vectors.
     */

    long offset = 0L;
    for (int index = 0; index < new_size; ++index) {
      this.pointer.setValue(offset);

      final double x = random() * 2.0 - 1.0;
      final double y = random() * 2.0 - 1.0;
      final double z = random();
      Vector3D v0 = Vectors3D.normalize(Vector3D.of(x, y, z));

      /*
       * Scale the vectors such that lower index samples are closer
       * to the origin.
       */

      final double scale = (double) index / (double) new_size;
      final double alpha = interpolateExponential(0.1, 1.0, scale);

      Invariants.checkInvariantD(
        alpha, alpha >= 0.099999, a -> "Alpha must be >= 0.099999");
      Invariants.checkInvariantD(
        alpha, alpha <= 1.0, a -> "Alpha must be <= 1.0");

      v0 = Vectors3D.scale(v0, alpha);

      final double mag = Vectors3D.magnitude(v0);
      Invariants.checkInvariantD(
        mag, mag >= 0.099999, a -> "Magnitude must be >= 0.099999");
      Invariants.checkInvariantD(
        mag, mag <= 1.0, a -> "Magnitude must be <= 1.0");

      this.data_cursor.setXYZ(v0.x(), v0.y(), v0.z());
      offset += 3L * 4L;
    }

    this.size = new_size;
    ++this.version;
  }

  @Override
  public Vector3D sample(
    final int index)
  {
    RangeCheck.checkGreaterEqualInteger(index, "Index", 0, "Minimum index");
    RangeCheck.checkLessInteger(index, "Index", this.size, "Maximum index");

    this.pointer.setValue(multiplyExact(multiplyExact(index, 4L), 3L));
    return Vector3D.of(
      this.data_cursor.x(),
      this.data_cursor.y(),
      this.data_cursor.z());
  }

  @Override
  public long version()
  {
    return this.version;
  }

  @Override
  public int size()
  {
    return this.size;
  }

  @Override
  public FloatBuffer floatBuffer()
  {
    return this.data;
  }
}
