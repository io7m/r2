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

package com.io7m.r2.tests.jogl.shaders.compositor;

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.filters.compositor.R2ShaderFilterTextureShow;
import com.io7m.r2.filters.compositor.R2ShaderFilterTextureShowParameters;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.shaders.filter.api.R2ShaderFilterType;
import com.io7m.r2.tests.shaders.compositor.R2ShaderTextureShowContract;
import com.io7m.r2.tests.jogl.R2TestContexts;

public final class R2ShaderFilterTextureShowTest
  extends R2ShaderTextureShowContract
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
  protected R2ShaderFilterType<R2ShaderFilterTextureShowParameters>
  newShaderWithVerifier(
    final JCGLInterfaceGL33Type g,
    final R2ShaderPreprocessingEnvironmentType sources,
    final R2IDPoolType pool)
  {
    return R2ShaderFilterTextureShow.create(g.shaders(), sources, pool);
  }
}