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

import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLClearSpecification;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.JCGLViewMatrices;
import com.io7m.jcanephora.core.api.JCGLClearType;
import com.io7m.jcanephora.core.api.JCGLColorBufferMaskingType;
import com.io7m.jcanephora.core.api.JCGLDepthBuffersType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLStencilBuffersType;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jregions.core.unparameterized.areas.AreasL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors3D;
import com.io7m.r2.core.R2AmbientOcclusionBufferDescription;
import com.io7m.r2.core.R2AmbientOcclusionBufferType;
import com.io7m.r2.core.R2AmbientOcclusionBufferUsableType;
import com.io7m.r2.core.R2DepthAttachmentShare;
import com.io7m.r2.core.R2DepthInstances;
import com.io7m.r2.core.R2DepthInstancesType;
import com.io7m.r2.core.R2DepthPrecision;
import com.io7m.r2.core.R2DepthVarianceBufferDescription;
import com.io7m.r2.core.R2DepthVariancePrecision;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2GeometryBufferDescription;
import com.io7m.r2.core.R2GeometryBufferType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2ImageBufferDescription;
import com.io7m.r2.core.R2ImageBufferType;
import com.io7m.r2.core.R2ImageBufferUsableType;
import com.io7m.r2.core.R2InstanceBatchedDynamic;
import com.io7m.r2.core.R2InstanceBillboardedDynamicType;
import com.io7m.r2.core.R2InstanceSingle;
import com.io7m.r2.core.R2LightAmbientScreenSingle;
import com.io7m.r2.core.R2LightBufferDescription;
import com.io7m.r2.core.R2LightBufferDiffuseSpecularUsableType;
import com.io7m.r2.core.R2LightBufferType;
import com.io7m.r2.core.R2LightProjectiveWithShadowVarianceType;
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
import com.io7m.r2.core.R2TransformSOT;
import com.io7m.r2.core.R2TransformSiOT;
import com.io7m.r2.core.R2TransformT;
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
import com.io7m.r2.examples.ExampleProfilingWindow;
import com.io7m.r2.examples.R2ExampleCustomType;
import com.io7m.r2.examples.R2ExampleServicesType;
import com.io7m.r2.facade.R2FacadeBufferProviderType;
import com.io7m.r2.facade.R2FacadeType;
import com.io7m.r2.filters.R2BilateralBlurParameters;
import com.io7m.r2.filters.R2BlurParameters;
import com.io7m.r2.filters.R2FilterBilateralBlurDepthAwareParameters;
import com.io7m.r2.filters.R2FilterBoxBlurParameters;
import com.io7m.r2.filters.R2FilterCompositorItem;
import com.io7m.r2.filters.R2FilterCompositorParameters;
import com.io7m.r2.filters.R2FilterEmissionParameters;
import com.io7m.r2.filters.R2FilterFXAAParameters;
import com.io7m.r2.filters.R2FilterLightApplicator;
import com.io7m.r2.filters.R2FilterLightApplicatorParameters;
import com.io7m.r2.filters.R2FilterOcclusionApplicatorParameters;
import com.io7m.r2.filters.R2FilterSSAOParameters;
import com.io7m.r2.filters.R2SSAOKernel;
import com.io7m.r2.filters.R2SSAOKernelType;
import com.io7m.r2.meshes.defaults.R2UnitSphere;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLFaceSelection.FACE_FRONT_AND_BACK;
import static com.io7m.r2.core.R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL;
import static com.io7m.r2.core.R2LightBufferComponents.R2_LIGHT_BUFFER_DIFFUSE_AND_SPECULAR;
import static com.io7m.r2.filters.R2FilterFXAAQuality.R2_FXAA_QUALITY_10;

// CHECKSTYLE:OFF

