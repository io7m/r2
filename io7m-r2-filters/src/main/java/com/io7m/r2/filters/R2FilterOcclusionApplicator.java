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

package com.io7m.r2.filters;

import com.io7m.jcanephora.core.JCGLBlendEquation;
import com.io7m.jcanephora.core.JCGLBlendFunction;
import com.io7m.jcanephora.core.JCGLPrimitives;
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
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightBufferUsableType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2TextureUnitContextType;
import com.io7m.r2.core.R2UnitQuadUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;

/**
 * A filter that applies ambient occlusion to a light buffer.
 */

public final class R2FilterOcclusionApplicator implements
  R2FilterType<R2FilterOcclusionApplicatorParametersType>
{
  private final JCGLInterfaceGL33Type
    g;
  private final
  R2ShaderFilterType<R2ShaderFilterOcclusionApplicatorParametersType>
    shader;
  private final R2UnitQuadUsableType
    quad;
  private final R2ShaderFilterOcclusionApplicatorParametersMutable
    shader_params;

  private R2FilterOcclusionApplicator(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderFilterType<R2ShaderFilterOcclusionApplicatorParametersType>
      in_shader,
    final R2UnitQuadUsableType in_quad)
  {
    this.g = NullCheck.notNull(in_g);
    this.shader = NullCheck.notNull(in_shader);
    this.quad = NullCheck.notNull(in_quad);
    this.shader_params =
      R2ShaderFilterOcclusionApplicatorParametersMutable.create();
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

  public static R2FilterType<R2FilterOcclusionApplicatorParametersType>
  newFilter(
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

    final R2ShaderFilterType<R2ShaderFilterOcclusionApplicatorParametersType>
      s =
      R2ShaderFilterOcclusionApplicator.newShader(
        in_g.getShaders(),
        in_sources,
        in_pool);

    return new R2FilterOcclusionApplicator(in_g, s, in_quad);
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type gx)
    throws R2Exception
  {
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
  public void runFilter(
    final R2TextureUnitContextParentType uc,
    final R2FilterOcclusionApplicatorParametersType parameters)
  {
    NullCheck.notNull(uc);
    NullCheck.notNull(parameters);

    final JCGLDepthBuffersType g_db = this.g.getDepthBuffers();
    final JCGLBlendingType g_b = this.g.getBlending();
    final JCGLCullingType g_cu = this.g.getCulling();
    final JCGLColorBufferMaskingType g_cm = this.g.getColorBufferMasking();
    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();
    final JCGLStencilBuffersType g_st = this.g.getStencilBuffers();
    final JCGLShadersType g_sh = this.g.getShaders();
    final JCGLDrawType g_dr = this.g.getDraw();
    final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
    final JCGLTexturesType g_tx = this.g.getTextures();
    final JCGLViewportsType g_v = this.g.getViewports();

    final R2LightBufferUsableType lb =
      parameters.getLightBuffer();
    final R2Texture2DUsableType tex =
      parameters.getOcclusionTexture();

    try {
      g_fb.framebufferDrawBind(lb.getPrimaryFramebuffer());

      if (g_db.depthBufferGetBits() > 0) {
        g_db.depthBufferTestDisable();
        g_db.depthBufferWriteDisable();
      }

      if (g_st.stencilBufferGetBits() > 0) {
        g_st.stencilBufferDisable();
      }

      g_b.blendingEnableSeparateWithEquationSeparate(
        JCGLBlendFunction.BLEND_ONE,
        JCGLBlendFunction.BLEND_ONE,
        JCGLBlendFunction.BLEND_ONE,
        JCGLBlendFunction.BLEND_ONE,
        JCGLBlendEquation.BLEND_EQUATION_REVERSE_SUBTRACT,
        JCGLBlendEquation.BLEND_EQUATION_ADD);

      g_cu.cullingDisable();
      g_cm.colorBufferMask(true, true, true, true);
      g_v.viewportSet(lb.getArea());

      final R2TextureUnitContextType c = uc.unitContextNew();
      try {
        try {
          this.shader_params.setTexture(
            parameters.getOcclusionTexture());
          this.shader_params.setIntensity(
            parameters.getIntensity());

          g_sh.shaderActivateProgram(this.shader.getShaderProgram());
          this.shader.onActivate(g_sh);
          this.shader.onReceiveFilterValues(g_tx, g_sh, c, this.shader_params);
          this.shader.onValidate();
          g_ao.arrayObjectBind(this.quad.getArrayObject());
          g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
        } finally {
          g_ao.arrayObjectUnbind();
          this.shader.onDeactivate(g_sh);
        }
      } finally {
        c.unitContextFinish(g_tx);
      }

    } finally {
      g_fb.framebufferDrawUnbind();
    }
  }
}
