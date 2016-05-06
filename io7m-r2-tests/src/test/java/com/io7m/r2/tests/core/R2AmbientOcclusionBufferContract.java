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
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2AmbientOcclusionBuffer;
import com.io7m.r2.core.R2AmbientOcclusionBufferDescription;
import com.io7m.r2.core.R2AmbientOcclusionBufferType;
import com.io7m.r2.core.R2Texture2DUsableType;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2AmbientOcclusionBufferContract extends R2JCGLContract
{
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

    final R2AmbientOcclusionBufferDescription.Builder db =
      R2AmbientOcclusionBufferDescription.builder();
    db.setArea(area);

    final JCGLTextureUnitContextParentType tc_root = tc.getRootContext();
    final JCGLTextureUnitContextType tc_alloc = tc_root.unitContextNew();

    try {
      final R2AmbientOcclusionBufferDescription desc = db.build();
      final R2AmbientOcclusionBufferType ab =
        R2AmbientOcclusionBuffer.newAmbientOcclusionBuffer(
          g.getFramebuffers(),
          g.getTextures(),
          tc_alloc,
          desc);

      Assert.assertEquals(
        640L * 480L, ab.getRange().getInterval());
      Assert.assertFalse(ab.isDeleted());

      final R2Texture2DUsableType t_ao =
        ab.getAmbientOcclusionTexture();
      final JCGLFramebufferUsableType fb =
        ab.getPrimaryFramebuffer();

      Assert.assertEquals(desc, ab.getDescription());
      Assert.assertEquals(area, ab.getArea());

      Assert.assertEquals(
        JCGLTextureFormat.TEXTURE_FORMAT_R_8_1BPP,
        t_ao.get().textureGetFormat());

      ab.delete(g);
      Assert.assertTrue(fb.isDeleted());
    } finally {
      tc_alloc.unitContextFinish(g.getTextures());
    }
  }

}
