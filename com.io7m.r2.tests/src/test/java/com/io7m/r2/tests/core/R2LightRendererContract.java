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

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizesL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.core.R2GeometryBuffer;
import com.io7m.r2.core.R2GeometryBufferType;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightBufferDescription;
import com.io7m.r2.core.R2LightBufferType;
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
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static com.io7m.jcanephora.profiler.JCGLProfiling.newProfiling;
import static com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator.newAllocatorWithStack;
import static com.io7m.jfunctional.Unit.unit;
import static com.io7m.r2.core.R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL;
import static com.io7m.r2.core.R2GeometryBufferDescription.of;
import static com.io7m.r2.core.R2IDPool.newPool;
import static com.io7m.r2.core.R2LightBufferComponents.R2_LIGHT_BUFFER_DIFFUSE_AND_SPECULAR;
import static com.io7m.r2.core.R2LightBuffers.newLightBuffer;
import static com.io7m.r2.core.R2TextureDefaults.create;
import static com.io7m.r2.core.R2UnitQuad.newUnitQuad;
import static com.io7m.r2.meshes.defaults.R2UnitSphere.newUnitSphere8;
import static com.io7m.r2.tests.core.ShaderPreprocessing.preprocessor;
import static java.util.Optional.empty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
      R2LightShaderSphericalLambertBlinnPhongSingle.create(
        g.shaders(), sources, id_pool);

    final R2SceneLightsType s = R2SceneLights.create();
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
        8, g.textures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.create(g.textures(), tc);

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
      g.framebuffers();

    final AreaSizeL area = AreaSizeL.of(640L, 480L);

    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        8, g.textures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      create(g.textures(), tc);

    final R2UnitSphereType sphere =
      newUnitSphere8(g);
    final R2UnitQuadType quad =
      newUnitQuad(g);
    final R2IDPoolType id_pool =
      newPool();

    final JCGLProfilingType pro =
      newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final R2SceneLightsType s =
      newScene(g, sphere, id_pool);

    final R2ShadowMapContextUsableType shadows =
      ls -> {
        throw new UnreachableCodeException();
      };

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.create();

    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2LightRendererType r =
      this.getRenderer(g, td, sources, id_pool, quad);

    final R2GeometryBufferType gbuffer =
      R2GeometryBuffer.create(
        g_fb, g.textures(), tc,
        of(
          area,
          R2_GEOMETRY_BUFFER_FULL));

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    final R2MatricesType m = R2Matrices.create();
    m.withObserver(
      PMatrices4x4D.identity(),
      proj,
      unit(),
      (x, y) -> {
        r.renderLightsToLightBuffer(
          gbuffer,
          AreaSizesL.area(area),
          empty(),
          pro_root,
          tc,
          shadows,
          x,
          s);
        return unit();
      });

    assertFalse(g_fb.framebufferReadAnyIsBound());
    assertFalse(g_fb.framebufferDrawAnyIsBound());
  }

  @Test
  public final void testFramebufferBindingDefaultProvided()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

    final JCGLFramebuffersType g_fb =
      g.framebuffers();

    final AreaSizeL area = AreaSizeL.of(640L, 480L);

    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        8, g.textures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      create(g.textures(), tc);

    final R2UnitSphereType sphere =
      newUnitSphere8(g);
    final R2UnitQuadType quad =
      newUnitQuad(g);
    final R2IDPoolType id_pool =
      newPool();

    final JCGLProfilingType pro =
      newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final R2SceneLightsType s =
      newScene(g, sphere, id_pool);

    final R2ShadowMapContextUsableType shadows =
      ls -> {
        throw new UnreachableCodeException();
      };

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.create();

    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2LightRendererType r =
      this.getRenderer(g, td, sources, id_pool, quad);

    final R2GeometryBufferType gbuffer =
      R2GeometryBuffer.create(
        g_fb, g.textures(), tc,
        of(area, R2_GEOMETRY_BUFFER_FULL));

    final R2LightBufferType lbuffer = newLightBuffer(
      g_fb,
      g.textures(),
      tc,
      R2LightBufferDescription.of(area, R2_LIGHT_BUFFER_DIFFUSE_AND_SPECULAR));

    g_fb.framebufferReadUnbind();
    g_fb.framebufferDrawUnbind();

    final R2MatricesType m = R2Matrices.create();
    m.withObserver(
      PMatrices4x4D.identity(),
      proj,
      unit(),
      (x, y) -> {
        r.renderLightsToLightBuffer(
          gbuffer,
          AreaSizesL.area(area),
          Optional.of(lbuffer),
          pro_root,
          tc,
          shadows,
          x,
          s);
        return unit();
      });

    assertFalse(g_fb.framebufferReadAnyIsBound());
    assertTrue(g_fb.framebufferDrawIsBound(
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
      g.framebuffers();

    final AreaSizeL area = AreaSizeL.of(640L, 480L);
    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        8, g.textures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      create(g.textures(), tc);

    final R2UnitSphereType sphere =
      newUnitSphere8(g);
    final R2UnitQuadType quad =
      newUnitQuad(g);
    final R2IDPoolType id_pool =
      newPool();

    final JCGLProfilingType pro =
      newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final R2SceneLightsType s =
      newScene(g, sphere, id_pool);

    final R2ShadowMapContextUsableType shadows =
      ls -> {
        throw new UnreachableCodeException();
      };

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.create();

    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2LightRendererType r =
      this.getRenderer(g, td, sources, id_pool, quad);

    final R2GeometryBufferType gbuffer =
      R2GeometryBuffer.create(
        g_fb, g.textures(), tc,
        of(
          area,
          R2_GEOMETRY_BUFFER_FULL));

    final R2LightBufferType lbuffer = newLightBuffer(
      g_fb,
      g.textures(),
      tc,
      R2LightBufferDescription.of(
        area,
        R2_LIGHT_BUFFER_DIFFUSE_AND_SPECULAR));

    g_fb.framebufferReadUnbind();
    assertTrue(g_fb.framebufferDrawIsBound(
      lbuffer.primaryFramebuffer()));

    final R2MatricesType m = R2Matrices.create();
    m.withObserver(
      PMatrices4x4D.identity(),
      proj,
      unit(),
      (x, y) -> {
        r.renderLightsToLightBuffer(
          gbuffer,
          AreaSizesL.area(area),
          empty(),
          pro_root,
          tc,
          shadows,
          x,
          s);
        return unit();
      });

    assertFalse(g_fb.framebufferReadAnyIsBound());
    assertTrue(g_fb.framebufferDrawIsBound(
      lbuffer.primaryFramebuffer()));
  }
}
