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

import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.r2.core.R2Texture2DType;
import com.io7m.r2.core.R2TextureUnitAllocator;
import com.io7m.r2.core.R2TextureUnitAllocatorType;
import com.io7m.r2.core.filters.R2SSAONoiseTexture;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2SSAONoiseTextureContract extends R2JCGLContract
{
  @Test
  public final void testTexture()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type gi = c.contextGetGL33();
    final JCGLTexturesType gt = gi.getTextures();
    final R2TextureUnitAllocatorType tc =
      R2TextureUnitAllocator.newAllocatorWithStack(32, gt.textureGetUnits());

    final R2Texture2DType tt =
      R2SSAONoiseTexture.newNoiseTexture(gt, tc.getRootContext());

    final JCGLTexture2DUsableType t = tt.get();
    Assert.assertEquals(64L, t.textureGetWidth());
    Assert.assertEquals(64L, t.textureGetWidth());
    Assert.assertEquals(
      JCGLTextureFormat.TEXTURE_FORMAT_RGB_8_3BPP, t.textureGetFormat());
  }
}
