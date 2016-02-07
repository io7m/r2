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

package com.io7m.r2.core.filters;

import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2LightBufferUsableType;

/**
 * The parameters used for SSAO.
 *
 * @see R2FilterSSAOType
 */

public final class R2FilterLightApplicatorParameters
{
  private R2GeometryBufferUsableType gbuffer;
  private R2LightBufferUsableType    lbuffer;

  private R2FilterLightApplicatorParameters(
    final R2GeometryBufferUsableType in_gbuffer,
    final R2LightBufferUsableType in_lbuffer)
  {
    this.gbuffer = NullCheck.notNull(in_gbuffer);
    this.lbuffer = NullCheck.notNull(in_lbuffer);
  }

  /**
   * Construct new light applicator parameters.
   *
   * @param in_gbuffer The G-Buffer
   * @param in_lbuffer The light accumulation buffer
   *
   * @return A new set of parameters
   */

  public static R2FilterLightApplicatorParameters newParameters(
    final R2GeometryBufferUsableType in_gbuffer,
    final R2LightBufferUsableType in_lbuffer)
  {
    return new R2FilterLightApplicatorParameters(in_gbuffer, in_lbuffer);
  }

  /**
   * @return The geometry buffer
   */

  public R2GeometryBufferUsableType getGBuffer()
  {
    return this.gbuffer;
  }

  /**
   * Set the geometry buffer.
   *
   * @param b The buffer
   */

  public void setGBuffer(final R2GeometryBufferUsableType b)
  {
    this.gbuffer = NullCheck.notNull(b);
  }

  /**
   * @return The light accumulation buffer
   */

  public R2LightBufferUsableType getLightBuffer()
  {
    return this.lbuffer;
  }

  /**
   * Set the light accumulation buffer.
   *
   * @param b The buffer
   */

  public void setLightBuffer(final R2LightBufferUsableType b)
  {
    this.lbuffer = NullCheck.notNull(b);
  }
}
