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

import com.io7m.r2.core.R2AmbientOcclusionBufferDescription;
import com.io7m.r2.core.R2AmbientOcclusionBufferUsableType;
import com.io7m.r2.core.R2FilterType;
import com.io7m.r2.core.R2FilterUsableType;
import com.io7m.r2.core.R2ImageBufferDescription;
import com.io7m.r2.core.R2ImageBufferUsableType;
import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.core.R2RenderTargetDescriptionType;
import com.io7m.r2.core.R2RenderTargetPoolUsableType;
import com.io7m.r2.core.R2RenderTargetUsableType;
import com.io7m.r2.filters.R2FilterBilateralBlurDepthAware;
import com.io7m.r2.filters.R2FilterBilateralBlurDepthAwareParameters;
import com.io7m.r2.filters.R2FilterBoxBlur;
import com.io7m.r2.filters.R2FilterBoxBlurParameters;
import com.io7m.r2.filters.R2FilterCompositor;
import com.io7m.r2.filters.R2FilterEmission;
import com.io7m.r2.filters.R2FilterEmissionType;
import com.io7m.r2.filters.R2FilterFXAA;
import com.io7m.r2.filters.R2FilterFXAAType;
import com.io7m.r2.filters.R2FilterFogDepth;
import com.io7m.r2.filters.R2FilterLightApplicator;
import com.io7m.r2.filters.R2FilterOcclusionApplicator;
import com.io7m.r2.filters.R2FilterSSAO;
import org.immutables.value.Value;

