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

package com.io7m.r2.core.filters;

import com.io7m.jcanephora.core.JCGLFramebufferBlitFilter;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.r2.core.R2MatricesObserverType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import com.io7m.r2.core.R2RenderTargetDescriptionType;
import com.io7m.r2.core.R2RenderTargetPoolUsableType;
import com.io7m.r2.core.R2RenderTargetUsableType;
import com.io7m.r2.core.R2Texture2DUsableType;

import java.util.function.Function;

/**
 * <p>Parameters for bilateral blur filters that  blur render targets of type
 * {@code S} and write the blurred results to render targets of type {@code
 * T}.</p>
 *
 * @param <SD> The type of source render target descriptions
 * @param <S>  The type of source render targets
 * @param <DD> The type of destination render target descriptions
 * @param <D>  The type of destination render targets
 */

public final class R2FilterBilateralBlurDepthAwareParameters<
  SD extends R2RenderTargetDescriptionType,
  S extends R2RenderTargetUsableType<SD>,
  DD extends R2RenderTargetDescriptionType,
  D extends R2RenderTargetUsableType<DD>>
{
  private           R2Texture2DUsableType               depth;
  private           Function<S, R2Texture2DUsableType>  source_value_selector;
  private           Function<D, R2Texture2DUsableType>  output_value_selector;
  private           S                                   source_buffer;
  private           D                                   output_buffer;
  private           R2RenderTargetPoolUsableType<DD, D> render_target_pool;
  private           float                               blur_sharpness;
  private           float                               blur_radius;
  private           float                               blur_scale;
  private           int                                 blur_passes;
  private           JCGLFramebufferBlitFilter           blur_scale_filter;
  private @Nullable R2MatricesObserverValuesType        matrices;

  private R2FilterBilateralBlurDepthAwareParameters(
    final S in_source_buffer,
    final Function<S, R2Texture2DUsableType> in_source_value_selector,
    final R2Texture2DUsableType in_depth,
    final D in_output_buffer,
    final Function<D, R2Texture2DUsableType> in_output_value_selector,
    final R2RenderTargetPoolUsableType<DD, D> rtp)
  {
    this.source_buffer = NullCheck.notNull(in_source_buffer);
    this.source_value_selector = NullCheck.notNull(in_source_value_selector);
    this.depth = NullCheck.notNull(in_depth);
    this.output_buffer = NullCheck.notNull(in_output_buffer);
    this.output_value_selector = NullCheck.notNull(in_output_value_selector);
    this.render_target_pool = NullCheck.notNull(rtp);

    this.blur_radius = 1.0f;
    this.blur_scale = 1.0f;
    this.blur_passes = 1;
    this.blur_sharpness = 16.0f;
    this.blur_scale_filter =
      JCGLFramebufferBlitFilter.FRAMEBUFFER_BLIT_FILTER_LINEAR;
  }

  /**
   * Construct a new set of parameters.
   *
   * @param in_source_buffer         The source render target
   * @param in_source_value_selector A function that, given a render target of
   *                                 type {@code S}, selects which texture on
   *                                 that render target will be used as the
   *                                 source of data for blurring.
   * @param in_depth                 The depth texture that will be used to
   *                                 control how the blur effect is applied
   * @param in_output_buffer         The output render target
   * @param in_output_value_selector A function that, given a render target of
   *                                 type {@code T}, selects which texture on
   *                                 that render target will be used for as the
   *                                 source of data for blurring.
   * @param r                        A pool used to construct temporary render
   *                                 targets
   * @param <SD>                     The type of source render target
   *                                 descriptions
   * @param <S>                      The type of source render targets
   * @param <DD>                     The type of destination render target
   *                                 descriptions
   * @param <D>                      The type of destination render targets
   *
   * @return A new set of parameters
   */

  public static <
    SD extends R2RenderTargetDescriptionType,
    S extends R2RenderTargetUsableType<SD>,
    DD extends R2RenderTargetDescriptionType,
    D extends R2RenderTargetUsableType<DD>>
  R2FilterBilateralBlurDepthAwareParameters<SD, S, DD, D>
  newParameters(
    final S in_source_buffer,
    final Function<S, R2Texture2DUsableType> in_source_value_selector,
    final R2Texture2DUsableType in_depth,
    final D in_output_buffer,
    final Function<D, R2Texture2DUsableType> in_output_value_selector,
    final R2RenderTargetPoolUsableType<DD, D> r)
  {
    NullCheck.notNull(in_source_buffer);
    NullCheck.notNull(in_output_buffer);
    NullCheck.notNull(r);
    return new R2FilterBilateralBlurDepthAwareParameters<>(
      in_source_buffer,
      in_source_value_selector,
      in_depth,
      in_output_buffer,
      in_output_value_selector,
      r);
  }

  /**
   * @return The current blur sharpness amount
   */

  public float getBlurSharpness()
  {
    return this.blur_sharpness;
  }

  /**
   * Set the blur sharpness amount.
   *
   * @param s The sharpness
   */

  public void setBlurSharpness(final float s)
  {
    this.blur_sharpness = Math.max(0.1f, s);
  }

  /**
   * @return The observer values that were used to produce the scene being
   * blurred
   */

  public R2MatricesObserverValuesType getSceneObserverValues()
  {
    if (this.matrices == null) {
      throw new IllegalStateException("Scene observer values not set");
    }
    return this.matrices;
  }

  /**
   * Set the observer values that were used to produce the original scene.
   *
   * @param m The observer values
   */

  public void setSceneObserverValues(final R2MatricesObserverType m)
  {
    this.matrices = NullCheck.notNull(m);
  }


  /**
   * @return The depth texture that will be used to control how the blur effect
   * is applied
   */

  public R2Texture2DUsableType getDepthTexture()
  {
    return this.depth;
  }

  /**
   * Set the depth texture that will be used to control how the blur effect is
   * applied.
   *
   * @param d The texture
   */

  public void setDepthTexture(final R2Texture2DUsableType d)
  {
    this.depth = NullCheck.notNull(d);
  }

  /**
   * @return The texture selector function for render targets of type {@code S}
   */

  public Function<S, R2Texture2DUsableType> getSourceValueTextureSelector()
  {
    return this.source_value_selector;
  }

  /**
   * Set the texture selector function for render targets of type {@code S}.
   *
   * @param f The texture selector function
   */

  public void setSourceValueTextureSelector(
    final Function<S, R2Texture2DUsableType> f)
  {
    this.source_value_selector = NullCheck.notNull(f);
  }

  /**
   * @return The texture selector function for render targets of type {@code D}
   */

  public Function<D, R2Texture2DUsableType> getOutputValueTextureSelector()
  {
    return this.output_value_selector;
  }

  /**
   * Set the texture selector function for render targets of type {@code D}.
   *
   * @param f The texture selector function
   */

  public void setOutputValueTextureSelector(
    final Function<D, R2Texture2DUsableType> f)
  {
    this.output_value_selector = NullCheck.notNull(f);
  }

  /**
   * @return The render target pool
   */

  public R2RenderTargetPoolUsableType<DD, D> getRenderTargetPool()
  {
    return this.render_target_pool;
  }

  /**
   * Set the render target pool.
   *
   * @param rtp The pool
   */

  public void setRenderTargetPool(
    final R2RenderTargetPoolUsableType<DD, D> rtp)
  {
    this.render_target_pool = NullCheck.notNull(rtp);
  }

  /**
   * @return The current source render target
   */

  public S getSourceRenderTarget()
  {
    return this.source_buffer;
  }

  /**
   * Set the source render target. Pixels will be sampled from this render
   * target during the blur operation.
   *
   * @param b The render target
   */

  public void setSourceRenderTarget(final S b)
  {
    this.source_buffer = NullCheck.notNull(b);
  }

  /**
   * @return The current destination render target
   */

  public D getOutputRenderTarget()
  {
    return this.output_buffer;
  }

  /**
   * Set the destination render target. The blurred image will be written to
   * this render target.
   *
   * @param b The render target
   */

  public void setOutputRenderTarget(final D b)
  {
    this.output_buffer = NullCheck.notNull(b);
  }

  /**
   * @return The current blur radius in texels
   */

  public float getBlurRadius()
  {
    return this.blur_radius;
  }

  /**
   * Set the blur radius in texels.
   *
   * @param s The radius
   */

  public void setBlurRadius(final float s)
  {
    this.blur_radius = Math.max(0.01f, s);
  }

  /**
   * @return The scale value for intermediate images
   */

  public float getBlurScale()
  {
    return this.blur_scale;
  }

  /**
   * Set the amount by which the image will be scaled during the blur operation.
   * By scaling an image down, blurring it, and then scaling it back up again,
   * the blur effect is emphasized without requiring additional passes.
   *
   * @param s The scale amount
   */

  public void setBlurScale(final float s)
  {
    this.blur_scale = Math.max(0.001f, s);
  }

  /**
   * @return The number of blur passes that will be used
   */

  public int getBlurPasses()
  {
    return this.blur_passes;
  }

  /**
   * Set the number of blur passes that will be used. If a value of {@code 0} is
   * given here, the image will only be scaled and not actually blurred (and
   * will not actually even be scaled, if a value of {@code 1.0} is given for
   * {@link #setBlurScale(float)}).
   *
   * @param p The number of blur passes
   */

  public void setBlurPasses(final int p)
  {
    this.blur_passes = Math.max(1, p);
  }

  /**
   * @return The filter that will be used when an image is scaled via
   * framebuffer blitting
   */

  public JCGLFramebufferBlitFilter getBlurScaleFilter()
  {
    return this.blur_scale_filter;
  }

  /**
   * Set the filter that will be used when an image is resized using framebuffer
   * blitting.
   *
   * @param f The filter
   */

  public void setBlurScaleFilter(final JCGLFramebufferBlitFilter f)
  {
    this.blur_scale_filter = NullCheck.notNull(f);
  }

}
