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
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.MatrixM4x4F;
import com.io7m.jtensors.VectorI3F;
import com.io7m.jtensors.VectorI4F;
import com.io7m.jtensors.parameterized.PMatrix4x4FType;
import com.io7m.jtensors.parameterized.PMatrixHeapArrayM4x4F;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.jtensors.parameterized.PVectorM3F;
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
import com.io7m.r2.core.R2TransformSOT;
import com.io7m.r2.core.R2UnitSphereType;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicBatched;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicParametersMutable;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicParametersType;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicSingle;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironment;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.examples.R2ExampleCustomType;
import com.io7m.r2.examples.R2ExampleServicesType;
import com.io7m.r2.filters.R2BlurParametersMutable;
import com.io7m.r2.filters.R2FilterBoxBlur;
import com.io7m.r2.filters.R2FilterBoxBlurParameters;
import com.io7m.r2.filters.R2FilterBoxBlurParametersType;
import com.io7m.r2.filters.R2FilterCompositor;
import com.io7m.r2.filters.R2FilterCompositorItem;
import com.io7m.r2.filters.R2FilterCompositorParameters;
import com.io7m.r2.filters.R2FilterCompositorParametersType;
import com.io7m.r2.filters.R2FilterSSAO;
import com.io7m.r2.filters.R2FilterSSAOParametersMutable;
import com.io7m.r2.filters.R2FilterSSAOParametersType;
import com.io7m.r2.filters.R2SSAOKernel;
import com.io7m.r2.filters.R2SSAONoiseTexture;
import com.io7m.r2.main.R2MainType;
import com.io7m.r2.meshes.defaults.R2UnitSphere;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import com.io7m.sombrero.core.SoShaderPreprocessorConfig;
import com.io7m.sombrero.core.SoShaderPreprocessorType;
import com.io7m.sombrero.core.SoShaderResolver;
import com.io7m.sombrero.jcpp.SoShaderPreprocessorJCPP;

import java.util.Optional;
import java.util.OptionalInt;

// CHECKSTYLE_JAVADOC:OFF

public final class ExampleSSAO0 implements R2ExampleCustomType
{
  private final PMatrix4x4FType<R2SpaceWorldType, R2SpaceEyeType> view;

  private JCGLInterfaceGL33Type g33;


  private R2MainType main;
  private R2SceneStencilsType stencils;
  private R2SceneOpaquesType opaques;

  private R2ProjectionFOV projection;
  private R2InstanceSingleType instance;

  private JCGLClearSpecification screen_clear_spec;

  private R2GeometryBufferType geom_buffer;
  private R2ShaderInstanceSingleType<R2SurfaceShaderBasicParametersType> geom_shader;
  private R2SurfaceShaderBasicParametersMutable geom_shader_params;
  private R2MaterialOpaqueSingleType<R2SurfaceShaderBasicParametersType> geom_material;

  private R2UnitSphereType sphere;
  private R2InstanceBatchedDynamicType batched_instance;
  private R2TransformSOT[] batched_transforms;
  private R2ShaderInstanceBatchedType<R2SurfaceShaderBasicParametersType> batched_geom_shader;
  private R2MaterialOpaqueBatchedType<R2SurfaceShaderBasicParametersType> batched_geom_material;

  // Compositor

  private R2FilterType<R2FilterCompositorParametersType> filter_compositor;
  private R2FilterCompositorParametersType filter_comp_parameters;

  // SSAO

  private R2AmbientOcclusionBufferType ssao_buffer;
  private R2FilterSSAOParametersMutable filter_ssao_params;
  private R2FilterType<R2FilterSSAOParametersType> filter_ssao;
  private R2RenderTargetPoolUsableType<R2AmbientOcclusionBufferDescriptionType, R2AmbientOcclusionBufferUsableType> pool_ssao;
  private R2FilterType<R2FilterBoxBlurParametersType<R2AmbientOcclusionBufferDescriptionType, R2AmbientOcclusionBufferUsableType, R2AmbientOcclusionBufferDescriptionType, R2AmbientOcclusionBufferUsableType>> filter_blur_ssao;
  private R2FilterBoxBlurParametersType<R2AmbientOcclusionBufferDescriptionType, R2AmbientOcclusionBufferUsableType, R2AmbientOcclusionBufferDescriptionType, R2AmbientOcclusionBufferUsableType> filter_blur_ssao_params;

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

      this.geom_buffer = R2GeometryBuffer.newGeometryBuffer(
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

      this.ssao_buffer =
        R2AmbientOcclusionBuffer.newAmbientOcclusionBuffer(
          g.getFramebuffers(),
          g.getTextures(),
          this.main.getTextureUnitAllocator().getRootContext(),
          b.build());
    }

