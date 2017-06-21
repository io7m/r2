/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.tests.meshes.loading.api;

import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.r2.meshes.api.R2MeshAttributePacked;
import com.io7m.r2.meshes.loading.api.R2MeshLoaded;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderRequest;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderType;
import com.io7m.r2.meshes.loading.api.R2MeshLoadingException;
import com.io7m.r2.meshes.loading.api.R2MeshLoadingExceptionValidation;
import com.io7m.r2.meshes.loading.api.R2MeshRequireTangents;
import com.io7m.r2.meshes.loading.api.R2MeshRequireUV;
import com.io7m.r2.tests.core.R2JCGLContract;
import org.hamcrest.core.StringContains;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URI;

import static com.io7m.r2.meshes.api.R2MeshAttributeComponents.R2_COMPONENT_FLOATING;
import static com.io7m.r2.meshes.api.R2MeshAttributeConventions.NORMAL_ATTRIBUTE_INDEX;
import static com.io7m.r2.meshes.api.R2MeshAttributeConventions.POSITION_ATTRIBUTE_INDEX;
import static com.io7m.r2.meshes.api.R2MeshAttributeConventions.TANGENT4_ATTRIBUTE_INDEX;
import static com.io7m.r2.meshes.api.R2MeshAttributeConventions.UV_ATTRIBUTE_INDEX;
import static org.junit.Assert.assertEquals;

public abstract class R2MeshLoaderSynchronousContract extends R2JCGLContract
{
  @Rule public final ExpectedException expected = ExpectedException.none();

  protected abstract R2MeshLoaderType create();

