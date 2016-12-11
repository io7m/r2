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

import com.io7m.jinterp.InterpolationF;
import com.io7m.jnull.NullCheck;
import com.io7m.jranges.RangeCheck;
import com.io7m.jranges.RangeInclusiveI;
import com.io7m.jtensors.VectorM3F;
import com.io7m.jtensors.VectorWritable3FType;
import com.io7m.jtensors.bytebuffered.VectorByteBuffered3FType;
import com.io7m.jtensors.bytebuffered.VectorByteBufferedM3F;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

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
  private final VectorByteBuffered3FType data_cursor;
  private int size;
  private long version;

  private R2SSAOKernel(
    final ByteBuffer in_data,
    final int in_size)
  {
    this.data =
      NullCheck.notNull(in_data).asFloatBuffer();
    this.data_cursor =
      VectorByteBufferedM3F.newVectorFromByteBuffer(in_data, 0L);
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

  public static R2SSAOKernelType newKernel(final int in_size)
  {
    RangeCheck.checkIncludedInInteger(
      in_size, "Size", VALID_SIZES, "Valid sizes");

    final ByteBuffer bb =
      ByteBuffer.allocateDirect(VALID_SIZES.getUpper() * (3 * 4))
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

    /**
     * Generate a hemisphere of random sample vectors.
     */

    long offset = 0L;
    for (int index = 0; index < new_size; ++index) {
      this.data_cursor.setByteOffset(offset);

      this.data_cursor.set3F(
        (float) ((Math.random() * 2.0) - 1.0),
        (float) ((Math.random() * 2.0) - 1.0),
        (float) Math.random());
      VectorM3F.normalizeInPlace(this.data_cursor);

      /**
       * Scale the vectors such that lower index samples are closer
       * to the origin.
       */

      final float scale = (float) index / (float) new_size;
      VectorM3F.scaleInPlace(
        this.data_cursor,
        (double) InterpolationF.interpolateExponential(0.1f, 1.0f, scale));

      offset += 3L * 4L;
    }

    this.size = new_size;
    ++this.version;
  }

  @Override
  public void sample(
    final int index,
    final VectorWritable3FType out)
  {
    RangeCheck.checkGreaterEqualInteger(index, "Index", 0, "Minimum index");
    RangeCheck.checkLessInteger(index, "Index", this.size, "Maximum index");
    this.data_cursor.setByteOffset((long) index * (3L * 4L));
    out.copyFrom3F(this.data_cursor);
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
