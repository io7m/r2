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

package com.io7m.r2.tests.core.profiling;

import com.io7m.jcanephora.core.JCGLClearSpecification;
import com.io7m.jcanephora.core.api.JCGLClearType;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jfunctional.Unit;
import com.io7m.jtensors.VectorI4F;
import com.io7m.r2.core.profiling.R2ProfilingContextType;
import com.io7m.r2.core.profiling.R2ProfilingFrameMeasurementType;
import com.io7m.r2.core.profiling.R2ProfilingFrameType;
import com.io7m.r2.core.profiling.R2ProfilingIteration;
import com.io7m.r2.core.profiling.R2ProfilingType;
import com.io7m.r2.tests.core.R2JCGLContract;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class R2ProfilingContract extends R2JCGLContract
{
  @Test
  public final void testInit()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ProfilingType p = this.newProfiling(g);

    Assert.assertFalse(p.isEnabled());

    final R2ProfilingFrameMeasurementType f = p.getMostRecentlyMeasuredFrame();
    Assert.assertTrue(f.getChildren().isEmpty());
  }

  @Test
  public final void testContextReuse()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ProfilingType p = this.newProfiling(g);

    Assert.assertFalse(p.isEnabled());

    final R2ProfilingFrameType f = p.startFrame();
    final R2ProfilingContextType c0_0 = f.getChildContext("c0");
    final R2ProfilingContextType c0_1 = f.getChildContext("c0");
    Assert.assertSame(c0_0, c0_1);
  }

  @Test
  public final void testTiming()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ProfilingType p = this.newProfiling(g);

    Assert.assertFalse(p.isEnabled());
    p.setEnabled(true);
    Assert.assertTrue(p.isEnabled());

    for (int frame = 0; frame < p.getFrameDelay(); ++frame) {
      final R2ProfilingFrameType f = p.startFrame();
      final R2ProfilingContextType c0 = f.getChildContext("c0");
      Assert.assertEquals("c0", c0.getName());
      Assert.assertFalse(c0.hasTimer());
      Assert.assertTrue(c0.isEnabled());

      c0.startMeasuringIfEnabled();
      {
        final JCGLClearType gc = g.getClear();
        final JCGLClearSpecification cs =
          JCGLClearSpecification.of(
            Optional.of(new VectorI4F(1.0f, 1.0f, 1.0f, 1.0f)),
            OptionalDouble.empty(),
            OptionalInt.empty(),
            false);
        for (int index = 0; index < 10; ++index) {
          gc.clear(cs);
        }
      }
      c0.stopMeasuringIfEnabled();
    }

    final R2ProfilingFrameMeasurementType fm_root =
      p.getMostRecentlyMeasuredFrame();
    Assert.assertTrue(fm_root.getElapsedTimeTotal() > 0L);
    Assert.assertEquals(fm_root.getElapsedTime(), 0L);

    final Map<String, R2ProfilingFrameMeasurementType> fmc_root =
      fm_root.getChildren();
    Assert.assertEquals(1L, (long) fmc_root.size());
    Assert.assertTrue(fmc_root.containsKey("c0"));

    {
      final R2ProfilingFrameMeasurementType fm_c0 = fmc_root.get("c0");
      Assert.assertEquals(
        fm_c0.getElapsedTimeTotal(),
        fm_root.getElapsedTimeTotal());
      Assert.assertEquals(
        fm_c0.getElapsedTime(),
        fm_root.getElapsedTimeTotal());

      final Map<String, R2ProfilingFrameMeasurementType> fm_cc =
        fm_c0.getChildren();
      Assert.assertTrue(fm_cc.isEmpty());
    }
  }

  @Test
  public final void testIterate()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ProfilingType p = this.newProfiling(g);

    Assert.assertFalse(p.isEnabled());
    p.setEnabled(true);
    Assert.assertTrue(p.isEnabled());

    for (int frame = 0; frame < p.getFrameDelay(); ++frame) {
      final R2ProfilingFrameType f = p.startFrame();
      final R2ProfilingContextType c0 = f.getChildContext("c0");
      final R2ProfilingContextType c1 = c0.getChildContext("c1");
      final R2ProfilingContextType c2 = c0.getChildContext("c2");
      final R2ProfilingContextType c3 = c0.getChildContext("c3");
    }

    final R2ProfilingFrameMeasurementType fm =
      p.getMostRecentlyMeasuredFrame();

    final HashSet<String> seen = new HashSet<>();

    final AtomicInteger ai = new AtomicInteger(0);
    fm.iterate(ai, (ii, depth, cx) -> {
      ii.incrementAndGet();
      seen.add(cx.getName());
      return R2ProfilingIteration.CONTINUE;
    });

    Assert.assertEquals(5, ai.get());
    Assert.assertEquals(5, seen.size());
    Assert.assertTrue(seen.contains("root"));
    Assert.assertTrue(seen.contains("c0"));
    Assert.assertTrue(seen.contains("c1"));
    Assert.assertTrue(seen.contains("c2"));
    Assert.assertTrue(seen.contains("c3"));
  }

  @Test
  public final void testIterateStop_0()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ProfilingType p = this.newProfiling(g);

    Assert.assertFalse(p.isEnabled());
    p.setEnabled(true);
    Assert.assertTrue(p.isEnabled());

    for (int frame = 0; frame < p.getFrameDelay(); ++frame) {
      final R2ProfilingFrameType f = p.startFrame();
      final R2ProfilingContextType c0 = f.getChildContext("c0");
      final R2ProfilingContextType c1 = c0.getChildContext("c1");
      final R2ProfilingContextType c2 = c0.getChildContext("c2");
      final R2ProfilingContextType c3 = c0.getChildContext("c3");
    }

    final R2ProfilingFrameMeasurementType fm =
      p.getMostRecentlyMeasuredFrame();

    final HashSet<String> seen = new HashSet<>();

    final AtomicInteger ai = new AtomicInteger(0);
    fm.iterate(ai, (ii, depth, cx) -> {
      ii.incrementAndGet();
      seen.add(cx.getName());
      return R2ProfilingIteration.STOP;
    });

    Assert.assertEquals(1, ai.get());
    Assert.assertEquals(1, seen.size());
    Assert.assertTrue(seen.contains("root"));
  }

  @Test
  public final void testIterateStop_1()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ProfilingType p = this.newProfiling(g);

    Assert.assertFalse(p.isEnabled());
    p.setEnabled(true);
    Assert.assertTrue(p.isEnabled());

    for (int frame = 0; frame < p.getFrameDelay(); ++frame) {
      final R2ProfilingFrameType f = p.startFrame();
      final R2ProfilingContextType c0 = f.getChildContext("c0");
      final R2ProfilingContextType c1 = c0.getChildContext("c1");
      final R2ProfilingContextType c2 = c0.getChildContext("c2");
      final R2ProfilingContextType c3 = c0.getChildContext("c3");
    }

    final R2ProfilingFrameMeasurementType fm =
      p.getMostRecentlyMeasuredFrame();

    final HashSet<String> seen = new HashSet<>();

    final AtomicInteger ai = new AtomicInteger(0);
    fm.iterate(ai, (ii, depth, cx) -> {
      ii.incrementAndGet();

      if ("c1".equals(cx.getName())) {
        return R2ProfilingIteration.STOP;
      }

      seen.add(cx.getName());
      return R2ProfilingIteration.CONTINUE;
    });

    Assert.assertEquals(3, ai.get());
    Assert.assertEquals(2, seen.size());
    Assert.assertFalse(seen.contains("c1"));
  }


  @Test
  public final void testContextTrim()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ProfilingType p = this.newProfiling(g);

    Assert.assertFalse(p.isEnabled());
    p.setEnabled(true);
    Assert.assertTrue(p.isEnabled());

    final IdentityHashMap<R2ProfilingContextType, Unit> seen =
      new IdentityHashMap<>();

    /**
     * Make sure there are a decent number of cached contexts.
     */

    for (int frame = 0; frame < p.getFrameDelay(); ++frame) {
      final R2ProfilingFrameType f = p.startFrame();
      final R2ProfilingContextType c0 = f.getChildContext("c0");

      Assert.assertEquals("c0", c0.getName());
      Assert.assertFalse(c0.hasTimer());
      Assert.assertTrue(c0.isEnabled());

      c0.startMeasuringIfEnabled();
      c0.stopMeasuringIfEnabled();
      Assert.assertTrue(c0.hasTimer());

      final R2ProfilingContextType c1 = c0.getChildContext("c1");
      c1.startMeasuringIfEnabled();
      c1.stopMeasuringIfEnabled();
      Assert.assertTrue(c1.hasTimer());

      final R2ProfilingContextType c2 = c0.getChildContext("c2");
      c2.startMeasuringIfEnabled();
      c2.stopMeasuringIfEnabled();
      Assert.assertTrue(c2.hasTimer());

      final R2ProfilingContextType c3 = c0.getChildContext("c3");

      seen.put(c0, Unit.unit());
      seen.put(c1, Unit.unit());
      seen.put(c2, Unit.unit());
      seen.put(c3, Unit.unit());
    }

    /**
     * Check that contexts are reused by default.
     */

    Assert.assertEquals(
      (long) (p.getFrameDelay() * 4), (long) seen.size());

    for (int frame = 0; frame < p.getFrameDelay() * 2; ++frame) {
      final R2ProfilingFrameType f = p.startFrame();

      final R2ProfilingContextType c0 = f.getChildContext("c0");
      final R2ProfilingContextType c1 = c0.getChildContext("c1");
      final R2ProfilingContextType c2 = c0.getChildContext("c2");
      final R2ProfilingContextType c3 = c0.getChildContext("c3");

      Assert.assertTrue(seen.containsKey(c0));
      Assert.assertTrue(seen.containsKey(c1));
      Assert.assertTrue(seen.containsKey(c2));
      Assert.assertTrue(seen.containsKey(c3));
    }

    /**
     * Check that trimming forces the creation of new contexts.
     */

    p.trimContexts();

    for (int frame = 0; frame < p.getFrameDelay() * 2; ++frame) {
      final R2ProfilingFrameType f = p.startFrame();
      final R2ProfilingContextType c0 = f.getChildContext("c0");
      Assert.assertEquals("c0", c0.getName());
      Assert.assertFalse(c0.hasTimer());
      Assert.assertTrue(c0.isEnabled());

      final R2ProfilingContextType c1 = c0.getChildContext("c1");
      Assert.assertEquals("c1", c1.getName());
      Assert.assertFalse(c1.hasTimer());
      Assert.assertTrue(c1.isEnabled());

      final R2ProfilingContextType c2 = c0.getChildContext("c2");
      Assert.assertEquals("c2", c2.getName());
      Assert.assertFalse(c2.hasTimer());
      Assert.assertTrue(c2.isEnabled());

      final R2ProfilingContextType c3 = c0.getChildContext("c3");
      Assert.assertEquals("c3", c3.getName());
      Assert.assertFalse(c3.hasTimer());
      Assert.assertTrue(c3.isEnabled());

      Assert.assertFalse(seen.containsKey(c0));
      Assert.assertFalse(seen.containsKey(c1));
      Assert.assertFalse(seen.containsKey(c2));
      Assert.assertFalse(seen.containsKey(c3));
    }
  }

  protected abstract R2ProfilingType newProfiling(JCGLInterfaceGL33Type g);
}
