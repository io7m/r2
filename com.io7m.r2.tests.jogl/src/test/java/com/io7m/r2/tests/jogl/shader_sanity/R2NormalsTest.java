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
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors2D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors3D;
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

  private static Vector2D normalsCompress(final Vector3D n)
  {
    final float p = (float) Math.sqrt((double) ((n.z() * 8.0) + 8.0));
    final float x = (float) ((n.x() / (double) p) + 0.5);
    final float y = (float) ((n.y() / (double) p) + 0.5);
    return Vector2D.of((double) x, (double) y);
  }

  private static Vector3D normalsDecompress(
    final Vector2D n)
  {
    final Vector2D fn = Vector2D.of(
      (n.x() * 4.0) - 2.0,
      (n.y() * 4.0) - 2.0
    );

    final float f = (float) Vectors2D.dotProduct(fn, fn);
    final float g = (float) Math.sqrt((double) (1.0f - (f / 4.0f)));
    final float x = (float) (fn.x() * (double) g);
    final float y = (float) (fn.y() * (double) g);
    final float z = 1.0f - (f / 2.0f);
    return Vector3D.of((double) x, (double) y, (double) z);
  }

  @Test
  public void testNormals()
  {
    final List<Vector3D> normals = new ArrayList<>(16);
    normals.add(Vector3D.of(0.0, 0.0, 1.0));
    normals.add(Vector3D.of(0.0, 1.0, 0.0));
    normals.add(Vector3D.of(1.0, 0.0, 0.0));

    for (final Vector3D n : normals) {
      final Vector2D c = R2NormalsTest.normalsCompress(n);
      final Vector3D r = R2NormalsTest.normalsDecompress(c);

      System.out.println("n: " + n);
      System.out.println("c: " + c);
      System.out.println("r: " + r);

      Assert.assertEquals(n.x(), r.x(), 0.000001);
      Assert.assertEquals(n.y(), r.y(), 0.000001);
      Assert.assertEquals(n.z(), r.z(), 0.000001);
    }
  }

  @Test
  public void testNormalCompressionIdentity()
    throws Exception
  {
    final JCGLInterfaceGL33Type gg = R2ShadersTestUtilities.getGL();
    final R2ShaderTestFunctionEvaluator eval =
      new R2ShaderTestFunctionEvaluator(
        gg, "R2DebugPositionOnly", "R2DebugNormalCompression");

    final List<Vector3D> vectors = new ArrayList<>(16);
    vectors.add(Vector3D.of(-1.0, -1.0, 0.0));
    vectors.add(Vector3D.of(0.0, -1.0, 0.0));
    vectors.add(Vector3D.of(1.0, -1.0, 0.0));

    vectors.add(Vector3D.of(-1.0, 0.0, 0.0));
    vectors.add(Vector3D.of(0.0, 0.0, 1.0));
    vectors.add(Vector3D.of(1.0, 0.0, 0.0));

    vectors.add(Vector3D.of(-1.0, 1.0, 0.0));
    vectors.add(Vector3D.of(0.0, 1.0, 0.0));
    vectors.add(Vector3D.of(1.0, 1.0, 0.0));

    for (int index = 0; index < vectors.size(); ++index) {
      final Vector3D v = vectors.get(index);
      final Vector3D vn = Vectors3D.normalize(v);
      R2NormalsTest.LOG.debug("[{}]: {} ({})", Integer.valueOf(index), v, vn);
      final Vector3D r = eval.evaluate3f(vn);
      Assert.assertEquals(vn.x(), r.x(), 0.000001);
      Assert.assertEquals(vn.y(), r.y(), 0.000001);
      Assert.assertEquals(vn.z(), r.z(), 0.000001);
    }
  }

}
