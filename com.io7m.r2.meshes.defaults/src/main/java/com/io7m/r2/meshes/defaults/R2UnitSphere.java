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

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jcanephora.core.JCGLArrayBufferType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.JCGLIndexBufferType;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2UnitSphereType;
import com.io7m.r2.meshes.loading.api.R2MeshLoaded;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderRequest;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderType;
import com.io7m.r2.meshes.loading.api.R2MeshRequireTangents;
import com.io7m.r2.meshes.loading.api.R2MeshRequireUV;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * The default implementation of the {@link R2UnitSphereType} interface.
 */

public final class R2UnitSphere implements R2UnitSphereType
{
  private final JCGLArrayBufferType array_buffer;
  private final JCGLIndexBufferType index_buffer;
  private final JCGLArrayObjectType array_object;
  private final UnsignedRangeInclusiveL range;

  private R2UnitSphere(
    final JCGLArrayBufferType ab,
    final JCGLArrayObjectType ao,
    final JCGLIndexBufferType ib)
  {
    this.array_buffer = NullCheck.notNull(ab, "Array buffer");
    this.index_buffer = NullCheck.notNull(ib, "Index buffer");
    this.array_object = NullCheck.notNull(ao, "Array object");

    long size = 0L;
    size += ab.byteRange().getInterval();
    size += ib.byteRange().getInterval();
    this.range = new UnsignedRangeInclusiveL(0L, size - 1L);
  }

  /**
   * Construct a new {@code 8} segment unit sphere.
   *
   * @param in_loader A mesh loader
   * @param in_g      An OpenGL interface
   *
   * @return A new unit sphere
   */

  public static R2UnitSphereType newUnitSphere8(
    final R2MeshLoaderType in_loader,
    final JCGLInterfaceGL33Type in_g)
  {
    NullCheck.notNull(in_loader, "Loader");
    NullCheck.notNull(in_g, "G33");
    return load(in_loader, in_g, "unit_sphere8.smfb");
  }

  /**
   * Construct a new {@code 16} segment unit sphere.
   *
   * @param in_loader A mesh loader
   * @param in_g      An OpenGL interface
   *
   * @return A new unit sphere
   */

  public static R2UnitSphereType newUnitSphere16(
    final R2MeshLoaderType in_loader,
    final JCGLInterfaceGL33Type in_g)
  {
    NullCheck.notNull(in_loader, "Loader");
    NullCheck.notNull(in_g, "G33");
    return load(in_loader, in_g, "unit_sphere16.smfb");
  }

  /**
   * Construct a new {@code 32} segment unit sphere.
   *
   * @param in_loader A mesh loader
   * @param in_g      An OpenGL interface
   *
   * @return A new unit sphere
   */

  public static R2UnitSphereType newUnitSphere32(
    final R2MeshLoaderType in_loader,
    final JCGLInterfaceGL33Type in_g)
  {
    NullCheck.notNull(in_loader, "Loader");
    NullCheck.notNull(in_g, "G33");
    return load(in_loader, in_g, "unit_sphere32.smfb");
  }

  private static R2UnitSphereType load(
    final R2MeshLoaderType in_loader,
    final JCGLInterfaceGL33Type in_g,
    final String file)
  {
    final URL url = R2UnitCube.class.getResource(file);
    if (url == null) {
      throw new IllegalStateException(file + " resource is missing");
    }

    try {
      final URI uri = url.toURI();
      final R2MeshLoaderRequest request =
        R2MeshLoaderRequest.of(
          uri,
          R2MeshRequireTangents.R2_TANGENTS_REQUIRED,
          R2MeshRequireUV.R2_UV_REQUIRED,
          JCGLUsageHint.USAGE_STATIC_DRAW,
          JCGLUsageHint.USAGE_STATIC_DRAW);
      final R2MeshLoaded mesh =
        in_loader.loadSynchronously(in_g, request);
      return new R2UnitSphere(
        mesh.arrayBuffer(),
        mesh.newArrayObject(in_g.arrayObjects()),
        mesh.indexBuffer());
    } catch (final URISyntaxException e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * <p>A UV sphere approximates a real sphere by using a fixed number of
   * vertical segments. When using {@code 8} segments, for example, the sphere
   * looks like an octagon when viewed from above. This function returns the
   * interior angle, in radians, of one of the triangles that make up the
   * shape.</p>
   *
   * <p>The following always holds: {@code 2 * π = n * uvSphereTriangleInteriorAngle(n)}</p>
   *
   * @param s The number of segments
   *
   * @return The interior angle of one of the resulting triangles
   */

  public static double uvSphereTriangleInteriorAngle(
    final int s)
  {
    Preconditions.checkPreconditionI(
      s, s > 0, c -> "Segment count must be positive");

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

  public static double uvSphereApproximationTriangleArea(
    final double r,
    final int s)
  {
    Preconditions.checkPreconditionI(
      s, s > 0, c -> "Segment count must be positive");
    Preconditions.checkPreconditionD(
      r, r > 0.0, c -> "Radius must be positive");

    final double a = uvSphereTriangleInteriorAngle(s);
    final double rs = r * r;
    return 0.5 * rs * StrictMath.sin(a);
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

  public static double uvSphereApproximationScaleFactor(
    final double r,
    final int s)
  {
    Preconditions.checkPreconditionI(
      s, s > 0, c -> "Segment count must be positive");
    Preconditions.checkPreconditionD(
      r, r > 0.0, c -> "Radius must be positive");

    final double ac = getCircleArea(r);
    final double aa = uvSphereApproximationArea(r, s);
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

  public static double uvSphereApproximationArea(
    final double r,
    final int s)
  {
    Preconditions.checkPreconditionI(
      s, s > 0, c -> "Segment count must be positive");
    Preconditions.checkPreconditionD(
      r, r > 0.0, c -> "Radius must be positive");

    final double ds = (double) s;
    final double a = uvSphereApproximationTriangleArea(r, s);
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
    Preconditions.checkPreconditionD(
      r, r > 0.0, c -> "Radius must be positive");

    return Math.PI * (r * r);
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    if (!this.isDeleted()) {
      g.arrayBuffers().arrayBufferDelete(this.array_buffer);
      g.arrayObjects().arrayObjectDelete(this.array_object);
      g.indexBuffers().indexBufferDelete(this.index_buffer);
    }
  }

  @Override
  public JCGLArrayObjectUsableType arrayObject()
  {
    return this.array_object;
  }

  @Override
  public UnsignedRangeInclusiveL byteRange()
  {
    return this.range;
  }

  @Override
  public boolean isDeleted()
  {
    return this.array_buffer.isDeleted();
  }
}
