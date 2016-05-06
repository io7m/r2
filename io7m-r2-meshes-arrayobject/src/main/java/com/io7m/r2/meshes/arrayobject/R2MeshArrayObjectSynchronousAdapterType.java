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

package com.io7m.r2.meshes.arrayobject;

import com.io7m.jcanephora.core.JCGLArrayBufferType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLIndexBufferType;
import com.io7m.r2.meshes.R2MeshParserInterleavedListenerType;

import java.util.Optional;

/**
 * The type of synchronous adapters that accept mesh data from a parser and
 * construct buffers on the GPU.
 *
 * In this case, synchronous refers to the fact that the GPU operations will
 * occur on the same thread that is performing I/O and parsing.
 */

public interface R2MeshArrayObjectSynchronousAdapterType extends
  R2MeshParserInterleavedListenerType
{
  /**
   * Indicate whether or not parsing has failed.
   *
   * @return {@code true} iff parsing failed.
   */

  boolean hasFailed();

  /**
   * @return The created array buffer
   *
   * @throws IllegalStateException Iff parsing has failed
   */

  JCGLArrayBufferType getArrayBuffer()
    throws IllegalStateException;

  /**
   * @return The created index buffer
   *
   * @throws IllegalStateException Iff parsing has failed
   */

  JCGLIndexBufferType getIndexBuffer()
    throws IllegalStateException;

  /**
   * @return The created array object
   *
   * @throws IllegalStateException Iff parsing has failed
   */

  JCGLArrayObjectType getArrayObject()
    throws IllegalStateException;

  /**
   * @return The exception that occurred during parsing, if any
   *
   * @throws IllegalStateException Iff parsing did not fail
   * @see #hasFailed()
   */

  Optional<Throwable> getErrorException()
    throws IllegalStateException;

  /**
   * @return The error message that occurred during parsing, if any
   *
   * @throws IllegalStateException Iff parsing did not fail
   * @see #hasFailed()
   */

  String getErrorMessage()
    throws IllegalStateException;
}
