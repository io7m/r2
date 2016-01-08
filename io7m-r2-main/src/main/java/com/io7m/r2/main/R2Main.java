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
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2GeometryRenderer;
import com.io7m.r2.core.R2GeometryRendererType;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2Matrices;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ShaderSourcesResources;
import com.io7m.r2.core.R2ShaderSourcesType;
import com.io7m.r2.core.R2StencilRenderer;
import com.io7m.r2.core.R2StencilRendererType;
import com.io7m.r2.core.R2TextureDefaults;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.shaders.R2Shaders;

import java.util.function.Supplier;

/**
 * The default implementation of the {@link R2MainType} interface.
 */

public final class R2Main implements R2MainType
{
  private final R2IDPoolType               pool;
  private final R2ShaderSourcesType        sources;
  private final R2StencilRendererType      stencil_renderer;
  private final R2MatricesType             matrices;
  private final JCGLViewMatricesType       view_matrices;
  private final JCGLProjectionMatricesType proj_matrices;
  private final R2TextureDefaultsType      texture_defaults;
  private final R2GeometryRendererType     geometry_renderer;
  private       boolean                    deleted;

  private R2Main(
    final R2IDPoolType in_pool,
    final R2ShaderSourcesType in_sources,
    final R2StencilRendererType in_stencil_renderer,
    final R2MatricesType in_matrices,
    final JCGLViewMatricesType in_view_matrices,
    final JCGLProjectionMatricesType in_proj_matrices,
    final R2TextureDefaultsType in_texture_defaults,
    final R2GeometryRendererType in_geometry_renderer)
  {
    this.pool = NullCheck.notNull(in_pool);
    this.sources = NullCheck.notNull(in_sources);
    this.stencil_renderer = NullCheck.notNull(in_stencil_renderer);
    this.matrices = NullCheck.notNull(in_matrices);
    this.view_matrices = NullCheck.notNull(in_view_matrices);
    this.proj_matrices = NullCheck.notNull(in_proj_matrices);
    this.texture_defaults = NullCheck.notNull(in_texture_defaults);
    this.geometry_renderer = NullCheck.notNull(in_geometry_renderer);
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
  public void delete(
    final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    if (!this.isDeleted()) {
      try {
        this.stencil_renderer.delete(g);
        this.texture_defaults.delete(g);
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
    private @Nullable R2StencilRendererType      stencil_renderer;
    private @Nullable R2ShaderSourcesType        sources;
    private @Nullable R2IDPoolType               pool;
    private @Nullable R2MatricesType             matrices;
    private @Nullable JCGLViewMatricesType       view_matrices;
    private @Nullable JCGLProjectionMatricesType proj_matrices;
    private @Nullable R2TextureDefaultsType      texture_defaults;
    private @Nullable R2GeometryRendererType     geometry_renderer;

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
    public R2MainType build(final JCGLInterfaceGL33Type g)
    {
      NullCheck.notNull(g);

      final R2IDPoolType ex_pool =
        Builder.compute(this.pool, R2IDPool::newPool);

      final R2ShaderSourcesType ex_sources =
        Builder.compute(
          this.sources,
          () -> R2ShaderSourcesResources.newSources(R2Shaders.class));

      final R2StencilRendererType ex_stencil_renderer =
        Builder.compute(
          this.stencil_renderer,
          () -> R2StencilRenderer.newRenderer(ex_sources, g, ex_pool));

      final R2MatricesType ex_matrices =
        Builder.compute(this.matrices, R2Matrices::newMatrices);

      final JCGLViewMatricesType ex_view_matrices =
        Builder.compute(
          this.view_matrices, JCGLViewMatrices::newMatrices);
      final JCGLProjectionMatricesType ex_proj_matrices =
        Builder.compute(
          this.proj_matrices, JCGLProjectionMatrices::newMatrices);

      final R2TextureDefaultsType ex_texture_defaults =
        Builder.compute(
          this.texture_defaults,
          () -> R2TextureDefaults.newDefaults(g));

      final R2GeometryRendererType ex_geometry_renderer =
        Builder.compute(
          this.geometry_renderer,
          R2GeometryRenderer::newRenderer);

      return new R2Main(
        ex_pool,
        ex_sources,
        ex_stencil_renderer,
        ex_matrices,
        ex_view_matrices,
        ex_proj_matrices,
        ex_texture_defaults,
        ex_geometry_renderer);
    }
  }
}