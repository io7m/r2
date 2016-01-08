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

import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jtensors.VectorI2F;
import com.io7m.jtensors.VectorI3F;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class R2NormalsTest extends R2ShaderTest
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2NormalsTest.class);
  }

  private static VectorI2F normalsCompress(final VectorI3F n)
  {
    final float p = (float) Math.sqrt((double) ((n.getZF() * 8.0f) + 8.0f));
    final float x = (n.getXF() / p) + 0.5f;
    final float y = (n.getYF() / p) + 0.5f;
    return new VectorI2F(x, y);
  }

  private static VectorI3F normalsDecompress(
    final VectorI2F n)
  {
    final VectorI2F fn = new VectorI2F(
      (n.getXF() * 4.0f) - 2.0f,
      (n.getYF() * 4.0f) - 2.0f
    );

    final float f = (float) VectorI2F.dotProduct(fn, fn);
    final float g = (float) Math.sqrt((double) (1.0f - (f / 4.0f)));
    final float x = fn.getXF() * g;
    final float y = fn.getYF() * g;
    final float z = 1.0f - (f / 2.0f);
    return new VectorI3F(x, y, z);
  }

  @Test public void testNormals()
  {
    final List<VectorI3F> normals = new ArrayList<>(16);
    normals.add(new VectorI3F(0.0f, 0.0f, 1.0f));
    normals.add(new VectorI3F(0.0f, 1.0f, 0.0f));
    normals.add(new VectorI3F(1.0f, 0.0f, 0.0f));

    for (final VectorI3F n : normals) {
      final VectorI2F c = R2NormalsTest.normalsCompress(n);
      final VectorI3F r = R2NormalsTest.normalsDecompress(c);

      System.out.println("n: " + n);
      System.out.println("c: " + c);
      System.out.println("r: " + r);

      Assert.assertEquals(n.getXF(), r.getXF(), 0.000001f);
      Assert.assertEquals(n.getYF(), r.getYF(), 0.000001f);
      Assert.assertEquals(n.getZF(), r.getZF(), 0.000001f);
    }
  }

  @Test public void testNormalCompressionIdentity()
    throws Exception
  {
    final JCGLInterfaceGL33Type gg = R2ShadersTestUtilities.getGL();
    final R2ShaderTestFunctionEvaluator eval =
      new R2ShaderTestFunctionEvaluator(gg, "R2DebugNormalCompression");

    final List<VectorI3F> vectors = new ArrayList<>(16);
    vectors.add(new VectorI3F(-1.0f, -1.0f, 0.0f));
    vectors.add(new VectorI3F(0.0f, -1.0f, 0.0f));
    vectors.add(new VectorI3F(1.0f, -1.0f, 0.0f));

    vectors.add(new VectorI3F(-1.0f, 0.0f, 0.0f));
    vectors.add(new VectorI3F(0.0f, 0.0f, 1.0f));
    vectors.add(new VectorI3F(1.0f, 0.0f, 0.0f));

    vectors.add(new VectorI3F(-1.0f, 1.0f, 0.0f));
    vectors.add(new VectorI3F(0.0f, 1.0f, 0.0f));
    vectors.add(new VectorI3F(1.0f, 1.0f, 0.0f));

    for (int index = 0; index < vectors.size(); ++index) {
      final VectorI3F v = vectors.get(index);
      final VectorI3F vn = VectorI3F.normalize(v);
      R2NormalsTest.LOG.debug("[{}]: {} ({})", Integer.valueOf(index), v, vn);
      final VectorI3F r = eval.evaluate3f(vn);
      Assert.assertEquals(vn.getXF(), r.getXF(), 0.000001f);
      Assert.assertEquals(vn.getYF(), r.getYF(), 0.000001f);
      Assert.assertEquals(vn.getZF(), r.getZF(), 0.000001f);
    }
  }

}
