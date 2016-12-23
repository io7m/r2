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

package com.io7m.r2.tests.jogl;

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.r2.meshes.R2MeshParserInterleavedListenerType;
import com.io7m.r2.meshes.binary.R2MBReaderType;
import com.io7m.r2.meshes.binary.R2MBUnmappedReader;
import com.io7m.r2.tests.meshes.arrayobject.R2MeshArrayObjectSynchronousAdapterContract;
import com.io7m.r2.tests.meshes.binary.R2MBMappedReaderTest;

import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public final class R2MeshArrayObjectSynchronousAdapterTest extends
  R2MeshArrayObjectSynchronousAdapterContract
{
  private static ReadableByteChannel openResource(final String resource)
  {
    final Class<R2MBMappedReaderTest> c = R2MBMappedReaderTest.class;
    return Channels.newChannel(c.getResourceAsStream(resource));
  }

  @Override
  protected R2MBReaderType getMeshReader(
    final String name,
    final R2MeshParserInterleavedListenerType listener)
  {
    return R2MBUnmappedReader.newReader(
      R2MeshArrayObjectSynchronousAdapterTest.openResource(name),
      listener);
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
