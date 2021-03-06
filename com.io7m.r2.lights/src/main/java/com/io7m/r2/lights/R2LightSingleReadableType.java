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

package com.io7m.r2.lights;

import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jfunctional.PartialBiFunctionType;
import com.io7m.r2.transforms.R2TransformReadableType;

/**
 * The type of single-instance lights.
 */

public interface R2LightSingleReadableType extends R2LightReadableType
{
  /**
   * @return The array object that will be used to render the light geometry
   */

  JCGLArrayObjectUsableType arrayObject();

  /**
   * @return The transform for the light origin
   */

  R2TransformReadableType transform();

  /**
   * Match on the type of light.
   *
   * @param context   A context value
   * @param on_volume Evaluated for volume lights
   * @param on_screen Evaluated for screen lights
   * @param <A>       The type of context values
   * @param <B>       The type of returned values
   * @param <E>       The type of raised exceptions
   *
   * @return A value of type {@code B}
   *
   * @throws E If any of the given functions raise {@code E}
   */

  <A, B, E extends Throwable>
  B matchLightSingle(
    A context,
    PartialBiFunctionType<A, R2LightVolumeSingleType, B, E> on_volume,
    PartialBiFunctionType<A, R2LightScreenSingleType, B, E> on_screen)
    throws E;
}
