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

import com.io7m.jcanephora.core.JCGLProjectionMatrices;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2ProjectionOrthographic;
import com.io7m.r2.core.R2ProjectionReadableType;
import com.io7m.r2.core.R2TextureUnitAllocator;
import com.io7m.r2.core.R2TextureUnitAllocatorType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2TextureUnitContextType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesResources;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;
import com.io7m.r2.shaders.R2Shaders;
import com.io7m.r2.tests.core.R2EmptyInstanceTransformValues;
import com.io7m.r2.tests.core.R2EmptyObserverValues;
import com.io7m.r2.tests.core.R2JCGLContract;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public abstract class R2ShaderInstanceSingleContract<T, TM extends T> extends
  R2JCGLContract
{
  @Rule public ExpectedException expected = ExpectedException.none();

  protected abstract R2ShaderInstanceSingleType<T> newShaderWithVerifier(
    JCGLInterfaceGL33Type g,
    R2ShaderSourcesType sources,
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
    final R2ShaderSourcesType sources =
      R2ShaderSourcesResources.newSources(R2Shaders.class);
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final R2ShaderInstanceSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLTexturesType g_tex = g.getTextures();
    final JCGLShadersType g_sh = g.getShaders();

    final R2TextureUnitAllocatorType ta =
      R2TextureUnitAllocator.newAllocatorWithStack(32, g_tex.textureGetUnits());
    final R2TextureUnitContextParentType tr =
      ta.getRootContext();
    final R2TextureUnitContextType tc =
      tr.unitContextNew();

    final R2ProjectionReadableType proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    f.onActivate(g.getShaders());
    f.onReceiveViewValues(g_sh, new R2EmptyObserverValues(proj));
    f.onReceiveMaterialValues(g_tex, g_sh, tc, t);
    f.onReceiveInstanceTransformValues(
      g_sh, new R2EmptyInstanceTransformValues());
    f.onValidate();
    f.onDeactivate(g_sh);
  }

  @Test
  public final void testCorrectMultiInstance() throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderSourcesType sources =
      R2ShaderSourcesResources.newSources(R2Shaders.class);
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final R2ShaderInstanceSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLTexturesType g_tex = g.getTextures();
    final JCGLShadersType g_sh = g.getShaders();
    final R2TextureUnitAllocatorType ta =
      R2TextureUnitAllocator.newAllocatorWithStack(32, g_tex.textureGetUnits());
    final R2TextureUnitContextParentType tr = ta.getRootContext();
    final R2TextureUnitContextType tc = tr.unitContextNew();

    final R2ProjectionReadableType proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    f.onActivate(g.getShaders());
    f.onReceiveViewValues(g_sh, new R2EmptyObserverValues(proj));

    f.onReceiveMaterialValues(g_tex, g_sh, tc, t);
    f.onReceiveInstanceTransformValues(
      g_sh, new R2EmptyInstanceTransformValues());
    f.onValidate();

    f.onReceiveMaterialValues(g_tex, g_sh, tc, t);
    f.onReceiveInstanceTransformValues(
      g_sh, new R2EmptyInstanceTransformValues());
    f.onValidate();

    f.onReceiveMaterialValues(g_tex, g_sh, tc, t);
    f.onReceiveInstanceTransformValues(
      g_sh, new R2EmptyInstanceTransformValues());
    f.onValidate();

    f.onDeactivate(g_sh);
  }

  @Test
  public final void testWrongOrder() throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderSourcesType sources =
      R2ShaderSourcesResources.newSources(R2Shaders.class);
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final R2ShaderInstanceSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLTexturesType g_tex = g.getTextures();
    final JCGLShadersType g_sh = g.getShaders();
    final R2TextureUnitAllocatorType ta =
      R2TextureUnitAllocator.newAllocatorWithStack(32, g_tex.textureGetUnits());
    final R2TextureUnitContextParentType tr = ta.getRootContext();
    final R2TextureUnitContextType tc = tr.unitContextNew();

    final R2ProjectionReadableType proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    f.onActivate(g.getShaders());
    f.onReceiveViewValues(g_sh, new R2EmptyObserverValues(proj));
    f.onReceiveMaterialValues(g_tex, g_sh, tc, t);
    f.onReceiveInstanceTransformValues(
      g_sh, new R2EmptyInstanceTransformValues());

    this.expected.expect(IllegalStateException.class);
    f.onReceiveViewValues(g_sh, new R2EmptyObserverValues(proj));
  }

  @Test
  public final void testInactive() throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderSourcesType sources =
      R2ShaderSourcesResources.newSources(R2Shaders.class);
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final R2ShaderInstanceSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLShadersType g_sh = g.getShaders();

    final R2ProjectionReadableType proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    this.expected.expect(IllegalStateException.class);
    f.onReceiveViewValues(g_sh, new R2EmptyObserverValues(proj));
  }

  @Test
  public final void testNoReceiveMaterial() throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderSourcesType sources =
      R2ShaderSourcesResources.newSources(R2Shaders.class);
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final R2ShaderInstanceSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final R2ProjectionReadableType proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    final JCGLShadersType g_sh = g.getShaders();

    f.onActivate(g_sh);
    f.onReceiveViewValues(g_sh, new R2EmptyObserverValues(proj));
    this.expected.expect(IllegalStateException.class);
    f.onReceiveInstanceTransformValues(
      g_sh, new R2EmptyInstanceTransformValues());
  }

  @Test
  public final void testNoReceiveInstance() throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderSourcesType sources =
      R2ShaderSourcesResources.newSources(R2Shaders.class);
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final R2ShaderInstanceSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final R2ProjectionReadableType proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    final JCGLShadersType g_sh = g.getShaders();
    final JCGLTexturesType g_tex = g.getTextures();
    final R2TextureUnitAllocatorType ta =
      R2TextureUnitAllocator.newAllocatorWithStack(32, g_tex.textureGetUnits());
    final R2TextureUnitContextParentType tr = ta.getRootContext();
    final R2TextureUnitContextType tc = tr.unitContextNew();

    f.onActivate(g.getShaders());
    f.onReceiveViewValues(g_sh, new R2EmptyObserverValues(proj));
    f.onReceiveMaterialValues(g_tex, g_sh, tc, t);
    this.expected.expect(IllegalStateException.class);
    f.onValidate();
    f.onDeactivate(g_sh);
  }

  @Test
  public final void testNoReceiveView() throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderSourcesType sources =
      R2ShaderSourcesResources.newSources(R2Shaders.class);
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final R2ShaderInstanceSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLShadersType g_sh = g.getShaders();
    final JCGLTexturesType g_tex = g.getTextures();
    final R2TextureUnitAllocatorType ta =
      R2TextureUnitAllocator.newAllocatorWithStack(32, g_tex.textureGetUnits());
    final R2TextureUnitContextParentType tr = ta.getRootContext();
    final R2TextureUnitContextType tc = tr.unitContextNew();

    f.onActivate(g_sh);
    this.expected.expect(IllegalStateException.class);
    f.onReceiveMaterialValues(g_tex, g_sh, tc, t);
  }

  @Test
  public final void testDeactivatedValidate() throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderSourcesType sources =
      R2ShaderSourcesResources.newSources(R2Shaders.class);
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final R2ShaderInstanceSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLShadersType g_sh = g.getShaders();

    f.onActivate(g_sh);
    f.onDeactivate(g_sh);

    this.expected.expect(IllegalStateException.class);
    f.onValidate();
  }
}
