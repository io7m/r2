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
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.core.R2InstanceSingleMeshType;
import com.io7m.r2.core.R2InstanceType;
import com.io7m.r2.core.R2MaterialOpaqueSingleMeshType;
import com.io7m.r2.core.R2MaterialType;
import com.io7m.r2.core.R2RendererExceptionInstanceAlreadyVisible;
import com.io7m.r2.core.R2SceneOpaques;
import com.io7m.r2.core.R2SceneOpaquesConsumerType;
import com.io7m.r2.core.R2SceneOpaquesType;
import com.io7m.r2.core.R2ShaderType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public final class R2SceneOpaquesTest
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2SceneOpaquesTest.class);
  }

  @Rule public ExpectedException expected = ExpectedException.none();

  private static R2InstanceSingleMeshType getInstance(
    final JCGLInterfaceGL33Type g,
    final JCGLArrayObjectType ao,
    final long id)
  {
    return new R2InstanceSingleMeshType()
    {
      @Override public long getInstanceID()
      {
        return id;
      }

      @Override public String toString()
      {
        return String.format                               (
          "[instance %d [array %d]]",
          Long.valueOf(id), Integer.valueOf(ao.getGLName()));
      }

      @Override public JCGLArrayObjectUsableType getArrayObject()
      {
        return ao;
      }
    };
  }

  private static JCGLArrayObjectType getArrayObject(
    final JCGLInterfaceGL33Type g)
  {
    final JCGLArrayBuffersType g_ab = g.getArrayBuffers();
    final JCGLIndexBuffersType g_ib = g.getIndexBuffers();
    final JCGLArrayObjectsType g_ao = g.getArrayObjects();

    final JCGLIndexBufferType ib =
      g_ib.indexBufferAllocate         (
        3L,
        JCGLUnsignedType.TYPE_UNSIGNED_INT,
        JCGLUsageHint.USAGE_STATIC_DRAW);
    final JCGLArrayBufferUsableType a =
      g_ab.arrayBufferAllocate(3L * 4L, JCGLUsageHint.USAGE_STATIC_DRAW);

    final JCGLArrayObjectBuilderType aob = g_ao.arrayObjectNewBuilder();
    aob.setAttributeFloatingPoint                         (
      0, a, 3, JCGLScalarType.TYPE_FLOAT, 3 * 4, 0L, false);
    aob.setIndexBuffer(ib);

    final JCGLArrayObjectType ao = g_ao.arrayObjectAllocate(aob);
    g_ao.arrayObjectUnbind();
    return ao;
  }

  private static R2MaterialOpaqueSingleMeshType<Object> getMaterial(
    final JCGLInterfaceGL33Type g,
    final R2ShaderType<Object> sh,
    final Object p,
    final long id)
  {
    return new R2MaterialOpaqueSingleMeshType<Object>()
    {
      @Override public long getMaterialID()
      {
        return id;
      }

      @Override public String toString()
      {
        return String.format("[material %d %s]", Long.valueOf(id), sh);
      }

      @Override public Object getShaderParameters()
      {
        return p;
      }

      @Override public R2ShaderType<Object> getShader()
      {
        return sh;
      }
    };
  }

  private static R2ShaderType<Object> getShader(
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

    return new R2ShaderType<Object>()
    {
      @Override public long getShaderID()
      {
        return s_id;
      }

      @Override public String toString()
      {
        return String.format("[shader %d]", Long.valueOf(s_id));
      }

      @Override public Class<Object> getShaderParametersType()
      {
        return Object.class;
      }
    };
  }

  private static JCGLInterfaceGL33Type getGL()
    throws JCGLExceptionUnsupported, JCGLExceptionNonCompliant
  {
    final JCGLImplementationFakeType gi =
      JCGLImplementationFake.getInstance();
    final JCGLContextType gc =
      gi.newContext("main", new FakeShaderListenerType()
      {
        @Override public void onCompileVertexShaderStart(
          final FakeContext context,
          final String name,
          final List<String> sources)
          throws JCGLException
        {

        }

        @Override public void onCompileFragmentShaderStart(
          final FakeContext context,
          final String name,
          final List<String> sources)
          throws JCGLException
        {

        }

        @Override public void onCompileGeometryShaderStart(
          final FakeContext context,
          final String name,
          final List<String> sources)
          throws JCGLException
        {

        }

        @Override public void onLinkProgram(
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

  @Test public void testAlreadyVisible()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2SceneOpaquesTest.getGL();
    final R2SceneOpaquesType o = R2SceneOpaques.newOpaques();
    final JCGLArrayObjectType a0 = R2SceneOpaquesTest.getArrayObject(g);
    final R2InstanceSingleMeshType i =
      R2SceneOpaquesTest.getInstance(g, a0, 0L);
    final R2ShaderType<Object> s = R2SceneOpaquesTest.getShader(g, 0L);
    final R2MaterialOpaqueSingleMeshType<Object> m0 =
      R2SceneOpaquesTest.getMaterial(g, s, new Object(), 0L);
    final R2MaterialOpaqueSingleMeshType<Object> m1 =
      R2SceneOpaquesTest.getMaterial(g, s, new Object(), 1L);

    o.opaquesAddSingleMesh(i, m0);
    this.expected.expect(R2RendererExceptionInstanceAlreadyVisible.class);
    o.opaquesAddSingleMesh(i, m1);
  }

  @Test public void testEmpty()
  {
    final AtomicBoolean finished = new AtomicBoolean(false);
    final AtomicBoolean started = new AtomicBoolean(false);
    final R2SceneOpaquesType o = R2SceneOpaques.newOpaques();

    o.opaquesExecute(new UnreachableConsumer()
    {
      @Override public void onFinish()
      {
        finished.set(true);
      }

      @Override public void onStart()
      {
        started.set(true);
      }
    });

    Assert.assertTrue(started.get());
    Assert.assertTrue(finished.get());
  }

  @Test public void testOrdering_0()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2SceneOpaquesTest.getGL();

    final JCGLArrayObjectType a0 = R2SceneOpaquesTest.getArrayObject(g);
    final JCGLArrayObjectType a1 = R2SceneOpaquesTest.getArrayObject(g);

    final R2InstanceSingleMeshType i0a0 =
      R2SceneOpaquesTest.getInstance(g, a0, 0L);
    final R2InstanceSingleMeshType i1a0 =
      R2SceneOpaquesTest.getInstance(g, a0, 1L);
    final R2InstanceSingleMeshType i2a0 =
      R2SceneOpaquesTest.getInstance(g, a0, 2L);

    final R2InstanceSingleMeshType i3a1 =
      R2SceneOpaquesTest.getInstance(g, a1, 3L);
    final R2InstanceSingleMeshType i4a1 =
      R2SceneOpaquesTest.getInstance(g, a1, 4L);
    final R2InstanceSingleMeshType i5a1 =
      R2SceneOpaquesTest.getInstance(g, a1, 5L);

    final R2InstanceSingleMeshType i6a0 =
      R2SceneOpaquesTest.getInstance(g, a0, 6L);
    final R2InstanceSingleMeshType i7a0 =
      R2SceneOpaquesTest.getInstance(g, a0, 7L);
    final R2InstanceSingleMeshType i8a0 =
      R2SceneOpaquesTest.getInstance(g, a0, 8L);

    final R2InstanceSingleMeshType i9a1 =
      R2SceneOpaquesTest.getInstance(g, a1, 9L);
    final R2InstanceSingleMeshType i10a1 =
      R2SceneOpaquesTest.getInstance(g, a1, 10L);
    final R2InstanceSingleMeshType i11a1 =
      R2SceneOpaquesTest.getInstance(g, a1, 11L);

    final R2InstanceSingleMeshType i12a0 =
      R2SceneOpaquesTest.getInstance(g, a0, 12L);
    final R2InstanceSingleMeshType i13a0 =
      R2SceneOpaquesTest.getInstance(g, a0, 13L);
    final R2InstanceSingleMeshType i14a0 =
      R2SceneOpaquesTest.getInstance(g, a0, 14L);

    final R2InstanceSingleMeshType i15a1 =
      R2SceneOpaquesTest.getInstance(g, a1, 15L);
    final R2InstanceSingleMeshType i16a1 =
      R2SceneOpaquesTest.getInstance(g, a1, 16L);
    final R2InstanceSingleMeshType i17a1 =
      R2SceneOpaquesTest.getInstance(g, a1, 17L);

    final R2InstanceSingleMeshType i18a0 =
      R2SceneOpaquesTest.getInstance(g, a0, 18L);
    final R2InstanceSingleMeshType i19a0 =
      R2SceneOpaquesTest.getInstance(g, a0, 19L);
    final R2InstanceSingleMeshType i20a0 =
      R2SceneOpaquesTest.getInstance(g, a0, 20L);

    final R2InstanceSingleMeshType i21a1 =
      R2SceneOpaquesTest.getInstance(g, a1, 21L);
    final R2InstanceSingleMeshType i22a1 =
      R2SceneOpaquesTest.getInstance(g, a1, 22L);
    final R2InstanceSingleMeshType i23a1 =
      R2SceneOpaquesTest.getInstance(g, a1, 23L);

    final R2ShaderType<Object> s0 = R2SceneOpaquesTest.getShader(g, 0L);
    final R2ShaderType<Object> s1 = R2SceneOpaquesTest.getShader(g, 1L);

    final R2MaterialOpaqueSingleMeshType<Object> m0 =
      R2SceneOpaquesTest.getMaterial(g, s0, new Object(), 0L);
    final R2MaterialOpaqueSingleMeshType<Object> m1 =
      R2SceneOpaquesTest.getMaterial(g, s0, new Object(), 1L);
    final R2MaterialOpaqueSingleMeshType<Object> m2 =
      R2SceneOpaquesTest.getMaterial(g, s1, new Object(), 2L);
    final R2MaterialOpaqueSingleMeshType<Object> m3 =
      R2SceneOpaquesTest.getMaterial(g, s1, new Object(), 3L);

    final R2SceneOpaquesType o = R2SceneOpaques.newOpaques();
    o.opaquesAddSingleMesh(i0a0, m0);
    o.opaquesAddSingleMesh(i1a0, m0);
    o.opaquesAddSingleMesh(i2a0, m0);
    o.opaquesAddSingleMesh(i3a1, m0);
    o.opaquesAddSingleMesh(i4a1, m0);
    o.opaquesAddSingleMesh(i5a1, m0);

    o.opaquesAddSingleMesh(i6a0, m1);
    o.opaquesAddSingleMesh(i7a0, m1);
    o.opaquesAddSingleMesh(i8a0, m1);
    o.opaquesAddSingleMesh(i9a1, m1);
    o.opaquesAddSingleMesh(i10a1, m1);
    o.opaquesAddSingleMesh(i11a1, m1);

    o.opaquesAddSingleMesh(i12a0, m2);
    o.opaquesAddSingleMesh(i13a0, m2);
    o.opaquesAddSingleMesh(i14a0, m2);
    o.opaquesAddSingleMesh(i15a1, m2);
    o.opaquesAddSingleMesh(i16a1, m2);
    o.opaquesAddSingleMesh(i17a1, m2);

    o.opaquesAddSingleMesh(i18a0, m3);
    o.opaquesAddSingleMesh(i19a0, m3);
    o.opaquesAddSingleMesh(i20a0, m3);
    o.opaquesAddSingleMesh(i21a1, m3);
    o.opaquesAddSingleMesh(i22a1, m3);
    o.opaquesAddSingleMesh(i23a1, m3);

    final AtomicBoolean finished = new AtomicBoolean(false);
    final AtomicBoolean started = new AtomicBoolean(false);
    final List<Ops.OpType> ops = new ArrayList<>();

    final Set<R2ShaderType<?>> shaders_started = new HashSet<>();
    final Set<R2MaterialType<?>> materials_started = new HashSet<>();
    final Set<R2InstanceType> instances = new HashSet<>();

    o.opaquesExecute(new R2SceneOpaquesConsumerType()
    {
      int shader_starts = 0;
      int instance_calls = 0;

      @Override public void onStart()
      {
        started.set(true);
      }

      @Override public void onShaderStart(final R2ShaderType<?> s)
      {
        Assert.assertTrue(this.shader_starts < 2);

        R2SceneOpaquesTest.LOG.debug("shader start {}", s);
        ops.add(new Ops.OpShaderStart(s));
        shaders_started.add(s);
        this.shader_starts++;
      }

      @Override public void onMaterialStart(
        final R2ShaderType<?> s,
        final R2MaterialType<?> material)
      {
        R2SceneOpaquesTest.LOG.debug("material start {} {}", s, material);
        ops.add(new Ops.OpMaterialStart(material, s));
        materials_started.add(material);
      }

      @Override public void onInstancesStartArray(final R2InstanceType i)
      {
        R2SceneOpaquesTest.LOG.debug("instance start array {}", i);
        ops.add(new Ops.OpInstanceStartArray(i));
      }

      @Override public void onInstance(
        final R2ShaderType<?> s,
        final R2MaterialType<?> material,
        final R2InstanceType i)
      {
        Assert.assertTrue(this.instance_calls < 24);
        R2SceneOpaquesTest.LOG.debug("instance {} {} {}", s, material, i);
        ops.add(new Ops.OpInstance(i, material, s));
        instances.add(i);
        this.instance_calls++;
      }

      @Override public void onMaterialFinish(
        final R2ShaderType<?> s,
        final R2MaterialType<?> material)
      {
        R2SceneOpaquesTest.LOG.debug("material finish {} {}", s, material);
        ops.add(new Ops.OpMaterialFinish(material, s));
      }

      @Override public void onShaderFinish(final R2ShaderType<?> s)
      {
        R2SceneOpaquesTest.LOG.debug("shader finish {}", s);
        ops.add(new Ops.OpShaderFinish(s));
      }

      @Override public void onFinish()
      {
        finished.set(true);
      }
    });

    Assert.assertTrue(started.get());
    Assert.assertTrue(finished.get());

    Assert.assertTrue(shaders_started.contains(s0));
    Assert.assertTrue(shaders_started.contains(s1));
    Assert.assertEquals(2L, (long) shaders_started.size());
    Assert.assertTrue(materials_started.contains(m0));
    Assert.assertTrue(materials_started.contains(m1));
    Assert.assertTrue(materials_started.contains(m2));
    Assert.assertTrue(materials_started.contains(m3));
    Assert.assertEquals(4L, (long) materials_started.size());

    Assert.assertTrue(instances.contains(i0a0));
    Assert.assertTrue(instances.contains(i1a0));
    Assert.assertTrue(instances.contains(i2a0));
    Assert.assertTrue(instances.contains(i3a1));
    Assert.assertTrue(instances.contains(i4a1));
    Assert.assertTrue(instances.contains(i5a1));
    Assert.assertTrue(instances.contains(i6a0));
    Assert.assertTrue(instances.contains(i7a0));
    Assert.assertTrue(instances.contains(i8a0));
    Assert.assertTrue(instances.contains(i9a1));
    Assert.assertTrue(instances.contains(i10a1));
    Assert.assertTrue(instances.contains(i11a1));
    Assert.assertTrue(instances.contains(i12a0));
    Assert.assertTrue(instances.contains(i13a0));
    Assert.assertTrue(instances.contains(i14a0));
    Assert.assertTrue(instances.contains(i15a1));
    Assert.assertTrue(instances.contains(i16a1));
    Assert.assertTrue(instances.contains(i17a1));
    Assert.assertTrue(instances.contains(i18a0));
    Assert.assertTrue(instances.contains(i19a0));
    Assert.assertTrue(instances.contains(i20a0));
    Assert.assertTrue(instances.contains(i21a1));
    Assert.assertTrue(instances.contains(i22a1));
    Assert.assertTrue(instances.contains(i23a1));
    Assert.assertEquals(24L, (long) instances.size());

    Assert.assertEquals(new Ops.OpShaderStart(s0), ops.get(0));
    Assert.assertEquals(Ops.OpMaterialStart.class, ops.get(1).getClass());
    Assert.assertEquals(Ops.OpInstanceStartArray.class, ops.get(2).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(3).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(4).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(5).getClass());
    Assert.assertEquals(Ops.OpInstanceStartArray.class, ops.get(6).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(7).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(8).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(9).getClass());
    Assert.assertEquals(Ops.OpMaterialFinish.class, ops.get(10).getClass());
    Assert.assertEquals(Ops.OpMaterialStart.class, ops.get(11).getClass());
    Assert.assertEquals(Ops.OpInstanceStartArray.class, ops.get(12).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(13).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(14).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(15).getClass());
    Assert.assertEquals(Ops.OpInstanceStartArray.class, ops.get(16).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(17).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(18).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(19).getClass());
    Assert.assertEquals(Ops.OpMaterialFinish.class, ops.get(20).getClass());
    Assert.assertEquals(new Ops.OpShaderFinish(s0), ops.get(21));

    Assert.assertEquals(new Ops.OpShaderStart(s1), ops.get(22));
    Assert.assertEquals(Ops.OpMaterialStart.class, ops.get(23).getClass());
    Assert.assertEquals(Ops.OpInstanceStartArray.class, ops.get(24).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(25).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(26).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(27).getClass());
    Assert.assertEquals(Ops.OpInstanceStartArray.class, ops.get(28).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(29).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(30).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(31).getClass());
    Assert.assertEquals(Ops.OpMaterialFinish.class, ops.get(32).getClass());
    Assert.assertEquals(Ops.OpMaterialStart.class, ops.get(33).getClass());
    Assert.assertEquals(Ops.OpInstanceStartArray.class, ops.get(34).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(35).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(36).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(37).getClass());
    Assert.assertEquals(Ops.OpInstanceStartArray.class, ops.get(38).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(39).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(40).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(41).getClass());
    Assert.assertEquals(Ops.OpMaterialFinish.class, ops.get(42).getClass());
    Assert.assertEquals(new Ops.OpShaderFinish(s1), ops.get(43));
  }

  @Test public void testReset_0()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2SceneOpaquesTest.getGL();

    final JCGLArrayObjectType a0 = R2SceneOpaquesTest.getArrayObject(g);
    final JCGLArrayObjectType a1 = R2SceneOpaquesTest.getArrayObject(g);

    final R2InstanceSingleMeshType i0a0 =
      R2SceneOpaquesTest.getInstance(g, a0, 0L);

    final R2ShaderType<Object> s0 = R2SceneOpaquesTest.getShader(g, 0L);

    final R2MaterialOpaqueSingleMeshType<Object> m0 =
      R2SceneOpaquesTest.getMaterial(g, s0, new Object(), 0L);

    final R2SceneOpaquesType o = R2SceneOpaques.newOpaques();
    o.opaquesAddSingleMesh(i0a0, m0);

    final AtomicBoolean finished = new AtomicBoolean(false);
    final AtomicBoolean started = new AtomicBoolean(false);
    final List<Ops.OpType> ops = new ArrayList<>();

    final Set<R2ShaderType<?>> shaders_started = new HashSet<>();
    final Set<R2MaterialType<?>> materials_started = new HashSet<>();
    final Set<R2InstanceType> instances = new HashSet<>();

    o.opaquesExecute(new R2SceneOpaquesConsumerType()
    {
      int shader_starts = 0;
      int instance_calls = 0;

      @Override public void onStart()
      {
        started.set(true);
      }

      @Override public void onShaderStart(final R2ShaderType<?> s)
      {
        Assert.assertTrue(this.shader_starts < 2);

        R2SceneOpaquesTest.LOG.debug("shader start {}", s);
        ops.add(new Ops.OpShaderStart(s));
        shaders_started.add(s);
        this.shader_starts++;
      }

      @Override public void onMaterialStart(
        final R2ShaderType<?> s,
        final R2MaterialType<?> material)
      {
        R2SceneOpaquesTest.LOG.debug("material start {} {}", s, material);
        ops.add(new Ops.OpMaterialStart(material, s));
        materials_started.add(material);
      }

      @Override public void onInstancesStartArray(final R2InstanceType i)
      {
        R2SceneOpaquesTest.LOG.debug("instance start array {}", i);
        ops.add(new Ops.OpInstanceStartArray(i));
      }

      @Override public void onInstance(
        final R2ShaderType<?> s,
        final R2MaterialType<?> material,
        final R2InstanceType i)
      {
        Assert.assertTrue(this.instance_calls < 24);
        R2SceneOpaquesTest.LOG.debug("instance {} {} {}", s, material, i);
        ops.add(new Ops.OpInstance(i, material, s));
        instances.add(i);
        this.instance_calls++;
      }

      @Override public void onMaterialFinish(
        final R2ShaderType<?> s,
        final R2MaterialType<?> material)
      {
        R2SceneOpaquesTest.LOG.debug("material finish {} {}", s, material);
        ops.add(new Ops.OpMaterialFinish(material, s));
      }

      @Override public void onShaderFinish(final R2ShaderType<?> s)
      {
        R2SceneOpaquesTest.LOG.debug("shader finish {}", s);
        ops.add(new Ops.OpShaderFinish(s));
      }

      @Override public void onFinish()
      {
        finished.set(true);
      }
    });

    Assert.assertTrue(started.get());
    Assert.assertTrue(finished.get());

    Assert.assertTrue(shaders_started.contains(s0));
    Assert.assertEquals(1L, (long) shaders_started.size());
    Assert.assertTrue(materials_started.contains(m0));
    Assert.assertEquals(1L, (long) materials_started.size());

    Assert.assertTrue(instances.contains(i0a0));
    Assert.assertEquals(1L, (long) instances.size());

    Assert.assertEquals(new Ops.OpShaderStart(s0), ops.get(0));
    Assert.assertEquals(Ops.OpMaterialStart.class, ops.get(1).getClass());
    Assert.assertEquals(Ops.OpInstanceStartArray.class, ops.get(2).getClass());
    Assert.assertEquals(Ops.OpInstance.class, ops.get(3).getClass());
    Assert.assertEquals(Ops.OpMaterialFinish.class, ops.get(4).getClass());
    Assert.assertEquals(new Ops.OpShaderFinish(s0), ops.get(5));

    ops.clear();
    started.set(false);
    finished.set(false);

    o.opaquesReset();

    o.opaquesExecute(new R2SceneOpaquesConsumerType()
    {
      int shader_starts = 0;
      int instance_calls = 0;

      @Override public void onStart()
      {
        started.set(true);
      }

      @Override public void onShaderStart(final R2ShaderType<?> s)
      {
        Assert.assertTrue(this.shader_starts < 2);

        R2SceneOpaquesTest.LOG.debug("shader start {}", s);
        ops.add(new Ops.OpShaderStart(s));
        shaders_started.add(s);
        this.shader_starts++;
      }

      @Override public void onMaterialStart(
        final R2ShaderType<?> s,
        final R2MaterialType<?> material)
      {
        R2SceneOpaquesTest.LOG.debug("material start {} {}", s, material);
        ops.add(new Ops.OpMaterialStart(material, s));
        materials_started.add(material);
      }

      @Override public void onInstancesStartArray(final R2InstanceType i)
      {
        R2SceneOpaquesTest.LOG.debug("instance start array {}", i);
        ops.add(new Ops.OpInstanceStartArray(i));
      }

      @Override public void onInstance(
        final R2ShaderType<?> s,
        final R2MaterialType<?> material,
        final R2InstanceType i)
      {
        Assert.assertTrue(this.instance_calls < 24);
        R2SceneOpaquesTest.LOG.debug("instance {} {} {}", s, material, i);
        ops.add(new Ops.OpInstance(i, material, s));
        instances.add(i);
        this.instance_calls++;
      }

      @Override public void onMaterialFinish(
        final R2ShaderType<?> s,
        final R2MaterialType<?> material)
      {
        R2SceneOpaquesTest.LOG.debug("material finish {} {}", s, material);
        ops.add(new Ops.OpMaterialFinish(material, s));
      }

      @Override public void onShaderFinish(final R2ShaderType<?> s)
      {
        R2SceneOpaquesTest.LOG.debug("shader finish {}", s);
        ops.add(new Ops.OpShaderFinish(s));
      }

      @Override public void onFinish()
      {
        finished.set(true);
      }
    });

    Assert.assertTrue(started.get());
    Assert.assertTrue(finished.get());

    Assert.assertTrue(shaders_started.contains(s0));
    Assert.assertEquals(1L, (long) shaders_started.size());
    Assert.assertTrue(materials_started.contains(m0));
    Assert.assertEquals(1L, (long) materials_started.size());

    Assert.assertTrue(instances.contains(i0a0));
    Assert.assertEquals(1L, (long) instances.size());

    Assert.assertEquals(0L, (long) ops.size());
  }

  private static abstract class UnreachableConsumer
    implements R2SceneOpaquesConsumerType
  {
    @Override public void onStart()
    {
      throw new UnreachableCodeException();
    }

    @Override public void onShaderStart(final R2ShaderType<?> s)
    {
      throw new UnreachableCodeException();
    }

    @Override public void onMaterialStart(
      final R2ShaderType<?> s,
      final R2MaterialType<?> material)
    {
      throw new UnreachableCodeException();
    }

    @Override public void onInstancesStartArray(final R2InstanceType i)
    {
      throw new UnreachableCodeException();
    }

    @Override public void onInstance(
      final R2ShaderType<?> s,
      final R2MaterialType<?> material,
      final R2InstanceType i)
    {
      throw new UnreachableCodeException();
    }

    @Override public void onMaterialFinish(
      final R2ShaderType<?> s,
      final R2MaterialType<?> material)
    {
      throw new UnreachableCodeException();
    }

    @Override public void onShaderFinish(final R2ShaderType<?> s)
    {
      throw new UnreachableCodeException();
    }

    @Override public void onFinish()
    {
      throw new UnreachableCodeException();
    }
  }

  private static final class Ops
  {
    interface OpType
    {
      <A> A matchOp(
        Function<OpShaderStart, A> on_shader_start,
        Function<OpMaterialStart, A> on_material_start,
        Function<OpInstanceStartArray, A> on_instance_start_array,
        Function<OpInstance, A> on_instance,
        Function<OpMaterialFinish, A> on_material_finish,
        Function<OpShaderFinish, A> on_shader_finish);
    }

    private static final class OpShaderStart implements OpType
    {
      R2ShaderType<?> shader;

      OpShaderStart(final R2ShaderType<?> s)
      {
        this.shader = s;
      }

      @Override public boolean equals(final Object o)
      {
        if (this == o) {
          return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
          return false;
        }

        final OpShaderStart that = (OpShaderStart) o;
        return this.shader.equals(that.shader);
      }

      @Override public int hashCode()
      {
        return this.shader.hashCode();
      }

      @Override public <A> A matchOp(
        final Function<OpShaderStart, A> on_shader_start,
        final Function<OpMaterialStart, A> on_material_start,
        final Function<OpInstanceStartArray, A> on_instance_start_array,
        final Function<OpInstance, A> on_instance,
        final Function<OpMaterialFinish, A> on_material_finish,
        final Function<OpShaderFinish, A> on_shader_finish)
      {
        return on_shader_start.apply(this);
      }
    }

    private static final class OpMaterialStart implements OpType
    {
      R2ShaderType<?>   shader;
      R2MaterialType<?> material;

      OpMaterialStart(
        final R2MaterialType<?> m,
        final R2ShaderType<?> s)
      {
        this.material = m;
        this.shader = s;
      }

      @Override public boolean equals(final Object o)
      {
        if (this == o) {
          return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
          return false;
        }

        final OpMaterialStart that = (OpMaterialStart) o;
        return this.shader.equals(that.shader)
               && this.material.equals(that.material);
      }

      @Override public int hashCode()
      {
        int result = this.shader.hashCode();
        result = 31 * result + this.material.hashCode();
        return result;
      }

      @Override public <A> A matchOp(
        final Function<OpShaderStart, A> on_shader_start,
        final Function<OpMaterialStart, A> on_material_start,
        final Function<OpInstanceStartArray, A> on_instance_start_array,
        final Function<OpInstance, A> on_instance,
        final Function<OpMaterialFinish, A> on_material_finish,
        final Function<OpShaderFinish, A> on_shader_finish)
      {
        return on_material_start.apply(this);
      }
    }

    private static final class OpInstanceStartArray implements OpType
    {
      R2InstanceType instance;

      OpInstanceStartArray(final R2InstanceType i)
      {
        this.instance = i;
      }

      @Override public boolean equals(final Object o)
      {
        if (this == o) {
          return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
          return false;
        }

        final OpInstanceStartArray that = (OpInstanceStartArray) o;
        return this.instance.equals(that.instance);
      }

      @Override public int hashCode()
      {
        return this.instance.hashCode();
      }

      @Override public <A> A matchOp(
        final Function<OpShaderStart, A> on_shader_start,
        final Function<OpMaterialStart, A> on_material_start,
        final Function<OpInstanceStartArray, A> on_instance_start_array,
        final Function<OpInstance, A> on_instance,
        final Function<OpMaterialFinish, A> on_material_finish,
        final Function<OpShaderFinish, A> on_shader_finish)
      {
        return on_instance_start_array.apply(this);
      }
    }

    private static final class OpInstance implements OpType
    {
      R2ShaderType<?>   shader;
      R2MaterialType<?> material;
      R2InstanceType    instance;

      OpInstance(
        final R2InstanceType i,
        final R2MaterialType<?> m,
        final R2ShaderType<?> s)
      {
        this.instance = i;
        this.material = m;
        this.shader = s;
      }

      @Override public boolean equals(final Object o)
      {
        if (this == o) {
          return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
          return false;
        }

        final OpInstance that = (OpInstance) o;
        return this.shader.equals(that.shader)
               && this.material.equals(that.material)
               && this.instance.equals(that.instance);
      }

      @Override public int hashCode()
      {
        int result = this.shader.hashCode();
        result = 31 * result + this.material.hashCode();
        result = 31 * result + this.instance.hashCode();
        return result;
      }

      @Override public <A> A matchOp(
        final Function<OpShaderStart, A> on_shader_start,
        final Function<OpMaterialStart, A> on_material_start,
        final Function<OpInstanceStartArray, A> on_instance_start_array,
        final Function<OpInstance, A> on_instance,
        final Function<OpMaterialFinish, A> on_material_finish,
        final Function<OpShaderFinish, A> on_shader_finish)
      {
        return on_instance.apply(this);
      }
    }

    private static final class OpMaterialFinish implements OpType
    {
      R2ShaderType<?>   shader;
      R2MaterialType<?> material;

      OpMaterialFinish(
        final R2MaterialType<?> m,
        final R2ShaderType<?> s)
      {
        this.material = m;
        this.shader = s;
      }

      @Override public boolean equals(final Object o)
      {
        if (this == o) {
          return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
          return false;
        }

        final OpMaterialFinish that = (OpMaterialFinish) o;
        return this.shader.equals(that.shader)
               && this.material.equals(that.material);
      }

      @Override public int hashCode()
      {
        int result = this.shader.hashCode();
        result = 31 * result + this.material.hashCode();
        return result;
      }

      @Override public <A> A matchOp(
        final Function<OpShaderStart, A> on_shader_start,
        final Function<OpMaterialStart, A> on_material_start,
        final Function<OpInstanceStartArray, A> on_instance_start_array,
        final Function<OpInstance, A> on_instance,
        final Function<OpMaterialFinish, A> on_material_finish,
        final Function<OpShaderFinish, A> on_shader_finish)
      {
        return on_material_finish.apply(this);
      }
    }

    private static final class OpShaderFinish implements OpType
    {
      R2ShaderType<?> shader;

      OpShaderFinish(final R2ShaderType<?> s)
      {
        this.shader = s;
      }

      @Override public boolean equals(final Object o)
      {
        if (this == o) {
          return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
          return false;
        }

        final OpShaderFinish that = (OpShaderFinish) o;
        return this.shader.equals(that.shader);
      }

      @Override public int hashCode()
      {
        return this.shader.hashCode();
      }

      @Override public <A> A matchOp(
        final Function<OpShaderStart, A> on_shader_start,
        final Function<OpMaterialStart, A> on_material_start,
        final Function<OpInstanceStartArray, A> on_instance_start_array,
        final Function<OpInstance, A> on_instance,
        final Function<OpMaterialFinish, A> on_material_finish,
        final Function<OpShaderFinish, A> on_shader_finish)
      {
        return on_shader_finish.apply(this);
      }
    }
  }

}
