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

package com.io7m.r2.tests.meshes.obj;

import com.io7m.r2.meshes.R2MeshBasicType;
import com.io7m.r2.meshes.obj.R2ObjMeshErrorListenerType;
import com.io7m.r2.meshes.obj.R2ObjMeshImporterType;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class R2ObjMeshImporterContract
{
  private static final Logger LOG;

  private static final R2ObjMeshErrorListenerType ERROR_LISTENER =
    (p, ex, m, ec, em) ->
      R2ObjMeshImporterContract.LOG.error("{}: {}: {}: {}", p, m, ec, em);

  static {
    LOG = LoggerFactory.getLogger(R2ObjMeshImporterContract.class);
  }

  private static InputStream getFile(final String s)
    throws FileNotFoundException
  {
    final InputStream i =
      R2ObjMeshImporterContract.class.getResourceAsStream(s);
    if (i == null) {
      throw new FileNotFoundException(s);
    }
    return i;
  }

  protected abstract R2ObjMeshImporterType getImporter();

  @Test public final void testEmpty()
    throws Exception
  {
    try (final InputStream s =
      R2ObjMeshImporterContract.getFile("empty.obj")) {
      final R2ObjMeshImporterType i = this.getImporter();
      final Map<String, R2MeshBasicType> ms =
        i.loadMeshesFromStream(
          Optional.empty(),
          s,
          R2ObjMeshImporterContract.ERROR_LISTENER);
      Assert.assertTrue(ms.isEmpty());
    }
  }

  @Test public final void testCubes()
    throws Exception
  {
    try (final InputStream s =
      R2ObjMeshImporterContract.getFile("cubes.obj")) {
      final R2ObjMeshImporterType i = this.getImporter();
      final Map<String, R2MeshBasicType> ms =
        i.loadMeshesFromStream(
          Optional.empty(),
          s,
          R2ObjMeshImporterContract.ERROR_LISTENER);
      Assert.assertEquals(3L, (long) ms.size());
    }
  }

  @Test public final void testCubesPartBroken()
    throws Exception
  {
    try (final InputStream s =
      R2ObjMeshImporterContract.getFile("cubes_part_broken.obj")) {
      final R2ObjMeshImporterType i = this.getImporter();
      final Map<String, R2MeshBasicType> ms =
        i.loadMeshesFromStream(
          Optional.empty(),
          s,
          R2ObjMeshImporterContract.ERROR_LISTENER);
      Assert.assertEquals(2L, (long) ms.size());
    }
  }

  @Test public final void testNotTriangle()
    throws Exception
  {
    final AtomicBoolean e_called = new AtomicBoolean(false);

    try (final InputStream s =
      R2ObjMeshImporterContract.getFile("not_triangle.obj")) {
      final R2ObjMeshImporterType i = this.getImporter();
      final Map<String, R2MeshBasicType> ms =
        i.loadMeshesFromStream(
          Optional.empty(),
          s,
          (p, ex, m, ec, em) -> {
            e_called.set(true);
            Assert.assertTrue(em.startsWith("Too many vertices for face"));
          });
      Assert.assertTrue(e_called.get());
      Assert.assertTrue(ms.isEmpty());
    }
  }

  @Test public final void testV_VN()
    throws Exception
  {
    final AtomicBoolean e_called = new AtomicBoolean(false);

    try (final InputStream s =
      R2ObjMeshImporterContract.getFile("v_vn.obj")) {
      final R2ObjMeshImporterType i = this.getImporter();
      final Map<String, R2MeshBasicType> ms =
        i.loadMeshesFromStream(
          Optional.empty(),
          s,
          (p, ex, m, ec, em) -> {
            e_called.set(true);
            Assert.assertTrue(em.startsWith("Vertices without"));
          });
      Assert.assertTrue(e_called.get());
      Assert.assertTrue(ms.isEmpty());
    }
  }

  @Test public final void testV_VT()
    throws Exception
  {
    final AtomicBoolean e_called = new AtomicBoolean(false);

    try (final InputStream s =
      R2ObjMeshImporterContract.getFile("v_vt.obj")) {
      final R2ObjMeshImporterType i = this.getImporter();
      final Map<String, R2MeshBasicType> ms =
        i.loadMeshesFromStream(
          Optional.empty(),
          s,
          (p, ex, m, ec, em) -> {
            e_called.set(true);
            Assert.assertTrue(em.startsWith("Vertices without"));
          });
      Assert.assertTrue(e_called.get());
      Assert.assertTrue(ms.isEmpty());
    }
  }

  @Test public final void testV()
    throws Exception
  {
    final AtomicBoolean e_called = new AtomicBoolean(false);

    try (final InputStream s =
      R2ObjMeshImporterContract.getFile("v.obj")) {
      final R2ObjMeshImporterType i = this.getImporter();
      final Map<String, R2MeshBasicType> ms =
        i.loadMeshesFromStream(
          Optional.empty(),
          s,
          (p, ex, m, ec, em) -> {
            e_called.set(true);
            Assert.assertTrue(em.startsWith("Vertices without"));
          });
      Assert.assertTrue(e_called.get());
      Assert.assertTrue(ms.isEmpty());
    }
  }

}
