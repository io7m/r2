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

import com.io7m.junreachable.UnreachableCodeException;

/**
 * Checks regarding the validity of basic mesh elements.
 */

public final class R2MeshBasicChecks
{
  private R2MeshBasicChecks()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Check the triangle for validity.
   *
   * @param triangle_index The index of the triangle
   * @param vertices_max   The number of vertices in the mesh
   * @param triangle       The triangle
   *
   * @throws R2MeshException If the mesh is not valid
   */

  public static void checkTriangle(
    final long triangle_index,
    final long vertices_max,
    final R2MeshTriangle triangle)
    throws R2MeshException
  {
    final long v0 = triangle.v0();
    if (Long.compareUnsigned(v0, vertices_max) >= 0) {
      throw new R2MeshExceptionMissingVertex(
        "Vertex 0: " + Long.toString(v0));
    }
    final long v1 = triangle.v1();
    if (Long.compareUnsigned(v1, vertices_max) >= 0) {
      throw new R2MeshExceptionMissingVertex(
        "Vertex 1: " + Long.toString(v1));
    }
    final long v2 = triangle.v2();
    if (Long.compareUnsigned(v2, vertices_max) >= 0) {
      throw new R2MeshExceptionMissingVertex(
        "Vertex 2: " + Long.toString(v2));
    }

    if (v0 == v1 || v1 == v2 || v0 == v2) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Malformed triangle.\n");
      sb.append("Duplicate vertex indices.\n");
      sb.append("Triangle: ");
      sb.append(triangle_index);
      sb.append(System.lineSeparator());
      sb.append("Indices: ");
      sb.append(v0);
      sb.append(" ");
      sb.append(v1);
      sb.append(" ");
      sb.append(v2);
      throw new R2MeshExceptionMalformedTriangle(sb.toString());
    }
  }
}
