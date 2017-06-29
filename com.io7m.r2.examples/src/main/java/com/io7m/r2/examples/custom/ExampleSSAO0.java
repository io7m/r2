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
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jregions.core.unparameterized.areas.AreasL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizesL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors3D;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.examples.R2ExampleCustomType;
import com.io7m.r2.examples.R2ExampleServicesType;
import com.io7m.r2.facade.R2FacadeBufferProviderType;
import com.io7m.r2.facade.R2FacadeType;
import com.io7m.r2.filters.api.R2FilterType;
import com.io7m.r2.filters.box_blur.api.R2BlurParameters;
import com.io7m.r2.filters.box_blur.api.R2FilterBoxBlurParameters;
import com.io7m.r2.filters.compositor.api.R2FilterCompositorItem;
import com.io7m.r2.filters.compositor.api.R2FilterCompositorParameters;
import com.io7m.r2.filters.ssao.R2FilterSSAO;
import com.io7m.r2.filters.ssao.R2SSAOKernel;
import com.io7m.r2.filters.ssao.api.R2AmbientOcclusionBufferDescription;
import com.io7m.r2.filters.ssao.api.R2AmbientOcclusionBufferType;
import com.io7m.r2.filters.ssao.api.R2AmbientOcclusionBufferUsableType;
import com.io7m.r2.filters.ssao.api.R2FilterSSAOParameters;
import com.io7m.r2.filters.ssao.api.R2SSAOKernelType;
import com.io7m.r2.instances.R2InstanceBatchedDynamicType;
import com.io7m.r2.instances.R2InstanceSingleType;
import com.io7m.r2.matrices.R2MatricesType;
import com.io7m.r2.projections.R2ProjectionFOV;
import com.io7m.r2.rendering.geometry.R2SceneOpaques;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferDescription;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferType;
import com.io7m.r2.rendering.geometry.api.R2MaterialOpaqueBatched;
import com.io7m.r2.rendering.geometry.api.R2MaterialOpaqueBatchedType;
import com.io7m.r2.rendering.geometry.api.R2MaterialOpaqueSingle;
import com.io7m.r2.rendering.geometry.api.R2MaterialOpaqueSingleType;
import com.io7m.r2.rendering.geometry.api.R2SceneOpaquesType;
import com.io7m.r2.rendering.stencil.R2SceneStencils;
import com.io7m.r2.rendering.stencil.api.R2SceneStencilsType;
import com.io7m.r2.rendering.targets.R2RenderTargetPoolType;
import com.io7m.r2.shaders.geometry.R2GeometryShaderBasicBatched;
import com.io7m.r2.shaders.geometry.R2GeometryShaderBasicParameters;
import com.io7m.r2.shaders.geometry.api.R2ShaderGeometrySingleType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import com.io7m.r2.textures.R2Texture2DType;
import com.io7m.r2.transforms.R2TransformSOT;

import java.util.Optional;

import static com.io7m.r2.rendering.geometry.api.R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL;
import static com.io7m.r2.rendering.stencil.api.R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_NEGATIVE;

// CHECKSTYLE_JAVADOC:OFF

public final class ExampleSSAO0 implements R2ExampleCustomType
{
  private JCGLInterfaceGL33Type g33;
  private R2FacadeType main;
  private R2SceneStencilsType stencils;
  private R2SceneOpaquesType opaques;
  private R2ProjectionFOV projection;
  private R2InstanceSingleType instance;
  private JCGLClearSpecification screen_clear_spec;
  private R2GeometryBufferType geom_buffer;
  private R2ShaderGeometrySingleType<R2GeometryShaderBasicParameters> geom_shader;
  private R2MaterialOpaqueSingleType<R2GeometryShaderBasicParameters> geom_material;
  private R2InstanceBatchedDynamicType batched_instance;
  private R2MaterialOpaqueBatchedType<R2GeometryShaderBasicParameters> batched_geom_material;
  private R2FilterType<R2FilterCompositorParameters> filter_compositor;
  private R2FilterCompositorParameters filter_comp_parameters;
  private R2AmbientOcclusionBufferType ssao_buffer;
  private R2FilterType<R2FilterBoxBlurParameters<R2AmbientOcclusionBufferDescription, R2AmbientOcclusionBufferUsableType, R2AmbientOcclusionBufferDescription, R2AmbientOcclusionBufferUsableType>> ssao_filter_blur;
  private R2FilterBoxBlurParameters<R2AmbientOcclusionBufferDescription, R2AmbientOcclusionBufferUsableType, R2AmbientOcclusionBufferDescription, R2AmbientOcclusionBufferUsableType> ssao_filter_blur_params;
  private R2Texture2DType ssao_noise_texture;
  private R2SSAOKernelType ssao_kernel;
  private R2FilterSSAO ssao_filter;
  private R2RenderTargetPoolType<R2AmbientOcclusionBufferDescription, R2AmbientOcclusionBufferUsableType> ssao_pool;

