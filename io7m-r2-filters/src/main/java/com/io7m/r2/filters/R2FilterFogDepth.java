package com.io7m.r2.filters;

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
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2UnitQuadUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import org.valid4j.Assertive;

/**
 * A depth-based fog filter.
 */

public final class R2FilterFogDepth implements R2FilterType<R2FilterFogParametersType>
{
  private final R2ShaderFilterType<R2ShaderFilterFogParametersType> shader_linear;
  private final R2ShaderFilterType<R2ShaderFilterFogParametersType> shader_quad;
  private final R2ShaderFilterType<R2ShaderFilterFogParametersType> shader_quad_inv;
  private final JCGLInterfaceGL33Type g;
  private final JCGLRenderStateMutable render_state;
  private final R2UnitQuadUsableType quad;
  private final R2ShaderFilterFogParametersMutable shader_params;

  private R2FilterFogDepth(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final R2UnitQuadUsableType in_quad)
  {
    this.g = NullCheck.notNull(in_g);
    this.shader_linear = R2ShaderFilterFogDepthLinear.newShader(
      this.g.getShaders(), in_shader_env, in_pool);
    this.shader_quad = R2ShaderFilterFogDepthQuadratic.newShader(
      this.g.getShaders(), in_shader_env, in_pool);
    this.shader_quad_inv = R2ShaderFilterFogDepthQuadraticInverse.newShader(
      this.g.getShaders(), in_shader_env, in_pool);

    this.render_state =
      JCGLRenderStateMutable.create();
    this.quad =
      NullCheck.notNull(in_quad);
    this.shader_params =
      R2ShaderFilterFogParametersMutable.create();
  }

  /**
   * @param in_g          An OpenGL interface
   * @param in_shader_env Shader sources
   * @param in_pool       The ID pool
   * @param in_quad       A unit quad
   *
   * @return A new filter
   */

  public static R2FilterType<R2FilterFogParametersType> newFilter(
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
    final R2FilterFogParametersType parameters)
  {
    NullCheck.notNull(uc);
    NullCheck.notNull(parameters);

    Assertive.require(!this.isDeleted(), "Filter not deleted");

    final JCGLProfilingContextType pc_base = pc.getChildContext("fog-depth");
    pc_base.startMeasuringIfEnabled();
    try {
      this.run(uc, parameters);
    } finally {
      pc_base.stopMeasuringIfEnabled();
    }
  }

  private void run(
    final JCGLTextureUnitContextParentType uc,
    final R2FilterFogParametersType parameters)
  {
    final JCGLArrayObjectsType g_ao = this.g.getArrayObjects();
    final JCGLFramebuffersType g_fb = this.g.getFramebuffers();
    final JCGLTexturesType g_tex = this.g.getTextures();
    final JCGLShadersType g_sh = this.g.getShaders();
    final JCGLDrawType g_dr = this.g.getDraw();
    final JCGLViewportsType g_v = this.g.getViewports();

    R2ShaderFilterType<R2ShaderFilterFogParametersType> s = null;
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
    NullCheck.notNull(s);

    try {
      final JCGLTextureUnitContextType tc = uc.unitContextNew();

      try {
        JCGLRenderStates.activate(this.g, this.render_state);
        g_v.viewportSet(parameters.viewport());

        try {
          g_ao.arrayObjectBind(this.quad.arrayObject());

          final R2MatricesObserverValuesType m = parameters.observerValues();

          this.shader_params.clear();
          this.shader_params.setImageTexture(
            parameters.imageTexture());
          this.shader_params.setImageDepthTexture(
            parameters.imageDepthTexture());
          this.shader_params.setObserverValues(
            m);
          this.shader_params.setFogColor(
            parameters.fogColor());
          this.shader_params.setFogFarPositiveZ(
            parameters.fogFarPositiveZ());
          this.shader_params.setFogNearPositiveZ(
            parameters.fogNearPositiveZ());

          s.onActivate(g_sh);
          s.onReceiveFilterValues(g_tex, g_sh, tc, this.shader_params);
          s.onValidate();

          g_dr.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
        } finally {
          g_ao.arrayObjectUnbind();
          s.onDeactivate(g_sh);
        }

      } finally {
        tc.unitContextFinish(g_tex);
      }

    } finally {
      g_fb.framebufferDrawUnbind();
    }
  }
}
