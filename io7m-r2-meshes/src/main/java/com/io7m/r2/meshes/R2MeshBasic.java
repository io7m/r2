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
import com.io7m.jtensors.parameterized.PVectorI2F;
import com.io7m.jtensors.parameterized.PVectorI3F;
import com.io7m.jtensors.parameterized.PVectorReadable2FType;
import com.io7m.jtensors.parameterized.PVectorReadable3FType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * The default implementation of the {@link R2MeshBasicType} interface.
 */

public final class R2MeshBasic implements R2MeshBasicType
{
  private final List<PVectorI3F<R2SpaceObjectType>>  positions;
  private final List<PVectorI3F<R2SpaceObjectType>>  normals;
  private final List<PVectorI2F<R2SpaceTextureType>> uvs;
  private final List<R2MeshBasicVertexType>          vertices;
  private final List<R2MeshTriangleType>             triangles;

  private R2MeshBasic(
    final List<PVectorI3F<R2SpaceObjectType>> in_positions,
    final List<PVectorI3F<R2SpaceObjectType>> in_normals,
    final List<PVectorI2F<R2SpaceTextureType>> in_uvs,
    final List<R2MeshBasicVertexType> in_vertices,
    final List<R2MeshTriangleType> in_triangles)
  {
    this.normals = NullCheck.notNull(in_normals);
    this.positions = NullCheck.notNull(in_positions);
    this.uvs = NullCheck.notNull(in_uvs);
    this.vertices = NullCheck.notNull(in_vertices);
    this.triangles = NullCheck.notNull(in_triangles);
  }

  /**
   * @return A new mutable mesh builder
   */

  public static R2MeshBasicBuilderType newBuilder()
  {
    return new Builder();
  }

  @Override public List<PVectorI3F<R2SpaceObjectType>> getNormals()
  {
    return this.normals;
  }

  @Override public List<PVectorI3F<R2SpaceObjectType>> getPositions()
  {
    return this.positions;
  }

  @Override public List<PVectorI2F<R2SpaceTextureType>> getUVs()
  {
    return this.uvs;
  }

  @Override public List<R2MeshBasicVertexType> getVertices()
  {
    return this.vertices;
  }

  @Override public List<R2MeshTriangleType> getTriangles()
  {
    return this.triangles;
  }

  private static final class Builder implements R2MeshBasicBuilderType
  {
    private final List<PVectorI3F<R2SpaceObjectType>>  positions;
    private final List<PVectorI3F<R2SpaceObjectType>>  normals;
    private final List<PVectorI2F<R2SpaceTextureType>> uvs;
    private final List<R2MeshBasicVertexType>          vertices;
    private final List<R2MeshTriangleType>             triangles;

    Builder()
    {
      this.positions = new ArrayList<>();
      this.normals = new ArrayList<>();
      this.uvs = new ArrayList<>();
      this.vertices = new ArrayList<>();
      this.triangles = new ArrayList<>();
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
    public long addPosition(final PVectorReadable3FType<R2SpaceObjectType> p)
    {
      this.positions.add(new PVectorI3F<>(p));
      return (long) (this.positions.size() - 1);
    }

    @Override
    public long addNormal(final PVectorReadable3FType<R2SpaceObjectType> n)
    {
      this.normals.add(new PVectorI3F<>(n));
      return (long) (this.normals.size() - 1);
    }

    @Override
    public long addUV(final PVectorReadable2FType<R2SpaceTextureType> u)
    {
      this.uvs.add(new PVectorI2F<>(u));
      return (long) (this.uvs.size() - 1);
    }

    @Override public long addVertex(
      final long p,
      final long n,
      final long u)
      throws NoSuchElementException
    {
      if (p < 0L
          || Long.compareUnsigned(p, (long) this.positions.size()) >= 0) {
        throw new NoSuchElementException("Position");
      }

      if (n < 0L || Long.compareUnsigned(n, (long) this.normals.size()) >= 0) {
        throw new NoSuchElementException("Normal");
      }

      if (u < 0L || Long.compareUnsigned(n, (long) this.uvs.size()) >= 0) {
        throw new NoSuchElementException("UV");
      }

      this.vertices.add(R2MeshBasicVertex.of(p, n, u));
      return (long) (this.vertices.size() - 1);
    }

    @Override public long addTriangle(
      final long v0,
      final long v1,
      final long v2)
      throws NoSuchElementException
    {
      if (v0 < 0L
          || Long.compareUnsigned(v0, (long) this.vertices.size()) >= 0) {
        throw new NoSuchElementException("Vertex 0");
      }
      if (v1 < 0L
          || Long.compareUnsigned(v1, (long) this.vertices.size()) >= 0) {
        throw new NoSuchElementException("Vertex 1");
      }
      if (v2 < 0L
          || Long.compareUnsigned(v0, (long) this.vertices.size()) >= 0) {
        throw new NoSuchElementException("Vertex 2");
      }

      this.triangles.add(R2MeshTriangle.of(v0, v1, v2));
      return (long) this.triangles.size() - 1L;
    }

    @Override public R2MeshBasicType build()
    {
      return new R2MeshBasic(
        Collections.unmodifiableList(new ArrayList<>(this.positions)),
        Collections.unmodifiableList(new ArrayList<>(this.normals)),
        Collections.unmodifiableList(new ArrayList<>(this.uvs)),
        Collections.unmodifiableList(new ArrayList<>(this.vertices)),
        Collections.unmodifiableList(new ArrayList<>(this.triangles))
      );
    }
  }
}
