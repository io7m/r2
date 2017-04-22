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

package com.io7m.r2.tests;

import org.junit.Test;

public final class R2LinearStepTest
{
  private static float clamp(
    final float x,
    final float min,
    final float max)
  {
    return Math.max(Math.min(x, max), min);
  }

  private static float linearStep(
    final float min,
    final float max,
    final float x)
  {
    final float vsm = x - min;
    final float msm = max - min;
    return R2LinearStepTest.clamp(vsm / msm, 0.0f, 1.0f);
  }

  @Test
  public void testLinearStep()
  {
    for (float index = -10.0f; index < 10.0f; index += 0.1f) {
      System.out.println(index + " -> " + R2LinearStepTest.linearStep(
        0.0f,
        2.0f,
        index));
    }
  }
}
