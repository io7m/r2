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

package com.io7m.r2.examples.custom;

import com.io7m.jareas.core.AreaInclusiveUnsignedL;
import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLClearSpecification;
import com.io7m.jcanephora.core.JCGLFaceSelection;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.api.JCGLClearType;
import com.io7m.jcanephora.core.api.JCGLColorBufferMaskingType;
import com.io7m.jcanephora.core.api.JCGLDepthBuffersType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLStencilBuffersType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.MatrixM4x4F;
import com.io7m.jtensors.VectorI3F;
import com.io7m.jtensors.VectorI4F;
import com.io7m.jtensors.parameterized.PMatrix4x4FType;
import com.io7m.jtensors.parameterized.PMatrixHeapArrayM4x4F;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2AmbientOcclusionBuffer;
import com.io7m.r2.core.R2AmbientOcclusionBufferDescription;
import com.io7m.r2.core.R2AmbientOcclusionBufferDescriptionType;
import com.io7m.r2.core.R2AmbientOcclusionBufferPool;
import com.io7m.r2.core.R2AmbientOcclusionBufferType;
import com.io7m.r2.core.R2AmbientOcclusionBufferUsableType;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2GeometryBuffer;
import com.io7m.r2.core.R2GeometryBufferDescription;
import com.io7m.r2.core.R2GeometryBufferType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2InstanceBatchedDynamic;
import com.io7m.r2.core.R2InstanceBatchedDynamicType;
import com.io7m.r2.core.R2InstanceSingle;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2MaterialOpaqueBatched;
import com.io7m.r2.core.R2MaterialOpaqueBatchedType;
import com.io7m.r2.core.R2MaterialOpaqueSingle;
import com.io7m.r2.core.R2MaterialOpaqueSingleType;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionFOV;
import com.io7m.r2.core.R2RenderTargetPoolUsableType;
import com.io7m.r2.core.R2SceneOpaques;
import com.io7m.r2.core.R2SceneOpaquesType;
import com.io7m.r2.core.R2SceneStencils;
import com.io7m.r2.core.R2SceneStencilsMode;
import com.io7m.r2.core.R2SceneStencilsType;
import com.io7m.r2.core.R2ShaderBatchedType;
import com.io7m.r2.core.R2ShaderSingleType;
import com.io7m.r2.core.R2ShaderSourcesResources;
import com.io7m.r2.core.R2ShaderSourcesType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2TransformOST;
import com.io7m.r2.core.R2UnitSphereType;
import com.io7m.r2.core.filters.R2FilterBoxBlur;
import com.io7m.r2.core.filters.R2FilterBoxBlurParameters;
import com.io7m.r2.core.filters.R2FilterSSAO;
import com.io7m.r2.core.filters.R2FilterSSAOParametersMutable;
import com.io7m.r2.core.filters.R2FilterSSAOParametersType;
import com.io7m.r2.core.filters.R2FilterShowTextureItem;
import com.io7m.r2.core.filters.R2FilterShowTextures;
import com.io7m.r2.core.filters.R2FilterShowTexturesParameters;
import com.io7m.r2.core.filters.R2FilterShowTexturesParametersType;
import com.io7m.r2.core.filters.R2SSAOKernel;
import com.io7m.r2.core.filters.R2SSAONoiseTexture;
import com.io7m.r2.core.shaders.R2SurfaceShaderBasicBatched;
import com.io7m.r2.core.shaders.R2SurfaceShaderBasicParameters;
import com.io7m.r2.core.shaders.R2SurfaceShaderBasicSingle;
import com.io7m.r2.examples.R2ExampleCustomType;
import com.io7m.r2.examples.R2ExampleServicesType;
import com.io7m.r2.main.R2MainType;
import com.io7m.r2.meshes.defaults.R2UnitSphere;
import com.io7m.r2.shaders.R2Shaders;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

// CHECKSTYLE_JAVADOC:OFF

public final class ExampleSSAO0 implements R2ExampleCustomType
{
  private final PMatrix4x4FType<R2SpaceWorldType, R2SpaceEyeType> view;

  private R2SceneStencilsType stencils;
  private R2SceneOpaquesType  opaques;

  private R2ProjectionFOV      projection;
  private R2InstanceSingleType instance;

  private R2AmbientOcclusionBufferType abuffer;
  private R2GeometryBufferType         gbuffer;
  private JCGLClearSpecification       geom_clear_spec;
  private JCGLClearSpecification       screen_clear_spec;

