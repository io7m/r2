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

import com.io7m.jaffirm.core.Invariants;
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jcanephora.core.JCGLArrayBufferType;
import com.io7m.jcanephora.core.JCGLBufferUpdate;
import com.io7m.jcanephora.core.JCGLIndexBufferType;
import com.io7m.jcanephora.core.JCGLUnsignedType;
import com.io7m.jcanephora.core.JCGLUsageHint;
import com.io7m.jcanephora.core.api.JCGLArrayBuffersType;
import com.io7m.jcanephora.core.api.JCGLArrayObjectsType;
import com.io7m.jcanephora.core.api.JCGLIndexBuffersType;
import com.io7m.jcanephora.core.api.JCGLInterfaceGL33Type;
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.meshes.api.R2MeshAttribute;
import com.io7m.r2.meshes.api.R2MeshAttributeComponents;
import com.io7m.r2.meshes.api.R2MeshAttributeConventions;
import com.io7m.r2.meshes.api.R2MeshAttributePacked;
import com.io7m.r2.meshes.loading.api.R2MeshLoaded;
import com.io7m.r2.meshes.loading.api.R2MeshLoaderRequest;
import com.io7m.r2.meshes.loading.api.R2MeshLoadingError;
import com.io7m.r2.meshes.loading.api.R2MeshLoadingExceptionIO;
import com.io7m.r2.meshes.loading.api.R2MeshLoadingExceptionValidation;
import com.io7m.r2.meshes.loading.api.R2MeshRequireTangents;
import com.io7m.r2.meshes.loading.api.R2MeshRequireUV;
import com.io7m.smfj.bytebuffer.SMFByteBufferPackedAttribute;
import com.io7m.smfj.bytebuffer.SMFByteBufferPackedAttributeSet;
import com.io7m.smfj.bytebuffer.SMFByteBufferPackedMesh;
import com.io7m.smfj.bytebuffer.SMFByteBufferPackedMeshLoaderType;
import com.io7m.smfj.bytebuffer.SMFByteBufferPackedMeshes;
import com.io7m.smfj.bytebuffer.SMFByteBufferPackedTriangles;
import com.io7m.smfj.bytebuffer.SMFByteBufferPackerEventsType;
import com.io7m.smfj.bytebuffer.SMFByteBufferPackingConfiguration;
import com.io7m.smfj.core.SMFAttribute;
import com.io7m.smfj.core.SMFAttributeName;
import com.io7m.smfj.core.SMFComponentType;
import com.io7m.smfj.core.SMFErrorType;
import com.io7m.smfj.core.SMFHeader;
import com.io7m.smfj.core.SMFTriangles;
import com.io7m.smfj.parser.api.SMFParserEventsDataMetaOptionalSupplierType;
import com.io7m.smfj.parser.api.SMFParserProviderType;
import com.io7m.smfj.parser.api.SMFParserSequentialType;
import com.io7m.smfj.validation.api.SMFSchema;
import com.io7m.smfj.validation.api.SMFSchemaValidator;
import it.unimi.dsi.fastutil.ints.Int2ReferenceRBTreeMap;
import javaslang.collection.List;
import javaslang.collection.SortedMap;
import javaslang.collection.TreeMap;
import javaslang.control.Validation;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalInt;

import static com.io7m.r2.meshes.loading.smf.R2SMFSchemas.R2_NORMAL;
import static com.io7m.r2.meshes.loading.smf.R2SMFSchemas.R2_NORMAL_NAME;
import static com.io7m.r2.meshes.loading.smf.R2SMFSchemas.R2_POSITION;
import static com.io7m.r2.meshes.loading.smf.R2SMFSchemas.R2_POSITION_NAME;
import static com.io7m.r2.meshes.loading.smf.R2SMFSchemas.R2_SCHEMA_IDENTIFIER;
import static com.io7m.r2.meshes.loading.smf.R2SMFSchemas.R2_TANGENT4;
import static com.io7m.r2.meshes.loading.smf.R2SMFSchemas.R2_TANGENT4_NAME;
import static com.io7m.r2.meshes.loading.smf.R2SMFSchemas.R2_UV;
import static com.io7m.r2.meshes.loading.smf.R2SMFSchemas.R2_UV_NAME;
import static com.io7m.smfj.validation.api.SMFSchemaAllowExtraAttributes.SMF_EXTRA_ATTRIBUTES_ALLOWED;
import static com.io7m.smfj.validation.api.SMFSchemaRequireTriangles.SMF_TRIANGLES_REQUIRED;
import static com.io7m.smfj.validation.api.SMFSchemaRequireVertices.SMF_VERTICES_REQUIRED;
import static javaslang.control.Validation.invalid;
import static javaslang.control.Validation.valid;

