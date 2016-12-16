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

package com.io7m.r2.tests.core.shaders;

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jtensors.parameterized.PVectorI4F;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.shaders.provided.R2ShaderDebugVisualBillboarded;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBillboardedType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.spaces.R2SpaceRGBAType;
import com.io7m.r2.tests.core.ShaderPreprocessing;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2ShaderDebugVisualBillboardedContract extends
  R2ShaderInstanceBillboardedContract<PVectorI4F<R2SpaceRGBAType>,
    PVectorI4F<R2SpaceRGBAType>>
{
  @Override
  protected final PVectorI4F<R2SpaceRGBAType> newParameters(
    final JCGLInterfaceGL33Type g)
  {
    return new PVectorI4F<>(1.0f, 1.0f, 1.0f, 1.0f);
  }

  @Override
  protected R2ShaderInstanceBillboardedType<PVectorI4F<R2SpaceRGBAType>>
  newShaderWithVerifier(
    final JCGLInterfaceGL33Type g,
    final R2ShaderPreprocessingEnvironmentType sources,
    final R2IDPoolType pool)
  {
    return R2ShaderDebugVisualBillboarded.newShader(
      g.getShaders(),
      sources,
      pool);
  }

  @Test
  public final void testNew()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool = R2IDPool.newPool();

    final R2ShaderInstanceBillboardedType<PVectorI4F<R2SpaceRGBAType>> s =
      R2ShaderDebugVisualBillboarded.newShader(
        g.getShaders(),
        sources,
        pool);

    Assert.assertFalse(s.isDeleted());
    s.delete(g);
    Assert.assertTrue(s.isDeleted());
  }
}
