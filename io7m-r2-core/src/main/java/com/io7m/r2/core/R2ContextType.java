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

import com.io7m.jcanephora.core.api.JCGLContextUsableType;
import net.jcip.annotations.NotThreadSafe;

/**
 * <p>A single rendering context.</p>
 *
 * <p>User code is expected to retrieve a value of type {@code R2ContextType}
 * once per frame and use it to perform rendering. The context has the same
 * threading semantics as OpenGL contexts: A given context {@code c} may be
 * current on at most one thread at any time.</p>
 */

@NotThreadSafe
public interface R2ContextType extends AutoCloseable
{
  /**
   * @return Access to the current OpenGL context
   */

  JCGLContextUsableType getContext();

  /**
   * @return Access to matrices for the current rendering operation
   */

  R2MatricesType getMatrices();

  /**
   * @return Access to the texture unit context for the current rendering
   * operation
   */

  R2TextureUnitContextParentType getTextureUnitContext();

  /**
   * Close the context.
   *
   * @throws R2Exception On errors
   */

  @Override
  void close()
    throws R2Exception;
}
