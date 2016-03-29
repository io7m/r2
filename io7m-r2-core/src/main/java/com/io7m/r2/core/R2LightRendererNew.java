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

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.JCGLBlendEquation;
import com.io7m.jcanephora.core.JCGLBlendFunction;
import com.io7m.jcanephora.core.JCGLDepthFunction;
import com.io7m.jcanephora.core.JCGLFaceSelection;
import com.io7m.jcanephora.core.JCGLFaceWindingOrder;
import com.io7m.jcanephora.core.JCGLFramebufferBlitBuffer;
import com.io7m.jcanephora.core.JCGLFramebufferBlitFilter;
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
import com.io7m.jcanephora.renderstate.JCGLBlendState;
import com.io7m.jcanephora.renderstate.JCGLCullingState;
import com.io7m.jcanephora.renderstate.JCGLDepthClamping;
import com.io7m.jcanephora.renderstate.JCGLDepthState;
import com.io7m.jcanephora.renderstate.JCGLDepthStrict;
import com.io7m.jcanephora.renderstate.JCGLDepthWriting;
import com.io7m.jcanephora.renderstate.JCGLRenderStateMutable;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.renderstate.JCGLStencilStateMutable;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.r2.core.shaders.types.R2ShaderLightProjectiveUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderLightProjectiveWithShadowUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderLightScreenSingleUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderLightSingleUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderLightVolumeSingleUsableType;
import org.valid4j.Assertive;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * The default implementation of the {@link R2LightRendererNewType} interface.
 */

public final class R2LightRendererNew implements R2LightRendererNewType
{
  private static final Set<JCGLFramebufferBlitBuffer> DEPTH_STENCIL;

  static {
    DEPTH_STENCIL = EnumSet.of(
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_DEPTH,
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_STENCIL);
  }

  private final LightConsumer light_consumer;
  private final JCGLInterfaceGL33Type g;
  private boolean deleted;

  private R2LightRendererNew(final JCGLInterfaceGL33Type in_g)
  {
    this.g = NullCheck.notNull(in_g);
    this.light_consumer = new LightConsumer(this.g);
  }

  /**
   * Construct a new renderer.
   *
   * @param in_g An OpenGL interface
   *
   * @return A new renderer
   */

