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

import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLViewMatrices;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jfunctional.Unit;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors3D;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.examples.R2ExampleCustomType;
import com.io7m.r2.examples.R2ExampleServicesType;
import com.io7m.r2.facade.R2FacadeBufferProviderType;
import com.io7m.r2.facade.R2FacadeType;
import com.io7m.r2.instances.R2InstanceSingleType;
import com.io7m.r2.matrices.R2MatricesType;
import com.io7m.r2.projections.R2ProjectionFOV;
import com.io7m.r2.rendering.geometry.R2SceneOpaques;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferDescription;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferType;
import com.io7m.r2.rendering.geometry.api.R2MaterialOpaqueSingle;
import com.io7m.r2.rendering.geometry.api.R2MaterialOpaqueSingleType;
import com.io7m.r2.rendering.geometry.api.R2SceneOpaquesType;
import com.io7m.r2.rendering.stencil.R2SceneStencils;
import com.io7m.r2.rendering.stencil.api.R2SceneStencilsType;
import com.io7m.r2.shaders.geometry.R2GeometryShaderBasicParameters;
import com.io7m.r2.shaders.geometry.api.R2ShaderGeometrySingleType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import com.io7m.r2.transforms.R2TransformReadableType;
import com.io7m.r2.transforms.R2TransformSOT;

import java.util.Optional;

import static com.io7m.r2.rendering.geometry.api.R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL;
import static com.io7m.r2.rendering.stencil.api.R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_NEGATIVE;

// CHECKSTYLE_JAVADOC:OFF

public final class ExampleGeometry4 implements R2ExampleCustomType
{
  private R2SceneStencilsType stencils;
  private R2MatricesType matrices;
  private R2ProjectionFOV projection;
  private R2SceneOpaquesType opaques;
  private R2GeometryBufferType gbuffer;
  private R2ShaderGeometrySingleType<R2GeometryShaderBasicParameters> shader;
  private R2MaterialOpaqueSingleType<R2GeometryShaderBasicParameters> material;
  private R2InstanceSingleType instance;
  private R2FacadeType main;

  public ExampleGeometry4()
  {

  }

  @Override
  public void onInitialize(
    final R2ExampleServicesType serv,
    final JCGLInterfaceGL33Type g,
    final AreaSizeL area,
    final R2FacadeType m)
  {
    this.opaques = R2SceneOpaques.create();
    this.stencils = R2SceneStencils.create();
    this.matrices = m.matrices();

    final R2FacadeBufferProviderType buffers = m.buffers();
    this.gbuffer = buffers.createGeometryBuffer(
      R2GeometryBufferDescription.of(area, R2_GEOMETRY_BUFFER_FULL));

    this.projection = R2ProjectionFOV.createWith(
      Math.toRadians(90.0), 640.0 / 480.0, 0.01, 1000.0);

    final R2IDPoolType id_pool = m.idPool();

    final R2TransformReadableType tr = R2TransformSOT.create();
    this.instance = m.instances().createCubeSingle(tr);
    this.shader = m.geometryShaders().createBasicSingle();
    final R2GeometryShaderBasicParameters shader_params =
      R2GeometryShaderBasicParameters.builder()
        .setTextureDefaults(m.textureDefaults())
        .build();
    this.material = R2MaterialOpaqueSingle.of(
      id_pool.freshID(), this.shader, shader_params);
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

    this.stencils.stencilsReset();
    this.stencils.stencilsSetMode(STENCIL_MODE_INSTANCES_ARE_NEGATIVE);

    this.opaques.opaquesReset();
    this.opaques.opaquesAddSingleInstance(this.instance, this.material);

    final JCGLFramebufferUsableType fb = this.gbuffer.primaryFramebuffer();
    final JCGLFramebuffersType g_fb = g.framebuffers();

    g_fb.framebufferDrawBind(fb);
    this.gbuffer.clearBoundPrimaryFramebuffer(g);

    final PMatrix4x4D<R2SpaceWorldType, R2SpaceEyeType> view;
    if (serv.isFreeCameraEnabled()) {
      view = serv.getFreeCameraViewMatrix();
    } else {
      view = JCGLViewMatrices.lookAtRHP(
        Vector3D.of(0.0, 0.0, 5.0),
        Vectors3D.zero(),
        Vector3D.of(0.0, 1.0, 0.0));
    }

    this.matrices.withObserver(view, this.projection, this, (mo, t) -> {
      final JCGLProfilingType pro = t.main.profiling();
      final JCGLProfilingFrameType pro_frame = pro.startFrame();
      final JCGLProfilingContextType pro_root =
        pro_frame.childContext("main");

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
      return Unit.unit();
    });

    g_fb.framebufferDrawUnbind();
  }

  @Override
  public void onFinish(
    final JCGLInterfaceGL33Type g,
    final R2FacadeType m)
  {
    this.shader.delete(g);
  }
}
