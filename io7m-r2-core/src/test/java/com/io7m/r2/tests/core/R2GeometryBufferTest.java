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

import com.io7m.jareas.core.AreaInclusiveUnsignedI;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveI;
import com.io7m.r2.core.R2GeometryBuffer;
import com.io7m.r2.core.R2GeometryBufferType;
import org.junit.Assert;
import org.junit.Test;

public final class R2GeometryBufferTest
{
  @Test public void testIdentities()
  {
    final JCGLContextType c = R2TestContexts.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final R2GeometryBufferType gb =
      R2GeometryBuffer.newGeometryBuffer      (
        g,
        AreaInclusiveUnsignedI.of            (
          new UnsignedRangeInclusiveI(0, 639),
          new UnsignedRangeInclusiveI(0, 479)));

    Assert.assertEquals(640L * 480L * 16L, gb.getRange().getInterval());
    Assert.assertFalse(gb.isDeleted());

    final JCGLTexture2DUsableType t_rgba =
      gb.getAlbedoEmissiveTexture();
    final JCGLTexture2DUsableType t_dept =
      gb.getDepthTexture();
    final JCGLTexture2DUsableType t_spec =
      gb.getSpecularTexture();
    final JCGLTexture2DUsableType t_norm =
      gb.getNormalTexture();
    final JCGLFramebufferUsableType fb =
      gb.getFramebuffer();

    Assert.assertEquals        (
      JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
      t_rgba.textureGetFormat());
    Assert.assertEquals        (
      JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
      t_spec.textureGetFormat());
    Assert.assertEquals        (
      JCGLTextureFormat.TEXTURE_FORMAT_RG_16F_4BPP,
      t_norm.textureGetFormat());
    Assert.assertEquals        (
      JCGLTextureFormat.TEXTURE_FORMAT_DEPTH_24_STENCIL_8_4BPP,
      t_dept.textureGetFormat());
  }
}