  @Test
  public void testLoadWithoutTangents()
    throws Exception
  {
    final JCGLContextType g =
      this.newGL33Context("main", 24, 8);

    final R2MeshLoaderType loader = this.create();
    final R2MeshLoaded loaded =
      loader.loadSynchronously(
        g.contextGetGL33(),
        R2MeshLoaderRequest.of(
          this.resolve("all_without_tangents"),
          R2MeshRequireTangents.R2_TANGENTS_OPTIONAL,
          R2MeshRequireUV.R2_UV_OPTIONAL,
          JCGLUsageHint.USAGE_STATIC_DRAW,
          JCGLUsageHint.USAGE_STATIC_DRAW));

    assertEquals(3L, (long) loaded.attributes().size());
    assertEquals((3L + 3L + 2L) * 4L, (long) loaded.vertexSizeOctets());

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(POSITION_ATTRIBUTE_INDEX));
      assertEquals("R2_POSITION", a.attribute().name());
      assertEquals(0L, (long) a.offsetOctets());
      assertEquals(3L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(NORMAL_ATTRIBUTE_INDEX));
      assertEquals("R2_NORMAL", a.attribute().name());
      assertEquals(12L, (long) a.offsetOctets());
      assertEquals(3L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(UV_ATTRIBUTE_INDEX));
      assertEquals("R2_UV", a.attribute().name());
      assertEquals(24L, (long) a.offsetOctets());
      assertEquals(2L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }
  }

  @Test
  public void testLoadWithoutUV()
    throws Exception
  {
    final JCGLContextType g =
      this.newGL33Context("main", 24, 8);

    final R2MeshLoaderType loader = this.create();
    final R2MeshLoaded loaded =
      loader.loadSynchronously(
        g.contextGetGL33(),
        R2MeshLoaderRequest.of(
          this.resolve("all_without_uv"),
          R2MeshRequireTangents.R2_TANGENTS_OPTIONAL,
          R2MeshRequireUV.R2_UV_OPTIONAL,
          JCGLUsageHint.USAGE_STATIC_DRAW,
          JCGLUsageHint.USAGE_STATIC_DRAW));

    assertEquals(3L, (long) loaded.attributes().size());
    assertEquals((3L + 3L + 4L) * 4L, (long) loaded.vertexSizeOctets());

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(POSITION_ATTRIBUTE_INDEX));
      assertEquals("R2_POSITION", a.attribute().name());
      assertEquals(0L, (long) a.offsetOctets());
      assertEquals(3L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(NORMAL_ATTRIBUTE_INDEX));
      assertEquals("R2_NORMAL", a.attribute().name());
      assertEquals(12L, (long) a.offsetOctets());
      assertEquals(3L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(TANGENT4_ATTRIBUTE_INDEX));
      assertEquals("R2_TANGENT4", a.attribute().name());
      assertEquals(24L, (long) a.offsetOctets());
      assertEquals(4L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }
  }

  @Test
  public void testLoadWithAllAttributes()
    throws Exception
  {
    final JCGLContextType g =
      this.newGL33Context("main", 24, 8);

    final R2MeshLoaderType loader = this.create();
    final R2MeshLoaded loaded =
      loader.loadSynchronously(
        g.contextGetGL33(),
        R2MeshLoaderRequest.of(
          this.resolve("all_attributes"),
          R2MeshRequireTangents.R2_TANGENTS_REQUIRED,
          R2MeshRequireUV.R2_UV_OPTIONAL,
          JCGLUsageHint.USAGE_STATIC_DRAW,
          JCGLUsageHint.USAGE_STATIC_DRAW));

    assertEquals(4L, (long) loaded.attributes().size());
    assertEquals((3L + 3L + 2L + 4L) * 4L, (long) loaded.vertexSizeOctets());

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(POSITION_ATTRIBUTE_INDEX));
      assertEquals("R2_POSITION", a.attribute().name());
      assertEquals(0L, (long) a.offsetOctets());
      assertEquals(3L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(NORMAL_ATTRIBUTE_INDEX));
      assertEquals("R2_NORMAL", a.attribute().name());
      assertEquals(12L, (long) a.offsetOctets());
      assertEquals(3L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(UV_ATTRIBUTE_INDEX));
      assertEquals("R2_UV", a.attribute().name());
      assertEquals(24L, (long) a.offsetOctets());
      assertEquals(2L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(TANGENT4_ATTRIBUTE_INDEX));
      assertEquals("R2_TANGENT4", a.attribute().name());
      assertEquals(32L, (long) a.offsetOctets());
      assertEquals(4L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }
  }

  @Test
  public void testLoadWithoutTangentsButRequired()
    throws Exception
  {
    final JCGLContextType g =
      this.newGL33Context("main", 24, 8);
    final R2MeshLoaderType loader = this.create();

    this.expected.expect(R2MeshLoadingException.class);
    this.expected.expectMessage(StringContains.containsString(
      "A required attribute is missing"));
    loader.loadSynchronously(
      g.contextGetGL33(),
      R2MeshLoaderRequest.of(
        this.resolve("all_without_tangents"),
        R2MeshRequireTangents.R2_TANGENTS_REQUIRED,
        R2MeshRequireUV.R2_UV_OPTIONAL,
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUsageHint.USAGE_STATIC_DRAW));
  }

  @Test
  public void testLoadWithoutUVButRequired()
    throws Exception
  {
    final JCGLContextType g =
      this.newGL33Context("main", 24, 8);
    final R2MeshLoaderType loader = this.create();

    this.expected.expect(R2MeshLoadingException.class);
    this.expected.expectMessage(StringContains.containsString(
      "A required attribute is missing"));
    loader.loadSynchronously(
      g.contextGetGL33(),
      R2MeshLoaderRequest.of(
        this.resolve("all_without_uv"),
        R2MeshRequireTangents.R2_TANGENTS_OPTIONAL,
        R2MeshRequireUV.R2_UV_REQUIRED,
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUsageHint.USAGE_STATIC_DRAW));
  }

  @Test
  public void testLoadWithoutTriangles()
    throws Exception
  {
    final JCGLContextType g =
      this.newGL33Context("main", 24, 8);
    final R2MeshLoaderType loader = this.create();

    this.expected.expect(R2MeshLoadingException.class);
    this.expected.expectMessage(StringContains.containsString(
      "a non-zero triangle count is required"));
    loader.loadSynchronously(
      g.contextGetGL33(),
      R2MeshLoaderRequest.of(
        this.resolve("all_without_triangles"),
        R2MeshRequireTangents.R2_TANGENTS_OPTIONAL,
        R2MeshRequireUV.R2_UV_OPTIONAL,
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUsageHint.USAGE_STATIC_DRAW));
  }

  @Test
  public void testLoadWithoutVertices()
    throws Exception
  {
    final JCGLContextType g =
      this.newGL33Context("main", 24, 8);
    final R2MeshLoaderType loader = this.create();

    this.expected.expect(R2MeshLoadingException.class);
    this.expected.expectMessage(StringContains.containsString(
      "a non-zero vertex count is required"));
    loader.loadSynchronously(
      g.contextGetGL33(),
      R2MeshLoaderRequest.of(
        this.resolve("all_without_vertices"),
        R2MeshRequireTangents.R2_TANGENTS_OPTIONAL,
        R2MeshRequireUV.R2_UV_OPTIONAL,
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUsageHint.USAGE_STATIC_DRAW));
  }

  @Test
  public void testLoadEmpty()
    throws Exception
  {
    final JCGLContextType g =
      this.newGL33Context("main", 24, 8);
    final R2MeshLoaderType loader = this.create();

    this.expected.expect(R2MeshLoadingExceptionValidation.class);
    this.expected.expectMessage(StringContains.containsString(
      "A required attribute is missing"));
    loader.loadSynchronously(
      g.contextGetGL33(),
      R2MeshLoaderRequest.of(
        this.resolve("empty"),
        R2MeshRequireTangents.R2_TANGENTS_OPTIONAL,
        R2MeshRequireUV.R2_UV_OPTIONAL,
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUsageHint.USAGE_STATIC_DRAW));
  }

  protected abstract URI resolve(String name);

}
