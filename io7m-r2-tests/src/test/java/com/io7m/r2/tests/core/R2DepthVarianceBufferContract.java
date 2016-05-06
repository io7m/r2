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

import com.io7m.jareas.core.AreaInclusiveUnsignedL;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2DepthPrecision;
import com.io7m.r2.core.R2DepthVarianceBuffer;
import com.io7m.r2.core.R2DepthVarianceBufferDescription;
import com.io7m.r2.core.R2DepthVarianceBufferType;
import com.io7m.r2.core.R2DepthVariancePrecision;
import com.io7m.r2.core.R2Texture2DUsableType;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2DepthVarianceBufferContract extends R2JCGLContract
{
  private static JCGLTextureFormat formatVarianceForPrecision(
    final R2DepthVariancePrecision p)
  {
    switch (p) {
      case R2_DEPTH_VARIANCE_PRECISION_16:
        return JCGLTextureFormat.TEXTURE_FORMAT_RG_16F_4BPP;
      case R2_DEPTH_VARIANCE_PRECISION_32:
        return JCGLTextureFormat.TEXTURE_FORMAT_RG_32F_8BPP;
    }

    throw new UnreachableCodeException();
  }

  private static JCGLTextureFormat formatDepthForPrecision(
    final R2DepthPrecision p)
  {
    switch (p) {
      case R2_DEPTH_PRECISION_16:
        return JCGLTextureFormat.TEXTURE_FORMAT_DEPTH_16_2BPP;
      case R2_DEPTH_PRECISION_24:
        return JCGLTextureFormat.TEXTURE_FORMAT_DEPTH_24_4BPP;
      case R2_DEPTH_PRECISION_32F:
        return JCGLTextureFormat.TEXTURE_FORMAT_DEPTH_32F_4BPP;
    }

    throw new UnreachableCodeException();
  }

  @Test
  public final void testIdentities()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final JCGLTextureUnitAllocatorType tc =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        3, g.getTextures().textureGetUnits());

    final AreaInclusiveUnsignedL area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 639L),
      new UnsignedRangeInclusiveL(0L, 479L));

    for (final R2DepthPrecision dp : R2DepthPrecision.values()) {
      for (final R2DepthVariancePrecision vp : R2DepthVariancePrecision
        .values()) {
        final JCGLTextureFormat df =
          R2DepthVarianceBufferContract.formatDepthForPrecision(dp);
        final JCGLTextureFormat vf =
          R2DepthVarianceBufferContract.formatVarianceForPrecision(vp);

        final R2DepthVarianceBufferDescription.Builder db =
          R2DepthVarianceBufferDescription.builder();
        db.setDepthVariancePrecision(vp);
        db.setDepthPrecision(dp);
        db.setArea(area);

        final R2DepthVarianceBufferDescription desc = db.build();
        final R2DepthVarianceBufferType gb =
          R2DepthVarianceBuffer.newDepthVarianceBuffer(
            g.getFramebuffers(),
            g.getTextures(),
            tc.getRootContext(),
            desc);

        final int bpp =
          vf.getBytesPerPixel() + df.getBytesPerPixel();

        Assert.assertEquals(
          640L * 480L * bpp, gb.getRange().getInterval());
        Assert.assertFalse(gb.isDeleted());

        final R2Texture2DUsableType t_dept =
          gb.getDepthVarianceTexture();
        final JCGLFramebufferUsableType fb =
          gb.getPrimaryFramebuffer();

        Assert.assertEquals(desc, gb.getDescription());
        Assert.assertEquals(area, gb.getArea());
        Assert.assertEquals(
          vf,
          t_dept.get().textureGetFormat());

        gb.delete(g);
        Assert.assertTrue(fb.isDeleted());
        Assert.assertTrue(gb.isDeleted());
      }
    }
  }
}
