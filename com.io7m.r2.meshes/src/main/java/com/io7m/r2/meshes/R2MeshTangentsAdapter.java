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

package com.io7m.r2.meshes;

import com.io7m.jaffirm.core.Postconditions;
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.core.parameterized.vectors.PVector2D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.jtensors.core.parameterized.vectors.PVectors3D;
import com.io7m.r2.spaces.R2SpaceObjectType;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * An implementation of the {@link R2MeshParserInterleavedListenerType}
 * interface that produces a value of type {@link R2MeshTangentsType} after
 * parsing has completed.
 */

public final class R2MeshTangentsAdapter implements R2MeshTangentsAdapterType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MeshTangentsAdapter.class);
  }

  private final R2MeshTangentsBuilderType builder;
  private final R2ErrorConsumerType error;
  private final Long2LongMap vertices;
  private Optional<R2MeshTangentsType> mesh;
  private State state;
  private long vertex_pos;
  private long vertex_nor;
  private long vertex_tan;
  private long vertex_uv;
  private long vertex_bit;
  private PVector3D<R2SpaceObjectType> normal;

  private R2MeshTangentsAdapter(
    final R2ErrorConsumerType in_error,
    final R2MeshTangentsBuilderType in_builder)
  {
    this.builder = NullCheck.notNull(in_builder, "Builder");
    this.mesh = Optional.empty();
    this.state = State.RUNNING;
    this.error = NullCheck.notNull(in_error, "Error consumer");
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

  @Override
  public Optional<R2MeshTangentsType> mesh()
  {
    return this.mesh;
  }

  @Override
  public void onEventStart()
  {

  }

  @Override
  public void onEventVertexCount(final long count)
  {
    Preconditions.checkPrecondition(
      this.state,
      this.state == State.RUNNING,
      s -> "State must be RUNNING");

    LOG.trace(
      "expecting {} vertices", Long.valueOf(count));
  }

  @Override
  public void onEventTriangleCount(final long count)
  {
    Preconditions.checkPrecondition(
      this.state,
      this.state == State.RUNNING,
      s -> "State must be RUNNING");

    LOG.trace(
      "expecting {} triangles", Long.valueOf(count));
  }

  @Override
  public void onError(
    final Optional<Throwable> e,
    final String message)
  {
    this.state = State.FAILED;
    this.error.onError(e, message);
  }

  @Override
  public void onEventVertexStarted(final long index)
  {
    Preconditions.checkPrecondition(
      this.state,
      this.state == State.RUNNING,
      s -> "State must be RUNNING");

    this.vertex_pos = -1L;
    this.vertex_nor = -1L;
    this.vertex_tan = -1L;
    this.vertex_uv = -1L;
    this.vertex_bit = -1L;
  }

  @Override
  public void onEventVertexPosition(
    final long index,
    final double x,
    final double y,
    final double z)
  {
    Preconditions.checkPrecondition(
      this.state,
      this.state == State.RUNNING,
      s -> "State must be RUNNING");

    this.vertex_pos = this.builder.addPosition(PVector3D.of(x, y, z));

    Postconditions.checkPostconditionL(
      this.vertex_pos,
      this.vertex_pos == index,
      i -> "Vertex position index must be correct");
  }

  @Override
  public void onEventVertexNormal(
    final long index,
    final double x,
    final double y,
    final double z)
  {
    Preconditions.checkPrecondition(
      this.state,
      this.state == State.RUNNING,
      s -> "State must be RUNNING");

    final PVector3D<R2SpaceObjectType> n = PVector3D.of(x, y, z);
    this.normal = n;
    this.vertex_nor = this.builder.addNormal(n);

    Postconditions.checkPostconditionL(
      this.vertex_nor,
      this.vertex_nor == index,
      i -> "Vertex normal index be correct");
  }

  @Override
  public void onEventVertexTangent(
    final long index,
    final double x,
    final double y,
    final double z,
    final double w)
  {
    Preconditions.checkPrecondition(
      this.state,
      this.state == State.RUNNING,
      s -> "State must be RUNNING");

    Preconditions.checkPrecondition(
      this.normal,
      this.normal != null,
      n -> "Normal must not be NULL");

    final PVector4D<R2SpaceObjectType> t = PVector4D.of(x, y, z, w);
    this.vertex_tan = this.builder.addTangent(t);

    Postconditions.checkPostconditionL(
      this.vertex_tan,
      this.vertex_tan == index,
      i -> "Vertex tangent index be correct");

    final PVector3D<R2SpaceObjectType> n = this.normal;
    this.vertex_bit = this.builder.addBitangent(
      PVectors3D.crossProduct(
        n,
        PVectors3D.scale(PVector3D.of(x, y, z), t.w())));
    this.normal = null;
  }

  @Override
  public void onEventVertexUV(
    final long index,
    final double x,
    final double y)
  {
    Preconditions.checkPrecondition(
      this.state,
      this.state == State.RUNNING,
      s -> "State must be RUNNING");

    this.vertex_uv = this.builder.addUV(PVector2D.of(x, y));

    Postconditions.checkPostconditionL(
      this.vertex_uv,
      this.vertex_uv == index,
      i -> "Vertex UV index be correct");
  }

  @Override
  public void onEventVertexFinished(
    final long index)
  {
    Preconditions.checkPrecondition(
      this.state,
      this.state == State.RUNNING,
      s -> "State must be RUNNING");

    Preconditions.checkPrecondition(
      this.vertex_pos != -1L, "Vertex position must not equal -1");
    Preconditions.checkPrecondition(
      this.vertex_nor != -1L, "Vertex normal must not equal -1");
    Preconditions.checkPrecondition(
      this.vertex_tan != -1L, "Vertex tangent must not equal -1");
    Preconditions.checkPrecondition(
      this.vertex_uv != -1L, "Vertex UV must not equal -1");
    Preconditions.checkPrecondition(
      this.vertex_bit != -1L, "Vertex bitangent must not equal -1");

    final long v = this.builder.addVertex(
      this.vertex_pos,
      this.vertex_nor,
      this.vertex_tan,
      this.vertex_bit,
      this.vertex_uv);
    this.vertices.put(index, v);
  }

  @Override
  public void onEventVerticesFinished()
  {
    Preconditions.checkPrecondition(
      this.state,
      this.state == State.RUNNING,
      s -> "State must be RUNNING");
  }

  @Override
  public void onEventTriangle(
    final long index,
    final long v0,
    final long v1,
    final long v2)
  {
    Preconditions.checkPrecondition(
      this.state,
      this.state == State.RUNNING,
      s -> "State must be RUNNING");

    final long vr0 = this.vertices.get(v0);
    final long vr1 = this.vertices.get(v1);
    final long vr2 = this.vertices.get(v2);
    final long r = this.builder.addTriangle(vr0, vr1, vr2);

    Postconditions.checkPostcondition(
      index == r, "Index must match triangle index");
  }

  @Override
  public void onEventTrianglesFinished()
  {
    Preconditions.checkPrecondition(
      this.state,
      this.state == State.RUNNING,
      s -> "State must be RUNNING");
  }

  @Override
  public void onEventFinished()
  {
    Preconditions.checkPrecondition(
      this.state,
      this.state == State.RUNNING,
      s -> "State must be RUNNING");

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
