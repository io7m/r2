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

package com.io7m.r2.core.shaders.types;

import com.io7m.jcanephora.texture.unit_allocator.JCGLTextureUnitContextMutableType;
import com.io7m.jregions.core.unparameterized.areas.AreaL;
import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.R2MatricesObserverValuesType;
import org.immutables.value.Value;

/**
 * The type of material-related parameters available to shaders.
 *
 * @param <M> The precise type of shader parameters
 */

@R2ImmutableStyleType
@Value.Immutable
@Value.Modifiable
public interface R2ShaderParametersLightType<M>
{
  /**
   * @return The current texture unit context
   */

  @Value.Parameter
  JCGLTextureUnitContextMutableType textureUnitContext();

  /**
   * @return The current shader values
   */

  @Value.Parameter
  M values();

  /**
   * @return The current observer matrices
   */

  @Value.Parameter
  R2MatricesObserverValuesType observerMatrices();

  /**
   * A specification of the viewport to which rendering is occurring.
   *
   * @return The current viewport
   */

  @Value.Parameter
  AreaL viewport();
}
