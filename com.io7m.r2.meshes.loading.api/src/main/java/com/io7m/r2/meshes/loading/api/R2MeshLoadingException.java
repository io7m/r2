/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.meshes.loading.api;

import com.io7m.jnull.NullCheck;
import com.io7m.r2.meshes.api.R2MeshException;

import java.util.List;

/**
 * Exceptions encountered when attempting to load meshes.
 */

public abstract class R2MeshLoadingException extends R2MeshException
{
  private final List<R2MeshLoadingError> errors;

  /**
   * Construct an exception.
   *
   * @param in_errors The list of causes, if any
   * @param message   The message
   */

  public R2MeshLoadingException(
    final List<R2MeshLoadingError> in_errors,
    final String message)
  {
    super(message);
    this.errors = NullCheck.notNull(in_errors, "Errors");
    this.errors.forEach(e -> e.exception().ifPresent(this::addSuppressed));
  }

  /**
   * Construct an exception.
   *
   * @param in_errors The list of causes, if any
   * @param message   The message
   * @param cause     The cause
   */

  public R2MeshLoadingException(
    final List<R2MeshLoadingError> in_errors,
    final String message,
    final Throwable cause)
  {
    super(message, cause);
    this.errors = NullCheck.notNull(in_errors, "Errors");
    this.errors.forEach(e -> e.exception().ifPresent(this::addSuppressed));
  }

  /**
   * Construct an exception.
   *
   * @param in_errors The list of causes, if any
   * @param cause     The cause
   */

  public R2MeshLoadingException(
    final List<R2MeshLoadingError> in_errors,
    final Throwable cause)
  {
    super(cause);
    this.errors = NullCheck.notNull(in_errors, "Errors");
    this.errors.forEach(e -> e.exception().ifPresent(this::addSuppressed));
  }

  /**
   * @return The list of causes
   */

  public final List<R2MeshLoadingError> errors()
  {
    return this.errors;
  }
}
