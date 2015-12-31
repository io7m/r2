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

import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
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

  @Test
  public void testAlreadyVisible()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getGL();
    final R2SceneOpaquesType o = R2SceneOpaques.newOpaques();
    final JCGLArrayObjectType a0 = R2TestUtilities.getArrayObject(g);
    final R2InstanceSingleMeshType i =
      R2TestUtilities.getInstance(g, a0, 0L);
    final R2ShaderType<Object> s = R2TestUtilities.getShader(g, 0L);
    final R2MaterialOpaqueSingleMeshType<Object> m0 =
      R2TestUtilities.getMaterial(g, s, new Object(), 0L);
    final R2MaterialOpaqueSingleMeshType<Object> m1 =
      R2TestUtilities.getMaterial(g, s, new Object(), 1L);

    o.opaquesAddSingleMesh(i, m0);
    this.expected.expect(R2RendererExceptionInstanceAlreadyVisible.class);
    o.opaquesAddSingleMesh(i, m1);
  }

  @Test
  public void testEmpty()
  {
    final AtomicBoolean finished = new AtomicBoolean(false);
    final AtomicBoolean started = new AtomicBoolean(false);
    final R2SceneOpaquesType o = R2SceneOpaques.newOpaques();

    o.opaquesExecute(new UnreachableConsumer()
    {
      @Override
      public void onFinish()
      {
        finished.set(true);
      }

      @Override
      public void onStart()
      {
        started.set(true);
      }
    });

    Assert.assertTrue(started.get());
    Assert.assertTrue(finished.get());
  }

  @Test
  public <M> void testOrdering_0()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getGL();

    final JCGLArrayObjectType a0 = R2TestUtilities.getArrayObject(g);
    final JCGLArrayObjectType a1 = R2TestUtilities.getArrayObject(g);

    final R2InstanceSingleMeshType i0a0 =
      R2TestUtilities.getInstance(g, a0, 0L);
    final R2InstanceSingleMeshType i1a0 =
      R2TestUtilities.getInstance(g, a0, 1L);
    final R2InstanceSingleMeshType i2a0 =
      R2TestUtilities.getInstance(g, a0, 2L);

    final R2InstanceSingleMeshType i3a1 =
      R2TestUtilities.getInstance(g, a1, 3L);
    final R2InstanceSingleMeshType i4a1 =
      R2TestUtilities.getInstance(g, a1, 4L);
    final R2InstanceSingleMeshType i5a1 =
      R2TestUtilities.getInstance(g, a1, 5L);

    final R2InstanceSingleMeshType i6a0 =
      R2TestUtilities.getInstance(g, a0, 6L);
    final R2InstanceSingleMeshType i7a0 =
      R2TestUtilities.getInstance(g, a0, 7L);
    final R2InstanceSingleMeshType i8a0 =
      R2TestUtilities.getInstance(g, a0, 8L);

    final R2InstanceSingleMeshType i9a1 =
      R2TestUtilities.getInstance(g, a1, 9L);
    final R2InstanceSingleMeshType i10a1 =
      R2TestUtilities.getInstance(g, a1, 10L);
    final R2InstanceSingleMeshType i11a1 =
      R2TestUtilities.getInstance(g, a1, 11L);

    final R2InstanceSingleMeshType i12a0 =
      R2TestUtilities.getInstance(g, a0, 12L);
    final R2InstanceSingleMeshType i13a0 =
      R2TestUtilities.getInstance(g, a0, 13L);
    final R2InstanceSingleMeshType i14a0 =
      R2TestUtilities.getInstance(g, a0, 14L);

    final R2InstanceSingleMeshType i15a1 =
      R2TestUtilities.getInstance(g, a1, 15L);
    final R2InstanceSingleMeshType i16a1 =
      R2TestUtilities.getInstance(g, a1, 16L);
    final R2InstanceSingleMeshType i17a1 =
      R2TestUtilities.getInstance(g, a1, 17L);

    final R2InstanceSingleMeshType i18a0 =
      R2TestUtilities.getInstance(g, a0, 18L);
    final R2InstanceSingleMeshType i19a0 =
      R2TestUtilities.getInstance(g, a0, 19L);
    final R2InstanceSingleMeshType i20a0 =
      R2TestUtilities.getInstance(g, a0, 20L);

    final R2InstanceSingleMeshType i21a1 =
      R2TestUtilities.getInstance(g, a1, 21L);
    final R2InstanceSingleMeshType i22a1 =
      R2TestUtilities.getInstance(g, a1, 22L);
    final R2InstanceSingleMeshType i23a1 =
      R2TestUtilities.getInstance(g, a1, 23L);

    final R2ShaderType<Object> s0 = R2TestUtilities.getShader(g, 0L);
    final R2ShaderType<Object> s1 = R2TestUtilities.getShader(g, 1L);

    final R2MaterialOpaqueSingleMeshType<Object> m0 =
      R2TestUtilities.getMaterial(g, s0, new Object(), 0L);
    final R2MaterialOpaqueSingleMeshType<Object> m1 =
      R2TestUtilities.getMaterial(g, s0, new Object(), 1L);
    final R2MaterialOpaqueSingleMeshType<Object> m2 =
      R2TestUtilities.getMaterial(g, s1, new Object(), 2L);
    final R2MaterialOpaqueSingleMeshType<Object> m3 =
      R2TestUtilities.getMaterial(g, s1, new Object(), 3L);

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
      R2MaterialType<?> current_material;
      R2ShaderType<?> current_shader;
      int shader_starts = 0;
      int instance_calls = 0;

      @Override
      public void onStart()
      {
        started.set(true);
      }

      @Override
      public <M> void onShaderStart(final R2ShaderType<M> s)
      {
        Assert.assertTrue(this.shader_starts < 2);

        R2SceneOpaquesTest.LOG.debug("shader start {}", s);
        ops.add(new Ops.OpShaderStart(s));
        shaders_started.add(s);
        this.current_shader = s;
        this.shader_starts++;
      }

      @Override
      public <M> void onMaterialStart(
        final R2MaterialType<M> material)
      {
        final R2ShaderType<M> s = material.getShader();
        Assert.assertEquals(s, this.current_shader);

        R2SceneOpaquesTest.LOG.debug("material start {} {}", s, material);
        ops.add(new Ops.OpMaterialStart(material, s));
        materials_started.add(material);
        this.current_material = material;
      }

      @Override
      public void onInstancesStartArray(final R2InstanceType i)
      {
        R2SceneOpaquesTest.LOG.debug("instance start array {}", i);
        ops.add(new Ops.OpInstanceStartArray(i));
      }

      @Override
      public <M> void onInstance(
        final R2MaterialType<M> material,
        final R2InstanceType i)
      {
        Assert.assertEquals(this.current_material, material);
        Assert.assertTrue(this.instance_calls < 24);
        R2SceneOpaquesTest.LOG.debug(
          "instance {} {} {}",
          this.current_shader,
          this.current_material,
          i);
        ops.add(new Ops.OpInstance(
          i,
          this.current_material,
          this.current_shader));
        instances.add(i);
        this.instance_calls++;
      }

      @Override
      public <M> void onMaterialFinish(
        final R2MaterialType<M> material)
      {
        final R2ShaderType<M> s = material.getShader();
        Assert.assertEquals(this.current_material, material);
        Assert.assertEquals(this.current_shader, s);
        R2SceneOpaquesTest.LOG.debug(
          "material finish {} {}",
          this.current_shader,
          material);
        ops.add(new Ops.OpMaterialFinish(material, this.current_shader));
        this.current_material = null;
      }

      @Override
      public <M> void onShaderFinish(final R2ShaderType<M> s)
      {
        Assert.assertEquals(this.current_shader, s);
        R2SceneOpaquesTest.LOG.debug("shader finish {}", s);
        ops.add(new Ops.OpShaderFinish(s));
        this.current_shader = null;
      }

      @Override
      public void onFinish()
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

  @Test
  public <M> void testReset_0()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getGL();

    final JCGLArrayObjectType a0 = R2TestUtilities.getArrayObject(g);
    final JCGLArrayObjectType a1 = R2TestUtilities.getArrayObject(g);

    final R2InstanceSingleMeshType i0a0 =
      R2TestUtilities.getInstance(g, a0, 0L);

    final R2ShaderType<Object> s0 = R2TestUtilities.getShader(g, 0L);

    final R2MaterialOpaqueSingleMeshType<Object> m0 =
      R2TestUtilities.getMaterial(g, s0, new Object(), 0L);

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
      R2MaterialType<?> current_material;
      R2ShaderType<?> current_shader;
      int shader_starts = 0;
      int instance_calls = 0;

      @Override
      public void onStart()
      {
        started.set(true);
      }

      @Override
      public <M> void onShaderStart(final R2ShaderType<M> s)
      {
        Assert.assertTrue(this.shader_starts < 2);

        R2SceneOpaquesTest.LOG.debug("shader start {}", s);
        ops.add(new Ops.OpShaderStart(s));
        shaders_started.add(s);
        this.shader_starts++;
        this.current_shader = s;
      }

      @Override
      public <M> void onMaterialStart(
        final R2MaterialType<M> material)
      {
        final R2ShaderType<M> s = material.getShader();
        Assert.assertEquals(s, this.current_shader);
        R2SceneOpaquesTest.LOG.debug("material start {} {}", s, material);
        ops.add(new Ops.OpMaterialStart(material, s));
        materials_started.add(material);
        this.current_material = material;
      }

      @Override
      public void onInstancesStartArray(final R2InstanceType i)
      {
        R2SceneOpaquesTest.LOG.debug("instance start array {}", i);
        ops.add(new Ops.OpInstanceStartArray(i));
      }

      @Override
      public <M> void onInstance(
        final R2MaterialType<M> material,
        final R2InstanceType i)
      {
        Assert.assertEquals(this.current_material, material);
        Assert.assertTrue(this.instance_calls < 24);
        R2SceneOpaquesTest.LOG.debug(
          "instance {} {} {}",
          this.current_shader,
          this.current_material, i);
        ops.add(new Ops.OpInstance(
          i,
          this.current_material,
          this.current_shader));
        instances.add(i);
        this.instance_calls++;
      }

      @Override
      public <M> void onMaterialFinish(
        final R2MaterialType<M> material)
      {
        final R2ShaderType<M> s = material.getShader();
        Assert.assertEquals(this.current_material, material);
        Assert.assertEquals(this.current_shader, s);
        R2SceneOpaquesTest.LOG.debug(
          "material finish {} {}",
          material.getShader(),
          material);
        ops.add(new Ops.OpMaterialFinish(material, s));
        this.current_material = null;
      }

      @Override
      public <M> void onShaderFinish(final R2ShaderType<M> s)
      {
        R2SceneOpaquesTest.LOG.debug("shader finish {}", s);
        ops.add(new Ops.OpShaderFinish(s));
        this.current_shader = null;
      }

      @Override
      public void onFinish()
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
      R2MaterialType<?> current_material;
      R2ShaderType<?> current_shader;
      int shader_starts = 0;
      int instance_calls = 0;

      @Override
      public void onStart()
      {
        started.set(true);
      }

      @Override
      public <M> void onShaderStart(final R2ShaderType<M> s)
      {
        Assert.assertTrue(this.shader_starts < 2);

        R2SceneOpaquesTest.LOG.debug("shader start {}", s);
        ops.add(new Ops.OpShaderStart(s));
        shaders_started.add(s);
        this.shader_starts++;
        this.current_shader = s;
      }

      @Override
      public <M> void onMaterialStart(
        final R2MaterialType<M> material)
      {
        final R2ShaderType<M> s = material.getShader();
        Assert.assertEquals(s, this.current_shader);
        R2SceneOpaquesTest.LOG.debug("material start {} {}", s, material);
        ops.add(new Ops.OpMaterialStart(material, s));
        materials_started.add(material);
        this.current_material = material;
      }

      @Override
      public void onInstancesStartArray(final R2InstanceType i)
      {
        R2SceneOpaquesTest.LOG.debug("instance start array {}", i);
        ops.add(new Ops.OpInstanceStartArray(i));
      }

      @Override
      public <M> void onInstance(
        final R2MaterialType<M> material,
        final R2InstanceType i)
      {
        Assert.assertTrue(this.instance_calls < 24);
        Assert.assertEquals(this.current_material, material);

        R2SceneOpaquesTest.LOG.debug(
          "instance {} {} {}",
          this.current_shader,
          this.current_material, i);
        ops.add(new Ops.OpInstance(
          i,
          this.current_material,
          this.current_shader));
        instances.add(i);
        this.instance_calls++;
      }

      @Override
      public <M> void onMaterialFinish(
        final R2MaterialType<M> material)
      {
        final R2ShaderType<M> s = material.getShader();
        Assert.assertEquals(this.current_material, material);
        Assert.assertEquals(this.current_shader, s);
        R2SceneOpaquesTest.LOG.debug("material finish {} {}", s, material);
        ops.add(new Ops.OpMaterialFinish(material, s));
        this.current_material = null;
      }

      @Override
      public <M> void onShaderFinish(final R2ShaderType<M> s)
      {
        R2SceneOpaquesTest.LOG.debug("shader finish {}", s);
        ops.add(new Ops.OpShaderFinish(s));
        this.current_shader = null;
      }

      @Override
      public void onFinish()
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
    @Override
    public void onStart()
    {
      throw new UnreachableCodeException();
    }

    @Override
    public <M> void onShaderStart(final R2ShaderType<M> s)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public <M> void onMaterialStart(
      final R2MaterialType<M> material)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public void onInstancesStartArray(final R2InstanceType i)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public <M> void onInstance(
      final R2MaterialType<M> material,
      final R2InstanceType i)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public <M> void onMaterialFinish(
      final R2MaterialType<M> material)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public <M> void onShaderFinish(final R2ShaderType<M> s)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public void onFinish()
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

      @Override
      public boolean equals(final Object o)
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

      @Override
      public int hashCode()
      {
        return this.shader.hashCode();
      }

      @Override
      public <A> A matchOp(
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

      @Override
      public boolean equals(final Object o)
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

      @Override
      public int hashCode()
      {
        int result = this.shader.hashCode();
        result = 31 * result + this.material.hashCode();
        return result;
      }

      @Override
      public <A> A matchOp(
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

      @Override
      public boolean equals(final Object o)
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

      @Override
      public int hashCode()
      {
        return this.instance.hashCode();
      }

      @Override
      public <A> A matchOp(
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

      @Override
      public boolean equals(final Object o)
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

      @Override
      public int hashCode()
      {
        int result = this.shader.hashCode();
        result = 31 * result + this.material.hashCode();
        result = 31 * result + this.instance.hashCode();
        return result;
      }

      @Override
      public <A> A matchOp(
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

      @Override
      public boolean equals(final Object o)
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

      @Override
      public int hashCode()
      {
        int result = this.shader.hashCode();
        result = 31 * result + this.material.hashCode();
        return result;
      }

      @Override
      public <A> A matchOp(
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

      @Override
      public boolean equals(final Object o)
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

      @Override
      public int hashCode()
      {
        return this.shader.hashCode();
      }

      @Override
      public <A> A matchOp(
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
