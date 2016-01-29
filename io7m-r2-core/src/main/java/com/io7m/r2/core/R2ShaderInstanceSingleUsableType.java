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

import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;

/**
 * The type of usable shaders for rendering single instances.
 *
 * @param <M> The type of parameter data
 */

public interface R2ShaderInstanceSingleUsableType<M> extends
  R2ShaderUsableType<M>
{
  /**
   * Set material textures. Any textures bound in the context are guaranteed to
   * remain bound until at least the next call to {@link
   * #setMaterialValues(JCGLShadersType, Object)}
   *
   * @param tc     A texture unit context
   * @param g_tex  An OpenGL interface
   * @param values The material parameters
   */

  void setMaterialTextures(
    JCGLTexturesType g_tex,
    R2TextureUnitContextMutableType tc,
    M values);

  /**
   * Set material values.
   *
   * @param g_sh   An OpenGL interface
   * @param values The material parameters
   */

  void setMaterialValues(
    JCGLShadersType g_sh,
    M values);

  /**
   * Set matrices relevant to the current observer.
   *
   * @param g_sh An OpenGL interface
   * @param m    The observer matrices
   */

  void setMatricesView(
    JCGLShadersType g_sh,
    R2MatricesObserverValuesType m);

  /**
   * Set matrices relevant to the current instance.
   *
   * @param g_sh An OpenGL interface
   * @param m    The instance matrices
   */

  void setMatricesInstance(
    JCGLShadersType g_sh,
    R2MatricesInstanceSingleValuesType m);
}
