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

package com.io7m.r2.core.debug;

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
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
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jtensors.VectorM4F;
import com.io7m.jtensors.VectorReadable4FType;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2InstanceBatchedType;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2LightProjectiveReadableType;
import com.io7m.r2.core.R2LightScreenSingleType;
import com.io7m.r2.core.R2LightSingleReadableType;
import com.io7m.r2.core.R2LightSphericalSingleReadableType;
import com.io7m.r2.core.R2LightVolumeSingleType;
import com.io7m.r2.core.R2MaterialOpaqueBatchedType;
import com.io7m.r2.core.R2MaterialOpaqueSingleType;
import com.io7m.r2.core.R2MatricesObserverType;
import com.io7m.r2.core.R2SceneLightsClipGroupConsumerType;
import com.io7m.r2.core.R2SceneLightsConsumerType;
import com.io7m.r2.core.R2SceneLightsGroupConsumerType;
import com.io7m.r2.core.R2SceneLightsType;
import com.io7m.r2.core.R2SceneOpaquesConsumerType;
import com.io7m.r2.core.R2SceneOpaquesType;
import com.io7m.r2.core.R2TransformST;
import com.io7m.r2.core.R2UnitSphereUsableType;
import com.io7m.r2.core.shaders.provided.R2ShaderDebugColorVerticesWorldPosition;
import com.io7m.r2.core.shaders.provided.R2ShaderDebugVisualBatched;
import com.io7m.r2.core.shaders.provided.R2ShaderDebugVisualScreen;
import com.io7m.r2.core.shaders.provided.R2ShaderDebugVisualSingle;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleScreenType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderLightSingleUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valid4j.Assertive;

import java.util.List;
import java.util.Optional;

/**
 * The default implementation of the {@link R2DebugVisualizerRendererType}
 * interface.
 */

