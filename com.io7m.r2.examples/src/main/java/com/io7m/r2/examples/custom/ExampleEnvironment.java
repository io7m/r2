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
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors3D;
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
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2LightAmbientScreenSingle;
import com.io7m.r2.core.R2LightBufferDescription;
import com.io7m.r2.core.R2LightBufferDiffuseSpecularUsableType;
import com.io7m.r2.core.R2LightBufferType;
import com.io7m.r2.core.R2LightProjectiveWithShadowVariance;
import com.io7m.r2.core.R2LightProjectiveWithShadowVarianceType;
import com.io7m.r2.core.R2LightSphericalSingleReadableType;
import com.io7m.r2.core.R2LightSphericalSingleType;
import com.io7m.r2.core.R2MaterialDepthSingle;
import com.io7m.r2.core.R2MaterialDepthSingleType;
import com.io7m.r2.core.R2MaterialOpaqueSingle;
import com.io7m.r2.core.R2MaterialOpaqueSingleType;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionFOV;
import com.io7m.r2.core.R2ProjectionFrustum;
import com.io7m.r2.core.R2ProjectionMesh;
import com.io7m.r2.core.R2ProjectionMeshType;
import com.io7m.r2.core.R2RenderTargetPoolType;
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
import com.io7m.r2.core.R2TransformSOT;
import com.io7m.r2.core.R2TransformSiOT;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicParameters;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicSingle;
import com.io7m.r2.core.shaders.provided.R2LightShaderAmbientSingle;
import com.io7m.r2.core.shaders.provided.R2LightShaderProjectiveLambertShadowVarianceSingle;
import com.io7m.r2.core.shaders.provided.R2LightShaderSphericalLambertBlinnPhongSingle;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicReflectiveParameters;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicReflectiveSingle;
import com.io7m.r2.core.shaders.types.R2ShaderDepthSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightProjectiveWithShadowType;
import com.io7m.r2.core.shaders.types.R2ShaderLightSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightVolumeSingleType;
import com.io7m.r2.examples.ExampleProfilingWindow;
import com.io7m.r2.examples.R2ExampleCustomType;
import com.io7m.r2.examples.R2ExampleServicesType;
import com.io7m.r2.facade.R2FacadeBufferProviderType;
import com.io7m.r2.facade.R2FacadeType;
import com.io7m.r2.filters.R2BlurParameters;
import com.io7m.r2.filters.R2FilterBoxBlurParameters;
import com.io7m.r2.filters.R2FilterCompositorItem;
import com.io7m.r2.filters.R2FilterCompositorParameters;
import com.io7m.r2.filters.R2FilterEmissionParameters;
import com.io7m.r2.filters.R2FilterFXAAParameters;
import com.io7m.r2.filters.R2FilterLightApplicator;
import com.io7m.r2.filters.R2FilterLightApplicatorParameters;
import com.io7m.r2.meshes.defaults.R2UnitSphere;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLFaceSelection.FACE_FRONT_AND_BACK;
import static com.io7m.r2.core.R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL;
import static com.io7m.r2.core.R2LightBufferComponents.R2_LIGHT_BUFFER_DIFFUSE_AND_SPECULAR;
import static com.io7m.r2.filters.R2FilterFXAAQuality.R2_FXAA_QUALITY_10;

// CHECKSTYLE:OFF

public final class ExampleEnvironment implements R2ExampleCustomType
{
  private JCGLClearSpecification screen_clear_spec;
  private R2SceneStencilsType stencils;
  private R2ProjectionFOV projection;
  private R2InstanceSingleType instance;
  private R2SceneOpaquesType opaques;
  private R2SceneLightsType lights;
  private R2GeometryBufferType gbuffer;
  private R2LightBufferType lbuffer;
  private R2ImageBufferType ibuffer;
  private R2ShaderInstanceSingleType<R2SurfaceShaderBasicReflectiveParameters> geom_shader;
  private R2MaterialOpaqueSingleType<R2SurfaceShaderBasicReflectiveParameters> geom_material;
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
  private R2FilterLightApplicator filter_light;
  private R2FilterLightApplicatorParameters filter_light_params;
  private R2FilterType<R2FilterFXAAParameters> filter_fxaa;
  private R2FilterFXAAParameters filter_fxaa_params;
  private R2FilterType<R2FilterCompositorParameters> filter_compositor;
  private R2FilterCompositorParameters filter_comp_parameters;
  private R2FacadeType main;
  private R2LightAmbientScreenSingle light_ambient;
  private R2ShaderLightSingleType<R2LightAmbientScreenSingle> light_ambient_shader;
  private JCGLInterfaceGL33Type g;
  private R2ShadowMapContextType shadow_context;
  private ExampleProfilingWindow profiling_window;
  private JCGLProfilingContextType profiling_root;
  private R2FilterType<R2FilterEmissionParameters> filter_emission;
  private R2FilterEmissionParameters filter_emission_params;

