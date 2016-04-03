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

package com.io7m.r2.core.profiling;

import com.io7m.jcanephora.core.JCGLTimerQueryType;
import com.io7m.jcanephora.core.JCGLTimerQueryUsableType;
import com.io7m.jcanephora.core.api.JCGLTimersType;
import com.io7m.jnull.NullCheck;
import it.unimi.dsi.fastutil.objects.Object2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import org.valid4j.Assertive;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * The default implementation of the {@link R2ProfilingType} interface.
 */

public final class R2Profiling implements R2ProfilingType
{
  private static final int FRAME_DELAY = 10;
  private final JCGLTimersType timers;
  private final Frame[] frames;
  private int frame_index;
  private boolean enabled;
  private boolean deleted;

  private R2Profiling(
    final JCGLTimersType in_timers)
  {
    this.timers = NullCheck.notNull(in_timers);
    this.frames = new Frame[R2Profiling.FRAME_DELAY];

    for (int index = 0; index < this.frames.length; ++index) {
      this.frames[index] = new Frame();
    }

    this.frame_index = 0;
  }

  /**
   * Construct a new profiling interface.
   *
   * @param t Access to timers
   *
   * @return A profiling interface
   */

  public static R2ProfilingType newProfiling(
    final JCGLTimersType t)
  {
    return new R2Profiling(t);
  }

  /**
   * Start rendering a frame.
   *
   * @return A new frame
   */
  @Override
  public R2ProfilingFrameType startFrame()
  {
    final Frame f = this.frames[this.frame_index];
    f.start();
    this.frame_index = (this.frame_index + 1) % this.frames.length;
    return f;
  }

  /**
   * Trim any cached internal storage.
   *
   * This is primarily useful because implementations are expected to reuse a
   * lot of data structures internally (because the graph of renderers and
   * filters being profiled rarely changes).
   */
  @Override
  public void trimContexts()
  {
    for (int index = 0; index < this.frames.length; ++index) {
      final Frame f = this.frames[index];
      f.trimRecursive();
    }

    this.frame_index = 0;
  }

  /**
   * @return {@code true} iff profiling is enabled
   *
   * @see #setEnabled(boolean)
   */
  @Override
  public boolean isEnabled()
  {
    return this.enabled;
  }

  /**
   * Enable/disable profiling.
   *
   * @param e {@code true} iff profiling should be enabled
   */
  @Override
  public void setEnabled(final boolean e)
  {
    this.enabled = e;
  }

  /**
   * @return The elapsed time of the most recently measured frame
   */

  @Override
  public R2ProfilingFrameMeasurementType getMostRecentlyMeasuredFrame()
  {
    final int shift =
      this.frames.length - 1;
    final int last_index =
      Math.abs((this.frame_index - shift) % this.frames.length);

    final Frame frame =
      this.frames[last_index];
    frame.retrieveRecursive();
    return frame;
  }

  @Override
  public int getFrameDelay()
  {
    return R2Profiling.FRAME_DELAY;
  }

