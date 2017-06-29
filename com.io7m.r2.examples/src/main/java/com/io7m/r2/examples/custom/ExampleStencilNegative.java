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

import com.io7m.jcanephora.core.JCGLViewMatrices;
import com.io7m.jcanephora.core.api.JCGLDepthBuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLStencilBuffersType;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jfunctional.Unit;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizesL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors3D;
import com.io7m.r2.examples.R2ExampleCustomType;
import com.io7m.r2.examples.R2ExampleServicesType;
import com.io7m.r2.facade.R2FacadeType;
import com.io7m.r2.instances.R2InstanceSingleType;
import com.io7m.r2.matrices.R2MatricesType;
import com.io7m.r2.projections.R2ProjectionFOV;
import com.io7m.r2.rendering.stencil.R2SceneStencils;
import com.io7m.r2.rendering.stencil.api.R2SceneStencilsType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import com.io7m.r2.transforms.R2TransformSiOT;

import static com.io7m.jcanephora.core.JCGLFaceSelection.FACE_FRONT_AND_BACK;
import static com.io7m.r2.rendering.stencil.api.R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_NEGATIVE;

// CHECKSTYLE_JAVADOC:OFF

public final class ExampleStencilNegative implements R2ExampleCustomType
{
  private R2SceneStencilsType stencils;
  private R2MatricesType matrices;
  private R2ProjectionFOV projection;
  private R2InstanceSingleType instance;
  private R2FacadeType main;

  public ExampleStencilNegative()
  {

  }

  @Override
  public void onInitialize(
    final R2ExampleServicesType serv,
    final JCGLInterfaceGL33Type g,
    final AreaSizeL area,
    final R2FacadeType m)
  {
    this.stencils = R2SceneStencils.create();
    this.matrices = m.matrices();

    this.projection = R2ProjectionFOV.createWith(
      Math.toRadians(90.0), 640.0 / 480.0, 0.01, 1000.0);

    final R2TransformSiOT transform = R2TransformSiOT.create();
    this.instance = m.instances().createQuadSingle(transform);
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
    this.stencils.stencilsAddSingle(this.instance);

    final JCGLDepthBuffersType g_dep = g.depthBuffers();
    g_dep.depthBufferClear(1.0f);

    final JCGLStencilBuffersType g_st = g.stencilBuffers();
    g_st.stencilBufferMask(FACE_FRONT_AND_BACK, 0b11111111);
    g_st.stencilBufferClear(0);

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
        AreaSizesL.area(area),
        t.stencils);
      return Unit.unit();
    });
  }

  @Override
  public void onFinish(
    final JCGLInterfaceGL33Type g,
    final R2FacadeType m)
  {

  }
}
