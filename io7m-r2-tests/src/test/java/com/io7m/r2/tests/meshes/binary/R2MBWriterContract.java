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

package com.io7m.r2.tests.meshes.binary;

import com.io7m.jtensors.parameterized.PVectorI2D;
import com.io7m.jtensors.parameterized.PVectorI3D;
import com.io7m.jtensors.parameterized.PVectorI4D;
import com.io7m.r2.meshes.R2MeshParserInterleavedListenerType;
import com.io7m.r2.meshes.R2MeshTangents;
import com.io7m.r2.meshes.R2MeshTangentsAdapter;
import com.io7m.r2.meshes.R2MeshTangentsAdapterType;
import com.io7m.r2.meshes.R2MeshTangentsBuilderType;
import com.io7m.r2.meshes.R2MeshTangentsType;
import com.io7m.r2.meshes.binary.R2MBReaderType;
import com.io7m.r2.meshes.binary.R2MBWriterType;
import com.io7m.r2.meshes.binary.r2mb.R2MBHeaderByteBuffered;
import com.io7m.r2.meshes.binary.r2mb.R2MBTriangleByteBuffered;
import com.io7m.r2.meshes.binary.r2mb.R2MBVertexByteBuffered;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class R2MBWriterContract
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MBWriterContract.class);
  }

  @Rule public ExpectedException expected = ExpectedException.none();

  protected abstract R2MBWriterType getWriter(
    Path p,
    R2MeshTangentsType m)
    throws IOException;

  protected abstract R2MBReaderType getReader(
    Path p,
    R2MeshParserInterleavedListenerType listener)
    throws IOException;

  @Test
  public final void testEmpty()
    throws Exception
  {
    final R2MeshTangentsType m = R2MeshTangents.newBuilder(100L, 100L).build();
    final Path path = Files.createTempFile("r2mb-writer-", "");
    final R2MBWriterType w = this.getWriter(path, m);
    final long s = w.run();
    Assert.assertEquals(R2MBHeaderByteBuffered.sizeInOctets(), s);
    this.compareWrittenMesh(m, path);
  }

  @Test
  public final void testTriangle()
    throws Exception
  {
    final R2MeshTangentsBuilderType rmb = R2MeshTangents.newBuilder(100L, 100L);
    final long v0 = rmb.addVertex(
      rmb.addPosition(new PVectorI3D<>(0.0, 1.0, 0.0)),
      rmb.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0)),
      rmb.addTangent(new PVectorI4D<>(1.0, 0.0, 0.0, 1.0)),
      rmb.addBitangent(new PVectorI3D<>(0.0, 1.0, 0.0)),
      rmb.addUV(new PVectorI2D<>(0.0, 1.0)));
    final long v1 = rmb.addVertex(
      rmb.addPosition(new PVectorI3D<>(0.0, 0.0, 0.0)),
      rmb.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0)),
      rmb.addTangent(new PVectorI4D<>(1.0, 0.0, 0.0, 1.0)),
      rmb.addBitangent(new PVectorI3D<>(0.0, 1.0, 0.0)),
      rmb.addUV(new PVectorI2D<>(0.0, 0.0)));
    final long v2 = rmb.addVertex(
      rmb.addPosition(new PVectorI3D<>(1.0, 0.0, 0.0)),
      rmb.addNormal(new PVectorI3D<>(0.0, 0.0, 1.0)),
      rmb.addTangent(new PVectorI4D<>(1.0, 0.0, 0.0, 1.0)),
      rmb.addBitangent(new PVectorI3D<>(0.0, 1.0, 0.0)),
      rmb.addUV(new PVectorI2D<>(1.0, 0.0)));
    rmb.addTriangle(v0, v1, v2);
    final R2MeshTangentsType m = rmb.build();

    final Path path = Files.createTempFile("r2mb-writer-", "");
    R2MBWriterContract.LOG.debug("writing {}", path);

    final R2MBWriterType w = this.getWriter(path, m);
    final long s = w.run();
    w.close();

    Assert.assertEquals(
      R2MBHeaderByteBuffered.sizeInOctets() +
        (3 * R2MBVertexByteBuffered.sizeInOctets()) +
        R2MBTriangleByteBuffered.sizeInOctets(), s);
    this.compareWrittenMesh(m, path);
  }

  private void compareWrittenMesh(
    final R2MeshTangentsType m,
    final Path path)
    throws IOException
  {
    final R2MeshTangentsAdapterType ma =
      R2MeshTangentsAdapter.newAdapter((e, em) -> {
        R2MBWriterContract.LOG.error("{}: {}: ", path, em, e);

        if (e.isPresent()) {
          throw new IllegalStateException(em, e.get());
        }
        throw new IllegalStateException(em);
      });

    final R2MBReaderType r = this.getReader(path, ma);
    r.run();

    Assert.assertTrue(ma.getMesh().isPresent());
    Assert.assertEquals(m, ma.getMesh().get());
  }
}
