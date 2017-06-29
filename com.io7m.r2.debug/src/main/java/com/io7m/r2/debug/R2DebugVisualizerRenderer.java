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

package com.io7m.r2.debug;

import com.io7m.jaffirm.core.Invariants;
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.JCGLDepthFunction;
import com.io7m.jcanephora.core.JCGLFaceSelection;
import com.io7m.jcanephora.core.JCGLFaceWindingOrder;
import com.io7m.jcanephora.core.JCGLPolygonMode;
import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
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
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices3x3D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix3x3D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.jtensors.core.parameterized.vectors.PVectors4D;
import com.io7m.r2.core.api.R2Exception;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.debug.api.R2DebugCubeInstance;
import com.io7m.r2.debug.api.R2DebugInstanceSingle;
import com.io7m.r2.debug.api.R2DebugInstancesType;
import com.io7m.r2.debug.api.R2DebugLineSegment;
import com.io7m.r2.debug.api.R2DebugVisualizerRendererParameters;
import com.io7m.r2.debug.api.R2DebugVisualizerRendererParametersType;
import com.io7m.r2.debug.api.R2DebugVisualizerRendererType;
import com.io7m.r2.instances.R2InstanceBatchedType;
import com.io7m.r2.instances.R2InstanceBillboardedType;
import com.io7m.r2.instances.R2InstanceSingleType;
import com.io7m.r2.lights.R2LightProjectiveReadableType;
import com.io7m.r2.lights.R2LightScreenSingleType;
import com.io7m.r2.lights.R2LightSingleReadableType;
import com.io7m.r2.lights.R2LightSphericalSingleReadableType;
import com.io7m.r2.lights.R2LightVolumeSingleType;
import com.io7m.r2.matrices.R2MatricesObserverType;
import com.io7m.r2.rendering.geometry.api.R2MaterialOpaqueBatchedType;
import com.io7m.r2.rendering.geometry.api.R2MaterialOpaqueBillboardedType;
import com.io7m.r2.rendering.geometry.api.R2MaterialOpaqueSingleType;
import com.io7m.r2.rendering.geometry.api.R2SceneOpaquesConsumerType;
import com.io7m.r2.rendering.geometry.api.R2SceneOpaquesReadableType;
import com.io7m.r2.rendering.lights.api.R2SceneLightsClipGroupConsumerType;
import com.io7m.r2.rendering.lights.api.R2SceneLightsConsumerType;
import com.io7m.r2.rendering.lights.api.R2SceneLightsGroupConsumerType;
import com.io7m.r2.rendering.lights.api.R2SceneLightsType;
import com.io7m.r2.shaders.api.R2ShaderInstanceBatchedType;
import com.io7m.r2.shaders.api.R2ShaderInstanceBatchedUsableType;
import com.io7m.r2.shaders.api.R2ShaderInstanceBillboardedType;
import com.io7m.r2.shaders.api.R2ShaderInstanceBillboardedUsableType;
import com.io7m.r2.shaders.api.R2ShaderInstanceSingleScreenType;
import com.io7m.r2.shaders.api.R2ShaderInstanceSingleType;
import com.io7m.r2.shaders.api.R2ShaderInstanceSingleUsableType;
import com.io7m.r2.shaders.api.R2ShaderParametersMaterialMutable;
import com.io7m.r2.shaders.api.R2ShaderParametersMaterialType;
import com.io7m.r2.shaders.api.R2ShaderParametersViewMutable;
import com.io7m.r2.shaders.api.R2ShaderParametersViewType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.debug.R2ShaderDebugColorVerticesWorldPosition;
import com.io7m.r2.shaders.debug.R2ShaderDebugVisualBatched;
import com.io7m.r2.shaders.debug.R2ShaderDebugVisualBillboarded;
import com.io7m.r2.shaders.debug.R2ShaderDebugVisualScreen;
import com.io7m.r2.shaders.debug.R2ShaderDebugVisualSingle;
import com.io7m.r2.shaders.light.api.R2ShaderLightSingleUsableType;
import com.io7m.r2.spaces.R2SpaceRGBAType;
import com.io7m.r2.spaces.R2SpaceRGBType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import com.io7m.r2.transforms.R2TransformST;
import com.io7m.r2.unit_spheres.R2UnitSphereUsableType;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * The default implementation of the {@link R2DebugVisualizerRendererType}
 * interface.
 */

