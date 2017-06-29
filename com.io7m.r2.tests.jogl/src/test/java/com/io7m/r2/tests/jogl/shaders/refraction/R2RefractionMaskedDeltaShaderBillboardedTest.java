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

package com.io7m.r2.tests.jogl.shaders.refraction;

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.shaders.refraction.R2RefractionMaskedDeltaParameters;
import com.io7m.r2.shaders.refraction.R2RefractionMaskedDeltaShaderBillboarded;
import com.io7m.r2.shaders.translucent.api.R2ShaderTranslucentInstanceBillboardedType;
import com.io7m.r2.tests.jogl.R2TestContexts;
import com.io7m.r2.tests.shaders.refraction.R2RefractionMaskedDeltaShaderBillboardedContract;

public final class R2RefractionMaskedDeltaShaderBillboardedTest extends
  R2RefractionMaskedDeltaShaderBillboardedContract
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
  protected R2ShaderTranslucentInstanceBillboardedType<R2RefractionMaskedDeltaParameters>
  newShaderWithVerifier(
    final JCGLInterfaceGL33Type g,
    final R2ShaderPreprocessingEnvironmentType sources,
    final R2IDPoolType pool)
  {
    return R2RefractionMaskedDeltaShaderBillboarded.create(
      g.shaders(), sources, pool);
  }
}
