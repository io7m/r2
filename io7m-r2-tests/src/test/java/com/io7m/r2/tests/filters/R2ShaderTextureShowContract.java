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

package com.io7m.r2.tests.filters;

import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jfunctional.Pair;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2Texture2DType;
import com.io7m.r2.core.R2TextureUnitAllocator;
import com.io7m.r2.core.R2TextureUnitAllocatorType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2TextureUnitContextType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesResources;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;
import com.io7m.r2.filters.R2ShaderFilterTextureShow;
import com.io7m.r2.filters.R2ShaderFilterTextureShowParametersMutable;
import com.io7m.r2.filters.R2ShaderFilterTextureShowParametersType;
import com.io7m.r2.shaders.R2Shaders;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2ShaderTextureShowContract extends
  R2ShaderFilterContract<R2ShaderFilterTextureShowParametersType,
    R2ShaderFilterTextureShowParametersMutable>
{
  @Override
  protected R2ShaderFilterTextureShowParametersMutable newParameters(
    final JCGLInterfaceGL33Type g)
  {
    final R2ShaderFilterTextureShowParametersMutable p =
      R2ShaderFilterTextureShowParametersMutable.create();

    final JCGLTexturesType g_tex = g.getTextures();

    final R2TextureUnitAllocatorType tp =
      R2TextureUnitAllocator.newAllocatorWithStack(8, g_tex.textureGetUnits());
    final R2TextureUnitContextParentType tc_root =
      tp.getRootContext();
    final R2TextureUnitContextType tc_alloc =
      tc_root.unitContextNew();

    final Pair<JCGLTextureUnitType, R2Texture2DType> r =
      tc_alloc.unitContextAllocateTexture2D(
        g_tex,
        4L,
        4L,
        JCGLTextureFormat.TEXTURE_FORMAT_RGB_8_3BPP,
        JCGLTextureWrapS.TEXTURE_WRAP_REPEAT,
        JCGLTextureWrapT.TEXTURE_WRAP_REPEAT,
        JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
        JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);
    tc_alloc.unitContextFinish(g_tex);

    p.setTexture(r.getRight());
    return p;
  }

  @Test
  public final void testNew()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ShaderSourcesType sources =
      R2ShaderSourcesResources.newSources(R2Shaders.class);
    final R2IDPoolType pool = R2IDPool.newPool();

    final R2ShaderFilterType<R2ShaderFilterTextureShowParametersType> s =
      R2ShaderFilterTextureShow.newShader(
        g.getShaders(),
        sources,
        pool);

    Assert.assertFalse(s.isDeleted());
    s.delete(g);
    Assert.assertTrue(s.isDeleted());
  }
}
