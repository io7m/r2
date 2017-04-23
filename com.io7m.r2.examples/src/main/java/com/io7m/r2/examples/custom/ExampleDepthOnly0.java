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
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors3D;
import com.io7m.r2.core.R2DepthInstances;
import com.io7m.r2.core.R2DepthInstancesType;
import com.io7m.r2.core.R2DepthOnlyBuffer;
import com.io7m.r2.core.R2DepthOnlyBufferDescription;
import com.io7m.r2.core.R2DepthOnlyBufferType;
import com.io7m.r2.core.R2DepthPrecision;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2MaterialDepthSingle;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionFOV;
import com.io7m.r2.core.R2TransformReadableType;
import com.io7m.r2.core.R2TransformSOT;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicParameters;
import com.io7m.r2.core.shaders.provided.R2DepthShaderBasicSingle;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicParameters;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicSingle;
import com.io7m.r2.core.shaders.types.R2ShaderDepthSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.examples.R2ExampleCustomType;
import com.io7m.r2.examples.R2ExampleServicesType;
import com.io7m.r2.facade.R2FacadeType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

// CHECKSTYLE_JAVADOC:OFF

public final class ExampleDepthOnly0 implements R2ExampleCustomType
{
  private R2ProjectionFOV projection;
  private R2ShaderInstanceSingleType<R2SurfaceShaderBasicParameters> shader;
  private R2InstanceSingleType instance;
  private R2DepthOnlyBufferType dbuffer;
  private R2DepthInstancesType depth_instances;
  private R2MaterialDepthSingle<R2DepthShaderBasicParameters> depth_material;
  private R2FacadeType main;
  private JCGLInterfaceGL33Type g33;

  public ExampleDepthOnly0()
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

    this.depth_instances = R2DepthInstances.create();

    {
      final R2DepthOnlyBufferDescription.Builder desc =
        R2DepthOnlyBufferDescription.builder();
      desc.setArea(area);
      desc.setDepthPrecision(R2DepthPrecision.R2_DEPTH_PRECISION_24);

      this.dbuffer = R2DepthOnlyBuffer.create(
        g.framebuffers(),
        g.textures(),
        m.textureUnitAllocator().rootContext(),
        desc.build());
    }

    this.projection = R2ProjectionFOV.createWith(
      Math.toRadians(90.0), 640.0 / 480.0, 0.01, 1000.0);

    final R2IDPoolType id_pool = m.idPool();
    final R2TransformReadableType tr = R2TransformSOT.create();

    this.instance =
      m.instances().createSingle(m.unitSphere8().arrayObject(), tr);

    final R2ShaderDepthSingleType<R2DepthShaderBasicParameters> depth_shader =
      R2DepthShaderBasicSingle.create(
        g.shaders(), m.shaderPreprocessingEnvironment(), m.idPool());
    final R2DepthShaderBasicParameters depth_shader_params =
      R2DepthShaderBasicParameters.of(
        m.textureDefaults(), m.textureDefaults().white2D(), 0.1);
    this.depth_material = R2MaterialDepthSingle.of(
      id_pool.freshID(), depth_shader, depth_shader_params);

    this.shader =
      R2SurfaceShaderBasicSingle.create(
        g.shaders(), m.shaderPreprocessingEnvironment(), id_pool);
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

    this.depth_instances.depthsReset();
    this.depth_instances.depthsAddSingleInstance(
      this.instance, this.depth_material);

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
      final JCGLFramebufferUsableType dbuffer_fb =
        t.dbuffer.primaryFramebuffer();

      final JCGLFramebuffersType g_fb = t.g33.framebuffers();

      g_fb.framebufferDrawBind(dbuffer_fb);
      this.dbuffer.clearBoundPrimaryFramebuffer(t.g33);
      t.main.depthRenderer().renderDepthWithBoundBuffer(
        t.dbuffer.sizeAsViewport(),
        t.main.textureUnitAllocator().rootContext(),
        mo,
        t.depth_instances);
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
  }
}
