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

package com.io7m.r2.tests.meshes.tangents;

import com.io7m.jtensors.core.parameterized.vectors.PVector2D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.jtensors.core.parameterized.vectors.PVectors3D;
import com.io7m.r2.meshes.api.R2MeshBasic;
import com.io7m.r2.meshes.api.R2MeshBasicVertex;
import com.io7m.r2.meshes.api.R2MeshExceptionMissingBitangent;
import com.io7m.r2.meshes.api.R2MeshExceptionMissingNormal;
import com.io7m.r2.meshes.api.R2MeshExceptionMissingPosition;
import com.io7m.r2.meshes.api.R2MeshExceptionMissingTangent;
import com.io7m.r2.meshes.api.R2MeshExceptionMissingUV;
import com.io7m.r2.meshes.api.R2MeshExceptionMissingVertex;
import com.io7m.r2.meshes.api.R2MeshTriangle;
import com.io7m.r2.meshes.tangents.R2MeshTangents;
import com.io7m.r2.meshes.tangents.R2MeshTangentsGenerator;
import com.io7m.r2.meshes.tangents.R2MeshTangentsVertex;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import org.hamcrest.core.StringStartsWith;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class R2MeshTangentsTest
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test
  public void testBuildEmpty()
  {
    final R2MeshTangents m = R2MeshTangents.of(
      new ObjectBigArrayBigList<>(),
      new ObjectBigArrayBigList<>(),
      new ObjectBigArrayBigList<>(),
      new ObjectBigArrayBigList<>(),
      new ObjectBigArrayBigList<>(),
      new ObjectBigArrayBigList<>(),
      new ObjectBigArrayBigList<>());

    Assert.assertEquals(0L, m.positions().size64());
    Assert.assertEquals(0L, m.normals().size64());
    Assert.assertEquals(0L, m.uvs().size64());
    Assert.assertEquals(0L, m.tangents().size64());
    Assert.assertEquals(0L, m.bitangents().size64());
    Assert.assertEquals(0L, m.vertices().size64());
    Assert.assertEquals(0L, m.triangles().size64());
  }

  @Test
  public void testBuildNoSuchPosition()
  {
    final ObjectBigArrayBigList<R2MeshTangentsVertex> vertices =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> positions =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> normals =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector2D<R2SpaceTextureType>> uvs =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector4D<R2SpaceObjectType>> tangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> bitangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<R2MeshTriangle> triangles =
      new ObjectBigArrayBigList<>();

    positions.add(PVector3D.of(1.0, 1.0, 1.0));
    normals.add(PVector3D.of(1.0, 1.0, 1.0));
    uvs.add(PVector2D.of(1.0, 1.0));
    tangents.add(PVector4D.of(1.0, 1.0, 1.0, 1.0));
    bitangents.add(PVector3D.of(1.0, 1.0, 1.0));

    vertices.add(R2MeshTangentsVertex.of(1L, 0L, 0L, 0L, 0L));

    this.expected.expect(R2MeshExceptionMissingPosition.class);
    this.expected.expectMessage(new StringStartsWith("1"));
    R2MeshTangents.of(
      positions,
      normals,
      bitangents,
      tangents,
      uvs,
      vertices,
      triangles);
  }

  @Test
  public void testBuildNoSuchNormal()
  {
    final ObjectBigArrayBigList<R2MeshTangentsVertex> vertices =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> positions =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> normals =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector2D<R2SpaceTextureType>> uvs =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector4D<R2SpaceObjectType>> tangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> bitangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<R2MeshTriangle> triangles =
      new ObjectBigArrayBigList<>();

    positions.add(PVector3D.of(1.0, 1.0, 1.0));
    normals.add(PVector3D.of(1.0, 1.0, 1.0));
    uvs.add(PVector2D.of(1.0, 1.0));
    tangents.add(PVector4D.of(1.0, 1.0, 1.0, 1.0));
    bitangents.add(PVector3D.of(1.0, 1.0, 1.0));

    vertices.add(R2MeshTangentsVertex.of(0L, 1L, 0L, 0L, 0L));

    this.expected.expect(R2MeshExceptionMissingNormal.class);
    this.expected.expectMessage(new StringStartsWith("1"));
    R2MeshTangents.of(
      positions,
      normals,
      bitangents,
      tangents,
      uvs,
      vertices,
      triangles);
  }

  @Test
  public void testBuildNoSuchUV()
  {
    final ObjectBigArrayBigList<R2MeshTangentsVertex> vertices =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> positions =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> normals =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector2D<R2SpaceTextureType>> uvs =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector4D<R2SpaceObjectType>> tangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> bitangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<R2MeshTriangle> triangles =
      new ObjectBigArrayBigList<>();

    positions.add(PVector3D.of(1.0, 1.0, 1.0));
    normals.add(PVector3D.of(1.0, 1.0, 1.0));
    uvs.add(PVector2D.of(1.0, 1.0));
    tangents.add(PVector4D.of(1.0, 1.0, 1.0, 1.0));
    bitangents.add(PVector3D.of(1.0, 1.0, 1.0));

    vertices.add(R2MeshTangentsVertex.of(0L, 0L, 0L, 0L, 1L));

    this.expected.expect(R2MeshExceptionMissingUV.class);
    this.expected.expectMessage(new StringStartsWith("1"));
    R2MeshTangents.of(
      positions,
      normals,
      bitangents,
      tangents,
      uvs,
      vertices,
      triangles);
  }

  @Test
  public void testBuildNoSuchBitangents()
  {
    final ObjectBigArrayBigList<R2MeshTangentsVertex> vertices =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> positions =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> normals =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector2D<R2SpaceTextureType>> uvs =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector4D<R2SpaceObjectType>> tangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> bitangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<R2MeshTriangle> triangles =
      new ObjectBigArrayBigList<>();

    positions.add(PVector3D.of(1.0, 1.0, 1.0));
    normals.add(PVector3D.of(1.0, 1.0, 1.0));
    uvs.add(PVector2D.of(1.0, 1.0));
    tangents.add(PVector4D.of(1.0, 1.0, 1.0, 1.0));
    bitangents.add(PVector3D.of(1.0, 1.0, 1.0));

    vertices.add(R2MeshTangentsVertex.of(0L, 0L, 0L, 1L, 0L));

    this.expected.expect(R2MeshExceptionMissingBitangent.class);
    this.expected.expectMessage(new StringStartsWith("1"));
    R2MeshTangents.of(
      positions,
      normals,
      bitangents,
      tangents,
      uvs,
      vertices,
      triangles);
  }

  @Test
  public void testBuildNoSuchTangents()
  {
    final ObjectBigArrayBigList<R2MeshTangentsVertex> vertices =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> positions =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> normals =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector2D<R2SpaceTextureType>> uvs =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector4D<R2SpaceObjectType>> tangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> bitangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<R2MeshTriangle> triangles =
      new ObjectBigArrayBigList<>();

    positions.add(PVector3D.of(1.0, 1.0, 1.0));
    normals.add(PVector3D.of(1.0, 1.0, 1.0));
    uvs.add(PVector2D.of(1.0, 1.0));
    tangents.add(PVector4D.of(1.0, 1.0, 1.0, 1.0));
    bitangents.add(PVector3D.of(1.0, 1.0, 1.0));

    vertices.add(R2MeshTangentsVertex.of(0L, 0L, 1L, 0L, 0L));

    this.expected.expect(R2MeshExceptionMissingTangent.class);
    this.expected.expectMessage(new StringStartsWith("1"));
    R2MeshTangents.of(
      positions,
      normals,
      bitangents,
      tangents,
      uvs,
      vertices,
      triangles);
  }

  @Test
  public void testBuildTriangleNoSuchV0()
  {
    final ObjectBigArrayBigList<R2MeshTangentsVertex> vertices =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> positions =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> normals =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector2D<R2SpaceTextureType>> uvs =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector4D<R2SpaceObjectType>> tangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> bitangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<R2MeshTriangle> triangles =
      new ObjectBigArrayBigList<>();

    positions.add(PVector3D.of(1.0, 1.0, 1.0));
    normals.add(PVector3D.of(1.0, 1.0, 1.0));
    uvs.add(PVector2D.of(1.0, 1.0));
    tangents.add(PVector4D.of(1.0, 1.0, 1.0, 1.0));
    bitangents.add(PVector3D.of(1.0, 1.0, 1.0));

    vertices.add(R2MeshTangentsVertex.of(0L, 0L, 0L, 0L, 0L));
    vertices.add(R2MeshTangentsVertex.of(0L, 0L, 0L, 0L, 0L));
    vertices.add(R2MeshTangentsVertex.of(0L, 0L, 0L, 0L, 0L));
    triangles.add(R2MeshTriangle.of(3L, 1L, 2L));

    this.expected.expect(R2MeshExceptionMissingVertex.class);
    this.expected.expectMessage(new StringStartsWith("Vertex 0: 3"));
    R2MeshTangents.of(
      positions,
      normals,
      bitangents,
      tangents,
      uvs,
      vertices,
      triangles);
  }

  @Test
  public void testBuildTriangleNoSuchV1()
  {
    final ObjectBigArrayBigList<R2MeshTangentsVertex> vertices =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> positions =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> normals =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector2D<R2SpaceTextureType>> uvs =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector4D<R2SpaceObjectType>> tangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> bitangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<R2MeshTriangle> triangles =
      new ObjectBigArrayBigList<>();

    positions.add(PVector3D.of(1.0, 1.0, 1.0));
    normals.add(PVector3D.of(1.0, 1.0, 1.0));
    uvs.add(PVector2D.of(1.0, 1.0));
    tangents.add(PVector4D.of(1.0, 1.0, 1.0, 1.0));
    bitangents.add(PVector3D.of(1.0, 1.0, 1.0));

    vertices.add(R2MeshTangentsVertex.of(0L, 0L, 0L, 0L, 0L));
    vertices.add(R2MeshTangentsVertex.of(0L, 0L, 0L, 0L, 0L));
    vertices.add(R2MeshTangentsVertex.of(0L, 0L, 0L, 0L, 0L));
    triangles.add(R2MeshTriangle.of(0L, 3L, 2L));

    this.expected.expect(R2MeshExceptionMissingVertex.class);
    this.expected.expectMessage(new StringStartsWith("Vertex 1: 3"));
    R2MeshTangents.of(
      positions,
      normals,
      bitangents,
      tangents,
      uvs,
      vertices,
      triangles);
  }

  @Test
  public void testBuildTriangleNoSuchV2()
  {
    final ObjectBigArrayBigList<R2MeshTangentsVertex> vertices =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> positions =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> normals =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector2D<R2SpaceTextureType>> uvs =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector4D<R2SpaceObjectType>> tangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> bitangents =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<R2MeshTriangle> triangles =
      new ObjectBigArrayBigList<>();

    positions.add(PVector3D.of(1.0, 1.0, 1.0));
    normals.add(PVector3D.of(1.0, 1.0, 1.0));
    uvs.add(PVector2D.of(1.0, 1.0));
    tangents.add(PVector4D.of(1.0, 1.0, 1.0, 1.0));
    bitangents.add(PVector3D.of(1.0, 1.0, 1.0));

    vertices.add(R2MeshTangentsVertex.of(0L, 0L, 0L, 0L, 0L));
    vertices.add(R2MeshTangentsVertex.of(0L, 0L, 0L, 0L, 0L));
    vertices.add(R2MeshTangentsVertex.of(0L, 0L, 0L, 0L, 0L));
    triangles.add(R2MeshTriangle.of(0L, 1L, 3L));

    this.expected.expect(R2MeshExceptionMissingVertex.class);
    this.expected.expectMessage(new StringStartsWith("Vertex 2: 3"));
    R2MeshTangents.of(
      positions,
      normals,
      bitangents,
      tangents,
      uvs,
      vertices,
      triangles);
  }


  @Test
  public void testGenerateTangentsRH()
  {
    final ObjectBigArrayBigList<R2MeshBasicVertex> vertices =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> positions =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> normals =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector2D<R2SpaceTextureType>> uvs =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<R2MeshTriangle> triangles =
      new ObjectBigArrayBigList<>();

    positions.add(PVector3D.of(0.0, 0.0, 0.0));
    normals.add(PVector3D.of(0.0, 0.0, 1.0));
    uvs.add(PVector2D.of(0.0, 0.0));

    final long p0 = positions.size64() - 1L;
    final long n0 = normals.size64() - 1L;
    final long u0 = uvs.size64() - 1L;

    positions.add(PVector3D.of(0.0, 1.0, 0.0));
    normals.add(PVector3D.of(0.0, 0.0, 1.0));
    uvs.add(PVector2D.of(0.0, 1.0));

    final long p1 = positions.size64() - 1L;
    final long n1 = normals.size64() - 1L;
    final long u1 = uvs.size64() - 1L;

    positions.add(PVector3D.of(1.0, 1.0, 0.0));
    normals.add(PVector3D.of(0.0, 0.0, 1.0));
    uvs.add(PVector2D.of(1.0, 1.0));

    final long p2 = positions.size64() - 1L;
    final long n2 = normals.size64() - 1L;
    final long u2 = uvs.size64() - 1L;

    vertices.add(R2MeshBasicVertex.of(p0, n0, u0));
    final long v0 = vertices.size64() - 1L;
    vertices.add(R2MeshBasicVertex.of(p1, n1, u1));
    final long v1 = vertices.size64() - 1L;
    vertices.add(R2MeshBasicVertex.of(p2, n2, u2));
    final long v2 = vertices.size64() - 1L;

    triangles.add(R2MeshTriangle.of(v0, v1, v2));
    final long b_tri_i = triangles.size64() - 1L;

    final R2MeshBasic mb =
      R2MeshBasic.of(positions, normals, uvs, vertices, triangles);
    final R2MeshTriangle b_tri = triangles.get(0L);
    final R2MeshTangents mtb =
      R2MeshTangentsGenerator.generateTangents(mb);

    final BigList<PVector3D<R2SpaceObjectType>> tp = mtb.positions();
    final BigList<PVector3D<R2SpaceObjectType>> tn = mtb.normals();
    final BigList<PVector2D<R2SpaceTextureType>> tu = mtb.uvs();
    final BigList<PVector4D<R2SpaceObjectType>> tt = mtb.tangents();
    final BigList<PVector3D<R2SpaceObjectType>> tb = mtb.bitangents();
    final BigList<R2MeshTangentsVertex> tv = mtb.vertices();

    final R2MeshTriangle t_tri = mtb.triangles().get(b_tri_i);

    Assert.assertEquals(PVector3D.of(0.0, 0.0, 0.0), tp.get(p0));
    Assert.assertEquals(PVector3D.of(0.0, 0.0, 1.0), tn.get(n0));
    Assert.assertEquals(PVector2D.of(0.0, 0.0), tu.get(u0));

    Assert.assertEquals(PVector3D.of(0.0, 1.0, 0.0), tp.get(p1));
    Assert.assertEquals(PVector3D.of(0.0, 0.0, 1.0), tn.get(n1));
    Assert.assertEquals(PVector2D.of(0.0, 1.0), tu.get(u1));

    Assert.assertEquals(PVector3D.of(1.0, 1.0, 0.0), tp.get(p2));
    Assert.assertEquals(PVector3D.of(0.0, 0.0, 1.0), tn.get(n2));
    Assert.assertEquals(PVector2D.of(1.0, 1.0), tu.get(u2));

    Assert.assertEquals(b_tri, t_tri);

    {
      final R2MeshTangentsVertex tv_0 = tv.get(t_tri.v0());
      final R2MeshTangentsVertex tv_1 = tv.get(t_tri.v1());
      final R2MeshTangentsVertex tv_2 = tv.get(t_tri.v2());

      final PVector3D<R2SpaceObjectType> n_0 = tn.get(tv_0.normalIndex());
      final PVector3D<R2SpaceObjectType> n_1 = tn.get(tv_1.normalIndex());
      final PVector3D<R2SpaceObjectType> n_2 = tn.get(tv_2.normalIndex());

      final PVector4D<R2SpaceObjectType> t_0 = tt.get(tv_0.tangentIndex());
      final PVector4D<R2SpaceObjectType> t_1 = tt.get(tv_1.tangentIndex());
      final PVector4D<R2SpaceObjectType> t_2 = tt.get(tv_2.tangentIndex());

      final PVector3D<R2SpaceObjectType> b_0 =
        tb.get(tv_0.bitangentIndex());
      final PVector3D<R2SpaceObjectType> b_1 =
        tb.get(tv_1.bitangentIndex());
      final PVector3D<R2SpaceObjectType> b_2 =
        tb.get(tv_2.bitangentIndex());

      /*
       * Check orthonornmality.
       */

      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(n_0, PVector3D.of(t_0.x(), t_0.y(), t_0.z())),
        0.000001);
      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(n_1, PVector3D.of(t_1.x(), t_1.y(), t_1.z())),
        0.000001);
      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(n_2, PVector3D.of(t_2.x(), t_2.y(), t_2.z())),
        0.000001);

      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(n_0, b_0),
        0.000001);
      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(n_1, b_1),
        0.000001);
      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(n_2, b_2),
        0.000001);

      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(PVector3D.of(t_0.x(), t_0.y(), t_0.z()), b_0),
        0.000001);
      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(PVector3D.of(t_1.x(), t_1.y(), t_1.z()), b_1),
        0.000001);
      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(PVector3D.of(t_2.x(), t_2.y(), t_2.z()), b_2),
        0.000001);

      /*
       * Check values.
       */

      Assert.assertEquals(PVector4D.of(1.0, 0.0, 0.0, 1.0), t_0);
      Assert.assertEquals(PVector4D.of(1.0, 0.0, 0.0, 1.0), t_1);
      Assert.assertEquals(PVector4D.of(1.0, 0.0, 0.0, 1.0), t_2);

      Assert.assertEquals(PVector3D.of(0.0, 1.0, 0.0), b_0);
      Assert.assertEquals(PVector3D.of(0.0, 1.0, 0.0), b_1);
      Assert.assertEquals(PVector3D.of(0.0, 1.0, 0.0), b_2);
    }
  }

  @Test
  public void testGenerateTangentsLH()
  {
    final ObjectBigArrayBigList<R2MeshBasicVertex> vertices =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> positions =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector3D<R2SpaceObjectType>> normals =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<PVector2D<R2SpaceTextureType>> uvs =
      new ObjectBigArrayBigList<>();
    final ObjectBigArrayBigList<R2MeshTriangle> triangles =
      new ObjectBigArrayBigList<>();

    positions.add(PVector3D.of(1.0, 1.0, 0.0));
    normals.add(PVector3D.of(0.0, 0.0, -1.0));
    uvs.add(PVector2D.of(1.0, 1.0));

    final long p0 = positions.size64() - 1L;
    final long n0 = normals.size64() - 1L;
    final long u0 = uvs.size64() - 1L;

    positions.add(PVector3D.of(1.0, 0.0, 0.0));
    normals.add(PVector3D.of(0.0, 0.0, -1.0));
    uvs.add(PVector2D.of(1.0, 0.0));

    final long p1 = positions.size64() - 1L;
    final long n1 = normals.size64() - 1L;
    final long u1 = uvs.size64() - 1L;

    positions.add(PVector3D.of(0.0, 0.0, 0.0));
    normals.add(PVector3D.of(0.0, 0.0, -1.0));
    uvs.add(PVector2D.of(0.0, 0.0));

    final long p2 = positions.size64() - 1L;
    final long n2 = normals.size64() - 1L;
    final long u2 = uvs.size64() - 1L;

    vertices.add(R2MeshBasicVertex.of(p0, n0, u0));
    final long v0 = vertices.size64() - 1L;
    vertices.add(R2MeshBasicVertex.of(p1, n1, u1));
    final long v1 = vertices.size64() - 1L;
    vertices.add(R2MeshBasicVertex.of(p2, n2, u2));
    final long v2 = vertices.size64() - 1L;

    triangles.add(R2MeshTriangle.of(v0, v1, v2));
    final long b_tri_i = triangles.size64() - 1L;

    final R2MeshBasic mb =
      R2MeshBasic.of(positions, normals, uvs, vertices, triangles);
    final R2MeshTriangle b_tri = triangles.get(0L);
    final R2MeshTangents mtb =
      R2MeshTangentsGenerator.generateTangents(mb);

    final BigList<PVector3D<R2SpaceObjectType>> tp = mtb.positions();
    final BigList<PVector3D<R2SpaceObjectType>> tn = mtb.normals();
    final BigList<PVector2D<R2SpaceTextureType>> tu = mtb.uvs();
    final BigList<PVector4D<R2SpaceObjectType>> tt = mtb.tangents();
    final BigList<PVector3D<R2SpaceObjectType>> tb = mtb.bitangents();
    final BigList<R2MeshTangentsVertex> tv = mtb.vertices();

    final R2MeshTriangle t_tri = mtb.triangles().get(b_tri_i);

    Assert.assertEquals(PVector3D.of(1.0, 1.0, 0.0), tp.get(p0));
    Assert.assertEquals(PVector3D.of(0.0, 0.0, -1.0), tn.get(n0));
    Assert.assertEquals(PVector2D.of(1.0, 1.0), tu.get(u0));

    Assert.assertEquals(PVector3D.of(1.0, 0.0, 0.0), tp.get(p1));
    Assert.assertEquals(PVector3D.of(0.0, 0.0, -1.0), tn.get(n1));
    Assert.assertEquals(PVector2D.of(1.0, 0.0), tu.get(u1));

    Assert.assertEquals(PVector3D.of(0.0, 0.0, 0.0), tp.get(p2));
    Assert.assertEquals(PVector3D.of(0.0, 0.0, -1.0), tn.get(n2));
    Assert.assertEquals(PVector2D.of(0.0, 0.0), tu.get(u2));

    Assert.assertEquals(b_tri, t_tri);

    {
      final R2MeshTangentsVertex tv_0 = tv.get(t_tri.v0());
      final R2MeshTangentsVertex tv_1 = tv.get(t_tri.v1());
      final R2MeshTangentsVertex tv_2 = tv.get(t_tri.v2());

      final PVector3D<R2SpaceObjectType> n_0 = tn.get(tv_0.normalIndex());
      final PVector3D<R2SpaceObjectType> n_1 = tn.get(tv_1.normalIndex());
      final PVector3D<R2SpaceObjectType> n_2 = tn.get(tv_2.normalIndex());

      final PVector4D<R2SpaceObjectType> t_0 = tt.get(tv_0.tangentIndex());
      final PVector4D<R2SpaceObjectType> t_1 = tt.get(tv_1.tangentIndex());
      final PVector4D<R2SpaceObjectType> t_2 = tt.get(tv_2.tangentIndex());

      final PVector3D<R2SpaceObjectType> b_0 =
        tb.get(tv_0.bitangentIndex());
      final PVector3D<R2SpaceObjectType> b_1 =
        tb.get(tv_1.bitangentIndex());
      final PVector3D<R2SpaceObjectType> b_2 =
        tb.get(tv_2.bitangentIndex());

      /*
       * Check orthonornmality.
       */

      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(n_0, PVector3D.of(t_0.x(), t_0.y(), t_0.z())),
        0.000001);
      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(n_1, PVector3D.of(t_1.x(), t_1.y(), t_1.z())),
        0.000001);
      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(n_2, PVector3D.of(t_2.x(), t_2.y(), t_2.z())),
        0.000001);

      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(n_0, b_0),
        0.000001);
      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(n_1, b_1),
        0.000001);
      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(n_2, b_2),
        0.000001);

      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(PVector3D.of(t_0.x(), t_0.y(), t_0.z()), b_0),
        0.000001);
      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(PVector3D.of(t_1.x(), t_1.y(), t_1.z()), b_1),
        0.000001);
      Assert.assertEquals(
        0.0,
        PVectors3D.dotProduct(PVector3D.of(t_2.x(), t_2.y(), t_2.z()), b_2),
        0.000001);

      /*
       * Check values.
       */

      Assert.assertEquals(PVector4D.of(1.0, 0.0, 0.0, -1.0), t_0);
      Assert.assertEquals(PVector4D.of(1.0, 0.0, 0.0, -1.0), t_1);
      Assert.assertEquals(PVector4D.of(1.0, 0.0, 0.0, -1.0), t_2);

      Assert.assertEquals(PVector3D.of(-0.0, -1.0, -0.0), b_0);
      Assert.assertEquals(PVector3D.of(-0.0, -1.0, -0.0), b_1);
      Assert.assertEquals(PVector3D.of(-0.0, -1.0, -0.0), b_2);
    }
  }
}
