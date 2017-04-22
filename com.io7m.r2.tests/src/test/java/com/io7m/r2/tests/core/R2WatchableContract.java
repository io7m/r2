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

package com.io7m.r2.tests.core;

import com.io7m.jfunctional.Unit;
import com.io7m.r2.core.R2WatchableType;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public abstract class R2WatchableContract
{
  protected abstract <T> R2WatchableType<T> getWatchable(T value);

  @Test
  public final void testAddRemove()
  {
    final AtomicInteger called = new AtomicInteger(0);
    final R2WatchableType<Unit> w = this.getWatchable(Unit.unit());

    final Consumer<Unit> c = x -> called.incrementAndGet();
    w.watchableAdd(c);
    Assert.assertEquals(1L, (long) called.get());
    w.watchableChanged();
    Assert.assertEquals(2L, (long) called.get());
    w.watchableRemove(c);
    Assert.assertEquals(2L, (long) called.get());
    w.watchableChanged();
    Assert.assertEquals(2L, (long) called.get());
    w.watchableAdd(c);
    Assert.assertEquals(3L, (long) called.get());
    w.watchableChanged();
    Assert.assertEquals(4L, (long) called.get());
  }
}
