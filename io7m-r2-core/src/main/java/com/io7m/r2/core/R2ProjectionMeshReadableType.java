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

package com.io7m.r2.core;

import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.api.JCGLArrayBuffersType;

/**
 * The type of readable projection meshes.
 */

public interface R2ProjectionMeshReadableType
{
  /**
   * @return The array object for the mesh
   */

  JCGLArrayObjectUsableType getArrayObject();

  /**
   * @return A readable reference to the mesh's projection
   */

  R2ProjectionReadableType getProjectionReadable();

  /**
   * @return {@code true} iff the values of the projection have changed more
   * recently than the last call to
   * {@link R2ProjectionMeshWritableType#updateProjection(JCGLArrayBuffersType)}
   */

  boolean isUpdateRequired();
}