public final class ExampleLightSpherical4Profiled implements R2ExampleCustomType
{
  private JCGLClearSpecification screen_clear_spec;
  private R2SceneStencilsType stencils;
  private R2ProjectionFOV projection;
  private R2InstanceSingle instance;
  private R2SceneOpaquesType opaques;
  private R2SceneLightsType lights;
  private R2GeometryBufferType gbuffer;
  private R2LightBufferType lbuffer;
  private R2ImageBufferType ibuffer;
  private R2ShaderInstanceSingleType<R2SurfaceShaderBasicParameters> geom_shader;
  private R2MaterialOpaqueSingleType<R2SurfaceShaderBasicParameters> geom_material;
  private R2ShaderLightVolumeSingleType<R2LightSphericalSingleReadableType> sphere_light_shader;
  private R2LightSphericalSingleType sphere_light;
  private R2LightSphericalSingleType sphere_light_bounded;
  private R2InstanceSingle sphere_light_bounds;
  private R2ShaderLightProjectiveWithShadowType<R2LightProjectiveWithShadowVarianceType> proj_light_shader;
  private R2ProjectionFrustum proj_proj;
  private R2ProjectionMeshType proj_mesh;
  private R2LightProjectiveWithShadowVarianceType proj_light;
  private R2DepthInstancesType proj_shadow_instances;
  private R2MaterialDepthSingleType<R2DepthShaderBasicParameters> depth_material;
  private R2InstanceSingle golden;
  private R2MaterialOpaqueSingleType<R2SurfaceShaderBasicParameters> golden_material;
  private R2MaterialDepthSingleType<R2DepthShaderBasicParameters> golden_depth_material;
  private R2InstanceSingle glow;
  private R2MaterialOpaqueSingleType<R2SurfaceShaderBasicParameters> glow_material;
  private R2InstanceBatchedDynamic batched_instance;
  private R2MaterialOpaqueBatchedType<R2SurfaceShaderBasicParameters> batched_geom_material;
  private R2FilterLightApplicator filter_light;
  private R2FilterLightApplicatorParameters filter_light_params;
  private R2FilterType<R2FilterFXAAParameters> filter_fxaa;
  private R2FilterFXAAParameters filter_fxaa_params;
  private R2FilterType<R2FilterCompositorParameters> filter_compositor;
  private R2FilterCompositorParameters filter_comp_parameters;
  private R2FacadeType main;
  private R2AmbientOcclusionBufferType ssao_buffer;
  private R2RenderTargetPoolUsableType<
    R2AmbientOcclusionBufferDescription,
    R2AmbientOcclusionBufferUsableType> ssao_pool;
  private R2FilterType<R2FilterBilateralBlurDepthAwareParameters<
    R2AmbientOcclusionBufferDescription,
    R2AmbientOcclusionBufferUsableType,
    R2AmbientOcclusionBufferDescription,
    R2AmbientOcclusionBufferUsableType>> ssao_filter_blur;
  private R2FilterBilateralBlurDepthAwareParameters<
    R2AmbientOcclusionBufferDescription,
    R2AmbientOcclusionBufferUsableType,
    R2AmbientOcclusionBufferDescription,
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
  private ExampleProfilingWindow profiling_window;
  private JCGLProfilingContextType profiling_root;
  private R2FilterType<R2FilterEmissionParameters> filter_emission;
  private R2FilterEmissionParameters filter_emission_params;
  private R2ShaderInstanceBillboardedType<R2SurfaceShaderBasicParameters> billboarded_shader;
  private R2MaterialOpaqueBillboarded<R2SurfaceShaderBasicParameters> billboarded_material;
  private R2InstanceBillboardedDynamicType billboarded_instance;

  public ExampleLightSpherical4Profiled()
  {

  }

