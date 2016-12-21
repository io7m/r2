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

import com.io7m.junreachable.UnreachableCodeException;

/**
 * Vertex attribute conventions.
 */

public final class R2AttributeConventions
{
  /**
   * The attribute index used to deliver position data to shaders.
   */

  public static final int POSITION_ATTRIBUTE_INDEX = 0;

  /**
   * The attribute index used to deliver UV data to shaders.
   */

  public static final int UV_ATTRIBUTE_INDEX = 1;

  /**
   * The attribute index used to deliver surface normal data to shaders.
   */

  public static final int NORMAL_ATTRIBUTE_INDEX = 2;

  /**
   * The attribute index used to deliver surface tangent4 data to shaders.
   */

  public static final int TANGENT4_ATTRIBUTE_INDEX = 3;

  /**
   * The attribute index used to deliver column 0 of the model matrix for
   * batched instances.
   */

  public static final int BATCHED_MODEL_MATRIX_COLUMN_0_ATTRIBUTE_INDEX = 4;

  /**
   * The attribute index used to deliver column 1 of the model matrix for
   * batched instances.
   */

  public static final int BATCHED_MODEL_MATRIX_COLUMN_1_ATTRIBUTE_INDEX = 5;

  /**
   * The attribute index used to deliver column 2 of the model matrix for
   * batched instances.
   */

  public static final int BATCHED_MODEL_MATRIX_COLUMN_2_ATTRIBUTE_INDEX = 6;

  /**
   * The attribute index used to deliver column 3 of the model matrix for
   * batched instances.
   */

  public static final int BATCHED_MODEL_MATRIX_COLUMN_3_ATTRIBUTE_INDEX = 7;

  /**
   * The attribute index used to deliver a scalar scale value to billboarded
   * instances.
   */

  public static final int BILLBOARDED_ATTRIBUTE_SCALE_INDEX = 1;

  /**
   * The attribute index used to deliver a scalar rotation value to billboarded
   * instances.
   */

  public static final int BILLBOARDED_ATTRIBUTE_ROTATION_INDEX = 2;

  private R2AttributeConventions()
  {
    throw new UnreachableCodeException();
  }
}
