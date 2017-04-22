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

import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfsm.core.FSMTransitionException;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jregions.core.unparameterized.areas.AreasL;
import com.io7m.r2.core.R2ProjectionReadableType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedVerifier;
import com.io7m.r2.core.shaders.types.R2ShaderParametersMaterial;
import com.io7m.r2.core.shaders.types.R2ShaderParametersView;
import com.io7m.r2.tests.core.R2EmptyObserverValues;
import com.io7m.r2.tests.core.R2TestUtilities;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator.newAllocatorWithStack;
import static com.io7m.r2.core.R2ProjectionOrthographic.create;
import static com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedVerifier.newVerifier;
import static com.io7m.r2.core.shaders.types.R2ShaderParametersView.of;
import static com.io7m.r2.tests.core.R2TestUtilities.getFakeGL;
import static com.io7m.r2.tests.core.R2TestUtilities.getShaderInstanceBatched;

public final class R2ShaderInstanceBatchedVerifierTest
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test
  public void testCorrect()
    throws Exception
  {
    final JCGLInterfaceGL33Type g =
      getFakeGL();
    final R2ShaderInstanceBatchedType<Object> f =
      getShaderInstanceBatched(g, 1L);
    final R2ShaderInstanceBatchedType<Object> v =
      newVerifier(f);

    final JCGLTexturesType g_tex = g.textures();
    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        32, g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tr = ta.rootContext();
    final JCGLTextureUnitContextType tc = tr.unitContextNew();

    final R2ProjectionReadableType proj =
      create();

    final AreaL area = AreasL.create(0, 0, 640L, 480L);

    final R2ShaderParametersView vp =
      of(new R2EmptyObserverValues(proj), area);
    final R2ShaderParametersMaterial<Object> mp =
      R2ShaderParametersMaterial.of(tc, new Object());

    v.onActivate(g);
    v.onReceiveViewValues(g, vp);
    v.onReceiveMaterialValues(g, mp);
    v.onValidate();
    v.onDeactivate(g);
  }

  @Test
  public void testCorrectMultiInstance()
    throws Exception
  {
    final JCGLInterfaceGL33Type g =
      getFakeGL();
    final R2ShaderInstanceBatchedType<Object> f =
      getShaderInstanceBatched(g, 1L);
    final R2ShaderInstanceBatchedType<Object> v =
      newVerifier(f);

    final JCGLTexturesType g_tex = g.textures();
    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        32, g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tr = ta.rootContext();
    final JCGLTextureUnitContextType tc = tr.unitContextNew();

    final R2ProjectionReadableType proj =
      create();

    final AreaL area = AreasL.create(0, 0, 640L, 480L);

    final R2ShaderParametersView vp =
      of(new R2EmptyObserverValues(proj), area);
    final R2ShaderParametersMaterial<Object> mp =
      R2ShaderParametersMaterial.of(tc, new Object());

    v.onActivate(g);
    v.onReceiveViewValues(g, vp);

    v.onReceiveMaterialValues(g, mp);
    v.onValidate();

    v.onReceiveMaterialValues(g, mp);
    v.onValidate();

    v.onReceiveMaterialValues(g, mp);
    v.onValidate();

    v.onDeactivate(g);
  }

  @Test
  public void testWrongOrder()
    throws Exception
  {
    final JCGLInterfaceGL33Type g =
      getFakeGL();
    final R2ShaderInstanceBatchedType<Object> f =
      getShaderInstanceBatched(g, 1L);
    final R2ShaderInstanceBatchedType<Object> v =
      newVerifier(f);

    final JCGLTexturesType g_tex = g.textures();
    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        32, g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tr = ta.rootContext();
    final JCGLTextureUnitContextType tc = tr.unitContextNew();

    final R2ProjectionReadableType proj =
      create();

    final AreaL area = AreasL.create(0, 0, 640L, 480L);

    final R2ShaderParametersView vp =
      of(new R2EmptyObserverValues(proj), area);
    final R2ShaderParametersMaterial<Object> mp =
      R2ShaderParametersMaterial.of(tc, new Object());

    v.onActivate(g);
    v.onReceiveViewValues(g, vp);
    v.onReceiveMaterialValues(g, mp);

    this.expected.expect(FSMTransitionException.class);
    v.onReceiveViewValues(g, vp);
  }

  @Test
  public void testInactive()
    throws Exception
  {
    final JCGLInterfaceGL33Type g =
      getFakeGL();
    final R2ShaderInstanceBatchedType<Object> f =
      getShaderInstanceBatched(g, 1L);
    final R2ShaderInstanceBatchedType<Object> v =
      newVerifier(f);

    final R2ProjectionReadableType proj =
      create();

    final AreaL area = AreasL.create(0, 0, 640L, 480L);

    final R2ShaderParametersView vp =
      of(new R2EmptyObserverValues(proj), area);

    this.expected.expect(FSMTransitionException.class);
    v.onReceiveViewValues(g, vp);
  }

  @Test
  public void testNoReceiveMaterial()
    throws Exception
  {
    final JCGLInterfaceGL33Type g =
      getFakeGL();
    final R2ShaderInstanceBatchedType<Object> f =
      getShaderInstanceBatched(g, 1L);
    final R2ShaderInstanceBatchedType<Object> v =
      newVerifier(f);

    final R2ProjectionReadableType proj =
      create();

    final AreaL area = AreasL.create(0, 0, 640L, 480L);

    final R2ShaderParametersView vp =
      of(new R2EmptyObserverValues(proj), area);

    v.onActivate(g);
    v.onReceiveViewValues(g, vp);
    this.expected.expect(FSMTransitionException.class);
    v.onValidate();
  }

  @Test
  public void testNoReceiveView()
    throws Exception
  {
    final JCGLInterfaceGL33Type g =
      R2TestUtilities.getFakeGL();
    final R2ShaderInstanceBatchedType<Object> f =
      R2TestUtilities.getShaderInstanceBatched(g, 1L);
    final R2ShaderInstanceBatchedType<Object> v =
      R2ShaderInstanceBatchedVerifier.newVerifier(f);

    final JCGLTexturesType g_tex = g.textures();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        32, g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tr = ta.rootContext();
    final JCGLTextureUnitContextType tc = tr.unitContextNew();

    final R2ShaderParametersMaterial<Object> mp =
      R2ShaderParametersMaterial.of(tc, new Object());

    v.onActivate(g);
    this.expected.expect(FSMTransitionException.class);
    v.onReceiveMaterialValues(g, mp);
  }

  @Test
  public void testDeactivatedValidate()
    throws Exception
  {
    final JCGLInterfaceGL33Type g =
      R2TestUtilities.getFakeGL();
    final R2ShaderInstanceBatchedType<Object> f =
      R2TestUtilities.getShaderInstanceBatched(g, 1L);
    final R2ShaderInstanceBatchedType<Object> v =
      R2ShaderInstanceBatchedVerifier.newVerifier(f);

    v.onActivate(g);
    v.onDeactivate(g);
    this.expected.expect(FSMTransitionException.class);
    v.onValidate();
  }
}
