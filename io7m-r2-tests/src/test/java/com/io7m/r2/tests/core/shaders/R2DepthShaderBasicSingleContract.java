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
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2TextureDefaults;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2TextureUnitAllocator;
import com.io7m.r2.core.R2TextureUnitAllocatorType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2TextureUnitContextType;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicParametersMutable;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicParametersType;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicParameters;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicSingle;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesResources;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;
import com.io7m.r2.shaders.R2Shaders;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2DepthShaderBasicSingleContract extends
  R2ShaderDepthSingleContract<R2DepthShaderBasicParametersType,
    R2DepthShaderBasicParametersMutable>
{
  @Override
  protected final R2DepthShaderBasicParametersMutable newParameters(
    final JCGLInterfaceGL33Type g)
  {
    final JCGLTexturesType g_tex = g.getTextures();

    final R2TextureUnitAllocatorType tp =
      R2TextureUnitAllocator.newAllocatorWithStack(8, g_tex.textureGetUnits());
    final R2TextureUnitContextParentType tc_root =
      tp.getRootContext();
    final R2TextureUnitContextType tc_alloc =
      tc_root.unitContextNew();

    try {
      final R2TextureDefaultsType t =
        R2TextureDefaults.newDefaults(g.getTextures(), tc_alloc);
      final R2DepthShaderBasicParametersMutable p =
        R2DepthShaderBasicParametersMutable.create();
      p.setAlbedoTexture(t.getWhiteTexture());
      return p;
    } finally {
      tc_alloc.unitContextFinish(g_tex);
    }
  }

  @Test
  public final void testNew()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ShaderSourcesType sources =
      R2ShaderSourcesResources.newSources(R2Shaders.class);
    final R2IDPoolType pool = R2IDPool.newPool();

    final R2ShaderInstanceSingleType<R2SurfaceShaderBasicParameters> s =
      R2SurfaceShaderBasicSingle.newShader(
        g.getShaders(),
        sources,
        pool);

    Assert.assertFalse(s.isDeleted());
    s.delete(g);
    Assert.assertTrue(s.isDeleted());
  }
}
