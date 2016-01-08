/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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
 * <p>A consumer for stencil instances.</p>
 *
 * <p>Instances are batched in a manner that is intended to require the minimum
 * number of state changes for rendering.</p>
 */

public interface R2SceneStencilsConsumerType
{
  /**
   * Called when rendering of stencil instances begins.
   */

  void onStart();

  /**
   * Called when a new array object should be bound.
   *
   * @param i The current instance
   */

  void onInstanceSingleStartArray(R2InstanceSingleType i);

  /**
   * Called when a single instance should be rendered.
   *
   * @param i The current instance
   */

  void onInstanceSingle(R2InstanceSingleType i);

  /**
   * Called when rendering of stencil instances is finished.
   */

  void onFinish();
}
