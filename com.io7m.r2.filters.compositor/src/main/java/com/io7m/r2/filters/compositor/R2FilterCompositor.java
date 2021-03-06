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

package com.io7m.r2.filters.compositor;

import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLBlendingType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
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
import com.io7m.r2.filters.compositor.api.R2FilterCompositorBlending;
import com.io7m.r2.filters.compositor.api.R2FilterCompositorItemType;
import com.io7m.r2.filters.compositor.api.R2FilterCompositorParameters;
import com.io7m.r2.filters.compositor.api.R2FilterCompositorType;
import com.io7m.r2.shaders.filter.api.R2ShaderFilterType;
import com.io7m.r2.shaders.filter.api.R2ShaderParametersFilterMutable;
import com.io7m.r2.textures.R2TextureDefaultsType;
import com.io7m.r2.unit_quads.R2UnitQuadUsableType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;

import java.util.Optional;

/**
 * A compositor filter that combines textures.
 */

public final class R2FilterCompositor implements R2FilterCompositorType
{
  private final R2ShaderFilterType<R2ShaderFilterTextureShowParameters> shader;
  private final JCGLInterfaceGL33Type g;
  private final R2UnitQuadUsableType quad;
  private final JCGLRenderState render_state;
  private final R2ShaderParametersFilterMutable<R2ShaderFilterTextureShowParameters> values;

  private R2FilterCompositor(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderFilterType<R2ShaderFilterTextureShowParameters> in_shader,
    final R2UnitQuadUsableType in_quad)
  {
    this.g = NullCheck.notNull(in_g, "G33");
    this.shader = NullCheck.notNull(in_shader, "Shader");
    this.quad = NullCheck.notNull(in_quad, "Quad");
    this.render_state = JCGLRenderState.builder().build();
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

  public static R2FilterCompositor newFilter(
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

    final R2ShaderFilterType<R2ShaderFilterTextureShowParameters> s =
      R2ShaderFilterTextureShow.create(
        in_g.shaders(), in_shader_env, in_pool);

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
    final JCGLProfilingContextType pc,
    final JCGLTextureUnitContextParentType uc,
    final R2FilterCompositorParameters parameters)
  {
    NullCheck.notNull(pc, "Profiling");
    NullCheck.notNull(uc, "Texture context");
    NullCheck.notNull(parameters, "Filter parameters");

    final JCGLProfilingContextType pc_base = pc.childContext("compositor");
    pc_base.startMeasuringIfEnabled();
    try {
      this.run(uc, parameters);
    } finally {
      pc_base.stopMeasuringIfEnabled();
    }
  }

  private void run(
    final JCGLTextureUnitContextParentType uc,
    final R2FilterCompositorParameters parameters)
  {
    final JCGLBlendingType g_b = this.g.blending();
    final JCGLDrawType g_dr = this.g.drawing();
    final JCGLArrayObjectsType g_ao = this.g.arrayObjects();
    final JCGLTexturesType g_tx = this.g.textures();
    final JCGLViewportsType g_v = this.g.viewports();

    JCGLRenderStates.activate(this.g, this.render_state);

    try {
      this.shader.onActivate(this.g);
      g_ao.arrayObjectBind(this.quad.arrayObject());

      for (final R2FilterCompositorItemType i : parameters.items()) {
        final Optional<R2FilterCompositorBlending> blend_opt =
          i.blending();
        if (blend_opt.isPresent()) {
          final R2FilterCompositorBlending blend = blend_opt.get();
          g_b.blendingEnableSeparateWithEquationSeparate(
            blend.blendFunctionSourceRGB(),
            blend.blendFunctionSourceAlpha(),
            blend.blendFunctionTargetRGB(),
            blend.blendFunctionTargetAlpha(),
            blend.blendEquationRGB(),
            blend.blendEquationAlpha());
        } else {
          g_b.blendingDisable();
        }

        final JCGLTextureUnitContextType c = uc.unitContextNew();
        try {
          g_v.viewportSet(i.outputViewport());

          this.values.setTextureUnitContext(c);
          this.values.setValues(
            R2ShaderFilterTextureShowParameters.builder()
              .setIntensity(i.intensity())
              .setTexture(i.texture())
              .build());

          this.shader.onReceiveFilterValues(this.g, this.values);
          this.shader.onValidate();

          g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
        } finally {
          c.unitContextFinish(g_tx);
        }
      }

    } finally {
      g_ao.arrayObjectUnbind();
      this.shader.onDeactivate(this.g);
    }
  }
}
