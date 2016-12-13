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
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
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
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2TransformSOT;
import com.io7m.r2.core.R2TransformSiOT;
import com.io7m.r2.core.R2UnitSphereType;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicParameters;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicSingle;
import com.io7m.r2.core.shaders.provided.R2LightShaderAmbientSingle;
import com.io7m.r2.core.shaders.provided.R2LightShaderProjectiveLambertShadowVarianceSingle;
import com.io7m.r2.core.shaders.provided.R2LightShaderSphericalLambertBlinnPhongSingle;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicReflectiveParameters;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicReflectiveParametersType;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicReflectiveSingle;
import com.io7m.r2.core.shaders.types.R2ShaderDepthSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightProjectiveWithShadowType;
import com.io7m.r2.core.shaders.types.R2ShaderLightSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderLightVolumeSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironment;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.examples.R2ExampleCustomType;
import com.io7m.r2.examples.R2ExampleServicesType;
import com.io7m.r2.filters.R2BlurParametersMutable;
import com.io7m.r2.filters.R2FilterBoxBlur;
import com.io7m.r2.filters.R2FilterBoxBlurParametersType;
import com.io7m.r2.filters.R2FilterCompositor;
import com.io7m.r2.filters.R2FilterCompositorItem;
import com.io7m.r2.filters.R2FilterCompositorParameters;
import com.io7m.r2.filters.R2FilterCompositorParametersType;
import com.io7m.r2.filters.R2FilterEmission;
import com.io7m.r2.filters.R2FilterEmissionParametersMutable;
import com.io7m.r2.filters.R2FilterEmissionParametersType;
import com.io7m.r2.filters.R2FilterFXAA;
import com.io7m.r2.filters.R2FilterFXAAParametersMutable;
import com.io7m.r2.filters.R2FilterFXAAParametersType;
import com.io7m.r2.filters.R2FilterFXAAQuality;
import com.io7m.r2.filters.R2FilterLightApplicator;
import com.io7m.r2.filters.R2FilterLightApplicatorParametersMutable;
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
import org.valid4j.Assertive;

import javax.swing.SwingUtilities;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicReference;

// CHECKSTYLE:OFF

