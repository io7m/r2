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
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jfunctional.Unit;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.jtensors.parameterized.PMatrixI4x4F;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2DepthInstances;
import com.io7m.r2.core.R2DepthInstancesType;
import com.io7m.r2.core.R2DepthOnlyBuffer;
import com.io7m.r2.core.R2DepthOnlyBufferDescription;
import com.io7m.r2.core.R2DepthOnlyBufferDescriptionType;
import com.io7m.r2.core.R2DepthOnlyBufferType;
import com.io7m.r2.core.R2DepthRendererType;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2InstanceSingle;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2MaterialDepthSingle;
import com.io7m.r2.core.R2MaterialDepthSingleType;
import com.io7m.r2.core.R2Matrices;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionOrthographic;
import com.io7m.r2.core.R2TextureDefaults;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2TransformIdentity;
import com.io7m.r2.core.R2UnitQuad;
import com.io7m.r2.core.R2UnitQuadType;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicParametersMutable;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicParametersType;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicSingle;
import com.io7m.r2.core.shaders.types.R2ShaderDepthSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import org.junit.Ignore;
import org.junit.Test;

public abstract class R2DepthRendererContract extends R2JCGLContract
{
  protected abstract R2DepthRendererType getRenderer(
    final JCGLInterfaceGL33Type g);

  @Test
  @Ignore("Broken on Mesa 11.1.2, Intel(R) Sandybridge Mobile (0x116)")
  public final void testTrivial()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

    final R2DepthRendererType r = this.getRenderer(g);

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

    final R2DepthOnlyBufferDescriptionType dbd =
      R2DepthOnlyBufferDescription.of(area);
    final R2DepthOnlyBufferType db = R2DepthOnlyBuffer
      .newDepthOnlyBuffer(
        g.getFramebuffers(),
        g.getTextures(),
        tc,
        dbd);

    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);
    final R2IDPoolType id_pool =
      R2IDPool.newPool();

    final R2InstanceSingleType i =
      R2InstanceSingle.of(
        id_pool.freshID(),
        quad.arrayObject(),
        R2TransformIdentity.getInstance(),
        PMatrixI3x3F.identity());

    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();

    final R2ShaderDepthSingleType<R2DepthShaderBasicParametersType> ds =
      R2DepthShaderBasicSingle.newShader(
        g.getShaders(),
        sources,
        id_pool);

    final R2DepthShaderBasicParametersMutable ds_param =
      R2DepthShaderBasicParametersMutable.create();
    ds_param.setAlbedoTexture(td.texture2DWhite());

    final R2MaterialDepthSingleType<R2DepthShaderBasicParametersType> mat =
      R2MaterialDepthSingle.newMaterial(id_pool, ds, ds_param);

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    final R2DepthInstancesType di =
      R2DepthInstances.newDepthInstances();
    di.depthsAddSingleInstance(i, mat);

    final R2MatricesType m = R2Matrices.newMatrices();
    m.withObserver(PMatrixI4x4F.identity(), proj, Unit.unit(), (x, y) -> {
      r.renderDepthWithBoundBuffer(area, tc, x, di);
      return Unit.unit();
    });
  }
}
