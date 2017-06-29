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

import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.shaders.light.R2LightShaderAmbientSingle;
import com.io7m.r2.shaders.light.R2LightShaderProjectiveLambertBlinnPhongShadowVarianceSingle;
import com.io7m.r2.shaders.light.R2LightShaderProjectiveLambertBlinnPhongSingle;
import com.io7m.r2.shaders.light.R2LightShaderProjectiveLambertShadowVarianceSingle;
import com.io7m.r2.shaders.light.R2LightShaderProjectiveLambertSingle;
import com.io7m.r2.shaders.light.R2LightShaderSphericalLambertBlinnPhongSingle;
import com.io7m.r2.shaders.light.R2LightShaderSphericalLambertPhongSingle;
import com.io7m.r2.shaders.light.R2LightShaderSphericalLambertSingle;
import org.immutables.value.Value;

/**
 * The type of convenient light shader providers.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2FacadeLightShaderProviderType
{
  /**
   * @return The current facade
   */

  @Value.Auxiliary
  @Value.Parameter
  R2FacadeType main();

  /**
   * @return A spherical Lambert-Blinn-Phong single-instance light shader
   */

  default R2LightShaderSphericalLambertBlinnPhongSingle createSphericalLambertBlinnPhongSingle()
  {
    return R2LightShaderSphericalLambertBlinnPhongSingle.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A spherical Lambert-Phong single-instance light shader
   */

  default R2LightShaderSphericalLambertPhongSingle createSphericalLambertPhongSingle()
  {
    return R2LightShaderSphericalLambertPhongSingle.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A spherical Lambert single-instance light shader
   */

  default R2LightShaderSphericalLambertSingle createSphericalLambertSingle()
  {
    return R2LightShaderSphericalLambertSingle.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return An ambient single-instance light shader
   */

  default R2LightShaderAmbientSingle createAmbientSingle()
  {
    return R2LightShaderAmbientSingle.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A projective light shader with a variance shadow
   */

  default R2LightShaderProjectiveLambertShadowVarianceSingle createProjectiveLambertShadowVarianceSingle()
  {
    return R2LightShaderProjectiveLambertShadowVarianceSingle.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A projective light shader with a variance shadow
   */

  default R2LightShaderProjectiveLambertBlinnPhongShadowVarianceSingle createProjectiveLambertBlinnPhongShadowVarianceSingle()
  {
    return R2LightShaderProjectiveLambertBlinnPhongShadowVarianceSingle.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A projective light shader
   */

  default R2LightShaderProjectiveLambertSingle createProjectiveLambertSingle()
  {
    return R2LightShaderProjectiveLambertSingle.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A projective light shader
   */

  default R2LightShaderProjectiveLambertBlinnPhongSingle createProjectiveLambertBlinnPhongSingle()
  {
    return R2LightShaderProjectiveLambertBlinnPhongSingle.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }
}
