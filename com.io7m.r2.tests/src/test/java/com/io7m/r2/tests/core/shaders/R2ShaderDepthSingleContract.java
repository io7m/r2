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

package com.io7m.r2.tests.core.shaders;

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfsm.core.FSMTransitionException;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jregions.core.unparameterized.areas.AreasL;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2ProjectionReadableType;
import com.io7m.r2.core.shaders.types.R2ShaderDepthSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersMaterial;
import com.io7m.r2.core.shaders.types.R2ShaderParametersView;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.tests.core.R2EmptyInstanceTransformValues;
import com.io7m.r2.tests.core.R2EmptyObserverValues;
import com.io7m.r2.tests.core.R2JCGLContract;
import com.io7m.r2.tests.core.ShaderPreprocessing;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator.newAllocatorWithStack;
import static com.io7m.r2.core.R2IDPool.newPool;
import static com.io7m.r2.core.R2ProjectionOrthographic.create;
import static com.io7m.r2.core.shaders.types.R2ShaderParametersView.of;
import static com.io7m.r2.tests.core.ShaderPreprocessing.preprocessor;

public abstract class R2ShaderDepthSingleContract<T, TM extends T> extends
  R2JCGLContract
{
  @Rule public ExpectedException expected = ExpectedException.none();

  protected abstract R2ShaderDepthSingleType<T> newShaderWithVerifier(
    JCGLInterfaceGL33Type g,
    R2ShaderPreprocessingEnvironmentType sources,
    R2IDPoolType pool);

  protected abstract TM newParameters(
    JCGLInterfaceGL33Type g);

  @Test
  public final void testCorrect()
    throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2IDPoolType pool =
      newPool();

    final R2ShaderDepthSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLTexturesType g_tex = g.textures();
    final JCGLShadersType g_sh = g.shaders();

    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        32,
        g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tr =
      ta.rootContext();
    final JCGLTextureUnitContextType tc =
      tr.unitContextNew();

    final R2ProjectionReadableType proj =
      create();

    final AreaL area = AreasL.create(0, 0, 640L, 480L);

    final R2ShaderParametersView vp =
      of(new R2EmptyObserverValues(proj), area);
    final R2ShaderParametersMaterial<T> mp =
      R2ShaderParametersMaterial.of(tc, t);

    f.onActivate(g);
    f.onReceiveViewValues(g, vp);
    f.onReceiveMaterialValues(g, mp);
    f.onReceiveInstanceTransformValues(g, new R2EmptyInstanceTransformValues());
    f.onValidate();
    f.onDeactivate(g);
  }

  @Test
  public final void testCorrectMultiInstance()
    throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2IDPoolType pool =
      newPool();

    final R2ShaderDepthSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLTexturesType g_tex = g.textures();
    final JCGLShadersType g_sh = g.shaders();
    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        32,
        g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tr = ta.rootContext();
    final JCGLTextureUnitContextType tc = tr.unitContextNew();

    final R2ProjectionReadableType proj =
      create();

    final AreaL area = AreasL.create(0, 0, 640L, 480L);

    final R2ShaderParametersView vp =
      of(new R2EmptyObserverValues(proj), area);
    final R2ShaderParametersMaterial<T> mp =
      R2ShaderParametersMaterial.of(tc, t);

    f.onActivate(g);
    f.onReceiveViewValues(g, vp);

    f.onReceiveMaterialValues(g, mp);
    f.onReceiveInstanceTransformValues(g, new R2EmptyInstanceTransformValues());
    f.onValidate();

    f.onReceiveMaterialValues(g, mp);
    f.onReceiveInstanceTransformValues(g, new R2EmptyInstanceTransformValues());
    f.onValidate();

    f.onReceiveMaterialValues(g, mp);
    f.onReceiveInstanceTransformValues(g, new R2EmptyInstanceTransformValues());
    f.onValidate();

    f.onDeactivate(g);
  }

  @Test
  public final void testWrongOrder()
    throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2IDPoolType pool =
      newPool();

    final R2ShaderDepthSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLTexturesType g_tex = g.textures();
    final JCGLShadersType g_sh = g.shaders();
    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        32,
        g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tr = ta.rootContext();
    final JCGLTextureUnitContextType tc = tr.unitContextNew();

    final R2ProjectionReadableType proj =
      create();

    final AreaL area = AreasL.create(0, 0, 640L, 480L);

    final R2ShaderParametersView vp =
      of(new R2EmptyObserverValues(proj), area);
    final R2ShaderParametersMaterial<T> mp =
      R2ShaderParametersMaterial.of(tc, t);

    f.onActivate(g);
    f.onReceiveViewValues(g, vp);
    f.onReceiveMaterialValues(g, mp);
    f.onReceiveInstanceTransformValues(g, new R2EmptyInstanceTransformValues());

    this.expected.expect(FSMTransitionException.class);
    f.onReceiveViewValues(g, vp);
  }

  @Test
  public final void testInactive()
    throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2IDPoolType pool =
      newPool();

    final R2ShaderDepthSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);

    final R2ProjectionReadableType proj =
      create();

    final AreaL area = AreasL.create(0, 0, 640L, 480L);

    final R2ShaderParametersView vp =
      of(new R2EmptyObserverValues(proj), area);

    this.expected.expect(FSMTransitionException.class);
    f.onReceiveViewValues(g, vp);
  }

  @Test
  public final void testNoReceiveMaterial()
    throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2IDPoolType pool =
      newPool();

    final R2ShaderDepthSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);

    final R2ProjectionReadableType proj =
      create();

    final AreaL area = AreasL.create(0, 0, 640L, 480L);

    final R2ShaderParametersView vp =
      of(new R2EmptyObserverValues(proj), area);

    final JCGLShadersType g_sh = g.shaders();

    f.onActivate(g);
    f.onReceiveViewValues(g, vp);
    this.expected.expect(FSMTransitionException.class);
    f.onReceiveInstanceTransformValues(g, new R2EmptyInstanceTransformValues());
  }

  @Test
  public final void testNoReceiveInstance()
    throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2IDPoolType pool =
      newPool();

    final R2ShaderDepthSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLTexturesType g_tex = g.textures();
    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        32,
        g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tr = ta.rootContext();
    final JCGLTextureUnitContextType tc = tr.unitContextNew();

    final R2ProjectionReadableType proj =
      create();

    final AreaL area = AreasL.create(0, 0, 640L, 480L);

    final R2ShaderParametersView vp =
      of(new R2EmptyObserverValues(proj), area);
    final R2ShaderParametersMaterial<T> mp =
      R2ShaderParametersMaterial.of(tc, t);

    f.onActivate(g);
    f.onReceiveViewValues(g, vp);
    f.onReceiveMaterialValues(g, mp);
    this.expected.expect(FSMTransitionException.class);
    f.onValidate();
  }

  @Test
  public final void testNoReceiveView()
    throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final R2ShaderDepthSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLTexturesType g_tex = g.textures();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        32,
        g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tr = ta.rootContext();
    final JCGLTextureUnitContextType tc = tr.unitContextNew();

    final R2ShaderParametersMaterial<T> mp =
      R2ShaderParametersMaterial.of(tc, t);

    f.onActivate(g);
    this.expected.expect(FSMTransitionException.class);
    f.onReceiveMaterialValues(g, mp);
  }

  @Test
  public final void testDeactivatedValidate()
    throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final R2ShaderDepthSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);

    f.onActivate(g);
    f.onDeactivate(g);

    this.expected.expect(FSMTransitionException.class);
    f.onValidate();
  }
}
