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

import com.io7m.jcanephora.core.JCGLFragmentShaderType;
import com.io7m.jcanephora.core.JCGLGeometryShaderType;
import com.io7m.jcanephora.core.JCGLProgramShaderType;
import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLVertexShaderType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jnull.NullCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * An abstract shader implementation.
 *
 * @param <M> The type of shader parameters
 */

public abstract class R2AbstractShader<M> implements R2ShaderType<M>
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2AbstractShader.class);
  }

  private final long                  id;
  private final JCGLProgramShaderType program;
  private       boolean               deleted;

  protected R2AbstractShader(
    final JCGLShadersType in_shaders,
    final R2ShaderSourcesType in_sources,
    final R2IDPoolType in_pool,
    final String in_name,
    final String in_vertex,
    final Optional<String> in_geometry,
    final String in_fragment)
  {
    this.id = NullCheck.notNull(in_pool).getFreshID();
    NullCheck.notNull(in_sources);

    if (R2AbstractShader.LOG.isDebugEnabled()) {
      R2AbstractShader.LOG.debug("compiling shader {}", in_name);
    }

    final JCGLVertexShaderType v =
      in_shaders.shaderCompileVertex(
        in_vertex, in_sources.getSourceLines(in_vertex));
    final Optional<JCGLGeometryShaderType> g =
      in_geometry.map(
        s -> in_shaders.shaderCompileGeometry(s, in_sources.getSourceLines(s)));
    final JCGLFragmentShaderType f =
      in_shaders.shaderCompileFragment(
        in_fragment, in_sources.getSourceLines(in_fragment));

    this.program = in_shaders.shaderLinkProgram(in_name, v, g.map(s -> s), f);
    in_shaders.shaderDeleteVertex(v);
    g.ifPresent(in_shaders::shaderDeleteGeometry);
    in_shaders.shaderDeleteFragment(f);

    this.deleted = false;
  }

  @Override
  public final boolean isDeleted()
  {
    return this.deleted;
  }

  @Override
  public final void delete(final JCGLInterfaceGL33Type g)
  {
    NullCheck.notNull(g);
    if (!this.isDeleted()) {
      try {
        final JCGLShadersType shaders = g.getShaders();
        shaders.shaderDeleteProgram(this.program);
      } finally {
        this.deleted = true;
      }
    }
  }

  @Override
  public final JCGLProgramShaderUsableType getShaderProgram()
  {
    return this.program;
  }

  @Override
  public final long getShaderID()
  {
    return this.id;
  }
}
