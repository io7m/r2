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

package com.io7m.r2.tests.meshes;

import com.io7m.jtensors.parameterized.PVectorI2D;
import com.io7m.jtensors.parameterized.PVectorI3D;
import com.io7m.jtensors.parameterized.PVectorI4D;
import com.io7m.r2.meshes.R2MeshBasic;
import com.io7m.r2.meshes.R2MeshBasicBuilderType;
import com.io7m.r2.meshes.R2MeshBasicType;
import com.io7m.r2.meshes.R2MeshExceptionMalformedTriangle;
import com.io7m.r2.meshes.R2MeshExceptionMissingBitangent;
import com.io7m.r2.meshes.R2MeshExceptionMissingNormal;
import com.io7m.r2.meshes.R2MeshExceptionMissingPosition;
import com.io7m.r2.meshes.R2MeshExceptionMissingTangent;
import com.io7m.r2.meshes.R2MeshExceptionMissingUV;
import com.io7m.r2.meshes.R2MeshExceptionMissingVertex;
import com.io7m.r2.meshes.R2MeshTangents;
import com.io7m.r2.meshes.R2MeshTangentsBuilderType;
import com.io7m.r2.meshes.R2MeshTangentsType;
import com.io7m.r2.meshes.R2MeshTangentsVertexType;
import com.io7m.r2.meshes.R2MeshTriangleType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import it.unimi.dsi.fastutil.BigList;
import org.hamcrest.core.StringContains;
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
    final R2MeshTangentsBuilderType b = R2MeshTangents.newBuilder(0L, 0L);
    final R2MeshTangentsType m = b.build();

    Assert.assertEquals(0L, m.getPositions().size64());
    Assert.assertEquals(0L, m.getNormals().size64());
    Assert.assertEquals(0L, m.getUVs().size64());
    Assert.assertEquals(0L, m.getTangents().size64());
    Assert.assertEquals(0L, m.getBitangents().size64());
    Assert.assertEquals(0L, m.getVertices().size64());
    Assert.assertEquals(0L, m.getTriangles().size64());
  }

  @Test
  public void testBuildNoSuchPosition()
  {
    final R2MeshTangentsBuilderType b = R2MeshTangents.newBuilder(0L, 0L);
    this.expected.expect(R2MeshExceptionMissingPosition.class);
    this.expected.expectMessage(new StringStartsWith("0"));
    b.addVertex(0L, 0L, 0L, 0L, 0L);
  }

  @Test
  public void testBuildNoSuchNormal()
  {
    final R2MeshTangentsBuilderType b = R2MeshTangents.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));
    b.addTangent(new PVectorI4D<>(0.0, 0.0, 0.0, 1.0));
    b.addBitangent(new PVectorI3D<>(1.0, 0.0, 0.0));

    this.expected.expect(R2MeshExceptionMissingNormal.class);
    this.expected.expectMessage(new StringStartsWith("0"));
    b.addVertex(0L, 0L, 0L, 0L, 0L);
  }

  @Test
  public void testBuildNoSuchUV()
  {
    final R2MeshTangentsBuilderType b = R2MeshTangents.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addTangent(new PVectorI4D<>(0.0, 0.0, 0.0, 1.0));
    b.addBitangent(new PVectorI3D<>(1.0, 0.0, 0.0));

    this.expected.expect(R2MeshExceptionMissingUV.class);
    this.expected.expectMessage(new StringStartsWith("0"));
    b.addVertex(0L, 0L, 0L, 0L, 0L);
  }

  @Test
  public void testBuildNoSuchTangent()
  {
    final R2MeshTangentsBuilderType b = R2MeshTangents.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));
    b.addBitangent(new PVectorI3D<>(1.0, 0.0, 0.0));

    this.expected.expect(R2MeshExceptionMissingTangent.class);
    this.expected.expectMessage(new StringStartsWith("0"));
    b.addVertex(0L, 0L, 0L, 0L, 0L);
  }

  @Test
  public void testBuildNoSuchBitangent()
  {
    final R2MeshTangentsBuilderType b = R2MeshTangents.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));
    b.addTangent(new PVectorI4D<>(0.0, 0.0, 0.0, 1.0));

    this.expected.expect(R2MeshExceptionMissingBitangent.class);
    this.expected.expectMessage(new StringStartsWith("0"));
    b.addVertex(0L, 0L, 0L, 0L, 0L);
  }

  @Test
  public void testBuildVertex0()
  {
    final R2MeshTangentsBuilderType b = R2MeshTangents.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));
    b.addTangent(new PVectorI4D<>(0.0, 0.0, 0.0, 1.0));
    b.addBitangent(new PVectorI3D<>(1.0, 0.0, 0.0));

    final long v = b.addVertex(0L, 0L, 0L, 0L, 0L);
    Assert.assertEquals(0L, v);
  }

  @Test
  public void testBuildTriangleNoSuchV0()
  {
    final R2MeshTangentsBuilderType b = R2MeshTangents.newBuilder(0L, 0L);
    this.expected.expect(R2MeshExceptionMissingVertex.class);
    this.expected.expectMessage(new StringStartsWith("Vertex 0: 0"));
    b.addTriangle(0L, 0L, 0L);
  }

  @Test
  public void testBuildTriangleNoSuchV1()
  {
    final R2MeshTangentsBuilderType b = R2MeshTangents.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));
    b.addTangent(new PVectorI4D<>(0.0, 0.0, 0.0, 1.0));
    b.addBitangent(new PVectorI3D<>(1.0, 0.0, 0.0));

    final long v = b.addVertex(0L, 0L, 0L, 0L, 0L);
    this.expected.expect(R2MeshExceptionMissingVertex.class);
    this.expected.expectMessage(new StringStartsWith("Vertex 1: 1"));
    b.addTriangle(v, v + 1L, v);
  }

  @Test
  public void testBuildTriangleNoSuchV2()
  {
    final R2MeshTangentsBuilderType b = R2MeshTangents.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));
    b.addTangent(new PVectorI4D<>(0.0, 0.0, 0.0, 1.0));
    b.addBitangent(new PVectorI3D<>(1.0, 0.0, 0.0));

    final long v = b.addVertex(0L, 0L, 0L, 0L, 0L);
    this.expected.expect(R2MeshExceptionMissingVertex.class);
    this.expected.expectMessage(new StringStartsWith("Vertex 2: 1"));
    b.addTriangle(v, v, v + 1L);
  }

  @Test
  public void testBuildTriangle0()
  {
    final R2MeshTangentsBuilderType b = R2MeshTangents.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));
    b.addTangent(new PVectorI4D<>(0.0, 0.0, 0.0, 1.0));
    b.addBitangent(new PVectorI3D<>(1.0, 0.0, 0.0));

    final long v0 = b.addVertex(0L, 0L, 0L, 0L, 0L);
    final long v1 = b.addVertex(0L, 0L, 0L, 0L, 0L);
    final long v2 = b.addVertex(0L, 0L, 0L, 0L, 0L);
    final long t = b.addTriangle(v0, v1, v2);
    Assert.assertEquals(0L, t);
  }

  @Test
  public void testBuildTriangleMalformed0()
  {
    final R2MeshTangentsBuilderType b = R2MeshTangents.newBuilder(0L, 0L);

    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));
    b.addTangent(new PVectorI4D<>(0.0, 0.0, 0.0, 1.0));
    b.addBitangent(new PVectorI3D<>(1.0, 0.0, 0.0));

    final long v0 = b.addVertex(0L, 0L, 0L, 0L, 0L);
    final long v1 = b.addVertex(0L, 0L, 0L, 0L, 0L);

    this.expected.expect(R2MeshExceptionMalformedTriangle.class);
    this.expected.expectMessage(new StringContains("Triangle: 0"));
    b.addTriangle(v0, v0, v1);
  }

  @Test
  public void testBuildTriangleMalformed1()
  {
    final R2MeshTangentsBuilderType b = R2MeshTangents.newBuilder(0L, 0L);

    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));
    b.addTangent(new PVectorI4D<>(0.0, 0.0, 0.0, 1.0));
    b.addBitangent(new PVectorI3D<>(1.0, 0.0, 0.0));

    final long v0 = b.addVertex(0L, 0L, 0L, 0L, 0L);
    final long v1 = b.addVertex(0L, 0L, 0L, 0L, 0L);

    this.expected.expect(R2MeshExceptionMalformedTriangle.class);
    this.expected.expectMessage(new StringContains("Triangle: 0"));
    b.addTriangle(v0, v1, v1);
  }

  @Test
  public void testBuildTriangleMalformed2()
  {
    final R2MeshTangentsBuilderType b = R2MeshTangents.newBuilder(0L, 0L);

    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));
    b.addTangent(new PVectorI4D<>(0.0, 0.0, 0.0, 1.0));
    b.addBitangent(new PVectorI3D<>(1.0, 0.0, 0.0));

    final long v0 = b.addVertex(0L, 0L, 0L, 0L, 0L);
    final long v1 = b.addVertex(0L, 0L, 0L, 0L, 0L);

    this.expected.expect(R2MeshExceptionMalformedTriangle.class);
    this.expected.expectMessage(new StringContains("Triangle: 0"));
    b.addTriangle(v0, v1, v0);
  }

  @Test
  public void testBuildResetEmpty()
  {
    final R2MeshTangentsBuilderType b = R2MeshTangents.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));
    b.addTangent(new PVectorI4D<>(0.0, 0.0, 0.0, 1.0));
    b.addBitangent(new PVectorI3D<>(1.0, 0.0, 0.0));

    final long v0 = b.addVertex(0L, 0L, 0L, 0L, 0L);
    final long v1 = b.addVertex(0L, 0L, 0L, 0L, 0L);
    final long v2 = b.addVertex(0L, 0L, 0L, 0L, 0L);
    final long t = b.addTriangle(v0, v1, v2);
    Assert.assertEquals(0L, t);

    b.reset();

    final R2MeshTangentsType m = b.build();
    Assert.assertEquals(0L, m.getPositions().size64());
    Assert.assertEquals(0L, m.getNormals().size64());
    Assert.assertEquals(0L, m.getUVs().size64());
    Assert.assertEquals(0L, m.getVertices().size64());
    Assert.assertEquals(0L, m.getTriangles().size64());
  }

  @Test
  public void testGenerateTangentsRH()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(3L, 1L);

    final long p0 = b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    final long n0 = b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    final long u0 = b.addUV(new PVectorI2D<>(0.0, 0.0));

    final long p1 = b.addPosition(new PVectorI3D<>(0.0, 1.0, 0.0));
    final long n1 = b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    final long u1 = b.addUV(new PVectorI2D<>(0.0, 1.0));

    final long p2 = b.addPosition(new PVectorI3D<>(1.0, 1.0, 0.0));
    final long n2 = b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    final long u2 = b.addUV(new PVectorI2D<>(1.0, 1.0));

    final long v0 = b.addVertex(p0, n0, u0);
    final long v1 = b.addVertex(p1, n1, u1);
    final long v2 = b.addVertex(p2, n2, u2);

    final long b_tri_i = b.addTriangle(v0, v1, v2);

    final R2MeshBasicType mb = b.build();
    final R2MeshTriangleType b_tri = mb.getTriangles().get(b_tri_i);
    final R2MeshTangentsType mtb = R2MeshTangents.generateTangents(mb);

    final BigList<PVectorI3D<R2SpaceObjectType>> tp = mtb.getPositions();
    final BigList<PVectorI3D<R2SpaceObjectType>> tn = mtb.getNormals();
    final BigList<PVectorI2D<R2SpaceTextureType>> tu = mtb.getUVs();
    final BigList<PVectorI4D<R2SpaceObjectType>> tt = mtb.getTangents();
    final BigList<PVectorI3D<R2SpaceObjectType>> tb = mtb.getBitangents();
    final BigList<R2MeshTangentsVertexType> tv = mtb.getVertices();

    final R2MeshTriangleType t_tri = mtb.getTriangles().get(b_tri_i);

    Assert.assertEquals(new PVectorI3D<>(0.0, 0.0, 0.0), tp.get(p0));
    Assert.assertEquals(new PVectorI3D<>(0.0, 0.0, 1.0), tn.get(n0));
    Assert.assertEquals(new PVectorI2D<>(0.0, 0.0), tu.get(u0));

    Assert.assertEquals(new PVectorI3D<>(0.0, 1.0, 0.0), tp.get(p1));
    Assert.assertEquals(new PVectorI3D<>(0.0, 0.0, 1.0), tn.get(n1));
    Assert.assertEquals(new PVectorI2D<>(0.0, 1.0), tu.get(u1));

    Assert.assertEquals(new PVectorI3D<>(1.0, 1.0, 0.0), tp.get(p2));
    Assert.assertEquals(new PVectorI3D<>(0.0, 0.0, 1.0), tn.get(n2));
    Assert.assertEquals(new PVectorI2D<>(1.0, 1.0), tu.get(u2));

    Assert.assertEquals(b_tri, t_tri);

    {
      final R2MeshTangentsVertexType tv_0 = tv.get(t_tri.getV0());
      final R2MeshTangentsVertexType tv_1 = tv.get(t_tri.getV1());
      final R2MeshTangentsVertexType tv_2 = tv.get(t_tri.getV2());

      final PVectorI3D<R2SpaceObjectType> n_0 = tn.get(tv_0.getNormalIndex());
      final PVectorI3D<R2SpaceObjectType> n_1 = tn.get(tv_1.getNormalIndex());
      final PVectorI3D<R2SpaceObjectType> n_2 = tn.get(tv_2.getNormalIndex());

      final PVectorI4D<R2SpaceObjectType> t_0 = tt.get(tv_0.getTangentIndex());
      final PVectorI4D<R2SpaceObjectType> t_1 = tt.get(tv_1.getTangentIndex());
      final PVectorI4D<R2SpaceObjectType> t_2 = tt.get(tv_2.getTangentIndex());

      final PVectorI3D<R2SpaceObjectType> b_0 =
        tb.get(tv_0.getBitangentIndex());
      final PVectorI3D<R2SpaceObjectType> b_1 =
        tb.get(tv_1.getBitangentIndex());
      final PVectorI3D<R2SpaceObjectType> b_2 =
        tb.get(tv_2.getBitangentIndex());

      /**
       * Check orthonornmality.
       */

      Assert.assertEquals(0.0, PVectorI3D.dotProduct(n_0, t_0), 0.000001);
      Assert.assertEquals(0.0, PVectorI3D.dotProduct(n_1, t_1), 0.000001);
      Assert.assertEquals(0.0, PVectorI3D.dotProduct(n_2, t_2), 0.000001);

      Assert.assertEquals(0.0, PVectorI3D.dotProduct(n_0, b_0), 0.000001);
      Assert.assertEquals(0.0, PVectorI3D.dotProduct(n_1, b_1), 0.000001);
      Assert.assertEquals(0.0, PVectorI3D.dotProduct(n_2, b_2), 0.000001);

      Assert.assertEquals(0.0, PVectorI3D.dotProduct(t_0, b_0), 0.000001);
      Assert.assertEquals(0.0, PVectorI3D.dotProduct(t_1, b_1), 0.000001);
      Assert.assertEquals(0.0, PVectorI3D.dotProduct(t_2, b_2), 0.000001);

      /**
       * Check values.
       */

      Assert.assertEquals(new PVectorI4D<>(1.0, 0.0, 0.0, 1.0), t_0);
      Assert.assertEquals(new PVectorI4D<>(1.0, 0.0, 0.0, 1.0), t_1);
      Assert.assertEquals(new PVectorI4D<>(1.0, 0.0, 0.0, 1.0), t_2);

      Assert.assertEquals(new PVectorI3D<>(0.0, 1.0, 0.0), b_0);
      Assert.assertEquals(new PVectorI3D<>(0.0, 1.0, 0.0), b_1);
      Assert.assertEquals(new PVectorI3D<>(0.0, 1.0, 0.0), b_2);
    }
  }

  @Test
  public void testGenerateTangentsLH()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(3L, 1L);

    final long p0 = b.addPosition(new PVectorI3D<>(1.0, 1.0, 0.0));
    final long n0 = b.addNormal(new PVectorI3D<>(0.0, 0.0, -1.0));
    final long u0 = b.addUV(new PVectorI2D<>(1.0, 1.0));

    final long p1 = b.addPosition(new PVectorI3D<>(1.0, 0.0, 0.0));
    final long n1 = b.addNormal(new PVectorI3D<>(0.0, 0.0, -1.0));
    final long u1 = b.addUV(new PVectorI2D<>(1.0, 0.0));

    final long p2 = b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    final long n2 = b.addNormal(new PVectorI3D<>(0.0, 0.0, -1.0));
    final long u2 = b.addUV(new PVectorI2D<>(0.0, 0.0));

    final long v0 = b.addVertex(p0, n0, u0);
    final long v1 = b.addVertex(p1, n1, u1);
    final long v2 = b.addVertex(p2, n2, u2);

    final long b_tri_i = b.addTriangle(v0, v1, v2);

    final R2MeshBasicType mb = b.build();
    final R2MeshTriangleType b_tri = mb.getTriangles().get(b_tri_i);
    final R2MeshTangentsType mtb = R2MeshTangents.generateTangents(mb);

    final BigList<PVectorI3D<R2SpaceObjectType>> tp = mtb.getPositions();
    final BigList<PVectorI3D<R2SpaceObjectType>> tn = mtb.getNormals();
    final BigList<PVectorI2D<R2SpaceTextureType>> tu = mtb.getUVs();
    final BigList<PVectorI4D<R2SpaceObjectType>> tt = mtb.getTangents();
    final BigList<PVectorI3D<R2SpaceObjectType>> tb = mtb.getBitangents();
    final BigList<R2MeshTangentsVertexType> tv = mtb.getVertices();

    final R2MeshTriangleType t_tri = mtb.getTriangles().get(b_tri_i);

    Assert.assertEquals(new PVectorI3D<>(1.0, 1.0, 0.0), tp.get(p0));
    Assert.assertEquals(new PVectorI3D<>(0.0, 0.0, -1.0), tn.get(n0));
    Assert.assertEquals(new PVectorI2D<>(1.0, 1.0), tu.get(u0));

    Assert.assertEquals(new PVectorI3D<>(1.0, 0.0, 0.0), tp.get(p1));
    Assert.assertEquals(new PVectorI3D<>(0.0, 0.0, -1.0), tn.get(n1));
    Assert.assertEquals(new PVectorI2D<>(1.0, 0.0), tu.get(u1));

    Assert.assertEquals(new PVectorI3D<>(0.0, 0.0, 0.0), tp.get(p2));
    Assert.assertEquals(new PVectorI3D<>(0.0, 0.0, -1.0), tn.get(n2));
    Assert.assertEquals(new PVectorI2D<>(0.0, 0.0), tu.get(u2));

    Assert.assertEquals(b_tri, t_tri);

    {
      final R2MeshTangentsVertexType tv_0 = tv.get(t_tri.getV0());
      final R2MeshTangentsVertexType tv_1 = tv.get(t_tri.getV1());
      final R2MeshTangentsVertexType tv_2 = tv.get(t_tri.getV2());

      final PVectorI3D<R2SpaceObjectType> n_0 = tn.get(tv_0.getNormalIndex());
      final PVectorI3D<R2SpaceObjectType> n_1 = tn.get(tv_1.getNormalIndex());
      final PVectorI3D<R2SpaceObjectType> n_2 = tn.get(tv_2.getNormalIndex());

      final PVectorI4D<R2SpaceObjectType> t_0 = tt.get(tv_0.getTangentIndex());
      final PVectorI4D<R2SpaceObjectType> t_1 = tt.get(tv_1.getTangentIndex());
      final PVectorI4D<R2SpaceObjectType> t_2 = tt.get(tv_2.getTangentIndex());

      final PVectorI3D<R2SpaceObjectType> b_0 =
        tb.get(tv_0.getBitangentIndex());
      final PVectorI3D<R2SpaceObjectType> b_1 =
        tb.get(tv_1.getBitangentIndex());
      final PVectorI3D<R2SpaceObjectType> b_2 =
        tb.get(tv_2.getBitangentIndex());

      /**
       * Check orthonornmality.
       */

      Assert.assertEquals(0.0, PVectorI3D.dotProduct(n_0, t_0), 0.000001);
      Assert.assertEquals(0.0, PVectorI3D.dotProduct(n_1, t_1), 0.000001);
      Assert.assertEquals(0.0, PVectorI3D.dotProduct(n_2, t_2), 0.000001);

      Assert.assertEquals(0.0, PVectorI3D.dotProduct(n_0, b_0), 0.000001);
      Assert.assertEquals(0.0, PVectorI3D.dotProduct(n_1, b_1), 0.000001);
      Assert.assertEquals(0.0, PVectorI3D.dotProduct(n_2, b_2), 0.000001);

      Assert.assertEquals(0.0, PVectorI3D.dotProduct(t_0, b_0), 0.000001);
      Assert.assertEquals(0.0, PVectorI3D.dotProduct(t_1, b_1), 0.000001);
      Assert.assertEquals(0.0, PVectorI3D.dotProduct(t_2, b_2), 0.000001);

      /**
       * Check values.
       */

      Assert.assertEquals(new PVectorI4D<>(1.0, 0.0, 0.0, -1.0), t_0);
      Assert.assertEquals(new PVectorI4D<>(1.0, 0.0, 0.0, -1.0), t_1);
      Assert.assertEquals(new PVectorI4D<>(1.0, 0.0, 0.0, -1.0), t_2);

      Assert.assertEquals(new PVectorI3D<>(-0.0, -1.0, -0.0), b_0);
      Assert.assertEquals(new PVectorI3D<>(-0.0, -1.0, -0.0), b_1);
      Assert.assertEquals(new PVectorI3D<>(-0.0, -1.0, -0.0), b_2);
    }
  }
}
