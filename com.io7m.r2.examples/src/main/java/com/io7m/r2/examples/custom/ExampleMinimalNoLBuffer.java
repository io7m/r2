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
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizesL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors3D;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.examples.ExampleProfilingWindow;
import com.io7m.r2.examples.R2ExampleCustomType;
import com.io7m.r2.examples.R2ExampleServicesType;
import com.io7m.r2.facade.R2FacadeBufferProviderType;
import com.io7m.r2.facade.R2FacadeType;
import com.io7m.r2.filters.api.R2FilterType;
import com.io7m.r2.filters.box_blur.api.R2BlurParameters;
import com.io7m.r2.filters.box_blur.api.R2FilterBoxBlurParameters;
import com.io7m.r2.filters.emission.api.R2FilterEmissionParameters;
import com.io7m.r2.filters.fxaa.api.R2FilterFXAAParameters;
import com.io7m.r2.filters.fxaa.api.R2FilterFXAAType;
import com.io7m.r2.images.R2ImageBuffer;
import com.io7m.r2.images.api.R2DepthAttachmentCreateWithStencil;
import com.io7m.r2.images.api.R2ImageBufferDescription;
import com.io7m.r2.images.api.R2ImageBufferUsableType;
import com.io7m.r2.instances.R2InstanceSingle;
import com.io7m.r2.instances.R2InstanceSingleType;
import com.io7m.r2.lights.R2LightAmbientScreenSingle;
import com.io7m.r2.lights.R2LightSphericalSingle;
import com.io7m.r2.matrices.R2MatricesType;
import com.io7m.r2.meshes.defaults.R2UnitSphere;
import com.io7m.r2.projections.R2ProjectionFOV;
import com.io7m.r2.rendering.geometry.R2SceneOpaques;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferDescription;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferType;
import com.io7m.r2.rendering.geometry.api.R2MaterialOpaqueSingle;
import com.io7m.r2.rendering.geometry.api.R2MaterialOpaqueSingleType;
import com.io7m.r2.rendering.geometry.api.R2SceneOpaquesType;
import com.io7m.r2.rendering.lights.R2SceneLights;
import com.io7m.r2.rendering.lights.api.R2SceneLightsClipGroupType;
import com.io7m.r2.rendering.lights.api.R2SceneLightsGroupType;
import com.io7m.r2.rendering.lights.api.R2SceneLightsType;
import com.io7m.r2.rendering.shadow.api.R2ShadowMapContextType;
import com.io7m.r2.rendering.shadow.api.R2ShadowMapRendererExecutionType;
import com.io7m.r2.rendering.stencil.R2SceneStencils;
import com.io7m.r2.rendering.stencil.api.R2SceneStencilsType;
import com.io7m.r2.rendering.targets.R2RenderTargetPoolType;
import com.io7m.r2.shaders.geometry.R2GeometryShaderBasicReflectiveParameters;
import com.io7m.r2.shaders.geometry.api.R2ShaderGeometrySingleType;
import com.io7m.r2.shaders.light.R2LightShaderSphericalLambertBlinnPhongSingle;
import com.io7m.r2.shaders.light.api.R2LightShaderDefines;
import com.io7m.r2.shaders.light.api.R2ShaderLightSingleType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import com.io7m.r2.transforms.R2TransformSOT;
import com.io7m.r2.transforms.R2TransformSiOT;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLFaceSelection.FACE_FRONT_AND_BACK;
import static com.io7m.r2.filters.fxaa.api.R2FilterFXAAQuality.R2_FXAA_QUALITY_10;
import static com.io7m.r2.rendering.geometry.api.R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL;
import static com.io7m.r2.rendering.stencil.api.R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_NEGATIVE;

// CHECKSTYLE:OFF

public final class ExampleMinimalNoLBuffer implements R2ExampleCustomType
{
  private JCGLClearSpecification screen_clear_spec;
  private R2SceneStencilsType stencils;
  private R2ProjectionFOV projection;
  private R2InstanceSingleType instance;
  private R2SceneLightsType lights;
  private R2GeometryBufferType gbuffer;
  private R2ImageBuffer ibuffer;
  private R2SceneOpaquesType opaques;
  private R2ShaderGeometrySingleType<R2GeometryShaderBasicReflectiveParameters> geom_shader;
  private R2MaterialOpaqueSingleType<R2GeometryShaderBasicReflectiveParameters> geom_material;
  private R2FilterFXAAParameters filter_fxaa_params;
  private R2FacadeType main;
  private R2LightAmbientScreenSingle light_ambient;
  private R2ShaderLightSingleType<R2LightAmbientScreenSingle> light_ambient_shader;
  private JCGLInterfaceGL33Type g;
  private R2ShadowMapContextType shadow_context;
  private ExampleProfilingWindow profiling_window;
  private JCGLProfilingContextType profiling_root;
  private R2FilterType<R2FilterEmissionParameters> filter_emission;
  private R2FilterEmissionParameters filter_emission_params;
  private R2FilterFXAAType filter_fxaa;
  private R2LightShaderSphericalLambertBlinnPhongSingle sphere_light_shader;
  private R2LightSphericalSingle sphere_light;
  private R2InstanceSingle sphere_light_bounds;
  private R2LightSphericalSingle sphere_light_bounded;

  public ExampleMinimalNoLBuffer()
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

