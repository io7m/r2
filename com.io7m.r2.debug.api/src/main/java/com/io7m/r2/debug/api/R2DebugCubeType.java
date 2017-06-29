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

package com.io7m.r2.debug.api;

import com.io7m.r2.core.api.deletable.R2DeletableType;

/**
 * <p>The type of debug cubes, from {@code (-0.5, -0.5, -0.5)} to {@code (0.5,
 * 0.5, 0.5)}.</p>
 *
 * <p>Debug cubes are implicitly assumed to be rendered as {@link
 * com.io7m.jcanephora.core.JCGLPrimitives#PRIMITIVE_LINES} and therefore the
 * associated index buffer is populated with a set of line segments.</p>
 */

public interface R2DebugCubeType extends R2DebugCubeUsableType, R2DeletableType
{
  // No extra methods
}
