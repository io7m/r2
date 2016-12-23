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

package com.io7m.r2.tests.core;

import com.io7m.jareas.core.AreaInclusiveUnsignedL;
import com.io7m.jcanephora.core.JCGLProjectionMatrices;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.profiler.JCGLProfiling;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jfunctional.Unit;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.jtensors.parameterized.PMatrixI4x4F;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2DepthAttachmentCreate;
import com.io7m.r2.core.R2DepthPrecision;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2InstanceBatchedDynamic;
import com.io7m.r2.core.R2InstanceBatchedDynamicType;
import com.io7m.r2.core.R2InstanceSingle;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2MaskBuffer;
import com.io7m.r2.core.R2MaskBufferDescription;
import com.io7m.r2.core.R2MaskBufferType;
import com.io7m.r2.core.R2MaskInstances;
import com.io7m.r2.core.R2MaskInstancesType;
import com.io7m.r2.core.R2MaskRendererType;
import com.io7m.r2.core.R2Matrices;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionOrthographic;
import com.io7m.r2.core.R2TextureDefaults;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2TransformIdentity;
import com.io7m.r2.core.R2UnitQuad;
import com.io7m.r2.core.R2UnitQuadType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

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
        R2TransformIdentity.getInstance(),
        PMatrixI3x3F.identity());

    final R2InstanceBatchedDynamicType ib0 =
      R2InstanceBatchedDynamic.newBatch(
        id_pool,
        g.getArrayBuffers(),
        g.getArrayObjects(),
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
      g.getFramebuffers();

    final AreaInclusiveUnsignedL area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 639L),
      new UnsignedRangeInclusiveL(0L, 479L));

    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g.getTextures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.getRootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.newDefaults(g.getTextures(), tc);

    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2IDPoolType id_pool =
      R2IDPool.newPool();

    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.getTimers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.getChildContext("main");

    final R2MaskInstancesType s = newScene(g, quad, id_pool);

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    final R2MatricesType m = R2Matrices.newMatrices();
    m.withObserver(PMatrixI4x4F.identity(), proj, Unit.unit(), (x, y) -> {
      r.renderMask(area, Optional.empty(), pro_root, tc, x, s);
      return Unit.unit();
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
      g.getFramebuffers();

    final AreaInclusiveUnsignedL area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 639L),
      new UnsignedRangeInclusiveL(0L, 479L));

    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g.getTextures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.getRootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.newDefaults(g.getTextures(), tc);

    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2IDPoolType id_pool =
      R2IDPool.newPool();

    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.getTimers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.getChildContext("main");

    final R2MaskInstancesType s = newScene(g, quad, id_pool);

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    final R2MaskBufferType mbuffer =
      R2MaskBuffer.newMaskBuffer(
        g_fb,
        g.getTextures(),
        tc,
        R2MaskBufferDescription.of(area, Optional.of(R2DepthAttachmentCreate.of(
          R2DepthPrecision.R2_DEPTH_PRECISION_16))));

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();
    g_fb.framebufferDrawBind(mbuffer.primaryFramebuffer());

    final R2MatricesType m = R2Matrices.newMatrices();
    m.withObserver(PMatrixI4x4F.identity(), proj, Unit.unit(), (x, y) -> {
      r.renderMask(area, Optional.empty(), pro_root, tc, x, s);
      return Unit.unit();
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
      g.getFramebuffers();

    final AreaInclusiveUnsignedL area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 639L),
      new UnsignedRangeInclusiveL(0L, 479L));

    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g.getTextures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.getRootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.newDefaults(g.getTextures(), tc);

    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2IDPoolType id_pool =
      R2IDPool.newPool();

    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.getTimers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.getChildContext("main");

    final R2MaskInstancesType s = newScene(g, quad, id_pool);

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    final R2MaskBufferType mbuffer =
      R2MaskBuffer.newMaskBuffer(
        g_fb,
        g.getTextures(),
        tc,
        R2MaskBufferDescription.of(area, Optional.of(R2DepthAttachmentCreate.of(
          R2DepthPrecision.R2_DEPTH_PRECISION_16))));

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    final R2MatricesType m = R2Matrices.newMatrices();
    m.withObserver(PMatrixI4x4F.identity(), proj, Unit.unit(), (x, y) -> {
      r.renderMask(area, Optional.of(mbuffer), pro_root, tc, x, s);
      return Unit.unit();
    });

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(
      g_fb.framebufferDrawIsBound(mbuffer.primaryFramebuffer()));
  }
}
