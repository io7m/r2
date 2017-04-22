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

import com.io7m.jaffirm.core.Invariants;
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jcanephora.core.JCGLBlendEquation;
import com.io7m.jcanephora.core.JCGLBlendFunction;
import com.io7m.jcanephora.core.JCGLDepthFunction;
import com.io7m.jcanephora.core.JCGLFaceSelection;
import com.io7m.jcanephora.core.JCGLFaceWindingOrder;
import com.io7m.jcanephora.core.JCGLFramebufferBlitBuffer;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.JCGLStencilFunction;
import com.io7m.jcanephora.core.JCGLStencilOperation;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.core.api.JCGLViewportsType;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.renderstate.JCGLBlendState;
import com.io7m.jcanephora.renderstate.JCGLColorBufferMaskingState;
import com.io7m.jcanephora.renderstate.JCGLCullingState;
import com.io7m.jcanephora.renderstate.JCGLDepthClamping;
import com.io7m.jcanephora.renderstate.JCGLDepthState;
import com.io7m.jcanephora.renderstate.JCGLDepthStrict;
import com.io7m.jcanephora.renderstate.JCGLDepthWriting;
import com.io7m.jcanephora.renderstate.JCGLRenderStateMutable;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.renderstate.JCGLStencilStateMutable;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.r2.core.shaders.provided.R2ShaderLogDepthOnlySingle;
import com.io7m.r2.core.shaders.provided.R2StencilShaderScreen;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleScreenType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleScreenUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderLightProjectiveUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderLightProjectiveWithShadowUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderLightScreenSingleUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderLightSingleUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderLightVolumeSingleUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersLightMutable;
import com.io7m.r2.core.shaders.types.R2ShaderParametersLightType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersMaterialMutable;
import com.io7m.r2.core.shaders.types.R2ShaderParametersMaterialType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersViewMutable;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static com.io7m.jcanephora.core.JCGLFramebufferBlitFilter.FRAMEBUFFER_BLIT_FILTER_NEAREST;

/**
 * The default implementation of the {@link R2LightRendererType} interface.
 */

public final class R2LightRenderer implements R2LightRendererType
{
  private static final Logger LOG;
  private static final Set<JCGLFramebufferBlitBuffer> DEPTH_STENCIL;

  static {
    LOG = LoggerFactory.getLogger(R2LightRenderer.class);
  }

  static {
    DEPTH_STENCIL = EnumSet.of(
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_DEPTH,
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_STENCIL);
  }

  private final LightConsumer light_consumer;
  private final JCGLInterfaceGL33Type g;
  private final R2ShaderInstanceSingleType<Unit> clip_volume_stencil;
  private final R2ShaderInstanceSingleScreenType<Unit> clip_screen_stencil;

  private R2LightRenderer(
    final JCGLInterfaceGL33Type in_g,
    final R2TextureDefaultsType in_defaults,
    final R2ShaderInstanceSingleType<Unit> in_clip_volume_stencil,
    final R2ShaderInstanceSingleScreenType<Unit> in_clip_screen_stencil,
    final R2UnitQuadUsableType in_quad)
  {
    this.g =
      NullCheck.notNull(in_g, "G33");
    this.clip_volume_stencil =
      NullCheck.notNull(in_clip_volume_stencil, "Volume stencil");
    this.clip_screen_stencil =
      NullCheck.notNull(in_clip_screen_stencil, "Screen stencil");
    this.light_consumer =
      new LightConsumer(
        this.g,
        in_defaults,
        in_clip_volume_stencil,
        in_clip_screen_stencil,
        in_quad);
  }

  /**
   * Construct a new renderer.
   *
   * @param in_g                An OpenGL interface
   * @param in_shader_env       Access to shader sources
   * @param in_texture_defaults A set of default textures
   * @param in_pool             An ID pool
   * @param in_quad             A usable unit quad
   *
   * @return A new renderer
   */

