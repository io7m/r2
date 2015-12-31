/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.core;

import com.io7m.jcanephora.core.JCGLDepthFunction;
import com.io7m.jcanephora.core.JCGLFaceSelection;
import com.io7m.jcanephora.core.JCGLFaceWindingOrder;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLStencilFunction;
import com.io7m.jcanephora.core.JCGLStencilOperation;
import com.io7m.jcanephora.core.api.JCGLBlendingType;
import com.io7m.jcanephora.core.api.JCGLColorBufferMaskingType;
import com.io7m.jcanephora.core.api.JCGLCullingType;
import com.io7m.jcanephora.core.api.JCGLDepthBuffersType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLStencilBuffersType;
import com.io7m.jnull.NullCheck;
import org.valid4j.Assertive;

/**
 * The default implementation of the {@link R2GeometryRendererType} interface.
 */

public final class R2GeometryRenderer implements R2GeometryRendererType
{
  private final OpaqueConsumer        opaque_consumer;
  private final JCGLInterfaceGL33Type g;

  private R2GeometryRenderer(
    final JCGLInterfaceGL33Type in_g)
  {
    this.g = NullCheck.notNull(in_g);
    this.opaque_consumer = new OpaqueConsumer(this.g);
  }

  /**
   * @param in_g An OpenGL interface
   * @return A new renderer
   */

  public static R2GeometryRendererType newRenderer(
    final JCGLInterfaceGL33Type in_g)
  {
    return new R2GeometryRenderer(in_g);
  }

  @Override
  public void renderGeometry(
    final R2GeometryBufferUsableType gb,
    final R2MatricesType m,
    final R2SceneOpaquesType s)
  {
    NullCheck.notNull(gb);
    NullCheck.notNull(m);
    NullCheck.notNull(s);

    final JCGLFramebufferUsableType gb_fb = gb.getFramebuffer();
    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();

    try {
      g_fb.framebufferDrawBind(gb_fb);
      this.renderGeometryWithBoundBuffer(m, s);
    } finally {
      g_fb.framebufferDrawUnbind();
    }
  }

  @Override
  public void renderGeometryWithBoundBuffer(
    final R2MatricesType m,
    final R2SceneOpaquesType s)
  {
    NullCheck.notNull(m);
    NullCheck.notNull(s);

    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();
    Assertive.require(g_fb.framebufferDrawAnyIsBound());

    final JCGLDepthBuffersType g_db = this.g.getDepthBuffers();
    final JCGLBlendingType g_b = this.g.getBlending();
    final JCGLColorBufferMaskingType g_cm = this.g.getColorBufferMasking();
    final JCGLCullingType g_cu = this.g.getCulling();
    final JCGLStencilBuffersType g_st = this.g.getStencilBuffers();

    /**
     * Configure state for geometry rendering.
     */

    g_b.blendingDisable();
    g_cm.colorBufferMask(true, true, true, true);
    g_cu.cullingEnable(
      JCGLFaceSelection.FACE_BACK,
      JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE);
    g_db.depthClampingEnable();
    g_db.depthBufferWriteEnable();
    g_db.depthBufferTestEnable(JCGLDepthFunction.DEPTH_LESS_THAN);

    /**
     * Disable writing to the stencil buffer, and only touch pixels with
     * a corresponding `1` value in the stencil buffer.
     */

    g_st.stencilBufferEnable();
    g_st.stencilBufferMask(JCGLFaceSelection.FACE_FRONT_AND_BACK, 0);
    g_st.stencilBufferOperation(
      JCGLFaceSelection.FACE_FRONT_AND_BACK,
      JCGLStencilOperation.STENCIL_OP_KEEP,
      JCGLStencilOperation.STENCIL_OP_KEEP,
      JCGLStencilOperation.STENCIL_OP_KEEP);
    g_st.stencilBufferFunction(
      JCGLFaceSelection.FACE_FRONT_AND_BACK,
      JCGLStencilFunction.STENCIL_EQUAL,
      0b00000000_00000000_00000000_00000001,
      0b11111111_11111111_11111111_11111111);

    s.opaquesExecute(this.opaque_consumer);
  }

  private static final class OpaqueConsumer implements
    R2SceneOpaquesConsumerType
  {
    private final JCGLInterfaceGL33Type g;

    private OpaqueConsumer(
      final JCGLInterfaceGL33Type in_g)
    {
      this.g = NullCheck.notNull(in_g);
    }

    @Override
    public void onStart()
    {

    }

    @Override
    public <M> void onShaderStart(final R2ShaderType<M> s)
    {

    }

    @Override
    public <M> void onMaterialStart(
      final R2MaterialType<M> material)
    {

    }

    @Override
    public void onInstancesStartArray(final R2InstanceType i)
    {

    }

    @Override
    public <M> void onInstance(
      final R2MaterialType<M> material,
      final R2InstanceType i)
    {

    }

    @Override
    public <M> void onMaterialFinish(
      final R2MaterialType<M> material)
    {

    }

    @Override
    public <M> void onShaderFinish(final R2ShaderType<M> s)
    {

    }

    @Override
    public void onFinish()
    {

    }
  }
}
