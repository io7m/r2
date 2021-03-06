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

package com.io7m.r2.tests.filters.ssao;

import com.io7m.jranges.RangeCheckException;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors3D;
import com.io7m.r2.filters.ssao.api.R2SSAOKernelType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashSet;
import java.util.Set;

public abstract class R2SSAOKernelContract
{
  @Rule public ExpectedException expected = ExpectedException.none();

  protected abstract R2SSAOKernelType newKernel(int size);

  @Test
  public final void testKernel()
  {
    final R2SSAOKernelType k = this.newKernel(128);
    Assert.assertEquals(128L, (long) k.size());

    final Set<Vector3D> points = new HashSet<>(256);

    Vector3D v;
    for (int index = 0; index < 128; ++index) {
      v = k.sample(index);
      System.out.printf(
        "%f,%f,%f\n",
        Double.valueOf(v.x()),
        Double.valueOf(v.y()),
        Double.valueOf(v.z()));

      final double mag = Vectors3D.magnitude(v);
      System.out.printf("mag %f\n", Double.valueOf(mag));

      Assert.assertTrue(
        String.format("Magnitude %f must be >= 0.099999", Double.valueOf(mag)),
        mag >= 0.099999);
      Assert.assertTrue(
        String.format("Magnitude %f must be <= 1.0", Double.valueOf(mag)),
        mag <= 1.0);

      final Vector3D r = Vector3D.of(v.x(), v.y(), v.z());
      Assert.assertFalse(points.contains(r));

      System.out.printf(
        "%f,%f,%f\n",
        Double.valueOf(v.x()),
        Double.valueOf(v.y()),
        Double.valueOf(v.z()));
    }

    k.regenerate(128);
    Assert.assertEquals(128L, (long) k.size());

    for (int index = 0; index < 128; ++index) {
      v = k.sample(index);
      final double mag = Vectors3D.magnitude(v);
      System.out.printf("mag %f\n", Double.valueOf(mag));

      Assert.assertTrue(
        String.format("Magnitude %f must be >= 0.099999", Double.valueOf(mag)),
        mag >= 0.099999);
      Assert.assertTrue(
        String.format("Magnitude %f must be <= 1.0", Double.valueOf(mag)),
        mag <= 1.0);
      final Vector3D r = Vector3D.of(v.x(), v.y(), v.z());
      Assert.assertFalse(points.contains(r));
    }
  }

  @Test
  public final void testOutOfRange_0()
  {
    final R2SSAOKernelType k = this.newKernel(2);
    this.expected.expect(RangeCheckException.class);
    k.sample(2);
  }

  @Test
  public final void testOutOfRange_1()
  {
    final R2SSAOKernelType k = this.newKernel(128);

    k.regenerate(100);
    this.expected.expect(RangeCheckException.class);
    k.sample(101);
  }

  @Test
  public final void testOutOfRange_2()
  {
    final R2SSAOKernelType k = this.newKernel(128);

    this.expected.expect(RangeCheckException.class);
    k.regenerate(129);
  }

  @Test
  public final void testBadKernelSize_0()
  {
    this.expected.expect(RangeCheckException.class);
    this.newKernel(0);
  }

  @Test
  public final void testBadKernelSize_1()
  {
    this.expected.expect(RangeCheckException.class);
    this.newKernel(129);
  }

}
