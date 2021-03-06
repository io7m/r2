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

package com.io7m.r2.filters.box_blur.api;

import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.rendering.targets.R2RenderTargetDescriptionType;
import com.io7m.r2.rendering.targets.R2RenderTargetPoolUsableType;
import com.io7m.r2.rendering.targets.R2RenderTargetUsableType;
import com.io7m.r2.textures.R2Texture2DUsableType;
import org.immutables.value.Value;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <p>Parameters for box blur filters that blur render targets of type {@code S}
 * and write the blurred results to render targets of type {@code D}.</p>
 *
 * @param <SD> The type of source render target descriptions
 * @param <S>  The type of source render targets
 * @param <DD> The type of destination render target descriptions
 * @param <D>  The type of destination render targets
 */

@R2ImmutableStyleType
@Value.Immutable
public interface R2FilterBoxBlurParametersType<
  SD extends R2RenderTargetDescriptionType,
  S extends R2RenderTargetUsableType<SD>,
  DD extends R2RenderTargetDescriptionType,
  D extends R2RenderTargetUsableType<DD>>
{
  /**
   * @return The source render target
   */

  @Value.Parameter
  S sourceRenderTarget();

  /**
   * @return A function that, given a render target of type {@code S}, selects
   * which texture on that render target will be used for blurring.
   */

  @Value.Parameter
  Function<S, R2Texture2DUsableType> sourceTextureSelector();

  /**
   * @return The output render target
   */

  @Value.Parameter
  D outputRenderTarget();

  /**
   * @return A function that, given a render target of type {@code D}, selects
   * which texture on that render target will be used for blurring.
   */

  @Value.Parameter
  Function<D, R2Texture2DUsableType> outputTextureSelector();

  /**
   * @return A pool used to construct temporary render targets
   */

  @Value.Parameter
  R2RenderTargetPoolUsableType<DD, D> renderTargetPool();

  /**
   * @return The blur parameters
   */

  @Value.Parameter
  @Value.Default
  default R2BlurParameters blurParameters()
  {
    return R2BlurParameters.builder().build();
  }

  /**
   * @return A function that, given an existing description {@code D} and an
   * area {@code A}, returns a new description with the values of {@code D} and
   * the area {@code A}.
   */

  @Value.Parameter
  BiFunction<DD, AreaSizeL, DD> outputDescriptionScaler();
}
