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
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jregions.core.unparameterized.areas.AreasL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices3x3D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionOrthographic;
import com.io7m.r2.core.R2SceneStencils;
import com.io7m.r2.core.R2SceneStencilsType;
import com.io7m.r2.core.R2StencilRendererType;
import com.io7m.r2.core.R2UnitQuadType;
import com.io7m.r2.core.R2UnitQuadUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import org.junit.Test;

import static com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator.newAllocatorWithStack;
import static com.io7m.jfunctional.Unit.unit;
import static com.io7m.r2.core.R2IDPool.newPool;
import static com.io7m.r2.core.R2InstanceSingle.of;
import static com.io7m.r2.core.R2Matrices.create;
import static com.io7m.r2.core.R2TransformIdentity.get;
import static com.io7m.r2.core.R2UnitQuad.newUnitQuad;
import static com.io7m.r2.tests.core.R2FakeProfilingContext.newFake;
import static com.io7m.r2.tests.core.ShaderPreprocessing.preprocessor;

public abstract class R2StencilRendererContract extends R2JCGLContract
{
  protected abstract R2StencilRendererType getRenderer(
    final JCGLInterfaceGL33Type g,
    R2ShaderPreprocessingEnvironmentType in_sources,
    R2IDPoolType in_pool,
    R2UnitQuadUsableType in_quad);

  /**
   * Just check that execution proceeds without errors.
   */

  @Test
  public final void testTrivial()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

    final R2UnitQuadType quad =
      newUnitQuad(g);
    final R2IDPoolType id_pool =
      newPool();
    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();

    final R2StencilRendererType r =
      this.getRenderer(g, sources, id_pool, quad);

    final AreaL area = AreasL.create(0, 0, 640L, 480L);

    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        8, g.textures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();

    final R2InstanceSingleType i =
      of(
        id_pool.freshID(),
        quad.arrayObject(),
        get(),
        PMatrices3x3D.identity());

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.create();

    final R2SceneStencilsType ii = R2SceneStencils.create();
    ii.stencilsAddSingle(i);

    final R2MatricesType m = create();
    m.withObserver(
      PMatrices4x4D.identity(),
      proj,
      unit(),
      (x, y) -> {
        r.renderStencilsWithBoundBuffer(x, newFake(), tc, area, ii);
        return unit();
      });
  }
}
