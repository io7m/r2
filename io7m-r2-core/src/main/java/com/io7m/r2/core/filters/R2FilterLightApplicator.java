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

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.JCGLPrimitives;
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
import com.io7m.jcanephora.core.api.JCGLViewportsType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2ImageBufferUsableType;
import com.io7m.r2.core.R2LightBufferUsableType;
import com.io7m.r2.core.R2ShaderSourcesType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2TextureUnitContextType;
import com.io7m.r2.core.R2UnitQuadUsableType;

/**
 * The default implementation of the {@link R2FilterLightApplicatorType}
 * interface.
 */

public final class R2FilterLightApplicator implements
  R2FilterLightApplicatorType
{
  private R2ShaderFilterLightApplicatorParameters shader_params;
  private final R2ShaderFilterLightApplicator           shader;
  private final R2UnitQuadUsableType                    quad;

  private R2FilterLightApplicator(
    final R2ShaderFilterLightApplicator in_shader,
    final R2UnitQuadUsableType in_quad)
  {
    this.shader = NullCheck.notNull(in_shader);
    this.quad = NullCheck.notNull(in_quad);
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

    final R2ShaderFilterLightApplicator s =
      R2ShaderFilterLightApplicator.newShader(
        in_g.getShaders(),
        in_sources,
        in_pool);

    return new R2FilterLightApplicator(s, in_quad);
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

  @Override
  public Class<R2FilterLightApplicatorParameters> getParametersType()
  {
    return R2FilterLightApplicatorParameters.class;
  }

  @Override
  public Class<R2ImageBufferUsableType> getRenderTargetType()
  {
    return R2ImageBufferUsableType.class;
  }

  @Override
  public void runFilter(
    final JCGLInterfaceGL33Type g,
    final R2TextureUnitContextParentType uc,
    final R2FilterLightApplicatorParameters parameters,
    final R2ImageBufferUsableType target)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(uc);
    NullCheck.notNull(parameters);
    NullCheck.notNull(target);

    final JCGLFramebuffersType g_fb = g.getFramebuffers();

    try {
      g_fb.framebufferDrawBind(target.getFramebuffer());
      this.runFilterWithBoundBuffer(g, uc, parameters, target.getArea());
    } finally {
      g_fb.framebufferDrawUnbind();
    }
  }

  @Override
  public void runFilterWithBoundBuffer(
    final JCGLInterfaceGL33Type g,
    final R2TextureUnitContextParentType uc,
    final R2FilterLightApplicatorParameters parameters,
    final AreaInclusiveUnsignedLType area)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(uc);
    NullCheck.notNull(parameters);
    NullCheck.notNull(area);

    final JCGLDepthBuffersType g_db = g.getDepthBuffers();
    final JCGLCullingType g_cu = g.getCulling();
    final JCGLColorBufferMaskingType g_cm = g.getColorBufferMasking();
    final JCGLStencilBuffersType g_st = g.getStencilBuffers();
    final JCGLShadersType g_sh = g.getShaders();
    final JCGLDrawType g_dr = g.getDraw();
    final JCGLArrayObjectsType g_ao = g.getArrayObjects();
    final JCGLTexturesType g_tx = g.getTextures();
    final JCGLViewportsType g_v = g.getViewports();

    if (g_db.depthBufferGetBits() > 0) {
      g_db.depthBufferTestDisable();
      g_db.depthBufferWriteDisable();
    }

    if (g_st.stencilBufferGetBits() > 0) {
      g_st.stencilBufferDisable();
    }

    g_cu.cullingDisable();
    g_cm.colorBufferMask(true, true, true, true);
    g_v.viewportSet(area);

    final R2TextureUnitContextType c = uc.unitContextNew();
    try {
      final R2GeometryBufferUsableType gb = parameters.getGBuffer();
      final R2LightBufferUsableType lb = parameters.getLightBuffer();

      if (this.shader_params == null) {
        this.shader_params =
          R2ShaderFilterLightApplicatorParameters.newParameters(
          gb.getAlbedoEmissiveTexture(),
          lb.getDiffuseTexture(),
          lb.getSpecularTexture());
      }

      this.shader_params.setAlbedoTexture(gb.getAlbedoEmissiveTexture());
      this.shader_params.setDiffuseTexture(lb.getDiffuseTexture());
      this.shader_params.setSpecularTexture(lb.getSpecularTexture());

      try {
        g_sh.shaderActivateProgram(this.shader.getShaderProgram());
        this.shader.setTextures(g_tx, c, this.shader_params);
        this.shader.setValues(g_sh, this.shader_params);

        g_ao.arrayObjectBind(this.quad.getArrayObject());
        g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
      } finally {
        g_ao.arrayObjectUnbind();
        g_sh.shaderDeactivateProgram();
      }

    } finally {
      c.unitContextFinish(g_tx);
    }
  }
}
