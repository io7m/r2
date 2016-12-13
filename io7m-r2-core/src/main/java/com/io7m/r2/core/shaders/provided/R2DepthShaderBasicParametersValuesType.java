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

import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureDefaultsType;

/**
 * The type of parameters for basic depth shaders.
 */

public interface R2DepthShaderBasicParametersValuesType
{
  /**
   * @return A reference to the default textures
   */

  R2TextureDefaultsType textureDefaults();

  /**
   * <p>An albedo map which is used primarily used to determine the opacity
   * of the shaded surface.</p>
   *
   * @return The albedo texture for the surface
   */

  default R2Texture2DUsableType albedoTexture()
  {
    return this.textureDefaults().texture2DWhite();
  }

  /**
   * The threshold value that defines whether or not the surface fragment will
   * be discarded. If the opacity of the surface is less than this threshold
   * value, the surface will be discarded. A threshold value of {@code 0.0} will
   * result in no fragments ever being discarded.
   *
   * @return The threshold value
   */

  default float alphaDiscardThreshold()
  {
    return 0.0f;
  }
}
