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

import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices3x3D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.r2.core.R2DepthInstances;
import com.io7m.r2.core.R2DepthInstancesType;
import com.io7m.r2.core.R2DepthPrecision;
import com.io7m.r2.core.R2DepthVarianceBufferDescription;
import com.io7m.r2.core.R2DepthVarianceBufferPool;
import com.io7m.r2.core.R2DepthVarianceBufferUsableType;
import com.io7m.r2.core.R2DepthVariancePrecision;
import com.io7m.r2.core.R2DepthVarianceRendererType;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2InstanceSingle;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2LightProjectiveWithShadowVariance;
import com.io7m.r2.core.R2LightProjectiveWithShadowVarianceType;
import com.io7m.r2.core.R2MaterialDepthSingle;
import com.io7m.r2.core.R2MaterialDepthSingleType;
import com.io7m.r2.core.R2Matrices;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionFOV;
import com.io7m.r2.core.R2ProjectionMesh;
import com.io7m.r2.core.R2ProjectionMeshType;
import com.io7m.r2.core.R2RenderTargetPoolType;
import com.io7m.r2.core.R2RenderTargetPoolUsableType;
import com.io7m.r2.core.R2RendererExceptionShadowExecutionAlreadyActive;
import com.io7m.r2.core.R2RendererExceptionShadowExecutionNotActive;
import com.io7m.r2.core.R2RendererExceptionShadowMapContextAlreadyActive;
import com.io7m.r2.core.R2RendererExceptionShadowNotRendered;
import com.io7m.r2.core.R2ShadowDepthVariance;
import com.io7m.r2.core.R2ShadowMapContextType;
import com.io7m.r2.core.R2ShadowMapContextUsableType;
import com.io7m.r2.core.R2ShadowMapRendererExecutionType;
import com.io7m.r2.core.R2ShadowMapRendererType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureDefaults;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2TransformIdentity;
import com.io7m.r2.core.R2TransformOTType;
import com.io7m.r2.core.R2UnitQuad;
import com.io7m.r2.core.R2UnitQuadType;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicParameters;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicSingle;
import com.io7m.r2.core.shaders.types.R2ShaderDepthSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public abstract class R2ShadowMapRendererContract extends R2JCGLContract
{
  @Rule public ExpectedException expected = ExpectedException.none();

  protected abstract R2DepthVarianceRendererType newVarianceRenderer(
    final JCGLInterfaceGL33Type g);

  protected abstract R2ShadowMapRendererType newShadowMapRenderer(
    final JCGLInterfaceGL33Type g,
    final R2DepthVarianceRendererType dvr,
    final R2RenderTargetPoolUsableType<R2DepthVarianceBufferDescription,
      R2DepthVarianceBufferUsableType> vp);

  @Test
  public final void testUseReturn()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

    final R2DepthVarianceRendererType vr =
      this.newVarianceRenderer(g);

    final R2RenderTargetPoolType<
      R2DepthVarianceBufferDescription,
      R2DepthVarianceBufferUsableType> vp =
      R2DepthVarianceBufferPool.newPool(g, 0L, Long.MAX_VALUE);

    final R2ShadowMapRendererType r =
      this.newShadowMapRenderer(g, vr, vp);

    final R2ShadowMapRendererExecutionType rc = r.shadowBegin();
    final R2ShadowMapContextType mc = rc.shadowExecComplete();
    mc.shadowMapContextFinish();
  }

  @Test
  public final void testBeginExecutionTwice()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

    final R2DepthVarianceRendererType vr =
      this.newVarianceRenderer(g);

    final R2RenderTargetPoolType<
      R2DepthVarianceBufferDescription,
      R2DepthVarianceBufferUsableType> vp =
      R2DepthVarianceBufferPool.newPool(g, 0L, Long.MAX_VALUE);

    final R2ShadowMapRendererType r =
      this.newShadowMapRenderer(g, vr, vp);

    final R2ShadowMapRendererExecutionType rc =
      r.shadowBegin();

    this.expected.expect(R2RendererExceptionShadowExecutionAlreadyActive.class);
    r.shadowBegin();
  }

  @Test
  public final void testCompleteExecutionTwice()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

    final R2DepthVarianceRendererType vr =
      this.newVarianceRenderer(g);

    final R2RenderTargetPoolType<
      R2DepthVarianceBufferDescription,
      R2DepthVarianceBufferUsableType> vp =
      R2DepthVarianceBufferPool.newPool(g, 0L, Long.MAX_VALUE);

    final R2ShadowMapRendererType r =
      this.newShadowMapRenderer(g, vr, vp);

    final R2ShadowMapRendererExecutionType rc =
      r.shadowBegin();
    final R2ShadowMapContextUsableType cc =
      rc.shadowExecComplete();

    this.expected.expect(
      R2RendererExceptionShadowMapContextAlreadyActive.class);
    rc.shadowExecComplete();
  }

  @Test
  public final void testCompleteExecutionInactive()
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();

    final R2DepthVarianceRendererType vr =
      this.newVarianceRenderer(g);

    final R2RenderTargetPoolType<
      R2DepthVarianceBufferDescription,
      R2DepthVarianceBufferUsableType> vp =
      R2DepthVarianceBufferPool.newPool(g, 0L, Long.MAX_VALUE);

    final R2ShadowMapRendererType r =
      this.newShadowMapRenderer(g, vr, vp);

    final R2ShadowMapRendererExecutionType rc =
      r.shadowBegin();
    final R2ShadowMapContextType cc =
      rc.shadowExecComplete();
    cc.shadowMapContextFinish();

    this.expected.expect(R2RendererExceptionShadowExecutionNotActive.class);
    rc.shadowExecComplete();
  }


  @Test
  public final void testRenderOne()
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
    final R2IDPoolType id_pool =
      R2IDPool.newPool();
    final R2DepthShaderBasicParameters ds_param =
      R2DepthShaderBasicParameters.of(td, td.white2D(), 0.0f);

    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2ShaderDepthSingleType<R2DepthShaderBasicParameters> ds =
      R2DepthShaderBasicSingle.newShader(g.shaders(), sources, id_pool);
    final R2MaterialDepthSingleType<R2DepthShaderBasicParameters> mat =
      R2MaterialDepthSingle.of(id_pool.freshID(), ds, ds_param);

    final R2InstanceSingleType i =
      R2InstanceSingle.of(
        id_pool.freshID(),
        quad.arrayObject(),
        R2TransformIdentity.get(),
        PMatrices3x3D.identity());

    final R2DepthInstancesType di =
      R2DepthInstances.create();
    di.depthsAddSingleInstance(i, mat);

    final R2DepthVarianceRendererType vr =
      this.newVarianceRenderer(g);

    final R2RenderTargetPoolType<
      R2DepthVarianceBufferDescription,
      R2DepthVarianceBufferUsableType> vp =
      R2DepthVarianceBufferPool.newPool(g, 0L, Long.MAX_VALUE);

    final R2ShadowMapRendererType r =
      this.newShadowMapRenderer(g, vr, vp);

    final R2ShadowMapRendererExecutionType rc =
      r.shadowBegin();

    final R2MatricesType m = R2Matrices.create();

    final R2ProjectionFOV proj =
      R2ProjectionFOV.createWith(
        (float) Math.toRadians(90.0f), 1.0f, 0.001f, 1000.0f);

    final R2ProjectionMeshType mesh =
      R2ProjectionMesh.create(
        g,
        proj,
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUsageHint.USAGE_STATIC_DRAW);
    final R2Texture2DUsableType image =
      td.whiteProjective2D();

    final R2DepthVarianceBufferDescription.Builder db =
      R2DepthVarianceBufferDescription.builder();
    db.setDepthPrecision(
      R2DepthPrecision.R2_DEPTH_PRECISION_24);
    db.setDepthVariancePrecision(
      R2DepthVariancePrecision.R2_DEPTH_VARIANCE_PRECISION_16);
    db.setMagnificationFilter(
      JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);
    db.setMinificationFilter(
      JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR_MIPMAP_LINEAR);
    db.setArea(AreaSizeL.of(512L, 512L));
    final R2DepthVarianceBufferDescription desc = db.build();

    final R2ShadowDepthVariance shadow =
      R2ShadowDepthVariance.of(id_pool.freshID(), desc);

    final R2LightProjectiveWithShadowVarianceType ls =
      R2LightProjectiveWithShadowVariance.create(
        mesh, image, shadow, id_pool);

    final R2TransformOTType tr = ls.transformWritable();
    tr.setTranslation(PVector3D.of(0.0, 0.0, 10.0));

    rc.shadowExecRenderLight(R2FakeProfilingContext.newFake(), tc, m, ls, di);

    final R2ShadowMapContextType mc =
      rc.shadowExecComplete();

    final R2Texture2DUsableType rt_map = mc.shadowMapGet(ls);
    final JCGLTexture2DUsableType map = rt_map.texture();
    Assert.assertEquals(
      map.size(), desc.area());
    Assert.assertEquals(
      map.wrapS(), JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE);
    Assert.assertEquals(
      map.wrapT(), JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE);

    mc.shadowMapContextFinish();

    Assert.assertTrue(map.isDeleted());
  }

  @Test
  public final void testNonexistentShadow()
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
    final R2IDPoolType id_pool =
      R2IDPool.newPool();
    final R2DepthShaderBasicParameters ds_param =
      R2DepthShaderBasicParameters.of(td, td.white2D(), 0.0f);

    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2ShaderDepthSingleType<R2DepthShaderBasicParameters> ds =
      R2DepthShaderBasicSingle.newShader(g.shaders(), sources, id_pool);
    final R2MaterialDepthSingleType<R2DepthShaderBasicParameters> mat =
      R2MaterialDepthSingle.of(id_pool.freshID(), ds, ds_param);

    final R2InstanceSingleType i =
      R2InstanceSingle.of(
        id_pool.freshID(),
        quad.arrayObject(),
        R2TransformIdentity.get(),
        PMatrices3x3D.identity());

    final R2DepthInstancesType di =
      R2DepthInstances.create();
    di.depthsAddSingleInstance(i, mat);

    final R2DepthVarianceRendererType vr =
      this.newVarianceRenderer(g);

    final R2RenderTargetPoolType<
      R2DepthVarianceBufferDescription,
      R2DepthVarianceBufferUsableType> vp =
      R2DepthVarianceBufferPool.newPool(g, 0L, Long.MAX_VALUE);

    final R2ShadowMapRendererType r =
      this.newShadowMapRenderer(g, vr, vp);

    final R2ShadowMapRendererExecutionType rc =
      r.shadowBegin();

    final R2ProjectionFOV proj =
      R2ProjectionFOV.createWith(
        (float) Math.toRadians(90.0f), 1.0f, 0.001f, 1000.0f);

    final R2ProjectionMeshType mesh =
      R2ProjectionMesh.create(
        g,
        proj,
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUsageHint.USAGE_STATIC_DRAW);
    final R2Texture2DUsableType image =
      td.whiteProjective2D();

    final R2DepthVarianceBufferDescription.Builder db =
      R2DepthVarianceBufferDescription.builder();
    db.setDepthPrecision(
      R2DepthPrecision.R2_DEPTH_PRECISION_24);
    db.setDepthVariancePrecision(
      R2DepthVariancePrecision.R2_DEPTH_VARIANCE_PRECISION_16);
    db.setMagnificationFilter(
      JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR);
    db.setMinificationFilter(
      JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR_MIPMAP_LINEAR);
    db.setArea(AreaSizeL.of(512L, 512L));
    final R2DepthVarianceBufferDescription desc = db.build();

    final R2ShadowDepthVariance shadow =
      R2ShadowDepthVariance.of(id_pool.freshID(), desc);

    final R2LightProjectiveWithShadowVarianceType ls =
      R2LightProjectiveWithShadowVariance.create(
        mesh, image, shadow, id_pool);

    final R2TransformOTType tr = ls.transformWritable();
    tr.setTranslation(PVector3D.of(0.0, 0.0, 10.0));

    final R2ShadowMapContextUsableType mc =
      rc.shadowExecComplete();

    this.expected.expect(R2RendererExceptionShadowNotRendered.class);
    mc.shadowMapGet(ls);
  }
}
