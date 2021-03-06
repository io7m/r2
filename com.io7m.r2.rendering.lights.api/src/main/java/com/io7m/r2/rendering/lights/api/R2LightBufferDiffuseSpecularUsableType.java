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

package com.io7m.r2.rendering.lights.api;

import com.io7m.jfunctional.PartialBiFunctionType;

/**
 * The type of usable diffuse+specular light buffers.
 */

public interface R2LightBufferDiffuseSpecularUsableType
  extends R2LightBufferUsableType,
  R2LightBufferWithDiffuseUsableType,
  R2LightBufferWithSpecularUsableType
{
  @Override
  default <A, B, E extends Throwable> B matchLightBuffer(
    final A context,
    final PartialBiFunctionType<A, R2LightBufferDiffuseOnlyUsableType, B, E> on_diffuse,
    final PartialBiFunctionType<A, R2LightBufferSpecularOnlyUsableType, B, E> on_specular,
    final PartialBiFunctionType<A, R2LightBufferDiffuseSpecularUsableType, B, E> on_diffuse_specular)
    throws E
  {
    return on_diffuse_specular.call(context, this);
  }
}
