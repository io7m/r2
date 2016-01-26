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

package com.io7m.r2.core.cursors;

/**
 * The type of cursors for writing to standard vertices.
 */

public interface R2VertexCursorType
{
  /**
   * Set the position vector for vertex {@code index}.
   *
   * @param index The vertex index
   * @param x     The X coordinate value
   * @param y     The Y coordinate value
   * @param z     The Z coordinate value
   */

  void setPosition(
    long index,
    double x,
    double y,
    double z);

  /**
   * Set the normal vector for vertex {@code index}.
   *
   * @param index The vertex index
   * @param x     The X coordinate value
   * @param y     The Y coordinate value
   * @param z     The Z coordinate value
   */

  void setNormal(
    long index,
    double x,
    double y,
    double z);

  /**
   * Set the UV vector for vertex {@code index}.
   *
   * @param index The vertex index
   * @param x     The X coordinate value
   * @param y     The Y coordinate value
   */

  void setUV(
    long index,
    double x,
    double y);

  /**
   * Set the tangent4 vector for vertex {@code index}.
   *
   * @param index The vertex index
   * @param x     The X coordinate value
   * @param y     The Y coordinate value
   * @param z     The Z coordinate value
   * @param w     The tangent sign
   */

  void setTangent4(
    long index,
    double x,
    double y,
    double z,
    double w);
}
