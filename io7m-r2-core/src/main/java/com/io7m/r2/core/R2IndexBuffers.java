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

import com.io7m.jcanephora.core.JCGLUnsignedType;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Functions for dealing with index buffers.
 */

public final class R2IndexBuffers
{
  private R2IndexBuffers()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Suggest an unsigned type used to hold {@code count} indices, using a type
   * no smaller than {@code minimum}.
   *
   * @param minimum The minimum type
   * @param count   The number of indices
   *
   * @return A type suggestion
   */

  public static JCGLUnsignedType getTypeForCount(
    final JCGLUnsignedType minimum,
    final long count)
  {
    if (count < 256L) {
      return R2IndexBuffers.maximum(
        minimum, JCGLUnsignedType.TYPE_UNSIGNED_BYTE);
    }
    if (count < 65536L) {
      return R2IndexBuffers.maximum(
        minimum, JCGLUnsignedType.TYPE_UNSIGNED_SHORT);
    }

    return JCGLUnsignedType.TYPE_UNSIGNED_INT;
  }

  private static JCGLUnsignedType maximum(
    final JCGLUnsignedType a,
    final JCGLUnsignedType b)
  {
    if (a.getSizeBytes() >= b.getSizeBytes()) {
      return a;
    }
    return b;
  }
}
