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

import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/**
 * The default implementation of the {@link R2WatchableType} interface.
 *
 * @param <T> The type of watched values
 */

public final class R2Watchable<T> implements R2WatchableType<T>
{
  private final Map<Consumer<T>, Unit> watchers;
  private final T                      value;

  private R2Watchable(final T in_value)
  {
    this.value = NullCheck.notNull(in_value);
    this.watchers = new WeakHashMap<>(16);
  }

  /**
   * Create a new watchable with the given initial value.
   *
   * @param initial The value
   * @param <T>     The type of value
   *
   * @return A new watchable
   */

  public static <T> R2WatchableType<T> newWatchable(final T initial)
  {
    return new R2Watchable<>(initial);
  }

  private void update()
  {
    final Iterator<Consumer<T>> iter = this.watchers.keySet().iterator();
    while (iter.hasNext()) {
      final Consumer<T> c = iter.next();
      c.accept(this.value);
    }
  }

  @Override
  public void watchableAdd(final Consumer<T> w)
  {
    NullCheck.notNull(w);
    this.watchers.put(w, Unit.unit());
    w.accept(this.value);
  }

  @Override
  public void watchableRemove(final Consumer<T> w)
  {
    NullCheck.notNull(w);
    this.watchers.remove(w, Unit.unit());
  }

  @Override
  public void watchableChanged()
  {
    this.update();
  }
}