  public ExampleSSAO0()
  {

  }

  @Override
  public void onInitialize(
    final R2ExampleServicesType serv,
    final JCGLInterfaceGL33Type g,
    final AreaSizeL area,
    final R2FacadeType m)
  {
    this.main = NullCheck.notNull(m, "Main");

    this.opaques = R2SceneOpaques.create();
    this.stencils = R2SceneStencils.create();

    final R2FacadeBufferProviderType buffers = m.buffers();
    this.geom_buffer = buffers.createGeometryBuffer(
      R2GeometryBufferDescription.of(area, R2_GEOMETRY_BUFFER_FULL));

    this.ssao_buffer =
      buffers.createAmbientOcclusionBuffer(
        R2AmbientOcclusionBufferDescription.of(
          AreaSizeL.of(area.sizeX() / 2L, area.sizeY() / 2L)));

    this.ssao_filter =
      m.filters().createSSAO();

    this.ssao_pool =
      m.pools().createAmbientOcclusionPool(
        area.sizeX() * area.sizeY() * 2L, Long.MAX_VALUE);

    this.ssao_kernel =
      R2SSAOKernel.newKernel(64);

    this.ssao_noise_texture =
      m.textures().createSSAONoiseTexture();

    this.ssao_filter_blur_params =
      R2FilterBoxBlurParameters.of(
        this.ssao_buffer,
        R2AmbientOcclusionBufferUsableType::ambientOcclusionTexture,
        this.ssao_buffer,
        R2AmbientOcclusionBufferUsableType::ambientOcclusionTexture,
        this.ssao_pool,
        R2BlurParameters.builder().build(),
        R2AmbientOcclusionBufferDescription::withArea);

    this.ssao_filter_blur =
      m.filters().createSSAOBoxBlur(this.ssao_pool);

    {
      final R2FilterCompositorParameters.Builder b =
        R2FilterCompositorParameters.builder();

      b.addItems(R2FilterCompositorItem.of(
        this.ssao_buffer.ambientOcclusionTexture(),
        AreaSizesL.area(area),
        1.0,
        Optional.empty()));

      long x = 20L;
      final long y = 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.geom_buffer.albedoEmissiveTexture(),
        AreasL.create(x, y, 128L, 96L),
        1.0,
        Optional.empty()));