  private final class Context implements R2ProfilingContextType,
    R2ProfilingFrameMeasurementType
  {
    private final String name;
    private final Object2ReferenceLinkedOpenHashMap<String, Context> children;
    private final Map<String, R2ProfilingFrameMeasurementType> children_ro;
    private Optional<JCGLTimerQueryType> timer;
    private long elapsed;
    private long elapsed_total;

    Context(final String in_name)
    {
      this.name = NullCheck.notNull(in_name);
      this.timer = Optional.empty();
      this.children = new Object2ReferenceLinkedOpenHashMap<>();
      this.children_ro = Collections.unmodifiableMap(this.children);
    }

    @Override
    public String getName()
    {
      return this.name;
    }

    @Override
    public long getElapsedTime()
    {
      return this.elapsed;
    }

    @Override
    public long getElapsedTimeTotal()
    {
      return this.elapsed_total;
    }

    @Override
    public Map<String, R2ProfilingFrameMeasurementType> getChildren()
    {
      return this.children_ro;
    }

    @Override
    public <A, E extends Exception> void iterate(
      final A context,
      final R2ProfilingFrameMeasurementProcedureType<A, E> proc)
      throws E
    {
      this.iterateOver(context, proc, 0);
    }

    private <A, E extends Exception> R2ProfilingIteration iterateOver(
      final A iter_state,
      final R2ProfilingFrameMeasurementProcedureType<A, E> proc,
      final int depth)
      throws E
    {
      final R2ProfilingIteration r = proc.apply(iter_state, depth, this);
      switch (r) {
        case CONTINUE:
          break;
        case STOP:
          return R2ProfilingIteration.STOP;
      }

      final ReferenceCollection<Context> os = this.children.values();
      final ObjectIterator<Context> o_iter = os.iterator();
      while (o_iter.hasNext()) {
        final Context c = o_iter.next();
        switch (c.iterateOver(iter_state, proc, depth + 1)) {
          case CONTINUE:
            break;
          case STOP:
            return R2ProfilingIteration.STOP;
        }
      }

      return R2ProfilingIteration.CONTINUE;
    }

    @Override
    public JCGLTimerQueryUsableType getTimer()
    {
      if (this.timer.isPresent()) {
        return this.timer.get();
      }

      final JCGLTimerQueryType t = R2Profiling.this.timers.timerQueryAllocate();
      this.timer = Optional.of(t);
      return t;
    }

    @Override
    public R2ProfilingContextType getChildContext(
      final String c_name)
    {
      NullCheck.notNull(c_name);

      if (this.children.containsKey(c_name)) {
        return this.children.get(c_name);
      }

      final Context ctx = new Context(c_name);
      this.children.put(c_name, ctx);
      return ctx;
    }

    @Override
    public boolean isEnabled()
    {
      return R2Profiling.this.enabled;
    }

    @Override
    public JCGLTimersType getTimers()
    {
      return R2Profiling.this.timers;
    }

    @Override
    public boolean hasTimer()
    {
      return this.timer.isPresent();
    }

    void trim()
    {
      if (this.timer.isPresent()) {
        final JCGLTimerQueryType t = this.timer.get();
        R2Profiling.this.timers.timerQueryDelete(t);
        this.timer = Optional.empty();
      }

      final ObjectBidirectionalIterator<String> k_iter =
        this.children.keySet().iterator();

      while (k_iter.hasNext()) {
        final String k = k_iter.next();
        final Context c = this.children.get(k);
        c.trim();
        k_iter.remove();
      }

      Assertive.ensure(this.children.isEmpty());
      Assertive.ensure(!this.timer.isPresent());
    }

    void startRecursive()
    {
      this.elapsed = 0L;
      this.elapsed_total = 0L;

      final ReferenceCollection<Context> os = this.children.values();
      final ObjectIterator<Context> o_iter = os.iterator();
      while (o_iter.hasNext()) {
        final Context c = o_iter.next();
        c.startRecursive();
      }
    }

    long retrieveRecursive()
    {
      this.elapsed = 0L;
      this.elapsed_total = 0L;

      if (this.hasTimer()) {
        final long r =
          R2Profiling.this.timers.timerQueryResultGet(this.timer.get());
        this.elapsed = r;
        this.elapsed_total = this.elapsed;
      }

      final ReferenceCollection<Context> os = this.children.values();
      final ObjectIterator<Context> o_iter = os.iterator();
      while (o_iter.hasNext()) {
        final Context c = o_iter.next();
        this.elapsed_total += c.retrieveRecursive();
      }

      return this.elapsed_total;
    }
  }

  private final class Frame implements R2ProfilingFrameType,
    R2ProfilingFrameMeasurementType
  {
    private final Object2ReferenceLinkedOpenHashMap<String, Context> children;
    private final Map<String, R2ProfilingFrameMeasurementType> children_ro;
    private long elapsed_total;

    Frame()
    {
      this.children = new Object2ReferenceLinkedOpenHashMap<>();
      this.children_ro = Collections.unmodifiableMap(this.children);
    }

    @Override
    public R2ProfilingContextType getChildContext(final String c_name)
    {
      NullCheck.notNull(c_name);

      if (this.children.containsKey(c_name)) {
        return this.children.get(c_name);
      }

      final Context ctx = new Context(c_name);
      this.children.put(c_name, ctx);
      return ctx;
    }

    void start()
    {
      final ReferenceCollection<Context> os = this.children.values();
      final ObjectIterator<Context> o_iter = os.iterator();
      while (o_iter.hasNext()) {
        final Context c = o_iter.next();
        c.startRecursive();
      }
    }

    @Override
    public String getName()
    {
      return "root";
    }

    @Override
    public long getElapsedTime()
    {
      return 0L;
    }

    @Override
    public long getElapsedTimeTotal()
    {
      return this.elapsed_total;
    }

    @Override
    public Map<String, R2ProfilingFrameMeasurementType> getChildren()
    {
      return this.children_ro;
    }

    @Override
    public <A, E extends Exception> void iterate(
      final A iter_state,
      final R2ProfilingFrameMeasurementProcedureType<A, E> proc)
      throws E
    {
      final R2ProfilingIteration r = proc.apply(iter_state, 0, this);
      switch (r) {
        case CONTINUE:
          break;
        case STOP:
          return;
      }

      final ReferenceCollection<Context> os = this.children.values();
      final ObjectIterator<Context> o_iter = os.iterator();
      while (o_iter.hasNext()) {
        final Context c = o_iter.next();
        switch (c.iterateOver(iter_state, proc, 1)) {
          case CONTINUE:
            break;
          case STOP:
            return;
        }
      }
    }

    void trimRecursive()
    {
      this.elapsed_total = 0L;

      final ReferenceCollection<Context> os = this.children.values();
      final ObjectIterator<Context> o_iter = os.iterator();
      while (o_iter.hasNext()) {
        final Context c = o_iter.next();
        c.trim();
        o_iter.remove();
      }

      this.children.trim();
      Assertive.ensure(this.children.isEmpty());
    }

    void retrieveRecursive()
    {
      this.elapsed_total = 0L;

      final ReferenceCollection<Context> os = this.children.values();
      final ObjectIterator<Context> o_iter = os.iterator();
      while (o_iter.hasNext()) {
        final Context c = o_iter.next();
        this.elapsed_total += c.retrieveRecursive();
      }
    }
  }
}
