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

package com.io7m.r2.shaders.preprocessor;

import com.io7m.jnull.NullCheck;
import org.anarres.cpp.ChrootFileSystem;
import org.anarres.cpp.DefaultPreprocessorListener;
import org.anarres.cpp.LexerException;
import org.anarres.cpp.Preprocessor;
import org.anarres.cpp.Source;
import org.anarres.cpp.Token;
import org.anarres.cpp.VirtualFileSystem;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of the {@link R2ShaderPreprocessorType} interface.
 */

public final class R2ShaderPreprocessor implements R2ShaderPreprocessorType
{
  private final VirtualFileSystem fs;

  private R2ShaderPreprocessor(
    final VirtualFileSystem in_fs)
  {
    this.fs = NullCheck.notNull(in_fs);
  }

  /**
   * @param in_root A root directory against which all paths will be resolved
   *
   * @return A new preprocessor
   */

  public static R2ShaderPreprocessorType newPreprocessor(
    final File in_root)
  {
    return R2ShaderPreprocessor.newPreprocessorFromFS(
      new ChrootFileSystem(NullCheck.notNull(in_root)));
  }

  /**
   * @param in_fs A file system
   *
   * @return A new preprocessor
   */

  public static R2ShaderPreprocessorType newPreprocessorFromFS(
    final VirtualFileSystem in_fs)
  {
    return new R2ShaderPreprocessor(NullCheck.notNull(in_fs));
  }

  @Override public List<String> preprocessFile(final String file)
    throws IOException, LexerException
  {
    try (final Preprocessor pp = new Preprocessor()) {
      pp.setFileSystem(this.fs);
      pp.addInput(this.fs.getFile(file).getSource());
      pp.setListener(new DefaultPreprocessorListener()
      {
        @Override public void handleError(
          final Source source,
          final int line,
          final int column,
          final String msg)
          throws LexerException
        {
          if ("Unknown preprocessor directive version".equals(msg)) {
            return;
          }

          throw new LexerException(
            String.format(
              "%d:%d: %s\n",
              Integer.valueOf(line),
              Integer.valueOf(column), msg));
        }
      });

      try (final ByteArrayOutputStream bao =
        new ByteArrayOutputStream(2 << 14)) {
        try (final PrintStream baos = new PrintStream(bao)) {
          baos.println("#version 330 core");
          while (true) {
            final Token tok = pp.token();
            if (tok == null) {
              break;
            }
            if (tok.getType() == Token.EOF) {
              break;
            }
            baos.print(tok.getText());
          }
        }

        try (final ByteArrayInputStream bai =
          new ByteArrayInputStream(bao.toByteArray())) {
          final List<String> lines = IOUtils.readLines(bai);
          return lines.stream()
            .filter(s -> !s.trim().isEmpty())
            .map(line -> line + "\n")
            .collect(Collectors.toList());
        }
      }
    }
  }
}
