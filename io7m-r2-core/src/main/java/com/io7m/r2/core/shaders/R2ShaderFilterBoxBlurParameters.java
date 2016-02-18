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

package com.io7m.r2.core.shaders;

import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureDefaultsType;

/**
 * The parameters used for the box blur shader.
 */

public final class R2ShaderFilterBoxBlurParameters
{
  /**
   * The default blur size.
   */

  public static final float DEFAULT_BLUR_SIZE = 1.0f;

  private R2Texture2DUsableType texture;
  private float                 blur_size;

  private R2ShaderFilterBoxBlurParameters(
    final R2Texture2DUsableType in_texture)
  {
    this.texture = NullCheck.notNull(in_texture);
    this.blur_size = R2ShaderFilterBoxBlurParameters.DEFAULT_BLUR_SIZE;
  }

  /**
   * Construct new parameters using a default set of textures.
   *
   * @param in_defaults The texture defaults
   *
   * @return A new set of parameters
   */

  public static R2ShaderFilterBoxBlurParameters newParameters(
    final R2TextureDefaultsType in_defaults)
  {
    NullCheck.notNull(in_defaults);
    return new R2ShaderFilterBoxBlurParameters(
      in_defaults.getWhiteTexture());
  }

  /**
   * @return The texture that will be blurred
   */

  public R2Texture2DUsableType getTexture()
  {
    return this.texture;
  }

  /**
   * Set the texture that will be blurred.
   *
   * @param t The texture
   */

  public void setTexture(final R2Texture2DUsableType t)
  {
    this.texture = NullCheck.notNull(t);
  }

  /**
   * @return The blur size in texels
   */

  public float getBlurSize()
  {
    return this.blur_size;
  }

  /**
   * Set the blur size in texels.
   *
   * @param s The size
   */

  public void setBlurSize(final float s)
  {
    this.blur_size = s;
  }
}
