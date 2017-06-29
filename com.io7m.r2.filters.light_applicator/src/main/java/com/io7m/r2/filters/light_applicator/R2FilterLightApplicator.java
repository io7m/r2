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

package com.io7m.r2.filters.light_applicator;

import com.io7m.jcanephora.core.JCGLFramebufferBlitBuffer;
import com.io7m.jcanephora.core.JCGLFramebufferBlitFilter;
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
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.r2.core.api.R2Exception;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.filters.light_applicator.api.R2FilterLightApplicatorParameters;
import com.io7m.r2.filters.light_applicator.api.R2FilterLightApplicatorType;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferUsableType;
import com.io7m.r2.rendering.lights.api.R2LightBufferUsableType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.filter.api.R2ShaderFilterType;
import com.io7m.r2.shaders.filter.api.R2ShaderParametersFilterMutable;
import com.io7m.r2.textures.R2Texture2DUsableType;
import com.io7m.r2.textures.R2TextureDefaultsType;
import com.io7m.r2.unit_quads.R2UnitQuadUsableType;

import java.util.EnumSet;
import java.util.Set;

import static com.io7m.r2.filters.light_applicator.api.R2CopyDepth.R2_COPY_DEPTH_ENABLED;

/**
 * <p>A trivial filter that combines a geometry buffer and a light buffer into a
 * lit image.</p>
 *
 * <p>The filter takes various buffers as input and writes a lit image to the
 * currently bound framebuffer. It can optionally copy the geometry buffer's
 * depth buffer to the current depth buffer.</p>
 *
 * @see R2GeometryBufferUsableType
 * @see R2LightBufferUsableType
 */

