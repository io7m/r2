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

import com.io7m.jareas.core.AreaInclusiveUnsignedIType;
import com.io7m.jcanephora.core.JCGLFaceSelection;
import com.io7m.jcanephora.core.api.JCGLDepthBuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLStencilBuffersType;
import com.io7m.jfunctional.Unit;
import com.io7m.jtensors.VectorI3F;
import com.io7m.jtensors.parameterized.PMatrix4x4FType;
import com.io7m.jtensors.parameterized.PMatrixHeapArrayM4x4F;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.r2.core.R2InstanceSingle;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionFOV;
import com.io7m.r2.core.R2SceneStencils;
import com.io7m.r2.core.R2SceneStencilsMode;
import com.io7m.r2.core.R2SceneStencilsType;
import com.io7m.r2.core.R2StencilRendererType;
import com.io7m.r2.core.R2TransformOSiT;
import com.io7m.r2.core.R2UnitQuad;
import com.io7m.r2.core.R2UnitQuadType;
import com.io7m.r2.examples.R2ExampleCustomType;
import com.io7m.r2.main.R2MainType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

// CHECKSTYLE_JAVADOC:OFF

public final class ExampleStencilNegative implements R2ExampleCustomType
{
  private final PMatrix4x4FType<R2SpaceWorldType, R2SpaceEyeType> view;

  private R2SceneStencilsType   stencils;
  private R2StencilRendererType stencil_renderer;
  private R2MatricesType        matrices;
  private R2ProjectionFOV       projection;
  private R2UnitQuadType        quad;
  private R2InstanceSingleType  instance;

  public ExampleStencilNegative()
  {
    this.view = PMatrixHeapArrayM4x4F.newMatrix();
  }

  @Override
  public void onInitialize(
    final JCGLInterfaceGL33Type g,
    final AreaInclusiveUnsignedIType area,
    final R2MainType m)
  {
    this.stencils = R2SceneStencils.newMasks();
    this.stencil_renderer = m.getStencilRenderer();
    this.matrices = m.getMatrices();
    this.quad = R2UnitQuad.newUnitQuad(g);

    this.projection = R2ProjectionFOV.newFrustumWith(
      m.getProjectionMatrices(),
      (float) Math.toRadians(90.0f), 640.0f / 480.0f, 0.01f, 1000.0f);

    m.getViewMatrices().lookAt(
      this.view,
      new VectorI3F(0.0f, 0.0f, 5.0f),
      new VectorI3F(0.0f, 0.0f, 0.0f),
      new VectorI3F(0.0f, 1.0f, 0.0f));

    final R2TransformOSiT transform = R2TransformOSiT.newTransform();
    this.instance = R2InstanceSingle.newInstance(
      m.getIDPool(),
      this.quad.getArrayObject(),
      transform,
      PMatrixI3x3F.identity());
  }

  @Override
  public void onRender(
    final JCGLInterfaceGL33Type g,
    final AreaInclusiveUnsignedIType area,
    final R2MainType m,
    final int frame)
  {
    this.stencils.stencilsReset();
    this.stencils.stencilsSetMode(
      R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_NEGATIVE);
    this.stencils.stencilsAddSingle(this.instance);

    final JCGLDepthBuffersType g_dep = g.getDepthBuffers();
    g_dep.depthBufferClear(1.0f);

    final JCGLStencilBuffersType g_st = g.getStencilBuffers();
    g_st.stencilBufferMask(JCGLFaceSelection.FACE_FRONT_AND_BACK, 0b11111111);
    g_st.stencilBufferClear(0);

    this.matrices.withObserver(this.view, this.projection, mo -> {
      this.stencil_renderer.renderStencilsWithBoundBuffer(g, mo, this.stencils);
      return Unit.unit();
    });
  }

  @Override
  public void onFinish(
    final JCGLInterfaceGL33Type g,
    final R2MainType m)
  {

  }
}
