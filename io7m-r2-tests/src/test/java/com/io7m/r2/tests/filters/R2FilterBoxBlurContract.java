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
import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.profiler.JCGLProfiling;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2ImageBuffer;
import com.io7m.r2.core.R2ImageBufferDescription;
import com.io7m.r2.core.R2ImageBufferDescriptionScaler;
import com.io7m.r2.core.R2ImageBufferDescriptionType;
import com.io7m.r2.core.R2ImageBufferPool;
import com.io7m.r2.core.R2ImageBufferType;
import com.io7m.r2.core.R2ImageBufferUsableType;
import com.io7m.r2.core.R2RenderTargetPoolType;
import com.io7m.r2.core.R2TextureDefaults;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2UnitQuad;
import com.io7m.r2.core.R2UnitQuadType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.filters.R2FilterBoxBlur;
import com.io7m.r2.filters.R2FilterBoxBlurParameters;
import com.io7m.r2.tests.core.R2JCGLContract;
import com.io7m.r2.tests.core.ShaderPreprocessing;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public abstract class R2FilterBoxBlurContract extends R2JCGLContract
{
  @Test
  public final void testIdentities()
  {
    final JCGLContextType gc =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      gc.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType id =
      R2IDPool.newPool();
    final JCGLTexturesType g_t =
      g.getTextures();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.getRootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.newDefaults(g.getTextures(), tc);
    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2RenderTargetPoolType<
      R2ImageBufferDescriptionType, R2ImageBufferUsableType> rtp =
      R2ImageBufferPool.newPool(g, Long.MAX_VALUE, Long.MAX_VALUE);

    final R2FilterType<
      R2FilterBoxBlurParameters<
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType,
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType>> f =
      R2FilterBoxBlur.newFilter(sources, g, td, rtp, id, quad);

    Assert.assertFalse(f.isDeleted());
    f.delete(g);
    Assert.assertTrue(f.isDeleted());
  }

  @Test
  public final void testFramebufferBindingPassesZeroSizeZeroScaleOne()
  {
    final JCGLContextType gc =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      gc.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType id =
      R2IDPool.newPool();
    final JCGLFramebuffersType g_fb =
      g.getFramebuffers();
    final JCGLTexturesType g_t =
      g.getTextures();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.getRootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.newDefaults(g_t, tc);
    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2RenderTargetPoolType<
      R2ImageBufferDescriptionType, R2ImageBufferUsableType> rtp =
      R2ImageBufferPool.newPool(g, Long.MAX_VALUE, Long.MAX_VALUE);
    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.getTimers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.getChildContext("main");

    final R2FilterType<
      R2FilterBoxBlurParameters<
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType,
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType>> f =
      R2FilterBoxBlur.newFilter(sources, g, td, rtp, id, quad);

    final AreaInclusiveUnsignedLType area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 127L),
      new UnsignedRangeInclusiveL(0L, 127L));

    final R2ImageBufferDescriptionType desc =
      R2ImageBufferDescription.of(area, Optional.empty());
    final R2ImageBufferType ib =
      R2ImageBuffer.newImageBuffer(g_fb, g_t, tc, desc);
    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    final R2FilterBoxBlurParameters<
      R2ImageBufferDescriptionType,
      R2ImageBufferUsableType,
      R2ImageBufferDescriptionType,
      R2ImageBufferUsableType> params =
      R2FilterBoxBlurParameters.newParameters(
        ib,
        R2ImageBufferUsableType::getRGBATexture,
        ib,
        R2ImageBufferUsableType::getRGBATexture,
        R2ImageBufferDescriptionScaler.get(), rtp);

    params.setSourceRenderTarget(ib);
    params.setOutputRenderTarget(ib);
    params.setBlurPasses(0);
    params.setBlurSize(0.0f);
    params.setBlurScale(1.0f);

    f.runFilter(pro_root, tc, params);

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(g_fb.framebufferDrawIsBound(ib.getPrimaryFramebuffer()));
  }

  @Test
  public final void testFramebufferBindingPassesZeroSizeZeroScaleOneNotSelf()
  {
    final JCGLContextType gc =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      gc.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType id =
      R2IDPool.newPool();
    final JCGLFramebuffersType g_fb =
      g.getFramebuffers();
    final JCGLTexturesType g_t =
      g.getTextures();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.getRootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.newDefaults(g_t, tc);
    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2RenderTargetPoolType<
      R2ImageBufferDescriptionType, R2ImageBufferUsableType> rtp =
      R2ImageBufferPool.newPool(g, Long.MAX_VALUE, Long.MAX_VALUE);
    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.getTimers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.getChildContext("main");

    final R2FilterType<
      R2FilterBoxBlurParameters<
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType,
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType>> f =
      R2FilterBoxBlur.newFilter(sources, g, td, rtp, id, quad);

    final AreaInclusiveUnsignedLType area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 127L),
      new UnsignedRangeInclusiveL(0L, 127L));

    final R2ImageBufferDescriptionType desc =
      R2ImageBufferDescription.of(area, Optional.empty());
    final R2ImageBufferType ib0 =
      R2ImageBuffer.newImageBuffer(g_fb, g_t, tc, desc);
    final R2ImageBufferType ib1 =
      R2ImageBuffer.newImageBuffer(g_fb, g_t, tc, desc);
    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    final R2FilterBoxBlurParameters<
      R2ImageBufferDescriptionType,
      R2ImageBufferUsableType,
      R2ImageBufferDescriptionType,
      R2ImageBufferUsableType> params =
      R2FilterBoxBlurParameters.newParameters(
        ib0,
        R2ImageBufferUsableType::getRGBATexture,
        ib1,
        R2ImageBufferUsableType::getRGBATexture,
        R2ImageBufferDescriptionScaler.get(), rtp);

    params.setSourceRenderTarget(ib0);
    params.setOutputRenderTarget(ib1);
    params.setBlurPasses(0);
    params.setBlurSize(0.0f);
    params.setBlurScale(1.0f);

    f.runFilter(pro_root, tc, params);

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(g_fb.framebufferDrawIsBound(ib1.getPrimaryFramebuffer()));
  }

  @Test
  public final void testFramebufferBindingPassesOneSizeZeroScaleOne()
  {
    final JCGLContextType gc =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      gc.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType id =
      R2IDPool.newPool();
    final JCGLFramebuffersType g_fb =
      g.getFramebuffers();
    final JCGLTexturesType g_t =
      g.getTextures();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.getRootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.newDefaults(g_t, tc);
    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2RenderTargetPoolType<
      R2ImageBufferDescriptionType, R2ImageBufferUsableType> rtp =
      R2ImageBufferPool.newPool(g, Long.MAX_VALUE, Long.MAX_VALUE);
    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.getTimers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.getChildContext("main");

    final R2FilterType<
      R2FilterBoxBlurParameters<
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType,
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType>> f =
      R2FilterBoxBlur.newFilter(sources, g, td, rtp, id, quad);

    final AreaInclusiveUnsignedLType area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 127L),
      new UnsignedRangeInclusiveL(0L, 127L));

    final R2ImageBufferDescriptionType desc =
      R2ImageBufferDescription.of(area, Optional.empty());
    final R2ImageBufferType ib =
      R2ImageBuffer.newImageBuffer(g_fb, g_t, tc, desc);
    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    final R2FilterBoxBlurParameters<
      R2ImageBufferDescriptionType,
      R2ImageBufferUsableType,
      R2ImageBufferDescriptionType,
      R2ImageBufferUsableType> params =
      R2FilterBoxBlurParameters.newParameters(
        ib,
        R2ImageBufferUsableType::getRGBATexture,
        ib,
        R2ImageBufferUsableType::getRGBATexture,
        R2ImageBufferDescriptionScaler.get(), rtp);

    params.setSourceRenderTarget(ib);
    params.setOutputRenderTarget(ib);
    params.setBlurPasses(1);
    params.setBlurScale(1.0f);
    params.setBlurSize(0.0f);

    f.runFilter(pro_root, tc, params);

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(g_fb.framebufferDrawIsBound(ib.getPrimaryFramebuffer()));
  }

  @Test
  public final void testFramebufferBindingPassesOneSizeOneScaleOne()
  {
    final JCGLContextType gc =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      gc.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType id =
      R2IDPool.newPool();
    final JCGLFramebuffersType g_fb =
      g.getFramebuffers();
    final JCGLTexturesType g_t =
      g.getTextures();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.getRootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.newDefaults(g_t, tc);
    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2RenderTargetPoolType<
      R2ImageBufferDescriptionType, R2ImageBufferUsableType> rtp =
      R2ImageBufferPool.newPool(g, Long.MAX_VALUE, Long.MAX_VALUE);
    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.getTimers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.getChildContext("main");

    final R2FilterType<
      R2FilterBoxBlurParameters<
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType,
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType>> f =
      R2FilterBoxBlur.newFilter(sources, g, td, rtp, id, quad);

    final AreaInclusiveUnsignedLType area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 127L),
      new UnsignedRangeInclusiveL(0L, 127L));

    final R2ImageBufferDescriptionType desc =
      R2ImageBufferDescription.of(area, Optional.empty());
    final R2ImageBufferType ib =
      R2ImageBuffer.newImageBuffer(g_fb, g_t, tc, desc);
    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    final R2FilterBoxBlurParameters<
      R2ImageBufferDescriptionType,
      R2ImageBufferUsableType,
      R2ImageBufferDescriptionType,
      R2ImageBufferUsableType> params =
      R2FilterBoxBlurParameters.newParameters(
        ib,
        R2ImageBufferUsableType::getRGBATexture,
        ib,
        R2ImageBufferUsableType::getRGBATexture,
        R2ImageBufferDescriptionScaler.get(), rtp);

    params.setSourceRenderTarget(ib);
    params.setOutputRenderTarget(ib);
    params.setBlurPasses(1);
    params.setBlurScale(1.0f);
    params.setBlurSize(1.0f);

    f.runFilter(pro_root, tc, params);

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(g_fb.framebufferDrawIsBound(ib.getPrimaryFramebuffer()));
  }

  @Test
  public final void testFramebufferBindingPassesOneSizeOneScaleHalf()
  {
    final JCGLContextType gc =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      gc.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType id =
      R2IDPool.newPool();
    final JCGLFramebuffersType g_fb =
      g.getFramebuffers();
    final JCGLTexturesType g_t =
      g.getTextures();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.getRootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.newDefaults(g_t, tc);
    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2RenderTargetPoolType<
      R2ImageBufferDescriptionType, R2ImageBufferUsableType> rtp =
      R2ImageBufferPool.newPool(g, Long.MAX_VALUE, Long.MAX_VALUE);
    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.getTimers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.getChildContext("main");

    final R2FilterType<
      R2FilterBoxBlurParameters<
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType,
        R2ImageBufferDescriptionType,
        R2ImageBufferUsableType>> f =
      R2FilterBoxBlur.newFilter(sources, g, td, rtp, id, quad);

    final AreaInclusiveUnsignedLType area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 127L),
      new UnsignedRangeInclusiveL(0L, 127L));

    final R2ImageBufferDescriptionType desc =
      R2ImageBufferDescription.of(area, Optional.empty());
    final R2ImageBufferType ib =
      R2ImageBuffer.newImageBuffer(g_fb, g_t, tc, desc);
    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    final R2FilterBoxBlurParameters<
      R2ImageBufferDescriptionType,
      R2ImageBufferUsableType,
      R2ImageBufferDescriptionType,
      R2ImageBufferUsableType> params =
      R2FilterBoxBlurParameters.newParameters(
        ib,
        R2ImageBufferUsableType::getRGBATexture,
        ib,
        R2ImageBufferUsableType::getRGBATexture,
        R2ImageBufferDescriptionScaler.get(), rtp);

    params.setSourceRenderTarget(ib);
    params.setOutputRenderTarget(ib);
    params.setBlurPasses(1);
    params.setBlurScale(0.5f);
    params.setBlurSize(1.0f);

    f.runFilter(pro_root, tc, params);

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(g_fb.framebufferDrawIsBound(ib.getPrimaryFramebuffer()));
  }
}