/**
 * The type of convenient filter providers.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2FacadeFilterProviderType
{
  /**
   * @return The current facade
   */

  @Value.Auxiliary
  @Value.Parameter
  R2FacadeType main();

  /**
   * @return A new FXAA filter
   */

  default R2FilterFXAAType createFXAA()
  {
    return R2FilterFXAA.newFilter(
      this.main().rendererGL33(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool(),
      this.main().unitQuad());
  }

  /**
   * Create a depth-aware bilateral blur.
   *
   * @param image_pool A pool used to store intermediate images
   * @param <D>        The type of render target descriptions
   * @param <S>        The type of render targets
   *
   * @return A new bilateral blur filter
   */

  default <D extends R2RenderTargetDescriptionType,
    S extends R2RenderTargetUsableType<D>>
  R2FilterType<R2FilterBilateralBlurDepthAwareParameters<D, S, D, S>>
  createBilateralBlur(
    final R2RenderTargetPoolUsableType<D, S> image_pool)
  {
    return R2FilterBilateralBlurDepthAware.newFilter(
      this.main().shaderPreprocessingEnvironment(),
      this.main().rendererGL33(),
      this.main().textureDefaults(),
      image_pool,
      this.main().idPool(),
      this.main().unitQuad());
  }

  /**
   * Create a depth-aware bilateral blur for RGBA images.
   *
   * @param image_pool A pool used to store intermediate images
   *
   * @return A new bilateral blur filter
   */

  default R2FilterType<
    R2FilterBilateralBlurDepthAwareParameters<
      R2ImageBufferDescription,
      R2ImageBufferUsableType,
      R2ImageBufferDescription,
      R2ImageBufferUsableType>> createRGBABilateralBlur(
    final R2RenderTargetPoolUsableType<R2ImageBufferDescription, R2ImageBufferUsableType> image_pool)
  {
    return this.createBilateralBlur(image_pool);
  }

  /**
   * Create a depth-aware bilateral blur for ambient occlusion images.
   *
   * @param image_pool A pool used to store intermediate images
   *
   * @return A new bilateral blur filter
   */

  default R2FilterType<
    R2FilterBilateralBlurDepthAwareParameters<
      R2AmbientOcclusionBufferDescription,
      R2AmbientOcclusionBufferUsableType,
      R2AmbientOcclusionBufferDescription,
      R2AmbientOcclusionBufferUsableType>> createSSAOBilateralBlur(
    final R2RenderTargetPoolUsableType<R2AmbientOcclusionBufferDescription, R2AmbientOcclusionBufferUsableType> image_pool)
  {
    return this.createBilateralBlur(image_pool);
  }

  /**
   * Create a box blur filter.
   *
   * @param image_pool A pool used to store intermediate images
   * @param <D>        The type of render target descriptions
   * @param <S>        The type of render targets
   *
   * @return A new box blur filter
   */

  default <D extends R2RenderTargetDescriptionType,
    S extends R2RenderTargetUsableType<D>>
  R2FilterType<R2FilterBoxBlurParameters<D, S, D, S>> createBoxBlur(
    final R2RenderTargetPoolUsableType<D, S> image_pool)
  {
    return R2FilterBoxBlur.newFilter(
      this.main().shaderPreprocessingEnvironment(),
      this.main().rendererGL33(),
      this.main().textureDefaults(),
      image_pool,
      this.main().idPool(),
      this.main().unitQuad());
  }

  /**
   * Create a box blur filter for RGBA images.
   *
   * @param image_pool A pool used to store intermediate images
   *
   * @return A new box blur filter
   */

  default R2FilterType<
    R2FilterBoxBlurParameters<
      R2ImageBufferDescription,
      R2ImageBufferUsableType,
      R2ImageBufferDescription,
      R2ImageBufferUsableType>> createRGBABoxBlur(
    final R2RenderTargetPoolUsableType<R2ImageBufferDescription, R2ImageBufferUsableType> image_pool)
  {
    return this.createBoxBlur(image_pool);
  }

  /**
   * Create a box blur filter for ambient occlusion images.
   *
   * @param image_pool A pool used to store intermediate images
   *
   * @return A new box blur filter
   */

  default R2FilterType<
    R2FilterBoxBlurParameters<
      R2AmbientOcclusionBufferDescription,
      R2AmbientOcclusionBufferUsableType,
      R2AmbientOcclusionBufferDescription,
      R2AmbientOcclusionBufferUsableType>> createSSAOBoxBlur(
    final R2RenderTargetPoolUsableType<R2AmbientOcclusionBufferDescription, R2AmbientOcclusionBufferUsableType> image_pool)
  {
    return this.createBoxBlur(image_pool);
  }

  /**
   * Create a new emission filter.
   *
   * @param image_pool  A pool used to store intermediate images
   * @param filter_blur A filter used to blur emission values
   *
   * @return A new emission filter
   */

  default R2FilterEmissionType createEmission(
    final R2RenderTargetPoolUsableType<R2ImageBufferDescription, R2ImageBufferUsableType> image_pool,
    final R2FilterUsableType<
      R2FilterBoxBlurParameters<
        R2ImageBufferDescription,
        R2ImageBufferUsableType,
        R2ImageBufferDescription,
        R2ImageBufferUsableType>> filter_blur)
  {
    return R2FilterEmission.newFilter(
      this.main().rendererGL33(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool(),
      filter_blur,
      image_pool,
      this.main().unitQuad());
  }

  /**
   * @return A new light applicator filter
   */

  default R2FilterLightApplicator createLightApplicator()
  {
    return R2FilterLightApplicator.newFilter(
      this.main().shaderPreprocessingEnvironment(),
      this.main().rendererGL33(),
      this.main().idPool(),
      this.main().unitQuad());
  }

  /**
   * @return A new screen-space ambient occlusion filter
   */

  default R2FilterSSAO createSSAO()
  {
    return R2FilterSSAO.newFilter(
      this.main().shaderPreprocessingEnvironment(),
      this.main().rendererGL33(),
      this.main().idPool(),
      this.main().unitQuad());
  }

  /**
   * @return A new ambient occlusion applicator filter
   */

  default R2FilterOcclusionApplicator createOcclusionApplicator()
  {
    return R2FilterOcclusionApplicator.newFilter(
      this.main().shaderPreprocessingEnvironment(),
      this.main().textureDefaults(),
      this.main().rendererGL33(),
      this.main().idPool(),
      this.main().unitQuad());
  }

  /**
   * @return A new compositor filter
   */

  default R2FilterCompositor createCompositor()
  {
    return R2FilterCompositor.newFilter(
      this.main().shaderPreprocessingEnvironment(),
      this.main().textureDefaults(),
      this.main().rendererGL33(),
      this.main().idPool(),
      this.main().unitQuad());
  }

  /**
   * @return A new depth-based fog filter
   */

  default R2FilterFogDepth createFogDepth()
  {
    return R2FilterFogDepth.newFilter(
      this.main().rendererGL33(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool(),
      this.main().unitQuad());
  }
}