    this.filter_ssao_params = R2FilterSSAOParametersMutable.create();
    this.filter_ssao_params.setKernel(R2SSAOKernel.newKernel(64));
    this.filter_ssao_params.setExponent(1.0f);
    this.filter_ssao_params.setSampleRadius(1.0f);
    this.filter_ssao_params.setGeometryBuffer(this.geom_buffer);
    this.filter_ssao_params.setNoiseTexture(
      R2SSAONoiseTexture.newNoiseTexture(
        g.getTextures(),
        this.main.getTextureUnitAllocator().getRootContext()));
    this.filter_ssao_params.setOutputBuffer(this.ssao_buffer);

    this.filter_ssao = R2FilterSSAO.newFilter(
      m.getShaderPreprocessingEnvironment(),
      g,
      m.getTextureDefaults(),
      m.getTextureUnitAllocator().getRootContext(),
      m.getIDPool(),
      m.getUnitQuad());

    {
      final UnsignedRangeInclusiveL screen_x = area.getRangeX();
      final UnsignedRangeInclusiveL screen_y = area.getRangeY();
      final long one = screen_x.getInterval() * screen_y.getInterval();
      final long soft = one * 2L;
      final long hard = one * 10L;
      this.pool_ssao = R2AmbientOcclusionBufferPool.newPool(g, soft, hard);
    }

    {
      final R2BlurParametersMutable blur = R2BlurParametersMutable.create();
      this.filter_blur_ssao_params =
        R2FilterBoxBlurParameters.of(
          this.ssao_buffer,
          R2AmbientOcclusionBufferUsableType::ambientOcclusionTexture,
          this.ssao_buffer,
          R2AmbientOcclusionBufferUsableType::ambientOcclusionTexture,
          this.pool_ssao,
          blur,
          (d, a) -> {
            final R2AmbientOcclusionBufferDescription.Builder b =
              R2AmbientOcclusionBufferDescription.builder();
            b.from(d);
            b.setArea(a);
            return b.build();
          });
      this.filter_blur_ssao = R2FilterBoxBlur.newFilter(
        m.getShaderPreprocessingEnvironment(),
        g,
        m.getTextureDefaults(),
        this.pool_ssao,
        m.getIDPool(),
        m.getUnitQuad());
    }

