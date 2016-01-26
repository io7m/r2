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

package com.io7m.r2.core.shaders;

import com.io7m.jnull.NullCheck;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.jtensors.parameterized.PVector4FType;
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.jtensors.parameterized.PVectorM4F;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.spaces.R2SpaceRGBAType;
import com.io7m.r2.spaces.R2SpaceRGBType;

/**
 * Parameters for the basic deferred shader.
 */

public final class R2SurfaceShaderBasicParameters
{
  private       float                          emission;
  private       R2Texture2DUsableType          emission_texture;
  private       R2Texture2DUsableType          albedo_texture;
  private final PVector4FType<R2SpaceRGBAType> albedo_color;
  private       float                          albedo_mix;
  private       R2Texture2DUsableType          specular_texture;
  private final PVector3FType<R2SpaceRGBType>  specular_color;
  private       float                          specular_exponent;
  private       R2Texture2DUsableType          normal_texture;

  private R2SurfaceShaderBasicParameters(
    final PVector4FType<R2SpaceRGBAType> in_albedo_color,
    final float in_albedo_mix,
    final R2Texture2DUsableType in_albedo_texture,
    final float in_emission,
    final R2Texture2DUsableType in_emission_texture,
    final R2Texture2DUsableType in_normal_texture,
    final PVector3FType<R2SpaceRGBType> in_specular_color,
    final float in_specular_exponent,
    final R2Texture2DUsableType in_specular_texture)
  {
    this.albedo_color = NullCheck.notNull(in_albedo_color);
    this.albedo_mix = in_albedo_mix;
    this.albedo_texture = NullCheck.notNull(in_albedo_texture);
    this.emission = in_emission;
    this.emission_texture = NullCheck.notNull(in_emission_texture);
    this.normal_texture = NullCheck.notNull(in_normal_texture);
    this.specular_color = in_specular_color;
    this.specular_exponent = in_specular_exponent;
    this.specular_texture = NullCheck.notNull(in_specular_texture);
  }

  /**
   * Construct a set of default material parameters.
   *
   * @param t The default textures
   *
   * @return A set of default material parameters
   */

  public static R2SurfaceShaderBasicParameters newParameters(
    final R2TextureDefaultsType t)
  {
    NullCheck.notNull(t);

    return new R2SurfaceShaderBasicParameters(
      new PVectorM4F<>(1.0f, 1.0f, 1.0f, 1.0f),
      0.0f,
      t.getWhiteTexture(),
      0.0f,
      t.getWhiteTexture(),
      t.getNormalTexture(),
      new PVectorM3F<>(0.0f, 0.0f, 0.0f),
      0.0f,
      t.getWhiteTexture()
    );
  }

  /**
   * @return Access to the writable albedo color
   */

  public PVector4FType<R2SpaceRGBAType> getAlbedoColor()
  {
    return this.albedo_color;
  }

  /**
   * @return The albedo color/texture mix
   */

  public float getAlbedoMix()
  {
    return this.albedo_mix;
  }

  /**
   * Set the albedo color/texture mix.
   *
   * @param m The mix value
   */

  public void setAlbedoMix(
    final float m)
  {
    this.albedo_mix = m;
  }

  /**
   * @return The albedo texture
   */

  public R2Texture2DUsableType getAlbedoTexture()
  {
    return this.albedo_texture;
  }

  /**
   * Set the albedo texture.
   *
   * @param t The texture
   */

  public void setAlbedoTexture(
    final R2Texture2DUsableType t)
  {
    this.albedo_texture = NullCheck.notNull(t);
  }

  /**
   * @return The emission value
   */

  public float getEmission()
  {
    return this.emission;
  }

  /**
   * Set the emission value.
   *
   * @param e The emission value
   */

  public void setEmission(
    final float e)
  {
    this.emission = e;
  }

  /**
   * @return The emission texture
   */

  public R2Texture2DUsableType getEmissionTexture()
  {
    return this.emission_texture;
  }

  /**
   * Set the emission texture.
   *
   * @param t The texture
   */

  public void setEmissionTexture(
    final R2Texture2DUsableType t)
  {
    this.emission_texture = NullCheck.notNull(t);
  }

  /**
   * @return The normal texture
   */

  public R2Texture2DUsableType getNormalTexture()
  {
    return this.normal_texture;
  }

  /**
   * Set the normal texture.
   *
   * @param t The texture
   */

  public void setNormalTexture(
    final R2Texture2DUsableType t)
  {
    this.normal_texture = NullCheck.notNull(t);
  }

  /**
   * @return Access to the writable specular color
   */

  public PVector3FType<R2SpaceRGBType> getSpecularColor()
  {
    return this.specular_color;
  }

  /**
   * @return The specular exponent value
   */

  public float getSpecularExponent()
  {
    return this.specular_exponent;
  }

  /**
   * Set the specular exponent value.
   *
   * @param e The specular exponent value
   */

  public void setSpecularExponent(
    final float e)
  {
    this.specular_exponent = e;
  }

  /**
   * @return The specular texture
   */

  public R2Texture2DUsableType getSpecularTexture()
  {
    return this.specular_texture;
  }

  /**
   * Set the specular texture.
   *
   * @param t The texture
   */

  public void setSpecularTexture(
    final R2Texture2DUsableType t)
  {
    this.specular_texture = NullCheck.notNull(t);
  }
}