  public static R2LightRendererNewType newRenderer(
    final JCGLInterfaceGL33Type
      in_g)
  {
    return new R2LightRendererNew(in_g);
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
  public void renderLights(
    final R2GeometryBufferUsableType gbuffer,
    final R2LightBufferUsableType lbuffer,
    final R2TextureUnitContextParentType uc,
    final R2ShadowMapContextUsableType shadows,
    final R2MatricesObserverType m,
    final R2SceneLightsType s)
  {
    NullCheck.notNull(gbuffer);
    NullCheck.notNull(lbuffer);
    NullCheck.notNull(uc);
    NullCheck.notNull(shadows);
    NullCheck.notNull(m);
    NullCheck.notNull(s);

    Assertive.require(!this.isDeleted(), "Renderer not deleted");

    final JCGLFramebufferUsableType lb_fb = lbuffer.getPrimaryFramebuffer();
    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();

    try {
      g_fb.framebufferDrawBind(lb_fb);
      this.renderLightsWithBoundBuffer(
        gbuffer, lbuffer.getArea(), uc, shadows, m, s);
    } finally {
      g_fb.framebufferDrawUnbind();
    }
  }

  @Override
  public void renderLightsWithBoundBuffer(
    final R2GeometryBufferUsableType gbuffer,
    final AreaInclusiveUnsignedLType lbuffer_area,
    final R2TextureUnitContextParentType uc,
    final R2ShadowMapContextUsableType shadows,
    final R2MatricesObserverType m,
    final R2SceneLightsType s)
  {
    NullCheck.notNull(gbuffer);
    NullCheck.notNull(lbuffer_area);
    NullCheck.notNull(uc);
    NullCheck.notNull(shadows);
    NullCheck.notNull(m);
    NullCheck.notNull(s);

    Assertive.require(!this.isDeleted(), "Renderer not deleted");

    final JCGLFramebufferUsableType gb_fb = gbuffer.getPrimaryFramebuffer();
    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();
    final JCGLViewportsType g_v = this.g.getViewports();

    /**
     * Copy the contents of the depth/stencil attachment of the G-Buffer to
     * the current depth/stencil buffer.
     */

    g_fb.framebufferReadBind(gb_fb);
    g_fb.framebufferBlit(
      gbuffer.getArea(),
      lbuffer_area,
      R2LightRendererNew.DEPTH_STENCIL,
      JCGLFramebufferBlitFilter.FRAMEBUFFER_BLIT_FILTER_NEAREST);
    g_fb.framebufferReadUnbind();

    if (s.lightsCount() > 0L) {
      g_v.viewportSet(lbuffer_area);

      this.light_consumer.input_state.set(
        gbuffer, m, uc, shadows, lbuffer_area);
      try {
        s.lightsExecute(this.light_consumer);
      } finally {
        this.light_consumer.input_state.clear();
      }
    }
  }

  private static final class LightGroupConsumerInputState
  {
    private final LightConsumerInputState parent;
    private @Nullable JCGLTextureUnitType unit_albedo;
    private @Nullable JCGLTextureUnitType unit_normals;
    private @Nullable JCGLTextureUnitType unit_specular;
    private @Nullable JCGLTextureUnitType unit_depth;
    private @Nullable R2TextureUnitContextType light_base_context;
    private @Nullable AreaInclusiveUnsignedLType viewport;
    private int group;

    LightGroupConsumerInputState(
      final LightConsumerInputState in_parent)
    {
      this.parent = NullCheck.notNull(in_parent);
    }

    void set(
      final int in_group,
      final JCGLTextureUnitType in_unit_albedo,
      final JCGLTextureUnitType in_unit_depth,
      final JCGLTextureUnitType in_unit_normals,
      final JCGLTextureUnitType in_unit_specular,
      final R2TextureUnitContextType in_light_base_context,
      final AreaInclusiveUnsignedLType in_viewport)
    {
      this.group = in_group;
      this.unit_albedo = NullCheck.notNull(in_unit_albedo);
      this.unit_normals = NullCheck.notNull(in_unit_normals);
      this.unit_specular = NullCheck.notNull(in_unit_specular);
      this.unit_depth = NullCheck.notNull(in_unit_depth);
      this.light_base_context = NullCheck.notNull(in_light_base_context);
      this.viewport = NullCheck.notNull(in_viewport);
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
    private final JCGLShadersType shaders;
    private final JCGLTexturesType textures;
    private final JCGLArrayObjectsType array_objects;
    private final JCGLDrawType draw;
    private final JCGLRenderStateMutable render_state_screen;
    private final JCGLRenderStateMutable render_state_volume;
    private final JCGLStencilStateMutable stencil_state_screen;
    private final JCGLStencilStateMutable stencil_state_volume;
    private final LightGroupConsumerInputState input_state;

    private @Nullable
    R2ShaderLightSingleUsableType<R2LightSingleReadableType> light_shader;
    private @Nullable R2TextureUnitContextType light_each_context;
    private @Nullable R2LightProjectiveWithShadowReadableType light_shadow;

    LightGroupConsumer(
      final JCGLInterfaceGL33Type in_g,
      final LightConsumerInputState in_input_state)
    {
      this.g33 = NullCheck.notNull(in_g);

      this.shaders = this.g33.getShaders();
      this.textures = this.g33.getTextures();
      this.array_objects = this.g33.getArrayObjects();
      this.draw = this.g33.getDraw();
      this.input_state = new LightGroupConsumerInputState(in_input_state);

      {
        this.render_state_screen = JCGLRenderStateMutable.create();

        /**
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

        /**
         * For full-screen quads, the front faces should be rendered.
         */

        this.render_state_screen.setCullingState(
          Optional.of(JCGLCullingState.of(
            JCGLFaceSelection.FACE_BACK,
            JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE)));

        /**
         * No depth testing and no depth writing is required.
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

        /**
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

        /**
         * For typical volume lights, only the back faces should be
         * rendered.
         */

        this.render_state_volume.setCullingState(
          Optional.of(JCGLCullingState.of(
            JCGLFaceSelection.FACE_FRONT,
            JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE)));

        /**
         * The fragments of the back faces of the light volume will have a
         * depth greater than or equal to the geometry fragments that
         * should be affected. No depth writing is required.
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

      ss.setOperationDepthFailFront(
        JCGLStencilOperation.STENCIL_OP_KEEP);
      ss.setOperationStencilFailFront(
        JCGLStencilOperation.STENCIL_OP_KEEP);
      ss.setOperationPassFront(
        JCGLStencilOperation.STENCIL_OP_KEEP);

      ss.setTestFunctionFront(
        JCGLStencilFunction.STENCIL_EQUAL);
      ss.setTestReferenceFront(
        group << R2Stencils.GROUP_LEFT_SHIFT);
      ss.setTestMaskFront(
        R2Stencils.GROUP_BITS);

      ss.setWriteMaskFrontFaces(0);

      ss.setOperationDepthFailBack(
        JCGLStencilOperation.STENCIL_OP_KEEP);
      ss.setOperationStencilFailBack(
        JCGLStencilOperation.STENCIL_OP_KEEP);
      ss.setOperationPassBack(
        JCGLStencilOperation.STENCIL_OP_KEEP);

      ss.setTestFunctionBack(
        JCGLStencilFunction.STENCIL_EQUAL);
      ss.setTestReferenceBack(
        group << R2Stencils.GROUP_LEFT_SHIFT);
      ss.setTestMaskBack(
        R2Stencils.GROUP_BITS);

      ss.setWriteMaskBackFaces(0);
    }

    @Override
    public void onStart()
    {
      R2Stencils.checkValidGroup(this.input_state.group);

      LightGroupConsumer.configureStencilState(
        this.input_state.group, this.stencil_state_screen);
      this.render_state_screen.setStencilState(this.stencil_state_screen);
      LightGroupConsumer.configureStencilState(
        this.input_state.group, this.stencil_state_volume);
      this.render_state_volume.setStencilState(this.stencil_state_volume);
    }

    @Override
    public <M extends R2LightSingleReadableType>
    void onLightSingleShaderStart(
      final R2ShaderLightSingleUsableType<M> s)
    {
      s.onActivate(this.shaders);
      s.onReceiveBoundGeometryBufferTextures(
        this.shaders,
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
      this.array_objects.arrayObjectBind(i.getArrayObject());
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
        Assertive.require(
          t.light_shader instanceof R2ShaderLightProjectiveUsableType);
        t.onLightSingleProjective(pl);
        return Unit.unit();
      }, (t, sl) -> {
        Assertive.require(
          t.light_shader instanceof R2ShaderLightVolumeSingleUsableType);
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
        s.onReceiveVolumeLightTransform(t.shaders, mv);
        s.onValidate();
        return Unit.unit();
      });
    }

    private void onLightSingleProjective(
      final R2LightProjectiveReadableType light)
    {
      light.matchProjectiveReadable(this, (t, pw) -> {
        Assertive.require(
          t.light_shader instanceof R2ShaderLightProjectiveUsableType);
        t.onLightSingleProjectiveWithoutShadow(pw);
        return Unit.unit();
      }, (t, pw) -> {
        Assertive.require(
          t.light_shader instanceof R2ShaderLightProjectiveWithShadowUsableType);
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
          s.onReceiveVolumeLightTransform(t.shaders, mp);
          s.onReceiveProjectiveLight(t.shaders, mp);

          final R2Texture2DUsableType map =
            t.input_state.parent.shadow_maps.shadowMapGet(t.light_shadow);

          s.onReceiveShadowMap(
            t.textures,
            t.shaders,
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
        s.onReceiveVolumeLightTransform(t.shaders, mp);
        s.onReceiveProjectiveLight(t.shaders, mp);
        s.onValidate();
        return Unit.unit();
      });
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
          this.light_shader.getShaderParametersType();
        final Class<? extends R2LightSingleReadableType> l_class =
          light.getClass();
        Assertive.require(s_class.isAssignableFrom(l_class));

        s.onReceiveValues(
          this.textures,
          this.shaders,
          this.light_each_context,
          this.input_state.viewport,
          light,
          this.input_state.parent.matrices);

        light.matchLightSingle(
          this,
          (t, lv) -> {
            Assertive.require(
              t.light_shader instanceof R2ShaderLightVolumeSingleUsableType);

            JCGLRenderStates.activate(t.g33, t.render_state_volume);
            t.onLightSingleVolume(lv);
            return Unit.unit();
          },
          (t, ls) -> {
            Assertive.require(
              t.light_shader instanceof R2ShaderLightScreenSingleUsableType);

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
      s.onDeactivate(this.shaders);
    }

    @Override
    public void onFinish()
    {
      // Nothing
    }
  }

  private static final class LightConsumerInputState
  {
    private @Nullable R2GeometryBufferUsableType gbuffer;
    private @Nullable R2MatricesObserverType matrices;
    private @Nullable R2TextureUnitContextParentType texture_context;
    private @Nullable R2ShadowMapContextUsableType shadow_maps;
    private @Nullable AreaInclusiveUnsignedLType viewport;

    LightConsumerInputState()
    {

    }

    void set(
      final R2GeometryBufferUsableType in_gbuffer,
      final R2MatricesObserverType in_m,
      final R2TextureUnitContextParentType in_texture_context,
      final R2ShadowMapContextUsableType in_shadows,
      final AreaInclusiveUnsignedLType in_viewport)
    {
      this.gbuffer = NullCheck.notNull(in_gbuffer);
      this.matrices = NullCheck.notNull(in_m);
      this.texture_context = NullCheck.notNull(in_texture_context);
      this.shadow_maps = NullCheck.notNull(in_shadows);
      this.viewport = NullCheck.notNull(in_viewport);
    }

    void clear()
    {
      this.gbuffer = null;
      this.matrices = null;
      this.texture_context = null;
      this.shadow_maps = null;
      this.viewport = null;
    }
  }

  private static final class LightConsumer implements
    R2SceneLightsConsumerType
  {
    private final JCGLInterfaceGL33Type g33;
    private final JCGLTexturesType textures;
    private final LightGroupConsumer group_consumer;
    private final LightConsumerInputState input_state;

    private @Nullable JCGLTextureUnitType unit_albedo;
    private @Nullable JCGLTextureUnitType unit_normals;
    private @Nullable JCGLTextureUnitType unit_specular;
    private @Nullable JCGLTextureUnitType unit_depth;
    private @Nullable R2TextureUnitContextType light_base_context;

    LightConsumer(final JCGLInterfaceGL33Type in_g)
    {
      this.g33 = NullCheck.notNull(in_g);
      this.input_state = new LightConsumerInputState();
      this.group_consumer = new LightGroupConsumer(this.g33, this.input_state);
      this.textures = this.g33.getTextures();
    }

    @Override
    public void onStart()
    {
      Assertive.require(this.g33 != null);

      /**
       * Create a new texture context and bind the geometry buffer textures
       * to it.
       */

      this.light_base_context =
        this.input_state.texture_context.unitContextNewWithReserved(4);
      this.unit_albedo =
        this.light_base_context.unitContextBindTexture2D(
          this.textures, this.input_state.gbuffer.getAlbedoEmissiveTexture());
      this.unit_normals =
        this.light_base_context.unitContextBindTexture2D(
          this.textures, this.input_state.gbuffer.getNormalTexture());
      this.unit_specular =
        this.light_base_context.unitContextBindTexture2D(
          this.textures, this.input_state.gbuffer.getSpecularTexture());
      this.unit_depth =
        this.light_base_context.unitContextBindTexture2D(
          this.textures, this.input_state.gbuffer.getDepthTexture());
    }

    @Override
    public R2SceneLightsClipGroupConsumerType onStartClipGroup(
      final R2InstanceSingleType i,
      final int group)
    {
      // TODO: Generated method stub!
      throw new UnimplementedCodeException();
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
