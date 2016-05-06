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

package com.io7m.r2.shaders.tests;

import com.io7m.jcanephora.core.JCGLArrayBufferType;
import com.io7m.jcanephora.core.JCGLArrayObjectBuilderType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLBufferUpdateType;
import com.io7m.jcanephora.core.JCGLBufferUpdates;
import com.io7m.jcanephora.core.JCGLFragmentShaderType;
import com.io7m.jcanephora.core.JCGLFramebufferBuilderType;
import com.io7m.jcanephora.core.JCGLFramebufferColorAttachmentPointType;
import com.io7m.jcanephora.core.JCGLFramebufferDrawBufferType;
import com.io7m.jcanephora.core.JCGLFramebufferType;
import com.io7m.jcanephora.core.JCGLIndexBufferType;
import com.io7m.jcanephora.core.JCGLProgramShaderType;
import com.io7m.jcanephora.core.JCGLScalarType;
import com.io7m.jcanephora.core.JCGLTexture2DType;
import com.io7m.jcanephora.core.JCGLTextureFilterMagnification;
import com.io7m.jcanephora.core.JCGLTextureFilterMinification;
import com.io7m.jcanephora.core.JCGLTextureFormat;
import com.io7m.jcanephora.core.JCGLTextureUnitType;
import com.io7m.jcanephora.core.JCGLTextureWrapS;
import com.io7m.jcanephora.core.JCGLTextureWrapT;
import com.io7m.jcanephora.core.JCGLUnsignedType;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.JCGLVertexShaderType;
import com.io7m.jcanephora.core.api.JCGLArrayBuffersType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLIndexBuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jpra.runtime.java.JPRACursor1DByteBufferedChecked;
import com.io7m.jpra.runtime.java.JPRACursor1DType;
import com.io7m.jtensors.Vector2FType;
import com.io7m.jtensors.Vector3FType;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.shaders.R2Shaders;
import com.io7m.r2.shaders.tests.cursors.VertexUVByteBuffered;
import com.io7m.r2.shaders.tests.cursors.VertexUVType;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class R2ShadersTestUtilities
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2ShadersTestUtilities.class);
  }

  private R2ShadersTestUtilities()
  {
    throw new UnreachableCodeException();
  }

  static JCGLInterfaceGL33Type getGL()
  {
    final JCGLContextType c = R2TestContexts.newGL33Context("main", 24, 8);
    return c.contextGetGL33();
  }

  static JCGLArrayObjectType newScreenQuad(
    final JCGLInterfaceGL33Type g)
  {
    final JCGLArrayObjectsType g_ao = g.getArrayObjects();
    final JCGLIndexBuffersType g_ib = g.getIndexBuffers();
    final JCGLArrayBuffersType g_ab = g.getArrayBuffers();

    final JCGLIndexBufferType ib =
      g_ib.indexBufferAllocate(
        6L,
        JCGLUnsignedType.TYPE_UNSIGNED_INT,
        JCGLUsageHint.USAGE_STATIC_DRAW);

    {
      final JCGLBufferUpdateType<JCGLIndexBufferType> u =
        JCGLBufferUpdates.newUpdateReplacingAll(ib);
      final IntBuffer i = u.getData().asIntBuffer();

      i.put(0, 0);
      i.put(1, 1);
      i.put(2, 2);

      i.put(3, 0);
      i.put(4, 2);
      i.put(5, 3);

      g_ib.indexBufferUpdate(u);
      g_ib.indexBufferUnbind();
    }

    final int vertex_size = VertexUVByteBuffered.sizeInOctets();
    final JCGLArrayBufferType ab =
      g_ab.arrayBufferAllocate(
        (long) vertex_size * 4L, JCGLUsageHint.USAGE_STATIC_DRAW);

    {
      final JCGLBufferUpdateType<JCGLArrayBufferType> u =
        JCGLBufferUpdates.newUpdateReplacingAll(ab);

      final JPRACursor1DType<VertexUVType> c =
        JPRACursor1DByteBufferedChecked.newCursor(
          u.getData(), VertexUVByteBuffered::newValueWithOffset);
      final VertexUVType v = c.getElementView();
      final Vector3FType pv = v.getPositionWritable();
      final Vector2FType uv = v.getUvWritable();

      c.setElementIndex(0);
      pv.set3F(-1.0f, 1.0f, 0.0f);
      uv.set2F(0.0f, 1.0f);

      c.setElementIndex(1);
      pv.set3F(-1.0f, -1.0f, 0.0f);
      uv.set2F(0.0f, 0.0f);

      c.setElementIndex(2);
      pv.set3F(1.0f, -1.0f, 0.0f);
      uv.set2F(1.0f, 0.0f);

      c.setElementIndex(3);
      pv.set3F(1.0f, 1.0f, 0.0f);
      uv.set2F(1.0f, 1.0f);

      g_ab.arrayBufferUpdate(u);
      g_ab.arrayBufferUnbind();
    }

    final JCGLArrayObjectBuilderType aob = g_ao.arrayObjectNewBuilder();
    aob.setIndexBuffer(ib);
    aob.setAttributeFloatingPoint(
      0,
      ab,
      3,
      JCGLScalarType.TYPE_FLOAT,
      vertex_size,
      (long) VertexUVByteBuffered.metaPositionStaticOffsetFromType(),
      false);
    aob.setAttributeFloatingPoint(
      1,
      ab,
      2,
      JCGLScalarType.TYPE_FLOAT,
      vertex_size,
      (long) VertexUVByteBuffered.metaUvStaticOffsetFromType(),
      false);

    final JCGLArrayObjectType ao = g_ao.arrayObjectAllocate(aob);
    g_ao.arrayObjectUnbind();
    return ao;
  }

  static JCGLFramebufferType newColorFramebuffer(
    final JCGLInterfaceGL33Type g,
    final JCGLTextureFormat f,
    final int w,
    final int h)
  {
    final JCGLTexturesType gt = g.getTextures();
    final JCGLFramebuffersType fb = g.getFramebuffers();
    final JCGLFramebufferBuilderType fbb = fb.framebufferNewBuilder();
    final List<JCGLFramebufferColorAttachmentPointType> points =
      fb.framebufferGetColorAttachments();
    final List<JCGLFramebufferDrawBufferType> buffers =
      fb.framebufferGetDrawBuffers();
    final List<JCGLTextureUnitType> units = gt.textureGetUnits();
    final JCGLTextureUnitType u = units.get(0);
    final JCGLTexture2DType t =
      R2ShadersTestUtilities.newTexture2D(g, f, w, h, u);

    fbb.attachColorTexture2DAt(points.get(0), buffers.get(0), t);
    return fb.framebufferAllocate(fbb);
  }

  static JCGLTexture2DType newTexture2D(
    final JCGLInterfaceGL33Type g,
    final JCGLTextureFormat f,
    final int w,
    final int h,
    final JCGLTextureUnitType u)
  {
    final JCGLTexturesType gt = g.getTextures();
    final JCGLTexture2DType t =
      gt.texture2DAllocate(
        u, (long) w, (long) h, f,
        JCGLTextureWrapS.TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureWrapT.TEXTURE_WRAP_CLAMP_TO_EDGE,
        JCGLTextureFilterMinification.TEXTURE_FILTER_NEAREST,
        JCGLTextureFilterMagnification.TEXTURE_FILTER_NEAREST);
    gt.textureUnitUnbind(u);
    return t;
  }

  static JCGLProgramShaderType compilerShaderVF(
    final JCGLShadersType gs,
    final String name)
    throws IOException
  {
    final String v_path = "/com/io7m/r2/shaders/" + name + ".vert";
    final String f_path = "/com/io7m/r2/shaders/" + name + ".frag";
    final Class<R2Shaders> cl = R2Shaders.class;
    final List<String> vs =
      IOUtils.readLines(cl.getResourceAsStream(v_path))
        .stream()
        .map(line -> {
          R2ShadersTestUtilities.LOG.trace("[{}][vertex]: {}", name, line);
          return line + "\n";
        }).collect(Collectors.toList());
    final List<String> fs =
      IOUtils.readLines(cl.getResourceAsStream(f_path))
        .stream()
        .map(line -> {
          R2ShadersTestUtilities.LOG.trace("[{}][fragment]: {}", name, line);
          return line + "\n";
        }).collect(Collectors.toList());

    final JCGLVertexShaderType v = gs.shaderCompileVertex(name, vs);
    final JCGLFragmentShaderType f = gs.shaderCompileFragment(name, fs);
    return gs.shaderLinkProgram(name, v, Optional.empty(), f);
  }
}
