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
import com.io7m.jcanephora.core.JCGLFramebufferBlitBuffer;
import com.io7m.jcanephora.core.JCGLFramebufferBlitFilter;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.core.api.JCGLViewportsType;
import com.io7m.jcanephora.renderstate.JCGLRenderStateMutable;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverType;
import com.io7m.r2.core.shaders.R2ShaderSourcesType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2TextureUnitContextType;
import com.io7m.r2.core.R2TransformIdentity;
import com.io7m.r2.core.R2UnitQuadUsableType;
import org.valid4j.Assertive;

import java.util.EnumSet;
import java.util.Set;

/**
 * The default implementation of the {@link R2EyeZRendererType} interface.
 */

public final class R2EyeZRenderer implements R2EyeZRendererType
{
  private static final Set<JCGLFramebufferBlitBuffer> DEPTH_STENCIL;

  static {
    DEPTH_STENCIL = EnumSet.of(
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_DEPTH,
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_STENCIL);
  }

  private final R2DebugShaderEyeZ      shader;
  private final JCGLInterfaceGL33Type  g;
  private final JCGLRenderStateMutable render_state;

  private R2EyeZRenderer(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    this.g = NullCheck.notNull(in_g);
    this.shader = R2DebugShaderEyeZ.newShader(
      this.g.getShaders(), in_sources, in_pool);
    this.render_state =
      JCGLRenderStateMutable.create();
  }

  /**
   * @param in_g       An OpenGL interface
   * @param in_sources Shader sources
   * @param in_pool    The ID pool
   *
   * @return A new renderer
   */

  public static R2EyeZRendererType newRenderer(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return new R2EyeZRenderer(in_g, in_sources, in_pool);
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g33)
    throws R2Exception
  {
    if (!this.isDeleted()) {
      this.shader.delete(g33);
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.shader.isDeleted();
  }

  @Override
  public void renderEyeZ(
    final R2GeometryBufferUsableType gbuffer,
    final R2EyeZBufferUsableType zbuffer,
    final R2TextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2UnitQuadUsableType q)
  {
    NullCheck.notNull(gbuffer);
    NullCheck.notNull(zbuffer);
    NullCheck.notNull(uc);
    NullCheck.notNull(m);
    NullCheck.notNull(q);

    Assertive.require(!this.isDeleted(), "Renderer not deleted");

    final JCGLFramebufferUsableType lb_fb = zbuffer.getFramebuffer();
    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();

    try {
      g_fb.framebufferDrawBind(lb_fb);
      this.renderEyeZWithBoundBuffer(gbuffer, zbuffer.getArea(), uc, m, q);
    } finally {
      g_fb.framebufferDrawUnbind();
    }
  }

  @Override
  public void renderEyeZWithBoundBuffer(
    final R2GeometryBufferUsableType gbuffer,
    final AreaInclusiveUnsignedLType zbuffer_area,
    final R2TextureUnitContextParentType uc,
    final R2MatricesObserverType m,
    final R2UnitQuadUsableType q)
  {
    NullCheck.notNull(gbuffer);
    NullCheck.notNull(zbuffer_area);
    NullCheck.notNull(uc);
    NullCheck.notNull(m);
    NullCheck.notNull(q);

    Assertive.require(!this.isDeleted(), "Renderer not deleted");

    final JCGLFramebufferUsableType gb_fb = gbuffer.getPrimaryFramebuffer();

    final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();
    final JCGLTexturesType g_tex = this.g.getTextures();
    final JCGLShadersType g_sh = this.g.getShaders();
    final JCGLDrawType g_dr = this.g.getDraw();
    final JCGLViewportsType g_v = this.g.getViewports();

    /**
     * Copy the contents of the depth/stencil attachment of the G-Buffer to
     * the current depth/stencil buffer.
     */

    g_fb.framebufferReadBind(gb_fb);
    g_fb.framebufferBlit(
      gbuffer.getArea(),
      zbuffer_area,
      R2EyeZRenderer.DEPTH_STENCIL,
      JCGLFramebufferBlitFilter.FRAMEBUFFER_BLIT_FILTER_NEAREST);
    g_fb.framebufferReadUnbind();

    /**
     * Bind G-buffer.
     */

    final R2TextureUnitContextType tc = uc.unitContextNew();

    try {

      final JCGLTextureUnitType unit_albedo =
        tc.unitContextBindTexture2D(g_tex, gbuffer.getAlbedoEmissiveTexture());
      final JCGLTextureUnitType unit_normals =
        tc.unitContextBindTexture2D(g_tex, gbuffer.getNormalTexture());
      final JCGLTextureUnitType unit_specular =
        tc.unitContextBindTexture2D(g_tex, gbuffer.getSpecularTexture());
      final JCGLTextureUnitType unit_depth =
        tc.unitContextBindTexture2D(g_tex, gbuffer.getDepthTexture());

      JCGLRenderStates.activate(this.g, this.render_state);
      g_v.viewportSet(zbuffer_area);

      try {
        g_sh.shaderActivateProgram(this.shader.getShaderProgram());
        g_ao.arrayObjectBind(q.getArrayObject());

        this.shader.setViewDependentValues(g_sh, m);
        this.shader.setGBuffer(
          g_sh,
          gbuffer,
          unit_albedo,
          unit_specular,
          unit_depth,
          unit_normals);

        m.withTransform(
          R2TransformIdentity.getInstance(),
          PMatrixI3x3F.identity(),
          this,
          (mi, t) -> {
            g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
            return Unit.unit();
          });

      } finally {
        g_ao.arrayObjectUnbind();
        g_sh.shaderDeactivateProgram();
      }

    } finally {
      tc.unitContextFinish(g_tex);
    }
  }
}
