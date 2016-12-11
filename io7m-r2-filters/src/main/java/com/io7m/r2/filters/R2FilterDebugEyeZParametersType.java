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

package com.io7m.r2.filters;

import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2MatricesObserverType;
import com.io7m.r2.core.R2TextureDefaultsType;
import org.immutables.value.Value;

/**
 * Parameters for filters that reproduce eye-space Z values.
 */

@Value.Immutable
@Value.Modifiable
@R2ImmutableStyleType
public interface R2FilterDebugEyeZParametersType
{
  /**
   * @return The geometry buffer that will be used to reproduce the eye-space Z
   * position
   */

  @Value.Parameter
  R2GeometryBufferUsableType geometryBuffer();

  /**
   * @return The output eye-Z buffer
   */

  @Value.Parameter
  R2EyeZBufferUsableType eyeZBuffer();

  /**
   * @return The observer matrix values that were used to produce the scene
   */

  @Value.Parameter
  R2MatricesObserverType observerValues();

  /**
   * @return A set of default textures
   */

  @Value.Parameter
  R2TextureDefaultsType textureDefaults();
}
