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

import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLResourceUsableType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;

/**
 * The type of usable shaders.
 *
 * @param <M> The type of parameter data
 */

public interface R2ShaderUsableType<M> extends JCGLResourceUsableType
{
  /**
   * @return The unique shader ID
   */

  long shaderID();

  /**
   * @return The shader parameter type
   */

  Class<M> shaderParametersType();

  /**
   * @return The compiled shader program
   */

  JCGLProgramShaderUsableType shaderProgram();

  /**
   * Activate the shader for rendering.
   *
   * @param g_sh A shader interface
   */

  void onActivate(JCGLShadersType g_sh);

  /**
   * Validate the shader prior to executing a draw call. This method exists so
   * that shader implementations can check that the caller has called all
   * required methods before drawing.
   *
   * @throws R2ExceptionShaderValidationFailed If validation fails
   */

  void onValidate()
    throws R2ExceptionShaderValidationFailed;

  /**
   * Deactivate the shader.
   *
   * @param g_sh A shader interface
   */

  void onDeactivate(JCGLShadersType g_sh);
}
