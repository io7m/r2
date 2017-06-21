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

package com.io7m.r2.meshes.loading.api;

import com.io7m.jcanephora.core.JCGLArrayBufferType;
import com.io7m.jcanephora.core.JCGLArrayObjectBuilderType;
import com.io7m.jcanephora.core.JCGLArrayObjectType;
import com.io7m.jcanephora.core.JCGLIndexBufferType;
import com.io7m.jcanephora.core.JCGLScalarIntegralType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.r2.meshes.api.R2MeshAttribute;
import com.io7m.r2.meshes.api.R2MeshAttributePacked;
import com.io7m.r2.meshes.api.R2MeshesImmutableStyleType;
import org.immutables.value.Value;

import java.util.SortedMap;

/**
 * The type of meshes loaded onto the GPU.
 */

@R2MeshesImmutableStyleType
@Value.Immutable
public interface R2MeshLoadedType
{
  /**
   * @return The buffer containing vertex data
   */

  @Value.Parameter
  JCGLArrayBufferType arrayBuffer();

  /**
   * @return The buffer containing index data
   */

  @Value.Parameter
  JCGLIndexBufferType indexBuffer();

  /**
   * The map of packed attributes. The attributes must be packed according
   * to the R2 attribute conventions if they are to be of general use with
   * the provided shaders.
   *
   * @return The attributes as they appear on the GPU
   *
   * @see com.io7m.r2.meshes.api.R2MeshAttributeConventions
   */

  @Value.Parameter
  SortedMap<Integer, R2MeshAttributePacked> attributes();

  /**
   * @return The size in octets of a single vertex
   */

  @Value.Derived
  default int vertexSizeOctets()
  {
    int size = 0;
    final SortedMap<Integer, R2MeshAttributePacked> attrs = this.attributes();
    for (final Integer index : attrs.keySet()) {
      final R2MeshAttributePacked attribute = attrs.get(index);
      size = Math.addExact(size, attribute.attribute().sizeOctets());
    }
    return size;
  }

  /**
   * Generate an array object given the loaded mesh's configuration.
   *
   * @param array_objects An array object interface
   *
   * @return A new array object
   */

  default JCGLArrayObjectType newArrayObject(
    final JCGLArrayObjectsType array_objects)
  {
    array_objects.arrayObjectUnbind();

    final JCGLArrayObjectBuilderType builder =
      array_objects.arrayObjectNewBuilder();

    builder.setIndexBuffer(this.indexBuffer());
    builder.setStrictChecking(true);

    final SortedMap<Integer, R2MeshAttributePacked> attrs = this.attributes();
    for (final Integer attribute_index : attrs.keySet()) {
      final R2MeshAttributePacked packed = attrs.get(attribute_index);
      final R2MeshAttribute attr = packed.attribute();

      switch (attr.componentType()) {
        case R2_COMPONENT_FLOATING:
          builder.setAttributeFloatingPoint(
            attribute_index.intValue(),
            this.arrayBuffer(),
            attr.componentCount(),
            attr.componentScalarType(),
            this.vertexSizeOctets(),
            (long) packed.offsetOctets(),
            false);
          break;
        case R2_COMPONENT_INTEGER_UNSIGNED:
        case R2_COMPONENT_INTEGER_SIGNED:
          builder.setAttributeIntegral(
            attribute_index.intValue(),
            this.arrayBuffer(),
            attr.componentCount(),
            JCGLScalarIntegralType.fromScalar(attr.componentScalarType()),
            this.vertexSizeOctets(),
            (long) packed.offsetOctets());
          break;
      }
    }

    return array_objects.arrayObjectAllocate(builder);
  }
}
