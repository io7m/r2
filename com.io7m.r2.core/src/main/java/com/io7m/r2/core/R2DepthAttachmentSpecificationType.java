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

import java.util.function.BiFunction;

/**
 * A specification of a whether a depth attachment should be created or
 * shared with an existing framebuffer.
 */

public interface R2DepthAttachmentSpecificationType
{
  /**
   * Match on the type of depth attachment specification.
   *
   * @param context   A user-defined context value
   * @param on_share  Evaluated on values of type {@link R2DepthAttachmentShareType}
   * @param on_create Evaluated on values of type {@link R2DepthAttachmentCreateType}
   * @param <A>       The type of context values
   * @param <B>       The type of returned values
   *
   * @return The value returned by one of the given functions
   */

  <A, B> B matchDepthAttachment(
    A context,
    BiFunction<A, R2DepthAttachmentShareType, B> on_share,
    BiFunction<A, R2DepthAttachmentCreateType, B> on_create);
}
