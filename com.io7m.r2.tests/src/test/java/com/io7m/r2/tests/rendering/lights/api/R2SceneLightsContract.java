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

package com.io7m.r2.tests.rendering.lights.api;

import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.instances.R2InstanceSingleType;
import com.io7m.r2.lights.R2LightSingleReadableType;
import com.io7m.r2.lights.R2LightSingleType;
import com.io7m.r2.rendering.lights.api.R2ExceptionLightAlreadyVisible;
import com.io7m.r2.rendering.lights.api.R2ExceptionLightClipGroupDeleted;
import com.io7m.r2.rendering.lights.api.R2SceneLightsClipGroupConsumerType;
import com.io7m.r2.rendering.lights.api.R2SceneLightsClipGroupType;
import com.io7m.r2.rendering.lights.api.R2SceneLightsConsumerType;
import com.io7m.r2.rendering.lights.api.R2SceneLightsGroupConsumerType;
import com.io7m.r2.rendering.lights.api.R2SceneLightsGroupType;
import com.io7m.r2.rendering.lights.api.R2SceneLightsType;
import com.io7m.r2.rendering.stencil.api.R2ExceptionStencilInvalidGroup;
import com.io7m.r2.shaders.light.api.R2ShaderLightSingleUsableType;
import com.io7m.r2.tests.core.R2TestUtilities;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class R2SceneLightsContract
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2SceneLightsContract.class);
  }

  @Rule public ExpectedException expected = ExpectedException.none();

  private static void dumpLog(final List<String> log)
  {
    for (int index = 0; index < log.size(); ++index) {
      LOG.debug("[{}]: {}", Integer.valueOf(index), log.get(index));
    }
  }

  protected abstract R2SceneLightsType newLights();

  @Test
  public final void testEmpty()
  {
    final AtomicBoolean finished = new AtomicBoolean(false);
    final AtomicBoolean started = new AtomicBoolean(false);
    final R2SceneLightsType o = this.newLights();

    o.lightsExecute(new UnreachableConsumer()
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
  public final void testGroupInvalid()
  {
    final R2SceneLightsType o = this.newLights();

    this.expected.expect(R2ExceptionStencilInvalidGroup.class);
    o.lightsGetGroup(0);
  }

  @Test
  public final void testGroupEmptyExec()
  {
    final AtomicBoolean finished = new AtomicBoolean(false);
    final AtomicBoolean started = new AtomicBoolean(false);
    final R2SceneLightsType o = this.newLights();
    final R2SceneLightsGroupType g = o.lightsGetGroup(1);

    Assert.assertEquals(0L, o.lightsCount());

    o.lightsExecute(new UnreachableConsumer()
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
  public final void testGroupSingleExec()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();

    final R2SceneLightsType o = this.newLights();
    final R2SceneLightsGroupType lg = o.lightsGetGroup(1);

    Assert.assertEquals(0L, o.lightsCount());

    final JCGLArrayObjectType a0 =
      R2TestUtilities.getArrayObject(g);

    final R2ShaderLightSingleUsableType<R2LightSingleType> s0 =
      R2TestUtilities.getShaderLightSingle(g, 0L);

    final R2LightSingleType l0 =
      R2TestUtilities.getLightSingle(a0, 1L);

    lg.lightGroupAddSingle(l0, s0);

    Assert.assertEquals(1L, o.lightsCount());

    final List<String> log = new ArrayList<>(128);
    o.lightsExecute(new LoggingConsumer(
      log, LoggingGroupConsumer::new, LoggingClipGroupConsumer::new));

    dumpLog(log);

    Assert.assertEquals("onStart", log.remove(0));
    Assert.assertEquals("onStartGroup 1", log.remove(0));
    Assert.assertEquals("Group.onStart 1", log.remove(0));
    Assert.assertEquals("Group.onLightSingleShaderStart 0", log.remove(0));
    Assert.assertEquals("Group.onLightSingleArrayStart 1", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 0 1", log.remove(0));
    Assert.assertEquals("Group.onLightSingleShaderFinish 0", log.remove(0));
    Assert.assertEquals("Group.onFinish 1", log.remove(0));
    Assert.assertEquals("onFinish", log.remove(0));
    Assert.assertEquals(0L, (long) log.size());
  }

  @Test
  public final void testGroupSingleClearExec()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();

    final R2SceneLightsType o = this.newLights();
    final R2SceneLightsGroupType lg = o.lightsGetGroup(1);

    Assert.assertEquals(0L, o.lightsCount());

    final JCGLArrayObjectType a0 =
      R2TestUtilities.getArrayObject(g);

    final R2ShaderLightSingleUsableType<R2LightSingleType> s0 =
      R2TestUtilities.getShaderLightSingle(g, 0L);

    final R2LightSingleType l0 =
      R2TestUtilities.getLightSingle(a0, 1L);

    lg.lightGroupAddSingle(l0, s0);

    Assert.assertEquals(1L, o.lightsCount());

    final List<String> log = new ArrayList<>(128);
    o.lightsExecute(new LoggingConsumer(
      log, LoggingGroupConsumer::new, LoggingClipGroupConsumer::new));
    dumpLog(log);

    Assert.assertEquals("onStart", log.remove(0));
    Assert.assertEquals("onStartGroup 1", log.remove(0));
    Assert.assertEquals("Group.onStart 1", log.remove(0));
    Assert.assertEquals("Group.onLightSingleShaderStart 0", log.remove(0));
    Assert.assertEquals("Group.onLightSingleArrayStart 1", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 0 1", log.remove(0));
    Assert.assertEquals("Group.onLightSingleShaderFinish 0", log.remove(0));
    Assert.assertEquals("Group.onFinish 1", log.remove(0));
    Assert.assertEquals("onFinish", log.remove(0));
    Assert.assertEquals(0L, (long) log.size());

    o.lightsReset();

    o.lightsExecute(new LoggingConsumer(
      log, LoggingGroupConsumer::new, LoggingClipGroupConsumer::new));
    dumpLog(log);

    Assert.assertEquals("onStart", log.remove(0));
    Assert.assertEquals("onFinish", log.remove(0));
    Assert.assertEquals(0L, (long) log.size());
  }

  @Test
  public final void testGroupSingleExecArrayOrder()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();

    final R2SceneLightsType o = this.newLights();
    final R2SceneLightsGroupType lg = o.lightsGetGroup(1);

    Assert.assertEquals(0L, o.lightsCount());

    final JCGLArrayObjectType a0 =
      R2TestUtilities.getArrayObject(g);
    final JCGLArrayObjectType a1 =
      R2TestUtilities.getArrayObject(g);
    final JCGLArrayObjectType a2 =
      R2TestUtilities.getArrayObject(g);

    final R2ShaderLightSingleUsableType<R2LightSingleType> s0 =
      R2TestUtilities.getShaderLightSingle(g, 0L);

    final R2LightSingleType l0 =
      R2TestUtilities.getLightSingle(a0, 1L);
    final R2LightSingleType l1 =
      R2TestUtilities.getLightSingle(a1, 2L);
    final R2LightSingleType l2 =
      R2TestUtilities.getLightSingle(a2, 3L);

    lg.lightGroupAddSingle(l0, s0);
    lg.lightGroupAddSingle(l1, s0);
    lg.lightGroupAddSingle(l2, s0);

    Assert.assertEquals(3L, o.lightsCount());

    final List<String> log = new ArrayList<>(128);
    o.lightsExecute(new LoggingConsumer(
      log, LoggingGroupConsumer::new, LoggingClipGroupConsumer::new));

    dumpLog(log);

    Assert.assertEquals("onStart", log.remove(0));
    Assert.assertEquals("onStartGroup 1", log.remove(0));
    Assert.assertEquals("Group.onStart 1", log.remove(0));
    Assert.assertEquals("Group.onLightSingleShaderStart 0", log.remove(0));
    Assert.assertEquals("Group.onLightSingleArrayStart 1", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 0 1", log.remove(0));
    Assert.assertEquals("Group.onLightSingleArrayStart 2", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 0 2", log.remove(0));
    Assert.assertEquals("Group.onLightSingleArrayStart 3", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 0 3", log.remove(0));
    Assert.assertEquals("Group.onLightSingleShaderFinish 0", log.remove(0));
    Assert.assertEquals("Group.onFinish 1", log.remove(0));
    Assert.assertEquals("onFinish", log.remove(0));
    Assert.assertEquals(0L, (long) log.size());
  }

  @Test
  public final void testGroupMultiExecArrayOrder()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();

    final R2SceneLightsType o = this.newLights();
    final R2SceneLightsGroupType lg = o.lightsGetGroup(1);

    Assert.assertEquals(0L, o.lightsCount());

    final JCGLArrayObjectType a0 =
      R2TestUtilities.getArrayObject(g);
    final JCGLArrayObjectType a1 =
      R2TestUtilities.getArrayObject(g);

    final R2ShaderLightSingleUsableType<R2LightSingleType> s40 =
      R2TestUtilities.getShaderLightSingle(g, 40L);

    final R2ShaderLightSingleUsableType<R2LightSingleType> s41 =
      R2TestUtilities.getShaderLightSingle(g, 41L);

    final R2LightSingleType la0_20 =
      R2TestUtilities.getLightSingle(a0, 20L);
    final R2LightSingleType la0_21 =
      R2TestUtilities.getLightSingle(a0, 21L);
    final R2LightSingleType la0_22 =
      R2TestUtilities.getLightSingle(a0, 22L);
    final R2LightSingleType la1_23 =
      R2TestUtilities.getLightSingle(a1, 23L);
    final R2LightSingleType la1_24 =
      R2TestUtilities.getLightSingle(a1, 24L);
    final R2LightSingleType la1_25 =
      R2TestUtilities.getLightSingle(a1, 25L);
    final R2LightSingleType la0_26 =
      R2TestUtilities.getLightSingle(a0, 26L);
    final R2LightSingleType la0_27 =
      R2TestUtilities.getLightSingle(a0, 27L);
    final R2LightSingleType la0_28 =
      R2TestUtilities.getLightSingle(a0, 28L);
    final R2LightSingleType la1_29 =
      R2TestUtilities.getLightSingle(a1, 29L);
    final R2LightSingleType la1_30 =
      R2TestUtilities.getLightSingle(a1, 30L);
    final R2LightSingleType la1_31 =
      R2TestUtilities.getLightSingle(a1, 31L);

    lg.lightGroupAddSingle(la0_20, s40);
    lg.lightGroupAddSingle(la0_21, s40);
    lg.lightGroupAddSingle(la0_22, s40);
    lg.lightGroupAddSingle(la1_23, s40);
    lg.lightGroupAddSingle(la1_24, s40);
    lg.lightGroupAddSingle(la1_25, s40);

    lg.lightGroupAddSingle(la0_26, s41);
    lg.lightGroupAddSingle(la0_27, s41);
    lg.lightGroupAddSingle(la0_28, s41);
    lg.lightGroupAddSingle(la1_29, s41);
    lg.lightGroupAddSingle(la1_30, s41);
    lg.lightGroupAddSingle(la1_31, s41);

    Assert.assertEquals(12L, o.lightsCount());

    final List<String> log = new ArrayList<>(128);
    o.lightsExecute(new LoggingConsumer(
      log, LoggingGroupConsumer::new, LoggingClipGroupConsumer::new));

    dumpLog(log);

    Assert.assertEquals("onStart", log.remove(0));
    Assert.assertEquals("onStartGroup 1", log.remove(0));
    Assert.assertEquals("Group.onStart 1", log.remove(0));
    Assert.assertEquals("Group.onLightSingleShaderStart 40", log.remove(0));
    Assert.assertEquals("Group.onLightSingleArrayStart 20", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 40 20", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 40 21", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 40 22", log.remove(0));
    Assert.assertEquals("Group.onLightSingleArrayStart 23", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 40 23", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 40 24", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 40 25", log.remove(0));
    Assert.assertEquals("Group.onLightSingleShaderFinish 40", log.remove(0));
    Assert.assertEquals("Group.onLightSingleShaderStart 41", log.remove(0));
    Assert.assertEquals("Group.onLightSingleArrayStart 26", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 41 26", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 41 27", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 41 28", log.remove(0));
    Assert.assertEquals("Group.onLightSingleArrayStart 29", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 41 29", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 41 30", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 41 31", log.remove(0));
    Assert.assertEquals("Group.onLightSingleShaderFinish 41", log.remove(0));
    Assert.assertEquals("Group.onFinish 1", log.remove(0));
    Assert.assertEquals("onFinish", log.remove(0));
    Assert.assertEquals(0L, (long) log.size());

    o.lightsReset();

    final AtomicBoolean finished = new AtomicBoolean(false);
    final AtomicBoolean started = new AtomicBoolean(false);
    o.lightsExecute(new UnreachableConsumer()
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
  public final void testGroupDiscontinuousExec()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();

    final R2SceneLightsType o = this.newLights();
    final R2SceneLightsGroupType lg = o.lightsGetGroup(3);

    final JCGLArrayObjectType a0 =
      R2TestUtilities.getArrayObject(g);
    final R2ShaderLightSingleUsableType<R2LightSingleType> s0 =
      R2TestUtilities.getShaderLightSingle(g, 0L);
    final R2LightSingleType l0 =
      R2TestUtilities.getLightSingle(a0, 1L);

    lg.lightGroupAddSingle(l0, s0);

    final List<String> log = new ArrayList<>(128);
    o.lightsExecute(new LoggingConsumer(
      log, LoggingGroupConsumer::new, LoggingClipGroupConsumer::new));

    dumpLog(log);

    Assert.assertEquals("onStart", log.remove(0));
    Assert.assertEquals("onStartGroup 3", log.remove(0));
    Assert.assertEquals("Group.onStart 3", log.remove(0));
    Assert.assertEquals("Group.onLightSingleShaderStart 0", log.remove(0));
    Assert.assertEquals("Group.onLightSingleArrayStart 1", log.remove(0));
    Assert.assertEquals("Group.onLightSingle 0 1", log.remove(0));
    Assert.assertEquals("Group.onLightSingleShaderFinish 0", log.remove(0));
    Assert.assertEquals("Group.onFinish 3", log.remove(0));
    Assert.assertEquals("onFinish", log.remove(0));
    Assert.assertEquals(0L, (long) log.size());
  }

  @Test
  public final void testGroupLightAddTwice()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();

    final R2SceneLightsType o = this.newLights();
    final R2SceneLightsGroupType lg = o.lightsGetGroup(1);

    final JCGLArrayObjectType a0 =
      R2TestUtilities.getArrayObject(g);

    final R2ShaderLightSingleUsableType<R2LightSingleType> s0 =
      R2TestUtilities.getShaderLightSingle(g, 0L);

    final R2LightSingleType l0 =
      R2TestUtilities.getLightSingle(a0, 1L);

    lg.lightGroupAddSingle(l0, s0);

    this.expected.expect(R2ExceptionLightAlreadyVisible.class);
    lg.lightGroupAddSingle(l0, s0);
  }

  @Test
  public final void testGroupLightAddTwiceViaClipGroup()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();

    final R2SceneLightsType o = this.newLights();
    final R2SceneLightsGroupType lg = o.lightsGetGroup(1);

    final JCGLArrayObjectType a0 =
      R2TestUtilities.getArrayObject(g);

    final R2InstanceSingleType i0 =
      R2TestUtilities.getInstanceSingle(g, a0, 0L);

    final R2ShaderLightSingleUsableType<R2LightSingleType> s0 =
      R2TestUtilities.getShaderLightSingle(g, 1L);

    final R2LightSingleType l0 =
      R2TestUtilities.getLightSingle(a0, 2L);

    lg.lightGroupAddSingle(l0, s0);

    final R2SceneLightsClipGroupType cg =
      lg.lightGroupNewClipGroup(i0);

    this.expected.expect(R2ExceptionLightAlreadyVisible.class);
    cg.clipGroupAddSingle(l0, s0);
  }

  @Test
  public final void testGroupLightAddClipGroupTwiceViaGroup()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();

    final R2SceneLightsType o = this.newLights();
    final R2SceneLightsGroupType lg = o.lightsGetGroup(1);

    final JCGLArrayObjectType a0 =
      R2TestUtilities.getArrayObject(g);

    final R2InstanceSingleType i0 =
      R2TestUtilities.getInstanceSingle(g, a0, 0L);

    final R2ShaderLightSingleUsableType<R2LightSingleType> s0 =
      R2TestUtilities.getShaderLightSingle(g, 1L);

    final R2LightSingleType l0 =
      R2TestUtilities.getLightSingle(a0, 2L);

    final R2SceneLightsClipGroupType cg =
      lg.lightGroupNewClipGroup(i0);

    cg.clipGroupAddSingle(l0, s0);

    this.expected.expect(R2ExceptionLightAlreadyVisible.class);
    lg.lightGroupAddSingle(l0, s0);
  }

  @Test
  public final void testGroupLightAddClipGroupTwiceViaClipGroup()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();

    final R2SceneLightsType o = this.newLights();
    final R2SceneLightsGroupType lg = o.lightsGetGroup(1);

    final JCGLArrayObjectType a0 =
      R2TestUtilities.getArrayObject(g);

    final R2InstanceSingleType i0 =
      R2TestUtilities.getInstanceSingle(g, a0, 0L);

    final R2ShaderLightSingleUsableType<R2LightSingleType> s0 =
      R2TestUtilities.getShaderLightSingle(g, 1L);

    final R2LightSingleType l0 =
      R2TestUtilities.getLightSingle(a0, 2L);

    final R2SceneLightsClipGroupType cg =
      lg.lightGroupNewClipGroup(i0);

    cg.clipGroupAddSingle(l0, s0);

    this.expected.expect(R2ExceptionLightAlreadyVisible.class);
    cg.clipGroupAddSingle(l0, s0);
  }

  @Test
  public final void testGroupLightAddClipGroupTwiceViaDifferentClipGroup()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();

    final R2SceneLightsType o = this.newLights();
    final R2SceneLightsGroupType lg = o.lightsGetGroup(1);

    final JCGLArrayObjectType a0 =
      R2TestUtilities.getArrayObject(g);

    final R2InstanceSingleType i0 =
      R2TestUtilities.getInstanceSingle(g, a0, 0L);
    final R2InstanceSingleType i1 =
      R2TestUtilities.getInstanceSingle(g, a0, 1L);

    final R2ShaderLightSingleUsableType<R2LightSingleType> s0 =
      R2TestUtilities.getShaderLightSingle(g, 2L);

    final R2LightSingleType l0 =
      R2TestUtilities.getLightSingle(a0, 3L);

    final R2SceneLightsClipGroupType cg0 =
      lg.lightGroupNewClipGroup(i0);
    final R2SceneLightsClipGroupType cg1 =
      lg.lightGroupNewClipGroup(i1);

    cg0.clipGroupAddSingle(l0, s0);

    this.expected.expect(R2ExceptionLightAlreadyVisible.class);
    cg1.clipGroupAddSingle(l0, s0);
  }

  @Test
  public final void testClipGroupMultiExecArrayOrder()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();

    final JCGLArrayObjectType ia0 =
      R2TestUtilities.getArrayObject(g);
    final R2InstanceSingleType i =
      R2TestUtilities.getInstanceSingle(g, ia0, 100L);

    final R2SceneLightsType o =
      this.newLights();
    final R2SceneLightsGroupType lg =
      o.lightsGetGroup(1);
    final R2SceneLightsClipGroupType lcg =
      lg.lightGroupNewClipGroup(i);

    Assert.assertEquals(0L, o.lightsCount());

    final JCGLArrayObjectType a0 =
      R2TestUtilities.getArrayObject(g);
    final JCGLArrayObjectType a1 =
      R2TestUtilities.getArrayObject(g);

    final R2ShaderLightSingleUsableType<R2LightSingleType> s40 =
      R2TestUtilities.getShaderLightSingle(g, 40L);

    final R2ShaderLightSingleUsableType<R2LightSingleType> s41 =
      R2TestUtilities.getShaderLightSingle(g, 41L);

    final R2LightSingleType la0_20 =
      R2TestUtilities.getLightSingle(a0, 20L);
    final R2LightSingleType la0_21 =
      R2TestUtilities.getLightSingle(a0, 21L);
    final R2LightSingleType la0_22 =
      R2TestUtilities.getLightSingle(a0, 22L);
    final R2LightSingleType la1_23 =
      R2TestUtilities.getLightSingle(a1, 23L);
    final R2LightSingleType la1_24 =
      R2TestUtilities.getLightSingle(a1, 24L);
    final R2LightSingleType la1_25 =
      R2TestUtilities.getLightSingle(a1, 25L);
    final R2LightSingleType la0_26 =
      R2TestUtilities.getLightSingle(a0, 26L);
    final R2LightSingleType la0_27 =
      R2TestUtilities.getLightSingle(a0, 27L);
    final R2LightSingleType la0_28 =
      R2TestUtilities.getLightSingle(a0, 28L);
    final R2LightSingleType la1_29 =
      R2TestUtilities.getLightSingle(a1, 29L);
    final R2LightSingleType la1_30 =
      R2TestUtilities.getLightSingle(a1, 30L);
    final R2LightSingleType la1_31 =
      R2TestUtilities.getLightSingle(a1, 31L);

    lcg.clipGroupAddSingle(la0_20, s40);
    lcg.clipGroupAddSingle(la0_21, s40);
    lcg.clipGroupAddSingle(la0_22, s40);
    lcg.clipGroupAddSingle(la1_23, s40);
    lcg.clipGroupAddSingle(la1_24, s40);
    lcg.clipGroupAddSingle(la1_25, s40);

    lcg.clipGroupAddSingle(la0_26, s41);
    lcg.clipGroupAddSingle(la0_27, s41);
    lcg.clipGroupAddSingle(la0_28, s41);
    lcg.clipGroupAddSingle(la1_29, s41);
    lcg.clipGroupAddSingle(la1_30, s41);
    lcg.clipGroupAddSingle(la1_31, s41);

    Assert.assertEquals(12L, o.lightsCount());

    final List<String> log = new ArrayList<>(128);
    o.lightsExecute(new LoggingConsumer(
      log, LoggingGroupConsumer::new, LoggingClipGroupConsumer::new));
    dumpLog(log);

    Assert.assertEquals("onStart", log.remove(0));
    Assert.assertEquals("onStartClipGroup 100 1", log.remove(0));
    Assert.assertEquals("ClipGroup.onStart 1", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingleShaderStart 40", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingleArrayStart 20", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingle 40 20", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingle 40 21", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingle 40 22", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingleArrayStart 23", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingle 40 23", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingle 40 24", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingle 40 25", log.remove(0));
    Assert.assertEquals(
      "ClipGroup.onLightSingleShaderFinish 40",
      log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingleShaderStart 41", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingleArrayStart 26", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingle 41 26", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingle 41 27", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingle 41 28", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingleArrayStart 29", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingle 41 29", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingle 41 30", log.remove(0));
    Assert.assertEquals("ClipGroup.onLightSingle 41 31", log.remove(0));
    Assert.assertEquals(
      "ClipGroup.onLightSingleShaderFinish 41",
      log.remove(0));
    Assert.assertEquals("ClipGroup.onFinish 1", log.remove(0));
    Assert.assertEquals("onFinish", log.remove(0));
    Assert.assertEquals(0L, (long) log.size());

    o.lightsReset();

    final AtomicBoolean finished = new AtomicBoolean(false);
    final AtomicBoolean started = new AtomicBoolean(false);
    o.lightsExecute(new UnreachableConsumer()
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
  public final void testClipGroupEmptyExec()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();

    final AtomicBoolean finished = new AtomicBoolean(false);
    final AtomicBoolean started = new AtomicBoolean(false);

    final JCGLArrayObjectType ia0 =
      R2TestUtilities.getArrayObject(g);
    final R2InstanceSingleType i =
      R2TestUtilities.getInstanceSingle(g, ia0, 100L);

    final R2SceneLightsType o = this.newLights();
    final R2SceneLightsGroupType lg = o.lightsGetGroup(1);
    final R2SceneLightsClipGroupType cg = lg.lightGroupNewClipGroup(i);

    Assert.assertEquals(0L, o.lightsCount());

    o.lightsExecute(new UnreachableConsumer()
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
  public final void testClipGroupDeleted()
    throws Exception
  {
    final JCGLInterfaceGL33Type g = R2TestUtilities.getFakeGL();

    final JCGLArrayObjectType ia0 =
      R2TestUtilities.getArrayObject(g);
    final R2InstanceSingleType i =
      R2TestUtilities.getInstanceSingle(g, ia0, 100L);

    final JCGLArrayObjectType a0 =
      R2TestUtilities.getArrayObject(g);
    final R2ShaderLightSingleUsableType<R2LightSingleType> s0 =
      R2TestUtilities.getShaderLightSingle(g, 0L);
    final R2LightSingleType l0 =
      R2TestUtilities.getLightSingle(a0, 1L);

    final R2SceneLightsType o = this.newLights();
    final R2SceneLightsGroupType lg = o.lightsGetGroup(1);
    final R2SceneLightsClipGroupType cg = lg.lightGroupNewClipGroup(i);

    o.lightsReset();

    this.expected.expect(R2ExceptionLightClipGroupDeleted.class);
    cg.clipGroupAddSingle(l0, s0);
  }

  private interface LoggingGroupConsumerConstructorType
  {
    R2SceneLightsGroupConsumerType apply(
      List<String> log,
      int group);
  }

  private interface LoggingClipGroupConsumerConstructorType
  {
    R2SceneLightsClipGroupConsumerType apply(
      List<String> log,
      R2InstanceSingleType i,
      int group);
  }

  private static final class LoggingClipGroupConsumer implements
    R2SceneLightsClipGroupConsumerType
  {
    private final List<String> log;
    private final R2InstanceSingleType volume;
    private final int group;

    LoggingClipGroupConsumer(
      final List<String> in_log,
      final R2InstanceSingleType i,
      final int g)
    {
      this.log = NullCheck.notNull(in_log);
      this.volume = NullCheck.notNull(i);
      this.group = g;
    }

    @Override
    public void onStart()
    {
      this.log.add(String.format(
        "ClipGroup.onStart %d",
        Integer.valueOf(this.group)));
    }

    @Override
    public <M extends R2LightSingleReadableType>
    void onLightSingleShaderStart(
      final R2ShaderLightSingleUsableType<M> s)
    {
      this.log.add(String.format(
        "ClipGroup.onLightSingleShaderStart %d",
        Long.valueOf(s.shaderID())));
    }

    @Override
    public void onLightSingleArrayStart(
      final R2LightSingleReadableType i)
    {
      this.log.add(String.format(
        "ClipGroup.onLightSingleArrayStart %d",
        Long.valueOf(i.lightID())));
    }

    @Override
    public <M extends R2LightSingleReadableType>
    void onLightSingle(
      final R2ShaderLightSingleUsableType<M> s,
      final M i)
    {
      this.log.add(String.format(
        "ClipGroup.onLightSingle %d %d",
        Long.valueOf(s.shaderID()),
        Long.valueOf(i.lightID())));
    }

    @Override
    public <M extends R2LightSingleReadableType>
    void onLightSingleShaderFinish(
      final R2ShaderLightSingleUsableType<M> s)
    {
      this.log.add(String.format(
        "ClipGroup.onLightSingleShaderFinish %d",
        Long.valueOf(s.shaderID())));
    }

    @Override
    public void onFinish()
    {
      this.log.add(String.format(
        "ClipGroup.onFinish %d", Integer.valueOf(this.group)));
    }
  }

  private static final class LoggingGroupConsumer
    implements R2SceneLightsGroupConsumerType
  {
    private final int group;
    private final List<String> log;

    LoggingGroupConsumer(
      final List<String> in_log,
      final int in_group)
    {
      this.log = NullCheck.notNull(in_log);
      this.group = in_group;
    }

    @Override
    public void onStart()
    {
      this.log.add(String.format(
        "Group.onStart %d",
        Integer.valueOf(this.group)));
    }

    @Override
    public <M extends R2LightSingleReadableType>
    void onLightSingleShaderStart(
      final R2ShaderLightSingleUsableType<M> s)
    {
      this.log.add(String.format(
        "Group.onLightSingleShaderStart %d",
        Long.valueOf(s.shaderID())));
    }

    @Override
    public void onLightSingleArrayStart(
      final R2LightSingleReadableType i)
    {
      this.log.add(String.format(
        "Group.onLightSingleArrayStart %d",
        Long.valueOf(i.lightID())));
    }

    @Override
    public <M extends R2LightSingleReadableType>
    void onLightSingle(
      final R2ShaderLightSingleUsableType<M> s,
      final M i)
    {
      this.log.add(String.format(
        "Group.onLightSingle %d %d",
        Long.valueOf(s.shaderID()),
        Long.valueOf(i.lightID())));
    }

    @Override
    public <M extends R2LightSingleReadableType>
    void onLightSingleShaderFinish(
      final R2ShaderLightSingleUsableType<M> s)
    {
      this.log.add(String.format(
        "Group.onLightSingleShaderFinish %d",
        Long.valueOf(s.shaderID())));
    }

    @Override
    public void onFinish()
    {
      this.log.add(String.format(
        "Group.onFinish %d", Integer.valueOf(this.group)));
    }
  }

  private static final class LoggingConsumer
    implements R2SceneLightsConsumerType
  {
    private final LoggingGroupConsumerConstructorType group_cons;
    private final LoggingClipGroupConsumerConstructorType clip_group_cons;
    private final List<String> log;

    LoggingConsumer(
      final List<String> in_log,
      final LoggingGroupConsumerConstructorType in_group_cons,
      final LoggingClipGroupConsumerConstructorType in_clip_group_cons)
    {
      this.log = NullCheck.notNull(in_log);
      this.group_cons = NullCheck.notNull(in_group_cons);
      this.clip_group_cons = NullCheck.notNull(in_clip_group_cons);
    }

    @Override
    public void onStart()
    {
      this.log.add("onStart");
    }

    @Override
    public R2SceneLightsClipGroupConsumerType onStartClipGroup(
      final R2InstanceSingleType i,
      final int group)
    {
      this.log.add(String.format(
        "onStartClipGroup %d %d",
        Long.valueOf(i.instanceID()),
        Integer.valueOf(group)));

      return this.clip_group_cons.apply(this.log, i, group);
    }

    @Override
    public R2SceneLightsGroupConsumerType onStartGroup(
      final int group)
    {
      this.log.add(String.format("onStartGroup %d", Integer.valueOf(group)));
      return this.group_cons.apply(this.log, group);
    }

    @Override
    public void onFinish()
    {
      this.log.add("onFinish");
    }
  }

  private static abstract class UnreachableConsumer implements
    R2SceneLightsConsumerType
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
    public R2SceneLightsClipGroupConsumerType onStartClipGroup(
      final R2InstanceSingleType i,
      final int group)
    {
      throw new UnreachableCodeException();
    }

    @Override
    public R2SceneLightsGroupConsumerType onStartGroup(final int group)
    {
      throw new UnreachableCodeException();
    }
  }
}
