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

package com.io7m.r2.rendering.shadow;

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jcanephora.core.JCGLClearSpecification;
import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfunctional.PartialBiFunctionType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector4D;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.core.api.R2Exception;
import com.io7m.r2.lights.R2LightProjectiveWithShadowType;
import com.io7m.r2.lights.R2LightWithShadowSingleType;
import com.io7m.r2.lights.shadows.R2ShadowDepthVarianceType;
import com.io7m.r2.lights.shadows.R2ShadowType;
import com.io7m.r2.matrices.R2MatricesType;
import com.io7m.r2.rendering.depth.api.R2DepthInstancesType;
import com.io7m.r2.rendering.depth.variance.api.R2DepthVarianceBufferDescription;
import com.io7m.r2.rendering.depth.variance.api.R2DepthVarianceBufferUsableType;
import com.io7m.r2.rendering.depth.variance.api.R2DepthVarianceRendererType;
import com.io7m.r2.rendering.shadow.api.R2ExceptionShadowExecutionAlreadyActive;
import com.io7m.r2.rendering.shadow.api.R2ExceptionShadowExecutionNotActive;
import com.io7m.r2.rendering.shadow.api.R2ExceptionShadowMapContextAlreadyActive;
import com.io7m.r2.rendering.shadow.api.R2ExceptionShadowNotRendered;
import com.io7m.r2.rendering.shadow.api.R2ShadowMapContextType;
import com.io7m.r2.rendering.shadow.api.R2ShadowMapRendererExecutionType;
import com.io7m.r2.rendering.shadow.api.R2ShadowMapRendererType;
import com.io7m.r2.rendering.targets.R2RenderTargetPoolUsableType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import com.io7m.r2.textures.R2Texture2DUsableType;
import com.io7m.r2.transforms.R2TransformViewReadableType;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * The default implementation of the {@link R2ShadowMapRendererType} interface.
 */

