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
import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.JCGLStencilFunction;
import com.io7m.jcanephora.core.JCGLStencilOperation;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.renderstate.JCGLDepthClamping;
import com.io7m.jcanephora.renderstate.JCGLDepthState;
import com.io7m.jcanephora.renderstate.JCGLDepthStrict;
import com.io7m.jcanephora.renderstate.JCGLDepthWriting;
import com.io7m.jcanephora.renderstate.JCGLRenderState;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.renderstate.JCGLStencilState;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.shaders.types.R2ShaderTranslucentInstanceBatchedUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderTranslucentInstanceBillboardedUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderTranslucentInstanceSingleUsableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * The default implementation of the {@link R2TranslucentRendererType}
 * interface.
 */

public final class R2TranslucentRenderer implements R2TranslucentRendererType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2TranslucentRenderer.class);
  }

  private final JCGLInterfaceGL33Type g33;
  private final JCGLRenderState render_state;
  private R2ShadowMapContextUsableType shadows;
  private R2MatricesObserverType matrices;
  private JCGLTextureUnitContextParentType texture_units;
  private boolean deleted;
  private AreaInclusiveUnsignedLType viewport;
  private R2TranslucentSingleType<?> single;

  private R2TranslucentRenderer(
    final JCGLInterfaceGL33Type g3)
  {
    this.g33 = NullCheck.notNull(g3, "g33");

    final JCGLStencilState stencil_state =
      JCGLStencilState.builder()
        .setOperationDepthFailBack(JCGLStencilOperation.STENCIL_OP_KEEP)
        .setOperationDepthFailFront(JCGLStencilOperation.STENCIL_OP_KEEP)
        .setOperationPassBack(JCGLStencilOperation.STENCIL_OP_KEEP)
        .setOperationPassFront(JCGLStencilOperation.STENCIL_OP_KEEP)
        .setOperationStencilFailBack(JCGLStencilOperation.STENCIL_OP_KEEP)
        .setOperationStencilFailFront(JCGLStencilOperation.STENCIL_OP_KEEP)
        .setTestFunctionBack(JCGLStencilFunction.STENCIL_EQUAL)
        .setTestFunctionFront(JCGLStencilFunction.STENCIL_EQUAL)
        .setWriteMaskBackFaces(0)
        .setWriteMaskFrontFaces(0)
        .setTestReferenceBack(0b1111_1111)
        .setTestReferenceFront(0b1111_1111)
        .setTestMaskBack(R2Stencils.ALLOW_BIT)
        .setTestMaskFront(R2Stencils.ALLOW_BIT)
        .setStencilEnabled(true)
        .setStencilStrict(true)
        .build();

    final JCGLDepthState depth_state =
      JCGLDepthState.builder()
        .setDepthClamp(JCGLDepthClamping.DEPTH_CLAMP_ENABLED)
        .setDepthStrict(JCGLDepthStrict.DEPTH_STRICT_ENABLED)
        .setDepthTest(JCGLDepthFunction.DEPTH_LESS_THAN)
        .setDepthWrite(JCGLDepthWriting.DEPTH_WRITE_DISABLED)
        .build();

    this.render_state =
      JCGLRenderState.builder()
        .setDepthState(depth_state)
        .setStencilState(stencil_state)
        .build();
  }

  /**
   * @param g33 A GL interface
   *
   * @return A new translucent renderer
   */

  public static R2TranslucentRendererType newRenderer(
    final JCGLInterfaceGL33Type g33)
  {
    return new R2TranslucentRenderer(g33);
  }

  @Override
  public void renderTranslucents(
    final AreaInclusiveUnsignedLType area,
    final Optional<R2ImageBufferUsableType> ibuffer,
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType uc,
    final R2ShadowMapContextUsableType in_shadows,
    final R2MatricesObserverType m,
    final List<R2TranslucentType<?>> s)
  {
    try {
      this.viewport = NullCheck.notNull(area);
      NullCheck.notNull(ibuffer);
      NullCheck.notNull(pc);
      this.texture_units = NullCheck.notNull(uc);
      this.shadows = NullCheck.notNull(in_shadows);
      this.matrices = NullCheck.notNull(m);
      NullCheck.notNull(s);

      Preconditions.checkPrecondition(
        !this.isDeleted(), "Renderer must not be deleted");

      final JCGLProfilingContextType pc_base =
        pc.getChildContext("translucents");

      try {
        pc_base.startMeasuringIfEnabled();

        this.g33.getViewports().viewportSet(area);

        for (int index = 0; index < s.size(); ++index) {
          final R2TranslucentType<?> translucent =
            NullCheck.notNull(s.get(index), "Translucent");
          translucent.matchTranslucent(
            this,
            (x, y) -> this.renderSingle(y),
            (x, y) -> this.renderBatched(y),
            (x, y) -> this.renderBillboarded(y));
        }
      } finally {
        pc_base.stopMeasuringIfEnabled();
      }

    } finally {
      this.shadows = null;
      this.matrices = null;
      this.texture_units = null;
    }
  }

  @SuppressWarnings("unchecked")
  private <T> Unit renderBatched(
    final R2TranslucentBatchedType<T> b)
  {
    final JCGLShadersType g_sh = this.g33.getShaders();
    final JCGLTexturesType g_tex = this.g33.getTextures();
    final JCGLArrayObjectsType g_ao = this.g33.getArrayObjects();
    final JCGLDrawType g_dr = this.g33.getDraw();

    final R2ShaderTranslucentInstanceBatchedUsableType<T> shader = b.shader();
    final T params = b.shaderParameters();

    final JCGLTextureUnitContextType tc =
      this.texture_units.unitContextNew();

    JCGLRenderStates.activate(
      this.g33,
      JCGLRenderState.builder()
        .from(this.render_state)
        .setBlendState(b.blending().map(x -> x))
        .setCullingState(b.culling())
        .build());

    b.instance().update(this.g33, this.matrices.transformContext());

    try {
      shader.onActivate(g_sh);
      try {
        shader.onReceiveViewValues(g_sh, this.matrices, this.viewport);
        shader.onReceiveMaterialValues(g_tex, g_sh, tc, params);
        shader.onValidate();
        g_ao.arrayObjectBind(b.instance().arrayObject());
        g_dr.drawElementsInstanced(
          JCGLPrimitives.PRIMITIVE_TRIANGLES,
          b.instance().renderCount());
      } finally {
        shader.onDeactivate(g_sh);
      }
    } finally {
      tc.unitContextFinish(g_tex);
    }

    return Unit.unit();
  }

  @SuppressWarnings("unchecked")
  private <T> Unit renderBillboarded(
    final R2TranslucentBillboardedType<T> b)
  {
    final JCGLShadersType g_sh = this.g33.getShaders();
    final JCGLTexturesType g_tex = this.g33.getTextures();
    final JCGLArrayObjectsType g_ao = this.g33.getArrayObjects();
    final JCGLDrawType g_dr = this.g33.getDraw();

    final R2ShaderTranslucentInstanceBillboardedUsableType<T> shader = b.shader();
    final T params = b.shaderParameters();

    final JCGLTextureUnitContextType tc =
      this.texture_units.unitContextNew();

    JCGLRenderStates.activate(
      this.g33,
      JCGLRenderState.builder()
        .from(this.render_state)
        .setBlendState(b.blending().map(x -> x))
        .setCullingState(b.culling())
        .build());

    b.instance().update(this.g33, this.matrices.transformContext());

    try {
      shader.onActivate(g_sh);
      try {
        shader.onReceiveViewValues(g_sh, this.matrices, this.viewport);
        shader.onReceiveMaterialValues(g_tex, g_sh, tc, params);
        shader.onValidate();
        g_ao.arrayObjectBind(b.instance().arrayObject());
        g_dr.draw(
          JCGLPrimitives.PRIMITIVE_POINTS, 0, b.instance().enabledCount());
      } finally {
        shader.onDeactivate(g_sh);
      }
    } finally {
      tc.unitContextFinish(g_tex);
    }

    return Unit.unit();
  }

  @SuppressWarnings("unchecked")
  private <T> Unit renderSingleTransformed(
    final R2MatricesInstanceSingleValuesType mi)
  {
    final R2TranslucentSingleType<T> s =
      (R2TranslucentSingleType<T>) this.single;

    final JCGLShadersType g_sh = this.g33.getShaders();
    final JCGLTexturesType g_tex = this.g33.getTextures();
    final JCGLArrayObjectsType g_ao = this.g33.getArrayObjects();
    final JCGLDrawType g_dr = this.g33.getDraw();

    final R2ShaderTranslucentInstanceSingleUsableType<T> shader = s.shader();
    final T params = s.shaderParameters();

    final JCGLTextureUnitContextType tc =
      this.texture_units.unitContextNew();

    JCGLRenderStates.activate(
      this.g33,
      JCGLRenderState.builder()
        .from(this.render_state)
        .setBlendState(s.blending().map(x -> x))
        .setCullingState(s.culling())
        .build());

    try {
      shader.onActivate(g_sh);
      try {
        shader.onReceiveViewValues(g_sh, this.matrices, this.viewport);
        shader.onReceiveMaterialValues(g_tex, g_sh, tc, params);
        shader.onReceiveInstanceTransformValues(g_sh, mi);
        shader.onValidate();
        g_ao.arrayObjectBind(s.instance().arrayObject());
        g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
      } finally {
        shader.onDeactivate(g_sh);
      }
    } finally {
      tc.unitContextFinish(g_tex);
    }

    return Unit.unit();
  }

  @SuppressWarnings("unchecked")
  private <T> Unit renderSingle(
    final R2TranslucentSingleType<?> s)
  {
    this.single = s;
    try {
      return this.matrices.withTransform(
        s.instance().transform(),
        s.instance().uvMatrix(),
        this,
        (mi, tt) -> tt.renderSingleTransformed(mi));
    } finally {
      this.single = null;
    }
  }

  @Override
  public void delete(
    final JCGLInterfaceGL33Type g3)
    throws R2Exception
  {
    NullCheck.notNull(g3);

    LOG.debug("delete");
    this.deleted = true;
  }

  @Override
  public boolean isDeleted()
  {
    return this.deleted;
  }
}
