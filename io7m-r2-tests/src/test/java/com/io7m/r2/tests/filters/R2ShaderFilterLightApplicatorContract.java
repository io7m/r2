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

import com.io7m.jareas.core.AreaInclusiveUnsignedL;
import com.io7m.jcanephora.core.JCGLProjectionMatrices;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2GeometryBuffer;
import com.io7m.r2.core.R2GeometryBufferDescription;
import com.io7m.r2.core.R2GeometryBufferType;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightBufferComponents;
import com.io7m.r2.core.R2LightBufferDescription;
import com.io7m.r2.core.R2LightBufferDiffuseSpecularUsableType;
import com.io7m.r2.core.R2LightBufferType;
import com.io7m.r2.core.R2LightBuffers;
import com.io7m.r2.core.R2ProjectionOrthographic;
import com.io7m.r2.core.R2ProjectionReadableType;
import com.io7m.r2.core.R2TextureUnitAllocator;
import com.io7m.r2.core.R2TextureUnitAllocatorType;
import com.io7m.r2.core.R2TextureUnitContextParentType;
import com.io7m.r2.core.R2TextureUnitContextType;
import com.io7m.r2.core.shaders.types.R2ShaderFilterType;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesResources;
import com.io7m.r2.core.shaders.types.R2ShaderSourcesType;
import com.io7m.r2.filters.R2ShaderFilterLightApplicator;
import com.io7m.r2.filters.R2ShaderFilterLightApplicatorParametersMutable;
import com.io7m.r2.filters.R2ShaderFilterLightApplicatorParametersType;
import com.io7m.r2.shaders.R2Shaders;
import org.junit.Assert;
import org.junit.Test;

public abstract class R2ShaderFilterLightApplicatorContract extends
  R2ShaderFilterContract<R2ShaderFilterLightApplicatorParametersType,
    R2ShaderFilterLightApplicatorParametersMutable>
{
  @Override
  protected final R2ShaderFilterLightApplicatorParametersMutable
  newParameters(final JCGLInterfaceGL33Type g)
  {
    final R2ShaderFilterLightApplicatorParametersMutable p =
      R2ShaderFilterLightApplicatorParametersMutable.create();

    final JCGLTexturesType g_tex = g.getTextures();
    final JCGLFramebuffersType g_fb = g.getFramebuffers();

    final R2TextureUnitAllocatorType tp =
      R2TextureUnitAllocator.newAllocatorWithStack(8, g_tex.textureGetUnits());
    final R2TextureUnitContextParentType tc_root =
      tp.getRootContext();
    final R2TextureUnitContextType tc_alloc =
      tc_root.unitContextNew();

    try {
      final R2ProjectionReadableType proj =
        R2ProjectionOrthographic.newFrustum(
          JCGLProjectionMatrices.newMatrices());

      final AreaInclusiveUnsignedL area =
        AreaInclusiveUnsignedL.of(
          new UnsignedRangeInclusiveL(0L, 639L),
          new UnsignedRangeInclusiveL(0L, 479L));

      final R2GeometryBufferType gb =
        R2GeometryBuffer.newGeometryBuffer(
          g.getFramebuffers(),
          g_tex,
          tc_alloc,
          R2GeometryBufferDescription.of(area));

        final R2LightBufferDescription.Builder desc_b =
          R2LightBufferDescription.builder();
        desc_b.setArea(area);
        desc_b.setComponents(
          R2LightBufferComponents.R2_LIGHT_BUFFER_DIFFUSE_AND_SPECULAR);

      final R2LightBufferType lb =
        R2LightBuffers.newLightBuffer(
          g.getFramebuffers(),
          g_tex,
          tc_alloc,
          desc_b.build());

      final R2LightBufferDiffuseSpecularUsableType diff_spec =
        (R2LightBufferDiffuseSpecularUsableType) lb;

      g_fb.framebufferDrawUnbind();

      p.setAlbedoTexture(gb.getAlbedoEmissiveTexture());
      p.setDiffuseTexture(diff_spec.getDiffuseTexture());
      p.setSpecularTexture(diff_spec.getSpecularTexture());

    } finally {
      tc_alloc.unitContextFinish(g_tex);
    }

    return p;
  }

  @Test
  public final void testNew()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ShaderSourcesType sources =
      R2ShaderSourcesResources.newSources(R2Shaders.class);
    final R2IDPoolType pool = R2IDPool.newPool();

    final R2ShaderFilterType<R2ShaderFilterLightApplicatorParametersType> s =
      R2ShaderFilterLightApplicator.newShader(
        g.getShaders(),
        sources,
        pool);

    Assert.assertFalse(s.isDeleted());
    s.delete(g);
    Assert.assertTrue(s.isDeleted());
  }
}
