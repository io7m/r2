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

import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix4x4D;
import com.io7m.r2.textures.R2Texture2DUsableType;
import com.io7m.r2.textures.R2TextureCubeUsableType;
import com.io7m.r2.spaces.R2SpaceEyeType;
import com.io7m.r2.spaces.R2SpaceWorldType;

/**
 * Services provided to examples.
 */

public interface R2ExampleServicesType
{
  /**
   * Load a texture by name from the example resources.
   *
   * @param name The texture file name
   *
   * @return A 2D texture
   */

  R2Texture2DUsableType getTexture2D(String name);

  /**
   * Load a texture by name from the example resources.
   *
   * @param name The texture directory name
   *
   * @return A 2D texture
   */

  R2TextureCubeUsableType getTextureCube(String name);

  /**
   * Load a mesh by name from the example resources.
   *
   * @param name The mesh file name
   *
   * @return A mesh
   */

  JCGLArrayObjectType getMesh(String name);

  /**
   * @return {@code true} Iff the free camera is enabled
   */

  boolean isFreeCameraEnabled();

  /**
   * Get access to the view matrix for the free camera. This matrix is only
   * updated when {@link #isFreeCameraEnabled()} returns {@code true}.
   *
   * @return The current free camera view matrix
   */

  PMatrix4x4D<R2SpaceWorldType, R2SpaceEyeType> getFreeCameraViewMatrix();
}