public final class R2ShadowMapRenderer implements R2ShadowMapRendererType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2ShadowMapRenderer.class);
  }

  private final RendererContext context;
  private boolean deleted;

  private R2ShadowMapRenderer(
    final JCGLInterfaceGL33Type g33,
    final R2DepthVarianceRendererType vr,
    final R2RenderTargetPoolUsableType<
      R2DepthVarianceBufferDescription,
      R2DepthVarianceBufferUsableType> vp)
  {
    this.context = new RendererContext(g33, vr, vp);
  }

  /**
   * @param g33 An OpenGL interface
   * @param vr  A depth-variance renderer
   * @param vp  A depth-variance buffer pool
   *
   * @return A new shadow map renderer
   */

  public static R2ShadowMapRenderer newRenderer(
    final JCGLInterfaceGL33Type g33,
    final R2DepthVarianceRendererType vr,
    final R2RenderTargetPoolUsableType<
      R2DepthVarianceBufferDescription,
      R2DepthVarianceBufferUsableType> vp)
  {
    return new R2ShadowMapRenderer(g33, vr, vp);
  }

  @Override
  public R2ShadowMapRendererExecutionType shadowBegin()
  {
    if (this.context.active) {
      throw new R2ExceptionShadowExecutionAlreadyActive(
        "Shadow execution is already active");
    }

    this.context.start();
    return this.context;
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    LOG.debug("delete");
    this.deleted = true;
  }

  @Override
  public boolean isDeleted()
  {
    return this.deleted;
  }

  private static final class RendererContext implements
    R2ShadowMapRendererExecutionType
  {
    private final MapContext map_context;
    private final JCGLInterfaceGL33Type g33;
    private final VarianceState variance;
    private PMatrix4x4D<R2SpaceWorldType, R2SpaceEyeType> view;
    private boolean active;
    private @Nullable JCGLTextureUnitContextParentType texture_context;
    private @Nullable R2LightWithShadowSingleType light;
    private @Nullable R2DepthInstancesType instances;
    private @Nullable R2MatricesType matrices;
    private @Nullable JCGLProfilingContextType profiling_variance;

    private RendererContext(
      final
      JCGLInterfaceGL33Type g,
      final R2DepthVarianceRendererType vr,
      final R2RenderTargetPoolUsableType<R2DepthVarianceBufferDescription,
        R2DepthVarianceBufferUsableType> vp)
    {
      this.g33 = NullCheck.notNull(g, "G33");
      this.variance = new VarianceState(g, vr, vp);
      this.view = PMatrices4x4D.identity();
      this.map_context = new MapContext();
    }

    void start()
    {
      Preconditions.checkPrecondition(
        !this.active, "Renderer context must not be active");
      this.variance.clear();
      this.active = true;
    }

    private void renderLightProjectiveWithShadowVariance(
      final R2LightProjectiveWithShadowType lp,
      final R2ShadowDepthVarianceType sv)
    {
      /*
       * Fetch a variance shadow map.
       */

      this.variance.current = this.variance.pool.get(
        this.texture_context, sv.mapDescription());
      this.variance.used.put(
        sv.shadowID(), this.variance.current);

      /*
       * Transform the light volume.
       */

      final R2TransformViewReadableType tr = lp.transform();
      this.view = tr.transformMakeViewMatrix4x4F();

      this.matrices.withObserver(
        this.view,
        lp.projection(),
        this,
        (z, t) -> {

          final R2DepthVarianceBufferUsableType buffer =
            t.variance.current;

          final JCGLFramebuffersType gfb = t.g33.framebuffers();
          final JCGLTexturesType gt = t.g33.textures();

          /*
           * Render all instances into the framebuffer, clearing
           * it first.
           */

          gfb.framebufferDrawBind(buffer.primaryFramebuffer());
          buffer.clearBoundPrimaryFramebuffer(t.g33);
          t.variance.renderer.renderDepthVarianceWithBoundBuffer(
            buffer.sizeAsViewport(),
            t.texture_context,
            z,
            t.instances);
          gfb.framebufferDrawUnbind();

          /*
           * If the shadow map uses mipmaps, regenerate them.
           */

          final R2Texture2DUsableType rt_texture =
            buffer.depthVarianceTexture();
          final JCGLTexture2DUsableType texture =
            rt_texture.texture();

          switch (texture.minificationFilter()) {
            case TEXTURE_FILTER_LINEAR:
            case TEXTURE_FILTER_NEAREST: {
              break;
            }
            case TEXTURE_FILTER_NEAREST_MIPMAP_NEAREST:
            case TEXTURE_FILTER_LINEAR_MIPMAP_NEAREST:
            case TEXTURE_FILTER_NEAREST_MIPMAP_LINEAR:
            case TEXTURE_FILTER_LINEAR_MIPMAP_LINEAR: {
              final JCGLTextureUnitContextType tc =
                t.texture_context.unitContextNew();

              try {
                final JCGLTextureUnitType u =
                  tc.unitContextBindTexture2D(gt, rt_texture.texture());
                gt.texture2DRegenerateMipmaps(u);
              } finally {
                tc.unitContextFinish(gt);
              }
              break;
            }

          }
          return Unit.unit();
        });
    }

    @Override
    public void shadowExecRenderLight(
      final JCGLProfilingContextType pc,
      final JCGLTextureUnitContextParentType tc,
      final R2MatricesType m,
      final R2LightWithShadowSingleType ls,
      final R2DepthInstancesType i)
    {
      NullCheck.notNull(pc, "Profiling");
      NullCheck.notNull(tc, "Texture context");
      NullCheck.notNull(m, "Matrices");
      NullCheck.notNull(ls, "Light");
      NullCheck.notNull(i, "Instances");

      final JCGLProfilingContextType pc_base =
        pc.childContext("shadow-map-renderer");
      this.profiling_variance =
        pc_base.childContext("variance");

      this.texture_context = tc;
      this.light = ls;
      this.instances = i;
      this.matrices = m;

      final PartialBiFunctionType<
        RendererContext, R2ShadowDepthVarianceType, Unit,
        UnreachableCodeException>
        on_variance = (t, sv) -> {
        t.variance.shadow = sv;
        return t.light.matchLightWithShadow(t, (t1, lp) -> {
          t.profiling_variance.startMeasuringIfEnabled();
          try {
            t1.renderLightProjectiveWithShadowVariance(lp, t1.variance.shadow);
            return Unit.unit();
          } finally {
            t.profiling_variance.stopMeasuringIfEnabled();
          }
        });
      };

      final R2ShadowType s = ls.shadow();
      s.matchShadow(this, on_variance);
    }

    @Override
    public R2ShadowMapContextType shadowExecComplete()
    {
      if (!this.active) {
        throw new R2ExceptionShadowExecutionNotActive(
          "Shadow execution is not active");
      }

      if (this.map_context.active) {
        throw new R2ExceptionShadowMapContextAlreadyActive(
          "Shadow map context is already active");
      }

      this.map_context.start();
      return this.map_context;
    }

    private void finishContext()
    {
      try {
        for (final long id : this.variance.used.keySet()) {
          final R2DepthVarianceBufferUsableType map =
            this.variance.used.get(id);
          this.variance.pool.returnValue(this.texture_context, map);
        }
      } finally {
        this.light = null;
        this.instances = null;
        this.matrices = null;
        this.active = false;
      }
    }

    private static final class VarianceState
    {
      private final R2DepthVarianceRendererType renderer;
      private final R2RenderTargetPoolUsableType<R2DepthVarianceBufferDescription, R2DepthVarianceBufferUsableType> pool;
      private final Long2ReferenceOpenHashMap<R2DepthVarianceBufferUsableType> used;
      private final JCGLClearSpecification clear;
      private @Nullable R2DepthVarianceBufferUsableType current;
      private @Nullable R2ShadowDepthVarianceType shadow;

      private VarianceState(
        final JCGLInterfaceGL33Type g,
        final R2DepthVarianceRendererType vr,
        final R2RenderTargetPoolUsableType<R2DepthVarianceBufferDescription,
          R2DepthVarianceBufferUsableType> vp)
      {
        this.renderer =
          NullCheck.notNull(vr, "Depth variance renderer");
        this.pool =
          NullCheck.notNull(vp, "Render target pool");
        this.used = new Long2ReferenceOpenHashMap<>(32);
        this.clear = JCGLClearSpecification.of(
          Optional.of(Vector4D.of(1.0, 1.0, 1.0, 1.0)),
          OptionalDouble.of(1.0),
          OptionalInt.empty(),
          true);
      }

      void clear()
      {
        this.used.clear();
      }
    }

    private final class MapContext implements R2ShadowMapContextType
    {
      private boolean active;
      private @Nullable R2LightWithShadowSingleType light;

      MapContext()
      {

      }

      @Override
      public R2Texture2DUsableType shadowMapGet(
        final R2LightWithShadowSingleType ls)
      {
        this.light = NullCheck.notNull(ls, "Light");

        final PartialBiFunctionType<
          RendererContext,
          R2LightProjectiveWithShadowType,
          R2Texture2DUsableType,
          RuntimeException>
          on_projective_with_shadow =
          (t, lp) ->
            lp.shadow().matchShadow(t, (t1, sv) -> {
              final long shadow_id = sv.shadowID();
              if (t1.variance.used.containsKey(shadow_id)) {
                final R2DepthVarianceBufferUsableType map =
                  t1.variance.used.get(shadow_id);
                return map.depthVarianceTexture();
              }

              final StringBuilder sb = new StringBuilder(128);
              sb.append("Shadow map has not been rendered!");
              sb.append(System.lineSeparator());
              sb.append("Light: ");
              sb.append(t1.light);
              sb.append(System.lineSeparator());
              sb.append("Shadow: ");
              sb.append(sv);
              sb.append(System.lineSeparator());
              throw new R2ExceptionShadowNotRendered(sb.toString());
            });

        return ls.matchLightWithShadow(
          RendererContext.this, on_projective_with_shadow);
      }

      @Override
      public void shadowMapContextFinish()
      {
        Preconditions.checkPrecondition(
          this.active, "Renderer context must be active");

        try {
          this.light = null;
          RendererContext.this.finishContext();
        } finally {
          this.active = false;
        }
      }

      void start()
      {
        Preconditions.checkPrecondition(
          !this.active, "Map context must not be active");
        Preconditions.checkPrecondition(
          RendererContext.this.active, "Renderer context must be active");

        this.active = true;
      }
    }
  }
}
