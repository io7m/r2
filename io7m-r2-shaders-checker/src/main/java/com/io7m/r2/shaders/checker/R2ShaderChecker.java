/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.shaders.checker;

import com.io7m.jcanephora.core.JCGLException;
import com.io7m.jcanephora.core.JCGLFragmentShaderType;
import com.io7m.jcanephora.core.JCGLGeometryShaderType;
import com.io7m.jcanephora.core.JCGLProgramShaderType;
import com.io7m.jcanephora.core.JCGLVertexShaderType;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jnull.NullCheck;

import java.util.List;
import java.util.Optional;

/**
 * The default implementation of the {@link R2ShaderCheckerType} interface.
 */

public final class R2ShaderChecker implements R2ShaderCheckerType
{
  private final JCGLShadersType shaders;

  private R2ShaderChecker(
    final JCGLShadersType in_shaders)
  {
    this.shaders = NullCheck.notNull(in_shaders);
  }

  /**
   * @param in_shaders A shader interface
   *
   * @return A new checker
   */

  public static R2ShaderCheckerType newChecker(
    final JCGLShadersType in_shaders)
  {
    return new R2ShaderChecker(in_shaders);
  }

  @Override public void check(
    final List<String> vertex,
    final Optional<List<String>> geom,
    final List<String> frag)
    throws R2ShaderCheckerException
  {
    try {
      final JCGLVertexShaderType v =
        this.shaders.shaderCompileVertex("v", vertex);
      final Optional<JCGLGeometryShaderType> g =
        geom.map(gg -> this.shaders.shaderCompileGeometry("g", gg));
      final JCGLFragmentShaderType f =
        this.shaders.shaderCompileFragment("f", frag);
      final JCGLProgramShaderType p =
        this.shaders.shaderLinkProgram("p", v, g.map(x -> x), f);
      this.shaders.shaderDeleteProgram(p);
    } catch (final JCGLException e) {
      throw new R2ShaderCheckerException(e);
    }
  }
}
