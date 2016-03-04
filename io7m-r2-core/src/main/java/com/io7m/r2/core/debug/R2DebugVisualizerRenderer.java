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
import com.io7m.jcanephora.renderstate.JCGLCullingState;
import com.io7m.jcanephora.renderstate.JCGLDepthClamping;
import com.io7m.jcanephora.renderstate.JCGLDepthState;
import com.io7m.jcanephora.renderstate.JCGLDepthStrict;
import com.io7m.jcanephora.renderstate.JCGLDepthWriting;
import com.io7m.jcanephora.renderstate.JCGLRenderState;
import com.io7m.jcanephora.renderstate.JCGLRenderStateMutable;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jtensors.VectorM3F;
import com.io7m.jtensors.VectorM4F;
import com.io7m.jtensors.VectorReadable4FType;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2InstanceBatchedType;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2LightProjectiveType;
import com.io7m.r2.core.R2LightScreenSingleType;
import com.io7m.r2.core.R2LightSingleType;
import com.io7m.r2.core.R2MaterialOpaqueBatchedType;
import com.io7m.r2.core.R2MaterialOpaqueSingleType;
import com.io7m.r2.core.R2MatricesObserverType;
import com.io7m.r2.core.R2SceneOpaqueLightsConsumerType;
import com.io7m.r2.core.R2SceneOpaqueLightsType;
import com.io7m.r2.core.R2SceneOpaquesConsumerType;
import com.io7m.r2.core.R2SceneOpaquesType;
import com.io7m.r2.core.shaders.R2ShaderBatchedType;
import com.io7m.r2.core.shaders.R2ShaderBatchedUsableType;
import com.io7m.r2.core.shaders.R2ShaderLightSingleUsableType;
import com.io7m.r2.core.shaders.R2ShaderSingleType;
import com.io7m.r2.core.shaders.R2ShaderSingleUsableType;
import com.io7m.r2.core.shaders.R2ShaderSourcesType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2TextureUnitContextType;
import com.io7m.r2.core.shaders.R2ShaderDebugVisualBatched;
import com.io7m.r2.core.shaders.R2ShaderDebugVisualScreen;
import com.io7m.r2.core.shaders.R2ShaderDebugVisualSingle;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import org.valid4j.Assertive;

import java.util.Optional;

/**
 * The default implementation of the {@link R2DebugVisualizerRendererType}
 * interface.
 */

