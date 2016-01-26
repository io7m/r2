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
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverType;
import com.io7m.r2.core.R2ShaderSourcesType;
import com.io7m.r2.core.R2TransformIdentity;
import com.io7m.r2.core.R2UnitQuadUsableType;
import org.valid4j.Assertive;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * The default implementation of the {@link R2EyePositionRendererType}
 * interface.
 */

public final class R2EyePositionRenderer implements R2EyePositionRendererType
{
  private static final Set<JCGLFramebufferBlitBuffer> DEPTH_STENCIL;

  static {
    DEPTH_STENCIL = EnumSet.of(
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_DEPTH,
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_STENCIL);
  }

  private final R2DebugShaderEyePosition shader;

  private R2EyePositionRenderer(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    this.shader = R2DebugShaderEyePosition.newShader(
      in_shaders, in_sources, in_pool);
  }

  /**
   * @param in_shaders A shader interface
   * @param in_sources Shader sources
   * @param in_pool    The ID pool
   *
   * @return A new renderer
   */

  public static R2EyePositionRendererType newRenderer(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool)
  {
    return new R2EyePositionRenderer(in_shaders, in_sources, in_pool);
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
  public void renderEyePosition(
    final JCGLInterfaceGL33Type g,
    final R2GeometryBufferUsableType gbuffer,
    final R2EyeZBufferUsableType zbuffer,
    final R2MatricesObserverType m,
    final R2UnitQuadUsableType q)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(gbuffer);
    NullCheck.notNull(zbuffer);
    NullCheck.notNull(m);
    NullCheck.notNull(q);

    Assertive.require(!this.isDeleted(), "Renderer not deleted");

    final JCGLFramebufferUsableType lb_fb = zbuffer.getFramebuffer();
    final JCGLFramebuffersType g_fb = g.getFramebuffers();

    try {
      g_fb.framebufferDrawBind(lb_fb);
      this.renderEyePositionWithBoundBuffer(g, gbuffer, zbuffer.getArea(), m, q);
    } finally {
      g_fb.framebufferDrawUnbind();
    }
  }

  @Override
  public void renderEyePositionWithBoundBuffer(
    final JCGLInterfaceGL33Type g,
    final R2GeometryBufferUsableType gbuffer,
    final AreaInclusiveUnsignedLType zbuffer_area,
    final R2MatricesObserverType m,
    final R2UnitQuadUsableType q)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(gbuffer);
    NullCheck.notNull(zbuffer_area);
    NullCheck.notNull(m);
    NullCheck.notNull(q);

    Assertive.require(!this.isDeleted(), "Renderer not deleted");

    final JCGLFramebufferUsableType gb_fb = gbuffer.getFramebuffer();

    final JCGLArrayObjectsType g_ao = g.getArrayObjects();
    final JCGLFramebuffersType g_fb = g.getFramebuffers();
    final JCGLDepthBuffersType g_db = g.getDepthBuffers();
    final JCGLBlendingType g_b = g.getBlending();
    final JCGLColorBufferMaskingType g_cm = g.getColorBufferMasking();
    final JCGLCullingType g_cu = g.getCulling();
    final JCGLTexturesType g_tex = g.getTextures();
    final JCGLShadersType g_sh = g.getShaders();
    final JCGLDrawType g_dr = g.getDraw();
    final JCGLStencilBuffersType g_st = g.getStencilBuffers();

    /**
     * Copy the contents of the depth/stencil attachment of the G-Buffer to
     * the current depth/stencil buffer.
     */

    g_fb.framebufferReadBind(gb_fb);
    g_fb.framebufferBlit(
      gbuffer.getArea(),
      zbuffer_area,
      R2EyePositionRenderer.DEPTH_STENCIL,
      JCGLFramebufferBlitFilter.FRAMEBUFFER_BLIT_FILTER_NEAREST);
    g_fb.framebufferReadUnbind();

    /**
     * Bind G-buffer.
     */

    final List<JCGLTextureUnitType> units = g_tex.textureGetUnits();
    final JCGLTextureUnitType unit_albedo = units.get(0);
    final JCGLTextureUnitType unit_normals = units.get(1);
    final JCGLTextureUnitType unit_specular = units.get(2);
    final JCGLTextureUnitType unit_depth = units.get(3);

    g_tex.texture2DBind(
      unit_albedo, gbuffer.getAlbedoEmissiveTexture().get());
    g_tex.texture2DBind(
      unit_normals, gbuffer.getNormalTexture().get());
    g_tex.texture2DBind(
      unit_depth, gbuffer.getDepthTexture().get());
    g_tex.texture2DBind(
      unit_specular, gbuffer.getSpecularTexture().get());

    g_b.blendingDisable();
    g_cm.colorBufferMask(true, true, true, true);
    g_cu.cullingDisable();
    g_db.depthClampingEnable();
    g_db.depthBufferWriteDisable();
    g_db.depthBufferTestDisable();
    g_st.stencilBufferDisable();

    try {
      g_sh.shaderActivateProgram(this.shader.getShaderProgram());
      g_ao.arrayObjectBind(q.getArrayObject());

      this.shader.setViewDependentValues(g_sh, m);
      this.shader.setGBuffer(
        g_sh,
        g_tex,
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
      g_tex.textureUnitUnbind(unit_albedo);
      g_tex.textureUnitUnbind(unit_normals);
      g_tex.textureUnitUnbind(unit_depth);
      g_tex.textureUnitUnbind(unit_specular);
    }
  }
}
