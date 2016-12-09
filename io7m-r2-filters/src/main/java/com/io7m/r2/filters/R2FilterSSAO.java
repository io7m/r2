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
import com.io7m.jcanephora.renderstate.JCGLRenderStateMutable;
import com.io7m.jcanephora.renderstate.JCGLRenderStates;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.VectorI4F;
import com.io7m.r2.core.R2AmbientOcclusionBufferUsableType;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2UnitQuadUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import org.valid4j.Assertive;

import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * A filter that calculates an ambient occlusion term from a given geometry
 * buffer.
 */

public final class R2FilterSSAO implements
  R2FilterType<R2FilterSSAOParametersType>
{
  private final R2ShaderFilterType<R2ShaderSSAOParametersType> shader;
  private final R2UnitQuadUsableType quad;
  private final JCGLInterfaceGL33Type g;
  private final JCGLClearSpecification clear;
  private final R2ShaderSSAOParametersMutable shader_params;
  private final JCGLRenderStateMutable render_state;
  private final R2TextureDefaultsType texture_defaults;

  private R2FilterSSAO(
    final JCGLInterfaceGL33Type in_g,
    final R2TextureDefaultsType in_texture_defaults,
    final R2ShaderFilterType<R2ShaderSSAOParametersType> in_shader,
    final R2UnitQuadUsableType in_quad)
  {
    this.g = NullCheck.notNull(in_g);
    this.shader = NullCheck.notNull(in_shader);
    this.quad = NullCheck.notNull(in_quad);
    this.texture_defaults = NullCheck.notNull(in_texture_defaults);

    final JCGLClearSpecification.Builder cb = JCGLClearSpecification.builder();
    cb.setColorBufferClear(new VectorI4F(0.0f, 0.0f, 0.0f, 0.0f));
    cb.setDepthBufferClear(OptionalDouble.empty());
    cb.setStencilBufferClear(OptionalInt.empty());
    this.clear = cb.build();

    this.shader_params = R2ShaderSSAOParametersMutable.create();
    this.render_state = JCGLRenderStateMutable.create();
  }

  /**
   * Construct a new filter.
   *
   * @param in_shader_env       Shader sources
   * @param in_g                A GL interface
   * @param in_texture_defaults A set of default textures
   * @param in_tc               A texture unit allocator
   * @param in_pool             An ID pool
   * @param in_quad             A unit quad
   *
   * @return A new filter
   */

  public static R2FilterType<R2FilterSSAOParametersType> newFilter(
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final JCGLInterfaceGL33Type in_g,
    final R2TextureDefaultsType in_texture_defaults,
    final JCGLTextureUnitContextParentType in_tc,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    NullCheck.notNull(in_shader_env);
    NullCheck.notNull(in_g);
    NullCheck.notNull(in_texture_defaults);
    NullCheck.notNull(in_pool);
    NullCheck.notNull(in_quad);
    NullCheck.notNull(in_tc);

    final R2ShaderFilterType<R2ShaderSSAOParametersType> s =
      R2ShaderSSAO.newShader(in_g.getShaders(), in_shader_env, in_pool);

    return new R2FilterSSAO(in_g, in_texture_defaults, s, in_quad);
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
  public void runFilter(
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType uc,
    final R2FilterSSAOParametersType parameters)
  {
    NullCheck.notNull(uc);
    NullCheck.notNull(parameters);

    final JCGLProfilingContextType pc_base = pc.getChildContext("ssao");
    pc_base.startMeasuringIfEnabled();
    try {
      this.run(uc, parameters);
    } finally {
      pc_base.stopMeasuringIfEnabled();
    }
  }

  private void run(
    final JCGLTextureUnitContextParentType uc,
    final R2FilterSSAOParametersType parameters)
  {
    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();
    final JCGLShadersType g_sh = this.g.getShaders();
    final JCGLDrawType g_dr = this.g.getDraw();
    final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
    final JCGLTexturesType g_tx = this.g.getTextures();
    final JCGLViewportsType g_v = this.g.getViewports();
    final JCGLClearType g_cl = this.g.getClear();

    final R2AmbientOcclusionBufferUsableType destination =
      parameters.getOutputBuffer();

    g_fb.framebufferDrawBind(destination.getPrimaryFramebuffer());

    JCGLRenderStates.activate(this.g, this.render_state);
    g_v.viewportSet(destination.getArea());
    g_cl.clear(this.clear);

    final JCGLTextureUnitContextType c = uc.unitContextNew();
    try {
      this.shader_params.clear();
      this.shader_params.setExponent(parameters.getExponent());
      this.shader_params.setGeometryBuffer(parameters.getGeometryBuffer());
      this.shader_params.setKernel(parameters.getKernel());
      this.shader_params.setNoiseTexture(parameters.getNoiseTexture());
      this.shader_params.setSampleRadius(parameters.getSampleRadius());
      this.shader_params.setViewport(destination.getArea());
      this.shader_params.setViewMatrices(parameters.getSceneObserverValues());
      Assertive.ensure(this.shader_params.isInitialized());

      try {
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
  }
}
