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

package com.io7m.r2.tests.core;

import com.io7m.jtensors.core.parameterized.matrices.PMatrices3x3D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix3x3D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceNormalEyeType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;

public final class R2EmptyInstanceTransformValues implements
  R2MatricesInstanceSingleValuesType
{
  @Override
  public PMatrix4x4D<R2SpaceObjectType, R2SpaceEyeType> matrixModelView()
  {
    return PMatrices4x4D.identity();
  }

  @Override
  public PMatrix3x3D<R2SpaceObjectType, R2SpaceNormalEyeType> matrixNormal()
  {
    return PMatrices3x3D.identity();
  }

  @Override
  public PMatrix3x3D<R2SpaceTextureType, R2SpaceTextureType> matrixUV()
  {
    return PMatrices3x3D.identity();
  }
}
