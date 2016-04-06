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

package com.io7m.r2.filters;

import com.io7m.jareas.core.AreaInclusiveUnsignedLType;
import com.io7m.jcanephora.core.JCGLFramebufferBlitFilter;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.core.R2RenderTargetDescriptionType;
import com.io7m.r2.core.R2RenderTargetPoolUsableType;
import com.io7m.r2.core.R2RenderTargetUsableType;
import com.io7m.r2.core.R2Texture2DUsableType;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <p>Parameters for box blur filters that blur render targets of type {@code S}
 * and write the blurred results to render targets of type {@code T}.</p>
 *
 * @param <SD> The type of source render target descriptions
 * @param <S>  The type of source render targets
 * @param <DD> The type of destination render target descriptions
 * @param <D>  The type of destination render targets
 */

public final class R2FilterBoxBlurParameters<
  SD extends R2RenderTargetDescriptionType,
  S extends R2RenderTargetUsableType<SD>,
  DD extends R2RenderTargetDescriptionType,
  D extends R2RenderTargetUsableType<DD>> implements
  R2BlurParametersReadableType,
  R2BlurParametersWritableType
{
  private BiFunction<DD, AreaInclusiveUnsignedLType, DD>
    output_desc_scaler;

  private Function<S, R2Texture2DUsableType> source_selector;
  private Function<D, R2Texture2DUsableType> output_selector;
  private S source_buffer;
  private D output_buffer;
  private R2RenderTargetPoolUsableType<DD, D> render_target_pool;
  private float blur_size;
  private float blur_scale;
  private int blur_passes;
  private JCGLFramebufferBlitFilter blur_scale_filter;

  private R2FilterBoxBlurParameters(
    final S in_source_buffer,
    final D in_output_buffer,
    final Function<S, R2Texture2DUsableType> in_source_selector,
    final Function<D, R2Texture2DUsableType> in_output_selector,
    final BiFunction<DD, AreaInclusiveUnsignedLType, DD> in_output_desc_scaler,
    final R2RenderTargetPoolUsableType<DD, D> rtp)
  {
    this.source_buffer =
      NullCheck.notNull(in_source_buffer);
    this.source_selector =
      NullCheck.notNull(in_source_selector);

    this.output_buffer =
      NullCheck.notNull(in_output_buffer);
    this.output_selector =
      NullCheck.notNull(in_output_selector);
    this.output_desc_scaler =
      NullCheck.notNull(in_output_desc_scaler);

    this.render_target_pool = NullCheck.notNull(rtp);

    this.blur_size = 1.0f;
    this.blur_scale = 0.5f;
    this.blur_passes = 1;
    this.blur_scale_filter =
      JCGLFramebufferBlitFilter.FRAMEBUFFER_BLIT_FILTER_LINEAR;
  }

  /**
   * Construct a new set of parameters.
   *
   * @param source                The source render target
   * @param in_source_selector    A function that, given a render target of type
   *                              {@code S}, selects which texture on that
   *                              render target will be used for blurring.
   * @param output                The output render target
   * @param in_output_selector    A function that, given a render target of type
   *                              {@code D}, selects which texture on that
   *                              render target will be used for blurring.
   * @param in_output_desc_scaler A function that, given an existing description
   *                              {@code D} and an area {@code A}, returns a new
   *                              description with the values of {@code D} and
   *                              the area {@code A}
   * @param r                     A pool used to construct temporary render
   *                              targets
   * @param <SD>                  The type of source render target descriptions
   * @param <S>                   The type of source render targets
   * @param <DD>                  The type of destination render target
   *                              descriptions
   * @param <D>                   The type of destination render targets
   *
   * @return A new set of parameters
   */

  public static <
    SD extends R2RenderTargetDescriptionType,
    S extends R2RenderTargetUsableType<SD>,
    DD extends R2RenderTargetDescriptionType,
    D extends R2RenderTargetUsableType<DD>> R2FilterBoxBlurParameters<SD, S,
    DD, D>
  newParameters(
    final S source,
    final Function<S, R2Texture2DUsableType> in_source_selector,
    final D output,
    final Function<D, R2Texture2DUsableType> in_output_selector,
    final BiFunction<DD, AreaInclusiveUnsignedLType, DD> in_output_desc_scaler,
    final R2RenderTargetPoolUsableType<DD, D> r)
  {
    NullCheck.notNull(source);
    NullCheck.notNull(output);
    NullCheck.notNull(r);

    return new R2FilterBoxBlurParameters<>(
      source,
      output,
      in_source_selector,
      in_output_selector,
      in_output_desc_scaler,
      r);
  }

  /**
   * @return The texture selector function for render targets of type {@code S}
   */

  public Function<S, R2Texture2DUsableType> getSourceTextureSelector()
  {
    return this.source_selector;
  }

  /**
   * Set the texture selector function for render targets of type {@code S}.
   *
   * @param f The texture selector function
   */

  public void setSourceTextureSelector(
    final Function<S, R2Texture2DUsableType> f)
  {
    this.source_selector = NullCheck.notNull(f);
  }

  /**
   * @return The texture selector function for render targets of type {@code D}
   */

  public Function<D, R2Texture2DUsableType> getOutputTextureSelector()
  {
    return this.output_selector;
  }

  /**
   * Set the texture selector function for render targets of type {@code D}.
   *
   * @param f The texture selector function
   */

  public void setOutputTextureSelector(
    final Function<D, R2Texture2DUsableType> f)
  {
    this.output_selector = NullCheck.notNull(f);
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

  @Override
  public float getBlurSize()
  {
    return this.blur_size;
  }

  @Override
  public void setBlurSize(final float s)
  {
    this.blur_size = Math.max(0.0f, s);
  }

  @Override
  public float getBlurScale()
  {
    return this.blur_scale;
  }

  @Override
  public void setBlurScale(final float s)
  {
    this.blur_scale = Math.max(0.001f, s);
  }

  @Override
  public int getBlurPasses()
  {
    return this.blur_passes;
  }

  @Override
  public void setBlurPasses(final int p)
  {
    this.blur_passes = Math.max(0, p);
  }

  @Override
  public JCGLFramebufferBlitFilter getBlurScaleFilter()
  {
    return this.blur_scale_filter;
  }

  @Override
  public void setBlurScaleFilter(final JCGLFramebufferBlitFilter f)
  {
    this.blur_scale_filter = NullCheck.notNull(f);
  }

  /**
   * @return The output description scaling function
   */

  public BiFunction<DD, AreaInclusiveUnsignedLType, DD>
  getOutputDescriptionScaler()
  {
    return this.output_desc_scaler;
  }

  /**
   * Set the output description scaling function.
   *
   * @param f A scaling function
   */

  public void setOutputDescriptionScaler(
    final BiFunction<DD, AreaInclusiveUnsignedLType, DD> f)
  {
    this.output_desc_scaler = NullCheck.notNull(f);
  }
}
