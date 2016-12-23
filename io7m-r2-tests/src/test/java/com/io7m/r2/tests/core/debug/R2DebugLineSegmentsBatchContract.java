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

package com.io7m.r2.tests.core.debug;

import com.io7m.jaffirm.core.PreconditionViolationException;
import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jtensors.parameterized.PVectorI3F;
import com.io7m.jtensors.parameterized.PVectorI4F;
import com.io7m.r2.core.debug.R2DebugLineSegment;
import com.io7m.r2.core.debug.R2DebugLineSegmentsBatch;
import com.io7m.r2.tests.core.R2JCGLContract;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

public abstract class R2DebugLineSegmentsBatchContract extends R2JCGLContract
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test
  public void testNoSegmentsSet()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g3 = gc.contextGetGL33();

    final R2DebugLineSegmentsBatch b = new R2DebugLineSegmentsBatch(g3);
    Assert.assertFalse(b.isDeleted());

    this.expected.expect(PreconditionViolationException.class);
    b.arrayObject();
  }

  @Test
  public void testDelete()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g3 = gc.contextGetGL33();

    final R2DebugLineSegmentsBatch b = new R2DebugLineSegmentsBatch(g3);
    Assert.assertFalse(b.isDeleted());
    b.delete(g3);
    Assert.assertTrue(b.isDeleted());
  }

  @Test
  public void testConstruct()
  {
    final JCGLContextType gc = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g3 = gc.contextGetGL33();

    final R2DebugLineSegmentsBatch b = new R2DebugLineSegmentsBatch(g3);
    Assert.assertFalse(b.isDeleted());

    final List<R2DebugLineSegment> segments = new ArrayList<>();
    b.setLineSegments(segments);

    segments.add(R2DebugLineSegment.of(
      new PVectorI3F<>(0.0f, 0.0f, 0.0f),
      new PVectorI4F<>(1.0f, 0.0f, 0.0f, 1.0f),
      new PVectorI3F<>(1.0f, 0.0f, 0.0f),
      new PVectorI4F<>(1.0f, 0.0f, 0.0f, 1.0f)
    ));

    b.setLineSegments(segments);

    segments.add(R2DebugLineSegment.of(
      new PVectorI3F<>(0.0f, 0.0f, 0.0f),
      new PVectorI4F<>(1.0f, 0.0f, 0.0f, 1.0f),
      new PVectorI3F<>(1.0f, 0.0f, 0.0f),
      new PVectorI4F<>(1.0f, 0.0f, 0.0f, 1.0f)
    ));

    b.setLineSegments(segments);

    segments.remove(0);

    b.setLineSegments(segments);

    final JCGLArrayObjectUsableType ao = b.arrayObject();

    b.delete(g3);
    Assert.assertTrue(b.isDeleted());
    Assert.assertTrue(ao.isDeleted());
  }
}
