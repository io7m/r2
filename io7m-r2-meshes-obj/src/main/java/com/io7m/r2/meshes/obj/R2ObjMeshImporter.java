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

package com.io7m.r2.meshes.obj;

import com.io7m.jlexing.core.LexicalPositionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jobj.core.JOParser;
import com.io7m.jobj.core.JOParserErrorCode;
import com.io7m.jobj.core.JOParserEventListenerType;
import com.io7m.jobj.core.JOParserType;
import com.io7m.jtensors.parameterized.PVectorI2D;
import com.io7m.jtensors.parameterized.PVectorI3D;
import com.io7m.jtensors.parameterized.PVectorI4D;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.r2.meshes.R2MeshBasic;
import com.io7m.r2.meshes.R2MeshBasicBuilderType;
import com.io7m.r2.meshes.R2MeshBasicType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valid4j.Assertive;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The default implementation of the {@link R2ObjMeshImporterType} interface.
 */

public final class R2ObjMeshImporter implements R2ObjMeshImporterType
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2ObjMeshImporter.class);
  }

  private R2ObjMeshImporter()
  {

  }

  /**
   * @return A new mesh importer
   */

  public static R2ObjMeshImporterType newImporter()
  {
    return new R2ObjMeshImporter();
  }

  @Override
  public Map<String, R2MeshBasicType> loadMeshesFromStream(
    final Optional<Path> path,
    final InputStream s,
    final R2ObjMeshErrorListenerType error)
  {
    NullCheck.notNull(path);
    NullCheck.notNull(s);
    NullCheck.notNull(error);

    final Listener ls = new Listener(error);
    final JOParserType p = JOParser.newParserFromStream(path, s, ls);
    p.run();
    return ls.meshes;
  }

  private static final class Listener implements JOParserEventListenerType
  {
    private final Int2LongMap                  mesh_positions;
    private final Int2LongMap                  mesh_normals;
    private final Int2LongOpenHashMap          mesh_textures;
    private final R2ObjMeshErrorListenerType   error;
    private final Map<String, R2MeshBasicType> meshes;
    private       R2MeshBasicBuilderType       builder;
    private       Optional<String>             mesh;
    private       int                          face_current;
    private       long                         f_v0;
    private       long                         f_v1;
    private       long                         f_v2;
    private       int                          face_vertices;
    private       boolean                      mesh_malformed;

    Listener(final R2ObjMeshErrorListenerType in_error)
    {
      this.mesh = Optional.empty();
      this.builder = R2MeshBasic.newBuilder(8192L, 8192L);
      this.meshes = new HashMap<>(32);
      this.mesh_positions = new Int2LongOpenHashMap();
      this.mesh_normals = new Int2LongOpenHashMap();
      this.mesh_textures = new Int2LongOpenHashMap();
      this.faceReset();
      this.mesh_malformed = false;
      this.error = in_error;
    }

    @Override
    public void onFatalError(
      final LexicalPositionType<Path> p,
      final Optional<Throwable> e,
      final String message)
    {
      try {
        this.error.onError(p, e, this.mesh, Optional.empty(), message);
      } catch (final Throwable ex) {
        this.ignoreException(ex);
      }
    }

    @Override
    public void onError(
      final LexicalPositionType<Path> p,
      final JOParserErrorCode e,
      final String message)
    {
      if (this.mesh.isPresent()) {
        this.mesh_malformed = true;
      }

      try {
        this.error.onError(
          p, Optional.empty(), this.mesh, Optional.of(e), message);
      } catch (final Throwable ex) {
        this.ignoreException(ex);
      }
    }

    @Override
    public void onLine(
      final LexicalPositionType<Path> p,
      final String line)
    {

    }

    @Override
    public void onEOF(final LexicalPositionType<Path> p)
    {
      if (this.mesh.isPresent() && !this.mesh_malformed) {
        this.finishCurrentMesh();
      }
    }

    private void finishCurrentMesh()
    {
      Assertive.require(this.mesh.isPresent());
      final String name = this.mesh.get();
      Assertive.require(!this.meshes.containsKey(name));
      final R2MeshBasicType m = this.builder.build();
      R2ObjMeshImporter.LOG.debug("loaded {}", name);
      this.meshes.put(name, m);
    }

    @Override
    public void onComment(
      final LexicalPositionType<Path> p,
      final String text)
    {

    }

    @Override
    public void onCommandUsemtl(
      final LexicalPositionType<Path> p,
      final String name)
    {

    }

    @Override
    public void onCommandMtllib(
      final LexicalPositionType<Path> p,
      final String name)
    {

    }

    @Override
    public void onCommandO(
      final LexicalPositionType<Path> p,
      final String name)
    {
      if (this.mesh.isPresent() && !this.mesh_malformed) {
        this.finishCurrentMesh();
      }

      R2ObjMeshImporter.LOG.trace("starting mesh {}", name);
      this.mesh = Optional.of(name);
      this.resetCurrentMesh();
    }

    private void resetCurrentMesh()
    {
      this.builder = R2MeshBasic.newBuilder(8192L, 8192L);
      this.mesh_normals.clear();
      this.mesh_positions.clear();
      this.mesh_textures.clear();
      this.mesh_malformed = false;
    }

    @Override
    public void onCommandS(
      final LexicalPositionType<Path> p,
      final int group_number)
    {

    }

    @Override
    public void onCommandV(
      final LexicalPositionType<Path> p,
      final int index,
      final double x,
      final double y,
      final double z,
      final double w)
    {
      if (this.mesh_malformed) {
        return;
      }

      final PVectorI4D<R2SpaceObjectType> v = new PVectorI4D<>(x, y, z, w);
      final PVectorI4D<R2SpaceObjectType> k = PVectorI4D.scale(v, 1.0 / w);
      final long pv = this.builder.addPosition(new PVectorI3D<>(k));
      this.mesh_positions.put(index, pv);
      R2ObjMeshImporter.LOG.trace(
        "v {} -> {}",
        Integer.valueOf(index), Long.valueOf(pv));
    }

    @Override
    public void onCommandVN(
      final LexicalPositionType<Path> p,
      final int index,
      final double x,
      final double y,
      final double z)
    {
      if (this.mesh_malformed) {
        return;
      }

      final long n = this.builder.addNormal(new PVectorI3D<>(x, y, z));
      this.mesh_normals.put(index, n);
      R2ObjMeshImporter.LOG.trace(
        "vn {} -> {}",
        Integer.valueOf(index), Long.valueOf(n));
    }

    @Override
    public void onCommandVT(
      final LexicalPositionType<Path> p,
      final int index,
      final double x,
      final double y,
      final double z)
    {
      if (this.mesh_malformed) {
        return;
      }

      final long u = this.builder.addUV(new PVectorI2D<>(x, y));
      this.mesh_textures.put(index, u);
      R2ObjMeshImporter.LOG.trace(
        "vt {} -> {}",
        Integer.valueOf(index), Long.valueOf(u));
    }

    @Override
    public void onCommandFVertexV_VT_VN(
      final LexicalPositionType<Path> p,
      final int index,
      final int v,
      final int vt,
      final int vn)
    {
      if (this.mesh_malformed) {
        return;
      }

      if (this.face_vertices >= 3) {
        this.mesh_malformed = true;
        try {
          this.error.onError(
            p,
            Optional.empty(),
            this.mesh,
            Optional.empty(),
            "Too many vertices for face: Only triangles are supported");
        } catch (final Throwable ex) {
          this.ignoreException(ex);
        }
        return;
      }

      Assertive.require(this.mesh_positions.containsKey(v));
      Assertive.require(this.mesh_normals.containsKey(vn));
      Assertive.require(this.mesh_textures.containsKey(vt));

      final long p_actual = this.mesh_positions.get(v);
      final long n_actual = this.mesh_normals.get(vn);
      final long t_actual = this.mesh_textures.get(vt);

      R2ObjMeshImporter.LOG.trace(
        "p {} -> {}",
        Integer.valueOf(v),
        Long.valueOf(p_actual));
      R2ObjMeshImporter.LOG.trace(
        "n {} -> {}",
        Integer.valueOf(vn),
        Long.valueOf(n_actual));
      R2ObjMeshImporter.LOG.trace(
        "t {} -> {}",
        Integer.valueOf(vt),
        Long.valueOf(t_actual));

      final long vk = this.builder.addVertex(p_actual, n_actual, t_actual);

      R2ObjMeshImporter.LOG.trace(
        "{}/{}/{} -> {}",
        Integer.valueOf(v),
        Integer.valueOf(vn),
        Integer.valueOf(vt),
        Long.valueOf(vk));

      switch (this.face_vertices) {
        case 0:
          this.f_v0 = vk;
          ++this.face_vertices;
          return;
        case 1:
          this.f_v1 = vk;
          ++this.face_vertices;
          return;
        case 2:
          this.f_v2 = vk;
          ++this.face_vertices;
          return;
      }

      throw new UnreachableCodeException();
    }

    private void ignoreException(final Throwable ex)
    {
      R2ObjMeshImporter.LOG.error(
        "ignoring exception raised in error listener: ", ex);
    }

    @Override
    public void onCommandFVertexV_VT(
      final LexicalPositionType<Path> p,
      final int index,
      final int v,
      final int vt)
    {
      this.mesh_malformed = true;
      try {
        this.error.onError(
          p, Optional.empty(), this.mesh, Optional.empty(),
          "Vertices without normals are not supported");
      } catch (final Throwable ex) {
        this.ignoreException(ex);
      }
    }

    @Override
    public void onCommandFVertexV_VN(
      final LexicalPositionType<Path> p,
      final int index,
      final int v,
      final int vn)
    {
      this.mesh_malformed = true;
      try {
        this.error.onError(
          p, Optional.empty(), this.mesh, Optional.empty(),
          "Vertices without texture coordinates are not supported");
      } catch (final Throwable ex) {
        this.ignoreException(ex);
      }
    }

    @Override
    public void onCommandFVertexV(
      final LexicalPositionType<Path> p,
      final int index,
      final int v)
    {
      this.mesh_malformed = true;
      try {
        this.error.onError(
          p, Optional.empty(), this.mesh, Optional.empty(),
          "Vertices without normals and texture coordinates are not supported");
      } catch (final Throwable ex) {
        this.ignoreException(ex);
      }
    }

    @Override
    public void onCommandFStarted(
      final LexicalPositionType<Path> p,
      final int index)
    {
      if (this.mesh_malformed) {
        return;
      }

      Assertive.require(this.mesh.isPresent());
      this.face_current = index;
    }

    @Override
    public void onCommandFFinished(
      final LexicalPositionType<Path> p,
      final int index)
    {
      if (this.mesh_malformed) {
        return;
      }

      Assertive.require(this.f_v0 != -1L);
      Assertive.require(this.f_v1 != -1L);
      Assertive.require(this.f_v2 != -1L);

      R2ObjMeshImporter.LOG.trace(
        "triangle {}/{}/{}",
        Long.valueOf(this.f_v0),
        Long.valueOf(this.f_v1),
        Long.valueOf(this.f_v2));

      this.builder.addTriangle(this.f_v0, this.f_v1, this.f_v2);
      this.faceReset();
    }

    private void faceReset()
    {
      this.face_vertices = 0;
      this.face_current = -1;
      this.f_v0 = -1L;
      this.f_v1 = -1L;
      this.f_v2 = -1L;
    }
  }
}
