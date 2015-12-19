/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.meshes.binary;

import com.io7m.jnull.NullCheck;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedChecked;
import com.io7m.jpra.runtime.java.JPRACursor1DType;
import com.io7m.jtensors.Vector2FType;
import com.io7m.jtensors.Vector3FType;
import com.io7m.jtensors.Vector4FType;
import com.io7m.jtensors.parameterized.PVectorI2D;
import com.io7m.jtensors.parameterized.PVectorI3D;
import com.io7m.jtensors.parameterized.PVectorI4D;
import com.io7m.r2.meshes.R2MeshTangentsType;
import com.io7m.r2.meshes.R2MeshTangentsVertexType;
import com.io7m.r2.meshes.R2MeshTriangleType;
import com.io7m.r2.meshes.binary.r2mb.R2MBHeaderByteBuffered;
import com.io7m.r2.meshes.binary.r2mb.R2MBHeaderType;
import com.io7m.r2.meshes.binary.r2mb.R2MBTriangleByteBuffered;
import com.io7m.r2.meshes.binary.r2mb.R2MBTriangleType;
import com.io7m.r2.meshes.binary.r2mb.R2MBVertexByteBuffered;
import com.io7m.r2.meshes.binary.r2mb.R2MBVertexType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import it.unimi.dsi.fastutil.BigList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * A memory-mapped implementation of the {@link R2MBWriterType} interface.
 */

