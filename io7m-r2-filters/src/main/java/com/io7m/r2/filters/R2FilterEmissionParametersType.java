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

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2Texture2DUsableType;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * The type of parameters for emission filters.
 */

@Value.Immutable
@Value.Modifiable
@R2ImmutableStyleType
public interface R2FilterEmissionParametersType
{
  /**
   * @return The framebuffer to which the output will be written. If no
   * framebuffer is specified, the output will go to the default framebuffer.
   */

  Optional<JCGLFramebufferUsableType> outputFramebuffer();

  /**
   * @return The viewport for the output framebuffer
   */

  @Value.Parameter
  AreaInclusiveUnsignedLType outputViewport();

  /**
   * @return A texture with the surface albedo in the {@code (r, g, b)}
   * components and the emission level in the alpha component.
   */

  @Value.Parameter
  R2Texture2DUsableType albedoEmissionMap();

  /**
   * If blurring is to be used, the value returned here specifies the scale at
   * which the intermediate emissive step is rendered. If blurring is not going
   * to be used, the emission image is simply blended into the existing
   * framebuffer without any intermediate framebuffers, and therefore the scale
   * value given here is ignored.
   *
   * @return The scale to use for unblurred intermediate framebuffers
   */

  @Value.Parameter
  @Value.Default
  default float scale()
  {
    return 0.5f;
  }

  /**
   * @return The blur parameters, if blurring is to be used
   */

  @Value.Parameter
  Optional<R2BlurParametersType> blurParameters();
}
