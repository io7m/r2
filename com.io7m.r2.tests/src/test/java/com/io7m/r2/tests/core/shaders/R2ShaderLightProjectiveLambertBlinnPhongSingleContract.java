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

package com.io7m.r2.tests.core.shaders;

import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightProjectiveReadableType;
import com.io7m.r2.core.R2LightProjectiveWithoutShadow;
import com.io7m.r2.core.R2ProjectionFrustum;
import com.io7m.r2.core.R2ProjectionMesh;
import com.io7m.r2.core.R2ProjectionMeshType;
import com.io7m.r2.core.R2ProjectionType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.shaders.provided.R2LightShaderProjectiveLambertBlinnPhongSingle;
import com.io7m.r2.core.shaders.types.R2ShaderLightProjectiveType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;

public abstract class R2ShaderLightProjectiveLambertBlinnPhongSingleContract
  extends R2ShaderLightProjectiveContract<R2LightProjectiveReadableType>
{
  @Override
  protected final R2ShaderLightProjectiveType<R2LightProjectiveReadableType>
  newShaderWithVerifier(
    final JCGLInterfaceGL33Type g,
    final R2ShaderPreprocessingEnvironmentType sources,
    final R2IDPoolType pool)
  {
    return R2LightShaderProjectiveLambertBlinnPhongSingle.newShader(
      g.shaders(), sources, pool);
  }

  @Override
  protected final R2LightProjectiveReadableType newLight(
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
    return R2LightProjectiveWithoutShadow.create(
      pm, td.whiteProjective2D(), pool);
  }
}
