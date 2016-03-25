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

package com.io7m.r2.core.shaders.provided;

import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2Texture2DUsableType;
import org.immutables.value.Value;

/**
 * The type of parameters for the basic depth shader.
 */

@Value.Immutable
@Value.Modifiable
@R2ImmutableStyleType
public interface R2DepthShaderBasicParametersType
{
  /**
   * @return The albedo texture for the surface
   */

  @Value.Parameter
  R2Texture2DUsableType getAlbedoTexture();

  /**
   * The threshold value that defines whether or not the surface fragment will
   * be discarded. If the opacity of the surface is less than this threshold
   * value, the surface will be discarded. A threshold value of {@code 0.0} will
   * result in no fragments ever being discarded.
   *
   * @return The threshold value
   */

  @Value.Parameter
  @Value.Default
  default float getAlphaDiscardThreshold()
  {
    return 0.0f;
  }
}
