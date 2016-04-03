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
 * A procedure used to iterate over measured contexts.
 *
 * @param <A>
 * @param <E>
 */

public interface R2ProfilingFrameMeasurementProcedureType<A, E extends Exception>
{
  /**
   * Receive the measured context.
   *
   * @param context A contextual value
   * @param depth   The depth of the context
   * @param c       The context
   *
   * @return {@link R2ProfilingIteration#CONTINUE} iff iteration should continue
   *
   * @throws E If required
   */

  R2ProfilingIteration apply(
    A context,
    int depth,
    R2ProfilingFrameMeasurementType c)
    throws E;
}
