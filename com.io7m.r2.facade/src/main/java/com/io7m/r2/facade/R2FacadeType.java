/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.facade;

import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.profiler.JCGLProfiling;
import com.io7m.jcanephora.profiler.JCGLProfilingType;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocator;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitAllocatorType;
import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.core.api.deletable.R2DeletableType;
import com.io7m.r2.core.api.R2Exception;
import com.io7m.r2.core.api.ids.R2IDPool;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.debug.R2DebugVisualizerRenderer;
import com.io7m.r2.debug.api.R2DebugVisualizerRendererType;
import com.io7m.r2.matrices.R2Matrices;
import com.io7m.r2.matrices.R2MatricesType;
import com.io7m.r2.meshes.defaults.R2UnitCube;
import com.io7m.r2.meshes.defaults.R2UnitQuad;
import com.io7m.r2.meshes.defaults.R2UnitSphere;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderAsynchronousType;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderType;
import com.io7m.r2.meshes.loading.smf.R2SMFMeshLoaderAsynchronous;
import com.io7m.r2.meshes.loading.smf.R2SMFMeshLoaderSynchronous;
import com.io7m.r2.rendering.depth.R2DepthOnlyRenderer;
import com.io7m.r2.rendering.depth.api.R2DepthRendererType;
import com.io7m.r2.rendering.depth.variance.R2DepthVarianceBufferPool;
import com.io7m.r2.rendering.depth.variance.R2DepthVarianceRenderer;
import com.io7m.r2.rendering.depth.variance.api.R2DepthVarianceBufferDescription;
import com.io7m.r2.rendering.depth.variance.api.R2DepthVarianceBufferUsableType;
import com.io7m.r2.rendering.depth.variance.api.R2DepthVarianceRendererType;
import com.io7m.r2.rendering.geometry.R2GeometryRenderer;
import com.io7m.r2.rendering.geometry.api.R2GeometryRendererType;
import com.io7m.r2.rendering.lights.R2LightRenderer;
import com.io7m.r2.rendering.lights.api.R2LightRendererType;
import com.io7m.r2.rendering.mask.R2MaskRenderer;
import com.io7m.r2.rendering.mask.api.R2MaskRendererType;
import com.io7m.r2.rendering.shadow.R2ShadowMapRenderer;
import com.io7m.r2.rendering.shadow.api.R2ShadowMapRendererType;
import com.io7m.r2.rendering.stencil.R2StencilRenderer;
import com.io7m.r2.rendering.stencil.api.R2StencilRendererType;
import com.io7m.r2.rendering.targets.R2RenderTargetPoolType;
import com.io7m.r2.rendering.translucent.R2TranslucentRenderer;
import com.io7m.r2.rendering.translucent.api.R2TranslucentRendererType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironment;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.textures.R2TextureDefaults;
import com.io7m.r2.textures.R2TextureDefaultsType;
import com.io7m.r2.unit_cubes.R2UnitCubeType;
import com.io7m.r2.unit_quads.R2UnitQuadType;
import com.io7m.r2.unit_spheres.R2UnitSphereType;
import com.io7m.smfj.format.binary.SMFFormatBinary;
import com.io7m.sombrero.core.SoShaderPreprocessorConfig;
import com.io7m.sombrero.core.SoShaderPreprocessorType;
import com.io7m.sombrero.core.SoShaderResolverType;
import com.io7m.sombrero.jcpp.SoShaderPreprocessorJCPP;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.OptionalInt;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User-friendly frontend.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2FacadeType extends R2DeletableType
{
  /**
   * @return The GL33 interface used for rendering operations
   */

  @Value.Parameter
  JCGLInterfaceGL33Type rendererGL33();

  /**
   * The executor service used to perform background I/O operations. The
   * default executor uses fixed thread pool containing a single low-priority
   * thread.
   *
   * @return An executor service used for background I/O operations
   */

  @Value.Default
  default ExecutorService executorForIO()
  {
    return Executors.newFixedThreadPool(1, r -> {
      final Thread th = new Thread(r);
      th.setName("com.io7m.r2.io:" + th.getId());
      th.setDaemon(true);
      th.setPriority(Thread.MIN_PRIORITY);
      return th;
    });
  }

  @Override
  default boolean isDeleted()
  {
    return this.debugVisualizerRenderer().isDeleted();
  }

  @Override
  default void delete(
    final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    final Collection<R2DeletableType> deletables = new ArrayList<>(16);
    deletables.add(this.debugVisualizerRenderer());
    deletables.add(this.depthRenderer());
    deletables.add(R2DeletableType.wrap(g33 -> this.depthVarianceBufferPool().delete(
      this.textureUnitAllocator().rootContext())));
    deletables.add(this.depthVarianceRenderer());
    deletables.add(this.geometryRenderer());
    deletables.add(this.lightRenderer());
    deletables.add(this.maskRenderer());
    deletables.add(this.shadowMapRenderer());
    deletables.add(this.stencilRenderer());
    deletables.add(this.textureDefaults());
    deletables.add(this.translucentRenderer());
    deletables.add(this.unitQuad());
    deletables.add(this.unitCube());
    deletables.add(this.unitSphere8());

    final AtomicReference<R2Exception> ref = new AtomicReference<>();
    deletables.forEach(d -> {
      try {
        d.delete(g);
      } catch (final R2Exception e) {
        ref.getAndUpdate(ex -> {
          if (ex == null) {
            return e;
          }
          ex.addSuppressed(e);
          return ex;
        });
      }
    });

    this.executorForIO().shutdown();
    try {
      this.executorForIO().awaitTermination(3L, TimeUnit.SECONDS);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    final R2Exception result = ref.get();
    if (result != null) {
      throw result;
    }
  }

  /**
   * @return The identifier pool
   */

  @Value.Default
  default R2IDPoolType idPool()
  {
    return R2IDPool.newPool();
  }

  /**
   * @return The shader resolver
   */

  @Value.Parameter
  SoShaderResolverType shaderResolver();

  /**
   * @return The shader preprocessor
   */

  @Value.Default
  default SoShaderPreprocessorType shaderPreprocessor()
  {
    final SoShaderPreprocessorConfig.Builder b =
      SoShaderPreprocessorConfig.builder();
    b.setResolver(this.shaderResolver());
    b.setVersion(OptionalInt.of(330));
    return SoShaderPreprocessorJCPP.create(b.build());
  }

  /**
   * @return The shader preprocessing environment
   */

  @Value.Default
  default R2ShaderPreprocessingEnvironmentType shaderPreprocessingEnvironment()
  {
    return R2ShaderPreprocessingEnvironment.create(this.shaderPreprocessor());
  }

  /**
   * @return A stencil renderer
   */

  @Value.Default
  default R2StencilRendererType stencilRenderer()
  {
    return R2StencilRenderer.create(
      this.shaderPreprocessingEnvironment(),
      this.rendererGL33(),
      this.idPool(),
      this.unitQuad());
  }

  /**
   * A mesh loader that loads meshes asynchronously. The default mesh loader
   * supports only SMF/B files.
   *
   * @return An asynchronous mesh loader
   */

  @Value.Default
  default R2MeshLoaderAsynchronousType meshLoaderAsynchronous()
  {
    return R2SMFMeshLoaderAsynchronous.create(
      new SMFFormatBinary(), this.executorForIO());
  }

  /**
   * A mesh loader that loads meshes synchronously. The default mesh loader
   * supports only SMF/B files.
   *
   * @return An asynchronous mesh loader
   */

  @Value.Default
  default R2MeshLoaderType meshLoader()
  {
    return R2SMFMeshLoaderSynchronous.create(new SMFFormatBinary());
  }

  /**
   * @return Access to matrices
   */

  @Value.Default
  default R2MatricesType matrices()
  {
    return R2Matrices.create();
  }

  /**
   * @return The texture unit allocator
   */

  @Value.Default
  default JCGLTextureUnitAllocatorType textureUnitAllocator()
  {
    return JCGLTextureUnitAllocator.newAllocatorWithStack(
      32, this.rendererGL33().textures().textureGetUnits());
  }

  /**
   * @return The set of default textures
   */

  @Value.Default
  default R2TextureDefaultsType textureDefaults()
  {
    return R2TextureDefaults.create(
      this.rendererGL33().textures(),
      this.textureUnitAllocator().rootContext());
  }

  /**
   * @return A geometry renderer
   */

  @Value.Default
  default R2GeometryRendererType geometryRenderer()
  {
    return R2GeometryRenderer.create(this.rendererGL33());
  }

  /**
   * @return A light renderer
   */

  @Value.Default
  default R2LightRendererType lightRenderer()
  {
    return R2LightRenderer.create(
      this.rendererGL33(), this.textureDefaults(),
      this.shaderPreprocessingEnvironment(),
      this.idPool(),
      this.unitQuad());
  }

  /**
   * @return A default unit quad
   */

  @Value.Default
  default R2UnitQuadType unitQuad()
  {
    return R2UnitQuad.newUnitQuad(this.rendererGL33());
  }

  /**
   * @return A default unit cube
   */

  @Value.Default
  default R2UnitCubeType unitCube()
  {
    return R2UnitCube.newUnitCube(this.meshLoader(), this.rendererGL33());
  }

  /**
   * @return A default unit sphere
   */

  @Value.Default
  default R2UnitSphereType unitSphere8()
  {
    return R2UnitSphere.newUnitSphere8(this.meshLoader(), this.rendererGL33());
  }

  /**
   * @return A renderer for visualizing debug info
   */

  @Value.Default
  default R2DebugVisualizerRendererType debugVisualizerRenderer()
  {
    return R2DebugVisualizerRenderer.create(
      this.rendererGL33(),
      this.shaderPreprocessingEnvironment(),
      this.idPool());
  }

  /**
   * @return A depth renderer
   */

  @Value.Default
  default R2DepthRendererType depthRenderer()
  {
    return R2DepthOnlyRenderer.create(this.rendererGL33());
  }

  /**
   * @return A depth variance renderer
   */

  @Value.Default
  default R2DepthVarianceRendererType depthVarianceRenderer()
  {
    return R2DepthVarianceRenderer.create(this.rendererGL33());
  }

  /**
   * @return A shadow map renderer
   */

  @Value.Default
  default R2ShadowMapRendererType shadowMapRenderer()
  {
    return R2ShadowMapRenderer.newRenderer(
      this.rendererGL33(),
      this.depthVarianceRenderer(),
      this.depthVarianceBufferPool());
  }

  /**
   * Specify the default depth variance buffer pool size. This is the size
   * in octets at which the implementation will attempt to keep the pool of
   * depth variance buffers for shadow map rendering. Setting this to {@code 0}
   * will result in no caching, with each shadow map being freshly reallocated
   * each time.
   *
   * @return The default depth variance buffer pool size
   */

  @Value.Default
  default long depthVarianceBufferPoolSteadySize()
  {
    // 8 512x512 shadow maps
    return 8L * (512L * 512L * 8L);
  }

  /**
   * Specify the default depth variance buffer pool hard limit size. This is the
   * absolute maximum size in octets that the implementation will use for depth
   * variance buffers.
   *
   * @return The default depth variance buffer pool maximum size
   */

  @Value.Default
  default long depthVarianceBufferPoolMaximumSize()
  {
    return Long.MAX_VALUE;
  }

  /**
   * @return The default depth variance buffer pool for shadow map rendering
   */

  @Value.Default
  default R2RenderTargetPoolType<R2DepthVarianceBufferDescription, R2DepthVarianceBufferUsableType>
  depthVarianceBufferPool()
  {
    return R2DepthVarianceBufferPool.newPool(
      this.rendererGL33(),
      this.depthVarianceBufferPoolSteadySize(),
      this.depthVarianceBufferPoolMaximumSize());
  }

  /**
   * @return A profiling interface
   */

  @Value.Default
  default JCGLProfilingType profiling()
  {
    return JCGLProfiling.newProfiling(this.rendererGL33().timers());
  }

  /**
   * @return A buffer provider
   */

  @Value.Default
  default R2FacadeBufferProviderType buffers()
  {
    return R2FacadeBufferProvider.of(this);
  }

  /**
   * @return A filter provider
   */

  @Value.Default
  default R2FacadeFilterProviderType filters()
  {
    return R2FacadeFilterProvider.of(this);
  }

  /**
   * @return An instance provider
   */

  @Value.Default
  default R2FacadeInstanceProviderType instances()
  {
    return R2FacadeInstanceProvider.of(this);
  }

  /**
   * @return A pool provider
   */

  @Value.Default
  default R2FacadePoolProviderType pools()
  {
    return R2FacadePoolProvider.of(this);
  }

  /**
   * @return A light shader provider
   */

  @Value.Default
  default R2FacadeLightShaderProviderType lightShaders()
  {
    return R2FacadeLightShaderProvider.of(this);
  }

  /**
   * @return A depth shader provider
   */

  @Value.Default
  default R2FacadeDepthShaderProviderType depthShaders()
  {
    return R2FacadeDepthShaderProvider.of(this);
  }

  /**
   * @return A geometry shader provider
   */

  @Value.Default
  default R2FacadeGeometryShaderProviderType geometryShaders()
  {
    return R2FacadeGeometryShaderProvider.of(this);
  }

  /**
   * @return A translucent instance shader provider
   */

  @Value.Default
  default R2FacadeTranslucentInstanceShaderProviderType instanceTranslucentShaders()
  {
    return R2FacadeTranslucentInstanceShaderProvider.of(this);
  }

  /**
   * @return A light provider
   */

  @Value.Default
  default R2FacadeLightProviderType lights()
  {
    return R2FacadeLightProvider.of(this);
  }

  /**
   * @return A texture provider
   */

  @Value.Default
  default R2FacadeTextureProviderType textures()
  {
    return R2FacadeTextureProvider.of(this);
  }

  /**
   * @return A translucent instance renderer
   */

  @Value.Default
  default R2TranslucentRendererType translucentRenderer()
  {
    return R2TranslucentRenderer.newRenderer(this.rendererGL33());
  }

  /**
   * @return A mask renderer
   */

  @Value.Default
  default R2MaskRendererType maskRenderer()
  {
    return R2MaskRenderer.create(
      this.rendererGL33(),
      this.shaderPreprocessingEnvironment(),
      this.idPool());
  }
}
