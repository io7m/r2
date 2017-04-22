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

import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2Texture2DUsableType;
import org.immutables.value.Value;

/**
 * Parameters for the SSAO shader.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2ShaderSSAOParametersType
{
  /**
   * The exponent that will be used to modify the generated SSAO term. Higher
   * exponents create occlusion with higher contrast.
   *
   * @return The SSAO exponent
   */

  @Value.Parameter
  @Value.Default
  default double exponent()
  {
    return 1.0;
  }

  /**
   * The eye-space radius around the sampling point that will be searched for
   * occluding surfaces. Larger radii will produce softer, more gradual ambient
   * occlusion.
   *
   * @return The SSAO exponent
   */

  @Value.Parameter
  @Value.Default
  default double sampleRadius()
  {
    return 1.0;
  }

  /**
   * The kernel that will be used for sampling. Larger kernels produce more
   * accurate occlusion terms but increase the cost of processing.
   *
   * @return The sampling kernel
   */

  @Value.Parameter
  R2SSAOKernelType kernel();

  /**
   * The noise texture that will be used to rotate the sampling hemisphere
   * randomly at each pixel.
   *
   * @return The noise texture
   */

  @Value.Parameter
  R2Texture2DUsableType noiseTexture();

  /**
   * The viewport of the ambient occlusion buffer to which occlusion terms are
   * written.
   *
   * @return The framebuffer viewport
   */

  @Value.Parameter
  AreaL viewport();

  /**
   * @return The matrices for the current view
   */

  @Value.Parameter
  R2MatricesObserverValuesType viewMatrices();

  /**
   * @return The geometry buffer that will be sampled
   */

  @Value.Parameter
  R2GeometryBufferUsableType geometryBuffer();
}