    {
      final R2FilterCompositorParameters.Builder b =
        R2FilterCompositorParameters.builder();

      b.addItems(R2FilterCompositorItem.of(
        this.ssao_buffer.ambientOcclusionTexture(),
        area,
        1.0f,
        Optional.empty()));

      long x = 20L;
      final long y = 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.geom_buffer.albedoEmissiveTexture(),
        AreaInclusiveUnsignedL.of(
          new UnsignedRangeInclusiveL(x, x + 128L),
          new UnsignedRangeInclusiveL(y, y + 96L)),
        1.0f,
        Optional.empty()));

      x += 128L + 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.geom_buffer.normalTexture(),
        AreaInclusiveUnsignedL.of(
          new UnsignedRangeInclusiveL(x, x + 128L),
          new UnsignedRangeInclusiveL(y, y + 96L)),
        1.0f,
        Optional.empty()));

      x += 128L + 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.geom_buffer.getSpecularTextureOrDefault(m.getTextureDefaults()),
        AreaInclusiveUnsignedL.of(
          new UnsignedRangeInclusiveL(x, x + 128L),
          new UnsignedRangeInclusiveL(y, y + 96L)),
        1.0f,
        Optional.empty()));

      x += 128L + 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.geom_buffer.depthTexture(),
        AreaInclusiveUnsignedL.of(
          new UnsignedRangeInclusiveL(x, x + 128L),
          new UnsignedRangeInclusiveL(y, y + 96L)),
        1.0f,
        Optional.empty()));

      this.filter_comp_parameters = b.build();
    }

    this.filter_compositor =
      R2FilterCompositor.newFilter(
        m.getShaderPreprocessingEnvironment(),
        m.getTextureDefaults(),
        g,
        m.getIDPool(),
        m.getUnitQuad());

    this.projection = R2ProjectionFOV.newFrustumWith(
      m.getProjectionMatrices(),
      (float) Math.toRadians(90.0), 640.0f / 480.0f, 0.001f, 1000.0f);

    final R2IDPoolType id_pool = m.getIDPool();
    final JCGLArrayObjectType mesh = serv.getMesh("halls_complex.r2z");

    final R2TransformSOT transform = R2TransformSOT.newTransform();
    transform.getTranslation().set3F(0.0f, -1.0f, 0.0f);

    this.instance =
      R2InstanceSingle.of(
        id_pool.freshID(), mesh, transform, PMatrixI3x3F.identity());

    final int width = 16;
    final int height = 16;
    final int depth = 16;
    final int instance_count = width * height * depth;
    this.batched_instance =
      R2InstanceBatchedDynamic.newBatch(
        id_pool,
        g.getArrayBuffers(),
        g.getArrayObjects(),
        this.sphere.arrayObject(),
        instance_count);

    this.batched_transforms = new R2TransformSOT[instance_count];

    int index = 0;
    for (int x = 0; x < width; ++x) {
      for (int y = 0; y < height; ++y) {
        for (int z = 0; z < depth; ++z) {
          final R2TransformSOT t = R2TransformSOT.newTransform();
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

    final SoShaderPreprocessorConfig.Builder b =
      SoShaderPreprocessorConfig.builder();
    b.setResolver(SoShaderResolver.create());
    b.setVersion(OptionalInt.of(330));
    final SoShaderPreprocessorType p =
      SoShaderPreprocessorJCPP.create(b.build());
    final R2ShaderPreprocessingEnvironmentType sources =
      R2ShaderPreprocessingEnvironment.create(p);

    this.geom_shader =
      R2SurfaceShaderBasicSingle.newShader(g.getShaders(), sources, id_pool);
    this.geom_shader_params =
      R2SurfaceShaderBasicParametersMutable.create(m.getTextureDefaults());
    this.geom_shader_params.setSpecularColor(
      new PVectorM3F<>(1.0f, 1.0f, 1.0f));
    this.geom_shader_params.setSpecularExponent(64.0f);
    this.geom_material = R2MaterialOpaqueSingle.of(
      id_pool.freshID(), this.geom_shader, this.geom_shader_params);

    this.batched_geom_shader =
      R2SurfaceShaderBasicBatched.newShader(
        g.getShaders(),
        sources,
        id_pool);
    this.batched_geom_material = R2MaterialOpaqueBatched.of(
      id_pool.freshID(), this.batched_geom_shader, this.geom_shader_params);

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
    this.g33 = g;

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

    final R2MatricesType matrices = m.getMatrices();
    matrices.withObserver(this.view, this.projection, this, (mo, t) -> {
      final JCGLTextureUnitContextParentType uc =
        t.main.getTextureUnitAllocator().getRootContext();
      final JCGLFramebufferUsableType gbuffer_fb =
        t.geom_buffer.primaryFramebuffer();

      final JCGLFramebuffersType g_fb =
        t.g33.getFramebuffers();
      final JCGLClearType g_cl =
        t.g33.getClear();
      final JCGLColorBufferMaskingType g_cb =
        t.g33.getColorBufferMasking();
      final JCGLStencilBuffersType g_sb =
        t.g33.getStencilBuffers();
      final JCGLDepthBuffersType g_db =
        t.g33.getDepthBuffers();

      final JCGLProfilingType pro =
        t.main.getProfiling();
      final JCGLProfilingFrameType pro_frame =
        pro.startFrame();
      final JCGLProfilingContextType pro_root =
        pro_frame.getChildContext("main");

      g_fb.framebufferDrawBind(gbuffer_fb);
      t.geom_buffer.clearBoundPrimaryFramebuffer(t.g33);
      t.main.getStencilRenderer().renderStencilsWithBoundBuffer(
        mo,
        pro_root,
        m.getTextureUnitAllocator().getRootContext(),
        t.geom_buffer.area(),
        t.stencils);
      t.main.getGeometryRenderer().renderGeometry(
        t.geom_buffer.area(),
        Optional.empty(),
        pro_root,
        m.getTextureUnitAllocator().getRootContext(),
        mo,
        t.opaques);
      g_fb.framebufferDrawUnbind();

      t.filter_ssao_params.setSceneObserverValues(mo);
      t.filter_ssao.runFilter(pro_root, uc, t.filter_ssao_params);
      t.filter_blur_ssao.runFilter(pro_root, uc, t.filter_blur_ssao_params);

      g_cb.colorBufferMask(true, true, true, true);
      g_db.depthBufferWriteEnable();
      g_sb.stencilBufferMask(
        JCGLFaceSelection.FACE_FRONT_AND_BACK, 0b11111111);
      g_cl.clear(t.screen_clear_spec);

      t.filter_compositor.runFilter(pro_root, uc, t.filter_comp_parameters);

      return Unit.unit();
    });
  }

  @Override
  public void onFinish(
    final JCGLInterfaceGL33Type g,
    final R2MainType m)
  {
    this.geom_shader.delete(g);
  }
}
