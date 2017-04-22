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

package com.io7m.r2.tests.meshes.binary;

import com.io7m.r2.meshes.R2MeshParserInterleavedListenerType;
import com.io7m.r2.meshes.binary.R2MBMappedReader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class R2MBMappedReaderTest extends R2MBReaderContract
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MBMappedReaderTest.class);
  }

  private static FileChannel openResource(final String resource)
    throws IOException
  {
    final Class<R2MBMappedReaderTest> c = R2MBMappedReaderTest.class;
    try (final InputStream s = c.getResourceAsStream(resource)) {
      final Path tp = Files.createTempFile("r2mb-", "");
      R2MBMappedReaderTest.LOG.debug("copying {} to {}", resource, tp);
      try (final OutputStream o = Files.newOutputStream(tp)) {
        IOUtils.copy(s, o);
        o.flush();
      }
      return FileChannel.open(tp, StandardOpenOption.READ);
    }
  }

  @Override
  protected Runnable getReader(
    final String name,
    final R2MeshParserInterleavedListenerType listener)
    throws IOException
  {
    return R2MBMappedReader.newMappedReaderForFileChannel(
      R2MBMappedReaderTest.openResource(name),
      listener);
  }
}