    this.main.shaderPreprocessingEnvironment().preprocessorDefineSet(
      R2LightShaderDefines.R2_LIGHT_SHADER_OUTPUT_TARGET_DEFINE,
      R2LightShaderDefines.R2_LIGHT_SHADER_OUTPUT_TARGET_IBUFFER);

    this.opaques = R2SceneOpaques.create();
    this.lights = R2SceneLights.create();
    this.stencils = R2SceneStencils.create();

    final R2FacadeBufferProviderType buffers = m.buffers();
    this.gbuffer =
      buffers.createGeometryBuffer(
        R2GeometryBufferDescription.of(area, R2_GEOMETRY_BUFFER_FULL));
    this.ibuffer =
      buffers.createImageBuffer(
        R2ImageBufferDescription.of(
          area,
          Optional.of(R2DepthAttachmentCreateWithStencil.builder().build())));

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
    final JCGLArrayObjectType mesh =
      serv.getMesh("halls_complex.smfb");

    final R2TransformSOT transform = R2TransformSOT.create();
    transform.setTranslation(PVector3D.of(0.0, -1.0, 0.0));

    this.instance = m.instances().createSingle(mesh, transform);

    final R2GeometryShaderBasicReflectiveParameters geom_shader_params;
    {
      final R2GeometryShaderBasicReflectiveParameters.Builder spb =
        R2GeometryShaderBasicReflectiveParameters.builder();
      spb.setTextureDefaults(m.textureDefaults());
      spb.setNormalTexture(serv.getTexture2D("halls_complex_normal.png"));
      spb.setEnvironmentTexture(serv.getTextureCube("toronto"));
      spb.setEnvironmentMix(0.5);
      geom_shader_params = spb.build();
    }

    this.geom_shader = m.geometryShaders().createBasicReflectiveSingle();
    this.geom_material = R2MaterialOpaqueSingle.of(
      id_pool.freshID(), this.geom_shader, geom_shader_params);

    this.light_ambient_shader = m.lightShaders().createAmbientSingle();
    this.light_ambient = m.lights().createAmbientScreenSingle();
    this.light_ambient.setIntensity(0.15);
    this.light_ambient.setColor(PVector3D.of(0.0, 1.0, 1.0));

    this.sphere_light_shader =
      m.lightShaders().createSphericalLambertBlinnPhongSingle();
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
      m.instances().createCubeSingle(sphere_light_bounded_transform);
    this.sphere_light_bounded = m.lights().createSphericalSingle();
    this.sphere_light_bounded.setColor(PVector3D.of(1.0, 0.0, 0.0));
    this.sphere_light_bounded.setIntensity(1.0);
    this.sphere_light_bounded.setOriginPosition(PVector3D.of(-10.0, 1.0, 0.0));
    this.sphere_light_bounded.setRadius(9.0);

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

    this.stencils.stencilsReset();
    this.stencils.stencilsSetMode(STENCIL_MODE_INSTANCES_ARE_NEGATIVE);

    this.opaques.opaquesReset();
    this.opaques.opaquesAddSingleInstance(this.instance, this.geom_material);

    this.lights.lightsReset();
    final R2SceneLightsGroupType lg = this.lights.lightsGetGroup(1);
    lg.lightGroupAddSingle(this.light_ambient, this.light_ambient_shader);
    lg.lightGroupAddSingle(this.sphere_light, this.sphere_light_shader);

    final R2SceneLightsClipGroupType lcg =
      lg.lightGroupNewClipGroup(this.sphere_light_bounds);
    lcg.clipGroupAddSingle(this.sphere_light_bounded, this.sphere_light_shader);

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
    this.shadow_context = sme.shadowExecComplete();

    matrices.withObserver(view, this.projection, this, (mo, t) -> {
      final JCGLTextureUnitContextParentType uc =
        t.main.textureUnitAllocator().rootContext();
      final JCGLFramebufferUsableType gbuffer_fb =
        t.gbuffer.primaryFramebuffer();
      final JCGLFramebufferUsableType ibuffer_fb =
        t.ibuffer.primaryFramebuffer();

      final JCGLFramebuffersType g_fb = t.g.framebuffers();
      final JCGLClearType g_cl = t.g.clearing();
      final JCGLColorBufferMaskingType g_cb = t.g.colorBufferMasking();
      final JCGLStencilBuffersType g_sb = t.g.stencilBuffers();
      final JCGLDepthBuffersType g_db = t.g.depthBuffers();

      /*
       * Populate geometry buffer.
       */

      final AreaL gbuffer_viewport = AreaSizesL.area(t.gbuffer.size());
      g_fb.framebufferDrawBind(gbuffer_fb);
      t.gbuffer.clearBoundPrimaryFramebuffer(t.g);
      t.main.stencilRenderer().renderStencilsWithBoundBuffer(
        mo,
        t.profiling_root,
        t.main.textureUnitAllocator().rootContext(),
        gbuffer_viewport,
        t.stencils);
      t.main.geometryRenderer().renderGeometry(
        gbuffer_viewport,
        Optional.empty(),
        t.profiling_root,
        uc,
        mo,
        t.opaques);

      g_fb.framebufferDrawBind(ibuffer_fb);
      t.ibuffer.clearBoundPrimaryFramebuffer(t.g);
      t.main.lightRenderer().renderLightsToImageBuffer(
        t.gbuffer,
        t.ibuffer.sizeAsViewport(),
        Optional.empty(),
        t.profiling_root,
        uc,
        t.shadow_context,
        mo,
        t.lights);

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
