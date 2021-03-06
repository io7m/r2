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

package com.io7m.r2.tests.filters.light_applicator;

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.r2.core.api.ids.R2IDPool;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.filters.light_applicator.R2ShaderFilterLightApplicator;
import com.io7m.r2.filters.light_applicator.R2ShaderFilterLightApplicatorParameters;
import com.io7m.r2.projections.R2ProjectionOrthographic;
import com.io7m.r2.projections.R2ProjectionReadableType;
import com.io7m.r2.rendering.geometry.R2GeometryBuffer;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferComponents;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferDescription;
import com.io7m.r2.rendering.geometry.api.R2GeometryBufferType;
import com.io7m.r2.rendering.lights.R2LightBuffers;
import com.io7m.r2.rendering.lights.api.R2LightBufferComponents;
import com.io7m.r2.rendering.lights.api.R2LightBufferDescription;
import com.io7m.r2.rendering.lights.api.R2LightBufferDiffuseSpecularUsableType;
import com.io7m.r2.rendering.lights.api.R2LightBufferType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.shaders.filter.api.R2ShaderFilterType;
import com.io7m.r2.tests.ShaderPreprocessing;
import com.io7m.r2.tests.filters.api.R2ShaderFilterContract;
import org.junit.Assert;
import org.junit.Test;


public abstract class R2ShaderFilterLightApplicatorContract extends
  R2ShaderFilterContract<R2ShaderFilterLightApplicatorParameters,
    R2ShaderFilterLightApplicatorParameters>
{
  @Override
  protected final R2ShaderFilterLightApplicatorParameters
  newParameters(final JCGLInterfaceGL33Type g)
  {
    final JCGLTexturesType g_tex = g.textures();
    final JCGLFramebuffersType g_fb = g.framebuffers();

    final JCGLTextureUnitAllocatorType tp =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8,
        g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tc_root =
      tp.rootContext();
    final JCGLTextureUnitContextType tc_alloc =
      tc_root.unitContextNew();

    try {
      final AreaSizeL area = AreaSizeL.of(640L, 480L);

      final R2GeometryBufferType gb =
        R2GeometryBuffer.create(
          g.framebuffers(),
          g_tex,
          tc_alloc,
          R2GeometryBufferDescription.of(
            area,
            R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL));

      final R2LightBufferDescription.Builder desc_b =
        R2LightBufferDescription.builder();
      desc_b.setArea(area);
      desc_b.setComponents(
        R2LightBufferComponents.R2_LIGHT_BUFFER_DIFFUSE_AND_SPECULAR);

      final R2LightBufferType lb =
        R2LightBuffers.newLightBuffer(
          g.framebuffers(),
          g_tex,
          tc_alloc,
          desc_b.build());

      final R2LightBufferDiffuseSpecularUsableType diff_spec =
        (R2LightBufferDiffuseSpecularUsableType) lb;

      g_fb.framebufferDrawUnbind();

      return R2ShaderFilterLightApplicatorParameters.builder()
        .setAlbedoTexture(gb.albedoEmissiveTexture())
        .setDiffuseTexture(diff_spec.diffuseTexture())
        .setSpecularTexture(diff_spec.specularTexture())
        .build();

    } finally {
      tc_alloc.unitContextFinish(g_tex);
    }
  }

  @Test
  public final void testNew()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool = R2IDPool.newPool();

    final R2ShaderFilterType<R2ShaderFilterLightApplicatorParameters> s =
      R2ShaderFilterLightApplicator.newShader(
        g.shaders(),
        sources,
        pool);

    Assert.assertFalse(s.isDeleted());
    s.delete(g);
    Assert.assertTrue(s.isDeleted());
  }
}
