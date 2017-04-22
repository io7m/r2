/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.facade;

import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2LightAmbientScreenSingle;
import com.io7m.r2.core.R2LightProjectiveWithShadowVariance;
import com.io7m.r2.core.R2LightSphericalSingle;
import com.io7m.r2.core.R2ProjectionMeshType;
import com.io7m.r2.core.R2ShadowDepthVariance;
import com.io7m.r2.core.R2Texture2DUsableType;
import org.immutables.value.Value;

/**
 * The type of convenient light providers.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2FacadeLightProviderType
{
  /**
   * @return The current facade
   */

  @Value.Auxiliary
  @Value.Parameter
  R2FacadeType main();

  /**
   * @return A new screen-based ambient light
   */

  default R2LightAmbientScreenSingle createAmbientScreenSingle()
  {
    return R2LightAmbientScreenSingle.create(
      this.main().unitQuad(),
      this.main().idPool(),
      this.main().textureDefaults());
  }

  /**
   * @return A new spherical light
   */

  default R2LightSphericalSingle createSphericalSingle()
  {
    return R2LightSphericalSingle.newLight(
      this.main().unitSphere8(),
      this.main().idPool());
  }

  /**
   * Create a new projective light with a variance shadow.
   *
   * @param proj_mesh   The projection mesh
   * @param texture     The projective texture
   * @param proj_shadow The shadow definition
   *
   * @return A projective light with a variance shadow
   */

  default R2LightProjectiveWithShadowVariance createProjectiveWithShadowVariance(
    final R2ProjectionMeshType proj_mesh,
    final R2Texture2DUsableType texture,
    final R2ShadowDepthVariance proj_shadow)
  {
    return R2LightProjectiveWithShadowVariance.create(
      proj_mesh,
      texture,
      proj_shadow,
      this.main().idPool());
  }

  /**
   * Create a new projective light with a variance shadow.
   *
   * @param proj_mesh   The projection mesh
   * @param proj_shadow The shadow definition
   *
   * @return A projective light with a variance shadow
   */

  default R2LightProjectiveWithShadowVariance createProjectiveWithShadowVariance(
    final R2ProjectionMeshType proj_mesh,
    final R2ShadowDepthVariance proj_shadow)
  {
    return this.createProjectiveWithShadowVariance(
      proj_mesh,
      this.main().textureDefaults().whiteProjective2D(),
      proj_shadow);
  }
}
