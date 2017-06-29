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

package com.io7m.r2.filters.fog;

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.core.api.JCGLViewportsType;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.renderstate.JCGLRenderState;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.api.R2Exception;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.filters.fog.api.R2FilterFogParameters;
import com.io7m.r2.filters.fog.api.R2FilterFogType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.filter.api.R2ShaderFilterType;
import com.io7m.r2.shaders.filter.api.R2ShaderParametersFilterMutable;
import com.io7m.r2.shaders.fog.R2ShaderFilterFogDepthLinear;
import com.io7m.r2.shaders.fog.R2ShaderFilterFogDepthQuadratic;
import com.io7m.r2.shaders.fog.R2ShaderFilterFogDepthQuadraticInverse;
import com.io7m.r2.shaders.fog.R2ShaderFilterFogParameters;
import com.io7m.r2.unit_quads.R2UnitQuadUsableType;

/**
 * <p>A depth-based fog filter.</p>
 *
 * <p>The filter takes a texture and a depth texture as input and writes
 * a filtered image to the currently bound framebuffer.</p>
 *
 * @see com.io7m.r2.filters.fog.api.R2FilterFogParameters#imageTexture()
 * @see com.io7m.r2.filters.fog.api.R2FilterFogParameters#imageDepthTexture()
 */

public final class R2FilterFogDepth implements R2FilterFogType
{
  private final R2ShaderFilterFogDepthLinear shader_linear;
  private final R2ShaderFilterFogDepthQuadratic shader_quad;
  private final R2ShaderFilterFogDepthQuadraticInverse shader_quad_inv;
  private final JCGLInterfaceGL33Type g;
  private final JCGLRenderState render_state;
  private final R2UnitQuadUsableType quad;
  private final R2ShaderParametersFilterMutable<R2ShaderFilterFogParameters> values;

  private R2FilterFogDepth(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    this.g = NullCheck.notNull(in_g, "G33");
    this.quad = NullCheck.notNull(in_quad, "Quad");

    this.shader_linear = R2ShaderFilterFogDepthLinear.create(
      this.g.shaders(), in_shader_env, in_pool);
    this.shader_quad = R2ShaderFilterFogDepthQuadratic.create(
      this.g.shaders(), in_shader_env, in_pool);
    this.shader_quad_inv = R2ShaderFilterFogDepthQuadraticInverse.create(
      this.g.shaders(), in_shader_env, in_pool);

    this.render_state = JCGLRenderState.builder().build();
    this.values = R2ShaderParametersFilterMutable.create();
  }

  /**
   * @param in_g          An OpenGL interface
   * @param in_shader_env Shader sources
   * @param in_pool       The ID pool
   * @param in_quad       A unit quad
   *
   * @return A new filter
   */

  public static R2FilterFogDepth newFilter(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    return new R2FilterFogDepth(in_g, in_shader_env, in_pool, in_quad);
  }

  @Override
  public void delete(
    final JCGLInterfaceGL33Type g33)
    throws R2Exception
  {
    if (!this.isDeleted()) {
      this.shader_linear.delete(g33);
      this.shader_quad.delete(g33);
      this.shader_quad_inv.delete(g33);
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.shader_linear.isDeleted();
  }

  @Override
  public void runFilter(
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType uc,
    final R2FilterFogParameters parameters)
  {
    NullCheck.notNull(pc, "Profiling");
    NullCheck.notNull(uc, "Texture context");
    NullCheck.notNull(parameters, "Filter parameters");

    Preconditions.checkPrecondition(
      !this.isDeleted(), "Filter must not be deleted");

    final JCGLProfilingContextType pc_base = pc.childContext("fog-depth");
    pc_base.startMeasuringIfEnabled();
    try {
      this.run(uc, parameters);
    } finally {
      pc_base.stopMeasuringIfEnabled();
    }
  }

  private void run(
    final JCGLTextureUnitContextParentType uc,
    final R2FilterFogParameters parameters)
  {
    final JCGLArrayObjectsType g_ao = this.g.arrayObjects();
    final JCGLFramebuffersType g_fb = this.g.framebuffers();
    final JCGLTexturesType g_tex = this.g.textures();
    final JCGLDrawType g_dr = this.g.drawing();
    final JCGLViewportsType g_v = this.g.viewports();

    R2ShaderFilterType<R2ShaderFilterFogParameters> s = null;
    switch (parameters.progression()) {
      case FOG_LINEAR: {
        s = this.shader_linear;
        break;
      }
      case FOG_QUADRATIC: {
        s = this.shader_quad;
        break;
      }
      case FOG_QUADRATIC_INVERSE: {
        s = this.shader_quad_inv;
        break;
      }
    }
    NullCheck.notNull(s, "Shader");

    try {
      final JCGLTextureUnitContextType tc = uc.unitContextNew();

      try {
        JCGLRenderStates.activate(this.g, this.render_state);
        g_v.viewportSet(parameters.viewport());

        try {
          g_ao.arrayObjectBind(this.quad.arrayObject());

          this.values.setTextureUnitContext(tc);
          this.values.setValues(
            R2ShaderFilterFogParameters.builder()
              .setFogColor(parameters.fogColor())
              .setFogFarPositiveZ(parameters.fogFarPositiveZ())
              .setFogNearPositiveZ(parameters.fogNearPositiveZ())
              .setImageDepthTexture(parameters.imageDepthTexture())
              .setImageTexture(parameters.imageTexture())
              .setObserverValues(parameters.observerValues())
              .build());

          s.onActivate(this.g);
          try {
            s.onReceiveFilterValues(this.g, this.values);
            s.onValidate();
            g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
          } finally {
            s.onDeactivate(this.g);
          }
        } finally {
          g_ao.arrayObjectUnbind();
        }
      } finally {
        tc.unitContextFinish(g_tex);
      }

    } finally {
      g_fb.framebufferDrawUnbind();
    }
  }
}
