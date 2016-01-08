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

package com.io7m.r2.tests.core;

import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2InstanceType;
import com.io7m.r2.core.R2SceneStencils;
import com.io7m.r2.core.R2SceneStencilsConsumerType;
import com.io7m.r2.core.R2SceneStencilsMode;
import com.io7m.r2.core.R2SceneStencilsType;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class R2SceneStencilsTest
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2SceneStencilsTest.class);
  }

  @Test
  public void testEmpty()
  {
    final AtomicBoolean finished = new AtomicBoolean(false);
    final AtomicBoolean started = new AtomicBoolean(false);
    final R2SceneStencilsType m = R2SceneStencils.newMasks();

    Assert.assertEquals(
      R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_NEGATIVE, m.stencilsGetMode());
    Assert.assertEquals(0L, m.stencilsCount());
    m.stencilsExecute(new UnreachableConsumer()
    {
      @Override
      public void onFinish()
      {
        finished.set(true);
      }

      @Override
      public void onStart()
      {
        started.set(true);
      }
    });

    Assert.assertTrue(started.get());
    Assert.assertTrue(finished.get());
  }

  @Test
  public void testMode()
  {
    final R2SceneStencilsType m = R2SceneStencils.newMasks();

    Assert.assertEquals(
      R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_NEGATIVE, m.stencilsGetMode());
    m.stencilsSetMode(R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_POSITIVE);
    Assert.assertEquals(
      R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_POSITIVE, m.stencilsGetMode());
    m.stencilsSetMode(R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_NEGATIVE);
    Assert.assertEquals(
      R2SceneStencilsMode.STENCIL_MODE_INSTANCES_ARE_NEGATIVE, m.stencilsGetMode());
  }

  @Test
  public void testOrdering()
    throws Exception
  {
    final AtomicBoolean finished = new AtomicBoolean(false);
    final AtomicBoolean started = new AtomicBoolean(false);
    final R2SceneStencilsType m = R2SceneStencils.newMasks();
    final List<JCGLArrayObjectUsableType> aos = new ArrayList<>(2);
    final List<R2InstanceType> is = new ArrayList<>(6);

    final JCGLInterfaceGL33Type g = R2TestUtilities.getGL();

    final JCGLArrayObjectType a1 = R2TestUtilities.getArrayObject(g);
    final JCGLArrayObjectType a2 = R2TestUtilities.getArrayObject(g);

    final R2InstanceSingleType i1a1 =
      R2TestUtilities.getInstance(g, a1, 1L);
    final R2InstanceSingleType i2a1 =
      R2TestUtilities.getInstance(g, a1, 2L);
    final R2InstanceSingleType i3a1 =
      R2TestUtilities.getInstance(g, a1, 3L);

    final R2InstanceSingleType i4a2 =
      R2TestUtilities.getInstance(g, a2, 4L);
    final R2InstanceSingleType i5a2 =
      R2TestUtilities.getInstance(g, a2, 5L);
    final R2InstanceSingleType i6a2 =
      R2TestUtilities.getInstance(g, a2, 6L);

    {
      final List<R2InstanceType> ras = new ArrayList<>(6);
      ras.add(i1a1);
      ras.add(i2a1);
      ras.add(i3a1);
      ras.add(i4a2);
      ras.add(i5a2);
      ras.add(i6a2);
      Collections.shuffle(ras);

      for (final R2InstanceType i : ras) {
        m.stencilsAddSingle((R2InstanceSingleType) i);
      }
    }

    Assert.assertEquals(6L, m.stencilsCount());

    m.stencilsExecute(new R2SceneStencilsConsumerType()
    {
      @Override
      public void onStart()
      {
        started.set(true);
      }

      @Override
      public void onInstanceSingleStartArray(final R2InstanceSingleType i)
      {
        R2SceneStencilsTest.LOG.debug("start-array {}", i);
        aos.add(i.getArrayObject());
      }

      @Override
      public void onInstanceSingle(final R2InstanceSingleType i)
      {
        R2SceneStencilsTest.LOG.debug("instance {}", i);
        is.add(i);
      }

      @Override
      public void onFinish()
      {
        finished.set(true);
      }
    });

    Assert.assertEquals(6L, (long) is.size());
    Assert.assertEquals(2L, (long) aos.size());

    Assert.assertEquals(a1, aos.get(0));
    Assert.assertEquals(a2, aos.get(1));

    Assert.assertEquals(i1a1, is.get(0));
    Assert.assertEquals(i2a1, is.get(1));
    Assert.assertEquals(i3a1, is.get(2));
    Assert.assertEquals(i4a2, is.get(3));
    Assert.assertEquals(i5a2, is.get(4));
    Assert.assertEquals(i6a2, is.get(5));

    Assert.assertTrue(started.get());
    Assert.assertTrue(finished.get());

    started.set(false);
    finished.set(false);

    m.stencilsReset();

    Assert.assertEquals(0L, m.stencilsCount());

    m.stencilsExecute(new UnreachableConsumer()
    {
      @Override
      public void onFinish()
      {
        finished.set(true);
      }

      @Override
      public void onStart()
      {
        started.set(true);
      }
    });

    Assert.assertTrue(started.get());
    Assert.assertTrue(finished.get());
  }

  private static class UnreachableConsumer implements
    R2SceneStencilsConsumerType
  {
    UnreachableConsumer()
    {

    }

    @Override
    public void onStart()
    {
      throw new UnreachableCodeException();
    }

    @Override
    public void onInstanceSingleStartArray(final R2InstanceSingleType i)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public void onInstanceSingle(final R2InstanceSingleType i)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public void onFinish()
    {
      throw new UnreachableCodeException();
    }
  }
}
