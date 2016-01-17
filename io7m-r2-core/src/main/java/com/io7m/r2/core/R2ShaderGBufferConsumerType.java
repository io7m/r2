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

import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;

/**
 * The type of shaders that consume G-Buffers.
 */

public interface R2ShaderGBufferConsumerType
{
  /**
   * Set the shader values related to the given G-Buffer.
   *
   * @param g_sh          An OpenGL interface
   * @param g_tex         An OpenGL interface
   * @param g             The G-Buffer
   * @param unit_albedo   The texture unit to which the G-Buffer's
   *                      albedo/emissive texture is bound
   * @param unit_specular The texture unit to which the G-Buffer's specular
   *                      texture is bound
   * @param unit_depth    The texture unit to which the G-Buffer's depth/stencil
   *                      texture is bound
   * @param unit_normals  The texture unit to which the G-Buffer's normal
   *                      texture is bound
   */

  void setGBuffer(
    JCGLShadersType g_sh,
    JCGLTexturesType g_tex,
    R2GeometryBufferUsableType g,
    JCGLTextureUnitType unit_albedo,
    JCGLTextureUnitType unit_specular,
    JCGLTextureUnitType unit_depth,
    JCGLTextureUnitType unit_normals);
}
