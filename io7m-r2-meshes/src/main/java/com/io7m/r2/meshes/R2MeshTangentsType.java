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

import com.io7m.jfunctional.PartialFunctionType;
import com.io7m.jtensors.parameterized.PVectorI2D;
import com.io7m.jtensors.parameterized.PVectorI3D;
import com.io7m.jtensors.parameterized.PVectorI4D;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import it.unimi.dsi.fastutil.BigList;

/**
 * The type of meshes with tangents.
 */

public interface R2MeshTangentsType extends R2MeshType
{
  /**
   * @return The list of normal vectors
   */

  BigList<PVectorI3D<R2SpaceObjectType>> normals();

  /**
   * @return The list of bitangent vectors
   */

  BigList<PVectorI3D<R2SpaceObjectType>> bitangents();

  /**
   * @return The list of tangent vectors
   */

  BigList<PVectorI4D<R2SpaceObjectType>> tangents();

  /**
   * @return The list of position vectors
   */

  BigList<PVectorI3D<R2SpaceObjectType>> positions();

  /**
   * @return The list of UV vectors
   */

  BigList<PVectorI2D<R2SpaceTextureType>> uvs();

  /**
   * @return The list of vertices
   */

  BigList<R2MeshTangentsVertexType> vertices();

  /**
   * @return The list of triangles
   */

  BigList<R2MeshTriangleType> triangles();

  @Override
  default <A, E extends Exception> A matchMesh(
    final PartialFunctionType<R2MeshBasicType, A, E> on_basic,
    final PartialFunctionType<R2MeshTangentsType, A, E> on_tangents)
    throws E
  {
    return on_tangents.call(this);
  }
}
