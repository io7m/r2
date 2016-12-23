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

package com.io7m.r2.core.shaders.types;

import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;

/**
 * The type of usable shaders for implementing full-screen filters.
 *
 * @param <M> The type of parameter data
 */

public interface R2ShaderFilterUsableType<M> extends R2ShaderScreenUsableType<M>
{
  /**
   * <p>Set filter values.</p>
   *
   * <p>This method will be called exactly once between calls to {@link
   * R2ShaderUsableType#onActivate(JCGLInterfaceGL33Type)} and {@link
   * #onValidate()}.</p>
   *
   * @param g          A texture interface
   * @param parameters The current filter parameters
   */

  void onReceiveFilterValues(
    JCGLInterfaceGL33Type g,
    R2ShaderParametersFilterType<M> parameters);
}
