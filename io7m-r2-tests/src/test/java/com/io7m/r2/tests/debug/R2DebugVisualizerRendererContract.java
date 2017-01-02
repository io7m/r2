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

package com.io7m.r2.tests.debug;

import com.io7m.jareas.core.AreaInclusiveUnsignedL;
import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.JCGLProjectionMatrices;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.profiler.JCGLProfiling;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture_unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jfunctional.Unit;
import com.io7m.jtensors.parameterized.PMatrixI4x4F;
import com.io7m.jtensors.parameterized.PVectorI3F;
import com.io7m.jtensors.parameterized.PVectorI4F;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveL;
import com.io7m.r2.core.R2IDPool;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2Matrices;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionOrthographic;
import com.io7m.r2.core.R2SceneLights;
import com.io7m.r2.core.R2SceneOpaques;
import com.io7m.r2.core.debug.R2DebugCube;
import com.io7m.r2.core.debug.R2DebugInstances;
import com.io7m.r2.core.debug.R2DebugLineSegment;
import com.io7m.r2.core.debug.R2DebugVisualizerRendererParameters;
import com.io7m.r2.core.debug.R2DebugVisualizerRendererType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.meshes.defaults.R2UnitSphere;
import com.io7m.r2.tests.core.R2JCGLContract;
import com.io7m.r2.tests.core.ShaderPreprocessing;
import mockit.Expectations;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public abstract class R2DebugVisualizerRendererContract extends R2JCGLContract
{
  protected abstract R2DebugVisualizerRendererType newRenderer(
    final JCGLInterfaceGL33Type in_g,
    final R2ShaderPreprocessingEnvironmentReadableType in_shader_env,
    final R2IDPoolType in_pool);

  @Test
  public void testLineSegmentCalls()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type g = c.contextGetGL33();

    final AreaInclusiveUnsignedL area = AreaInclusiveUnsignedL.of(
      new UnsignedRangeInclusiveL(0L, 639L),
      new UnsignedRangeInclusiveL(0L, 479L));

    final JCGLTextureUnitAllocatorType ta =
      JCGLTextureUnitAllocator.newAllocatorWithStack(
        8, g.getTextures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.getRootContext();

    final R2ShaderPreprocessingEnvironmentType sources =
      ShaderPreprocessing.preprocessor();
    final R2IDPoolType id_pool =
      R2IDPool.newPool();

    final JCGLProfilingType pro =
      JCGLProfiling.newProfiling(g.getTimers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.getChildContext("main");

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.newFrustum(JCGLProjectionMatrices.newMatrices());

    final R2DebugVisualizerRendererType r =
      this.newRenderer(g, sources, id_pool);

    final List<R2DebugLineSegment> segments = new ArrayList<>();
    segments.add(R2DebugLineSegment.of(
      new PVectorI3F<>(0.0f, 0.0f, 0.0f),
      new PVectorI4F<>(1.0f, 1.0f, 1.0f, 1.0f),
      new PVectorI3F<>(1.0f, 1.0f, 1.0f),
      new PVectorI4F<>(1.0f, 0.0f, 0.0f, 1.0f)
    ));
    segments.add(R2DebugLineSegment.of(
      new PVectorI3F<>(0.0f, 0.0f, 0.0f),
      new PVectorI4F<>(1.0f, 1.0f, 1.0f, 1.0f),
      new PVectorI3F<>(1.0f, 1.0f, 1.0f),
      new PVectorI4F<>(1.0f, 0.0f, 0.0f, 1.0f)
    ));
    segments.add(R2DebugLineSegment.of(
      new PVectorI3F<>(0.0f, 0.0f, 0.0f),
      new PVectorI4F<>(1.0f, 1.0f, 1.0f, 1.0f),
      new PVectorI3F<>(1.0f, 1.0f, 1.0f),
      new PVectorI4F<>(1.0f, 0.0f, 0.0f, 1.0f)
    ));

    new Expectations(g)
    {{
      g.getDraw().draw(JCGLPrimitives.PRIMITIVE_LINES, 0, 6);
    }};

    final R2MatricesType m = R2Matrices.newMatrices();
    m.withObserver(PMatrixI4x4F.identity(), proj, Unit.unit(), (mo, y) -> {
      final R2DebugVisualizerRendererParameters params =
        R2DebugVisualizerRendererParameters.builder()
          .setLights(R2SceneLights.newLights())
          .setOpaqueInstances(R2SceneOpaques.newOpaques())
          .setUnitSphere(R2UnitSphere.newUnitSphere8(g))
          .setDebugCube(R2DebugCube.newDebugCube(g))
          .setDebugInstances(R2DebugInstances.builder().addAllLineSegments(
            segments).build())
          .build();
      r.renderScene(area, pro_root, tc, mo, params);
      return Unit.unit();
    });
  }
}
