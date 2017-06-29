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

package com.io7m.r2.core.api.ids;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The default implementation of the {@link R2IDPoolType} interface.
 */

@ThreadSafe
public final class R2IDPool implements R2IDPoolType
{
  private final AtomicLong next;

  private R2IDPool()
  {
    this.next = new AtomicLong(0L);
  }

  /**
   * @return A new ID pool
   */

  public static R2IDPoolType newPool()
  {
    return new R2IDPool();
  }

  @Override
  public long freshID()
  {
    return this.next.getAndUpdate(x -> {
      if (x == Long.MAX_VALUE) {
        throw new IllegalStateException("ID pool overflow!");
      }
      return x + 1L;
    });
  }
}
