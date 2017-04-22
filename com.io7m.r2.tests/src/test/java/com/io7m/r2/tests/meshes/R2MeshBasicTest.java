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

import com.io7m.jtensors.core.parameterized.vectors.PVector2D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.r2.meshes.R2MeshBasic;
import com.io7m.r2.meshes.R2MeshBasicBuilderType;
import com.io7m.r2.meshes.R2MeshBasicType;
import com.io7m.r2.meshes.R2MeshExceptionMalformedTriangle;
import com.io7m.r2.meshes.R2MeshExceptionMissingNormal;
import com.io7m.r2.meshes.R2MeshExceptionMissingPosition;
import com.io7m.r2.meshes.R2MeshExceptionMissingUV;
import com.io7m.r2.meshes.R2MeshExceptionMissingVertex;
import org.hamcrest.core.StringContains;
import org.hamcrest.core.StringStartsWith;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class R2MeshBasicTest
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test
  public void testBuildEmpty()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    final R2MeshBasicType m = b.build();

    Assert.assertEquals(0L, m.getPositions().size64());
    Assert.assertEquals(0L, m.getNormals().size64());
    Assert.assertEquals(0L, m.getUVs().size64());
    Assert.assertEquals(0L, m.getVertices().size64());
    Assert.assertEquals(0L, m.getTriangles().size64());
  }

  @Test
  public void testBuildNoSuchPosition()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    this.expected.expect(R2MeshExceptionMissingPosition.class);
    this.expected.expectMessage(new StringStartsWith("0"));
    b.addVertex(0L, 0L, 0L);
  }

  @Test
  public void testBuildNoSuchNormal()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    b.addPosition(PVector3D.of(0.0, 0.0, 0.0));

    this.expected.expect(R2MeshExceptionMissingNormal.class);
    this.expected.expectMessage(new StringStartsWith("0"));
    b.addVertex(0L, 0L, 0L);
  }

  @Test
  public void testBuildNoSuchUV()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    b.addPosition(PVector3D.of(0.0, 0.0, 0.0));
    b.addNormal(PVector3D.of(0.0, 0.0, 1.0));

    this.expected.expect(R2MeshExceptionMissingUV.class);
    this.expected.expectMessage(new StringStartsWith("0"));
    b.addVertex(0L, 0L, 0L);
  }

  @Test
  public void testBuildVertex0()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    b.addPosition(PVector3D.of(0.0, 0.0, 0.0));
    b.addNormal(PVector3D.of(0.0, 0.0, 1.0));
    b.addUV(PVector2D.of(1.0, 0.0));

    final long v = b.addVertex(0L, 0L, 0L);
    Assert.assertEquals(0L, v);
  }

  @Test
  public void testBuildTriangleNoSuchV0()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    this.expected.expect(R2MeshExceptionMissingVertex.class);
    this.expected.expectMessage(new StringStartsWith("Vertex 0: 0"));
    b.addTriangle(0L, 0L, 0L);
  }

  @Test
  public void testBuildTriangleNoSuchV1()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    b.addPosition(PVector3D.of(0.0, 0.0, 0.0));
    b.addNormal(PVector3D.of(0.0, 0.0, 1.0));
    b.addUV(PVector2D.of(1.0, 0.0));

    final long v = b.addVertex(0L, 0L, 0L);
    this.expected.expect(R2MeshExceptionMissingVertex.class);
    this.expected.expectMessage(new StringStartsWith("Vertex 1: 1"));
    b.addTriangle(v, v + 1L, v);
  }

  @Test
  public void testBuildTriangleNoSuchV2()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    b.addPosition(PVector3D.of(0.0, 0.0, 0.0));
    b.addNormal(PVector3D.of(0.0, 0.0, 1.0));
    b.addUV(PVector2D.of(1.0, 0.0));

    final long v = b.addVertex(0L, 0L, 0L);
    this.expected.expect(R2MeshExceptionMissingVertex.class);
    this.expected.expectMessage(new StringStartsWith("Vertex 2: 1"));
    b.addTriangle(v, v, v + 1L);
  }

  @Test
  public void testBuildTriangle0()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);

    b.addPosition(PVector3D.of(0.0, 0.0, 0.0));
    b.addNormal(PVector3D.of(0.0, 0.0, 1.0));
    b.addUV(PVector2D.of(1.0, 0.0));

    final long v0 = b.addVertex(0L, 0L, 0L);
    final long v1 = b.addVertex(0L, 0L, 0L);
    final long v2 = b.addVertex(0L, 0L, 0L);
    final long t = b.addTriangle(v0, v1, v2);
    Assert.assertEquals(0L, t);
  }

  @Test
  public void testBuildTriangleMalformed0()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);

    b.addPosition(PVector3D.of(0.0, 0.0, 0.0));
    b.addNormal(PVector3D.of(0.0, 0.0, 1.0));
    b.addUV(PVector2D.of(1.0, 0.0));

    final long v0 = b.addVertex(0L, 0L, 0L);
    final long v1 = b.addVertex(0L, 0L, 0L);

    this.expected.expect(R2MeshExceptionMalformedTriangle.class);
    this.expected.expectMessage(new StringContains("Triangle: 0"));
    b.addTriangle(v0, v0, v1);
  }

  @Test
  public void testBuildTriangleMalformed1()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);

    b.addPosition(PVector3D.of(0.0, 0.0, 0.0));
    b.addNormal(PVector3D.of(0.0, 0.0, 1.0));
    b.addUV(PVector2D.of(1.0, 0.0));

    final long v0 = b.addVertex(0L, 0L, 0L);
    final long v1 = b.addVertex(0L, 0L, 0L);

    this.expected.expect(R2MeshExceptionMalformedTriangle.class);
    this.expected.expectMessage(new StringContains("Triangle: 0"));
    b.addTriangle(v0, v1, v1);
  }

  @Test
  public void testBuildTriangleMalformed2()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);

    b.addPosition(PVector3D.of(0.0, 0.0, 0.0));
    b.addNormal(PVector3D.of(0.0, 0.0, 1.0));
    b.addUV(PVector2D.of(1.0, 0.0));

    final long v0 = b.addVertex(0L, 0L, 0L);
    final long v1 = b.addVertex(0L, 0L, 0L);

    this.expected.expect(R2MeshExceptionMalformedTriangle.class);
    this.expected.expectMessage(new StringContains("Triangle: 0"));
    b.addTriangle(v0, v1, v0);
  }

  @Test
  public void testBuildResetEmpty()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    b.addPosition(PVector3D.of(0.0, 0.0, 0.0));
    b.addNormal(PVector3D.of(0.0, 0.0, 1.0));
    b.addUV(PVector2D.of(1.0, 0.0));

    final long v0 = b.addVertex(0L, 0L, 0L);
    final long v1 = b.addVertex(0L, 0L, 0L);
    final long v2 = b.addVertex(0L, 0L, 0L);
    final long t = b.addTriangle(v0, v1, v2);
    Assert.assertEquals(0L, t);

    b.reset();

    final R2MeshBasicType m = b.build();
    Assert.assertEquals(0L, m.getPositions().size64());
    Assert.assertEquals(0L, m.getNormals().size64());
    Assert.assertEquals(0L, m.getUVs().size64());
    Assert.assertEquals(0L, m.getVertices().size64());
    Assert.assertEquals(0L, m.getTriangles().size64());
  }
}
