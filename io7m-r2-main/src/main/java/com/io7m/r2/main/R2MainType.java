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

import com.io7m.jcanephora.core.JCGLProjectionMatricesType;
import com.io7m.jcanephora.core.JCGLViewMatricesType;
import com.io7m.r2.core.R2DeletableType;
import com.io7m.r2.core.R2DepthRendererType;
import com.io7m.r2.core.R2DepthVarianceRendererType;
import com.io7m.r2.core.R2GeometryRendererType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightRendererType;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2StencilRendererType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2TextureUnitAllocatorType;
import com.io7m.r2.core.R2UnitQuadUsableType;
import com.io7m.r2.core.debug.R2DebugVisualizerRendererType;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;

/**
 * User-friendly frontend.
 */

public interface R2MainType extends R2DeletableType
{
  /**
   * @return The identifier pool
   */

  R2IDPoolType getIDPool();

  /**
   * @return The default set of shader sources
   */

  R2ShaderSourcesType getShaderSources();

  /**
   * @return Access to functions to produce view matrices
   */

  JCGLViewMatricesType getViewMatrices();

  /**
   * @return Access to functions to produce projection matrices
   */

  JCGLProjectionMatricesType getProjectionMatrices();

  /**
   * @return A stencil renderer
   */

  R2StencilRendererType getStencilRenderer();

  /**
   * @return Access to matrices
   */

  R2MatricesType getMatrices();

  /**
   * @return The texture unit allocator
   */

  R2TextureUnitAllocatorType getTextureUnitAllocator();

  /**
   * @return The set of default textures
   */

  R2TextureDefaultsType getTextureDefaults();

  /**
   * @return A geometry renderer
   */

  R2GeometryRendererType getGeometryRenderer();

  /**
   * @return A light renderer
   */

  R2LightRendererType getLightRenderer();

  /**
   * @return A default unit quad
   */

  R2UnitQuadUsableType getUnitQuad();

  /**
   * @return A renderer for visualizing debug info
   */

  R2DebugVisualizerRendererType getDebugVisualizerRenderer();

  /**
   * @return A depth renderer
   */

  R2DepthRendererType getDepthRenderer();

  /**
   * @return A depth variance renderer
   */

  R2DepthVarianceRendererType getDepthVarianceRenderer();
}
