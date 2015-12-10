/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.shaders.checker;

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.jogl.JCGLImplementationJOGL;
import com.io7m.jcanephora.jogl.JCGLImplementationJOGLType;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessor;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorType;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Main preprocessor.
 */

public final class Main
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(Main.class);
  }

  private Main()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Main program.
   *
   * @param args Command line arguments
   *
   * @throws Exception On errors
   */

  public static void main(final String[] args)
    throws Exception
  {
    if (args.length != 3 && args.length != 4) {
      Main.LOG.info("usage: root file.vert [file.geom] file.frag");
      System.exit(1);
    }

    final File root = new File(args[0]);

    final R2ShaderPreprocessorType p =
      R2ShaderPreprocessor.newPreprocessor(root);

    List<String> vs = null;
    Optional<List<String>> gs = null;
    List<String> fs = null;

    try {
      if (args.length == 4) {
        vs = p.preprocessFile(args[1]);
        gs = Optional.of(p.preprocessFile(args[2]));
        fs = p.preprocessFile(args[3]);
      } else {
        vs = p.preprocessFile(args[1]);
        gs = Optional.empty();
        fs = p.preprocessFile(args[2]);
      }
    } catch (final Exception e) {
      Main.LOG.error("Preprocessing failure: ", e);
      System.exit(1);
    }

    final GLProfile pro = GLProfile.get(GLProfile.GL3);
    final GLCapabilities caps = new GLCapabilities(pro);
    final GLDrawableFactory f = GLDrawableFactory.getFactory(pro);
    final GLOffscreenAutoDrawable drawable =
      f.createOffscreenAutoDrawable(null, caps, null, 32, 32);
    drawable.display();
    final GLContext ctx = drawable.getContext();
    ctx.makeCurrent();

    try {
      final JCGLImplementationJOGLType i =
        JCGLImplementationJOGL.getInstance();
      final JCGLContextType jc =
        i.newContextFrom(ctx, "offscreen");
      final JCGLInterfaceGL33Type gi =
        jc.contextGetGL33();
      final R2ShaderCheckerType ch =
        R2ShaderChecker.newChecker(gi.getShaders());

      ch.check(vs, gs, fs);
    } finally {
      ctx.release();
      drawable.destroy();
    }
  }
}
