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

package com.io7m.r2.shaders.translucent.api;

import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.r2.matrices.R2MatricesInstanceSingleValuesType;
import com.io7m.r2.shaders.api.R2ShaderParametersMaterialType;
import com.io7m.r2.shaders.api.R2ShaderParametersViewType;

/**
 * The type of usable shaders for rendering the surfaces of translucent single
 * instances.
 *
 * @param <M> The type of parameter data
 */

public interface R2ShaderTranslucentInstanceSingleUsableType<M>
  extends R2ShaderTranslucentUsableType<M>
{
  /**
   * <p>Set shader values that are derived from the current view.</p>
   *
   * <p>This method will be called exactly once between calls to {@link
   * com.io7m.r2.shaders.api.R2ShaderUsableType#onActivate(JCGLInterfaceGL33Type)}
   * and {@link #onValidate()}.</p>
   *
   * @param g               An OpenGL interface
   * @param view_parameters The view parameters
   */

  void onReceiveViewValues(
    JCGLInterfaceGL33Type g,
    R2ShaderParametersViewType view_parameters);

  /**
   * <p>Set material values.</p>
   *
   * <p>This method will be called once for each group of instances that use the
   * material {@code M}.</p>
   *
   * <p>This method will be called after a call to {@link
   * com.io7m.r2.shaders.api.R2ShaderUsableType#onActivate(JCGLInterfaceGL33Type)}
   * and before a call to {@link #onValidate()}.</p>
   *
   * @param g              A texture interface
   * @param mat_parameters The material parameters
   */

  void onReceiveMaterialValues(
    JCGLInterfaceGL33Type g,
    R2ShaderParametersMaterialType<M> mat_parameters);

  /**
   * <p>Set shader values that are derived from the current instance
   * transform.</p>
   *
   * <p>This method will be called exactly once for each single instance that
   * uses the shader.</p>
   *
   * <p>This method will be called after a call to {@link
   * com.io7m.r2.shaders.api.R2ShaderUsableType#onActivate(JCGLInterfaceGL33Type)}
   * and before a call to {@link #onValidate()}.</p>
   *
   * @param g An OpenGL interface
   * @param m The instance matrices
   */

  void onReceiveInstanceTransformValues(
    JCGLInterfaceGL33Type g,
    R2MatricesInstanceSingleValuesType m);
}
