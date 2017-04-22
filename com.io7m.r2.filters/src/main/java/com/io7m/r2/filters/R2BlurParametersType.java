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

package com.io7m.r2.filters;

import com.io7m.jcanephora.core.JCGLFramebufferBlitFilter;
import com.io7m.r2.core.R2ImmutableStyleType;
import org.immutables.value.Value;

/**
 * The type of blur parameters.
 */

@R2ImmutableStyleType
@Value.Immutable
public interface R2BlurParametersType
{
  /**
   * @return The current blur size in texels
   */

  @Value.Parameter
  @Value.Default
  default double blurSize()
  {
    return 1.0;
  }

  /**
   * The amount by which the image will be scaled during the blur operation.
   * By scaling an image down, blurring it, and then scaling it back up again,
   * the blur effect is emphasized without requiring additional passes.
   *
   * @return The scale value for intermediate images
   */

  @Value.Parameter
  @Value.Default
  default double blurScale()
  {
    return 1.0;
  }

  /**
   * The number of blur passes that will be used. If a value of {@code 0} is
   * given here, the image will only be scaled and not actually blurred (and
   * will not actually even be scaled, if a value of {@code 1.0} is given for
   * {@link #blurScale()}).
   *
   * @return The number of blur passes that will be used
   */

  @Value.Parameter
  @Value.Default
  default int blurPasses()
  {
    return 1;
  }

  /**
   * @return The filter that will be used when an image is scaled via
   * framebuffer blitting
   */

  @Value.Parameter
  @Value.Default
  default JCGLFramebufferBlitFilter blurScaleFilter()
  {
    return JCGLFramebufferBlitFilter.FRAMEBUFFER_BLIT_FILTER_LINEAR;
  }
}
