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

package com.io7m.r2.core;

import com.io7m.junreachable.UnreachableCodeException;

/**
 * Stencil constants.
 */

public final class R2Stencils
{
  /**
   * Only pixels with a corresponding {@code ALLOW_BIT} in the stencil buffer
   * will be affected by rendering.
   */

  public static final int ALLOW_BIT;

  /**
   * Pixels that have ever contained geometry will have a corresponding {@code
   * GEOMETRY_BIT} in the stencil buffer.
   */

  public static final int GEOMETRY_BIT;

  /**
   * Pixels that contain geometry due to the most recent rendering pass will
   * have a corresponding {@code GEOMETRY_MOST_RECENT_BIT} in the stencil
   * buffer.
   */

  public static final int GEOMETRY_MOST_RECENT_BIT;

  static {
    ALLOW_BIT = 0b00000000_00000000_00000000_00000001;
    GEOMETRY_BIT = 0b00000000_00000000_00000000_00000010;
    GEOMETRY_MOST_RECENT_BIT = 0b00000000_00000000_00000000_00000100;
  }

  private R2Stencils()
  {
    throw new UnreachableCodeException();
  }
}
