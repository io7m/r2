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

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.r2.core.R2ExceptionBatchIsFull;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2InstanceBatchedDynamic;
import com.io7m.r2.core.R2InstanceBatchedDynamicType;
import com.io7m.r2.core.R2TransformT;
import com.io7m.r2.core.R2UnitQuad;
import com.io7m.r2.core.R2UnitQuadType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public abstract class R2InstanceBatchedDynamicContract extends R2JCGLContract
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test
  public void testTooMany()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g33 = c.contextGetGL33();
    final R2IDPoolType id_pool = R2IDPool.newPool();
    final R2UnitQuadType quad = R2UnitQuad.newUnitQuad(g33);

    final R2InstanceBatchedDynamicType i =
      R2InstanceBatchedDynamic.create(
        id_pool,
        g33.arrayBuffers(),
        g33.arrayObjects(),
        quad.arrayObject(),
        8);

    for (int index = 0; index < 8; ++index) {
      i.enableInstance(R2TransformT.create());
    }

    this.expected.expect(R2ExceptionBatchIsFull.class);
    i.enableInstance(R2TransformT.create());
  }
}
