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

package com.io7m.r2.rendering.depth.variance;

import com.io7m.jaffirm.core.Invariants;
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jcanephora.core.JCGLClearSpecification;
import com.io7m.jcanephora.core.JCGLDepthFunction;
import com.io7m.jcanephora.core.JCGLFaceSelection;
import com.io7m.jcanephora.core.JCGLFaceWindingOrder;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.core.api.JCGLViewportsType;
import com.io7m.jcanephora.renderstate.JCGLColorBufferMaskingState;
import com.io7m.jcanephora.renderstate.JCGLCullingState;
import com.io7m.jcanephora.renderstate.JCGLCullingStateType;
import com.io7m.jcanephora.renderstate.JCGLDepthClamping;
import com.io7m.jcanephora.renderstate.JCGLDepthState;
import com.io7m.jcanephora.renderstate.JCGLDepthStrict;
import com.io7m.jcanephora.renderstate.JCGLDepthWriting;
import com.io7m.jcanephora.renderstate.JCGLRenderStateMutable;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jtensors.core.unparameterized.vectors.Vector4D;
import com.io7m.r2.core.api.R2Exception;
import com.io7m.r2.instances.R2InstanceBatchedType;
import com.io7m.r2.instances.R2InstanceSingleType;
import com.io7m.r2.matrices.R2MatricesObserverType;
import com.io7m.r2.rendering.depth.api.R2DepthInstancesConsumerType;
import com.io7m.r2.rendering.depth.api.R2DepthInstancesType;
import com.io7m.r2.rendering.depth.api.R2MaterialDepthBatchedType;
import com.io7m.r2.rendering.depth.api.R2MaterialDepthSingleType;
import com.io7m.r2.rendering.depth.variance.api.R2DepthVarianceBufferUsableType;
import com.io7m.r2.rendering.depth.variance.api.R2DepthVarianceRendererType;
import com.io7m.r2.shaders.api.R2ShaderParametersMaterialMutable;
import com.io7m.r2.shaders.api.R2ShaderParametersMaterialType;
import com.io7m.r2.shaders.api.R2ShaderParametersViewMutable;
import com.io7m.r2.shaders.depth.api.R2ShaderDepthBatchedUsableType;
import com.io7m.r2.shaders.depth.api.R2ShaderDepthSingleUsableType;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * The default implementation of the {@link R2DepthVarianceRendererType}
 * interface.
 */

