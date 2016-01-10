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

import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;

/**
 * <p>The type of batched instances.</p>
 *
 * <p>A batch consists of a set of one or more vertex buffers containing mesh
 * data, and a vertex buffer containing a set of transforms - one per rendered
 * object.</p>
 *
 * <p>The primary use case for batched instances is rendering a lot of copies of
 * the same mesh in a single draw call.</p>
 */

public interface R2InstanceBatchedType extends R2InstanceType
{
  /**
   * @return The instance array object
   */

  JCGLArrayObjectType getArrayObject();

  /**
   * Update any data required for rendering on the GPU.
   *
   * @param context The transform context
   * @param g       An OpenGL interface
   */

  void update(
    JCGLInterfaceGL33Type g,
    R2TransformContextType context);

  /**
   * @return The number of instances that will be rendered.
   */

  int getRenderCount();
}
