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

import java.util.Map;

/**
 * The measured time for a frame in a given context.
 */

public interface R2ProfilingFrameMeasurementType
{
  /**
   * @return The name of the measured context
   */

  String getName();

  /**
   * @return The time elapsed for this context
   */

  long getElapsedTime();

  /**
   * @return The time elapsed for this context, plus any children it may have
   */

  long getElapsedTimeTotal();

  /**
   * @return The children for this context
   */

  Map<String, R2ProfilingFrameMeasurementType> getChildren();

  /**
   * Iterate over this and all children of this context in depth-first order.
   *
   * @param context A contextual value
   * @param proc    A procedure that will receive each context
   * @param <A>     The type of contextual values
   * @param <E>     The type of raised exceptions
   *
   * @throws E Iff {@code proc} raises {@code E}
   */

  <A, E extends Exception> void iterate(
    A context,
    R2ProfilingFrameMeasurementProcedureType<A, E> proc)
    throws E;
}
