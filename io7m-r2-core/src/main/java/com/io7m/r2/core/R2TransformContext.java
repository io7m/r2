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

import com.io7m.jtensors.Matrix4x4FType;
import com.io7m.jtensors.MatrixHeapArrayM4x4F;
import com.io7m.jtensors.MatrixM4x4F;
import com.io7m.jtensors.parameterized.PMatrixM4x4F;

/**
 * The default implementation of the {@link R2TransformContextType} interface.
 */

public final class R2TransformContext implements R2TransformContextType
{
  private final Matrix4x4FType           m4x4_0;
  private final Matrix4x4FType           m4x4_1;
  private final PMatrixM4x4F.ContextPM4F context_pm4;
  private final MatrixM4x4F.ContextMM4F  context_m4;

  private R2TransformContext()
  {
    this.m4x4_0 = MatrixHeapArrayM4x4F.newMatrix();
    this.m4x4_1 = MatrixHeapArrayM4x4F.newMatrix();
    this.context_m4 = new MatrixM4x4F.ContextMM4F();
    this.context_pm4 = new PMatrixM4x4F.ContextPM4F();
  }

  @Override
  public PMatrixM4x4F.ContextPM4F getContextPM4F()
  {
    return this.context_pm4;
  }

  @Override
  public MatrixM4x4F.ContextMM4F getContextMM4F()
  {
    return this.context_m4;
  }

  /**
   * @return A new transform context
   */

  public static R2TransformContextType newContext()
  {
    return new R2TransformContext();
  }

  @Override
  public Matrix4x4FType getTemporaryMatrix4x4_0()
  {
    return this.m4x4_0;
  }

  @Override
  public Matrix4x4FType getTemporaryMatrix4x4_1()
  {
    return this.m4x4_1;
  }
}
