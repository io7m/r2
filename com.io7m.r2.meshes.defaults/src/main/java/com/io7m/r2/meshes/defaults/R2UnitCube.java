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
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2UnitCubeType;
import com.io7m.r2.meshes.loading.api.R2MeshLoaded;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderRequest;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderType;
import com.io7m.r2.meshes.loading.api.R2MeshRequireTangents;
import com.io7m.r2.meshes.loading.api.R2MeshRequireUV;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * The default implementation of the {@link R2UnitCubeType} interface.
 */

public final class R2UnitCube implements R2UnitCubeType
{
  private final JCGLArrayBufferType array_buffer;
  private final JCGLIndexBufferType index_buffer;
  private final JCGLArrayObjectType array_object;
  private final UnsignedRangeInclusiveL range;

  private R2UnitCube(
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
   * Construct a new unit cube.
   *
   * @param in_loader A mesh loader
   * @param in_g      An OpenGL interface
   *
   * @return A new unit sphere
   */

  public static R2UnitCubeType newUnitCube(
    final R2MeshLoaderType in_loader,
    final JCGLInterfaceGL33Type in_g)
  {
    NullCheck.notNull(in_loader, "Loader");
    NullCheck.notNull(in_g, "G33");

    final URL url = R2UnitCube.class.getResource("unit_cube.smfb");
    if (url == null) {
      throw new IllegalStateException("unit_cube.smfb resource is missing");
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
      return new R2UnitCube(
        mesh.arrayBuffer(),
        mesh.newArrayObject(in_g.arrayObjects()),
        mesh.indexBuffer());
    } catch (final URISyntaxException e) {
      throw new UnreachableCodeException(e);
    }
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
