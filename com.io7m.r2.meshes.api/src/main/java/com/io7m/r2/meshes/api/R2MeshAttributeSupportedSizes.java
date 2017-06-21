/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.meshes.api;

import com.io7m.junreachable.UnreachableCodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The supported sizes in bits of each type.
 */

public final class R2MeshAttributeSupportedSizes
{
  private static final List<Integer> SUPPORTED_INTEGER_UNSIGNED;
  private static final List<Integer> SUPPORTED_INTEGER_SIGNED;
  private static final List<Integer> SUPPORTED_FLOAT;

  static {
    SUPPORTED_INTEGER_UNSIGNED = new ArrayList<>(4);
    SUPPORTED_INTEGER_UNSIGNED.add(Integer.valueOf(8));
    SUPPORTED_INTEGER_UNSIGNED.add(Integer.valueOf(16));
    SUPPORTED_INTEGER_UNSIGNED.add(Integer.valueOf(32));

    SUPPORTED_INTEGER_SIGNED = new ArrayList<>(4);
    SUPPORTED_INTEGER_SIGNED.add(Integer.valueOf(8));
    SUPPORTED_INTEGER_SIGNED.add(Integer.valueOf(16));
    SUPPORTED_INTEGER_SIGNED.add(Integer.valueOf(32));

    SUPPORTED_FLOAT = new ArrayList<>(3);
    SUPPORTED_FLOAT.add(Integer.valueOf(16));
    SUPPORTED_FLOAT.add(Integer.valueOf(32));
  }

  private R2MeshAttributeSupportedSizes()
  {
    throw new UnreachableCodeException();
  }

  /**
   * @param bits The size in bits
   *
   * @return {@code true} if an unsigned integer type with the given size is
   * supported
   */

  public static boolean isIntegerUnsignedSupported(
    final int bits)
  {
    return SUPPORTED_INTEGER_UNSIGNED.contains(Integer.valueOf(bits));
  }

  /**
   * Check if an unsigned integer type with the given size is supported.
   *
   * @param name The name of the attribute containing the data
   * @param bits The size in bits
   *
   * @return {@code bits}
   *
   * @throws UnsupportedOperationException If the size is not supported
   */

  public static int checkIntegerUnsignedSupported(
    final String name,
    final int bits)
    throws UnsupportedOperationException
  {
    if (!isIntegerUnsignedSupported(bits)) {
      return unsupported(
        name,
        bits,
        "unsigned integer",
        SUPPORTED_INTEGER_UNSIGNED);
    }
    return bits;
  }

  /**
   * @param bits The size in bits
   *
   * @return {@code true} if a signed integer type with the given size is
   * supported
   */

  public static boolean isIntegerSignedSupported(
    final int bits)
  {
    return SUPPORTED_INTEGER_SIGNED.contains(Integer.valueOf(bits));
  }

  /**
   * Check if a signed integer type with the given size is supported.
   *
   * @param name The name of the attribute containing the data
   * @param bits The size in bits
   *
   * @return {@code bits}
   *
   * @throws UnsupportedOperationException If the size is not supported
   */

  public static int checkIntegerSignedSupported(
    final String name,
    final int bits)
    throws UnsupportedOperationException
  {
    if (!isIntegerSignedSupported(bits)) {
      return unsupported(
        name,
        bits,
        "signed integer",
        SUPPORTED_INTEGER_SIGNED);
    }
    return bits;
  }

  /**
   * @param bits The size in bits
   *
   * @return {@code true} if a floating point type with the given size is
   * supported
   */

  public static boolean isFloatSupported(
    final int bits)
  {
    return SUPPORTED_FLOAT.contains(Integer.valueOf(bits));
  }

  /**
   * Check if a floating point type with the given size is supported.
   *
   * @param name The name of the attribute containing the data
   * @param bits The size in bits
   *
   * @return {@code bits}
   *
   * @throws UnsupportedOperationException If the size is not supported
   */

  public static int checkFloatSupported(
    final String name,
    final int bits)
    throws UnsupportedOperationException
  {
    if (!isFloatSupported(bits)) {
      return unsupported(name, bits, "float", SUPPORTED_FLOAT);
    }
    return bits;
  }

  private static int unsupported(
    final String name,
    final int bits,
    final String type,
    final List<Integer> sizes)
  {
    final StringBuilder sb = new StringBuilder(128);
    sb.append("Unsupported ").append(type).append(" size.")
      .append(System.lineSeparator())
      .append("  Attribute: ").append(name).append(System.lineSeparator())
      .append("  Received:  ").append(bits).append(System.lineSeparator())
      .append("  Supported: ").append(sizes.stream()
                                        .map(x -> Integer.toUnsignedString(x.intValue()))
                                        .collect(Collectors.joining("|")))
      .append(System.lineSeparator());
    throw new UnsupportedOperationException(sb.toString());
  }
}
