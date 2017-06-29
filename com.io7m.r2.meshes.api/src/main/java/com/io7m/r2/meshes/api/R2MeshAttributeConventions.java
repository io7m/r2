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

import com.io7m.junreachable.UnreachableCodeException;

/**
 * Mesh attribute conventions.
 */

public final class R2MeshAttributeConventions
{
  private static final int POSITION_ATTRIBUTE_INDEX = 0;

  private static final int UV_ATTRIBUTE_INDEX = 1;

  private static final int NORMAL_ATTRIBUTE_INDEX = 2;

  private static final int TANGENT4_ATTRIBUTE_INDEX = 3;

  private static final int BATCHED_MODEL_MATRIX_COLUMN_0_ATTRIBUTE_INDEX = 4;

  private static final int BATCHED_MODEL_MATRIX_COLUMN_1_ATTRIBUTE_INDEX = 5;

  private static final int BATCHED_MODEL_MATRIX_COLUMN_2_ATTRIBUTE_INDEX = 6;

  private static final int BATCHED_MODEL_MATRIX_COLUMN_3_ATTRIBUTE_INDEX = 7;

  private static final int USER_ATTRIBUTES_INDEX = 8;

  private static final int BILLBOARDED_ATTRIBUTE_SCALE_INDEX = 1;

  private static final int BILLBOARDED_ATTRIBUTE_ROTATION_INDEX = 2;

  private R2MeshAttributeConventions()
  {
    throw new UnreachableCodeException();
  }

  /**
   * @return The attribute index used to deliver position data to shaders.
   */

  public static int positionAttributeIndex()
  {
    return POSITION_ATTRIBUTE_INDEX;
  }

  /**
   * @return The attribute index used to deliver UV data to shaders.
   */

  public static int uvAttributeIndex()
  {
    return UV_ATTRIBUTE_INDEX;
  }

  /**
   * @return The attribute index used to deliver surface normal data to shaders.
   */

  public static int normalAttributeIndex()
  {
    return NORMAL_ATTRIBUTE_INDEX;
  }

  /**
   * @return The attribute index used to deliver surface tangent4 data to
   * shaders.
   */

  public static int tangent4AttributeIndex()
  {
    return TANGENT4_ATTRIBUTE_INDEX;
  }

  /**
   * @return The attribute index used to deliver column 0 of the model matrix
   * for batched instances.
   */

  public static int batchedModelMatrixColumn0AttributeIndex()
  {
    return BATCHED_MODEL_MATRIX_COLUMN_0_ATTRIBUTE_INDEX;
  }

  /**
   * @return The attribute index used to deliver column 1 of the model matrix
   * for batched instances.
   */

  public static int batchedModelMatrixColumn1AttributeIndex()
  {
    return BATCHED_MODEL_MATRIX_COLUMN_1_ATTRIBUTE_INDEX;
  }

  /**
   * @return The attribute index used to deliver column 2 of the model matrix
   * for batched instances.
   */

  public static int batchedModelMatrixColumn2AttributeIndex()
  {
    return BATCHED_MODEL_MATRIX_COLUMN_2_ATTRIBUTE_INDEX;
  }

  /**
   * @return The attribute index used to deliver column 3 of the model matrix
   * for batched instances.
   */

  public static int batchedModelMatrixColumn3AttributeIndex()
  {
    return BATCHED_MODEL_MATRIX_COLUMN_3_ATTRIBUTE_INDEX;
  }

  /**
   * @return The index of the first available user attribute.
   */

  public static int userAttributesIndex()
  {
    return USER_ATTRIBUTES_INDEX;
  }

  /**
   * @return The attribute index used to deliver a scalar scale value to
   * billboarded instances.
   */

  public static int billboardedAttributeScaleIndex()
  {
    return BILLBOARDED_ATTRIBUTE_SCALE_INDEX;
  }

  /**
   * @return The attribute index used to deliver a scalar rotation value to
   * billboarded instances.
   */

  public static int billboardedAttributeRotationIndex()
  {
    return BILLBOARDED_ATTRIBUTE_ROTATION_INDEX;
  }
}
