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

package com.io7m.r2.tests.filters;

import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.r2.core.R2TextureUnitAllocator;
import com.io7m.r2.core.R2TextureUnitAllocatorType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2TextureUnitContextType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterVerifier;
import com.io7m.r2.tests.core.R2TestUtilities;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class R2ShaderFilterVerifierTest
{
  @Rule public ExpectedException expected = ExpectedException.none();

  @Test
  public void testCorrect() throws Exception
  {
    final JCGLInterfaceGL33Type g =
      R2TestUtilities.getFakeGL();
    final R2ShaderFilterType<Object> f =
      R2TestUtilities.getShaderFilter(g, 1L);
    final R2ShaderFilterType<Object> v =
      R2ShaderFilterVerifier.newVerifier(f);

    final JCGLTexturesType g_tex = g.getTextures();
    final JCGLShadersType g_sh = g.getShaders();
    final R2TextureUnitAllocatorType ta =
      R2TextureUnitAllocator.newAllocatorWithStack(32, g_tex.textureGetUnits());
    final R2TextureUnitContextParentType tr = ta.getRootContext();
    final R2TextureUnitContextType tc = tr.unitContextNew();

    v.onActivate(g.getShaders());
    v.onReceiveFilterValues(g_tex, g_sh, tc, new Object());
    v.onValidate();
    v.onDeactivate(g_sh);
  }

  @Test
  public void testInactive() throws Exception
  {
    final JCGLInterfaceGL33Type g =
      R2TestUtilities.getFakeGL();
    final R2ShaderFilterType<Object> f =
      R2TestUtilities.getShaderFilter(g, 1L);
    final R2ShaderFilterType<Object> v =
      R2ShaderFilterVerifier.newVerifier(f);

    final JCGLTexturesType g_tex = g.getTextures();
    final JCGLShadersType g_sh = g.getShaders();
    final R2TextureUnitAllocatorType ta =
      R2TextureUnitAllocator.newAllocatorWithStack(32, g_tex.textureGetUnits());
    final R2TextureUnitContextParentType tr = ta.getRootContext();
    final R2TextureUnitContextType tc = tr.unitContextNew();

    this.expected.expect(IllegalStateException.class);
    v.onReceiveFilterValues(g_tex, g_sh, tc, new Object());
  }

  @Test
  public void testNoReceiveFilter() throws Exception
  {
    final JCGLInterfaceGL33Type g =
      R2TestUtilities.getFakeGL();
    final R2ShaderFilterType<Object> f =
      R2TestUtilities.getShaderFilter(g, 1L);
    final R2ShaderFilterType<Object> v =
      R2ShaderFilterVerifier.newVerifier(f);

    final JCGLShadersType g_sh = g.getShaders();

    v.onActivate(g_sh);
    this.expected.expect(IllegalStateException.class);
    v.onValidate();
  }

  @Test
  public void testDeactivatedValidate() throws Exception
  {
    final JCGLInterfaceGL33Type g =
      R2TestUtilities.getFakeGL();
    final R2ShaderFilterType<Object> f =
      R2TestUtilities.getShaderFilter(g, 1L);
    final R2ShaderFilterType<Object> v =
      R2ShaderFilterVerifier.newVerifier(f);

    final JCGLShadersType g_sh = g.getShaders();

    v.onActivate(g_sh);
    v.onDeactivate(g_sh);

    this.expected.expect(IllegalStateException.class);
    v.onValidate();
  }
}
