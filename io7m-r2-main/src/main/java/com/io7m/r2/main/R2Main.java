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

package com.io7m.r2.main;

import com.io7m.jcanephora.core.JCGLProjectionMatrices;
import com.io7m.jcanephora.core.JCGLProjectionMatricesType;
import com.io7m.jcanephora.core.JCGLViewMatrices;
import com.io7m.jcanephora.core.JCGLViewMatricesType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.profiler.JCGLProfiling;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.core.R2DeletableType;
import com.io7m.r2.core.R2DepthOnlyRenderer;
import com.io7m.r2.core.R2DepthRendererType;
import com.io7m.r2.core.R2DepthVarianceBufferDescriptionType;
import com.io7m.r2.core.R2DepthVarianceBufferPool;
import com.io7m.r2.core.R2DepthVarianceBufferUsableType;
import com.io7m.r2.core.R2DepthVarianceRenderer;
import com.io7m.r2.core.R2DepthVarianceRendererType;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2GeometryRenderer;
import com.io7m.r2.core.R2GeometryRendererType;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightRenderer;
import com.io7m.r2.core.R2LightRendererType;
import com.io7m.r2.core.R2Matrices;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2RenderTargetPoolUsableType;
import com.io7m.r2.core.R2ShadowMapRenderer;
import com.io7m.r2.core.R2ShadowMapRendererType;
import com.io7m.r2.core.R2StencilRenderer;
import com.io7m.r2.core.R2StencilRendererType;
import com.io7m.r2.core.R2TextureDefaults;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2UnitQuad;
import com.io7m.r2.core.R2UnitQuadType;
import com.io7m.r2.core.R2UnitQuadUsableType;
import com.io7m.r2.core.debug.R2DebugVisualizerRenderer;
import com.io7m.r2.core.debug.R2DebugVisualizerRendererType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironment;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.sombrero.core.SoShaderPreprocessorConfig;
import com.io7m.sombrero.core.SoShaderPreprocessorType;
import com.io7m.sombrero.core.SoShaderResolver;
import com.io7m.sombrero.jcpp.SoShaderPreprocessorJCPP;

import java.lang.reflect.Field;
import java.util.OptionalInt;
import java.util.function.Supplier;

/**
 * The default implementation of the {@link R2MainType} interface.
 */

public final class R2Main implements R2MainType
{
  /**
   * The default soft limit for the depth variance buffer pool.
   */

  public static final long DEFAULT_DEPTH_VARIANCE_POOL_SOFT_LIMIT;

  static {
    DEFAULT_DEPTH_VARIANCE_POOL_SOFT_LIMIT = 8L * (512L * 512L * 8L);
  }

  private final R2IDPoolType pool;
  private final R2ShaderPreprocessingEnvironmentType sources;
  private final R2StencilRendererType stencil_renderer;
  private final R2MatricesType matrices;
  private final JCGLViewMatricesType view_matrices;
  private final JCGLProjectionMatricesType proj_matrices;
  private final R2TextureDefaultsType texture_defaults;
  private final R2GeometryRendererType geometry_renderer;
  private final R2LightRendererType light_renderer;
  private final JCGLTextureUnitAllocatorType texture_allocator;
  private final R2UnitQuadType unit_quad;
  private final R2DebugVisualizerRendererType debug_visual_renderer;
  private final R2DepthRendererType depth_renderer;
  private final R2DepthVarianceRendererType depth_variance_renderer;
  private final R2ShadowMapRendererType shadow_map_renderer;
  private final R2RenderTargetPoolUsableType<R2DepthVarianceBufferDescriptionType, R2DepthVarianceBufferUsableType> depth_variance_pool;
  private final JCGLProfilingType profiling;
  private boolean deleted;

