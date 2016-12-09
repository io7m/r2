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

import com.io7m.jnull.NullCheck;
import com.io7m.sombrero.core.SoShaderPreprocessorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The default implementation of the {@link R2ShaderPreprocessingEnvironmentType}
 * interface.
 */

public final class R2ShaderPreprocessingEnvironment implements
  R2ShaderPreprocessingEnvironmentType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2ShaderPreprocessingEnvironment.class);
  }

  private final Map<String, String> defines;
  private final Map<String, String> defines_view;
  private final SoShaderPreprocessorType preproc;
  private R2ShaderPreprocessingEnvironment(
    final SoShaderPreprocessorType in_preproc)
  {
    this.defines = new HashMap<>(16);
    this.defines_view = Collections.unmodifiableMap(this.defines);
    this.preproc = NullCheck.notNull(in_preproc, "Preproc");
  }

  /**
   * Create a new environment.
   *
   * @param in_preproc The preprocessor
   *
   * @return A new empty environment
   */

  public static R2ShaderPreprocessingEnvironmentType create(
    final SoShaderPreprocessorType in_preproc)
  {
    return new R2ShaderPreprocessingEnvironment(in_preproc);
  }

  @Override
  public void preprocessorDefineSet(
    final String name,
    final String value)
  {
    NullCheck.notNull(name, "name");
    NullCheck.notNull(value, "value");

    if (LOG.isTraceEnabled()) {
      LOG.trace("preprocessor set: {} {}", name, value);
    }

    this.defines.put(name, value);
  }

  @Override
  public void preprocessorDefineUnset(
    final String name)
  {
    NullCheck.notNull(name, "name");

    if (LOG.isTraceEnabled()) {
      LOG.trace("preprocessor unset: {}", name);
    }

    this.defines.remove(name);
  }

  @Override
  public void preprocessorDefinesClear()
  {
    this.defines.clear();
  }

  @Override
  public Map<String, String> preprocessorDefines()
  {
    return this.defines_view;
  }

  @Override
  public SoShaderPreprocessorType preprocessor()
  {
    return this.preproc;
  }
}
