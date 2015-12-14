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
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedUnchecked;
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;

/**
 * A mesh reader that consumes data from a {@link ReadableByteChannel}.
 */

public final class R2MBUnmappedReader implements Runnable
{
  private final R2MeshParserListenerType listener;
  private final ReadableByteChannel      channel;
  private final ByteBuffer               buffer_header;
  private final ByteBuffer               buffer_vertex;
  private final ByteBuffer               buffer_tri;

  private R2MBUnmappedReader(
    final ReadableByteChannel in_channel,
    final R2MeshParserListenerType in_listener)
  {
    this.channel = NullCheck.notNull(in_channel);
    this.listener = NullCheck.notNull(in_listener);

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
   * Construct a new reader from the given byte channel.
   *
   * @param in_channel  The channel
   * @param in_listener A parser listener
   *
   * @return A new reader
   */

  public static Runnable newReader(
    final ReadableByteChannel in_channel,
    final R2MeshParserListenerType in_listener)
  {
    return new R2MBUnmappedReader(in_channel, in_listener);
  }

  private static void readAll(
    final ByteBuffer bh,
    final ReadableByteChannel channel)
    throws IOException
  {
    while (bh.hasRemaining()) {
      if (channel.read(bh) == -1) {
        break;
      }
    }
  }

  @Override public void run()
  {
    final long v_count;
    final long t_count;

    try {
      final ByteBuffer bh = this.buffer_header;
      R2MBUnmappedReader.readAll(bh, this.channel);

      {
        if (!this.checkSize(
          bh,
          R2MBHeaderByteBuffered.sizeInOctets(),
          "header")) {
          return;
        }

        final JPRACursor1DType<R2MBHeaderType> c =
          JPRACursor1DByteBufferedUnchecked.newCursor(
            bh, R2MBHeaderByteBuffered::newValueWithOffset);

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
      }

      if (v_count > 0L) {
        final int size = R2MBVertexByteBuffered.sizeInOctets();
        final ByteBuffer b = this.buffer_vertex;
        final JPRACursor1DType<R2MBVertexType> c =
          JPRACursor1DByteBufferedChecked.newCursor(
            b, R2MBVertexByteBuffered::newValueWithOffset);

        final R2MBVertexType v = c.getElementView();
        final VectorReadable3FType v_pos = v.getPositionReadable();
        final VectorReadable3FType v_nor = v.getNormalReadable();
        final VectorReadable4FType v_tan = v.getTangentReadable();
        final VectorReadable2FType v_uv = v.getUvReadable();

        for (int index = 0; index < v_count; ++index) {
          b.rewind();
          R2MBUnmappedReader.readAll(b, this.channel);
          if (!this.checkSize(b, size, "vertex")) {
            return;
          }
          R2MBMappedReader.parseVertex(
            this.listener, index, v_pos, v_nor, v_tan, v_uv);
        }
      }

      this.listener.onEventVerticesFinished();

      if (t_count > 0L) {
        final ByteBuffer b = this.buffer_tri;
        final JPRACursor1DType<R2MBTriangleType> c =
          JPRACursor1DByteBufferedUnchecked.newCursor(
            b, R2MBTriangleByteBuffered::newValueWithOffset);
        final R2MBTriangleType v = c.getElementView();

        for (int index = 0; index < t_count; ++index) {
          b.rewind();
          R2MBUnmappedReader.readAll(b, this.channel);
          this.listener.onEventTriangle(
            index, v.getV0(), v.getV1(), v.getV2());
        }
      }

      this.listener.onEventTrianglesFinished();

    } catch (final IOException e) {
      this.listener.onError(Optional.of(e), e.getMessage());
    }
  }

  private boolean checkSize(
    final ByteBuffer b,
    final int size,
    final String section)
  {
    if (b.hasRemaining()) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Error parsing ");
      sb.append(section);
      sb.append(".\n");
      sb.append("Unexpected end of stream.\n");
      sb.append("Expected: ");
      sb.append(size);
      sb.append(" octets\n");
      sb.append("Got:      ");
      sb.append(size - b.remaining());
      sb.append(" octets\n");
      this.listener.onError(Optional.empty(), sb.toString());
      return false;
    }

    return true;
  }
}
