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
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2LightSingleReadableType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2TextureUnitContextMutableType;

/**
 * The type of usable single-instance light shaders.
 *
 * @param <M> The type of shader parameters
 */

public interface R2ShaderLightSingleUsableType<
  M extends R2LightSingleReadableType>
  extends R2ShaderUsableType<M>
{
  /**
   * <p>Set the bound geometry buffer textures for the current shader.</p>
   *
   * <p>This method will be called exactly once between calls to {@link
   * #onActivate(JCGLShadersType)} and {@link #onValidate()}.</p>
   *
   * @param g_sh          An OpenGL interface
   * @param g             The geometry buffer
   * @param unit_albedo   The texture unit to which the geometry buffer's
   *                      albedo/emissive texture is bound
   * @param unit_specular The texture unit to which the geometry buffer's
   *                      specular texture is bound
   * @param unit_depth    The texture unit to which the geometry buffer's
   *                      depth/stencil texture is bound
   * @param unit_normals  The texture unit to which the geometry buffer's normal
   *                      texture is bound
   */

  void onReceiveBoundGeometryBufferTextures(
    JCGLShadersType g_sh,
    R2GeometryBufferUsableType g,
    JCGLTextureUnitType unit_albedo,
    JCGLTextureUnitType unit_specular,
    JCGLTextureUnitType unit_depth,
    JCGLTextureUnitType unit_normals);

  /**
   * <p>Set light values.</p>
   *
   * <p>This method will be called once for each projective light that uses the
   * current shader.</p>
   *
   * <p>This method will be called after a call to {@link
   * #onActivate(JCGLShadersType)} and before a call to {@link
   * #onValidate()}.</p>
   *
   * @param g_tex    A texture interface
   * @param g_sh     A shader interface
   * @param tc       A texture unit context
   * @param viewport The viewport
   * @param values   The material parameters
   * @param m        Observer values
   */

  void onReceiveValues(
    JCGLTexturesType g_tex,
    JCGLShadersType g_sh,
    R2TextureUnitContextMutableType tc,
    AreaInclusiveUnsignedLType viewport,
    M values,
    R2MatricesObserverValuesType m);
}
