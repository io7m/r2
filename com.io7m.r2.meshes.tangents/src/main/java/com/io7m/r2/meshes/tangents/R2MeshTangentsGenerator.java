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

package com.io7m.r2.meshes.tangents;

import com.io7m.jaffirm.core.Invariants;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.core.parameterized.vectors.PVector2D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.jtensors.core.parameterized.vectors.PVectors3D;
import com.io7m.jtensors.core.parameterized.vectors.PVectors4D;
import com.io7m.jtensors.orthonormalization.POrthonormalization;
import com.io7m.jtensors.orthonormalization.POrthonormalized3D;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.meshes.api.R2MeshBasic;
import com.io7m.r2.meshes.api.R2MeshBasicVertex;
import com.io7m.r2.meshes.api.R2MeshExceptionMalformedTriangle;
import com.io7m.r2.meshes.api.R2MeshTriangle;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.io7m.r2.meshes.api.R2MeshBasicChecks.checkTriangle;

/**
 * Functions to generate tangent vectors for meshes.
 */

public final class R2MeshTangentsGenerator
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MeshTangentsGenerator.class);
  }

  private R2MeshTangentsGenerator()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Generate tangent and bitangent vectors for the given mesh.
   *
   * @param in_m The initial mesh
   *
   * @return A mesh with generated tangent/bitangent vectors
   */

  public static R2MeshTangents generateTangents(
    final R2MeshBasic in_m)
  {
    final R2MeshBasic m = NullCheck.notNull(in_m, "Mesh");

    LOG.debug("generating tangents");

    final BigList<PVector3D<R2SpaceObjectType>> positions = m.positions();
    final BigList<PVector3D<R2SpaceObjectType>> normals = m.normals();
    final BigList<PVector2D<R2SpaceTextureType>> uvs = m.uvs();
    final BigList<R2MeshTriangle> triangles = m.triangles();
    final BigList<R2MeshBasicVertex> vertices = m.vertices();

    /*
     * Create a set of zero vectors for the initial tangent and bitangent
     * vectors.
     */

    final ObjectBigArrayBigList<PVector4D<R2SpaceObjectType>> tangents =
      new ObjectBigArrayBigList<>(vertices.size64());

    for (long index = 0L; index < normals.size64(); ++index) {
      tangents.add(PVectors4D.zero());
    }

    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> bitangents =
      new ObjectBigArrayBigList<>(vertices.size64());

    for (long index = 0L; index < normals.size64(); ++index) {
      bitangents.add(PVectors3D.zero());
    }

    /*
     * Generate initial tangent and bitangent vectors.
     */

    for (long tri_index = 0L; tri_index < triangles.size64(); ++tri_index) {
      final R2MeshTriangle triangle = triangles.get(tri_index);
      checkTriangle(tri_index, vertices.size64(), triangle);

      final R2MeshBasicVertex v0 = vertices.get(triangle.v0());
      final R2MeshBasicVertex v1 = vertices.get(triangle.v1());
      final R2MeshBasicVertex v2 = vertices.get(triangle.v2());

      final PVector3D<R2SpaceObjectType> v0p =
        positions.get(v0.positionIndex());
      final PVector3D<R2SpaceObjectType> v1p =
        positions.get(v1.positionIndex());
      final PVector3D<R2SpaceObjectType> v2p =
        positions.get(v2.positionIndex());

      checkVector3D(v0p, "Position", v0.positionIndex());
      checkVector3D(v1p, "Position", v1.positionIndex());
      checkVector3D(v2p, "Position", v2.positionIndex());

      /*
       * Fetch whatever tangent and bitangent vectors are currently
       * at the same array index as the normal vectors.
       */

      final PVector4D<R2SpaceObjectType> v0t =
        tangents.get(v0.normalIndex());
      final PVector4D<R2SpaceObjectType> v1t =
        tangents.get(v1.normalIndex());
      final PVector4D<R2SpaceObjectType> v2t =
        tangents.get(v2.normalIndex());

      checkVector4D(v0t, "Tangent", v0.normalIndex());
      checkVector4D(v1t, "Tangent", v1.normalIndex());
      checkVector4D(v2t, "Tangent", v2.normalIndex());

      final PVector3D<R2SpaceObjectType> v0b =
        bitangents.get(v0.normalIndex());
      final PVector3D<R2SpaceObjectType> v1b =
        bitangents.get(v1.normalIndex());
      final PVector3D<R2SpaceObjectType> v2b =
        bitangents.get(v2.normalIndex());

      checkVector3D(v0b, "Bitangent", v0.normalIndex());
      checkVector3D(v1b, "Bitangent", v1.normalIndex());
      checkVector3D(v2b, "Bitangent", v2.normalIndex());

      final PVector2D<R2SpaceTextureType> v0u = uvs.get(v0.uvIndex());
      final PVector2D<R2SpaceTextureType> v1u = uvs.get(v1.uvIndex());
      final PVector2D<R2SpaceTextureType> v2u = uvs.get(v2.uvIndex());

      checkVector2D(v0u, "UV", v0.uvIndex());
      checkVector2D(v1u, "UV", v1.uvIndex());
      checkVector2D(v2u, "UV", v2.uvIndex());

      /*
       * In the case where, for example, two vertices in a triangle share the
       * same UV coordinates, it's simply not possible to generate a reasonable
       * tangent vector.
       */

      checkTriangleUVs(tri_index, triangle, v0, v1, v2, v0u, v1u, v2u);

      final double x1 = v1p.x() - v0p.x();
      final double x2 = v2p.x() - v0p.x();

      final double y1 = v1p.y() - v0p.y();
      final double y2 = v2p.y() - v0p.y();

      final double z1 = v1p.z() - v0p.z();
      final double z2 = v2p.z() - v0p.z();

      final double s1 = v1u.x() - v0u.x();
      final double s2 = v2u.x() - v0u.x();

      final double t1 = v1u.y() - v0u.y();
      final double t2 = v2u.y() - v0u.y();

      final double d = (s1 * t2) - (s2 * t1);

      /*
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

      final PVector4D<R2SpaceObjectType> v0t_acc =
        PVector4D.of(
          v0t.x() + tx,
          v0t.y() + ty,
          v0t.z() + tz,
          1.0);
      final PVector4D<R2SpaceObjectType> v1t_acc =
        PVector4D.of(
          v1t.x() + tx,
          v1t.y() + ty,
          v1t.z() + tz,
          1.0);
      final PVector4D<R2SpaceObjectType> v2t_acc =
        PVector4D.of(
          v2t.x() + tx,
          v2t.y() + ty,
          v2t.z() + tz,
          1.0);

      checkVector4D(v0t_acc, "Generated Tangent", v0.normalIndex());
      checkVector4D(v1t_acc, "Generated Tangent", v1.normalIndex());
      checkVector4D(v2t_acc, "Generated Tangent", v2.normalIndex());

      tangents.set(v0.normalIndex(), v0t_acc);
      tangents.set(v1.normalIndex(), v1t_acc);
      tangents.set(v2.normalIndex(), v2t_acc);

      final PVector3D<R2SpaceObjectType> v0b_acc =
        PVector3D.of(v0b.x() + bx, v0b.y() + by, v0b.z() + bz);
      final PVector3D<R2SpaceObjectType> v1b_acc =
        PVector3D.of(v1b.x() + bx, v1b.y() + by, v1b.z() + bz);
      final PVector3D<R2SpaceObjectType> v2b_acc =
        PVector3D.of(v2b.x() + bx, v2b.y() + by, v2b.z() + bz);

      checkVector3D(v0b_acc, "Generated Bitangent", v0.normalIndex());
      checkVector3D(v1b_acc, "Generated Bitangent", v1.normalIndex());
      checkVector3D(v2b_acc, "Generated Bitangent", v2.normalIndex());

      bitangents.set(v0.normalIndex(), v0b_acc);
      bitangents.set(v1.normalIndex(), v1b_acc);
      bitangents.set(v2.normalIndex(), v2b_acc);
    }

    /*
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
      final PVector4D<R2SpaceObjectType> t = tangents.get(index);
      final PVector3D<R2SpaceObjectType> t3 = PVector3D.of(t.x(), t.y(), t.z());
      final PVector3D<R2SpaceObjectType> b = bitangents.get(index);
      final PVector3D<R2SpaceObjectType> n = normals.get(index);

      final POrthonormalized3D<R2SpaceObjectType> o =
        POrthonormalization.orthonormalize3D(n, t3, b);

      final PVector3D<R2SpaceObjectType> ot = o.v1();
      final PVector3D<R2SpaceObjectType> ob = o.v2();

      checkVector3D(ot, "Orthonormalized Tangent", index);
      checkVector3D(ob, "Orthonormalized Bitangent", index);

      /*
       * Invert the bitangent if the resulting coordinate system is not
       * right-handed (and save the fact that the inversion occurred in the w
       * component of the tangent vector).
       */

      final PVector4D<R2SpaceObjectType> rt;
      final PVector3D<R2SpaceObjectType> rb;
      if (PVectors3D.dotProduct(PVectors3D.crossProduct(n, t3), b) < 0.0) {
        rt = PVector4D.of(ot.x(), ot.y(), ot.z(), -1.0);
        rb = PVector3D.of(-ob.x(), -ob.y(), -ob.z());
      } else {
        rt = PVector4D.of(ot.x(), ot.y(), ot.z(), 1.0);
        rb = PVector3D.of(ob.x(), ob.y(), ob.z());
      }

      checkVector4D(rt, "Final Tangent", index);
      checkVector3D(rb, "Final Bitangent", index);

      tangents.set(index, rt);
      bitangents.set(index, rb);
    }

    final ObjectBigArrayBigList<R2MeshTangentsVertex> tan_vertices =
      new ObjectBigArrayBigList<>(vertices.size64());

    for (long index = 0L; index < vertices.size64(); ++index) {
      final R2MeshBasicVertex v = vertices.get(index);
      tan_vertices.add(R2MeshTangentsVertex.of(
        v.positionIndex(),
        v.normalIndex(),
        v.normalIndex(),
        v.normalIndex(),
        v.uvIndex()));
    }

    return R2MeshTangents.of(
      positions, normals, bitangents, tangents, uvs, tan_vertices, triangles);
  }

  /**
   * Check that a given triangle does not have at least two UV
   * coordinates that are the same.
   */

  private static void checkTriangleUVs(
    final long tri_index,
    final R2MeshTriangle triangle,
    final R2MeshBasicVertex v0,
    final R2MeshBasicVertex v1,
    final R2MeshBasicVertex v2,
    final PVector2D<R2SpaceTextureType> v0u,
    final PVector2D<R2SpaceTextureType> v1u,
    final PVector2D<R2SpaceTextureType> v2u)
  {
    final boolean sharing =
      (v0.uvIndex() == v1.uvIndex())
        || (v1.uvIndex() == v2.uvIndex())
        || (v0.uvIndex() == v2.uvIndex());

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
      sb.append(triangle.v0());
      sb.append(" ");
      sb.append(triangle.v1());
      sb.append(" ");
      sb.append(triangle.v2());
      sb.append(System.lineSeparator());

      sb.append("UV of V0: ");
      sb.append(v0.uvIndex());
      sb.append(" (");
      sb.append(v0u);
      sb.append(")");
      sb.append(System.lineSeparator());

      sb.append("UV of V1: ");
      sb.append(v1.uvIndex());
      sb.append(" (");
      sb.append(v1u);
      sb.append(")");
      sb.append(System.lineSeparator());

      sb.append("UV of V2: ");
      sb.append(v2.uvIndex());
      sb.append(" (");
      sb.append(v2u);
      sb.append(")");
      sb.append(System.lineSeparator());

      throw new R2MeshExceptionMalformedTriangle(sb.toString());
    }
  }

  private static <T> void checkVector2D(
    final PVector2D<T> v,
    final String name,
    final long index)
  {
    final Long bi = Long.valueOf(index);

    Invariants.checkInvariantD(
      v.x(),
      Double.isFinite(v.x()),
      x -> String.format("%s [%d].x must be finite", name, bi));
    Invariants.checkInvariantD(
      v.y(),
      Double.isFinite(v.y()),
      y -> String.format("%s [%d].y must be finite", name, bi));

    Invariants.checkInvariantD(
      v.x(),
      !Double.isNaN(v.x()),
      x -> String.format("%s [%d].x must be a valid number", name, bi));
    Invariants.checkInvariantD(
      v.y(),
      !Double.isNaN(v.y()),
      y -> String.format("%s [%d].y must be a valid number", name, bi));
  }

  private static <T> void checkVector4D(
    final PVector4D<T> v,
    final String name,
    final long index)
  {
    final Long bi = Long.valueOf(index);

    Invariants.checkInvariantD(
      v.x(),
      Double.isFinite(v.x()),
      x -> String.format("%s [%d].x must be finite", name, bi));
    Invariants.checkInvariantD(
      v.y(),
      Double.isFinite(v.y()),
      y -> String.format("%s [%d].y must be finite", name, bi));
    Invariants.checkInvariantD(
      v.z(),
      Double.isFinite(v.z()),
      z -> String.format("%s [%d].z must be finite", name, bi));
    Invariants.checkInvariantD(
      v.w(),
      Double.isFinite(v.w()),
      w -> String.format("%s [%d].w must be finite", name, bi));

    Invariants.checkInvariantD(
      v.x(),
      !Double.isNaN(v.x()),
      x -> String.format("%s [%d].x must be a valid number", name, bi));
    Invariants.checkInvariantD(
      v.y(),
      !Double.isNaN(v.y()),
      y -> String.format("%s [%d].y must be a valid number", name, bi));
    Invariants.checkInvariantD(
      v.z(),
      !Double.isNaN(v.z()),
      z -> String.format("%s [%d].z must be a valid number", name, bi));
    Invariants.checkInvariantD(
      v.w(),
      !Double.isNaN(v.w()),
      w -> String.format("%s [%d].w must be a valid number", name, bi));
  }

  private static <T> void checkVector3D(
    final PVector3D<T> v,
    final String name,
    final long index)
  {
    final Long bi = Long.valueOf(index);

    Invariants.checkInvariantD(
      v.x(),
      Double.isFinite(v.x()),
      x -> String.format("%s [%d].x must be finite", name, bi));
    Invariants.checkInvariantD(
      v.y(),
      Double.isFinite(v.y()),
      y -> String.format("%s [%d].y must be finite", name, bi));
    Invariants.checkInvariantD(
      v.z(),
      Double.isFinite(v.z()),
      z -> String.format("%s [%d].z must be finite", name, bi));

    Invariants.checkInvariantD(
      v.x(),
      !Double.isNaN(v.x()),
      x -> String.format("%s [%d].x must be a valid number", name, bi));
    Invariants.checkInvariantD(
      v.y(),
      !Double.isNaN(v.y()),
      y -> String.format("%s [%d].y must be a valid number", name, bi));
    Invariants.checkInvariantD(
      v.z(),
      !Double.isNaN(v.z()),
      z -> String.format("%s [%d].z must be a valid number", name, bi));
  }
}
