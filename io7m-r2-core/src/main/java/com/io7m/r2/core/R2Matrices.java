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

package com.io7m.r2.core;

import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.MatrixM3x3F;
import com.io7m.jtensors.MatrixM4x4F;
import com.io7m.jtensors.parameterized.PMatrix3x3FType;
import com.io7m.jtensors.parameterized.PMatrix4x4FType;
import com.io7m.jtensors.parameterized.PMatrixDirect3x3FType;
import com.io7m.jtensors.parameterized.PMatrixDirect4x4FType;
import com.io7m.jtensors.parameterized.PMatrixDirectM3x3F;
import com.io7m.jtensors.parameterized.PMatrixDirectM4x4F;
import com.io7m.jtensors.parameterized.PMatrixDirectReadable3x3FType;
import com.io7m.jtensors.parameterized.PMatrixDirectReadable4x4FType;
import com.io7m.jtensors.parameterized.PMatrixHeapArrayM4x4F;
import com.io7m.jtensors.parameterized.PMatrixM4x4F;
import com.io7m.jtensors.parameterized.PMatrixReadable3x3FType;
import com.io7m.jtensors.parameterized.PMatrixReadable4x4FType;
import com.io7m.r2.spaces.R2SpaceClipType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceNormalEyeType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import org.valid4j.Assertive;

/**
 * Default implementation of the {@link R2MatricesType} interface.
 */

public final class R2Matrices implements R2MatricesType
{
  private final Observer               observer;
  private final R2TransformContextType context_tr;

  private R2Matrices()
  {
    this.context_tr = R2TransformContext.newContext();
    this.observer = new Observer(this.context_tr);
  }

  /**
   * @return New matrices
   */

  public static R2MatricesType newMatrices()
  {
    return new R2Matrices();
  }

  private static void makeNormalMatrix(
    final MatrixM3x3F.ContextMM3F c,
    final PMatrixReadable4x4FType<R2SpaceObjectType, R2SpaceEyeType> m,
    final PMatrix3x3FType<R2SpaceObjectType, R2SpaceNormalEyeType> mr)
  {
    mr.setR0C0F(m.getR0C0F());
    mr.setR1C0F(m.getR1C0F());
    mr.setR2C0F(m.getR2C0F());
    mr.setR0C1F(m.getR0C1F());
    mr.setR1C1F(m.getR1C1F());
    mr.setR2C1F(m.getR2C1F());
    mr.setR0C2F(m.getR0C2F());
    mr.setR1C2F(m.getR1C2F());
    mr.setR2C2F(m.getR2C2F());

    MatrixM3x3F.invertInPlace(c, mr);
    MatrixM3x3F.transposeInPlace(mr);
  }

  @Override
  public <A, B> B withObserver(
    final PMatrixReadable4x4FType<R2SpaceWorldType, R2SpaceEyeType> view,
    final R2ProjectionReadableType projection,
    final A x,
    final R2MatricesObserverFunctionType<A, B> f)
  {
    NullCheck.notNull(view);
    NullCheck.notNull(projection);
    NullCheck.notNull(x);
    NullCheck.notNull(f);

    if (this.observer.active) {
      throw new R2RendererExceptionObserverAlreadyActive(
        "Observer already active");
    }

    try {
      this.observer.active = true;
      MatrixM4x4F.copy(view, this.observer.m_view);
      MatrixM4x4F.invert(
        this.context_tr.getContextMM4F(),
        this.observer.m_view,
        this.observer.m_view_inverse);

      /**
       * Produce projection and inverse projection matrices.
       */

      projection.projectionMakeMatrix(this.observer.m_projection);
      PMatrixM4x4F.invert(
        this.context_tr.getContextPM4F(),
        this.observer.m_projection,
        this.observer.m_projection_inverse);

      /**
       * Recalculate view rays for the given projection.
       */

      this.observer.view_rays.recalculate(
        this.observer.context_p4f,
        this.observer.m_projection_inverse);

      this.observer.projection = projection;
      return f.apply(this.observer, x);
    } finally {
      this.observer.active = false;
    }
  }

  @Override
  public R2TransformContextType getTransformContext()
  {
    return this.context_tr;
  }

