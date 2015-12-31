/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
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

package com.io7m.r2.meshes.tools;

import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.meshes.R2MeshBasicType;
import com.io7m.r2.meshes.R2MeshTangents;
import com.io7m.r2.meshes.R2MeshTangentsAdapter;
import com.io7m.r2.meshes.R2MeshTangentsAdapterType;
import com.io7m.r2.meshes.R2MeshTangentsType;
import com.io7m.r2.meshes.R2MeshType;
import com.io7m.r2.meshes.binary.R2MBMappedReader;
import com.io7m.r2.meshes.binary.R2MBMappedWriter;
import com.io7m.r2.meshes.binary.R2MBReaderType;
import com.io7m.r2.meshes.binary.R2MBUnmappedReader;
import com.io7m.r2.meshes.binary.R2MBUnmappedWriter;
import com.io7m.r2.meshes.binary.R2MBWriterType;
import com.io7m.r2.meshes.obj.R2ObjMeshImporter;
import com.io7m.r2.meshes.obj.R2ObjMeshImporterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * The default implementation of the {@link R2MeshConverterType} interface.
 */

public final class R2MeshConverter implements R2MeshConverterType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MeshConverter.class);
  }

  private final Map<String, R2MeshType>     meshes;
  private final R2MeshConverterListenerType listener;
  private final Map<String, R2MeshType>     meshes_view;

  private R2MeshConverter(
    final R2MeshConverterListenerType in_listener)
  {
    this.listener = NullCheck.notNull(in_listener);
    this.meshes = new HashMap<>(128);
    this.meshes_view = Collections.unmodifiableMap(this.meshes);
  }

  /**
   * @param in_listener The mesh converter listener
   *
   * @return A new mesh converter
   */

  public static R2MeshConverterType newConverter(
    final R2MeshConverterListenerType in_listener)
  {
    return new R2MeshConverter(in_listener);
  }

  @Override
  public void loadMeshesFromFile(
    final Path p,
    final R2MeshFileFormat fmt)
  {
    NullCheck.notNull(p);

    switch (fmt) {
      case MESH_FILE_FORMAT_OBJ: {
        this.loadObj(p);
        return;
      }
      case MESH_FILE_FORMAT_R2MB: {
        this.loadRMB(p);
        return;
      }
      case MESH_FILE_FORMAT_R2MBZ: {
        this.loadRMBZ(p);
        return;
      }
    }

    throw new UnreachableCodeException();
  }

  @Override
  public void loadMeshesFromFileInferred(final Path p)
  {
    for (final R2MeshFileFormat v : R2MeshFileFormat.values()) {
      final String suffix = "." + v.getName();
      final String fn = p.getFileName().toString();
      if (fn.endsWith(suffix)) {
        this.loadMeshesFromFile(p, v);
        return;
      }
    }

    this.listener.onError(Optional.empty(), p, "Unrecognized file suffix");
  }

  @Override
  public Map<String, R2MeshType> getMeshes()
  {
    return this.meshes_view;
  }

  @Override
  public void writeMeshToFile(
    final Path p,
    final String name,
    final R2MeshFileFormat fmt)
  {
    if (!this.meshes.containsKey(name)) {
      this.listener.onError(Optional.empty(), p, "No such mesh");
      return;
    }

    final R2MeshType m = this.meshes.get(name);
    switch (fmt) {
      case MESH_FILE_FORMAT_OBJ:
        this.writeObj(p, name, m);
        return;
      case MESH_FILE_FORMAT_R2MB:
        this.writeRMB(p, m);
        return;
      case MESH_FILE_FORMAT_R2MBZ:
        this.writeRMBZ(p, m);
        return;
    }

    throw new UnreachableCodeException();
  }

  @Override
  public void writeMeshToFileInferred(
    final Path p,
    final String name)
  {
    for (final R2MeshFileFormat v : R2MeshFileFormat.values()) {
      final String suffix = "." + v.getName();
      final String fn = p.getFileName().toString();
      if (fn.endsWith(suffix)) {
        this.writeMeshToFile(p, name, v);
        return;
      }
    }

    this.listener.onError(Optional.empty(), p, "Unrecognized file suffix");
  }

  private void writeRMBZ(
    final Path p,
    final R2MeshType m)
  {
    final R2MeshTangentsType mt = m.matchMesh(
      R2MeshTangents::generateTangents,
      tangents -> tangents);

    try (final OutputStream os =
           Files.newOutputStream(
             p,
             StandardOpenOption.CREATE,
             StandardOpenOption.TRUNCATE_EXISTING,
             StandardOpenOption.WRITE)) {
      try (final GZIPOutputStream s = new GZIPOutputStream(os)) {
        try (final R2MBWriterType rm =
               R2MBUnmappedWriter.newWriterForOutputStream(s, mt)) {
          rm.run();
        }
      }
    } catch (final IOException e) {
      this.listener.onError(Optional.of(e), p, e.getMessage());
    }
  }

  private void writeRMB(
    final Path p,
    final R2MeshType m)
  {
    final R2MeshTangentsType mt = m.matchMesh(
      R2MeshTangents::generateTangents,
      tangents -> tangents);

    try (final R2MBWriterType rm = R2MBMappedWriter.newWriterForPath(p, mt)) {
      rm.run();
    } catch (final IOException e) {
      this.listener.onError(Optional.of(e), p, e.getMessage());
    }
  }

  private void writeObj(
    final Path p,
    final String name,
    final R2MeshType m)
  {
    // TODO: Generated method stub!
    throw new UnimplementedCodeException();
  }

  private void loadRMBZ(final Path p)
  {
    final R2MeshTangentsAdapterType adapter =
      R2MeshTangentsAdapter.newAdapter(
        (ex, s) -> this.listener.onError(ex, p, s));

    final StandardOpenOption opt = StandardOpenOption.READ;
    try (final InputStream is =
           new GZIPInputStream(Files.newInputStream(p, opt))) {

      final R2MBReaderType r = R2MBUnmappedReader.newReader(
        Channels.newChannel(is),
        adapter);
      r.run();

      final Optional<R2MeshTangentsType> m_opt = adapter.getMesh();
      if (m_opt.isPresent()) {
        final String name = p.getFileName().toString();
        final R2MeshTangentsType m = m_opt.get();
        this.listener.onMeshLoaded(p, name, m);
        this.meshes.put(name, m);
      }

    } catch (final NoSuchFileException e) {
      this.listener.onError(Optional.of(e), p, "File not found");
    } catch (final IOException e) {
      this.listener.onError(Optional.of(e), p, e.getMessage());
    }
  }

  private void loadRMB(final Path p)
  {
    final R2MeshTangentsAdapterType adapter =
      R2MeshTangentsAdapter.newAdapter(
        (ex, s) -> this.listener.onError(ex, p, s));

    try (final FileChannel fc = FileChannel.open(p, StandardOpenOption.READ)) {

      final R2MBReaderType mr =
        R2MBMappedReader.newMappedReaderForFileChannel(fc, adapter);
      mr.run();

      final Optional<R2MeshTangentsType> m_opt = adapter.getMesh();
      if (m_opt.isPresent()) {
        final String name = p.getFileName().toString();
        final R2MeshTangentsType m = m_opt.get();
        this.listener.onMeshLoaded(p, name, m);
        this.meshes.put(name, m);
      }

    } catch (final NoSuchFileException e) {
      this.listener.onError(Optional.of(e), p, "File not found");
    } catch (final IOException e) {
      this.listener.onError(Optional.of(e), p, e.getMessage());
    }
  }

  private void loadObj(final Path p)
  {
    final R2ObjMeshImporterType mi = R2ObjMeshImporter.newImporter();
    final StandardOpenOption opt = StandardOpenOption.READ;
    try (final InputStream is = Files.newInputStream(p, opt)) {
      final Map<String, R2MeshBasicType> rs = mi
        .loadMeshesFromStream(
          Optional.of(p),
          is,
          (pos, exception, mesh, code, message) ->
          {
            final StringBuilder sb = new StringBuilder(message.length() + 32);
            sb.append(pos);
            sb.append(": ");
            code.ifPresent(c -> {
              sb.append(c);
              sb.append(": ");
            });
            sb.append(message);
            this.listener.onError(exception, p, sb.toString());
          });

      final Iterator<String> k_iter = rs.keySet().iterator();
      while (k_iter.hasNext()) {
        final String name = k_iter.next();
        final R2MeshBasicType m = rs.get(name);
        this.listener.onMeshLoaded(p, name, m);
        this.meshes.put(name, m);
      }

    } catch (final NoSuchFileException e) {
      this.listener.onError(Optional.of(e), p, "File not found");
    } catch (final IOException e) {
      this.listener.onError(Optional.of(e), p, e.getMessage());
    }
  }
}
