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
import com.io7m.r2.meshes.api.R2MeshBasic;
import com.io7m.r2.meshes.api.R2MeshBasicVertex;
import com.io7m.r2.meshes.api.R2MeshExceptionMalformedTriangle;
import com.io7m.r2.meshes.api.R2MeshExceptionMissingNormal;
import com.io7m.r2.meshes.api.R2MeshExceptionMissingPosition;
import com.io7m.r2.meshes.api.R2MeshExceptionMissingUV;
import com.io7m.r2.meshes.api.R2MeshExceptionMissingVertex;
import com.io7m.r2.meshes.api.R2MeshTriangle;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
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
    final R2MeshBasic m = R2MeshBasic.of(
      new ObjectBigArrayBigList<>(),
      new ObjectBigArrayBigList<>(),
      new ObjectBigArrayBigList<>(),
      new ObjectBigArrayBigList<>(),
      new ObjectBigArrayBigList<>());

    Assert.assertEquals(0L, m.positions().size64());
    Assert.assertEquals(0L, m.normals().size64());
    Assert.assertEquals(0L, m.uvs().size64());
    Assert.assertEquals(0L, m.vertices().size64());
    Assert.assertEquals(0L, m.triangles().size64());
  }

  @Test
  public void testBuildNoSuchPosition()
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

    normals.add(PVector3D.of(1.0, 1.0, 1.0));
    uvs.add(PVector2D.of(1.0, 1.0));

    vertices.add(R2MeshBasicVertex.of(1L, 0L, 0L));

    this.expected.expect(R2MeshExceptionMissingPosition.class);
    this.expected.expectMessage(new StringStartsWith("1"));
    R2MeshBasic.of(positions, normals, uvs, vertices, triangles);
  }

  @Test
  public void testBuildNoSuchNormal()
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

    positions.add(PVector3D.of(1.0, 1.0, 1.0));
    uvs.add(PVector2D.of(1.0, 1.0));

    vertices.add(R2MeshBasicVertex.of(0L, 1L, 0L));

    this.expected.expect(R2MeshExceptionMissingNormal.class);
    this.expected.expectMessage(new StringStartsWith("1"));
    R2MeshBasic.of(positions, normals, uvs, vertices, triangles);
  }

  @Test
  public void testBuildNoSuchUV()
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

    positions.add(PVector3D.of(1.0, 1.0, 1.0));
    normals.add(PVector3D.of(1.0, 1.0, 1.0));
    vertices.add(R2MeshBasicVertex.of(0L, 0L, 1L));

    this.expected.expect(R2MeshExceptionMissingUV.class);
    this.expected.expectMessage(new StringStartsWith("1"));
    R2MeshBasic.of(positions, normals, uvs, vertices, triangles);
  }

  @Test
  public void testBuildTriangleNoSuchV0()
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

    positions.add(PVector3D.of(1.0, 1.0, 1.0));
    normals.add(PVector3D.of(1.0, 1.0, 1.0));
    uvs.add(PVector2D.of(1.0, 1.0));
    vertices.add(R2MeshBasicVertex.of(0L, 0L, 0L));
    vertices.add(R2MeshBasicVertex.of(0L, 0L, 0L));
    vertices.add(R2MeshBasicVertex.of(0L, 0L, 0L));
    triangles.add(R2MeshTriangle.of(3L, 1L, 2L));

    this.expected.expect(R2MeshExceptionMissingVertex.class);
    this.expected.expectMessage(new StringStartsWith("Vertex 0: 3"));
    R2MeshBasic.of(positions, normals, uvs, vertices, triangles);
  }

  @Test
  public void testBuildTriangleNoSuchV1()
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

    positions.add(PVector3D.of(1.0, 1.0, 1.0));
    normals.add(PVector3D.of(1.0, 1.0, 1.0));
    uvs.add(PVector2D.of(1.0, 1.0));
    vertices.add(R2MeshBasicVertex.of(0L, 0L, 0L));
    vertices.add(R2MeshBasicVertex.of(0L, 0L, 0L));
    vertices.add(R2MeshBasicVertex.of(0L, 0L, 0L));
    triangles.add(R2MeshTriangle.of(0L, 3L, 2L));

    this.expected.expect(R2MeshExceptionMissingVertex.class);
    this.expected.expectMessage(new StringStartsWith("Vertex 1: 3"));
    R2MeshBasic.of(positions, normals, uvs, vertices, triangles);
  }

  @Test
  public void testBuildTriangleNoSuchV2()
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

    positions.add(PVector3D.of(1.0, 1.0, 1.0));
    normals.add(PVector3D.of(1.0, 1.0, 1.0));
    uvs.add(PVector2D.of(1.0, 1.0));
    vertices.add(R2MeshBasicVertex.of(0L, 0L, 0L));
    vertices.add(R2MeshBasicVertex.of(0L, 0L, 0L));
    vertices.add(R2MeshBasicVertex.of(0L, 0L, 0L));
    triangles.add(R2MeshTriangle.of(0L, 1L, 3L));

    this.expected.expect(R2MeshExceptionMissingVertex.class);
    this.expected.expectMessage(new StringStartsWith("Vertex 2: 3"));
    R2MeshBasic.of(positions, normals, uvs, vertices, triangles);
  }

  @Test
  public void testBuildTriangleMalformed0()
  {
    this.expected.expect(R2MeshExceptionMalformedTriangle.class);
    this.expected.expectMessage(new StringContains("Indices: 0 0 1"));
    R2MeshTriangle.of(0L, 0L, 1L);
  }

  @Test
  public void testBuildTriangleMalformed1()
  {
    this.expected.expect(R2MeshExceptionMalformedTriangle.class);
    this.expected.expectMessage(new StringContains("Indices: 0 1 0"));
    R2MeshTriangle.of(0L, 1L, 0L);
  }

  @Test
  public void testBuildTriangleMalformed2()
  {
    this.expected.expect(R2MeshExceptionMalformedTriangle.class);
    this.expected.expectMessage(new StringContains("Indices: 1 0 0"));
    R2MeshTriangle.of(1L, 0L, 0L);
  }
}