public final class ExampleEnvironment implements R2ExampleCustomType
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

  private R2ShaderInstanceSingleType<R2SurfaceShaderBasicReflectiveParametersType> geom_shader;
  private R2SurfaceShaderBasicReflectiveParametersType geom_shader_params;
  private R2MaterialOpaqueSingleType<R2SurfaceShaderBasicReflectiveParametersType> geom_material;

  private R2ShaderLightVolumeSingleType<R2LightSphericalSingleReadableType> sphere_light_shader;
  private R2LightSphericalSingleType sphere_light;
  private R2UnitSphereType sphere;
  private R2LightSphericalSingleType sphere_light_bounded;
  private R2TransformSiOT sphere_light_bounded_transform;
  private R2InstanceSingleType sphere_light_bounds;

  private R2ShaderLightProjectiveWithShadowType<R2LightProjectiveWithShadowVarianceType> proj_light_shader;
  private R2ProjectionFrustum proj_proj;
  private R2ProjectionMeshType proj_mesh;
  private R2LightProjectiveWithShadowVarianceType proj_light;
  private R2DepthInstancesType proj_shadow_instances;
  private R2ShadowDepthVariance proj_shadow;

  private R2ShaderDepthSingleType<R2DepthShaderBasicParameters> depth_shader;
  private R2DepthShaderBasicParameters depth_params;
  private R2MaterialDepthSingleType<R2DepthShaderBasicParameters> depth_material;

  private R2FilterType<R2FilterLightApplicatorParametersType> filter_light;
  private R2FilterLightApplicatorParametersMutable filter_light_params;

  private R2FilterType<R2FilterFXAAParametersType> filter_fxaa;
  private R2FilterFXAAParametersMutable filter_fxaa_params;

  private R2FilterType<R2FilterCompositorParametersType> filter_compositor;
  private R2FilterCompositorParameters filter_comp_parameters;

  private R2MainType main;

  private R2LightAmbientScreenSingle light_ambient;
  private R2ShaderLightSingleType<R2LightAmbientScreenSingle> light_ambient_shader;

  private JCGLInterfaceGL33Type g;

  private R2ShadowMapContextType shadow_context;

  private AtomicReference<ExampleProfilingWindow> profiling_window;
  private StringBuilder text_buffer;
  private String text;
  private JCGLProfilingFrameType profiling_frame;
  private JCGLProfilingContextType profiling_root;
  private R2RenderTargetPoolType<R2ImageBufferDescriptionType, R2ImageBufferUsableType> image_pool;

  private R2FilterType<
    R2FilterBoxBlurParametersType<
      R2ImageBufferDescriptionType,
      R2ImageBufferUsableType,
      R2ImageBufferDescriptionType,
      R2ImageBufferUsableType>> filter_blur;
  private R2FilterType<R2FilterEmissionParametersType> filter_emission;
  private R2FilterEmissionParametersMutable filter_emission_params;
  private R2BlurParametersMutable filter_emission_blur_params;

  public ExampleEnvironment()
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

    this.sphere = R2UnitSphere.newUnitSphere8(gx);
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

      this.filter_comp_parameters = b.build();
    }

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
      R2FilterFXAAParametersMutable.create();

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

    this.depth_shader = R2DepthShaderBasicSingle.newShader(
      gx.getShaders(), m.getShaderPreprocessingEnvironment(), m.getIDPool());
    this.depth_params = R2DepthShaderBasicParameters.of(
      m.getTextureDefaults(), m.getTextureDefaults().texture2DWhite(), 0.1f);
    this.depth_material = R2MaterialDepthSingle.of(
      id_pool.freshID(), this.depth_shader, this.depth_params);

    this.geom_shader =
      R2SurfaceShaderBasicReflectiveSingle.newShader(
        gx.getShaders(),
        sources,
        id_pool);

    {
      final R2SurfaceShaderBasicReflectiveParameters.Builder spb =
        R2SurfaceShaderBasicReflectiveParameters.builder();
      spb.setTextureDefaults(m.getTextureDefaults());
      spb.setNormalTexture(serv.getTexture2D("halls_complex_normal.png"));
      spb.setEnvironmentTexture(serv.getTextureCube("toronto"));
      spb.setEnvironmentMix(0.5f);
      this.geom_shader_params = spb.build();
    }

    this.geom_material = R2MaterialOpaqueSingle.of(
      id_pool.freshID(), this.geom_shader, this.geom_shader_params);

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

    {
      final AreaInclusiveUnsignedLType shadow_area = AreaInclusiveUnsignedL.of(
        new UnsignedRangeInclusiveL(0L, 255L),
        new UnsignedRangeInclusiveL(0L, 255L));

      this.proj_shadow =
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
        this.proj_shadow,
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
      R2LightSphericalSingle.newLight(this.sphere, id_pool);
    this.sphere_light.colorWritable().set3F(1.0f, 1.0f, 1.0f);
    this.sphere_light.setIntensity(1.0f);
    this.sphere_light.originPositionWritable().set3F(0.0f, 1.0f, 1.0f);
    this.sphere_light.setRadius(30.0f);
    this.sphere_light.setGeometryScaleFactor(
      (float) R2UnitSphere.getUVSphereApproximationScaleFactor(30.0f, 8));

    this.sphere_light_bounded_transform = R2TransformSiOT.newTransform();
    this.sphere_light_bounded_transform.getTranslation()
      .set3F(-10.0f, 1.0f, 0.0f);
    this.sphere_light_bounded_transform.getScale().set3F(9.0f, 9.0f, 9.0f);

    this.sphere_light_bounds =
      R2InstanceSingle.of(
        id_pool.freshID(),
        R2UnitCube.newUnitCube(gx).arrayObject(),
        this.sphere_light_bounded_transform,
        PMatrixI3x3F.identity());
    this.sphere_light_bounded =
      R2LightSphericalSingle.newLight(this.sphere, id_pool);
    this.sphere_light_bounded.colorWritable().set3F(1.0f, 0.0f, 0.0f);
    this.sphere_light_bounded.setIntensity(1.0f);
    this.sphere_light_bounded.originPositionWritable()
      .set3F(-10.0f, 1.0f, 0.0f);
    this.sphere_light_bounded.setRadius(9.0f);

    this.filter_light =
      R2FilterLightApplicator.newFilter(sources, gx, id_pool, m.getUnitQuad());

    {
      this.filter_light_params =
        R2FilterLightApplicatorParametersMutable.create();
    }

    {
      this.image_pool = R2ImageBufferPool.newPool(
        gx, 1024L * 768L * 4L, Long.MAX_VALUE);

      this.filter_blur = R2FilterBoxBlur.newFilter(
        m.getShaderPreprocessingEnvironment(),
        gx,
        m.getTextureDefaults(),
        this.image_pool,
        m.getIDPool(),
        m.getUnitQuad());

      this.filter_emission_params =
        R2FilterEmissionParametersMutable.create();
      this.filter_emission_blur_params =
        R2BlurParametersMutable.create();

      this.filter_emission = R2FilterEmission.newFilter(
        gx,
        m.getShaderPreprocessingEnvironment(),
        m.getIDPool(),
        this.filter_blur,
        this.image_pool,
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
      this.profiling_frame = pro.startFrame();
      this.profiling_root = this.profiling_frame.getChildContext("main");

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
        t.filter_light_params.clear();
        t.filter_light_params.setGeometryBuffer(t.gbuffer);
        t.filter_light_params.setOutputViewport(t.ibuffer.area());
        t.filter_light_params.setCopyDepth(R2CopyDepth.R2_COPY_DEPTH_ENABLED);
        t.lbuffer.matchLightBuffer(
          this,
          (tt, lbdo) -> {
            final R2TextureDefaultsType td = tt.main.getTextureDefaults();
            tt.filter_light_params.setLightDiffuseTexture(
              lbdo.diffuseTexture());
            tt.filter_light_params.setLightSpecularTexture(
              td.texture2DBlack());
            return Unit.unit();
          }, (tt, lbso) -> {
            final R2TextureDefaultsType td = tt.main.getTextureDefaults();
            tt.filter_light_params.setLightDiffuseTexture(
              td.texture2DBlack());
            tt.filter_light_params.setLightSpecularTexture(
              lbso.specularTexture());
            return Unit.unit();
          }, (tt, lb) -> {
            tt.filter_light_params.setLightDiffuseTexture(
              lb.diffuseTexture());
            tt.filter_light_params.setLightSpecularTexture(
              lb.specularTexture());
            return Unit.unit();
          });
        Assertive.require(t.filter_light_params.isInitialized());
        t.filter_light.runFilter(t.profiling_root, uc, t.filter_light_params);

        /*
         * Apply emission.
         */

        t.filter_emission_params.clear();
        t.filter_emission_params.setAlbedoEmissionMap(
          t.gbuffer.albedoEmissiveTexture());
        t.filter_emission_params.setBlurParameters(
          t.filter_emission_blur_params);
        t.filter_emission_params.setOutputFramebuffer(
          Optional.of(t.ibuffer.primaryFramebuffer()));
        t.filter_emission_params.setOutputViewport(
          t.ibuffer.area());
        t.filter_emission_params.setScale(0.25f);
        Assertive.ensure(t.filter_emission_params.isInitialized());

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

        t.filter_fxaa_params.clear();
        t.filter_fxaa_params.setSubPixelAliasingRemoval(0.0f);
        t.filter_fxaa_params.setEdgeThreshold(0.333f);
        t.filter_fxaa_params.setEdgeThresholdMinimum(0.0833f);
        t.filter_fxaa_params.setQuality(R2FilterFXAAQuality.R2_FXAA_QUALITY_10);
        t.filter_fxaa_params.setTexture(t.ibuffer.imageTexture());
        Assertive.require(t.filter_fxaa_params.isInitialized());
        t.filter_fxaa.runFilter(t.profiling_root, uc, t.filter_fxaa_params);

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
