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
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.core.R2ExceptionShaderParameterNotPresent;

import java.util.Map;

/**
 * Convenient functions for dealing with shader parameters.
 */

public final class R2ShaderParameters
{
  private R2ShaderParameters()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Retrieve a uniform parameter for the given program, raising an exception if
   * the parameter is not present for any reason.
   *
   * @param p    The program
   * @param name The parameter name
   *
   * @return The parameter, if it exists
   *
   * @throws R2ExceptionShaderParameterNotPresent Iff the shader parameter was
   *                                              either undeclared or was
   *                                              optimized out
   */

  public static JCGLProgramUniformType getUniformChecked(
    final JCGLProgramShaderUsableType p,
    final String name)
  {
    final Map<String, JCGLProgramUniformType> u = p.getUniforms();
    if (u.containsKey(name)) {
      return u.get(name);
    }

    final StringBuilder sb = new StringBuilder(128);
    sb.append("Shader parameter either undeclared or optimized out.\n");
    sb.append("Program name: ");
    sb.append(p.getName());
    sb.append("\n");
    sb.append("Parameter name: ");
    sb.append(name);
    sb.append("\n");
    throw new R2ExceptionShaderParameterNotPresent(sb.toString());
  }
}