  public static R2LightRenderer create(
    final JCGLInterfaceGL33Type in_g,
    final R2TextureDefaultsType in_texture_defaults,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    final JCGLShadersType g_sh = in_g.shaders();
    final R2ShaderInstanceSingleType<Unit> volume_stencil =
      R2ShaderLogDepthOnlySingle.newShader(
        g_sh, in_shader_env, in_pool);
    final R2ShaderInstanceSingleScreenType<Unit> screen_stencil =
      R2StencilShaderScreen.newShader(
        g_sh, in_shader_env, in_pool);

    return new R2LightRenderer(
      in_g, in_texture_defaults, volume_stencil, screen_stencil, in_quad);
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g3)
    throws R2Exception
  {
    NullCheck.notNull(g3, "G33");

    if (!this.clip_volume_stencil.isDeleted()) {
      LOG.debug("delete");
      this.clip_volume_stencil.delete(g3);
      this.clip_screen_stencil.delete(g3);
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.clip_volume_stencil.isDeleted();
  }

  @Override
  public void renderLights(
    final R2GeometryBufferUsableType gbuffer,
    final AreaL area,
    final Optional<R2LightBufferUsableType> lbuffer,
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType uc,
    final R2ShadowMapContextUsableType shadows,
    final R2MatricesObserverType m,
    final R2SceneLightsType s)
  {
    NullCheck.notNull(gbuffer, "G-Buffer");
    NullCheck.notNull(area, "Area");
    NullCheck.notNull(lbuffer, "L-Buffer");
    NullCheck.notNull(pc, "Profiling");
    NullCheck.notNull(uc, "Texture context");
    NullCheck.notNull(shadows, "Shadows");
    NullCheck.notNull(m, "Matrices");
    NullCheck.notNull(s, "Scene");

    Preconditions.checkPrecondition(
      !this.isDeleted(), "Renderer must not be deleted");

    final JCGLProfilingContextType pc_base =
      pc.childContext("lights");

    final JCGLProfilingContextType pc_copy_depth =
      pc_base.childContext("copy-depth");

    pc_copy_depth.startMeasuringIfEnabled();
    try {
      if (lbuffer.isPresent()) {
        final R2LightBufferUsableType lb = lbuffer.get();
        final JCGLFramebuffersType g_fb = this.g.framebuffers();
        g_fb.framebufferDrawBind(lb.primaryFramebuffer());
      }

      this.renderCopyDepthStencil(gbuffer, area);
    } finally {
      pc_copy_depth.stopMeasuringIfEnabled();
    }

    this.renderLightInstances(
      gbuffer, area, pc_base, uc, shadows, m, s);
  }

  private void renderLightInstances(
    final R2GeometryBufferUsableType gbuffer,
    final AreaL lbuffer_area,
    final JCGLProfilingContextType pc_base,
    final JCGLTextureUnitContextParentType uc,
    final R2ShadowMapContextUsableType shadows,
    final R2MatricesObserverType m,
    final R2SceneLightsType s)
  {
    final JCGLProfilingContextType pc_instances =
      pc_base.childContext("instances");

    if (s.lightsCount() > 0L) {
      final JCGLViewportsType g_v = this.g.viewports();
      g_v.viewportSet(lbuffer_area);

      this.light_consumer.input_state.set(
        gbuffer, m, uc, shadows, lbuffer_area, pc_instances);
      try {
        s.lightsExecute(this.light_consumer);
      } finally {
        this.light_consumer.input_state.clear();
      }
    }
  }

  /**
   * Copy the contents of the depth/stencil attachment of the G-Buffer to the
   * current depth/stencil buffer.
   */

  private void renderCopyDepthStencil(
    final R2GeometryBufferUsableType gbuffer,
    final AreaL lbuffer_area)
  {
    final JCGLFramebufferUsableType gb_fb = gbuffer.primaryFramebuffer();
    final JCGLFramebuffersType g_fb = this.g.framebuffers();

    g_fb.framebufferReadBind(gb_fb);
    g_fb.framebufferBlit(
      gbuffer.sizeAsViewport(),
      lbuffer_area,
      DEPTH_STENCIL,
      FRAMEBUFFER_BLIT_FILTER_NEAREST);
    g_fb.framebufferReadUnbind();
  }

  private static final class LightGroupConsumerInputState
  {
    private final LightConsumerInputState parent;
    private @Nullable JCGLTextureUnitType unit_albedo;
    private @Nullable JCGLTextureUnitType unit_normals;
    private @Nullable JCGLTextureUnitType unit_specular;
    private @Nullable JCGLTextureUnitType unit_depth;
    private @Nullable JCGLTextureUnitContextType light_base_context;
    private @Nullable AreaL viewport;
    private int group;

    LightGroupConsumerInputState(
      final LightConsumerInputState in_parent)
    {
      this.parent = NullCheck.notNull(in_parent, "Parent");
    }

    void set(
      final int in_group,
      final JCGLTextureUnitType in_unit_albedo,
      final JCGLTextureUnitType in_unit_depth,
      final JCGLTextureUnitType in_unit_normals,
      final JCGLTextureUnitType in_unit_specular,
      final JCGLTextureUnitContextType in_light_base_context,
      final AreaL in_viewport)
    {
      this.group = in_group;
      this.unit_albedo =
        NullCheck.notNull(in_unit_albedo, "Albedo texture");
      this.unit_normals =
        NullCheck.notNull(in_unit_normals, "Normals texture");
      this.unit_specular =
        NullCheck.notNull(in_unit_specular, "Specular texture");
      this.unit_depth =
        NullCheck.notNull(in_unit_depth, "Depth texture");
      this.light_base_context =
        NullCheck.notNull(in_light_base_context, "Context");
      this.viewport =
        NullCheck.notNull(in_viewport, "Viewport");
    }

    void clear()
    {
      this.group = -1;
      this.unit_albedo = null;
      this.unit_depth = null;
      this.unit_normals = null;
      this.unit_specular = null;
      this.light_base_context = null;
      this.viewport = null;
    }
  }

  private static final class LightGroupConsumer
    implements R2SceneLightsGroupConsumerType
  {
    private final JCGLInterfaceGL33Type g33;
    private final JCGLTexturesType textures;
    private final JCGLArrayObjectsType array_objects;
    private final JCGLDrawType draw;
    private final JCGLRenderStateMutable render_state_screen;
    private final JCGLRenderStateMutable render_state_volume;
    private final JCGLStencilStateMutable stencil_state_screen;
    private final JCGLStencilStateMutable stencil_state_volume;
    private final LightGroupConsumerInputState input_state;
    private final R2ShaderParametersLightMutable<R2LightSingleReadableType> params_light;

    private @Nullable
    R2ShaderLightSingleUsableType<R2LightSingleReadableType> light_shader;
    private @Nullable JCGLTextureUnitContextType light_each_context;
    private @Nullable R2LightProjectiveWithShadowReadableType light_shadow;
    private @Nullable JCGLProfilingContextType profiling_instances;

    LightGroupConsumer(
      final JCGLInterfaceGL33Type in_g,
      final LightConsumerInputState in_input_state)
    {
      this.g33 = NullCheck.notNull(in_g, "G33");

      this.textures = this.g33.textures();
      this.array_objects = this.g33.arrayObjects();
      this.draw = this.g33.drawing();
      this.input_state = new LightGroupConsumerInputState(in_input_state);
      this.params_light = R2ShaderParametersLightMutable.create();

      {
        this.render_state_screen = JCGLRenderStateMutable.create();

        /*
         * Write RGB, ignore alpha.
         */

        this.render_state_screen.setColorBufferMaskingState(
          JCGLColorBufferMaskingState.of(true, true, true, false));

        /*
          The light contributions are summed with pure additive blending.
         */

        this.render_state_screen.setBlendState(
          Optional.of(JCGLBlendState.of(
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendEquation.BLEND_EQUATION_ADD,
            JCGLBlendEquation.BLEND_EQUATION_ADD)));

        /*
          For full-screen quads, the front faces should be rendered.
         */

        this.render_state_screen.setCullingState(
          Optional.of(JCGLCullingState.of(
            JCGLFaceSelection.FACE_BACK,
            JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE)));

        /*
          No depth testing and no depth writing is required.
         */

        this.render_state_screen.setDepthState(JCGLDepthState.of(
          JCGLDepthStrict.DEPTH_STRICT_ENABLED,
          Optional.empty(),
          JCGLDepthWriting.DEPTH_WRITE_DISABLED,
          JCGLDepthClamping.DEPTH_CLAMP_ENABLED));

        this.stencil_state_screen = JCGLStencilStateMutable.create();
        this.render_state_screen.setStencilState(this.stencil_state_screen);
      }

      {
        this.render_state_volume = JCGLRenderStateMutable.create();

        /*
         * Write RGB, ignore alpha.
         */

        this.render_state_volume.setColorBufferMaskingState(
          JCGLColorBufferMaskingState.of(true, true, true, false));

        /*
          The light contributions are summed with pure additive blending.
         */

        this.render_state_volume.setBlendState(
          Optional.of(JCGLBlendState.of(
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendEquation.BLEND_EQUATION_ADD,
            JCGLBlendEquation.BLEND_EQUATION_ADD)));

        /*
          For typical volume lights, only the back faces should be
          rendered.
         */

        this.render_state_volume.setCullingState(
          Optional.of(JCGLCullingState.of(
            JCGLFaceSelection.FACE_FRONT,
            JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE)));

        /*
          The fragments of the back faces of the light volume will have a
          depth greater than or equal to the geometry fragments that
          should be affected. No depth writing is required.
         */

        this.render_state_volume.setDepthState(
          JCGLDepthState.of(
            JCGLDepthStrict.DEPTH_STRICT_ENABLED,
            Optional.of(JCGLDepthFunction.DEPTH_GREATER_THAN_OR_EQUAL),
            JCGLDepthWriting.DEPTH_WRITE_DISABLED,
            JCGLDepthClamping.DEPTH_CLAMP_ENABLED));

        this.stencil_state_volume = JCGLStencilStateMutable.create();
        this.render_state_volume.setStencilState(this.stencil_state_volume);
      }
    }

    /**
     * Configure stencilling such that only pixels with a stencil value equal to
     * {@code group} are touched, and no writes are made to the stencil buffer.
     */

    private static void configureStencilState(
      final int group,
      final JCGLStencilStateMutable ss)
    {
      ss.setStencilStrict(true);
      ss.setStencilEnabled(true);

      ss.setOperationDepthFailFront(JCGLStencilOperation.STENCIL_OP_KEEP);
      ss.setOperationStencilFailFront(JCGLStencilOperation.STENCIL_OP_KEEP);
      ss.setOperationPassFront(JCGLStencilOperation.STENCIL_OP_KEEP);

      ss.setTestFunctionFront(JCGLStencilFunction.STENCIL_EQUAL);
      ss.setTestReferenceFront(group << R2Stencils.GROUP_LEFT_SHIFT);
      ss.setTestMaskFront(R2Stencils.GROUP_BITS);

      ss.setWriteMaskFrontFaces(0);

      ss.setOperationDepthFailBack(JCGLStencilOperation.STENCIL_OP_KEEP);
      ss.setOperationStencilFailBack(JCGLStencilOperation.STENCIL_OP_KEEP);
      ss.setOperationPassBack(JCGLStencilOperation.STENCIL_OP_KEEP);

      ss.setTestFunctionBack(JCGLStencilFunction.STENCIL_EQUAL);
      ss.setTestReferenceBack(group << R2Stencils.GROUP_LEFT_SHIFT);
      ss.setTestMaskBack(R2Stencils.GROUP_BITS);

      ss.setWriteMaskBackFaces(0);
    }

    @Override
    public void onStart()
    {
      R2Stencils.checkValidGroup(this.input_state.group);

      final JCGLProfilingContextType pc_base =
        this.input_state.parent.profiling_context.childContext("unclipped");
      this.profiling_instances =
        pc_base.childContext("instances");
      this.profiling_instances.startMeasuringIfEnabled();

      configureStencilState(
        this.input_state.group, this.stencil_state_screen);
      this.render_state_screen.setStencilState(this.stencil_state_screen);
      configureStencilState(
        this.input_state.group, this.stencil_state_volume);
      this.render_state_volume.setStencilState(this.stencil_state_volume);
    }

    @Override
    public <M extends R2LightSingleReadableType>
    void onLightSingleShaderStart(
      final R2ShaderLightSingleUsableType<M> s)
    {
      s.onActivate(this.g33);
      s.onReceiveBoundGeometryBufferTextures(
        this.g33,
        this.input_state.parent.gbuffer,
        this.input_state.unit_albedo,
        this.input_state.unit_specular,
        this.input_state.unit_depth,
        this.input_state.unit_normals);
    }

    @Override
    public void onLightSingleArrayStart(
      final R2LightSingleReadableType i)
    {
      this.array_objects.arrayObjectBind(i.arrayObject());
    }

    private void onLightSingleScreen(
      final R2LightScreenSingleType light)
    {
      // Nothing!
    }

    private void onLightSingleVolume(
      final R2LightVolumeSingleReadableType light)
    {
      light.matchLightVolumeSingleReadable(this, (t, pl) -> {
        Preconditions.checkPrecondition(
          t.light_shader instanceof R2ShaderLightProjectiveUsableType,
          "Shader must be a projective light shader");

        t.onLightSingleProjective(pl);
        return Unit.unit();
      }, (t, sl) -> {
        Preconditions.checkPrecondition(
          t.light_shader instanceof R2ShaderLightVolumeSingleUsableType,
          "Shader must be a volume light shader");

        t.onLightSingleSpherical(sl);
        return Unit.unit();
      });
    }

    @SuppressWarnings("unchecked")
    private void onLightSingleSpherical(
      final R2LightSphericalSingleReadableType light)
    {
      final R2ShaderLightVolumeSingleUsableType<
        R2LightSphericalSingleReadableType> s =
        R2ShaderLightVolumeSingleUsableType.class.cast(
          this.light_shader);

      this.input_state.parent.matrices.withVolumeLight(light, this, (mv, t) -> {
        s.onReceiveVolumeLightTransform(t.g33, mv);
        s.onValidate();
        return Unit.unit();
      });
    }

    private void onLightSingleProjective(
      final R2LightProjectiveReadableType light)
    {
      light.matchProjectiveReadable(this, (t, pw) -> {
        Preconditions.checkPrecondition(
          t.light_shader instanceof R2ShaderLightProjectiveUsableType,
          "Shader must be a projective light shader");

        t.onLightSingleProjectiveWithoutShadow(pw);
        return Unit.unit();
      }, (t, pw) -> {
        Preconditions.checkPrecondition(
          t.light_shader instanceof R2ShaderLightProjectiveUsableType,
          "Shader must be a projective light with shadow shader");

        t.onLightSingleProjectiveWithShadow(pw);
        return Unit.unit();
      });
    }

    @SuppressWarnings("unchecked")
    private void onLightSingleProjectiveWithShadow(
      final R2LightProjectiveWithShadowReadableType light)
    {
      final R2ShaderLightProjectiveWithShadowUsableType<
        R2LightProjectiveWithShadowReadableType> s =
        R2ShaderLightProjectiveWithShadowUsableType.class.cast(
          this.light_shader);

      this.light_shadow = light;
      try {
        this.input_state.parent.matrices.withProjectiveLight(
          light, this, (mp, t) -> {
            s.onReceiveVolumeLightTransform(t.g33, mp);
            s.onReceiveProjectiveLight(t.g33, mp);

            final R2Texture2DUsableType map =
              t.input_state.parent.shadow_maps.shadowMapGet(t.light_shadow);

            s.onReceiveShadowMap(
              t.g33,
              t.light_each_context,
              map);
            s.onValidate();
            return Unit.unit();
          });
      } finally {
        this.light_shadow = null;
      }
    }

    @SuppressWarnings("unchecked")
    private void onLightSingleProjectiveWithoutShadow(
      final R2LightProjectiveWithoutShadowReadableType light)
    {
      final R2ShaderLightProjectiveUsableType<
        R2LightProjectiveReadableType> s =
        R2ShaderLightProjectiveUsableType.class.cast(
          this.light_shader);

      this.input_state.parent.matrices.withProjectiveLight(
        light, this, (mp, t) -> {
          s.onReceiveVolumeLightTransform(t.g33, mp);
          s.onReceiveProjectiveLight(t.g33, mp);
          s.onValidate();
          return Unit.unit();
        });
    }

    @SuppressWarnings("unchecked")
    private <M extends R2LightSingleReadableType> R2ShaderParametersLightType<M>
    configureLightParameters(
      final JCGLTextureUnitContextType tc,
      final M p)
    {
      this.params_light.clear();
      this.params_light.setTextureUnitContext(tc);
      this.params_light.setObserverMatrices(this.input_state.parent.matrices);
      this.params_light.setViewport(this.input_state.viewport);
      this.params_light.setValues(p);
      Invariants.checkInvariant(
        this.params_light.isInitialized(),
        "Light parameters must be initialized");
      return (R2ShaderParametersLightType<M>) this.params_light;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <M extends R2LightSingleReadableType> void onLightSingle(
      final R2ShaderLightSingleUsableType<M> s,
      final M light)
    {
      try {
        this.light_shader =
          (R2ShaderLightSingleUsableType<R2LightSingleReadableType>) s;
        this.light_each_context =
          this.input_state.light_base_context.unitContextNew();

        final Class<R2LightSingleReadableType> s_class =
          this.light_shader.shaderParametersType();
        final Class<? extends R2LightSingleReadableType> l_class =
          light.getClass();

        Preconditions.checkPrecondition(
          s_class.isAssignableFrom(l_class),
          "Shader parameter type must be compatible with light type");

        s.onReceiveValues(
          this.g33,
          this.configureLightParameters(
            this.light_each_context,
            light
          ));

        light.matchLightSingle(
          this,
          (t, lv) -> {
            Preconditions.checkPrecondition(
              t.light_shader instanceof R2ShaderLightVolumeSingleUsableType,
              "Shader must be a volume light shader");

            JCGLRenderStates.activate(t.g33, t.render_state_volume);
            t.onLightSingleVolume(lv);
            return Unit.unit();
          },
          (t, ls) -> {
            Preconditions.checkPrecondition(
              t.light_shader instanceof R2ShaderLightScreenSingleUsableType,
              "Shader must be a screen light shader");

            JCGLRenderStates.activate(t.g33, t.render_state_screen);
            t.onLightSingleScreen(ls);
            return Unit.unit();
          });

        this.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
      } finally {
        this.light_each_context.unitContextFinish(this.textures);
        this.light_each_context = null;
      }
    }

    @Override
    public <M extends R2LightSingleReadableType>
    void onLightSingleShaderFinish(
      final R2ShaderLightSingleUsableType<M> s)
    {
      s.onDeactivate(this.g33);
    }

    @Override
    public void onFinish()
    {
      NullCheck.notNull(this.profiling_instances, "Instances");
      this.profiling_instances.stopMeasuringIfEnabled();
    }
  }

  private static final class LightConsumerInputState
  {
    private @Nullable R2GeometryBufferUsableType gbuffer;
    private @Nullable R2MatricesObserverType matrices;
    private @Nullable JCGLTextureUnitContextParentType texture_context;
    private @Nullable R2ShadowMapContextUsableType shadow_maps;
    private @Nullable AreaL viewport;
    private @Nullable JCGLProfilingContextType profiling_context;

    LightConsumerInputState()
    {

    }

    void set(
      final R2GeometryBufferUsableType in_gbuffer,
      final R2MatricesObserverType in_m,
      final JCGLTextureUnitContextParentType in_texture_context,
      final R2ShadowMapContextUsableType in_shadows,
      final AreaL in_viewport,
      final JCGLProfilingContextType pc_base)
    {
      this.gbuffer =
        NullCheck.notNull(in_gbuffer, "G-Buffer");
      this.matrices =
        NullCheck.notNull(in_m, "Matrices");
      this.texture_context =
        NullCheck.notNull(in_texture_context, "Texture context");
      this.shadow_maps =
        NullCheck.notNull(in_shadows, "Shadows");
      this.viewport =
        NullCheck.notNull(in_viewport, "Viewport");
      this.profiling_context =
        NullCheck.notNull(pc_base, "Profiling");
    }

    void clear()
    {
      this.gbuffer = null;
      this.matrices = null;
      this.texture_context = null;
      this.shadow_maps = null;
      this.viewport = null;
      this.profiling_context = null;
    }
  }

  private static final class LightClipGroupConsumer implements
    R2SceneLightsClipGroupConsumerType
  {
    private final JCGLInterfaceGL33Type g33;
    private final LightConsumerInputState parent;
    private final LightClipGroupConsumerInputState input_state;
    private final R2ShaderInstanceSingleUsableType<Unit> clip_volume_stencil;
    private final R2ShaderInstanceSingleScreenUsableType<Unit> clip_screen_stencil;
    private final JCGLRenderStateMutable clip_screen_stencil_state;
    private final JCGLRenderStateMutable clip_volume_stencil_state;
    private final JCGLTexturesType textures;
    private final R2UnitQuadUsableType quad;
    private final JCGLArrayObjectsType array_objects;
    private final JCGLDrawType draw;
    private final JCGLRenderStateMutable render_state_screen;
    private final JCGLRenderStateMutable render_state_volume;
    private final JCGLStencilStateMutable render_stencil_state;
    private final R2ShaderParametersViewMutable params_view;
    private final R2ShaderParametersMaterialMutable<Object> params_material;
    private final R2ShaderParametersLightMutable<R2LightSingleReadableType> params_light;

    private @Nullable
    R2ShaderLightSingleUsableType<R2LightSingleReadableType> light_shader;
    private @Nullable JCGLTextureUnitContextType light_each_context;
    private @Nullable R2LightProjectiveWithShadowReadableType light_shadow;
    private @Nullable JCGLProfilingContextType profiling_instances;

    LightClipGroupConsumer(
      final JCGLInterfaceGL33Type in_g33,
      final LightConsumerInputState in_input_state,
      final R2ShaderInstanceSingleUsableType<Unit> in_clip_volume_stencil,
      final R2ShaderInstanceSingleScreenUsableType<Unit> in_clip_screen_stencil,
      final R2UnitQuadUsableType in_quad)
    {
      this.g33 =
        NullCheck.notNull(in_g33, "G33");
      this.parent =
        NullCheck.notNull(in_input_state, "Input");
      this.quad =
        NullCheck.notNull(in_quad, "Quad");

      this.textures = this.g33.textures();
      this.array_objects = this.g33.arrayObjects();
      this.draw = this.g33.drawing();

      this.input_state =
        new LightClipGroupConsumerInputState(in_input_state);
      this.clip_volume_stencil =
        NullCheck.notNull(in_clip_volume_stencil, "Volume stencil");
      this.clip_screen_stencil =
        NullCheck.notNull(in_clip_screen_stencil, "Screen stencil");

      this.params_view =
        R2ShaderParametersViewMutable.create();
      this.params_material =
        R2ShaderParametersMaterialMutable.create();
      this.params_light =
        R2ShaderParametersLightMutable.create();

      {
        /*
          Configure rendering state for the full-screen stencil clearing pass.

          Configure stencil settings that will unconditionally clear the
          {@link R2Stencils#LIGHT_MASK_BIT}, but leave the rest of the
          contents of the stencil buffer intact.
         */

        this.clip_screen_stencil_state =
          JCGLRenderStateMutable.create();
        this.clip_screen_stencil_state.setColorBufferMaskingState(
          JCGLColorBufferMaskingState.of(false, false, false, false));

        final JCGLStencilStateMutable ss = JCGLStencilStateMutable.create();
        ss.setStencilEnabled(true);
        ss.setStencilStrict(true);

        ss.setOperationDepthFailBack(JCGLStencilOperation.STENCIL_OP_KEEP);
        ss.setOperationStencilFailBack(JCGLStencilOperation.STENCIL_OP_KEEP);
        ss.setOperationPassBack(JCGLStencilOperation.STENCIL_OP_REPLACE);

        ss.setOperationDepthFailFront(JCGLStencilOperation.STENCIL_OP_KEEP);
        ss.setOperationStencilFailFront(JCGLStencilOperation.STENCIL_OP_KEEP);
        ss.setOperationPassFront(JCGLStencilOperation.STENCIL_OP_REPLACE);

        ss.setTestFunctionBack(JCGLStencilFunction.STENCIL_ALWAYS);
        ss.setTestMaskBack(0);
        ss.setTestReferenceBack(0);
        ss.setWriteMaskBackFaces(R2Stencils.LIGHT_MASK_BIT);

        ss.setTestFunctionFront(JCGLStencilFunction.STENCIL_ALWAYS);
        ss.setTestMaskFront(0);
        ss.setTestReferenceFront(0);
        ss.setWriteMaskFrontFaces(R2Stencils.LIGHT_MASK_BIT);

        this.clip_screen_stencil_state.setStencilState(ss);
      }

      {
        /*
          Configure rendering state for clipping volumes.
         */

        this.clip_volume_stencil_state =
          JCGLRenderStateMutable.create();

        /*
          Use a standard less-than-or-equal depth test for the clip volume.
         */

        this.clip_volume_stencil_state.setDepthState(JCGLDepthState.of(
          JCGLDepthStrict.DEPTH_STRICT_ENABLED,
          Optional.of(JCGLDepthFunction.DEPTH_LESS_THAN_OR_EQUAL),
          JCGLDepthWriting.DEPTH_WRITE_DISABLED,
          JCGLDepthClamping.DEPTH_CLAMP_ENABLED
        ));

        this.clip_volume_stencil_state.setColorBufferMaskingState(
          JCGLColorBufferMaskingState.of(false, false, false, false));

        /*
          Configure stencil settings that will, in effect, set the
          {@link R2Stencils#LIGHT_MASK_BIT} for any fragment the light volume
          touches.
         */

        final JCGLStencilStateMutable ss = JCGLStencilStateMutable.create();
        ss.setStencilEnabled(true);
        ss.setStencilStrict(true);

        ss.setOperationDepthFailBack(JCGLStencilOperation.STENCIL_OP_INVERT);
        ss.setOperationStencilFailBack(JCGLStencilOperation.STENCIL_OP_KEEP);
        ss.setOperationPassBack(JCGLStencilOperation.STENCIL_OP_KEEP);

        ss.setOperationDepthFailFront(JCGLStencilOperation.STENCIL_OP_INVERT);
        ss.setOperationStencilFailFront(JCGLStencilOperation.STENCIL_OP_KEEP);
        ss.setOperationPassFront(JCGLStencilOperation.STENCIL_OP_KEEP);

        ss.setTestFunctionBack(JCGLStencilFunction.STENCIL_ALWAYS);
        ss.setTestMaskBack(R2Stencils.LIGHT_MASK_BIT);
        ss.setTestReferenceBack(R2Stencils.LIGHT_MASK_BIT);
        ss.setWriteMaskBackFaces(R2Stencils.LIGHT_MASK_BIT);

        ss.setTestFunctionFront(JCGLStencilFunction.STENCIL_ALWAYS);
        ss.setTestMaskFront(R2Stencils.LIGHT_MASK_BIT);
        ss.setTestReferenceFront(R2Stencils.LIGHT_MASK_BIT);
        ss.setWriteMaskFrontFaces(R2Stencils.LIGHT_MASK_BIT);

        this.clip_volume_stencil_state.setStencilState(ss);
      }

      {
        this.render_state_screen = JCGLRenderStateMutable.create();
        
        /*
         * Write RGB, ignore alpha.
         */

        this.render_state_screen.setColorBufferMaskingState(
          JCGLColorBufferMaskingState.of(true, true, true, false));

        /*
         * The light contributions are summed with pure additive blending.
         */

        this.render_state_screen.setBlendState(
          Optional.of(JCGLBlendState.of(
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendEquation.BLEND_EQUATION_ADD,
            JCGLBlendEquation.BLEND_EQUATION_ADD)));

        /*
         * For full-screen quads, the front faces should be rendered.
         */

        this.render_state_screen.setCullingState(
          Optional.of(JCGLCullingState.of(
            JCGLFaceSelection.FACE_BACK,
            JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE)));

        /*
         * No depth testing and no depth writing is required.
         */

        this.render_state_screen.setDepthState(JCGLDepthState.of(
          JCGLDepthStrict.DEPTH_STRICT_ENABLED,
          Optional.empty(),
          JCGLDepthWriting.DEPTH_WRITE_DISABLED,
          JCGLDepthClamping.DEPTH_CLAMP_ENABLED));

        /*
         * Configure the stencil test such that only those pixels that
         * belong to the current group AND have the {@link R2Stencils#LIGHT_MASK_BIT}
         * set are touched. Stencil writing is disabled.
         */

        final JCGLStencilStateMutable ss = JCGLStencilStateMutable.create();
        this.render_stencil_state = ss;

        ss.setStencilEnabled(true);
        ss.setStencilStrict(true);

        ss.setOperationDepthFailBack(JCGLStencilOperation.STENCIL_OP_KEEP);
        ss.setOperationStencilFailBack(JCGLStencilOperation.STENCIL_OP_KEEP);
        ss.setOperationPassBack(JCGLStencilOperation.STENCIL_OP_KEEP);

        ss.setOperationDepthFailFront(JCGLStencilOperation.STENCIL_OP_KEEP);
        ss.setOperationStencilFailFront(JCGLStencilOperation.STENCIL_OP_KEEP);
        ss.setOperationPassFront(JCGLStencilOperation.STENCIL_OP_KEEP);

        ss.setTestFunctionBack(JCGLStencilFunction.STENCIL_EQUAL);
        ss.setTestFunctionFront(JCGLStencilFunction.STENCIL_EQUAL);
        ss.setTestMaskFront(R2Stencils.GROUP_BITS | R2Stencils.LIGHT_MASK_BIT);
        ss.setTestMaskBack(R2Stencils.GROUP_BITS | R2Stencils.LIGHT_MASK_BIT);

        final int ref =
          (this.input_state.group << R2Stencils.GROUP_LEFT_SHIFT)
            | R2Stencils.LIGHT_MASK_BIT;

        // These are populated with the shifted group index on rendering
        ss.setTestReferenceFront(0);
        ss.setTestReferenceBack(0);

        ss.setWriteMaskBackFaces(0);
        ss.setWriteMaskFrontFaces(0);

        this.render_state_screen.setStencilState(ss);
      }

      {
        this.render_state_volume = JCGLRenderStateMutable.create();

        /*
         * Work around an issue with @Default on Immutables @Modifiable values;
         * the call to getDepthState will return a freshly allocated value
         * every time unless setDepthState has been called at least once.
         */

        this.render_state_volume.setDepthState(
          this.render_state_volume.depthState());

        /*
         * Write RGB, ignore alpha.
         */

        this.render_state_volume.setColorBufferMaskingState(
          JCGLColorBufferMaskingState.of(true, true, true, false));

        /*
         * The light contributions are summed with pure additive blending.
         */

        this.render_state_volume.setBlendState(
          Optional.of(JCGLBlendState.of(
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendFunction.BLEND_ONE,
            JCGLBlendEquation.BLEND_EQUATION_ADD,
            JCGLBlendEquation.BLEND_EQUATION_ADD)));

        /*
         * For typical volume lights, only the back faces should be
         * rendered.
         */

        this.render_state_volume.setCullingState(
          Optional.of(JCGLCullingState.of(
            JCGLFaceSelection.FACE_FRONT,
            JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE)));

        /*
         * No depth testing and no depth writing is required.
         */

        this.render_state_volume.setDepthState(JCGLDepthState.of(
          JCGLDepthStrict.DEPTH_STRICT_ENABLED,
          Optional.empty(),
          JCGLDepthWriting.DEPTH_WRITE_DISABLED,
          JCGLDepthClamping.DEPTH_CLAMP_ENABLED));

        /*
         * See above for the stencil test.
         */

        this.render_state_volume.setStencilState(this.render_stencil_state);
      }
    }

    @Override
    public void onStart()
    {
      final JCGLProfilingContextType pc_clipped =
        this.input_state.parent.profiling_context.childContext("clipped");
      final JCGLProfilingContextType pc_setup =
        pc_clipped.childContext("setup");

      pc_setup.startMeasuringIfEnabled();
      try {
        this.clearStencilForClipVolume();
        this.renderStencilForClipVolume();
        this.configureStencilsForLights();
      } finally {
        pc_setup.stopMeasuringIfEnabled();
      }

      this.profiling_instances = pc_clipped.childContext("instances");
      this.profiling_instances.startMeasuringIfEnabled();
    }

    /*
     * Configure the stencil reference value such that the stencil test checks
     * for the current group number and the {@link R2Stencils#LIGHT_MASK_BIT}.
     */

    private void configureStencilsForLights()
    {
      R2Stencils.checkValidGroup(this.input_state.group);
      final int ref =
        (this.input_state.group << R2Stencils.GROUP_LEFT_SHIFT)
          | R2Stencils.LIGHT_MASK_BIT;

      this.render_stencil_state.setTestReferenceFront(ref);
      this.render_stencil_state.setTestReferenceBack(ref);
      this.render_state_volume.setStencilState(this.render_stencil_state);
      this.render_state_screen.setStencilState(this.render_stencil_state);
    }

    /*
     * Render a volume that will set the {@link R2Stencils#LIGHT_MASK_BIT} for
     * all pixels that the light should affect.
     */

    private void renderStencilForClipVolume()
    {
      JCGLRenderStates.activate(this.g33, this.clip_volume_stencil_state);

      final JCGLTextureUnitContextType tc =
        this.input_state.light_base_context.unitContextNew();

      try {
        this.clip_volume_stencil.onActivate(this.g33);

        try {
          this.clip_volume_stencil.onReceiveViewValues(
            this.g33, this.configureViewParameters());
          this.clip_volume_stencil.onReceiveMaterialValues(
            this.g33, this.configureMaterialParameters(tc, Unit.unit()));

          this.input_state.parent.matrices.withTransform(
            this.input_state.volume.transform(),
            this.input_state.volume.uvMatrix(),
            this,
            (mi, t) -> {
              t.clip_volume_stencil.onReceiveInstanceTransformValues(t.g33, mi);
              t.clip_volume_stencil.onValidate();
              t.array_objects.arrayObjectBind(
                t.input_state.volume.arrayObject());

              try {
                t.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              } finally {
                t.array_objects.arrayObjectUnbind();
              }

              return Unit.unit();
            }
          );

        } finally {
          this.clip_volume_stencil.onDeactivate(this.g33);
        }

      } finally {
        tc.unitContextFinish(this.textures);
      }
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

    @SuppressWarnings("unchecked")
    private <M extends R2LightSingleReadableType> R2ShaderParametersLightType<M>
    configureLightParameters(
      final JCGLTextureUnitContextType tc,
      final M p)
    {
      this.params_light.clear();
      this.params_light.setTextureUnitContext(tc);
      this.params_light.setObserverMatrices(this.parent.matrices);
      this.params_light.setViewport(this.parent.viewport);
      this.params_light.setValues(p);
      Invariants.checkInvariant(
        this.params_light.isInitialized(),
        "Light parameters must be initialized");
      return (R2ShaderParametersLightType<M>) this.params_light;
    }

    private R2ShaderParametersViewMutable configureViewParameters()
    {
      this.params_view.clear();
      this.params_view.setViewport(this.parent.viewport);
      this.params_view.setObserverMatrices(this.parent.matrices);
      Invariants.checkInvariant(
        this.params_view.isInitialized(),
        "View parameters must be initialized");
      return this.params_view;
    }

    /*
     * Clear the {@link R2Stencils#LIGHT_MASK_BIT} for all pixels in the stencil
     * buffer.
     */

    private void clearStencilForClipVolume()
    {
      JCGLRenderStates.activate(this.g33, this.clip_screen_stencil_state);

      final JCGLTextureUnitContextType tc =
        this.input_state.light_base_context.unitContextNew();

      try {
        this.clip_screen_stencil.onActivate(this.g33);

        try {
          this.clip_screen_stencil.onReceiveViewValues(
            this.g33, this.configureViewParameters());
          this.clip_screen_stencil.onReceiveMaterialValues(
            this.g33, this.configureMaterialParameters(tc, Unit.unit()));
          this.clip_screen_stencil.onValidate();

          this.array_objects.arrayObjectBind(this.quad.arrayObject());
          try {
            this.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
          } finally {
            this.array_objects.arrayObjectUnbind();
          }

        } finally {
          this.clip_screen_stencil.onDeactivate(this.g33);
        }

      } finally {
        tc.unitContextFinish(this.textures);
      }
    }

    private void onLightSingleScreen(
      final R2LightScreenSingleType light)
    {
      // Nothing!
    }

    @SuppressWarnings("unchecked")
    private void onLightSingleProjectiveWithoutShadow(
      final R2LightProjectiveWithoutShadowReadableType light)
    {
      final R2ShaderLightProjectiveUsableType<
        R2LightProjectiveReadableType> s =
        R2ShaderLightProjectiveUsableType.class.cast(
          this.light_shader);

      this.input_state.parent.matrices.withProjectiveLight(
        light, this, (mp, t) -> {
          s.onReceiveVolumeLightTransform(t.g33, mp);
          s.onReceiveProjectiveLight(t.g33, mp);
          s.onValidate();
          return Unit.unit();
        });
    }

    @SuppressWarnings("unchecked")
    private void onLightSingleSpherical(
      final R2LightSphericalSingleReadableType light)
    {
      final R2ShaderLightVolumeSingleUsableType<
        R2LightSphericalSingleReadableType> s =
        R2ShaderLightVolumeSingleUsableType.class.cast(
          this.light_shader);

      this.input_state.parent.matrices.withVolumeLight(light, this, (mv, t) -> {
        s.onReceiveVolumeLightTransform(t.g33, mv);
        s.onValidate();
        return Unit.unit();
      });
    }

    private void onLightSingleProjective(
      final R2LightProjectiveReadableType light)
    {
      light.matchProjectiveReadable(this, (t, pw) -> {
        Preconditions.checkPrecondition(
          t.light_shader instanceof R2ShaderLightProjectiveUsableType,
          "Shader must be a projective light shader");

        t.onLightSingleProjectiveWithoutShadow(pw);
        return Unit.unit();
      }, (t, pw) -> {
        Preconditions.checkPrecondition(
          t.light_shader instanceof R2ShaderLightProjectiveUsableType,
          "Shader must be a projective with shadow light shader");

        t.onLightSingleProjectiveWithShadow(pw);
        return Unit.unit();
      });
    }

    @SuppressWarnings("unchecked")
    private void onLightSingleProjectiveWithShadow(
      final R2LightProjectiveWithShadowReadableType light)
    {
      final R2ShaderLightProjectiveWithShadowUsableType<
        R2LightProjectiveWithShadowReadableType> s =
        R2ShaderLightProjectiveWithShadowUsableType.class.cast(
          this.light_shader);

      this.light_shadow = light;
      try {
        this.input_state.parent.matrices.withProjectiveLight(
          light, this, (mp, t) -> {
            s.onReceiveVolumeLightTransform(t.g33, mp);
            s.onReceiveProjectiveLight(t.g33, mp);

            final R2Texture2DUsableType map =
              t.input_state.parent.shadow_maps.shadowMapGet(t.light_shadow);

            s.onReceiveShadowMap(
              t.g33,
              t.light_each_context,
              map);
            s.onValidate();
            return Unit.unit();
          });
      } finally {
        this.light_shadow = null;
      }
    }

    private void onLightSingleVolume(
      final R2LightVolumeSingleReadableType light)
    {
      light.matchLightVolumeSingleReadable(this, (t, pl) -> {
        Preconditions.checkPrecondition(
          t.light_shader instanceof R2ShaderLightProjectiveUsableType,
          "Shader must be a projective light shader");

        t.onLightSingleProjective(pl);
        return Unit.unit();
      }, (t, sl) -> {
        Preconditions.checkPrecondition(
          t.light_shader instanceof R2ShaderLightVolumeSingleUsableType,
          "Shader must be a volume light shader");

        t.onLightSingleSpherical(sl);
        return Unit.unit();
      });
    }

    @Override
    public <M extends R2LightSingleReadableType>
    void onLightSingleShaderStart(
      final R2ShaderLightSingleUsableType<M> s)
    {
      s.onActivate(this.g33);
      s.onReceiveBoundGeometryBufferTextures(
        this.g33,
        this.input_state.parent.gbuffer,
        this.input_state.unit_albedo,
        this.input_state.unit_specular,
        this.input_state.unit_depth,
        this.input_state.unit_normals);
    }

    @Override
    public void onLightSingleArrayStart(
      final R2LightSingleReadableType i)
    {
      this.array_objects.arrayObjectBind(i.arrayObject());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <M extends R2LightSingleReadableType>
    void onLightSingle(
      final R2ShaderLightSingleUsableType<M> s,
      final M light)
    {
      try {
        this.light_shader =
          (R2ShaderLightSingleUsableType<R2LightSingleReadableType>) s;
        this.light_each_context =
          this.input_state.light_base_context.unitContextNew();

        final Class<R2LightSingleReadableType> s_class =
          this.light_shader.shaderParametersType();
        final Class<? extends R2LightSingleReadableType> l_class =
          light.getClass();

        Preconditions.checkPrecondition(
          s_class.isAssignableFrom(l_class),
          "Shader parameter type must be compatible with light type");

        s.onReceiveValues(
          this.g33,
          this.configureLightParameters(this.light_each_context, light));

        light.matchLightSingle(
          this,
          (t, lv) -> {
            Preconditions.checkPrecondition(
              t.light_shader instanceof R2ShaderLightVolumeSingleUsableType,
              "Shader must be a volume light shader");

            JCGLRenderStates.activate(t.g33, t.render_state_volume);
            t.onLightSingleVolume(lv);
            return Unit.unit();
          },
          (t, ls) -> {
            Preconditions.checkPrecondition(
              t.light_shader instanceof R2ShaderLightScreenSingleUsableType,
              "Shader must be a screen light shader");

            JCGLRenderStates.activate(t.g33, t.render_state_screen);
            t.onLightSingleScreen(ls);
            return Unit.unit();
          });

        this.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
      } finally {
        this.light_each_context.unitContextFinish(this.textures);
        this.light_each_context = null;
      }
    }

    @Override
    public <M extends R2LightSingleReadableType>
    void onLightSingleShaderFinish(
      final R2ShaderLightSingleUsableType<M> s)
    {
      s.onDeactivate(this.g33);
    }

    @Override
    public void onFinish()
    {
      this.profiling_instances.stopMeasuringIfEnabled();
      this.profiling_instances = null;
    }
  }

  private static final class LightClipGroupConsumerInputState
  {
    private final LightConsumerInputState parent;
    private @Nullable JCGLTextureUnitType unit_albedo;
    private @Nullable JCGLTextureUnitType unit_normals;
    private @Nullable JCGLTextureUnitType unit_specular;
    private @Nullable JCGLTextureUnitType unit_depth;
    private @Nullable JCGLTextureUnitContextType light_base_context;
    private @Nullable R2InstanceSingleType volume;
    private int group;

    LightClipGroupConsumerInputState(
      final LightConsumerInputState in_parent)
    {
      this.parent = NullCheck.notNull(in_parent, "Parent");
    }

    void set(
      final R2InstanceSingleType in_volume,
      final int in_group,
      final JCGLTextureUnitType in_unit_albedo,
      final JCGLTextureUnitType in_unit_depth,
      final JCGLTextureUnitType in_unit_normals,
      final JCGLTextureUnitType in_unit_specular,
      final JCGLTextureUnitContextType in_light_base_context)
    {
      this.volume = in_volume;
      this.group = in_group;
      this.unit_albedo =
        NullCheck.notNull(in_unit_albedo, "Albedo texture");
      this.unit_normals =
        NullCheck.notNull(in_unit_normals, "Normals texture");
      this.unit_specular =
        NullCheck.notNull(in_unit_specular, "Specular texture");
      this.unit_depth =
        NullCheck.notNull(in_unit_depth, "Depth texture");
      this.light_base_context =
        NullCheck.notNull(in_light_base_context, "Context");
    }

    void clear()
    {
      this.group = -1;
      this.volume = null;
      this.unit_albedo = null;
      this.unit_depth = null;
      this.unit_normals = null;
      this.unit_specular = null;
      this.light_base_context = null;
    }
  }

  private static final class LightConsumer implements
    R2SceneLightsConsumerType
  {
    private final JCGLInterfaceGL33Type g33;
    private final JCGLTexturesType textures;
    private final LightGroupConsumer group_consumer;
    private final LightConsumerInputState input_state;
    private final LightClipGroupConsumer clip_group_consumer;
    private final R2TextureDefaultsType texture_defaults;

    private @Nullable JCGLTextureUnitType unit_albedo;
    private @Nullable JCGLTextureUnitType unit_normals;
    private @Nullable JCGLTextureUnitType unit_specular;
    private @Nullable JCGLTextureUnitType unit_depth;
    private @Nullable JCGLTextureUnitContextType light_base_context;

    LightConsumer(
      final JCGLInterfaceGL33Type in_g,
      final R2TextureDefaultsType in_defaults,
      final R2ShaderInstanceSingleUsableType<Unit> in_clip_volume_stencil,
      final R2ShaderInstanceSingleScreenUsableType<Unit> in_clip_screen_stencil,
      final R2UnitQuadUsableType in_quad)
    {
      this.g33 = NullCheck.notNull(in_g, "G33");
      NullCheck.notNull(in_clip_volume_stencil, "Volume stencil");

      this.texture_defaults = NullCheck.notNull(in_defaults, "Defaults");
      this.input_state =
        new LightConsumerInputState();
      this.group_consumer =
        new LightGroupConsumer(this.g33, this.input_state);
      this.clip_group_consumer =
        new LightClipGroupConsumer(
          this.g33,
          this.input_state,
          in_clip_volume_stencil,
          in_clip_screen_stencil,
          in_quad);

      this.textures = this.g33.textures();
    }

    @Override
    public void onStart()
    {
      NullCheck.notNull(this.g33, "g33");

      /*
       * Create a new texture context and bind the geometry buffer textures
       * to it.
       */

      this.light_base_context =
        this.input_state.texture_context.unitContextNewWithReserved(4);

      this.unit_albedo =
        this.light_base_context.unitContextBindTexture2D(
          this.textures,
          this.input_state.gbuffer.albedoEmissiveTexture().texture());
      this.unit_normals =
        this.light_base_context.unitContextBindTexture2D(
          this.textures,
          this.input_state.gbuffer.normalTexture().texture());
      this.unit_specular =
        this.light_base_context.unitContextBindTexture2D(
          this.textures,
          this.input_state.gbuffer.specularTextureOrDefault(
            this.texture_defaults).texture());
      this.unit_depth =
        this.light_base_context.unitContextBindTexture2D(
          this.textures,
          this.input_state.gbuffer.depthTexture().texture());
    }

    @Override
    public R2SceneLightsClipGroupConsumerType onStartClipGroup(
      final R2InstanceSingleType i,
      final int group)
    {
      this.clip_group_consumer.input_state.set(
        i,
        group,
        this.unit_albedo,
        this.unit_depth,
        this.unit_normals,
        this.unit_specular,
        this.light_base_context
      );
      return this.clip_group_consumer;
    }

    @Override
    public R2SceneLightsGroupConsumerType onStartGroup(
      final int group)
    {
      this.group_consumer.input_state.set(
        group,
        this.unit_albedo,
        this.unit_depth,
        this.unit_normals,
        this.unit_specular,
        this.light_base_context,
        this.input_state.viewport);
      return this.group_consumer;
    }

    @Override
    public void onFinish()
    {
      this.group_consumer.input_state.clear();
      this.light_base_context.unitContextFinish(this.textures);
      this.light_base_context = null;
    }
  }
}
