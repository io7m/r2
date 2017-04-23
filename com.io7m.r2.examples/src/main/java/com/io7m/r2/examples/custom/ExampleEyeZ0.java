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
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors3D;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2GeometryBufferDescription;
import com.io7m.r2.core.R2GeometryBufferType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2MaterialOpaqueSingle;
import com.io7m.r2.core.R2MaterialOpaqueSingleType;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionFOV;
import com.io7m.r2.core.R2SceneOpaques;
import com.io7m.r2.core.R2SceneOpaquesType;
import com.io7m.r2.core.R2SceneStencils;
import com.io7m.r2.core.R2SceneStencilsType;
import com.io7m.r2.core.R2TransformReadableType;
import com.io7m.r2.core.R2TransformSOT;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicParameters;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicSingle;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.examples.R2ExampleCustomType;
import com.io7m.r2.examples.R2ExampleServicesType;
import com.io7m.r2.facade.R2FacadeBufferProviderType;
import com.io7m.r2.facade.R2FacadeType;
import com.io7m.r2.filters.R2EyeZBuffer;
import com.io7m.r2.filters.R2EyeZBufferType;
import com.io7m.r2.filters.R2FilterDebugEyeZ;
import com.io7m.r2.filters.R2FilterDebugEyeZParameters;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

import java.util.Optional;

import static com.io7m.jcanephora.core.JCGLFaceSelection.FACE_FRONT_AND_BACK;
import static com.io7m.r2.core.R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL;
import static com.io7m.r2.core.R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_NEGATIVE;

// CHECKSTYLE_JAVADOC:OFF

public final class ExampleEyeZ0 implements R2ExampleCustomType
{
  private R2SceneStencilsType stencils;
  private R2ProjectionFOV projection;
  private R2SceneOpaquesType opaques;
  private R2GeometryBufferType gbuffer;
  private R2ShaderInstanceSingleType<R2SurfaceShaderBasicParameters> shader;
  private R2MaterialOpaqueSingleType<R2SurfaceShaderBasicParameters> material;
  private R2InstanceSingleType instance;
  private JCGLClearSpecification geom_clear_spec;
  private JCGLClearSpecification eye_clear_spec;
  private R2FilterType<R2FilterDebugEyeZParameters> eye_filter;
  private R2EyeZBufferType eye_buffer;
  private R2FacadeType main;
  private JCGLInterfaceGL33Type g33;

  public ExampleEyeZ0()
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

    this.eye_filter =
      R2FilterDebugEyeZ.newFilter(
        g,
        m.shaderPreprocessingEnvironment(),
        m.idPool(),
        m.unitQuad());

    this.eye_buffer =
      R2EyeZBuffer.newEyeZBuffer(
        g.framebuffers(),
        g.textures(),
        m.textureUnitAllocator().rootContext(),
        area);

    final R2FacadeBufferProviderType buffers = m.buffers();
    this.gbuffer = buffers.createGeometryBuffer(
      R2GeometryBufferDescription.of(area, R2_GEOMETRY_BUFFER_FULL));

    this.projection = R2ProjectionFOV.createWith(
      Math.toRadians(90.0), 640.0 / 480.0, 0.01, 1000.0);

    final R2IDPoolType id_pool = m.idPool();

    final R2TransformReadableType tr = R2TransformSOT.create();

    this.instance =
      m.instances().createSingle(m.unitSphere8().arrayObject(), tr);

    this.shader =
      R2SurfaceShaderBasicSingle.create(
        g.shaders(),
        m.shaderPreprocessingEnvironment(),
        id_pool);
    final R2SurfaceShaderBasicParameters shader_params =
      R2SurfaceShaderBasicParameters.builder()
        .setTextureDefaults(m.textureDefaults())
        .build();

    this.material = R2MaterialOpaqueSingle.of(
      id_pool.freshID(), this.shader, shader_params);

    {
      final JCGLClearSpecification.Builder csb =
        JCGLClearSpecification.builder();
      csb.setStencilBufferClear(0);
      csb.setDepthBufferClear(1.0);
      csb.setColorBufferClear(Vector4D.of(0.0, 0.0, 0.0, 0.0));
      this.geom_clear_spec = csb.build();
    }

    {
      final JCGLClearSpecification.Builder csb =
        JCGLClearSpecification.builder();
      csb.setColorBufferClear(Vector4D.of(0.0, 0.0, 0.0, 0.0));
      this.eye_clear_spec = csb.build();
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
    this.main = m;
    this.g33 = g;

    this.stencils.stencilsReset();
    this.stencils.stencilsSetMode(STENCIL_MODE_INSTANCES_ARE_NEGATIVE);

    this.opaques.opaquesReset();
    this.opaques.opaquesAddSingleInstance(this.instance, this.material);

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
      final JCGLFramebuffersType g_fb = t.g33.framebuffers();
      final JCGLClearType g_cl = t.g33.clearing();
      final JCGLColorBufferMaskingType g_cb = t.g33.colorBufferMasking();
      final JCGLStencilBuffersType g_sb = t.g33.stencilBuffers();
      final JCGLDepthBuffersType g_db = t.g33.depthBuffers();

      final JCGLFramebufferUsableType gbuffer_fb =
        t.gbuffer.primaryFramebuffer();
      final JCGLFramebufferUsableType eye_buffer_fb =
        t.eye_buffer.framebuffer();

      final JCGLProfilingType pro =
        t.main.profiling();
      final JCGLProfilingFrameType pro_frame =
        pro.startFrame();
      final JCGLProfilingContextType pro_root =
        pro_frame.childContext("main");

      g_fb.framebufferDrawBind(gbuffer_fb);
      g_cb.colorBufferMask(true, true, true, true);
      g_db.depthBufferWriteEnable();
      g_sb.stencilBufferMask(FACE_FRONT_AND_BACK, 0b11111111);
      g_cl.clear(t.geom_clear_spec);

      t.main.stencilRenderer().renderStencilsWithBoundBuffer(
        mo,
        pro_root,
        t.main.textureUnitAllocator().rootContext(),
        t.gbuffer.sizeAsViewport(),
        t.stencils);
      t.main.geometryRenderer().renderGeometry(
        t.gbuffer.sizeAsViewport(),
        Optional.empty(),
        pro_root,
        t.main.textureUnitAllocator().rootContext(),
        mo,
        t.opaques);
      g_fb.framebufferDrawUnbind();

      g_fb.framebufferDrawBind(eye_buffer_fb);
      g_cb.colorBufferMask(true, true, true, true);
      g_db.depthBufferWriteEnable();
      g_sb.stencilBufferMask(FACE_FRONT_AND_BACK, 0b11111111);
      g_cl.clear(t.eye_clear_spec);

      final R2FilterDebugEyeZParameters eye_filter_params =
        R2FilterDebugEyeZParameters.builder()
          .setObserverValues(mo)
          .setEyeZBuffer(t.eye_buffer)
          .setGeometryBuffer(t.gbuffer)
          .build();

      t.eye_filter.runFilter(
        pro_root,
        t.main.textureUnitAllocator().rootContext(),
        eye_filter_params);

      g_fb.framebufferDrawUnbind();
      return Unit.unit();
    });
  }

  @Override
  public void onFinish(
    final JCGLInterfaceGL33Type g,
    final R2FacadeType m)
  {
    this.shader.delete(g);
    this.eye_filter.delete(g);
  }
}
