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

import com.io7m.jcanephora.core.JCGLFramebufferBlitBuffer;
import com.io7m.jcanephora.core.JCGLFramebufferBlitFilter;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.core.api.JCGLViewportsType;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.renderstate.JCGLRenderStateMutable;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverType;
import com.io7m.r2.core.R2TransformIdentity;
import com.io7m.r2.core.R2UnitQuadUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import org.valid4j.Assertive;

import java.util.EnumSet;
import java.util.Set;

/**
 * A filter that recovers the eye-space position of the pixels in the given
 * geometry buffer.
 */

public final class R2FilterDebugEyePosition implements
  R2FilterType<R2FilterDebugEyePositionParametersType>
{
  private static final Set<JCGLFramebufferBlitBuffer> DEPTH_STENCIL;

  static {
    DEPTH_STENCIL = EnumSet.of(
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_DEPTH,
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_STENCIL);
  }

  private final R2ShaderFilterDebugEyePosition shader;
  private final JCGLInterfaceGL33Type g;
  private final JCGLRenderStateMutable render_state;
  private final R2UnitQuadUsableType quad;

  private R2FilterDebugEyePosition(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    this.g = NullCheck.notNull(in_g);
    this.shader = R2ShaderFilterDebugEyePosition.newShader(
      this.g.getShaders(), in_shader_env, in_pool);
    this.render_state =
      JCGLRenderStateMutable.create();
    this.quad = NullCheck.notNull(in_quad);
  }

  /**
   * @param in_g          An OpenGL interface
   * @param in_shader_env Shader sources
   * @param in_pool       The ID pool
   * @param in_quad       A unit quad
   *
   * @return A new renderer
   */

  public static R2FilterType<R2FilterDebugEyePositionParametersType>
  newRenderer(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    return new R2FilterDebugEyePosition(in_g, in_shader_env, in_pool, in_quad);
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
  public void runFilter(
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType uc,
    final R2FilterDebugEyePositionParametersType parameters)
  {
    NullCheck.notNull(uc);
    NullCheck.notNull(parameters);

    Assertive.require(!this.isDeleted(), "Renderer not deleted");

    final JCGLProfilingContextType pc_base =
      pc.getChildContext("debug-eye-position");
    pc_base.startMeasuringIfEnabled();
    try {
      this.run(uc, parameters);
    } finally {
      pc_base.stopMeasuringIfEnabled();
    }
  }

  private void run(
    final JCGLTextureUnitContextParentType uc,
    final R2FilterDebugEyePositionParametersType parameters)
  {
    final R2GeometryBufferUsableType gbuffer =
      parameters.geometryBuffer();
    final R2EyePositionBufferUsableType ebuffer =
      parameters.eyePositionBuffer();

    final JCGLFramebufferUsableType gb_fb =
      gbuffer.primaryFramebuffer();

    final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();
    final JCGLTexturesType g_tex = this.g.getTextures();
    final JCGLShadersType g_sh = this.g.getShaders();
    final JCGLDrawType g_dr = this.g.getDraw();
    final JCGLViewportsType g_v = this.g.getViewports();

    try {
      g_fb.framebufferDrawBind(ebuffer.framebuffer());

      /**
       * Copy the contents of the depth/stencil attachment of the G-Buffer to
       * the current depth/stencil buffer.
       */

      g_fb.framebufferReadBind(gb_fb);
      g_fb.framebufferBlit(
        gbuffer.area(),
        ebuffer.area(),
        DEPTH_STENCIL,
        JCGLFramebufferBlitFilter.FRAMEBUFFER_BLIT_FILTER_NEAREST);
      g_fb.framebufferReadUnbind();

      final JCGLTextureUnitContextType tc = uc.unitContextNew();

      try {
        JCGLRenderStates.activate(this.g, this.render_state);
        g_v.viewportSet(ebuffer.area());

        try {
          g_ao.arrayObjectBind(this.quad.arrayObject());

          this.shader.onActivate(g_sh);
          this.shader.onReceiveFilterValues(g_tex, g_sh, tc, parameters);
          this.shader.onValidate();

          final R2MatricesObserverType m = parameters.observerValues();

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

    } finally {
      g_fb.framebufferDrawUnbind();
    }
  }
}
