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

package com.io7m.r2.core.shaders.types;

import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.r2.core.R2LightVolumeSingleReadableType;
import com.io7m.r2.core.R2MatricesVolumeLightValuesType;

/**
 * The type of usable single-instance volume light shaders.
 *
 * @param <M> The type of shader parameters
 */

public interface R2ShaderLightVolumeSingleUsableType<
  M extends R2LightVolumeSingleReadableType>
  extends R2ShaderLightSingleUsableType<M>
{
  /**
   * <p>Set shader values that are derived from the current volume light
   * transform.</p>
   *
   * <p>This method will be called exactly once for each light that uses the
   * shader.</p>
   *
   * <p>This method will be called after a call to {@link
   * R2ShaderUsableType#onActivate(com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type)}
   * and before a call to {@link #onValidate()}.</p>
   *
   * @param g_sh An OpenGL interface
   * @param m    The instance matrices
   */

  void onReceiveVolumeLightTransform(
    JCGLShadersType g_sh,
    R2MatricesVolumeLightValuesType m);
}
