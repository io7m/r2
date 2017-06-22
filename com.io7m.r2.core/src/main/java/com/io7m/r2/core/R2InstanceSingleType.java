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
import com.io7m.jtensors.core.parameterized.matrices.PMatrix3x3D;
import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import org.immutables.value.Value;

/**
 * <p>The type of single instances.</p>
 *
 * <p>A single instance consists of a set of one or more vertex buffers
 * containing mesh data, a per-instance UV matrix, and a transform.</p>
 */

@R2ImmutableStyleType
@Value.Immutable
public interface R2InstanceSingleType extends R2InstanceType
{
  @Override
  @Value.Parameter
  long instanceID();

  /**
   * @return The instance array object
   */

  @Value.Parameter
  JCGLArrayObjectUsableType arrayObject();

  /**
   * @return The instance transform
   */

  @Value.Parameter
  R2TransformReadableType transform();

  /**
   * @return The instance UV matrix
   */

  @Value.Parameter
  PMatrix3x3D<R2SpaceTextureType, R2SpaceTextureType> uvMatrix();
}
