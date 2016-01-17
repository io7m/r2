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

package com.io7m.r2.core;

import com.io7m.jcanephora.core.JCGLDepthFunction;
import com.io7m.jcanephora.core.JCGLFaceSelection;
import com.io7m.jcanephora.core.JCGLFaceWindingOrder;
import com.io7m.jcanephora.core.JCGLFramebufferUsableType;
import com.io7m.jcanephora.core.JCGLPrimitives;
import com.io7m.jcanephora.core.JCGLStencilFunction;
import com.io7m.jcanephora.core.JCGLStencilOperation;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLBlendingType;
import com.io7m.jcanephora.core.api.JCGLColorBufferMaskingType;
import com.io7m.jcanephora.core.api.JCGLCullingType;
import com.io7m.jcanephora.core.api.JCGLDepthBuffersType;
import com.io7m.jcanephora.core.api.JCGLDrawType;
import com.io7m.jcanephora.core.api.JCGLFramebuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jcanephora.core.api.JCGLShadersType;
import com.io7m.jcanephora.core.api.JCGLStencilBuffersType;
import com.io7m.jcanephora.core.api.JCGLTexturesType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import org.valid4j.Assertive;

/**
 * The default implementation of the {@link R2GeometryRendererType} interface.
 */

public final class R2GeometryRenderer implements R2GeometryRendererType
{
  private final OpaqueConsumer opaque_consumer;
  private       boolean        deleted;

  private R2GeometryRenderer()
  {
    this.opaque_consumer = new OpaqueConsumer();
  }

  /**
   * @return A new renderer
   */

  public static R2GeometryRendererType newRenderer()
  {
    return new R2GeometryRenderer();
  }

  @Override
  public void renderGeometry(
    final JCGLInterfaceGL33Type g,
    final R2GeometryBufferUsableType gbuffer,
    final R2MatricesObserverType m,
    final R2SceneOpaquesType s)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(gbuffer);
    NullCheck.notNull(m);
    NullCheck.notNull(s);

    Assertive.require(!this.isDeleted(), "Renderer not deleted");

    final JCGLFramebufferUsableType gb_fb = gbuffer.getFramebuffer();
    final JCGLFramebuffersType g_fb = g.getFramebuffers();

