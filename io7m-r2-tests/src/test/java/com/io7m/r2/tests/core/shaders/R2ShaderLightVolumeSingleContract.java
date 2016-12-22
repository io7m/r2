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

package com.io7m.r2.tests.core.shaders;

import com.io7m.jareas.core.AreaInclusiveUnsignedL;
import com.io7m.jcanephora.core.JCGLProjectionMatrices;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextType;
import com.io7m.jfsm.core.FSMTransitionException;
import com.io7m.jfunctional.Unit;
import com.io7m.jtensors.parameterized.PMatrix4x4FType;
import com.io7m.jtensors.parameterized.PMatrixHeapArrayM4x4F;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2GeometryBuffer;
import com.io7m.r2.core.R2GeometryBufferComponents;
import com.io7m.r2.core.R2GeometryBufferDescription;
import com.io7m.r2.core.R2GeometryBufferType;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2LightVolumeSingleReadableType;
import com.io7m.r2.core.R2Matrices;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionOrthographic;
import com.io7m.r2.core.R2ProjectionReadableType;
import com.io7m.r2.core.R2TextureDefaults;
import com.io7m.r2.core.R2TextureDefaultsType;
import com.io7m.r2.core.shaders.types.R2ShaderLightVolumeSingleType;
import com.io7m.r2.core.shaders.types.R2ShaderParametersLight;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;
import com.io7m.r2.tests.core.R2JCGLContract;
import com.io7m.r2.tests.core.ShaderPreprocessing;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public abstract class R2ShaderLightVolumeSingleContract<
  T extends R2LightVolumeSingleReadableType>
  extends R2JCGLContract
{
  @Rule public ExpectedException expected = ExpectedException.none();

  private static R2GeometryBufferType newGeometryBuffer(
    final JCGLFramebuffersType g_fb,
    final JCGLTexturesType g_tex,
    final JCGLTextureUnitContextParentType tr)
  {
    final JCGLTextureUnitContextType tc =
      tr.unitContextNew();

    try {
      final R2GeometryBufferDescription gbuffer_desc =
        R2GeometryBufferDescription.of(
          AreaInclusiveUnsignedL.of(
            new UnsignedRangeInclusiveL(0L, 4L),
            new UnsignedRangeInclusiveL(0L, 4L)),
          R2GeometryBufferComponents.R2_GEOMETRY_BUFFER_FULL);
      final R2GeometryBufferType gb = R2GeometryBuffer.newGeometryBuffer(
        g_fb, g_tex, tc, gbuffer_desc);
      g_fb.framebufferDrawUnbind();
      return gb;
    } finally {
      tc.unitContextFinish(g_tex);
    }
  }

  protected abstract R2ShaderLightVolumeSingleType<T> newShaderWithVerifier(
    JCGLInterfaceGL33Type g,
    R2ShaderPreprocessingEnvironmentReadableType sources,
    R2IDPoolType pool);

  protected abstract T newLight(
    JCGLInterfaceGL33Type g,
    R2IDPoolType pool);

  @Test
  public final void testCorrect()
    throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final JCGLFramebuffersType g_fb =
      g.getFramebuffers();
    final JCGLTexturesType g_tex =
      g.getTextures();
    final JCGLShadersType g_sh =
      g.getShaders();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        32,
        g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tr =
      ta.getRootContext();
    final R2TextureDefaultsType td =
      R2TextureDefaults.newDefaults(g_tex, tr);

    final R2GeometryBufferType gbuffer =
      newGeometryBuffer(g_fb, g_tex, tr);

    final JCGLTextureUnitContextType tc = tr.unitContextNew();
    final JCGLTextureUnitType ua =
      tc.unitContextBindTexture2D(
        g_tex, gbuffer.albedoEmissiveTexture().texture());
    final JCGLTextureUnitType un =
      tc.unitContextBindTexture2D(
        g_tex, gbuffer.normalTexture().texture());
    final JCGLTextureUnitType us =
      tc.unitContextBindTexture2D(
        g_tex, gbuffer.getSpecularTextureOrDefault(td).texture());
    final JCGLTextureUnitType ud =
      tc.unitContextBindTexture2D(
        g_tex, gbuffer.depthTexture().texture());

    final R2ShaderLightVolumeSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T params =
      this.newLight(g, pool);

    final R2ProjectionReadableType proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());
    final R2MatricesType mat =
      R2Matrices.newMatrices();
    final PMatrix4x4FType<R2SpaceWorldType, R2SpaceEyeType> view =
      PMatrixHeapArrayM4x4F.newMatrix();

    mat.withObserver(view, proj, this, (mo, x) -> {
      f.onActivate(g);
      f.onReceiveBoundGeometryBufferTextures(g, gbuffer, ua, us, ud, un);
      f.onReceiveValues(
        g, R2ShaderParametersLight.of(tc, params, mo, gbuffer.area()));

      return mo.withVolumeLight(params, this, (mv, y) -> {
        f.onReceiveVolumeLightTransform(g, mv);
        f.onValidate();
        f.onDeactivate(g);
        return Unit.unit();
      });
    });
  }

  @Test
  public final void testMissedGeometryBuffer()
    throws Exception
  {
    final JCGLContextType c =
      this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g =
      c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool =
      R2IDPool.newPool();

    final JCGLFramebuffersType g_fb =
      g.getFramebuffers();
    final JCGLTexturesType g_tex =
      g.getTextures();
    final JCGLShadersType g_sh =
      g.getShaders();
    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        32,
        g_tex.textureGetUnits());
    final JCGLTextureUnitContextParentType tr =
      ta.getRootContext();

    final R2GeometryBufferType gbuffer =
      newGeometryBuffer(g_fb, g_tex, tr);

    final JCGLTextureUnitContextType tc = tr.unitContextNew();

    final R2ShaderLightVolumeSingleType<T> f =
      this.newShaderWithVerifier(g, sources, pool);
    final T params =
      this.newLight(g, pool);

    final R2ProjectionReadableType proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());
    final R2MatricesType mat =
      R2Matrices.newMatrices();
    final PMatrix4x4FType<R2SpaceWorldType, R2SpaceEyeType> view =
      PMatrixHeapArrayM4x4F.newMatrix();

    f.onActivate(g);

    this.expected.expect(FSMTransitionException.class);
    mat.withObserver(view, proj, this, (mo, x) -> {
      f.onReceiveValues(
        g, R2ShaderParametersLight.of(tc, params, mo, gbuffer.area()));
      return Unit.unit();
    });
  }

  @Test
  public final void testNew()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();
    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType pool = R2IDPool.newPool();

    final R2ShaderLightVolumeSingleType<T> s =
      this.newShaderWithVerifier(g, sources, pool);
    final T light =
      this.newLight(g, pool);

    final Class<?> s_class = s.shaderParametersType();
    final Class<?> l_class = light.getClass();
    Assert.assertTrue(s_class.isAssignableFrom(l_class));
    Assert.assertTrue(light.lightID() >= 0L);

    Assert.assertFalse(s.isDeleted());
    s.delete(g);
    Assert.assertTrue(s.isDeleted());
  }
}
