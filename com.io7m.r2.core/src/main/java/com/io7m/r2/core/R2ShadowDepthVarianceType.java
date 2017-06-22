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

package com.io7m.r2.core;

import com.io7m.jfunctional.PartialBiFunctionType;
import com.io7m.r2.annotations.R2ImmutableStyleType;
import org.immutables.value.Value;

/**
 * The type of depth variance shadows.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2ShadowDepthVarianceType extends R2ShadowType
{
  @Override
  @Value.Parameter
  long shadowID();

  @Override
  @Value.Parameter
  default float minimumFactor()
  {
    return 0.2f;
  }

  /**
   * @return The shadow map description
   */

  @Value.Parameter
  R2DepthVarianceBufferDescription mapDescription();

  /**
   * <p>The amount of light bleed reduction to apply.</p>
   *
   * <p>This is a scene-dependent value that effectively darkens shadows in
   * order to eliminate "light bleeding" (where light appears to bleed through
   * occluding objects). Setting this value too high results in a loss of detail
   * for shadows.</p>
   *
   * @return The amount of light bleed reduction
   */

  @Value.Parameter
  default float lightBleedReduction()
  {
    return 0.2f;
  }

  /**
   * <p>The the minimum variance value for the shadow.</p>
   *
   * <p>The value is used to eliminate biasing issues in shadows. The default
   * value is sufficient for almost all scenes.</p>
   *
   * @return The amount of light bleed reduction
   */

  @Value.Parameter
  default float minimumVariance()
  {
    return 0.00002f;
  }

  @Override
  default <A, B, E extends Throwable> B matchShadow(
    final A context,
    final PartialBiFunctionType<A, R2ShadowDepthVarianceType, B, E> on_variance)
    throws E
  {
    return on_variance.call(context, this);
  }
}
