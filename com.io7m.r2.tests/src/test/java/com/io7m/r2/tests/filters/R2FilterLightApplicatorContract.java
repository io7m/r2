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

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizesL;
import com.io7m.r2.core.R2DepthAttachmentCreate;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2GeometryBuffer;
import com.io7m.r2.core.R2GeometryBufferDescription;
import com.io7m.r2.core.R2GeometryBufferType;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2ImageBuffer;
import com.io7m.r2.core.R2ImageBufferDescription;
import com.io7m.r2.core.R2ImageBufferPool;
import com.io7m.r2.core.R2ImageBufferType;
import com.io7m.r2.core.R2ImageBufferUsableType;
import com.io7m.r2.core.R2RenderTargetPoolType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2UnitQuad;
import com.io7m.r2.core.R2UnitQuadType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.filters.R2FilterLightApplicator;
import com.io7m.r2.filters.R2FilterLightApplicatorParameters;
import com.io7m.r2.tests.core.R2JCGLContract;
import com.io7m.r2.tests.core.ShaderPreprocessing;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static com.io7m.jcanephora.profiler.JCGLProfiling.newProfiling;
import static com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator.newAllocatorWithStack;
import static com.io7m.r2.core.R2CopyDepth.R2_COPY_DEPTH_DISABLED;
import static com.io7m.r2.core.R2CopyDepth.R2_COPY_DEPTH_ENABLED;
import static com.io7m.r2.core.R2DepthPrecision.R2_DEPTH_PRECISION_24;
import static com.io7m.r2.core.R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL;
import static com.io7m.r2.core.R2IDPool.newPool;
import static com.io7m.r2.core.R2ImageBufferDescription.of;
import static com.io7m.r2.core.R2TextureDefaults.create;
import static com.io7m.r2.core.R2UnitQuad.newUnitQuad;
import static com.io7m.r2.filters.R2FilterLightApplicator.newFilter;
import static com.io7m.r2.filters.R2FilterLightApplicatorParameters.builder;
import static com.io7m.r2.tests.core.ShaderPreprocessing.preprocessor;
import static java.util.Optional.empty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class R2FilterLightApplicatorContract extends R2JCGLContract
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
    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2RenderTargetPoolType<
      R2ImageBufferDescription, R2ImageBufferUsableType> rtp =
      R2ImageBufferPool.newPool(g, Long.MAX_VALUE, Long.MAX_VALUE);

    final R2FilterType<R2FilterLightApplicatorParameters> f =
      R2FilterLightApplicator.newFilter(sources, g, id, quad);

    Assert.assertFalse(f.isDeleted());
    Assert.assertFalse(f.isDeleted());
    f.delete(g);
    Assert.assertTrue(f.isDeleted());
  }

  @Test
  public final void testFramebufferBindingCopyDepthNoDepth()
  {
    final JCGLContextType gc =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      gc.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2IDPoolType id =
      newPool();
    final JCGLFramebuffersType g_fb =
      g.framebuffers();
    final JCGLTexturesType g_t =
      g.textures();
    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      create(g.textures(), tc);
    final R2UnitQuadType quad =
      newUnitQuad(g);
    final JCGLProfilingType pro =
      newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final AreaSizeL area = AreaSizeL.of(128L, 128L);

    final R2ImageBufferType ib =
      R2ImageBuffer.create(
        g_fb, g_t, tc, of(area, empty()));

    final R2GeometryBufferDescription desc =
      R2GeometryBufferDescription.of(
        area, R2_GEOMETRY_BUFFER_FULL);
    final R2GeometryBufferType gbuffer =
      R2GeometryBuffer.create(g_fb, g_t, tc, desc);

    final R2FilterType<R2FilterLightApplicatorParameters> f =
      newFilter(sources, g, id, quad);

    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    assertFalse(g_fb.framebufferReadAnyIsBound());
    assertFalse(g_fb.framebufferDrawAnyIsBound());

    g_fb.framebufferDrawBind(ib.primaryFramebuffer());

    final R2FilterLightApplicatorParameters params =
      builder()
        .setLightDiffuseTexture(td.black2D())
        .setLightSpecularTexture(td.black2D())
        .setGeometryBuffer(gbuffer)
        .setOutputViewport(AreaSizesL.area(area))
        .setCopyDepth(R2_COPY_DEPTH_ENABLED)
        .build();

    f.runFilter(pro_root, tc, params);

    assertFalse(g_fb.framebufferReadAnyIsBound());
    assertTrue(g_fb.framebufferDrawIsBound(ib.primaryFramebuffer()));
  }

  @Test
  public final void testFramebufferBindingNoCopyDepthNoDepth()
  {
    final JCGLContextType gc =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      gc.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2IDPoolType id =
      newPool();
    final JCGLFramebuffersType g_fb =
      g.framebuffers();
    final JCGLTexturesType g_t =
      g.textures();
    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      create(g.textures(), tc);
    final R2UnitQuadType quad =
      newUnitQuad(g);
    final JCGLProfilingType pro =
      newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final AreaSizeL area = AreaSizeL.of(128L, 128L);

    final R2ImageBufferType ib =
      R2ImageBuffer.create(
        g_fb, g_t, tc, of(area, empty()));

    final R2GeometryBufferDescription desc =
      R2GeometryBufferDescription.of(
        area, R2_GEOMETRY_BUFFER_FULL);
    final R2GeometryBufferType gbuffer =
      R2GeometryBuffer.create(g_fb, g_t, tc, desc);

    final R2FilterType<R2FilterLightApplicatorParameters> f =
      newFilter(sources, g, id, quad);

    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    assertFalse(g_fb.framebufferReadAnyIsBound());
    assertFalse(g_fb.framebufferDrawAnyIsBound());

    g_fb.framebufferDrawBind(ib.primaryFramebuffer());

    final R2FilterLightApplicatorParameters params =
      builder()
        .setLightDiffuseTexture(td.black2D())
        .setLightSpecularTexture(td.black2D())
        .setGeometryBuffer(gbuffer)
        .setOutputViewport(AreaSizesL.area(area))
        .setCopyDepth(R2_COPY_DEPTH_DISABLED)
        .build();

    f.runFilter(pro_root, tc, params);

    assertFalse(g_fb.framebufferReadAnyIsBound());
    assertTrue(g_fb.framebufferDrawIsBound(ib.primaryFramebuffer()));
  }


  @Test
  public final void testFramebufferBindingCopyDepthWithDepth()
  {
    final JCGLContextType gc =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      gc.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2IDPoolType id =
      newPool();
    final JCGLFramebuffersType g_fb =
      g.framebuffers();
    final JCGLTexturesType g_t =
      g.textures();
    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      create(g.textures(), tc);
    final R2UnitQuadType quad =
      newUnitQuad(g);
    final JCGLProfilingType pro =
      newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final AreaSizeL area = AreaSizeL.of(128L, 128L);

    final R2ImageBufferType ib =
      R2ImageBuffer.create(
        g_fb, g_t, tc,
        of(area, Optional.of(
          R2DepthAttachmentCreate.of(R2_DEPTH_PRECISION_24))));

    final R2GeometryBufferDescription desc =
      R2GeometryBufferDescription.of(
        area, R2_GEOMETRY_BUFFER_FULL);
    final R2GeometryBufferType gbuffer =
      R2GeometryBuffer.create(g_fb, g_t, tc, desc);

    final R2FilterType<R2FilterLightApplicatorParameters> f =
      newFilter(sources, g, id, quad);

    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    assertFalse(g_fb.framebufferReadAnyIsBound());
    assertFalse(g_fb.framebufferDrawAnyIsBound());

    g_fb.framebufferDrawBind(ib.primaryFramebuffer());

    final R2FilterLightApplicatorParameters params =
      builder()
        .setLightDiffuseTexture(td.black2D())
        .setLightSpecularTexture(td.black2D())
        .setGeometryBuffer(gbuffer)
        .setOutputViewport(AreaSizesL.area(area))
        .setCopyDepth(R2_COPY_DEPTH_ENABLED)
        .build();

    f.runFilter(pro_root, tc, params);

    assertFalse(g_fb.framebufferReadAnyIsBound());
    assertTrue(g_fb.framebufferDrawIsBound(ib.primaryFramebuffer()));
  }

  @Test
  public final void testFramebufferBindingNoCopyDepthWithDepth()
  {
    final JCGLContextType gc =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      gc.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2IDPoolType id =
      newPool();
    final JCGLFramebuffersType g_fb =
      g.framebuffers();
    final JCGLTexturesType g_t =
      g.textures();
    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      create(g.textures(), tc);
    final R2UnitQuadType quad =
      newUnitQuad(g);
    final JCGLProfilingType pro =
      newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final AreaSizeL area = AreaSizeL.of(128L, 128L);

    final R2ImageBufferType ib =
      R2ImageBuffer.create(
        g_fb, g_t, tc,
        of(area, Optional.of(
          R2DepthAttachmentCreate.of(R2_DEPTH_PRECISION_24))));

    final R2GeometryBufferDescription desc =
      R2GeometryBufferDescription.of(
        area, R2_GEOMETRY_BUFFER_FULL);
    final R2GeometryBufferType gbuffer =
      R2GeometryBuffer.create(g_fb, g_t, tc, desc);

    final R2FilterType<R2FilterLightApplicatorParameters> f =
      newFilter(sources, g, id, quad);

    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    assertFalse(g_fb.framebufferReadAnyIsBound());
    assertFalse(g_fb.framebufferDrawAnyIsBound());

    g_fb.framebufferDrawBind(ib.primaryFramebuffer());

    final R2FilterLightApplicatorParameters params =
      builder()
        .setLightDiffuseTexture(td.black2D())
        .setLightSpecularTexture(td.black2D())
        .setGeometryBuffer(gbuffer)
        .setOutputViewport(AreaSizesL.area(area))
        .setCopyDepth(R2_COPY_DEPTH_DISABLED)
        .build();

    f.runFilter(pro_root, tc, params);

    assertFalse(g_fb.framebufferReadAnyIsBound());
    assertTrue(g_fb.framebufferDrawIsBound(ib.primaryFramebuffer()));
  }
}