public final class R2DebugVisualizerRenderer implements
  R2DebugVisualizerRendererType
{
  private final JCGLInterfaceGL33Type                     g;
  private final R2ShaderSingleType<VectorReadable4FType>  shader_single;
  private final R2ShaderBatchedType<VectorReadable4FType> shader_batched;
  private final R2ShaderSingleType<VectorReadable4FType>  shader_screen;

  private final JCGLRenderState render_geom_state_base;

  private final OpaqueConsumer opaque_consumer;
  private final LightConsumer  light_consumer;


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

    {
      final JCGLRenderState.Builder b = JCGLRenderState.builder();

      /**
       * Only front faces are rendered.
       */

      b.setCullingState(Optional.of(JCGLCullingState.of(
        JCGLFaceSelection.FACE_BACK,
        JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE)));

      /**
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

      /**
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
    final R2TextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugVisualizerRendererParametersType s)
  {
    NullCheck.notNull(area);
    NullCheck.notNull(uc);
    NullCheck.notNull(m);
    NullCheck.notNull(s);

    Assertive.require(!this.isDeleted(), "Renderer not deleted");

    this.renderSceneOpaques(area, uc, m, s);
    this.renderSceneLights(area, uc, m, s);
  }

  private void renderSceneLights(
    final AreaInclusiveUnsignedLType area,
    final R2TextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugVisualizerRendererParametersType s)
  {
    final R2SceneOpaqueLightsType so = s.getOpaqueLights();
    final JCGLViewportsType g_v = this.g.getViewports();

    final long light_count = so.opaqueLightsCount();
    if (light_count > 0L && s.getShowOpaqueLights()) {
      g_v.viewportSet(area);

      this.light_consumer.matrices = m;
      this.light_consumer.texture_context = uc;
      try {
        so.opaqueLightsExecute(this.light_consumer);
      } finally {
        this.light_consumer.texture_context = null;
        this.light_consumer.matrices = null;
      }
    }
  }

  private void renderSceneOpaques(
    final AreaInclusiveUnsignedLType area,
    final R2TextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2DebugVisualizerRendererParametersType s)
  {
    final R2SceneOpaquesType so = s.getOpaqueInstances();
    final JCGLViewportsType g_v = this.g.getViewports();

    final long instance_count = so.opaquesCount();
    if (instance_count > 0L && s.getShowOpaqueInstances()) {
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
      this.shader_batched.delete(ig);
      this.shader_single.delete(ig);
      this.shader_screen.delete(ig);
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
    private final JCGLInterfaceGL33Type                     g33;
    private final JCGLShadersType                           shaders;
    private final JCGLTexturesType                          textures;
    private final JCGLArrayObjectsType                      array_objects;
    private final JCGLDrawType                              draw;
    private final JCGLRenderStateMutable                    render_state;
    private final R2ShaderSingleType<VectorReadable4FType>  shader_single;
    private final R2ShaderBatchedType<VectorReadable4FType> shader_batched;

    private @Nullable R2MatricesObserverType         matrices;
    private @Nullable R2TextureUnitContextParentType texture_context;
    private @Nullable R2TextureUnitContextType       material_texture_context;

    private @Nullable R2DebugVisualizerRendererParametersType parameters;
    private           VectorReadable4FType                    color;

    OpaqueConsumer(
      final JCGLInterfaceGL33Type ig,
      final R2ShaderSingleType<VectorReadable4FType> in_shader_single,
      final R2ShaderBatchedType<VectorReadable4FType> in_shader_batched)
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
        this.parameters.getGeometryGroupColors();
      if (g_colors.containsKey(group)) {
        this.color = g_colors.get(group);
      } else {
        this.color = this.parameters.getGeometryDefaultColor();
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
      final R2ShaderBatchedUsableType<M> s)
    {
      this.shaders.shaderActivateProgram(
        this.shader_batched.getShaderProgram());
      this.shader_batched.setMatricesView(
        this.shaders, this.matrices);
    }

    @Override
    public <M> void onInstanceBatchedMaterialStart(
      final R2MaterialOpaqueBatchedType<M> material)
    {
      this.material_texture_context = this.texture_context.unitContextNew();

      final R2ShaderBatchedType<VectorReadable4FType> s = this.shader_batched;
      s.setMaterialTextures(
        this.textures, this.material_texture_context, this.color);
      s.setMaterialValues(
        this.shaders, this.color);
    }

    @Override
    public <M> void onInstanceBatched(
      final R2MaterialOpaqueBatchedType<M> material,
      final R2InstanceBatchedType i)
    {
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
      final R2ShaderBatchedUsableType<M> s)
    {
      this.shaders.shaderDeactivateProgram();
    }

    @Override
    public <M> void onInstanceSingleShaderStart(
      final R2ShaderSingleUsableType<M> s)
    {
      this.shaders.shaderActivateProgram(
        this.shader_single.getShaderProgram());
      this.shader_single.setMatricesView(
        this.shaders, this.matrices);
    }

    @Override
    public <M> void onInstanceSingleMaterialStart(
      final R2MaterialOpaqueSingleType<M> material)
    {
      this.material_texture_context = this.texture_context.unitContextNew();

      final R2ShaderSingleType<VectorReadable4FType> s = this.shader_single;
      s.setMaterialTextures(
        this.textures, this.material_texture_context, this.color);
      s.setMaterialValues(
        this.shaders, this.color);
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
          final R2ShaderSingleUsableType<?> s = t.shader_single;
          s.setMatricesInstance(t.shaders, mi);
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
      final R2ShaderSingleUsableType<M> s)
    {
      this.shaders.shaderDeactivateProgram();
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
    R2SceneOpaqueLightsConsumerType
  {
    private final JCGLInterfaceGL33Type g33;
    private final JCGLShadersType       shaders;
    private final JCGLTexturesType      textures;
    private final JCGLArrayObjectsType  array_objects;
    private final JCGLDrawType          draw;

    private final     R2ShaderSingleType<VectorReadable4FType>  shader_single;
    private final     R2ShaderBatchedType<VectorReadable4FType> shader_batched;
    private final     R2ShaderSingleType<VectorReadable4FType>  shader_screen;
    private final     VectorM4F                                 light_color;
    private final     JCGLRenderStateMutable
      render_state_volume;
    private final     JCGLRenderStateMutable
      render_state_screen;
    private           R2ShaderSingleType<VectorReadable4FType>
                                                                shader_single_current;
    private @Nullable R2MatricesObserverType                    matrices;
    private @Nullable R2TextureUnitContextParentType            texture_context;

    private LightConsumer(
      final JCGLInterfaceGL33Type ig,
      final R2ShaderSingleType<VectorReadable4FType> in_shader_single,
      final R2ShaderBatchedType<VectorReadable4FType> in_shader_batched,
      final R2ShaderSingleType<VectorReadable4FType> in_shader_screen)
    {
      this.g33 = NullCheck.notNull(ig);
      this.shader_single = NullCheck.notNull(in_shader_single);
      this.shader_batched = NullCheck.notNull(in_shader_batched);
      this.shader_screen = NullCheck.notNull(in_shader_screen);

      this.shaders = this.g33.getShaders();
      this.textures = this.g33.getTextures();
      this.array_objects = this.g33.getArrayObjects();
      this.draw = this.g33.getDraw();

      /**
       * Only render wireframes.
       */

      this.render_state_volume = JCGLRenderStateMutable.create();
      this.render_state_volume.setDepthState(JCGLDepthState.of(
        JCGLDepthStrict.DEPTH_STRICT_ENABLED,
        Optional.of(JCGLDepthFunction.DEPTH_LESS_THAN_OR_EQUAL),
        JCGLDepthWriting.DEPTH_WRITE_DISABLED,
        JCGLDepthClamping.DEPTH_CLAMP_ENABLED));
      this.render_state_volume.setPolygonMode(JCGLPolygonMode.POLYGON_LINES);
      this.render_state_screen = JCGLRenderStateMutable.create();
      this.render_state_screen.setPolygonMode(JCGLPolygonMode.POLYGON_LINES);

      this.light_color = new VectorM4F(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void onStart()
    {
      Assertive.require(this.g33 != null);
    }

    @Override
    public void onFinish()
    {

    }

    @Override
    public void onStartGroup(final int group)
    {

    }

    @Override
    public <M extends R2LightSingleType> void onLightSingleShaderStart(
      final R2ShaderLightSingleUsableType<M> s)
    {

    }

    @Override
    public void onLightSingleArrayStart(final R2LightSingleType i)
    {
      this.array_objects.arrayObjectBind(i.getArrayObject());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <M extends R2LightSingleType> void onLightSingle(
      final R2ShaderLightSingleUsableType<M> s,
      final M i)
    {

      /**
       * Create a new texture context for this particular light.
       */

      final R2TextureUnitContextType uc =
        this.texture_context.unitContextNew();

      try {
        if (i instanceof R2LightScreenSingleType) {
          this.shader_single_current = this.shader_screen;
          JCGLRenderStates.activate(this.g33, this.render_state_screen);
        } else {
          this.shader_single_current = this.shader_single;
          JCGLRenderStates.activate(this.g33, this.render_state_volume);
        }

        VectorM3F.scale(
          i.getColor(),
          (double) i.getIntensity(),
          this.light_color);
        this.light_color.setWF(1.0f);

        this.shaders.shaderActivateProgram(
          this.shader_single_current.getShaderProgram());

        this.shader_single_current.setMaterialTextures(
          this.textures, uc, this.light_color);
        this.shader_single_current.setMaterialValues(
          this.shaders, this.light_color);
        this.shader_single_current.setMatricesView(
          this.shaders, this.matrices);

        if (i instanceof R2LightProjectiveType) {
          final R2LightProjectiveType it = (R2LightProjectiveType) i;
          this.matrices.withProjectiveLight(
            it.getTransform(),
            it.getProjection(),
            this,
            (mi, t) -> {
              t.shader_single_current.setMatricesInstance(t.shaders, mi);
              t.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });
        } else {
          this.matrices.withTransform(
            i.getTransform(),
            PMatrixI3x3F.identity(),
            this,
            (mi, t) -> {
              t.shader_single_current.setMatricesInstance(t.shaders, mi);
              t.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });
        }

      } finally {
        uc.unitContextFinish(this.textures);
        this.shaders.shaderDeactivateProgram();
      }
    }

    @Override
    public <M extends R2LightSingleType> void onLightSingleShaderFinish(
      final R2ShaderLightSingleUsableType<M> s)
    {
      this.shaders.shaderDeactivateProgram();
    }

    @Override
    public void onFinishGroup(final int group)
    {

    }
  }
}
