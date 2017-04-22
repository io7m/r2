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
import com.io7m.jtensors.core.parameterized.matrices.PMatrices3x3D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix3x3D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
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

  private R2Matrices()
  {
    this.observer = new Observer();
  }

  /**
   * @return New matrices
   */

  public static R2Matrices create()
  {
    return new R2Matrices();
  }

  private static PMatrix3x3D<R2SpaceObjectType, R2SpaceNormalEyeType>
  makeNormalMatrix(
    final PMatrix4x4D<R2SpaceObjectType, R2SpaceEyeType> m)
  {
    final PMatrix3x3D<R2SpaceNormalEyeType, R2SpaceObjectType> m3 =
      PMatrix3x3D.of(
        m.r0c0(), m.r0c1(), m.r0c2(),
        m.r1c0(), m.r1c1(), m.r1c2(),
        m.r2c0(), m.r2c1(), m.r2c2());
    return PMatrices3x3D.transpose(PMatrices3x3D.invert(m3).get());
  }

  @Override
  public <A, B> B withObserver(
    final PMatrix4x4D<R2SpaceWorldType, R2SpaceEyeType> view,
    final R2ProjectionReadableType projection,
    final A x,
    final BiFunction<R2MatricesObserverType, A, B> f)
  {
    NullCheck.notNull(view, "View");
    NullCheck.notNull(projection, "Projection");
    NullCheck.notNull(x, "Context");
    NullCheck.notNull(f, "Receiver");

    if (this.observer.active) {
      throw new R2RendererExceptionObserverAlreadyActive(
        "Observer already active");
    }

    try {
      this.observer.active = true;
      this.observer.m_view = view;
      this.observer.m_view_inverse = PMatrices4x4D.invert(view).get();

      /*
       * Produce projection and inverse projection matrices.
       */

      this.observer.m_projection = projection.projectionMakeMatrix();
      this.observer.m_projection_inverse =
        PMatrices4x4D.invert(this.observer.m_projection).get();

      /*
       * Recalculate view rays for the given projection.
       */

      this.observer.view_rays.recalculate(this.observer.m_projection_inverse);

      this.observer.projection = projection;
      return f.apply(this.observer, x);
    } finally {
      this.observer.active = false;
    }
  }

  private static final class ProjectiveLight implements
    R2MatricesProjectiveLightType
  {
    private final PMatrix3x3D<R2SpaceObjectType, R2SpaceNormalEyeType> m_normal;
    private final PMatrix3x3D<R2SpaceTextureType, R2SpaceTextureType> m_uv;
    private PMatrix4x4D<R2SpaceEyeType, R2SpaceLightEyeType> m_projective_eye_to_light_eye;
    private PMatrix4x4D<R2SpaceLightEyeType, R2SpaceLightClipType> m_projective_projection;
    private PMatrix4x4D<R2SpaceWorldType, R2SpaceLightEyeType> m_projective_view;
    private PMatrix4x4D<R2SpaceObjectType, R2SpaceWorldType> m_model;
    private PMatrix4x4D<R2SpaceObjectType, R2SpaceEyeType> m_modelview;
    private boolean active;
    private R2ProjectionType projection;

    ProjectiveLight()
    {
      this.active = false;
      this.m_model = PMatrices4x4D.identity();
      this.m_modelview = PMatrices4x4D.identity();
      this.m_projective_eye_to_light_eye = PMatrices4x4D.identity();
      this.m_projective_projection = PMatrices4x4D.identity();
      this.m_projective_view = PMatrices4x4D.identity();
      this.m_normal = PMatrices3x3D.identity();
      this.m_uv = PMatrices3x3D.identity();
    }

    @Override
    public PMatrix4x4D<R2SpaceEyeType, R2SpaceLightEyeType>
    matrixProjectiveEyeToLightEye()
    {
      Preconditions.checkPrecondition(this.active, "Projective must be active");
      return this.m_projective_eye_to_light_eye;
    }

    @Override
    public PMatrix4x4D<R2SpaceLightEyeType,
      R2SpaceLightClipType> matrixProjectiveProjection()
    {
      Preconditions.checkPrecondition(this.active, "Projective must be active");
      return this.m_projective_projection;
    }

    @Override
    public PMatrix4x4D<R2SpaceWorldType, R2SpaceLightEyeType>
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
    public PMatrix4x4D<R2SpaceObjectType, R2SpaceEyeType>
    matrixLightModelView()
    {
      Preconditions.checkPrecondition(this.active, "Projective must be active");
      return this.m_modelview;
    }
  }

  private static final class InstanceSingle implements
    R2MatricesInstanceSingleType
  {
    private PMatrix4x4D<R2SpaceObjectType, R2SpaceWorldType> m_model;
    private PMatrix4x4D<R2SpaceObjectType, R2SpaceEyeType> m_modelview;
    private PMatrix3x3D<R2SpaceObjectType, R2SpaceNormalEyeType> m_normal;
    private PMatrix3x3D<R2SpaceTextureType, R2SpaceTextureType> m_uv;

    private boolean active;

    InstanceSingle()
    {
      this.active = false;
      this.m_model = PMatrices4x4D.identity();
      this.m_modelview = PMatrices4x4D.identity();
      this.m_normal = PMatrices3x3D.identity();
      this.m_uv = PMatrices3x3D.identity();
    }

    @Override
    public PMatrix4x4D<R2SpaceObjectType, R2SpaceEyeType>
    matrixModelView()
    {
      Preconditions.checkPrecondition(this.active, "Instance must be active");
      return this.m_modelview;
    }

    @Override
    public PMatrix3x3D<R2SpaceObjectType, R2SpaceNormalEyeType> matrixNormal()
    {
      Preconditions.checkPrecondition(this.active, "Instance must be active");
      return this.m_normal;
    }

    @Override
    public PMatrix3x3D<R2SpaceTextureType, R2SpaceTextureType> matrixUV()
    {
      Preconditions.checkPrecondition(this.active, "Instance must be active");
      return this.m_uv;
    }
  }

  private static final class VolumeLight implements R2MatricesVolumeLightType
  {
    private PMatrix4x4D<R2SpaceObjectType, R2SpaceWorldType> m_model;
    private PMatrix4x4D<R2SpaceObjectType, R2SpaceEyeType> m_modelview;
    private boolean active;

    VolumeLight()
    {
      this.active = false;
      this.m_model = PMatrices4x4D.identity();
      this.m_modelview = PMatrices4x4D.identity();
    }

    @Override
    public PMatrix4x4D<R2SpaceObjectType, R2SpaceEyeType>
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
    private final InstanceSingle instance_single;
    private final R2ViewRaysType view_rays;
    private final ProjectiveLight projective;
    private final VolumeLight volume;
    private PMatrix4x4D<R2SpaceEyeType, R2SpaceClipType> m_projection;
    private PMatrix4x4D<R2SpaceClipType, R2SpaceEyeType> m_projection_inverse;
    private PMatrix4x4D<R2SpaceWorldType, R2SpaceEyeType> m_view;
    private PMatrix4x4D<R2SpaceEyeType, R2SpaceWorldType> m_view_inverse;
    private boolean active;
    private R2ProjectionReadableType projection;

    private Observer()
    {
      this.active = false;
      this.m_projection = PMatrices4x4D.identity();
      this.m_projection_inverse = PMatrices4x4D.identity();
      this.m_view = PMatrices4x4D.identity();
      this.m_view_inverse = PMatrices4x4D.identity();
      this.instance_single = new InstanceSingle();
      this.projective = new ProjectiveLight();
      this.volume = new VolumeLight();
      this.projection = null;
      this.view_rays = R2ViewRays.newViewRays();
    }

    @Override
    public R2ProjectionReadableType projection()
    {
      Preconditions.checkPrecondition(this.active, "Observer must be active");
      return this.projection;
    }

    @Override
    public PMatrix4x4D<R2SpaceEyeType, R2SpaceClipType>
    matrixProjection()
    {
      Preconditions.checkPrecondition(this.active, "Observer must be active");
      return this.m_projection;
    }

    @Override
    public PMatrix4x4D<R2SpaceClipType, R2SpaceEyeType>
    matrixProjectionInverse()
    {
      Preconditions.checkPrecondition(this.active, "Observer must be active");
      return this.m_projection_inverse;
    }

    @Override
    public PMatrix4x4D<R2SpaceWorldType, R2SpaceEyeType>
    matrixView()
    {
      Preconditions.checkPrecondition(this.active, "Observer must be active");
      return this.m_view;
    }

    @Override
    public PMatrix4x4D<R2SpaceEyeType, R2SpaceWorldType>
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
      final PMatrix3x3D<R2SpaceTextureType, R2SpaceTextureType> uv,
      final A x,
      final BiFunction<R2MatricesInstanceSingleType, A, B> f)
      throws R2Exception
    {
      NullCheck.notNull(t, "Transform");
      NullCheck.notNull(uv, "UV matrix");
      NullCheck.notNull(x, "Context");
      NullCheck.notNull(f, "Receiver");

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

        this.instance_single.m_model = t.transformMakeMatrix4x4F();
        this.instance_single.m_modelview =
          PMatrices4x4D.multiply(this.m_view, this.instance_single.m_model);

        this.instance_single.m_normal =
          makeNormalMatrix(this.instance_single.m_modelview);

        this.instance_single.m_uv = uv;
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
      NullCheck.notNull(t, "Projective");
      NullCheck.notNull(x, "Context");
      NullCheck.notNull(f, "Receiver");

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

        this.projective.m_model = ot.transformMakeMatrix4x4F();
        this.projective.m_modelview =
          PMatrices4x4D.multiply(this.m_view, this.projective.m_model);

        /*
         * Produce a view and projection matrix for the light's projection. The
         * view matrix is based on the light's origin transform.
         */

        this.projective.m_projective_view = ot.transformMakeViewMatrix4x4F();

        final R2ProjectionReadableType p = t.projection();
        this.projective.m_projective_projection =
          PMatrices4x4D.toParameterized(p.projectionMakeMatrixUntyped());

        /*
         * Produce a matrix that transforms a position in eye space to a
         * position in the light's eye space.
         */

        this.projective.m_projective_eye_to_light_eye =
          PMatrices4x4D.multiply(
            this.projective.m_projective_view,
            this.m_view_inverse);

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
      NullCheck.notNull(t, "Light");
      NullCheck.notNull(x, "Context");
      NullCheck.notNull(f, "Receiver");

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

        this.volume.m_model =
          ot.transformMakeMatrix4x4F();
        this.volume.m_modelview =
          PMatrices4x4D.multiply(this.m_view, this.volume.m_model);

        return f.apply(this.volume, x);
      } finally {
        this.volume.active = false;
      }
    }
  }
}
