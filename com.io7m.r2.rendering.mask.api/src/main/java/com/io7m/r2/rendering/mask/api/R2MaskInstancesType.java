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

package com.io7m.r2.rendering.mask.api;

import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.instances.R2InstanceBatchedType;
import com.io7m.r2.instances.R2InstanceSingleType;
import org.immutables.value.Value;

import java.util.List;

/**
 * The set of mask instances that will be rendered.
 */

@R2ImmutableStyleType
@Value.Immutable
public interface R2MaskInstancesType
{
  /**
   * @return The list of single instances that will be rendered
   */

  List<R2InstanceSingleType> singles();

  /**
   * @return The list of batched instances that will be rendered
   */

  List<R2InstanceBatchedType> batched();
}
