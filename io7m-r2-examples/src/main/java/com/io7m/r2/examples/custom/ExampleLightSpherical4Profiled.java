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
import com.io7m.jcanephora.core.JCGLProjectionMatrices;
import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLClearType;
import com.io7m.jcanephora.core.api.JCGLColorBufferMaskingType;
import com.io7m.jcanephora.core.api.JCGLDepthBuffersType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLStencilBuffersType;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameMeasurementType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingIteration;
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
import com.io7m.jtensors.parameterized.PVectorI3F;
import com.io7m.jtensors.parameterized.PVectorI4F;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2AmbientOcclusionBuffer;
import com.io7m.r2.core.R2AmbientOcclusionBufferDescription;
import com.io7m.r2.core.R2AmbientOcclusionBufferDescriptionType;
import com.io7m.r2.core.R2AmbientOcclusionBufferPool;
import com.io7m.r2.core.R2AmbientOcclusionBufferType;
import com.io7m.r2.core.R2AmbientOcclusionBufferUsableType;
import com.io7m.r2.core.R2CopyDepth;
import com.io7m.r2.core.R2DepthAttachmentShare;
import com.io7m.r2.core.R2DepthInstances;
import com.io7m.r2.core.R2DepthInstancesType;
import com.io7m.r2.core.R2DepthPrecision;
import com.io7m.r2.core.R2DepthVarianceBufferDescription;
import com.io7m.r2.core.R2DepthVariancePrecision;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2GeometryBuffer;
import com.io7m.r2.core.R2GeometryBufferComponents;
import com.io7m.r2.core.R2GeometryBufferDescription;
import com.io7m.r2.core.R2GeometryBufferType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2ImageBuffer;
import com.io7m.r2.core.R2ImageBufferDescription;
import com.io7m.r2.core.R2ImageBufferDescriptionType;
import com.io7m.r2.core.R2ImageBufferPool;
import com.io7m.r2.core.R2ImageBufferType;
import com.io7m.r2.core.R2ImageBufferUsableType;
import com.io7m.r2.core.R2InstanceBatchedDynamic;
import com.io7m.r2.core.R2InstanceBatchedDynamicType;
import com.io7m.r2.core.R2InstanceBillboardedDynamic;
import com.io7m.r2.core.R2InstanceBillboardedDynamicType;
import com.io7m.r2.core.R2InstanceSingle;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2LightAmbientScreenSingle;
import com.io7m.r2.core.R2LightBufferDescription;
import com.io7m.r2.core.R2LightBufferDiffuseSpecularUsableType;
import com.io7m.r2.core.R2LightBufferType;
import com.io7m.r2.core.R2LightBuffers;
import com.io7m.r2.core.R2LightProjectiveWithShadowVariance;
import com.io7m.r2.core.R2LightProjectiveWithShadowVarianceType;
import com.io7m.r2.core.R2LightSphericalSingle;
import com.io7m.r2.core.R2LightSphericalSingleReadableType;
import com.io7m.r2.core.R2LightSphericalSingleType;
import com.io7m.r2.core.R2MaterialDepthSingle;
import com.io7m.r2.core.R2MaterialDepthSingleType;
import com.io7m.r2.core.R2MaterialOpaqueBatched;
import com.io7m.r2.core.R2MaterialOpaqueBatchedType;
import com.io7m.r2.core.R2MaterialOpaqueBillboarded;
import com.io7m.r2.core.R2MaterialOpaqueSingle;
import com.io7m.r2.core.R2MaterialOpaqueSingleType;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionFOV;
import com.io7m.r2.core.R2ProjectionFrustum;
import com.io7m.r2.core.R2ProjectionMesh;
import com.io7m.r2.core.R2ProjectionMeshType;
import com.io7m.r2.core.R2RenderTargetPoolType;
import com.io7m.r2.core.R2RenderTargetPoolUsableType;
import com.io7m.r2.core.R2SceneLights;
import com.io7m.r2.core.R2SceneLightsClipGroupType;
import com.io7m.r2.core.R2SceneLightsGroupType;
import com.io7m.r2.core.R2SceneLightsType;
import com.io7m.r2.core.R2SceneOpaques;
import com.io7m.r2.core.R2SceneOpaquesType;
import com.io7m.r2.core.R2SceneStencils;
import com.io7m.r2.core.R2SceneStencilsMode;
import com.io7m.r2.core.R2SceneStencilsType;
import com.io7m.r2.core.R2ShadowDepthVariance;
import com.io7m.r2.core.R2ShadowMapContextType;
import com.io7m.r2.core.R2ShadowMapRendererExecutionType;
import com.io7m.r2.core.R2Texture2DType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2TransformSOT;
import com.io7m.r2.core.R2TransformSiOT;
import com.io7m.r2.core.R2TransformT;
import com.io7m.r2.core.R2UnitSphereType;
import com.io7m.r2.core.debug.R2DebugCube;
import com.io7m.r2.core.debug.R2DebugCubeInstance;
import com.io7m.r2.core.debug.R2DebugCubeType;
import com.io7m.r2.core.debug.R2DebugInstanceSingle;
import com.io7m.r2.core.debug.R2DebugInstances;
import com.io7m.r2.core.debug.R2DebugLineSegment;
import com.io7m.r2.core.debug.R2DebugVisualizerRendererParameters;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicParameters;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicSingle;
import com.io7m.r2.core.shaders.provided.R2LightShaderAmbientSingle;
import com.io7m.r2.core.shaders.provided.R2LightShaderProjectiveLambertShadowVarianceSingle;
import com.io7m.r2.core.shaders.provided.R2LightShaderSphericalLambertBlinnPhongSingle;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicBatched;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicBillboarded;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicParameters;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicSingle;
import com.io7m.r2.core.shaders.types.R2ShaderDepthSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBillboardedType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightProjectiveWithShadowType;
import com.io7m.r2.core.shaders.types.R2ShaderLightSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightVolumeSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.examples.R2ExampleCustomType;
import com.io7m.r2.examples.R2ExampleServicesType;
import com.io7m.r2.filters.R2BilateralBlurParameters;
import com.io7m.r2.filters.R2BlurParameters;
import com.io7m.r2.filters.R2FilterBilateralBlurDepthAware;
import com.io7m.r2.filters.R2FilterBilateralBlurDepthAwareParameters;
import com.io7m.r2.filters.R2FilterBoxBlur;
import com.io7m.r2.filters.R2FilterBoxBlurParameters;
import com.io7m.r2.filters.R2FilterCompositor;
import com.io7m.r2.filters.R2FilterCompositorItem;
import com.io7m.r2.filters.R2FilterCompositorParameters;
import com.io7m.r2.filters.R2FilterEmission;
import com.io7m.r2.filters.R2FilterEmissionParameters;
import com.io7m.r2.filters.R2FilterFXAA;
import com.io7m.r2.filters.R2FilterFXAAParameters;
import com.io7m.r2.filters.R2FilterFXAAQuality;
import com.io7m.r2.filters.R2FilterLightApplicator;
import com.io7m.r2.filters.R2FilterLightApplicatorParameters;
import com.io7m.r2.filters.R2FilterLightApplicatorParametersType;
import com.io7m.r2.filters.R2FilterOcclusionApplicator;
import com.io7m.r2.filters.R2FilterOcclusionApplicatorParameters;
import com.io7m.r2.filters.R2FilterSSAO;
import com.io7m.r2.filters.R2FilterSSAOParameters;
import com.io7m.r2.filters.R2SSAOKernel;
import com.io7m.r2.filters.R2SSAOKernelType;
import com.io7m.r2.filters.R2SSAONoiseTexture;
import com.io7m.r2.main.R2MainType;
import com.io7m.r2.meshes.defaults.R2UnitCube;
import com.io7m.r2.meshes.defaults.R2UnitSphere;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

