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

/**
 * The type of batched instances that are expected to change often, both in the
 * number of instances, and the transforms of individual instances.
 */

public interface R2InstanceBatchedDynamicType extends R2InstanceBatchedType,
  R2DeletableType
{
  /**
   * @return The maximum number of instances that can be placed into the batch
   */

  int getMaximumSize();

  /**
   * @return The current number of enabled instances in the batch
   */

  int getEnabledCount();

  /**
   * Disable rendering of all instances
   */

  void disableAll();

  /**
   * Enable rendering of an instance with transform {@code t}
   *
   * @param t The transform for the instance
   *
   * @return The index of the enabled instance
   *
   * @throws R2ExceptionBatchIsFull If the batch cannot accept any more
   *                                instances
   */

  int enableInstance(R2TransformOrthogonalReadableType t)
    throws R2ExceptionBatchIsFull;

  /**
   * Disable rendering of the given instance.
   *
   * @param id The index of the instance
   */

  void disableInstance(int id);
}
