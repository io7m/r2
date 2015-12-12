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
import com.io7m.jtensors.VectorI2F;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class R2LogDepthTest extends R2ShaderTest
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2LogDepthTest.class);
  }

  private static float depthCoefficient(
    final float far)
  {
    return 2.0f / R2LogDepthTest.log2(far + 1.0f);
  }

  private static float log2(
    final float x)
  {
    return (float) (Math.log(x) / Math.log(2.0));
  }

  @Test public void testLogDepthIdentity()
    throws Exception
  {
    final JCGLInterfaceGL33Type gg = R2ShadersTestUtilities.getGL();
    final R2ShaderTestFunctionEvaluator eval =
      new R2ShaderTestFunctionEvaluator(gg, "R2DebugLogDepth");

    final List<VectorI2F> vectors = new ArrayList<>(16);
    vectors.add(new VectorI2F(0.0f, R2LogDepthTest.depthCoefficient(100.0f)));
    vectors.add(new VectorI2F(1.0f, R2LogDepthTest.depthCoefficient(100.0f)));
    vectors.add(new VectorI2F(10.0f, R2LogDepthTest.depthCoefficient(100.0f)));
    vectors.add(new VectorI2F(50.0f, R2LogDepthTest.depthCoefficient(100.0f)));
    vectors.add(new VectorI2F(100.0f, R2LogDepthTest.depthCoefficient(100.0f)));

    for (int index = 0; index < vectors.size(); ++index) {
      final VectorI2F v = vectors.get(index);
      final VectorI2F r = eval.evaluate2f(v);
      R2LogDepthTest.LOG.debug("[{}]: {} -> {}", Integer.valueOf(index), v, r);
      Assert.assertEquals(v.getXF(), r.getXF(), 0.0001f);
    }
  }

  @Test public void testLogDepthComposedIdentity()
    throws Exception
  {
    final JCGLInterfaceGL33Type gg = R2ShadersTestUtilities.getGL();
    final R2ShaderTestFunctionEvaluator eval =
      new R2ShaderTestFunctionEvaluator(gg, "R2DebugLogDepthComposed");

    final List<VectorI2F> vectors = new ArrayList<>(16);
    vectors.add(new VectorI2F(0.0f, R2LogDepthTest.depthCoefficient(100.0f)));
    vectors.add(new VectorI2F(1.0f, R2LogDepthTest.depthCoefficient(100.0f)));
    vectors.add(new VectorI2F(10.0f, R2LogDepthTest.depthCoefficient(100.0f)));
    vectors.add(new VectorI2F(50.0f, R2LogDepthTest.depthCoefficient(100.0f)));
    vectors.add(new VectorI2F(100.0f, R2LogDepthTest.depthCoefficient(100.0f)));

    for (int index = 0; index < vectors.size(); ++index) {
      final VectorI2F v = vectors.get(index);
      final VectorI2F r = eval.evaluate2f(v);
      R2LogDepthTest.LOG.debug("[{}]: {} -> {}", Integer.valueOf(index), v, r);
      Assert.assertEquals(v.getXF(), r.getXF(), 0.0001f);
    }
  }
}
