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

import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextParentType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpuddle.core.JPPoolSynchronous;
import com.io7m.jpuddle.core.JPPoolSynchronousType;
import com.io7m.jpuddle.core.JPPoolableListenerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * <p>A pool of light buffers, with a configurable <i>soft</i> and
 * <i>hard</i> size.</p>
 *
 * <p>Objects within the pool will occasionally be discarded so that the storage
 * size of the pool stays below the <i>soft</i> limit. The size of the pool will
 * never be allowed to grow beyond the <i>hard</i> limit, and attempting to do
 * this will result in exceptions being raised.</p>
 */

public final class R2LightBufferPool implements
  R2RenderTargetPoolType<R2LightBufferDescription, R2LightBufferUsableType>
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2LightBufferPool.class);
  }

  private final
  JPPoolSynchronousType<
    R2LightBufferDescription,
    R2LightBufferType,
    R2LightBufferUsableType,
    JCGLTextureUnitContextParentType> actual;

  private R2LightBufferPool(
    final JPPoolSynchronousType<R2LightBufferDescription,
      R2LightBufferType,
      R2LightBufferUsableType,
      JCGLTextureUnitContextParentType> in_actual)
  {
    this.actual = NullCheck.notNull(in_actual, "Pool");
  }

  /**
   * Construct a new pool with the given size limits.
   *
   * @param g    An OpenGL interface
   * @param soft The soft limit for the pool in bytes
   * @param hard The hard limit for the pool in bytes
   *
   * @return A new pool
   */

  public static R2RenderTargetPoolType<R2LightBufferDescription,
    R2LightBufferUsableType> newPool(
    final JCGLInterfaceGL33Type g,
    final long soft,
    final long hard)
  {
    return new R2LightBufferPool(
      JPPoolSynchronous.newPool(new Listener(g), soft, hard));
  }

  @Override
  public R2LightBufferUsableType get(
    final JCGLTextureUnitContextParentType tc,
    final R2LightBufferDescription desc)
  {
    NullCheck.notNull(tc, "Texture context");
    NullCheck.notNull(desc, "Description");
    return this.actual.get(tc, desc);
  }

  @Override
  public void returnValue(
    final JCGLTextureUnitContextParentType tc,
    final R2LightBufferUsableType target)
  {
    NullCheck.notNull(tc, "Texture context");
    NullCheck.notNull(target, "Light buffer");
    this.actual.returnValue(tc, target);
  }

  @Override
  public void delete(final JCGLTextureUnitContextParentType c)
  {
    this.actual.deleteUnsafely(c);
  }

  @Override
  public boolean isDeleted()
  {
    return this.actual.isDeleted();
  }

  private static final class Listener implements JPPoolableListenerType<
    R2LightBufferDescription,
    R2LightBufferType,
    JCGLTextureUnitContextParentType>
  {
    private final JCGLInterfaceGL33Type g;

    Listener(final JCGLInterfaceGL33Type in_g)
    {
      this.g = NullCheck.notNull(in_g, "G33");
    }

    @Override
    public long onEstimateSize(
      final JCGLTextureUnitContextParentType tc,
      final R2LightBufferDescription key)
    {
      return Math.multiplyExact(key.area().width(), key.area().height());
    }

    @Override
    public R2LightBufferType onCreate(
      final JCGLTextureUnitContextParentType tc,
      final R2LightBufferDescription key)
    {
      return R2LightBuffers.newLightBuffer(
        this.g.framebuffers(),
        this.g.textures(),
        tc,
        key);
    }

    @Override
    public long onGetSize(
      final JCGLTextureUnitContextParentType tc,
      final R2LightBufferDescription key,
      final R2LightBufferType value)
    {
      return value.byteRange().getInterval();
    }

    @Override
    public void onReuse(
      final JCGLTextureUnitContextParentType tc,
      final R2LightBufferDescription key,
      final R2LightBufferType value)
    {
      if (LOG.isTraceEnabled()) {
        LOG.trace("reuse {}", key);
      }
    }

    @Override
    public void onDelete(
      final JCGLTextureUnitContextParentType tc,
      final R2LightBufferDescription key,
      final R2LightBufferType value)
    {
      if (LOG.isTraceEnabled()) {
        LOG.trace("delete {}", value);
      }

      value.delete(this.g);
    }

    @Override
    public void onError(
      final JCGLTextureUnitContextParentType tc,
      final R2LightBufferDescription key,
      final Optional<R2LightBufferType> value,
      final Throwable e)
    {
      LOG.error(
        "Exception raised in cache listener: {}: ", key, e);
    }
  }
}
