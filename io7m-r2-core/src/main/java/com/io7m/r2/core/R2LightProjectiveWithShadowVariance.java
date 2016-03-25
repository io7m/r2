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

import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jfunctional.PartialBiFunctionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.r2.spaces.R2SpaceRGBType;
import org.valid4j.Assertive;

/**
 * Parameters for simple projective lights that operate by rendering frustum
 * meshes and that have variance shadows.
 */

public final class R2LightProjectiveWithShadowVariance implements
  R2LightProjectiveWithShadowVarianceType
{
  private final PVector3FType<R2SpaceRGBType> color;
  private final long id;
  private final R2TransformOT transform;
  private final R2ProjectionMeshReadableType mesh;
  private final R2ProjectionReadableType projection;
  private final R2Texture2DUsableType image;
  private final R2ShadowDepthVarianceType shadow;
  private float falloff;
  private float intensity;
  private float radius;

  private R2LightProjectiveWithShadowVariance(
    final R2ProjectionMeshReadableType in_mesh,
    final R2Texture2DUsableType in_image,
    final R2ShadowDepthVarianceType in_shadow,
    final long in_id)
  {
    this.mesh = NullCheck.notNull(in_mesh);
    this.image = NullCheck.notNull(in_image);
    this.shadow = NullCheck.notNull(in_shadow);

    this.projection = this.mesh.getProjectionReadable();
    this.id = in_id;
    this.transform = R2TransformOT.newTransform();
    this.color = new PVectorM3F<>(1.0f, 1.0f, 1.0f);
    this.intensity = 1.0f;
    this.falloff = 1.0f;
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

  public static R2LightProjectiveWithShadowVarianceType newLight(
    final R2ProjectionMeshReadableType in_mesh,
    final R2Texture2DUsableType in_image,
    final R2ShadowDepthVarianceType in_shadow,
    final R2IDPoolType in_pool)
  {
    NullCheck.notNull(in_pool);
    NullCheck.notNull(in_mesh);
    NullCheck.notNull(in_shadow);

    final JCGLTexture2DUsableType t = in_image.get();
    final JCGLTextureWrapS wrap_s = t.textureGetWrapS();
    final JCGLTextureWrapT wrap_t = t.textureGetWrapT();
    Assertive.ensure(
      wrap_s.equals(JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE),
      "Wrapping mode should be CLAMP_TO_EDGE (is %s)", wrap_s);
    Assertive.ensure(
      wrap_t.equals(JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE),
      "Wrapping mode should be CLAMP_TO_EDGE (is %s)", wrap_t);

    return new R2LightProjectiveWithShadowVariance(
      in_mesh,
      in_image,
      in_shadow,
      in_pool.getFreshID());
  }

  @Override
  public float getRadius()
  {
    return this.radius;
  }

  @Override
  public void setRadius(
    final float r)
  {
    this.radius = Math.max(0.001f, r);
  }

  @Override
  public R2TransformOTType getTransformWritable()
  {
    return this.transform;
  }

  @Override
  public float getFalloff()
  {
    return this.falloff;
  }

  @Override
  public void setFalloff(final float f)
  {
    this.falloff = f;
  }

  @Override
  public PVector3FType<R2SpaceRGBType> getColor()
  {
    return this.color;
  }

  @Override
  public float getIntensity()
  {
    return this.intensity;
  }

  @Override
  public void setIntensity(
    final float i)
  {
    this.intensity = Math.max(0.0f, i);
  }

  @Override
  public JCGLArrayObjectUsableType getArrayObject()
  {
    return this.mesh.getArrayObject();
  }

  @Override
  public long getLightID()
  {
    return this.id;
  }

  @Override
  public R2TransformOTReadableType getTransform()
  {
    return this.transform;
  }

  @Override
  public <A, B, E extends Throwable> B matchLightSingle(
    final A context,
    final PartialBiFunctionType<A, R2LightVolumeSingleType, B, E> on_volume,
    final PartialBiFunctionType<A, R2LightScreenSingleType, B, E> on_screen)
    throws E
  {
    return on_volume.call(context, this);
  }

  @Override
  public R2ProjectionReadableType getProjection()
  {
    return this.projection;
  }

  @Override
  public R2Texture2DUsableType getImage()
  {
    return this.image;
  }

  @Override
  public R2ShadowDepthVarianceType getShadow()
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
