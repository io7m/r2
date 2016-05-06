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

import com.io7m.jfunctional.PartialFunctionType;

/**
 * The type of meshes.
 */

public interface R2MeshType
{
  /**
   * Match a mesh type.
   *
   * @param on_basic    Evaluated on basic meshes
   * @param on_tangents Evaluated on meshes with tangents
   * @param <A>         The type of returned values
   * @param <E>         The type of raised exceptions
   *
   * @return A value of {@code A}
   *
   * @throws E If the given functions raise {@code E}
   */

  <A, E extends Exception> A matchMesh(
    PartialFunctionType<R2MeshBasicType, A, E> on_basic,
    PartialFunctionType<R2MeshTangentsType, A, E> on_tangents)
    throws E;
}
