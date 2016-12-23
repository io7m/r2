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

import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

public final class R2IDPoolTest
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test
  public void testInitial()
  {
    final R2IDPoolType p = R2IDPool.newPool();
    Assert.assertEquals(0L, p.freshID());
    Assert.assertEquals(1L, p.freshID());
    Assert.assertEquals(2L, p.freshID());
  }

  @Test
  public void testOverflow()
    throws Exception
  {
    final R2IDPoolType p = R2IDPool.newPool();

    final Field af = R2IDPool.class.getDeclaredField("next");
    af.setAccessible(true);
    af.set(p, new AtomicLong(Long.MAX_VALUE));

    this.expected.expect(IllegalStateException.class);
    p.freshID();
  }
}
