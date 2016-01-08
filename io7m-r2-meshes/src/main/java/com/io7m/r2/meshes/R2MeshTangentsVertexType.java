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

import org.immutables.value.Value;

/**
 * The type of basic mesh vertices.
 */

@Value.Immutable public interface R2MeshTangentsVertexType
{
  /**
   * @return The index of the position attribute
   */

  @Value.Parameter long getPositionIndex();

  /**
   * @return The index of the normal attribute
   */

  @Value.Parameter long getNormalIndex();

  /**
   * @return The index of the tangent attribute
   */

  @Value.Parameter long getTangentIndex();

  /**
   * @return The index of the bitangent attribute
   */

  @Value.Parameter long getBitangentIndex();

  /**
   * @return The index of the UV attribute
   */

  @Value.Parameter long getUVIndex();
}
