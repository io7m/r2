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

package com.io7m.r2.meshes.loading.smf;

import com.io7m.jcanephora.async.JCGLAsyncInterfaceUsableGL33Type;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.meshes.loading.api.R2MeshLoaded;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderAsynchronousType;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderRequest;
import com.io7m.r2.meshes.loading.api.R2MeshLoadingException;
import com.io7m.smfj.parser.api.SMFParserProviderType;
import com.io7m.smfj.validation.api.SMFSchemaValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * An asynchronous SMF-based loader.
 */

public final class R2SMFMeshLoaderAsynchronous
  implements R2MeshLoaderAsynchronousType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2SMFMeshLoaderAsynchronous.class);
  }

  private final ExecutorService exec_io;
  private final SMFParserProviderType parsers;
  private final SMFSchemaValidator validator;

  private R2SMFMeshLoaderAsynchronous(
    final SMFParserProviderType in_parsers,
    final ExecutorService in_exec_io)
  {
    this.parsers =
      NullCheck.notNull(in_parsers, "Parsers");
    this.exec_io =
      NullCheck.notNull(in_exec_io, "I/O Executor");
    this.validator =
      new SMFSchemaValidator();
  }

  /**
   * Create a new asynchronous loader.
   *
   * @param in_parsers A parser provider
   * @param in_exec_io An executor service upon which I/O operations will be
   *                   performed
   *
   * @return A new loader
   */

  public static R2MeshLoaderAsynchronousType create(
    final SMFParserProviderType in_parsers,
    final ExecutorService in_exec_io)
  {
    return new R2SMFMeshLoaderAsynchronous(in_parsers, in_exec_io);
  }

  private static CompletableFuture<R2MeshLoaded> uploadAsync(
    final JCGLAsyncInterfaceUsableGL33Type in_async_gl,
    final R2MeshLoaderRequest request,
    final R2SMFMeshUploading.Packed m)
  {
    return in_async_gl.evaluateWith(
      m, (g33, packed) -> upload(request, g33, packed));
  }

  private static R2MeshLoaded upload(
    final R2MeshLoaderRequest request,
    final JCGLInterfaceGL33Type g33,
    final R2SMFMeshUploading.Packed packed)
  {
    return R2SMFMeshUploading.meshUpload(LOG, g33, packed);
  }

  @Override
  public R2MeshLoaded loadSynchronously(
    final JCGLInterfaceGL33Type g33,
    final R2MeshLoaderRequest request)
    throws R2MeshLoadingException
  {
    NullCheck.notNull(g33, "G33");
    NullCheck.notNull(request, "Request");

    final R2SMFMeshUploading.Packed packed =
      R2SMFMeshUploading.meshParseAndPack(
        LOG, this.parsers, this.validator, request);
    return R2SMFMeshUploading.meshUpload(LOG, g33, packed);
  }

  @Override
  public CompletableFuture<R2MeshLoaded> loadAsynchronously(
    final JCGLAsyncInterfaceUsableGL33Type in_async_gl,
    final R2MeshLoaderRequest request)
  {
    NullCheck.notNull(in_async_gl, "Async GL");
    NullCheck.notNull(request, "Request");

    final CompletableFuture<R2SMFMeshUploading.Packed> f =
      CompletableFuture.supplyAsync(
        () -> R2SMFMeshUploading.meshParseAndPack(
          LOG, this.parsers, this.validator, request),
        this.exec_io);

    return f.thenCompose(m -> uploadAsync(in_async_gl, request, m));
  }
}
