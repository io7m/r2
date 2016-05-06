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
 * The type of readable blur parameters.
 */

public interface R2BlurParametersReadableType
{
  /**
   * @return The current blur size in texels
   */

  default float getBlurSize()
  {
    return 1.0f;
  }

  /**
   * @return The scale value for intermediate images
   */

  default float getBlurScale()
  {
    return 1.0f;
  }

  /**
   * @return The number of blur passes that will be used
   */

  default int getBlurPasses()
  {
    return 1;
  }

  /**
   * @return The filter that will be used when an image is scaled via
   * framebuffer blitting
   */

  default JCGLFramebufferBlitFilter getBlurScaleFilter()
  {
    return JCGLFramebufferBlitFilter.FRAMEBUFFER_BLIT_FILTER_LINEAR;
  }
}
