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

package com.io7m.r2.meshes;

import com.io7m.jtensors.parameterized.PVectorReadable2DType;
import com.io7m.jtensors.parameterized.PVectorReadable3DType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;

/**
 * The type of mutable builders for basic meshes.
 */

public interface R2MeshBasicBuilderType
{
  /**
   * Clear the contents of the builder
   */

  void reset();

  /**
   * Add a position vector.
   *
   * @param p The position
   *
   * @return The index of the added vector
   */

  long addPosition(
    PVectorReadable3DType<R2SpaceObjectType> p);

  /**
   * Add a normal vector.
   *
   * @param n The normal
   *
   * @return The index of the added vector
   */

  long addNormal(
    PVectorReadable3DType<R2SpaceObjectType> n);

  /**
   * Add a UV vector.
   *
   * @param u The UV
   *
   * @return The index of the added vector
   */

  long addUV(
    PVectorReadable2DType<R2SpaceTextureType> u);

  /**
   * Add a vertex.
   *
   * @param p The index of the position vector
   * @param n The index of the normal vector
   * @param u The index of the UV vector
   *
   * @return The index of the new vertex
   *
   * @throws R2MeshExceptionMissingPosition Iff no position vector exists for
   *                                        {@code p}
   * @throws R2MeshExceptionMissingNormal   Iff no normal vector exists for
   *                                        {@code n}
   * @throws R2MeshExceptionMissingUV       Iff no UV vector exists for {@code
   *                                        u}
   * @see #addPosition(PVectorReadable3DType)
   * @see #addNormal(PVectorReadable3DType)
   * @see #addUV(PVectorReadable2DType)
   */

  long addVertex(
    long p,
    long n,
    long u)
    throws
    R2MeshExceptionMissingPosition,
    R2MeshExceptionMissingNormal,
    R2MeshExceptionMissingUV;

  /**
   * Add a triangle.
   *
   * @param v0 The index of the first vertex
   * @param v1 The index of the second vertex
   * @param v2 The index of the third vertex
   *
   * @return The index of the new triangle
   *
   * @throws R2MeshExceptionMissingVertex Iff any of the given vertices do not
   *                                      exist
   * @see #addVertex(long, long, long)
   */

  long addTriangle(
    long v0,
    long v1,
    long v2)
    throws R2MeshExceptionMissingVertex;

  /**
   * @return A basic mesh with all of the data specified so far
   */

  R2MeshBasicType build();

}
