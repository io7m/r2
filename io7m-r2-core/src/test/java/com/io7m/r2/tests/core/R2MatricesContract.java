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

package com.io7m.r2.tests.core;

import com.io7m.jcanephora.core.JCGLProjectionMatrices;
import com.io7m.jcanephora.core.JCGLProjectionMatricesType;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.jtensors.parameterized.PMatrixI4x4F;
import com.io7m.jtensors.parameterized.PMatrixReadable3x3FType;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.core.R2MatricesObserverType;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionOrthographic;
import com.io7m.r2.core.R2RendererExceptionInstanceAlreadyActive;
import com.io7m.r2.core.R2RendererExceptionObserverAlreadyActive;
import com.io7m.r2.core.R2TransformOST;
import com.io7m.r2.core.R2TransformReadableType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.valid4j.exceptions.RequireViolation;

public abstract class R2MatricesContract
{
  @Rule public ExpectedException expected = ExpectedException.none();

  protected abstract R2MatricesType newMatrices();

  @Test
  public final void testMatricesObserverCalled()
  {
    final R2MatricesType m = this.newMatrices();
    final JCGLProjectionMatricesType pm = JCGLProjectionMatrices.newMatrices();

    final Integer r = m.withObserver(
      PMatrixI4x4F.identity(),
      R2ProjectionOrthographic.newFrustum(pm),
      mm -> Integer.valueOf(23));

    Assert.assertEquals(Integer.valueOf(23), r);
  }

  @Test
  public final void testMatricesObserverInactive0()
  {
    final R2MatricesType m = this.newMatrices();
    final JCGLProjectionMatricesType pm = JCGLProjectionMatrices.newMatrices();

    final R2MatricesObserverType r = m.withObserver(
      PMatrixI4x4F.identity(),
      R2ProjectionOrthographic.newFrustum(pm),
      mm -> mm);

    this.expected.expect(RequireViolation.class);
    r.getMatrixProjection();
  }

  @Test
  public final void testMatricesObserverInactive1()
  {
    final R2MatricesType m = this.newMatrices();
    final JCGLProjectionMatricesType pm = JCGLProjectionMatrices.newMatrices();

    final R2MatricesObserverType r = m.withObserver(
      PMatrixI4x4F.identity(),
      R2ProjectionOrthographic.newFrustum(pm),
      mm -> mm);

    this.expected.expect(RequireViolation.class);
    r.getMatrixView();
  }

  @Test
  public final void testMatricesObserverInactive2()
  {
    final R2MatricesType m = this.newMatrices();
    final JCGLProjectionMatricesType pm = JCGLProjectionMatrices.newMatrices();

    final R2MatricesObserverType r = m.withObserver(
      PMatrixI4x4F.identity(),
      R2ProjectionOrthographic.newFrustum(pm),
      mm -> mm);

    this.expected.expect(RequireViolation.class);
    r.getMatrixViewInverse();
  }

  @Test
  public final void testMatricesObserverInactive3()
  {
    final R2MatricesType m = this.newMatrices();
    final JCGLProjectionMatricesType pm = JCGLProjectionMatrices.newMatrices();

    final R2MatricesObserverType r = m.withObserver(
      PMatrixI4x4F.identity(),
      R2ProjectionOrthographic.newFrustum(pm),
      mm -> mm);

    this.expected.expect(RequireViolation.class);
    r.getViewRays();
  }

  @Test
  public final void testMatricesObserverCalledTwice()
  {
    final R2MatricesType m = this.newMatrices();
    final JCGLProjectionMatricesType pm =
      JCGLProjectionMatrices.newMatrices();
    final R2ProjectionOrthographic pp =
      R2ProjectionOrthographic.newFrustum(pm);

    this.expected.expect(R2RendererExceptionObserverAlreadyActive.class);
    this.expected.expectMessage("Observer already active");

    m.withObserver(
      PMatrixI4x4F.identity(),
      pp,
      mm -> m.withObserver(PMatrixI4x4F.identity(), pp, mk -> {
        throw new UnreachableCodeException();
      }));
  }

  @Test
  public final void testMatricesObserverTransformCalled()
  {
    final R2MatricesType m = this.newMatrices();
    final JCGLProjectionMatricesType pm =
      JCGLProjectionMatrices.newMatrices();
    final PMatrixReadable3x3FType<R2SpaceTextureType, R2SpaceTextureType> uv =
      PMatrixI3x3F.identity();
    final R2TransformReadableType t =
      R2TransformOST.newTransform();

    final Integer r = m.withObserver(
      PMatrixI4x4F.identity(),
      R2ProjectionOrthographic.newFrustum(pm),
      mm -> mm.withTransform(t, uv, mi -> Integer.valueOf(23))
    );

    Assert.assertEquals(Integer.valueOf(23), r);
  }

  @Test
  public final void testMatricesObserverTransformCalledTwice()
  {
    final R2MatricesType m = this.newMatrices();
    final JCGLProjectionMatricesType pm =
      JCGLProjectionMatrices.newMatrices();
    final PMatrixReadable3x3FType<R2SpaceTextureType, R2SpaceTextureType> uv =
      PMatrixI3x3F.identity();
    final R2TransformReadableType t =
      R2TransformOST.newTransform();

    this.expected.expect(R2RendererExceptionInstanceAlreadyActive.class);
    this.expected.expectMessage("Instance already active");

    m.withObserver(
      PMatrixI4x4F.identity(),
      R2ProjectionOrthographic.newFrustum(pm),
      mm -> mm.withTransform(t, uv, mi ->
        mm.withTransform(t, uv, mk -> {
          throw new UnreachableCodeException();
        }))
    );
  }
}
