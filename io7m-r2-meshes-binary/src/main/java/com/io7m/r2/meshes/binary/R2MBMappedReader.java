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
import com.io7m.jtensors.VectorReadable2FType;
import com.io7m.jtensors.VectorReadable3FType;
import com.io7m.jtensors.VectorReadable4FType;
import com.io7m.r2.meshes.R2MeshParserListenerType;
import com.io7m.r2.meshes.binary.r2mb.R2MBHeaderByteBuffered;
import com.io7m.r2.meshes.binary.r2mb.R2MBHeaderType;
import com.io7m.r2.meshes.binary.r2mb.R2MBTriangleByteBuffered;
import com.io7m.r2.meshes.binary.r2mb.R2MBTriangleType;
import com.io7m.r2.meshes.binary.r2mb.R2MBVertexByteBuffered;
import com.io7m.r2.meshes.binary.r2mb.R2MBVertexType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Optional;

/**
 * A memory-mapped mesh reader.
 */

public final class R2MBMappedReader implements R2MBReaderType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MBMappedReader.class);
  }

  private final R2MeshParserListenerType listener;
  private final ByteBuffer               buffer;

  private R2MBMappedReader(
    final MappedByteBuffer in_buffer,
    final R2MeshParserListenerType in_listener)
  {
    this.buffer = NullCheck.notNull(in_buffer);
    this.listener = NullCheck.notNull(in_listener);
  }

  /**
   * Create a new parser for the given file channel.
   *
   * @param in_chan     The file channel
   * @param in_listener The parser listener
   *
   * @return A new parser
   *
   * @throws IOException On I/O errors
   */

  public static R2MBReaderType newMappedReaderForFileChannel(
    final FileChannel in_chan,
    final R2MeshParserListenerType in_listener)
    throws IOException
  {
    final MappedByteBuffer map =
      in_chan.map(FileChannel.MapMode.READ_ONLY, 0L, in_chan.size());
    map.order(ByteOrder.BIG_ENDIAN);
    return new R2MBMappedReader(map, in_listener);
  }

  static boolean checkVersion(
    final R2MBHeaderType v,
    final R2MeshParserListenerType pl)
  {
    final int vv = v.getVersion();
    if (vv != R2MBVersion.R2MB_VERSION) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Unsupported version.\n");
      sb.append("Expected: ");
      sb.append(R2MBVersion.R2MB_VERSION);
      sb.append("\n");
      sb.append("Got: ");
      sb.append(vv);
      sb.append("\n");
      pl.onError(Optional.empty(), sb.toString());
      return false;
    }
    return true;
  }

  static boolean checkMagicNumber(
    final R2MBHeaderType v,
    final R2MeshParserListenerType pl)
  {
    final byte m0 = v.getMagic0();
    final byte m1 = v.getMagic1();
    final byte m2 = v.getMagic2();
    final byte m3 = v.getMagic3();
    if (!((int) m0 == (int) 'R'
          && (int) m1 == (int) '2'
          && (int) m2 == (int) 'B'
          && (int) m3 == (int) '\n')) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Bad magic number.\n");
      sb.append("Expected: 0x52 0x32 0x42 0x0A\n");
      sb.append("Got:      ");
      sb.append(String.format("0x%02X ", Byte.valueOf(m0)));
      sb.append(String.format("0x%02X ", Byte.valueOf(m1)));
      sb.append(String.format("0x%02X ", Byte.valueOf(m2)));
      sb.append(String.format("0x%02X\n", Byte.valueOf(m3)));
      pl.onError(Optional.empty(), sb.toString());
      return false;
    }
    return true;
  }

  static void parseVertex(
    final R2MeshParserListenerType pl,
    final int index,
    final VectorReadable3FType v_pos,
    final VectorReadable3FType v_nor,
    final VectorReadable4FType v_tan,
    final VectorReadable2FType v_uv)
  {
    pl.onEventVertexStarted(index);
    pl.onEventVertexPosition(
      index, v_pos.getXF(), v_pos.getYF(), v_pos.getZF());
    pl.onEventVertexNormal(
      index, v_nor.getXF(), v_nor.getYF(), v_nor.getZF());
    pl.onEventVertexTangent(
      index, v_tan.getXF(), v_tan.getYF(), v_tan.getZF(), v_tan.getWF());
    pl.onEventVertexUV(
      index, v_uv.getXF(), v_uv.getYF());
    pl.onEventVertexFinished(index);
  }

  @Override public void run()
  {
    final long v_count;
    final long t_count;
    final long v_offset;
    final long t_offset;

    {
      final JPRACursor1DType<R2MBHeaderType> c;

      try {
        c =
          JPRACursor1DByteBufferedChecked.newCursor(
            this.buffer, R2MBHeaderByteBuffered::newValueWithOffset);
      } catch (final IllegalArgumentException e) {
        this.listener.onError(
          Optional.of(e),
          "Error parsing header: " + e.getMessage());
        return;
      }

      final R2MBHeaderType v = c.getElementView();
      if (!R2MBMappedReader.checkMagicNumber(v, this.listener)) {
        return;
      }
      if (!R2MBMappedReader.checkVersion(v, this.listener)) {
        return;
      }

      v_count = Integer.toUnsignedLong(v.getVertexCount());
      t_count = Integer.toUnsignedLong(v.getTriangleCount());
      this.listener.onEventVertexCount(v_count);
      this.listener.onEventTriangleCount(t_count);

      {
        v_offset = (long) R2MBHeaderByteBuffered.sizeInOctets();
        R2MBMappedReader.LOG.debug(
          "Vertices start at offset: {}",
          Long.toUnsignedString(v_offset));
      }

      {
        final long header =
          (long) R2MBHeaderByteBuffered.sizeInOctets();
        final long vertices =
          v_count * (long) R2MBVertexByteBuffered.sizeInOctets();
        t_offset = header + vertices;
        R2MBMappedReader.LOG.debug(
          "Triangles start at offset: {}",
          Long.toUnsignedString(t_offset));
      }
    }

    if (v_count > 0L) {
      try {
        final JPRACursor1DType<R2MBVertexType> c =
          JPRACursor1DByteBufferedChecked.newCursor(
            this.buffer,
            (b, p, o) -> R2MBVertexByteBuffered.newValueWithOffset(
              b, p, (int) (o + v_offset)));

        final R2MBVertexType v = c.getElementView();
        final VectorReadable3FType v_pos = v.getPositionReadable();
        final VectorReadable3FType v_nor = v.getNormalReadable();
        final VectorReadable4FType v_tan = v.getTangentReadable();
        final VectorReadable2FType v_uv = v.getUvReadable();

        for (int index = 0; index < v_count; ++index) {
          c.setElementIndex(index);
          R2MBMappedReader.parseVertex(
            this.listener,
            index,
            v_pos,
            v_nor,
            v_tan,
            v_uv);
        }

      } catch (final IllegalArgumentException e) {
        this.listener.onError(
          Optional.of(e), "Error parsing vertices: " + e.getMessage());
        return;
      }
    }

    this.listener.onEventVerticesFinished();

    if (t_count > 0L) {
      try {
        final JPRACursor1DType<R2MBTriangleType> c =
          JPRACursor1DByteBufferedChecked.newCursor(
            this.buffer,
            (b, p, o) -> R2MBTriangleByteBuffered.newValueWithOffset(
              b, p, (int) (o + t_offset)));

        final R2MBTriangleType v = c.getElementView();

        for (int index = 0; index < t_count; ++index) {
          c.setElementIndex(index);
          this.listener.onEventTriangle(
            index, v.getV0(), v.getV1(), v.getV2());
        }

      } catch (final IllegalArgumentException e) {
        this.listener.onError(
          Optional.of(e),
          "Error parsing vertices: " + e.getMessage());
        return;
      }
    }

    this.listener.onEventTrianglesFinished();
  }
}
