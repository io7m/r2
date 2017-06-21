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

import com.io7m.jtensors.core.parameterized.vectors.PVector2D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import it.unimi.dsi.fastutil.BigList;
import org.immutables.value.Value;

/**
 * The type of basic meshes.
 */

@R2MeshesImmutableStyleType
@Value.Immutable
public interface R2MeshBasicType
{
  /**
   * @return The list of position vectors
   */

  @Value.Parameter
  BigList<PVector3D<R2SpaceObjectType>> positions();

  /**
   * @return The list of normal vectors
   */

  @Value.Parameter
  BigList<PVector3D<R2SpaceObjectType>> normals();

  /**
   * @return The list of UV vectors
   */

  @Value.Parameter
  BigList<PVector2D<R2SpaceTextureType>> uvs();

  /**
   * @return The list of vertices
   */

  @Value.Parameter
  BigList<R2MeshBasicVertex> vertices();

  /**
   * @return The list of triangles
   */

  @Value.Parameter
  BigList<R2MeshTriangle> triangles();

  /**
   * Check preconditions for the type.
   */

  @Value.Check
  default void checkPreconditions()
  {
    for (final R2MeshBasicVertex v : this.vertices()) {
      if (Long.compareUnsigned(
        v.positionIndex(), this.positions().size64()) >= 0) {
        throw new R2MeshExceptionMissingPosition(
          Long.toUnsignedString(v.positionIndex()));
      }

      if (Long.compareUnsigned(
        v.normalIndex(), this.normals().size64()) >= 0) {
        throw new R2MeshExceptionMissingNormal(
          Long.toString(v.normalIndex()));
      }

      if (Long.compareUnsigned(v.uvIndex(), this.uvs().size64()) >= 0) {
        throw new R2MeshExceptionMissingUV(
          Long.toString(v.uvIndex()));
      }
    }

    final long vertices_max = this.vertices().size64();
    for (long index = 0L; index < this.triangles().size64(); ++index) {
      R2MeshBasicChecks.checkTriangle(
        index, vertices_max, this.triangles().get(index));
    }
  }
}
