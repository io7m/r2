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

package com.io7m.r2.shaders.tests;

import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jtensors.VectorI4F;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class R2SanityTest extends R2ShaderTest
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2SanityTest.class);
  }

  @Test public void testIdentity()
    throws Exception
  {
    final JCGLInterfaceGL33Type gg = R2ShadersTestUtilities.getGL();
    final R2ShaderTestFunctionEvaluator eval =
      new R2ShaderTestFunctionEvaluator(gg, "R2DebugIdentity");

    for (int index = 0; index < 10; ++index) {
      final VectorI4F in = new VectorI4F(
        (float) Math.random(),
        (float) Math.random(),
        (float) Math.random(),
        (float) Math.random());
      final VectorI4F r = eval.evaluate4f(in);

      R2SanityTest.LOG.debug("[{}] in  {}", Integer.valueOf(index), in);
      R2SanityTest.LOG.debug("[{}] out {}", Integer.valueOf(index), r);

      Assert.assertEquals(in.getXF(), r.getXF(), 0.000001f);
      Assert.assertEquals(in.getYF(), r.getYF(), 0.000001f);
      Assert.assertEquals(in.getZF(), r.getZF(), 0.000001f);
      Assert.assertEquals(in.getWF(), r.getWF(), 0.000001f);
    }
  }
}
