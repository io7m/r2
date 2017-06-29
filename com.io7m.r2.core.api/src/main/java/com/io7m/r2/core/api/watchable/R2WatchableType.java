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

package com.io7m.r2.core.api.watchable;

import java.util.function.Consumer;

/**
 * <p>A watchable value.</p>
 *
 * <p>This is very similar to {@link java.util.Observable} except that it is
 * strongly typed and implementations are required to use weak references to
 * observers so that the observers do not prevent the observed values from being
 * garbage collected.</p>
 *
 * @param <T> The precise type of value
 */

public interface R2WatchableType<T>
{
  /**
   * Add a watcher. The watcher will be notified every time {@link
   * #watchableChanged()} is called.
   *
   * @param w The watcher
   */

  void watchableAdd(Consumer<T> w);

  /**
   * Remove a watcher.
   *
   * @param w The watcher
   */

  void watchableRemove(Consumer<T> w);

  /**
   * Notify all watchers that the value has changed.
   */

  void watchableChanged();
}
