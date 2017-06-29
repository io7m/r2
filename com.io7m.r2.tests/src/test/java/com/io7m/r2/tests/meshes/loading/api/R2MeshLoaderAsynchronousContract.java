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

import com.io7m.jcanephora.async.JCGLAsyncInterfaceUsableGL33Type;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.r2.meshes.api.R2MeshAttributeConventions;
import com.io7m.r2.meshes.api.R2MeshAttributePacked;
import com.io7m.r2.meshes.loading.api.R2MeshLoaded;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderAsynchronousType;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderRequest;
import com.io7m.r2.meshes.loading.api.R2MeshLoadingExceptionValidation;
import com.io7m.r2.meshes.loading.api.R2MeshRequireTangents;
import com.io7m.r2.meshes.loading.api.R2MeshRequireUV;
import com.io7m.r2.tests.R2AsyncGLRule;
import com.io7m.r2.tests.R2JCGLContract;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.StringContains;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.io7m.r2.meshes.api.R2MeshAttributeComponents.R2_COMPONENT_FLOATING;
import static org.junit.Assert.assertEquals;

public abstract class R2MeshLoaderAsynchronousContract extends R2JCGLContract
{
  @Rule public final R2AsyncGLRule async =
    new R2AsyncGLRule(() -> this.newGL33Context("external", 24, 8));

  @Rule public final ExpectedException expected = ExpectedException.none();

  protected abstract R2MeshLoaderAsynchronousType create();