public final class R2MBMappedWriter implements R2MBWriterType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MBMappedWriter.class);
  }

  private final R2MeshTangentsType mesh;
  private final MappedByteBuffer   buffer;
  private final FileChannel        channel;

  private R2MBMappedWriter(
    final FileChannel in_channel,
    final MappedByteBuffer in_buffer,
    final R2MeshTangentsType in_mesh)
  {
    this.channel = NullCheck.notNull(in_channel);
    this.buffer = NullCheck.notNull(in_buffer);
    this.mesh = NullCheck.notNull(in_mesh);
  }

  /**
   * Construct a new writer for the given file.
   *
   * @param p The file
   * @param m The mesh
   *
   * @return A new writer
   *
   * @throws IOException On I/O errors
   */

  public static R2MBWriterType newWriterForPath(
    final Path p,
    final R2MeshTangentsType m)
    throws IOException
  {
    NullCheck.notNull(p);
    NullCheck.notNull(m);

    final long size = R2MBMappedWriter.expectedSize(m);
    R2MBMappedWriter.LOG.debug("map {} ({} bytes)", p, Long.valueOf(size));

    final FileChannel fc = FileChannel.open(
      p,
      StandardOpenOption.CREATE,
      StandardOpenOption.WRITE,
      StandardOpenOption.READ,
      StandardOpenOption.TRUNCATE_EXISTING);
    final MappedByteBuffer map =
      fc.map(FileChannel.MapMode.READ_WRITE, 0L, size);
    map.order(ByteOrder.BIG_ENDIAN);
    return new R2MBMappedWriter(fc, map, m);
  }

  private static long expectedSize(final R2MeshTangentsType m)
  {
    final long header = (long) R2MBHeaderByteBuffered.sizeInOctets();
    final long triangle = (long) R2MBTriangleByteBuffered.sizeInOctets();
    final long vertex = (long) R2MBVertexByteBuffered.sizeInOctets();

    long size = 0L;
    size += header;
    size += triangle * m.getTriangles().size64();
    size += vertex * m.getVertices().size64();
    return size;
  }

  @Override public long run()
    throws IOException
  {
    final long v_offset;
    final long t_offset;

    final long v_count = this.mesh.getVertices().size64();
    final long t_count = this.mesh.getTriangles().size64();

    /**
     * Write header.
     */

    {
      final JPRACursor1DType<R2MBHeaderType> c =
        JPRACursor1DByteBufferedChecked.newCursor(
          this.buffer, R2MBHeaderByteBuffered::newValueWithOffset);

      final R2MBHeaderType v = c.getElementView();
      v.setMagic0((byte) 'R');
      v.setMagic1((byte) '2');
      v.setMagic2((byte) 'B');
      v.setMagic3((byte) '\n');
      v.setVersion(R2MBVersion.R2MB_VERSION);
      v.setTriangleCount((int) (t_count & 0xFFFFFFFFL));
      v.setVertexCount((int) (v_count & 0xFFFFFFFFL));

      v_offset = (long) R2MBHeaderByteBuffered.sizeInOctets();

      final long header =
        (long) R2MBHeaderByteBuffered.sizeInOctets();
      final long vertices =
        v_count * (long) R2MBVertexByteBuffered.sizeInOctets();
      t_offset = header + vertices;
    }

    /**
     * Write vertices.
     */

    if (v_count > 0L) {
      final JPRACursor1DType<R2MBVertexType> c =
        JPRACursor1DByteBufferedChecked.newCursor(
          this.buffer,
          (b, p, o) -> R2MBVertexByteBuffered.newValueWithOffset(
            b, p, (int) (o + v_offset)));

      final R2MBVertexType v = c.getElementView();
      final Vector3FType v_pos = v.getPositionWritable();
      final Vector3FType v_nor = v.getNormalWritable();
      final Vector4FType v_tan = v.getTangentWritable();
      final Vector2FType v_uv = v.getUvWritable();

      final BigList<R2MeshTangentsVertexType> vertices =
        this.mesh.getVertices();
      final BigList<PVectorI3D<R2SpaceObjectType>> m_pos =
        this.mesh.getPositions();
      final BigList<PVectorI3D<R2SpaceObjectType>> m_nor =
        this.mesh.getNormals();
      final BigList<PVectorI4D<R2SpaceObjectType>> m_tan =
        this.mesh.getTangents();
      final BigList<PVectorI2D<R2SpaceTextureType>> m_uv =
        this.mesh.getUVs();

      for (long index = 0L; index < v_count; ++index) {
        final R2MeshTangentsVertexType vv = vertices.get(index);
        c.setElementIndex((int) index);

        {
          final PVectorI3D<R2SpaceObjectType> k =
            m_pos.get(vv.getPositionIndex());
          v_pos.set3F(
            (float) k.getXD(),
            (float) k.getYD(),
            (float) k.getZD());
        }

        {
          final PVectorI3D<R2SpaceObjectType> k =
            m_nor.get(vv.getNormalIndex());
          v_nor.set3F(
            (float) k.getXD(),
            (float) k.getYD(),
            (float) k.getZD());
        }

        {
          final PVectorI4D<R2SpaceObjectType> k =
            m_tan.get(vv.getTangentIndex());
          v_tan.set4F(
            (float) k.getXD(),
            (float) k.getYD(),
            (float) k.getZD(),
            (float) k.getWD());
        }

        {
          final PVectorI2D<R2SpaceTextureType> k =
            m_uv.get(vv.getUVIndex());
          v_uv.set2F(
            (float) k.getXD(),
            (float) k.getYD());
        }
      }
    }

    /**
     * Write triangles.
     */

    if (t_count > 0L) {
      final JPRACursor1DType<R2MBTriangleType> c =
        JPRACursor1DByteBufferedChecked.newCursor(
          this.buffer,
          (b, p, o) -> R2MBTriangleByteBuffered.newValueWithOffset(
            b, p, (int) (o + t_offset)));

      final R2MBTriangleType v = c.getElementView();

      final BigList<R2MeshTriangleType> m_tri =
        this.mesh.getTriangles();

      for (long index = 0L; index < t_count; ++index) {
        final R2MeshTriangleType t = m_tri.get(index);
        v.setV0((int) (t.getV0() & 0xFFFFFFFFL));
        v.setV1((int) (t.getV1() & 0xFFFFFFFFL));
        v.setV2((int) (t.getV2() & 0xFFFFFFFFL));
      }
    }

    this.buffer.force();
    return (long) this.buffer.capacity();
  }

  @Override public void close()
    throws IOException
  {
    this.channel.close();
  }
}
