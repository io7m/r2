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
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2GeometryBuffer;
import com.io7m.r2.core.R2GeometryBufferComponents;
import com.io7m.r2.core.R2GeometryBufferDescription;
import com.io7m.r2.core.R2GeometryBufferType;
import com.io7m.r2.core.R2Texture2DUsableType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public abstract class R2GeometryBufferContract extends R2JCGLContract
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

    for (final R2GeometryBufferComponents cc : R2GeometryBufferComponents.values()) {
      final R2GeometryBufferDescription desc =
        R2GeometryBufferDescription.of(area, cc);

      final R2GeometryBufferType gb =
        R2GeometryBuffer.newGeometryBuffer(
          g.getFramebuffers(),
          g.getTextures(),
          tc.getRootContext(),
          desc);

      Assert.assertFalse(gb.isDeleted());

      final R2Texture2DUsableType t_rgba =
        gb.albedoEmissiveTexture();
      final R2Texture2DUsableType t_dept =
        gb.depthTexture();
      final Optional<R2Texture2DUsableType> t_spec =
        gb.specularTexture();
      final R2Texture2DUsableType t_norm =
        gb.normalTexture();
      final JCGLFramebufferUsableType fb =
        gb.primaryFramebuffer();

      Assert.assertEquals(desc, gb.description());
      Assert.assertEquals(area, gb.area());

      Assert.assertEquals(
        JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
        t_rgba.texture().textureGetFormat());
      Assert.assertEquals(
        JCGLTextureFormat.TEXTURE_FORMAT_RG_16F_4BPP,
        t_norm.texture().textureGetFormat());
      Assert.assertEquals(
        JCGLTextureFormat.TEXTURE_FORMAT_DEPTH_24_STENCIL_8_4BPP,
        t_dept.texture().textureGetFormat());

      switch (cc) {
        case R2_GEOMETRY_BUFFER_FULL: {
          Assert.assertEquals(640L * 480L * 16L, gb.getRange().getInterval());
          Assert.assertEquals(
            JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
            t_spec.get().texture().textureGetFormat());
          break;
        }
        case R2_GEOMETRY_BUFFER_NO_SPECULAR: {
          Assert.assertEquals(640L * 480L * 12L, gb.getRange().getInterval());
          break;
        }
      }

      gb.delete(g);
      Assert.assertTrue(fb.isDeleted());
      Assert.assertTrue(gb.isDeleted());
    }
  }
}
