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

package com.io7m.r2.meshes.defaults;

import com.io7m.jcanephora.core.JCGLArrayBufferType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.JCGLIndexBufferType;
import com.io7m.jcanephora.core.JCGLUnsignedType;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jnull.NullCheck;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2ExceptionIO;
import com.io7m.r2.core.R2UnitSphereType;
import com.io7m.r2.core.cursors.R2VertexCursorPUNT16;
import com.io7m.r2.core.cursors.R2VertexCursorProducerInfoType;
import com.io7m.r2.core.cursors.R2VertexCursorProducerType;
import com.io7m.r2.meshes.arrayobject.R2MeshArrayObjectSynchronousAdapter;
import com.io7m.r2.meshes.arrayobject.R2MeshArrayObjectSynchronousAdapterType;
import com.io7m.r2.meshes.binary.R2MBReaderType;
import com.io7m.r2.meshes.binary.R2MBUnmappedReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valid4j.Assertive;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;

/**
 * The default implementation of the {@link R2UnitSphereType} interface.
 */

public final class R2UnitSphere implements R2UnitSphereType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2UnitSphere.class);
  }

  private final JCGLArrayBufferType array_buffer;
  private final JCGLIndexBufferType index_buffer;
  private final JCGLArrayObjectType array_object;
  private final UnsignedRangeInclusiveL range;

  private R2UnitSphere(
    final JCGLArrayBufferType ab,
    final JCGLArrayObjectType ao,
    final JCGLIndexBufferType ib)
  {
    this.array_buffer = NullCheck.notNull(ab);
    this.index_buffer = NullCheck.notNull(ib);
    this.array_object = NullCheck.notNull(ao);

    long size = 0L;
    size += ab.getRange().getInterval();
    size += ib.getRange().getInterval();
    this.range = new UnsignedRangeInclusiveL(0L, size - 1L);
  }

  /**
   * Construct a new {@code 8} segment unit sphere.
   *
   * @param g An OpenGL interface
   *
   * @return A new unit sphere
   */

  public static R2UnitSphereType newUnitSphere8(
    final JCGLInterfaceGL33Type g)
  {
    final JCGLUsageHint array_usage = JCGLUsageHint.USAGE_STATIC_DRAW;
    final JCGLUsageHint index_usage = JCGLUsageHint.USAGE_STATIC_DRAW;
    final R2VertexCursorPUNT16 ci = R2VertexCursorPUNT16.getInstance();
    final String name = "sphere8.r2b";
    return R2UnitSphere.newUnitSphere(g, array_usage, index_usage, ci, name);
  }

  private static <T extends
    R2VertexCursorProducerInfoType & R2VertexCursorProducerType<ByteBuffer>>
  R2UnitSphereType newUnitSphere(
    final JCGLInterfaceGL33Type g,
    final JCGLUsageHint array_usage,
    final JCGLUsageHint index_usage,
    final T ci,
    final String name)
  {
    R2UnitSphere.LOG.debug("allocating unit sphere");

    final R2MeshArrayObjectSynchronousAdapterType adapter =
      R2MeshArrayObjectSynchronousAdapter.newAdapter(
        g.getArrayObjects(),
        g.getArrayBuffers(),
        g.getIndexBuffers(),
        array_usage,
        JCGLUnsignedType.TYPE_UNSIGNED_SHORT,
        index_usage,
        ci,
        ci);

    final Class<R2UnitSphere> cc = R2UnitSphere.class;
    try (final InputStream is = cc.getResourceAsStream(name)) {
      try (final ReadableByteChannel chan = Channels.newChannel(is)) {
        final R2MBReaderType r = R2MBUnmappedReader.newReader(chan, adapter);
        r.run();

        if (adapter.hasFailed()) {
          final Optional<Throwable> ex_opt =
            adapter.getErrorException();
          final String ex_msg =
            adapter.getErrorMessage();

          if (ex_opt.isPresent()) {
            throw new R2ExceptionIO(ex_msg, ex_opt.get());
          }
          throw new R2ExceptionIO(ex_msg);
        }

        return new R2UnitSphere(
          adapter.getArrayBuffer(),
          adapter.getArrayObject(),
          adapter.getIndexBuffer());
      }
    } catch (final IOException e) {
      throw new R2ExceptionIO(e.getMessage(), e);
    }
  }

  /**
   * <p>A UV sphere approximates a real sphere by using a fixed number of
   * vertical segments. When using {@code 8} segments, for example, the sphere
   * looks like an octagon when viewed from above. This function returns the
   * interior angle, in radians, of one of the triangles that make up the
   * shape.</p>
   *
   * <p>The following always holds: {@code 2 * π = n * getUVSphereTriangleInteriorAngle(n)}</p>
   *
   * @param s The number of segments
   *
   * @return The interior angle of one of the resulting triangles
   */

  public static double getUVSphereTriangleInteriorAngle(
    final int s)
  {
    Assertive.require(s > 0, "Segment count must be positive");
    return (2.0 * Math.PI) / (double) s;
  }

  /**
   * <p>Calculate the area of one of the triangles that make up an approximation
   * of a circle with radius {@code r} constructed with {@code s} line
   * segments.</p>
   *
   * @param r The radius
   * @param s The number of segments in the approximation
   *
   * @return The approximation area
   */

  public static double getUVSphereApproximationTriangleArea(
    final double r,
    final int s)
  {
    Assertive.require(s > 0, "Segment count must be positive");
    Assertive.require(r > 0.0, "Radius must be positive");

    final double a = R2UnitSphere.getUVSphereTriangleInteriorAngle(s);
    final double rs = r * r;
    return 0.5 * rs * Math.sin(a);
  }

  /**
   * <p>Calculate the scale factor required to completely contain a circle of
   * radius {@code r} inside an approximation with radius {@code r} constructed
   * with {@code s} line segments.</p>
   *
   * @param r The radius
   * @param s The number of segments in the approximation
   *
   * @return The approximation area
   */

  public static double getUVSphereApproximationScaleFactor(
    final double r,
    final int s)
  {
    Assertive.require(s > 0, "Segment count must be positive");
    Assertive.require(r > 0.0, "Radius must be positive");

    final double ac = R2UnitSphere.getCircleArea(r);
    final double aa = R2UnitSphere.getUVSphereApproximationArea(r, s);
    return ac / aa;
  }

  /**
   * <p>Calculate the area of an approximation of a circle with radius {@code r}
   * constructed with {@code s} line segments.</p>
   *
   * @param r The radius
   * @param s The number of segments in the approximation
   *
   * @return The approximation area
   */

  public static double getUVSphereApproximationArea(
    final double r,
    final int s)
  {
    Assertive.require(s > 0, "Segment count must be positive");
    Assertive.require(r > 0.0, "Radius must be positive");
    final double ds = (double) s;
    final double a = R2UnitSphere.getUVSphereApproximationTriangleArea(r, s);
    return ds * a;
  }

  /**
   * <p>Calculate the area of a circle with radius {@code r}.</p>
   *
   * @param r The radius
   *
   * @return The circle area
   */

  public static double getCircleArea(
    final double r)
  {
    Assertive.require(r > 0.0, "Radius must be positive");
    return Math.PI * (r * r);
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    if (!this.isDeleted()) {
      g.getArrayBuffers().arrayBufferDelete(this.array_buffer);
      g.getArrayObjects().arrayObjectDelete(this.array_object);
      g.getIndexBuffers().indexBufferDelete(this.index_buffer);
    }
  }

  @Override
  public JCGLArrayObjectUsableType getArrayObject()
  {
    return this.array_object;
  }

  @Override
  public UnsignedRangeInclusiveL getRange()
  {
    return this.range;
  }

  @Override
  public boolean isDeleted()
  {
    return this.array_buffer.isDeleted();
  }
}
