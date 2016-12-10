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

import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLFramebufferColorAttachmentPointType;
import com.io7m.jcanephora.core.JCGLFramebufferType;
import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.JCGLProgramShaderType;
import com.io7m.jcanephora.core.JCGLProgramUniformType;
import com.io7m.jcanephora.core.JCGLTexture2DUsableType;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jcanephora.cursors.JCGLRGBA32FByteBuffered;
import com.io7m.jcanephora.cursors.JCGLRGBA32FType;
import com.io7m.jnull.NullCheck;
import com.io7m.jpra.runtime.java.JPRACursor2DByteBufferedChecked;
import com.io7m.jpra.runtime.java.JPRACursor2DType;
import com.io7m.jtensors.VectorI4F;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironment;
import com.io7m.r2.core.shaders.types.R2ShaderPreprocessingEnvironmentType;
import com.io7m.sombrero.core.SoShaderPreprocessorConfig;
import com.io7m.sombrero.core.SoShaderResolver;
import com.io7m.sombrero.jcpp.SoShaderPreprocessorJCPP;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.OptionalInt;

public final class R2ShaderTestFunctionEvaluator
  implements R2ShaderTestFunctionEvaluatorType
{
  private final JCGLFramebufferType framebuffer;
  private final JCGLProgramShaderType program;
  private final JCGLArrayObjectType quad;
  private final JCGLInterfaceGL33Type gl;

  public R2ShaderTestFunctionEvaluator(
    final JCGLInterfaceGL33Type gg,
    final String v_shader_name,
    final String f_shader_name)
    throws IOException
  {
    final JCGLShadersType gs = gg.getShaders();
    final JCGLTexturesType gt = gg.getTextures();
    final JCGLArrayObjectsType ga = gg.getArrayObjects();
    final JCGLDrawType gd = gg.getDraw();
    final JCGLFramebuffersType gf = gg.getFramebuffers();
    final List<JCGLTextureUnitType> units = gt.textureGetUnits();

    final JCGLTextureFormat fmt =
      JCGLTextureFormat.TEXTURE_FORMAT_RGBA_32F_16BPP;
    this.framebuffer =
      R2ShadersTestUtilities.newColorFramebuffer(gg, fmt, 2, 2);

    final SoShaderPreprocessorConfig config =
      SoShaderPreprocessorConfig.of(
        SoShaderResolver.create(),
        OptionalInt.of(330));
    final R2ShaderPreprocessingEnvironmentType env =
      R2ShaderPreprocessingEnvironment.create(
        SoShaderPreprocessorJCPP.create(config));

    this.program =
      R2ShadersTestUtilities.compilerShaderVF(
        gs, env, v_shader_name, f_shader_name);
    this.quad =
      R2ShadersTestUtilities.newScreenQuad(gg);
    this.gl = NullCheck.notNull(gg);
  }

  @Override public VectorI4F evaluate4f(final VectorI4F x)
  {
    final JCGLShadersType gs = this.gl.getShaders();
    final JCGLTexturesType gt = this.gl.getTextures();
    final JCGLArrayObjectsType ga = this.gl.getArrayObjects();
    final JCGLDrawType gd = this.gl.getDraw();
    final JCGLFramebuffersType gf = this.gl.getFramebuffers();

    final JCGLProgramUniformType u_data =
      NullCheck.notNull(this.program.getUniforms().get("data"));

    gf.framebufferDrawBind(this.framebuffer);
    gs.shaderActivateProgram(this.program);
    gs.shaderUniformPutVector4f(u_data, x);
    ga.arrayObjectBind(this.quad);
    gd.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
    ga.arrayObjectUnbind();
    gs.shaderDeactivateProgram();
    gf.framebufferDrawUnbind();

    final List<JCGLFramebufferColorAttachmentPointType> attaches =
      gf.framebufferGetColorAttachments();
    final JCGLTexture2DUsableType at =
      (JCGLTexture2DUsableType)
        this.framebuffer.framebufferGetColorAttachment(attaches.get(0)).get();

    final JCGLTextureUnitType unit_0 = gt.textureGetUnits().get(0);
    final ByteBuffer i = gt.texture2DGetImage(unit_0, at);
    gt.textureUnitUnbind(unit_0);

    final JPRACursor2DType<JCGLRGBA32FType> c =
      JPRACursor2DByteBufferedChecked.newCursor(
        i, 2, 2,
        JCGLRGBA32FByteBuffered::newValueWithOffset);
    final JCGLRGBA32FType v = c.getElementView();

    c.setElementPosition(0, 0);
    return new VectorI4F(v.getR(), v.getG(), v.getB(), v.getA());
  }
}
