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

import com.io7m.jaffirm.core.Invariants;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.OrthonormalizedI3D;
import com.io7m.jtensors.VectorI3D;
import com.io7m.jtensors.VectorReadable2DType;
import com.io7m.jtensors.VectorReadable3DType;
import com.io7m.jtensors.VectorReadable4DType;
import com.io7m.jtensors.parameterized.PVectorI2D;
import com.io7m.jtensors.parameterized.PVectorI3D;
import com.io7m.jtensors.parameterized.PVectorI4D;
import com.io7m.jtensors.parameterized.PVectorReadable2DType;
import com.io7m.jtensors.parameterized.PVectorReadable3DType;
import com.io7m.jtensors.parameterized.PVectorReadable4DType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigLists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;

/**
 * The default implementation of the {@link R2MeshTangentsType} interface.
 */

public final class R2MeshTangents implements R2MeshTangentsType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MeshTangents.class);
  }

  private final BigList<PVectorI3D<R2SpaceObjectType>> positions;
  private final BigList<PVectorI3D<R2SpaceObjectType>> normals;
  private final BigList<PVectorI4D<R2SpaceObjectType>> tangents;
  private final BigList<PVectorI3D<R2SpaceObjectType>> bitangents;
  private final BigList<PVectorI2D<R2SpaceTextureType>> uvs;
  private final BigList<R2MeshTangentsVertexType> vertices;
  private final BigList<R2MeshTriangleType> triangles;

  private R2MeshTangents(
    final BigList<PVectorI3D<R2SpaceObjectType>> in_positions,
    final BigList<PVectorI3D<R2SpaceObjectType>> in_normals,
    final BigList<PVectorI4D<R2SpaceObjectType>> in_tangents,
    final BigList<PVectorI3D<R2SpaceObjectType>> in_bitangents,
    final BigList<PVectorI2D<R2SpaceTextureType>> in_uvs,
    final BigList<R2MeshTangentsVertexType> in_vertices,
    final BigList<R2MeshTriangleType> in_triangles)
  {
    this.positions = NullCheck.notNull(in_positions);
    this.normals = NullCheck.notNull(in_normals);
    this.tangents = NullCheck.notNull(in_tangents);
    this.bitangents = NullCheck.notNull(in_bitangents);
    this.uvs = NullCheck.notNull(in_uvs);
    this.vertices = NullCheck.notNull(in_vertices);
    this.triangles = NullCheck.notNull(in_triangles);

    if (R2MeshTangents.LOG.isTraceEnabled()) {
      R2MeshTangents.LOG.trace("allocated mesh");
      R2MeshTangents.LOG.trace(
        "mesh positions:  {}", Long.valueOf(this.positions.size64()));
      R2MeshTangents.LOG.trace(
        "mesh normals:    {}", Long.valueOf(this.normals.size64()));
      R2MeshTangents.LOG.trace(
        "mesh tangents:   {}", Long.valueOf(this.tangents.size64()));
      R2MeshTangents.LOG.trace(
        "mesh bitangents: {}", Long.valueOf(this.bitangents.size64()));
      R2MeshTangents.LOG.trace(
        "mesh uvs:        {}", Long.valueOf(this.uvs.size64()));
      R2MeshTangents.LOG.trace(
        "mesh vertices:   {}", Long.valueOf(this.vertices.size64()));
      R2MeshTangents.LOG.trace(
        "mesh triangles:  {}", Long.valueOf(this.triangles.size64()));
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

  public static R2MeshTangentsBuilderType newBuilder(
    final long v_count,
    final long t_count)
  {
    return new Builder(v_count, t_count);
  }

  /**
   * Generate tangent and bitangent vectors for the given mesh.
   *
   * @param in_m The initial mesh
   *
   * @return A mesh with generated tangent/bitangent vectors
   */

  public static R2MeshTangentsType generateTangents(
    final R2MeshBasicType in_m)
  {
    final R2MeshBasicType m = NullCheck.notNull(in_m);

    R2MeshTangents.LOG.debug("generating tangents");

    final BigList<PVectorI3D<R2SpaceObjectType>> positions = m.getPositions();
    final BigList<PVectorI3D<R2SpaceObjectType>> normals = m.getNormals();
    final BigList<PVectorI2D<R2SpaceTextureType>> uvs = m.getUVs();
    final BigList<R2MeshTriangleType> triangles = m.getTriangles();
    final BigList<R2MeshBasicVertexType> vertices = m.getVertices();

    /**
     * Create a set of zero vectors for the initial tangent and bitangent
     * vectors.
     */

    final ObjectBigArrayBigList<PVectorI4D<R2SpaceObjectType>> tangents =
      new ObjectBigArrayBigList<>(vertices.size64());

    for (long index = 0L; index < normals.size64(); ++index) {
      tangents.add(PVectorI4D.zero());
    }

    final ObjectBigArrayBigList<PVectorI3D<R2SpaceObjectType>> bitangents =
      new ObjectBigArrayBigList<>(vertices.size64());

    for (long index = 0L; index < normals.size64(); ++index) {
      bitangents.add(PVectorI3D.zero());
    }

    /**
     * Generate initial tangent and bitangent vectors.
     */

    for (long tri_index = 0L; tri_index < triangles.size64(); ++tri_index) {
      final R2MeshTriangleType triangle = triangles.get(tri_index);
      R2MeshTangents.checkTriangleVertices(
        triangle.getV0(),
        triangle.getV1(),
        triangle.getV2(),
        vertices.size64(),
        triangles);

      final R2MeshBasicVertexType v0 = vertices.get(triangle.getV0());
      final R2MeshBasicVertexType v1 = vertices.get(triangle.getV1());
      final R2MeshBasicVertexType v2 = vertices.get(triangle.getV2());

      final PVectorI3D<R2SpaceObjectType> v0p =
        positions.get(v0.getPositionIndex());
      final PVectorI3D<R2SpaceObjectType> v1p =
        positions.get(v1.getPositionIndex());
      final PVectorI3D<R2SpaceObjectType> v2p =
        positions.get(v2.getPositionIndex());

      R2MeshTangents.checkVector3D(v0p, "Position", v0.getPositionIndex());
      R2MeshTangents.checkVector3D(v1p, "Position", v1.getPositionIndex());
      R2MeshTangents.checkVector3D(v2p, "Position", v2.getPositionIndex());

      /**
       * Fetch whatever tangent and bitangent vectors are currently
       * at the same array index as the normal vectors.
       */

      final PVectorI4D<R2SpaceObjectType> v0t =
        tangents.get(v0.getNormalIndex());
      final PVectorI4D<R2SpaceObjectType> v1t =
        tangents.get(v1.getNormalIndex());
      final PVectorI4D<R2SpaceObjectType> v2t =
        tangents.get(v2.getNormalIndex());

      R2MeshTangents.checkVector4D(v0t, "Tangent", v0.getNormalIndex());
      R2MeshTangents.checkVector4D(v1t, "Tangent", v1.getNormalIndex());
      R2MeshTangents.checkVector4D(v2t, "Tangent", v2.getNormalIndex());

      final PVectorI3D<R2SpaceObjectType> v0b =
        bitangents.get(v0.getNormalIndex());
      final PVectorI3D<R2SpaceObjectType> v1b =
        bitangents.get(v1.getNormalIndex());
      final PVectorI3D<R2SpaceObjectType> v2b =
        bitangents.get(v2.getNormalIndex());

      R2MeshTangents.checkVector3D(v0b, "Bitangent", v0.getNormalIndex());
      R2MeshTangents.checkVector3D(v1b, "Bitangent", v1.getNormalIndex());
      R2MeshTangents.checkVector3D(v2b, "Bitangent", v2.getNormalIndex());

      final PVectorI2D<R2SpaceTextureType> v0u = uvs.get(v0.getUVIndex());
      final PVectorI2D<R2SpaceTextureType> v1u = uvs.get(v1.getUVIndex());
      final PVectorI2D<R2SpaceTextureType> v2u = uvs.get(v2.getUVIndex());

      R2MeshTangents.checkVector2D(v0u, "UV", v0.getUVIndex());
      R2MeshTangents.checkVector2D(v1u, "UV", v1.getUVIndex());
      R2MeshTangents.checkVector2D(v2u, "UV", v2.getUVIndex());

      /**
       * In the case where, for example, two vertices in a triangle share the
       * same UV coordinates, it's simply not possible to generate a reasonable
       * tangent vector.
       */

      R2MeshTangents.checkTriangleUVs(
        tri_index, triangle, v0, v1, v2, v0u, v1u, v2u);

      final double x1 = v1p.getXD() - v0p.getXD();
      final double x2 = v2p.getXD() - v0p.getXD();

      final double y1 = v1p.getYD() - v0p.getYD();
      final double y2 = v2p.getYD() - v0p.getYD();

      final double z1 = v1p.getZD() - v0p.getZD();
      final double z2 = v2p.getZD() - v0p.getZD();

      final double s1 = v1u.getXD() - v0u.getXD();
      final double s2 = v2u.getXD() - v0u.getXD();

      final double t1 = v1u.getYD() - v0u.getYD();
      final double t2 = v2u.getYD() - v0u.getYD();

      final double d = (s1 * t2) - (s2 * t1);

      /**
       * Typically caused by triangle vertices sharing the same UV
       * coordinate. Should be prevented by earlier checks.
       */

      Invariants.checkInvariantD(
        d,
        d != 0.0,
        x -> String.format("d (%f) must be != 0.0", Double.valueOf(x)));

      final double r = 1.0 / d;

      final double tx = ((t2 * x1) - (t1 * x2)) * r;
      final double ty = ((t2 * y1) - (t1 * y2)) * r;
      final double tz = ((t2 * z1) - (t1 * z2)) * r;

      final double bx = ((s1 * x2) - (s2 * x1)) * r;
      final double by = ((s1 * y2) - (s2 * y1)) * r;
      final double bz = ((s1 * z2) - (s2 * z1)) * r;

      final PVectorI4D<R2SpaceObjectType> v0t_acc =
        new PVectorI4D<>(
          v0t.getXD() + tx,
          v0t.getYD() + ty,
          v0t.getZD() + tz,
          1.0);
      final PVectorI4D<R2SpaceObjectType> v1t_acc =
        new PVectorI4D<>(
          v1t.getXD() + tx,
          v1t.getYD() + ty,
          v1t.getZD() + tz,
          1.0);
      final PVectorI4D<R2SpaceObjectType> v2t_acc =
        new PVectorI4D<>(
          v2t.getXD() + tx,
          v2t.getYD() + ty,
          v2t.getZD() + tz,
          1.0);

      R2MeshTangents.checkVector4D(
        v0t_acc, "Generated Tangent", v0.getNormalIndex());
      R2MeshTangents.checkVector4D(
        v1t_acc, "Generated Tangent", v1.getNormalIndex());
      R2MeshTangents.checkVector4D(
        v2t_acc, "Generated Tangent", v2.getNormalIndex());

      tangents.set(v0.getNormalIndex(), v0t_acc);
      tangents.set(v1.getNormalIndex(), v1t_acc);
      tangents.set(v2.getNormalIndex(), v2t_acc);

      final PVectorI3D<R2SpaceObjectType> v0b_acc =
        new PVectorI3D<>(v0b.getXD() + bx, v0b.getYD() + by, v0b.getZD() + bz);
      final PVectorI3D<R2SpaceObjectType> v1b_acc =
        new PVectorI3D<>(v1b.getXD() + bx, v1b.getYD() + by, v1b.getZD() + bz);
      final PVectorI3D<R2SpaceObjectType> v2b_acc =
        new PVectorI3D<>(v2b.getXD() + bx, v2b.getYD() + by, v2b.getZD() + bz);

      R2MeshTangents.checkVector3D(
        v0b_acc, "Generated Bitangent", v0.getNormalIndex());
      R2MeshTangents.checkVector3D(
        v1b_acc, "Generated Bitangent", v1.getNormalIndex());
      R2MeshTangents.checkVector3D(
        v2b_acc, "Generated Bitangent", v2.getNormalIndex());

      bitangents.set(v0.getNormalIndex(), v0b_acc);
      bitangents.set(v1.getNormalIndex(), v1b_acc);
      bitangents.set(v2.getNormalIndex(), v2b_acc);
    }

    /**
     * Orthonormalize tangents and bitangents.
     *
     * The normal, tangent, and bitangent vectors must form an orthonormal
     * right-handed basis.
     *
     * Because precomputed bitangents are optional, this code does two things:
     * It calculates bitangents, inverting them if necessary to form a
     * right-handed coordinate space, and it also saves a value in the w
     * component of the tangent vector in order to allow shading language
     * programs to perform this inversion themselves, if they are calculating
     * the bitangents at runtime (with {@code cross (N, T.xyz) * T.w}).
     */

    for (long index = 0L; index < tangents.size64(); ++index) {
      final PVectorI4D<R2SpaceObjectType> t = tangents.get(index);
      final PVectorI3D<R2SpaceObjectType> b = bitangents.get(index);
      final PVectorI3D<R2SpaceObjectType> n = normals.get(index);

      final OrthonormalizedI3D o = new OrthonormalizedI3D(n, t, b);
      final VectorI3D ot = o.getV1();
      final VectorI3D ob = o.getV2();

      R2MeshTangents.checkVector3D(ot, "Orthonormalized Tangent", index);
      R2MeshTangents.checkVector3D(ob, "Orthonormalized Bitangent", index);

      /**
       * Invert the bitangent if the resulting coordinate system is not
       * right-handed (and save the fact that the inversion occurred in the w
       * component of the tangent vector).
       */

      final PVectorI4D<R2SpaceObjectType> rt;
      final PVectorI3D<R2SpaceObjectType> rb;
      if (VectorI3D.dotProduct(VectorI3D.crossProduct(n, t), b) < 0.0) {
        rt = new PVectorI4D<>(ot.getXD(), ot.getYD(), ot.getZD(), -1.0);
        rb = new PVectorI3D<>(-ob.getXD(), -ob.getYD(), -ob.getZD());
      } else {
        rt = new PVectorI4D<>(ot.getXD(), ot.getYD(), ot.getZD(), 1.0);
        rb = new PVectorI3D<>(ob.getXD(), ob.getYD(), ob.getZD());
      }

      R2MeshTangents.checkVector4D(rt, "Final Tangent", index);
      R2MeshTangents.checkVector3D(rb, "Final Bitangent", index);

      tangents.set(index, rt);
      bitangents.set(index, rb);
    }

    final ObjectBigArrayBigList<R2MeshTangentsVertexType> tan_vertices =
      new ObjectBigArrayBigList<>(vertices.size64());

    for (long index = 0L; index < vertices.size64(); ++index) {
      final R2MeshBasicVertexType v = vertices.get(index);
      tan_vertices.add(R2MeshTangentsVertex.of(
        v.getPositionIndex(),
        v.getNormalIndex(),
        v.getNormalIndex(),
        v.getNormalIndex(),
        v.getUVIndex()));
    }

    return new R2MeshTangents(
      positions,
      normals,
      tangents,
      bitangents,
      uvs,
      tan_vertices,
      triangles);
  }

  /**
   * Check that a given triangle does not have at least two UV
   * coordinates that are the same.
   */

  private static void checkTriangleUVs(
    final long tri_index,
    final R2MeshTriangleType triangle,
    final R2MeshBasicVertexType v0,
    final R2MeshBasicVertexType v1,
    final R2MeshBasicVertexType v2,
    final PVectorI2D<R2SpaceTextureType> v0u,
    final PVectorI2D<R2SpaceTextureType> v1u,
    final PVectorI2D<R2SpaceTextureType> v2u)
  {
    final boolean sharing =
      (v0.getUVIndex() == v1.getUVIndex())
        || (v1.getUVIndex() == v2.getUVIndex())
        || (v0.getUVIndex() == v2.getUVIndex());

    if (sharing) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append(
        "At least two vertices of a triangle are sharing UV coordinates.");
      sb.append(System.lineSeparator());

      sb.append(
        "It is not possible to generate tangent vectors for this mesh.");
      sb.append(System.lineSeparator());

      sb.append("Triangle: ");
      sb.append(tri_index);
      sb.append(System.lineSeparator());

      sb.append("Triangle vertices: ");
      sb.append(triangle.getV0());
      sb.append(" ");
      sb.append(triangle.getV1());
      sb.append(" ");
      sb.append(triangle.getV2());
      sb.append(System.lineSeparator());

      sb.append("UV of V0: ");
      sb.append(v0.getUVIndex());
      sb.append(" (");
      sb.append(v0u);
      sb.append(")");
      sb.append(System.lineSeparator());

      sb.append("UV of V1: ");
      sb.append(v1.getUVIndex());
      sb.append(" (");
      sb.append(v1u);
      sb.append(")");
      sb.append(System.lineSeparator());

      sb.append("UV of V2: ");
      sb.append(v2.getUVIndex());
      sb.append(" (");
      sb.append(v2u);
      sb.append(")");
      sb.append(System.lineSeparator());

      throw new R2MeshExceptionMalformedTriangle(sb.toString());
    }
  }

  private static void checkVector2D(
    final VectorReadable2DType v,
    final String name,
    final long index)
  {
    final Long bi = Long.valueOf(index);

    Invariants.checkInvariantD(
      v.getXD(),
      Double.isFinite(v.getXD()),
      x -> String.format("%s [%d].x must be finite", name, bi));
    Invariants.checkInvariantD(
      v.getYD(),
      Double.isFinite(v.getYD()),
      y -> String.format("%s [%d].y must be finite", name, bi));

    Invariants.checkInvariantD(
      v.getXD(),
      !Double.isNaN(v.getXD()),
      x -> String.format("%s [%d].x must be a valid number", name, bi));
    Invariants.checkInvariantD(
      v.getYD(),
      !Double.isNaN(v.getYD()),
      y -> String.format("%s [%d].y must be a valid number", name, bi));
  }

  private static void checkVector4D(
    final VectorReadable4DType v,
    final String name,
    final long index)
  {
    final Long bi = Long.valueOf(index);

    Invariants.checkInvariantD(
      v.getXD(),
      Double.isFinite(v.getXD()),
      x -> String.format("%s [%d].x must be finite", name, bi));
    Invariants.checkInvariantD(
      v.getYD(),
      Double.isFinite(v.getYD()),
      y -> String.format("%s [%d].y must be finite", name, bi));
    Invariants.checkInvariantD(
      v.getZD(),
      Double.isFinite(v.getZD()),
      z -> String.format("%s [%d].z must be finite", name, bi));
    Invariants.checkInvariantD(
      v.getWD(),
      Double.isFinite(v.getWD()),
      w -> String.format("%s [%d].w must be finite", name, bi));

    Invariants.checkInvariantD(
      v.getXD(),
      !Double.isNaN(v.getXD()),
      x -> String.format("%s [%d].x must be a valid number", name, bi));
    Invariants.checkInvariantD(
      v.getYD(),
      !Double.isNaN(v.getYD()),
      y -> String.format("%s [%d].y must be a valid number", name, bi));
    Invariants.checkInvariantD(
      v.getZD(),
      !Double.isNaN(v.getZD()),
      z -> String.format("%s [%d].z must be a valid number", name, bi));
    Invariants.checkInvariantD(
      v.getWD(),
      !Double.isNaN(v.getWD()),
      w -> String.format("%s [%d].w must be a valid number", name, bi));
  }

  private static void checkVector3D(
    final VectorReadable3DType v,
    final String name,
    final long index)
  {
    final Long bi = Long.valueOf(index);

    Invariants.checkInvariantD(
      v.getXD(),
      Double.isFinite(v.getXD()),
      x -> String.format("%s [%d].x must be finite", name, bi));
    Invariants.checkInvariantD(
      v.getYD(),
      Double.isFinite(v.getYD()),
      y -> String.format("%s [%d].y must be finite", name, bi));
    Invariants.checkInvariantD(
      v.getZD(),
      Double.isFinite(v.getZD()),
      z -> String.format("%s [%d].z must be finite", name, bi));

    Invariants.checkInvariantD(
      v.getXD(),
      !Double.isNaN(v.getXD()),
      x -> String.format("%s [%d].x must be a valid number", name, bi));
    Invariants.checkInvariantD(
      v.getYD(),
      !Double.isNaN(v.getYD()),
      y -> String.format("%s [%d].y must be a valid number", name, bi));
    Invariants.checkInvariantD(
      v.getZD(),
      !Double.isNaN(v.getZD()),
      z -> String.format("%s [%d].z must be a valid number", name, bi));
  }

  private static void checkTriangleVertices(
    final long v0,
    final long v1,
    final long v2,
    final long vertices_max,
    final BigList<R2MeshTriangleType> triangles)
  {
    if (R2MeshTangents.LOG.isTraceEnabled()) {
      R2MeshTangents.LOG.trace(
        "check triangle: {} {} {}",
        Long.valueOf(v0),
        Long.valueOf(v1),
        Long.valueOf(v2));
    }

    if (v0 < 0L || Long.compareUnsigned(v0, vertices_max) >= 0) {
      throw new R2MeshExceptionMissingVertex(
        "Vertex 0: " + Long.toString(v0));
    }
    if (v1 < 0L || Long.compareUnsigned(v1, vertices_max) >= 0) {
      throw new R2MeshExceptionMissingVertex(
        "Vertex 1: " + Long.toString(v1));
    }
    if (v2 < 0L || Long.compareUnsigned(v2, vertices_max) >= 0) {
      throw new R2MeshExceptionMissingVertex(
        "Vertex 2: " + Long.toString(v2));
    }

    if (v0 == v1 || v1 == v2 || v0 == v2) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Malformed triangle.\n");
      sb.append("Duplicate vertex indices.\n");
      sb.append("Triangle: ");
      sb.append(triangles.size64());
      sb.append("\n");
      sb.append("Indices: ");
      sb.append(v0);
      sb.append(" ");
      sb.append(v1);
      sb.append(" ");
      sb.append(v2);
      throw new R2MeshExceptionMalformedTriangle(sb.toString());
    }
  }

  @Override
  public BigList<PVectorI3D<R2SpaceObjectType>> getNormals()
  {
    return this.normals;
  }

  @Override
  public BigList<PVectorI3D<R2SpaceObjectType>> getBitangents()
  {
    return this.bitangents;
  }

  @Override
  public BigList<PVectorI4D<R2SpaceObjectType>> getTangents()
  {
    return this.tangents;
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
  public BigList<R2MeshTangentsVertexType> getVertices()
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

    final R2MeshTangents that = (R2MeshTangents) o;
    return this.getPositions().equals(that.getPositions())
      && this.getNormals().equals(that.getNormals())
      && this.getTangents().equals(that.getTangents())
      && this.getBitangents().equals(that.getBitangents())
      && this.getUVs().equals(that.getUVs())
      && this.getVertices().equals(that.getVertices())
      && this.getTriangles().equals(that.getTriangles());
  }

  @Override
  public int hashCode()
  {
    int result = this.getPositions().hashCode();
    result = 31 * result + this.getNormals().hashCode();
    result = 31 * result + this.getTangents().hashCode();
    result = 31 * result + this.getBitangents().hashCode();
    result = 31 * result + this.uvs.hashCode();
    result = 31 * result + this.getVertices().hashCode();
    result = 31 * result + this.getTriangles().hashCode();
    return result;
  }

  private static final class Builder implements R2MeshTangentsBuilderType
  {
    private final ObjectBigList<PVectorI3D<R2SpaceObjectType>> positions;
    private final ObjectBigList<PVectorI3D<R2SpaceObjectType>> normals;
    private final ObjectBigList<PVectorI3D<R2SpaceObjectType>> bitangents;
    private final ObjectBigList<PVectorI4D<R2SpaceObjectType>> tangents;
    private final ObjectBigList<PVectorI2D<R2SpaceTextureType>> uvs;
    private final ObjectBigList<R2MeshTangentsVertexType> vertices;
    private final ObjectBigList<R2MeshTriangleType> triangles;

    Builder(
      final long v_count,
      final long t_count)
    {
      this.positions = new ObjectBigArrayBigList<>(v_count);
      this.normals = new ObjectBigArrayBigList<>(v_count);
      this.tangents = new ObjectBigArrayBigList<>(v_count);
      this.bitangents = new ObjectBigArrayBigList<>(v_count);
      this.uvs = new ObjectBigArrayBigList<>(v_count);
      this.vertices = new ObjectBigArrayBigList<>(v_count);
      this.triangles = new ObjectBigArrayBigList<>(t_count);
    }

    @Override
    public void reset()
    {
      this.positions.clear();
      this.normals.clear();
      this.tangents.clear();
      this.bitangents.clear();
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

    @Override
    public long addTangent(final PVectorReadable4DType<R2SpaceObjectType> t)
    {
      this.tangents.add(new PVectorI4D<>(t));
      return this.tangents.size64() - 1L;
    }

    @Override
    public long addBitangent(final PVectorReadable3DType<R2SpaceObjectType> b)
    {
      this.bitangents.add(new PVectorI3D<>(b));
      return this.bitangents.size64() - 1L;
    }

    @Override
    public long addVertex(
      final long p,
      final long n,
      final long t,
      final long b,
      final long u)
      throws NoSuchElementException
    {
      if (p < 0L || Long.compareUnsigned(p, this.positions.size64()) >= 0) {
        throw new R2MeshExceptionMissingPosition(Long.toString(p));
      }

      if (n < 0L || Long.compareUnsigned(n, this.normals.size64()) >= 0) {
        throw new R2MeshExceptionMissingNormal(Long.toString(n));
      }

      if (t < 0L || Long.compareUnsigned(t, this.tangents.size64()) >= 0) {
        throw new R2MeshExceptionMissingTangent(Long.toString(t));
      }

      if (b < 0L || Long.compareUnsigned(b, this.bitangents.size64()) >= 0) {
        throw new R2MeshExceptionMissingBitangent(Long.toString(b));
      }

      if (u < 0L || Long.compareUnsigned(u, this.uvs.size64()) >= 0) {
        throw new R2MeshExceptionMissingUV(Long.toString(u));
      }

      this.vertices.add(R2MeshTangentsVertex.of(p, n, t, b, u));
      return this.vertices.size64() - 1L;
    }

    @Override
    public long addTriangle(
      final long v0,
      final long v1,
      final long v2)
      throws NoSuchElementException
    {
      R2MeshTangents.checkTriangleVertices(
        v0, v1, v2, this.vertices.size64(), this.triangles);
      this.triangles.add(R2MeshTriangle.of(v0, v1, v2));
      return this.triangles.size64() - 1L;
    }

    @Override
    public R2MeshTangentsType build()
    {
      final ObjectBigList<PVectorI3D<R2SpaceObjectType>> p = this.positions;

      return new R2MeshTangents(
        ObjectBigLists.unmodifiable(
          new ObjectBigArrayBigList<>(this.positions)),
        ObjectBigLists.unmodifiable(
          new ObjectBigArrayBigList<>(this.normals)),
        ObjectBigLists.unmodifiable(
          new ObjectBigArrayBigList<>(this.tangents)),
        ObjectBigLists.unmodifiable(
          new ObjectBigArrayBigList<>(this.bitangents)),
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