  private R2Main(
    final R2IDPoolType in_pool,
    final R2ShaderPreprocessingEnvironmentType in_sources,
    final R2StencilRendererType in_stencil_renderer,
    final R2MatricesType in_matrices,
    final JCGLViewMatricesType in_view_matrices,
    final JCGLProjectionMatricesType in_proj_matrices,
    final JCGLTextureUnitAllocatorType in_texture_allocator,
    final R2TextureDefaultsType in_texture_defaults,
    final R2GeometryRendererType in_geometry_renderer,
    final R2LightRendererType in_light_renderer,
    final R2DebugVisualizerRendererType in_debug_visual_renderer,
    final R2UnitQuadType in_unit_quad,
    final R2DepthRendererType in_depth_renderer,
    final R2DepthVarianceRendererType in_depth_variance_renderer,
    final R2RenderTargetPoolUsableType<R2DepthVarianceBufferDescriptionType, R2DepthVarianceBufferUsableType> in_depth_variance_pool,
    final R2ShadowMapRendererType in_shadow_map_renderer,
    final JCGLProfilingType in_profiling)
  {
    this.pool =
      NullCheck.notNull(in_pool);
    this.sources =
      NullCheck.notNull(in_sources);
    this.stencil_renderer =
      NullCheck.notNull(in_stencil_renderer);
    this.matrices =
      NullCheck.notNull(in_matrices);
    this.view_matrices =
      NullCheck.notNull(in_view_matrices);
    this.proj_matrices =
      NullCheck.notNull(in_proj_matrices);
    this.texture_allocator =
      NullCheck.notNull(in_texture_allocator);
    this.texture_defaults =
      NullCheck.notNull(in_texture_defaults);
    this.geometry_renderer =
      NullCheck.notNull(in_geometry_renderer);
    this.light_renderer =
      NullCheck.notNull(in_light_renderer);
    this.debug_visual_renderer =
      NullCheck.notNull(in_debug_visual_renderer);
    this.unit_quad =
      NullCheck.notNull(in_unit_quad);
    this.depth_renderer =
      NullCheck.notNull(in_depth_renderer);
    this.depth_variance_renderer =
      NullCheck.notNull(in_depth_variance_renderer);
    this.depth_variance_pool =
      NullCheck.notNull(in_depth_variance_pool);
    this.shadow_map_renderer =
      NullCheck.notNull(in_shadow_map_renderer);
    this.profiling =
      NullCheck.notNull(in_profiling);

    this.deleted = false;
  }

  /**
   * @return A new frontend builder
   */

  public static R2MainBuilderType newBuilder()
  {
    return new Builder();
  }

  @Override
  public R2IDPoolType getIDPool()
  {
    return this.pool;
  }

  @Override
  public R2ShaderPreprocessingEnvironmentType getShaderPreprocessingEnvironment()
  {
    return this.sources;
  }

  @Override
  public JCGLViewMatricesType getViewMatrices()
  {
    return this.view_matrices;
  }

  @Override
  public JCGLProjectionMatricesType getProjectionMatrices()
  {
    return this.proj_matrices;
  }

  @Override
  public R2StencilRendererType getStencilRenderer()
  {
    return this.stencil_renderer;
  }

  @Override
  public R2MatricesType getMatrices()
  {
    return this.matrices;
  }

  @Override
  public JCGLTextureUnitAllocatorType getTextureUnitAllocator()
  {
    return this.texture_allocator;
  }

  @Override
  public R2TextureDefaultsType getTextureDefaults()
  {
    return this.texture_defaults;
  }

  @Override
  public R2GeometryRendererType getGeometryRenderer()
  {
    return this.geometry_renderer;
  }

  @Override
  public R2LightRendererType getLightRenderer()
  {
    return this.light_renderer;
  }

  @Override
  public R2UnitQuadUsableType getUnitQuad()
  {
    return this.unit_quad;
  }

  @Override
  public R2DebugVisualizerRendererType getDebugVisualizerRenderer()
  {
    return this.debug_visual_renderer;
  }

  @Override
  public R2DepthRendererType getDepthRenderer()
  {
    return this.depth_renderer;
  }

  @Override
  public R2DepthVarianceRendererType getDepthVarianceRenderer()
  {
    return this.depth_variance_renderer;
  }

  @Override
  public R2ShadowMapRendererType getShadowMapRenderer()
  {
    return this.shadow_map_renderer;
  }

  @Override
  public JCGLProfilingType getProfiling()
  {
    return this.profiling;
  }

  @Override
  public void delete(
    final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    if (!this.isDeleted()) {
      try {
        final Class<? extends R2Main> c = this.getClass();
        final Field[] fields = c.getDeclaredFields();
        for (int index = 0; index < fields.length; ++index) {
          final Field field = fields[index];
          final Class<?> field_type = field.getType();
          if (R2DeletableType.class.isAssignableFrom(field_type)) {
            try {
              final R2DeletableType k = (R2DeletableType) field.get(this);
              k.delete(g);
            } catch (final IllegalAccessException e) {
              throw new UnreachableCodeException(e);
            }
          }
        }
      } finally {
        this.deleted = true;
      }
    }
  }

  @Override
  public boolean isDeleted()
  {
    return this.deleted;
  }

  private static final class Builder implements R2MainBuilderType
  {
    private @Nullable R2StencilRendererType stencil_renderer;
    private @Nullable R2ShaderPreprocessingEnvironmentType sources;
    private @Nullable R2IDPoolType pool;
    private @Nullable R2MatricesType matrices;
    private @Nullable JCGLViewMatricesType view_matrices;
    private @Nullable JCGLProjectionMatricesType proj_matrices;
    private @Nullable R2TextureDefaultsType texture_defaults;
    private @Nullable R2GeometryRendererType geometry_renderer;
    private @Nullable R2LightRendererType light_renderer;
    private @Nullable JCGLTextureUnitAllocatorType texture_unit_alloc;
    private @Nullable R2UnitQuadType unit_quad;
    private @Nullable R2DebugVisualizerRendererType debug_visual_renderer;
    private @Nullable R2DepthRendererType depth_renderer;
    private @Nullable R2DepthVarianceRendererType depth_variance_renderer;
    private @Nullable
    R2RenderTargetPoolUsableType<R2DepthVarianceBufferDescriptionType, R2DepthVarianceBufferUsableType> depth_variance_pool;
    private @Nullable R2ShadowMapRendererType shadow_map_renderer;
    private @Nullable JCGLProfilingType profiling;

