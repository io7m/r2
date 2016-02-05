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

import com.io7m.jareas.core.AreaInclusiveUnsignedI;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveI;
import com.io7m.r2.core.R2ImageBuffer;
import com.io7m.r2.core.R2ImageBufferType;
import com.io7m.r2.core.R2Texture2DUsableType;
import com.io7m.r2.core.R2TextureUnitAllocator;
import com.io7m.r2.core.R2TextureUnitAllocatorType;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2ImageBufferContract extends R2JCGLContract
{
  @Test
  public final void testIdentities()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2TextureUnitAllocatorType a =
      R2TextureUnitAllocator.newAllocatorWithStack(
        4, g.getTextures().textureGetUnits());

    final AreaInclusiveUnsignedI area = AreaInclusiveUnsignedI.of(
      new UnsignedRangeInclusiveI(0, 639),
      new UnsignedRangeInclusiveI(0, 479));
    final R2ImageBufferType gb =
      R2ImageBuffer.newImageBuffer(
        g.getFramebuffers(), g.getTextures(), a.getRootContext(), area);

    Assert.assertEquals(640L * 480L * 4L, gb.getRange().getInterval());
    Assert.assertFalse(gb.isDeleted());

    final R2Texture2DUsableType t_rgba =
      gb.getRGBATexture();
    final JCGLFramebufferUsableType fb =
      gb.getFramebuffer();

    Assert.assertEquals(
      JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
      t_rgba.get().textureGetFormat());

    gb.delete(g);
    Assert.assertTrue(fb.isDeleted());
    Assert.assertTrue(gb.isDeleted());
  }
}
