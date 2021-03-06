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

import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.sombrero.core.SoShaderResolverType;
import org.osgi.service.component.annotations.Component;

/**
 * A facade provider.
 */

@Component(immediate = true)
public final class R2FacadeProvider implements R2FacadeProviderType
{
  /**
   * Construct a new facade provider.
   */

  public R2FacadeProvider()
  {

  }

  @Override
  public R2FacadeType create(
    final JCGLInterfaceGL33Type renderer_gl33,
    final SoShaderResolverType resolver)
  {
    return R2Facade.of(renderer_gl33, resolver);
  }
}