public final class R2DebugVisualizerRenderer
  implements R2DebugVisualizerRendererType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2DebugVisualizerRenderer.class);
  }

  private final JCGLInterfaceGL33Type g;

  private final R2ShaderInstanceSingleType<PVector4D<R2SpaceRGBAType>> shader_single;
  private final R2ShaderInstanceBatchedType<PVector4D<R2SpaceRGBAType>> shader_batched;
  private final R2ShaderInstanceBillboardedType<PVector4D<R2SpaceRGBAType>> shader_billboarded;
  private final R2ShaderInstanceSingleScreenType<PVector4D<R2SpaceRGBAType>> shader_screen;
  private final R2ShaderInstanceSingleScreenType<Unit> shader_lines;

  private final JCGLRenderState render_geom_state_base;
  private final OpaqueConsumer opaque_consumer;
  private final LightConsumer light_consumer;
  private final R2DebugLineSegmentsBatch lines_batch;

  private final R2ShaderParametersViewMutable params_view;
  private final R2ShaderParametersMaterialMutable<Object> params_material;

  private R2DebugVisualizerRenderer(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    this.g = NullCheck.notNull(in_g, "GL33");
    this.shader_single = R2ShaderDebugVisualSingle.create(
      this.g.shaders(), in_shader_env, in_pool);
    this.shader_batched = R2ShaderDebugVisualBatched.create(
      this.g.shaders(), in_shader_env, in_pool);
    this.shader_billboarded = R2ShaderDebugVisualBillboarded.create(
      this.g.shaders(), in_shader_env, in_pool);
    this.shader_screen = R2ShaderDebugVisualScreen.create(
      this.g.shaders(), in_shader_env, in_pool);
    this.shader_lines = R2ShaderDebugColorVerticesWorldPosition.create(
      this.g.shaders(), in_shader_env, in_pool);

    {
      final JCGLRenderState.Builder b = JCGLRenderState.builder();

      /*
       * Only front faces are rendered.
       */

      b.setCullingState(Optional.of(JCGLCullingState.of(
        JCGLFaceSelection.FACE_BACK,
        JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE)));

      /*
       * Enable depth testing and clamping. Note that the geometry in the
       * scene was originally rendered with DEPTH_LESS_THAN, so overwriting
       * is allowed here with DEPTH_LESS_THAN_OR_EQUAL.
       */

      b.setDepthState(JCGLDepthState.of(
        JCGLDepthStrict.DEPTH_STRICT_ENABLED,
        Optional.of(JCGLDepthFunction.DEPTH_LESS_THAN_OR_EQUAL),
        JCGLDepthWriting.DEPTH_WRITE_DISABLED,
        JCGLDepthClamping.DEPTH_CLAMP_ENABLED
      ));

      /*
       * Only render wireframes.
       */

      b.setPolygonMode(JCGLPolygonMode.POLYGON_LINES);

      this.render_geom_state_base = b.build();
    }

    this.opaque_consumer =
      new OpaqueConsumer(
        this.g,
        this.shader_single,
        this.shader_batched,
        this.shader_billboarded);
    this.light_consumer =
      new LightConsumer(
        this.g,
        this.shader_single,
        this.shader_batched,
        this.shader_screen);

    this.lines_batch = new R2DebugLineSegmentsBatch(this.g);

    this.params_view = R2ShaderParametersViewMutable.create();
    this.params_material = R2ShaderParametersMaterialMutable.create();
  }

  /**
   * Construct a new renderer.
   *
   * @param in_g          An OpenGL interface
   * @param in_pool       The ID pool
   * @param in_shader_env Access to shader sources
   *
   * @return A new renderer
   */

  public static R2DebugVisualizerRenderer create(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool)
  {
    return new R2DebugVisualizerRenderer(in_g, in_shader_env, in_pool);
  }

  @Override
  public void renderScene(
    final AreaL area,
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugVisualizerRendererParameters s)
  {
    NullCheck.notNull(area, "Area");
    NullCheck.notNull(pc, "Profiling");
    NullCheck.notNull(uc, "Texture context");
    NullCheck.notNull(m, "Matrices");
    NullCheck.notNull(s, "Parameters");

    Preconditions.checkPrecondition(
      !this.isDeleted(), "Renderer must not be deleted");

    final JCGLProfilingContextType pc_base =
      pc.childContext("debug-visualizer");

    {
      final JCGLProfilingContextType pc_opaques =
        pc_base.childContext("opaques");
      pc_opaques.startMeasuringIfEnabled();
      try {
        this.renderSceneOpaques(area, uc, m, s);
      } finally {
        pc_opaques.stopMeasuringIfEnabled();
      }
    }

    {
      final JCGLProfilingContextType pc_lights =
        pc_base.childContext("lights");
      pc_lights.startMeasuringIfEnabled();
      try {
        this.renderSceneLights(area, uc, m, s);
      } finally {
        pc_lights.stopMeasuringIfEnabled();
      }
    }

    {
      final JCGLProfilingContextType pc_extras =
        pc_base.childContext("extras");
      pc_extras.startMeasuringIfEnabled();
      try {
        this.renderSceneExtras(area, uc, m, s);
      } finally {
        pc_extras.stopMeasuringIfEnabled();
      }
    }
  }

  private void renderSceneExtras(
    final AreaL area,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugVisualizerRendererParametersType p)
  {
    final R2DebugInstancesType extras = p.debugInstances();
    this.renderSceneExtrasLineSegments(area, uc, m, extras);
    this.renderSceneExtrasBoxes(area, uc, m, p, extras);
    this.renderSceneExtrasInstanceSingles(area, uc, m, p, extras);
  }

  @SuppressWarnings("unchecked")
  private <M> R2ShaderParametersMaterialType<M> configureMaterialParameters(
    final JCGLTextureUnitContextMutableType tc,
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

  private R2ShaderParametersViewType configureViewParameters(
    final AreaL area,
    final R2MatricesObserverType matrices)
  {
    this.params_view.clear();
    this.params_view.setViewport(area);
    this.params_view.setObserverMatrices(matrices);
    Invariants.checkInvariant(
      this.params_view.isInitialized(),
      "View parameters must be initialized");
    return this.params_view;
  }

  private void renderSceneExtrasInstanceSingles(
    final AreaL area,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugVisualizerRendererParametersType p,
    final R2DebugInstancesType extras)
  {
    final List<R2DebugInstanceSingle> singles = extras.instanceSingles();
    if (!singles.isEmpty()) {
      final JCGLViewportsType g_v = this.g.viewports();
      g_v.viewportSet(area);

      JCGLRenderStates.activate(this.g, this.render_geom_state_base);

      final JCGLTextureUnitContextType tc = uc.unitContextNew();
      try {
        this.shader_single.onActivate(this.g);
        try {
          this.shader_single.onReceiveViewValues(
            this.g, this.configureViewParameters(area, m));

          for (int index = 0; index < singles.size(); ++index) {
            final R2DebugInstanceSingle single = singles.get(index);

            final JCGLArrayObjectUsableType ao = single.instance().arrayObject();
            final JCGLArrayObjectsType g_ao = this.g.arrayObjects();
            try {
              g_ao.arrayObjectBind(ao);

              this.shader_single.onReceiveMaterialValues(
                this.g,
                this.configureMaterialParameters(tc, single.color()));

              m.withTransform(
                single.instance().transform(),
                PMatrices3x3D.identity(),
                this,
                (m_instance, t) -> {
                  t.shader_single.onReceiveInstanceTransformValues(
                    t.g, m_instance);
                  t.shader_single.onValidate();
                  t.g.drawing().drawElements(
                    JCGLPrimitives.PRIMITIVE_TRIANGLES);
                  return Unit.unit();
                });

            } finally {
              g_ao.arrayObjectUnbind();
            }
          }

        } finally {
          this.shader_single.onDeactivate(this.g);
        }
      } finally {
        tc.unitContextFinish(this.g.textures());
      }
    }
  }

  private void renderSceneExtrasBoxes(
    final AreaL area,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugVisualizerRendererParametersType p,
    final R2DebugInstancesType extras)
  {
    final List<R2DebugCubeInstance> cubes = extras.cubes();
    if (!cubes.isEmpty()) {
      final JCGLViewportsType g_v = this.g.viewports();
      g_v.viewportSet(area);

      JCGLRenderStates.activate(this.g, this.render_geom_state_base);

      final JCGLTextureUnitContextType tc = uc.unitContextNew();
      try {
        this.shader_single.onActivate(this.g);
        try {
          this.shader_single.onReceiveViewValues(
            this.g, this.configureViewParameters(area, m));

          final JCGLArrayObjectUsableType ao = p.debugCube().arrayObject();
          final JCGLArrayObjectsType g_ao = this.g.arrayObjects();
          try {
            g_ao.arrayObjectBind(ao);

            for (int index = 0; index < cubes.size(); ++index) {
              final R2DebugCubeInstance cube = cubes.get(index);

              this.shader_single.onReceiveMaterialValues(
                this.g,
                this.configureMaterialParameters(tc, cube.color()));

              m.withTransform(
                cube.transform(),
                PMatrices3x3D.identity(),
                this,
                (m_instance, t) -> {
                  t.shader_single.onReceiveInstanceTransformValues(
                    t.g, m_instance);
                  t.shader_single.onValidate();
                  t.g.drawing().drawElements(JCGLPrimitives.PRIMITIVE_LINES);
                  return Unit.unit();
                });
            }

          } finally {
            g_ao.arrayObjectUnbind();
          }

        } finally {
          this.shader_single.onDeactivate(this.g);
        }
      } finally {
        tc.unitContextFinish(this.g.textures());
      }
    }
  }

  private void renderSceneExtrasLineSegments(
    final AreaL area,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugInstancesType extras)
  {
    final List<R2DebugLineSegment> segments = extras.lineSegments();
    if (!segments.isEmpty()) {
      this.lines_batch.setLineSegments(segments);

      final JCGLViewportsType g_v = this.g.viewports();
      g_v.viewportSet(area);

      JCGLRenderStates.activate(this.g, this.render_geom_state_base);

      final JCGLTextureUnitContextType tc = uc.unitContextNew();
      try {
        this.shader_lines.onActivate(this.g);
        try {
          this.shader_lines.onReceiveViewValues(
            this.g, this.configureViewParameters(area, m));
          this.shader_lines.onReceiveMaterialValues(
            this.g, this.configureMaterialParameters(tc, Unit.unit()));
          this.shader_lines.onValidate();

          final JCGLArrayObjectUsableType ao = this.lines_batch.arrayObject();
          final JCGLArrayObjectsType g_ao = this.g.arrayObjects();
          final JCGLDrawType g_d = this.g.drawing();
          try {
            g_ao.arrayObjectBind(ao);
            g_d.draw(JCGLPrimitives.PRIMITIVE_LINES, 0, segments.size() * 2);
          } finally {
            g_ao.arrayObjectUnbind();
          }

        } finally {
          this.shader_lines.onDeactivate(this.g);
        }
      } finally {
        tc.unitContextFinish(this.g.textures());
      }
    }
  }

  private void renderSceneLights(
    final AreaL area,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugVisualizerRendererParametersType s)
  {
    final R2SceneLightsType so = s.lights();
    final JCGLViewportsType g_v = this.g.viewports();

    final long light_count = so.lightsCount();
    if (light_count > 0L && s.showLights()) {
      g_v.viewportSet(area);

      this.light_consumer.matrices = m;
      this.light_consumer.texture_context = uc;
      this.light_consumer.sphere = s.unitSphere();
      this.light_consumer.screen_area = area;
      try {
        so.lightsExecute(this.light_consumer);
      } finally {
        this.light_consumer.texture_context = null;
        this.light_consumer.matrices = null;
        this.light_consumer.sphere = null;
        this.light_consumer.screen_area = null;
      }
    }
  }

  private void renderSceneOpaques(
    final AreaL area,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugVisualizerRendererParametersType s)
  {
    final R2SceneOpaquesReadableType so = s.opaqueInstances();
    final JCGLViewportsType g_v = this.g.viewports();

    final long instance_count = so.opaquesCount();
    if (instance_count > 0L && s.showOpaqueInstances()) {
      g_v.viewportSet(area);

      this.opaque_consumer.screen_area = area;
      this.opaque_consumer.render_state.from(this.render_geom_state_base);
      this.opaque_consumer.matrices = m;
      this.opaque_consumer.texture_context = uc;
      this.opaque_consumer.parameters = s;
      try {
        so.opaquesExecute(this.opaque_consumer);
      } finally {
        this.opaque_consumer.screen_area = null;
        this.opaque_consumer.matrices = null;
        this.opaque_consumer.texture_context = null;
        this.opaque_consumer.parameters = null;
      }
    }
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type ig)
    throws R2Exception
  {
    if (!this.isDeleted()) {
      LOG.debug("delete");
      this.shader_batched.delete(ig);
      this.shader_single.delete(ig);
      this.shader_screen.delete(ig);
      this.shader_lines.delete(ig);
      this.lines_batch.delete(ig);
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.shader_batched.isDeleted();
  }

  private static final class OpaqueConsumer
    implements R2SceneOpaquesConsumerType
  {
    private final JCGLInterfaceGL33Type g33;
    private final JCGLShadersType shaders;
    private final JCGLTexturesType textures;
    private final JCGLArrayObjectsType array_objects;
    private final JCGLDrawType draw;
    private final JCGLRenderStateMutable render_state;
    private final R2ShaderInstanceSingleType<PVector4D<R2SpaceRGBAType>> shader_single;
    private final R2ShaderInstanceBatchedType<PVector4D<R2SpaceRGBAType>> shader_batched;
    private final R2ShaderInstanceBillboardedType<PVector4D<R2SpaceRGBAType>> shader_billboarded;
    private final R2ShaderParametersViewMutable params_view;
    private final R2ShaderParametersMaterialMutable<Object> params_material;
    private @Nullable R2MatricesObserverType matrices;
    private @Nullable JCGLTextureUnitContextParentType texture_context;
    private @Nullable JCGLTextureUnitContextType material_texture_context;
    private @Nullable R2DebugVisualizerRendererParametersType parameters;
    private @Nullable AreaL screen_area;
    private PVector4D<R2SpaceRGBAType> color;

    OpaqueConsumer(
      final JCGLInterfaceGL33Type ig,
      final R2ShaderInstanceSingleType<PVector4D<R2SpaceRGBAType>> in_shader_single,
      final R2ShaderInstanceBatchedType<PVector4D<R2SpaceRGBAType>> in_shader_batched,
      final R2ShaderInstanceBillboardedType<PVector4D<R2SpaceRGBAType>> in_shader_billboarded)
    {
      this.g33 =
        NullCheck.notNull(ig, "G33");
      this.shader_single =
        NullCheck.notNull(in_shader_single, "Shader");
      this.shader_batched =
        NullCheck.notNull(in_shader_batched, "Batched");
      this.shader_billboarded =
        NullCheck.notNull(in_shader_billboarded, "Billboarded");

      this.shaders = this.g33.shaders();
      this.textures = this.g33.textures();
      this.array_objects = this.g33.arrayObjects();
      this.draw = this.g33.drawing();
      this.render_state = JCGLRenderStateMutable.create();

      this.params_view = R2ShaderParametersViewMutable.create();
      this.params_material = R2ShaderParametersMaterialMutable.create();
    }

    @Override
    public void onStart()
    {
      NullCheck.notNull(this.g33, "g33");
    }

    @Override
    public void onStartGroup(final int group)
    {
      JCGLRenderStates.activate(this.g33, this.render_state);

      final Int2ReferenceMap<PVector4D<R2SpaceRGBAType>> g_colors =
        this.parameters.geometryGroupColors();
      if (g_colors.containsKey(group)) {
        this.color = g_colors.get(group);
      } else {
        this.color = this.parameters.geometryDefaultColor();
      }
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
      final R2ShaderInstanceBatchedUsableType<M> s)
    {
      this.shader_batched.onActivate(this.g33);
      this.shader_batched.onReceiveViewValues(
        this.g33, this.configureViewParameters());
    }

    @SuppressWarnings("unchecked")
    private <M> R2ShaderParametersMaterialType<M> configureMaterialParameters(
      final JCGLTextureUnitContextMutableType tc,
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

    private R2ShaderParametersViewType configureViewParameters()
    {
      this.params_view.clear();
      this.params_view.setViewport(this.screen_area);
      this.params_view.setObserverMatrices(this.matrices);
      Invariants.checkInvariant(
        this.params_view.isInitialized(),
        "View parameters must be initialized");
      return this.params_view;
    }

    @Override
    public <M> void onInstanceBatchedMaterialStart(
      final R2MaterialOpaqueBatchedType<M> material)
    {
      this.material_texture_context = this.texture_context.unitContextNew();
      this.shader_batched.onReceiveMaterialValues(
        this.g33,
        this.configureMaterialParameters(
          this.material_texture_context, this.color));
    }

    @Override
    public <M> void onInstanceBatched(
      final R2MaterialOpaqueBatchedType<M> material,
      final R2InstanceBatchedType i)
    {
      this.shader_batched.onValidate();

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
      this.shader_batched.onDeactivate(this.g33);
    }

    @Override
    public void onInstanceBillboardedUpdate(
      final R2InstanceBillboardedType i)
    {
      if (i.updateRequired()) {
        i.update(this.g33);
      }
    }

    @Override
    public <M> void onInstanceBillboardedShaderStart(
      final R2ShaderInstanceBillboardedUsableType<M> s)
    {
      this.shader_billboarded.onActivate(this.g33);

      this.configureViewParameters();
      this.shader_billboarded.onReceiveViewValues(this.g33, this.params_view);
    }

    @Override
    public <M> void onInstanceBillboardedMaterialStart(
      final R2MaterialOpaqueBillboardedType<M> material)
    {
      this.material_texture_context = this.texture_context.unitContextNew();
      this.shader_billboarded.onReceiveMaterialValues(
        this.g33,
        this.configureMaterialParameters(
          this.material_texture_context, this.color));
    }

    @Override
    public <M> void onInstanceBillboarded(
      final R2MaterialOpaqueBillboardedType<M> material,
      final R2InstanceBillboardedType i)
    {
      this.shader_billboarded.onValidate();
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
      this.shader_billboarded.onDeactivate(this.g33);
    }

    @Override
    public <M> void onInstanceSingleShaderStart(
      final R2ShaderInstanceSingleUsableType<M> s)
    {
      this.shader_single.onActivate(this.g33);
      this.configureViewParameters();
      this.shader_single.onReceiveViewValues(this.g33, this.params_view);
    }

    @Override
    public <M> void onInstanceSingleMaterialStart(
      final R2MaterialOpaqueSingleType<M> material)
    {
      this.material_texture_context = this.texture_context.unitContextNew();
      this.shader_single.onReceiveMaterialValues(
        this.g33,
        this.configureMaterialParameters(
          this.material_texture_context, this.color));
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
          final R2ShaderInstanceSingleUsableType<?> s = t.shader_single;
          s.onReceiveInstanceTransformValues(t.g33, mi);
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
      this.shader_single.onDeactivate(this.g33);
    }

    @Override
    public void onFinishGroup(final int group)
    {

    }

    @Override
    public void onFinish()
    {

    }
  }

  private static final class LightConsumer
    implements R2SceneLightsConsumerType
  {
    private final JCGLInterfaceGL33Type g33;
    private final JCGLShadersType shaders;
    private final JCGLTexturesType textures;
    private final JCGLArrayObjectsType array_objects;
    private final JCGLDrawType draw;
    private final R2ShaderInstanceBatchedType<PVector4D<R2SpaceRGBAType>> shader_batched;
    private final R2ShaderInstanceSingleScreenType<PVector4D<R2SpaceRGBAType>> shader_screen;
    private final R2ShaderInstanceSingleType<PVector4D<R2SpaceRGBAType>> shader_single;
    private final JCGLRenderStateMutable render_state_volume_fill;
    private final JCGLRenderStateMutable render_state_volume_lines;
    private final JCGLRenderStateMutable render_state_screen_lines;
    private final ClipGroupConsumer clip_group_consumer;
    private final GroupConsumer group_consumer;
    private final R2TransformST sphere_transform;
    private final R2ShaderParametersViewMutable params_view;
    private final R2ShaderParametersMaterialMutable<Object> params_material;
    private @Nullable R2MatricesObserverType matrices;
    private @Nullable JCGLTextureUnitContextParentType texture_context;
    private @Nullable R2UnitSphereUsableType sphere;
    private @Nullable AreaL screen_area;
    private PVector4D<R2SpaceRGBAType> light_color;

    private LightConsumer(
      final JCGLInterfaceGL33Type ig,
      final R2ShaderInstanceSingleType<PVector4D<R2SpaceRGBAType>> in_shader_single,
      final R2ShaderInstanceBatchedType<PVector4D<R2SpaceRGBAType>> in_shader_batched,
      final R2ShaderInstanceSingleScreenType<PVector4D<R2SpaceRGBAType>> in_shader_screen)
    {
      this.g33 =
        NullCheck.notNull(ig, "G33");
      this.shader_single =
        NullCheck.notNull(in_shader_single, "Single");
      this.shader_batched =
        NullCheck.notNull(in_shader_batched, "Batched");
      this.shader_screen =
        NullCheck.notNull(in_shader_screen, "Screen");

      this.shaders = this.g33.shaders();
      this.textures = this.g33.textures();
      this.array_objects = this.g33.arrayObjects();
      this.draw = this.g33.drawing();

      this.render_state_volume_fill = JCGLRenderStateMutable.create();
      this.render_state_volume_fill.setDepthState(
        JCGLDepthState.of(
          JCGLDepthStrict.DEPTH_STRICT_ENABLED,
          Optional.of(JCGLDepthFunction.DEPTH_LESS_THAN_OR_EQUAL),
          JCGLDepthWriting.DEPTH_WRITE_DISABLED,
          JCGLDepthClamping.DEPTH_CLAMP_ENABLED));
      this.render_state_volume_fill.setPolygonMode(
        JCGLPolygonMode.POLYGON_FILL);

      this.render_state_volume_lines = JCGLRenderStateMutable.create();
      this.render_state_volume_lines.setDepthState(
        JCGLDepthState.of(
          JCGLDepthStrict.DEPTH_STRICT_ENABLED,
          Optional.of(JCGLDepthFunction.DEPTH_LESS_THAN_OR_EQUAL),
          JCGLDepthWriting.DEPTH_WRITE_DISABLED,
          JCGLDepthClamping.DEPTH_CLAMP_ENABLED));
      this.render_state_volume_lines.setPolygonMode(
        JCGLPolygonMode.POLYGON_LINES);

      this.render_state_screen_lines = JCGLRenderStateMutable.create();
      this.render_state_screen_lines.setPolygonMode(
        JCGLPolygonMode.POLYGON_LINES);

      this.sphere_transform = R2TransformST.create();
      this.clip_group_consumer = new ClipGroupConsumer();
      this.group_consumer = new GroupConsumer();

      this.params_view = R2ShaderParametersViewMutable.create();
      this.params_material = R2ShaderParametersMaterialMutable.create();
    }

    @SuppressWarnings("unchecked")
    private <M> R2ShaderParametersMaterialType<M> configureMaterialParameters(
      final JCGLTextureUnitContextMutableType tc,
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

    private R2ShaderParametersViewType configureViewParameters()
    {
      this.params_view.clear();
      this.params_view.setViewport(this.screen_area);
      this.params_view.setObserverMatrices(this.matrices);
      Invariants.checkInvariant(
        this.params_view.isInitialized(),
        "View parameters must be initialized");
      return this.params_view;
    }

    @Override
    public void onStart()
    {
      NullCheck.notNull(this.g33, "g33");
    }

    @Override
    public R2SceneLightsClipGroupConsumerType onStartClipGroup(
      final R2InstanceSingleType i,
      final int group)
    {
      this.clip_group_consumer.volume = i;
      this.clip_group_consumer.group = group;
      return this.clip_group_consumer;
    }

    @Override
    public R2SceneLightsGroupConsumerType onStartGroup(
      final int group)
    {
      return this.group_consumer;
    }

    @Override
    public void onFinish()
    {

    }

    private final class GroupConsumer
      implements R2SceneLightsGroupConsumerType
    {
      private JCGLTextureUnitContextType group_texture_context;

      GroupConsumer()
      {

      }

      @Override
      public void onStart()
      {

      }

      @Override
      public <M extends R2LightSingleReadableType>
      void onLightSingleShaderStart(
        final R2ShaderLightSingleUsableType<M> s)
      {

      }

      @Override
      public void onLightSingleArrayStart(
        final R2LightSingleReadableType i)
      {

      }

      @Override
      public <M extends R2LightSingleReadableType>
      void onLightSingle(
        final R2ShaderLightSingleUsableType<M> s,
        final M light)
      {
        this.group_texture_context =
          LightConsumer.this.texture_context.unitContextNew();

        try {
          light.matchLightSingle(
            this,
            GroupConsumer::onLightSingleVolume,
            GroupConsumer::onLightSingleScreen);
        } finally {
          this.group_texture_context.unitContextFinish(
            LightConsumer.this.g33.textures());
        }
      }

      private Unit onLightSingleScreen(
        final R2LightScreenSingleType ls)
      {
        return Unit.unit();
      }

      private Unit onLightSingleVolume(
        final R2LightVolumeSingleType lv)
      {
        return lv.matchLightVolumeSingleReadable(
          this,
          GroupConsumer::onLightProjective,
          GroupConsumer::onLightSpherical);
      }

      private Unit onLightSpherical(
        final R2LightSphericalSingleReadableType ls)
      {
        final LightConsumer c = LightConsumer.this;

        final PMatrix3x3D<R2SpaceTextureType, R2SpaceTextureType> im =
          PMatrices3x3D.identity();

        try {
          c.shader_single.onActivate(c.g33);
          c.array_objects.arrayObjectBind(c.sphere.arrayObject());

          try {
            c.light_color =
              PVectors4D.scale(
                PVector4D.of(
                  ls.color().x(),
                  ls.color().y(),
                  ls.color().z(),
                  1.0),
                ls.intensity());

            /*
             * Render a tiny sphere at the light origin.
             */

            c.sphere_transform.setScale(0.1);
            c.sphere_transform.setTranslation(ls.originPosition());
            c.matrices.withTransform(c.sphere_transform, im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_fill);

              c.shader_single.onReceiveViewValues(
                c.g33, c.configureViewParameters());
              c.shader_single.onReceiveMaterialValues(
                c.g33,
                c.configureMaterialParameters(
                  lc.group_texture_context, c.light_color));
              c.shader_single.onReceiveInstanceTransformValues(c.g33, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

            /*
             * Render the light volume in wireframe mode.
             */

            c.array_objects.arrayObjectBind(ls.arrayObject());
            c.matrices.withTransform(ls.transform(), im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_lines);

              c.shader_single.onReceiveMaterialValues(
                c.g33,
                c.configureMaterialParameters(
                  lc.group_texture_context, c.light_color));
              c.shader_single.onReceiveInstanceTransformValues(c.g33, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

          } finally {
            c.array_objects.arrayObjectUnbind();
          }

        } finally {
          c.shader_single.onDeactivate(c.g33);
        }

        return Unit.unit();
      }

      private Unit onLightProjective(
        final R2LightProjectiveReadableType lp)
      {
        final LightConsumer c = LightConsumer.this;

        final PMatrix3x3D<R2SpaceTextureType, R2SpaceTextureType> im =
          PMatrices3x3D.identity();

        try {
          c.shader_single.onActivate(c.g33);
          c.array_objects.arrayObjectBind(c.sphere.arrayObject());

          try {
            final PVector3D<R2SpaceRGBType> lp_color = lp.color();
            c.light_color =
              PVectors4D.scale(
                PVector4D.of(
                  lp_color.x(),
                  lp_color.y(),
                  lp_color.z(),
                  1.0),
                lp.intensity());

            /*
             * Render a tiny sphere at the light origin.
             */

            c.sphere_transform.setScale(0.1);
            c.sphere_transform.setTranslation(lp.position());
            c.matrices.withTransform(c.sphere_transform, im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_fill);

              c.shader_single.onReceiveViewValues(
                c.g33, c.configureViewParameters());
              c.shader_single.onReceiveMaterialValues(
                c.g33,
                c.configureMaterialParameters(
                  lc.group_texture_context, c.light_color));
              c.shader_single.onReceiveInstanceTransformValues(c.g33, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

            /*
             * Render the light volume in wireframe mode.
             */

            c.array_objects.arrayObjectBind(lp.arrayObject());
            c.matrices.withTransform(lp.transform(), im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_lines);

              c.shader_single.onReceiveMaterialValues(
                c.g33,
                c.configureMaterialParameters(
                  lc.group_texture_context, c.light_color));
              c.shader_single.onReceiveInstanceTransformValues(c.g33, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

          } finally {
            c.array_objects.arrayObjectUnbind();
          }

        } finally {
          c.shader_single.onDeactivate(c.g33);
        }

        return Unit.unit();
      }

      @Override
      public <M extends R2LightSingleReadableType>
      void onLightSingleShaderFinish(
        final R2ShaderLightSingleUsableType<M> s)
      {

      }

      @Override
      public void onFinish()
      {

      }
    }

    private final class ClipGroupConsumer
      implements R2SceneLightsClipGroupConsumerType
    {
      private @Nullable R2InstanceSingleType volume;
      private int group;
      private @Nullable JCGLTextureUnitContextType texture_context;

      ClipGroupConsumer()
      {

      }

      @Override
      public void onStart()
      {
        this.texture_context =
          LightConsumer.this.texture_context.unitContextNew();

        try {

          final LightConsumer c = LightConsumer.this;

          final PMatrix3x3D<R2SpaceTextureType, R2SpaceTextureType> im =
            PMatrices3x3D.identity();

          try {
            c.shader_single.onActivate(c.g33);
            c.array_objects.arrayObjectBind(this.volume.arrayObject());

            try {
              c.light_color = PVector4D.of(1.0, 1.0, 1.0, 1.0);

              /*
               * Render the clip volume.
               */

              c.matrices.withTransform(
                this.volume.transform(),
                im,
                this,
                (mi, lc) -> {
                  JCGLRenderStates.activate(c.g33, c.render_state_volume_lines);

                  c.shader_single.onReceiveViewValues(
                    c.g33, c.configureViewParameters());
                  c.shader_single.onReceiveMaterialValues(
                    c.g33,
                    c.configureMaterialParameters(
                      lc.texture_context, c.light_color));
                  c.shader_single.onReceiveInstanceTransformValues(c.g33, mi);
                  c.shader_single.onValidate();

                  c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
                  return Unit.unit();
                });

            } finally {
              c.array_objects.arrayObjectUnbind();
            }

          } finally {
            c.shader_single.onDeactivate(c.g33);
          }

        } finally {
          this.texture_context.unitContextFinish(
            LightConsumer.this.g33.textures());
        }
      }

      @Override
      public <M extends R2LightSingleReadableType>
      void onLightSingleShaderStart(
        final R2ShaderLightSingleUsableType<M> s)
      {

      }

      @Override
      public void onLightSingleArrayStart(
        final R2LightSingleReadableType i)
      {

      }

      @Override
      public <M extends R2LightSingleReadableType>
      void onLightSingle(
        final R2ShaderLightSingleUsableType<M> s,
        final M light)
      {
        this.texture_context =
          LightConsumer.this.texture_context.unitContextNew();

        try {
          light.matchLightSingle(
            this,
            ClipGroupConsumer::onLightSingleVolume,
            ClipGroupConsumer::onLightSingleScreen);
        } finally {
          this.texture_context.unitContextFinish(
            LightConsumer.this.g33.textures());
        }
      }

      private Unit onLightSingleScreen(
        final R2LightScreenSingleType ls)
      {
        return Unit.unit();
      }

      private Unit onLightSingleVolume(
        final R2LightVolumeSingleType lv)
      {
        return lv.matchLightVolumeSingleReadable(
          this,
          ClipGroupConsumer::onLightProjective,
          ClipGroupConsumer::onLightSpherical);
      }

      private Unit onLightSpherical(
        final R2LightSphericalSingleReadableType ls)
      {
        final LightConsumer c = LightConsumer.this;

        final PMatrix3x3D<R2SpaceTextureType, R2SpaceTextureType> im =
          PMatrices3x3D.identity();

        try {
          c.shader_single.onActivate(c.g33);
          c.array_objects.arrayObjectBind(c.sphere.arrayObject());

          try {
            final PVector3D<R2SpaceRGBType> lp_color = ls.color();
            c.light_color =
              PVectors4D.scale(
                PVector4D.of(lp_color.x(), lp_color.y(), lp_color.z(), 1.0),
                ls.intensity());

            /*
             * Render a tiny sphere at the light origin.
             */

            c.sphere_transform.setScale(0.1);
            c.sphere_transform.setTranslation(ls.originPosition());
            c.matrices.withTransform(c.sphere_transform, im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_fill);

              c.shader_single.onReceiveViewValues(
                c.g33, c.configureViewParameters());
              c.shader_single.onReceiveMaterialValues(
                c.g33,
                c.configureMaterialParameters(
                  lc.texture_context, c.light_color));
              c.shader_single.onReceiveInstanceTransformValues(c.g33, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

            /*
             * Render the light volume in wireframe mode.
             */

            c.array_objects.arrayObjectBind(ls.arrayObject());
            c.matrices.withTransform(ls.transform(), im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_lines);

              c.shader_single.onReceiveMaterialValues(
                c.g33,
                c.configureMaterialParameters(
                  lc.texture_context, c.light_color));
              c.shader_single.onReceiveInstanceTransformValues(c.g33, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

          } finally {
            c.array_objects.arrayObjectUnbind();
          }

        } finally {
          c.shader_single.onDeactivate(c.g33);
        }

        return Unit.unit();
      }

      private Unit onLightProjective(
        final R2LightProjectiveReadableType lp)
      {
        final LightConsumer c = LightConsumer.this;

        final PMatrix3x3D<R2SpaceTextureType, R2SpaceTextureType> im =
          PMatrices3x3D.identity();

        try {
          c.shader_single.onActivate(c.g33);
          c.array_objects.arrayObjectBind(c.sphere.arrayObject());

          try {
            final PVector3D<R2SpaceRGBType> lp_color = lp.color();
            c.light_color =
              PVectors4D.scale(
                PVector4D.of(lp_color.x(), lp_color.y(), lp_color.z(), 1.0),
                lp.intensity());

            /*
             * Render a tiny sphere at the light origin.
             */

            c.sphere_transform.setScale(0.1);
            c.sphere_transform.setTranslation(lp.position());
            c.matrices.withTransform(c.sphere_transform, im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_fill);

              c.shader_single.onReceiveViewValues(
                c.g33, c.configureViewParameters());
              c.shader_single.onReceiveMaterialValues(
                c.g33,
                c.configureMaterialParameters(
                  lc.texture_context, c.light_color));
              c.shader_single.onReceiveInstanceTransformValues(c.g33, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

            /*
             * Render the light volume in wireframe mode.
             */

            c.array_objects.arrayObjectBind(lp.arrayObject());
            c.matrices.withTransform(lp.transform(), im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_lines);

              c.shader_single.onReceiveMaterialValues(
                c.g33,
                c.configureMaterialParameters(
                  lc.texture_context, c.light_color));
              c.shader_single.onReceiveInstanceTransformValues(c.g33, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

          } finally {
            c.array_objects.arrayObjectUnbind();
          }

        } finally {
          c.shader_single.onDeactivate(c.g33);
        }

        return Unit.unit();
      }

      @Override
      public <M extends R2LightSingleReadableType>
      void onLightSingleShaderFinish(
        final R2ShaderLightSingleUsableType<M> s)
      {

      }

      @Override
      public void onFinish()
      {

      }
    }
  }
}
