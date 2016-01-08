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

package com.io7m.r2.meshes.tools;

import com.io7m.jnull.NullCheck;

/**
 * Available mesh formats.
 */

public enum R2MeshFileFormat
{
  /**
   * The Wavefront OBJ format.
   */

  MESH_FILE_FORMAT_OBJ("obj"),

  /**
   * Uncompressed R2B format.
   */

  MESH_FILE_FORMAT_R2MB("r2b"),

  /**
   * GZip compressed R2B format.
   */

  MESH_FILE_FORMAT_R2MBZ("r2z");

  private final String name;

  R2MeshFileFormat(final String in_name)
  {
    this.name = NullCheck.notNull(in_name);
  }

  @Override
  public String toString()
  {
    return this.name;
  }

  /**
   * @return The short name of the format
   */

  public String getName()
  {
    return this.name;
  }
}
