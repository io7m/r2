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

package com.io7m.r2.core.debug;

import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2TransformReadableType;
import com.io7m.r2.spaces.R2SpaceRGBAType;
import org.immutables.value.Value;

/**
 * A cube.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2DebugCubeInstanceType
{
  /**
   * @return The transform applied to the center of the cube
   */

  @Value.Parameter
  R2TransformReadableType transform();

  /**
   * @return The color of the cube
   */

  @Value.Parameter
  PVector4D<R2SpaceRGBAType> color();
}
