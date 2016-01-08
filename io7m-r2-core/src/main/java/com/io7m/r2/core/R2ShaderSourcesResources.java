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

package com.io7m.r2.core;

import com.io7m.jnull.NullCheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the {@link R2ShaderSourcesType} that loads shaders from
 * Java resources.
 */

public final class R2ShaderSourcesResources implements R2ShaderSourcesType
{
  private final Class<?> base;

  private R2ShaderSourcesResources(final Class<?> in_base)
  {
    this.base = NullCheck.notNull(in_base);
  }

  /**
   * @param base The base class for resolving resources
   *
   * @return A shader sources instance
   */

  public static R2ShaderSourcesType newSources(final Class<?> base)
  {
    return new R2ShaderSourcesResources(base);
  }

  @Override
  public List<String> getSourceLines(final String name)
  {
    final List<String> lines = new ArrayList<>(64);

    try (final InputStream is = this.base.getResourceAsStream(name)) {
      if (is == null) {
        throw new R2RendererExceptionNoSuchShader(name);
      }
      try (final BufferedReader r =
             new BufferedReader(new InputStreamReader(is))) {
        while (true) {
          final String line = r.readLine();
          if (line == null) {
            return lines;
          }
          lines.add(line + "\n");
        }
      }
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
