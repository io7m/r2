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

package com.io7m.r2.core.shaders;

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.filters.R2SSAOKernelType;

/**
 * The parameters used for SSAO shaders.
 *
 * @see R2ShaderSSAO
 */

public final class R2ShaderSSAOParameters
{
  /**
   * The default occlusion power.
   *
   * @see #setPower(float)
   */

  public static final float DEFAULT_POWER = 1.0f;

  /**
   * The default sample radius, in eye-space units.
   *
   * @see #setSampleRadius(float)
   */

  public static final float DEFAULT_SAMPLE_RADIUS = 0.25f;

  private float                      power;
  private R2Texture2DUsableType      noise;
  private R2SSAOKernelType           kernel;
  private AreaInclusiveUnsignedLType viewport;
  private float                      sample_radius;

  private R2ShaderSSAOParameters(
    final R2SSAOKernelType in_kernel,
    final R2Texture2DUsableType in_noise,
    final AreaInclusiveUnsignedLType in_viewport)
  {
    this.kernel = NullCheck.notNull(in_kernel);
    this.noise = NullCheck.notNull(in_noise);
    this.sample_radius = R2ShaderSSAOParameters.DEFAULT_SAMPLE_RADIUS;
    this.power = R2ShaderSSAOParameters.DEFAULT_POWER;
  }

  /**
   * Construct new parameters.
   *
   * @param in_kernel   An SSAO kernel
   * @param in_noise    The noise texture
   * @param in_viewport The viewport for the occlusion buffer
   *
   * @return A new set of parameters
   */

  public static R2ShaderSSAOParameters newParameters(
    final R2SSAOKernelType in_kernel,
    final R2Texture2DUsableType in_noise,
    final AreaInclusiveUnsignedLType in_viewport)
  {
    return new R2ShaderSSAOParameters(in_kernel, in_noise, in_viewport);
  }

  /**
   * @return The occlusion exponent
   *
   * @see #setPower(float)
   */

  public float getPower()
  {
    return this.power;
  }

  /**
   * Set the occlusion exponent. Higher values will have the effect of darkening
   * and raising the contrast of the calculated occlusion term.
   *
   * @param p The occlusion exponent
   */

  public void setPower(final float p)
  {
    this.power = p;
  }

  /**
   * @return The current sample radius, in eye-space units
   *
   * @see #setSampleRadius(float)
   */

  public float getSampleRadius()
  {
    return this.sample_radius;
  }

  /**
   * Set the maximum radius, in eye-space units, that will be sampled for
   * occlusion around each surface point.
   *
   * @param s The maximum sample radius
   */

  public void setSampleRadius(final float s)
  {
    this.sample_radius = s;
  }

  /**
   * @return The current sampling kernel
   */

  public R2SSAOKernelType getKernel()
  {
    return this.kernel;
  }

  /**
   * Set the sampling kernel
   *
   * @param k The kernel
   */

  public void setKernel(
    final R2SSAOKernelType k)
  {
    this.kernel = NullCheck.notNull(k);
  }

  /**
   * @return The current noise texture
   */

  public R2Texture2DUsableType getNoiseTexture()
  {
    return this.noise;
  }

  /**
   * Set the noise texture that will be used to peturb sample vectors
   *
   * @param n The noise texture
   */

  public void setNoiseTexture(final R2Texture2DUsableType n)
  {
    this.noise = NullCheck.notNull(n);
  }

  /**
   * @return The current viewport for the ambient occlusion buffer
   */

  public AreaInclusiveUnsignedLType getViewport()
  {
    return this.viewport;
  }

  /**
   * Set the viewport size for the ambient occlusion buffer
   *
   * @param v The viewport
   */

  public void setViewport(
    final AreaInclusiveUnsignedLType v)
  {
    this.viewport = NullCheck.notNull(v);
  }
}
