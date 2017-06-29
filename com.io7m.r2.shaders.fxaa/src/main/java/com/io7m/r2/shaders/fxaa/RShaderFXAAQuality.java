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

package com.io7m.r2.shaders.fxaa;

/**
 * A specification of which FXAA preset to use, based on the desired quality
 * level.
 */

public enum RShaderFXAAQuality
{
  /**
   * Medium dithering, lowest quality, fastest possible processing.
   */

  R2_FXAA_QUALITY_10(10),

  /**
   * Medium dithering, better quality than {@link #R2_FXAA_QUALITY_10}, fast
   * processing.
   */

  R2_FXAA_QUALITY_15(15),

  /**
   * Low dithering, fastest/lowest quality.
   */

  R2_FXAA_QUALITY_20(20),

  /**
   * Low dithering, better quality than {@link #R2_FXAA_QUALITY_20}.
   */

  R2_FXAA_QUALITY_25(25),

  /**
   * Low dithering, better quality than {@link #R2_FXAA_QUALITY_25}.
   */

  R2_FXAA_QUALITY_29(29),

  /**
   * No dithering, expensive processing.
   */

  R2_FXAA_QUALITY_39(39);

  private final int preset;

  RShaderFXAAQuality(final int p)
  {
    this.preset = p;
  }

  /**
   * @return The FXAA preset number
   */

  public int getPreset()
  {
    return this.preset;
  }
}
