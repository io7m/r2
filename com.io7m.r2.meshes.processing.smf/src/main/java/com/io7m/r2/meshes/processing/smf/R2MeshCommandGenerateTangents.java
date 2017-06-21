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

package com.io7m.r2.meshes.processing.smf;

import com.io7m.jtensors.core.parameterized.vectors.PVector2D;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.parameterized.vectors.PVector4D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector4D;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.meshes.api.R2MeshAttributeComponents;
import com.io7m.r2.meshes.api.R2MeshBasic;
import com.io7m.r2.meshes.api.R2MeshBasicVertex;
import com.io7m.r2.meshes.api.R2MeshTriangle;
import com.io7m.r2.meshes.tangents.R2MeshTangents;
import com.io7m.r2.meshes.tangents.R2MeshTangentsGenerator;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import com.io7m.smfj.core.SMFAttribute;
import com.io7m.smfj.core.SMFAttributeName;
import com.io7m.smfj.core.SMFComponentType;
import com.io7m.smfj.core.SMFHeader;
import com.io7m.smfj.parser.api.SMFParseError;
import com.io7m.smfj.processing.api.SMFAttributeArrayFloating2;
import com.io7m.smfj.processing.api.SMFAttributeArrayFloating3;
import com.io7m.smfj.processing.api.SMFAttributeArrayFloating4;
import com.io7m.smfj.processing.api.SMFAttributeArrayType;
import com.io7m.smfj.processing.api.SMFFilterCommandContext;
import com.io7m.smfj.processing.api.SMFMemoryMesh;
import com.io7m.smfj.processing.api.SMFMemoryMeshFilterType;
import com.io7m.smfj.processing.api.SMFProcessingError;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.collection.SortedMap;
import javaslang.collection.Vector;
import javaslang.control.Validation;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.jnull.NullCheck.notNull;
import static com.io7m.r2.meshes.api.R2MeshAttributeComponents.R2_COMPONENT_FLOATING;
import static com.io7m.smfj.core.SMFComponentType.ELEMENT_TYPE_FLOATING;
import static com.io7m.smfj.core.SMFComponentType.ELEMENT_TYPE_INTEGER_SIGNED;
import static com.io7m.smfj.core.SMFComponentType.ELEMENT_TYPE_INTEGER_UNSIGNED;
import static com.io7m.smfj.processing.api.SMFFilterCommandChecks.checkAttributeExists;
import static com.io7m.smfj.processing.api.SMFFilterCommandChecks.checkAttributeNonexistent;
import static com.io7m.smfj.processing.api.SMFFilterCommandParsing.errorExpectedGotValidation;
import static javaslang.control.Validation.invalid;
import static javaslang.control.Validation.valid;

/**
 * A filter that generates tangent vectors for a mesh.
 */

