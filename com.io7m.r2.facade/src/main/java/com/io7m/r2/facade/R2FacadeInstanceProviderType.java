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

import com.io7m.jcanephora.core.JCGLArrayObjectUsableType;
import com.io7m.jtensors.core.parameterized.matrices.PMatrices3x3D;
import com.io7m.jtensors.core.parameterized.matrices.PMatrix3x3D;
import com.io7m.r2.annotations.R2ImmutableStyleType;
import com.io7m.r2.core.R2InstanceBatchedDynamic;
import com.io7m.r2.core.R2InstanceBillboardedDynamic;
import com.io7m.r2.core.R2InstanceSingle;
import com.io7m.r2.core.R2TransformReadableType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import org.immutables.value.Value;

/**
 * The type of convenient instance providers.
 */

@Value.Immutable
@R2ImmutableStyleType
public interface R2FacadeInstanceProviderType
{
  /**
   * @return The current facade
   */

  @Value.Auxiliary
  @Value.Parameter
  R2FacadeType main();

  /**
   * Create a new single instance.
   *
   * @param array_object The mesh to be used for the instance
   * @param transform    The instance transform
   * @param uv_matrix    The per-instance UV matrix
   *
   * @return A new single instance
   */

  default R2InstanceSingle createSingle(
    final JCGLArrayObjectUsableType array_object,
    final R2TransformReadableType transform,
    final PMatrix3x3D<R2SpaceTextureType, R2SpaceTextureType> uv_matrix)
  {
    return R2InstanceSingle.of(
      this.main().idPool().freshID(),
      array_object,
      transform,
      uv_matrix);
  }

  /**
   * Create a new single instance with an identity UV matrix.
   *
   * @param array_object The mesh to be used for the instance
   * @param transform    The instance transform
   *
   * @return A new single instance
   */

  default R2InstanceSingle createSingle(
    final JCGLArrayObjectUsableType array_object,
    final R2TransformReadableType transform)
  {
    return this.createSingle(
      array_object, transform, PMatrices3x3D.identity());
  }

  /**
   * Create a new billboarded instance.
   *
   * @param size The size of the billboarded instance
   *
   * @return A new billboarded instance
   */

  default R2InstanceBillboardedDynamic createBillboardedDynamic(
    final int size)
  {
    return R2InstanceBillboardedDynamic.create(
      this.main().idPool(),
      this.main().rendererGL33().arrayBuffers(),
      this.main().rendererGL33().arrayObjects(),
      size);
  }

  /**
   * Create a new batched instance.
   *
   * @param array_object The mesh to be used for the instance
   * @param size         The size of the batched instance
   *
   * @return A new billboarded instance
   */

  default R2InstanceBatchedDynamic createBatchedDynamic(
    final JCGLArrayObjectUsableType array_object,
    final int size)
  {
    return R2InstanceBatchedDynamic.create(
      this.main().idPool(),
      this.main().rendererGL33().arrayBuffers(),
      this.main().rendererGL33().arrayObjects(),
      array_object,
      size);
  }

  /**
   * Create a single instance that uses a unit cube as the mesh.
   *
   * @param transform The mesh transform
   *
   * @return A new single instance
   */

  default R2InstanceSingle createCubeSingle(
    final R2TransformReadableType transform)
  {
    return this.createSingle(
      this.main().unitCube().arrayObject(),
      transform);
  }

  /**
   * Create a single instance that uses a unit sphere as the mesh.
   *
   * @param transform The mesh transform
   *
   * @return A new single instance
   */

  default R2InstanceSingle createSphere8Single(
    final R2TransformReadableType transform)
  {
    return this.createSingle(
      this.main().unitSphere8().arrayObject(),
      transform);
  }

  /**
   * Create a single instance that uses a unit quad as the mesh.
   *
   * @param transform The mesh transform
   *
   * @return A new single instance
   */

  default R2InstanceSingle createQuadSingle(
    final R2TransformReadableType transform)
  {
    return this.createSingle(
      this.main().unitQuad().arrayObject(),
      transform);
  }

  /**
   * Create a new batched instance using a unit sphere as the base.
   *
   * @param size The size of the batched instance
   *
   * @return A new batched instance
   */

  default R2InstanceBatchedDynamic createSphere8BatchedDynamic(
    final int size)
  {
    return this.createBatchedDynamic(
      this.main().unitSphere8().arrayObject(),
      size);
  }
}
