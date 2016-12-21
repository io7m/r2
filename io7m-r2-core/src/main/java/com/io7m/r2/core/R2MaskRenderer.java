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
import com.io7m.jcanephora.core.JCGLDepthFunction;
import com.io7m.jcanephora.core.JCGLFaceSelection;
import com.io7m.jcanephora.core.JCGLFaceWindingOrder;
import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.core.api.JCGLViewportsType;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.renderstate.JCGLCullingState;
import com.io7m.jcanephora.renderstate.JCGLDepthClamping;
import com.io7m.jcanephora.renderstate.JCGLDepthState;
import com.io7m.jcanephora.renderstate.JCGLDepthStrict;
import com.io7m.jcanephora.renderstate.JCGLDepthWriting;
import com.io7m.jcanephora.renderstate.JCGLRenderState;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.r2.core.shaders.provided.R2MaskShaderBatched;
import com.io7m.r2.core.shaders.provided.R2MaskShaderSingle;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * The default implementation of the {@link R2MaskRendererType} interface.
 */

public final class R2MaskRenderer implements R2MaskRendererType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MaskRenderer.class);
  }

  private final JCGLInterfaceGL33Type g;
  private final JCGLRenderState render_state;
  private final R2ShaderInstanceSingleType<Unit> shader_single;
  private final R2ShaderInstanceBatchedType<Unit> shader_batched;
  private boolean deleted;

  private R2MaskRenderer(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderInstanceSingleType<Unit> in_shader_single,
    final R2ShaderInstanceBatchedType<Unit> in_shader_batched)
  {
    this.g =
      NullCheck.notNull(in_g);
    this.shader_single =
      NullCheck.notNull(in_shader_single, "Shader single");
    this.shader_batched =
      NullCheck.notNull(in_shader_batched, "Shader batched");

    {
      final JCGLRenderState.Builder b = JCGLRenderState.builder();

      /*
       * Only front faces are rendered.
       */

      b.setCullingState(Optional.of(JCGLCullingState.of(
        JCGLFaceSelection.FACE_BACK,
        JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE)));

      /*
       * Enable depth testing and clamping.
       */

      b.setDepthState(JCGLDepthState.of(
        JCGLDepthStrict.DEPTH_STRICT_ENABLED,
        Optional.of(JCGLDepthFunction.DEPTH_LESS_THAN),
        JCGLDepthWriting.DEPTH_WRITE_DISABLED,
        JCGLDepthClamping.DEPTH_CLAMP_ENABLED));

      this.render_state = b.build();
    }
  }

  /**
   * Construct a new renderer.
   *
   * @param in_g          An OpenGL interface
   * @param in_shader_env A shader preprocessing environment
   * @param in_pool       An ID pool
   *
   * @return A new renderer
   */

  public static R2MaskRendererType newRenderer(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    final R2ShaderInstanceSingleType<Unit> shader_single =
      R2MaskShaderSingle.newShader(in_g.getShaders(), in_shader_env, in_pool);
    final R2ShaderInstanceBatchedType<Unit> shader_batched =
      R2MaskShaderBatched.newShader(in_g.getShaders(), in_shader_env, in_pool);

    return new R2MaskRenderer(in_g, shader_single, shader_batched);
  }

  @Override
  public void renderMask(
    final AreaInclusiveUnsignedLType area,
    final Optional<R2MaskBufferUsableType> mbuffer,
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType tucp,
    final R2MatricesObserverType m,
    final R2MaskInstancesType s)
  {
    NullCheck.notNull(area);
    NullCheck.notNull(mbuffer);
    NullCheck.notNull(pc);
    NullCheck.notNull(tucp);
    NullCheck.notNull(m);
    NullCheck.notNull(s);

    Preconditions.checkPrecondition(
      !this.isDeleted(), "Renderer must not be deleted");

    final JCGLProfilingContextType p_mask = pc.getChildContext("mask");
    p_mask.startMeasuringIfEnabled();
    try {
      final JCGLFramebuffersType g_fb = this.g.getFramebuffers();
      final JCGLViewportsType g_v = this.g.getViewports();

      if (mbuffer.isPresent()) {
        final R2MaskBufferUsableType gb = mbuffer.get();
        g_fb.framebufferDrawBind(gb.primaryFramebuffer());
      }

      g_v.viewportSet(area);

      JCGLRenderStates.activate(this.g, this.render_state);
      final JCGLTexturesType g_tx = this.g.getTextures();
      final JCGLTextureUnitContextType up = tucp.unitContextNew();
      try {
        this.renderSingles(area, m, s, up);
        this.renderBatches(area, m, s, up);
      } finally {
        up.unitContextFinish(g_tx);
      }

    } finally {
      p_mask.stopMeasuringIfEnabled();
    }
  }

  private void renderBatches(
    final AreaInclusiveUnsignedLType area,
    final R2MatricesObserverType m,
    final R2MaskInstancesType s,
    final JCGLTextureUnitContextType up)
  {
    final JCGLTexturesType g_tx = this.g.getTextures();
    final JCGLShadersType g_sh = this.g.getShaders();
    final JCGLArrayObjectsType g_a = this.g.getArrayObjects();
    final JCGLDrawType g_d = this.g.getDraw();

    final List<R2InstanceBatchedType> batches = s.batched();
    if (!batches.isEmpty()) {

      this.shader_batched.onActivate(g_sh);
      try {
        this.shader_batched.onReceiveViewValues(g_sh, m, area);
        this.shader_batched.onReceiveMaterialValues(
          g_tx, g_sh, up, Unit.unit());

        for (int index = 0; index < batches.size(); ++index) {
          final R2InstanceBatchedType instance = batches.get(index);
          instance.update(this.g, m.transformContext());
          g_a.arrayObjectBind(instance.arrayObject());

          this.shader_batched.onValidate();
          g_d.drawElementsInstanced(
            JCGLPrimitives.PRIMITIVE_TRIANGLES, instance.renderCount());
        }
      } finally {
        this.shader_batched.onDeactivate(g_sh);
        g_a.arrayObjectUnbind();
      }
    }
  }

  private void renderSingles(
    final AreaInclusiveUnsignedLType area,
    final R2MatricesObserverType m,
    final R2MaskInstancesType s,
    final JCGLTextureUnitContextMutableType up)
  {
    final JCGLTexturesType g_tx = this.g.getTextures();
    final JCGLShadersType g_sh = this.g.getShaders();
    final JCGLArrayObjectsType g_a = this.g.getArrayObjects();

    final List<R2InstanceSingleType> singles = s.singles();
    if (!singles.isEmpty()) {

      this.shader_single.onActivate(g_sh);
      try {
        this.shader_single.onReceiveViewValues(g_sh, m, area);
        this.shader_single.onReceiveMaterialValues(
          g_tx, g_sh, up, Unit.unit());

        for (int index = 0; index < singles.size(); ++index) {
          final R2InstanceSingleType instance = singles.get(index);
          g_a.arrayObjectBind(instance.arrayObject());
          m.withTransform(
            instance.transform(),
            PMatrixI3x3F.identity(),
            this,
            (mi, tt) -> {
              tt.shader_single.onReceiveInstanceTransformValues(
                tt.g.getShaders(), mi);
              tt.shader_single.onValidate();
              tt.g.getDraw().drawElements(
                JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });
        }
      } finally {
        this.shader_single.onDeactivate(g_sh);
        g_a.arrayObjectUnbind();
      }
    }
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g3)
    throws R2Exception
  {
    LOG.debug("delete");

    if (!this.shader_single.isDeleted()) {
      try {
        this.shader_single.delete(this.g);
        this.shader_batched.delete(this.g);
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
}