  @Override
  public void onInitialize(
    final R2ExampleServicesType serv,
    final JCGLInterfaceGL33Type gx,
    final AreaSizeL area,
    final R2FacadeType m)
  {
    this.main = NullCheck.notNull(m, "Main");

    this.opaques = R2SceneOpaques.create();
    this.lights = R2SceneLights.create();
    this.stencils = R2SceneStencils.create();

    final R2FacadeBufferProviderType buffers = m.buffers();
    this.gbuffer =
      buffers.createGeometryBuffer(
        R2GeometryBufferDescription.of(area, R2_GEOMETRY_BUFFER_FULL));
    this.lbuffer =
      buffers.createLightBuffer(
        R2LightBufferDescription.of(
          area, R2_LIGHT_BUFFER_DIFFUSE_AND_SPECULAR));
    this.ibuffer =
      buffers.createImageBuffer(
        R2ImageBufferDescription.of(
          area,
          Optional.of(R2DepthAttachmentShare.of(this.gbuffer.depthTexture()))));

    this.ssao_buffer =
      buffers.createAmbientOcclusionBuffer(
        R2AmbientOcclusionBufferDescription.of(
          AreaSizeL.of(area.width() / 2L, area.height() / 2L)));

    this.ssao_filter =
      m.filters().createSSAO();

    this.ssao_applicator =
      m.filters().createOcclusionApplicator();

    this.ssao_pool =
      m.pools().createAmbientOcclusionPool(
        area.width() * area.height() * 2L, Long.MAX_VALUE);

    this.ssao_kernel =
      R2SSAOKernel.newKernel(64);

    this.ssao_noise_texture =
      m.textures().createSSAONoiseTexture();

    this.ssao_filter_blur =
      m.filters().createSSAOBilateralBlur(this.ssao_pool);

    {
      final R2FilterCompositorParameters.Builder b =
        R2FilterCompositorParameters.builder();

      long x = 20L;
      long y = 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.gbuffer.albedoEmissiveTexture(),
        AreasL.create(x, y, 128L, 96L),
        1.0,
        Optional.empty()));

