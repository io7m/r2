/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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

import com.io7m.junreachable.UnreachableCodeException;
import org.anarres.cpp.ChrootFileSystem;
import org.anarres.cpp.DefaultPreprocessorListener;
import org.anarres.cpp.LexerException;
import org.anarres.cpp.Preprocessor;
import org.anarres.cpp.Source;
import org.anarres.cpp.Token;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

/**
 * Main preprocessor.
 */

public final class Main
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(Main.class);
  }

  private Main()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Main program.
   *
   * @param args Command line arguments
   */

  public static void main(final String[] args)
  {
    if (args.length < 2) {
      Main.LOG.info("usage: root file");
      System.exit(1);
    }

    try {
      final File root = new File(args[0]);
      final Preprocessor pp = new Preprocessor();
      final ChrootFileSystem fs = new ChrootFileSystem(root);
      pp.setFileSystem(fs);
      pp.addInput(fs.getFile(args[1]).getSource());
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

          super.handleError(source, line, column, msg);
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
          for (int index = 0; index < lines.size(); ++index) {
            final String line = lines.get(index);
            if (line.isEmpty()) {
              continue;
            }
            if (line.trim().isEmpty()) {
              continue;
            }
            System.out.println(line);
          }
        }
      }

    } catch (final Exception e) {
      Main.LOG.error("Preprocessing failure: ", e);
      System.exit(1);
    }
  }
}