  private static final class InstanceSingle implements
    R2MatricesInstanceSingleType
  {
    private final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType>
      m_model;
    private final PMatrixDirect4x4FType<R2SpaceObjectType, R2SpaceEyeType>
      m_modelview;
    private final PMatrixDirect3x3FType<R2SpaceObjectType, R2SpaceNormalEyeType>
      m_normal;

    private boolean active;

    InstanceSingle()
    {
      this.active = false;
      this.m_model = PMatrixHeapArrayM4x4F.newMatrix();
      this.m_modelview = PMatrixDirectM4x4F.newMatrix();
      this.m_normal = PMatrixDirectM3x3F.newMatrix();
    }

    @Override
    public PMatrixDirectReadable4x4FType<R2SpaceObjectType, R2SpaceEyeType>
    getMatrixModelView()
    {
      return this.m_modelview;
    }

    @Override
    public PMatrixDirectReadable3x3FType<R2SpaceObjectType,
      R2SpaceNormalEyeType> getMatrixNormal()
    {
      return this.m_normal;
    }
  }

  private static final class Observer implements R2MatricesObserverType
  {
    private final PMatrixDirect4x4FType<R2SpaceEyeType, R2SpaceClipType>
      m_projection;
    private final PMatrixDirect4x4FType<R2SpaceClipType, R2SpaceEyeType>
      m_projection_inverse;
    private final PMatrixDirect4x4FType<R2SpaceWorldType, R2SpaceEyeType>
      m_view;
    private final PMatrixDirect4x4FType<R2SpaceEyeType, R2SpaceWorldType>
      m_view_inverse;

    private final InstanceSingle           instance_single;
    private final R2TransformContextType   context_tr;
    private final MatrixM3x3F.ContextMM3F  context_3f;
    private final PMatrixM4x4F.ContextPM4F context_p4f;
    private final R2ViewRaysType           view_rays;
    private       boolean                  active;
    private       R2ProjectionReadableType projection;

    private Observer(
      final R2TransformContextType in_context_tr)
    {
      this.active = false;
      this.m_projection = PMatrixDirectM4x4F.newMatrix();
      this.m_projection_inverse = PMatrixDirectM4x4F.newMatrix();
      this.m_view = PMatrixDirectM4x4F.newMatrix();
      this.m_view_inverse = PMatrixDirectM4x4F.newMatrix();
      this.instance_single = new InstanceSingle();
      this.context_tr = NullCheck.notNull(in_context_tr);
      this.projection = null;
      this.context_3f = new MatrixM3x3F.ContextMM3F();
      this.context_p4f = new PMatrixM4x4F.ContextPM4F();
      this.view_rays = R2ViewRays.newViewRays(this.context_p4f);
    }

    @Override
    public R2ProjectionReadableType getProjection()
    {
      return this.projection;
    }

    @Override
    public PMatrixDirectReadable4x4FType<R2SpaceEyeType, R2SpaceClipType>
    getMatrixProjection()
    {
      Assertive.require(this.active, "Observer is active");
      return this.m_projection;
    }

    @Override
    public PMatrixDirectReadable4x4FType<R2SpaceWorldType, R2SpaceEyeType>
    getMatrixView()
    {
      Assertive.require(this.active, "Observer is active");
      return this.m_view;
    }

    @Override
    public PMatrixDirectReadable4x4FType<R2SpaceEyeType, R2SpaceWorldType>
    getMatrixViewInverse()
    {
      Assertive.require(this.active, "Observer is active");
      return this.m_view_inverse;
    }

    @Override
    public R2ViewRaysReadableType getViewRays()
    {
      Assertive.require(this.active, "Observer is active");
      return this.view_rays;
    }

    @Override
    public <A, B> B withTransform(
      final R2TransformReadableType t,
      final PMatrixReadable3x3FType<R2SpaceTextureType, R2SpaceTextureType> uv,
      final A x,
      final R2MatricesInstanceSingleFunctionType<A, B> f)
      throws R2Exception
    {
      NullCheck.notNull(t);
      NullCheck.notNull(uv);
      NullCheck.notNull(x);
      NullCheck.notNull(f);

      Assertive.require(this.active, "Observer is active");

      if (this.instance_single.active) {
        throw new R2RendererExceptionInstanceAlreadyActive(
          "Instance already active");
      }

      try {
        this.instance_single.active = true;

        t.transformMakeMatrix4x4F(
          this.context_tr,
          this.instance_single.m_model);

        PMatrixM4x4F.multiply(
          this.m_view,
          this.instance_single.m_model,
          this.instance_single.m_modelview);

        R2Matrices.makeNormalMatrix(
          this.context_3f,
          this.instance_single.m_modelview,
          this.instance_single.m_normal);

        return f.apply(this.instance_single, x);
      } finally {
        this.instance_single.active = false;
      }
    }

    @Override
    public R2TransformContextType getTransformContext()
    {
      return this.context_tr;
    }
  }
}