    try {
      g_fb.framebufferDrawBind(gb_fb);
      this.renderGeometryWithBoundBuffer(g, m, s);
    } finally {
      g_fb.framebufferDrawUnbind();
    }
  }

  @Override
  public void renderGeometryWithBoundBuffer(
    final JCGLInterfaceGL33Type g,
    final R2MatricesObserverType m,
    final R2SceneOpaquesType s)
  {
    NullCheck.notNull(g);
    NullCheck.notNull(m);
    NullCheck.notNull(s);

    Assertive.require(!this.isDeleted(), "Renderer not deleted");

    final JCGLFramebuffersType g_fb = g.getFramebuffers();
    Assertive.require(g_fb.framebufferDrawAnyIsBound());

    final JCGLDepthBuffersType g_db = g.getDepthBuffers();
    final JCGLBlendingType g_b = g.getBlending();
    final JCGLColorBufferMaskingType g_cm = g.getColorBufferMasking();
    final JCGLCullingType g_cu = g.getCulling();

    if (s.opaquesCount() > 0L) {

      /**
       * Configure state for geometry rendering.
       */

      g_b.blendingDisable();
      g_cm.colorBufferMask(true, true, true, true);
      g_cu.cullingEnable(
        JCGLFaceSelection.FACE_BACK,
        JCGLFaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE);
      g_db.depthClampingEnable();
      g_db.depthBufferWriteEnable();
      g_db.depthBufferTestEnable(JCGLDepthFunction.DEPTH_LESS_THAN);

      this.opaque_consumer.g33 = g;
      this.opaque_consumer.matrices = m;
      try {
        s.opaquesExecute(this.opaque_consumer);
      } finally {
        this.opaque_consumer.matrices = null;
        this.opaque_consumer.g33 = null;
      }
    }
  }

  @Override
  public void delete(final JCGLInterfaceGL33Type g3)
    throws R2Exception
  {
    this.deleted = true;
  }

  @Override
  public boolean isDeleted()
  {
    return this.deleted;
  }

  private static final class OpaqueConsumer implements
    R2SceneOpaquesConsumerType
  {
    private @Nullable JCGLInterfaceGL33Type  g33;
    private @Nullable R2MatricesObserverType matrices;
    private @Nullable JCGLShadersType        shaders;
    private @Nullable JCGLTexturesType       textures;
    private @Nullable JCGLArrayObjectsType   array_objects;
    private @Nullable JCGLDrawType           draw;
    private @Nullable JCGLStencilBuffersType stencils;

    private OpaqueConsumer()
    {

    }

    @Override
    public void onStart()
    {
      Assertive.require(this.g33 != null);

      this.shaders = this.g33.getShaders();
      this.textures = this.g33.getTextures();
      this.array_objects = this.g33.getArrayObjects();
      this.draw = this.g33.getDraw();
      this.stencils = this.g33.getStencilBuffers();
    }

    @Override
    public void onStartGroup(final int group)
    {
      this.stencils.stencilBufferEnable();
      this.stencils.stencilBufferOperation(
        JCGLFaceSelection.FACE_FRONT_AND_BACK,
        JCGLStencilOperation.STENCIL_OP_KEEP,
        JCGLStencilOperation.STENCIL_OP_KEEP,
        JCGLStencilOperation.STENCIL_OP_REPLACE);
      this.stencils.stencilBufferMask(
        JCGLFaceSelection.FACE_FRONT_AND_BACK,
        R2Stencils.GROUP_BITS);
      this.stencils.stencilBufferFunction(
        JCGLFaceSelection.FACE_FRONT_AND_BACK,
        JCGLStencilFunction.STENCIL_EQUAL,
        group | R2Stencils.ALLOW_BIT,
        R2Stencils.ALLOW_BIT);
    }

    @Override
    public void onInstanceBatchedUpdate(
      final R2InstanceBatchedType i)
    {
      i.update(this.g33, this.matrices.getTransformContext());
    }

    @Override
    public <M> void onInstanceBatchedShaderStart(
      final R2ShaderBatchedUsableType<M> s)
    {
      this.shaders.shaderActivateProgram(s.getShaderProgram());
      s.setMatricesView(this.shaders, this.matrices);
    }

    @Override
    public <M> void onInstanceBatchedMaterialStart(
      final R2MaterialOpaqueBatchedType<M> material)
    {
      final R2ShaderBatchedUsableType<M> s = material.getShader();
      final M p = material.getShaderParameters();
      s.setMaterialTextures(this.textures, p);
      s.setMaterialValues(this.shaders, this.textures, p);
    }

    @Override
    public <M> void onInstanceBatched(
      final R2MaterialOpaqueBatchedType<M> material,
      final R2InstanceBatchedType i)
    {
      this.array_objects.arrayObjectBind(i.getArrayObject());
      this.draw.drawElementsInstanced(
        JCGLPrimitives.PRIMITIVE_TRIANGLES, i.getRenderCount());
    }

    @Override
    public <M> void onInstanceBatchedMaterialFinish(
      final R2MaterialOpaqueBatchedType<M> material)
    {

    }

    @Override
    public <M> void onInstanceBatchedShaderFinish(
      final R2ShaderBatchedUsableType<M> s)
    {
      this.shaders.shaderDeactivateProgram();
    }

    @Override
    public <M> void onInstanceSingleShaderStart(
      final R2ShaderInstanceSingleUsableType<M> s)
    {
      this.shaders.shaderActivateProgram(s.getShaderProgram());
      s.setMatricesView(this.shaders, this.matrices);
    }

    @Override
    public <M> void onInstanceSingleMaterialStart(
      final R2MaterialOpaqueSingleType<M> material)
    {
      final R2ShaderInstanceSingleUsableType<M> s = material.getShader();
      final M p = material.getShaderParameters();
      s.setMaterialTextures(this.textures, p);
      s.setMaterialValues(this.shaders, this.textures, p);
    }

    @Override
    public void onInstanceSingleArrayStart(
      final R2InstanceSingleType i)
    {
      this.array_objects.arrayObjectBind(i.getArrayObject());
    }

    @Override
    public <M> void onInstanceSingle(
      final R2MaterialOpaqueSingleType<M> material,
      final R2InstanceSingleType i)
    {
      this.matrices.withTransform(i.getTransform(), i.getUVMatrix(), mi -> {
        final R2ShaderInstanceSingleUsableType<M> s = material.getShader();
        s.setMatricesInstance(this.shaders, mi);
        this.draw.drawElements(JCGLPrimitives.PRIMITIVE_TRIANGLES);
        return Unit.unit();
      });
    }

    @Override
    public <M> void onInstanceSingleMaterialFinish(
      final R2MaterialOpaqueSingleType<M> material)
    {

    }

    @Override
    public <M> void onInstanceSingleShaderFinish(
      final R2ShaderInstanceSingleUsableType<M> s)
    {
      this.shaders.shaderDeactivateProgram();
    }

    @Override
    public void onFinishGroup(final int group)
    {

    }

    @Override
    public void onFinish()
    {
      this.array_objects = null;
      this.shaders = null;
      this.draw = null;
      this.textures = null;
    }
  }
}
