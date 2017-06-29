/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.meshes.loading.api;

import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.r2.annotations.R2ImmutableStyleType;
import org.immutables.value.Value;

import java.net.URI;

/**
 * A request to load a mesh.
 */

@R2ImmutableStyleType
@Value.Immutable
public interface R2MeshLoaderRequestType
{
  /**
   * @return The URI of the mesh that will be loaded
   */

  @Value.Parameter(order = 0)
  URI uri();

  /**
   * @return A specification of whether or not the mesh must have tangent
   * vectors
   */

  @Value.Parameter(order = 1)
  R2MeshRequireTangents requireTangents();

  /**
   * @return A specification of whether or not the mesh must have UV coordinates
   */

  @Value.Parameter(order = 1)
  R2MeshRequireUV requireUV();

  /**
   * @return A usage hint that will be used when creating an array buffer to
   * hold vertex data
   */

  @Value.Parameter(order = 2)
  JCGLUsageHint arrayBufferUsageHint();

  /**
   * @return A usage hint that will be used when creating an index buffer to
   * hold index data
   */

  @Value.Parameter(order = 3)
  JCGLUsageHint indexBufferUsageHint();
}