final class R2SMFMeshUploading
{
  private R2SMFMeshUploading()
  {
    throw new UnreachableCodeException();
  }

  public static R2MeshLoaded meshUpload(
    final Logger log,
    final JCGLInterfaceGL33Type current_g33,
    final Packed packed)
  {
    log.debug("uploading mesh {}", packed.request.uri());

    Preconditions.checkPreconditionI(
      packed.mesh.attributeSets().size(),
      packed.mesh.attributeSets().size() == 1,
      i -> "Must receive a single attribute set");
    Preconditions.checkPrecondition(
      packed.mesh.triangles(),
      packed.mesh.triangles().isPresent(),
      t -> "Must have packed triangles");

    final JCGLArrayBuffersType g_ab = current_g33.arrayBuffers();
    final JCGLIndexBuffersType g_ib = current_g33.indexBuffers();
    final JCGLArrayObjectsType g_ao = current_g33.arrayObjects();

    final ByteBuffer data_buffer = packed.data_buffer;
    final ByteBuffer triangle_buffer = packed.tri_buffer;
    final SMFByteBufferPackedTriangles triangles = packed.mesh.triangles().get();

    g_ao.arrayObjectUnbind();
    final JCGLArrayBufferType data_array_buffer =
      g_ab.arrayBufferAllocate(
        Integer.toUnsignedLong(data_buffer.capacity()),
        packed.request.arrayBufferUsageHint());
    g_ab.arrayBufferUpdate(
      JCGLBufferUpdate.of(
        data_array_buffer, data_buffer, data_array_buffer.byteRange()));

    final long triangle_index_count =
      Math.multiplyExact(triangles.triangleCount(), 3L);
    final JCGLUnsignedType triangle_index_type =
      toJCGLUnsigned(triangles.triangleIndexSizeBits());
    final JCGLUsageHint triangle_usage =
      packed.request.indexBufferUsageHint();

    final JCGLIndexBufferType triangle_index_buffer =
      g_ib.indexBufferAllocate(
        triangle_index_count,
        triangle_index_type,
        triangle_usage);

    g_ib.indexBufferUpdate(
      JCGLBufferUpdate.of(
        triangle_index_buffer,
        triangle_buffer,
        triangle_index_buffer.byteRange()));

    return R2MeshLoaded.of(
      data_array_buffer, triangle_index_buffer, packed.by_index);
  }

  private static R2MeshAttribute transformAttribute(
    final int index,
    final SMFAttribute attribute)
  {
    final R2MeshAttribute.Builder ab = R2MeshAttribute.builder();
    ab.setComponentCount(attribute.componentCount());
    ab.setComponentSizeBits(attribute.componentSizeBits());
    ab.setComponentType(transformComponentType(attribute.componentType()));
    ab.setSemantic(R2SMFSchemas.semanticForName(attribute.name()));
    ab.setName(attribute.name().value());
    ab.setIndex(index);
    return ab.build();
  }

  private static R2MeshAttributeComponents transformComponentType(
    final SMFComponentType type)
  {
    switch (type) {
      case ELEMENT_TYPE_INTEGER_SIGNED:
        return R2MeshAttributeComponents.R2_COMPONENT_INTEGER_SIGNED;
      case ELEMENT_TYPE_INTEGER_UNSIGNED:
        return R2MeshAttributeComponents.R2_COMPONENT_INTEGER_UNSIGNED;
      case ELEMENT_TYPE_FLOATING:
        return R2MeshAttributeComponents.R2_COMPONENT_FLOATING;
    }
    throw new UnreachableCodeException();
  }

  private static JCGLUnsignedType toJCGLUnsigned(
    final int size)
  {
    switch (size) {
      case 8: {
        return JCGLUnsignedType.TYPE_UNSIGNED_BYTE;
      }
      case 16: {
        return JCGLUnsignedType.TYPE_UNSIGNED_SHORT;
      }
      case 32: {
        return JCGLUnsignedType.TYPE_UNSIGNED_INT;
      }
      default: {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("Triangle index size not supported.");
        sb.append(System.lineSeparator());
        sb.append("  Received: ");
        sb.append(size);
        sb.append(System.lineSeparator());
        sb.append("  Expected: {8, 16, 32}");
        sb.append(System.lineSeparator());
        throw new UnsupportedOperationException(sb.toString());
      }
    }
  }

