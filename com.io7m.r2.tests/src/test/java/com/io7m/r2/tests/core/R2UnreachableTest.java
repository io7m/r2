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

import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.core.R2Bilinear;
import com.io7m.r2.core.R2Core;
import com.io7m.r2.core.R2IndexBuffers;
import com.io7m.r2.core.R2Projections;
import com.io7m.r2.core.R2RenderTargetDescriptions;
import com.io7m.r2.core.R2Stencils;
import com.io7m.r2.core.debug.R2DebugVisualizerDefaults;
import com.io7m.r2.core.shaders.types.R2ShaderParameters;
import com.io7m.r2.meshes.api.R2MeshAttributeConventions;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Abuse reflection to verify that code marked as unreachable really is
 * unreachable.
 */

public final class R2UnreachableTest
{
  private static void checkUnreachable(final Class<?> c)
    throws Throwable
  {
    try {
      final Constructor<?> cc = c.getDeclaredConstructors()[0];
      cc.setAccessible(true);
      cc.newInstance();
    } catch (final InvocationTargetException e) {
      throw e.getCause();
    }
  }

  @Test(expected = UnreachableCodeException.class)
  public void testAttributeConventions()
    throws Throwable
  {
    checkUnreachable(R2MeshAttributeConventions.class);
  }

  @Test(expected = UnreachableCodeException.class)
  public void testIndexBuffers()
    throws Throwable
  {
    checkUnreachable(R2IndexBuffers.class);
  }

  @Test(expected = UnreachableCodeException.class)
  public void testRenderTargetDescriptions()
    throws Throwable
  {
    checkUnreachable(R2RenderTargetDescriptions.class);
  }

  @Test(expected = UnreachableCodeException.class)
  public void testDebugVisualizerDefaults()
    throws Throwable
  {
    checkUnreachable(R2DebugVisualizerDefaults.class);
  }

  @Test(expected = UnreachableCodeException.class)
  public void testBilinear()
    throws Throwable
  {
    checkUnreachable(R2Bilinear.class);
  }

  @Test(expected = UnreachableCodeException.class)
  public void testCore()
    throws Throwable
  {
    checkUnreachable(R2Core.class);
  }

  @Test(expected = UnreachableCodeException.class)
  public void testProjections()
    throws Throwable
  {
    checkUnreachable(R2Projections.class);
  }

  @Test(expected = UnreachableCodeException.class)
  public void testShaderParameters()
    throws Throwable
  {
    checkUnreachable(R2ShaderParameters.class);
  }

  @Test(expected = UnreachableCodeException.class)
  public void testStencils()
    throws Throwable
  {
    checkUnreachable(R2Stencils.class);
  }
}
