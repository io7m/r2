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

import com.io7m.jcanephora.core.JCGLBlendEquation;
import com.io7m.jcanephora.core.JCGLBlendFunction;

/**
 * Parameters for blending an image in the compositor.
 */

public interface R2FilterCompositorBlendingType
{
  /**
   * @return The blend function used for the RGB components of a blended image
   *
   * @see JCGLBlendFunction
   */

  default JCGLBlendFunction blendFunctionSourceRGB()
  {
    return JCGLBlendFunction.BLEND_ONE;
  }

  /**
   * @return The blend function used for the alpha component of a blended image
   *
   * @see JCGLBlendFunction
   */

  default JCGLBlendFunction blendFunctionSourceAlpha()
  {
    return JCGLBlendFunction.BLEND_ONE;
  }

  /**
   * @return The blend function used for the RGB components of a blended image
   *
   * @see JCGLBlendFunction
   */

  default JCGLBlendFunction blendFunctionTargetRGB()
  {
    return JCGLBlendFunction.BLEND_ONE;
  }

  /**
   * @return The blend function used for the alpha component of a blended image
   *
   * @see JCGLBlendFunction
   */

  default JCGLBlendFunction blendFunctionTargetAlpha()
  {
    return JCGLBlendFunction.BLEND_ONE;
  }

  /**
   * @return The blend equation used for the RGB components of a blended image
   *
   * @see JCGLBlendEquation
   */

  default JCGLBlendEquation blendEquationRGB()
  {
    return JCGLBlendEquation.BLEND_EQUATION_ADD;
  }

  /**
   * @return The blend equation used for the alpha component of a blended image
   *
   * @see JCGLBlendEquation
   */

  default JCGLBlendEquation blendEquationAlpha()
  {
    return JCGLBlendEquation.BLEND_EQUATION_ADD;
  }
}
