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

package com.io7m.r2.meshes.api;

import com.io7m.r2.annotations.R2ImmutableStyleType;
import org.immutables.value.Value;

/**
 * The type of triangles.
 */

@R2ImmutableStyleType
@Value.Immutable
public interface R2MeshTriangleType
{
  /**
   * @return The index of the first vertex
   */

  @Value.Parameter
  long v0();

  /**
   * @return The index of the second vertex
   */

  @Value.Parameter
  long v1();

  /**
   * @return The index of the third vertex
   */

  @Value.Parameter
  long v2();

  /**
   * Check preconditions for the type.
   */

  @Value.Check
  default void checkPreconditions()
  {
    if (this.v0() == this.v1() || this.v1() == this.v2() || this.v0() == this.v2()) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Malformed triangle.\n");
      sb.append("Duplicate vertex indices.\n");
      sb.append("\n");
      sb.append("Indices: ");
      sb.append(this.v0());
      sb.append(" ");
      sb.append(this.v1());
      sb.append(" ");
      sb.append(this.v2());
      throw new R2MeshExceptionMalformedTriangle(sb.toString());
    }
  }
}