  private R2ShaderSingleType<R2SurfaceShaderBasicParameters>
    geom_shader;
  private R2SurfaceShaderBasicParameters
    geom_shader_params;
  private R2MaterialOpaqueSingleType<R2SurfaceShaderBasicParameters>
    geom_material;

  private R2UnitSphereType             sphere;
  private R2InstanceBatchedDynamicType batched_instance;
  private R2TransformOST[]
                                       batched_transforms;
  private R2ShaderBatchedType<R2SurfaceShaderBasicParameters>
                                       batched_geom_shader;
  private R2MaterialOpaqueBatchedType<R2SurfaceShaderBasicParameters>
                                       batched_geom_material;

  private R2FilterSSAOParametersMutable
    filter_ssao_params;
  private R2FilterType<R2FilterSSAOParametersType>
    filter_ssao;

  private R2FilterType<R2FilterShowTexturesParametersType>
    filter_show;
  private R2FilterShowTexturesParametersType
    filter_show_parameters;

  private R2MainType main;

  private R2RenderTargetPoolUsableType<
    R2AmbientOcclusionBufferDescriptionType,
    R2AmbientOcclusionBufferUsableType>
    pool_ssao;

  private R2FilterType<R2FilterBoxBlurParameters<
    R2AmbientOcclusionBufferDescriptionType,
    R2AmbientOcclusionBufferUsableType,
    R2AmbientOcclusionBufferDescriptionType,
    R2AmbientOcclusionBufferUsableType>>
    filter_blur_ssao;

  private R2FilterBoxBlurParameters<
    R2AmbientOcclusionBufferDescriptionType,
    R2AmbientOcclusionBufferUsableType,
    R2AmbientOcclusionBufferDescriptionType,
    R2AmbientOcclusionBufferUsableType>
    filter_blur_ssao_params;

  public ExampleSSAO0()
  {
    this.view = PMatrixHeapArrayM4x4F.newMatrix();
  }

