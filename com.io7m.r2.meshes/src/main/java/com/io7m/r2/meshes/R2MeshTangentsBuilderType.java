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

import com.io7m.jtensors.core.parameterized.vectors.PVector2D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;

import java.util.NoSuchElementException;

/**
 * The type of mutable builders for meshes that have tangent vectors.
 */

public interface R2MeshTangentsBuilderType
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
    PVector3D<R2SpaceObjectType> p);

  /**
   * Add a normal vector.
   *
   * @param n The normal
   *
   * @return The index of the added vector
   */

  long addNormal(
    PVector3D<R2SpaceObjectType> n);

  /**
   * Add a UV vector.
   *
   * @param u The UV
   *
   * @return The index of the added vector
   */

  long addUV(
    PVector2D<R2SpaceTextureType> u);

  /**
   * Add a tangent vector.
   *
   * @param p The position
   *
   * @return The index of the added vector
   */

  long addTangent(
    PVector4D<R2SpaceObjectType> p);

  /**
   * Add a bitangent vector.
   *
   * @param p The position
   *
   * @return The index of the added vector
   */

  long addBitangent(
    PVector3D<R2SpaceObjectType> p);

  /**
   * Add a vertex.
   *
   * @param p The index of the position vector
   * @param n The index of the normal vector
   * @param t The index of the tangent vector
   * @param b The index of the bitangent vector
   * @param u The index of the UV vector
   *
   * @return The index of the new vertex
   *
   * @throws NoSuchElementException Iff any of the given indices do not exist
   * @see #addPosition(PVector3D)
   * @see #addNormal(PVector3D)
   * @see #addUV(PVector2D)
   * @see #addTangent(PVector4D)
   * @see #addBitangent(PVector3D)
   */

  long addVertex(
    long p,
    long n,
    long t,
    long b,
    long u)
    throws NoSuchElementException;

  /**
   * Add a triangle.
   *
   * @param v0 The index of the first vertex
   * @param v1 The index of the second vertex
   * @param v2 The index of the third vertex
   *
   * @return The index of the new triangle
   *
   * @throws NoSuchElementException Iff any of the given vertices do not exist
   * @see #addVertex(long, long, long, long, long)
   */

  long addTriangle(
    long v0,
    long v1,
    long v2)
    throws NoSuchElementException;

  /**
   * @return A mesh with all of the data specified so far
   */

  R2MeshTangentsType build();

}
