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

package com.io7m.r2.tests.jogl;

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.r2.core.R2DepthVarianceBufferDescription;
import com.io7m.r2.core.R2DepthVarianceBufferUsableType;
import com.io7m.r2.core.R2DepthVarianceRenderer;
import com.io7m.r2.core.R2DepthVarianceRendererType;
import com.io7m.r2.core.R2RenderTargetPoolUsableType;
import com.io7m.r2.core.R2ShadowMapRenderer;
import com.io7m.r2.core.R2ShadowMapRendererType;
import com.io7m.r2.tests.core.R2ShadowMapRendererContract;

public final class R2ShadowMapRendererTest extends R2ShadowMapRendererContract
{
  @Override
  protected R2DepthVarianceRendererType newVarianceRenderer(
    final JCGLInterfaceGL33Type g)
  {
    return R2DepthVarianceRenderer.create(g);
  }

  @Override
  protected R2ShadowMapRendererType newShadowMapRenderer(
    final JCGLInterfaceGL33Type g,
    final R2DepthVarianceRendererType dvr,
    final R2RenderTargetPoolUsableType<R2DepthVarianceBufferDescription,
      R2DepthVarianceBufferUsableType> vp)
  {
    return R2ShadowMapRenderer.newRenderer(g, dvr, vp);
  }

  @Override
  protected JCGLContextType newGL33Context(
    final String name,
    final int depth_bits,
    final int stencil_bits)
  {
    return R2TestContexts.newGL33Context(name, depth_bits, stencil_bits);
  }
}
