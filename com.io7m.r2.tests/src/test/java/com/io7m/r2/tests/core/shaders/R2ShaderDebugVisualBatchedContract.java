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
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.shaders.provided.R2ShaderDebugVisualBatched;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.spaces.R2SpaceRGBAType;
import com.io7m.r2.tests.core.ShaderPreprocessing;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2ShaderDebugVisualBatchedContract extends
  R2ShaderInstanceBatchedContract<PVector4D<R2SpaceRGBAType>,
    PVector4D<R2SpaceRGBAType>>
{
  @Override
  protected final PVector4D<R2SpaceRGBAType> newParameters(
    final JCGLInterfaceGL33Type g)
  {
    return PVector4D.of(1.0f, 1.0f, 1.0f, 1.0f);
  }

  @Override
  protected R2ShaderInstanceBatchedType<PVector4D<R2SpaceRGBAType>>
  newShaderWithVerifier(
    final JCGLInterfaceGL33Type g,
    final R2ShaderPreprocessingEnvironmentType sources,
    final R2IDPoolType pool)
  {
    return R2ShaderDebugVisualBatched.newShader(
      g.shaders(),
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

    final R2ShaderInstanceBatchedType<PVector4D<R2SpaceRGBAType>> s =
      R2ShaderDebugVisualBatched.newShader(
        g.shaders(),
        sources,
        pool);

    Assert.assertFalse(s.isDeleted());
    s.delete(g);
    Assert.assertTrue(s.isDeleted());
  }
}