  @Override
  public void onInitialize(
    final R2ExampleServicesType serv,
    final JCGLInterfaceGL33Type g,
    final AreaInclusiveUnsignedLType area,
    final R2MainType m)
  {
    this.main = NullCheck.notNull(m);
    this.sphere = R2UnitSphere.newUnitSphere8(g);
    this.opaques = R2SceneOpaques.newOpaques();
    this.stencils = R2SceneStencils.newMasks();

    {
      final R2GeometryBufferDescription.Builder b =
        R2GeometryBufferDescription.builder();
      b.setArea(area);

      this.gbuffer = R2GeometryBuffer.newGeometryBuffer(
        g.getFramebuffers(),
        g.getTextures(),
        this.main.getTextureUnitAllocator().getRootContext(),
        b.build());
    }

    {
      final R2AmbientOcclusionBufferDescription.Builder b =
        R2AmbientOcclusionBufferDescription.builder();
      b.setArea(AreaInclusiveUnsignedL.of(
        new UnsignedRangeInclusiveL(0L, area.getRangeX().getUpper() / 2L),
        new UnsignedRangeInclusiveL(0L, area.getRangeY().getUpper() / 2L)
      ));

      this.abuffer =
        R2AmbientOcclusionBuffer.newAmbientOcclusionBuffer(
          g.getFramebuffers(),
          g.getTextures(),
          this.main.getTextureUnitAllocator().getRootContext(),
          b.build());
    }

    this.filter_ssao_params = R2FilterSSAOParametersMutable.create();
    this.filter_ssao_params.setKernel(R2SSAOKernel.newKernel(32));
    this.filter_ssao_params.setGeometryBuffer(this.gbuffer);
    this.filter_ssao_params.setNoiseTexture(
      R2SSAONoiseTexture.new4x4Noise(
        g.getTextures(),
        this.main.getTextureUnitAllocator().getRootContext()));
    this.filter_ssao_params.setOutputBuffer(this.abuffer);

    this.filter_ssao = R2FilterSSAO.newFilter(
      m.getShaderSources(),
      g,
      this.main.getTextureUnitAllocator().getRootContext(),
      m.getIDPool(),
      m.getUnitQuad());

    {
      this.pool_ssao =
        R2AmbientOcclusionBufferPool.newPool(g, 614400L, 6144000L);
    }

    {
      this.filter_blur_ssao_params =
        R2FilterBoxBlurParameters.newParameters(
          this.abuffer,
          R2AmbientOcclusionBufferUsableType::getAmbientOcclusionTexture,
          this.abuffer,
          R2AmbientOcclusionBufferUsableType::getAmbientOcclusionTexture,
          this.pool_ssao);
      this.filter_blur_ssao_params.setBlurSize(0.0f);
      this.filter_blur_ssao_params.setBlurScale(0.75f);
      this.filter_blur_ssao = R2FilterBoxBlur.newFilter(
        m.getShaderSources(),
        g,
        m.getTextureDefaults(),
        this.pool_ssao,
        m.getIDPool(),
        m.getUnitQuad());
    }

    {
      final R2FilterShowTexturesParameters.Builder b =
        R2FilterShowTexturesParameters.builder();

      b.addItems(R2FilterShowTextureItem.of(
        this.abuffer.getAmbientOcclusionTexture(), area));

      long x = 20L;
      final long y = 20L;

      b.addItems(R2FilterShowTextureItem.of(
        this.gbuffer.getAlbedoEmissiveTexture(),
        AreaInclusiveUnsignedL.of(
          new UnsignedRangeInclusiveL(x, x + 128L),
          new UnsignedRangeInclusiveL(y, y + 96L))
      ));

      x += 128L + 20L;

      b.addItems(R2FilterShowTextureItem.of(
        this.gbuffer.getNormalTexture(),
        AreaInclusiveUnsignedL.of(
          new UnsignedRangeInclusiveL(x, x + 128L),
          new UnsignedRangeInclusiveL(y, y + 96L))
      ));

      x += 128L + 20L;

      b.addItems(R2FilterShowTextureItem.of(
        this.gbuffer.getSpecularTexture(),
        AreaInclusiveUnsignedL.of(
          new UnsignedRangeInclusiveL(x, x + 128L),
          new UnsignedRangeInclusiveL(y, y + 96L))
      ));

      x += 128L + 20L;

      b.addItems(R2FilterShowTextureItem.of(
        this.gbuffer.getDepthTexture(),
        AreaInclusiveUnsignedL.of(
          new UnsignedRangeInclusiveL(x, x + 128L),
          new UnsignedRangeInclusiveL(y, y + 96L))
      ));

      this.filter_show_parameters = b.build();
    }

    this.filter_show =
      R2FilterShowTextures.newFilter(
        g, m.getShaderSources(), m.getIDPool(), m.getUnitQuad());

    this.projection = R2ProjectionFOV.newFrustumWith(
      m.getProjectionMatrices(),
      (float) Math.toRadians(90.0), 640.0f / 480.0f, 0.001f, 1000.0f);

    final R2IDPoolType id_pool = m.getIDPool();
    final JCGLArrayObjectType mesh = serv.getMesh("halls.r2z");

    final R2TransformOST transform = R2TransformOST.newTransform();
    transform.getTranslation().set3F(0.0f, -1.0f, 0.0f);

    this.instance =
      R2InstanceSingle.newInstance(
        id_pool, mesh, transform, PMatrixI3x3F.identity());

    final int width = 16;
    final int height = 16;
    final int depth = 16;
    final int instance_count = width * height * depth;
    this.batched_instance =
      R2InstanceBatchedDynamic.newBatch(
        id_pool,
        g.getArrayBuffers(),
        g.getArrayObjects(),
        this.sphere.getArrayObject(),
        instance_count);

    this.batched_transforms = new R2TransformOST[instance_count];

    int index = 0;
    for (int x = 0; x < width; ++x) {
      for (int y = 0; y < height; ++y) {
        for (int z = 0; z < depth; ++z) {
          final R2TransformOST t = R2TransformOST.newTransform();
          t.setScale(0.2f);
          final PVector3FType<R2SpaceWorldType> tr = t.getTranslation();
          final float fx = (float) (x - (width / 2));
          final float fy = (float) (y - (height / 2));
          final float fz = (float) -z * 1.5f;
          tr.set3F(fx, fy, fz);
          this.batched_transforms[index] = t;
          this.batched_instance.enableInstance(this.batched_transforms[index]);
          ++index;
        }
      }
    }

    final R2ShaderSourcesType sources =
      R2ShaderSourcesResources.newSources(R2Shaders.class);

    this.geom_shader =
      R2SurfaceShaderBasicSingle.newShader(
        g.getShaders(),
        sources,
        id_pool);
    this.geom_shader_params =
      R2SurfaceShaderBasicParameters.newParameters(
        m.getTextureDefaults());
    this.geom_shader_params.getSpecularColor().set3F(1.0f, 1.0f, 1.0f);
    this.geom_shader_params.setSpecularExponent(64.0f);
    this.geom_material = R2MaterialOpaqueSingle.newMaterial(
      id_pool, this.geom_shader, this.geom_shader_params);

    this.batched_geom_shader =
      R2SurfaceShaderBasicBatched.newShader(
        g.getShaders(),
        sources,
        id_pool);
    this.batched_geom_material = R2MaterialOpaqueBatched.newMaterial(
      id_pool, this.batched_geom_shader, this.geom_shader_params);

    {
      final JCGLClearSpecification.Builder csb =
        JCGLClearSpecification.builder();
      csb.setStencilBufferClear(0);
      csb.setDepthBufferClear(1.0);
      csb.setColorBufferClear(new VectorI4F(0.0f, 0.0f, 0.0f, 0.0f));
      this.geom_clear_spec = csb.build();
    }

    {
      final JCGLClearSpecification.Builder csb =
        JCGLClearSpecification.builder();
      csb.setStencilBufferClear(0);
      csb.setDepthBufferClear(1.0);
      csb.setColorBufferClear(new VectorI4F(0.0f, 0.0f, 0.0f, 0.0f));
      this.screen_clear_spec = csb.build();
    }
  }

