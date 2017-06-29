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

import com.io7m.r2.core.api.deletable.R2DeletableType;

/**
 * The type of renderers that populate shadow maps.
 */

public interface R2ShadowMapRendererType extends R2DeletableType
{
  /**
   * Begin rendering shadow maps.
   *
   * @return A shadow map execution
   *
   * @throws R2ExceptionShadowExecutionAlreadyActive If an execution is already
   *                                                 active
   * @throws R2ExceptionShadow                       On other shadow-related
   *                                                 errors
   */

  R2ShadowMapRendererExecutionType shadowBegin()
    throws
    R2ExceptionShadowExecutionAlreadyActive,
    R2ExceptionShadow;
}
