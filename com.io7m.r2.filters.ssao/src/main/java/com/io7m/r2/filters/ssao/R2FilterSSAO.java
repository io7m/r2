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

package com.io7m.r2.filters.ssao;

import com.io7m.jcanephora.core.JCGLClearSpecification;
import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLClearType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.core.api.JCGLViewportsType;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.renderstate.JCGLRenderState;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jnull.NullCheck;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors4D;
import com.io7m.r2.core.api.R2Exception;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.filters.ssao.api.R2AmbientOcclusionBufferUsableType;
import com.io7m.r2.filters.ssao.api.R2FilterSSAOParameters;
import com.io7m.r2.filters.ssao.api.R2FilterSSAOType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.filter.api.R2ShaderFilterType;
import com.io7m.r2.shaders.filter.api.R2ShaderParametersFilterMutable;
import com.io7m.r2.unit_quads.R2UnitQuadUsableType;

import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * A filter that calculates an ambient occlusion term from a given geometry
 * buffer.
 */

public final class R2FilterSSAO implements R2FilterSSAOType
{
  private final R2ShaderFilterType<R2ShaderSSAOParameters> shader;
  private final R2UnitQuadUsableType quad;
  private final JCGLInterfaceGL33Type g;
  private final JCGLClearSpecification clear;
  private final JCGLRenderState render_state;
  private final R2ShaderParametersFilterMutable<R2ShaderSSAOParameters> values;

  private R2FilterSSAO(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderFilterType<R2ShaderSSAOParameters> in_shader,
    final R2UnitQuadUsableType in_quad)
  {
    this.g = NullCheck.notNull(in_g, "G33");
    this.shader = NullCheck.notNull(in_shader, "Shader");
    this.quad = NullCheck.notNull(in_quad, "Quad");

    final JCGLClearSpecification.Builder cb = JCGLClearSpecification.builder();
    cb.setColorBufferClear(Vectors4D.zero());
    cb.setDepthBufferClear(OptionalDouble.empty());
    cb.setStencilBufferClear(OptionalInt.empty());
    this.clear = cb.build();

    this.render_state = JCGLRenderState.builder().build();
    this.values = R2ShaderParametersFilterMutable.create();
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

  public static R2FilterSSAO newFilter(
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final JCGLInterfaceGL33Type in_g,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    NullCheck.notNull(in_shader_env, "Shader environment");
    NullCheck.notNull(in_g, "G33");
    NullCheck.notNull(in_pool, "ID Pool");
    NullCheck.notNull(in_quad, "Quad");

    final R2ShaderFilterType<R2ShaderSSAOParameters> s =
      R2ShaderSSAO.create(in_g.shaders(), in_shader_env, in_pool);

    return new R2FilterSSAO(in_g, s, in_quad);
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type gx)
    throws R2Exception
  {
    NullCheck.notNull(gx, "G33");

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
    final R2FilterSSAOParameters parameters)
  {
    NullCheck.notNull(pc, "Profiling");
    NullCheck.notNull(uc, "Texture context");
    NullCheck.notNull(parameters, "Filter parameters");

    final JCGLProfilingContextType pc_base = pc.childContext("ssao");
    pc_base.startMeasuringIfEnabled();
    try {
      this.run(uc, parameters);
    } finally {
      pc_base.stopMeasuringIfEnabled();
    }
  }

  private void run(
    final JCGLTextureUnitContextParentType uc,
    final R2FilterSSAOParameters parameters)
  {
    final JCGLFramebuffersType g_fb = this.g.framebuffers();
    final JCGLShadersType g_sh = this.g.shaders();
    final JCGLDrawType g_dr = this.g.drawing();
    final JCGLArrayObjectsType g_ao = this.g.arrayObjects();
    final JCGLTexturesType g_tx = this.g.textures();
    final JCGLViewportsType g_v = this.g.viewports();
    final JCGLClearType g_cl = this.g.clearing();

    final R2AmbientOcclusionBufferUsableType destination =
      parameters.outputBuffer();
    final AreaL viewport =
      parameters.outputBuffer().sizeAsViewport();

    g_fb.framebufferDrawBind(destination.primaryFramebuffer());

    JCGLRenderStates.activate(this.g, this.render_state);
    g_v.viewportSet(viewport);
    g_cl.clear(this.clear);

    final JCGLTextureUnitContextType c = uc.unitContextNew();
    try {
      this.values.setTextureUnitContext(c);
      this.values.setValues(
        R2ShaderSSAOParameters.builder()
          .setExponent(parameters.exponent())
          .setGeometryBuffer(parameters.geometryBuffer())
          .setKernel(parameters.kernel())
          .setNoiseTexture(parameters.noiseTexture())
          .setSampleRadius(parameters.sampleRadius())
          .setViewport(viewport)
          .setViewMatrices(parameters.sceneObserverValues())
          .build());

      try {
        g_sh.shaderActivateProgram(this.shader.shaderProgram());

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
