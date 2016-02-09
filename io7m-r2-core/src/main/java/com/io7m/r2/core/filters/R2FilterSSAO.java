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
import com.io7m.jcanephora.core.api.JCGLViewportsType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.r2.core.R2AmbientOcclusionBufferUsableType;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2ShaderSourcesType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2TextureUnitContextType;
import com.io7m.r2.core.R2UnitQuadUsableType;

/**
 * The default implementation of the {@link R2FilterSSAOType} interface.
 */

public final class R2FilterSSAO implements R2FilterSSAOType
{
  private final     R2ShaderSSAO           shader;
  private final     R2UnitQuadUsableType   quad;
  private final     JCGLInterfaceGL33Type  g;
  private @Nullable R2ShaderSSAOParameters shader_params;

  private R2FilterSSAO(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderSSAO in_shader,
    final R2UnitQuadUsableType in_quad)
  {
    this.g = NullCheck.notNull(in_g);
    this.shader = NullCheck.notNull(in_shader);
    this.quad = NullCheck.notNull(in_quad);
  }

  /**
   * Construct a new filter.
   *
   * @param in_sources Shader sources
   * @param in_g       A GL interface
   * @param in_tc      A texture unit allocator
   * @param in_pool    An ID pool
   * @param in_quad    A unit quad
   *
   * @return A new filter
   */

  public static R2FilterSSAOType newFilter(
    final R2ShaderSourcesType in_sources,
    final JCGLInterfaceGL33Type in_g,
    final R2TextureUnitContextParentType in_tc,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    NullCheck.notNull(in_sources);
    NullCheck.notNull(in_g);
    NullCheck.notNull(in_pool);
    NullCheck.notNull(in_quad);
    NullCheck.notNull(in_tc);

    final R2ShaderSSAO s =
      R2ShaderSSAO.newShader(
        in_g.getShaders(),
        in_sources,
        in_pool);

    return new R2FilterSSAO(in_g, s, in_quad);
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type gx)
    throws R2Exception
  {
    NullCheck.notNull(gx);

    if (!this.isDeleted()) {
      this.shader.delete(gx);
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.shader.isDeleted();
  }

  @Override
  public Class<R2FilterSSAOParameters> getParametersType()
  {
    return R2FilterSSAOParameters.class;
  }

  @Override
  public Class<R2AmbientOcclusionBufferUsableType> getRenderTargetType()
  {
    return R2AmbientOcclusionBufferUsableType.class;
  }

  @Override
  public void runFilter(
    final R2TextureUnitContextParentType uc,
    final R2MatricesObserverValuesType m,
    final R2FilterSSAOParameters parameters,
    final R2AmbientOcclusionBufferUsableType target)
  {
    NullCheck.notNull(uc);
    NullCheck.notNull(m);
    NullCheck.notNull(parameters);
    NullCheck.notNull(target);

    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();

    try {
      g_fb.framebufferDrawBind(target.getFramebuffer());
      this.runFilterWithBoundBuffer(uc, m, parameters, target.getArea());
    } finally {
      g_fb.framebufferDrawUnbind();
    }
  }

  @Override
  public void runFilterWithBoundBuffer(
    final R2TextureUnitContextParentType uc,
    final R2MatricesObserverValuesType m,
    final R2FilterSSAOParameters parameters,
    final AreaInclusiveUnsignedLType area)
  {
    NullCheck.notNull(uc);
    NullCheck.notNull(m);
    NullCheck.notNull(parameters);
    NullCheck.notNull(area);

    final JCGLDepthBuffersType g_db = this.g.getDepthBuffers();
    final JCGLCullingType g_cu = this.g.getCulling();
    final JCGLColorBufferMaskingType g_cm = this.g.getColorBufferMasking();
    final JCGLStencilBuffersType g_st = this.g.getStencilBuffers();
    final JCGLShadersType g_sh = this.g.getShaders();
    final JCGLDrawType g_dr = this.g.getDraw();
    final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
    final JCGLTexturesType g_tx = this.g.getTextures();
    final JCGLViewportsType g_v = this.g.getViewports();

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
      if (this.shader_params == null) {
        this.shader_params = R2ShaderSSAOParameters.newParameters(
          parameters.getKernel(),
          parameters.getNoiseTexture(),
          area);
      }

      final R2ShaderSSAOParameters sp = NullCheck.notNull(this.shader_params);
      sp.setKernel(parameters.getKernel());
      sp.setNoiseTexture(parameters.getNoiseTexture());
      sp.setViewport(area);

      final R2GeometryBufferUsableType gb = parameters.getGBuffer();

      final JCGLTextureUnitType ua =
        c.unitContextBindTexture2D(g_tx, gb.getAlbedoEmissiveTexture());
      final JCGLTextureUnitType us =
        c.unitContextBindTexture2D(g_tx, gb.getSpecularTexture());
      final JCGLTextureUnitType ud =
        c.unitContextBindTexture2D(g_tx, gb.getDepthTexture());
      final JCGLTextureUnitType un =
        c.unitContextBindTexture2D(g_tx, gb.getNormalTexture());

      try {
        g_sh.shaderActivateProgram(this.shader.getShaderProgram());
        this.shader.setGBuffer(g_sh, gb, ua, us, ud, un);
        this.shader.setTextures(g_tx, c, sp);
        this.shader.setViewDependentValues(g_sh, m);
        this.shader.setValues(g_sh, sp);

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
