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

package com.io7m.r2.examples;

import com.io7m.jareas.core.AreaInclusiveUnsignedIType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.r2.main.R2MainType;

import java.util.function.Consumer;

/**
 * The type of examples.
 */

public interface R2ExampleType
{
  /**
   * Initialize any resources the example needs. Called once.
   *
   * @param g    An OpenGL interface
   * @param area The window area
   * @param m    The main renderer interface
   */

  void onInitialize(
    JCGLInterfaceGL33Type g,
    AreaInclusiveUnsignedIType area,
    R2MainType m);

  /**
   * Render the example. Called repeatedly.
   *
   * @param g     An OpenGL interface
   * @param area  The window area
   * @param m     The main renderer interface
   * @param frame The current frame
   */

  void onRender(
    JCGLInterfaceGL33Type g,
    AreaInclusiveUnsignedIType area,
    R2MainType m,
    int frame);

  /**
   * Dispose of any resources the example has allocated. Called once.
   *
   * @param g An OpenGL interface
   * @param m The main renderer interface
   */

  void onFinish(
    JCGLInterfaceGL33Type g,
    R2MainType m);

  /**
   * Match the type of exception.
   *
   * @param on_image  Evaluated on instances of {@link R2ExampleImageType}
   * @param on_scene  Evaluated on instances of {@link R2ExampleSceneType}
   * @param on_custom Evaluated on instances of {@link R2ExampleCustomType}
   */

  void matchExample(
    Consumer<R2ExampleImageType> on_image,
    Consumer<R2ExampleSceneType> on_scene,
    Consumer<R2ExampleCustomType> on_custom
  );
}
