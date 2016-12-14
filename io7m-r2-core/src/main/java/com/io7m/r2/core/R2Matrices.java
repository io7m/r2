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

import com.io7m.jaffirm.core.Preconditions;
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
import com.io7m.r2.spaces.R2SpaceLightClipType;
import com.io7m.r2.spaces.R2SpaceLightEyeType;
import com.io7m.r2.spaces.R2SpaceNormalEyeType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import com.io7m.r2.spaces.R2SpaceWorldType;

import java.util.function.BiFunction;

/**
 * Default implementation of the {@link R2MatricesType} interface.
 */

public final class R2Matrices implements R2MatricesType
{
  private final Observer observer;
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
    final BiFunction<R2MatricesObserverType, A, B> f)
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
        this.context_tr.contextMM4F(),
        this.observer.m_view,
        this.observer.m_view_inverse);

      /*
       * Produce projection and inverse projection matrices.
       */

      projection.projectionMakeMatrix(this.observer.m_projection);
      PMatrixM4x4F.invert(
        this.context_tr.contextPM4F(),
        this.observer.m_projection,
        this.observer.m_projection_inverse);

      /*
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
  public R2TransformContextType transformContext()
  {
    return this.context_tr;
  }

  private static final class ProjectiveLight implements
    R2MatricesProjectiveLightType
  {
    private final PMatrixDirect4x4FType<R2SpaceEyeType, R2SpaceLightEyeType> m_projective_eye_to_light_eye;
    private final PMatrixDirect4x4FType<R2SpaceLightEyeType, R2SpaceLightClipType> m_projective_projection;
    private final PMatrixDirect4x4FType<R2SpaceWorldType, R2SpaceLightEyeType> m_projective_view;
    private final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m_model;
    private final PMatrixDirect4x4FType<R2SpaceObjectType, R2SpaceEyeType> m_modelview;
    private final PMatrixDirect3x3FType<R2SpaceObjectType, R2SpaceNormalEyeType> m_normal;
    private final PMatrixDirect3x3FType<R2SpaceTextureType, R2SpaceTextureType> m_uv;
    private boolean active;
    private R2ProjectionType projection;

    ProjectiveLight()
    {
      this.active = false;
      this.m_model = PMatrixHeapArrayM4x4F.newMatrix();
      this.m_modelview = PMatrixDirectM4x4F.newMatrix();
      this.m_projective_eye_to_light_eye = PMatrixDirectM4x4F.newMatrix();
      this.m_projective_projection = PMatrixDirectM4x4F.newMatrix();
      this.m_projective_view = PMatrixDirectM4x4F.newMatrix();
      this.m_normal = PMatrixDirectM3x3F.newMatrix();
      this.m_uv = PMatrixDirectM3x3F.newMatrix();
    }


    @Override
    public PMatrixDirectReadable4x4FType<R2SpaceEyeType, R2SpaceLightEyeType>
    matrixProjectiveEyeToLightEye()
    {
      Preconditions.checkPrecondition(this.active, "Projective must be active");
      return this.m_projective_eye_to_light_eye;
    }

    @Override
    public PMatrixDirectReadable4x4FType<R2SpaceLightEyeType,
      R2SpaceLightClipType> matrixProjectiveProjection()
    {
      Preconditions.checkPrecondition(this.active, "Projective must be active");
      return this.m_projective_projection;
    }

    @Override
    public PMatrixDirectReadable4x4FType<R2SpaceWorldType, R2SpaceLightEyeType>
    matrixProjectiveView()
    {
      Preconditions.checkPrecondition(this.active, "Projective must be active");
      return this.m_projective_view;
    }

    @Override
    public R2ProjectionType projectiveProjection()
    {
      Preconditions.checkPrecondition(this.active, "Projective must be active");
      return this.projection;
    }

    @Override
    public PMatrixDirectReadable4x4FType<R2SpaceObjectType, R2SpaceEyeType>
    matrixLightModelView()
    {
      Preconditions.checkPrecondition(this.active, "Projective must be active");
      return this.m_modelview;
    }
  }

  private static final class InstanceSingle implements
    R2MatricesInstanceSingleType
  {
    private final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m_model;
    private final PMatrixDirect4x4FType<R2SpaceObjectType, R2SpaceEyeType> m_modelview;
    private final PMatrixDirect3x3FType<R2SpaceObjectType, R2SpaceNormalEyeType> m_normal;
    private final PMatrixDirect3x3FType<R2SpaceTextureType, R2SpaceTextureType> m_uv;

    private boolean active;

    InstanceSingle()
    {
      this.active = false;
      this.m_model = PMatrixHeapArrayM4x4F.newMatrix();
      this.m_modelview = PMatrixDirectM4x4F.newMatrix();
      this.m_normal = PMatrixDirectM3x3F.newMatrix();
      this.m_uv = PMatrixDirectM3x3F.newMatrix();
    }

    @Override
    public PMatrixDirectReadable4x4FType<R2SpaceObjectType, R2SpaceEyeType>
    matrixModelView()
    {
      Preconditions.checkPrecondition(this.active, "Instance must be active");
      return this.m_modelview;
    }

    @Override
    public PMatrixDirectReadable3x3FType<R2SpaceObjectType,
      R2SpaceNormalEyeType> matrixNormal()
    {
      Preconditions.checkPrecondition(this.active, "Instance must be active");
      return this.m_normal;
    }

    @Override
    public PMatrixDirectReadable3x3FType<R2SpaceTextureType,
      R2SpaceTextureType> matrixUV()
    {
      Preconditions.checkPrecondition(this.active, "Instance must be active");
      return this.m_uv;
    }
  }

  private static final class VolumeLight implements R2MatricesVolumeLightType
  {
    private final PMatrix4x4FType<R2SpaceObjectType, R2SpaceWorldType> m_model;
    private final PMatrixDirect4x4FType<R2SpaceObjectType, R2SpaceEyeType> m_modelview;
    private boolean active;

    VolumeLight()
    {
      this.active = false;
      this.m_model = PMatrixHeapArrayM4x4F.newMatrix();
      this.m_modelview = PMatrixDirectM4x4F.newMatrix();
    }

    @Override
    public PMatrixDirectReadable4x4FType<R2SpaceObjectType, R2SpaceEyeType>
    matrixLightModelView()
    {
      Preconditions.checkPrecondition(
        this.active,
        "Volume light must be active");
      return this.m_modelview;
    }
  }

  private static final class Observer implements R2MatricesObserverType
  {
    private final PMatrixDirect4x4FType<R2SpaceEyeType, R2SpaceClipType> m_projection;
    private final PMatrixDirect4x4FType<R2SpaceClipType, R2SpaceEyeType> m_projection_inverse;
    private final PMatrixDirect4x4FType<R2SpaceWorldType, R2SpaceEyeType> m_view;
    private final PMatrixDirect4x4FType<R2SpaceEyeType, R2SpaceWorldType> m_view_inverse;

    private final InstanceSingle instance_single;
    private final R2TransformContextType context_tr;
    private final MatrixM3x3F.ContextMM3F context_3f;
    private final PMatrixM4x4F.ContextPM4F context_p4f;
    private final R2ViewRaysType view_rays;
    private final ProjectiveLight projective;
    private final VolumeLight volume;
    private boolean active;
    private R2ProjectionReadableType projection;

    private Observer(
      final R2TransformContextType in_context_tr)
    {
      this.active = false;
      this.m_projection = PMatrixDirectM4x4F.newMatrix();
      this.m_projection_inverse = PMatrixDirectM4x4F.newMatrix();
      this.m_view = PMatrixDirectM4x4F.newMatrix();
      this.m_view_inverse = PMatrixDirectM4x4F.newMatrix();
      this.instance_single = new InstanceSingle();
      this.projective = new ProjectiveLight();
      this.volume = new VolumeLight();
      this.context_tr = NullCheck.notNull(in_context_tr);
      this.projection = null;
      this.context_3f = new MatrixM3x3F.ContextMM3F();
      this.context_p4f = new PMatrixM4x4F.ContextPM4F();
      this.view_rays = R2ViewRays.newViewRays(this.context_p4f);
    }

    @Override
    public R2ProjectionReadableType projection()
    {
      Preconditions.checkPrecondition(this.active, "Observer must be active");
      return this.projection;
    }

    @Override
    public PMatrixDirectReadable4x4FType<R2SpaceEyeType, R2SpaceClipType>
    matrixProjection()
    {
      Preconditions.checkPrecondition(this.active, "Observer must be active");
      return this.m_projection;
    }

    @Override
    public PMatrixDirectReadable4x4FType<R2SpaceClipType, R2SpaceEyeType>
    matrixProjectionInverse()
    {
      Preconditions.checkPrecondition(this.active, "Observer must be active");
      return this.m_projection_inverse;
    }

    @Override
    public PMatrixDirectReadable4x4FType<R2SpaceWorldType, R2SpaceEyeType>
    matrixView()
    {
      Preconditions.checkPrecondition(this.active, "Observer must be active");
      return this.m_view;
    }

    @Override
    public PMatrixDirectReadable4x4FType<R2SpaceEyeType, R2SpaceWorldType>
    matrixViewInverse()
    {
      Preconditions.checkPrecondition(this.active, "Observer must be active");
      return this.m_view_inverse;
    }

    @Override
    public R2ViewRaysReadableType viewRays()
    {
      Preconditions.checkPrecondition(this.active, "Observer must be active");
      return this.view_rays;
    }

    @Override
    public <A, B> B withTransform(
      final R2TransformReadableType t,
      final PMatrixReadable3x3FType<R2SpaceTextureType, R2SpaceTextureType> uv,
      final A x,
      final BiFunction<R2MatricesInstanceSingleType, A, B> f)
      throws R2Exception
    {
      NullCheck.notNull(t);
      NullCheck.notNull(uv);
      NullCheck.notNull(x);
      NullCheck.notNull(f);

      Preconditions.checkPrecondition(this.active, "Observer must be active");

      if (this.instance_single.active) {
        throw new R2RendererExceptionInstanceAlreadyActive(
          "Instance already active");
      }

      if (this.projective.active) {
        throw new R2RendererExceptionProjectiveAlreadyActive(
          "Projective already active");
      }

      if (this.volume.active) {
        throw new R2RendererExceptionVolumeLightAlreadyActive(
          "Volume light already active");
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

        makeNormalMatrix(
          this.context_3f,
          this.instance_single.m_modelview,
          this.instance_single.m_normal);

        MatrixM3x3F.copy(uv, this.instance_single.m_uv);

        return f.apply(this.instance_single, x);
      } finally {
        this.instance_single.active = false;
      }
    }

    @Override
    public <A, B> B withProjectiveLight(
      final R2LightProjectiveReadableType t,
      final A x,
      final BiFunction<R2MatricesProjectiveLightType, A, B> f)
      throws R2Exception
    {
      NullCheck.notNull(t);
      NullCheck.notNull(x);
      NullCheck.notNull(f);

      Preconditions.checkPrecondition(this.active, "Observer must be active");

      if (this.instance_single.active) {
        throw new R2RendererExceptionInstanceAlreadyActive(
          "Instance already active");
      }

      if (this.projective.active) {
        throw new R2RendererExceptionProjectiveAlreadyActive(
          "Projective already active");
      }

      if (this.volume.active) {
        throw new R2RendererExceptionVolumeLightAlreadyActive(
          "Volume light already active");
      }

      try {
        this.projective.active = true;

        /*
         * Produce a modelview matrix for the light.
         */

        final R2TransformViewReadableType ot = t.transform();

        ot.transformMakeMatrix4x4F(
          this.context_tr, this.projective.m_model);

        PMatrixM4x4F.multiply(
          this.m_view,
          this.projective.m_model,
          this.projective.m_modelview);

        /*
         * Produce a view and projection matrix for the light's projection. The
         * view matrix is based on the light's origin transform.
         */

        ot.transformMakeViewMatrix4x4F(
          this.context_tr,
          this.projective.m_projective_view);

        final R2ProjectionReadableType p = t.projection();

        p.projectionMakeMatrixUntyped(
          this.projective.m_projective_projection);

        /*
         * Produce a matrix that transforms a position in eye space to a
         * position in the light's eye space.
         */

        PMatrixM4x4F.multiply(
          this.projective.m_projective_view,
          this.m_view_inverse,
          this.projective.m_projective_eye_to_light_eye);

        return f.apply(this.projective, x);
      } finally {
        this.projective.active = false;
      }
    }

    @Override
    public <A, B> B withVolumeLight(
      final R2LightVolumeSingleReadableType t,
      final A x,
      final BiFunction<R2MatricesVolumeLightType, A, B> f)
      throws R2Exception
    {
      NullCheck.notNull(t);
      NullCheck.notNull(x);
      NullCheck.notNull(f);

      Preconditions.checkPrecondition(this.active, "Observer must be active");

      if (this.instance_single.active) {
        throw new R2RendererExceptionInstanceAlreadyActive(
          "Instance already active");
      }

      if (this.projective.active) {
        throw new R2RendererExceptionProjectiveAlreadyActive(
          "Projective already active");
      }

      if (this.volume.active) {
        throw new R2RendererExceptionVolumeLightAlreadyActive(
          "Volume light already active");
      }

      try {
        this.volume.active = true;

        /*
         * Produce a modelview matrix for the light.
         */

        final R2TransformReadableType ot = t.transform();

        ot.transformMakeMatrix4x4F(
          this.context_tr, this.volume.m_model);

        PMatrixM4x4F.multiply(
          this.m_view,
          this.volume.m_model,
          this.volume.m_modelview);

        return f.apply(this.volume, x);
      } finally {
        this.volume.active = false;
      }
    }

    @Override
    public R2TransformContextType transformContext()
    {
      return this.context_tr;
    }
  }
}
