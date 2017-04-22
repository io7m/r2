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

package com.io7m.r2.examples;

import com.io7m.jcanephora.profiler.JCGLProfilingFrameMeasurementType;
import com.io7m.jnull.NullCheck;
import com.io7m.jproperties.JProperties;
import com.io7m.jproperties.JPropertyIncorrectType;
import com.io7m.jproperties.JPropertyNonexistent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static com.io7m.jcanephora.profiler.JCGLProfilingIteration.CONTINUE;

/**
 * A Swing window for displaying profiling information.
 */

public final class ExampleProfilingWindow
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(ExampleProfilingWindow.class);
  }

  private final AtomicReference<ProfilingFrame> frame_ref;

  private ExampleProfilingWindow(
    final AtomicReference<ProfilingFrame> ref)
  {
    this.frame_ref = NullCheck.notNull(ref, "ref");
  }

  /**
   * Create a new window if profiling is enabled.
   *
   * @return A new window
   */

  public static ExampleProfilingWindow create()
  {
    final AtomicReference<ProfilingFrame> ref = new AtomicReference<>();

    try {
      final Properties props = System.getProperties();
      final boolean profiling = JProperties.getBooleanOptional(
        props, "com.io7m.r2.profiling", false);

      if (profiling) {
        SwingUtilities.invokeLater(() -> {
          final ProfilingFrame frame = new ProfilingFrame();
          frame.setVisible(true);
          ref.set(frame);
        });
      }
    } catch (final JPropertyNonexistent e) {
      LOG.error("missing system property: ", e);
    } catch (final JPropertyIncorrectType e) {
      LOG.error("incorrect system property type: ", e);
    }

    return new ExampleProfilingWindow(ref);
  }

  /**
   * Update the window with profiling information.
   *
   * @param frame_number The current frame number
   * @param measurement  The frame profiling measurement
   */

  public void update(
    final int frame_number,
    final JCGLProfilingFrameMeasurementType measurement)
  {
    if (frame_number % 60 == 0) {
      final ProfilingFrame frame = this.frame_ref.get();
      if (frame != null) {
        final StringBuilder sb = new StringBuilder(128);
        measurement.iterate(this, (tt, depth, fm) -> {
          for (int index = 0; index < depth; ++index) {
            sb.append("    ");
          }
          sb.append(fm.contextName());
          sb.append(" ");

          final double nanos = (double) fm.elapsedTimeTotal();
          final double millis = nanos / 1_000_000.0;
          sb.append(String.format("%.6f", Double.valueOf(millis)));
          sb.append("ms");
          sb.append(System.lineSeparator());
          return CONTINUE;
        });

        SwingUtilities.invokeLater(() -> frame.text.setText(sb.toString()));
      }
    }
  }

  private static final class ProfilingFrame extends JFrame
  {
    private final JTextArea text;

    ProfilingFrame()
    {
      super("Profiling");

      this.text = new JTextArea();
      this.text.setFont(Font.decode("Monospace 10"));
      final JScrollPane scroll = new JScrollPane(this.text);

      final Container cp = this.getContentPane();
      cp.add(scroll);

      this.setPreferredSize(new Dimension(640, 480));
      this.pack();
    }
  }
}
