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

import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.lights.R2LightSphericalSingle;
import com.io7m.r2.lights.R2LightSphericalSingleReadableType;
import com.io7m.r2.meshes.defaults.R2UnitSphere;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.shaders.light.R2LightShaderSphericalLambertPhongSingle;
import com.io7m.r2.shaders.light.api.R2ShaderLightVolumeSingleType;
import com.io7m.r2.tests.shaders.lights.api.R2ShaderLightVolumeSingleContract;

public abstract class R2ShaderLightSphericalLambertPhongSingleContract extends
  R2ShaderLightVolumeSingleContract<R2LightSphericalSingleReadableType>
{
  protected abstract R2MeshLoaderType loader();

  @Override
  protected final R2ShaderLightVolumeSingleType<
    R2LightSphericalSingleReadableType> newShaderWithVerifier(
    final JCGLInterfaceGL33Type g,
    final R2ShaderPreprocessingEnvironmentReadableType sources,
    final R2IDPoolType pool)
  {
    return R2LightShaderSphericalLambertPhongSingle.create(
      g.shaders(), sources, pool);
  }

  @Override
  protected final R2LightSphericalSingleReadableType newLight(
    final JCGLInterfaceGL33Type g,
    final R2IDPoolType pool)
  {
    return R2LightSphericalSingle.newLight(
      R2UnitSphere.newUnitSphere8(this.loader(), g), pool);
  }
}
