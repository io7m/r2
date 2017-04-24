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

package com.io7m.r2.core.shaders.abstracts;

import com.io7m.jcanephora.core.JCGLFragmentShaderType;
import com.io7m.jcanephora.core.JCGLGeometryShaderType;
import com.io7m.jcanephora.core.JCGLProgramShaderType;
import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLVertexShaderType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jfsm.core.FSMEnumMutable;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2ExceptionShaderPreprocessingFailed;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.core.shaders.types.R2ShaderType;
import com.io7m.sombrero.core.SoShaderException;
import com.io7m.sombrero.core.SoShaderPreprocessorType;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMaps;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An abstract shader implementation.
 *
 * @param <S> The type of shader state identifiers
 * @param <M> The type of shader parameters
 */

public abstract class R2AbstractShader<S extends Enum<S>, M> implements
  R2ShaderType<M>
{
  private static final Logger LOG;
  private static final Logger LOG_VS;
  private static final Logger LOG_FS;
  private static final Logger LOG_GS;

  static {
    LOG = LoggerFactory.getLogger(
      R2AbstractShader.class);
    LOG_VS = LoggerFactory.getLogger(
      R2AbstractShader.class.getCanonicalName() + ".vertex");
    LOG_FS = LoggerFactory.getLogger(
      R2AbstractShader.class.getCanonicalName() + ".fragment");
    LOG_GS = LoggerFactory.getLogger(
      R2AbstractShader.class.getCanonicalName() + ".geometry");
  }

  private final long id;
  private final JCGLProgramShaderType program;
  private final FSMEnumMutable<S> fsm;
  private final Object2ReferenceMap<String, String> environment;
  private boolean deleted;

  protected R2AbstractShader(
    final JCGLShadersType in_shaders,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool,
    final String in_name,
    final String in_vertex,
    final Optional<String> in_geometry,
    final String in_fragment)
    throws R2ExceptionShaderPreprocessingFailed
  {
    try {
      this.id = NullCheck.notNull(in_pool, "ID Pool").freshID();
      NullCheck.notNull(in_shader_env, "Shader environment");

      if (LOG.isDebugEnabled()) {
        LOG.debug("compiling shader {}", in_name);
      }

      final SoShaderPreprocessorType pp =
        in_shader_env.preprocessor();
      final Map<String, String> pp_defines =
        in_shader_env.preprocessorDefines();

      final List<String> v_lines =
        pp.preprocessFile(pp_defines, in_vertex);

      if (LOG_VS.isTraceEnabled()) {
        for (int index = 0; index < v_lines.size(); ++index) {
          LOG_VS.trace(
            "{}:{}: {}",
            in_vertex,
            Integer.valueOf(index + 1),
            v_lines.get(index).replaceAll("\\s+$", ""));
        }
      }

      final JCGLVertexShaderType v =
        in_shaders.shaderCompileVertex(in_vertex, v_lines);

      final Optional<JCGLGeometryShaderType> g;
      if (in_geometry.isPresent()) {
        final String s = in_geometry.get();
        final List<String> g_lines = pp.preprocessFile(pp_defines, s);

        if (LOG_GS.isTraceEnabled()) {
          for (int index = 0; index < g_lines.size(); ++index) {
            LOG_GS.trace(
              "{}:{}: {}",
              s,
              Integer.valueOf(index + 1),
              g_lines.get(index).replaceAll("\\s+$", ""));
          }
        }

        g = Optional.of(in_shaders.shaderCompileGeometry(s, g_lines));
      } else {
        g = Optional.empty();
      }

      final List<String> f_lines =
        pp.preprocessFile(pp_defines, in_fragment);

      if (LOG_FS.isTraceEnabled()) {
        for (int index = 0; index < f_lines.size(); ++index) {
          LOG_FS.trace(
            "{}:{}: {}",
            in_fragment,
            Integer.valueOf(index + 1),
            f_lines.get(index).replaceAll("\\s+$", ""));
        }
      }

      final JCGLFragmentShaderType f =
        in_shaders.shaderCompileFragment(in_fragment, f_lines);

      this.program = in_shaders.shaderLinkProgram(in_name, v, g.map(s -> s), f);
      in_shaders.shaderDeleteVertex(v);
      g.ifPresent(in_shaders::shaderDeleteGeometry);
      in_shaders.shaderDeleteFragment(f);

      this.environment =
        Object2ReferenceMaps.unmodifiable(
          new Object2ReferenceOpenHashMap<>(pp_defines));

      this.fsm = this.onCheckGetFSM();
      this.deleted = false;
    } catch (final SoShaderException e) {
      throw new R2ExceptionShaderPreprocessingFailed(e);
    }
  }

  @Override
  public final Map<String, String> environment()
  {
    return this.environment;
  }

  protected abstract FSMEnumMutable<S> onCheckGetFSM();

  protected abstract void onCheckActivated();

  protected abstract void onCheckDeactivated();

  protected abstract void onCheckValidated();

  @Override
  public final void onValidate()
    throws R2ExceptionShaderValidationFailed
  {
    this.onCheckValidated();
  }

  @Override
  public final boolean isDeleted()
  {
    return this.deleted;
  }

  @Override
  public final void onActivate(
    final JCGLInterfaceGL33Type g)
  {
    this.onCheckActivated();
    g.shaders().shaderActivateProgram(this.program);
  }

  @Override
  public final void onDeactivate(
    final JCGLInterfaceGL33Type g)
  {
    this.onCheckDeactivated();
    g.shaders().shaderDeactivateProgram();
  }

  @Override
  public final void delete(
    final JCGLInterfaceGL33Type g)
  {
    NullCheck.notNull(g, "G33");
    if (!this.isDeleted()) {
      try {
        final JCGLShadersType shaders = g.shaders();
        shaders.shaderDeleteProgram(this.program);
      } finally {
        this.deleted = true;
      }
    }
  }

  @Override
  public final JCGLProgramShaderUsableType shaderProgram()
  {
    return this.program;
  }

  @Override
  public final long shaderID()
  {
    return this.id;
  }
}
