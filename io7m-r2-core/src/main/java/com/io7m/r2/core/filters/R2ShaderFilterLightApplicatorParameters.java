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

package com.io7m.r2.core.filters;

import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureDefaultsType;

/**
 * The parameters used for light application.
 *
 * @see R2FilterLightApplicatorType
 */

public final class R2ShaderFilterLightApplicatorParameters
{
  private R2Texture2DUsableType albedo;
  private R2Texture2DUsableType diffuse;
  private R2Texture2DUsableType specular;

  private R2ShaderFilterLightApplicatorParameters(
    final R2Texture2DUsableType in_albedo,
    final R2Texture2DUsableType in_diffuse,
    final R2Texture2DUsableType in_specular)
  {
    this.albedo = NullCheck.notNull(in_albedo);
    this.diffuse = NullCheck.notNull(in_diffuse);
    this.specular = NullCheck.notNull(in_specular);
  }

  /**
   * Construct new parameters using a default set of textures.
   *
   * @param in_defaults The texture defaults
   *
   * @return A new set of parameters
   */

  public static R2ShaderFilterLightApplicatorParameters newParameters(
    final R2TextureDefaultsType in_defaults)
  {
    NullCheck.notNull(in_defaults);
    return new R2ShaderFilterLightApplicatorParameters(
      in_defaults.getWhiteTexture(),
      in_defaults.getWhiteTexture(),
      in_defaults.getBlackTexture());
  }

  /**
   * @return The scene's albedo texture
   */

  public R2Texture2DUsableType getAlbedoTexture()
  {
    return this.albedo;
  }

  /**
   * Set the scene's albedo texture.
   *
   * @param in_albedo The albedo texture
   */

  public void setAlbedoTexture(final R2Texture2DUsableType in_albedo)
  {
    this.albedo = NullCheck.notNull(in_albedo);
  }

  /**
   * @return The scene's light diffuse term texture
   */

  public R2Texture2DUsableType getDiffuseTexture()
  {
    return this.diffuse;
  }

  /**
   * Set the scene's light diffuse term texture.
   *
   * @param in_diffuse The diffuse term texture
   */

  public void setDiffuseTexture(final R2Texture2DUsableType in_diffuse)
  {
    this.diffuse = NullCheck.notNull(in_diffuse);
  }

  /**
   * @return The scene's light specular term texture
   */

  public R2Texture2DUsableType getSpecularTexture()
  {
    return this.specular;
  }

  /**
   * Set the scene's light specular term texture.
   *
   * @param in_specular The specular term texture
   */

  public void setSpecularTexture(final R2Texture2DUsableType in_specular)
  {
    this.specular = NullCheck.notNull(in_specular);
  }
}
