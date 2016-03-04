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
import com.io7m.r2.core.R2LightSingleType;
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2MatricesProjectiveLightValuesType;
import com.io7m.r2.core.R2TextureUnitContextMutableType;

/**
 * The type of usable single-instance light shaders.
 *
 * @param <M> The type of shader parameters
 */

public interface R2ShaderLightSingleUsableType<M extends R2LightSingleType>
  extends
  R2ShaderUsableType<M>, R2ShaderGBufferConsumerType
{
  /**
   * Bind any textures needed for execution.
   *
   * @param g_tex  A texture interface
   * @param uc     A texture interface
   * @param values The parameters
   */

  void setLightTextures(
    JCGLTexturesType g_tex,
    R2TextureUnitContextMutableType uc,
    M values);

  /**
   * Set light values.
   *
   * @param g_sh   An OpenGL interface
   * @param g_tex  An OpenGL interface
   * @param values The light parameters
   */

  void setLightValues(
    JCGLShadersType g_sh,
    JCGLTexturesType g_tex,
    M values);

  /**
   * Set light values related to the current view.
   *
   * @param g_sh     An OpenGL interface
   * @param m        The current view matrices and values
   * @param viewport The current viewport
   * @param values   The light parameters
   */

  void setLightViewDependentValues(
    JCGLShadersType g_sh,
    R2MatricesObserverValuesType m,
    AreaInclusiveUnsignedLType viewport,
    M values);

  /**
   * Set light values related to the current view and instance transform.
   *
   * @param g_sh   An OpenGL interface
   * @param m      The current instance matrices and values
   * @param values The light parameters
   */

  void setLightTransformDependentValues(
    JCGLShadersType g_sh,
    R2MatricesInstanceSingleValuesType m,
    M values);

  /**
   * Set light values related to the current view and instance transform.
   *
   * @param g_sh   An OpenGL interface
   * @param m      The current instance matrices and values
   * @param values The light parameters
   */

  void setLightProjectiveDependentValues(
    JCGLShadersType g_sh,
    R2MatricesProjectiveLightValuesType m,
    M values);
}
