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
import com.io7m.jcanephora.profiler.JCGLProfilingFrameMeasurementType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingIteration;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jproperties.JProperties;
import com.io7m.jproperties.JPropertyIncorrectType;
import com.io7m.jproperties.JPropertyNonexistent;
import com.io7m.jtensors.MatrixM4x4F;
import com.io7m.jtensors.VectorI3F;
import com.io7m.jtensors.VectorI4F;
import com.io7m.jtensors.parameterized.PMatrix4x4FType;
import com.io7m.jtensors.parameterized.PMatrixHeapArrayM4x4F;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.r2.core.R2CopyDepth;
import com.io7m.r2.core.R2DepthAttachmentShare;
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
import com.io7m.r2.core.R2InstanceSingle;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2LightAmbientScreenSingle;
import com.io7m.r2.core.R2LightBufferDescription;
import com.io7m.r2.core.R2LightBufferType;
import com.io7m.r2.core.R2LightBuffers;
import com.io7m.r2.core.R2LightSphericalSingle;
import com.io7m.r2.core.R2LightSphericalSingleReadableType;
import com.io7m.r2.core.R2LightSphericalSingleType;
import com.io7m.r2.core.R2MaterialOpaqueSingle;
import com.io7m.r2.core.R2MaterialOpaqueSingleType;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionFOV;
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
import com.io7m.r2.core.R2ShadowMapContextType;
import com.io7m.r2.core.R2ShadowMapRendererExecutionType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2TransformSOT;
import com.io7m.r2.core.R2TransformSiOT;
import com.io7m.r2.core.R2UnitSphereType;
import com.io7m.r2.core.shaders.provided.R2LightShaderAmbientSingle;
import com.io7m.r2.core.shaders.provided.R2LightShaderSphericalLambertBlinnPhongSingle;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicReflectiveParameters;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicReflectiveSingle;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightVolumeSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironment;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.examples.R2ExampleCustomType;
import com.io7m.r2.examples.R2ExampleServicesType;
import com.io7m.r2.filters.R2BlurParameters;
import com.io7m.r2.filters.R2FilterBoxBlur;
import com.io7m.r2.filters.R2FilterBoxBlurParameters;
import com.io7m.r2.filters.R2FilterEmission;
import com.io7m.r2.filters.R2FilterEmissionParameters;
import com.io7m.r2.filters.R2FilterFXAA;
import com.io7m.r2.filters.R2FilterFXAAParameters;
import com.io7m.r2.filters.R2FilterFXAAQuality;
import com.io7m.r2.filters.R2FilterLightApplicator;
import com.io7m.r2.filters.R2FilterLightApplicatorParameters;
import com.io7m.r2.filters.R2FilterLightApplicatorParametersType;
import com.io7m.r2.main.R2MainType;
import com.io7m.r2.meshes.defaults.R2UnitCube;
import com.io7m.r2.meshes.defaults.R2UnitSphere;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import com.io7m.sombrero.core.SoShaderPreprocessorConfig;
import com.io7m.sombrero.core.SoShaderPreprocessorType;
import com.io7m.sombrero.core.SoShaderResolver;
import com.io7m.sombrero.jcpp.SoShaderPreprocessorJCPP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingUtilities;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

// CHECKSTYLE:OFF