      x += 128L + 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.gbuffer.normalTexture(),
        AreasL.create(x, y, 128L, 96L),
        1.0,
        Optional.empty()));

      x += 128L + 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.gbuffer.specularTextureOrDefault(m.textureDefaults()),
        AreasL.create(x, y, 128L, 96L),
        1.0,
        Optional.empty()));

      x += 128L + 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.gbuffer.depthTexture(),
        AreasL.create(x, y, 128L, 96L),
        1.0,
        Optional.empty()));

      x = 20L;
      y = 20L + 96L + 20L;

      {
        final R2LightBufferDiffuseSpecularUsableType lbb =
          (R2LightBufferDiffuseSpecularUsableType) this.lbuffer;

        b.addItems(R2FilterCompositorItem.of(
          lbb.diffuseTexture(),
          AreasL.create(x, y, 128L, 96L),
          1.0,
          Optional.empty()));

        x += 128L + 20L;

        b.addItems(R2FilterCompositorItem.of(
          lbb.specularTexture(),
          AreasL.create(x, y, 128L, 96L),
          1.0,
          Optional.empty()));
      }

      x += 128L + 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.ssao_buffer.ambientOcclusionTexture(),
        AreasL.create(x, y, 128L, 96L),
        1.0,
        Optional.empty()));

      this.filter_comp_parameters = b.build();
    }

    this.filter_compositor =
      m.filters().createCompositor();

    this.filter_fxaa = m.filters().createFXAA();
    this.filter_fxaa_params =
      R2FilterFXAAParameters.builder()
        .setSubPixelAliasingRemoval(0.0)
        .setEdgeThreshold(0.333)
        .setEdgeThresholdMinimum(0.0833)
        .setQuality(R2_FXAA_QUALITY_10)
        .setTexture(this.ibuffer.imageTexture())
        .build();

    this.projection = R2ProjectionFOV.createWith(
      Math.toRadians(90.0), 640.0 / 480.0, 0.001, 1000.0);

    final R2IDPoolType id_pool = m.idPool();
    final JCGLArrayObjectType mesh = serv.getMesh("halls_complex.r2z");

    final R2TransformSOT transform = R2TransformSOT.create();
    transform.setTranslation(PVector3D.of(0.0, -1.0, 0.0));

    this.instance = m.instances().createSingle(mesh, transform);

    final int width = 8;
    final int height = 8;
    final int depth = 8;
    final int instance_count = width * height * depth;

    this.batched_instance =
      m.instances().createBatchedDynamic(
        m.unitSphere8().arrayObject(),
        instance_count);

    final R2TransformSOT[] batched_transforms = new R2TransformSOT[instance_count];

    int index = 0;
    for (int x = 0; x < width; ++x) {
      for (int y = 0; y < height; ++y) {
        for (int z = 0; z < depth; ++z) {
          final R2TransformSOT t = R2TransformSOT.create();
          final double fx = (double) x - ((double) width / 2.0);
          final double fy = (double) y - ((double) height / 2.0);
          final double fz = (double) -z * 1.5;
          t.setScale(0.2);
          t.setTranslation(PVector3D.of(fx, fy, fz));
          batched_transforms[index] = t;
          this.batched_instance.enableInstance(batched_transforms[index]);
          ++index;
        }
      }
    }

    final R2ShaderPreprocessingEnvironmentType sources =
      this.main.shaderPreprocessingEnvironment();

    this.billboarded_shader =
      R2SurfaceShaderBasicBillboarded.create(
        gx.shaders(), sources, id_pool);

    final R2SurfaceShaderBasicParameters billboarded_shader_params =
      R2SurfaceShaderBasicParameters.builder()
        .setTextureDefaults(this.main.textureDefaults())
        .setSpecularColor(PVector3D.of(1.0, 1.0, 1.0))
        .setEmission(1.0)
        .setAlbedoColor(PVector4D.of(1.0, 1.0, 1.0, 1.0))
        .build();

    this.billboarded_material =
      R2MaterialOpaqueBillboarded.of(
        id_pool.freshID(), this.billboarded_shader, billboarded_shader_params);

    this.billboarded_instance =
      m.instances().createBillboardedDynamic(100);

    {
      for (int particle = 0; particle < 100; ++particle) {
        final double x = (Math.random() * 8.0) - 4.0;
        final double y = Math.random() * 5.0;
        final double z = (Math.random() * 8.0) - 4.0;
        this.billboarded_instance.addInstance(
          PVector3D.of(x, y, z), 0.25, 0.0);
      }
    }

    final R2ShaderDepthSingleType<R2DepthShaderBasicParameters> depth_shader = R2DepthShaderBasicSingle.create(
      gx.shaders(), m.shaderPreprocessingEnvironment(), m.idPool());
    final R2DepthShaderBasicParameters depth_params = R2DepthShaderBasicParameters.of(
      m.textureDefaults(), m.textureDefaults().white2D(), 0.1);
    this.depth_material = R2MaterialDepthSingle.of(
      id_pool.freshID(), depth_shader, depth_params);

    {
      this.geom_shader =
        R2SurfaceShaderBasicSingle.create(gx.shaders(), sources, id_pool);

      final R2SurfaceShaderBasicParameters gs =
        R2SurfaceShaderBasicParameters.builder()
          .setTextureDefaults(m.textureDefaults())
          .setSpecularColor(PVector3D.of(1.0, 1.0, 1.0))
          .setSpecularExponent(64.0)
          .setAlbedoTexture(serv.getTexture2D("halls_complex_albedo.png"))
          .setAlbedoMix(1.0)
          .setNormalTexture(serv.getTexture2D("halls_complex_normal.png"))
          .build();

      this.geom_material = R2MaterialOpaqueSingle.of(
        id_pool.freshID(), this.geom_shader, gs);
    }

    {
      this.golden =
        m.instances().createSingle(m.unitQuad().arrayObject(), transform);

      final R2SurfaceShaderBasicParameters gs =
        R2SurfaceShaderBasicParameters.builder()
          .setTextureDefaults(m.textureDefaults())
          .setAlbedoColor(PVector4D.of(0.0, 0.0, 0.0, 0.0))
          .setAlbedoTexture(serv.getTexture2D("golden_albedo.png"))
          .setAlbedoMix(1.0)
          .setAlphaDiscardThreshold(0.1)
          .build();

      this.golden_material = R2MaterialOpaqueSingle.of(
        id_pool.freshID(), this.geom_shader, gs);

      final R2DepthShaderBasicParameters golden_depth_params = R2DepthShaderBasicParameters.of(
        m.textureDefaults(), gs.albedoTexture(), 0.1);
      this.golden_depth_material = R2MaterialDepthSingle.of(
        id_pool.freshID(), depth_shader, golden_depth_params);
    }

    {
      final R2TransformSOT glow_transform = R2TransformSOT.create();
      glow_transform.setTranslation(PVector3D.of(-2.0, 1.0, 0.0));

      this.glow =
        m.instances().createSingle(m.unitQuad().arrayObject(), glow_transform);

      final R2SurfaceShaderBasicParameters gs =
        R2SurfaceShaderBasicParameters.builder()
          .setTextureDefaults(m.textureDefaults())
          .setAlbedoColor(PVector4D.of(1.0, 1.0, 1.0, 1.0))
          .setAlbedoMix(0.0)
          .setAlphaDiscardThreshold(0.0)
          .setEmission(1.0)
          .build();

      this.glow_material =
        R2MaterialOpaqueSingle.of(id_pool.freshID(), this.geom_shader, gs);
    }

    final R2ShaderInstanceBatchedType<R2SurfaceShaderBasicParameters> batched_geom_shader =
      R2SurfaceShaderBasicBatched.create(
        gx.shaders(),
        sources,
        id_pool);
    this.batched_geom_material =
      R2MaterialOpaqueBatched.of(
        id_pool.freshID(),
        batched_geom_shader,
        this.geom_material.shaderParameters());

    this.light_ambient_shader =
      R2LightShaderAmbientSingle.create(gx.shaders(), sources, id_pool);
    this.light_ambient = m.lights().createAmbientScreenSingle();
    this.light_ambient.setIntensity(0.15);
    this.light_ambient.setColor(PVector3D.of(0.0, 1.0, 1.0));

    this.proj_light_shader =
      R2LightShaderProjectiveLambertShadowVarianceSingle.create(
        gx.shaders(), sources, id_pool);
    this.proj_proj =
      R2ProjectionFrustum.createWith(-0.5, 0.5, -0.5, 0.5, 1.0, 10.0);
    this.proj_mesh =
      R2ProjectionMesh.create(
        gx,
        this.proj_proj,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW);

    final R2ShadowDepthVariance proj_shadow =
      R2ShadowDepthVariance.of(
        m.idPool().freshID(),
        R2DepthVarianceBufferDescription.of(
          AreaSizeL.of(256L, 256L),
          JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR,
          JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR_MIPMAP_LINEAR,
          R2DepthPrecision.R2_DEPTH_PRECISION_16,
          R2DepthVariancePrecision.R2_DEPTH_VARIANCE_PRECISION_16));

    this.proj_light =
      m.lights().createProjectiveWithShadowVariance(
        this.proj_mesh, proj_shadow);

    this.proj_light.setRadius(10.0);
    this.proj_light.setColor(PVector3D.of(1.0, 1.0, 1.0));
    this.proj_light.transformWritable()
      .setTranslation(PVector3D.of(0.0, 0.0, 3.0));

    this.proj_shadow_instances = R2DepthInstances.create();

    this.sphere_light_shader =
      R2LightShaderSphericalLambertBlinnPhongSingle.create(
        gx.shaders(), sources, id_pool);

    this.sphere_light = m.lights().createSphericalSingle();
    this.sphere_light.setColor(PVector3D.of(1.0, 1.0, 1.0));
    this.sphere_light.setIntensity(1.0);
    this.sphere_light.setOriginPosition(PVector3D.of(0.0, 1.0, 1.0));
    this.sphere_light.setRadius(30.0);
    this.sphere_light.setGeometryScaleFactor(
      R2UnitSphere.uvSphereApproximationScaleFactor(30.0, 8));

    final R2TransformSiOT sphere_light_bounded_transform = R2TransformSiOT.create();
    sphere_light_bounded_transform
      .setTranslation(PVector3D.of(-10.0, 1.0, 0.0));
    sphere_light_bounded_transform
      .setScaleAxes(Vector3D.of(9.0, 9.0, 9.0));

    this.sphere_light_bounds =
      m.instances().createSingle(
        m.unitCube().arrayObject(), sphere_light_bounded_transform);
    this.sphere_light_bounded = m.lights().createSphericalSingle();
    this.sphere_light_bounded.setColor(PVector3D.of(1.0, 0.0, 0.0));
    this.sphere_light_bounded.setIntensity(1.0);
    this.sphere_light_bounded.setOriginPosition(PVector3D.of(-10.0, 1.0, 0.0));
    this.sphere_light_bounded.setRadius(9.0);

    this.filter_light = m.filters().createLightApplicator();
    this.filter_light_params =
      R2FilterLightApplicator.parametersFor(
        m.textureDefaults(),
        this.gbuffer,
        this.lbuffer,
        this.ibuffer.sizeAsViewport());

    {
      final R2DebugCubeType debug_cube = R2DebugCube.create(gx);

      final R2DebugInstances.Builder ib = R2DebugInstances.builder();

      for (int y = 0; y < 100; ++y) {
        ib.addLineSegments(R2DebugLineSegment.of(
          PVector3D.of(-20.0, (double) y, 0.0),
          PVector4D.of(1.0, 0.0, 1.0, 1.0),
          PVector3D.of(20.0, (double) y, 0.0),
          PVector4D.of(0.0, 1.0, 1.0, 1.0)));
      }

      for (int x = 0; x < 20; x += 2) {
        final R2TransformT t = R2TransformT.create();
        t.setTranslation(PVector3D.of((double) x, 1.0, 1.0));
        ib.addCubes(R2DebugCubeInstance.of(
          t, PVector4D.of(0.0, 1.0, 0.0, 1.0)));
      }

      ib.addInstanceSingles(R2DebugInstanceSingle.of(
        this.instance,
        PVector4D.of(1.0, 0.0, 1.0, 1.0)));

      this.debug_params = R2DebugVisualizerRendererParameters.builder()
        .setOpaqueInstances(this.opaques)
        .setShowOpaqueInstances(true)
        .setShowLights(true)
        .setLights(this.lights)
        .setUnitSphere(m.unitSphere8())
        .setDebugCube(debug_cube)
        .setDebugInstances(ib.build())
        .build();
    }

    {
      final R2RenderTargetPoolType<R2ImageBufferDescription, R2ImageBufferUsableType> pool =
        m.pools().createRGBAPool(1024L * 768L * 4L, Long.MAX_VALUE);

      final R2FilterType<R2FilterBoxBlurParameters<
        R2ImageBufferDescription,
        R2ImageBufferUsableType,
        R2ImageBufferDescription,
        R2ImageBufferUsableType>> filter_blur =
        m.filters().createRGBABoxBlur(pool);

      this.filter_emission_params =
        R2FilterEmissionParameters.builder()
          .setTextureDefaults(m.textureDefaults())
          .setAlbedoEmissionMap(this.gbuffer.albedoEmissiveTexture())
          .setBlurParameters(R2BlurParameters.builder().build())
          .setOutputFramebuffer(this.ibuffer.primaryFramebuffer())
          .setOutputViewport(this.ibuffer.sizeAsViewport())
          .setScale(0.25)
          .build();

      this.filter_emission = m.filters().createEmission(pool, filter_blur);
    }

    {
      final JCGLClearSpecification.Builder csb =
        JCGLClearSpecification.builder();
      csb.setStencilBufferClear(0);
      csb.setDepthBufferClear(1.0);
      csb.setColorBufferClear(Vector4D.of(0.0, 0.0, 0.0, 0.0));
      this.screen_clear_spec = csb.build();
    }

    this.profiling_window = ExampleProfilingWindow.create();
  }

  @Override
  public void onRender(
    final R2ExampleServicesType servx,
    final JCGLInterfaceGL33Type gx,
    final AreaSizeL areax,
    final R2FacadeType mx,
    final int frame)
  {
    this.g = gx;

    this.proj_proj.projectionSetXMaximum(
      Math.abs(StrictMath.sin((double) frame * 0.01) * 2.0));
    this.proj_proj.projectionSetXMinimum(
      -Math.abs(StrictMath.sin((double) frame * 0.01) * 2.0));
    this.proj_mesh.updateProjection(gx.arrayBuffers());

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

    final PMatrix4x4D<R2SpaceWorldType, R2SpaceEyeType> view;
    if (servx.isFreeCameraEnabled()) {
      view = servx.getFreeCameraViewMatrix();
    } else {
      view = JCGLViewMatrices.lookAtRHP(
        Vector3D.of(0.0, 0.0, 5.0),
        Vectors3D.zero(),
        Vector3D.of(0.0, 1.0, 0.0));
    }

    final R2MatricesType matrices = mx.matrices();

    final JCGLProfilingType pro = this.main.profiling();
    pro.setEnabled(true);
    final JCGLProfilingFrameType profiling_frame = pro.startFrame();
    this.profiling_root = profiling_frame.childContext("main");

    final R2ShadowMapRendererExecutionType sme =
      this.main.shadowMapRenderer().shadowBegin();

    sme.shadowExecRenderLight(
      this.profiling_root,
      this.main.textureUnitAllocator().rootContext(),
      matrices,
      this.proj_light,
      this.proj_shadow_instances);

    this.shadow_context = sme.shadowExecComplete();

    matrices.withObserver(view, this.projection, this, (mo, t) -> {
      final JCGLTextureUnitContextParentType uc =
        t.main.textureUnitAllocator().rootContext();
      final JCGLFramebufferUsableType gbuffer_fb =
        t.gbuffer.primaryFramebuffer();
      final JCGLFramebufferUsableType lbuffer_fb =
        t.lbuffer.primaryFramebuffer();

      final JCGLFramebuffersType g_fb = t.g.framebuffers();
      final JCGLClearType g_cl = t.g.clearing();
      final JCGLColorBufferMaskingType g_cb = t.g.colorBufferMasking();
      final JCGLStencilBuffersType g_sb = t.g.stencilBuffers();
      final JCGLDepthBuffersType g_db = t.g.depthBuffers();

      /*
       * Populate geometry buffer.
       */

      g_fb.framebufferDrawBind(gbuffer_fb);
      t.gbuffer.clearBoundPrimaryFramebuffer(t.g);
      t.main.stencilRenderer().renderStencilsWithBoundBuffer(
        mo,
        t.profiling_root,
        t.main.textureUnitAllocator().rootContext(),
        t.gbuffer.sizeAsViewport(),
        t.stencils);
      t.main.geometryRenderer().renderGeometry(
        t.gbuffer.sizeAsViewport(),
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
          .setExponent(1.0)
          .setSampleRadius(1.0)
          .setGeometryBuffer(t.gbuffer)
          .setNoiseTexture(t.ssao_noise_texture)
          .setOutputBuffer(t.ssao_buffer)
          .setSceneObserverValues(mo)
          .build();

      t.ssao_filter.runFilter(t.profiling_root, uc, t.ssao_filter_params);

      final R2BilateralBlurParameters blur =
        R2BilateralBlurParameters.builder()
          .setBlurPasses(1)
          .setBlurSize(2.0)
          .setBlurScale(1.0)
          .setBlurSharpness(4.0)
          .build();

      t.ssao_filter_blur_params =
        R2FilterBilateralBlurDepthAwareParameters.of(
          t.ssao_buffer,
          R2AmbientOcclusionBufferUsableType::ambientOcclusionTexture,
          t.ssao_buffer,
          R2AmbientOcclusionBufferUsableType::ambientOcclusionTexture,
          t.ssao_pool,
          R2AmbientOcclusionBufferDescription::withArea,
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
      t.main.lightRenderer().renderLights(
        t.gbuffer,
        t.lbuffer.sizeAsViewport(),
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
          .setIntensity(0.3)
          .setOcclusionTexture(t.ssao_buffer.ambientOcclusionTexture())
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

      //      t.facade.debugVisualizerRenderer().renderScene(
      //        this.ibuffer.sizeAsViewport(),
      //        t.profiling_root,
      //        uc,
      //        mo,
      //        t.debug_params);

      /*
       * Filter the lit image with FXAA, writing it to the screen.
       */

      g_fb.framebufferDrawUnbind();
      g_cb.colorBufferMask(true, true, true, true);
      g_db.depthBufferWriteEnable();
      g_sb.stencilBufferMask(FACE_FRONT_AND_BACK, 0b11111111);
      g_cl.clear(t.screen_clear_spec);

      t.filter_fxaa.runFilter(t.profiling_root, uc, t.filter_fxaa_params);

      t.filter_compositor.runFilter(
        t.profiling_root, uc, t.filter_comp_parameters);
      return Unit.unit();
    });

    this.shadow_context.shadowMapContextFinish();
    this.profiling_window.update(frame, pro.mostRecentlyMeasuredFrame());
  }

  @Override
  public void onFinish(
    final JCGLInterfaceGL33Type gx,
    final R2FacadeType m)
  {
    this.geom_shader.delete(gx);
  }
}
