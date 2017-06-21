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

package com.io7m.r2.tests.meshes.processing.smf;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import com.io7m.r2.meshes.processing.smf.R2MeshCommandGenerateTangents;
import com.io7m.smfj.core.SMFAttributeName;
import com.io7m.smfj.format.text.SMFFormatText;
import com.io7m.smfj.parser.api.SMFParseError;
import com.io7m.smfj.parser.api.SMFParserSequentialType;
import com.io7m.smfj.processing.api.SMFFilterCommandContext;
import com.io7m.smfj.processing.api.SMFMemoryMesh;
import com.io7m.smfj.processing.api.SMFMemoryMeshFilterType;
import com.io7m.smfj.processing.api.SMFMemoryMeshProducer;
import com.io7m.smfj.processing.api.SMFMemoryMeshProducerType;
import com.io7m.smfj.processing.api.SMFProcessingError;
import javaslang.collection.List;
import javaslang.control.Validation;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public final class R2MeshCommandGenerateTangentsTest
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MeshCommandGenerateTangentsTest.class);
  }

  @Test
  public void testParseTooFew0()
  {
    final Validation<List<SMFParseError>, SMFMemoryMeshFilterType> result =
      R2MeshCommandGenerateTangents.parse(
        Optional.empty(), 1, List.empty());

    Assert.assertTrue(result.isInvalid());
  }

  @Test
  public void testParseTooFew1()
  {
    final Validation<List<SMFParseError>, SMFMemoryMeshFilterType> result =
      R2MeshCommandGenerateTangents.parse(
        Optional.empty(), 1, List.of(
          "POSITION"));

    Assert.assertTrue(result.isInvalid());
  }

  @Test
  public void testParseTooFew2()
  {
    final Validation<List<SMFParseError>, SMFMemoryMeshFilterType> result =
      R2MeshCommandGenerateTangents.parse(
        Optional.empty(), 1, List.of(
          "POSITION",
          "NORMAL"));

    Assert.assertTrue(result.isInvalid());
  }

  @Test
  public void testParseTooFew3()
  {
    final Validation<List<SMFParseError>, SMFMemoryMeshFilterType> result =
      R2MeshCommandGenerateTangents.parse(
        Optional.empty(), 1, List.of(
          "POSITION",
          "NORMAL",
          "UV"));

    Assert.assertTrue(result.isInvalid());
  }

  @Test
  public void testParseTooFew4()
  {
    final Validation<List<SMFParseError>, SMFMemoryMeshFilterType> result =
      R2MeshCommandGenerateTangents.parse(
        Optional.empty(), 1, List.of(
          "POSITION",
          "NORMAL",
          "UV",
          "TANGENT4"));

    Assert.assertTrue(result.isInvalid());
  }

  @Test
  public void testParseTooMany0()
  {
    final Validation<List<SMFParseError>, SMFMemoryMeshFilterType> result =
      R2MeshCommandGenerateTangents.parse(
        Optional.empty(), 1, List.of(
          "POSITION",
          "NORMAL",
          "UV",
          "TANGENT4",
          "BITANGENT",
          "EXTRA"));

    Assert.assertTrue(result.isInvalid());
  }

  @Test
  public void testParse()
  {
    final Validation<List<SMFParseError>, SMFMemoryMeshFilterType> result =
      R2MeshCommandGenerateTangents.parse(
        Optional.empty(), 1, List.of(
          "POSITION",
          "NORMAL",
          "UV",
          "TANGENT4",
          "BITANGENT"));

    Assert.assertTrue(result.isValid());
  }

  @Test
  public void testProcess()
    throws Exception
  {
    try (final FileSystem fs = MemoryFileSystemBuilder.newEmpty().build("data")) {
      final SMFMemoryMeshProducerType producer =
        SMFMemoryMeshProducer.create();

      runParser(producer, "no_tangents.smft");

      final SMFMemoryMeshFilterType command =
        R2MeshCommandGenerateTangents.create(
          SMFAttributeName.of("POSITION"),
          SMFAttributeName.of("NORMAL"),
          SMFAttributeName.of("UV"),
          SMFAttributeName.of("TANGENT4"),
          SMFAttributeName.of("BITANGENT"));

      final Path root =
        fs.getRootDirectories().iterator().next();
      final SMFFilterCommandContext context =
        SMFFilterCommandContext.of(root, root);
      final Validation<List<SMFProcessingError>, SMFMemoryMesh> result =
        command.filter(context, producer.mesh());

      dumpResult(result);
      Assert.assertTrue(result.isValid());
    }
  }

  @Test
  public void testTangentCollision()
    throws Exception
  {
    try (final FileSystem fs = MemoryFileSystemBuilder.newEmpty().build("data")) {
      final SMFMemoryMeshProducerType producer =
        SMFMemoryMeshProducer.create();

      runParser(producer, "no_tangents.smft");

      final SMFMemoryMeshFilterType command =
        R2MeshCommandGenerateTangents.create(
          SMFAttributeName.of("POSITION"),
          SMFAttributeName.of("NORMAL"),
          SMFAttributeName.of("UV"),
          SMFAttributeName.of("POSITION"),
          SMFAttributeName.of("BITANGENT"));

      final Path root =
        fs.getRootDirectories().iterator().next();
      final SMFFilterCommandContext context =
        SMFFilterCommandContext.of(root, root);
      final Validation<List<SMFProcessingError>, SMFMemoryMesh> result =
        command.filter(context, producer.mesh());

      dumpResult(result);
      Assert.assertFalse(result.isValid());
    }
  }

  @Test
  public void testBitangentCollision()
    throws Exception
  {
    try (final FileSystem fs = MemoryFileSystemBuilder.newEmpty().build("data")) {
      final SMFMemoryMeshProducerType producer =
        SMFMemoryMeshProducer.create();

      runParser(producer, "no_tangents.smft");

      final SMFMemoryMeshFilterType command =
        R2MeshCommandGenerateTangents.create(
          SMFAttributeName.of("POSITION"),
          SMFAttributeName.of("NORMAL"),
          SMFAttributeName.of("UV"),
          SMFAttributeName.of("TANGENT4"),
          SMFAttributeName.of("POSITION"));

      final Path root =
        fs.getRootDirectories().iterator().next();
      final SMFFilterCommandContext context =
        SMFFilterCommandContext.of(root, root);
      final Validation<List<SMFProcessingError>, SMFMemoryMesh> result =
        command.filter(context, producer.mesh());

      dumpResult(result);
      Assert.assertFalse(result.isValid());
    }
  }

  private static void dumpResult(
    final Validation<List<SMFProcessingError>, SMFMemoryMesh> result)
  {
    if (result.isInvalid()) {
      result.getError().forEach(c -> LOG.error("{}", c));
    } else {
      LOG.debug("{}", result.get());
    }
  }

  private static void runParser(
    final SMFMemoryMeshProducerType producer,
    final String name)
    throws IOException
  {
    final SMFFormatText fmt = new SMFFormatText();
    try (final InputStream stream = R2MeshCommandGenerateTangentsTest.class.getResourceAsStream(
      "/com/io7m/r2/tests/meshes/smf/processing/" + name)) {
      try (final SMFParserSequentialType parser =
             fmt.parserCreateSequential(
               producer, Paths.get(name).toUri(), stream)) {
        parser.parse();
      }
    }
  }
}
