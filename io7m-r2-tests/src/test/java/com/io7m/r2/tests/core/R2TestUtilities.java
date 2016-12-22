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

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
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
import com.io7m.jcanephora.core.JCGLProjectionMatrices;
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
import com.io7m.jcanephora.renderstate.JCGLBlendState;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jfunctional.PartialBiFunctionType;
import com.io7m.jtensors.parameterized.PMatrixDirect4x4FType;
import com.io7m.jtensors.parameterized.PMatrixDirectM4x4F;
import com.io7m.jtensors.parameterized.PMatrixDirectReadable4x4FType;
import com.io7m.jtensors.parameterized.PMatrixI3x3F;
import com.io7m.jtensors.parameterized.PMatrixM4x4F;
import com.io7m.jtensors.parameterized.PMatrixReadable3x3FType;
import com.io7m.jtensors.parameterized.PVector3FType;
import com.io7m.jtensors.parameterized.PVectorM3F;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.r2.core.R2Exception;
import com.io7m.r2.core.R2ExceptionShaderValidationFailed;
import com.io7m.r2.core.R2GeometryBufferUsableType;
import com.io7m.r2.core.R2InstanceBatchedType;
import com.io7m.r2.core.R2InstanceBillboardedType;
import com.io7m.r2.core.R2InstanceSingleType;
import com.io7m.r2.core.R2LightScreenSingleType;
import com.io7m.r2.core.R2LightSingleType;
import com.io7m.r2.core.R2LightVolumeSingleType;
import com.io7m.r2.core.R2MaterialDepthBatchedType;
import com.io7m.r2.core.R2MaterialDepthSingleType;
import com.io7m.r2.core.R2MaterialOpaqueBatchedType;
import com.io7m.r2.core.R2MaterialOpaqueBillboardedType;
import com.io7m.r2.core.R2MaterialOpaqueSingleType;
import com.io7m.r2.core.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2ProjectionFOV;
import com.io7m.r2.core.R2ProjectionReadableType;
import com.io7m.r2.core.R2TransformContext;
import com.io7m.r2.core.R2TransformContextType;
import com.io7m.r2.core.R2TransformIdentity;
import com.io7m.r2.core.R2TransformReadableType;
import com.io7m.r2.core.R2TransformSiOT;
import com.io7m.r2.core.R2ViewRays;
import com.io7m.r2.core.R2ViewRaysReadableType;
import com.io7m.r2.core.R2ViewRaysType;
import com.io7m.r2.core.shaders.types.R2ShaderDepthBatchedType;
import com.io7m.r2.core.shaders.types.R2ShaderDepthBatchedUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderDepthSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderDepthSingleUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBatchedUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBillboardedType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceBillboardedUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderInstanceSingleUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderLightSingleUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersMaterialType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersViewType;
import com.io7m.r2.core.shaders.types.R2ShaderTranslucentInstanceBatchedType;
import com.io7m.r2.core.shaders.types.R2ShaderTranslucentInstanceBillboardedType;
import com.io7m.r2.core.shaders.types.R2ShaderTranslucentInstanceSingleType;
import com.io7m.r2.spaces.R2SpaceClipType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceRGBType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import com.io7m.r2.spaces.R2SpaceWorldType;

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
      public long instanceID()
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
      public JCGLArrayObjectUsableType arrayObject()
      {
        return ao;
      }

      @Override
      public R2TransformReadableType transform()
      {
        return R2TransformSiOT.newTransform();
      }

      @Override
      public PMatrixReadable3x3FType<R2SpaceTextureType, R2SpaceTextureType>
      uvMatrix()
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
      public R2ShaderInstanceSingleUsableType<Object> shader()
      {
        return sh;
      }

      @Override
      public long materialID()
      {
        return id;
      }

      @Override
      public String toString()
      {
        return String.format("[material %d %s]", Long.valueOf(id), sh);
      }

      @Override
      public Object shaderParameters()
      {
        return p;
      }
    };
  }

  static R2MaterialDepthSingleType<Object> getMaterialDepth(
    final JCGLInterfaceGL33Type g,
    final R2ShaderDepthSingleUsableType<Object> sh,
    final Object p,
    final long id)
  {
    return new R2MaterialDepthSingleType<Object>()
    {
      @Override
      public R2ShaderDepthSingleUsableType<Object> shader()
      {
        return sh;
      }

      @Override
      public long materialID()
      {
        return id;
      }

      @Override
      public String toString()
      {
        return String.format("[material %d %s]", Long.valueOf(id), sh);
      }

      @Override
      public Object shaderParameters()
      {
        return p;
      }
    };
  }

  public static R2ShaderInstanceSingleType<Object>
  getShaderInstanceSingle(
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
      public void delete(final JCGLInterfaceGL33Type g)
        throws R2Exception
      {

      }

      @Override
      public void onReceiveViewValues(
        final JCGLInterfaceGL33Type g,
        final R2ShaderParametersViewType view_parameters)
      {

      }

      @Override
      public void onReceiveMaterialValues(
        final JCGLInterfaceGL33Type g,
        R2ShaderParametersMaterialType<Object> mat_parameters)
      {

      }

      @Override
      public void onReceiveInstanceTransformValues(
        final JCGLInterfaceGL33Type g,
        final R2MatricesInstanceSingleValuesType m)
      {

      }

      @Override
      public boolean isDeleted()
      {
        return false;
      }

      @Override
      public long shaderID()
      {
        return s_id;
      }

      @Override
      public String toString()
      {
        return String.format("[shader %d]", Long.valueOf(s_id));
      }

      @Override
      public Class<Object> shaderParametersType()
      {
        return Object.class;
      }

      @Override
      public JCGLProgramShaderUsableType shaderProgram()
      {
        return pr;
      }

      @Override
      public void onActivate(final JCGLInterfaceGL33Type g)
      {

      }

      @Override
      public void onValidate()
        throws R2ExceptionShaderValidationFailed
      {

      }

      @Override
      public void onDeactivate(final JCGLInterfaceGL33Type g)
      {

      }
    };
  }

  public static R2ShaderDepthSingleType<Object>
  getShaderDepthSingle(
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

    return new R2ShaderDepthSingleType<Object>()
    {
      @Override
      public void delete(final JCGLInterfaceGL33Type g)
        throws R2Exception
      {

      }

      @Override
      public void onReceiveViewValues(
        final JCGLInterfaceGL33Type g,
        final R2ShaderParametersViewType view_parameters)
      {

      }

      @Override
      public void onReceiveMaterialValues(
        final JCGLInterfaceGL33Type g,
        R2ShaderParametersMaterialType<Object> mat_parameters)
      {

      }

      @Override
      public void onReceiveInstanceTransformValues(
        final JCGLInterfaceGL33Type g,
        final R2MatricesInstanceSingleValuesType m)
      {

      }

      @Override
      public boolean isDeleted()
      {
        return false;
      }

      @Override
      public long shaderID()
      {
        return s_id;
      }

      @Override
      public String toString()
      {
        return String.format("[shader %d]", Long.valueOf(s_id));
      }

      @Override
      public Class<Object> shaderParametersType()
      {
        return Object.class;
      }

      @Override
      public JCGLProgramShaderUsableType shaderProgram()
      {
        return pr;
      }

      @Override
      public void onActivate(final JCGLInterfaceGL33Type g)
      {

      }

      @Override
      public void onValidate()
        throws R2ExceptionShaderValidationFailed
      {

      }

      @Override
      public void onDeactivate(final JCGLInterfaceGL33Type g)
      {

      }
    };
  }

  public static JCGLInterfaceGL33Type getFakeGL()
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
      public JCGLArrayObjectType arrayObject()
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
      public int renderCount()
      {
        return 1;
      }

      @Override
      public long instanceID()
      {
        return l;
      }
    };
  }

  public static R2ShaderInstanceBatchedType<Object>
  getShaderInstanceBatched(
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

    return new R2ShaderInstanceBatchedType<Object>()
    {
      @Override
      public void delete(final JCGLInterfaceGL33Type g)
        throws R2Exception
      {

      }

      @Override
      public void onReceiveViewValues(
        final JCGLInterfaceGL33Type g,
        final R2ShaderParametersViewType view_parameters)
      {

      }

      @Override
      public void onReceiveMaterialValues(
        final JCGLInterfaceGL33Type g,
        R2ShaderParametersMaterialType<Object> mat_parameters)
      {

      }

      @Override
      public boolean isDeleted()
      {
        return false;
      }

      @Override
      public long shaderID()
      {
        return s_id;
      }

      @Override
      public String toString()
      {
        return String.format("[shader %d]", Long.valueOf(s_id));
      }

      @Override
      public Class<Object> shaderParametersType()
      {
        return Object.class;
      }

      @Override
      public JCGLProgramShaderUsableType shaderProgram()
      {
        return pr;
      }

      @Override
      public void onActivate(final JCGLInterfaceGL33Type g)
      {

      }

      @Override
      public void onValidate()
        throws R2ExceptionShaderValidationFailed
      {

      }

      @Override
      public void onDeactivate(final JCGLInterfaceGL33Type g)
      {

      }
    };
  }

  public static R2ShaderInstanceBillboardedType<Object>
  getShaderInstanceBillboarded(
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

    return new R2ShaderInstanceBillboardedType<Object>()
    {
      @Override
      public void delete(final JCGLInterfaceGL33Type g)
        throws R2Exception
      {

      }

      @Override
      public void onReceiveViewValues(
        final JCGLInterfaceGL33Type g,
        final R2ShaderParametersViewType view_parameters)
      {

      }

      @Override
      public void onReceiveMaterialValues(
        final JCGLInterfaceGL33Type g,
        R2ShaderParametersMaterialType<Object> mat_parameters)
      {

      }

      @Override
      public boolean isDeleted()
      {
        return false;
      }

      @Override
      public long shaderID()
      {
        return s_id;
      }

      @Override
      public String toString()
      {
        return String.format("[shader %d]", Long.valueOf(s_id));
      }

      @Override
      public Class<Object> shaderParametersType()
      {
        return Object.class;
      }

      @Override
      public JCGLProgramShaderUsableType shaderProgram()
      {
        return pr;
      }

      @Override
      public void onActivate(final JCGLInterfaceGL33Type g)
      {

      }

      @Override
      public void onValidate()
        throws R2ExceptionShaderValidationFailed
      {

      }

      @Override
      public void onDeactivate(final JCGLInterfaceGL33Type g)
      {

      }
    };
  }

  public static R2ShaderDepthBatchedType<Object>
  getShaderDepthBatched(
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

    return new R2ShaderDepthBatchedType<Object>()
    {
      @Override
      public void delete(final JCGLInterfaceGL33Type g)
        throws R2Exception
      {

      }

      @Override
      public void onReceiveViewValues(
        final JCGLInterfaceGL33Type g,
        R2ShaderParametersViewType view_parameters)
      {

      }

      @Override
      public void onReceiveMaterialValues(
        final JCGLInterfaceGL33Type g,
        R2ShaderParametersMaterialType<Object> mat_parameters)
      {

      }

      @Override
      public boolean isDeleted()
      {
        return false;
      }

      @Override
      public long shaderID()
      {
        return s_id;
      }

      @Override
      public String toString()
      {
        return String.format("[shader %d]", Long.valueOf(s_id));
      }

      @Override
      public Class<Object> shaderParametersType()
      {
        return Object.class;
      }

      @Override
      public JCGLProgramShaderUsableType shaderProgram()
      {
        return pr;
      }

      @Override
      public void onActivate(final JCGLInterfaceGL33Type g)
      {

      }

      @Override
      public void onValidate()
        throws R2ExceptionShaderValidationFailed
      {

      }

      @Override
      public void onDeactivate(final JCGLInterfaceGL33Type g)
      {

      }
    };
  }

  public static R2MaterialDepthBatchedType<Object> getMaterialDepthBatched(
    final JCGLInterfaceGL33Type g,
    final R2ShaderDepthBatchedUsableType<Object> sh,
    final Object p,
    final long id)
  {
    return new R2MaterialDepthBatchedType<Object>()
    {
      @Override
      public R2ShaderDepthBatchedUsableType<Object> shader()
      {
        return sh;
      }

      @Override
      public long materialID()
      {
        return id;
      }

      @Override
      public String toString()
      {
        return String.format("[material %d %s]", Long.valueOf(id), sh);
      }

      @Override
      public Object shaderParameters()
      {
        return p;
      }
    };
  }

  public static R2MaterialOpaqueBatchedType<Object> getMaterialBatched(
    final JCGLInterfaceGL33Type g,
    final R2ShaderInstanceBatchedUsableType<Object> sh,
    final Object p,
    final long id)
  {
    return new R2MaterialOpaqueBatchedType<Object>()
    {
      @Override
      public R2ShaderInstanceBatchedUsableType<Object> shader()
      {
        return sh;
      }

      @Override
      public long materialID()
      {
        return id;
      }

      @Override
      public String toString()
      {
        return String.format("[material %d %s]", Long.valueOf(id), sh);
      }

      @Override
      public Object shaderParameters()
      {
        return p;
      }
    };
  }

  public static R2ShaderFilterType<Object>
  getShaderFilter(
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

    return new R2ShaderFilterType<Object>()
    {
      @Override
      public void delete(final JCGLInterfaceGL33Type g)
        throws R2Exception
      {

      }

      @Override
      public void onReceiveFilterValues(
        final JCGLInterfaceGL33Type g,
        R2ShaderParametersFilterType<Object> parameters)
      {

      }

      @Override
      public long shaderID()
      {
        return s_id;
      }

      @Override
      public Class<Object> shaderParametersType()
      {
        return Object.class;
      }

      @Override
      public JCGLProgramShaderUsableType shaderProgram()
      {
        return pr;
      }

      @Override
      public void onActivate(final JCGLInterfaceGL33Type g)
      {

      }

      @Override
      public void onValidate()
        throws R2ExceptionShaderValidationFailed
      {

      }

      @Override
      public void onDeactivate(final JCGLInterfaceGL33Type g)
      {

      }

      @Override
      public boolean isDeleted()
      {
        return false;
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
      public void onReceiveBoundGeometryBufferTextures(
        final JCGLShadersType g_sh,
        final R2GeometryBufferUsableType g,
        final JCGLTextureUnitType unit_albedo,
        final JCGLTextureUnitType unit_specular,
        final JCGLTextureUnitType unit_depth,
        final JCGLTextureUnitType unit_normals)
      {

      }

      @Override
      public void onReceiveValues(
        final JCGLTexturesType g_tex,
        final JCGLShadersType g_sh,
        final JCGLTextureUnitContextMutableType tc,
        final AreaInclusiveUnsignedLType viewport,
        final R2LightSingleType values,
        final R2MatricesObserverValuesType m)
      {

      }

      @Override
      public long shaderID()
      {
        return s_id;
      }

      @Override
      public Class<R2LightSingleType> shaderParametersType()
      {
        return R2LightSingleType.class;
      }

      @Override
      public JCGLProgramShaderUsableType shaderProgram()
      {
        return pr;
      }

      @Override
      public void onActivate(final JCGLInterfaceGL33Type g)
      {

      }

      @Override
      public void onValidate()
        throws R2ExceptionShaderValidationFailed
      {

      }

      @Override
      public void onDeactivate(final JCGLInterfaceGL33Type g)
      {

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
    final PVector3FType<R2SpaceRGBType> black = new PVectorM3F<>();
    return new R2LightSingleType()
    {
      @Override
      public PVector3FType<R2SpaceRGBType> colorWritable()
      {
        return black;
      }

      @Override
      public JCGLArrayObjectUsableType arrayObject()
      {
        return a0;
      }

      @Override
      public R2TransformReadableType transform()
      {
        return R2TransformIdentity.getInstance();
      }

      @Override
      public <A, B, E extends Throwable> B matchLightSingle(
        final A context,
        final PartialBiFunctionType<A, R2LightVolumeSingleType, B, E> on_volume,
        final PartialBiFunctionType<A, R2LightScreenSingleType, B, E> on_screen)
        throws E
      {
        throw new UnimplementedCodeException();
      }

      @Override
      public long lightID()
      {
        return light_id;
      }

      @Override
      public PVector3FType<R2SpaceRGBType> color()
      {
        return black;
      }

      @Override
      public float intensity()
      {
        return 1.0f;
      }

      @Override
      public void setIntensity(final float i)
      {

      }
    };
  }

  @SuppressWarnings("unchecked")
  public static R2MatricesObserverValuesType getMatricesObserverValues()
  {
    final R2TransformContextType tc =
      R2TransformContext.newContext();
    final R2ProjectionFOV p =
      R2ProjectionFOV.newFrustumWith(
        JCGLProjectionMatrices.newMatrices(),
        (float) Math.toRadians(45.0),
        1.0f,
        1.0f,
        100.0f);
    final PMatrixDirect4x4FType<?, ?> m =
      PMatrixDirectM4x4F.newMatrix();
    final R2ViewRaysType vr =
      R2ViewRays.newViewRays(new PMatrixM4x4F.ContextPM4F());

    return new R2MatricesObserverValuesType()
    {
      @Override
      public R2ProjectionReadableType projection()
      {
        return p;
      }

      @Override
      public PMatrixDirectReadable4x4FType<R2SpaceEyeType, R2SpaceClipType> matrixProjection()
      {
        return (PMatrixDirectReadable4x4FType<R2SpaceEyeType, R2SpaceClipType>) m;
      }

      @Override
      public PMatrixDirectReadable4x4FType<R2SpaceClipType, R2SpaceEyeType> matrixProjectionInverse()
      {
        return (PMatrixDirectReadable4x4FType<R2SpaceClipType, R2SpaceEyeType>) m;
      }

      @Override
      public PMatrixDirectReadable4x4FType<R2SpaceWorldType, R2SpaceEyeType> matrixView()
      {
        return (PMatrixDirectReadable4x4FType<R2SpaceWorldType, R2SpaceEyeType>) m;
      }

      @Override
      public PMatrixDirectReadable4x4FType<R2SpaceEyeType, R2SpaceWorldType> matrixViewInverse()
      {
        return (PMatrixDirectReadable4x4FType<R2SpaceEyeType, R2SpaceWorldType>) m;
      }

      @Override
      public R2ViewRaysReadableType viewRays()
      {
        return vr;
      }

      @Override
      public R2TransformContextType transformContext()
      {
        return tc;
      }
    };
  }

  public static R2InstanceBillboardedType getInstanceBillboarded(
    final JCGLInterfaceGL33Type g,
    final JCGLArrayObjectType a0,
    final long l)
  {
    return new R2InstanceBillboardedType()
    {
      @Override
      public JCGLArrayObjectType arrayObject()
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
      public int enabledCount()
      {
        return 100;
      }

      @Override
      public long instanceID()
      {
        return l;
      }
    };
  }

  public static R2MaterialOpaqueBillboardedType<Object> getMaterialBillboarded(
    final JCGLInterfaceGL33Type g,
    final R2ShaderInstanceBillboardedUsableType<Object> sh,
    final Object p,
    final long id)
  {
    return new R2MaterialOpaqueBillboardedType<Object>()
    {
      @Override
      public R2ShaderInstanceBillboardedUsableType<Object> shader()
      {
        return sh;
      }

      @Override
      public long materialID()
      {
        return id;
      }

      @Override
      public String toString()
      {
        return String.format("[material %d %s]", Long.valueOf(id), sh);
      }

      @Override
      public Object shaderParameters()
      {
        return p;
      }
    };
  }

  public static R2ShaderTranslucentInstanceSingleType<Object>
  getShaderTranslucentInstanceSingle(
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

    return new R2ShaderTranslucentInstanceSingleType<Object>()
    {
      @Override
      public Optional<JCGLBlendState> suggestedBlendState()
      {
        return Optional.empty();
      }

      @Override
      public void delete(final JCGLInterfaceGL33Type g)
        throws R2Exception
      {

      }

      @Override
      public void onReceiveViewValues(
        final JCGLInterfaceGL33Type g,
        final R2ShaderParametersViewType view_parameters)
      {

      }

      @Override
      public void onReceiveMaterialValues(
        final JCGLInterfaceGL33Type g,
        R2ShaderParametersMaterialType<Object> mat_parameters)
      {

      }

      @Override
      public void onReceiveInstanceTransformValues(
        final JCGLInterfaceGL33Type g,
        final R2MatricesInstanceSingleValuesType m)
      {

      }

      @Override
      public boolean isDeleted()
      {
        return false;
      }

      @Override
      public long shaderID()
      {
        return s_id;
      }

      @Override
      public String toString()
      {
        return String.format("[shader %d]", Long.valueOf(s_id));
      }

      @Override
      public Class<Object> shaderParametersType()
      {
        return Object.class;
      }

      @Override
      public JCGLProgramShaderUsableType shaderProgram()
      {
        return pr;
      }

      @Override
      public void onActivate(final JCGLInterfaceGL33Type g)
      {

      }

      @Override
      public void onValidate()
        throws R2ExceptionShaderValidationFailed
      {

      }

      @Override
      public void onDeactivate(final JCGLInterfaceGL33Type g)
      {

      }
    };
  }

  public static R2ShaderTranslucentInstanceBatchedType<Object>
  getShaderTranslucentInstanceBatched(
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

    return new R2ShaderTranslucentInstanceBatchedType<Object>()
    {
      @Override
      public Optional<JCGLBlendState> suggestedBlendState()
      {
        return Optional.empty();
      }

      @Override
      public void delete(final JCGLInterfaceGL33Type g)
        throws R2Exception
      {

      }

      @Override
      public void onReceiveViewValues(
        final JCGLInterfaceGL33Type g,
        final R2ShaderParametersViewType view_parameters)
      {

      }

      @Override
      public void onReceiveMaterialValues(
        final JCGLInterfaceGL33Type g,
        R2ShaderParametersMaterialType<Object> mat_parameters)
      {

      }

      @Override
      public boolean isDeleted()
      {
        return false;
      }

      @Override
      public long shaderID()
      {
        return s_id;
      }

      @Override
      public String toString()
      {
        return String.format("[shader %d]", Long.valueOf(s_id));
      }

      @Override
      public Class<Object> shaderParametersType()
      {
        return Object.class;
      }

      @Override
      public JCGLProgramShaderUsableType shaderProgram()
      {
        return pr;
      }

      @Override
      public void onActivate(final JCGLInterfaceGL33Type g)
      {

      }

      @Override
      public void onValidate()
        throws R2ExceptionShaderValidationFailed
      {

      }

      @Override
      public void onDeactivate(final JCGLInterfaceGL33Type g)
      {

      }
    };
  }

  public static R2ShaderTranslucentInstanceBillboardedType<Object>
  getShaderTranslucentInstanceBillboarded(
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

    return new R2ShaderTranslucentInstanceBillboardedType<Object>()
    {

      @Override
      public Optional<JCGLBlendState> suggestedBlendState()
      {
        return Optional.empty();
      }

      @Override
      public void delete(final JCGLInterfaceGL33Type g)
        throws R2Exception
      {

      }

      @Override
      public void onReceiveViewValues(
        final JCGLInterfaceGL33Type g,
        final R2ShaderParametersViewType view_parameters)
      {

      }

      @Override
      public void onReceiveMaterialValues(
        final JCGLInterfaceGL33Type g,
        R2ShaderParametersMaterialType<Object> mat_parameters)
      {

      }

      @Override
      public boolean isDeleted()
      {
        return false;
      }

      @Override
      public long shaderID()
      {
        return s_id;
      }

      @Override
      public String toString()
      {
        return String.format("[shader %d]", Long.valueOf(s_id));
      }

      @Override
      public Class<Object> shaderParametersType()
      {
        return Object.class;
      }

      @Override
      public JCGLProgramShaderUsableType shaderProgram()
      {
        return pr;
      }

      @Override
      public void onActivate(final JCGLInterfaceGL33Type g)
      {

      }

      @Override
      public void onValidate()
        throws R2ExceptionShaderValidationFailed
      {

      }

      @Override
      public void onDeactivate(final JCGLInterfaceGL33Type g)
      {

      }
    };
  }
}
