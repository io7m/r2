package com.io7m.r2.tests.jogl.shader_sanity;

import com.io7m.sombrero.core.SoShaderPreprocessorConfig;
import com.io7m.sombrero.core.SoShaderPreprocessorType;
import com.io7m.sombrero.jcpp.SoShaderPreprocessorJCPP;
import com.io7m.sombrero.serviceloader.SoShaderResolverServiceLoader;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

public final class R2ShaderSourceTest
{
  @Test
  public void testFetch()
    throws Exception
  {
    final SoShaderPreprocessorConfig.Builder b =
      SoShaderPreprocessorConfig.builder();
    b.setResolver(SoShaderResolverServiceLoader.create());
    b.setVersion(OptionalInt.of(330));
    final SoShaderPreprocessorType p =
      SoShaderPreprocessorJCPP.create(b.build());

    final List<String> lines = p.preprocessFile(
      Collections.emptyMap(),
      "com.io7m.r2.shaders.geometry.api/R2GeometrySingle.vert");

    Assert.assertFalse(lines.isEmpty());
  }
}
