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

/**
 * Access to default textures for materials.
 */

public interface R2TextureDefaultsType extends R2DeletableType
{
  /**
   * @return A flat normal texture
   */

  R2Texture2DUsableType texture2DNormal();

  /**
   * @return A flat white texture
   */

  R2Texture2DUsableType texture2DWhite();

  /**
   * @return A flat black texture
   */

  R2Texture2DUsableType texture2DBlack();

  /**
   * @return A hard-edged white projective light texture
   */

  R2Texture2DUsableType texture2DProjectiveWhite();

  /**
   * @return A flat black texture
   */

  R2TextureCubeUsableType textureCubeBlack();
}
