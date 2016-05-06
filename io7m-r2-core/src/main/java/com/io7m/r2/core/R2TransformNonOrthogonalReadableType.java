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
 * <p>The type of readable and non-orthogonal transforms.</p>
 *
 * <p>A transform is considered to be <i>non-orthogonal</i> iff the 4x4 matrix
 * it produces is not guaranteed to be orthogonal - it may sometimes be
 * orthogonal but is not guaranteed to be.</p>
 */

public interface R2TransformNonOrthogonalReadableType extends
  R2TransformReadableType
{
  /**
   * @return The watchable value for this transform
   */

  R2WatchableType<R2TransformNonOrthogonalReadableType>
  transformNonOrthogonalGetWatchable();
}
