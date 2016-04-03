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
import com.io7m.jcanephora.renderstate.JCGLRenderStateMutable;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2TextureUnitContextType;
import com.io7m.r2.core.R2TransformIdentity;
import com.io7m.r2.core.R2UnitQuadUsableType;
import com.io7m.r2.core.profiling.R2ProfilingContextType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;
import org.valid4j.Assertive;

import java.util.EnumSet;
import java.util.Set;

/**
 * A filter that recovers the eye-space Z position of the pixels in the given
 * geometry buffer.
 */

public final class R2FilterDebugEyeZ implements
  R2FilterType<R2FilterDebugEyeZParametersType>
{
  private static final Set<JCGLFramebufferBlitBuffer> DEPTH_STENCIL;

  static {
    DEPTH_STENCIL = EnumSet.of(
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_DEPTH,
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_STENCIL);
  }

  private final R2ShaderFilterType<R2FilterDebugEyeZParametersType> shader;
  private final JCGLInterfaceGL33Type g;
  private final JCGLRenderStateMutable render_state;
  private final R2UnitQuadUsableType quad;

  private R2FilterDebugEyeZ(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    this.g = NullCheck.notNull(in_g);
    this.shader = R2ShaderFilterDebugEyeZ.newShader(
      this.g.getShaders(), in_sources, in_pool);
    this.render_state =
      JCGLRenderStateMutable.create();
    this.quad = NullCheck.notNull(in_quad);
  }

  /**
   * @param in_g       An OpenGL interface
   * @param in_sources Shader sources
   * @param in_pool    The ID pool
   * @param in_quad    A unit quad
   *
   * @return A new renderer
   */

  public static R2FilterType<R2FilterDebugEyeZParametersType> newFilter(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    return new R2FilterDebugEyeZ(in_g, in_sources, in_pool, in_quad);
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
    final R2ProfilingContextType pc,
    final R2TextureUnitContextParentType uc,
    final R2FilterDebugEyeZParametersType parameters)
  {
    NullCheck.notNull(uc);
    NullCheck.notNull(parameters);

    Assertive.require(!this.isDeleted(), "Filter not deleted");

    final R2ProfilingContextType pc_base = pc.getChildContext("debug-eye-z");
    pc_base.startMeasuringIfEnabled();
    try {
      this.run(uc, parameters);
    } finally {
      pc_base.stopMeasuringIfEnabled();
    }
  }

  private void run(
    final R2TextureUnitContextParentType uc,
    final R2FilterDebugEyeZParametersType parameters)
  {
    final R2GeometryBufferUsableType gbuffer =
      parameters.getGeometryBuffer();
    final R2EyeZBufferUsableType zbuffer =
      parameters.getEyeZBuffer();

    final JCGLFramebufferUsableType gb_fb = gbuffer.getPrimaryFramebuffer();
    final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();
    final JCGLTexturesType g_tex = this.g.getTextures();
    final JCGLShadersType g_sh = this.g.getShaders();
    final JCGLDrawType g_dr = this.g.getDraw();
    final JCGLViewportsType g_v = this.g.getViewports();

    try {
      g_fb.framebufferDrawBind(zbuffer.getFramebuffer());

      /**
       * Copy the contents of the depth/stencil attachment of the G-Buffer to
       * the current depth/stencil buffer.
       */

      g_fb.framebufferReadBind(gb_fb);
      g_fb.framebufferBlit(
        gbuffer.getArea(),
        zbuffer.getArea(),
        R2FilterDebugEyeZ.DEPTH_STENCIL,
        JCGLFramebufferBlitFilter.FRAMEBUFFER_BLIT_FILTER_NEAREST);
      g_fb.framebufferReadUnbind();

      /**
       * Bind G-buffer.
       */

      final R2TextureUnitContextType tc = uc.unitContextNew();

      try {
        JCGLRenderStates.activate(this.g, this.render_state);
        g_v.viewportSet(zbuffer.getArea());

        try {
          g_ao.arrayObjectBind(this.quad.getArrayObject());

          final R2MatricesObserverType m = parameters.getObserverValues();

          this.shader.onActivate(g_sh);
          this.shader.onReceiveFilterValues(g_tex, g_sh, tc, parameters);
          this.shader.onValidate();

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
          this.shader.onDeactivate(g_sh);
        }

      } finally {
        tc.unitContextFinish(g_tex);
      }

    } finally {
      g_fb.framebufferDrawUnbind();
    }
  }
}
