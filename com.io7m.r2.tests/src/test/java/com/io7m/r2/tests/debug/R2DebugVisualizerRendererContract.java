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

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.profiler.JCGLProfilingContextType;
import com.io7m.jcanephora.profiler.JCGLProfilingFrameType;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.jregions.core.unparameterized.areas.AreasL;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices4x4D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.r2.core.R2IDPoolType;
import com.io7m.r2.core.R2Matrices;
import com.io7m.r2.core.R2MatricesType;
import com.io7m.r2.core.R2ProjectionOrthographic;
import com.io7m.r2.core.R2SceneLights;
import com.io7m.r2.core.R2SceneOpaques;
import com.io7m.r2.core.debug.R2DebugInstances;
import com.io7m.r2.core.debug.R2DebugLineSegment;
import com.io7m.r2.core.debug.R2DebugVisualizerRendererParameters;
import com.io7m.r2.core.debug.R2DebugVisualizerRendererType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentReadableType;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.tests.core.R2JCGLContract;
import mockit.Expectations;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.io7m.jcanephora.core.JCGLPrimitives.PRIMITIVE_LINES;
import static com.io7m.jcanephora.profiler.JCGLProfiling.newProfiling;
import static com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator.newAllocatorWithStack;
import static com.io7m.jfunctional.Unit.unit;
import static com.io7m.r2.core.R2IDPool.newPool;
import static com.io7m.r2.core.debug.R2DebugCube.create;
import static com.io7m.r2.core.debug.R2DebugLineSegment.of;
import static com.io7m.r2.core.debug.R2DebugVisualizerRendererParameters.builder;
import static com.io7m.r2.meshes.defaults.R2UnitSphere.newUnitSphere8;
import static com.io7m.r2.tests.core.ShaderPreprocessing.preprocessor;

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

    final AreaL area = AreasL.create(0L, 0L, 640L, 480L);

    final JCGLTextureUnitAllocatorType ta =
      newAllocatorWithStack(
        8, g.textures().textureGetUnits());
    final JCGLTextureUnitContextParentType tc =
      ta.rootContext();

    final R2ShaderPreprocessingEnvironmentType sources =
      preprocessor();
    final R2IDPoolType id_pool =
      newPool();

    final JCGLProfilingType pro =
      newProfiling(g.timers());
    final JCGLProfilingFrameType pro_frame =
      pro.startFrame();
    final JCGLProfilingContextType pro_root =
      pro_frame.childContext("main");

    final R2ProjectionOrthographic proj =
      R2ProjectionOrthographic.create();

    final R2DebugVisualizerRendererType r =
      this.newRenderer(g, sources, id_pool);

    final List<R2DebugLineSegment> segments = new ArrayList<>();
    segments.add(of(
      PVector3D.of(0.0, 0.0, 0.0),
      PVector4D.of(1.0, 1.0, 1.0, 1.0),
      PVector3D.of(1.0, 1.0, 1.0),
      PVector4D.of(1.0, 0.0, 0.0, 1.0)
    ));
    segments.add(of(
      PVector3D.of(0.0, 0.0, 0.0),
      PVector4D.of(1.0, 1.0, 1.0, 1.0),
      PVector3D.of(1.0, 1.0, 1.0),
      PVector4D.of(1.0, 0.0, 0.0, 1.0)
    ));
    segments.add(of(
      PVector3D.of(0.0, 0.0, 0.0),
      PVector4D.of(1.0, 1.0, 1.0, 1.0),
      PVector3D.of(1.0, 1.0, 1.0),
      PVector4D.of(1.0, 0.0, 0.0, 1.0)
    ));

    new Expectations(g)
    {{
      g.drawing().draw(PRIMITIVE_LINES, 0, 6);
    }};

    final R2MatricesType m = R2Matrices.create();
    m.withObserver(
      PMatrices4x4D.identity(),
      proj,
      unit(),
      (mo, y) -> {
        final R2DebugVisualizerRendererParameters params =
          builder()
            .setLights(R2SceneLights.create())
            .setOpaqueInstances(R2SceneOpaques.create())
            .setUnitSphere(newUnitSphere8(g))
            .setDebugCube(create(g))
            .setDebugInstances(R2DebugInstances.builder().addAllLineSegments(
              segments).build())
            .build();
        r.renderScene(area, pro_root, tc, mo, params);
        return unit();
      });
  }
}
