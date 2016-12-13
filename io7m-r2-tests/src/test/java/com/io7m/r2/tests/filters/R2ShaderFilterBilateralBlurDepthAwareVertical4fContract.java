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

import com.io7m.jareas.core.AreaInclusiveUnsignedL;
import com.io7m.jcanephora.core.JCGLProjectionMatrices;
import com.io7m.jcanephora.core.JCGLTexture2DType;
import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Pair;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2ProjectionOrthographic;
import com.io7m.r2.core.R2ProjectionReadableType;
import com.io7m.r2.core.R2Texture2DStatic;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.filters.R2ShaderFilterBilateralBlurDepthAwareParameters;
import com.io7m.r2.filters.R2ShaderFilterBilateralBlurDepthAwareParametersType;
import com.io7m.r2.filters.R2ShaderFilterBilateralBlurDepthAwareVertical4f;
import com.io7m.r2.tests.core.R2EmptyObserverValues;
import com.io7m.r2.tests.core.ShaderPreprocessing;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2ShaderFilterBilateralBlurDepthAwareVertical4fContract
  extends
  R2ShaderFilterContract<R2ShaderFilterBilateralBlurDepthAwareParameters,
    R2ShaderFilterBilateralBlurDepthAwareParameters>
{
  @Override
  protected final R2ShaderFilterBilateralBlurDepthAwareParameters newParameters(
    final JCGLInterfaceGL33Type g)
  {
    final R2ShaderFilterBilateralBlurDepthAwareParameters.Builder b =
      R2ShaderFilterBilateralBlurDepthAwareParameters.builder();

    final JCGLTexturesType g_tex = g.getTextures();
    final JCGLFramebuffersType g_fb = g.getFramebuffers();

    final JCGLTextureUnitAllocatorType tp =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8,
        g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tc_root =
      tp.getRootContext();
    final JCGLTextureUnitContextType tc_alloc =
      tc_root.unitContextNew();

    try {
      final R2ProjectionReadableType proj =
        R2ProjectionOrthographic.newFrustum(
          JCGLProjectionMatrices.newMatrices());

      final AreaInclusiveUnsignedL area =
        AreaInclusiveUnsignedL.of(
          new UnsignedRangeInclusiveL(0L, 639L),
          new UnsignedRangeInclusiveL(0L, 479L));

      final Pair<JCGLTextureUnitType, JCGLTexture2DType> dp =
        tc_alloc.unitContextAllocateTexture2D(
          g_tex,
          4L,
          4L,
          JCGLTextureFormat.TEXTURE_FORMAT_DEPTH_24_4BPP,
          JCGLTextureWrapS.TEXTURE_WRAP_REPEAT,
          JCGLTextureWrapT.TEXTURE_WRAP_REPEAT,
          JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST,
          JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);

      final Pair<JCGLTextureUnitType, JCGLTexture2DType> ip =
        tc_alloc.unitContextAllocateTexture2D(
          g_tex,
          4L,
          4L,
          JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
          JCGLTextureWrapS.TEXTURE_WRAP_REPEAT,
          JCGLTextureWrapT.TEXTURE_WRAP_REPEAT,
          JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST,
          JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);

      b.setDepthCoefficient(1.0f);
      b.setBlurFalloff(1.0f);
      b.setBlurOutputInverseWidth(1.0f);
      b.setBlurOutputInverseHeight(1.0f);
      b.setDepthTexture(R2Texture2DStatic.of(dp.getRight()));
      b.setImageTexture(R2Texture2DStatic.of(ip.getRight()));
      b.setViewMatrices(new R2EmptyObserverValues(proj));
    } finally {
      tc_alloc.unitContextFinish(g_tex);
    }

    return b.build();
  }

  @Test
  public final void testNew()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool = R2IDPool.newPool();

    final R2ShaderFilterType<
      R2ShaderFilterBilateralBlurDepthAwareParameters> s =
      R2ShaderFilterBilateralBlurDepthAwareVertical4f.newShader(
        g.getShaders(), sources, pool);

    Assert.assertFalse(s.isDeleted());
    s.delete(g);
    Assert.assertTrue(s.isDeleted());
  }
}
