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

package com.io7m.r2.tests.rendering.mask.api;

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.profiler.JCGLProfiling;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizesL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices3x3D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.r2.core.api.ids.R2IDPool;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.images.api.R2DepthAttachmentCreate;
import com.io7m.r2.instances.R2InstanceBatchedDynamic;
import com.io7m.r2.instances.R2InstanceBatchedDynamicType;
import com.io7m.r2.instances.R2InstanceSingle;
import com.io7m.r2.instances.R2InstanceSingleType;
import com.io7m.r2.matrices.R2Matrices;
import com.io7m.r2.matrices.R2MatricesType;
import com.io7m.r2.meshes.defaults.R2UnitQuad;
import com.io7m.r2.projections.R2ProjectionOrthographic;
import com.io7m.r2.rendering.depth.api.R2DepthPrecision;
import com.io7m.r2.rendering.mask.R2MaskBuffer;
import com.io7m.r2.rendering.mask.api.R2MaskBufferDescription;
import com.io7m.r2.rendering.mask.api.R2MaskBufferType;
import com.io7m.r2.rendering.mask.api.R2MaskInstances;
import com.io7m.r2.rendering.mask.api.R2MaskInstancesType;
import com.io7m.r2.rendering.mask.api.R2MaskRendererType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.tests.R2JCGLContract;
import com.io7m.r2.tests.ShaderPreprocessing;
import com.io7m.r2.transforms.R2TransformIdentity;
import com.io7m.r2.unit_quads.R2UnitQuadType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static com.io7m.jfunctional.Unit.unit;
import static java.util.Optional.empty;

public abstract class R2MaskRendererContract extends R2JCGLContract
{
  private static R2MaskInstancesType newScene(
    final JCGLInterfaceGL33Type g,
    final R2UnitQuadType quad,
    final R2IDPoolType id_pool)
  {
    final R2InstanceSingleType is0 =
      R2InstanceSingle.of(
        id_pool.freshID(),
        quad.arrayObject(),
        R2TransformIdentity.get(),
        PMatrices3x3D.identity());

    final R2InstanceBatchedDynamicType ib0 =
      R2InstanceBatchedDynamic.create(
        id_pool,
        g.arrayBuffers(),
        g.arrayObjects(),
        quad.arrayObject(),
        3);

    return R2MaskInstances.builder()
      .addSingles(is0)
      .addBatched(ib0)
      .build();
  }

  protected abstract R2MaskRendererType getRenderer(
    final JCGLInterfaceGL33Type g,
    R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    R2IDPoolType in_id_pool);

  @Test
  public final void testIdentities()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool = R2IDPool.newPool();

    final R2MaskRendererType r = this.getRenderer(g, sources, pool);
    Assert.assertFalse(r.isDeleted());
    r.delete(g);
    Assert.assertTrue(r.isDeleted());
  }

  @Test
  public final void testFramebufferBindingDefaultNotProvided()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool = R2IDPool.newPool();

    final R2MaskRendererType r =
      this.getRenderer(g, sources, pool);
    final JCGLFramebuffersType g_fb =
      g.framebuffers();

    final AreaSizeL area = AreaSizeL.of(640L, 480L);

    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g.textures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();

    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);

    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final R2MaskInstancesType s = newScene(g, quad, pool);

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.create();

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    final R2MatricesType m = R2Matrices.create();
    m.withObserver(
      PMatrices4x4D.identity(),
      proj,
      unit(),
      (x, y) -> {
        r.renderMask(AreaSizesL.area(area), empty(), pro_root, tc, x, s);
        return unit();
      });

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());
  }

  @Test
  public final void testFramebufferBindingNonDefaultNotProvided()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool = R2IDPool.newPool();

    final R2MaskRendererType r =
      this.getRenderer(g, sources, pool);
    final JCGLFramebuffersType g_fb =
      g.framebuffers();

    final AreaSizeL area = AreaSizeL.of(640L, 480L);
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g.textures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();

    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);

    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final R2MaskInstancesType s = newScene(g, quad, pool);

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.create();

    final R2MaskBufferType mbuffer =
      R2MaskBuffer.create(
        g_fb,
        g.textures(),
        tc,
        R2MaskBufferDescription.of(area, Optional.of(R2DepthAttachmentCreate.of(
          R2DepthPrecision.R2_DEPTH_PRECISION_16))));

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();
    g_fb.framebufferDrawBind(mbuffer.primaryFramebuffer());

    final R2MatricesType m = R2Matrices.create();
    m.withObserver(
      PMatrices4x4D.identity(),
      proj,
      unit(),
      (x, y) -> {
        r.renderMask(AreaSizesL.area(area), empty(), pro_root, tc, x, s);
        return unit();
      });

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(
      g_fb.framebufferDrawIsBound(mbuffer.primaryFramebuffer()));
  }

  @Test
  public final void testFramebufferBindingDefaultProvided()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool = R2IDPool.newPool();

    final R2MaskRendererType r =
      this.getRenderer(g, sources, pool);
    final JCGLFramebuffersType g_fb =
      g.framebuffers();

    final AreaSizeL area = AreaSizeL.of(640L, 480L);

    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g.textures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();

    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);

    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final R2MaskInstancesType s = newScene(g, quad, pool);

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.create();

    final R2MaskBufferType mbuffer =
      R2MaskBuffer.create(
        g_fb,
        g.textures(),
        tc,
        R2MaskBufferDescription.of(area, Optional.of(R2DepthAttachmentCreate.of(
          R2DepthPrecision.R2_DEPTH_PRECISION_16))));

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    final R2MatricesType m = R2Matrices.create();
    m.withObserver(
      PMatrices4x4D.identity(),
      proj,
      unit(),
      (x, y) -> {
        r.renderMask(
          AreaSizesL.area(area),
          Optional.of(mbuffer),
          pro_root,
          tc,
          x,
          s);
        return unit();
      });

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(
      g_fb.framebufferDrawIsBound(mbuffer.primaryFramebuffer()));
  }
}
