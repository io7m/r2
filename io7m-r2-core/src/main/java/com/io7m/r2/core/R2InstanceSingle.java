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

import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.parameterized.PMatrixReadable3x3FType;
import com.io7m.r2.spaces.R2SpaceTextureType;

/**
 * The default implementation of the {@link R2InstanceSingleType} type.
 */

public final class R2InstanceSingle implements R2InstanceSingleType
{
  private final JCGLArrayObjectUsableType array_object;
  private final R2TransformReadableType   transform;
  private final long                      id;

  private final PMatrixReadable3x3FType<R2SpaceTextureType,
    R2SpaceTextureType> uv;

  private R2InstanceSingle(
    final long in_id,
    final JCGLArrayObjectUsableType in_array_object,
    final R2TransformReadableType in_transform,
    final PMatrixReadable3x3FType<R2SpaceTextureType, R2SpaceTextureType> in_uv)
  {
    this.id = in_id;
    this.array_object = NullCheck.notNull(in_array_object);
    this.transform = NullCheck.notNull(in_transform);
    this.uv = NullCheck.notNull(in_uv);
  }

  /**
   * Construct a new instance with a fresh ID taken from {@code in_pool}.
   *
   * @param in_pool         The ID pool
   * @param in_array_object The array object
   * @param in_transform    The transform
   * @param in_uv           The UV matrix
   *
   * @return A new instance
   */

  public static R2InstanceSingleType newInstance(
    final R2IDPoolType in_pool,
    final JCGLArrayObjectUsableType in_array_object,
    final R2TransformReadableType in_transform,
    final PMatrixReadable3x3FType<R2SpaceTextureType, R2SpaceTextureType> in_uv)
  {
    return new R2InstanceSingle(
      NullCheck.notNull(in_pool).getFreshID(),
      in_array_object,
      in_transform,
      in_uv);
  }

  @Override
  public long getInstanceID()
  {
    return this.id;
  }

  @Override
  public JCGLArrayObjectUsableType getArrayObject()
  {
    return this.array_object;
  }

  @Override
  public R2TransformReadableType getTransform()
  {
    return this.transform;
  }

  @Override
  public PMatrixReadable3x3FType<R2SpaceTextureType, R2SpaceTextureType>
  getUVMatrix()
  {
    return this.uv;
  }
}
