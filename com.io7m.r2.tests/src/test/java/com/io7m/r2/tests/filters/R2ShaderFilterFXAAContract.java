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

import com.io7m.jcanephora.core.JCGLTexture2DType;
import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Pair;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jregions.core.unparameterized.areas.AreasL;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.filters.R2FilterFXAAQuality;
import com.io7m.r2.filters.R2ShaderFilterFXAAA;
import com.io7m.r2.filters.R2ShaderFilterFXAAParameters;
import com.io7m.r2.tests.core.ShaderPreprocessing;
import org.junit.Assert;
import org.junit.Test;

import static com.io7m.jcanephora.core.JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR;
import static com.io7m.jcanephora.core.JCGLTextureFormat.TEXTURE_FORMAT_RGB_8_3BPP;
import static com.io7m.jcanephora.core.JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE;
import static com.io7m.r2.core.R2Texture2DStatic.of;
import static com.io7m.r2.filters.R2ShaderFilterFXAAParameters.builder;

public abstract class R2ShaderFilterFXAAContract extends
  R2ShaderFilterContract<R2ShaderFilterFXAAParameters,
    R2ShaderFilterFXAAParameters>
{
  @Override
  protected final R2ShaderFilterFXAAParameters
  newParameters(final JCGLInterfaceGL33Type g)
  {
    final JCGLTexturesType g_tex = g.textures();

    final JCGLTextureUnitAllocatorType tp =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tc_root = tp.rootContext();
    final JCGLTextureUnitContextType tc_alloc = tc_root.unitContextNew();

    try {
      final AreaL area = AreasL.create(0, 0, 640L, 480L);

      final Pair<JCGLTextureUnitType, JCGLTexture2DType> pq =
        tc_alloc.unitContextAllocateTexture2D(
          g_tex,
          640L,
          480L,
          TEXTURE_FORMAT_RGB_8_3BPP,
          TEXTURE_WRAP_CLAMP_TO_EDGE,
          JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
          TEXTURE_FILTER_LINEAR,
          JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);

      return builder()
        .setTexture(of(pq.getRight()))
        .build();
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

    for (final R2FilterFXAAQuality q : R2FilterFXAAQuality.values()) {
      final R2ShaderFilterType<R2ShaderFilterFXAAParameters> s =
        R2ShaderFilterFXAAA.newShader(g.shaders(), sources, pool, q);
      Assert.assertFalse(s.isDeleted());
      s.delete(g);
      Assert.assertTrue(s.isDeleted());
    }
  }
}
