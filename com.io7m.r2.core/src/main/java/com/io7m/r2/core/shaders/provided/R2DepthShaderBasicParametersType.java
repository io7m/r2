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

package com.io7m.r2.core.shaders.provided;

import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureDefaultsType;
import org.immutables.value.Value;

/**
 * The type of parameters for the basic depth shader.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2DepthShaderBasicParametersType
  extends R2DepthShaderBasicParametersValuesType
{
  @Override
  @Value.Parameter
  R2TextureDefaultsType textureDefaults();

  @Override
  @Value.Default
  @Value.Parameter
  default R2Texture2DUsableType albedoTexture()
  {
    return R2DepthShaderBasicParametersValuesType.super.albedoTexture();
  }

  @Override
  @Value.Default
  @Value.Parameter
  default double alphaDiscardThreshold()
  {
    return R2DepthShaderBasicParametersValuesType.super.alphaDiscardThreshold();
  }
}