  public ExampleEnvironment()
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
    this.gbuffer = buffers.createGeometryBuffer(
      R2GeometryBufferDescription.of(area, R2_GEOMETRY_BUFFER_FULL));
    this.lbuffer = buffers.createLightBuffer(
      R2LightBufferDescription.of(area, R2_LIGHT_BUFFER_DIFFUSE_AND_SPECULAR));
    this.ibuffer = buffers.createImageBuffer(
      R2ImageBufferDescription.of(
        area,
        Optional.of(R2DepthAttachmentShare.of(this.gbuffer.depthTexture()))));

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

    final R2ShaderDepthSingleType<R2DepthShaderBasicParameters> depth_shader =
      R2DepthShaderBasicSingle.create(
        gx.shaders(), m.shaderPreprocessingEnvironment(), m.idPool());
    final R2DepthShaderBasicParameters depth_params =
      R2DepthShaderBasicParameters.of(
        m.textureDefaults(), m.textureDefaults().white2D(), 0.1);
    this.depth_material = R2MaterialDepthSingle.of(
      id_pool.freshID(), depth_shader, depth_params);

    this.geom_shader =
      R2SurfaceShaderBasicReflectiveSingle.create(
        gx.shaders(), m.shaderPreprocessingEnvironment(), id_pool);

    final R2SurfaceShaderBasicReflectiveParameters geom_shader_params;
    {
      final R2SurfaceShaderBasicReflectiveParameters.Builder spb =
        R2SurfaceShaderBasicReflectiveParameters.builder();
      spb.setTextureDefaults(m.textureDefaults());
      spb.setNormalTexture(serv.getTexture2D("halls_complex_normal.png"));
      spb.setEnvironmentTexture(serv.getTextureCube("toronto"));
      spb.setEnvironmentMix(0.5);
      geom_shader_params = spb.build();
    }

    this.geom_material = R2MaterialOpaqueSingle.of(
      id_pool.freshID(), this.geom_shader, geom_shader_params);

    this.light_ambient_shader =
      R2LightShaderAmbientSingle.create(
        gx.shaders(), m.shaderPreprocessingEnvironment(), id_pool);
    this.light_ambient = m.lights().createAmbientScreenSingle();
    this.light_ambient.setIntensity(0.15);
    this.light_ambient.setColor(PVector3D.of(0.0, 1.0, 1.0));

    this.proj_light_shader =
      R2LightShaderProjectiveLambertShadowVarianceSingle.create(
        gx.shaders(), m.shaderPreprocessingEnvironment(), id_pool);
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
      R2LightProjectiveWithShadowVariance.create(
        this.proj_mesh,
        m.textureDefaults().whiteProjective2D(),
        proj_shadow,
        m.idPool());
    this.proj_light.setRadius(10.0);
    this.proj_light.setColor(
      PVector3D.of(1.0, 1.0, 1.0));
    this.proj_light.transformWritable().setTranslation(
      PVector3D.of(0.0, 0.0, 3.0));

    this.proj_shadow_instances = R2DepthInstances.create();

    this.sphere_light_shader =
      R2LightShaderSphericalLambertBlinnPhongSingle.create(
        gx.shaders(), m.shaderPreprocessingEnvironment(), id_pool);

    this.sphere_light = m.lights().createSphericalSingle();
    this.sphere_light.setColor(PVector3D.of(1.0, 1.0, 1.0));
    this.sphere_light.setIntensity(1.0);
    this.sphere_light.setOriginPosition(PVector3D.of(0.0, 1.0, 1.0));
    this.sphere_light.setRadius(30.0);
    this.sphere_light.setGeometryScaleFactor(
      R2UnitSphere.uvSphereApproximationScaleFactor(30.0, 8));

    final R2TransformSiOT sphere_light_bounded_transform =
      R2TransformSiOT.create();
    sphere_light_bounded_transform.setTranslation(
      PVector3D.of(-10.0, 1.0, 0.0));
    sphere_light_bounded_transform.setScaleAxes(
      Vector3D.of(9.0, 9.0, 9.0));

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

    this.opaques.opaquesReset();
    this.opaques.opaquesAddSingleInstance(
      this.instance, this.geom_material);

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
