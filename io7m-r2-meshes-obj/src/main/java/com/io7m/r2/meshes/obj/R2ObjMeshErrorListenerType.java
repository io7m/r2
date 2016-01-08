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

package com.io7m.r2.meshes.obj;

import com.io7m.jlexing.core.LexicalPositionType;
import com.io7m.jobj.core.JOParserErrorCode;

import java.nio.file.Path;
import java.util.Optional;

/**
 * The type of error listeners.
 */

public interface R2ObjMeshErrorListenerType
{
  /**
   * An error occurred.
   *
   * @param p         The lexical position of the error
   * @param exception The exception raised, if any
   * @param mesh      The current mesh, if any
   * @param code      The error code, if any
   * @param message   The error message
   */

  void onError(
    final LexicalPositionType<Path> p,
    final Optional<Throwable> exception,
    final Optional<String> mesh,
    final Optional<JOParserErrorCode> code,
    final String message);
}
