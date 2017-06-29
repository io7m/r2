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

package com.io7m.r2.tests.jogl.rendering.light;

import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.r2.core.api.ids.R2IDPoolType;
import com.io7m.r2.rendering.lights.R2LightRenderer;
import com.io7m.r2.rendering.lights.api.R2LightRendererType;
import com.io7m.r2.tests.jogl.R2TestContexts;
import com.io7m.r2.textures.R2TextureDefaultsType;
import com.io7m.r2.unit_quads.R2UnitQuadUsableType;
import com.io7m.r2.shaders.api.R2ShaderPreprocessingEnvironmentType;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderType;
import com.io7m.r2.meshes.loading.smf.R2SMFMeshLoaderSynchronous;
import com.io7m.r2.tests.rendering.lights.api.R2LightRendererContract;
import com.io7m.smfj.format.binary.SMFFormatBinary;

public final class R2LightRendererTest extends R2LightRendererContract
{
  @Override
  protected R2MeshLoaderType loader()
  {
    return R2SMFMeshLoaderSynchronous.create(new SMFFormatBinary());
  }

  @Override
  protected R2LightRendererType getRenderer(
    final JCGLInterfaceGL33Type g,
    final R2TextureDefaultsType td,
    final R2ShaderPreprocessingEnvironmentType ss,
    final R2IDPoolType id_pool,
    final R2UnitQuadUsableType quad)
  {
    return R2LightRenderer.create(g, td, ss, id_pool, quad);
  }

  @Override
  protected JCGLContextType newGL33Context(
    final String name,
    final int depth_bits,
    final int stencil_bits)
  {
    return R2TestContexts.newGL33Context(name, depth_bits, stencil_bits);
  }
}
