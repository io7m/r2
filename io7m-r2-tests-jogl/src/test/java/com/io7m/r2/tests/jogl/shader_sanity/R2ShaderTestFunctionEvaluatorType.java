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

package com.io7m.r2.tests.jogl.shader_sanity;

import com.io7m.jtensors.VectorI2F;
import com.io7m.jtensors.VectorI3F;
import com.io7m.jtensors.VectorI4F;

public interface R2ShaderTestFunctionEvaluatorType
{
  VectorI4F evaluate4f(VectorI4F v);

  default VectorI3F evaluate3f(final VectorI3F v)
  {
    final VectorI4F r =
      this.evaluate4f(new VectorI4F(v.getXF(), v.getYF(), v.getZF(), 0.0f));
    return new VectorI3F(r.getXF(), r.getYF(), r.getZF());
  }

  default VectorI2F evaluate2f(final VectorI2F v)
  {
    final VectorI4F r =
      this.evaluate4f(new VectorI4F(v.getXF(), v.getYF(), 0.0f, 0.0f));
    return new VectorI2F(r.getXF(), r.getYF());
  }

  default float evaluate1f(final float v)
  {
    final VectorI4F r =
      this.evaluate4f(new VectorI4F(v, 0.0f, 0.0f, 0.0f));
    return r.getXF();
  }
}
