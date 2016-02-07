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
import com.io7m.jtensors.MatrixM4x4F;
import com.io7m.jtensors.VectorI3F;
import com.io7m.jtensors.VectorI4F;
import com.io7m.jtensors.parameterized.PMatrix4x4FType;
import com.io7m.jtensors.parameterized.PMatrixHeapArrayM4x4F;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.r2.core.R2GeometryBuffer;
import com.io7m.r2.core.R2GeometryBufferType;
import com.io7m.r2.core.R2GeometryRendererType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2InstanceBatchedDynamic;
import com.io7m.r2.core.R2InstanceBatchedDynamicType;
import com.io7m.r2.core.R2LightBuffer;
import com.io7m.r2.core.R2LightBufferType;
import com.io7m.r2.core.R2LightRendererType;
import com.io7m.r2.core.R2LightSphericalSimpleSingle;
import com.io7m.r2.core.R2LightSphericalSingleType;
import com.io7m.r2.core.R2MaterialOpaqueBatched;
import com.io7m.r2.core.R2MaterialOpaqueBatchedType;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionFOV;
import com.io7m.r2.core.R2SceneOpaqueLights;
import com.io7m.r2.core.R2SceneOpaqueLightsType;
import com.io7m.r2.core.R2SceneOpaques;
import com.io7m.r2.core.R2SceneOpaquesType;
import com.io7m.r2.core.R2SceneStencils;
import com.io7m.r2.core.R2SceneStencilsMode;
import com.io7m.r2.core.R2SceneStencilsType;
import com.io7m.r2.core.R2ShaderBatchedType;
import com.io7m.r2.core.R2ShaderLightSingleType;
import com.io7m.r2.core.R2ShaderSourcesResources;
import com.io7m.r2.core.R2ShaderSourcesType;
import com.io7m.r2.core.R2StencilRendererType;
import com.io7m.r2.core.R2TransformOST;
import com.io7m.r2.core.R2UnitQuad;
import com.io7m.r2.core.R2UnitQuadType;
import com.io7m.r2.core.R2UnitSphereType;
import com.io7m.r2.core.debug.R2DebugShaderLightSphericalAttenuationSingle;
import com.io7m.r2.core.filters.R2FilterLightApplicator;
import com.io7m.r2.core.filters.R2FilterLightApplicatorType;
import com.io7m.r2.core.shaders.R2SurfaceShaderBasicBatched;
import com.io7m.r2.core.shaders.R2SurfaceShaderBasicParameters;
import com.io7m.r2.examples.R2ExampleCustomType;
import com.io7m.r2.examples.R2ExampleServicesType;
import com.io7m.r2.main.R2MainType;
import com.io7m.r2.meshes.defaults.R2UnitSphere;
import com.io7m.r2.shaders.R2Shaders;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

// CHECKSTYLE_JAVADOC:OFF

public final class ExampleLightAttenuation0 implements R2ExampleCustomType
{
  private final PMatrix4x4FType<R2SpaceWorldType, R2SpaceEyeType> view;
  private       R2TransformOST[]                                  transforms;

  private JCGLClearSpecification light_clear_spec;
  private R2SceneStencilsType stencils;
  private R2StencilRendererType stencil_renderer;
  private R2GeometryRendererType geom_renderer;
  private R2LightRendererType light_renderer;
  private R2MatricesType matrices;
  private R2ProjectionFOV projection;
  private R2UnitQuadType quad;
  private R2InstanceBatchedDynamicType instance;
  private R2SceneOpaquesType opaques;
  private R2SceneOpaqueLightsType lights;
  private R2GeometryBufferType gbuffer;
  private R2LightBufferType lbuffer;
  private JCGLClearSpecification geom_clear_spec;
  private JCGLClearSpecification screen_clear_spec;

  private R2ShaderBatchedType<R2SurfaceShaderBasicParameters>
    geom_shader;
  private R2SurfaceShaderBasicParameters
    geom_shader_params;
  private R2MaterialOpaqueBatchedType<R2SurfaceShaderBasicParameters>
    geom_material;

  private R2ShaderLightSingleType<R2LightSphericalSingleType> light_shader;
  private R2LightSphericalSingleType                          light;
  private R2UnitSphereType                                    sphere;
  private R2FilterLightApplicatorType                         filter;

  public ExampleLightAttenuation0()
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
    this.sphere = R2UnitSphere.newUnitSphere8(g);
    this.opaques = R2SceneOpaques.newOpaques();
    this.lights = R2SceneOpaqueLights.newLights();
    this.stencils = R2SceneStencils.newMasks();
    this.stencil_renderer = m.getStencilRenderer();
    this.geom_renderer = m.getGeometryRenderer();
    this.light_renderer = m.getLightRenderer();
    this.matrices = m.getMatrices();
    this.quad = R2UnitQuad.newUnitQuad(g);
    this.gbuffer = R2GeometryBuffer.newGeometryBuffer(
      g.getFramebuffers(),
      g.getTextures(),
      m.getTextureUnitAllocator().getRootContext(),
      area);
    this.lbuffer = R2LightBuffer.newLightBuffer(
      g.getFramebuffers(),
      g.getTextures(),
      m.getTextureUnitAllocator().getRootContext(),
      area);

