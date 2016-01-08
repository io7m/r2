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

package com.io7m.r2.shaders.tests;

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.r2.shaders.R2Shaders;
import com.io7m.r2.shaders.checker.R2ShaderChecker;
import com.io7m.r2.shaders.checker.R2ShaderCheckerException;
import com.io7m.r2.shaders.checker.R2ShaderCheckerType;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class R2ShadersCheckTest extends R2ShaderTest
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2ShadersCheckTest.class);
  }

  private static void check(final String name)
    throws IOException, R2ShaderCheckerException
  {
    final String v_path = "/com/io7m/r2/shaders/" + name + ".vert";
    final String f_path = "/com/io7m/r2/shaders/" + name + ".frag";
    final R2ShaderCheckerType c = R2ShadersCheckTest.getChecker();
    final Class<R2Shaders> cl = R2Shaders.class;
    final List<String> vs =
      IOUtils.readLines(cl.getResourceAsStream(v_path))
        .stream()
        .map(line -> {
          R2ShadersCheckTest.LOG.trace("[{}][vertex]: {}", name, line);
          return line + "\n";
        }).collect(Collectors.toList());
    final List<String> fs =
      IOUtils.readLines(cl.getResourceAsStream(f_path))
        .stream()
        .map(line -> {
          R2ShadersCheckTest.LOG.trace("[{}][fragment]: {}", name, line);
          return line + "\n";
        }).collect(Collectors.toList());
    c.check(vs, Optional.empty(), fs);
  }

  private static R2ShaderCheckerType getChecker()
  {
    final JCGLContextType c = R2TestContexts.newGL33Context("main", 24, 8);
    return R2ShaderChecker.newChecker(c.contextGetGL33().getShaders());
  }

  @Test public void testDeferredSurfaceBasicSingle()
    throws Exception
  {
    R2ShadersCheckTest.check("R2DeferredSurfaceBasicSingle");
  }

  @Test public void testStencilSingle()
    throws Exception
  {
    R2ShadersCheckTest.check("R2StencilSingle");
  }

  @Test public void testStencilScreen()
    throws Exception
  {
    R2ShadersCheckTest.check("R2StencilScreen");
  }

  @Test public void testDeferredSurfaceBasicReflectiveSingle()
    throws Exception
  {
    R2ShadersCheckTest.check("R2DeferredSurfaceBasicReflectiveSingle");
  }
}
