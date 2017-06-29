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

package com.io7m.r2.tests.filters.emission;

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.profiler.JCGLProfiling;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizesL;
import com.io7m.r2.core.api.ids.R2IDPool;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.filters.api.R2FilterType;
import com.io7m.r2.filters.box_blur.R2FilterBoxBlur;
import com.io7m.r2.filters.box_blur.api.R2BlurParameters;
import com.io7m.r2.filters.box_blur.api.R2FilterBoxBlurParameters;
import com.io7m.r2.filters.emission.R2FilterEmission;
import com.io7m.r2.filters.emission.api.R2FilterEmissionParameters;
import com.io7m.r2.images.R2ImageBuffer;
import com.io7m.r2.images.R2ImageBufferPool;
import com.io7m.r2.images.api.R2ImageBufferDescription;
import com.io7m.r2.images.api.R2ImageBufferType;
import com.io7m.r2.images.api.R2ImageBufferUsableType;
import com.io7m.r2.meshes.defaults.R2UnitQuad;
import com.io7m.r2.rendering.targets.R2RenderTargetPoolType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.tests.R2JCGLContract;
import com.io7m.r2.tests.ShaderPreprocessing;
import com.io7m.r2.textures.R2TextureDefaults;
import com.io7m.r2.textures.R2TextureDefaultsType;
import com.io7m.r2.unit_quads.R2UnitQuadType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static java.util.Optional.empty;


