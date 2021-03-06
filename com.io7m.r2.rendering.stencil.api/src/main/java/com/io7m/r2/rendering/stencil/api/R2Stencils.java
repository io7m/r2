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

package com.io7m.r2.rendering.stencil.api;

import com.io7m.junreachable.UnreachableCodeException;

/**
 * Stencil values.
 */

public final class R2Stencils
{
  private static final int ALLOW_BIT = 0b1000_0000;

  private static final int LIGHT_MASK_BIT = 0b0000_0001;

  private static final int GROUP_BITS = 0b0111_1000;

  private static final int GROUP_LEFT_SHIFT = 3;

  private static final int MAXIMUM_GROUPS = 16;

  private R2Stencils()
  {
    throw new UnreachableCodeException();
  }

  /**
   * @param x The group value
   *
   * @return {@code true} iff the given value is a valid stencil group value
   */

  public static boolean isValidGroup(
    final int x)
  {
    return x > 0 && x < maximumGroups();
  }

  /**
   * Check that {@code x} is a valid group number.
   *
   * @param x The group number
   *
   * @return {@code x}
   *
   * @throws R2ExceptionStencilInvalidGroup Iff {@code x} is not valid
   */

  public static int checkValidGroup(final int x)
    throws R2ExceptionStencilInvalidGroup
  {
    if (!isValidGroup(x)) {
      throw new R2ExceptionStencilInvalidGroup(
        String.format(
          "Group number %d is not in the range [1, %d]",
          Integer.valueOf(x),
          Integer.valueOf(maximumGroups() - 1)));
    }
    return x;
  }

  /**
   * Only pixels with a corresponding stencil value that contains the <i>allow
   * bit</i> are touched by rendering operations.
   *
   * @return The allow bit
   */

  public static int allowBit()
  {
    return ALLOW_BIT;
  }

  /**
   * @return The stencil bit used to mask the contributions of lights.
   */

  public static int lightMaskBit()
  {
    return LIGHT_MASK_BIT;
  }

  /**
   * @return The bits used to store group values.
   */

  public static int groupBits()
  {
    return GROUP_BITS;
  }

  /**
   * @return The left shift used to store group values;
   */

  public static int groupLeftShift()
  {
    return GROUP_LEFT_SHIFT;
  }

  /**
   * @return The maximum number of groups in a scene.
   */

  public static int maximumGroups()
  {
    return MAXIMUM_GROUPS;
  }
}
