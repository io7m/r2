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

package com.io7m.r2.tests.rendering.depth.api;

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizesL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices3x3D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.r2.core.api.ids.R2IDPool;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.instances.R2InstanceSingle;
import com.io7m.r2.instances.R2InstanceSingleType;
import com.io7m.r2.matrices.R2Matrices;
import com.io7m.r2.matrices.R2MatricesType;
import com.io7m.r2.meshes.defaults.R2UnitQuad;
import com.io7m.r2.projections.R2ProjectionOrthographic;
import com.io7m.r2.rendering.depth.R2DepthInstances;
import com.io7m.r2.rendering.depth.R2DepthOnlyBuffer;
import com.io7m.r2.rendering.depth.api.R2DepthInstancesType;
import com.io7m.r2.rendering.depth.api.R2DepthOnlyBufferDescription;
import com.io7m.r2.rendering.depth.api.R2DepthOnlyBufferType;
import com.io7m.r2.rendering.depth.api.R2DepthRendererType;
import com.io7m.r2.rendering.depth.api.R2MaterialDepthSingle;
import com.io7m.r2.rendering.depth.api.R2MaterialDepthSingleType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.shaders.depth.R2DepthShaderBasicParameters;
import com.io7m.r2.shaders.depth.R2DepthShaderBasicSingle;
import com.io7m.r2.shaders.depth.api.R2ShaderDepthSingleType;
import com.io7m.r2.tests.R2JCGLContract;
import com.io7m.r2.tests.ShaderPreprocessing;
import com.io7m.r2.textures.R2TextureDefaults;
import com.io7m.r2.textures.R2TextureDefaultsType;
import com.io7m.r2.transforms.R2TransformIdentity;
import com.io7m.r2.unit_quads.R2UnitQuadType;
import org.junit.Ignore;
import org.junit.Test;

import static com.io7m.jfunctional.Unit.unit;

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

    final AreaSizeL area = AreaSizeL.of(640L, 480L);

    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g.textures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();

    final R2TextureDefaultsType td =
      R2TextureDefaults.create(g.textures(), tc);

    final R2DepthOnlyBufferDescription dbd =
      R2DepthOnlyBufferDescription.of(area);
    final R2DepthOnlyBufferType db =
      R2DepthOnlyBuffer.create(
        g.framebuffers(),
        g.textures(),
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
        R2TransformIdentity.get(),
        PMatrices3x3D.identity());

    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();

    final R2ShaderDepthSingleType<R2DepthShaderBasicParameters> ds =
      R2DepthShaderBasicSingle.create(g.shaders(), sources, id_pool);

    final R2DepthShaderBasicParameters ds_param =
      R2DepthShaderBasicParameters.of(td, td.white2D(), 0.0f);

    final R2MaterialDepthSingleType<R2DepthShaderBasicParameters> mat =
      R2MaterialDepthSingle.of(id_pool.freshID(), ds, ds_param);

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.create();

    final R2DepthInstancesType di =
      R2DepthInstances.create();
    di.depthsAddSingleInstance(i, mat);

    final R2MatricesType m = R2Matrices.create();
    m.withObserver(
      PMatrices4x4D.identity(),
      proj,
      unit(),
      (x, y) -> {
        r.renderDepthWithBoundBuffer(AreaSizesL.area(area), tc, x, di);
        return unit();
      });
  }
}