public final class R2FilterLightApplicator implements
  R2FilterLightApplicatorType
{
  private static final Set<JCGLFramebufferBlitBuffer> BLIT_BUFFERS;

  static {
    BLIT_BUFFERS = EnumSet.of(
      JCGLFramebufferBlitBuffer.FRAMEBUFFER_BLIT_BUFFER_DEPTH);
  }

  private final R2ShaderFilterType<R2ShaderFilterLightApplicatorParameters> shader;
  private final JCGLInterfaceGL33Type g;
  private final R2UnitQuadUsableType quad;
  private final JCGLRenderState render_state;
  private final R2ShaderParametersFilterMutable<R2ShaderFilterLightApplicatorParameters> values;

  private R2FilterLightApplicator(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderFilterType<R2ShaderFilterLightApplicatorParameters> in_shader,
    final R2UnitQuadUsableType in_quad)
  {
    this.g = NullCheck.notNull(in_g, "G33");
    this.shader = NullCheck.notNull(in_shader, "Shader");
    this.quad = NullCheck.notNull(in_quad, "Quad");
    this.render_state = JCGLRenderState.builder().build();
    this.values = R2ShaderParametersFilterMutable.create();
  }

  /**
   * Create a set of filter parameters for the given values.
   *
   * @param defaults The set of default textures
   * @param gbuffer  The G-Buffer
   * @param lbuffer  The L-Buffer
   * @param viewport The output viewport
   *
   * @return A set of filter parameters
   */

  public static R2FilterLightApplicatorParameters parametersFor(
    final R2TextureDefaultsType defaults,
    final R2GeometryBufferUsableType gbuffer,
    final R2LightBufferUsableType lbuffer,
    final AreaL viewport)
  {
    NullCheck.notNull(defaults, "Defaults");
    NullCheck.notNull(gbuffer, "G-Buffer");
    NullCheck.notNull(lbuffer, "L-Buffer");
    NullCheck.notNull(viewport, "Viewport");

    final R2FilterLightApplicatorParameters.Builder pb =
      R2FilterLightApplicatorParameters.builder();
    pb.setGeometryBuffer(gbuffer);
    pb.setOutputViewport(viewport);
    pb.setCopyDepth(R2_COPY_DEPTH_ENABLED);
    lbuffer.matchLightBuffer(
      Unit.unit(),
      (tt, lbdo) -> {
        pb.setLightDiffuseTexture(lbdo.diffuseTexture());
        pb.setLightSpecularTexture(defaults.black2D());
        return Unit.unit();
      }, (tt, lbso) -> {
        pb.setLightDiffuseTexture(defaults.black2D());
        pb.setLightSpecularTexture(lbso.specularTexture());
        return Unit.unit();
      }, (tt, lb) -> {
        pb.setLightDiffuseTexture(lb.diffuseTexture());
        pb.setLightSpecularTexture(lb.specularTexture());
        return Unit.unit();
      });
    return pb.build();
  }

  /**
   * Construct a new filter.
   *
   * @param in_shader_env Shader sources
   * @param in_g          A GL interface
   * @param in_pool       An ID pool
   * @param in_quad       A unit quad
   *
   * @return A new filter
   */

  public static R2FilterLightApplicator newFilter(
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final JCGLInterfaceGL33Type in_g,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    NullCheck.notNull(in_shader_env, "Shader environment");
    NullCheck.notNull(in_g, "G33");
    NullCheck.notNull(in_pool, "ID Pool");
    NullCheck.notNull(in_quad, "Quad");

    final R2ShaderFilterType<R2ShaderFilterLightApplicatorParameters> s =
      R2ShaderFilterLightApplicator.newShader(
        in_g.shaders(), in_shader_env, in_pool);

    return new R2FilterLightApplicator(in_g, s, in_quad);
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
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType uc,
    final R2FilterLightApplicatorParameters parameters)
  {
    NullCheck.notNull(pc, "Profiling");
    NullCheck.notNull(uc, "Texture context");
    NullCheck.notNull(parameters, "Filter parameters");

    final JCGLProfilingContextType pc_base = pc.childContext("light-applicator");
    pc_base.startMeasuringIfEnabled();
    try {
      this.run(uc, parameters);
    } finally {
      pc_base.stopMeasuringIfEnabled();
    }
  }

  private void run(
    final JCGLTextureUnitContextParentType uc,
    final R2FilterLightApplicatorParameters parameters)
  {
    final R2Texture2DUsableType ldiff =
      parameters.lightDiffuseTexture();
    final R2Texture2DUsableType lspec =
      parameters.lightSpecularTexture();
    final R2GeometryBufferUsableType gb =
      parameters.geometryBuffer();

    final JCGLDrawType g_dr = this.g.drawing();
    final JCGLArrayObjectsType g_ao = this.g.arrayObjects();
    final JCGLTexturesType g_tx = this.g.textures();
    final JCGLViewportsType g_v = this.g.viewports();
    final JCGLFramebuffersType g_fb = this.g.framebuffers();

    switch (parameters.copyDepth()) {
      case R2_COPY_DEPTH_ENABLED: {
        g_fb.framebufferReadBind(gb.primaryFramebuffer());
        g_fb.framebufferBlit(
          gb.sizeAsViewport(),
          parameters.outputViewport(),
          BLIT_BUFFERS,
          JCGLFramebufferBlitFilter.FRAMEBUFFER_BLIT_FILTER_NEAREST);
        g_fb.framebufferReadUnbind();
        break;
      }
      case R2_COPY_DEPTH_DISABLED: {
        break;
      }
    }

    g_v.viewportSet(parameters.outputViewport());
    JCGLRenderStates.activate(this.g, this.render_state);

    final JCGLTextureUnitContextType c = uc.unitContextNew();
    try {
      this.values.setTextureUnitContext(c);
      this.values.setValues(
        R2ShaderFilterLightApplicatorParameters.builder()
          .setAlbedoTexture(gb.albedoEmissiveTexture())
          .setDiffuseTexture(ldiff)
          .setSpecularTexture(lspec)
          .build());

      try {
        this.shader.onActivate(this.g);
        this.shader.onReceiveFilterValues(this.g, this.values);
        this.shader.onValidate();

        g_ao.arrayObjectBind(this.quad.arrayObject());
        g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
      } finally {
        g_ao.arrayObjectUnbind();
        this.shader.onDeactivate(this.g);
      }

    } finally {
      c.unitContextFinish(g_tx);
    }
  }
}