public final class R2DepthVarianceRenderer
  implements R2DepthVarianceRendererType
{
  private final DepthConsumer depth_consumer;
  private final JCGLInterfaceGL33Type g;
  private boolean deleted;

  private R2DepthVarianceRenderer(
    final JCGLInterfaceGL33Type in_g)
  {
    this.g = NullCheck.notNull(in_g, "G33");
    this.depth_consumer = new DepthConsumer(this.g);
    final JCGLClearSpecification clear = JCGLClearSpecification.of(
      Optional.of(Vector4D.of(1.0, 1.0, 1.0, 1.0)),
      OptionalDouble.of(1.0),
      OptionalInt.empty(),
      true);
  }

  /**
   * Construct a new renderer.
   *
   * @param in_g An OpenGL interface
   *
   * @return A new renderer
   */

  public static R2DepthVarianceRenderer create(
    final JCGLInterfaceGL33Type in_g)
  {
    return new R2DepthVarianceRenderer(in_g);
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g3)
    throws R2Exception
  {
    this.deleted = true;
  }

  @Override
  public boolean isDeleted()
  {
    return this.deleted;
  }

  @Override
  public void renderDepthVariance(
    final R2DepthVarianceBufferUsableType dbuffer,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DepthInstancesType s)
  {
    NullCheck.notNull(dbuffer, "Depth buffer");
    NullCheck.notNull(uc, "Texture context");
    NullCheck.notNull(m, "Matrices");
    NullCheck.notNull(s, "Depth instances");

    Preconditions.checkPrecondition(
      !this.isDeleted(), "Renderer must not be deleted");

    final JCGLFramebufferUsableType gb_fb = dbuffer.primaryFramebuffer();
    final JCGLFramebuffersType g_fb = this.g.framebuffers();

    try {
      g_fb.framebufferDrawBind(gb_fb);
      this.renderDepthVarianceWithBoundBuffer(
        dbuffer.sizeAsViewport(), uc, m, s);
    } finally {
      g_fb.framebufferDrawUnbind();
    }
  }

  @Override
  public void renderDepthVarianceWithBoundBuffer(
    final AreaL area,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DepthInstancesType s)
  {
    NullCheck.notNull(uc, "Texture context");
    NullCheck.notNull(m, "Matrices");
    NullCheck.notNull(s, "Depth instances");

    Preconditions.checkPrecondition(
      !this.isDeleted(), "Renderer must not be deleted");

    final JCGLFramebuffersType g_fb = this.g.framebuffers();

    Preconditions.checkPrecondition(
      g_fb.framebufferDrawAnyIsBound(),
      "Framebuffer must be bound");

    final JCGLViewportsType g_v = this.g.viewports();

    if (s.depthsCount() > 0L) {
      g_v.viewportSet(area);

      this.depth_consumer.matrices = m;
      this.depth_consumer.texture_context = uc;
      this.depth_consumer.culling = s.depthsGetFaceCulling();
      this.depth_consumer.viewport_area = area;
      try {
        s.depthsExecute(this.depth_consumer);
      } finally {
        this.depth_consumer.texture_context = null;
        this.depth_consumer.matrices = null;
        this.depth_consumer.viewport_area = null;
      }
    }
  }

  private static final class DepthConsumer implements
    R2DepthInstancesConsumerType
  {
    private static final Optional<JCGLCullingStateType> CULL_BACK;
    private static final Optional<JCGLCullingStateType> CULL_FRONT;
    private static final Optional<JCGLCullingStateType> CULL_BOTH;

    static {
      CULL_BACK = Optional.of(
        JCGLCullingState.of(
          JCGLFaceSelection.FACE_BACK,
          JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE));

      CULL_FRONT = Optional.of(
        JCGLCullingState.of(
          JCGLFaceSelection.FACE_FRONT,
          JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE));

      CULL_BOTH = Optional.of(
        JCGLCullingState.of(
          JCGLFaceSelection.FACE_FRONT_AND_BACK,
          JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE));
    }

    private final JCGLInterfaceGL33Type g33;
    private final JCGLShadersType shaders;
    private final JCGLTexturesType textures;
    private final JCGLArrayObjectsType array_objects;
    private final JCGLDrawType draw;
    private final JCGLRenderStateMutable render_state;
    private final R2ShaderParametersViewMutable params_view;
    private final R2ShaderParametersMaterialMutable<Object> params_material;

    private @Nullable R2MatricesObserverType matrices;
    private @Nullable JCGLTextureUnitContextParentType texture_context;
    private @Nullable JCGLTextureUnitContextType material_texture_context;
    private @Nullable R2MaterialDepthSingleType<?> material_single;
    private @Nullable JCGLFaceSelection culling;
    private @Nullable AreaL viewport_area;

    private DepthConsumer(
      final JCGLInterfaceGL33Type ig)
    {
      this.g33 = NullCheck.notNull(ig, "G33");
      this.shaders = this.g33.shaders();
      this.textures = this.g33.textures();
      this.array_objects = this.g33.arrayObjects();
      this.draw = this.g33.drawing();
      this.params_view = R2ShaderParametersViewMutable.create();
      this.params_material = R2ShaderParametersMaterialMutable.create();

      {
        this.render_state = JCGLRenderStateMutable.create();

        /*
         * Enable color buffer rendering to the first two channels.
         */

        this.render_state.setColorBufferMaskingState(
          JCGLColorBufferMaskingState.of(true, true, false, false));

        /*
         * Enable depth testing, writing, and clamping.
         */

        this.render_state.setDepthState(JCGLDepthState.of(
          JCGLDepthStrict.DEPTH_STRICT_ENABLED,
          Optional.of(JCGLDepthFunction.DEPTH_LESS_THAN),
          JCGLDepthWriting.DEPTH_WRITE_ENABLED,
          JCGLDepthClamping.DEPTH_CLAMP_ENABLED
        ));
      }
    }

    @Override
    public void onStart()
    {
      NullCheck.notNull(this.g33, "g33");

      switch (this.culling) {
        case FACE_BACK:
          this.render_state.setCullingState(CULL_BACK);
          break;
        case FACE_FRONT:
          this.render_state.setCullingState(CULL_FRONT);
          break;
        case FACE_FRONT_AND_BACK:
          this.render_state.setCullingState(CULL_BOTH);
          break;
      }

      JCGLRenderStates.activate(this.g33, this.render_state);
    }

    @Override
    public void onInstanceBatchedUpdate(
      final R2InstanceBatchedType i)
    {
      if (i.updateRequired()) {
        i.update(this.g33);
      }
    }

    @Override
    public <M> void onInstanceBatchedShaderStart(
      final R2ShaderDepthBatchedUsableType<M> s)
    {
      this.shaders.shaderActivateProgram(s.shaderProgram());
      s.onReceiveViewValues(this.g33, this.configureViewParameters());
    }

    @Override
    public <M> void onInstanceBatchedMaterialStart(
      final R2MaterialDepthBatchedType<M> material)
    {
      this.material_texture_context = this.texture_context.unitContextNew();
      final R2ShaderDepthBatchedUsableType<M> s = material.shader();
      s.onReceiveMaterialValues(
        this.g33,
        this.configureMaterialParameters(
          this.material_texture_context, material.shaderParameters()));
    }

    @Override
    public <M> void onInstanceBatched(
      final R2MaterialDepthBatchedType<M> material,
      final R2InstanceBatchedType i)
    {
      material.shader().onValidate();

      this.array_objects.arrayObjectBind(i.arrayObject());
      this.draw.drawElementsInstanced(
        JCGLPrimitives.PRIMITIVE_TRIANGLES, i.renderCount());
    }

    @Override
    public <M> void onInstanceBatchedMaterialFinish(
      final R2MaterialDepthBatchedType<M> material)
    {
      this.material_texture_context.unitContextFinish(this.textures);
      this.material_texture_context = null;
    }

    @Override
    public <M> void onInstanceBatchedShaderFinish(
      final R2ShaderDepthBatchedUsableType<M> s)
    {
      s.onDeactivate(this.g33);
    }

    private R2ShaderParametersViewMutable configureViewParameters()
    {
      this.params_view.clear();
      this.params_view.setViewport(this.viewport_area);
      this.params_view.setObserverMatrices(this.matrices);
      Invariants.checkInvariant(
        this.params_view.isInitialized(),
        "View parameters must be initialized");
      return this.params_view;
    }

    @SuppressWarnings("unchecked")
    private <M> R2ShaderParametersMaterialType<M> configureMaterialParameters(
      final JCGLTextureUnitContextType tc,
      final M p)
    {
      this.params_material.clear();
      this.params_material.setTextureUnitContext(tc);
      this.params_material.setValues(p);
      Invariants.checkInvariant(
        this.params_material.isInitialized(),
        "Material parameters must be initialized");
      return (R2ShaderParametersMaterialType<M>) this.params_material;
    }

    @Override
    public <M> void onInstanceSingleShaderStart(
      final R2ShaderDepthSingleUsableType<M> s)
    {
      s.onActivate(this.g33);
      s.onReceiveViewValues(this.g33, this.configureViewParameters());
    }

    @Override
    public <M> void onInstanceSingleMaterialStart(
      final R2MaterialDepthSingleType<M> material)
    {
      this.material_single = material;
      this.material_texture_context = this.texture_context.unitContextNew();

      final R2ShaderDepthSingleUsableType<M> s = material.shader();
      s.onReceiveMaterialValues(
        this.g33,
        this.configureMaterialParameters(
          this.material_texture_context, material.shaderParameters()));
    }

    @Override
    public void onInstanceSingleArrayStart(
      final R2InstanceSingleType i)
    {
      this.array_objects.arrayObjectBind(i.arrayObject());
    }

    @Override
    public <M> void onInstanceSingle(
      final R2MaterialDepthSingleType<M> material,
      final R2InstanceSingleType i)
    {
      this.matrices.withTransform(
        i.transform(),
        i.uvMatrix(),
        this,
        (mi, t) -> {
          final R2ShaderDepthSingleUsableType<?> s =
            t.material_single.shader();
          s.onReceiveInstanceTransformValues(t.g33, mi);
          s.onValidate();

          t.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
          return Unit.unit();
        });
    }

    @Override
    public <M> void onInstanceSingleMaterialFinish(
      final R2MaterialDepthSingleType<M> material)
    {
      this.material_texture_context.unitContextFinish(this.textures);
      this.material_texture_context = null;
    }

    @Override
    public <M> void onInstanceSingleShaderFinish(
      final R2ShaderDepthSingleUsableType<M> s)
    {
      s.onDeactivate(this.g33);
    }

    @Override
    public void onFinish()
    {
      this.material_single = null;
    }
  }
}
