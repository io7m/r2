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

import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.r2.lights.R2LightWithShadowSingleType;
import com.io7m.r2.matrices.R2MatricesType;
import com.io7m.r2.rendering.depth.api.R2DepthInstancesType;

/**
 * A contextual value representing the in-process rendering of a set of shadow
 * maps.
 */

public interface R2ShadowMapRendererExecutionType
{
  /**
   * Render the shadow map for the given light.
   *
   * @param pc A profiling context
   * @param tc A texture unit context
   * @param m  A set of matrices
   * @param ls A light with a shadow
   * @param i  The instances for the light
   *
   * @throws R2ExceptionShadowExecutionNotActive Iff the execution has already
   *                                             completed
   * @throws R2ExceptionShadow                   On other shadow-related errors
   */

  void shadowExecRenderLight(
    JCGLProfilingContextType pc,
    JCGLTextureUnitContextParentType tc,
    R2MatricesType m,
    R2LightWithShadowSingleType ls,
    R2DepthInstancesType i)
    throws
    R2ExceptionShadowExecutionNotActive,
    R2ExceptionShadow;

  /**
   * Complete rendering of the shadow maps, returning a context containing all
   * of the rendered maps.
   *
   * @return A shadow map context
   *
   * @throws R2ExceptionShadowExecutionNotActive Iff the execution has already
   *                                             completed
   * @throws R2ExceptionShadow                   On other shadow-related errors
   */

  R2ShadowMapContextType shadowExecComplete()
    throws
    R2ExceptionShadowExecutionNotActive,
    R2ExceptionShadow;
}
