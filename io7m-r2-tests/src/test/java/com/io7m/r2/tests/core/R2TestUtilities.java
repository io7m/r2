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

import com.io7m.jcanephora.core.JCGLArrayBufferUsableType;
import com.io7m.jcanephora.core.JCGLArrayObjectBuilderType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.JCGLException;
import com.io7m.jcanephora.core.JCGLExceptionNonCompliant;
import com.io7m.jcanephora.core.JCGLExceptionUnsupported;
import com.io7m.jcanephora.core.JCGLFragmentShaderType;
import com.io7m.jcanephora.core.JCGLFragmentShaderUsableType;
import com.io7m.jcanephora.core.JCGLGeometryShaderUsableType;
import com.io7m.jcanephora.core.JCGLIndexBufferType;
import com.io7m.jcanephora.core.JCGLProgramAttributeType;
import com.io7m.jcanephora.core.JCGLProgramShaderType;
import com.io7m.jcanephora.core.JCGLProgramShaderUsableType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLScalarType;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLUnsignedType;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.JCGLVertexShaderType;
import com.io7m.jcanephora.core.JCGLVertexShaderUsableType;
import com.io7m.jcanephora.core.api.JCGLArrayBuffersType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLIndexBuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.fake.FakeContext;
import com.io7m.jcanephora.fake.FakeShaderListenerType;
import com.io7m.jcanephora.fake.JCGLImplementationFake;
import com.io7m.jcanephora.fake.JCGLImplementationFakeType;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.jtensors.parameterized.PMatrixReadable3x3FType;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2InstanceBatchedType;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2LightSingleType;
import com.io7m.r2.core.R2MaterialOpaqueBatchedType;
import com.io7m.r2.core.R2MaterialOpaqueSingleType;
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2ShaderBatchedUsableType;
import com.io7m.r2.core.R2ShaderInstanceSingleType;
import com.io7m.r2.core.R2ShaderInstanceSingleUsableType;
import com.io7m.r2.core.R2ShaderLightSingleUsableType;
import com.io7m.r2.core.R2TextureUnitContextMutableType;
import com.io7m.r2.core.R2TransformContextType;
import com.io7m.r2.core.R2TransformIdentity;
import com.io7m.r2.core.R2TransformOSiT;
import com.io7m.r2.core.R2TransformReadableType;
import com.io7m.r2.spaces.R2SpaceTextureType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class R2TestUtilities
{
  private R2TestUtilities()
  {

  }

  static R2InstanceSingleType getInstanceSingle(
    final JCGLInterfaceGL33Type g,
    final JCGLArrayObjectType ao,
    final long id)
  {
    return new R2InstanceSingleType()
    {
      @Override
      public long getInstanceID()
      {
        return id;
      }

      @Override
      public String toString()
      {
        return String.format(
          "[instance %d [array %d]]",
          Long.valueOf(id), Integer.valueOf(ao.getGLName()));
      }

      @Override
      public JCGLArrayObjectUsableType getArrayObject()
      {
        return ao;
      }

      @Override
      public R2TransformReadableType getTransform()
      {
        return R2TransformOSiT.newTransform();
      }

      @Override
      public PMatrixReadable3x3FType<R2SpaceTextureType, R2SpaceTextureType>
      getUVMatrix()
      {
        return PMatrixI3x3F.identity();
      }
    };
  }

  static JCGLArrayObjectType getArrayObject(
    final JCGLInterfaceGL33Type g)
  {
    final JCGLArrayBuffersType g_ab = g.getArrayBuffers();
    final JCGLIndexBuffersType g_ib = g.getIndexBuffers();
    final JCGLArrayObjectsType g_ao = g.getArrayObjects();

    final JCGLIndexBufferType ib =
      g_ib.indexBufferAllocate(
        3L,
        JCGLUnsignedType.TYPE_UNSIGNED_INT,
        JCGLUsageHint.USAGE_STATIC_DRAW);
    final JCGLArrayBufferUsableType a =
      g_ab.arrayBufferAllocate(3L * 4L, JCGLUsageHint.USAGE_STATIC_DRAW);

    final JCGLArrayObjectBuilderType aob = g_ao.arrayObjectNewBuilder();
    aob.setAttributeFloatingPoint(
      0, a, 3, JCGLScalarType.TYPE_FLOAT, 3 * 4, 0L, false);
    aob.setIndexBuffer(ib);

    final JCGLArrayObjectType ao = g_ao.arrayObjectAllocate(aob);
    g_ao.arrayObjectUnbind();
    return ao;
  }

  static R2MaterialOpaqueSingleType<Object> getMaterialSingle(
    final JCGLInterfaceGL33Type g,
    final R2ShaderInstanceSingleUsableType<Object> sh,
    final Object p,
    final long id)
  {
    return new R2MaterialOpaqueSingleType<Object>()
    {
      @Override
      public R2ShaderInstanceSingleUsableType<Object> getShader()
      {
        return sh;
      }

      @Override
      public long getMaterialID()
      {
        return id;
      }

      @Override
      public String toString()
      {
        return String.format("[material %d %s]", Long.valueOf(id), sh);
      }

      @Override
      public Object getShaderParameters()
      {
        return p;
      }
    };
  }

  static R2ShaderInstanceSingleUsableType<Object> getShaderSingle(
    final JCGLInterfaceGL33Type g,
    final long s_id)
  {
    final JCGLShadersType g_sh = g.getShaders();

    final List<String> v_lines = new ArrayList<>(3);
    v_lines.add("void main() {\n");
    v_lines.add("  gl_Position = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    v_lines.add("}\n");
    final JCGLVertexShaderType v =
      g_sh.shaderCompileVertex("v_main", v_lines);

    final List<String> f_lines = new ArrayList<>(4);
    f_lines.add("out vec4 color_0;\n");
    f_lines.add("void main() {\n");
    f_lines.add("  color_0 = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    f_lines.add("}\n");
    final JCGLFragmentShaderType f =
      g_sh.shaderCompileFragment("f_main", f_lines);

    final JCGLProgramShaderType pr =
      g_sh.shaderLinkProgram("p_main", v, Optional.empty(), f);

    return new R2ShaderInstanceSingleType<Object>()
    {
      @Override
      public void setMaterialTextures(
        final JCGLTexturesType g_tex,
        final R2TextureUnitContextMutableType tc,
        final Object values)
      {

      }

      @Override
      public void setMaterialValues(
        final JCGLShadersType g_sh,
        final Object values)
      {

      }

      @Override
      public void setMatricesView(
        final JCGLShadersType g_sh,
        final R2MatricesObserverValuesType m)
      {

      }

      @Override
      public void setMatricesInstance(
        final JCGLShadersType g_sh,
        final R2MatricesInstanceSingleValuesType m)
      {

      }

      @Override
      public void delete(final JCGLInterfaceGL33Type g)
        throws R2Exception
      {

      }

      @Override
      public boolean isDeleted()
      {
        return false;
      }

      @Override
      public long getShaderID()
      {
        return s_id;
      }

      @Override
      public String toString()
      {
        return String.format("[shader %d]", Long.valueOf(s_id));
      }

      @Override
      public Class<Object> getShaderParametersType()
      {
        return Object.class;
      }

      @Override
      public JCGLProgramShaderUsableType getShaderProgram()
      {
        return pr;
      }
    };
  }

  static JCGLInterfaceGL33Type getGL()
    throws JCGLExceptionUnsupported, JCGLExceptionNonCompliant
  {
    final JCGLImplementationFakeType gi =
      JCGLImplementationFake.getInstance();
    final JCGLContextType gc =
      gi.newContext("main", new FakeShaderListenerType()
      {
        @Override
        public void onCompileVertexShaderStart(
          final FakeContext context,
          final String name,
          final List<String> sources)
          throws JCGLException
        {

        }

        @Override
        public void onCompileFragmentShaderStart(
          final FakeContext context,
          final String name,
          final List<String> sources)
          throws JCGLException
        {

        }

        @Override
        public void onCompileGeometryShaderStart(
          final FakeContext context,
          final String name,
          final List<String> sources)
          throws JCGLException
        {

        }

        @Override
        public void onLinkProgram(
          final FakeContext context,
          final JCGLProgramShaderUsableType p,
          final String name,
          final JCGLVertexShaderUsableType v,
          final Optional<JCGLGeometryShaderUsableType> g,
          final JCGLFragmentShaderUsableType f,
          final Map<String, JCGLProgramAttributeType> attributes,
          final Map<String, JCGLProgramUniformType> uniforms)
          throws JCGLException
        {

        }
      });
    return gc.contextGetGL33();
  }

  public static R2InstanceBatchedType getInstanceBatched(
    final JCGLInterfaceGL33Type g,
    final JCGLArrayObjectType a0,
    final long l)
  {
    return new R2InstanceBatchedType()
    {
      @Override
      public JCGLArrayObjectType getArrayObject()
      {
        return a0;
      }

      @Override
      public void update(
        final JCGLInterfaceGL33Type g,
        final R2TransformContextType context)
      {

      }

      @Override
      public int getRenderCount()
      {
        return 1;
      }

      @Override
      public long getInstanceID()
      {
        return l;
      }
    };
  }

  public static R2ShaderBatchedUsableType<Object> getShaderBatched(
    final JCGLInterfaceGL33Type g,
    final long s_id)
  {
    final JCGLShadersType g_sh = g.getShaders();

    final List<String> v_lines = new ArrayList<>(3);
    v_lines.add("void main() {\n");
    v_lines.add("  gl_Position = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    v_lines.add("}\n");
    final JCGLVertexShaderType v =
      g_sh.shaderCompileVertex("v_main", v_lines);

    final List<String> f_lines = new ArrayList<>(4);
    f_lines.add("out vec4 color_0;\n");
    f_lines.add("void main() {\n");
    f_lines.add("  color_0 = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    f_lines.add("}\n");
    final JCGLFragmentShaderType f =
      g_sh.shaderCompileFragment("f_main", f_lines);

    final JCGLProgramShaderType pr =
      g_sh.shaderLinkProgram("p_main", v, Optional.empty(), f);

    return new R2ShaderBatchedUsableType<Object>()
    {
      @Override
      public void setMaterialTextures(
        final JCGLTexturesType g_tex,
        final R2TextureUnitContextMutableType tc,
        final Object values)
      {

      }

      @Override
      public void setMaterialValues(
        final JCGLShadersType g_sh,
        final Object values)
      {

      }

      @Override
      public void setMatricesView(
        final JCGLShadersType g_sh,
        final R2MatricesObserverValuesType m)
      {

      }

      @Override
      public boolean isDeleted()
      {
        return false;
      }

      @Override
      public long getShaderID()
      {
        return s_id;
      }

      @Override
      public String toString()
      {
        return String.format("[shader %d]", Long.valueOf(s_id));
      }

      @Override
      public Class<Object> getShaderParametersType()
      {
        return Object.class;
      }

      @Override
      public JCGLProgramShaderUsableType getShaderProgram()
      {
        return pr;
      }
    };
  }

  public static R2MaterialOpaqueBatchedType<Object> getMaterialBatched(
    final JCGLInterfaceGL33Type g,
    final R2ShaderBatchedUsableType<Object> sh,
    final Object p,
    final long id)
  {
    return new R2MaterialOpaqueBatchedType<Object>()
    {
      @Override
      public R2ShaderBatchedUsableType<Object> getShader()
      {
        return sh;
      }

      @Override
      public long getMaterialID()
      {
        return id;
      }

      @Override
      public String toString()
      {
        return String.format("[material %d %s]", Long.valueOf(id), sh);
      }

      @Override
      public Object getShaderParameters()
      {
        return p;
      }
    };
  }

  public static R2ShaderLightSingleUsableType<R2LightSingleType>
  getShaderLightSingle(
    final JCGLInterfaceGL33Type g,
    final long s_id)
  {
    final JCGLShadersType g_sh = g.getShaders();

    final List<String> v_lines = new ArrayList<>(3);
    v_lines.add("void main() {\n");
    v_lines.add("  gl_Position = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    v_lines.add("}\n");
    final JCGLVertexShaderType v =
      g_sh.shaderCompileVertex("v_main", v_lines);

    final List<String> f_lines = new ArrayList<>(4);
    f_lines.add("out vec4 color_0;\n");
    f_lines.add("void main() {\n");
    f_lines.add("  color_0 = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    f_lines.add("}\n");
    final JCGLFragmentShaderType f =
      g_sh.shaderCompileFragment("f_main", f_lines);

    final JCGLProgramShaderType pr =
      g_sh.shaderLinkProgram("p_main", v, Optional.empty(), f);

    return new R2ShaderLightSingleUsableType<R2LightSingleType>()
    {
      @Override
      public void setGBuffer(
        final JCGLShadersType g_sh,
        final R2GeometryBufferUsableType g,
        final JCGLTextureUnitType unit_albedo,
        final JCGLTextureUnitType unit_specular,
        final JCGLTextureUnitType unit_depth,
        final JCGLTextureUnitType unit_normals)
      {

      }

      @Override
      public void setLightValues(
        final JCGLShadersType g_sh,
        final JCGLTexturesType g_tex,
        final R2LightSingleType values)
      {

      }

      @Override
      public void setLightViewDependentValues(
        final JCGLShadersType g_sh,
        final R2MatricesObserverValuesType m,
        final R2LightSingleType values)
      {

      }

      @Override
      public void setLightTransformDependentValues(
        final JCGLShadersType g_sh,
        final R2MatricesInstanceSingleValuesType m,
        final R2LightSingleType values)
      {

      }

      @Override
      public long getShaderID()
      {
        return s_id;
      }

      @Override
      public Class<R2LightSingleType> getShaderParametersType()
      {
        return R2LightSingleType.class;
      }

      @Override
      public JCGLProgramShaderUsableType getShaderProgram()
      {
        return pr;
      }

      @Override
      public boolean isDeleted()
      {
        return false;
      }
    };
  }

  public static R2LightSingleType getLightSingle(
    final JCGLArrayObjectType a0,
    final long light_id)
  {
    return new R2LightSingleType()
    {
      @Override
      public JCGLArrayObjectUsableType getArrayObject()
      {
        return a0;
      }

      @Override
      public R2TransformReadableType getTransform()
      {
        return R2TransformIdentity.getInstance();
      }

      @Override
      public long getLightID()
      {
        return light_id;
      }
    };
  }
}