  public static Packed meshParseAndPack(
    final Logger log,
    final SMFParserProviderType parsers,
    final SMFSchemaValidator validator,
    final R2MeshLoaderRequest request)
  {
    log.debug("loading mesh {}", request.uri());

    try {
      final SMFByteBufferPackerEventsType events =
        new Packer(validator, request);

      final SMFByteBufferPackedMeshLoaderType loader =
        SMFByteBufferPackedMeshes.newLoader(
          SMFParserEventsDataMetaOptionalSupplierType.ignoring(),
          events);

      final URL url = request.uri().toURL();
      try (final InputStream stream = url.openStream()) {
        try (final SMFParserSequentialType parser =
               parsers.parserCreateSequential(loader, request.uri(), stream)) {
          parser.parse();
        }
      }

      {
        final List<SMFErrorType> errors = loader.errors();
        if (!errors.isEmpty()) {
          final StringBuilder sb = new StringBuilder(256);
          sb.append("Mesh parsing failed.");
          sb.append(System.lineSeparator());
          errors.forEach(e -> sb.append(e.fullMessage()));

          throw new R2MeshLoadingExceptionValidation(
            errors.map(e -> R2MeshLoadingError.of(
              e.message(),
              e.exception())).toJavaList(),
            sb.toString());
        }
      }

      final SMFByteBufferPackedMesh mesh = loader.mesh();

      Invariants.checkInvariantI(
        mesh.attributeSets().size(),
        mesh.attributeSets().size() == 1,
        i -> "Mesh must be packed into a single set");

      Invariants.checkInvariant(
        mesh.triangles().isPresent(),
        "Mesh triangles must be present");

      final SMFByteBufferPackedTriangles triangles = mesh.triangles().get();
      final SMFByteBufferPackedAttributeSet set = mesh.attributeSets().get();
      final SMFByteBufferPackingConfiguration config = set.configuration();
      final SortedMap<SMFAttributeName, SMFByteBufferPackedAttribute> by_name =
        config.packedAttributesByName();

      if (log.isDebugEnabled()) {
        log.debug(
          "vertices {}",
          Long.toUnsignedString(mesh.header().vertexCount()));
        log.debug(
          "triangles {} {}",
          Long.toUnsignedString(triangles.triangleCount()),
          Integer.valueOf(triangles.triangleIndexSizeBits()));
      }

      final Int2ReferenceRBTreeMap<R2MeshAttributePacked> by_index =
        new Int2ReferenceRBTreeMap<>();

      int user_attrib = R2MeshAttributeConventions.USER_ATTRIBUTES_INDEX;
      for (final SMFAttributeName name : by_name.keySet()) {
        final OptionalInt index_opt =
          R2SMFSchemas.attributeIndexForName(name);
        final int index =
          index_opt.orElse(user_attrib);
        final SMFByteBufferPackedAttribute packed =
          by_name.get(name).get();
        final R2MeshAttribute r2_attribute =
          transformAttribute(index, packed.attribute());
        final R2MeshAttributePacked r2_attribute_packed =
          R2MeshAttributePacked.of(r2_attribute, packed.offsetOctets());

        if (log.isDebugEnabled()) {
          log.debug(
            "attribute {} {} {} {} -> index {}",
            name.value(),
            r2_attribute.componentType(),
            Integer.valueOf(r2_attribute.componentCount()),
            Integer.valueOf(r2_attribute.componentSizeBits()),
            Integer.valueOf(index));
        }

        by_index.put(index, r2_attribute_packed);
        user_attrib = Math.addExact(user_attrib, 1);
      }

      return new Packed(
        request, by_index, mesh, set.byteBuffer(), triangles.byteBuffer());
    } catch (final MalformedURLException e) {
      throw new R2MeshLoadingExceptionIO(
        "Malformed URL: " + e.getMessage(), e);
    } catch (final IOException e) {
      throw new R2MeshLoadingExceptionIO(
        "I/O error: " + e.getMessage(), e);
    }
  }

  static final class Packed
  {
    private final R2MeshLoaderRequest request;
    private final Int2ReferenceRBTreeMap<R2MeshAttributePacked> by_index;
    private final SMFByteBufferPackedMesh mesh;
    private final ByteBuffer data_buffer;
    private final ByteBuffer tri_buffer;

