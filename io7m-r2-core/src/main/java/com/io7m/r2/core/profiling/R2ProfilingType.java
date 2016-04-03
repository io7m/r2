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

/**
 * <p>An interface providing profiling services to components of the rendering
 * pipeline.</p>
 *
 * <p> Profiling proceeds by having the user first start a new frame with {@link
 * #startFrame()}. Then, the user typically creates a root context for the frame
 * with {@link R2ProfilingFrameType#getChildContext(String)}. Then, the created
 * context is passed to the various renderers and filters that the user is using
 * to render. The renderers and filters create new named child contexts which
 * are then used to record timing information.</p>
 */

public interface R2ProfilingType
{
  /**
   * Start rendering a frame.
   *
   * @return A new frame
   */

  R2ProfilingFrameType startFrame();

  /**
   * Trim any cached internal storage.
   *
   * This is primarily useful because implementations are expected to reuse a
   * lot of data structures internally (because the graph of renderers and
   * filters being profiled rarely changes).
   */

  void trimContexts();

  /**
   * @return {@code true} iff profiling is enabled
   *
   * @see #setEnabled(boolean)
   */

  boolean isEnabled();

  /**
   * Enable/disable profiling.
   *
   * @param e {@code true} iff profiling should be enabled
   */

  void setEnabled(boolean e);

  /**
   * @return The elapsed time of the most recently measured frame
   */

  R2ProfilingFrameMeasurementType getMostRecentlyMeasuredFrame();

  /**
   * As polling profiling timers every frame would cause excessive GPU/CPU
   * syncs, profiling implementations will typically lag behind by a given
   * number of frames to avoid causing the driver to synchronize. This method
   * returns the number of frames behind that profiling data is lagging.
   *
   * @return The number of frames behind that profiling data typically lags
   */

  int getFrameDelay();
}
