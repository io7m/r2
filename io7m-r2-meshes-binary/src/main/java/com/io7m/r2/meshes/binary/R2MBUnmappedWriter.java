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
import org.valid4j.Assertive;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * A mesh writer that writes data to a
 * {@link java.nio.channels.WritableByteChannel}.
 */

public final class R2MBUnmappedWriter implements R2MBWriterType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MBUnmappedWriter.class);
  }

  private final WritableByteChannel channel;
  private final ByteBuffer          buffer_header;
  private final ByteBuffer          buffer_vertex;
  private final ByteBuffer          buffer_tri;
  private final R2MeshTangentsType  mesh;

  private R2MBUnmappedWriter(
    final WritableByteChannel in_channel,
    final R2MeshTangentsType in_mesh)
  {
    this.channel = NullCheck.notNull(in_channel);
    this.mesh = NullCheck.notNull(in_mesh);

    this.buffer_header =
      ByteBuffer.allocate(R2MBHeaderByteBuffered.sizeInOctets());
    this.buffer_header.order(ByteOrder.BIG_ENDIAN);
    this.buffer_vertex =
      ByteBuffer.allocate(R2MBVertexByteBuffered.sizeInOctets());
    this.buffer_vertex.order(ByteOrder.BIG_ENDIAN);
    this.buffer_tri =
      ByteBuffer.allocate(R2MBTriangleByteBuffered.sizeInOctets());
    this.buffer_tri.order(ByteOrder.BIG_ENDIAN);
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

    final FileChannel fc = FileChannel.open(
      p,
      StandardOpenOption.CREATE,
      StandardOpenOption.WRITE,
      StandardOpenOption.READ,
      StandardOpenOption.TRUNCATE_EXISTING);
    return new R2MBUnmappedWriter(fc, m);
  }

  /**
   * Construct a new writer for the given stream.
   *
   * @param s The output stream
   * @param m The mesh
   *
   * @return A new writer
   *
   * @throws IOException On I/O errors
   */

  public static R2MBWriterType newWriterForOutputStream(
    final OutputStream s,
    final R2MeshTangentsType m)
    throws IOException
  {
    NullCheck.notNull(s);
    NullCheck.notNull(m);

    return new R2MBUnmappedWriter(Channels.newChannel(s), m);
  }

  private static long writeAll(
    final ByteBuffer bh,
    final WritableByteChannel channel)
    throws IOException
  {
    long all = 0L;
    while (bh.hasRemaining()) {
      final int w = channel.write(bh);
      if (w == -1) {
        break;
      }
      all += w;
    }
    return all;
  }

  @Override
  public long run()
    throws IOException
  {
    long bc = 0L;
    final long v_count = this.mesh.getVertices().size64();
    final long t_count = this.mesh.getTriangles().size64();

    /**
     * Write header.
     */

    {
      final JPRACursor1DType<R2MBHeaderType> c =
        JPRACursor1DByteBufferedChecked.newCursor(
          this.buffer_header, R2MBHeaderByteBuffered::newValueWithOffset);

      final R2MBHeaderType v = c.getElementView();
      v.setMagic0((byte) 'R');
      v.setMagic1((byte) '2');
      v.setMagic2((byte) 'B');
      v.setMagic3((byte) '\n');
      v.setVersion(R2MBVersion.R2MB_VERSION);
      v.setTriangleCount((int) (t_count & 0xFFFFFFFFL));
      v.setVertexCount((int) (v_count & 0xFFFFFFFFL));

      bc += R2MBUnmappedWriter.writeAll(this.buffer_header, this.channel);
    }

    /**
     * Write vertices.
     */

    if (v_count > 0L) {
      final JPRACursor1DType<R2MBVertexType> c =
        JPRACursor1DByteBufferedChecked.newCursor(
          this.buffer_vertex,
          R2MBVertexByteBuffered::newValueWithOffset);

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
        this.buffer_vertex.rewind();

        final Long bi = Long.valueOf(index);
        final R2MeshTangentsVertexType vv = vertices.get(index);

        {
          final PVectorI3D<R2SpaceObjectType> k =
            m_pos.get(vv.getPositionIndex());

          final double x = k.getXD();
          final double y = k.getYD();
          final double z = k.getZD();

          Assertive.ensure(
            Double.isFinite(x), "Position [%d].x must be finite", bi);
          Assertive.ensure(
            Double.isFinite(y), "Position [%d].y must be finite", bi);
          Assertive.ensure(
            Double.isFinite(z), "Position [%d].z must be finite", bi);

          Assertive.ensure(
            !Double.isNaN(x), "Position [%d].x must be a valid number", bi);
          Assertive.ensure(
            !Double.isNaN(y), "Position [%d].y must be a valid number", bi);
          Assertive.ensure(
            !Double.isNaN(z), "Position [%d].z must be a valid number", bi);

          v_pos.set3F((float) x, (float) y, (float) z);
        }

        {
          final PVectorI3D<R2SpaceObjectType> k =
            m_nor.get(vv.getNormalIndex());

          final double x = k.getXD();
          final double y = k.getYD();
          final double z = k.getZD();

          Assertive.ensure(
            Double.isFinite(x), "Normal [%d].x must be finite", bi);
          Assertive.ensure(
            Double.isFinite(y), "Normal [%d].y must be finite", bi);
          Assertive.ensure(
            Double.isFinite(z), "Normal [%d].z must be finite", bi);

          Assertive.ensure(
            !Double.isNaN(x), "Normal [%d].x must be a valid number", bi);
          Assertive.ensure(
            !Double.isNaN(y), "Normal [%d].y must be a valid number", bi);
          Assertive.ensure(
            !Double.isNaN(z), "Normal [%d].z must be a valid number", bi);

          v_nor.set3F((float) x, (float) y, (float) z);
        }

        {
          final PVectorI4D<R2SpaceObjectType> k =
            m_tan.get(vv.getTangentIndex());

          final double x = k.getXD();
          final double y = k.getYD();
          final double z = k.getZD();
          final double w = k.getWD();

          Assertive.ensure(
            Double.isFinite(x), "Tangent [%d].x must be finite", bi);
          Assertive.ensure(
            Double.isFinite(y), "Tangent [%d].y must be finite", bi);
          Assertive.ensure(
            Double.isFinite(z), "Tangent [%d].z must be finite", bi);
          Assertive.ensure(
            Double.isFinite(w), "Tangent [%d].w must be finite", bi);

          Assertive.ensure(
            !Double.isNaN(x), "Tangent [%d].x must be a valid number", bi);
          Assertive.ensure(
            !Double.isNaN(y), "Tangent [%d].y must be a valid number", bi);
          Assertive.ensure(
            !Double.isNaN(z), "Tangent [%d].z must be a valid number", bi);
          Assertive.ensure(
            !Double.isNaN(w), "Tangent [%d].w must be a valid number", bi);

          v_tan.set4F((float) x, (float) y, (float) z, (float) w);
        }

        {
          final PVectorI2D<R2SpaceTextureType> k =
            m_uv.get(vv.getUVIndex());

          final double x = k.getXD();
          final double y = k.getYD();

          Assertive.ensure(
            Double.isFinite(x), "UV [%d].x must be finite", bi);
          Assertive.ensure(
            Double.isFinite(y), "UV [%d].y must be finite", bi);

          Assertive.ensure(
            !Double.isNaN(x), "UV [%d].x must be a valid number", bi);
          Assertive.ensure(
            !Double.isNaN(y), "UV [%d].y must be a valid number", bi);

          v_uv.set2F((float) x, (float) y);
        }

        bc += R2MBUnmappedWriter.writeAll(this.buffer_vertex, this.channel);
      }
    }

    /**
     * Write triangles.
     */

    if (t_count > 0L) {
      final JPRACursor1DType<R2MBTriangleType> c =
        JPRACursor1DByteBufferedChecked.newCursor(
          this.buffer_tri,
          R2MBTriangleByteBuffered::newValueWithOffset);

      final R2MBTriangleType v = c.getElementView();

      final BigList<R2MeshTriangleType> m_tri =
        this.mesh.getTriangles();

      for (long index = 0L; index < t_count; ++index) {
        this.buffer_tri.rewind();

        final R2MeshTriangleType t = m_tri.get(index);
        v.setV0((int) (t.getV0() & 0xFFFFFFFFL));
        v.setV1((int) (t.getV1() & 0xFFFFFFFFL));
        v.setV2((int) (t.getV2() & 0xFFFFFFFFL));

        bc += R2MBUnmappedWriter.writeAll(this.buffer_tri, this.channel);
      }
    }

    return bc;
  }

  @Override
  public void close()
    throws IOException
  {
    this.channel.close();
  }
}
