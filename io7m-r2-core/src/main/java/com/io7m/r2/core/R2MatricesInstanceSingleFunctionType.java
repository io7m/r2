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

/**
 * The type of functions that take a value of type {@link
 * R2MatricesInstanceSingleType} and yield a value of type {@code B}.
 *
 * @param <A> The type of consumed values
 * @param <B> The type of returned values
 */

public interface R2MatricesInstanceSingleFunctionType<A, B>
{
  /**
   * Apply the function.
   *
   * @param o The instance matrices
   * @param x A consumed value
   *
   * @return A value of {@code B}
   */

  B apply(
    R2MatricesInstanceSingleType o,
    A x);
}