public abstract class R2FilterEmissionContract extends R2JCGLContract
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
      g.textures();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.create(g.textures(), tc);
    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2RenderTargetPoolType<
      R2ImageBufferDescription, R2ImageBufferUsableType> rtp =
      R2ImageBufferPool.newPool(g, Long.MAX_VALUE, Long.MAX_VALUE);

    final R2FilterType<
      R2FilterBoxBlurParameters<
        R2ImageBufferDescription,
        R2ImageBufferUsableType,
        R2ImageBufferDescription,
        R2ImageBufferUsableType>> fblur =
      R2FilterBoxBlur.newFilter(sources, g, td, rtp, id, quad);

    final R2FilterType<R2FilterEmissionParameters> f =
      R2FilterEmission.newFilter(g, sources, id, fblur, rtp, quad);

    Assert.assertFalse(fblur.isDeleted());
    Assert.assertFalse(f.isDeleted());
    f.delete(g);
    Assert.assertFalse(fblur.isDeleted());
    Assert.assertTrue(f.isDeleted());
  }

  @Test
  public final void testFramebufferBindingNoBlurNoOutput()
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
      g.framebuffers();
    final JCGLTexturesType g_t =
      g.textures();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.create(g.textures(), tc);
    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2RenderTargetPoolType<
      R2ImageBufferDescription, R2ImageBufferUsableType> rtp =
      R2ImageBufferPool.newPool(g, Long.MAX_VALUE, Long.MAX_VALUE);
    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final R2FilterType<
      R2FilterBoxBlurParameters<
        R2ImageBufferDescription,
        R2ImageBufferUsableType,
        R2ImageBufferDescription,
        R2ImageBufferUsableType>> fblur =
      R2FilterBoxBlur.newFilter(sources, g, td, rtp, id, quad);

    final R2FilterType<R2FilterEmissionParameters> f =
      R2FilterEmission.newFilter(g, sources, id, fblur, rtp, quad);

    final AreaSizeL area = AreaSizeL.of(128L, 128L);

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    final R2FilterEmissionParameters params =
      R2FilterEmissionParameters.builder()
        .setTextureDefaults(td)
        .setAlbedoEmissionMap(td.white2D())
        .setBlurParameters(empty())
        .setOutputViewport(AreaSizesL.area(area))
        .setOutputFramebuffer(empty())
        .setScale(1.0f)
        .build();

    f.runFilter(pro_root, tc, params);

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());
  }

  @Test
  public final void testFramebufferBindingNoBlurWithOutput()
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
      g.framebuffers();
    final JCGLTexturesType g_t =
      g.textures();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.create(g_t, tc);
    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2RenderTargetPoolType<
      R2ImageBufferDescription, R2ImageBufferUsableType> rtp =
      R2ImageBufferPool.newPool(g, Long.MAX_VALUE, Long.MAX_VALUE);
    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final R2FilterType<
      R2FilterBoxBlurParameters<
        R2ImageBufferDescription,
        R2ImageBufferUsableType,
        R2ImageBufferDescription,
        R2ImageBufferUsableType>> fblur =
      R2FilterBoxBlur.newFilter(sources, g, td, rtp, id, quad);

    final R2FilterType<R2FilterEmissionParameters> f =
      R2FilterEmission.newFilter(g, sources, id, fblur, rtp, quad);

    final AreaSizeL area = AreaSizeL.of(128L, 128L);

    final R2ImageBufferDescription desc =
      R2ImageBufferDescription.of(area, empty());
    final R2ImageBufferType ib =
      R2ImageBuffer.create(g_fb, g_t, tc, desc);
    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    final R2FilterEmissionParameters params =
      R2FilterEmissionParameters.builder()
        .setTextureDefaults(td)
        .setAlbedoEmissionMap(td.white2D())
        .setBlurParameters(empty())
        .setOutputViewport(AreaSizesL.area(area))
        .setOutputFramebuffer(Optional.of(ib.primaryFramebuffer()))
        .setScale(1.0f)
        .build();

    f.runFilter(pro_root, tc, params);

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(g_fb.framebufferDrawIsBound(ib.primaryFramebuffer()));
  }

  @Test
  public final void testFramebufferBindingBlurWithOutput()
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
      g.framebuffers();
    final JCGLTexturesType g_t =
      g.textures();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.create(g_t, tc);
    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2RenderTargetPoolType<
      R2ImageBufferDescription, R2ImageBufferUsableType> rtp =
      R2ImageBufferPool.newPool(g, Long.MAX_VALUE, Long.MAX_VALUE);
    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final R2FilterType<
      R2FilterBoxBlurParameters<
        R2ImageBufferDescription,
        R2ImageBufferUsableType,
        R2ImageBufferDescription,
        R2ImageBufferUsableType>> fblur =
      R2FilterBoxBlur.newFilter(sources, g, td, rtp, id, quad);

    final R2FilterType<R2FilterEmissionParameters> f =
      R2FilterEmission.newFilter(g, sources, id, fblur, rtp, quad);

    final AreaSizeL area = AreaSizeL.of(128L, 128L);

    final R2ImageBufferDescription desc =
      R2ImageBufferDescription.of(area, empty());
    final R2ImageBufferType ib =
      R2ImageBuffer.create(g_fb, g_t, tc, desc);
    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    final R2FilterEmissionParameters params =
      R2FilterEmissionParameters.builder()
        .setTextureDefaults(td)
        .setAlbedoEmissionMap(td.white2D())
        .setBlurParameters(Optional.of(R2BlurParameters.builder().build()))
        .setOutputViewport(AreaSizesL.area(area))
        .setOutputFramebuffer(Optional.of(ib.primaryFramebuffer()))
        .setScale(1.0f)
        .build();

    f.runFilter(pro_root, tc, params);

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(g_fb.framebufferDrawIsBound(ib.primaryFramebuffer()));
  }

  @Test
  public final void testFramebufferBindingBlurScaleWithOutput()
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
      g.framebuffers();
    final JCGLTexturesType g_t =
      g.textures();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.create(g_t, tc);
    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2RenderTargetPoolType<
      R2ImageBufferDescription, R2ImageBufferUsableType> rtp =
      R2ImageBufferPool.newPool(g, Long.MAX_VALUE, Long.MAX_VALUE);
    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final R2FilterType<
      R2FilterBoxBlurParameters<
        R2ImageBufferDescription,
        R2ImageBufferUsableType,
        R2ImageBufferDescription,
        R2ImageBufferUsableType>> fblur =
      R2FilterBoxBlur.newFilter(sources, g, td, rtp, id, quad);

    final R2FilterType<R2FilterEmissionParameters> f =
      R2FilterEmission.newFilter(g, sources, id, fblur, rtp, quad);

    final AreaSizeL area = AreaSizeL.of(128L, 128L);

    final R2ImageBufferDescription desc =
      R2ImageBufferDescription.of(area, empty());
    final R2ImageBufferType ib =
      R2ImageBuffer.create(g_fb, g_t, tc, desc);
    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    final R2BlurParameters.Builder bpb = R2BlurParameters.builder();
    bpb.setBlurScale(0.5f);

    final R2FilterEmissionParameters params =
      R2FilterEmissionParameters.builder()
        .setTextureDefaults(td)
        .setAlbedoEmissionMap(td.white2D())
        .setBlurParameters(Optional.of(bpb.build()))
        .setOutputViewport(AreaSizesL.area(area))
        .setOutputFramebuffer(Optional.of(ib.primaryFramebuffer()))
        .setScale(0.5f)
        .build();

    f.runFilter(pro_root, tc, params);

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(g_fb.framebufferDrawIsBound(ib.primaryFramebuffer()));
  }

  @Test
  public final void testFramebufferBindingBlurScaleWithoutOutput()
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
      g.framebuffers();
    final JCGLTexturesType g_t =
      g.textures();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.create(g_t, tc);
    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2RenderTargetPoolType<
      R2ImageBufferDescription, R2ImageBufferUsableType> rtp =
      R2ImageBufferPool.newPool(g, Long.MAX_VALUE, Long.MAX_VALUE);
    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final R2FilterType<
      R2FilterBoxBlurParameters<
        R2ImageBufferDescription,
        R2ImageBufferUsableType,
        R2ImageBufferDescription,
        R2ImageBufferUsableType>> fblur =
      R2FilterBoxBlur.newFilter(sources, g, td, rtp, id, quad);

    final R2FilterType<R2FilterEmissionParameters> f =
      R2FilterEmission.newFilter(g, sources, id, fblur, rtp, quad);

    final AreaSizeL area = AreaSizeL.of(128L, 128L);

    final R2ImageBufferDescription desc = R2ImageBufferDescription.of(
      area,
      empty());
    final R2ImageBufferType ib =
      R2ImageBuffer.create(g_fb, g_t, tc, desc);
    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    final R2BlurParameters.Builder bpb = R2BlurParameters.builder();
    bpb.setBlurScale(0.5f);

    final R2FilterEmissionParameters params =
      R2FilterEmissionParameters.builder()
        .setTextureDefaults(td)
        .setAlbedoEmissionMap(td.white2D())
        .setBlurParameters(Optional.of(bpb.build()))
        .setOutputViewport(AreaSizesL.area(area))
        .setOutputFramebuffer(empty())
        .setScale(0.5f)
        .build();

    f.runFilter(pro_root, tc, params);

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());
  }
}
