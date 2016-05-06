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

package com.io7m.r2.tests.shaders.preprocessor;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessor;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorBatchRunner;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorBatchRunnerType;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorProgram;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorProgramType;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorType;
import org.anarres.cpp.ResourceFileSystem;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class R2ShaderPreprocessorBatchRunnerTest
{
  private static FileSystem getFS()
    throws IOException
  {
    return MemoryFileSystemBuilder.newLinux().build("test");
  }

  @Test
  public void testEmptyNoPrograms()
    throws Exception
  {
    try (final FileSystem fs = R2ShaderPreprocessorBatchRunnerTest.getFS()) {
      final Class<R2ShaderPreprocessorBatchRunnerTest> c =
        R2ShaderPreprocessorBatchRunnerTest.class;

      final R2ShaderPreprocessorType p =
        R2ShaderPreprocessor.newPreprocessorFromFS(
          new ResourceFileSystem(c.getClassLoader(), StandardCharsets.UTF_8));
      final R2ShaderPreprocessorBatchRunnerType r =
        R2ShaderPreprocessorBatchRunner.newRunner(p);

      final Path out = fs.getPath("/out");
      Files.createDirectories(out);
      r.process(out, new HashMap<>(1));

      Assert.assertEquals(0L, Files.list(out).count());
    }
  }

  @Test
  public void testProgramFull()
    throws Exception
  {
    try (final FileSystem fs = R2ShaderPreprocessorBatchRunnerTest.getFS()) {
      final Class<R2ShaderPreprocessorBatchRunnerTest> c =
        R2ShaderPreprocessorBatchRunnerTest.class;

      final R2ShaderPreprocessorType p =
        R2ShaderPreprocessor.newPreprocessorFromFS(
          new TestVirtualFilesystem(R2ShaderPreprocessor.class));
      final R2ShaderPreprocessorBatchRunnerType r =
        R2ShaderPreprocessorBatchRunner.newRunner(p);

      final Path out = fs.getPath("/out");
      Files.createDirectories(out);
      final Map<String, R2ShaderPreprocessorProgramType> m = new HashMap<>(1);
      m.put(
        "p",
        R2ShaderPreprocessorProgram.of(
          "p",
          "/com/io7m/r2/tests/shaders/preprocessor/v.vert",
          Optional.of("/com/io7m/r2/tests/shaders/preprocessor/g.geom"),
          "/com/io7m/r2/tests/shaders/preprocessor/f.frag"));
      r.process(out, m);

      Assert.assertEquals(3L, Files.list(out).count());
      Assert.assertTrue(Files.exists(out.resolve("p.vert")));
      Assert.assertTrue(Files.exists(out.resolve("p.frag")));
      Assert.assertTrue(Files.exists(out.resolve("p.geom")));
    }
  }

  @Test
  public void testProgramNoGeom()
    throws Exception
  {
    try (final FileSystem fs = R2ShaderPreprocessorBatchRunnerTest.getFS()) {
      final Class<R2ShaderPreprocessorBatchRunnerTest> c =
        R2ShaderPreprocessorBatchRunnerTest.class;

      final R2ShaderPreprocessorType p =
        R2ShaderPreprocessor.newPreprocessorFromFS(
          new TestVirtualFilesystem(R2ShaderPreprocessor.class));
      final R2ShaderPreprocessorBatchRunnerType r =
        R2ShaderPreprocessorBatchRunner.newRunner(p);

      final Path out = fs.getPath("/out");
      Files.createDirectories(out);
      final Map<String, R2ShaderPreprocessorProgramType> m = new HashMap<>(1);
      m.put(
        "p",
        R2ShaderPreprocessorProgram.of(
          "p",
          "/com/io7m/r2/tests/shaders/preprocessor/v.vert",
          Optional.empty(),
          "/com/io7m/r2/tests/shaders/preprocessor/f.frag"));
      r.process(out, m);

      Assert.assertEquals(2L, Files.list(out).count());
      Assert.assertTrue(Files.exists(out.resolve("p.vert")));
      Assert.assertTrue(Files.exists(out.resolve("p.frag")));
    }
  }
}
