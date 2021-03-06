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

import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.textures.R2Texture2DUsableType;
import org.immutables.value.Value;

/**
 * Parameters for the FXAA shader.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2ShaderFilterFXAAParametersType
{
  /**
   * @return The texture that will be filtered
   */

  @Value.Parameter
  R2Texture2DUsableType texture();

  /**
   * <p>The amount of subpixel aliasing removal.</p>
   *
   * <p>The range of useful values is {@code [0.0, 1.0]}, where {@code 0.0}
   * turns off filtering and {@code 1.0} gives almost excessive filtering.</p>
   *
   * @return The subpixel aliasing removal amount
   */

  @Value.Parameter
  @Value.Default
  default double subPixelAliasingRemoval()
  {
    return 0.75;
  }

  /**
   * <p>The minimum amount of local contrast required to apply the
   * algorithm.</p>
   *
   * <p>The range of useful values is {@code [0.063, 0.333]}, where {@code
   * 0.333} is fastest but lowest quality, and {@code 0.063} is overkill quality
   * and is relatively slow.</p>
   *
   * @return The edge threshold
   */

  @Value.Parameter
  @Value.Default
  default double edgeThreshold()
  {
    return 0.166;
  }

  /**
   * <p>The minimum edge brightness for which to enable filtering.</p>
   *
   * <p>The range of useful values is {@code [0.0312, 0.0833]}, where {@code
   * 0.0312} is slowest, and {@code 0.0833} is fastest.</p>
   *
   * <p>This value is somewhat scene-specific, but the default (and highest)
   * value appears to work well. The documentation for the FXAA algorithm
   * suggests looking at mostly-green rendered content (the algorithm depends on
   * luma information taken from the green channel of the filtered image),
   * setting this value at {@code 0.0}, and then increasing it until aliasing
   * artifacts appear.</p>
   *
   * @return The edge threshold minimum
   */

  @Value.Parameter
  @Value.Default
  default double edgeThresholdMinimum()
  {
    return 0.0833;
  }
}
