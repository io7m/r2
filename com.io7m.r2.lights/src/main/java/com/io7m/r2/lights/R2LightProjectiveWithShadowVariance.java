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

package com.io7m.r2.lights;

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jfunctional.PartialBiFunctionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.lights.shadows.R2ShadowDepthVarianceType;
import com.io7m.r2.projections.R2ProjectionMeshReadableType;
import com.io7m.r2.projections.R2ProjectionReadableType;
import com.io7m.r2.textures.R2Texture2DUsableType;
import com.io7m.r2.transforms.R2TransformOT;
import com.io7m.r2.transforms.R2TransformOTReadableType;
import com.io7m.r2.transforms.R2TransformOTType;
import com.io7m.r2.spaces.R2SpaceRGBType;
import com.io7m.r2.spaces.R2SpaceWorldType;

import java.util.Objects;

/**
 * Parameters for simple projective lights that operate by rendering frustum
 * meshes and that have variance shadows.
 */

public final class R2LightProjectiveWithShadowVariance implements
  R2LightProjectiveWithShadowVarianceType
{
  private final long id;
  private final R2TransformOT transform;
  private final R2ProjectionMeshReadableType mesh;
  private final R2ProjectionReadableType projection;
  private final R2Texture2DUsableType image;
  private final R2ShadowDepthVarianceType shadow;
  private PVector3D<R2SpaceRGBType> color;
  private double falloff;
  private double intensity;
  private double radius;

  private R2LightProjectiveWithShadowVariance(
    final R2ProjectionMeshReadableType in_mesh,
    final R2Texture2DUsableType in_image,
    final R2ShadowDepthVarianceType in_shadow,
    final long in_id)
  {
    this.mesh = NullCheck.notNull(in_mesh, "Mesh");
    this.image = NullCheck.notNull(in_image, "Image");
    this.shadow = NullCheck.notNull(in_shadow, "Shadow");

    this.projection = this.mesh.projectionReadable();
    this.id = in_id;
    this.transform = R2TransformOT.create();
    this.color = PVector3D.of(1.0, 1.0, 1.0);
    this.intensity = 1.0;
    this.falloff = 1.0;
  }

  /**
   * Construct a new light.
   *
   * @param in_mesh   The light projection mesh
   * @param in_image  The image that will be projected by the light
   * @param in_shadow The shadow
   * @param in_pool   The ID pool
   *
   * @return A new light
   */

  public static R2LightProjectiveWithShadowVariance create(
    final R2ProjectionMeshReadableType in_mesh,
    final R2Texture2DUsableType in_image,
    final R2ShadowDepthVarianceType in_shadow,
    final R2IDPoolType in_pool)
  {
    NullCheck.notNull(in_pool, "Pool");
    NullCheck.notNull(in_mesh, "Mesh");
    NullCheck.notNull(in_shadow, "Shadow");

    final JCGLTexture2DUsableType t = in_image.texture();
    final JCGLTextureWrapS wrap_s = t.wrapS();
    final JCGLTextureWrapT wrap_t = t.wrapT();

    Preconditions.checkPrecondition(
      wrap_s,
      Objects.equals(wrap_s, JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE),
      m -> "Wrapping mode should be CLAMP_TO_EDGE (is " + m + ")");
    Preconditions.checkPrecondition(
      wrap_t,
      Objects.equals(wrap_t, JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE),
      m -> "Wrapping mode should be CLAMP_TO_EDGE (is " + m + ")");

    return new R2LightProjectiveWithShadowVariance(
      in_mesh, in_image, in_shadow, in_pool.freshID());
  }

  @Override
  public double radius()
  {
    return this.radius;
  }

  @Override
  public void setRadius(
    final double r)
  {
    this.radius = Math.max(0.001, r);
  }

  @Override
  public R2TransformOTType transformWritable()
  {
    return this.transform;
  }

  @Override
  public double falloff()
  {
    return this.falloff;
  }

  @Override
  public void setFalloff(final double f)
  {
    this.falloff = f;
  }

  @Override
  public PVector3D<R2SpaceRGBType> color()
  {
    return this.color;
  }

  @Override
  public double intensity()
  {
    return this.intensity;
  }

  @Override
  public void setColor(
    final PVector3D<R2SpaceRGBType> in_color)
  {
    this.color = NullCheck.notNull(in_color, "Color");
  }

  @Override
  public void setIntensity(
    final double i)
  {
    this.intensity = Math.max(0.0, i);
  }

  @Override
  public JCGLArrayObjectUsableType arrayObject()
  {
    return this.mesh.arrayObject();
  }

  @Override
  public R2TransformOTReadableType transform()
  {
    return this.transform;
  }

  @Override
  public PVector3D<R2SpaceWorldType> position()
  {
    return this.transform.translation();
  }

  @Override
  public long lightID()
  {
    return this.id;
  }

  @Override
  public R2ProjectionReadableType projection()
  {
    return this.projection;
  }

  @Override
  public R2Texture2DUsableType image()
  {
    return this.image;
  }

  @Override
  public R2ShadowDepthVarianceType shadow()
  {
    return this.shadow;
  }

  @Override
  public <A, B, E extends Throwable> B matchLightWithShadow(
    final A context,
    final PartialBiFunctionType<A, R2LightProjectiveWithShadowType, B, E>
      on_project)
    throws E
  {
    return on_project.call(context, this);
  }
}
