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

package com.io7m.r2.meshes;

import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.parameterized.PVectorI2D;
import com.io7m.jtensors.parameterized.PVectorI3D;
import com.io7m.jtensors.parameterized.PVectorI4D;
import com.io7m.r2.spaces.R2SpaceObjectType;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valid4j.Assertive;

import java.util.Optional;

/**
 * An implementation of the {@link R2MeshParserListenerType} interface that
 * produces a value of type {@link R2MeshTangentsType} after parsing has
 * completed.
 */

public final class R2MeshTangentsAdapter implements R2MeshTangentsAdapterType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MeshTangentsAdapter.class);
  }

  private final R2MeshTangentsBuilderType     builder;
  private final R2ErrorConsumerType           error;
  private final Long2LongMap                  vertices;
  private       Optional<R2MeshTangentsType>  mesh;
  private       State                         state;
  private       long                          vertex_pos;
  private       long                          vertex_nor;
  private       long                          vertex_tan;
  private       long                          vertex_uv;
  private       long                          vertex_bit;
  private       PVectorI3D<R2SpaceObjectType> normal;

  private R2MeshTangentsAdapter(
    final R2ErrorConsumerType in_error,
    final R2MeshTangentsBuilderType in_builder)
  {
    this.builder = NullCheck.notNull(in_builder);
    this.mesh = Optional.empty();
    this.state = State.RUNNING;
    this.error = NullCheck.notNull(in_error);
    this.normal = null;
    this.vertices = new Long2LongOpenHashMap();
  }

  /**
   * @param in_error An error handler
   *
   * @return A new adapter
   */

  public static R2MeshTangentsAdapterType newAdapter(
    final R2ErrorConsumerType in_error)
  {
    return new R2MeshTangentsAdapter(
      in_error, R2MeshTangents.newBuilder(8192L, 8192L));
  }

  @Override public Optional<R2MeshTangentsType> getMesh()
  {
    return this.mesh;
  }

  @Override public void onEventVertexCount(final long count)
  {
    Assertive.require(this.state == State.RUNNING);
    R2MeshTangentsAdapter.LOG.trace(
      "expecting {} vertices", Long.valueOf(count));
  }

  @Override public void onEventTriangleCount(final long count)
  {
    Assertive.require(this.state == State.RUNNING);
    R2MeshTangentsAdapter.LOG.trace(
      "expecting {} triangles", Long.valueOf(count));
  }

  @Override public void onError(
    final Optional<Throwable> e,
    final String message)
  {
    this.state = State.FAILED;
    this.error.onError(e, message);
  }

  @Override public void onEventVertexStarted(final long index)
  {
    Assertive.require(this.state == State.RUNNING);
    this.vertex_pos = -1L;
    this.vertex_nor = -1L;
    this.vertex_tan = -1L;
    this.vertex_uv = -1L;
    this.vertex_bit = -1L;
  }

  @Override public void onEventVertexPosition(
    final long index,
    final double x,
    final double y,
    final double z)
  {
    Assertive.require(this.state == State.RUNNING);
    this.vertex_pos = this.builder.addPosition(new PVectorI3D<>(x, y, z));
    Assertive.ensure(this.vertex_pos == index);
  }

  @Override public void onEventVertexNormal(
    final long index,
    final double x,
    final double y,
    final double z)
  {
    Assertive.require(this.state == State.RUNNING);
    final PVectorI3D<R2SpaceObjectType> n = new PVectorI3D<>(x, y, z);
    this.normal = n;
    this.vertex_nor = this.builder.addNormal(n);
    Assertive.ensure(this.vertex_nor == index);
  }

  @Override public void onEventVertexTangent(
    final long index,
    final double x,
    final double y,
    final double z,
    final double w)
  {
    Assertive.require(this.state == State.RUNNING);
    Assertive.ensure(this.normal != null);

    final PVectorI4D<R2SpaceObjectType> t = new PVectorI4D<>(x, y, z, w);
    this.vertex_tan = this.builder.addTangent(t);
    Assertive.ensure(this.vertex_tan == index);
    final PVectorI3D<R2SpaceObjectType> n = this.normal;
    this.vertex_bit = this.builder.addBitangent(
      PVectorI3D.crossProduct(n, PVectorI3D.scale(t, t.getWD())));
    this.normal = null;
  }

  @Override public void onEventVertexUV(
    final long index,
    final double x,
    final double y)
  {
    Assertive.require(this.state == State.RUNNING);
    this.vertex_uv = this.builder.addUV(new PVectorI2D<>(x, y));
    Assertive.ensure(this.vertex_uv == index);
  }

  @Override public void onEventVertexFinished(
    final long index)
  {
    Assertive.require(this.state == State.RUNNING);
    Assertive.ensure(this.vertex_pos != -1L);
    Assertive.ensure(this.vertex_nor != -1L);
    Assertive.ensure(this.vertex_tan != -1L);
    Assertive.ensure(this.vertex_uv != -1L);
    Assertive.ensure(this.vertex_bit != -1L);

    final long v = this.builder.addVertex(
      this.vertex_pos,
      this.vertex_nor,
      this.vertex_tan,
      this.vertex_bit,
      this.vertex_uv);
    this.vertices.put(index, v);
  }

  @Override public void onEventVerticesFinished()
  {
    Assertive.require(this.state == State.RUNNING);
  }

  @Override public void onEventTriangle(
    final long index,
    final long v0,
    final long v1,
    final long v2)
  {
    Assertive.require(this.state == State.RUNNING);
    final long vr0 = this.vertices.get(v0);
    final long vr1 = this.vertices.get(v1);
    final long vr2 = this.vertices.get(v2);
    final long r = this.builder.addTriangle(vr0, vr1, vr2);
    Assertive.ensure(index == r);
  }

  @Override public void onEventTrianglesFinished()
  {
    Assertive.require(this.state == State.RUNNING);
    this.state = State.FINISHED;
    this.mesh = Optional.of(this.builder.build());
  }

  private enum State
  {
    RUNNING,
    FINISHED,
    FAILED
  }
}
