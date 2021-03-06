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

package com.io7m.r2.rendering.shadow.api;

import com.io7m.r2.lights.R2LightWithShadowSingleType;
import com.io7m.r2.textures.R2Texture2DUsableType;

/**
 * The type of usable shadow map contexts.
 */

public interface R2ShadowMapContextUsableType
{
  /**
   * @param ls A light
   *
   * @return The rendered shadow map for the given light
   *
   * @throws R2ExceptionShadowMapContextNotActive Iff the shadow map context is
   *                                              not active
   * @throws R2ExceptionShadowNotRendered         Iff the light was not rendered
   *                                              by the shadow map renderer
   *                                              execution that produced this
   *                                              context
   * @throws R2ExceptionShadow                    On other shadow-related
   *                                              errors
   */

  R2Texture2DUsableType shadowMapGet(
    R2LightWithShadowSingleType ls)
    throws
    R2ExceptionShadowMapContextNotActive,
    R2ExceptionShadowNotRendered,
    R2ExceptionShadow;
}