import javax.swing.SwingUtilities;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

// CHECKSTYLE:OFF

public final class ExampleLightSpherical4Profiled implements R2ExampleCustomType
{
  private final PMatrix4x4FType<R2SpaceWorldType, R2SpaceEyeType> view;

  private JCGLClearSpecification screen_clear_spec;
  private R2SceneStencilsType stencils;
  private R2ProjectionFOV projection;
  private R2InstanceSingleType instance;
  private R2SceneOpaquesType opaques;
  private R2SceneLightsType lights;

  private R2GeometryBufferType gbuffer;
  private R2LightBufferType lbuffer;
  private R2ImageBufferType ibuffer;
  private R2AmbientOcclusionBufferType abuffer;

  private R2ShaderInstanceSingleType<R2SurfaceShaderBasicParameters> geom_shader;
  private R2MaterialOpaqueSingleType<R2SurfaceShaderBasicParameters> geom_material;

  private R2ShaderLightVolumeSingleType<R2LightSphericalSingleReadableType> sphere_light_shader;
  private R2LightSphericalSingleType sphere_light;
  private R2LightSphericalSingleType sphere_light_bounded;
  private R2InstanceSingleType sphere_light_bounds;

  private R2ShaderLightProjectiveWithShadowType<R2LightProjectiveWithShadowVarianceType> proj_light_shader;
  private R2ProjectionFrustum proj_proj;
  private R2ProjectionMeshType proj_mesh;
  private R2LightProjectiveWithShadowVarianceType proj_light;
  private R2DepthInstancesType proj_shadow_instances;

  private R2MaterialDepthSingleType<R2DepthShaderBasicParameters> depth_material;

  private R2InstanceSingleType golden;
  private R2MaterialOpaqueSingleType<R2SurfaceShaderBasicParameters> golden_material;
  private R2MaterialDepthSingleType<R2DepthShaderBasicParameters> golden_depth_material;

  private R2InstanceSingleType glow;
  private R2MaterialOpaqueSingleType<R2SurfaceShaderBasicParameters> glow_material;

  private R2InstanceBatchedDynamicType batched_instance;
  private R2MaterialOpaqueBatchedType<R2SurfaceShaderBasicParameters> batched_geom_material;

  private R2FilterType<R2FilterLightApplicatorParametersType> filter_light;
  private R2FilterLightApplicatorParameters filter_light_params;

  private R2FilterType<R2FilterFXAAParameters> filter_fxaa;
  private R2FilterFXAAParameters filter_fxaa_params;

  private R2FilterType<R2FilterCompositorParameters> filter_compositor;
  private R2FilterCompositorParameters filter_comp_parameters;

  private R2MainType main;

  private R2AmbientOcclusionBufferType ssao_buffer;
  private R2RenderTargetPoolUsableType<
    R2AmbientOcclusionBufferDescriptionType,
    R2AmbientOcclusionBufferUsableType> ssao_pool;
  private R2FilterType<
    R2FilterBilateralBlurDepthAwareParameters<
      R2AmbientOcclusionBufferDescriptionType,
      R2AmbientOcclusionBufferUsableType,
      R2AmbientOcclusionBufferDescriptionType,
      R2AmbientOcclusionBufferUsableType>> ssao_filter_blur;
  private R2FilterBilateralBlurDepthAwareParameters<
    R2AmbientOcclusionBufferDescriptionType,
    R2AmbientOcclusionBufferUsableType,
    R2AmbientOcclusionBufferDescriptionType,
    R2AmbientOcclusionBufferUsableType> ssao_filter_blur_params;
  private R2FilterSSAOParameters ssao_filter_params;
  private R2FilterType<R2FilterSSAOParameters> ssao_filter;
  private R2SSAOKernelType ssao_kernel;
  private R2Texture2DType ssao_noise_texture;
  private R2FilterType<R2FilterOcclusionApplicatorParameters> ssao_applicator;

