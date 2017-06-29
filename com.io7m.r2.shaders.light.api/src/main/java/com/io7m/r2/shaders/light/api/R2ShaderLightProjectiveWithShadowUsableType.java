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

package com.io7m.r2.shaders.light.api;

import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.r2.lights.R2LightProjectiveWithShadowReadableType;
import com.io7m.r2.textures.R2Texture2DUsableType;

/**
 * The type of usable single-instance projective light shaders with shadows.
 *
 * @param <M> The type of shader parameters
 */

public interface R2ShaderLightProjectiveWithShadowUsableType<
  M extends R2LightProjectiveWithShadowReadableType>
  extends R2ShaderLightProjectiveUsableType<M>
{
  /**
   * <p>Set values from the given shadow map context.</p>
   *
   * <p>This method will be called once for each projective light that uses the
   * current shader.</p>
   *
   * <p>This method will be called after a call to {@link
   * com.io7m.r2.shaders.api.R2ShaderUsableType#onActivate(JCGLInterfaceGL33Type)}
   * and before a call to {@link #onValidate()}.</p>
   *
   * @param g   An OpenGL interface
   * @param tc  A texture unit context
   * @param map A rendered shadow map
   */

  void onReceiveShadowMap(
    JCGLInterfaceGL33Type g,
    JCGLTextureUnitContextMutableType tc,
    R2Texture2DUsableType map);
}