  @Override
  public void onRender(
    final R2ExampleServicesType serv,
    final JCGLInterfaceGL33Type g,
    final AreaInclusiveUnsignedLType area,
    final R2MainType m,
    final int frame)
  {
    this.stencils.stencilsReset();
    this.stencils.stencilsSetMode(
      R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_NEGATIVE);

    this.opaques.opaquesReset();
    this.opaques.opaquesAddSingleInstance(this.instance, this.geom_material);
    this.opaques.opaquesAddBatchedInstance(
      this.batched_instance, this.batched_geom_material);

    if (serv.isFreeCameraEnabled()) {
      MatrixM4x4F.copy(serv.getFreeCameraViewMatrix(), this.view);
    } else {
      m.getViewMatrices().lookAt(
        this.view,
        new VectorI3F(0.0f, 0.0f, 5.0f),
        new VectorI3F(0.0f, 0.0f, 0.0f),
        new VectorI3F(0.0f, 1.0f, 0.0f));
    }

    {
      final R2MatricesType matrices = m.getMatrices();

      matrices.withObserver(this.view, this.projection, this, (mo, t) -> {
        final R2TextureUnitContextParentType uc =
          t.main.getTextureUnitAllocator().getRootContext();
        final JCGLFramebufferUsableType gbuffer_fb =
          t.gbuffer.getPrimaryFramebuffer();

        final JCGLFramebuffersType g_fb = g.getFramebuffers();
        final JCGLClearType g_cl = g.getClear();
        final JCGLColorBufferMaskingType g_cb = g.getColorBufferMasking();
        final JCGLStencilBuffersType g_sb = g.getStencilBuffers();
        final JCGLDepthBuffersType g_db = g.getDepthBuffers();

        g_fb.framebufferDrawBind(gbuffer_fb);
        g_cb.colorBufferMask(true, true, true, true);
        g_db.depthBufferWriteEnable();
        g_sb.stencilBufferMask(
          JCGLFaceSelection.FACE_FRONT_AND_BACK, 0b11111111);
        g_cl.clear(t.geom_clear_spec);

        t.main.getStencilRenderer().renderStencilsWithBoundBuffer(
          mo,
          t.gbuffer.getArea(),
          t.stencils);
        t.main.getGeometryRenderer().renderGeometryWithBoundBuffer(
          t.gbuffer.getArea(),
          m.getTextureUnitAllocator().getRootContext(),
          mo,
          t.opaques);
        g_fb.framebufferDrawUnbind();

        t.filter_ssao_params.setSceneObserverValues(mo);
        t.filter_ssao.runFilter(uc, t.filter_ssao_params);
        t.filter_blur_ssao.runFilter(uc, t.filter_blur_ssao_params);

        g_cb.colorBufferMask(true, true, true, true);
        g_db.depthBufferWriteEnable();
        g_sb.stencilBufferMask(
          JCGLFaceSelection.FACE_FRONT_AND_BACK, 0b11111111);
        g_cl.clear(t.screen_clear_spec);

        t.filter_show.runFilter(uc, t.filter_show_parameters);

        return Unit.unit();
      });
    }
  }

  @Override
  public void onFinish(
    final JCGLInterfaceGL33Type g,
    final R2MainType m)
  {
    this.geom_shader.delete(g);
  }
}
