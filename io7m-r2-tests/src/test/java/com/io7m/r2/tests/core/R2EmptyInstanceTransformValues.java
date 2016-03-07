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

import com.io7m.jtensors.parameterized.PMatrixDirectM3x3F;
import com.io7m.jtensors.parameterized.PMatrixDirectM4x4F;
import com.io7m.jtensors.parameterized.PMatrixDirectReadable3x3FType;
import com.io7m.jtensors.parameterized.PMatrixDirectReadable4x4FType;
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceNormalEyeType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;

public final class R2EmptyInstanceTransformValues implements
  R2MatricesInstanceSingleValuesType
{
  @Override
  public PMatrixDirectReadable4x4FType<R2SpaceObjectType, R2SpaceEyeType>
  getMatrixModelView()
  {
    return PMatrixDirectM4x4F.newMatrix();
  }

  @Override
  public PMatrixDirectReadable3x3FType<R2SpaceObjectType,
    R2SpaceNormalEyeType> getMatrixNormal()
  {
    return PMatrixDirectM3x3F.newMatrix();
  }

  @Override
  public PMatrixDirectReadable3x3FType<R2SpaceTextureType,
    R2SpaceTextureType> getMatrixUV()
  {
    return PMatrixDirectM3x3F.newMatrix();
  }
}
