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
import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.jtensors.parameterized.PVectorReadable3FType;
import com.io7m.r2.spaces.R2SpaceRGBType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * Parameters for simple projective lights that operate by rendering frustum
 * meshes.
 */

public final class R2LightProjectiveWithoutShadow implements
  R2LightProjectiveWithoutShadowType
{
  private final PVector3FType<R2SpaceRGBType> color;
  private final long id;
  private final R2TransformOT transform;
  private final R2ProjectionMeshReadableType mesh;
  private final R2ProjectionReadableType projection;
  private final R2Texture2DUsableType image;
  private float falloff;
  private float intensity;
  private float radius;

  private R2LightProjectiveWithoutShadow(
    final R2ProjectionMeshReadableType in_mesh,
    final R2Texture2DUsableType in_image,
    final long in_id)
  {
    this.mesh = NullCheck.notNull(in_mesh);
    this.image = NullCheck.notNull(in_image);

    this.projection = this.mesh.projectionReadable();
    this.id = in_id;
    this.transform = R2TransformOT.newTransform();
    this.color = new PVectorM3F<>(1.0f, 1.0f, 1.0f);
    this.intensity = 1.0f;
    this.falloff = 1.0f;
  }

  /**
   * Construct a new light.
   *
   * @param in_mesh  The light projection mesh
   * @param in_image The image that will be projected by the light
   * @param in_pool  The ID pool
   *
   * @return A new light
   */

  public static R2LightProjectiveWithoutShadowType newLight(
    final R2ProjectionMeshReadableType in_mesh,
    final R2Texture2DUsableType in_image,
    final R2IDPoolType in_pool)
  {
    NullCheck.notNull(in_pool);
    NullCheck.notNull(in_mesh);

    final JCGLTexture2DUsableType t = in_image.texture();
    final JCGLTextureWrapS wrap_s = t.textureGetWrapS();
    final JCGLTextureWrapT wrap_t = t.textureGetWrapT();

    Preconditions.checkPrecondition(
      wrap_s,
      wrap_s.equals(JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE),
      m -> "Wrapping mode should be CLAMP_TO_EDGE (is " + m + ")");
    Preconditions.checkPrecondition(
      wrap_t,
      wrap_t.equals(JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE),
      m -> "Wrapping mode should be CLAMP_TO_EDGE (is " + m + ")");

    return new R2LightProjectiveWithoutShadow(
      in_mesh,
      in_image,
      in_pool.freshID());
  }

  @Override
  public float radius()
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
  public R2TransformOTType transformWritable()
  {
    return this.transform;
  }

  @Override
  public float falloff()
  {
    return this.falloff;
  }

  @Override
  public void setFalloff(final float f)
  {
    this.falloff = f;
  }

  @Override
  public PVectorReadable3FType<R2SpaceRGBType> color()
  {
    return this.color;
  }

  @Override
  public float intensity()
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
  public PVector3FType<R2SpaceRGBType> colorWritable()
  {
    return this.color;
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
  public PVectorReadable3FType<R2SpaceWorldType> position()
  {
    return this.transform.translationReadable();
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
}
