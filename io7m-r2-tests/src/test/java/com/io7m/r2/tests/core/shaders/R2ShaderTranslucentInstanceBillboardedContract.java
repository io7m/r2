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

import com.io7m.jareas.core.AreaInclusiveUnsignedL;
import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.JCGLProjectionMatrices;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfsm.core.FSMTransitionException;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2ProjectionOrthographic;
import com.io7m.r2.core.R2ProjectionReadableType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.core.shaders.types.R2ShaderTranslucentInstanceBillboardedType;
import com.io7m.r2.tests.core.R2EmptyObserverValues;
import com.io7m.r2.tests.core.R2JCGLContract;
import com.io7m.r2.tests.core.ShaderPreprocessing;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public abstract class R2ShaderTranslucentInstanceBillboardedContract<T, TM extends T> extends
  R2JCGLContract
{
  @Rule public ExpectedException expected = ExpectedException.none();

  protected abstract R2ShaderTranslucentInstanceBillboardedType<T> newShaderWithVerifier(
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
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final R2ShaderTranslucentInstanceBillboardedType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLTexturesType g_tex = g.getTextures();
    final JCGLShadersType g_sh = g.getShaders();

    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        32,
        g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tr =
      ta.getRootContext();
    final JCGLTextureUnitContextType tc =
      tr.unitContextNew();

    final R2ProjectionReadableType proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    final AreaInclusiveUnsignedLType area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 639L),
      new UnsignedRangeInclusiveL(0L, 479L));

    f.onActivate(g.getShaders());
    f.onReceiveViewValues(g_sh, new R2EmptyObserverValues(proj), area);
    f.onReceiveMaterialValues(g_tex, g_sh, tc, t);
    f.onValidate();
    f.onDeactivate(g_sh);
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
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final R2ShaderTranslucentInstanceBillboardedType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLTexturesType g_tex = g.getTextures();
    final JCGLShadersType g_sh = g.getShaders();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        32,
        g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tr = ta.getRootContext();
    final JCGLTextureUnitContextType tc = tr.unitContextNew();

    final AreaInclusiveUnsignedLType area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 639L),
      new UnsignedRangeInclusiveL(0L, 479L));

    final R2ProjectionReadableType proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    f.onActivate(g.getShaders());
    f.onReceiveViewValues(g_sh, new R2EmptyObserverValues(proj), area);

    f.onReceiveMaterialValues(g_tex, g_sh, tc, t);
    f.onValidate();

    f.onReceiveMaterialValues(g_tex, g_sh, tc, t);
    f.onValidate();

    f.onReceiveMaterialValues(g_tex, g_sh, tc, t);
    f.onValidate();

    f.onDeactivate(g_sh);
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
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final R2ShaderTranslucentInstanceBillboardedType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLTexturesType g_tex = g.getTextures();
    final JCGLShadersType g_sh = g.getShaders();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        32,
        g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tr = ta.getRootContext();
    final JCGLTextureUnitContextType tc = tr.unitContextNew();

    final AreaInclusiveUnsignedLType area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 639L),
      new UnsignedRangeInclusiveL(0L, 479L));

    final R2ProjectionReadableType proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    f.onActivate(g.getShaders());
    f.onReceiveViewValues(g_sh, new R2EmptyObserverValues(proj), area);
    f.onReceiveMaterialValues(g_tex, g_sh, tc, t);

    this.expected.expect(FSMTransitionException.class);
    f.onReceiveViewValues(g_sh, new R2EmptyObserverValues(proj), area);
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
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final R2ShaderTranslucentInstanceBillboardedType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLShadersType g_sh = g.getShaders();

    final AreaInclusiveUnsignedLType area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 639L),
      new UnsignedRangeInclusiveL(0L, 479L));

    final R2ProjectionReadableType proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    this.expected.expect(FSMTransitionException.class);
    f.onReceiveViewValues(g_sh, new R2EmptyObserverValues(proj), area);
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
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final R2ShaderTranslucentInstanceBillboardedType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final R2ProjectionReadableType proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    final AreaInclusiveUnsignedLType area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 639L),
      new UnsignedRangeInclusiveL(0L, 479L));

    final JCGLShadersType g_sh = g.getShaders();

    f.onActivate(g_sh);
    f.onReceiveViewValues(g_sh, new R2EmptyObserverValues(proj), area);
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

    final R2ShaderTranslucentInstanceBillboardedType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLShadersType g_sh = g.getShaders();
    final JCGLTexturesType g_tex = g.getTextures();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        32,
        g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tr = ta.getRootContext();
    final JCGLTextureUnitContextType tc = tr.unitContextNew();

    f.onActivate(g_sh);
    this.expected.expect(FSMTransitionException.class);
    f.onReceiveMaterialValues(g_tex, g_sh, tc, t);
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

    final R2ShaderTranslucentInstanceBillboardedType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T t =
      this.newParameters(g);

    final JCGLShadersType g_sh = g.getShaders();

    f.onActivate(g_sh);
    f.onDeactivate(g_sh);

    this.expected.expect(FSMTransitionException.class);
    f.onValidate();
  }
}
