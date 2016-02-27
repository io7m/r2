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
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLBlendingType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.core.api.JCGLViewportsType;
import com.io7m.jcanephora.renderstate.JCGLRenderStateMutable;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2ShaderSourcesType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2TextureUnitContextType;
import com.io7m.r2.core.R2UnitQuadUsableType;
import com.io7m.r2.core.shaders.R2ShaderTextureShow;
import com.io7m.r2.core.shaders.R2ShaderTextureShowParametersMutable;

import java.util.Optional;

/**
 * A compositor filter that combines textures.
 */

public final class R2FilterCompositor implements
  R2FilterType<R2FilterCompositorParametersType>
{
  private final JCGLInterfaceGL33Type                g;
  private final R2ShaderTextureShow                  shader;
  private final R2UnitQuadUsableType                 quad;
  private final R2ShaderTextureShowParametersMutable shader_params;
  private final JCGLRenderStateMutable render_state;

  private R2FilterCompositor(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderTextureShow in_shader,
    final R2UnitQuadUsableType in_quad)
  {
    this.g = NullCheck.notNull(in_g);
    this.shader = NullCheck.notNull(in_shader);
    this.quad = NullCheck.notNull(in_quad);
    this.shader_params = R2ShaderTextureShowParametersMutable.create();
    this.render_state = JCGLRenderStateMutable.create();
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

  public static R2FilterType<R2FilterCompositorParametersType>
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

    final R2ShaderTextureShow s = R2ShaderTextureShow.newShader(
      in_g.getShaders(),
      in_sources,
      in_pool);

    return new R2FilterCompositor(in_g, s, in_quad);
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
    final R2FilterCompositorParametersType parameters)
  {
    NullCheck.notNull(uc);
    NullCheck.notNull(parameters);

    final JCGLBlendingType g_b = this.g.getBlending();
    final JCGLShadersType g_sh = this.g.getShaders();
    final JCGLDrawType g_dr = this.g.getDraw();
    final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
    final JCGLTexturesType g_tx = this.g.getTextures();
    final JCGLViewportsType g_v = this.g.getViewports();

    JCGLRenderStates.activate(this.g, this.render_state);

    try {
      g_sh.shaderActivateProgram(this.shader.getShaderProgram());
      g_ao.arrayObjectBind(this.quad.getArrayObject());

      for (final R2FilterCompositorItemType i : parameters.getItems()) {
        final Optional<R2FilterCompositorBlendingType> blend_opt =
          i.getBlending();
        if (blend_opt.isPresent()) {
          final R2FilterCompositorBlendingType blend = blend_opt.get();
          g_b.blendingEnableSeparateWithEquationSeparate(
            blend.getBlendFunctionSourceRGB(),
            blend.getBlendFunctionSourceAlpha(),
            blend.getBlendFunctionTargetRGB(),
            blend.getBlendFunctionTargetAlpha(),
            blend.getBlendEquationRGB(),
            blend.getBlendEquationAlpha());
        } else {
          g_b.blendingDisable();
        }

        final R2TextureUnitContextType c = uc.unitContextNew();
        try {
          g_v.viewportSet(i.getOutputViewport());

          this.shader_params.setIntensity(i.getIntensity());
          this.shader_params.setTexture(i.getTexture());
          this.shader.setTextures(g_tx, c, this.shader_params);
          this.shader.setValues(g_sh, this.shader_params);

          g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
        } finally {
          c.unitContextFinish(g_tx);
        }
      }

    } finally {
      g_ao.arrayObjectUnbind();
      g_sh.shaderDeactivateProgram();
    }
  }
}
