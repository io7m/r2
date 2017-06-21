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

package com.io7m.r2.meshes.loading.smf;

import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.meshes.api.R2MeshAttributeSemantic;
import com.io7m.smfj.core.SMFAttributeName;
import com.io7m.smfj.core.SMFComponentType;
import com.io7m.smfj.core.SMFSchemaIdentifier;
import com.io7m.smfj.core.SMFSchemaName;
import com.io7m.smfj.validation.api.SMFSchemaAttribute;
import javaslang.collection.HashMap;

import java.util.Optional;
import java.util.OptionalInt;

import static com.io7m.r2.meshes.api.R2MeshAttributeConventions.NORMAL_ATTRIBUTE_INDEX;
import static com.io7m.r2.meshes.api.R2MeshAttributeConventions.POSITION_ATTRIBUTE_INDEX;
import static com.io7m.r2.meshes.api.R2MeshAttributeConventions.TANGENT4_ATTRIBUTE_INDEX;
import static com.io7m.r2.meshes.api.R2MeshAttributeConventions.UV_ATTRIBUTE_INDEX;
import static com.io7m.r2.meshes.api.R2MeshAttributeSemantic.R2_MESH_ATTRIBUTE_NORMAL;
import static com.io7m.r2.meshes.api.R2MeshAttributeSemantic.R2_MESH_ATTRIBUTE_POSITION;
import static com.io7m.r2.meshes.api.R2MeshAttributeSemantic.R2_MESH_ATTRIBUTE_TANGENT4;
import static com.io7m.r2.meshes.api.R2MeshAttributeSemantic.R2_MESH_ATTRIBUTE_UNKNOWN;
import static com.io7m.r2.meshes.api.R2MeshAttributeSemantic.R2_MESH_ATTRIBUTE_UV;

/**
 * Schemas for R2-compatible SMF meshes.
 */

public final class R2SMFSchemas
{
  /**
   * The required name of the position data attribute.
   */

  public static final SMFAttributeName R2_POSITION_NAME;

  /**
   * The type of position attributes.
   */

  public static final SMFSchemaAttribute R2_POSITION;

  /**
   * The required name of the UV data attribute.
   */

  public static final SMFAttributeName R2_UV_NAME;

  /**
   * The type of UV attributes.
   */

  public static final SMFSchemaAttribute R2_UV;

  /**
   * The required name of the normal data attribute.
   */

  public static final SMFAttributeName R2_NORMAL_NAME;

  /**
   * The type of normal attributes.
   */

  public static final SMFSchemaAttribute R2_NORMAL;

  /**
   * The required name of the tangent4 data attribute.
   */

  public static final SMFAttributeName R2_TANGENT4_NAME;

  /**
   * The type of tangent4 attributes.
   */

  public static final SMFSchemaAttribute R2_TANGENT4;

  /**
   * The schema identifier for R2 meshes.
   */

  public static final SMFSchemaIdentifier R2_SCHEMA_IDENTIFIER;

  private static final HashMap<SMFAttributeName, R2MeshAttributeSemantic> R2_ATTRIBUTE_SEMANTICS;

  private static final HashMap<SMFAttributeName, Integer> R2_ATTRIBUTE_INDICES;

  static {
    R2_POSITION_NAME =
      SMFAttributeName.of("R2_POSITION");

    R2_POSITION =
      SMFSchemaAttribute.of(
        R2_POSITION_NAME,
        Optional.of(SMFComponentType.ELEMENT_TYPE_FLOATING),
        OptionalInt.of(3),
        OptionalInt.empty());

    R2_UV_NAME =
      SMFAttributeName.of("R2_UV");

    R2_UV =
      SMFSchemaAttribute.of(
        R2_UV_NAME,
        Optional.of(SMFComponentType.ELEMENT_TYPE_FLOATING),
        OptionalInt.of(2),
        OptionalInt.empty());

    R2_NORMAL_NAME =
      SMFAttributeName.of("R2_NORMAL");

    R2_NORMAL =
      SMFSchemaAttribute.of(
        R2_NORMAL_NAME,
        Optional.of(SMFComponentType.ELEMENT_TYPE_FLOATING),
        OptionalInt.of(3),
        OptionalInt.empty());

    R2_TANGENT4_NAME =
      SMFAttributeName.of("R2_TANGENT4");

    R2_TANGENT4 =
      SMFSchemaAttribute.of(
        R2_TANGENT4_NAME,
        Optional.of(SMFComponentType.ELEMENT_TYPE_FLOATING),
        OptionalInt.of(4),
        OptionalInt.empty());

    R2_SCHEMA_IDENTIFIER =
      SMFSchemaIdentifier.of(
        SMFSchemaName.of("com.io7m.r2.mesh.standard"),
        1,
        0);

    R2_ATTRIBUTE_SEMANTICS =
      HashMap.of(
        R2_POSITION_NAME, R2_MESH_ATTRIBUTE_POSITION,
        R2_NORMAL_NAME, R2_MESH_ATTRIBUTE_NORMAL,
        R2_UV_NAME, R2_MESH_ATTRIBUTE_UV,
        R2_TANGENT4_NAME, R2_MESH_ATTRIBUTE_TANGENT4);

    R2_ATTRIBUTE_INDICES =
      HashMap.of(
        R2_POSITION_NAME, Integer.valueOf(POSITION_ATTRIBUTE_INDEX),
        R2_NORMAL_NAME, Integer.valueOf(NORMAL_ATTRIBUTE_INDEX),
        R2_UV_NAME, Integer.valueOf(UV_ATTRIBUTE_INDEX),
        R2_TANGENT4_NAME, Integer.valueOf(TANGENT4_ATTRIBUTE_INDEX));
  }

  private R2SMFSchemas()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Determine the mesh semantic for the given name.
   *
   * @param name The name
   *
   * @return The mesh semantic
   */

  public static R2MeshAttributeSemantic semanticForName(
    final SMFAttributeName name)
  {
    NullCheck.notNull(name, "Name");
    return R2_ATTRIBUTE_SEMANTICS.getOrElse(name, R2_MESH_ATTRIBUTE_UNKNOWN);
  }

  /**
   * Determine the mesh semantic for the given name.
   *
   * @param name The name
   *
   * @return The mesh semantic
   */

  public static OptionalInt attributeIndexForName(
    final SMFAttributeName name)
  {
    NullCheck.notNull(name, "Name");
    if (R2_ATTRIBUTE_INDICES.containsKey(name)) {
      return OptionalInt.of(R2_ATTRIBUTE_INDICES.get(name).get().intValue());
    }
    return OptionalInt.empty();
  }
}
