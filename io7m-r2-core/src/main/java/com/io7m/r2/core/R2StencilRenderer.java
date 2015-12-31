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

import com.io7m.jcanephora.core.JCGLFaceSelection;
import com.io7m.jcanephora.core.JCGLFaceWindingOrder;
import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.JCGLStencilFunction;
import com.io7m.jcanephora.core.JCGLStencilOperation;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLBlendingType;
import com.io7m.jcanephora.core.api.JCGLColorBufferMaskingType;
import com.io7m.jcanephora.core.api.JCGLCullingType;
import com.io7m.jcanephora.core.api.JCGLDepthBuffersType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLStencilBuffersType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jtensors.parameterized.PMatrixReadable3x3FType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valid4j.Assertive;

/**
 * The default implementation of the {@link R2StencilRendererType} interface.
 */

public final class R2StencilRenderer implements R2StencilRendererType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2StencilRenderer.class);
  }

  private final StencilConsumer            stencil_consumer;
  private final JCGLInterfaceGL33Type      g;
  private final R2ShaderInstanceType<Unit> program_instance;
  private final R2ShaderScreenType<Unit>   program_screen;
  private final R2UnitQuadType             quad;
  private       boolean                    deleted;

  private R2StencilRenderer(
    final R2ShaderSourcesType in_sources,
    final JCGLInterfaceGL33Type in_g,
    final R2IDPoolType in_pool)
  {
    this.g = NullCheck.notNull(in_g);
    NullCheck.notNull(in_sources);

    R2StencilRenderer.LOG.debug("initializing");

    final JCGLShadersType g_sh = in_g.getShaders();
    this.program_instance =
      R2StencilInstanceShader.newShader(g_sh, in_sources, in_pool);
    this.program_screen =
      R2StencilScreenShader.newShader(g_sh, in_sources, in_pool);

    this.stencil_consumer = new StencilConsumer(this.g, this.program_instance);
    this.quad = R2UnitQuad.newUnitQuad(this.g);

    R2StencilRenderer.LOG.debug("initialized");
  }

  /**
   * @param in_sources Shader source access
   * @param in_g       An OpenGL interface
   * @param in_pool    The ID pool
   *
   * @return A new renderer
   */

  public static R2StencilRendererType newRenderer(
    final R2ShaderSourcesType in_sources,
    final JCGLInterfaceGL33Type in_g,
    final R2IDPoolType in_pool)
  {
    return new R2StencilRenderer(in_sources, in_g, in_pool);
  }

  @Override
  public void renderStencilsWithBoundBuffer(
    final R2MatricesObserverType m,
    final R2SceneStencilsType s)
  {
    NullCheck.notNull(m);
    NullCheck.notNull(s);

    Assertive.require(!this.deleted);

    final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
    final JCGLDepthBuffersType g_db = this.g.getDepthBuffers();
    final JCGLBlendingType g_b = this.g.getBlending();
    final JCGLColorBufferMaskingType g_cm = this.g.getColorBufferMasking();
    final JCGLCullingType g_cu = this.g.getCulling();
    final JCGLStencilBuffersType g_st = this.g.getStencilBuffers();
    final JCGLShadersType g_sh = this.g.getShaders();
    final JCGLDrawType g_dr = this.g.getDraw();

    /**
     * Configure state for rendering stencil instances.
     */

    g_b.blendingDisable();
    g_cm.colorBufferMask(false, false, false, false);
    g_cu.cullingEnable(
      JCGLFaceSelection.FACE_BACK,
      JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE);
    g_db.depthClampingEnable();
    g_db.depthBufferWriteDisable();
    g_db.depthBufferTestDisable();

    /**
     * Populate the stencil buffer with the values required for each
     * mode.
     */

    g_st.stencilBufferEnable();
    g_st.stencilBufferMask(
      JCGLFaceSelection.FACE_FRONT_AND_BACK,
      R2Stencils.ALLOW_BIT);
    g_st.stencilBufferOperation(
      JCGLFaceSelection.FACE_FRONT_AND_BACK,
      JCGLStencilOperation.STENCIL_OP_KEEP,
      JCGLStencilOperation.STENCIL_OP_KEEP,
      JCGLStencilOperation.STENCIL_OP_REPLACE);

    switch (s.stencilsGetMode()) {
      case STENCIL_MODE_NEGATIVE: {

        /**
         * Set the {@link R2Stencils#ALLOW_BIT} for each pixel in the current
         * framebuffer, leaving other bits untouched.
         */

        g_st.stencilBufferFunction(
          JCGLFaceSelection.FACE_FRONT_AND_BACK,
          JCGLStencilFunction.STENCIL_ALWAYS,
          R2Stencils.ALLOW_BIT,
          0b11111111_11111111_11111111_11111111);

        break;
      }
      case STENCIL_MODE_POSITIVE: {

        /**
         * Unset the {@link R2Stencils#ALLOW_BIT} for each pixel in the current
         * framebuffer, leaving other bits untouched.
         */

        g_st.stencilBufferFunction(
          JCGLFaceSelection.FACE_FRONT_AND_BACK,
          JCGLStencilFunction.STENCIL_ALWAYS,
          0,
          0b11111111_11111111_11111111_11111111);

        break;
      }
    }

    g_sh.shaderActivateProgram(this.program_screen.getShaderProgram());
    g_ao.arrayObjectBind(this.quad.getArrayObject());
    g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
    g_ao.arrayObjectUnbind();
    g_sh.shaderDeactivateProgram();

    if (s.stencilsCount() > 0L) {

      switch (s.stencilsGetMode()) {
        case STENCIL_MODE_NEGATIVE: {

          /**
           * Each instance will unset the {@link R2Stencils#ALLOW_BIT} for
           * each affected pixel.
           */

          g_st.stencilBufferFunction(
            JCGLFaceSelection.FACE_FRONT_AND_BACK,
            JCGLStencilFunction.STENCIL_ALWAYS,
            0,
            0b11111111_11111111_11111111_11111111);

          break;
        }
        case STENCIL_MODE_POSITIVE: {

          /**
           * Each instance will set the {@link R2Stencils#ALLOW_BIT} for
           * each affected pixel.
           */

          g_st.stencilBufferFunction(
            JCGLFaceSelection.FACE_FRONT_AND_BACK,
            JCGLStencilFunction.STENCIL_ALWAYS,
            R2Stencils.ALLOW_BIT,
            0b11111111_11111111_11111111_11111111);

          break;
        }
      }

      try {
        this.stencil_consumer.matrices = m;
        s.stencilsExecute(this.stencil_consumer);
      } finally {
        this.stencil_consumer.matrices = null;
      }
    }
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type gi)
    throws R2Exception
  {
    if (!this.deleted) {
      try {
        this.program_instance.delete(gi);
        this.quad.delete(gi);
      } finally {
        this.deleted = true;
      }
    }
  }

  private static final class StencilConsumer implements
    R2SceneStencilsConsumerType
  {
    private final     JCGLInterfaceGL33Type      g;
    private final     R2ShaderInstanceType<Unit> program;
    private final     JCGLShadersType            shaders;
    private final     JCGLArrayObjectsType       array_objects;
    private final     JCGLDrawType               draw;
    private @Nullable R2MatricesObserverType     matrices;

    StencilConsumer(
      final JCGLInterfaceGL33Type in_g,
      final R2ShaderInstanceType<Unit> in_program)
    {
      this.g = NullCheck.notNull(in_g);
      this.program = NullCheck.notNull(in_program);
      this.shaders = this.g.getShaders();
      this.array_objects = this.g.getArrayObjects();
      this.draw = this.g.getDraw();
    }

    @Override
    public void onStart()
    {
      this.shaders.shaderActivateProgram(this.program.getShaderProgram());
      this.program.setMatricesView(this.shaders, this.matrices);
    }

    @Override
    public void onInstancesStartArray(final R2InstanceType i)
    {
      this.array_objects.arrayObjectBind(i.getArrayObject());
    }

    @Override
    public void onInstance(final R2InstanceType i)
    {
      final R2TransformReadableType it =
        i.getTransform();
      final PMatrixReadable3x3FType<R2SpaceTextureType, R2SpaceTextureType> uv =
        i.getUVMatrix();

      this.matrices.withTransform(it, uv, mi -> {
        this.program.setMatricesInstance(this.shaders, mi);
        this.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
        return Unit.unit();
      });
    }

    @Override
    public void onFinish()
    {
      this.shaders.shaderDeactivateProgram();
    }
  }
}