  private R2LightAmbientScreenSingle light_ambient;
  private R2ShaderLightSingleType<R2LightAmbientScreenSingle> light_ambient_shader;

  private JCGLInterfaceGL33Type g;

  private R2DebugVisualizerRendererParameters debug_params;
  private R2ShadowMapContextType shadow_context;

  private AtomicReference<ExampleProfilingWindow> profiling_window;
  private StringBuilder text_buffer;
  private String text;
  private JCGLProfilingContextType profiling_root;

  private R2FilterType<R2FilterEmissionParameters> filter_emission;
  private R2FilterEmissionParameters filter_emission_params;

  private R2ShaderInstanceBillboardedType<R2SurfaceShaderBasicParameters> billboarded_shader;
  private R2MaterialOpaqueBillboarded<R2SurfaceShaderBasicParameters> billboarded_material;
  private R2InstanceBillboardedDynamicType billboarded_instance;

  public ExampleLightSpherical4Profiled()
  {
    this.view = PMatrixHeapArrayM4x4F.newMatrix();
  }

  @Override
  public void onInitialize(
    final R2ExampleServicesType serv,
    final JCGLInterfaceGL33Type gx,
    final AreaInclusiveUnsignedLType area,
    final R2MainType m)
  {
    this.main = NullCheck.notNull(m);

    final R2UnitSphereType sphere = R2UnitSphere.newUnitSphere8(gx);
    this.opaques = R2SceneOpaques.newOpaques();
    this.lights = R2SceneLights.newLights();
    this.stencils = R2SceneStencils.newMasks();

    {
      final R2AmbientOcclusionBufferDescription.Builder b =
        R2AmbientOcclusionBufferDescription.builder();
      b.setArea(AreaInclusiveUnsignedL.of(
        new UnsignedRangeInclusiveL(0L, area.getRangeX().getUpper() / 2L),
        new UnsignedRangeInclusiveL(0L, area.getRangeY().getUpper() / 2L)
      ));

      this.abuffer =
        R2AmbientOcclusionBuffer.newAmbientOcclusionBuffer(
          gx.getFramebuffers(),
          gx.getTextures(),
          this.main.getTextureUnitAllocator().getRootContext(),
          b.build());
    }

    {
      final R2GeometryBufferDescription.Builder b =
        R2GeometryBufferDescription.builder();
      b.setArea(area);
      b.setComponents(
        R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_NO_SPECULAR);

      this.gbuffer = R2GeometryBuffer.newGeometryBuffer(
        gx.getFramebuffers(),
        gx.getTextures(),
        m.getTextureUnitAllocator().getRootContext(),
        b.build());
    }

    {
      final R2LightBufferDescription.Builder b =
        R2LightBufferDescription.builder();
      b.setArea(area);

      this.lbuffer = R2LightBuffers.newLightBuffer(
        gx.getFramebuffers(),
        gx.getTextures(),
        m.getTextureUnitAllocator().getRootContext(),
        b.build());
    }

    {
      final R2ImageBufferDescription.Builder b =
        R2ImageBufferDescription.builder();
      b.setArea(area);
      b.setDepthAttachment(
        R2DepthAttachmentShare.of(this.gbuffer.depthTexture()));

      this.ibuffer = R2ImageBuffer.newImageBuffer(
        gx.getFramebuffers(),
        gx.getTextures(),
        m.getTextureUnitAllocator().getRootContext(),
        b.build());
    }

    {
      final R2FilterCompositorParameters.Builder b =
        R2FilterCompositorParameters.builder();

      long x = 20L;
      long y = 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.gbuffer.albedoEmissiveTexture(),
        AreaInclusiveUnsignedL.of(
          new UnsignedRangeInclusiveL(x, x + 128L),
          new UnsignedRangeInclusiveL(y, y + 96L)),
        1.0f,
        Optional.empty()));

