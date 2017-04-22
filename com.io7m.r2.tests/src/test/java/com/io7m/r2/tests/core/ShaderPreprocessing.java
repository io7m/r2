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


package com.io7m.r2.tests.core;

import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironment;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.sombrero.core.SoShaderPreprocessorConfig;
import com.io7m.sombrero.core.SoShaderPreprocessorType;
import com.io7m.sombrero.jcpp.SoShaderPreprocessorJCPP;
import com.io7m.sombrero.serviceloader.SoShaderResolverServiceLoader;

import java.util.OptionalInt;

public final class ShaderPreprocessing
{
  private ShaderPreprocessing()
  {

  }

  public static R2ShaderPreprocessingEnvironmentType preprocessor()
  {
    final SoShaderPreprocessorConfig.Builder b =
      SoShaderPreprocessorConfig.builder();
    b.setResolver(SoShaderResolverServiceLoader.create());
    b.setVersion(OptionalInt.of(330));
    final SoShaderPreprocessorType p =
      SoShaderPreprocessorJCPP.create(b.build());
    return R2ShaderPreprocessingEnvironment.create(p);
  }
}
