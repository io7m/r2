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

package com.io7m.r2.core.shaders.types;

import com.io7m.junreachable.UnreachableCodeException;

import java.util.Objects;

/**
 * Functions for implementing shader verifiers.
 */

public final class R2ShaderVerifiers
{
  private R2ShaderVerifiers()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Check that {@code expected == actual}.
   *
   * @param text     A buffer used to construct error messages
   * @param name     The program name
   * @param expected The expected program state
   * @param actual   The actual program state
   * @param <T>      The precise type of program states
   */

  public static <T extends Enum<T>> void checkState(
    final StringBuilder text,
    final String name,
    final T expected,
    final T actual)
  {
    if (!Objects.equals(expected, actual)) {
      text.setLength(0);
      text.append("Failed to call shader correctly.");
      text.append(System.lineSeparator());
      text.append("Program:               ");
      text.append(name);
      text.append(System.lineSeparator());
      text.append("Expected shader state: ");
      text.append(expected);
      text.append(System.lineSeparator());
      text.append("Actual shader state:   ");
      text.append(actual);
      text.append(System.lineSeparator());
      final String m = text.toString();
      text.setLength(0);
      throw new IllegalStateException(m);
    }
  }

  /**
   * Check that {@code ∃i. expected[i] == actual}.
   *
   * @param text     A buffer used to construct error messages
   * @param name     The program name
   * @param expected The expected program state
   * @param actual   The actual program state
   * @param <T>      The precise type of program states
   */

  public static <T extends Enum<T>> void checkStates(
    final StringBuilder text,
    final String name,
    final T[] expected,
    final T actual)
  {
    boolean ok = false;
    for (int index = 0; index < expected.length; ++index) {
      final T s = expected[index];
      if (Objects.equals(s, actual)) {
        ok = true;
        break;
      }
    }

    if (!ok) {
      text.setLength(0);
      text.append("Failed to call shader correctly.");
      text.append(System.lineSeparator());
      text.append("Program:                       ");
      text.append(name);
      text.append(System.lineSeparator());
      text.append("Expected one of shader states: ");
      for (int index = 0; index < expected.length; ++index) {
        final T s = expected[index];
        text.append(s);
        text.append(" ");
      }
      text.append(System.lineSeparator());
      text.append("Actual shader state:           ");
      text.append(actual);
      text.append(System.lineSeparator());
      final String m = text.toString();
      text.setLength(0);
      throw new IllegalStateException(m);
    }
  }
}
