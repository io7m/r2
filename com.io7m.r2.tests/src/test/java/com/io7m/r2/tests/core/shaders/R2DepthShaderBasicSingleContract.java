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
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2TextureDefaults;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicParameters;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicSingle;
import com.io7m.r2.core.shaders.types.R2ShaderDepthSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.tests.core.ShaderPreprocessing;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2DepthShaderBasicSingleContract extends
  R2ShaderDepthSingleContract<R2DepthShaderBasicParameters, R2DepthShaderBasicParameters>
{
  @Override
  protected final R2DepthShaderBasicParameters newParameters(
    final JCGLInterfaceGL33Type g)
  {
    final JCGLTexturesType g_tex = g.textures();

    final JCGLTextureUnitAllocatorType tp =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8,
        g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tc_root =
      tp.rootContext();
    final JCGLTextureUnitContextType tc_alloc =
      tc_root.unitContextNew();

    try {
      final R2TextureDefaultsType t =
        R2TextureDefaults.create(g.textures(), tc_alloc);
      return R2DepthShaderBasicParameters.of(t, t.white2D(), 0.0f);
    } finally {
      tc_alloc.unitContextFinish(g_tex);
    }
  }

  @Test
  public final void testNew()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool = R2IDPool.newPool();

    final R2ShaderDepthSingleType<R2DepthShaderBasicParameters> s =
      R2DepthShaderBasicSingle.newShader(g.shaders(), sources, pool);

    Assert.assertFalse(s.isDeleted());
    s.delete(g);
    Assert.assertTrue(s.isDeleted());
  }
}
