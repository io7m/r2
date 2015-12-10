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

import com.io7m.jnull.NullCheck;
import org.anarres.cpp.LexerException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The default implementation of the {@link R2ShaderPreprocessorBatchRunnerType}
 * interface.
 */

public final class R2ShaderPreprocessorBatchRunner
  implements R2ShaderPreprocessorBatchRunnerType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2ShaderPreprocessorBatchRunner.class);
  }

  private final R2ShaderPreprocessorType proc;

  private R2ShaderPreprocessorBatchRunner(
    final R2ShaderPreprocessorType in_proc)
  {
    this.proc = NullCheck.notNull(in_proc);
  }

  /**
   * @param p A preprocessor
   *
   * @return A new batch runner
   */

  public static R2ShaderPreprocessorBatchRunnerType newRunner(
    final R2ShaderPreprocessorType p)
  {
    return new R2ShaderPreprocessorBatchRunner(p);
  }

  @Override public void process(
    final Path output,
    final Map<String, R2ShaderPreprocessorProgramType> sources)
    throws IOException, LexerException
  {
    NullCheck.notNull(output);
    NullCheck.notNull(sources);

    Files.createDirectories(output);

    for (final String name : sources.keySet()) {
      final R2ShaderPreprocessorProgramType p = sources.get(name);
      R2ShaderPreprocessorBatchRunner.LOG.debug("processing {}", name);

      {
        final List<String> lines =
          this.proc.preprocessFile(p.getVertexShaderFile());
        final Path fname = output.resolve(name + ".vert");
        R2ShaderPreprocessorBatchRunner.LOG.debug("writing {}", fname);
        try (final OutputStream os = Files.newOutputStream(fname)) {
          IOUtils.writeLines(lines, "", os);
        }
      }

      {
        final List<String> lines =
          this.proc.preprocessFile(p.getFragmentShaderFile());
        final Path fname = output.resolve(name + ".frag");
        R2ShaderPreprocessorBatchRunner.LOG.debug("writing {}", fname);
        try (final OutputStream os = Files.newOutputStream(fname)) {
          IOUtils.writeLines(lines, "", os);
        }
      }

      {
        final Optional<String> pg_opt = p.getGeometryShaderFile();
        if (pg_opt.isPresent()) {
          final List<String> lines = this.proc.preprocessFile(pg_opt.get());
          final Path fname = output.resolve(name + ".geom");
          R2ShaderPreprocessorBatchRunner.LOG.debug("writing {}", fname);
          try (final OutputStream os = Files.newOutputStream(fname)) {
            IOUtils.writeLines(lines, "", os);
          }
        }
      }
    }
  }
}
