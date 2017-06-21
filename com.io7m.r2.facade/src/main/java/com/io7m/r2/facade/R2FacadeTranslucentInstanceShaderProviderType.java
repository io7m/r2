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
import com.io7m.r2.core.shaders.provided.R2RefractionMaskedDeltaShaderBatched;
import com.io7m.r2.core.shaders.provided.R2RefractionMaskedDeltaShaderBillboarded;
import com.io7m.r2.core.shaders.provided.R2RefractionMaskedDeltaShaderSingle;
import com.io7m.r2.core.shaders.provided.R2TranslucentShaderBasicPremultipliedBatched;
import com.io7m.r2.core.shaders.provided.R2TranslucentShaderBasicPremultipliedBillboarded;
import com.io7m.r2.core.shaders.provided.R2TranslucentShaderBasicPremultipliedSingle;
import org.immutables.value.Value;

/**
 * The type of convenient translucent instance shader providers.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2FacadeTranslucentInstanceShaderProviderType
{
  /**
   * @return The current facade
   */

  @Value.Auxiliary
  @Value.Parameter
  R2FacadeType main();

  /**
   * @return A basic premultiplied shader
   */

  default R2TranslucentShaderBasicPremultipliedSingle createBasicPremultipliedSingle()
  {
    return R2TranslucentShaderBasicPremultipliedSingle.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A basic premultiplied shader
   */

  default R2TranslucentShaderBasicPremultipliedBatched createBasicPremultipliedBatched()
  {
    return R2TranslucentShaderBasicPremultipliedBatched.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A basic premultiplied shader
   */

  default R2TranslucentShaderBasicPremultipliedBillboarded createBasicPremultipliedBillboarded()
  {
    return R2TranslucentShaderBasicPremultipliedBillboarded.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A basic refractive shader
   */

  default R2RefractionMaskedDeltaShaderSingle createRefractionMaskedDeltaSingle()
  {
    return R2RefractionMaskedDeltaShaderSingle.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A basic refractive shader
   */

  default R2RefractionMaskedDeltaShaderBatched createRefractionMaskedDeltaBatched()
  {
    return R2RefractionMaskedDeltaShaderBatched.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }

  /**
   * @return A basic billboarded shader
   */

  default R2RefractionMaskedDeltaShaderBillboarded createRefractionMaskedDeltaBillboarded()
  {
    return R2RefractionMaskedDeltaShaderBillboarded.create(
      this.main().rendererGL33().shaders(),
      this.main().shaderPreprocessingEnvironment(),
      this.main().idPool());
  }
}
