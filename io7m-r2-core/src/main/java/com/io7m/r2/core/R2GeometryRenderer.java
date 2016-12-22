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
import com.io7m.jcanephora.core.JCGLStencilFunction;
import com.io7m.jcanephora.core.JCGLStencilOperation;
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
import com.io7m.jcanephora.renderstate.JCGLRenderStateMutable;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.renderstate.JCGLStencilStateMutable;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBillboardedUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleUsableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * The default implementation of the {@link R2GeometryRendererType} interface.
 */

public final class R2GeometryRenderer implements R2GeometryRendererType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2GeometryRenderer.class);
  }

  private final OpaqueConsumer opaque_consumer;
  private final JCGLInterfaceGL33Type g;
  private final JCGLRenderState render_state_base;
  private boolean deleted;

  private R2GeometryRenderer(final JCGLInterfaceGL33Type in_g)
  {
    this.g = NullCheck.notNull(in_g);
    this.opaque_consumer = new OpaqueConsumer(this.g);

    {
      final JCGLRenderState.Builder b = JCGLRenderState.builder();

      /*
       * Only front faces are rendered.
       */

      b.setCullingState(Optional.of(JCGLCullingState.of(
        JCGLFaceSelection.FACE_BACK,
        JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE)));

      /*
       * Enable depth testing, writing, and clamping.
       */

      b.setDepthState(JCGLDepthState.of(
        JCGLDepthStrict.DEPTH_STRICT_ENABLED,
        Optional.of(JCGLDepthFunction.DEPTH_LESS_THAN),
        JCGLDepthWriting.DEPTH_WRITE_ENABLED,
        JCGLDepthClamping.DEPTH_CLAMP_ENABLED
      ));

      this.render_state_base = b.build();
    }
  }

  /**
   * Construct a new renderer.
   *
   * @param in_g An OpenGL interface
   *
   * @return A new renderer
   */

  public static R2GeometryRendererType newRenderer(
    final JCGLInterfaceGL33Type in_g)
  {
    return new R2GeometryRenderer(in_g);
  }

  @Override
  public void renderGeometry(
    final AreaInclusiveUnsignedLType area,
    final Optional<R2GeometryBufferUsableType> gbuffer,
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2SceneOpaquesReadableType s)
  {
    NullCheck.notNull(area);
    NullCheck.notNull(gbuffer);
    NullCheck.notNull(pc);
    NullCheck.notNull(uc);
    NullCheck.notNull(m);
    NullCheck.notNull(s);

    Preconditions.checkPrecondition(
      !this.isDeleted(), "Renderer must not be deleted");

    final JCGLProfilingContextType p_geom = pc.getChildContext("geometry");
    p_geom.startMeasuringIfEnabled();
    try {
      final JCGLFramebuffersType g_fb = this.g.getFramebuffers();
      final JCGLViewportsType g_v = this.g.getViewports();

      if (gbuffer.isPresent()) {
        final R2GeometryBufferUsableType gb = gbuffer.get();
        g_fb.framebufferDrawBind(gb.primaryFramebuffer());
      }

      if (s.opaquesCount() > 0L) {
        g_v.viewportSet(area);

        this.opaque_consumer.render_state.from(this.render_state_base);
        this.opaque_consumer.matrices = m;
        this.opaque_consumer.texture_context = uc;
        this.opaque_consumer.gbuffer_area = area;
        try {
          s.opaquesExecute(this.opaque_consumer);
        } finally {
          this.opaque_consumer.texture_context = null;
          this.opaque_consumer.matrices = null;
          this.opaque_consumer.gbuffer_area = null;
        }
      }
    } finally {
      p_geom.stopMeasuringIfEnabled();
    }
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g3)
    throws R2Exception
  {
    LOG.debug("delete");
    this.deleted = true;
  }

  @Override
  public boolean isDeleted()
  {
    return this.deleted;
  }

  private static final class OpaqueConsumer implements
    R2SceneOpaquesConsumerType
  {
    private final JCGLInterfaceGL33Type g33;
    private final JCGLShadersType shaders;
    private final JCGLTexturesType textures;
    private final JCGLArrayObjectsType array_objects;
    private final JCGLDrawType draw;
    private final JCGLRenderStateMutable render_state;
    private final JCGLStencilStateMutable stencil_state;

    private @Nullable R2MatricesObserverType matrices;
    private @Nullable JCGLTextureUnitContextParentType texture_context;
    private @Nullable JCGLTextureUnitContextType material_texture_context;
    private @Nullable R2MaterialOpaqueSingleType<?> material_single;
    private @Nullable AreaInclusiveUnsignedLType gbuffer_area;

    private OpaqueConsumer(
      final JCGLInterfaceGL33Type ig)
    {
      this.g33 = NullCheck.notNull(ig);
      this.shaders = this.g33.getShaders();
      this.textures = this.g33.getTextures();
      this.array_objects = this.g33.getArrayObjects();
      this.draw = this.g33.getDraw();
      this.render_state = JCGLRenderStateMutable.create();
      this.stencil_state = JCGLStencilStateMutable.create();
    }

    @Override
    public void onStart()
    {
      NullCheck.notNull(this.g33, "g33");
    }

    @Override
    public void onStartGroup(final int group)
    {
      /*
       * Only touch pixels that have the `ALLOW_BIT` set, and write the
       * given `group` number to the stencil buffer. Back faces are culled,
       * so are left unconfigured here.
       */

      this.stencil_state.setOperationDepthFailFront(
        JCGLStencilOperation.STENCIL_OP_KEEP);
      this.stencil_state.setOperationStencilFailFront(
        JCGLStencilOperation.STENCIL_OP_KEEP);
      this.stencil_state.setOperationPassFront(
        JCGLStencilOperation.STENCIL_OP_REPLACE);

      this.stencil_state.setTestFunctionFront(
        JCGLStencilFunction.STENCIL_EQUAL);
      this.stencil_state.setTestReferenceFront(
        (group << R2Stencils.GROUP_LEFT_SHIFT) | R2Stencils.ALLOW_BIT);
      this.stencil_state.setTestMaskFront(
        R2Stencils.ALLOW_BIT);

      this.stencil_state.setWriteMaskFrontFaces(
        R2Stencils.GROUP_BITS);

      this.stencil_state.setStencilStrict(true);
      this.stencil_state.setStencilEnabled(true);
      this.render_state.setStencilState(this.stencil_state);

      JCGLRenderStates.activate(this.g33, this.render_state);
    }

    @Override
    public void onInstanceBatchedUpdate(
      final R2InstanceBatchedType i)
    {
      i.update(this.g33, this.matrices.transformContext());
    }

    @Override
    public <M> void onInstanceBatchedShaderStart(
      final R2ShaderInstanceBatchedUsableType<M> s)
    {
      s.onActivate(this.g33);
      s.onReceiveViewValues(this.shaders, this.matrices, this.gbuffer_area);
    }

    @Override
    public <M> void onInstanceBatchedMaterialStart(
      final R2MaterialOpaqueBatchedType<M> material)
    {
      this.material_texture_context = this.texture_context.unitContextNew();

      final R2ShaderInstanceBatchedUsableType<M> s = material.shader();
      final M p = material.shaderParameters();
      s.onReceiveMaterialValues(
        this.textures, this.shaders, this.material_texture_context, p);
    }

    @Override
    public <M> void onInstanceBatched(
      final R2MaterialOpaqueBatchedType<M> material,
      final R2InstanceBatchedType i)
    {
      final R2ShaderInstanceBatchedUsableType<M> s = material.shader();
      s.onValidate();

      this.array_objects.arrayObjectBind(i.arrayObject());
      this.draw.drawElementsInstanced(
        JCGLPrimitives.PRIMITIVE_TRIANGLES, i.renderCount());
    }

    @Override
    public <M> void onInstanceBatchedMaterialFinish(
      final R2MaterialOpaqueBatchedType<M> material)
    {
      this.material_texture_context.unitContextFinish(this.textures);
      this.material_texture_context = null;
    }

    @Override
    public <M> void onInstanceBatchedShaderFinish(
      final R2ShaderInstanceBatchedUsableType<M> s)
    {
      s.onDeactivate(this.g33);
    }

    @Override
    public void onInstanceBillboardedUpdate(
      final R2InstanceBillboardedType i)
    {
      i.update(this.g33, this.matrices.transformContext());
    }

    @Override
    public <M> void onInstanceBillboardedShaderStart(
      final R2ShaderInstanceBillboardedUsableType<M> s)
    {
      s.onActivate(this.g33);
      s.onReceiveViewValues(this.shaders, this.matrices, this.gbuffer_area);
    }

    @Override
    public <M> void onInstanceBillboardedMaterialStart(
      final R2MaterialOpaqueBillboardedType<M> material)
    {
      this.material_texture_context = this.texture_context.unitContextNew();

      final R2ShaderInstanceBillboardedUsableType<M> s = material.shader();
      final M p = material.shaderParameters();
      s.onReceiveMaterialValues(
        this.textures, this.shaders, this.material_texture_context, p);
    }

    @Override
    public <M> void onInstanceBillboarded(
      final R2MaterialOpaqueBillboardedType<M> material,
      final R2InstanceBillboardedType i)
    {
      final R2ShaderInstanceBillboardedUsableType<M> s = material.shader();
      s.onValidate();

      this.array_objects.arrayObjectBind(i.arrayObject());
      this.draw.draw(JCGLPrimitives.PRIMITIVE_POINTS, 0, i.enabledCount());
    }

    @Override
    public <M> void onInstanceBillboardedMaterialFinish(
      final R2MaterialOpaqueBillboardedType<M> material)
    {
      this.material_texture_context.unitContextFinish(this.textures);
      this.material_texture_context = null;
    }

    @Override
    public <M> void onInstanceBillboardedShaderFinish(
      final R2ShaderInstanceBillboardedUsableType<M> s)
    {
      s.onDeactivate(this.g33);
    }

    @Override
    public <M> void onInstanceSingleShaderStart(
      final R2ShaderInstanceSingleUsableType<M> s)
    {
      s.onActivate(this.g33);
      s.onReceiveViewValues(this.shaders, this.matrices, this.gbuffer_area);
    }

    @Override
    public <M> void onInstanceSingleMaterialStart(
      final R2MaterialOpaqueSingleType<M> material)
    {
      this.material_single = material;
      this.material_texture_context = this.texture_context.unitContextNew();

      final R2ShaderInstanceSingleUsableType<M> s = material.shader();
      final M p = material.shaderParameters();
      s.onReceiveMaterialValues(
        this.textures, this.shaders, this.material_texture_context, p);
    }

    @Override
    public void onInstanceSingleArrayStart(
      final R2InstanceSingleType i)
    {
      this.array_objects.arrayObjectBind(i.arrayObject());
    }

    @Override
    public <M> void onInstanceSingle(
      final R2MaterialOpaqueSingleType<M> material,
      final R2InstanceSingleType i)
    {
      this.matrices.withTransform(
        i.transform(),
        i.uvMatrix(),
        this,
        (mi, t) -> {
          final R2ShaderInstanceSingleUsableType<?> s =
            t.material_single.shader();
          s.onReceiveInstanceTransformValues(t.shaders, mi);
          s.onValidate();
          t.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
          return Unit.unit();
        });
    }

    @Override
    public <M> void onInstanceSingleMaterialFinish(
      final R2MaterialOpaqueSingleType<M> material)
    {
      this.material_texture_context.unitContextFinish(this.textures);
      this.material_texture_context = null;
    }

    @Override
    public <M> void onInstanceSingleShaderFinish(
      final R2ShaderInstanceSingleUsableType<M> s)
    {
      s.onDeactivate(this.g33);
    }

    @Override
    public void onFinishGroup(final int group)
    {

    }

    @Override
    public void onFinish()
    {
      this.material_single = null;
    }
  }
}
