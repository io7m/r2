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

package com.io7m.r2.tests.shaders.lights;

import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.lights.R2LightProjectiveWithShadowVariance;
import com.io7m.r2.lights.R2LightProjectiveWithShadowVarianceType;
import com.io7m.r2.lights.shadows.R2ShadowDepthVariance;
import com.io7m.r2.projections.R2ProjectionFrustum;
import com.io7m.r2.projections.R2ProjectionMesh;
import com.io7m.r2.projections.R2ProjectionMeshType;
import com.io7m.r2.projections.R2ProjectionType;
import com.io7m.r2.rendering.depth.api.R2DepthPrecision;
import com.io7m.r2.rendering.depth.variance.api.R2DepthVarianceBufferDescription;
import com.io7m.r2.rendering.depth.variance.api.R2DepthVariancePrecision;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.shaders.light.R2LightShaderProjectiveLambertBlinnPhongShadowVarianceSingle;
import com.io7m.r2.shaders.light.api.R2ShaderLightProjectiveWithShadowType;
import com.io7m.r2.tests.shaders.lights.api.R2ShaderLightProjectiveWithShadowContract;
import com.io7m.r2.textures.R2TextureDefaultsType;

public abstract class R2ShaderLightProjectiveLambertBlinnPhongShadowVarianceSingleContract
  extends R2ShaderLightProjectiveWithShadowContract<R2LightProjectiveWithShadowVarianceType>
{
  @Override
  protected final R2LightProjectiveWithShadowVarianceType newLight(
    final JCGLInterfaceGL33Type g,
    final R2IDPoolType pool,
    final JCGLTextureUnitContextType uc,
    final R2TextureDefaultsType td)
  {
    final R2ProjectionType p =
      R2ProjectionFrustum.create();
    final R2ProjectionMeshType pm =
      R2ProjectionMesh.create(
        g, p, JCGLUsageHint.USAGE_STATIC_DRAW, JCGLUsageHint.USAGE_STATIC_DRAW);

    final R2ShadowDepthVariance shadow = R2ShadowDepthVariance.of(
      pool.freshID(),
      R2DepthVarianceBufferDescription.of(
        AreaSizeL.of(64L, 64L),
        JCGLTextureFilterMagnification.TEXTURE_FILTER_LINEAR,
        JCGLTextureFilterMinification.TEXTURE_FILTER_LINEAR,
        R2DepthPrecision.R2_DEPTH_PRECISION_24,
        R2DepthVariancePrecision.R2_DEPTH_VARIANCE_PRECISION_16));

    return R2LightProjectiveWithShadowVariance.create(
      pm, td.whiteProjective2D(), shadow, pool);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected final R2ShaderLightProjectiveWithShadowType<
    R2LightProjectiveWithShadowVarianceType>
  newShaderWithVerifier(
    final JCGLInterfaceGL33Type g,
    final R2ShaderPreprocessingEnvironmentType sources,
    final R2IDPoolType pool)
  {
    return R2LightShaderProjectiveLambertBlinnPhongShadowVarianceSingle.create(
      g.shaders(), sources, pool);
  }
}
