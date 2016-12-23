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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of the {@link R2MeshBasicType} interface.
 */

public final class R2MeshBasic implements R2MeshBasicType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MeshBasic.class);
  }

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

    if (LOG.isTraceEnabled()) {
      LOG.trace("allocated mesh");
      LOG.trace(
        "mesh positions: {}", Long.valueOf(this.positions.size64()));
      LOG.trace(
        "mesh normals:   {}", Long.valueOf(this.normals.size64()));
      LOG.trace(
        "mesh uvs:       {}", Long.valueOf(this.uvs.size64()));
      LOG.trace(
        "mesh vertices:  {}", Long.valueOf(this.vertices.size64()));
      LOG.trace(
        "mesh triangles: {}", Long.valueOf(this.triangles.size64()));
    }
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

  @Override
  public BigList<PVectorI3D<R2SpaceObjectType>> getNormals()
  {
    return this.normals;
  }

  @Override
  public BigList<PVectorI3D<R2SpaceObjectType>> getPositions()
  {
    return this.positions;
  }

  @Override
  public BigList<PVectorI2D<R2SpaceTextureType>> getUVs()
  {
    return this.uvs;
  }

  @Override
  public BigList<R2MeshBasicVertexType> getVertices()
  {
    return this.vertices;
  }

  @Override
  public BigList<R2MeshTriangleType> getTriangles()
  {
    return this.triangles;
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    final R2MeshBasic that = (R2MeshBasic) o;
    return this.getPositions().equals(that.getPositions())
      && this.getNormals().equals(that.getNormals())
      && this.getUVs().equals(that.getUVs())
      && this.getVertices().equals(that.getVertices())
      && this.getTriangles().equals(that.getTriangles());
  }

  @Override
  public int hashCode()
  {
    int result = this.getPositions().hashCode();
    result = 31 * result + this.getNormals().hashCode();
    result = 31 * result + this.uvs.hashCode();
    result = 31 * result + this.getVertices().hashCode();
    result = 31 * result + this.getTriangles().hashCode();
    return result;
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

    @Override
    public void reset()
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
      if (LOG.isTraceEnabled()) {
        LOG.trace(
          "[{}] position {}", Long.valueOf(this.positions.size64()), p);
      }

      this.positions.add(new PVectorI3D<>(p));
      return this.positions.size64() - 1L;
    }

    @Override
    public long addNormal(final PVectorReadable3DType<R2SpaceObjectType> n)
    {
      if (LOG.isTraceEnabled()) {
        LOG.trace(
          "[{}] normal {}", Long.valueOf(this.normals.size64()), n);
      }

      this.normals.add(new PVectorI3D<>(n));
      return this.normals.size64() - 1L;
    }

    @Override
    public long addUV(final PVectorReadable2DType<R2SpaceTextureType> u)
    {
      if (LOG.isTraceEnabled()) {
        LOG.trace(
          "[{}] uv {}", Long.valueOf(this.uvs.size64()), u);
      }

      this.uvs.add(new PVectorI2D<>(u));
      return this.uvs.size64() - 1L;
    }

    @Override
    public long addVertex(
      final long p,
      final long n,
      final long u)
    {
      if (LOG.isTraceEnabled()) {
        LOG.trace(
          "[{}] vertex {} {} {}",
          Long.valueOf(this.vertices.size64()),
          Long.valueOf(p),
          Long.valueOf(n),
          Long.valueOf(u));
      }

      if (p < 0L || Long.compareUnsigned(p, this.positions.size64()) >= 0) {
        throw new R2MeshExceptionMissingPosition(Long.toString(p));
      }
      if (n < 0L || Long.compareUnsigned(n, this.normals.size64()) >= 0) {
        throw new R2MeshExceptionMissingNormal(Long.toString(n));
      }
      if (u < 0L || Long.compareUnsigned(u, this.uvs.size64()) >= 0) {
        throw new R2MeshExceptionMissingUV(Long.toString(u));
      }

      this.vertices.add(R2MeshBasicVertex.of(p, n, u));
      return this.vertices.size64() - 1L;
    }

    @Override
    public long addTriangle(
      final long v0,
      final long v1,
      final long v2)
    {
      if (LOG.isTraceEnabled()) {
        LOG.trace(
          "[{}] triangle {} {} {}",
          Long.valueOf(this.triangles.size64()),
          Long.valueOf(v0),
          Long.valueOf(v1),
          Long.valueOf(v2));
      }

      if (v0 < 0L || Long.compareUnsigned(v0, this.vertices.size64()) >= 0) {
        throw new R2MeshExceptionMissingVertex(
          "Vertex 0: " + Long.toString(v0));
      }
      if (v1 < 0L || Long.compareUnsigned(v1, this.vertices.size64()) >= 0) {
        throw new R2MeshExceptionMissingVertex(
          "Vertex 1: " + Long.toString(v1));
      }
      if (v2 < 0L || Long.compareUnsigned(v2, this.vertices.size64()) >= 0) {
        throw new R2MeshExceptionMissingVertex(
          "Vertex 2: " + Long.toString(v2));
      }

      if (v0 == v1 || v1 == v2 || v0 == v2) {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("Malformed triangle.\n");
        sb.append("Duplicate vertex indices.\n");
        sb.append("Triangle: ");
        sb.append(this.triangles.size64());
        sb.append("\n");
        sb.append("Indices: ");
        sb.append(v0);
        sb.append(" ");
        sb.append(v1);
        sb.append(" ");
        sb.append(v2);
        throw new R2MeshExceptionMalformedTriangle(sb.toString());
      }

      this.triangles.add(R2MeshTriangle.of(v0, v1, v2));
      return this.triangles.size64() - 1L;
    }

    @Override
    public R2MeshBasicType build()
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
