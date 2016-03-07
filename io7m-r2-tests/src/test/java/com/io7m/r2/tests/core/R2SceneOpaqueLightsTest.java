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

import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.core.R2LightSingleType;
import com.io7m.r2.core.R2RendererExceptionLightAlreadyVisible;
import com.io7m.r2.core.R2SceneOpaqueLights;
import com.io7m.r2.core.R2SceneOpaqueLightsConsumerType;
import com.io7m.r2.core.R2SceneOpaqueLightsType;
import com.io7m.r2.core.shaders.types.R2ShaderLightSingleUsableType;
import com.io7m.r2.core.shaders.types.R2ShaderUsableType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class R2SceneOpaqueLightsTest
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2SceneOpaqueLightsTest.class);
  }

  @Rule public ExpectedException expected = ExpectedException.none();

  @Test
  public void testEmpty()
  {
    final AtomicBoolean finished = new AtomicBoolean(false);
    final AtomicBoolean started = new AtomicBoolean(false);
    final R2SceneOpaqueLightsType o = R2SceneOpaqueLights.newLights();

    o.opaqueLightsExecute(new UnreachableConsumer()
    {
      @Override
      public void onStart()
      {
        started.set(true);
      }

      @Override
      public void onFinish()
      {
        finished.set(true);
      }
    });

    Assert.assertTrue(started.get());
    Assert.assertTrue(finished.get());
  }

  @Test
  public void testSingleAlreadyVisible()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();
    final R2SceneOpaqueLightsType o = R2SceneOpaqueLights.newLights();
    final JCGLArrayObjectType a0 = R2TestUtilities.getArrayObject(g);
    final R2ShaderLightSingleUsableType<R2LightSingleType> s =
      R2TestUtilities.getShaderLightSingle(g, 0L);

    final R2LightSingleType light = R2TestUtilities.getLightSingle(a0, 2L);

    o.opaqueLightsAddSingle(light, s);
    this.expected.expect(R2RendererExceptionLightAlreadyVisible.class);
    o.opaqueLightsAddSingle(light, s);
  }

  @Test
  public void testSingleOrdering()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();
    final R2SceneOpaqueLightsType o = R2SceneOpaqueLights.newLights();

    final JCGLArrayObjectType a0 = R2TestUtilities.getArrayObject(g);
    final JCGLArrayObjectType a1 = R2TestUtilities.getArrayObject(g);
    final JCGLArrayObjectType a2 = R2TestUtilities.getArrayObject(g);

    final R2ShaderLightSingleUsableType<R2LightSingleType> s0 =
      R2TestUtilities.getShaderLightSingle(g, 0L);
    final R2ShaderLightSingleUsableType<R2LightSingleType> s1 =
      R2TestUtilities.getShaderLightSingle(g, 1L);

    final R2LightSingleType l0a0 =
      R2TestUtilities.getLightSingle(a0, 0L);
    final R2LightSingleType l1a0 =
      R2TestUtilities.getLightSingle(a0, 1L);

    final R2LightSingleType l2a1 =
      R2TestUtilities.getLightSingle(a1, 2L);
    final R2LightSingleType l3a1 =
      R2TestUtilities.getLightSingle(a1, 3L);

    final R2LightSingleType l4a2 =
      R2TestUtilities.getLightSingle(a2, 4L);
    final R2LightSingleType l5a2 =
      R2TestUtilities.getLightSingle(a2, 5L);

    final R2LightSingleType l6a0 =
      R2TestUtilities.getLightSingle(a0, 6L);
    final R2LightSingleType l7a0 =
      R2TestUtilities.getLightSingle(a0, 7L);

    final R2LightSingleType l8a1 =
      R2TestUtilities.getLightSingle(a1, 8L);
    final R2LightSingleType l9a1 =
      R2TestUtilities.getLightSingle(a1, 9L);

    final R2LightSingleType l10a2 =
      R2TestUtilities.getLightSingle(a2, 10L);
    final R2LightSingleType l11a2 =
      R2TestUtilities.getLightSingle(a2, 11L);

    o.opaqueLightsAddSingle(l0a0, s0);
    o.opaqueLightsAddSingle(l1a0, s0);
    o.opaqueLightsAddSingle(l2a1, s0);
    o.opaqueLightsAddSingle(l3a1, s0);
    o.opaqueLightsAddSingle(l4a2, s0);
    o.opaqueLightsAddSingle(l5a2, s0);

    o.opaqueLightsAddSingle(l6a0, s1);
    o.opaqueLightsAddSingle(l7a0, s1);
    o.opaqueLightsAddSingle(l8a1, s1);
    o.opaqueLightsAddSingle(l9a1, s1);
    o.opaqueLightsAddSingle(l10a2, s1);
    o.opaqueLightsAddSingle(l11a2, s1);

    Assert.assertEquals(12L, o.opaqueLightsCount());

    final LoggingConsumer cc = new LoggingConsumer();
    final List<String> op = cc.ops;
    o.opaqueLightsExecute(cc);

    Assert.assertEquals("onStart", op.remove(0));
    Assert.assertEquals("onStartGroup 1", op.remove(0));
    Assert.assertEquals("onLightSingleShaderStart 0", op.remove(0));
    Assert.assertEquals("onLightSingleArrayStart 4", op.remove(0));
    Assert.assertEquals("onLightSingle 0", op.remove(0));
    Assert.assertEquals("onLightSingle 1", op.remove(0));
    Assert.assertEquals("onLightSingleArrayStart 7", op.remove(0));
    Assert.assertEquals("onLightSingle 3", op.remove(0));
    Assert.assertEquals("onLightSingle 2", op.remove(0));
    Assert.assertEquals("onLightSingleArrayStart 10", op.remove(0));
    Assert.assertEquals("onLightSingle 5", op.remove(0));
    Assert.assertEquals("onLightSingle 4", op.remove(0));
    Assert.assertEquals("onLightSingleShaderFinish 0", op.remove(0));
    Assert.assertEquals("onLightSingleShaderStart 1", op.remove(0));
    Assert.assertEquals("onLightSingleArrayStart 4", op.remove(0));
    Assert.assertEquals("onLightSingle 6", op.remove(0));
    Assert.assertEquals("onLightSingle 7", op.remove(0));
    Assert.assertEquals("onLightSingleArrayStart 7", op.remove(0));
    Assert.assertEquals("onLightSingle 9", op.remove(0));
    Assert.assertEquals("onLightSingle 8", op.remove(0));
    Assert.assertEquals("onLightSingleArrayStart 10", op.remove(0));
    Assert.assertEquals("onLightSingle 11", op.remove(0));
    Assert.assertEquals("onLightSingle 10", op.remove(0));
    Assert.assertEquals("onLightSingleShaderFinish 1", op.remove(0));
    Assert.assertEquals("onFinishGroup 1", op.remove(0));
    Assert.assertEquals("onFinish", op.remove(0));
    Assert.assertTrue(op.isEmpty());

    o.opaqueLightsReset();

    Assert.assertEquals(0L, o.opaqueLightsCount());
    o.opaqueLightsExecute(cc);

    Assert.assertEquals("onStart", op.remove(0));
    Assert.assertEquals("onFinish", op.remove(0));
    Assert.assertTrue(op.isEmpty());
  }

  @Test
  public void testSingleGroups()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();
    final R2SceneOpaqueLightsType o = R2SceneOpaqueLights.newLights();

    final JCGLArrayObjectType a0 = R2TestUtilities.getArrayObject(g);

    final R2ShaderLightSingleUsableType<R2LightSingleType> s0 =
      R2TestUtilities.getShaderLightSingle(g, 0L);

    final R2LightSingleType l0a0 =
      R2TestUtilities.getLightSingle(a0, 0L);
    final R2LightSingleType l1a0 =
      R2TestUtilities.getLightSingle(a0, 1L);

    o.opaqueLightsAddSingleWithGroup(l0a0, s0, 1);
    o.opaqueLightsAddSingleWithGroup(l1a0, s0, 3);

    Assert.assertEquals(2L, o.opaqueLightsCount());

    final LoggingConsumer cc = new LoggingConsumer();
    final List<String> op = cc.ops;
    o.opaqueLightsExecute(cc);

    Assert.assertEquals("onStart", op.remove(0));
    Assert.assertEquals("onStartGroup 1", op.remove(0));
    Assert.assertEquals("onLightSingleShaderStart 0", op.remove(0));
    Assert.assertEquals("onLightSingleArrayStart 4", op.remove(0));
    Assert.assertEquals("onLightSingle 0", op.remove(0));
    Assert.assertEquals("onLightSingleShaderFinish 0", op.remove(0));
    Assert.assertEquals("onFinishGroup 1", op.remove(0));
    Assert.assertEquals("onStartGroup 3", op.remove(0));
    Assert.assertEquals("onLightSingleShaderStart 0", op.remove(0));
    Assert.assertEquals("onLightSingleArrayStart 4", op.remove(0));
    Assert.assertEquals("onLightSingle 1", op.remove(0));
    Assert.assertEquals("onLightSingleShaderFinish 0", op.remove(0));
    Assert.assertEquals("onFinishGroup 3", op.remove(0));
    Assert.assertEquals("onFinish", op.remove(0));
    Assert.assertTrue(op.isEmpty());

    o.opaqueLightsReset();

    Assert.assertEquals(0L, o.opaqueLightsCount());
    o.opaqueLightsExecute(cc);

    Assert.assertEquals("onStart", op.remove(0));
    Assert.assertEquals("onFinish", op.remove(0));
    Assert.assertTrue(op.isEmpty());
  }

  private static abstract class UnreachableConsumer implements
    R2SceneOpaqueLightsConsumerType
  {
    @Override
    public void onStart()
    {
      throw new UnreachableCodeException();
    }

    @Override
    public void onFinish()
    {
      throw new UnreachableCodeException();
    }

    @Override
    public void onStartGroup(final int group)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public <M extends R2LightSingleType> void onLightSingleShaderStart(
      final R2ShaderLightSingleUsableType<M> s)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public void onLightSingleArrayStart(final R2LightSingleType i)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public <M extends R2LightSingleType> void onLightSingle(
      final R2ShaderLightSingleUsableType<M> s,
      final M i)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public <M extends R2LightSingleType> void onLightSingleShaderFinish(
      final R2ShaderLightSingleUsableType<M> s)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public void onFinishGroup(final int group)
    {
      throw new UnreachableCodeException();
    }
  }

  private static final class LoggingConsumer implements
    R2SceneOpaqueLightsConsumerType
  {
    private final List<String>              ops;
    private       R2ShaderUsableType<?>     shader_current;
    private       JCGLArrayObjectUsableType array_current;
    private       int                       group;

    LoggingConsumer()
    {
      this.ops = new ArrayList<>(256);
      this.group = -1;
    }

    @Override
    public void onStart()
    {
      this.ops.add("onStart");
    }

    @Override
    public void onStartGroup(final int g)
    {
      this.group = g;
      this.ops.add(String.format(
        "onStartGroup %d", Integer.valueOf(g)));
    }

    @Override
    public <M extends R2LightSingleType> void onLightSingleShaderStart(
      final R2ShaderLightSingleUsableType<M> s)
    {
      this.shader_current = s;
      this.ops.add(String.format(
        "onLightSingleShaderStart %d",
        Long.valueOf(s.getShaderID())));
    }

    @Override
    public void onLightSingleArrayStart(final R2LightSingleType i)
    {
      this.array_current = i.getArrayObject();
      this.ops.add(String.format(
        "onLightSingleArrayStart %d",
        Integer.valueOf(this.array_current.getGLName())));
    }

    @Override
    public <M extends R2LightSingleType> void onLightSingle(
      final R2ShaderLightSingleUsableType<M> s,
      final M i)
    {
      Assert.assertEquals(s, this.shader_current);
      Assert.assertEquals(i.getArrayObject(), this.array_current);
      this.ops.add(String.format(
        "onLightSingle %d",
        Long.valueOf(i.getLightID())));
    }

    @Override
    public <M extends R2LightSingleType> void onLightSingleShaderFinish(
      final R2ShaderLightSingleUsableType<M> s)
    {
      Assert.assertEquals(s, this.shader_current);
      this.shader_current = null;
      this.ops.add(String.format(
        "onLightSingleShaderFinish %d",
        Long.valueOf(s.getShaderID())));
    }

    @Override
    public void onFinishGroup(final int g)
    {
      Assert.assertEquals((long) g, (long) this.group);
      this.group = -1;
      this.ops.add(String.format(
        "onFinishGroup %d", Integer.valueOf(g)));
    }

    @Override
    public void onFinish()
    {
      this.ops.add("onFinish");
      this.shader_current = null;
      this.array_current = null;
    }
  }
}
