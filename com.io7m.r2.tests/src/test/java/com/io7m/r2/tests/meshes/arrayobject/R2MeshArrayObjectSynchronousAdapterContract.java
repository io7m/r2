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

package com.io7m.r2.tests.meshes.arrayobject;

import com.io7m.jcanephora.core.JCGLArrayBufferType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLIndexBufferType;
import com.io7m.jcanephora.core.JCGLUnsignedType;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLContextType;
import com.io7m.jcanephora.core.api.JCGLIndexBuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.r2.core.R2AttributeConventions;
import com.io7m.r2.core.cursors.R2VertexCursorP32UNT16;
import com.io7m.r2.meshes.R2MeshParserInterleavedListenerType;
import com.io7m.r2.meshes.arrayobject.R2MeshArrayObjectSynchronousAdapter;
import com.io7m.r2.meshes.arrayobject.R2MeshArrayObjectSynchronousAdapterType;
import com.io7m.r2.meshes.binary.R2MBReaderType;
import com.io7m.r2.tests.core.R2JCGLContract;
import org.hamcrest.core.StringStartsWith;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Optional;

public abstract class R2MeshArrayObjectSynchronousAdapterContract extends
  R2JCGLContract
{
  @Rule public ExpectedException expected = ExpectedException.none();

  protected abstract R2MBReaderType getMeshReader(
    String name,
    R2MeshParserInterleavedListenerType listener);

  @Test
  public final void testOneTriOK_0()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type gi = c.contextGetGL33();

    final R2VertexCursorP32UNT16 ci = R2VertexCursorP32UNT16.getInstance();
    final R2MeshArrayObjectSynchronousAdapterType adapter =
      R2MeshArrayObjectSynchronousAdapter.newAdapter(
        gi.arrayObjects(),
        gi.arrayBuffers(),
        gi.indexBuffers(),
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUnsignedType.TYPE_UNSIGNED_INT,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW,
        ci,
        ci);

    final R2MBReaderType reader = this.getMeshReader("one_tri.r2mb", adapter);
    reader.run();

    if (adapter.hasFailed()) {
      final Optional<Throwable> error_opt = adapter.errorException();
      error_opt.ifPresent(Throwable::printStackTrace);
    }

    Assert.assertFalse(adapter.hasFailed());

    /**
     * The array buffer should contain one vertex and have the correct
     * usage hints.
     */

    final JCGLArrayBufferType ab = adapter.arrayBuffer();
    Assert.assertEquals(JCGLUsageHint.USAGE_STATIC_DRAW, ab.usageHint());
    Assert.assertEquals(ci.vertexSize(), ab.byteRange().getInterval());

    /**
     * The index buffer should have three indices and have the correct
     * usage hints.
     */

    final JCGLIndexBufferType ib = adapter.indexBuffer();
    Assert.assertEquals(JCGLUsageHint.USAGE_DYNAMIC_DRAW, ib.usageHint());
    Assert.assertEquals(JCGLUnsignedType.TYPE_UNSIGNED_INT, ib.type());
    Assert.assertEquals(3L * 4L, ib.byteRange().getInterval());

    /**
     * The index buffer should describe a triangle of vertices.
     */

    {
      final JCGLIndexBuffersType g_ib = gi.indexBuffers();
      final JCGLArrayObjectsType g_ao = gi.arrayObjects();
      g_ao.arrayObjectBind(adapter.arrayObject());

      final ByteBuffer data = g_ib.indexBufferRead(
        ib, size -> {
          final ByteBuffer b = ByteBuffer.allocateDirect((int) size);
          b.order(ByteOrder.nativeOrder());
          return b;
        });

      final int i0 = data.getInt(0);
      final int i1 = data.getInt(4);
      final int i2 = data.getInt(8);

      System.out.printf(
        "[0] triangle %d %d %d\n",
        Integer.valueOf(i0),
        Integer.valueOf(i1),
        Integer.valueOf(i2));

      Assert.assertEquals(0L, (long) i0);
      Assert.assertEquals(1L, (long) i1);
      Assert.assertEquals(2L, (long) i2);

      g_ao.arrayObjectUnbind();
    }

    /**
     * The array object should have at least four attributes and have the right
     * index buffer.
     */

    final JCGLArrayObjectType ao = adapter.arrayObject();
    Assert.assertEquals(ao.indexBufferBound().get(), ib);
    Assert.assertTrue(ao.attributeAt(
      R2AttributeConventions.POSITION_ATTRIBUTE_INDEX).isPresent());
    Assert.assertTrue(ao.attributeAt(
      R2AttributeConventions.NORMAL_ATTRIBUTE_INDEX).isPresent());
    Assert.assertTrue(ao.attributeAt(
      R2AttributeConventions.TANGENT4_ATTRIBUTE_INDEX).isPresent());
    Assert.assertTrue(ao.attributeAt(
      R2AttributeConventions.UV_ATTRIBUTE_INDEX).isPresent());
  }

  @Test
  public final void testOneTriOK_1()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type gi = c.contextGetGL33();

    final R2VertexCursorP32UNT16 ci = R2VertexCursorP32UNT16.getInstance();
    final R2MeshArrayObjectSynchronousAdapterType adapter =
      R2MeshArrayObjectSynchronousAdapter.newAdapter(
        gi.arrayObjects(),
        gi.arrayBuffers(),
        gi.indexBuffers(),
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUnsignedType.TYPE_UNSIGNED_SHORT,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW,
        ci,
        ci);

    final R2MBReaderType reader = this.getMeshReader("one_tri.r2mb", adapter);
    reader.run();

    if (adapter.hasFailed()) {
      final Optional<Throwable> error_opt = adapter.errorException();
      error_opt.ifPresent(Throwable::printStackTrace);
    }

    Assert.assertFalse(adapter.hasFailed());

    /**
     * The array buffer should contain one vertex and have the correct
     * usage hints.
     */

    final JCGLArrayBufferType ab = adapter.arrayBuffer();
    Assert.assertEquals(JCGLUsageHint.USAGE_STATIC_DRAW, ab.usageHint());
    Assert.assertEquals(ci.vertexSize(), ab.byteRange().getInterval());

    /**
     * The index buffer should have three indices and have the correct
     * usage hints.
     */

    final JCGLIndexBufferType ib = adapter.indexBuffer();
    Assert.assertEquals(JCGLUsageHint.USAGE_DYNAMIC_DRAW, ib.usageHint());
    Assert.assertEquals(JCGLUnsignedType.TYPE_UNSIGNED_SHORT, ib.type());
    Assert.assertEquals(3L * 2L, ib.byteRange().getInterval());

    /**
     * The index buffer should describe a triangle of vertices.
     */

    {
      final JCGLIndexBuffersType g_ib = gi.indexBuffers();
      final JCGLArrayObjectsType g_ao = gi.arrayObjects();
      g_ao.arrayObjectBind(adapter.arrayObject());

      final ByteBuffer data = g_ib.indexBufferRead(
        ib, size -> {
          final ByteBuffer b = ByteBuffer.allocateDirect((int) size);
          b.order(ByteOrder.nativeOrder());
          return b;
        });

      final short i0 = data.getShort(0);
      final short i1 = data.getShort(2);
      final short i2 = data.getShort(4);

      System.out.printf(
        "[0] triangle %d %d %d\n",
        Short.valueOf(i0),
        Short.valueOf(i1),
        Short.valueOf(i2));

      Assert.assertEquals(0L, (long) i0);
      Assert.assertEquals(1L, (long) i1);
      Assert.assertEquals(2L, (long) i2);

      g_ao.arrayObjectUnbind();
    }

    /**
     * The array object should have at least four attributes and have the right
     * index buffer.
     */

    final JCGLArrayObjectType ao = adapter.arrayObject();
    Assert.assertEquals(ao.indexBufferBound().get(), ib);
    Assert.assertTrue(ao.attributeAt(
      R2AttributeConventions.POSITION_ATTRIBUTE_INDEX).isPresent());
    Assert.assertTrue(ao.attributeAt(
      R2AttributeConventions.NORMAL_ATTRIBUTE_INDEX).isPresent());
    Assert.assertTrue(ao.attributeAt(
      R2AttributeConventions.TANGENT4_ATTRIBUTE_INDEX).isPresent());
    Assert.assertTrue(ao.attributeAt(
      R2AttributeConventions.UV_ATTRIBUTE_INDEX).isPresent());
  }

  @Test
  public final void testOneTriOK_2()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type gi = c.contextGetGL33();

    final R2VertexCursorP32UNT16 ci = R2VertexCursorP32UNT16.getInstance();
    final R2MeshArrayObjectSynchronousAdapterType adapter =
      R2MeshArrayObjectSynchronousAdapter.newAdapter(
        gi.arrayObjects(),
        gi.arrayBuffers(),
        gi.indexBuffers(),
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUnsignedType.TYPE_UNSIGNED_BYTE,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW,
        ci,
        ci);

    final R2MBReaderType reader = this.getMeshReader("one_tri.r2mb", adapter);
    reader.run();

    if (adapter.hasFailed()) {
      final Optional<Throwable> error_opt = adapter.errorException();
      error_opt.ifPresent(Throwable::printStackTrace);
    }

    Assert.assertFalse(adapter.hasFailed());

    /**
     * The array buffer should contain one vertex and have the correct
     * usage hints.
     */

    final JCGLArrayBufferType ab = adapter.arrayBuffer();
    Assert.assertEquals(JCGLUsageHint.USAGE_STATIC_DRAW, ab.usageHint());
    Assert.assertEquals(ci.vertexSize(), ab.byteRange().getInterval());

    /**
     * The index buffer should have three indices and have the correct
     * usage hints.
     */

    final JCGLIndexBufferType ib = adapter.indexBuffer();
    Assert.assertEquals(JCGLUsageHint.USAGE_DYNAMIC_DRAW, ib.usageHint());
    Assert.assertEquals(JCGLUnsignedType.TYPE_UNSIGNED_BYTE, ib.type());
    Assert.assertEquals(3L, ib.byteRange().getInterval());

    /**
     * The index buffer should describe a triangle of vertices.
     */

    {
      final JCGLIndexBuffersType g_ib = gi.indexBuffers();
      final JCGLArrayObjectsType g_ao = gi.arrayObjects();
      g_ao.arrayObjectBind(adapter.arrayObject());

      final ByteBuffer data = g_ib.indexBufferRead(
        ib, size -> {
          final ByteBuffer b = ByteBuffer.allocateDirect((int) size);
          b.order(ByteOrder.nativeOrder());
          return b;
        });

      final byte i0 = data.get(0);
      final byte i1 = data.get(1);
      final byte i2 = data.get(2);

      System.out.printf(
        "[0] triangle %d %d %d\n",
        Byte.valueOf(i0),
        Byte.valueOf(i1),
        Byte.valueOf(i2));

      Assert.assertEquals(0L, (long) i0);
      Assert.assertEquals(1L, (long) i1);
      Assert.assertEquals(2L, (long) i2);

      g_ao.arrayObjectUnbind();
    }

    /**
     * The array object should have at least four attributes and have the right
     * index buffer.
     */

    final JCGLArrayObjectType ao = adapter.arrayObject();
    Assert.assertEquals(ao.indexBufferBound().get(), ib);
    Assert.assertTrue(ao.attributeAt(
      R2AttributeConventions.POSITION_ATTRIBUTE_INDEX).isPresent());
    Assert.assertTrue(ao.attributeAt(
      R2AttributeConventions.NORMAL_ATTRIBUTE_INDEX).isPresent());
    Assert.assertTrue(ao.attributeAt(
      R2AttributeConventions.TANGENT4_ATTRIBUTE_INDEX).isPresent());
    Assert.assertTrue(ao.attributeAt(
      R2AttributeConventions.UV_ATTRIBUTE_INDEX).isPresent());
  }

  @Test
  public final void testError()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type gi = c.contextGetGL33();

    final R2VertexCursorP32UNT16 ci = R2VertexCursorP32UNT16.getInstance();
    final R2MeshArrayObjectSynchronousAdapterType adapter =
      R2MeshArrayObjectSynchronousAdapter.newAdapter(
        gi.arrayObjects(),
        gi.arrayBuffers(),
        gi.indexBuffers(),
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUnsignedType.TYPE_UNSIGNED_INT,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW,
        ci,
        ci);

    final R2MBReaderType reader =
      this.getMeshReader("bad_magic.r2mb", adapter);
    reader.run();

    if (adapter.hasFailed()) {
      final Optional<Throwable> error_opt = adapter.errorException();
      error_opt.ifPresent(Throwable::printStackTrace);
    }

    Assert.assertTrue(adapter.hasFailed());
    Assert.assertThat(
      adapter.errorMessage(), new StringStartsWith("Bad magic number"));
    Assert.assertFalse(adapter.errorException().isPresent());
  }

  @Test
  public final void testErrorArrayObject()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type gi = c.contextGetGL33();

    final R2VertexCursorP32UNT16 ci = R2VertexCursorP32UNT16.getInstance();
    final R2MeshArrayObjectSynchronousAdapterType adapter =
      R2MeshArrayObjectSynchronousAdapter.newAdapter(
        gi.arrayObjects(),
        gi.arrayBuffers(),
        gi.indexBuffers(),
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUnsignedType.TYPE_UNSIGNED_INT,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW,
        ci,
        ci);

    final R2MBReaderType reader =
      this.getMeshReader("bad_magic.r2mb", adapter);
    reader.run();

    Assert.assertTrue(adapter.hasFailed());
    this.expected.expect(IllegalStateException.class);
    adapter.arrayObject();
  }

  @Test
  public final void testErrorArrayBuffer()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type gi = c.contextGetGL33();

    final R2VertexCursorP32UNT16 ci = R2VertexCursorP32UNT16.getInstance();
    final R2MeshArrayObjectSynchronousAdapterType adapter =
      R2MeshArrayObjectSynchronousAdapter.newAdapter(
        gi.arrayObjects(),
        gi.arrayBuffers(),
        gi.indexBuffers(),
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUnsignedType.TYPE_UNSIGNED_INT,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW,
        ci,
        ci);

    final R2MBReaderType reader =
      this.getMeshReader("bad_magic.r2mb", adapter);
    reader.run();

    Assert.assertTrue(adapter.hasFailed());
    this.expected.expect(IllegalStateException.class);
    adapter.arrayBuffer();
  }

  @Test
  public final void testErrorIndexBuffer()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type gi = c.contextGetGL33();

    final R2VertexCursorP32UNT16 ci = R2VertexCursorP32UNT16.getInstance();
    final R2MeshArrayObjectSynchronousAdapterType adapter =
      R2MeshArrayObjectSynchronousAdapter.newAdapter(
        gi.arrayObjects(),
        gi.arrayBuffers(),
        gi.indexBuffers(),
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUnsignedType.TYPE_UNSIGNED_INT,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW,
        ci,
        ci);

    final R2MBReaderType reader =
      this.getMeshReader("bad_magic.r2mb", adapter);
    reader.run();

    Assert.assertTrue(adapter.hasFailed());
    this.expected.expect(IllegalStateException.class);
    adapter.indexBuffer();
  }


  @Test
  public final void testNoErrorMessage()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type gi = c.contextGetGL33();

    final R2VertexCursorP32UNT16 ci = R2VertexCursorP32UNT16.getInstance();
    final R2MeshArrayObjectSynchronousAdapterType adapter =
      R2MeshArrayObjectSynchronousAdapter.newAdapter(
        gi.arrayObjects(),
        gi.arrayBuffers(),
        gi.indexBuffers(),
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUnsignedType.TYPE_UNSIGNED_BYTE,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW,
        ci,
        ci);

    final R2MBReaderType reader = this.getMeshReader("one_tri.r2mb", adapter);
    reader.run();

    Assert.assertFalse(adapter.hasFailed());
    this.expected.expect(IllegalStateException.class);
    adapter.errorMessage();
  }

  @Test
  public final void testNoErrorException()
  {
    final JCGLContextType c = this.newGL33Context("main", 24, 8);
    final JCGLInterfaceGL33Type gi = c.contextGetGL33();

    final R2VertexCursorP32UNT16 ci = R2VertexCursorP32UNT16.getInstance();
    final R2MeshArrayObjectSynchronousAdapterType adapter =
      R2MeshArrayObjectSynchronousAdapter.newAdapter(
        gi.arrayObjects(),
        gi.arrayBuffers(),
        gi.indexBuffers(),
        JCGLUsageHint.USAGE_STATIC_DRAW,
        JCGLUnsignedType.TYPE_UNSIGNED_BYTE,
        JCGLUsageHint.USAGE_DYNAMIC_DRAW,
        ci,
        ci);

    final R2MBReaderType reader = this.getMeshReader("one_tri.r2mb", adapter);
    reader.run();

    Assert.assertFalse(adapter.hasFailed());
    this.expected.expect(IllegalStateException.class);
    adapter.errorException();
  }
}