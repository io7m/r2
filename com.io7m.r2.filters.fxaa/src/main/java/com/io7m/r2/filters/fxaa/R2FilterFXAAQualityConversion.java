/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.filters.fxaa;

import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.filters.fxaa.api.R2FilterFXAAQuality;
import com.io7m.r2.shaders.fxaa.RShaderFXAAQuality;

/**
 * Quality conversion functions.
 */

public final class R2FilterFXAAQualityConversion
{
  private R2FilterFXAAQualityConversion()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Convert the given filter quality to a shader quality value.
   * @param c The quality value
   * @return A shader quality value
   */

  public static RShaderFXAAQuality shaderQuality(
    final R2FilterFXAAQuality c)
  {
    switch (c) {
      case R2_FXAA_QUALITY_10:
        return RShaderFXAAQuality.R2_FXAA_QUALITY_10;
      case R2_FXAA_QUALITY_15:
        return RShaderFXAAQuality.R2_FXAA_QUALITY_15;
      case R2_FXAA_QUALITY_20:
        return RShaderFXAAQuality.R2_FXAA_QUALITY_20;
      case R2_FXAA_QUALITY_25:
        return RShaderFXAAQuality.R2_FXAA_QUALITY_25;
      case R2_FXAA_QUALITY_29:
        return RShaderFXAAQuality.R2_FXAA_QUALITY_29;
      case R2_FXAA_QUALITY_39:
        return RShaderFXAAQuality.R2_FXAA_QUALITY_39;
    }

    throw new UnreachableCodeException();
  }
}
