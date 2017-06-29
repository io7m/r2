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

package com.io7m.r2.tests.filters.ssao;

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
import com.io7m.r2.core.api.ids.R2IDPool;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.filters.api.R2FilterType;
import com.io7m.r2.filters.ssao.R2AmbientOcclusionBuffer;
import com.io7m.r2.filters.ssao.R2FilterSSAO;
import com.io7m.r2.filters.ssao.R2SSAOKernel;
import com.io7m.r2.filters.ssao.api.R2AmbientOcclusionBufferDescription;
import com.io7m.r2.filters.ssao.api.R2AmbientOcclusionBufferType;
import com.io7m.r2.filters.ssao.api.R2FilterSSAOParameters;
import com.io7m.r2.filters.ssao.api.R2SSAOKernelType;
import com.io7m.r2.meshes.defaults.R2UnitQuad;
import com.io7m.r2.rendering.geometry.R2GeometryBuffer;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferComponents;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferDescription;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.tests.R2JCGLContract;
import com.io7m.r2.tests.ShaderPreprocessing;
import com.io7m.r2.textures.R2TextureDefaults;
import com.io7m.r2.textures.R2TextureDefaultsType;
import com.io7m.r2.unit_quads.R2UnitQuadType;
import org.junit.Assert;
import org.junit.Test;

import static com.io7m.r2.tests.core.R2TestUtilities.getMatricesObserverValues;

public abstract class R2FilterSSAOContract extends R2JCGLContract
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

    final R2FilterType<R2FilterSSAOParameters> f =
      R2FilterSSAO.newFilter(sources, g, id, quad);

    Assert.assertFalse(f.isDeleted());
    Assert.assertFalse(f.isDeleted());
    f.delete(g);
    Assert.assertTrue(f.isDeleted());
  }

  @Test
  public final void testFramebufferBinding()
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
    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final AreaSizeL area = AreaSizeL.of(128L, 128L);

    final R2AmbientOcclusionBufferType abuffer =
      R2AmbientOcclusionBuffer.create(
        g_fb,
        g_t,
        tc,
        R2AmbientOcclusionBufferDescription.of(area));

    final R2GeometryBufferType gbuffer =
      R2GeometryBuffer.create(
        g_fb,
        g_t,
        tc,
        R2GeometryBufferDescription.of(
          area, R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL));

    final R2SSAOKernelType k = R2SSAOKernel.newKernel(64);

    final R2FilterType<R2FilterSSAOParameters> f =
      R2FilterSSAO.newFilter(sources, g, id, quad);

    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());

    final R2FilterSSAOParameters params =
      R2FilterSSAOParameters.builder()
        .setNoiseTexture(td.normal2D())
        .setOutputBuffer(abuffer)
        .setKernel(k)
        .setGeometryBuffer(gbuffer)
        .setSampleRadius(1.0f)
        .setSceneObserverValues(getMatricesObserverValues())
        .build();

    f.runFilter(pro_root, tc, params);

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(g_fb.framebufferDrawIsBound(abuffer.primaryFramebuffer()));
  }
}
