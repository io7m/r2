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

package com.io7m.r2.core.debug;

import com.io7m.r2.annotations.R2ImmutableStyleType;
import org.immutables.value.Value;

import java.util.List;

/**
 * Extra instances for displaying debugging data.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2DebugInstancesType
{
  /**
   * @return The set of line segments to display
   */

  @Value.Parameter
  List<R2DebugLineSegment> lineSegments();

  /**
   * @return The set of debug cubes to display
   */

  @Value.Parameter
  List<R2DebugCubeInstance> cubes();

  /**
   * @return The set of single instances to display
   */

  @Value.Parameter
  List<R2DebugInstanceSingle> instanceSingles();
}