public final class R2DebugVisualizerRenderer implements
  R2DebugVisualizerRendererType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2DebugVisualizerRenderer.class);
  }

  private final JCGLInterfaceGL33Type g;

  private final R2ShaderInstanceSingleType<VectorReadable4FType> shader_single;
  private final R2ShaderInstanceBatchedType<VectorReadable4FType> shader_batched;
  private final R2ShaderInstanceSingleScreenType<VectorReadable4FType> shader_screen;
  private final R2ShaderInstanceSingleScreenType<Unit> shader_lines;

  private final JCGLRenderState render_geom_state_base;
  private final OpaqueConsumer opaque_consumer;
  private final LightConsumer light_consumer;
  private final R2DebugLineSegmentsBatch lines_batch;

  private R2DebugVisualizerRenderer(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    this.g = NullCheck.notNull(in_g);
    this.shader_single = R2ShaderDebugVisualSingle.newShader(
      this.g.getShaders(), in_sources, in_pool);
    this.shader_batched = R2ShaderDebugVisualBatched.newShader(
      this.g.getShaders(), in_sources, in_pool);
    this.shader_screen = R2ShaderDebugVisualScreen.newShader(
      this.g.getShaders(), in_sources, in_pool);
    this.shader_lines = R2ShaderDebugColorVerticesWorldPosition.newShader(
      this.g.getShaders(), in_sources, in_pool);

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

    this.opaque_consumer = new OpaqueConsumer(
      this.g,
      this.shader_single,
      this.shader_batched);
    this.light_consumer = new LightConsumer(
      this.g,
      this.shader_single,
      this.shader_batched,
      this.shader_screen);

    this.lines_batch = new R2DebugLineSegmentsBatch(this.g);
  }

  /**
   * Construct a new renderer.
   *
   * @param in_g       An OpenGL interface
   * @param in_pool    The ID pool
   * @param in_sources Access to shader sources
   *
   * @return A new renderer
   */

  public static R2DebugVisualizerRendererType newRenderer(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return new R2DebugVisualizerRenderer(in_g, in_sources, in_pool);
  }

  @Override
  public void renderScene(
    final AreaInclusiveUnsignedLType area,
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugVisualizerRendererParametersType s)
  {
    NullCheck.notNull(area);
    NullCheck.notNull(pc);
    NullCheck.notNull(uc);
    NullCheck.notNull(m);
    NullCheck.notNull(s);

    Assertive.require(!this.isDeleted(), "Renderer not deleted");

    final JCGLProfilingContextType pc_base =
      pc.getChildContext("debug-visualizer");

    {
      final JCGLProfilingContextType pc_opaques =
        pc_base.getChildContext("opaques");
      pc_opaques.startMeasuringIfEnabled();
      try {
        this.renderSceneOpaques(area, uc, m, s);
      } finally {
        pc_opaques.stopMeasuringIfEnabled();
      }
    }

    {
      final JCGLProfilingContextType pc_lights =
        pc_base.getChildContext("lights");
      pc_lights.startMeasuringIfEnabled();
      try {
        this.renderSceneLights(area, uc, m, s);
      } finally {
        pc_lights.stopMeasuringIfEnabled();
      }
    }

    {
      final JCGLProfilingContextType pc_extras =
        pc_base.getChildContext("extras");
      pc_extras.startMeasuringIfEnabled();
      try {
        this.renderSceneExtras(area, uc, m, s);
      } finally {
        pc_extras.stopMeasuringIfEnabled();
      }
    }
  }

  private void renderSceneExtras(
    final AreaInclusiveUnsignedLType area,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugVisualizerRendererParametersType p)
  {
    final R2DebugInstancesType extras = p.debugInstances();
    this.renderSceneExtrasLineSegments(area, uc, m, extras);
    this.renderSceneExtrasBoxes(area, uc, m, p, extras);
  }

  private void renderSceneExtrasBoxes(
    final AreaInclusiveUnsignedLType area,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugVisualizerRendererParametersType p,
    final R2DebugInstancesType extras)
  {
    final List<R2DebugCubeInstance> cubes = extras.cubes();
    if (!cubes.isEmpty()) {
      final JCGLViewportsType g_v = this.g.getViewports();
      g_v.viewportSet(area);

      JCGLRenderStates.activate(this.g, this.render_geom_state_base);

      final JCGLTextureUnitContextType tc = uc.unitContextNew();
      try {
        this.shader_single.onActivate(this.g.getShaders());
        try {
          this.shader_single.onReceiveViewValues(this.g.getShaders(), m);

          final JCGLArrayObjectUsableType ao = p.debugCube().arrayObject();
          final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
          try {
            g_ao.arrayObjectBind(ao);

            for (int index = 0; index < cubes.size(); ++index) {
              final R2DebugCubeInstance cube = cubes.get(index);

              this.shader_single.onReceiveMaterialValues(
                this.g.getTextures(), this.g.getShaders(), tc, cube.color());

              m.withTransform(
                cube.transform(),
                PMatrixI3x3F.identity(),
                this,
                (m_instance, t) -> {
                  t.shader_single.onReceiveInstanceTransformValues(
                    t.g.getShaders(),
                    m_instance);
                  t.shader_single.onValidate();
                  t.g.getDraw().drawElements(JCGLPrimitives.PRIMITIVE_LINES);
                  return Unit.unit();
                });
            }

          } finally {
            g_ao.arrayObjectUnbind();
          }

        } finally {
          this.shader_single.onDeactivate(this.g.getShaders());
        }
      } finally {
        tc.unitContextFinish(this.g.getTextures());
      }
    }
  }

  private void renderSceneExtrasLineSegments(
    final AreaInclusiveUnsignedLType area,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugInstancesType extras)
  {
    final List<R2DebugLineSegment> segments = extras.lineSegments();
    if (!segments.isEmpty()) {
      this.lines_batch.setLineSegments(segments);

      final JCGLViewportsType g_v = this.g.getViewports();
      g_v.viewportSet(area);

      JCGLRenderStates.activate(this.g, this.render_geom_state_base);

      final JCGLTextureUnitContextType tc = uc.unitContextNew();
      try {
        this.shader_lines.onActivate(this.g.getShaders());
        try {
          this.shader_lines.onReceiveViewValues(this.g.getShaders(), m);
          this.shader_lines.onReceiveMaterialValues(
            this.g.getTextures(), this.g.getShaders(), tc, Unit.unit());
          this.shader_lines.onValidate();

          final JCGLArrayObjectUsableType ao = this.lines_batch.arrayObject();
          final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
          final JCGLDrawType g_d = this.g.getDraw();
          try {
            g_ao.arrayObjectBind(ao);
            g_d.draw(JCGLPrimitives.PRIMITIVE_LINES, 0, segments.size());
          } finally {
            g_ao.arrayObjectUnbind();
          }

        } finally {
          this.shader_lines.onDeactivate(this.g.getShaders());
        }
      } finally {
        tc.unitContextFinish(this.g.getTextures());
      }
    }
  }

  private void renderSceneLights(
    final AreaInclusiveUnsignedLType area,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugVisualizerRendererParametersType s)
  {
    final R2SceneLightsType so = s.lights();
    final JCGLViewportsType g_v = this.g.getViewports();

    final long light_count = so.lightsCount();
    if (light_count > 0L && s.showLights()) {
      g_v.viewportSet(area);

      this.light_consumer.matrices = m;
      this.light_consumer.texture_context = uc;
      this.light_consumer.sphere = s.unitSphere();
      try {
        so.lightsExecute(this.light_consumer);
      } finally {
        this.light_consumer.texture_context = null;
        this.light_consumer.matrices = null;
        this.light_consumer.sphere = null;
      }
    }
  }

  private void renderSceneOpaques(
    final AreaInclusiveUnsignedLType area,
    final JCGLTextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugVisualizerRendererParametersType s)
  {
    final R2SceneOpaquesType so = s.opaqueInstances();
    final JCGLViewportsType g_v = this.g.getViewports();

    final long instance_count = so.opaquesCount();
    if (instance_count > 0L && s.showOpaqueInstances()) {
      g_v.viewportSet(area);

      this.opaque_consumer.render_state.from(this.render_geom_state_base);
      this.opaque_consumer.matrices = m;
      this.opaque_consumer.texture_context = uc;
      this.opaque_consumer.parameters = s;
      try {
        so.opaquesExecute(this.opaque_consumer);
      } finally {
        this.opaque_consumer.texture_context = null;
        this.opaque_consumer.matrices = null;
      }
    }
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type ig)
    throws R2Exception
  {
    if (!this.isDeleted()) {
      R2DebugVisualizerRenderer.LOG.debug("delete");
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

  private static final class OpaqueConsumer implements
    R2SceneOpaquesConsumerType
  {
    private final JCGLInterfaceGL33Type g33;
    private final JCGLShadersType shaders;
    private final JCGLTexturesType textures;
    private final JCGLArrayObjectsType array_objects;
    private final JCGLDrawType draw;
    private final JCGLRenderStateMutable render_state;
    private final R2ShaderInstanceSingleType<VectorReadable4FType> shader_single;
    private final R2ShaderInstanceBatchedType<VectorReadable4FType> shader_batched;
    private @Nullable R2MatricesObserverType matrices;
    private @Nullable JCGLTextureUnitContextParentType texture_context;
    private @Nullable JCGLTextureUnitContextType material_texture_context;
    private @Nullable R2DebugVisualizerRendererParametersType parameters;
    private VectorReadable4FType color;

    OpaqueConsumer(
      final JCGLInterfaceGL33Type ig,
      final R2ShaderInstanceSingleType<VectorReadable4FType> in_shader_single,
      final R2ShaderInstanceBatchedType<VectorReadable4FType> in_shader_batched)
    {
      this.g33 = NullCheck.notNull(ig);
      this.shader_single = NullCheck.notNull(in_shader_single);
      this.shader_batched = NullCheck.notNull(in_shader_batched);

      this.shaders = this.g33.getShaders();
      this.textures = this.g33.getTextures();
      this.array_objects = this.g33.getArrayObjects();
      this.draw = this.g33.getDraw();
      this.render_state = JCGLRenderStateMutable.create();
    }

    @Override
    public void onStart()
    {
      Assertive.require(this.g33 != null);
    }

    @Override
    public void onStartGroup(final int group)
    {
      JCGLRenderStates.activate(this.g33, this.render_state);

      final Int2ReferenceMap<VectorReadable4FType> g_colors =
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
      i.update(this.g33, this.matrices.getTransformContext());
    }

    @Override
    public <M> void onInstanceBatchedShaderStart(
      final R2ShaderInstanceBatchedUsableType<M> s)
    {
      this.shader_batched.onActivate(this.shaders);
      this.shader_batched.onReceiveViewValues(this.shaders, this.matrices);
    }

    @Override
    public <M> void onInstanceBatchedMaterialStart(
      final R2MaterialOpaqueBatchedType<M> material)
    {
      this.material_texture_context = this.texture_context.unitContextNew();

      final R2ShaderInstanceBatchedType<VectorReadable4FType> s =
        this.shader_batched;
      s.onReceiveMaterialValues(
        this.textures, this.shaders, this.material_texture_context, this.color);
    }

    @Override
    public <M> void onInstanceBatched(
      final R2MaterialOpaqueBatchedType<M> material,
      final R2InstanceBatchedType i)
    {
      this.shader_batched.onValidate();

      this.array_objects.arrayObjectBind(i.getArrayObject());
      this.draw.drawElementsInstanced(
        JCGLPrimitives.PRIMITIVE_TRIANGLES, i.getRenderCount());
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
      this.shader_batched.onDeactivate(this.shaders);
    }

    @Override
    public <M> void onInstanceSingleShaderStart(
      final R2ShaderInstanceSingleUsableType<M> s)
    {
      this.shader_single.onActivate(this.shaders);
      this.shader_single.onReceiveViewValues(this.shaders, this.matrices);
    }

    @Override
    public <M> void onInstanceSingleMaterialStart(
      final R2MaterialOpaqueSingleType<M> material)
    {
      this.material_texture_context = this.texture_context.unitContextNew();

      final R2ShaderInstanceSingleType<VectorReadable4FType> s =
        this.shader_single;
      s.onReceiveMaterialValues(
        this.textures, this.shaders, this.material_texture_context, this.color);
    }

    @Override
    public void onInstanceSingleArrayStart(
      final R2InstanceSingleType i)
    {
      this.array_objects.arrayObjectBind(i.getArrayObject());
    }

    @Override
    public <M> void onInstanceSingle(
      final R2MaterialOpaqueSingleType<M> material,
      final R2InstanceSingleType i)
    {
      this.matrices.withTransform(
        i.getTransform(),
        i.getUVMatrix(),
        this,
        (mi, t) -> {
          final R2ShaderInstanceSingleUsableType<?> s = t.shader_single;
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
      s.onDeactivate(this.shaders);
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

  private static final class LightConsumer implements
    R2SceneLightsConsumerType
  {
    private final JCGLInterfaceGL33Type g33;
    private final JCGLShadersType shaders;
    private final JCGLTexturesType textures;
    private final JCGLArrayObjectsType array_objects;
    private final JCGLDrawType draw;
    private final R2ShaderInstanceBatchedType<VectorReadable4FType> shader_batched;
    private final R2ShaderInstanceSingleScreenType<VectorReadable4FType> shader_screen;
    private final VectorM4F light_color;
    private final JCGLRenderStateMutable render_state_volume_fill;
    private final JCGLRenderStateMutable render_state_volume_lines;
    private final JCGLRenderStateMutable render_state_screen_lines;
    private final R2ShaderInstanceSingleType<VectorReadable4FType> shader_single;
    private final ClipGroupConsumer clip_group_consumer;
    private final GroupConsumer group_consumer;
    private final R2TransformST sphere_transform;
    private @Nullable R2MatricesObserverType matrices;
    private @Nullable JCGLTextureUnitContextParentType texture_context;
    private @Nullable R2UnitSphereUsableType sphere;

    private LightConsumer(
      final JCGLInterfaceGL33Type ig,
      final R2ShaderInstanceSingleType<VectorReadable4FType> in_shader_single,
      final R2ShaderInstanceBatchedType<VectorReadable4FType> in_shader_batched,
      final R2ShaderInstanceSingleScreenType<VectorReadable4FType> in_shader_screen)
    {
      this.g33 = NullCheck.notNull(ig);
      this.shader_single = NullCheck.notNull(in_shader_single);
      this.shader_batched = NullCheck.notNull(in_shader_batched);
      this.shader_screen = NullCheck.notNull(in_shader_screen);

      this.shaders = this.g33.getShaders();
      this.textures = this.g33.getTextures();
      this.array_objects = this.g33.getArrayObjects();
      this.draw = this.g33.getDraw();

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

      this.sphere_transform = R2TransformST.newTransform();
      this.light_color = new VectorM4F(1.0f, 1.0f, 1.0f, 1.0f);
      this.clip_group_consumer = new ClipGroupConsumer();
      this.group_consumer = new GroupConsumer();
    }

    @Override
    public void onStart()
    {
      Assertive.require(this.g33 != null);
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
      this.group_consumer.group = group;
      return this.group_consumer;
    }

    @Override
    public void onFinish()
    {

    }

    private final class GroupConsumer
      implements R2SceneLightsGroupConsumerType
    {
      private int group;
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
            (gc, lv) -> gc.onLightSingleVolume(lv),
            (gc, ls) -> gc.onLightSingleScreen(ls));
        } finally {
          this.group_texture_context.unitContextFinish(
            LightConsumer.this.g33.getTextures());
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
          (gc, lp) -> gc.onLightProjective(lp),
          (gc, ls) -> gc.onLightSpherical(ls));
      }

      private Unit onLightSpherical(
        final R2LightSphericalSingleReadableType ls)
      {
        final LightConsumer c = LightConsumer.this;

        final PMatrixI3x3F<R2SpaceTextureType, R2SpaceTextureType> im =
          PMatrixI3x3F.identity();

        try {
          c.shader_single.onActivate(c.shaders);
          c.array_objects.arrayObjectBind(c.sphere.getArrayObject());

          try {
            c.light_color.copyFrom3F(ls.getColor());
            VectorM4F.scaleInPlace(c.light_color, (double) ls.getIntensity());

            /**
             * Render a tiny sphere at the light origin.
             */

            c.sphere_transform.setScale(0.1f);
            c.sphere_transform.getTranslation().copyFrom3F(ls.getOriginPosition());
            c.matrices.withTransform(c.sphere_transform, im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_fill);
              c.shader_single.onReceiveViewValues(
                c.shaders, c.matrices);
              c.shader_single.onReceiveMaterialValues(
                c.textures, c.shaders, lc.group_texture_context, c.light_color);
              c.shader_single.onReceiveInstanceTransformValues(
                c.shaders, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

            /**
             * Render the light volume in wireframe mode.
             */

            c.array_objects.arrayObjectBind(ls.getArrayObject());
            c.matrices.withTransform(ls.getTransform(), im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_lines);
              c.shader_single.onReceiveMaterialValues(
                c.textures, c.shaders, lc.group_texture_context, c.light_color);
              c.shader_single.onReceiveInstanceTransformValues(
                c.shaders, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

          } finally {
            c.array_objects.arrayObjectUnbind();
          }

        } finally {
          c.shader_single.onDeactivate(c.shaders);
        }

        return Unit.unit();
      }

      private Unit onLightProjective(
        final R2LightProjectiveReadableType lp)
      {
        final LightConsumer c = LightConsumer.this;

        final PMatrixI3x3F<R2SpaceTextureType, R2SpaceTextureType> im =
          PMatrixI3x3F.identity();

        try {
          c.shader_single.onActivate(c.shaders);
          c.array_objects.arrayObjectBind(c.sphere.getArrayObject());

          try {
            c.light_color.copyFrom3F(lp.getColor());
            VectorM4F.scaleInPlace(c.light_color, (double) lp.getIntensity());

            /**
             * Render a tiny sphere at the light origin.
             */

            c.sphere_transform.setScale(0.1f);
            c.sphere_transform.getTranslation().copyFrom3F(lp.getPosition());
            c.matrices.withTransform(c.sphere_transform, im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_fill);
              c.shader_single.onReceiveViewValues(
                c.shaders, c.matrices);
              c.shader_single.onReceiveMaterialValues(
                c.textures, c.shaders, lc.group_texture_context, c.light_color);
              c.shader_single.onReceiveInstanceTransformValues(
                c.shaders, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

            /**
             * Render the light volume in wireframe mode.
             */

            c.array_objects.arrayObjectBind(lp.getArrayObject());
            c.matrices.withTransform(lp.getTransform(), im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_lines);
              c.shader_single.onReceiveMaterialValues(
                c.textures, c.shaders, lc.group_texture_context, c.light_color);
              c.shader_single.onReceiveInstanceTransformValues(
                c.shaders, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

          } finally {
            c.array_objects.arrayObjectUnbind();
          }

        } finally {
          c.shader_single.onDeactivate(c.shaders);
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

          final PMatrixI3x3F<R2SpaceTextureType, R2SpaceTextureType> im =
            PMatrixI3x3F.identity();

          try {
            c.shader_single.onActivate(c.shaders);
            c.array_objects.arrayObjectBind(this.volume.getArrayObject());

            try {
              c.light_color.set4F(1.0f, 1.0f, 1.0f, 1.0f);

              /**
               * Render the clip volume.
               */

              c.matrices.withTransform(
                this.volume.getTransform(),
                im,
                this,
                (mi, lc) -> {
                  JCGLRenderStates.activate(
                    c.g33,
                    c.render_state_volume_lines);
                  c.shader_single.onReceiveViewValues(
                    c.shaders, c.matrices);
                  c.shader_single.onReceiveMaterialValues(
                    c.textures,
                    c.shaders,
                    lc.texture_context,
                    c.light_color);
                  c.shader_single.onReceiveInstanceTransformValues(
                    c.shaders, mi);
                  c.shader_single.onValidate();

                  c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
                  return Unit.unit();
                });

            } finally {
              c.array_objects.arrayObjectUnbind();
            }

          } finally {
            c.shader_single.onDeactivate(c.shaders);
          }

        } finally {
          this.texture_context.unitContextFinish(
            LightConsumer.this.g33.getTextures());
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
            (cc, lv) -> cc.onLightSingleVolume(lv),
            (cc, ls) -> cc.onLightSingleScreen(ls));
        } finally {
          this.texture_context.unitContextFinish(
            LightConsumer.this.g33.getTextures());
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
          (cc, lp) -> cc.onLightProjective(lp),
          (cc, ls) -> cc.onLightSpherical(ls));
      }

      private Unit onLightSpherical(
        final R2LightSphericalSingleReadableType ls)
      {
        final LightConsumer c = LightConsumer.this;

        final PMatrixI3x3F<R2SpaceTextureType, R2SpaceTextureType> im =
          PMatrixI3x3F.identity();

        try {
          c.shader_single.onActivate(c.shaders);
          c.array_objects.arrayObjectBind(c.sphere.getArrayObject());

          try {
            c.light_color.copyFrom3F(ls.getColor());
            VectorM4F.scaleInPlace(c.light_color, (double) ls.getIntensity());

            /**
             * Render a tiny sphere at the light origin.
             */

            c.sphere_transform.setScale(0.1f);
            c.sphere_transform.getTranslation().copyFrom3F(ls.getOriginPosition());
            c.matrices.withTransform(c.sphere_transform, im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_fill);
              c.shader_single.onReceiveViewValues(
                c.shaders, c.matrices);
              c.shader_single.onReceiveMaterialValues(
                c.textures, c.shaders, lc.texture_context, c.light_color);
              c.shader_single.onReceiveInstanceTransformValues(
                c.shaders, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

            /**
             * Render the light volume in wireframe mode.
             */

            c.array_objects.arrayObjectBind(ls.getArrayObject());
            c.matrices.withTransform(ls.getTransform(), im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_lines);
              c.shader_single.onReceiveMaterialValues(
                c.textures, c.shaders, lc.texture_context, c.light_color);
              c.shader_single.onReceiveInstanceTransformValues(
                c.shaders, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

          } finally {
            c.array_objects.arrayObjectUnbind();
          }

        } finally {
          c.shader_single.onDeactivate(c.shaders);
        }

        return Unit.unit();
      }

      private Unit onLightProjective(
        final R2LightProjectiveReadableType lp)
      {
        final LightConsumer c = LightConsumer.this;

        final PMatrixI3x3F<R2SpaceTextureType, R2SpaceTextureType> im =
          PMatrixI3x3F.identity();

        try {
          c.shader_single.onActivate(c.shaders);
          c.array_objects.arrayObjectBind(c.sphere.getArrayObject());

          try {
            c.light_color.copyFrom3F(lp.getColor());
            VectorM4F.scaleInPlace(c.light_color, (double) lp.getIntensity());

            /**
             * Render a tiny sphere at the light origin.
             */

            c.sphere_transform.setScale(0.1f);
            c.sphere_transform.getTranslation().copyFrom3F(lp.getPosition());
            c.matrices.withTransform(c.sphere_transform, im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_fill);
              c.shader_single.onReceiveViewValues(
                c.shaders, c.matrices);
              c.shader_single.onReceiveMaterialValues(
                c.textures, c.shaders, lc.texture_context, c.light_color);
              c.shader_single.onReceiveInstanceTransformValues(
                c.shaders, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

            /**
             * Render the light volume in wireframe mode.
             */

            c.array_objects.arrayObjectBind(lp.getArrayObject());
            c.matrices.withTransform(lp.getTransform(), im, this, (mi, lc) -> {
              JCGLRenderStates.activate(c.g33, c.render_state_volume_lines);
              c.shader_single.onReceiveMaterialValues(
                c.textures, c.shaders, lc.texture_context, c.light_color);
              c.shader_single.onReceiveInstanceTransformValues(
                c.shaders, mi);
              c.shader_single.onValidate();

              c.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

          } finally {
            c.array_objects.arrayObjectUnbind();
          }

        } finally {
          c.shader_single.onDeactivate(c.shaders);
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