    Builder()
    {

    }

    private static <T> T compute(
      final T field,
      final Supplier<T> c)
    {
      if (field == null) {
        return c.get();
      }
      return field;
    }

    @Override
    public R2MainType build(
      final JCGLInterfaceGL33Type g)
    {
      NullCheck.notNull(g);

      final R2IDPoolType ex_pool =
        Builder.compute(this.pool, R2IDPool::newPool);

      final R2ShaderPreprocessingEnvironmentType ex_sources =
        Builder.compute(
          this.sources,
          () -> {
            final SoShaderPreprocessorConfig.Builder b =
              SoShaderPreprocessorConfig.builder();
            b.setResolver(SoShaderResolver.create());
            b.setVersion(OptionalInt.of(330));
            final SoShaderPreprocessorType p =
              SoShaderPreprocessorJCPP.create(b.build());
            return R2ShaderPreprocessingEnvironment.create(p);
          });

      final R2UnitQuadType ex_quad = Builder.compute(
        this.unit_quad,
        () -> R2UnitQuad.newUnitQuad(g));

      final R2StencilRendererType ex_stencil_renderer =
        Builder.compute(
          this.stencil_renderer,
          () -> R2StencilRenderer.newRenderer(ex_sources, g, ex_pool, ex_quad));

      final R2MatricesType ex_matrices =
        Builder.compute(this.matrices, R2Matrices::newMatrices);

      final JCGLViewMatricesType ex_view_matrices =
        Builder.compute(
          this.view_matrices, JCGLViewMatrices::newMatrices);
      final JCGLProjectionMatricesType ex_proj_matrices =
        Builder.compute(
          this.proj_matrices, JCGLProjectionMatrices::newMatrices);

      final JCGLTextureUnitAllocatorType ex_unit_alloc =
        Builder.compute(
          this.texture_unit_alloc,
          () -> JCGLTextureUnitAllocator.newAllocatorWithStack(
            32,
            g.getTextures().textureGetUnits()));

      final R2TextureDefaultsType ex_texture_defaults =
        Builder.compute(
          this.texture_defaults,
          () -> R2TextureDefaults.newDefaults(
            g.getTextures(),
            ex_unit_alloc.getRootContext()));

      final R2GeometryRendererType ex_geometry_renderer =
        Builder.compute(
          this.geometry_renderer,
          () -> R2GeometryRenderer.newRenderer(g));

      final R2LightRendererType ex_light_renderer = Builder.compute(
        this.light_renderer,
        () -> R2LightRenderer.newRenderer(
          g,
          ex_texture_defaults,
          ex_sources,
          ex_pool,
          ex_quad));

      final R2DebugVisualizerRendererType ex_debug_visual_renderer =
        Builder.compute(
          this.debug_visual_renderer,
          () -> R2DebugVisualizerRenderer.newRenderer(g, ex_sources, ex_pool));

      final R2DepthRendererType ex_depth_renderer =
        Builder.compute(
          this.depth_renderer,
          () -> R2DepthOnlyRenderer.newRenderer(g));

      final R2DepthVarianceRendererType ex_depth_variance_renderer =
        Builder.compute(
          this.depth_variance_renderer,
          () -> R2DepthVarianceRenderer.newRenderer(g));

      final R2RenderTargetPoolUsableType
        <R2DepthVarianceBufferDescriptionType,
          R2DepthVarianceBufferUsableType> ex_depth_variance_pool =
        Builder.compute(
          this.depth_variance_pool,
          () -> R2DepthVarianceBufferPool.newPool(
            g, R2Main.DEFAULT_DEPTH_VARIANCE_POOL_SOFT_LIMIT, Long.MAX_VALUE));

      final R2ShadowMapRendererType ex_shadow_map_renderer =
        Builder.compute(
          this.shadow_map_renderer,
          () -> R2ShadowMapRenderer.newRenderer(
            g,
            ex_depth_variance_renderer,
            ex_depth_variance_pool));

      final JCGLProfilingType ex_profiling =
        Builder.compute(
          this.profiling, () -> JCGLProfiling.newProfiling(g.getTimers()));

      return new R2Main(
        ex_pool,
        ex_sources,
        ex_stencil_renderer,
        ex_matrices,
        ex_view_matrices,
        ex_proj_matrices,
        ex_unit_alloc,
        ex_texture_defaults,
        ex_geometry_renderer,
        ex_light_renderer,
        ex_debug_visual_renderer,
        ex_quad,
        ex_depth_renderer,
        ex_depth_variance_renderer,
        ex_depth_variance_pool,
        ex_shadow_map_renderer,
        ex_profiling);
    }
  }
}
