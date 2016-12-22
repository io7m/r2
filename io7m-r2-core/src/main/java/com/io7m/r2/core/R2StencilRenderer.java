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

package com.io7m.r2.core;

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
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
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.core.api.JCGLViewportsType;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jtensors.parameterized.PMatrixReadable3x3FType;
import com.io7m.r2.core.shaders.provided.R2StencilShaderScreen;
import com.io7m.r2.core.shaders.provided.R2StencilShaderSingle;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleScreenType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of the {@link R2StencilRendererType} interface.
 */

public final class R2StencilRenderer implements R2StencilRendererType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2StencilRenderer.class);
  }

  private final StencilConsumer stencil_consumer;
  private final R2ShaderInstanceSingleType<Unit> program_instance;
  private final R2ShaderInstanceSingleScreenType<Unit> program_screen;
  private final R2UnitQuadUsableType quad;
  private final JCGLInterfaceGL33Type g;
  private boolean deleted;

  private R2StencilRenderer(
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final JCGLInterfaceGL33Type in_g,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    NullCheck.notNull(in_shader_env);
    this.g = NullCheck.notNull(in_g);
    NullCheck.notNull(in_pool);
    this.quad = NullCheck.notNull(in_quad);

    LOG.debug("initializing");

    final JCGLShadersType g_sh = in_g.getShaders();
    this.program_instance =
      R2StencilShaderSingle.newShader(g_sh, in_shader_env, in_pool);
    this.program_screen =
      R2StencilShaderScreen.newShader(g_sh, in_shader_env, in_pool);
    this.stencil_consumer =
      new StencilConsumer(this.program_instance);

    LOG.debug("initialized");
  }

  /**
   * @param in_shader_env Shader source access
   * @param in_g          An OpenGL interface
   * @param in_pool       The ID pool
   * @param in_quad       A unit quad
   *
   * @return A new renderer
   */

  public static R2StencilRendererType newRenderer(
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final JCGLInterfaceGL33Type in_g,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    return new R2StencilRenderer(in_shader_env, in_g, in_pool, in_quad);
  }

  @Override
  public void renderStencilsWithBoundBuffer(
    final R2MatricesObserverType m,
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType uc,
    final AreaInclusiveUnsignedLType area,
    final R2SceneStencilsType s)
  {
    NullCheck.notNull(m);
    NullCheck.notNull(pc);
    NullCheck.notNull(uc);
    NullCheck.notNull(area);
    NullCheck.notNull(s);

    Preconditions.checkPrecondition(
      !this.deleted, "Renderer must be deleted");

    final JCGLProfilingContextType pc_base =
      pc.getChildContext("stencil");
    final JCGLProfilingContextType pc_setup =
      pc_base.getChildContext("clear");
    pc_setup.startMeasuringIfEnabled();

    try {
      this.renderBase(area, s);
    } finally {
      pc_setup.stopMeasuringIfEnabled();
    }

    final JCGLProfilingContextType pc_instances =
      pc_base.getChildContext("instances");
    pc_instances.startMeasuringIfEnabled();

    try {
      this.renderInstances(area, m, uc, s);
    } finally {
      pc_instances.stopMeasuringIfEnabled();
    }
  }

  private void renderInstances(
    final AreaInclusiveUnsignedLType area,
    final R2MatricesObserverType m,
    final JCGLTextureUnitContextParentType uc,
    final R2SceneStencilsType s)
  {
    if (s.stencilsCount() > 0L) {
      final JCGLStencilBuffersType g_st = this.g.getStencilBuffers();
      switch (s.stencilsGetMode()) {
        case STENCIL_MODE_INSTANCES_ARE_NEGATIVE: {

          /*
           * Each instance will unset the {@link R2Stencils#ALLOW_BIT} for
           * each affected pixel.
           */

          g_st.stencilBufferFunction(
            JCGLFaceSelection.FACE_FRONT_AND_BACK,
            JCGLStencilFunction.STENCIL_ALWAYS,
            0,
            0);

          break;
        }
        case STENCIL_MODE_INSTANCES_ARE_POSITIVE: {

          /*
           * Each instance will set the {@link R2Stencils#ALLOW_BIT} for
           * each affected pixel.
           */

          g_st.stencilBufferFunction(
            JCGLFaceSelection.FACE_FRONT_AND_BACK,
            JCGLStencilFunction.STENCIL_ALWAYS,
            R2Stencils.ALLOW_BIT,
            R2Stencils.ALLOW_BIT);

          break;
        }
      }

      try {
        this.stencil_consumer.g33 = this.g;
        this.stencil_consumer.matrices = m;
        this.stencil_consumer.texture_context = uc;
        this.stencil_consumer.viewport_area = area;

        s.stencilsExecute(this.stencil_consumer);
      } finally {
        this.stencil_consumer.g33 = null;
        this.stencil_consumer.texture_context = null;
        this.stencil_consumer.matrices = null;
        this.stencil_consumer.viewport_area = null;
      }
    }
  }

  private void renderBase(
    final AreaInclusiveUnsignedLType area,
    final R2SceneStencilsType s)
  {
    final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
    final JCGLDepthBuffersType g_db = this.g.getDepthBuffers();
    final JCGLBlendingType g_b = this.g.getBlending();
    final JCGLColorBufferMaskingType g_cm = this.g.getColorBufferMasking();
    final JCGLCullingType g_cu = this.g.getCulling();
    final JCGLStencilBuffersType g_st = this.g.getStencilBuffers();
    final JCGLShadersType g_sh = this.g.getShaders();
    final JCGLDrawType g_dr = this.g.getDraw();
    final JCGLViewportsType g_v = this.g.getViewports();

    /*
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
    g_v.viewportSet(area);

    /*
     * Populate the stencil buffer with the values required for each
     * mode.
     */

    g_st.stencilBufferEnable();

    g_st.stencilBufferOperation(
      JCGLFaceSelection.FACE_FRONT_AND_BACK,
      JCGLStencilOperation.STENCIL_OP_KEEP,
      JCGLStencilOperation.STENCIL_OP_KEEP,
      JCGLStencilOperation.STENCIL_OP_REPLACE);

    /*
     * Allow writing to the {@link R2Stencils#ALLOW_BIT}.
     */

    g_st.stencilBufferMask(
      JCGLFaceSelection.FACE_FRONT_AND_BACK,
      R2Stencils.ALLOW_BIT);

    switch (s.stencilsGetMode()) {
      case STENCIL_MODE_INSTANCES_ARE_NEGATIVE: {

        /*
         * Set the {@link R2Stencils#ALLOW_BIT} for each pixel in the current
         * framebuffer, leaving other bits untouched.
         */

        g_st.stencilBufferFunction(
          JCGLFaceSelection.FACE_FRONT_AND_BACK,
          JCGLStencilFunction.STENCIL_ALWAYS,
          R2Stencils.ALLOW_BIT,
          R2Stencils.ALLOW_BIT);

        break;
      }
      case STENCIL_MODE_INSTANCES_ARE_POSITIVE: {

        /*
         * Unset the {@link R2Stencils#ALLOW_BIT} for each pixel in the current
         * framebuffer, leaving other bits untouched.
         */

        g_st.stencilBufferFunction(
          JCGLFaceSelection.FACE_FRONT_AND_BACK,
          JCGLStencilFunction.STENCIL_ALWAYS,
          0,
          0);

        break;
      }
    }

    /*
     * Render a screen-sized quad to provide the base stencil value.
     */

    g_sh.shaderActivateProgram(this.program_screen.shaderProgram());
    g_ao.arrayObjectBind(this.quad.arrayObject());
    g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
    g_ao.arrayObjectUnbind();
    g_sh.shaderDeactivateProgram();
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type gi)
    throws R2Exception
  {
    if (!this.isDeleted()) {
      try {
        LOG.debug("delete");
        this.program_instance.delete(gi);
        this.program_screen.delete(gi);
      } finally {
        this.deleted = true;
      }
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.deleted;
  }

  private static final class StencilConsumer implements
    R2SceneStencilsConsumerType
  {
    private final R2ShaderInstanceSingleType<Unit> program;
    private @Nullable JCGLInterfaceGL33Type g33;
    private @Nullable JCGLShadersType shaders;
    private @Nullable JCGLArrayObjectsType array_objects;
    private @Nullable JCGLDrawType draw;
    private @Nullable R2MatricesObserverType matrices;
    private @Nullable JCGLTextureUnitContextParentType texture_context;
    private @Nullable JCGLTexturesType textures;
    private @Nullable AreaInclusiveUnsignedLType viewport_area;

    StencilConsumer(
      final R2ShaderInstanceSingleType<Unit> in_program)
    {
      this.program = NullCheck.notNull(in_program);
    }

    @Override
    public void onStart()
    {
      this.shaders = this.g33.getShaders();
      this.textures = this.g33.getTextures();
      this.array_objects = this.g33.getArrayObjects();
      this.draw = this.g33.getDraw();

      this.program.onActivate(this.g33);
      this.program.onReceiveViewValues(
        this.shaders, this.matrices, this.viewport_area);
    }

    @Override
    public void onInstanceSingleStartArray(final R2InstanceSingleType i)
    {
      this.array_objects.arrayObjectBind(i.arrayObject());
    }

    @Override
    public void onInstanceSingle(final R2InstanceSingleType i)
    {
      final R2TransformReadableType it =
        i.transform();
      final PMatrixReadable3x3FType<R2SpaceTextureType, R2SpaceTextureType> uv =
        i.uvMatrix();

      this.matrices.withTransform(it, uv, this, (mi, t) -> {
        final JCGLTextureUnitContextType tc = t.texture_context.unitContextNew();
        try {
          t.program.onReceiveMaterialValues(
            t.textures, t.shaders, tc, Unit.unit());
          t.program.onReceiveInstanceTransformValues(t.shaders, mi);
          t.program.onValidate();
          t.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
        } finally {
          tc.unitContextFinish(t.textures);
        }
        return Unit.unit();
      });
    }

    @Override
    public void onFinish()
    {
      this.program.onDeactivate(this.g33);
      this.shaders = null;
      this.array_objects = null;
      this.draw = null;
    }
  }
}
