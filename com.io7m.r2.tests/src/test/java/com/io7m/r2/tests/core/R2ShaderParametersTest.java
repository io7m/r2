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

import com.io7m.jcanephora.core.JCGLProgramAttributeType;
import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLReferableType;
import com.io7m.jcanephora.core.JCGLType;
import com.io7m.r2.core.R2ExceptionShaderParameterNotPresent;
import com.io7m.r2.core.R2ExceptionShaderParameterWrongType;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class R2ShaderParametersTest
{
  @Rule public ExpectedException expected = ExpectedException.none();

  public void testShaderParametersNonexistent()
  {
    final JCGLProgramShaderUsableType p = new JCGLProgramShaderUsableType()
    {
      @Override
      public String name()
      {
        return "p";
      }

      @Override
      public Map<String, JCGLProgramAttributeType> attributes()
      {
        return new HashMap<>();
      }

      @Override
      public Map<String, JCGLProgramUniformType> uniforms()
      {
        return new HashMap<>();
      }

      @Override
      public int glName()
      {
        return 1;
      }

      @Override
      public Set<JCGLReferableType> references()
      {
        return new HashSet<>();
      }

      @Override
      public boolean isDeleted()
      {
        return false;
      }
    };

    this.expected.expect(R2ExceptionShaderParameterNotPresent.class);
    R2ShaderParameters.uniform(p, "nonexistent", JCGLType.TYPE_FLOAT);
  }

  @Test
  public void testShaderParametersExists()
  {
    final Map<String, JCGLProgramUniformType> u = new HashMap<>();
    final JCGLProgramShaderUsableType p = new JCGLProgramShaderUsableType()
    {
      @Override
      public String name()
      {
        return "p";
      }

      @Override
      public Map<String, JCGLProgramAttributeType> attributes()
      {
        return new HashMap<>();
      }

      @Override
      public Map<String, JCGLProgramUniformType> uniforms()
      {
        return u;
      }

      @Override
      public int glName()
      {
        return 1;
      }

      @Override
      public Set<JCGLReferableType> references()
      {
        return new HashSet<>();
      }

      @Override
      public boolean isDeleted()
      {
        return false;
      }
    };

    final JCGLProgramUniformType uu = new JCGLProgramUniformType()
    {
      @Override
      public String name()
      {
        return "exists";
      }

      @Override
      public JCGLProgramShaderUsableType program()
      {
        return p;
      }

      @Override
      public JCGLType type()
      {
        return JCGLType.TYPE_FLOAT_VECTOR_4;
      }

      @Override
      public int size()
      {
        return 1;
      }

      @Override
      public int glName()
      {
        return 0;
      }
    };

    u.put("exists", uu);

    final JCGLProgramUniformType ur =
      R2ShaderParameters.uniform(
        p,
        "exists",
        JCGLType.TYPE_FLOAT_VECTOR_4);
    Assert.assertEquals(uu, ur);
  }

  @Test
  public void testShaderParametersWrongType()
  {
    final Map<String, JCGLProgramUniformType> u = new HashMap<>();
    final JCGLProgramShaderUsableType p = new JCGLProgramShaderUsableType()
    {
      @Override
      public String name()
      {
        return "p";
      }

      @Override
      public Map<String, JCGLProgramAttributeType> attributes()
      {
        return new HashMap<>();
      }

      @Override
      public Map<String, JCGLProgramUniformType> uniforms()
      {
        return u;
      }

      @Override
      public int glName()
      {
        return 1;
      }

      @Override
      public Set<JCGLReferableType> references()
      {
        return new HashSet<>();
      }

      @Override
      public boolean isDeleted()
      {
        return false;
      }
    };

    final JCGLProgramUniformType uu = new JCGLProgramUniformType()
    {
      @Override
      public String name()
      {
        return "exists";
      }

      @Override
      public JCGLProgramShaderUsableType program()
      {
        return p;
      }

      @Override
      public JCGLType type()
      {
        return JCGLType.TYPE_FLOAT_VECTOR_4;
      }

      @Override
      public int size()
      {
        return 1;
      }

      @Override
      public int glName()
      {
        return 0;
      }
    };

    u.put("exists", uu);

    this.expected.expect(R2ExceptionShaderParameterWrongType.class);
    R2ShaderParameters.uniform(
      p,
      "exists",
      JCGLType.TYPE_FLOAT_VECTOR_3);
  }
}