      x += 128L + 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.gbuffer.normalTexture(),
        AreaInclusiveUnsignedL.of(
          new UnsignedRangeInclusiveL(x, x + 128L),
          new UnsignedRangeInclusiveL(y, y + 96L)),
        1.0f,
        Optional.empty()));

      x += 128L + 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.gbuffer.getSpecularTextureOrDefault(m.getTextureDefaults()),
        AreaInclusiveUnsignedL.of(
          new UnsignedRangeInclusiveL(x, x + 128L),
          new UnsignedRangeInclusiveL(y, y + 96L)),
        1.0f,
        Optional.empty()));

      x += 128L + 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.gbuffer.depthTexture(),
        AreaInclusiveUnsignedL.of(
          new UnsignedRangeInclusiveL(x, x + 128L),
          new UnsignedRangeInclusiveL(y, y + 96L)),
        1.0f,
        Optional.empty()));

      x = 20L;
      y = 20L + 96L + 20L;

      {
        final R2LightBufferDiffuseSpecularUsableType lbb =
          (R2LightBufferDiffuseSpecularUsableType) this.lbuffer;

        b.addItems(R2FilterCompositorItem.of(
          lbb.diffuseTexture(),
          AreaInclusiveUnsignedL.of(
            new UnsignedRangeInclusiveL(x, x + 128L),
            new UnsignedRangeInclusiveL(y, y + 96L)),
          1.0f,
          Optional.empty()));

        x += 128L + 20L;

        b.addItems(R2FilterCompositorItem.of(
          lbb.specularTexture(),
          AreaInclusiveUnsignedL.of(
            new UnsignedRangeInclusiveL(x, x + 128L),
            new UnsignedRangeInclusiveL(y, y + 96L)),
          1.0f,
          Optional.empty()));
      }

      x += 128L + 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.abuffer.ambientOcclusionTexture(),
        AreaInclusiveUnsignedL.of(
          new UnsignedRangeInclusiveL(x, x + 128L),
          new UnsignedRangeInclusiveL(y, y + 96L)),
        1.0f,
        Optional.empty()));

      this.filter_comp_parameters = b.build();
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
          gx.getFramebuffers(),
          gx.getTextures(),
          this.main.getTextureUnitAllocator().getRootContext(),
          b.build());
    }

    this.ssao_kernel =
      R2SSAOKernel.newKernel(64);

    this.ssao_noise_texture =
      R2SSAONoiseTexture.newNoiseTexture(
        gx.getTextures(),
        this.main.getTextureUnitAllocator().getRootContext());

    this.ssao_filter = R2FilterSSAO.newFilter(
      m.getShaderPreprocessingEnvironment(),
      gx,
      m.getTextureUnitAllocator().getRootContext(),
      m.getIDPool(),
      m.getUnitQuad());

    {
      final UnsignedRangeInclusiveL screen_x = area.getRangeX();
      final UnsignedRangeInclusiveL screen_y = area.getRangeY();
      final long one = screen_x.getInterval() * screen_y.getInterval();
      final long soft = one * 2L;
      final long hard = one * 10L;
      this.ssao_pool = R2AmbientOcclusionBufferPool.newPool(gx, soft, hard);
    }

    this.ssao_filter_blur =
      R2FilterBilateralBlurDepthAware.newFilter(
        m.getShaderPreprocessingEnvironment(),
        gx,
        m.getTextureDefaults(),
        this.ssao_pool,
        m.getIDPool(),
        m.getUnitQuad());

    this.ssao_applicator =
      R2FilterOcclusionApplicator.newFilter(
        m.getShaderPreprocessingEnvironment(),
        m.getTextureDefaults(),
        gx,
        m.getIDPool(),
        m.getUnitQuad());

    this.filter_compositor =
      R2FilterCompositor.newFilter(
        m.getShaderPreprocessingEnvironment(),
        m.getTextureDefaults(),
        gx,
        m.getIDPool(),
        m.getUnitQuad());

    this.filter_fxaa =
      R2FilterFXAA.newFilter(
        gx,
        m.getShaderPreprocessingEnvironment(),
        m.getIDPool(),
        m.getUnitQuad());

    this.filter_fxaa_params =
      R2FilterFXAAParameters.builder()
        .setSubPixelAliasingRemoval(0.0f)
        .setEdgeThreshold(0.333f)
        .setEdgeThresholdMinimum(0.0833f)
        .setQuality(R2FilterFXAAQuality.R2_FXAA_QUALITY_10)
        .setTexture(this.ibuffer.imageTexture())
        .build();

    this.projection = R2ProjectionFOV.newFrustumWith(
      m.getProjectionMatrices(),
      (float) Math.toRadians(90.0f), 640.0f / 480.0f, 0.001f, 1000.0f);

    final R2IDPoolType id_pool = m.getIDPool();
    final JCGLArrayObjectType mesh = serv.getMesh("halls_complex.r2z");

    final R2TransformSOT transform = R2TransformSOT.newTransform();
    transform.getTranslation().set3F(0.0f, -1.0f, 0.0f);

    this.instance =
      R2InstanceSingle.of(
        id_pool.freshID(), mesh, transform, PMatrixI3x3F.identity());

    final int width = 8;
    final int height = 8;
    final int depth = 8;
    final int instance_count = width * height * depth;
    this.batched_instance =
      R2InstanceBatchedDynamic.newBatch(
        id_pool,
        gx.getArrayBuffers(),
        gx.getArrayObjects(),
        sphere.arrayObject(),
        instance_count);

    final R2TransformSOT[] batched_transforms = new R2TransformSOT[instance_count];

    int index = 0;
    for (int x = 0; x < width; ++x) {
      for (int y = 0; y < height; ++y) {
        for (int z = 0; z < depth; ++z) {
          final R2TransformSOT t = R2TransformSOT.newTransform();
          t.setScale(0.2f);
          final PVector3FType<R2SpaceWorldType> tr = t.getTranslation();
          final float fx = x - (width / 2);
          final float fy = y - (height / 2);
          final float fz = -z * 1.5f;
          tr.set3F(fx, fy, fz);
          batched_transforms[index] = t;
          this.batched_instance.enableInstance(batched_transforms[index]);
          ++index;
        }
      }
    }

    final R2ShaderPreprocessingEnvironmentType sources =
      this.main.getShaderPreprocessingEnvironment();

    this.billboarded_shader =
      R2SurfaceShaderBasicBillboarded.newShader(
        gx.getShaders(), sources, id_pool);

    final R2SurfaceShaderBasicParameters billboarded_shader_params =
      R2SurfaceShaderBasicParameters.builder()
        .setTextureDefaults(this.main.getTextureDefaults())
        .setSpecularColor(new PVectorI3F<>(1.0f, 1.0f, 1.0f))
        .setEmission(1.0f)
        .setAlbedoColor(new PVectorI4F<>(1.0f, 1.0f, 1.0f, 1.0f))
        .build();

    this.billboarded_material =
      R2MaterialOpaqueBillboarded.of(
        id_pool.freshID(), this.billboarded_shader, billboarded_shader_params);

    this.billboarded_instance =
      R2InstanceBillboardedDynamic.newBillboarded(
        id_pool,
        gx.getArrayBuffers(),
        gx.getArrayObjects(),
        100);

    {
      for (int particle = 0; particle < 100; ++particle) {
        final float x = (float) ((Math.random() * 8.0f) - 4.0f);
        final float y = (float) (Math.random() * 5.0f);
        final float z = (float) ((Math.random() * 8.0f) - 4.0f);
        this.billboarded_instance.addInstance(
          new PVectorI3F<>(x, y, z), 0.25f, 0.0f);
      }
    }

    final R2ShaderDepthSingleType<R2DepthShaderBasicParameters> depth_shader = R2DepthShaderBasicSingle.newShader(
      gx.getShaders(), m.getShaderPreprocessingEnvironment(), m.getIDPool());
    final R2DepthShaderBasicParameters depth_params = R2DepthShaderBasicParameters.of(
      m.getTextureDefaults(), m.getTextureDefaults().texture2DWhite(), 0.1f);
    this.depth_material = R2MaterialDepthSingle.of(
      id_pool.freshID(), depth_shader, depth_params);

    {
      this.geom_shader =
        R2SurfaceShaderBasicSingle.newShader(gx.getShaders(), sources, id_pool);

      final R2SurfaceShaderBasicParameters gs =
        R2SurfaceShaderBasicParameters.builder()
          .setTextureDefaults(m.getTextureDefaults())
          .setSpecularColor(new PVectorI3F<>(1.0f, 1.0f, 1.0f))
          .setSpecularExponent(64.0f)
          .setAlbedoTexture(serv.getTexture2D("halls_complex_albedo.png"))
          .setAlbedoMix(1.0f)
          .setNormalTexture(serv.getTexture2D("halls_complex_normal.png"))
          .build();

      this.geom_material = R2MaterialOpaqueSingle.of(
        id_pool.freshID(), this.geom_shader, gs);
    }

    {
      this.golden =
        R2InstanceSingle.of(
          id_pool.freshID(),
          m.getUnitQuad().arrayObject(),
          transform,
          PMatrixI3x3F.identity());

      final R2SurfaceShaderBasicParameters gs =
        R2SurfaceShaderBasicParameters.builder()
          .setTextureDefaults(m.getTextureDefaults())
          .setAlbedoColor(new PVectorI4F<>(0.0f, 0.0f, 0.0f, 0.0f))
          .setAlbedoTexture(serv.getTexture2D("golden_albedo.png"))
          .setAlbedoMix(1.0f)
          .setAlphaDiscardThreshold(0.1f)
          .build();

      this.golden_material = R2MaterialOpaqueSingle.of(
        id_pool.freshID(), this.geom_shader, gs);

      final R2DepthShaderBasicParameters golden_depth_params = R2DepthShaderBasicParameters.of(
        m.getTextureDefaults(), gs.albedoTexture(), 0.1f);
      this.golden_depth_material = R2MaterialDepthSingle.of(
        id_pool.freshID(), depth_shader, golden_depth_params);
    }

    {
      final R2TransformSOT glow_transform = R2TransformSOT.newTransform();
      glow_transform.getTranslation().set3F(-2.0f, 1.0f, 0.0f);

      this.glow =
        R2InstanceSingle.of(
          id_pool.freshID(),
          m.getUnitQuad().arrayObject(),
          glow_transform,
          PMatrixI3x3F.identity());

      final R2SurfaceShaderBasicParameters gs =
        R2SurfaceShaderBasicParameters.builder()
          .setTextureDefaults(m.getTextureDefaults())
          .setAlbedoColor(new PVectorI4F<>(1.0f, 1.0f, 1.0f, 1.0f))
          .setAlbedoMix(0.0f)
          .setAlphaDiscardThreshold(0.0f)
          .setEmission(1.0f)
          .build();

      this.glow_material = R2MaterialOpaqueSingle.of(
        id_pool.freshID(), this.geom_shader, gs);
    }

    final R2ShaderInstanceBatchedType<R2SurfaceShaderBasicParameters> batched_geom_shader =
      R2SurfaceShaderBasicBatched.newShader(
        gx.getShaders(),
        sources,
        id_pool);
    this.batched_geom_material = R2MaterialOpaqueBatched.of(
      id_pool.freshID(),
      batched_geom_shader,
      this.geom_material.shaderParameters());

    this.light_ambient_shader =
      R2LightShaderAmbientSingle.newShader(gx.getShaders(), sources, id_pool);
    this.light_ambient =
      R2LightAmbientScreenSingle.newLight(
        m.getUnitQuad(), id_pool, m.getTextureDefaults());
    this.light_ambient.setIntensity(0.15f);
    this.light_ambient.colorWritable().set3F(0.0f, 1.0f, 1.0f);

    this.proj_light_shader =
      R2LightShaderProjectiveLambertShadowVarianceSingle.newShader(
        gx.getShaders(), sources, id_pool);
    this.proj_proj =
      R2ProjectionFrustum.newFrustumWith(
        JCGLProjectionMatrices.newMatrices(),
        -0.5f, 0.5f, -0.5f, 0.5f, 1.0f, 10.0f);
    this.proj_mesh =
      R2ProjectionMesh.newMesh(
        gx,
        this.proj_proj,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW);

    final R2ShadowDepthVariance proj_shadow;
    {
      final AreaInclusiveUnsignedLType shadow_area = AreaInclusiveUnsignedL.of(
        new UnsignedRangeInclusiveL(0L, 255L),
        new UnsignedRangeInclusiveL(0L, 255L));

      proj_shadow =
        R2ShadowDepthVariance.of(
          m.getIDPool().freshID(),
          R2DepthVarianceBufferDescription.of(
            shadow_area,
            JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR,
            JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR_MIPMAP_LINEAR,
            R2DepthPrecision.R2_DEPTH_PRECISION_16,
            R2DepthVariancePrecision.R2_DEPTH_VARIANCE_PRECISION_16));
    }

    this.proj_light =
      R2LightProjectiveWithShadowVariance.newLight(
        this.proj_mesh,
        m.getTextureDefaults().texture2DProjectiveWhite(),
        proj_shadow,
        m.getIDPool());
    this.proj_light.setRadius(10.0f);
    this.proj_light.colorWritable().set3F(1.0f, 1.0f, 1.0f);
    this.proj_light.transformWritable().translation()
      .set3F(0.0f, 0.0f, 3.0f);

    this.proj_shadow_instances = R2DepthInstances.newDepthInstances();

    this.sphere_light_shader =
      R2LightShaderSphericalLambertBlinnPhongSingle.newShader(
        gx.getShaders(), sources, id_pool);

    this.sphere_light =
      R2LightSphericalSingle.newLight(sphere, id_pool);
    this.sphere_light.colorWritable().set3F(1.0f, 1.0f, 1.0f);
    this.sphere_light.setIntensity(1.0f);
    this.sphere_light.originPositionWritable().set3F(0.0f, 1.0f, 1.0f);
    this.sphere_light.setRadius(30.0f);
    this.sphere_light.setGeometryScaleFactor(
      (float) R2UnitSphere.getUVSphereApproximationScaleFactor(30.0f, 8));

    final R2TransformSiOT sphere_light_bounded_transform = R2TransformSiOT.newTransform();
    sphere_light_bounded_transform.getTranslation()
      .set3F(-10.0f, 1.0f, 0.0f);
    sphere_light_bounded_transform.getScale().set3F(9.0f, 9.0f, 9.0f);

    this.sphere_light_bounds =
      R2InstanceSingle.of(
        id_pool.freshID(),
        R2UnitCube.newUnitCube(gx).arrayObject(),
        sphere_light_bounded_transform,
        PMatrixI3x3F.identity());
    this.sphere_light_bounded =
      R2LightSphericalSingle.newLight(sphere, id_pool);
    this.sphere_light_bounded.colorWritable().set3F(1.0f, 0.0f, 0.0f);
    this.sphere_light_bounded.setIntensity(1.0f);
    this.sphere_light_bounded.originPositionWritable()
      .set3F(-10.0f, 1.0f, 0.0f);
    this.sphere_light_bounded.setRadius(9.0f);

    this.filter_light =
      R2FilterLightApplicator.newFilter(sources, gx, id_pool, m.getUnitQuad());

    {
      final R2DebugCubeType debug_cube = R2DebugCube.newDebugCube(gx);

      final R2DebugInstances.Builder ib = R2DebugInstances.builder();

      for (int y = 0; y < 100; ++y) {
        ib.addLineSegments(R2DebugLineSegment.of(
          new PVectorI3F<>(-20.0f, y, 0.0f),
          new PVectorI4F<>(1.0f, 0.0f, 1.0f, 1.0f),
          new PVectorI3F<>(20.0f, y, 0.0f),
          new PVectorI4F<>(0.0f, 1.0f, 1.0f, 1.0f)));
      }

      for (int x = 0; x < 20; x += 2) {
        final R2TransformT t = R2TransformT.newTransform();
        t.getTranslation().set3F((float) x, 1.0f, 1.0f);
        ib.addCubes(R2DebugCubeInstance.of(
          t, new PVectorI4F<>(0.0f, 1.0f, 0.0f, 1.0f)));
      }

      ib.addInstanceSingles(R2DebugInstanceSingle.of(
        this.instance,
        new PVectorI4F<>(1.0f, 0.0f, 1.0f, 1.0f)));

      this.debug_params = R2DebugVisualizerRendererParameters.builder()
        .setOpaqueInstances(this.opaques)
        .setShowOpaqueInstances(true)
        .setShowLights(true)
        .setLights(this.lights)
        .setUnitSphere(sphere)
        .setDebugCube(debug_cube)
        .setDebugInstances(ib.build())
        .build();
    }

    {
      final R2FilterLightApplicatorParameters.Builder pb =
        R2FilterLightApplicatorParameters.builder();
      pb.setGeometryBuffer(this.gbuffer);
      pb.setOutputViewport(this.ibuffer.area());
      pb.setCopyDepth(R2CopyDepth.R2_COPY_DEPTH_ENABLED);
      this.lbuffer.matchLightBuffer(
        this,
        (tt, lbdo) -> {
          final R2TextureDefaultsType td = tt.main.getTextureDefaults();
          pb.setLightDiffuseTexture(lbdo.diffuseTexture());
          pb.setLightSpecularTexture(td.texture2DBlack());
          return Unit.unit();
        }, (tt, lbso) -> {
          final R2TextureDefaultsType td = tt.main.getTextureDefaults();
          pb.setLightDiffuseTexture(td.texture2DBlack());
          pb.setLightSpecularTexture(lbso.specularTexture());
          return Unit.unit();
        }, (tt, lb) -> {
          pb.setLightDiffuseTexture(lb.diffuseTexture());
          pb.setLightSpecularTexture(lb.specularTexture());
          return Unit.unit();
        });
      this.filter_light_params = pb.build();
    }

    {
      final R2RenderTargetPoolType<R2ImageBufferDescriptionType, R2ImageBufferUsableType> image_pool =
        R2ImageBufferPool.newPool(
          gx, 1024L * 768L * 4L, Long.MAX_VALUE);

      final R2FilterType<R2FilterBoxBlurParameters<R2ImageBufferDescriptionType, R2ImageBufferUsableType, R2ImageBufferDescriptionType, R2ImageBufferUsableType>> filter_blur = R2FilterBoxBlur.newFilter(
        m.getShaderPreprocessingEnvironment(),
        gx,
        m.getTextureDefaults(),
        image_pool,
        m.getIDPool(),
        m.getUnitQuad());

      this.filter_emission_params =
        R2FilterEmissionParameters.builder()
          .setAlbedoEmissionMap(this.gbuffer.albedoEmissiveTexture())
          .setBlurParameters(R2BlurParameters.builder().build())
          .setOutputFramebuffer(this.ibuffer.primaryFramebuffer())
          .setOutputViewport(this.ibuffer.area())
          .setScale(0.25f)
          .build();

      this.filter_emission = R2FilterEmission.newFilter(
        gx,
        m.getShaderPreprocessingEnvironment(),
        m.getIDPool(),
        filter_blur,
        image_pool,
        m.getUnitQuad());
    }

    {
      final JCGLClearSpecification.Builder csb =
        JCGLClearSpecification.builder();
      csb.setStencilBufferClear(0);
      csb.setDepthBufferClear(1.0);
      csb.setColorBufferClear(new VectorI4F(0.0f, 0.0f, 0.0f, 0.0f));
      this.screen_clear_spec = csb.build();
    }

    this.profiling_window = new AtomicReference<>();
    SwingUtilities.invokeLater(() -> {
      final ExampleProfilingWindow frame = new ExampleProfilingWindow();
      frame.setVisible(true);
      this.profiling_window.set(frame);
    });

    this.text_buffer = new StringBuilder(256);
    this.text = "";
  }

  @Override
  public void onRender(
    final R2ExampleServicesType servx,
    final JCGLInterfaceGL33Type gx,
    final AreaInclusiveUnsignedLType areax,
    final R2MainType mx,
    final int frame)
  {
    this.g = gx;

    this.proj_proj.projectionSetXMaximum(
      (float) Math.abs(Math.sin(frame * 0.01) * 2.0f));
    this.proj_proj.projectionSetXMinimum(
      (float) -Math.abs(Math.sin(frame * 0.01) * 2.0f));
    this.proj_mesh.updateProjection(gx.getArrayBuffers());

    this.stencils.stencilsReset();
    this.stencils.stencilsSetMode(
      R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_NEGATIVE);

    this.proj_shadow_instances.depthsReset();
    this.proj_shadow_instances.depthsAddSingleInstance(
      this.instance, this.depth_material);
    this.proj_shadow_instances.depthsAddSingleInstance(
      this.golden, this.golden_depth_material);

    this.opaques.opaquesReset();
    this.opaques.opaquesAddSingleInstance(
      this.instance, this.geom_material);
    this.opaques.opaquesAddSingleInstance(
      this.golden, this.golden_material);
    this.opaques.opaquesAddSingleInstance(
      this.glow, this.glow_material);
    this.opaques.opaquesAddBatchedInstance(
      this.batched_instance, this.batched_geom_material);
    this.opaques.opaquesAddBillboardedInstance(
      this.billboarded_instance, this.billboarded_material);

    this.lights.lightsReset();

    final R2SceneLightsGroupType lg = this.lights.lightsGetGroup(1);
    lg.lightGroupAddSingle(
      this.light_ambient, this.light_ambient_shader);
    lg.lightGroupAddSingle(
      this.sphere_light, this.sphere_light_shader);
    lg.lightGroupAddSingle(
      this.proj_light, this.proj_light_shader);

    final R2SceneLightsClipGroupType lcg =
      lg.lightGroupNewClipGroup(this.sphere_light_bounds);
    lcg.clipGroupAddSingle(
      this.sphere_light_bounded, this.sphere_light_shader);

    if (servx.isFreeCameraEnabled()) {
      MatrixM4x4F.copy(servx.getFreeCameraViewMatrix(), this.view);
    } else {
      mx.getViewMatrices().lookAt(
        this.view,
        new VectorI3F(0.0f, 0.0f, 5.0f),
        new VectorI3F(0.0f, 0.0f, 0.0f),
        new VectorI3F(0.0f, 1.0f, 0.0f));
    }

    {
      final R2MatricesType matrices = mx.getMatrices();

      final JCGLProfilingType pro = this.main.getProfiling();
      pro.setEnabled(true);
      final JCGLProfilingFrameType profiling_frame = pro.startFrame();
      this.profiling_root = profiling_frame.getChildContext("main");

      final R2ShadowMapRendererExecutionType sme =
        this.main.getShadowMapRenderer().shadowBegin();

      sme.shadowExecRenderLight(
        this.profiling_root,
        this.main.getTextureUnitAllocator().getRootContext(),
        matrices,
        this.proj_light,
        this.proj_shadow_instances);

      this.shadow_context = sme.shadowExecComplete();

      matrices.withObserver(this.view, this.projection, this, (mo, t) -> {
        final JCGLTextureUnitContextParentType uc =
          t.main.getTextureUnitAllocator().getRootContext();
        final JCGLFramebufferUsableType gbuffer_fb =
          t.gbuffer.primaryFramebuffer();
        final JCGLFramebufferUsableType lbuffer_fb =
          t.lbuffer.primaryFramebuffer();

        final JCGLFramebuffersType g_fb = t.g.getFramebuffers();
        final JCGLClearType g_cl = t.g.getClear();
        final JCGLColorBufferMaskingType g_cb = t.g.getColorBufferMasking();
        final JCGLStencilBuffersType g_sb = t.g.getStencilBuffers();
        final JCGLDepthBuffersType g_db = t.g.getDepthBuffers();

        /*
         * Populate geometry buffer.
         */

        g_fb.framebufferDrawBind(gbuffer_fb);
        t.gbuffer.clearBoundPrimaryFramebuffer(t.g);
        t.main.getStencilRenderer().renderStencilsWithBoundBuffer(
          mo,
          t.profiling_root,
          t.main.getTextureUnitAllocator().getRootContext(),
          t.gbuffer.area(),
          t.stencils);
        t.main.getGeometryRenderer().renderGeometry(
          t.gbuffer.area(),
          Optional.empty(),
          t.profiling_root,
          uc,
          mo,
          t.opaques);

        /*
         * Evaluate and blur ambient occlusion.
         */

        t.ssao_filter_params =
          R2FilterSSAOParameters.builder()
            .setKernel(t.ssao_kernel)
            .setExponent(1.0f)
            .setSampleRadius(1.0f)
            .setGeometryBuffer(t.gbuffer)
            .setNoiseTexture(t.ssao_noise_texture)
            .setOutputBuffer(t.ssao_buffer)
            .setSceneObserverValues(mo)
            .build();

        t.ssao_filter.runFilter(t.profiling_root, uc, t.ssao_filter_params);

        final R2BilateralBlurParameters blur =
          R2BilateralBlurParameters.builder()
            .setBlurPasses(1)
            .setBlurSize(2.0f)
            .setBlurScale(1.0f)
            .setBlurSharpness(4.0f)
            .build();

        t.ssao_filter_blur_params =
          R2FilterBilateralBlurDepthAwareParameters.of(
            t.ssao_buffer,
            R2AmbientOcclusionBufferUsableType::ambientOcclusionTexture,
            t.ssao_buffer,
            R2AmbientOcclusionBufferUsableType::ambientOcclusionTexture,
            t.ssao_pool,
            (d, a) -> {
              final R2AmbientOcclusionBufferDescription.Builder b =
                R2AmbientOcclusionBufferDescription.builder();
              b.from(d);
              b.setArea(a);
              return b.build();
            },
            mo,
            t.gbuffer.depthTexture(),
            blur
          );

        t.ssao_filter_blur.runFilter(
          t.profiling_root,
          uc,
          t.ssao_filter_blur_params);

        /*
         * Populate light buffer.
         */

        g_fb.framebufferDrawBind(lbuffer_fb);
        t.lbuffer.clearBoundPrimaryFramebuffer(t.g);
        t.main.getLightRenderer().renderLights(
          t.gbuffer,
          t.lbuffer.area(),
          Optional.empty(),
          t.profiling_root,
          uc,
          t.shadow_context,
          mo,
          t.lights);

        /*
         * Apply ambient occlusion to light buffer.
         */


        final R2FilterOcclusionApplicatorParameters ssao_app_params =
          R2FilterOcclusionApplicatorParameters.builder()
            .setIntensity(0.3f)
            .setOcclusionTexture(t.abuffer.ambientOcclusionTexture())
            .setOutputLightBuffer(t.lbuffer)
            .build();
        t.ssao_applicator.runFilter(t.profiling_root, uc, ssao_app_params);

        /*
         * Combine light and geometry buffers into lit image.
         */

        g_fb.framebufferDrawBind(t.ibuffer.primaryFramebuffer());
        t.ibuffer.clearBoundPrimaryFramebuffer(t.g);
        t.filter_light.runFilter(t.profiling_root, uc, t.filter_light_params);

        /*
         * Apply emission.
         */

        t.filter_emission.runFilter(
          t.profiling_root, uc, t.filter_emission_params);

        /*
         * Render debug geometry.
         */

        t.main.getDebugVisualizerRenderer().renderScene(
          areax,
          t.profiling_root,
          uc,
          mo,
          t.debug_params);

        /*
         * Filter the lit image with FXAA, writing it to the screen.
         */

        g_fb.framebufferDrawUnbind();
        g_cb.colorBufferMask(true, true, true, true);
        g_db.depthBufferWriteEnable();
        g_sb.stencilBufferMask(
          JCGLFaceSelection.FACE_FRONT_AND_BACK, 0b11111111);
        g_cl.clear(t.screen_clear_spec);

        t.filter_fxaa.runFilter(
          t.profiling_root, uc, t.filter_fxaa_params);

        t.filter_compositor.runFilter(
          t.profiling_root, uc, t.filter_comp_parameters);
        return Unit.unit();
      });

      this.shadow_context.shadowMapContextFinish();

      final JCGLProfilingFrameMeasurementType pro_measure =
        pro.getMostRecentlyMeasuredFrame();

      if (frame % 60 == 0) {
        this.text_buffer.setLength(0);
        pro_measure.iterate(this, (tt, depth, fm) -> {
          final double nanos = fm.getElapsedTimeTotal();
          final double millis = nanos / 1_000_000.0;

          for (int index = 0; index < depth; ++index) {
            tt.text_buffer.append("    ");
          }
          tt.text_buffer.append(fm.getName());
          tt.text_buffer.append(" ");
          tt.text_buffer.append(String.format("%.6f", Double.valueOf(millis)));
          tt.text_buffer.append("ms");
          tt.text_buffer.append(System.lineSeparator());
          return JCGLProfilingIteration.CONTINUE;
        });
        this.text = this.text_buffer.toString();
        this.text_buffer.setLength(0);

        SwingUtilities.invokeLater(
          () -> this.profiling_window.get().sendText(this.text));
      }
    }
  }

  @Override
  public void onFinish(
    final JCGLInterfaceGL33Type gx,
    final R2MainType m)
  {
    this.geom_shader.delete(gx);
  }
}
