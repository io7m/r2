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

import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightSphericalSingle;
import com.io7m.r2.core.R2LightSphericalSingleReadableType;
import com.io7m.r2.core.R2TransformOT;
import com.io7m.r2.core.R2UnitQuad;
import com.io7m.r2.core.debug.R2DebugShaderLightSphericalConstantSingle;
import com.io7m.r2.core.shaders.types.R2ShaderLightVolumeSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;

public abstract class R2ShaderLightSphericalDebugConstantSingleContract
  extends R2ShaderLightVolumeSingleContract<R2LightSphericalSingleReadableType>
{
  @Override
  protected final R2ShaderLightVolumeSingleType<
    R2LightSphericalSingleReadableType> newShaderWithVerifier(
    final JCGLInterfaceGL33Type g,
    final R2ShaderSourcesType sources,
    final R2IDPoolType pool)
  {
    return R2DebugShaderLightSphericalConstantSingle.newShader(
      g.getShaders(), sources, pool);
  }

  @Override
  protected final R2LightSphericalSingleReadableType newLight(
    final JCGLInterfaceGL33Type g,
    final R2IDPoolType pool)
  {
    return R2LightSphericalSingle.newLightWithVolume(
      R2UnitQuad.newUnitQuad(g).getArrayObject(),
      R2TransformOT.newTransform(), pool);
  }
}
