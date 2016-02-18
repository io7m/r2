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
import com.io7m.jcanephora.core.api.JCGLBlendingType;
import com.io7m.jcanephora.core.api.JCGLColorBufferMaskingType;
import com.io7m.jcanephora.core.api.JCGLCullingType;
import com.io7m.jcanephora.core.api.JCGLDepthBuffersType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLStencilBuffersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.core.api.JCGLViewportsType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import org.valid4j.Assertive;

import java.util.EnumSet;
import java.util.Set;

/**
 * The default implementation of the {@link R2LightRendererType} interface.
 */

public final class R2LightRenderer implements R2LightRendererType
{
  private static final Set<JCGLFramebufferBlitBuffer> DEPTH_STENCIL;

  static {
    DEPTH_STENCIL = EnumSet.of(
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_DEPTH,
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_STENCIL);
  }

  private final LightConsumer         light_consumer;
  private final JCGLInterfaceGL33Type g;
  private       boolean               deleted;

  private R2LightRenderer(final JCGLInterfaceGL33Type in_g)
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

  public static R2LightRendererType newRenderer(
    final JCGLInterfaceGL33Type
      in_g)
  {
    return new R2LightRenderer(in_g);
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
    final R2MatricesObserverType m,
    final R2SceneOpaqueLightsType s)
  {
    NullCheck.notNull(this.g);
    NullCheck.notNull(gbuffer);
    NullCheck.notNull(lbuffer);
    NullCheck.notNull(uc);
    NullCheck.notNull(m);
    NullCheck.notNull(s);

    Assertive.require(!this.isDeleted(), "Renderer not deleted");

    final JCGLFramebufferUsableType lb_fb = lbuffer.getPrimaryFramebuffer();
    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();

    try {
      g_fb.framebufferDrawBind(lb_fb);
      this.renderLightsWithBoundBuffer(gbuffer, lbuffer.getArea(), uc, m, s);
    } finally {
      g_fb.framebufferDrawUnbind();
    }
  }

  @Override
  public void renderLightsWithBoundBuffer(
    final R2GeometryBufferUsableType gbuffer,
    final AreaInclusiveUnsignedLType lbuffer_area,
    final R2TextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2SceneOpaqueLightsType s)
  {
    NullCheck.notNull(this.g);
    NullCheck.notNull(gbuffer);
    NullCheck.notNull(lbuffer_area);
    NullCheck.notNull(uc);
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
      R2LightRenderer.DEPTH_STENCIL,
      JCGLFramebufferBlitFilter.FRAMEBUFFER_BLIT_FILTER_NEAREST);
    g_fb.framebufferReadUnbind();

