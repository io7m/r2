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

package com.io7m.r2.images.api;

import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.textures.R2Texture2DUsableType;
import org.immutables.value.Value;

import java.util.function.BiFunction;

/**
 * A preexisting depth texture that will be used as an attachment on a created
 * framebuffer.
 */

@R2ImmutableStyleType
@Value.Immutable
public interface R2DepthAttachmentShareType extends
  R2DepthAttachmentSpecificationType
{
  @Override
  default <A, B> B matchDepthAttachment(
    final A context,
    final BiFunction<A, R2DepthAttachmentShareType, B> on_share,
    final BiFunction<A, R2DepthAttachmentCreateType, B> on_create,
    final BiFunction<A, R2DepthAttachmentCreateWithStencilType, B> on_create_stencil)
  {
    return on_share.apply(context, this);
  }

  /**
   * The depth texture that will be used as a depth attachment.
   *
   * @return A depth texture
   */

  @Value.Parameter
  R2Texture2DUsableType texture();
}
