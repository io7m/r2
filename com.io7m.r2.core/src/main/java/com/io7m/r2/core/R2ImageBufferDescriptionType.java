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

package com.io7m.r2.core;

import com.io7m.jregions.core.unparameterized.sizes.AreaSizeL;
import com.io7m.r2.annotations.R2ImmutableStyleType;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * The type of image buffer descriptions.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2ImageBufferDescriptionType
  extends R2RenderTargetDescriptionType
{
  @Override
  @Value.Parameter
  AreaSizeL area();

  /**
   * A specification of whether a new depth attachment should be created,
   * a depth attachment should be shared with an existing framebuffer, or
   * no depth attachment should exist at all.
   *
   * @return The specification of the attachment, if one is to be provided
   */

  @Value.Parameter
  Optional<R2DepthAttachmentSpecificationType> depthAttachment();
}
