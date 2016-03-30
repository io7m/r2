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
import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jfunctional.Pair;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jranges.RangeCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valid4j.Assertive;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * The default implementation of the {@link R2TextureUnitAllocatorType}
 * interface.
 */

public final class R2TextureUnitAllocator implements R2TextureUnitAllocatorType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2TextureUnitAllocator.class);
  }

  private final List<JCGLTextureUnitType> units;
  private final Context                   root;
  private final Deque<Context>            contexts_active;
  private final Deque<Context>            contexts_free;

  private R2TextureUnitAllocator(
    final int stack_depth,
    final List<JCGLTextureUnitType> in_units)
  {
    RangeCheck.checkGreaterInteger(
      stack_depth,
      "Stack depth",
      1,
      "Minimum stack depth");

    this.units = NullCheck.notNullAll(in_units);
    this.contexts_active = new ArrayDeque<>(stack_depth);
    this.contexts_free = new ArrayDeque<>(stack_depth);
    this.root = new Context();
    this.contexts_active.push(this.root);

    for (int index = 0; index < stack_depth - 1; ++index) {
      this.contexts_free.push(new Context());
    }
  }

  /**
   * Create a new allocator that will allocate a stack of {@code max_depth}
   * contexts. The number of contexts (including the initial root context)
   * will not be allowed to exceed {@code max_depth}).
   *
   * @param max_depth    The maximum stack depth
   * @param in_units The texture units
   *
   * @return A new allocator
   */

  public static R2TextureUnitAllocatorType newAllocatorWithStack(
    final int max_depth,
    final List<JCGLTextureUnitType> in_units)
  {
    return new R2TextureUnitAllocator(max_depth, in_units);
  }

  @Override
  public R2TextureUnitContextParentType getRootContext()
  {
    return this.root;
  }

  private Context getCurrent()
  {
    return this.contexts_active.peek();
  }

  private Context contextFresh()
  {
    if (!this.contexts_free.isEmpty()) {
      final Context c = this.contexts_free.pop();
      this.contexts_active.push(c);
      return c;
    }

    throw new R2ExceptionTextureUnitContextLimitReached(
      "Stack limit reached: " + this.contexts_active.size());
  }

  private void contextPop(
    final JCGLTexturesType g,
    final Context c)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(c);

    final Context c_this = R2TextureUnitAllocator.this.contexts_active.pop();
    Assertive.require(Objects.equals(c_this, c));
    R2TextureUnitAllocator.this.contexts_free.push(c_this);

    final Context c_prev = R2TextureUnitAllocator.this.contexts_active.peek();
    c_prev.activate(g);
  }

  private final class Context implements R2TextureUnitContextType
  {
    private final     R2TextureUsableType[] bindings;
    private           int                   next;
    private @Nullable JCGLTexturesType      g3;
    private @Nullable JCGLTextureUnitType   unit;

    Context()
    {
      this.bindings =
        new R2TextureUsableType[R2TextureUnitAllocator.this.units.size()];
      this.next = 0;
    }

    private void copyFrom(final Context c)
    {
      this.next = c.next;
      for (int index = 0; index < this.bindings.length; ++index) {
        this.bindings[index] = c.bindings[index];
      }
    }

    @Override
    public R2TextureUnitContextType unitContextNew()
    {
      if (!this.isCurrent()) {
        throw new R2ExceptionTextureUnitContextNotActive(
          "Context not current");
      }

      R2TextureUnitAllocator.LOG.trace("new context");
      final Context c = R2TextureUnitAllocator.this.contextFresh();
      c.copyFrom(this);
      return c;
    }

    @Override
    public R2TextureUnitContextType unitContextNewWithReserved(final int r)
    {
      this.checkTextureUnitsRequired(this.next + r);
      return this.unitContextNew();
    }

    private void checkTextureUnitsRequired(final int required)
    {
      if (required > R2TextureUnitAllocator.this.units.size()) {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("Out of texture units.");
        sb.append("Required:  ");
        sb.append(required);
        sb.append("\n");
        sb.append("Available: ");
        sb.append(R2TextureUnitAllocator.this.units.size());
        sb.append("\n");
        throw new R2ExceptionTextureUnitExhausted(sb.toString());
      }
    }

    private boolean isCurrent()
    {
      return Objects.equals(this, R2TextureUnitAllocator.this.getCurrent());
    }

    private void activate(final JCGLTexturesType g)
    {
      NullCheck.notNull(g);

      R2TextureUnitAllocator.LOG.trace("make current");

      final List<JCGLTextureUnitType> us = R2TextureUnitAllocator.this.units;
      this.g3 = g;
      for (int index = 0; index < this.bindings.length; ++index) {
        this.unit = us.get(index);
        final R2TextureUsableType t = this.bindings[index];
        if (t == null) {
          if (R2TextureUnitAllocator.LOG.isTraceEnabled()) {
            R2TextureUnitAllocator.LOG.trace(
              "[{}]: set unbound",
              Integer.valueOf(this.unit.unitGetIndex()));
          }
          g.textureUnitUnbind(this.unit);
        } else {
          if (t.isDeleted()) {
            if (R2TextureUnitAllocator.LOG.isTraceEnabled()) {
              R2TextureUnitAllocator.LOG.trace(
                "[{}]: set unbound (deleted)",
                Integer.valueOf(this.unit.unitGetIndex()));
            }
            g.textureUnitUnbind(this.unit);
            this.bindings[index] = null;
          } else {
            t.matchTexture(this, (a, t2d) -> {
              if (R2TextureUnitAllocator.LOG.isTraceEnabled()) {
                R2TextureUnitAllocator.LOG.trace(
                  "[{}]: set 2D",
                  Integer.valueOf(a.unit.unitGetIndex()));
              }

              a.g3.texture2DBind(a.unit, t2d.get());
              return Unit.unit();
            });
          }
        }
        this.unit = null;
      }
      this.g3 = null;
    }

    @Override
    public JCGLTextureUnitType unitContextBindTexture2D(
      final JCGLTexturesType g,
      final R2Texture2DUsableType t)
    {
      NullCheck.notNull(g);
      NullCheck.notNull(t);

      if (!this.isCurrent()) {
        throw new R2ExceptionTextureUnitContextNotActive(
          "Context not current");
      }

      this.checkTextureUnitsRequired(this.next + 1);

      if (R2TextureUnitAllocator.LOG.isTraceEnabled()) {
        R2TextureUnitAllocator.LOG.trace("bind {}", t.get());
      }

      final List<JCGLTextureUnitType> us = R2TextureUnitAllocator.this.units;
      final JCGLTextureUnitType u = us.get(this.next);
      g.texture2DBind(u, t.get());
      this.bindings[this.next] = t;
      ++this.next;
      return u;
    }

    @Override
    public Pair<JCGLTextureUnitType, R2Texture2DType>
    unitContextAllocateTexture2D(
      final JCGLTexturesType g,
      final long width,
      final long height,
      final JCGLTextureFormat format,
      final JCGLTextureWrapS wrap_s,
      final JCGLTextureWrapT wrap_t,
      final JCGLTextureFilterMinification min_filter,
      final JCGLTextureFilterMagnification mag_filter)
    {
      NullCheck.notNull(g);
      NullCheck.notNull(format);
      NullCheck.notNull(wrap_s);
      NullCheck.notNull(wrap_t);
      NullCheck.notNull(min_filter);
      NullCheck.notNull(mag_filter);

      if (!this.isCurrent()) {
        throw new R2ExceptionTextureUnitContextNotActive(
          "Context not current");
      }

      this.checkTextureUnitsRequired(this.next + 1);

      if (R2TextureUnitAllocator.LOG.isTraceEnabled()) {
        R2TextureUnitAllocator.LOG.trace("allocate 2d");
      }

      final List<JCGLTextureUnitType> us = R2TextureUnitAllocator.this.units;
      final JCGLTextureUnitType u = us.get(this.next);
      final JCGLTexture2DType t = g.texture2DAllocate(
        u, width, height, format, wrap_s, wrap_t, min_filter, mag_filter);

      final R2Texture2DType rt = R2Texture2DStatic.of(t);
      g.texture2DBind(u, t);
      this.bindings[this.next] = rt;
      ++this.next;
      return Pair.pair(u, rt);
    }


    @Override
    public void unitContextFinish(final JCGLTexturesType g)
    {
      if (!this.isCurrent()) {
        throw new R2ExceptionTextureUnitContextNotActive(
          "Context not current");
      }

      R2TextureUnitAllocator.LOG.trace("finish");
      R2TextureUnitAllocator.this.contextPop(g, this);
    }
  }
}