  @Test
  @Ignore("Broken on 3.3 (Core Profile) Mesa 17.0.5, Mesa DRI Intel(R) Sandybridge Mobile")
  public void testLoadWithoutTangents()
    throws Exception
  {
    final JCGLAsyncInterfaceUsableGL33Type g33 = this.async.gl();
    final R2MeshLoaderAsynchronousType loader = this.create();

    final CompletableFuture<R2MeshLoaded> f_load =
      loader.loadAsynchronously(
        g33,
        R2MeshLoaderRequest.of(
          this.resolve("all_without_tangents"),
          R2MeshRequireTangents.R2_TANGENTS_OPTIONAL,
          R2MeshRequireUV.R2_UV_OPTIONAL,
          JCGLUsageHint.USAGE_STATIC_DRAW,
          JCGLUsageHint.USAGE_STATIC_DRAW));

    final R2MeshLoaded loaded = f_load.get(5L, TimeUnit.SECONDS);

    assertEquals(3L, (long) loaded.attributes().size());
    assertEquals((3L + 3L + 2L) * 4L, (long) loaded.vertexSizeOctets());

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(R2MeshAttributeConventions.positionAttributeIndex()));
      assertEquals("R2_POSITION", a.attribute().name());
      assertEquals(0L, (long) a.offsetOctets());
      assertEquals(3L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(R2MeshAttributeConventions.normalAttributeIndex()));
      assertEquals("R2_NORMAL", a.attribute().name());
      assertEquals(12L, (long) a.offsetOctets());
      assertEquals(3L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(R2MeshAttributeConventions.uvAttributeIndex()));
      assertEquals("R2_UV", a.attribute().name());
      assertEquals(24L, (long) a.offsetOctets());
      assertEquals(2L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }
  }

  @Test
  @Ignore("Broken on 3.3 (Core Profile) Mesa 17.0.5, Mesa DRI Intel(R) Sandybridge Mobile")
  public void testLoadWithAllAttributes()
    throws Exception
  {
    final JCGLAsyncInterfaceUsableGL33Type g33 = this.async.gl();
    final R2MeshLoaderAsynchronousType loader = this.create();

    final CompletableFuture<R2MeshLoaded> f_load =
      loader.loadAsynchronously(
        g33,
        R2MeshLoaderRequest.of(
          this.resolve("all_attributes"),
          R2MeshRequireTangents.R2_TANGENTS_REQUIRED,
          R2MeshRequireUV.R2_UV_OPTIONAL,
          JCGLUsageHint.USAGE_STATIC_DRAW,
          JCGLUsageHint.USAGE_STATIC_DRAW));

    final R2MeshLoaded loaded = f_load.get(5L, TimeUnit.SECONDS);

    assertEquals(4L, (long) loaded.attributes().size());
    assertEquals((3L + 3L + 2L + 4L) * 4L, (long) loaded.vertexSizeOctets());

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(R2MeshAttributeConventions.positionAttributeIndex()));
      assertEquals("R2_POSITION", a.attribute().name());
      assertEquals(0L, (long) a.offsetOctets());
      assertEquals(3L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(R2MeshAttributeConventions.normalAttributeIndex()));
      assertEquals("R2_NORMAL", a.attribute().name());
      assertEquals(12L, (long) a.offsetOctets());
      assertEquals(3L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(R2MeshAttributeConventions.uvAttributeIndex()));
      assertEquals("R2_UV", a.attribute().name());
      assertEquals(24L, (long) a.offsetOctets());
      assertEquals(2L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }

    {
      final R2MeshAttributePacked a =
        loaded.attributes().get(Integer.valueOf(R2MeshAttributeConventions.tangent4AttributeIndex()));
      assertEquals("R2_TANGENT4", a.attribute().name());
      assertEquals(32L, (long) a.offsetOctets());
      assertEquals(4L, (long) a.attribute().componentCount());
      assertEquals(32L, (long) a.attribute().componentSizeBits());
      assertEquals(R2_COMPONENT_FLOATING, a.attribute().componentType());
    }
  }

  @Test
  @Ignore("Broken on 3.3 (Core Profile) Mesa 17.0.5, Mesa DRI Intel(R) Sandybridge Mobile")
  public void testLoadWithoutTangentsButRequired()
    throws Exception
  {
    final JCGLAsyncInterfaceUsableGL33Type g33 = this.async.gl();
    final R2MeshLoaderAsynchronousType loader = this.create();

    final CompletableFuture<R2MeshLoaded> f_load =
      loader.loadAsynchronously(
        g33,
        R2MeshLoaderRequest.of(
          this.resolve("all_without_tangents"),
          R2MeshRequireTangents.R2_TANGENTS_REQUIRED,
          R2MeshRequireUV.R2_UV_OPTIONAL,
          JCGLUsageHint.USAGE_STATIC_DRAW,
          JCGLUsageHint.USAGE_STATIC_DRAW));

    this.expected.expect(ExecutionException.class);
    this.expected.expectCause(IsInstanceOf.any(R2MeshLoadingExceptionValidation.class));
    this.expected.expectMessage(StringContains.containsString(
      "A required attribute is missing."));
    f_load.get(5L, TimeUnit.SECONDS);
  }

  @Test
  @Ignore("Broken on 3.3 (Core Profile) Mesa 17.0.5, Mesa DRI Intel(R) Sandybridge Mobile")
  public void testLoadWithoutTriangles()
    throws Exception
  {
    final JCGLAsyncInterfaceUsableGL33Type g33 = this.async.gl();
    final R2MeshLoaderAsynchronousType loader = this.create();

    final CompletableFuture<R2MeshLoaded> f_load =
      loader.loadAsynchronously(
        g33,
        R2MeshLoaderRequest.of(
          this.resolve("all_without_triangles"),
          R2MeshRequireTangents.R2_TANGENTS_OPTIONAL,
          R2MeshRequireUV.R2_UV_OPTIONAL,
          JCGLUsageHint.USAGE_STATIC_DRAW,
          JCGLUsageHint.USAGE_STATIC_DRAW));

    this.expected.expect(ExecutionException.class);
    this.expected.expectCause(IsInstanceOf.any(R2MeshLoadingExceptionValidation.class));
    this.expected.expectMessage(StringContains.containsString(
      "a non-zero triangle count is required"));
    f_load.get(5L, TimeUnit.SECONDS);
  }

  @Test
  @Ignore("Broken on 3.3 (Core Profile) Mesa 17.0.5, Mesa DRI Intel(R) Sandybridge Mobile")
  public void testLoadWithoutVertices()
    throws Exception
  {
    final JCGLAsyncInterfaceUsableGL33Type g33 = this.async.gl();
    final R2MeshLoaderAsynchronousType loader = this.create();

    final CompletableFuture<R2MeshLoaded> f_load =
      loader.loadAsynchronously(
        g33,
        R2MeshLoaderRequest.of(
          this.resolve("all_without_vertices"),
          R2MeshRequireTangents.R2_TANGENTS_OPTIONAL,
          R2MeshRequireUV.R2_UV_OPTIONAL,
          JCGLUsageHint.USAGE_STATIC_DRAW,
          JCGLUsageHint.USAGE_STATIC_DRAW));

    this.expected.expect(ExecutionException.class);
    this.expected.expectCause(IsInstanceOf.any(R2MeshLoadingExceptionValidation.class));
    this.expected.expectMessage(StringContains.containsString(
      "a non-zero vertex count is required"));
    f_load.get(5L, TimeUnit.SECONDS);
  }

  @Test
  @Ignore("Broken on 3.3 (Core Profile) Mesa 17.0.5, Mesa DRI Intel(R) Sandybridge Mobile")
  public void testLoadEmpty()
    throws Exception
  {
    final JCGLAsyncInterfaceUsableGL33Type g33 = this.async.gl();
    final R2MeshLoaderAsynchronousType loader = this.create();

    final CompletableFuture<R2MeshLoaded> f_load =
      loader.loadAsynchronously(
        g33,
        R2MeshLoaderRequest.of(
          this.resolve("empty"),
          R2MeshRequireTangents.R2_TANGENTS_OPTIONAL,
          R2MeshRequireUV.R2_UV_OPTIONAL,
          JCGLUsageHint.USAGE_STATIC_DRAW,
          JCGLUsageHint.USAGE_STATIC_DRAW));

    this.expected.expect(ExecutionException.class);
    this.expected.expectCause(IsInstanceOf.any(R2MeshLoadingExceptionValidation.class));
    this.expected.expectMessage(StringContains.containsString(
      "A required attribute is missing"));
    f_load.get(5L, TimeUnit.SECONDS);
  }

  protected abstract URI resolve(String name);

}
