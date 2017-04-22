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

import com.io7m.jcanephora.core.JCGLResourceUsableType;

import java.util.function.BiFunction;

/**
 * The type of usable textures.
 */

public interface R2TextureUsableType extends JCGLResourceUsableType
{
  /**
   * Match on the type of texture.
   *
   * @param context A user-defined context value
   * @param on_2d   Evaluated on values of type {@link R2Texture2DUsableType}
   * @param on_cube Evaluated on values of type {@link R2TextureCubeUsableType}
   * @param <A>     The type of context values
   * @param <B>     The type of returned values
   *
   * @return The value returned by one of the given functions
   */

  <A, B> B matchTexture(
    A context,
    BiFunction<A, R2Texture2DUsableType, B> on_2d,
    BiFunction<A, R2TextureCubeUsableType, B> on_cube);
}
