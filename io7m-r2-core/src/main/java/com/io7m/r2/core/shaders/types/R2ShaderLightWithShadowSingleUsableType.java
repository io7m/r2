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

import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.r2.core.R2LightWithShadowSingleType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureUnitContextMutableType;

/**
 * The type of usable single-instance light shaders that consume shadows.
 *
 * @param <M> The type of shader parameters
 */

public interface R2ShaderLightWithShadowSingleUsableType<M extends
  R2LightWithShadowSingleType>
  extends R2ShaderLightSingleType<M>
{
  /**
   * <p>Set values from the given shadow map context.</p>
   *
   * <p>This method will be called once for each projective light that uses the
   * current shader.</p>
   *
   * <p>This method will be called after a call to {@link
   * #onActivate(JCGLShadersType)} and before a call to {@link
   * #onValidate()}.</p>
   *
   * @param g_tex  A texture interface
   * @param g_sh   A shader interface
   * @param tc     A texture unit context
   * @param values The material parameters
   * @param map    A rendered shadow map
   */

  void onReceiveShadowMap(
    JCGLTexturesType g_tex,
    JCGLShadersType g_sh,
    R2TextureUnitContextMutableType tc,
    M values,
    R2Texture2DUsableType map);
}
