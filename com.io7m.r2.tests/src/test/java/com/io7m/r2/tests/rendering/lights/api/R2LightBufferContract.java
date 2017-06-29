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

import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.r2.rendering.lights.api.R2LightBufferComponents;
import com.io7m.r2.rendering.lights.api.R2LightBufferDescription;
import com.io7m.r2.rendering.lights.api.R2LightBufferDiffuseOnlyUsableType;
import com.io7m.r2.rendering.lights.api.R2LightBufferDiffuseSpecularUsableType;
import com.io7m.r2.rendering.lights.api.R2LightBufferSpecularOnlyUsableType;
import com.io7m.r2.rendering.lights.api.R2LightBufferType;
import com.io7m.r2.tests.R2JCGLContract;
import com.io7m.r2.textures.R2Texture2DUsableType;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2LightBufferContract extends R2JCGLContract
{
  protected abstract R2LightBufferType newLightBuffer(
    JCGLFramebuffersType g_fb,
    JCGLTexturesType g_tex,
    JCGLTextureUnitContextParentType tc,
    R2LightBufferDescription desc);

  @Test
  public final void testIdentities()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final JCGLTextureUnitAllocatorType tc =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        3, g.textures().textureGetUnits());

    final AreaSizeL area = AreaSizeL.of(640L, 480L);

    for (final R2LightBufferComponents lc : R2LightBufferComponents.values()) {
      final R2LightBufferDescription.Builder desc_b =
        R2LightBufferDescription.builder();
      desc_b.setArea(area);
      desc_b.setComponents(lc);

      final R2LightBufferType gb =
        this.newLightBuffer(
          g.framebuffers(),
          g.textures(),
          tc.rootContext(),
          desc_b.build());

      Assert.assertFalse(gb.isDeleted());

      switch (lc) {

        case R2_LIGHT_BUFFER_DIFFUSE_ONLY: {
          final R2LightBufferDiffuseOnlyUsableType gbb =
            (R2LightBufferDiffuseOnlyUsableType) gb;
          final R2Texture2DUsableType t_diffuse =
            gbb.diffuseTexture();

          Assert.assertEquals(
            JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
            t_diffuse.texture().format());

          Assert.assertEquals(640L * 480L * 8L, gb.byteRange().getInterval());
          break;
        }

        case R2_LIGHT_BUFFER_SPECULAR_ONLY: {
          final R2LightBufferSpecularOnlyUsableType gbb =
            (R2LightBufferSpecularOnlyUsableType) gb;
          final R2Texture2DUsableType t_spec =
            gbb.specularTexture();

          Assert.assertEquals(
            JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
            t_spec.texture().format());

          Assert.assertEquals(640L * 480L * 8L, gb.byteRange().getInterval());
          break;
        }

        case R2_LIGHT_BUFFER_DIFFUSE_AND_SPECULAR: {
          final R2LightBufferDiffuseSpecularUsableType gbb =
            (R2LightBufferDiffuseSpecularUsableType) gb;
          final R2Texture2DUsableType t_diffuse =
            gbb.diffuseTexture();
          final R2Texture2DUsableType t_spec =
            gbb.specularTexture();

          Assert.assertEquals(
            JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
            t_diffuse.texture().format());
          Assert.assertEquals(
            JCGLTextureFormat.TEXTURE_FORMAT_RGBA_8_4BPP,
            t_spec.texture().format());

          Assert.assertEquals(640L * 480L * 12L, gb.byteRange().getInterval());
          break;
        }
      }

      final JCGLFramebufferUsableType fb =
        gb.primaryFramebuffer();

      gb.delete(g);
      Assert.assertTrue(fb.isDeleted());
      Assert.assertTrue(gb.isDeleted());
    }
  }
}
