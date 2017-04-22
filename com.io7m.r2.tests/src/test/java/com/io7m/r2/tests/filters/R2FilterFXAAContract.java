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

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2ImageBuffer;
import com.io7m.r2.core.R2ImageBufferType;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.R2UnitQuad;
import com.io7m.r2.core.R2UnitQuadType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.filters.R2FilterFXAA;
import com.io7m.r2.filters.R2FilterFXAAParameters;
import com.io7m.r2.tests.core.R2JCGLContract;
import com.io7m.r2.tests.core.ShaderPreprocessing;
import org.junit.Assert;
import org.junit.Test;

import static com.io7m.jcanephora.profiler.JCGLProfiling.newProfiling;
import static com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator.newAllocatorWithStack;
import static com.io7m.r2.core.R2IDPool.newPool;
import static com.io7m.r2.core.R2ImageBufferDescription.of;
import static com.io7m.r2.core.R2TextureDefaults.create;
import static com.io7m.r2.core.R2UnitQuad.newUnitQuad;
import static com.io7m.r2.filters.R2FilterFXAA.newFilter;
import static com.io7m.r2.filters.R2FilterFXAAParameters.builder;
import static com.io7m.r2.tests.core.ShaderPreprocessing.preprocessor;
import static java.util.Optional.empty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class R2FilterFXAAContract extends R2JCGLContract
{
  @Test
  public final void testIdentities()
  {
    final JCGLContextType gc =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      gc.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType id =
      R2IDPool.newPool();
    final R2UnitQuadType quad =
      R2UnitQuad.newUnitQuad(g);

    final R2FilterType<R2FilterFXAAParameters> f =
      R2FilterFXAA.newFilter(g, sources, id, quad);

    Assert.assertFalse(f.isDeleted());
    Assert.assertFalse(f.isDeleted());
    f.delete(g);
    Assert.assertTrue(f.isDeleted());
  }

  @Test
  public final void testFramebufferBindingDefault()
  {
    final JCGLContextType gc =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      gc.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2IDPoolType id =
      newPool();
    final JCGLFramebuffersType g_fb =
      g.framebuffers();
    final JCGLTexturesType g_t =
      g.textures();
    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      create(g.textures(), tc);
    final R2UnitQuadType quad =
      newUnitQuad(g);
    final JCGLProfilingType pro =
      newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final AreaSizeL area = AreaSizeL.of(128L, 128L);

    final R2FilterType<R2FilterFXAAParameters> f =
      newFilter(g, sources, id, quad);

    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    assertFalse(g_fb.framebufferReadAnyIsBound());
    assertFalse(g_fb.framebufferDrawAnyIsBound());

    final R2FilterFXAAParameters params =
      builder()
        .setTexture(td.black2D())
        .build();

    f.runFilter(pro_root, tc, params);

    assertFalse(g_fb.framebufferReadAnyIsBound());
    assertFalse(g_fb.framebufferDrawAnyIsBound());
  }

  @Test
  public final void testFramebufferBindingNonDefault()
  {
    final JCGLContextType gc =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      gc.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2IDPoolType id =
      newPool();
    final JCGLFramebuffersType g_fb =
      g.framebuffers();
    final JCGLTexturesType g_t =
      g.textures();
    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        8, g_t.textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();
    final R2TextureDefaultsType td =
      create(g.textures(), tc);
    final R2UnitQuadType quad =
      newUnitQuad(g);
    final JCGLProfilingType pro =
      newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final AreaSizeL area = AreaSizeL.of(128L, 128L);

    final R2ImageBufferType ib = R2ImageBuffer.create(
      g_fb, g_t, tc, of(area, empty()));

    final R2FilterType<R2FilterFXAAParameters> f =
      newFilter(g, sources, id, quad);

    g_fb.framebufferDrawUnbind();
    g_fb.framebufferReadUnbind();

    assertFalse(g_fb.framebufferReadAnyIsBound());
    assertFalse(g_fb.framebufferDrawAnyIsBound());

    final R2FilterFXAAParameters params =
      builder()
        .setTexture(td.black2D())
        .build();

    g_fb.framebufferDrawBind(ib.primaryFramebuffer());
    f.runFilter(pro_root, tc, params);

    assertFalse(g_fb.framebufferReadAnyIsBound());
    assertTrue(g_fb.framebufferDrawIsBound(ib.primaryFramebuffer()));
  }
}