    if (s.opaqueLightsCount() > 0L) {

      /**
       * Configure state for light geometry rendering.
       */

      final JCGLDepthBuffersType g_db = this.g.getDepthBuffers();
      final JCGLBlendingType g_b = this.g.getBlending();
      final JCGLColorBufferMaskingType g_cm = this.g.getColorBufferMasking();

      g_b.blendingEnable(
        JCGLBlendFunction.BLEND_ONE, JCGLBlendFunction.BLEND_ONE);
      g_cm.colorBufferMask(true, true, true, true);
      g_db.depthClampingEnable();
      g_db.depthBufferWriteDisable();
      g_v.viewportSet(lbuffer_area);

      this.light_consumer.gbuffer = gbuffer;
      this.light_consumer.matrices = m;
      this.light_consumer.texture_context = uc;
      this.light_consumer.viewport = lbuffer_area;
      try {
        s.opaqueLightsExecute(this.light_consumer);
      } finally {
        this.light_consumer.matrices = null;
        this.light_consumer.gbuffer = null;
        this.light_consumer.texture_context = null;
        this.light_consumer.viewport = null;
      }
    }
  }

  private static final class LightConsumer implements
    R2SceneOpaqueLightsConsumerType
  {
    private final JCGLInterfaceGL33Type  g33;
    private final JCGLCullingType        culling;
    private final JCGLShadersType        shaders;
    private final JCGLTexturesType       textures;
    private final JCGLArrayObjectsType   array_objects;
    private final JCGLDrawType           draw;
    private final JCGLDepthBuffersType   depth;
    private final JCGLStencilBuffersType stencils;

    private @Nullable R2MatricesObserverType         matrices;
    private @Nullable R2GeometryBufferUsableType     gbuffer;
    private @Nullable JCGLTextureUnitType            unit_albedo;
    private @Nullable JCGLTextureUnitType            unit_normals;
    private @Nullable JCGLTextureUnitType            unit_specular;
    private @Nullable JCGLTextureUnitType            unit_depth;
    private @Nullable R2TextureUnitContextParentType texture_context;

    private R2LightSingleType                                light;
    private R2ShaderLightSingleUsableType<R2LightSingleType> light_shader;
    private R2TextureUnitContextType                         light_base_context;
    private AreaInclusiveUnsignedLType                       viewport;

    LightConsumer(final JCGLInterfaceGL33Type in_g)
    {
      this.g33 = NullCheck.notNull(in_g);

      this.shaders = this.g33.getShaders();
      this.textures = this.g33.getTextures();
      this.array_objects = this.g33.getArrayObjects();
      this.draw = this.g33.getDraw();
      this.stencils = this.g33.getStencilBuffers();
      this.culling = this.g33.getCulling();
      this.depth = this.g33.getDepthBuffers();
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
        this.texture_context.unitContextNew();
      this.unit_albedo =
        this.light_base_context.unitContextBindTexture2D(
          this.textures, this.gbuffer.getAlbedoEmissiveTexture());
      this.unit_normals =
        this.light_base_context.unitContextBindTexture2D(
          this.textures, this.gbuffer.getNormalTexture());
      this.unit_specular =
        this.light_base_context.unitContextBindTexture2D(
          this.textures, this.gbuffer.getSpecularTexture());
      this.unit_depth =
        this.light_base_context.unitContextBindTexture2D(
          this.textures, this.gbuffer.getDepthTexture());
    }

    @Override
    public void onFinish()
    {
      this.light_base_context.unitContextFinish(this.textures);
      this.light_base_context = null;
    }

    @Override
    public void onStartGroup(final int group)
    {
      this.stencils.stencilBufferEnable();
      this.stencils.stencilBufferOperation(
        JCGLFaceSelection.FACE_FRONT_AND_BACK,
        JCGLStencilOperation.STENCIL_OP_KEEP,
        JCGLStencilOperation.STENCIL_OP_KEEP,
        JCGLStencilOperation.STENCIL_OP_KEEP);
      this.stencils.stencilBufferMask(
        JCGLFaceSelection.FACE_FRONT_AND_BACK,
        0);
      this.stencils.stencilBufferFunction(
        JCGLFaceSelection.FACE_FRONT_AND_BACK,
        JCGLStencilFunction.STENCIL_EQUAL,
        group,
        R2Stencils.GROUP_BITS);
    }

    @Override
    public <M extends R2LightSingleType> void onLightSingleShaderStart(
      final R2ShaderLightSingleUsableType<M> s)
    {
      this.shaders.shaderActivateProgram(s.getShaderProgram());
      s.setGBuffer(
        this.shaders,
        this.gbuffer,
        this.unit_albedo,
        this.unit_specular,
        this.unit_depth,
        this.unit_normals);
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
      this.light = i;
      this.light_shader = (R2ShaderLightSingleUsableType<R2LightSingleType>) s;

      try {

        /**
         * Create a new texture context for this particular light.
         */

        final R2TextureUnitContextType uc =
          this.light_base_context.unitContextNew();

        try {

          /**
           * For full-screen quads, the front faces should be rendered. For
           * everything else, render only back faces.
           *
           * The fragments of the back faces of the light volume will have a
           * depth greater than or equal to the geometry fragments that
           * should be affected.
           */

          if (i instanceof R2LightScreenSingleType) {
            this.culling.cullingEnable(
              JCGLFaceSelection.FACE_BACK,
              JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE);
            this.depth.depthBufferTestDisable();
          } else {
            this.culling.cullingEnable(
              JCGLFaceSelection.FACE_FRONT,
              JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE);
            this.depth.depthBufferTestEnable(
              JCGLDepthFunction.DEPTH_GREATER_THAN_OR_EQUAL);
          }

          s.setLightTextures(
            this.textures, uc, i);
          s.setLightViewDependentValues(
            this.shaders, this.matrices, this.viewport, i);
          s.setLightValues(
            this.shaders, this.textures, i);

          this.matrices.withTransform(
            i.getTransform(),
            PMatrixI3x3F.identity(),
            this,
            (mi, t) -> {
              t.light_shader.setLightTransformDependentValues(
                t.shaders, mi, t.light);
              t.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
              return Unit.unit();
            });

        } finally {
          uc.unitContextFinish(this.textures);
        }

      } finally {
        this.light = null;
        this.light_shader = null;
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
