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

package com.io7m.r2.shaders.geometry;

import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.spaces.R2SpaceRGBAType;
import com.io7m.r2.spaces.R2SpaceRGBType;
import com.io7m.r2.textures.R2Texture2DUsableType;
import com.io7m.r2.textures.R2TextureDefaultsType;
import org.immutables.value.Value;

/**
 * Parameters for the basic geometry shader.
 */

@R2ImmutableStyleType
@Value.Immutable
public interface R2GeometryShaderBasicParametersType
  extends R2GeometryShaderBasicParametersValuesType
{
  @Override
  @Value.Parameter
  R2TextureDefaultsType textureDefaults();

  @Override
  @Value.Default
  @Value.Parameter
  default PVector4D<R2SpaceRGBAType> albedoColor()
  {
    return R2GeometryShaderBasicParametersValuesType.super.albedoColor();
  }

  @Override
  @Value.Default
  @Value.Parameter
  default PVector3D<R2SpaceRGBType> specularColor()
  {
    return R2GeometryShaderBasicParametersValuesType.super.specularColor();
  }

  @Override
  @Value.Default
  @Value.Parameter
  default double emission()
  {
    return R2GeometryShaderBasicParametersValuesType.super.emission();
  }

  @Override
  @Value.Default
  @Value.Parameter
  default R2Texture2DUsableType emissionTexture()
  {
    return R2GeometryShaderBasicParametersValuesType.super.emissionTexture();
  }

  @Override
  @Value.Default
  @Value.Parameter
  default R2Texture2DUsableType albedoTexture()
  {
    return R2GeometryShaderBasicParametersValuesType.super.albedoTexture();
  }

  @Override
  @Value.Default
  @Value.Parameter
  default double albedoMix()
  {
    return R2GeometryShaderBasicParametersValuesType.super.albedoMix();
  }

  @Override
  @Value.Default
  @Value.Parameter
  default R2Texture2DUsableType specularTexture()
  {
    return R2GeometryShaderBasicParametersValuesType.super.specularTexture();
  }

  @Override
  @Value.Default
  @Value.Parameter
  default double specularExponent()
  {
    return R2GeometryShaderBasicParametersValuesType.super.specularExponent();
  }

  @Override
  @Value.Default
  @Value.Parameter
  default R2Texture2DUsableType normalTexture()
  {
    return R2GeometryShaderBasicParametersValuesType.super.normalTexture();
  }

  @Override
  @Value.Default
  @Value.Parameter
  default double alphaDiscardThreshold()
  {
    return R2GeometryShaderBasicParametersValuesType.super.alphaDiscardThreshold();
  }
}
