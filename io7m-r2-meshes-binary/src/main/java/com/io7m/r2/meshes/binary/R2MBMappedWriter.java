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

package com.io7m.r2.meshes.binary;

import com.io7m.jaffirm.core.Invariants;
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
  private final MappedByteBuffer buffer;
  private final FileChannel channel;

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
    size += triangle * m.triangles().size64();
    size += vertex * m.vertices().size64();
    return size;
  }

  @Override
  public long run()
    throws IOException
  {
    final long v_offset;
    final long t_offset;

    final long v_count = this.mesh.vertices().size64();
    final long t_count = this.mesh.triangles().size64();

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
        this.mesh.vertices();
      final BigList<PVectorI3D<R2SpaceObjectType>> m_pos =
        this.mesh.positions();
      final BigList<PVectorI3D<R2SpaceObjectType>> m_nor =
        this.mesh.normals();
      final BigList<PVectorI4D<R2SpaceObjectType>> m_tan =
        this.mesh.tangents();
      final BigList<PVectorI2D<R2SpaceTextureType>> m_uv =
        this.mesh.uvs();

      for (long index = 0L; index < v_count; ++index) {
        c.setElementIndex((int) index);

        final Long bi = Long.valueOf(index);
        final R2MeshTangentsVertexType vv = vertices.get(index);

        {
          final PVectorI3D<R2SpaceObjectType> k =
            m_pos.get(vv.positionIndex());

          final double x = k.getXD();
          final double y = k.getYD();
          final double z = k.getZD();

          Invariants.checkInvariantD(
            x,
            Double.isFinite(x),
            n -> String.format("Position [%d].x must be finite", bi));
          Invariants.checkInvariantD(
            y,
            Double.isFinite(y),
            n -> String.format("Position [%d].y must be finite", bi));
          Invariants.checkInvariantD(
            z,
            Double.isFinite(z),
            n -> String.format("Position [%d].z must be finite", bi));

          Invariants.checkInvariantD(
            x,
            !Double.isNaN(x),
            n -> String.format("Position [%d].x must be a valid number", bi));
          Invariants.checkInvariantD(
            y,
            !Double.isNaN(y),
            n -> String.format("Position [%d].y must be a valid number", bi));
          Invariants.checkInvariantD(
            z,
            !Double.isNaN(z),
            n -> String.format("Position [%d].z must be a valid number", bi));

          v_pos.set3F((float) x, (float) y, (float) z);
        }

        {
          final PVectorI3D<R2SpaceObjectType> k =
            m_nor.get(vv.normalIndex());

          final double x = k.getXD();
          final double y = k.getYD();
          final double z = k.getZD();

          Invariants.checkInvariantD(
            x,
            Double.isFinite(x),
            n -> String.format("Normal [%d].x must be finite", bi));
          Invariants.checkInvariantD(
            y,
            Double.isFinite(y),
            n -> String.format("Normal [%d].y must be finite", bi));
          Invariants.checkInvariantD(
            z,
            Double.isFinite(z),
            n -> String.format("Normal [%d].z must be finite", bi));

          Invariants.checkInvariantD(
            x,
            !Double.isNaN(x),
            n -> String.format("Normal [%d].x must be a valid number", bi));
          Invariants.checkInvariantD(
            y,
            !Double.isNaN(y),
            n -> String.format("Normal [%d].y must be a valid number", bi));
          Invariants.checkInvariantD(
            z,
            !Double.isNaN(z),
            n -> String.format("Normal [%d].z must be a valid number", bi));

          v_nor.set3F((float) x, (float) y, (float) z);
        }

        {
          final PVectorI4D<R2SpaceObjectType> k =
            m_tan.get(vv.tangentIndex());

          final double x = k.getXD();
          final double y = k.getYD();
          final double z = k.getZD();
          final double w = k.getWD();

          Invariants.checkInvariantD(
            x,
            Double.isFinite(x),
            n -> String.format("Tangent [%d].x must be finite", bi));
          Invariants.checkInvariantD(
            y,
            Double.isFinite(y),
            n -> String.format("Tangent [%d].y must be finite", bi));
          Invariants.checkInvariantD(
            z,
            Double.isFinite(z),
            n -> String.format("Tangent [%d].z must be finite", bi));
          Invariants.checkInvariantD(
            w,
            Double.isFinite(z),
            n -> String.format("Tangent [%d].w must be finite", bi));

          Invariants.checkInvariantD(
            x,
            !Double.isNaN(x),
            n -> String.format("Tangent [%d].x must be a valid number", bi));
          Invariants.checkInvariantD(
            y,
            !Double.isNaN(y),
            n -> String.format("Tangent [%d].y must be a valid number", bi));
          Invariants.checkInvariantD(
            z,
            !Double.isNaN(z),
            n -> String.format("Tangent [%d].z must be a valid number", bi));
          Invariants.checkInvariantD(
            w,
            !Double.isNaN(w),
            n -> String.format("Tangent [%d].w must be a valid number", bi));

          v_tan.set4F((float) x, (float) y, (float) z, (float) w);
        }

        {
          final PVectorI2D<R2SpaceTextureType> k =
            m_uv.get(vv.uvIndex());

          final double x = k.getXD();
          final double y = k.getYD();

          Invariants.checkInvariantD(
            x,
            Double.isFinite(x),
            n -> String.format("UV [%d].x must be finite", bi));
          Invariants.checkInvariantD(
            y,
            Double.isFinite(y),
            n -> String.format("UV [%d].y must be finite", bi));

          Invariants.checkInvariantD(
            x,
            !Double.isNaN(x),
            n -> String.format("UV [%d].x must be a valid number", bi));
          Invariants.checkInvariantD(
            y,
            !Double.isNaN(y),
            n -> String.format("UV [%d].y must be a valid number", bi));

          v_uv.set2F((float) x, (float) y);
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
        this.mesh.triangles();

      for (long index = 0L; index < t_count; ++index) {
        c.setElementIndex((int) index);
        final R2MeshTriangleType t = m_tri.get(index);
        v.setV0((int) (t.v0() & 0xFFFFFFFFL));
        v.setV1((int) (t.v1() & 0xFFFFFFFFL));
        v.setV2((int) (t.v2() & 0xFFFFFFFFL));
      }
    }

    this.buffer.force();
    return (long) this.buffer.capacity();
  }

  @Override
  public void close()
    throws IOException
  {
    this.channel.close();
  }
}
