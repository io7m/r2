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

/**
 * <p>The type of batched instances.</p>
 *
 * <p>A batch consists of a set of one or more vertex buffers containing mesh
 * data, and a vertex buffer containing a set of transforms - one per rendered
 * object.</p>
 *
 * <p>The primary use case for batched instances is rendering a lot of copies of
 * the same mesh in a single draw call.</p>
 *
 * <p>Batched instances may only use orthogonal transforms, to avoid having to
 * perform a costly matrix inversion on the GPU (to produce a per-instance
 * normal matrix).</p>
 *
 * @param <T> The precise type of transform used for each instance
 */

public interface R2InstanceBatchedType<T extends
  R2TransformOrthogonalReadableType>
{

}