    this.projection = R2ProjectionFOV.newFrustumWith(
      m.getProjectionMatrices(),
      (float) Math.toRadians(90.0f), 640.0f / 480.0f, 0.01f, 1000.0f);

    final R2IDPoolType id_pool = m.getIDPool();

    final int width = 16;
    final int height = 16;
    final int depth = 16;

    final int instance_count = width * height * depth;
    this.instance =
      R2InstanceBatchedDynamic.newBatch(
        id_pool,
        g.getArrayBuffers(),
        g.getArrayObjects(),
        this.quad.getArrayObject(),
        instance_count);

    this.transforms = new R2TransformOST[instance_count];

    int index = 0;
    for (int x = 0; x < width; ++x) {
      for (int y = 0; y < height; ++y) {
        for (int z = 0; z < depth; ++z) {
          final R2TransformOST t = R2TransformOST.newTransform();
          t.setScale(0.2f);
          final PVector3FType<R2SpaceWorldType> tr = t.getTranslation();
          final float fx = x - (width / 2);
          final float fy = y - (height / 2);
          final float fz = -z * 1.5f;
          tr.set3F(fx, fy, fz);
          this.transforms[index] = t;
          this.instance.enableInstance(this.transforms[index]);
          ++index;
        }
      }
    }

    final R2ShaderSourcesType sources =
      R2ShaderSourcesResources.newSources(R2Shaders.class);

    this.geom_shader =
      R2SurfaceShaderBasicBatched.newShader(
        g.getShaders(),
        sources,
        id_pool);
    this.geom_shader_params =
      R2SurfaceShaderBasicParameters.newParameters(
        m.getTextureDefaults());
    this.geom_shader_params.getSpecularColor().set3F(1.0f, 1.0f, 1.0f);
    this.geom_shader_params.setSpecularExponent(32.0f);
    this.geom_material = R2MaterialOpaqueBatched.newMaterial(
      id_pool, this.geom_shader, this.geom_shader_params);

    this.light_shader =
      R2DebugShaderLightSphericalAttenuationSingle.newShader(
        g.getShaders(), sources, id_pool);
    this.light =
      R2LightSphericalSimpleSingle.newLight(this.sphere, id_pool);
    this.light.getColor().set3F(1.0f, 1.0f, 1.0f);
    this.light.setIntensity(1.0f);
    this.light.getPosition().set3F(0.0f, 0.0f, 1.0f);
    this.light.setRadius(4.0f);

    this.filter =
      R2FilterLightApplicator.newFilter(
        sources, m.getTextureDefaults(), g, id_pool, this.quad);

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

    {
      final JCGLClearSpecification.Builder csb =
        JCGLClearSpecification.builder();
      csb.setColorBufferClear(new VectorI4F(0.0f, 0.0f, 0.0f, 1.0f));
      this.light_clear_spec = csb.build();
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
    this.opaques.opaquesAddBatchedInstance(this.instance, this.geom_material);

    this.lights.opaqueLightsReset();
    this.lights.opaqueLightsAddSingle(this.light, this.light_shader);

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
      this.matrices.withObserver(this.view, this.projection, this, (mo, t) -> {
        final JCGLFramebufferUsableType gbuffer_fb =
          t.gbuffer.getFramebuffer();
        final JCGLFramebufferUsableType lbuffer_fb =
          t.lbuffer.getFramebuffer();

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

        t.stencil_renderer.renderStencilsWithBoundBuffer(
          g,
          mo,
          t.gbuffer.getArea(),
          t.stencils);
        t.geom_renderer.renderGeometryWithBoundBuffer(
          g,
          t.gbuffer.getArea(),
          m.getTextureUnitAllocator().getRootContext(),
          mo,
          t.opaques);
        g_fb.framebufferDrawUnbind();

        g_fb.framebufferDrawBind(lbuffer_fb);
        g_cb.colorBufferMask(true, true, true, true);
        g_db.depthBufferWriteEnable();
        g_sb.stencilBufferMask(
          JCGLFaceSelection.FACE_FRONT_AND_BACK, 0b11111111);
        g_cl.clear(t.light_clear_spec);

        t.light_renderer.renderLightsWithBoundBuffer(
          g,
          t.gbuffer,
          t.lbuffer.getArea(),
          m.getTextureUnitAllocator().getRootContext(),
          mo,
          t.lights);
        g_fb.framebufferDrawUnbind();

        g_cb.colorBufferMask(true, true, true, true);
        g_db.depthBufferWriteEnable();
        g_sb.stencilBufferMask(
          JCGLFaceSelection.FACE_FRONT_AND_BACK, 0b11111111);
        g_cl.clear(t.screen_clear_spec);

        this.filter.runLightApplicatorWithBoundBuffer(
          g,
          m.getTextureUnitAllocator().getRootContext(),
          this.gbuffer,
          this.lbuffer,
          area);
        return Unit.unit();
      });
    }
  }

  @Override
  public void onFinish(
    final JCGLInterfaceGL33Type g,
    final R2MainType m)
  {
    this.quad.delete(g);
    this.geom_shader.delete(g);
    this.stencil_renderer.delete(g);
  }
}
