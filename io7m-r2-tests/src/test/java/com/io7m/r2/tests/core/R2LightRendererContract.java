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
import com.io7m.jtensors.parameterized.PMatrixI4x4F;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2GeometryBuffer;
import com.io7m.r2.core.R2GeometryBufferComponents;
import com.io7m.r2.core.R2GeometryBufferDescription;
import com.io7m.r2.core.R2GeometryBufferType;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightBufferComponents;
import com.io7m.r2.core.R2LightBufferDescription;
import com.io7m.r2.core.R2LightBufferType;
import com.io7m.r2.core.R2LightBuffers;
import com.io7m.r2.core.R2LightRendererType;
import com.io7m.r2.core.R2LightSphericalSingle;
import com.io7m.r2.core.R2LightSphericalSingleReadableType;
import com.io7m.r2.core.R2LightSphericalSingleType;
import com.io7m.r2.core.R2Matrices;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionOrthographic;
import com.io7m.r2.core.R2SceneLights;
import com.io7m.r2.core.R2SceneLightsGroupType;
import com.io7m.r2.core.R2SceneLightsType;
import com.io7m.r2.core.R2ShadowMapContextUsableType;
import com.io7m.r2.core.R2TextureDefaults;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2UnitQuad;
import com.io7m.r2.core.R2UnitQuadType;
import com.io7m.r2.core.R2UnitQuadUsableType;
import com.io7m.r2.core.R2UnitSphereType;
import com.io7m.r2.core.R2UnitSphereUsableType;
import com.io7m.r2.core.shaders.provided.R2LightShaderSphericalLambertBlinnPhongSingle;
import com.io7m.r2.core.shaders.types.R2ShaderLightVolumeSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.meshes.defaults.R2UnitSphere;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public abstract class R2LightRendererContract extends R2JCGLContract
{
  private static R2SceneLightsType newScene(
    final JCGLInterfaceGL33Type g,
    final R2UnitSphereUsableType sphere,
    final R2IDPoolType id_pool)
  {
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2LightSphericalSingleType ls =
      R2LightSphericalSingle.newLight(sphere, id_pool);

    final R2ShaderLightVolumeSingleType<R2LightSphericalSingleReadableType> ds =
      R2LightShaderSphericalLambertBlinnPhongSingle.newShader(
        g.getShaders(), sources, id_pool);

    final R2SceneLightsType s = R2SceneLights.newLights();
    final R2SceneLightsGroupType lg = s.lightsGetGroup(1);
    lg.lightGroupAddSingle(ls, ds);
    return s;
  }

  protected abstract R2LightRendererType getRenderer(
    final JCGLInterfaceGL33Type g,
    R2TextureDefaultsType td,
    R2ShaderPreprocessingEnvironmentType ss,
    R2IDPoolType id_pool,
    R2UnitQuadUsableType quad);

  @Test
  public final void testIdentities()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g.getTextures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.getRootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.newDefaults(g.getTextures(), tc);

    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType id_pool =
      R2IDPool.newPool();
    final R2LightRendererType r =
      this.getRenderer(g, td, sources, id_pool, quad);

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

    final R2UnitSphereType sphere =
      R2UnitSphere.newUnitSphere8(g);
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

    final R2SceneLightsType s =
      R2LightRendererContract.newScene(g, sphere, id_pool);

    final R2ShadowMapContextUsableType shadows =
      ls -> {
        throw new UnreachableCodeException();
      };

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2LightRendererType r =
      this.getRenderer(g, td, sources, id_pool, quad);

    final R2GeometryBufferType gbuffer =
      R2GeometryBuffer.newGeometryBuffer(
        g_fb, g.getTextures(), tc,
        R2GeometryBufferDescription.of(
          area,
          R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL));

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    final R2MatricesType m = R2Matrices.newMatrices();
    m.withObserver(PMatrixI4x4F.identity(), proj, Unit.unit(), (x, y) -> {
      r.renderLights(
        gbuffer,
        area,
        Optional.empty(),
        pro_root,
        tc,
        shadows,
        x,
        s);
      return Unit.unit();
    });

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertFalse(g_fb.framebufferDrawAnyIsBound());
  }

  @Test
  public final void testFramebufferBindingDefaultProvided()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

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

    final R2UnitSphereType sphere =
      R2UnitSphere.newUnitSphere8(g);
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

    final R2SceneLightsType s =
      R2LightRendererContract.newScene(g, sphere, id_pool);

    final R2ShadowMapContextUsableType shadows =
      ls -> {
        throw new UnreachableCodeException();
      };

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2LightRendererType r =
      this.getRenderer(g, td, sources, id_pool, quad);

    final R2GeometryBufferType gbuffer =
      R2GeometryBuffer.newGeometryBuffer(
        g_fb, g.getTextures(), tc,
        R2GeometryBufferDescription.of(
          area,
          R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL));

    final R2LightBufferType lbuffer = R2LightBuffers.newLightBuffer(
      g_fb,
      g.getTextures(),
      tc,
      R2LightBufferDescription.of(
        area,
        R2LightBufferComponents.R2_LIGHT_BUFFER_DIFFUSE_AND_SPECULAR));

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    final R2MatricesType m = R2Matrices.newMatrices();
    m.withObserver(PMatrixI4x4F.identity(), proj, Unit.unit(), (x, y) -> {
      r.renderLights(
        gbuffer,
        area,
        Optional.of(lbuffer),
        pro_root,
        tc,
        shadows,
        x,
        s);
      return Unit.unit();
    });

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(g_fb.framebufferDrawIsBound(
      lbuffer.primaryFramebuffer()));
  }

  @Test
  public final void testFramebufferBindingNonDefaultNotProvided()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

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

    final R2UnitSphereType sphere =
      R2UnitSphere.newUnitSphere8(g);
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

    final R2SceneLightsType s =
      R2LightRendererContract.newScene(g, sphere, id_pool);

    final R2ShadowMapContextUsableType shadows =
      ls -> {
        throw new UnreachableCodeException();
      };

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2LightRendererType r =
      this.getRenderer(g, td, sources, id_pool, quad);

    final R2GeometryBufferType gbuffer =
      R2GeometryBuffer.newGeometryBuffer(
        g_fb, g.getTextures(), tc,
        R2GeometryBufferDescription.of(
          area,
          R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL));

    final R2LightBufferType lbuffer = R2LightBuffers.newLightBuffer(
      g_fb,
      g.getTextures(),
      tc,
      R2LightBufferDescription.of(
        area,
        R2LightBufferComponents.R2_LIGHT_BUFFER_DIFFUSE_AND_SPECULAR));

    g_fb.framebufferReadUnbind();
    Assert.assertTrue(g_fb.framebufferDrawIsBound(
      lbuffer.primaryFramebuffer()));

    final R2MatricesType m = R2Matrices.newMatrices();
    m.withObserver(PMatrixI4x4F.identity(), proj, Unit.unit(), (x, y) -> {
      r.renderLights(
        gbuffer,
        area,
        Optional.empty(),
        pro_root,
        tc,
        shadows,
        x,
        s);
      return Unit.unit();
    });

    Assert.assertFalse(g_fb.framebufferReadAnyIsBound());
    Assert.assertTrue(g_fb.framebufferDrawIsBound(
      lbuffer.primaryFramebuffer()));
  }
}
