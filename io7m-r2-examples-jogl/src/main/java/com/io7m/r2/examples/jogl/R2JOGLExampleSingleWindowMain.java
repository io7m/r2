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

package com.io7m.r2.examples.jogl;

import com.io7m.jareas.core.AreaInclusiveUnsignedI;
import com.io7m.jareas.core.AreaInclusiveUnsignedIType;
import com.io7m.jcanephora.core.JCGLExceptionNonCompliant;
import com.io7m.jcanephora.core.JCGLExceptionUnsupported;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.jogl.JCGLImplementationJOGL;
import com.io7m.jcanephora.jogl.JCGLImplementationJOGLType;
import com.io7m.jnull.NullCheck;
import com.io7m.junsigned.ranges.UnsignedRangeInclusiveI;
import com.io7m.r2.examples.R2ExampleType;
import com.io7m.r2.main.R2Main;
import com.io7m.r2.main.R2MainType;
import com.jogamp.nativewindow.WindowClosingProtocol;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.DebugGL3;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JOGL example frontend.
 */

public final class R2JOGLExampleSingleWindowMain implements Runnable
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2JOGLExampleSingleWindowMain.class);
  }

  private final Class<? extends R2ExampleType> example_type;

  private R2JOGLExampleSingleWindowMain(
    final Class<? extends R2ExampleType> c)
  {
    this.example_type = NullCheck.notNull(c);
  }

  /**
   * Main entry point.
   *
   * @param args Command line arguments
   *
   * @throws Exception On errors
   */

  public static void main(final String[] args)
    throws Exception
  {
    if (args.length < 1) {
      R2JOGLExampleSingleWindowMain.LOG.error("usage: class-name");
      System.exit(1);
    }

    @SuppressWarnings("unchecked") final Class<? extends R2ExampleType> ck =
      (Class<? extends R2ExampleType>) Class.forName(args[0]);
    new R2JOGLExampleSingleWindowMain(ck).run();
  }

  @Override
  public void run()
  {
    try {
      final R2ExampleType example = this.example_type.newInstance();

      final GLProfile pro = GLProfile.get(GLProfile.GL3);
      final GLCapabilities caps = new GLCapabilities(pro);
      final GLWindow win = GLWindow.create(caps);
      win.setSize(640, 480);
      win.setDefaultCloseOperation(
        WindowClosingProtocol.WindowClosingMode.DISPOSE_ON_CLOSE);
      win.addGLEventListener(new ExampleListener(example));

      final Animator anim = new Animator(win);

      /**
       * Close the program when the window closes.
       */

      win.addWindowListener(new WindowAdapter()
      {
        @Override
        public void windowDestroyed(
          final WindowEvent e)
        {
          R2JOGLExampleSingleWindowMain.LOG.debug("Stopping animator");
          anim.stop();
          R2JOGLExampleSingleWindowMain.LOG.debug("Exiting");
          System.exit(0);
        }
      });

      anim.start();
      win.setVisible(true);
    } catch (final InstantiationException e) {
      R2JOGLExampleSingleWindowMain.LOG.error("instantiation error: {}", e);
    } catch (final IllegalAccessException e) {
      R2JOGLExampleSingleWindowMain.LOG.error("access error: {}", e);
    }
  }

  private static final class ExampleListener implements GLEventListener
  {
    private final R2ExampleType              example;
    private       JCGLContextType            context;
    private       int                        frame;
    private       R2MainType                 r2_main;
    private       AreaInclusiveUnsignedIType area;

    ExampleListener(
      final R2ExampleType in_example)
    {
      this.example = NullCheck.notNull(in_example);
    }

    @Override
    public void init(final GLAutoDrawable drawable)
    {
      this.resize(drawable);
    }

    @Override
    public void dispose(final GLAutoDrawable drawable)
    {
      R2JOGLExampleSingleWindowMain.LOG.debug("finishing example");
      this.example.onFinish(this.context.contextGetGL33(), this.r2_main);
    }

    @Override
    public void display(final GLAutoDrawable drawable)
    {
      try {
        if (this.frame == 0) {
          final GL gl = drawable.getGL();
          gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
          gl.glClear(
            GL.GL_COLOR_BUFFER_BIT
              | GL.GL_DEPTH_BUFFER_BIT
              | GL.GL_STENCIL_BUFFER_BIT);
          return;
        }


        if (this.frame == 1) {
          final JCGLImplementationJOGLType g =
            JCGLImplementationJOGL.getInstance();

          this.context = g.newContextFromWithSupplier(
            drawable.getContext(),
            (c) -> new DebugGL3(c.getGL().getGL3()),
            "main");

          final JCGLInterfaceGL33Type g33 = this.context.contextGetGL33();
          this.r2_main = R2Main.newBuilder().build(g33);

          R2JOGLExampleSingleWindowMain.LOG.debug("initializing example");
          this.example.onInitialize(g33, this.area, this.r2_main);
          R2JOGLExampleSingleWindowMain.LOG.debug("initialized example");
          return;
        }

        final JCGLInterfaceGL33Type g33 = this.context.contextGetGL33();
        this.example.onRender(g33, this.area, this.r2_main, this.frame - 2);
      } catch (final JCGLExceptionUnsupported x) {
        R2JOGLExampleSingleWindowMain.LOG.error("unsupported: ", x);
      } catch (final JCGLExceptionNonCompliant x) {
        R2JOGLExampleSingleWindowMain.LOG.error("non compliant: ", x);
      } finally {
        ++this.frame;
      }
    }

    @Override
    public void reshape(
      final GLAutoDrawable drawable,
      final int x,
      final int y,
      final int width,
      final int height)
    {
      this.resize(drawable);
    }

    private void resize(final GLAutoDrawable drawable)
    {
      this.area = AreaInclusiveUnsignedI.of(
        new UnsignedRangeInclusiveI(0, drawable.getSurfaceWidth() - 1),
        new UnsignedRangeInclusiveI(0, drawable.getSurfaceHeight() - 1));
    }
  }
}