public final class R2MeshCommandGenerateTangents
  implements SMFMemoryMeshFilterType
{
  /**
   * The command name.
   */

  public static final String NAME = "generate-tangents";

  private static final String SYNTAX =
    "<position-name> <normal-name> <uv-name> <tangent-name> <bitangent-name>";

  private final SMFAttributeName position;
  private final SMFAttributeName normal;
  private final SMFAttributeName uv;
  private final SMFAttributeName tangent;
  private final SMFAttributeName bitangent;

  private R2MeshCommandGenerateTangents(
    final SMFAttributeName in_position,
    final SMFAttributeName in_normal,
    final SMFAttributeName in_uv,
    final SMFAttributeName in_tangent,
    final SMFAttributeName in_bitangent)
  {
    this.position = notNull(in_position, "Position");
    this.normal = notNull(in_normal, "Normal");
    this.uv = notNull(in_uv, "UV");
    this.tangent = notNull(in_tangent, "Tangent");
    this.bitangent = notNull(in_bitangent, "Bitangent");
  }

  /**
   * Create a new filter.
   *
   * @param in_position  The position attribute name
   * @param in_normal    The normal attribute name
   * @param in_uv        The UV attribute name
   * @param in_tangent   The tangent name
   * @param in_bitangent The bitangent name
   *
   * @return A new filter
   */

  public static SMFMemoryMeshFilterType create(
    final SMFAttributeName in_position,
    final SMFAttributeName in_normal,
    final SMFAttributeName in_uv,
    final SMFAttributeName in_tangent,
    final SMFAttributeName in_bitangent)
  {
    return new R2MeshCommandGenerateTangents(
      in_position, in_normal, in_uv, in_tangent, in_bitangent);
  }

  /**
   * Attempt to parse a command.
   *
   * @param file The file, if any
   * @param line The line
   * @param text The text
   *
   * @return A parsed command or a list of parse errors
   */

  public static Validation<List<SMFParseError>, SMFMemoryMeshFilterType> parse(
    final Optional<URI> file,
    final int line,
    final List<String> text)
  {
    notNull(file, "file");
    notNull(text, "text");

    if (text.length() == 5) {
      try {
        final SMFAttributeName attr_pos = SMFAttributeName.of(text.get(0));
        final SMFAttributeName attr_normal = SMFAttributeName.of(text.get(1));
        final SMFAttributeName attr_uv = SMFAttributeName.of(text.get(2));
        final SMFAttributeName attr_tangent = SMFAttributeName.of(text.get(3));
        final SMFAttributeName attr_bitangent = SMFAttributeName.of(text.get(4));
        return valid(create(
          attr_pos, attr_normal, attr_uv, attr_tangent, attr_bitangent));
      } catch (final IllegalArgumentException e) {
        return errorExpectedGotValidation(file, line, makeSyntax(), text);
      }
    }
    return errorExpectedGotValidation(file, line, makeSyntax(), text);
  }

  private static String makeSyntax()
  {
    return NAME + " " + SYNTAX;
  }

  @Override
  public String name()
  {
    return NAME;
  }

  @Override
  public String syntax()
  {
    return makeSyntax();
  }

  @Override
  public Validation<List<SMFProcessingError>, SMFMemoryMesh> filter(
    final SMFFilterCommandContext context,
    final SMFMemoryMesh m)
  {
    notNull(context, "Context");
    notNull(m, "Mesh");

    final SMFHeader orig_header = m.header();
    final SortedMap<SMFAttributeName, SMFAttribute> by_name =
      orig_header.attributesByName();

    Seq<SMFProcessingError> err = List.empty();
    err = checkAttributeExistsWithType(
      err, by_name, this.position, 3, R2_COMPONENT_FLOATING);
    err = checkAttributeExistsWithType(
      err, by_name, this.normal, 3, R2_COMPONENT_FLOATING);
    err = checkAttributeExistsWithType(
      err, by_name, this.uv, 2, R2_COMPONENT_FLOATING);
    err = checkAttributeNonexistent(err, by_name, this.tangent);

    if (!err.isEmpty()) {
      return invalid(List.ofAll(err));
    }

    try {
      final R2MeshBasic basic =
        toMeshBasic(m, this.position, this.normal, this.uv);
      final R2MeshTangents tangents =
        R2MeshTangentsGenerator.generateTangents(basic);

      Map<SMFAttributeName, SMFAttributeArrayType> arrays = m.arrays();
      arrays = arrays.put(this.tangent, toFloating4(tangents.tangents()));
      arrays = arrays.put(this.bitangent, toFloating3(tangents.bitangents()));

      final SMFAttribute attr_tan =
        SMFAttribute.of(
          this.tangent, ELEMENT_TYPE_FLOATING, 4, 64);
      final SMFAttribute attr_bitan =
        SMFAttribute.of(
          this.bitangent, ELEMENT_TYPE_FLOATING, 3, 64);

      final SMFHeader header = orig_header.withAttributesInOrder(
        orig_header.attributesInOrder().append(attr_tan).append(attr_bitan));

      return valid(
        SMFMemoryMesh.builder()
          .from(m)
          .setHeader(header)
          .setArrays(arrays)
          .build());

    } catch (final Exception e) {
      return invalid(List.ofAll(
        err.append(SMFProcessingError.of(e.getMessage(), Optional.of(e)))));
    }
  }

  private static SMFAttributeArrayFloating4 toFloating4(
    final BigList<PVector4D<R2SpaceObjectType>> v4)
  {
    return SMFAttributeArrayFloating4.of(
      Vector.ofAll(v4.stream())
        .map(v -> Vector4D.of(v.x(), v.y(), v.z(), v.w())));
  }

  private static SMFAttributeArrayFloating3 toFloating3(
    final BigList<PVector3D<R2SpaceObjectType>> v3)
  {
    return SMFAttributeArrayFloating3.of(
      Vector.ofAll(v3.stream()).map(v -> Vector3D.of(v.x(), v.y(), v.z())));
  }

  private static R2MeshBasic toMeshBasic(
    final SMFMemoryMesh m,
    final SMFAttributeName position,
    final SMFAttributeName normal,
    final SMFAttributeName uv)
  {
    final Map<SMFAttributeName, SMFAttributeArrayType> arrays = m.arrays();

    final BigList<PVector3D<R2SpaceObjectType>> positions =
      new ObjectBigArrayBigList<>();
    final SMFAttributeArrayFloating3 array_pos =
      (SMFAttributeArrayFloating3) arrays.get(position).get();
    array_pos.values().forEach(
      v -> positions.add(PVector3D.of(v.x(), v.y(), v.z())));

    final BigList<PVector3D<R2SpaceObjectType>> normals =
      new ObjectBigArrayBigList<>();
    final SMFAttributeArrayFloating3 array_normal =
      (SMFAttributeArrayFloating3) arrays.get(normal).get();
    array_normal.values().forEach(
      v -> normals.add(PVector3D.of(v.x(), v.y(), v.z())));

    final BigList<PVector2D<R2SpaceTextureType>> uvs =
      new ObjectBigArrayBigList<>();
    final SMFAttributeArrayFloating2 array_uv =
      (SMFAttributeArrayFloating2) arrays.get(uv).get();
    array_uv.values().forEach(
      v -> uvs.add(PVector2D.of(v.x(), v.y())));

    final BigList<R2MeshBasicVertex> vertices = new ObjectBigArrayBigList<>();
    for (int index = 0; index < array_pos.size(); ++index) {
      vertices.add(R2MeshBasicVertex.of(
        (long) index, (long) index, (long) index));
    }

    final BigList<R2MeshTriangle> trianges = new ObjectBigArrayBigList<>();
    m.triangles().forEach(
      t -> trianges.add(R2MeshTriangle.of(t.x(), t.y(), t.z())));

    return R2MeshBasic.of(positions, normals, uvs, vertices, trianges);
  }

  private static Seq<SMFProcessingError> checkAttributeExistsWithType(
    final Seq<SMFProcessingError> errors,
    final SortedMap<SMFAttributeName, SMFAttribute> by_name,
    final SMFAttributeName name,
    final int component_count,
    final R2MeshAttributeComponents component_type)
  {
    final Seq<SMFProcessingError> error_xs =
      checkAttributeExists(errors, by_name, name);
    if (Objects.equals(error_xs, errors)) {
      final SMFAttribute attr = by_name.get(name).get();
      if (component_count != attr.componentCount()
        || !typeMatches(attr.componentType(), component_type)) {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("Attribute has the wrong type.");
        sb.append(System.lineSeparator());
        sb.append("Expected: ");
        sb.append(name.value());
        sb.append(" ");
        sb.append(ofR2Type(component_type));
        sb.append(" ");
        sb.append(component_count);
        sb.append(System.lineSeparator());
        sb.append("Received: ");
        sb.append(name.value());
        sb.append(" ");
        sb.append(attr.componentType());
        sb.append(" ");
        sb.append(attr.componentCount());
        sb.append(System.lineSeparator());
        return error_xs.append(
          SMFProcessingError.of(sb.toString(), Optional.empty()));
      }
      return error_xs;
    }
    return error_xs;
  }

  private static SMFComponentType ofR2Type(
    final R2MeshAttributeComponents r2_type)
  {
    switch (r2_type) {
      case R2_COMPONENT_FLOATING:
        return ELEMENT_TYPE_FLOATING;
      case R2_COMPONENT_INTEGER_SIGNED:
        return ELEMENT_TYPE_INTEGER_SIGNED;
      case R2_COMPONENT_INTEGER_UNSIGNED:
        return ELEMENT_TYPE_INTEGER_UNSIGNED;
    }
    throw new UnreachableCodeException();
  }

  private static boolean typeMatches(
    final SMFComponentType smf_type,
    final R2MeshAttributeComponents r2_type)
  {
    return ofR2Type(r2_type) == smf_type;
  }
}