    Packed(
      final R2MeshLoaderRequest in_request,
      final Int2ReferenceRBTreeMap<R2MeshAttributePacked> in_by_index,
      final SMFByteBufferPackedMesh in_mesh,
      final ByteBuffer in_data_buffer,
      final ByteBuffer in_tri_buffer)
    {
      this.request =
        NullCheck.notNull(in_request, "Request");
      this.by_index =
        NullCheck.notNull(in_by_index, "By Index");
      this.mesh =
        NullCheck.notNull(in_mesh, "Mesh");
      this.data_buffer =
        NullCheck.notNull(in_data_buffer, "Data buffer");
      this.tri_buffer =
        NullCheck.notNull(in_tri_buffer, "Tri buffer");
    }
  }

  private static final class Packer implements SMFByteBufferPackerEventsType
  {
    private final R2MeshLoaderRequest request;
    private final SMFSchemaValidator validator;

    Packer(
      final SMFSchemaValidator in_validator,
      final R2MeshLoaderRequest in_request)
    {
      this.validator = NullCheck.notNull(in_validator, "Validator");
      this.request = NullCheck.notNull(in_request, "Request");
    }

    @Override
    public Validation<List<SMFErrorType>, SortedMap<Integer, SMFByteBufferPackingConfiguration>> onHeader(
      final SMFHeader header)
    {
      final SMFSchema.Builder schema_builder =
        SMFSchema.builder()
          .putRequiredAttributes(R2_POSITION_NAME, R2_POSITION)
          .putRequiredAttributes(R2_NORMAL_NAME, R2_NORMAL)
          .setRequireVertices(SMF_VERTICES_REQUIRED)
          .setRequireTriangles(SMF_TRIANGLES_REQUIRED)
          .setAllowExtraAttributes(SMF_EXTRA_ATTRIBUTES_ALLOWED)
          .setSchemaIdentifier(R2_SCHEMA_IDENTIFIER);

      if (this.request.requireTangents() == R2MeshRequireTangents.R2_TANGENTS_REQUIRED) {
        schema_builder.putRequiredAttributes(R2_TANGENT4_NAME, R2_TANGENT4);
      } else {
        schema_builder.putOptionalAttributes(R2_TANGENT4_NAME, R2_TANGENT4);
      }

      if (this.request.requireUV() == R2MeshRequireUV.R2_UV_REQUIRED) {
        schema_builder.putRequiredAttributes(R2_UV_NAME, R2_UV);
      } else {
        schema_builder.putOptionalAttributes(R2_UV_NAME, R2_UV);
      }

      final SMFSchema schema = schema_builder.build();

      final Validation<List<SMFErrorType>, SMFHeader> valid_r =
        this.validator.validate(header, schema);

      if (valid_r.isValid()) {
        final SortedMap<SMFAttributeName, SMFAttribute> by_name =
          header.attributesByName();

        final SMFByteBufferPackingConfiguration.Builder b =
          SMFByteBufferPackingConfiguration.builder();

        b.addAttributesOrdered(by_name.get(R2_POSITION_NAME).get());
        b.addAttributesOrdered(by_name.get(R2_NORMAL_NAME).get());

        if (by_name.containsKey(R2_UV_NAME)) {
          b.addAttributesOrdered(by_name.get(R2_UV_NAME).get());
        }
        if (by_name.containsKey(R2_TANGENT4_NAME)) {
          b.addAttributesOrdered(by_name.get(R2_TANGENT4_NAME).get());
        }

        return valid(TreeMap.of(Integer.valueOf(0), b.build()));
      }

      return invalid(valid_r.getError());
    }

    @Override
    public boolean onShouldPackTriangles()
    {
      return true;
    }

    @Override
    public ByteBuffer onAllocateTriangleBuffer(
      final SMFTriangles triangles,
      final long size)
    {
      return ByteBuffer.allocateDirect(Math.toIntExact(size))
        .order(ByteOrder.nativeOrder());
    }

    @Override
    public ByteBuffer onAllocateAttributeBuffer(
      final Integer id,
      final SMFByteBufferPackingConfiguration config,
      final long size)
    {
      return ByteBuffer.allocateDirect(Math.toIntExact(size))
        .order(ByteOrder.nativeOrder());
    }
  }
}
