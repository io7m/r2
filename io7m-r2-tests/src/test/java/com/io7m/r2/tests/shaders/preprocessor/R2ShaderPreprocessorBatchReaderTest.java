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

import com.fasterxml.jackson.databind.JsonMappingException;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorBatchReader;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorBatchReaderType;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorProgramType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public final class R2ShaderPreprocessorBatchReaderTest
{
  @Rule public final ExpectedException expected = ExpectedException.none();

  @Test
  public void testEmptyInvalid()
    throws Exception
  {
    final Class<R2ShaderPreprocessorBatchReaderTest> c =
      R2ShaderPreprocessorBatchReaderTest.class;

    final R2ShaderPreprocessorBatchReaderType r =
      R2ShaderPreprocessorBatchReader.newReader();

    try (final InputStream is = c.getResourceAsStream("empty.json")) {
      this.expected.expect(JsonMappingException.class);
      final Map<String, R2ShaderPreprocessorProgramType> rm =
        r.readFromStream(is);
      Assert.assertEquals(0L, (long) rm.size());
    }
  }

  @Test
  public void testEmptyNoPrograms()
    throws Exception
  {
    final Class<R2ShaderPreprocessorBatchReaderTest> c =
      R2ShaderPreprocessorBatchReaderTest.class;

    final R2ShaderPreprocessorBatchReaderType r =
      R2ShaderPreprocessorBatchReader.newReader();

    try (final InputStream is = c.getResourceAsStream("no-programs.json")) {
      final Map<String, R2ShaderPreprocessorProgramType> rm =
        r.readFromStream(is);
      Assert.assertEquals(0L, (long) rm.size());
    }
  }

  @Test
  public void testBatch()
    throws Exception
  {
    final Class<R2ShaderPreprocessorBatchReaderTest> c =
      R2ShaderPreprocessorBatchReaderTest.class;

    final R2ShaderPreprocessorBatchReaderType r =
      R2ShaderPreprocessorBatchReader.newReader();

    try (final InputStream is = c.getResourceAsStream("batch.json")) {
      final Map<String, R2ShaderPreprocessorProgramType> rm =
        r.readFromStream(is);

      Assert.assertEquals(2L, (long) rm.size());
      Assert.assertTrue(rm.containsKey("p"));
      Assert.assertTrue(rm.containsKey("q"));

      {
        final R2ShaderPreprocessorProgramType p = rm.get("p");
        Assert.assertEquals("v", p.getVertexShaderFile());
        Assert.assertEquals("f", p.getFragmentShaderFile());
        Assert.assertEquals(Optional.of("g"), p.getGeometryShaderFile());
      }

      {
        final R2ShaderPreprocessorProgramType p = rm.get("q");
        Assert.assertEquals("v", p.getVertexShaderFile());
        Assert.assertEquals("f", p.getFragmentShaderFile());
        Assert.assertEquals(Optional.empty(), p.getGeometryShaderFile());
      }
    }
  }
}
