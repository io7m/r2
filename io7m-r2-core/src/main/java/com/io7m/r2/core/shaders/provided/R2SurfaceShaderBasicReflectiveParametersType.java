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

package com.io7m.r2.core.shaders.provided;

import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2TextureCubeUsableType;
import org.immutables.value.Value;

/**
 * Parameters for the basic reflective surface shader.
 */

@R2ImmutableStyleType
@Value.Immutable
@Value.Modifiable
public interface R2SurfaceShaderBasicReflectiveParametersType
  extends R2SurfaceShaderBasicParametersValuesType
{
  /**
   * <p>An environment map used to simulate reflections on the rendered surface.
   * The resulting surface appearance is a linear mix between the <i>albedo</i>
   * and the reflection values sampled from the environment map, based on {@link
   * #environmentMix()}.</p>
   *
   * @return The environment map
   *
   * @see #albedoMix()
   */

  @Value.Default
  default R2TextureCubeUsableType environmentTexture()
  {
    return this.textureDefaults().textureCubeBlack();
  }

  /**
   * The linear mix between the surface's <i>albedo</i> and calculated
   * reflection values. A value of {@code 0.0} specifies that the appearance
   * of the surface will be entirely defined by the albedo. A value of
   * {@code 1.0} specifies that the appearance of the surface will be entirely
   * defined by the environment reflection.
   *
   * @return A mix value in the range {@code [0.0, 1.0]}
   *
   * @see #environmentTexture()
   */

  @Value.Default
  default float environmentMix()
  {
    return 0.0f;
  }
}
