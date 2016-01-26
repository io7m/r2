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

package com.io7m.r2.core.cursors;

import com.io7m.jcanephora.core.JCGLScalarType;
import net.jcip.annotations.ThreadSafe;

/**
 * Information about cursors that can be produced by a {@link
 * R2VertexCursorProducerType} implementation.
 */

@ThreadSafe
public interface R2VertexCursorProducerInfoType
{
  /**
   * @return The scalar type used to hold the elements of position vectors
   */

  JCGLScalarType getPositionElementType();

  /**
   * @return The offset in octets of the position vector from the start of each
   * vertex
   */

  long getPositionOffset();

  /**
   * @return The scalar type used to hold the elements of normal vectors
   */

  JCGLScalarType getNormalElementType();

  /**
   * @return The offset in octets of the normal vector from the start of each
   * vertex
   */

  long getNormalOffset();

  /**
   * @return The scalar type used to hold the elements of UV vectors
   */

  JCGLScalarType getUVElementType();

  /**
   * @return The offset in octets of the UV vector from the start of each vertex
   */

  long getUVOffset();

  /**
   * @return The scalar type used to hold the elements of tangent vectors
   */

  JCGLScalarType getTangent4ElementType();

  /**
   * @return The offset in octets of the tangent4 vector from the start of each
   * vertex
   */

  long getTangent4Offset();

  /**
   * @return The size in octets of one vertex
   */

  long getVertexSize();
}
