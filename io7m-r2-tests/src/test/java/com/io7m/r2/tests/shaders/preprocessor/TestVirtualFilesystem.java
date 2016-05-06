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

package com.io7m.r2.tests.shaders.preprocessor;

import com.io7m.jnull.NullCheck;
import org.anarres.cpp.InputLexerSource;
import org.anarres.cpp.Source;
import org.anarres.cpp.VirtualFile;
import org.anarres.cpp.VirtualFileSystem;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class TestVirtualFilesystem implements VirtualFileSystem
{
  private final Class<?> base;

  public TestVirtualFilesystem(
    final Class<?> base)
  {
    this.base = base;
  }

  @Nonnull
  @Override
  public VirtualFile getFile(@Nonnull final String path)
  {
    return new ResourceFile(this.base, path);
  }

  @Nonnull
  @Override
  public VirtualFile getFile(
    @Nonnull final String dir,
    @Nonnull final String name)
  {
    return this.getFile(dir + "/" + name);
  }

  private static final class ResourceFile implements VirtualFile
  {
    private final Class<?> base;
    private final String path;

    public ResourceFile(
      final Class<?> in_base,
      final String in_path)
    {
      this.base = NullCheck.notNull(in_base);
      this.path = NullCheck.notNull(in_path);
    }

    @Override
    public boolean isFile()
    {
      throw new UnsupportedOperationException("Not supported yet."); //To
      // change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPath()
    {
      return this.path;
    }

    @Override
    public String getName()
    {
      return this.path.substring(this.path.lastIndexOf('/') + 1);
    }

    @Override
    public ResourceFile getParentFile()
    {
      final int idx = this.path.lastIndexOf('/');
      if (idx < 1) {
        return null;
      }
      return new ResourceFile(this.base, this.path.substring(0, idx));
    }

    @Override
    public ResourceFile getChildFile(final String name)
    {
      return new ResourceFile(this.base, this.path + "/" + name);
    }

    @Override
    public Source getSource()
      throws IOException
    {
      final InputStream stream = this.base.getResourceAsStream(this.path);
      return new InputLexerSource(stream, StandardCharsets.UTF_8);
    }
  }
}