public final class ExampleMinimal implements R2ExampleCustomType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(ExampleMinimal.class);
  }

  private final PMatrix4x4FType<R2SpaceWorldType, R2SpaceEyeType> view;

  private JCGLClearSpecification screen_clear_spec;
  private R2SceneStencilsType stencils;
  private R2ProjectionFOV projection;
  private R2InstanceSingleType instance;
  private R2SceneLightsType lights;

  private R2GeometryBufferType gbuffer;
  private R2LightBufferType lbuffer;
  private R2ImageBufferType ibuffer;
  private R2SceneOpaquesType opaques;

  private R2ShaderInstanceSingleType<R2SurfaceShaderBasicReflectiveParameters> geom_shader;
  private R2MaterialOpaqueSingleType<R2SurfaceShaderBasicReflectiveParameters> geom_material;

  private R2ShaderLightVolumeSingleType<R2LightSphericalSingleReadableType> sphere_light_shader;
  private R2LightSphericalSingleType sphere_light;
  private R2LightSphericalSingleType sphere_light_bounded;
  private R2InstanceSingleType sphere_light_bounds;

  private R2FilterType<R2FilterLightApplicatorParametersType> filter_light;
  private R2FilterLightApplicatorParameters filter_light_params;

  private R2FilterType<R2FilterFXAAParameters> filter_fxaa;
  private R2FilterFXAAParameters filter_fxaa_params;

  private R2MainType main;

  private R2LightAmbientScreenSingle light_ambient;
  private R2ShaderLightSingleType<R2LightAmbientScreenSingle> light_ambient_shader;

  private JCGLInterfaceGL33Type g;

  private R2ShadowMapContextType shadow_context;

  private AtomicReference<ExampleProfilingWindow> profiling_window;
  private StringBuilder text_buffer;
  private String text;
  private JCGLProfilingContextType profiling_root;

  private R2FilterType<R2FilterEmissionParameters> filter_emission;
  private R2FilterEmissionParameters filter_emission_params;

  public ExampleMinimal()
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
      final R2GeometryBufferDescription.Builder b =
        R2GeometryBufferDescription.builder();
      b.setArea(area);
      b.setComponents(
        R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL);

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

    final SoShaderPreprocessorConfig.Builder b =
      SoShaderPreprocessorConfig.builder();
    b.setResolver(SoShaderResolver.create());
    b.setVersion(OptionalInt.of(330));
    final SoShaderPreprocessorType p =
      SoShaderPreprocessorJCPP.create(b.build());
    final R2ShaderPreprocessingEnvironmentType sources =
      R2ShaderPreprocessingEnvironment.create(p);

    this.geom_shader =
      R2SurfaceShaderBasicReflectiveSingle.newShader(
        gx.getShaders(), sources, id_pool);

    final R2SurfaceShaderBasicReflectiveParameters geom_shader_params;
    {
      final R2SurfaceShaderBasicReflectiveParameters.Builder spb =
        R2SurfaceShaderBasicReflectiveParameters.builder();
      spb.setTextureDefaults(m.getTextureDefaults());
      spb.setNormalTexture(serv.getTexture2D("halls_complex_normal.png"));
      spb.setEnvironmentTexture(serv.getTextureCube("toronto"));
      spb.setEnvironmentMix(0.5f);
      geom_shader_params = spb.build();
    }

    this.geom_material = R2MaterialOpaqueSingle.of(
      id_pool.freshID(), this.geom_shader, geom_shader_params);

    this.light_ambient_shader =
      R2LightShaderAmbientSingle.newShader(gx.getShaders(), sources, id_pool);
    this.light_ambient =
      R2LightAmbientScreenSingle.newLight(
        m.getUnitQuad(), id_pool, m.getTextureDefaults());
    this.light_ambient.setIntensity(0.15f);
    this.light_ambient.colorWritable().set3F(0.0f, 1.0f, 1.0f);

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

    try {
      final Properties props = System.getProperties();
      final boolean profiling = JProperties.getBooleanOptional(
        props, "com.io7m.r2.profiling", false);
      if (profiling) {
        SwingUtilities.invokeLater(() -> {
          final ExampleProfilingWindow frame = new ExampleProfilingWindow();
          frame.setVisible(true);
          this.profiling_window.set(frame);
        });
        this.text_buffer = new StringBuilder(256);
        this.text = "";
      }
    } catch (final JPropertyNonexistent e) {
      LOG.error("missing system property: ", e);
    } catch (final JPropertyIncorrectType e) {
      LOG.error("incorrect system property type: ", e);
    }
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

    this.stencils.stencilsReset();
    this.stencils.stencilsSetMode(
      R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_NEGATIVE);

    this.opaques.opaquesReset();
    this.opaques.opaquesAddSingleInstance(this.instance, this.geom_material);

    this.lights.lightsReset();
    final R2SceneLightsGroupType lg = this.lights.lightsGetGroup(1);
    lg.lightGroupAddSingle(
      this.light_ambient, this.light_ambient_shader);
    lg.lightGroupAddSingle(
      this.sphere_light, this.sphere_light_shader);

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
        g_sb.stencilBufferMask(
          JCGLFaceSelection.FACE_FRONT_AND_BACK, 0b11111111);
        g_cl.clear(t.screen_clear_spec);

        t.filter_fxaa.runFilter(t.profiling_root, uc, t.filter_fxaa_params);
        return Unit.unit();
      });

      this.shadow_context.shadowMapContextFinish();

      final JCGLProfilingFrameMeasurementType pro_measure =
        pro.getMostRecentlyMeasuredFrame();

      if (frame % 60 == 0) {
        if (this.profiling_window.get() != null) {
          this.text_buffer.setLength(0);
          pro_measure.iterate(this, (tt, depth, fm) -> {
            for (int index = 0; index < depth; ++index) {
              tt.text_buffer.append("    ");
            }
            tt.text_buffer.append(fm.getName());
            tt.text_buffer.append(" ");

            final double nanos = (double) fm.getElapsedTimeTotal();
            final double millis = nanos / 1_000_000.0;
            tt.text_buffer.append(String.format(
              "%.6f",
              Double.valueOf(millis)));
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
  }

  @Override
  public void onFinish(
    final JCGLInterfaceGL33Type gx,
    final R2MainType m)
  {
    this.geom_shader.delete(gx);
  }
}
