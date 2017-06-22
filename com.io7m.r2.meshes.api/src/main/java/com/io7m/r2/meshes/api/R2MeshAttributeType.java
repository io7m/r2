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

package com.io7m.r2.meshes.api;

import com.io7m.jcanephora.core.JCGLScalarType;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.annotations.R2ImmutableStyleType;
import org.immutables.value.Value;

import static com.io7m.r2.meshes.api.R2MeshAttributeSupportedSizes.checkFloatSupported;
import static com.io7m.r2.meshes.api.R2MeshAttributeSupportedSizes.checkIntegerSignedSupported;
import static com.io7m.r2.meshes.api.R2MeshAttributeSupportedSizes.checkIntegerUnsignedSupported;

/**
 * The type of mesh attributes.
 */

@R2ImmutableStyleType
@Value.Immutable
public interface R2MeshAttributeType
{
  /**
   * @return The attribute index
   */

  @Value.Parameter
  int index();

  /**
   * @return The attribute name
   */

  @Value.Parameter
  String name();

  /**
   * @return The attribute semantic
   */

  @Value.Parameter
  R2MeshAttributeSemantic semantic();

  /**
   * @return The attribute component type
   */

  @Value.Parameter
  R2MeshAttributeComponents componentType();

  /**
   * @return The attribute component count
   */

  @Value.Parameter
  int componentCount();

  /**
   * @return The size of a single attribute component in bits
   */

  @Value.Parameter
  int componentSizeBits();

  /**
   * @return The size of a single component in octets
   */

  @Value.Derived
  default int componentSizeOctets()
  {
    return (int) Math.ceil((double) this.componentSizeBits() / 8.0);
  }

  /**
   * @return The size of the attribute in octets
   */

  @Value.Derived
  default int sizeOctets()
  {
    return Math.multiplyExact(
      this.componentSizeOctets(),
      this.componentCount());
  }

  /**
   * Check preconditions for the type.
   */

  @Value.Check
  default void checkPreconditions()
  {
    if (Integer.compareUnsigned(this.componentCount(), 1) < 0) {
      throw new IllegalArgumentException(
        "Component count must be in the range [1, 4]");
    }
    if (Integer.compareUnsigned(this.componentCount(), 4) > 0) {
      throw new IllegalArgumentException(
        "Component count must be in the range [1, 4]");
    }

    switch (this.componentType()) {
      case R2_COMPONENT_INTEGER_SIGNED: {
        checkIntegerSignedSupported(this.name(), this.componentSizeBits());
        break;
      }
      case R2_COMPONENT_INTEGER_UNSIGNED: {
        checkIntegerUnsignedSupported(this.name(), this.componentSizeBits());
        break;
      }
      case R2_COMPONENT_FLOATING: {
        checkFloatSupported(this.name(), this.componentSizeBits());
        break;
      }
    }
  }

  /**
   * @return The {@code jcanephora} scalar type that corresponds to the
   * component type of this attribute.
   */

  default JCGLScalarType componentScalarType()
  {
    switch (this.componentType()) {

      case R2_COMPONENT_FLOATING: {
        switch (this.componentSizeBits()) {
          case 16:
            return JCGLScalarType.TYPE_HALF_FLOAT;
          case 32:
            return JCGLScalarType.TYPE_FLOAT;
          default:
            throw new UnreachableCodeException();
        }
      }

      case R2_COMPONENT_INTEGER_SIGNED: {
        switch (this.componentSizeBits()) {
          case 8:
            return JCGLScalarType.TYPE_BYTE;
          case 16:
            return JCGLScalarType.TYPE_SHORT;
          case 32:
            return JCGLScalarType.TYPE_INT;
          default:
            throw new UnreachableCodeException();
        }
      }

      case R2_COMPONENT_INTEGER_UNSIGNED: {
        switch (this.componentSizeBits()) {
          case 8:
            return JCGLScalarType.TYPE_UNSIGNED_BYTE;
          case 16:
            return JCGLScalarType.TYPE_UNSIGNED_SHORT;
          case 32:
            return JCGLScalarType.TYPE_UNSIGNED_INT;
          default:
            throw new UnreachableCodeException();
        }
      }
    }

    throw new UnreachableCodeException();
  }
}
