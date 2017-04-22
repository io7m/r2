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

import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.r2.core.R2AmbientOcclusionBufferDescription;
import com.io7m.r2.core.R2AmbientOcclusionBufferType;
import com.io7m.r2.core.R2Texture2DUsableType;
import org.junit.Assert;
import org.junit.Test;

import static com.io7m.jcanephora.core.JCGLTextureFormat.TEXTURE_FORMAT_R_8_1BPP;
import static com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator.newAllocatorWithStack;
import static com.io7m.r2.core.R2AmbientOcclusionBuffer.create;
import static com.io7m.r2.core.R2AmbientOcclusionBufferDescription.Builder;
import static com.io7m.r2.core.R2AmbientOcclusionBufferDescription.builder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class R2AmbientOcclusionBufferContract extends R2JCGLContract
{
  @Test
  public final void testIdentities()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final JCGLTextureUnitAllocatorType tc =
      newAllocatorWithStack(
        3, g.textures().textureGetUnits());

    final AreaSizeL area = AreaSizeL.of(640L, 480L);

    final Builder db =
      builder();
    db.setArea(area);

    final JCGLTextureUnitContextParentType tc_root = tc.rootContext();
    final JCGLTextureUnitContextType tc_alloc = tc_root.unitContextNew();

    try {
      final R2AmbientOcclusionBufferDescription desc = db.build();
      final R2AmbientOcclusionBufferType ab =
        create(
          g.framebuffers(),
          g.textures(),
          tc_alloc,
          desc);

      Assert.assertEquals(
        640L * 480L, ab.byteRange().getInterval());
      assertFalse(ab.isDeleted());

      final R2Texture2DUsableType t_ao =
        ab.ambientOcclusionTexture();
      final JCGLFramebufferUsableType fb =
        ab.primaryFramebuffer();

      assertEquals(desc, ab.description());
      Assert.assertEquals(area, ab.size());

      Assert.assertEquals(
        TEXTURE_FORMAT_R_8_1BPP,
        t_ao.texture().format());

      ab.delete(g);
      assertTrue(fb.isDeleted());
    } finally {
      tc_alloc.unitContextFinish(g.textures());
    }
  }

}
