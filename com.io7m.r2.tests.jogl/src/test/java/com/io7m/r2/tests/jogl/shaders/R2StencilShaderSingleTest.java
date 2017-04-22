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

package com.io7m.r2.tests.jogl.shaders;

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jfunctional.Unit;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.shaders.provided.R2StencilShaderSingle;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.tests.core.shaders.R2StencilShaderSingleContract;
import com.io7m.r2.tests.jogl.R2TestContexts;

public final class R2StencilShaderSingleTest extends
  R2StencilShaderSingleContract
{
  @Override
  protected JCGLContextType newGL33Context(
    final String name,
    final int depth_bits,
    final int stencil_bits)
  {
    return R2TestContexts.newGL33Context(name, depth_bits, stencil_bits);
  }

  @Override
  protected R2ShaderInstanceSingleType<Unit> newShaderWithVerifier(
    final JCGLInterfaceGL33Type g,
    final R2ShaderPreprocessingEnvironmentType sources,
    final R2IDPoolType pool)
  {
    return R2StencilShaderSingle.newShader(g.shaders(), sources, pool);
  }
}
