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

import com.io7m.jcanephora.core.JCGLTexture2DType;
import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jnull.NullCheck;

import java.util.function.BiFunction;

/**
 * A simple static texture.
 */

public final class R2Texture2DStatic implements R2Texture2DType
{
  private final JCGLTexture2DType texture;

  private R2Texture2DStatic(
    final JCGLTexture2DType in_texture)
  {
    this.texture = NullCheck.notNull(in_texture);
  }

  /**
   * Wrap an existing texture.
   *
   * @param t The existing texture
   *
   * @return A texture
   */

  public static R2Texture2DType of(final JCGLTexture2DType t)
  {
    return new R2Texture2DStatic(t);
  }

  @Override
  public void delete(
    final JCGLInterfaceGL33Type g)
    throws R2Exception
  {
    if (!this.texture.isDeleted()) {
      final JCGLTexturesType g_tx = g.getTextures();
      g_tx.texture2DDelete(this.texture);
    }
  }

  @Override
  public JCGLTexture2DUsableType texture()
  {
    return this.texture;
  }

  @Override
  public boolean isDeleted()
  {
    return this.texture.isDeleted();
  }

  @Override
  public JCGLTexture2DType textureWritable()
  {
    return this.texture;
  }

  @Override
  public <A, B> B matchTexture(
    final A context,
    final BiFunction<A, R2Texture2DUsableType, B> on_2d,
    final BiFunction<A, R2TextureCubeUsableType, B> on_cube)
  {
    return on_2d.apply(context, this);
  }
}
