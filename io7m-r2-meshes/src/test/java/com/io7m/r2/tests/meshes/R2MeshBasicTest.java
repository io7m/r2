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
import com.io7m.r2.meshes.R2MeshBasic;
import com.io7m.r2.meshes.R2MeshBasicBuilderType;
import com.io7m.r2.meshes.R2MeshBasicType;
import org.hamcrest.core.StringStartsWith;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.NoSuchElementException;

public final class R2MeshBasicTest
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test public void testBuildEmpty()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    final R2MeshBasicType m = b.build();

    Assert.assertEquals(0L, m.getPositions().size64());
    Assert.assertEquals(0L, m.getNormals().size64());
    Assert.assertEquals(0L, m.getUVs().size64());
    Assert.assertEquals(0L, m.getVertices().size64());
    Assert.assertEquals(0L, m.getTriangles().size64());
  }

  @Test public void testBuildNoSuchPosition()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    this.expected.expect(NoSuchElementException.class);
    this.expected.expectMessage(new StringStartsWith("Position"));
    b.addVertex(0L, 0L, 0L);
  }

  @Test public void testBuildNoSuchNormal()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));

    this.expected.expect(NoSuchElementException.class);
    this.expected.expectMessage(new StringStartsWith("Normal"));
    b.addVertex(0L, 0L, 0L);
  }

  @Test public void testBuildNoSuchUV()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));

    this.expected.expect(NoSuchElementException.class);
    this.expected.expectMessage(new StringStartsWith("UV"));
    b.addVertex(0L, 0L, 0L);
  }

  @Test public void testBuildVertex0()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));

    final long v = b.addVertex(0L, 0L, 0L);
    Assert.assertEquals(0L, v);
  }

  @Test public void testBuildTriangleNoSuchV0()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    this.expected.expect(NoSuchElementException.class);
    this.expected.expectMessage(new StringStartsWith("Vertex 0"));
    b.addTriangle(0L, 0L, 0L);
  }

  @Test public void testBuildTriangleNoSuchV1()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));

    final long v = b.addVertex(0L, 0L, 0L);
    this.expected.expect(NoSuchElementException.class);
    this.expected.expectMessage(new StringStartsWith("Vertex 1"));
    b.addTriangle(v, v + 1L, v);
  }

  @Test public void testBuildTriangleNoSuchV2()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));

    final long v = b.addVertex(0L, 0L, 0L);
    this.expected.expect(NoSuchElementException.class);
    this.expected.expectMessage(new StringStartsWith("Vertex 2"));
    b.addTriangle(v, v, v + 1L);
  }

  @Test public void testBuildTriangle0()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));

    final long v = b.addVertex(0L, 0L, 0L);
    final long t = b.addTriangle(v, v, v);
    Assert.assertEquals(0L, t);
  }

  @Test public void testBuildResetEmpty()
  {
    final R2MeshBasicBuilderType b = R2MeshBasic.newBuilder(0L, 0L);
    b.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0));
    b.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0));
    b.addUV(new PVectorI2D<>(1.0, 0.0));

    final long v = b.addVertex(0L, 0L, 0L);
    final long t = b.addTriangle(v, v, v);
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
