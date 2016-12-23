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

import com.io7m.jtensors.QuaternionReadable4FType;
import com.io7m.jtensors.parameterized.PVectorReadable3FType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * A readable transform that exposes an orientation, followed by a translation.
 */

public interface R2TransformOTReadableType extends
  R2TransformOrthogonalReadableType
{
  /**
   * @return A quaternion representing the current orientation
   */

  QuaternionReadable4FType orientationReadable();

  /**
   * @return A translation in world-space
   */

  PVectorReadable3FType<R2SpaceWorldType> translationReadable();
}
