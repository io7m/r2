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

package com.io7m.r2.images.api;

import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;

import java.util.function.BiFunction;

/**
 * A function that, given a render target description and an inclusive area,
 * returns a new description of the same type with the given area.
 */

public final class R2ImageBufferDescriptionScaler implements
  BiFunction<R2ImageBufferDescription, AreaSizeL, R2ImageBufferDescription>
{
  private static final R2ImageBufferDescriptionScaler INSTANCE =
    new R2ImageBufferDescriptionScaler();

  private R2ImageBufferDescriptionScaler()
  {

  }

  /**
   * @return A function that, given a render target description and an inclusive
   * area, returns a new description of the same type with the given area
   */

  public static R2ImageBufferDescriptionScaler get()
  {
    return INSTANCE;
  }

  @Override
  public R2ImageBufferDescription apply(
    final R2ImageBufferDescription d,
    final AreaSizeL a)
  {
    return d.withArea(a);
  }
}
