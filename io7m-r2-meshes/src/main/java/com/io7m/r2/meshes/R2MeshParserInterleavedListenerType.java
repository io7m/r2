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

/**
 * The type of mesh parser listeners that deliver interleaved data to
 * consumers.
 */

public interface R2MeshParserInterleavedListenerType extends R2ErrorConsumerType
{
  /**
   * Called unconditionally at the start of parsing.
   */

  void onEventStart();

  /**
   * The number of vertices was received.
   *
   * @param count The number of vertices
   */

  void onEventVertexCount(long count);

  /**
   * The number of triangles was received.
   *
   * @param count The number of triangles
   */

  void onEventTriangleCount(long count);

  /**
   * Vertex {@code index} is starting.
   *
   * @param index The vertex index
   */

  void onEventVertexStarted(long index);

  /**
   * A vertex position was received.
   *
   * The given {@code index} will be equal to the {@code index} that was
   * received by the most recent call to {@link #onEventVertexStarted(long)}.
   *
   * @param index The vertex number
   * @param x     The {@code X} coordinate
   * @param y     The {@code Y} coordinate
   * @param z     The {@code Z} coordinate
   */

  void onEventVertexPosition(
    long index,
    double x,
    double y,
    double z);

  /**
   * A vertex normal was received.
   *
   * The given {@code index} will be equal to the {@code index} that was
   * received by the most recent call to {@link #onEventVertexStarted(long)}.
   *
   * @param index The vertex number
   * @param x     The {@code X} coordinate
   * @param y     The {@code Y} coordinate
   * @param z     The {@code Z} coordinate
   */

  void onEventVertexNormal(
    long index,
    double x,
    double y,
    double z);

  /**
   * A vertex tangent was received.
   *
   * The given {@code index} will be equal to the {@code index} that was
   * received by the most recent call to {@link #onEventVertexStarted(long)}.
   *
   * @param index The vertex number
   * @param x     The {@code X} coordinate
   * @param y     The {@code Y} coordinate
   * @param z     The {@code Z} coordinate
   * @param w     The {@code W} coordinate
   */

  void onEventVertexTangent(
    long index,
    double x,
    double y,
    double z,
    double w);

  /**
   * A vertex UV coordinate was received.
   *
   * The given {@code index} will be equal to the {@code index} that was
   * received by the most recent call to {@link #onEventVertexStarted(long)}.
   *
   * @param index The vertex number
   * @param x     The {@code X} coordinate
   * @param y     The {@code Y} coordinate
   */

  void onEventVertexUV(
    long index,
    double x,
    double y);

  /**
   * Vertex {@code index} is finished.
   *
   * The given {@code index} will be equal to the {@code index} that was
   * received by the most recent call to {@link #onEventVertexStarted(long)}.
   *
   * @param index The vertex index
   */

  void onEventVertexFinished(long index);

  /**
   * All vertices have been parsed.
   */

  void onEventVerticesFinished();

  /**
   * A triangle was received.
   *
   * @param index The triangle number
   * @param v0    The index of the first vertex
   * @param v1    The index of the second vertex
   * @param v2    The index of the third vertex
   */

  void onEventTriangle(
    long index,
    long v0,
    long v1,
    long v2);

  /**
   * All triangles have been parsed.
   */

  void onEventTrianglesFinished();

  /**
   * Called unconditionally at the end of parsing.
   */

  void onEventFinished();
}
