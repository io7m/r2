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

import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessor;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorType;
import org.anarres.cpp.LexerException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class R2ShaderPreprocessorTest
{
  @Rule public final ExpectedException expected = ExpectedException.none();

  @Test public void testBadDirective()
    throws Exception
  {
    final R2ShaderPreprocessorType p =
      R2ShaderPreprocessor.newPreprocessorFromFS(
        new TestVirtualFilesystem(R2ShaderPreprocessor.class));

    this.expected.expect(LexerException.class);
    p.preprocessFile("/com/io7m/r2/tests/shaders/preprocessor/bad-directive.h");
  }

  @Test public void testIncludeNonexistent()
    throws Exception
  {
    final R2ShaderPreprocessorType p =
      R2ShaderPreprocessor.newPreprocessorFromFS(
        new TestVirtualFilesystem(R2ShaderPreprocessor.class));

    this.expected.expect(LexerException.class);
    p.preprocessFile("/com/io7m/r2/tests/shaders/preprocessor/bad-include.h");
  }

  @Test public void testVersion()
    throws Exception
  {
    final R2ShaderPreprocessorType p =
      R2ShaderPreprocessor.newPreprocessorFromFS(
        new TestVirtualFilesystem(R2ShaderPreprocessor.class));

    p.preprocessFile("/com/io7m/r2/tests/shaders/preprocessor/version.h");
  }
}
