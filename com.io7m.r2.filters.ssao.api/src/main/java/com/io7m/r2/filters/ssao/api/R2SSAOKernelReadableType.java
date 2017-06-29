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

package com.io7m.r2.filters.ssao.api;

import com.io7m.jranges.RangeInclusiveI;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;

import java.nio.FloatBuffer;

/**
 * The type of readable SSAO kernels.
 */

public interface R2SSAOKernelReadableType
{
  /**
   * The range of valid kernel sizes.
   */

  RangeInclusiveI VALID_SIZES = new RangeInclusiveI(1, 128);

  /**
   * @param index The sample index
   *
   * @return The vector at {@code index}
   */

  Vector3D sample(
    final int index);

  /**
   * @return The number of times this kernel has been regenerated
   */

  long version();

  /**
   * @return The number of samples in the kernel
   */

  int size();

  /**
   * @return The raw float buffer that backs the kernel
   */

  FloatBuffer floatBuffer();
}
