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

package com.io7m.r2.facade;

import com.io7m.r2.core.R2ImmutableStyleType;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicBatched;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicBillboarded;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicReflectiveSingle;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicSingle;
import com.io7m.r2.core.shaders.provided.R2SurfaceShaderBasicStippledSingle;
import org.immutables.value.Value;

/**
 * The type of convenient instance shader providers.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2FacadeInstanceShaderProviderType
{
  /**
   * @return The current facade
   */

  @Value.Auxiliary
  @Value.Parameter
  R2FacadeType main();

  /**
   * @return A new basic reflective instance shader
   */

  default R2SurfaceShaderBasicReflectiveSingle createBasicReflectiveSingle()
  {
    return R2SurfaceShaderBasicReflectiveSingle.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A new basic instance shader
   */

  default R2SurfaceShaderBasicSingle createBasicSingle()
  {
    return R2SurfaceShaderBasicSingle.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A new basic stippled instance shader
   */

  default R2SurfaceShaderBasicStippledSingle createBasicStippledSingle()
  {
    return R2SurfaceShaderBasicStippledSingle.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A new basic instance shader
   */

  default R2SurfaceShaderBasicBillboarded createBasicBillboarded()
  {
    return R2SurfaceShaderBasicBillboarded.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A new basic instance shader
   */

  default R2SurfaceShaderBasicBatched createBasicBatched()
  {
    return R2SurfaceShaderBasicBatched.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }
}
