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

import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.shaders.depth.R2DepthShaderBasicBatched;
import com.io7m.r2.shaders.depth.R2DepthShaderBasicSingle;
import com.io7m.r2.shaders.depth.R2DepthShaderBasicStippledSingle;
import org.immutables.value.Value;

/**
 * The type of convenient depth shader providers.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2FacadeDepthShaderProviderType
{
  /**
   * @return The current facade
   */

  @Value.Auxiliary
  @Value.Parameter
  R2FacadeType main();

  /**
   * @return A basic depth shader
   */

  default R2DepthShaderBasicSingle createBasicSingle()
  {
    return R2DepthShaderBasicSingle.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A basic depth shader
   */

  default R2DepthShaderBasicBatched createBasicBatched()
  {
    return R2DepthShaderBasicBatched.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A basic stippled depth shader
   */

  default R2DepthShaderBasicStippledSingle createBasicStippledSingle()
  {
    return R2DepthShaderBasicStippledSingle.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }
}
