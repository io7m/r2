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

import com.io7m.jtensors.VectorReadable4FType;

/**
 * The readable interface to view rays.
 */

public interface R2ViewRaysReadableType
{
  /**
   * @return The x0y0 origin
   */

  VectorReadable4FType originX0Y0();

  /**
   * @return The x0y1 origin
   */

  VectorReadable4FType originX0Y1();

  /**
   * @return The x1y0 origin
   */

  VectorReadable4FType originX1Y0();

  /**
   * @return The x1y1 origin
   */

  VectorReadable4FType originX1Y1();

  /**
   * @return The x0y0 view ray
   */

  VectorReadable4FType rayX0Y0();

  /**
   * @return The x0y1 view ray
   */

  VectorReadable4FType rayX0Y1();

  /**
   * @return The x1y0 view ray
   */

  VectorReadable4FType rayX1Y0();

  /**
   * @return The x1y1 view ray
   */

  VectorReadable4FType rayX1Y1();
}