      x += 128L + 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.geom_buffer.normalTexture(),
        AreasL.create(x, y, 128L, 96L),
        1.0,
        Optional.empty()));

      x += 128L + 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.geom_buffer.specularTextureOrDefault(m.textureDefaults()),
        AreasL.create(x, y, 128L, 96L),
        1.0,
        Optional.empty()));

      x += 128L + 20L;

      b.addItems(R2FilterCompositorItem.of(
        this.geom_buffer.depthTexture(),
        AreasL.create(x, y, 128L, 96L),
        1.0,
        Optional.empty()));

      this.filter_comp_parameters = b.build();
    }

    this.filter_compositor =
      m.filters().createCompositor();

    this.projection = R2ProjectionFOV.createWith(
      Math.toRadians(90.0), 640.0 / 480.0, 0.001, 1000.0);

    final R2IDPoolType id_pool = m.idPool();
    final JCGLArrayObjectType mesh = serv.getMesh("halls_complex.r2z");

    final R2TransformSOT transform = R2TransformSOT.create();
    transform.setTranslation(PVector3D.of(0.0, -1.0, 0.0));

    this.instance =
      m.instances().createSingle(mesh, transform);

    final int width = 16;
    final int height = 16;
    final int depth = 16;
    final int instance_count = width * height * depth;
    this.batched_instance =
      m.instances().createSphere8BatchedDynamic(instance_count);

    final R2TransformSOT[] batched_transforms = new R2TransformSOT[instance_count];

    int index = 0;
    for (int x = 0; x < width; ++x) {
      for (int y = 0; y < height; ++y) {
        for (int z = 0; z < depth; ++z) {
          final R2TransformSOT t = R2TransformSOT.create();
          t.setScale(0.2);
          final double fx = (double) (x - (width / 2));
          final double fy = (double) (y - (height / 2));
          final double fz = (double) -z * 1.5;
          t.setTranslation(PVector3D.of(fx, fy, fz));
          batched_transforms[index] = t;
          this.batched_instance.enableInstance(batched_transforms[index]);
          ++index;
        }
      }
    }

    {
      this.geom_shader = m.geometryShaders().createBasicSingle();
      final R2GeometryShaderBasicParameters gsp =
        R2GeometryShaderBasicParameters.builder()
          .setTextureDefaults(m.textureDefaults())
          .setSpecularColor(PVector3D.of(1.0, 1.0, 1.0))
          .setSpecularExponent(64.0)
          .build();
      this.geom_material =
        R2MaterialOpaqueSingle.of(id_pool.freshID(), this.geom_shader, gsp);
    }

    final R2GeometryShaderBasicBatched batched_geom_shader =
      m.geometryShaders().createBasicBatched();
    this.batched_geom_material = R2MaterialOpaqueBatched.of(
      id_pool.freshID(),
      batched_geom_shader,
      this.geom_material.shaderParameters());

    {
      final JCGLClearSpecification.Builder csb =
        JCGLClearSpecification.builder();
      csb.setStencilBufferClear(0);
      csb.setDepthBufferClear(1.0);
      csb.setColorBufferClear(Vector4D.of(0.0, 0.0, 0.0, 0.0));
      this.screen_clear_spec = csb.build();
    }
  }

  @Override
  public void onRender(
    final R2ExampleServicesType serv,
    final JCGLInterfaceGL33Type g,
    final AreaSizeL area,
    final R2FacadeType m,
    final int frame)
  {
    this.g33 = g;

    this.stencils.stencilsReset();
    this.stencils.stencilsSetMode(
      STENCIL_MODE_INSTANCES_ARE_NEGATIVE);

    this.opaques.opaquesReset();
    this.opaques.opaquesAddSingleInstance(this.instance, this.geom_material);
    this.opaques.opaquesAddBatchedInstance(
      this.batched_instance, this.batched_geom_material);

    final PMatrix4x4D<R2SpaceWorldType, R2SpaceEyeType> view;
    if (serv.isFreeCameraEnabled()) {
      view = serv.getFreeCameraViewMatrix();
    } else {
      view = JCGLViewMatrices.lookAtRHP(
        Vector3D.of(0.0, 0.0, 5.0),
        Vectors3D.zero(),
        Vector3D.of(0.0, 1.0, 0.0));
    }

    final R2MatricesType matrices = m.matrices();
    matrices.withObserver(view, this.projection, this, (mo, t) -> {
      final JCGLTextureUnitContextParentType uc =
        t.main.textureUnitAllocator().rootContext();
      final JCGLFramebufferUsableType gbuffer_fb =
        t.geom_buffer.primaryFramebuffer();

      final JCGLFramebuffersType g_fb = t.g33.framebuffers();
      final JCGLClearType g_cl = t.g33.clearing();
      final JCGLColorBufferMaskingType g_cb = t.g33.colorBufferMasking();

      final JCGLProfilingType pro =
        t.main.profiling();
      final JCGLProfilingFrameType pro_frame =
        pro.startFrame();
      final JCGLProfilingContextType pro_root =
        pro_frame.childContext("main");

      g_fb.framebufferDrawBind(gbuffer_fb);
      t.geom_buffer.clearBoundPrimaryFramebuffer(t.g33);
      t.main.stencilRenderer().renderStencilsWithBoundBuffer(
        mo,
        pro_root,
        m.textureUnitAllocator().rootContext(),
        t.geom_buffer.sizeAsViewport(),
        t.stencils);
      t.main.geometryRenderer().renderGeometry(
        t.geom_buffer.sizeAsViewport(),
        Optional.empty(),
        pro_root,
        m.textureUnitAllocator().rootContext(),
        mo,
        t.opaques);
      g_fb.framebufferDrawUnbind();

      final R2FilterSSAOParameters filter_ssao_params =
        R2FilterSSAOParameters.builder()
          .setKernel(t.ssao_kernel)
          .setExponent(1.0)
          .setSampleRadius(1.0)
          .setGeometryBuffer(t.geom_buffer)
          .setNoiseTexture(t.ssao_noise_texture)
          .setOutputBuffer(t.ssao_buffer)
          .setSceneObserverValues(mo)
          .build();

      t.ssao_filter.runFilter(pro_root, uc, filter_ssao_params);
      t.ssao_filter_blur.runFilter(pro_root, uc, t.ssao_filter_blur_params);

      g_fb.framebufferDrawUnbind();
      g_cb.colorBufferMask(true, true, true, true);
      g_cl.clear(t.screen_clear_spec);

      t.filter_compositor.runFilter(pro_root, uc, t.filter_comp_parameters);
      return Unit.unit();
    });
  }

  @Override
  public void onFinish(
    final JCGLInterfaceGL33Type g,
    final R2FacadeType m)
  {
    this.geom_shader.delete(g);
  }
}
