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

package com.io7m.r2.tests.jogl.shader_sanity;

import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jtensors.core.unparameterized.vectors.Vector2D;
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

  private static double depthCoefficient(
    final double far)
  {
    return 2.0 / log2(far + 1.0);
  }

  private static double log2(
    final double x)
  {
    return StrictMath.log(x) / StrictMath.log(2.0);
  }

  @Test
  public void testLogDepthIdentity()
    throws Exception
  {
    final JCGLInterfaceGL33Type gg = R2ShadersTestUtilities.getGL();
    final R2ShaderTestFunctionEvaluator eval =
      new R2ShaderTestFunctionEvaluator(
        gg, "R2DebugPositionOnly", "R2DebugLogDepth");

    final List<Vector2D> vectors = new ArrayList<>(16);
    vectors.add(Vector2D.of(
      0.0,
      depthCoefficient(100.0)));
    vectors.add(Vector2D.of(
      1.0,
      depthCoefficient(100.0)));
    vectors.add(Vector2D.of(
      10.0,
      depthCoefficient(100.0)));
    vectors.add(Vector2D.of(
      50.0,
      depthCoefficient(100.0)));
    vectors.add(Vector2D.of(
      100.0,
      depthCoefficient(100.0)));

    for (int index = 0; index < vectors.size(); ++index) {
      final Vector2D v = vectors.get(index);
      final Vector2D r = eval.evaluate2f(v);
      LOG.debug("[{}]: {} -> {}", Integer.valueOf(index), v, r);
      Assert.assertEquals(v.x(), r.x(), 0.0001);
    }
  }

  @Test
  public void testLogDepthComposedIdentity()
    throws Exception
  {
    final JCGLInterfaceGL33Type gg = R2ShadersTestUtilities.getGL();
    final R2ShaderTestFunctionEvaluator eval =
      new R2ShaderTestFunctionEvaluator(
        gg, "R2DebugPositionOnly", "R2DebugLogDepthComposed");

    final List<Vector2D> vectors = new ArrayList<>(16);
    vectors.add(Vector2D.of(
      0.0,
      depthCoefficient(100.0)));
    vectors.add(Vector2D.of(
      1.0,
      depthCoefficient(100.0)));
    vectors.add(Vector2D.of(
      10.0,
      depthCoefficient(100.0)));
    vectors.add(Vector2D.of(
      50.0,
      depthCoefficient(100.0)));
    vectors.add(Vector2D.of(
      100.0,
      depthCoefficient(100.0)));

    for (int index = 0; index < vectors.size(); ++index) {
      final Vector2D v = vectors.get(index);
      final Vector2D r = eval.evaluate2f(v);
      LOG.debug("[{}]: {} -> {}", Integer.valueOf(index), v, r);
      Assert.assertEquals(v.x(), r.x(), 0.0001);
    }
  }
}
