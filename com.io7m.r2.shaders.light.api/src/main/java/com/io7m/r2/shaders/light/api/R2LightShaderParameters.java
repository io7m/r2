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

package com.io7m.r2.shaders.light.api;

import com.io7m.junreachable.UnreachableCodeException;

import java.util.Map;
import java.util.Objects;

/**
 * Preprocessor defines related to light shaders.
 */

public final class R2LightShaderParameters
{
  private R2LightShaderParameters()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Determine if a given shader environment implies that the light shader to
   * which the environment belongs was compiled to write its output to an image
   * buffer instead of a light buffer.
   *
   * @param environment The shader environment of the shader
   *
   * @return {@code true} if the given shader is writing to an image buffer
   */

  public static boolean lightShaderTargetIsImageBuffer(
    final Map<String, String> environment)
  {
    return Objects.equals(
      R2LightShaderDefines.R2_LIGHT_SHADER_OUTPUT_TARGET_IBUFFER,
      environment.get(R2LightShaderDefines.R2_LIGHT_SHADER_OUTPUT_TARGET_DEFINE));
  }

  /**
   * Determine if a given shader environment implies that the light shader to
   * which the environment belongs was compiled to write its output to a light
   * buffer instead of an image buffer.
   *
   * @param environment The shader environment of the shader
   *
   * @return {@code true} if the given shader is writing to a light buffer
   */

  public static boolean lightShaderTargetIsLightBuffer(
    final Map<String, String> environment)
  {
    return Objects.equals(
      R2LightShaderDefines.R2_LIGHT_SHADER_OUTPUT_TARGET_LBUFFER,
      environment.get(R2LightShaderDefines.R2_LIGHT_SHADER_OUTPUT_TARGET_DEFINE));
  }
}
