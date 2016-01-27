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

package com.io7m.r2.core.filters;

import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLColorBufferMaskingType;
import com.io7m.jcanephora.core.api.JCGLCullingType;
import com.io7m.jcanephora.core.api.JCGLDepthBuffersType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLStencilBuffersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2ImageBufferUsableType;
import com.io7m.r2.core.R2LightBufferUsableType;
import com.io7m.r2.core.R2ShaderSourcesType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2UnitQuadUsableType;

import java.util.List;

/**
 * The default implementation of the {@link R2FilterLightApplicatorType}
 * interface.
 */

public final class R2FilterLightApplicator implements
  R2FilterLightApplicatorType
{
  private final R2ShaderFilterLightApplicator           shader;
  private final R2UnitQuadUsableType                    quad;
  private final R2ShaderFilterLightApplicatorParameters params;

  private R2FilterLightApplicator(
    final R2ShaderFilterLightApplicatorParameters p,
    final R2ShaderFilterLightApplicator in_shader,
    final R2UnitQuadUsableType in_quad)
  {
    this.shader = NullCheck.notNull(in_shader);
    this.quad = NullCheck.notNull(in_quad);
    this.params = NullCheck.notNull(p);
  }

  /**
   * Construct a new filter.
   *
   * @param in_sources  Shader sources
   * @param in_textures A texture interface
   * @param in_g        A GL interface
   * @param in_pool     An ID pool
   * @param in_quad     A unit quad
   *
   * @return A new filter
   */

  public static R2FilterLightApplicatorType newFilter(
    final R2ShaderSourcesType in_sources,
    final R2TextureDefaultsType in_textures,
    final JCGLInterfaceGL33Type in_g,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    NullCheck.notNull(in_sources);
    NullCheck.notNull(in_textures);
    NullCheck.notNull(in_g);
    NullCheck.notNull(in_pool);
    NullCheck.notNull(in_quad);

    final R2ShaderFilterLightApplicatorParameters p =
      R2ShaderFilterLightApplicatorParameters.newParameters(
        in_textures);

    final R2ShaderFilterLightApplicator s =
      R2ShaderFilterLightApplicator.newShader(
        in_g.getShaders(),
        in_sources,
        in_pool);

    return new R2FilterLightApplicator(p, s, in_quad);
  }

  @Override
  public void runLightApplicator(
    final JCGLInterfaceGL33Type g,
    final R2GeometryBufferUsableType gbuffer,
    final R2LightBufferUsableType lbuffer,
    final R2ImageBufferUsableType ibuffer)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(gbuffer);
    NullCheck.notNull(lbuffer);
    NullCheck.notNull(ibuffer);

    final JCGLFramebuffersType g_fb = g.getFramebuffers();

    try {
      g_fb.framebufferDrawBind(ibuffer.getFramebuffer());
      this.runLightApplicatorWithBoundBuffer(g, gbuffer, lbuffer);
    } finally {
      g_fb.framebufferDrawUnbind();
    }
  }

  @Override
  public void runLightApplicatorWithBoundBuffer(
    final JCGLInterfaceGL33Type g,
    final R2GeometryBufferUsableType gbuffer,
    final R2LightBufferUsableType lbuffer)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(gbuffer);
    NullCheck.notNull(lbuffer);

    final JCGLDepthBuffersType g_db = g.getDepthBuffers();
    final JCGLCullingType g_cu = g.getCulling();
    final JCGLColorBufferMaskingType g_cm = g.getColorBufferMasking();
    final JCGLStencilBuffersType g_st = g.getStencilBuffers();
    final JCGLShadersType g_sh = g.getShaders();
    final JCGLDrawType g_dr = g.getDraw();
    final JCGLArrayObjectsType g_ao = g.getArrayObjects();
    final JCGLTexturesType g_tx = g.getTextures();

    if (g_db.depthBufferGetBits() > 0) {
      g_db.depthBufferTestDisable();
      g_db.depthBufferWriteDisable();
    }

    if (g_st.stencilBufferGetBits() > 0) {
      g_st.stencilBufferDisable();
    }

    g_cu.cullingDisable();
    g_cm.colorBufferMask(true, true, true, true);

    this.params.setAlbedoTexture(gbuffer.getAlbedoEmissiveTexture());
    this.params.setDiffuseTexture(lbuffer.getDiffuseTexture());
    this.params.setSpecularTexture(lbuffer.getSpecularTexture());

    try {
      g_sh.shaderActivateProgram(this.shader.getShaderProgram());
      this.shader.setTextures(g_tx, this.params);
      this.shader.setValues(g_sh, g_tx, this.params);

      g_ao.arrayObjectBind(this.quad.getArrayObject());
      g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
    } finally {
      g_ao.arrayObjectUnbind();
      g_sh.shaderDeactivateProgram();

      final List<JCGLTextureUnitType> units = g_tx.textureGetUnits();
      for (int index = 0; index < units.size(); ++index) {
        g_tx.textureUnitUnbind(units.get(0));
      }
    }
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    if (!this.isDeleted()) {
      this.shader.delete(g);
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.shader.isDeleted();
  }
}
