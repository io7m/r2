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

import com.io7m.jcanephora.core.JCGLProjectionMatrices;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jtensors.parameterized.PVectorI3F;
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2ProjectionFOV;
import com.io7m.r2.core.R2TextureDefaults;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.filters.R2ShaderFilterFogDepthQuadratic;
import com.io7m.r2.filters.R2ShaderFilterFogParameters;
import com.io7m.r2.tests.core.R2EmptyObserverValues;
import com.io7m.r2.tests.core.ShaderPreprocessing;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2ShaderFilterFogDepthQuadraticContract extends
  R2ShaderFilterContract<R2ShaderFilterFogParameters,
    R2ShaderFilterFogParameters>
{
  @Override
  protected final R2ShaderFilterFogParameters
  newParameters(final JCGLInterfaceGL33Type g)
  {
    final JCGLTexturesType g_tex = g.getTextures();
    final JCGLTextureUnitAllocatorType tp =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tc_root =
      tp.getRootContext();
    final JCGLTextureUnitContextType tc_alloc =
      tc_root.unitContextNew();
    final R2TextureDefaultsType td =
      R2TextureDefaults.newDefaults(g_tex, tc_alloc);

    return R2ShaderFilterFogParameters.builder()
      .setFogColor(new PVectorI3F<>(0.0f, 0.0f, 0.0f))
      .setImageDepthTexture(td.texture2DWhite())
      .setImageTexture(td.texture2DWhite())
      .setObserverValues(new R2EmptyObserverValues(R2ProjectionFOV.newFrustumWith(
        JCGLProjectionMatrices.newMatrices(),
        (float) Math.toRadians(90.0f), 1.0f, 0.0f, 100.0f)))
      .build();
  }

  @Test
  public final void testNew()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool = R2IDPool.newPool();

    final R2ShaderFilterType<R2ShaderFilterFogParameters> s =
      R2ShaderFilterFogDepthQuadratic.newShader(g.getShaders(), sources, pool);
    Assert.assertFalse(s.isDeleted());
    s.delete(g);
    Assert.assertTrue(s.isDeleted());
  }
}
