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
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightBufferUsableType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2UnitQuadUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersFilterMutable;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;

/**
 * A filter that applies ambient occlusion to a light buffer.
 */

public final class R2FilterOcclusionApplicator implements
  R2FilterType<R2FilterOcclusionApplicatorParameters>
{
  private final JCGLInterfaceGL33Type g;
  private final R2ShaderFilterType<R2ShaderFilterOcclusionApplicatorParameters> shader;
  private final R2UnitQuadUsableType quad;
  private final R2ShaderParametersFilterMutable<R2ShaderFilterOcclusionApplicatorParameters> values;

  private R2FilterOcclusionApplicator(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderFilterType<R2ShaderFilterOcclusionApplicatorParameters> in_shader,
    final R2UnitQuadUsableType in_quad)
  {
    this.g = NullCheck.notNull(in_g, "G33");
    this.shader = NullCheck.notNull(in_shader, "Shader");
    this.quad = NullCheck.notNull(in_quad, "Quad");
    this.values = R2ShaderParametersFilterMutable.create();
  }

  /**
   * Construct a new filter.
   *
   * @param in_shader_env Shader sources
   * @param in_textures   A texture interface
   * @param in_g          A GL interface
   * @param in_pool       An ID pool
   * @param in_quad       A unit quad
   *
   * @return A new filter
   */

  public static R2FilterOcclusionApplicator newFilter(
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2TextureDefaultsType in_textures,
    final JCGLInterfaceGL33Type in_g,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    NullCheck.notNull(in_shader_env, "Shader environment");
    NullCheck.notNull(in_textures, "Textures");
    NullCheck.notNull(in_g, "G33");
    NullCheck.notNull(in_pool, "ID Pool");
    NullCheck.notNull(in_quad, "Quad");

    final R2ShaderFilterType<R2ShaderFilterOcclusionApplicatorParameters> s =
      R2ShaderFilterOcclusionApplicator.create(
        in_g.shaders(), in_shader_env, in_pool);

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
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType uc,
    final R2FilterOcclusionApplicatorParameters parameters)
  {
    NullCheck.notNull(pc, "Profiling");
    NullCheck.notNull(uc, "Texture context");
    NullCheck.notNull(parameters, "Filter parameters");

    final JCGLProfilingContextType pc_base =
      pc.childContext("occlusion-applicator");
    pc_base.startMeasuringIfEnabled();
    try {
      this.run(uc, parameters);
    } finally {
      pc_base.stopMeasuringIfEnabled();
    }
  }

  private void run(
    final JCGLTextureUnitContextParentType uc,
    final R2FilterOcclusionApplicatorParameters parameters)
  {
    final JCGLDepthBuffersType g_db = this.g.depthBuffers();
    final JCGLBlendingType g_b = this.g.blending();
    final JCGLCullingType g_cu = this.g.culling();
    final JCGLColorBufferMaskingType g_cm = this.g.colorBufferMasking();
    final JCGLFramebuffersType g_fb = this.g.framebuffers();
    final JCGLStencilBuffersType g_st = this.g.stencilBuffers();
    final JCGLShadersType g_sh = this.g.shaders();
    final JCGLDrawType g_dr = this.g.drawing();
    final JCGLArrayObjectsType g_ao = this.g.arrayObjects();
    final JCGLTexturesType g_tx = this.g.textures();
    final JCGLViewportsType g_v = this.g.viewports();

    final R2LightBufferUsableType lb =
      parameters.outputLightBuffer();

    g_fb.framebufferDrawBind(lb.primaryFramebuffer());

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
    g_v.viewportSet(lb.sizeAsViewport());

    final JCGLTextureUnitContextType c = uc.unitContextNew();
    try {
      try {
        this.values.setTextureUnitContext(c);
        this.values.setValues(
          R2ShaderFilterOcclusionApplicatorParameters.builder()
            .setTexture(parameters.occlusionTexture())
            .setIntensity(parameters.intensity())
            .build());

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
