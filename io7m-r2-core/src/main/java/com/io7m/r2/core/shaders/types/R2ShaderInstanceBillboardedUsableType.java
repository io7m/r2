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

package com.io7m.r2.core.shaders.types;

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.r2.core.R2MatricesObserverValuesType;

/**
 * The type of usable shaders for rendering the surfaces of billboarded
 * instances.
 *
 * @param <M> The type of parameter data
 */

public interface R2ShaderInstanceBillboardedUsableType<M> extends
  R2ShaderUsableType<M>
{
  /**
   * <p>Set shader values that are derived from the current view.</p>
   *
   * <p>This method will be called exactly once between calls to {@link
   * #onActivate(JCGLShadersType)} and {@link #onValidate()}.</p>
   *
   * @param g_sh     An OpenGL interface
   * @param m        The view matrices
   * @param viewport The target viewport
   */

  void onReceiveViewValues(
    JCGLShadersType g_sh,
    R2MatricesObserverValuesType m,
    AreaInclusiveUnsignedLType viewport);

  /**
   * <p>Set material values.</p>
   *
   * <p>This method will be called once for each group of instances that use the
   * material {@code M}.</p>
   *
   * <p>This method will be called after a call to {@link
   * #onActivate(JCGLShadersType)} and before a call to {@link
   * #onValidate()}.</p>
   *
   * @param g_tex  A texture interface
   * @param g_sh   A shader interface
   * @param tc     A texture unit context
   * @param values The material parameters
   */

  void onReceiveMaterialValues(
    JCGLTexturesType g_tex,
    JCGLShadersType g_sh,
    JCGLTextureUnitContextMutableType tc,
    M values);
}
