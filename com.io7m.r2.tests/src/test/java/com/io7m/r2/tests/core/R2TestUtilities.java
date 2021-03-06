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
import com.io7m.jcanephora.fake.FakeContext;
import com.io7m.jcanephora.fake.FakeShaderListenerType;
import com.io7m.jcanephora.fake.JCGLImplementationFake;
import com.io7m.jcanephora.fake.JCGLImplementationFakeType;
import com.io7m.jcanephora.renderstate.JCGLBlendState;
import com.io7m.jfunctional.PartialBiFunctionType;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices3x3D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix3x3D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVectors3D;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.r2.core.api.R2Exception;
import com.io7m.r2.instances.R2InstanceBatchedType;
import com.io7m.r2.instances.R2InstanceBillboardedType;
import com.io7m.r2.instances.R2InstanceSingleType;
import com.io7m.r2.lights.R2LightScreenSingleType;
import com.io7m.r2.lights.R2LightSingleType;
import com.io7m.r2.lights.R2LightVolumeSingleType;
import com.io7m.r2.matrices.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.matrices.R2MatricesObserverValuesType;
import com.io7m.r2.projections.R2ProjectionFOV;
import com.io7m.r2.projections.R2ProjectionReadableType;
import com.io7m.r2.rendering.depth.api.R2MaterialDepthBatchedType;
import com.io7m.r2.rendering.depth.api.R2MaterialDepthSingleType;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferUsableType;
import com.io7m.r2.rendering.geometry.api.R2MaterialOpaqueBatchedType;
import com.io7m.r2.rendering.geometry.api.R2MaterialOpaqueBillboardedType;
import com.io7m.r2.rendering.geometry.api.R2MaterialOpaqueSingleType;
import com.io7m.r2.shaders.api.R2ExceptionShaderValidationFailed;
import com.io7m.r2.shaders.api.R2ShaderParametersMaterialType;
import com.io7m.r2.shaders.api.R2ShaderParametersViewType;
import com.io7m.r2.shaders.depth.api.R2ShaderDepthBatchedType;
import com.io7m.r2.shaders.depth.api.R2ShaderDepthBatchedUsableType;
import com.io7m.r2.shaders.depth.api.R2ShaderDepthSingleType;
import com.io7m.r2.shaders.depth.api.R2ShaderDepthSingleUsableType;
import com.io7m.r2.shaders.filter.api.R2ShaderFilterType;
import com.io7m.r2.shaders.filter.api.R2ShaderParametersFilterType;
import com.io7m.r2.shaders.geometry.api.R2ShaderGeometryBatchedType;
import com.io7m.r2.shaders.geometry.api.R2ShaderGeometryBatchedUsableType;
import com.io7m.r2.shaders.geometry.api.R2ShaderGeometryBillboardedType;
import com.io7m.r2.shaders.geometry.api.R2ShaderGeometryBillboardedUsableType;
import com.io7m.r2.shaders.geometry.api.R2ShaderGeometrySingleType;
import com.io7m.r2.shaders.geometry.api.R2ShaderGeometrySingleUsableType;
import com.io7m.r2.shaders.light.api.R2ShaderLightSingleUsableType;
import com.io7m.r2.shaders.light.api.R2ShaderParametersLightType;
import com.io7m.r2.shaders.translucent.api.R2ShaderTranslucentInstanceBatchedType;
import com.io7m.r2.shaders.translucent.api.R2ShaderTranslucentInstanceBillboardedType;
import com.io7m.r2.shaders.translucent.api.R2ShaderTranslucentInstanceSingleType;
import com.io7m.r2.spaces.R2SpaceClipType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceRGBType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import com.io7m.r2.transforms.R2TransformIdentity;
import com.io7m.r2.transforms.R2TransformReadableType;
import com.io7m.r2.transforms.R2TransformSiOT;
import com.io7m.r2.view_rays.R2ViewRays;
import com.io7m.r2.view_rays.R2ViewRaysReadableType;
import com.io7m.r2.view_rays.R2ViewRaysType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class R2TestUtilities
{
  private R2TestUtilities()
  {

  }

  public static R2InstanceSingleType getInstanceSingle(
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
          Long.valueOf(id), Integer.valueOf(ao.glName()));
      }

      @Override
      public JCGLArrayObjectUsableType arrayObject()
      {
        return ao;
      }

      @Override
      public R2TransformReadableType transform()
      {
        return R2TransformSiOT.create();
      }

      @Override
      public PMatrix3x3D<R2SpaceTextureType, R2SpaceTextureType> uvMatrix()
      {
        return PMatrices3x3D.identity();
      }
    };
  }

  public static JCGLArrayObjectType getArrayObject(
    final JCGLInterfaceGL33Type g)
  {
    final JCGLArrayBuffersType g_ab = g.arrayBuffers();
    final JCGLIndexBuffersType g_ib = g.indexBuffers();
    final JCGLArrayObjectsType g_ao = g.arrayObjects();

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

  public static R2MaterialOpaqueSingleType<Object> getMaterialSingle(
    final JCGLInterfaceGL33Type g,
    final R2ShaderGeometrySingleUsableType<Object> sh,
    final Object p,
    final long id)
  {
    return new R2MaterialOpaqueSingleType<Object>()
    {
      @Override
      public R2ShaderGeometrySingleUsableType<Object> shader()
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

  public static R2MaterialDepthSingleType<Object> getMaterialDepth(
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

  public static R2ShaderGeometrySingleType<Object>
  getShaderInstanceSingle(
    final JCGLInterfaceGL33Type g,
    final long s_id)
  {
    final JCGLShadersType g_sh = g.shaders();

    final List<String> v_lines = new ArrayList<>(3);
    v_lines.add("void facade() {\n");
    v_lines.add("  gl_Position = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    v_lines.add("}\n");
    final JCGLVertexShaderType v =
      g_sh.shaderCompileVertex("v_main", v_lines);

    final List<String> f_lines = new ArrayList<>(4);
    f_lines.add("out vec4 color_0;\n");
    f_lines.add("void facade() {\n");
    f_lines.add("  color_0 = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    f_lines.add("}\n");
    final JCGLFragmentShaderType f =
      g_sh.shaderCompileFragment("f_main", f_lines);

    final JCGLProgramShaderType pr =
      g_sh.shaderLinkProgram("p_main", v, Optional.empty(), f);

    return new R2ShaderGeometrySingleType<Object>()
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
        final R2ShaderParametersMaterialType<Object> mat_parameters)
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
      public Map<String, String> environment()
      {
        return new HashMap<>();
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
    final JCGLShadersType g_sh = g.shaders();

    final List<String> v_lines = new ArrayList<>(3);
    v_lines.add("void facade() {\n");
    v_lines.add("  gl_Position = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    v_lines.add("}\n");
    final JCGLVertexShaderType v =
      g_sh.shaderCompileVertex("v_main", v_lines);

    final List<String> f_lines = new ArrayList<>(4);
    f_lines.add("out vec4 color_0;\n");
    f_lines.add("void facade() {\n");
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
      public Map<String, String> environment()
      {
        return new HashMap<>();
      }

      @Override
      public void onReceiveMaterialValues(
        final JCGLInterfaceGL33Type g,
        final R2ShaderParametersMaterialType<Object> mat_parameters)
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
        final JCGLInterfaceGL33Type g)
      {

      }

      @Override
      public boolean updateRequired()
      {
        return false;
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

  public static R2ShaderGeometryBatchedType<Object>
  getShaderInstanceBatched(
    final JCGLInterfaceGL33Type g,
    final long s_id)
  {
    final JCGLShadersType g_sh = g.shaders();

    final List<String> v_lines = new ArrayList<>(3);
    v_lines.add("void facade() {\n");
    v_lines.add("  gl_Position = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    v_lines.add("}\n");
    final JCGLVertexShaderType v =
      g_sh.shaderCompileVertex("v_main", v_lines);

    final List<String> f_lines = new ArrayList<>(4);
    f_lines.add("out vec4 color_0;\n");
    f_lines.add("void facade() {\n");
    f_lines.add("  color_0 = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    f_lines.add("}\n");
    final JCGLFragmentShaderType f =
      g_sh.shaderCompileFragment("f_main", f_lines);

    final JCGLProgramShaderType pr =
      g_sh.shaderLinkProgram("p_main", v, Optional.empty(), f);

    return new R2ShaderGeometryBatchedType<Object>()
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
      public Map<String, String> environment()
      {
        return new HashMap<>();
      }

      @Override
      public void onReceiveMaterialValues(
        final JCGLInterfaceGL33Type g,
        final R2ShaderParametersMaterialType<Object> mat_parameters)
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

  public static R2ShaderGeometryBillboardedType<Object>
  getShaderInstanceBillboarded(
    final JCGLInterfaceGL33Type g,
    final long s_id)
  {
    final JCGLShadersType g_sh = g.shaders();

    final List<String> v_lines = new ArrayList<>(3);
    v_lines.add("void facade() {\n");
    v_lines.add("  gl_Position = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    v_lines.add("}\n");
    final JCGLVertexShaderType v =
      g_sh.shaderCompileVertex("v_main", v_lines);

    final List<String> f_lines = new ArrayList<>(4);
    f_lines.add("out vec4 color_0;\n");
    f_lines.add("void facade() {\n");
    f_lines.add("  color_0 = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    f_lines.add("}\n");
    final JCGLFragmentShaderType f =
      g_sh.shaderCompileFragment("f_main", f_lines);

    final JCGLProgramShaderType pr =
      g_sh.shaderLinkProgram("p_main", v, Optional.empty(), f);

    return new R2ShaderGeometryBillboardedType<Object>()
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
        final R2ShaderParametersMaterialType<Object> mat_parameters)
      {

      }

      @Override
      public Map<String, String> environment()
      {
        return new HashMap<>();
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
    final JCGLShadersType g_sh = g.shaders();

    final List<String> v_lines = new ArrayList<>(3);
    v_lines.add("void facade() {\n");
    v_lines.add("  gl_Position = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    v_lines.add("}\n");
    final JCGLVertexShaderType v =
      g_sh.shaderCompileVertex("v_main", v_lines);

    final List<String> f_lines = new ArrayList<>(4);
    f_lines.add("out vec4 color_0;\n");
    f_lines.add("void facade() {\n");
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
        final R2ShaderParametersViewType view_parameters)
      {

      }

      @Override
      public void onReceiveMaterialValues(
        final JCGLInterfaceGL33Type g,
        final R2ShaderParametersMaterialType<Object> mat_parameters)
      {

      }

      @Override
      public Map<String, String> environment()
      {
        return new HashMap<>();
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
    final R2ShaderGeometryBatchedUsableType<Object> sh,
    final Object p,
    final long id)
  {
    return new R2MaterialOpaqueBatchedType<Object>()
    {
      @Override
      public R2ShaderGeometryBatchedUsableType<Object> shader()
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
    final JCGLShadersType g_sh = g.shaders();

    final List<String> v_lines = new ArrayList<>(3);
    v_lines.add("void facade() {\n");
    v_lines.add("  gl_Position = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    v_lines.add("}\n");
    final JCGLVertexShaderType v =
      g_sh.shaderCompileVertex("v_main", v_lines);

    final List<String> f_lines = new ArrayList<>(4);
    f_lines.add("out vec4 color_0;\n");
    f_lines.add("void facade() {\n");
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
        final R2ShaderParametersFilterType<Object> parameters)
      {

      }

      @Override
      public Map<String, String> environment()
      {
        return new HashMap<>();
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
    final JCGLShadersType g_sh = g.shaders();

    final List<String> v_lines = new ArrayList<>(3);
    v_lines.add("void facade() {\n");
    v_lines.add("  gl_Position = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    v_lines.add("}\n");
    final JCGLVertexShaderType v =
      g_sh.shaderCompileVertex("v_main", v_lines);

    final List<String> f_lines = new ArrayList<>(4);
    f_lines.add("out vec4 color_0;\n");
    f_lines.add("void facade() {\n");
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
        final JCGLInterfaceGL33Type g,
        final R2GeometryBufferUsableType gbuffer,
        final JCGLTextureUnitType unit_albedo,
        final JCGLTextureUnitType unit_specular,
        final JCGLTextureUnitType unit_depth,
        final JCGLTextureUnitType unit_normals)
      {

      }

      @Override
      public void onReceiveValues(
        final JCGLInterfaceGL33Type g,
        final R2ShaderParametersLightType<R2LightSingleType> light_parameters)
      {

      }

      @Override
      public Map<String, String> environment()
      {
        return new HashMap<>();
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
    final PVector3D<R2SpaceRGBType> black = PVectors3D.zero();
    return new R2LightSingleType()
    {
      @Override
      public JCGLArrayObjectUsableType arrayObject()
      {
        return a0;
      }

      @Override
      public R2TransformReadableType transform()
      {
        return R2TransformIdentity.get();
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
      public PVector3D<R2SpaceRGBType> color()
      {
        return black;
      }

      @Override
      public double intensity()
      {
        return 1.0f;
      }

      @Override
      public void setColor(
        final PVector3D<R2SpaceRGBType> color)
      {

      }

      @Override
      public void setIntensity(final double i)
      {

      }
    };
  }

  @SuppressWarnings("unchecked")
  public static R2MatricesObserverValuesType getMatricesObserverValues()
  {
    final R2ProjectionFOV p =
      R2ProjectionFOV.createWith(
        (double) Math.toRadians(45.0),
        1.0f,
        1.0f,
        100.0f);

    final R2ViewRaysType vr =
      R2ViewRays.newViewRays();

    return new R2MatricesObserverValuesType()
    {
      @Override
      public R2ProjectionReadableType projection()
      {
        return p;
      }

      @Override
      public PMatrix4x4D<R2SpaceEyeType, R2SpaceClipType> matrixProjection()
      {
        return PMatrices4x4D.identity();
      }

      @Override
      public PMatrix4x4D<R2SpaceClipType, R2SpaceEyeType> matrixProjectionInverse()
      {
        return PMatrices4x4D.identity();
      }

      @Override
      public PMatrix4x4D<R2SpaceWorldType, R2SpaceEyeType> matrixView()
      {
        return PMatrices4x4D.identity();
      }

      @Override
      public PMatrix4x4D<R2SpaceEyeType, R2SpaceWorldType> matrixViewInverse()
      {
        return PMatrices4x4D.identity();
      }

      @Override
      public R2ViewRaysReadableType viewRays()
      {
        return vr;
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
        final JCGLInterfaceGL33Type g)
      {

      }

      @Override
      public boolean updateRequired()
      {
        return false;
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
    final R2ShaderGeometryBillboardedUsableType<Object> sh,
    final Object p,
    final long id)
  {
    return new R2MaterialOpaqueBillboardedType<Object>()
    {
      @Override
      public R2ShaderGeometryBillboardedUsableType<Object> shader()
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
    final JCGLShadersType g_sh = g.shaders();

    final List<String> v_lines = new ArrayList<>(3);
    v_lines.add("void facade() {\n");
    v_lines.add("  gl_Position = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    v_lines.add("}\n");
    final JCGLVertexShaderType v =
      g_sh.shaderCompileVertex("v_main", v_lines);

    final List<String> f_lines = new ArrayList<>(4);
    f_lines.add("out vec4 color_0;\n");
    f_lines.add("void facade() {\n");
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
      public Map<String, String> environment()
      {
        return new HashMap<>();
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
        final R2ShaderParametersMaterialType<Object> mat_parameters)
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
    final JCGLShadersType g_sh = g.shaders();

    final List<String> v_lines = new ArrayList<>(3);
    v_lines.add("void facade() {\n");
    v_lines.add("  gl_Position = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    v_lines.add("}\n");
    final JCGLVertexShaderType v =
      g_sh.shaderCompileVertex("v_main", v_lines);

    final List<String> f_lines = new ArrayList<>(4);
    f_lines.add("out vec4 color_0;\n");
    f_lines.add("void facade() {\n");
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
      public Map<String, String> environment()
      {
        return new HashMap<>();
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
        final R2ShaderParametersMaterialType<Object> mat_parameters)
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
    final JCGLShadersType g_sh = g.shaders();

    final List<String> v_lines = new ArrayList<>(3);
    v_lines.add("void facade() {\n");
    v_lines.add("  gl_Position = vec4 (1.0, 1.0, 1.0, 1.0);\n");
    v_lines.add("}\n");
    final JCGLVertexShaderType v =
      g_sh.shaderCompileVertex("v_main", v_lines);

    final List<String> f_lines = new ArrayList<>(4);
    f_lines.add("out vec4 color_0;\n");
    f_lines.add("void facade() {\n");
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
      public Map<String, String> environment()
      {
        return new HashMap<>();
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
        final R2ShaderParametersMaterialType<Object> mat_parameters)
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
