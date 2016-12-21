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

import com.io7m.jtensors.parameterized.PVectorI3F;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * <p>The type of billboarded instances that are expected to change often, both
 * in the number of instances, and the values of individual instances.</p>
 */

public interface R2InstanceBillboardedDynamicType
  extends R2InstanceBillboardedType, R2DeletableType
{
  /**
   * @return The maximum number of instances that can be placed into the set
   */

  int maximumSize();

  /**
   * Clear all instances.
   */

  void clear();

  /**
   * Enable rendering of an instance with transform {@code t}
   *
   * @param position The world-space position
   * @param scale    The scale
   * @param rotation The rotation
   *
   * @return The index of the enabled instance
   *
   * @throws R2ExceptionBatchIsFull If the batch cannot accept any more
   *                                instances
   */

  int addInstance(
    PVectorI3F<R2SpaceWorldType> position,
    float scale,
    float rotation)
    throws R2ExceptionBatchIsFull;
}
