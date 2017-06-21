/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.meshes.api;

/**
 * The type of mesh listeners that deliver data to consumers.
 */

public interface R2MeshListenerType
{
  /**
   * Called unconditionally at the start of parsing.
   */

  void onEventStart();

  /**
   * The number of attributes that will be delivered.
   *
   * @param count The number of attributes
   */

  void onEventAttributesStart(
    long count);

  /**
   * An attribute has been received.
   *
   * @param attribute The attribute
   */

  void onEventAttribute(
    R2MeshAttribute attribute);

  /**
   * All attributes have been delivered.
   */

  void onEventAttributesFinished();

  /**
   * The number of vertices that will be delivered.
   *
   * @param count The number of vertices
   */

  void onEventVertexCount(
    long count);

  /**
   * The number of triangles that will be delivered.
   *
   * @param count The number of triangles
   */

  void onEventTriangleCount(
    long count);

  /**
   * Triangles are about to be delivered.
   */

  void onEventTrianglesStart();

  /**
   * All triangles have been delivered.
   */

  void onEventTrianglesFinished();

  /**
   * Called unconditionally at the end of parsing.
   */

  void onEventFinished();
}
