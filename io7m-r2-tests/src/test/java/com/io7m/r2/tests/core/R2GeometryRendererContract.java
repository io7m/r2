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
import com.io7m.jfunctional.Unit;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.jtensors.parameterized.PMatrixI4x4F;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2GeometryBuffer;
import com.io7m.r2.core.R2GeometryBufferComponents;
import com.io7m.r2.core.R2GeometryBufferDescription;
import com.io7m.r2.core.R2GeometryBufferType;
import com.io7m.r2.core.R2GeometryRendererType;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2InstanceSingle;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2MaterialOpaqueSingle;
import com.io7m.r2.core.R2MaterialOpaqueSingleType;
import com.io7m.r2.core.R2Matrices;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionOrthographic;
import com.io7m.r2.core.R2SceneOpaques;
import com.io7m.r2.core.R2SceneOpaquesType;
import com.io7m.r2.core.R2TextureDefaults;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2TextureUnitAllocator;
import com.io7m.r2.core.R2TextureUnitAllocatorType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2TransformIdentity;
import com.io7m.r2.core.R2UnitQuad;
import com.io7m.r2.core.R2UnitQuadType;
import com.io7m.r2.core.profiling.R2Profiling;
import com.io7m.r2.core.profiling.R2ProfilingContextType;
import com.io7m.r2.core.profiling.R2ProfilingFrameType;
import com.io7m.r2.core.profiling.R2ProfilingType;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicParameters;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicSingle;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesResources;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;
import com.io7m.r2.shaders.R2Shaders;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public abstract class R2GeometryRendererContract extends R2JCGLContract
{
  private static R2SceneOpaquesType newScene(
    final JCGLInterfaceGL33Type g,
    final R2TextureDefaultsType td,
    final R2UnitQuadType quad,
    final R2IDPoolType id_pool)
  {
    final R2ShaderSourcesType ss =
      R2ShaderSourcesResources.newSources(R2Shaders.class);

    final R2InstanceSingleType i =
      R2InstanceSingle.newInstance(
        id_pool,
        quad.getArrayObject(),
        R2TransformIdentity.getInstance(),
        PMatrixI3x3F.identity());

    final R2ShaderInstanceSingleType<R2SurfaceShaderBasicParameters> ds =
      R2SurfaceShaderBasicSingle.newShader(
        g.getShaders(),
        ss,
        id_pool);

    final R2SurfaceShaderBasicParameters ds_param =
      R2SurfaceShaderBasicParameters.newParameters(td);

    final R2MaterialOpaqueSingleType<R2SurfaceShaderBasicParameters> mat =
      R2MaterialOpaqueSingle.newMaterial(id_pool, ds, ds_param);

    final R2SceneOpaquesType s = R2SceneOpaques.newOpaques();
    s.opaquesAddSingleInstance(i, mat);
    return s;
  }

  protected abstract R2GeometryRendererType getRenderer(
    final JCGLInterfaceGL33Type g);

  @Test
  public final void testIdentities()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

    final R2GeometryRendererType r = this.getRenderer(g);
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

    final R2GeometryRendererType r =
      this.getRenderer(g);
    final JCGLFramebuffersType g_fb =
      g.getFramebuffers();

    final AreaInclusiveUnsignedL area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 639L),
      new UnsignedRangeInclusiveL(0L, 479L));

    final R2TextureUnitAllocatorType ta =
      R2TextureUnitAllocator.newAllocatorWithStack(
        8, g.getTextures().textureGetUnits());
    final R2TextureUnitContextParentType tc =
      ta.getRootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.newDefaults(g.getTextures(), tc);

    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2IDPoolType id_pool =
      R2IDPool.newPool();

    final R2ProfilingType pro =
      R2Profiling.newProfiling(g.getTimers());
    final R2ProfilingFrameType pro_frame =
      pro.startFrame();
    final R2ProfilingContextType pro_root =
      pro_frame.getChildContext("main");

    final R2SceneOpaquesType s =
      R2GeometryRendererContract.newScene(g, td, quad, id_pool);

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    final R2MatricesType m = R2Matrices.newMatrices();
    m.withObserver(PMatrixI4x4F.identity(), proj, Unit.unit(), (x, y) -> {
      r.renderGeometry(area, Optional.empty(), pro_root, tc, x, s);
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

    final R2GeometryRendererType r =
      this.getRenderer(g);
    final JCGLFramebuffersType g_fb =
      g.getFramebuffers();

    final AreaInclusiveUnsignedL area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 639L),
      new UnsignedRangeInclusiveL(0L, 479L));

    final R2TextureUnitAllocatorType ta =
      R2TextureUnitAllocator.newAllocatorWithStack(
        8, g.getTextures().textureGetUnits());
    final R2TextureUnitContextParentType tc =
      ta.getRootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.newDefaults(g.getTextures(), tc);

    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2IDPoolType id_pool =
      R2IDPool.newPool();

    final R2ProfilingType pro =
      R2Profiling.newProfiling(g.getTimers());
    final R2ProfilingFrameType pro_frame =
      pro.startFrame();
    final R2ProfilingContextType pro_root =
      pro_frame.getChildContext("main");

    final R2SceneOpaquesType s =
      R2GeometryRendererContract.newScene(g, td, quad, id_pool);

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    final R2GeometryBufferType gbuffer = R2GeometryBuffer.newGeometryBuffer(
      g_fb,
      g.getTextures(),
      tc,
      R2GeometryBufferDescription.of(
        area, R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL));

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();
    g_fb.framebufferDrawBind(gbuffer.getPrimaryFramebuffer());

    final R2MatricesType m = R2Matrices.newMatrices();
    m.withObserver(PMatrixI4x4F.identity(), proj, Unit.unit(), (x, y) -> {
      r.renderGeometry(area, Optional.empty(), pro_root, tc, x, s);
      return Unit.unit();
    });

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(
      g_fb.framebufferDrawIsBound(gbuffer.getPrimaryFramebuffer()));
  }

  @Test
  public final void testFramebufferBindingDefaultProvided()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

    final R2GeometryRendererType r =
      this.getRenderer(g);
    final JCGLFramebuffersType g_fb =
      g.getFramebuffers();

    final AreaInclusiveUnsignedL area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 639L),
      new UnsignedRangeInclusiveL(0L, 479L));

    final R2TextureUnitAllocatorType ta =
      R2TextureUnitAllocator.newAllocatorWithStack(
        8, g.getTextures().textureGetUnits());
    final R2TextureUnitContextParentType tc =
      ta.getRootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.newDefaults(g.getTextures(), tc);

    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2IDPoolType id_pool =
      R2IDPool.newPool();

    final R2ProfilingType pro =
      R2Profiling.newProfiling(g.getTimers());
    final R2ProfilingFrameType pro_frame =
      pro.startFrame();
    final R2ProfilingContextType pro_root =
      pro_frame.getChildContext("main");

    final R2SceneOpaquesType s =
      R2GeometryRendererContract.newScene(g, td, quad, id_pool);

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    final R2GeometryBufferType gbuffer = R2GeometryBuffer.newGeometryBuffer(
      g_fb,
      g.getTextures(),
      tc,
      R2GeometryBufferDescription.of(
        area, R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL));

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    final R2MatricesType m = R2Matrices.newMatrices();
    m.withObserver(PMatrixI4x4F.identity(), proj, Unit.unit(), (x, y) -> {
      r.renderGeometry(area, Optional.of(gbuffer), pro_root, tc, x, s);
      return Unit.unit();
    });

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(
      g_fb.framebufferDrawIsBound(gbuffer.getPrimaryFramebuffer()));
  }
}
