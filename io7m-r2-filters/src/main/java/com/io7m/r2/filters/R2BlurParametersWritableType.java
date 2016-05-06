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

/**
 * The type of writable blur parameters.
 */

public interface R2BlurParametersWritableType
{
  /**
   * Set the blur size in texels.
   *
   * @param s The size
   */

  void setBlurSize(float s);

  /**
   * Set the amount by which the image will be scaled during the blur operation.
   * By scaling an image down, blurring it, and then scaling it back up again,
   * the blur effect is emphasized without requiring additional passes.
   *
   * @param s The scale amount
   */

  void setBlurScale(float s);

  /**
   * Set the number of blur passes that will be used. If a value of {@code 0} is
   * given here, the image will only be scaled and not actually blurred (and
   * will not actually even be scaled, if a value of {@code 1.0} is given for
   * {@link #setBlurScale(float)}).
   *
   * @param p The number of blur passes
   */

  void setBlurPasses(int p);

  /**
   * Set the filter that will be used when an image is resized using framebuffer
   * blitting.
   *
   * @param f The filter
   */

  void setBlurScaleFilter(JCGLFramebufferBlitFilter f);
}
