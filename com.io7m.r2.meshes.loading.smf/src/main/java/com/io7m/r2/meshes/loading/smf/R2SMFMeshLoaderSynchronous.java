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

import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jnull.NullCheck;
import com.io7m.r2.meshes.loading.api.R2MeshLoaded;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderRequest;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderType;
import com.io7m.r2.meshes.loading.api.R2MeshLoadingException;
import com.io7m.smfj.parser.api.SMFParserProviderType;
import com.io7m.smfj.validation.api.SMFSchemaValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.io7m.r2.meshes.loading.smf.R2SMFMeshUploading.Packed;
import static com.io7m.r2.meshes.loading.smf.R2SMFMeshUploading.meshParseAndPack;
import static com.io7m.r2.meshes.loading.smf.R2SMFMeshUploading.meshUpload;

/**
 * A synchronous SMF-based loader.
 */

public final class R2SMFMeshLoaderSynchronous implements R2MeshLoaderType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2SMFMeshLoaderSynchronous.class);
  }

  private final SMFParserProviderType parsers;
  private final SMFSchemaValidator validator;

  private R2SMFMeshLoaderSynchronous(
    final SMFParserProviderType in_parsers)
  {
    this.parsers = NullCheck.notNull(in_parsers, "Parsers");
    this.validator = new SMFSchemaValidator();
  }

  /**
   * Create a new synchronous loader.
   *
   * @param in_parsers A parser provider
   *
   * @return A new loader
   */

  public static R2MeshLoaderType create(
    final SMFParserProviderType in_parsers)
  {
    return new R2SMFMeshLoaderSynchronous(in_parsers);
  }

  @Override
  public R2MeshLoaded loadSynchronously(
    final JCGLInterfaceGL33Type g33,
    final R2MeshLoaderRequest request)
    throws R2MeshLoadingException
  {
    NullCheck.notNull(g33, "G33");
    NullCheck.notNull(request, "Request");

    final Packed packed =
      meshParseAndPack(LOG, this.parsers, this.validator, request);
    return meshUpload(LOG, g33, packed);
  }
}
