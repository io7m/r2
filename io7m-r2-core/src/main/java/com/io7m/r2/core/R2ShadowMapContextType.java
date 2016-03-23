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
 * A set of rendered shadow maps.
 */

public interface R2ShadowMapContextType
{
  /**
   * @param ls A light
   *
   * @return The rendered shadow map for the given light
   *
   * @throws R2RendererExceptionShadowMapContextNotActive Iff the shadow map
   *                                                      context is not active
   * @throws R2RendererExceptionShadowNotRendered         Iff the light was not
   *                                                      rendered by the shadow
   *                                                      map renderer execution
   *                                                      that produced this
   *                                                      context
   */

  R2Texture2DUsableType shadowMapGet(R2LightWithShadowType ls)
    throws
    R2RendererExceptionShadowMapContextNotActive,
    R2RendererExceptionShadowNotRendered;

  /**
   * Finish using the shadow map context.
   *
   * @throws R2RendererExceptionShadowMapContextNotActive Iff the context has
   *                                                      already finished
   */

  void shadowMapContextFinish()
    throws R2RendererExceptionShadowMapContextNotActive;
}
