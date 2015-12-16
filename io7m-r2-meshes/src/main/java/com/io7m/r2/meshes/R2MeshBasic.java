/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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

import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.parameterized.PVectorI2D;
import com.io7m.jtensors.parameterized.PVectorI3D;
import com.io7m.jtensors.parameterized.PVectorReadable2DType;
import com.io7m.jtensors.parameterized.PVectorReadable3DType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigLists;

import java.util.NoSuchElementException;

/**
 * The default implementation of the {@link R2MeshBasicType} interface.
 */

public final class R2MeshBasic implements R2MeshBasicType
{
  private final ObjectBigList<PVectorI3D<R2SpaceObjectType>>  positions;
  private final ObjectBigList<PVectorI3D<R2SpaceObjectType>>  normals;
  private final ObjectBigList<PVectorI2D<R2SpaceTextureType>> uvs;
  private final ObjectBigList<R2MeshBasicVertexType>          vertices;
  private final ObjectBigList<R2MeshTriangleType>             triangles;

  private R2MeshBasic(
    final ObjectBigList<PVectorI3D<R2SpaceObjectType>> in_positions,
    final ObjectBigList<PVectorI3D<R2SpaceObjectType>> in_normals,
    final ObjectBigList<PVectorI2D<R2SpaceTextureType>> in_uvs,
    final ObjectBigList<R2MeshBasicVertexType> in_vertices,
    final ObjectBigList<R2MeshTriangleType> in_triangles)
  {
    this.normals = NullCheck.notNull(in_normals);
    this.positions = NullCheck.notNull(in_positions);
    this.uvs = NullCheck.notNull(in_uvs);
    this.vertices = NullCheck.notNull(in_vertices);
    this.triangles = NullCheck.notNull(in_triangles);
  }

  /**
   * @param v_count A hint to the implementation regarding the number of
   *                vertices that are expected to be created. The implementation
   *                will allocate {@code v_count} vertices ahead of time to
   *                avoid having to perform any internal reallocations during
   *                building.
   * @param t_count A hint to the implementation regarding the number of
   *                triangles that are expected to be created. The
   *                implementation will allocate {@code t_count} triangles ahead
   *                of time to avoid having to perform any internal
   *                reallocations during building.
   *
   * @return A new mutable mesh builder
   */

  public static R2MeshBasicBuilderType newBuilder(
    final long v_count,
    final long t_count)
  {
    return new Builder(v_count, t_count);
  }

  @Override public BigList<PVectorI3D<R2SpaceObjectType>> getNormals()
  {
    return this.normals;
  }

  @Override public BigList<PVectorI3D<R2SpaceObjectType>> getPositions()
  {
    return this.positions;
  }

  @Override public BigList<PVectorI2D<R2SpaceTextureType>> getUVs()
  {
    return this.uvs;
  }

  @Override public BigList<R2MeshBasicVertexType> getVertices()
  {
    return this.vertices;
  }

  @Override public BigList<R2MeshTriangleType> getTriangles()
  {
    return this.triangles;
  }

  private static final class Builder implements R2MeshBasicBuilderType
  {
    private final ObjectBigList<PVectorI3D<R2SpaceObjectType>>  positions;
    private final ObjectBigList<PVectorI3D<R2SpaceObjectType>>  normals;
    private final ObjectBigList<PVectorI2D<R2SpaceTextureType>> uvs;
    private final ObjectBigList<R2MeshBasicVertexType>          vertices;
    private final ObjectBigList<R2MeshTriangleType>             triangles;

    Builder(
      final long v_count,
      final long t_count)
    {
      this.positions = new ObjectBigArrayBigList<>(v_count);
      this.normals = new ObjectBigArrayBigList<>(v_count);
      this.uvs = new ObjectBigArrayBigList<>(v_count);
      this.vertices = new ObjectBigArrayBigList<>(v_count);
      this.triangles = new ObjectBigArrayBigList<>(t_count);
    }

    @Override public void reset()
    {
      this.positions.clear();
      this.normals.clear();
      this.uvs.clear();
      this.vertices.clear();
      this.triangles.clear();
    }

    @Override
    public long addPosition(final PVectorReadable3DType<R2SpaceObjectType> p)
    {
      this.positions.add(new PVectorI3D<>(p));
      return this.positions.size64() - 1L;
    }

    @Override
    public long addNormal(final PVectorReadable3DType<R2SpaceObjectType> n)
    {
      this.normals.add(new PVectorI3D<>(n));
      return this.normals.size64() - 1L;
    }

    @Override
    public long addUV(final PVectorReadable2DType<R2SpaceTextureType> u)
    {
      this.uvs.add(new PVectorI2D<>(u));
      return this.uvs.size64() - 1L;
    }

    @Override public long addVertex(
      final long p,
      final long n,
      final long u)
      throws NoSuchElementException
    {
      if (p < 0L || Long.compareUnsigned(p, this.positions.size64()) >= 0) {
        throw new NoSuchElementException("Position");
      }
      if (n < 0L || Long.compareUnsigned(n, this.normals.size64()) >= 0) {
        throw new NoSuchElementException("Normal");
      }
      if (u < 0L || Long.compareUnsigned(n, this.uvs.size64()) >= 0) {
        throw new NoSuchElementException("UV");
      }

      this.vertices.add(R2MeshBasicVertex.of(p, n, u));
      return this.vertices.size64() - 1L;
    }

    @Override public long addTriangle(
      final long v0,
      final long v1,
      final long v2)
      throws NoSuchElementException
    {
      if (v0 < 0L || Long.compareUnsigned(v0, this.vertices.size64()) >= 0) {
        throw new NoSuchElementException("Vertex 0");
      }
      if (v1 < 0L || Long.compareUnsigned(v1, this.vertices.size64()) >= 0) {
        throw new NoSuchElementException("Vertex 1");
      }
      if (v2 < 0L || Long.compareUnsigned(v2, this.vertices.size64()) >= 0) {
        throw new NoSuchElementException("Vertex 2");
      }

      this.triangles.add(R2MeshTriangle.of(v0, v1, v2));
      return this.triangles.size64() - 1L;
    }

    @Override public R2MeshBasicType build()
    {
      return new R2MeshBasic(
        ObjectBigLists.unmodifiable(
          new ObjectBigArrayBigList<>(this.positions)),
        ObjectBigLists.unmodifiable(
          new ObjectBigArrayBigList<>(this.normals)),
        ObjectBigLists.unmodifiable(
          new ObjectBigArrayBigList<>(this.uvs)),
        ObjectBigLists.unmodifiable(
          new ObjectBigArrayBigList<>(this.vertices)),
        ObjectBigLists.unmodifiable(
          new ObjectBigArrayBigList<>(this.triangles))
      );
    }
  }
}
