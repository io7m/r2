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

package com.io7m.r2.tests.jogl.meshes.loading.smf;

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderAsynchronousType;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderType;
import com.io7m.r2.meshes.loading.smf.R2SMFMeshLoaderAsynchronous;
import com.io7m.r2.meshes.loading.smf.R2SMFMeshLoaderSynchronous;
import com.io7m.r2.tests.R2AsyncGLRule;
import com.io7m.r2.tests.jogl.R2TestContexts;
import com.io7m.r2.tests.meshes.loading.api.R2MeshLoaderAsynchronousContract;
import com.io7m.r2.tests.meshes.loading.api.R2MeshLoaderSynchronousContract;
import com.io7m.smfj.format.binary.SMFFormatBinary;
import com.io7m.smfj.format.text.SMFFormatText;
import org.junit.Rule;

import java.net.URI;
import java.net.URISyntaxException;

public final class R2SMFMeshLoaderSynchronousTest
  extends R2MeshLoaderSynchronousContract
{
  @Override
  protected R2MeshLoaderType create()
  {
    return R2SMFMeshLoaderSynchronous.create(new SMFFormatText());
  }

  @Override
  protected URI resolve(
    final String name)
  {
    try {
      final String file =
        "/com/io7m/r2/tests/meshes/loading/smf/" + name + ".smft";
      return R2MeshLoaderSynchronousContract.class.getResource(file).toURI();
    } catch (final URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  protected JCGLContextType newGL33Context(
    final String name,
    final int depth_bits,
    final int stencil_bits)
  {
    return R2TestContexts.newGL33Context(name, depth_bits, stencil_bits);
  }
}
