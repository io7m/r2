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

package com.io7m.r2.meshes.tools;

// CHECKSTYLE:OFF

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.io7m.jfunctional.Unit;
import com.io7m.jtensors.parameterized.PVectorI2D;
import com.io7m.jtensors.parameterized.PVectorI3D;
import com.io7m.jtensors.parameterized.PVectorI4D;
import com.io7m.r2.meshes.R2MeshBasicType;
import com.io7m.r2.meshes.R2MeshBasicVertexType;
import com.io7m.r2.meshes.R2MeshTangentsType;
import com.io7m.r2.meshes.R2MeshTangentsVertexType;
import com.io7m.r2.meshes.R2MeshTriangleType;
import com.io7m.r2.meshes.R2MeshType;
import com.io7m.r2.spaces.R2SpaceObjectType;
import com.io7m.r2.spaces.R2SpaceTextureType;
import it.unimi.dsi.fastutil.BigList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

public final class R2MeshTool implements Runnable
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MeshTool.class);
  }

  private final Map<String, CommandType> commands;
  private final JCommander               commander;
  private final String[]                 args;
  private       int                      exit_code;

  private R2MeshTool(final String[] in_args)
    throws Exception
  {
    final CommandRoot r = new CommandRoot();
    final CommandCheck check = new CommandCheck();
    final CommandConvert convert = new CommandConvert();
    final CommandDump dump = new CommandDump();

    this.commands = new HashMap<>();
    this.commands.put("check", check);
    this.commands.put("convert", convert);
    this.commands.put("dump", dump);

    this.commander = new JCommander(r);
    this.commander.setProgramName("meshtool");
    this.commander.addCommand("check", check);
    this.commander.addCommand("convert", convert);
    this.commander.addCommand("dump", dump);
    this.args = in_args;
  }

  public static void main(final String[] args)
    throws Exception
  {
    final R2MeshTool mt = new R2MeshTool(args);
    mt.run();
    System.exit(mt.exit_code);
  }

  @Override
  public void run()
  {
    try {
      this.commander.parse(this.args);

      final String cmd = this.commander.getParsedCommand();
      if (cmd == null) {
        final StringBuilder sb = new StringBuilder(128);
        this.commander.usage(sb);
        R2MeshTool.LOG.info("Arguments required.\n{}", sb.toString());
        return;
      }

      final CommandType command = this.commands.get(cmd);
      command.call();

    } catch (final ParameterException e) {
      final StringBuilder sb = new StringBuilder(128);
      this.commander.usage(sb);
      R2MeshTool.LOG.error("{}\n{}", e.getMessage(), sb.toString());
      this.exit_code = 1;
    } catch (final R2MeshFileFormatUnrecognized e) {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Unrecognized mesh format (must be one of {");
      final R2MeshFileFormat[] vs = R2MeshFileFormat.values();
      for (int index = 0; index < vs.length; ++index) {
        final R2MeshFileFormat v = vs[index];
        sb.append(v.getName());
        if (index + 1 < vs.length) {
          sb.append(" ");
        }
      }
      sb.append("})\n");
      R2MeshTool.LOG.error("{}", sb.toString());
      this.exit_code = 1;
    } catch (final Exception e) {
      R2MeshTool.LOG.error("{}", e.getMessage(), e);
      this.exit_code = 1;
    }
  }

  interface CommandType extends Callable<Unit>
  {

  }


  private class CommandRoot implements CommandType,
    R2MeshConverterListenerType
  {
    @Parameter(
      names = "-debug",
      converter = R2MeshToolLogLevelConverter.class,
      description = "Set debug level")
    protected R2MeshToolLogLevel debug = R2MeshToolLogLevel.LOG_INFO;

    CommandRoot()
    {

    }

    @Override
    public Unit call()
      throws Exception
    {
      final ch.qos.logback.classic.Logger root =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(
          Logger.ROOT_LOGGER_NAME);
      root.setLevel(this.debug.toLevel());
      return Unit.unit();
    }

    @Override
    public final void onMeshLoaded(
      final Path p,
      final String name,
      final R2MeshType m)
    {
      R2MeshTool.LOG.info("{}: loaded {}", p, name);

      m.matchMesh(
        basic -> {
          R2MeshTool.LOG.info(
            "{}: basic mesh",
            name);
          R2MeshTool.LOG.info(
            "{}: {} positions",
            name,
            Long.valueOf(basic.getPositions().size64()));
          R2MeshTool.LOG.info(
            "{}: {} normals",
            name,
            Long.valueOf(basic.getNormals().size64()));
          R2MeshTool.LOG.info(
            "{}: {} uvs",
            name,
            Long.valueOf(basic.getUVs().size64()));
          R2MeshTool.LOG.info(
            "{}: {} unique vertices",
            name,
            Long.valueOf(basic.getVertices().size64()));
          R2MeshTool.LOG.info(
            "{}: {} triangles",
            name,
            Long.valueOf(basic.getTriangles().size64()));
          return Unit.unit();
        },
        with_tangents -> {
          R2MeshTool.LOG.info(
            "{}: mesh with tangents",
            name);
          R2MeshTool.LOG.info(
            "{}: {} positions",
            name,
            Long.valueOf(with_tangents.getPositions().size64()));
          R2MeshTool.LOG.info(
            "{}: {} normals",
            name,
            Long.valueOf(with_tangents.getNormals().size64()));
          R2MeshTool.LOG.info(
            "{}: {} uvs",
            name,
            Long.valueOf(with_tangents.getUVs().size64()));
          R2MeshTool.LOG.info(
            "{}: {} tangents",
            name,
            Long.valueOf(with_tangents.getTangents().size64()));
          R2MeshTool.LOG.info(
            "{}: {} bitangents",
            name,
            Long.valueOf(with_tangents.getBitangents().size64()));
          R2MeshTool.LOG.info(
            "{}: {} unique vertices",
            name,
            Long.valueOf(with_tangents.getVertices().size64()));
          R2MeshTool.LOG.info(
            "{}: {} triangles",
            name,
            Long.valueOf(with_tangents.getTriangles().size64()));
          return Unit.unit();
        });
    }

    @Override
    public final void onError(
      final Optional<Throwable> e,
      final Path p,
      final String message)
    {
      switch (this.debug) {
        case LOG_TRACE:
        case LOG_DEBUG:
          if (e.isPresent()) {
            R2MeshTool.LOG.error("{}: {}: ", p, message, e.get());
          }
          break;
        case LOG_INFO:
        case LOG_WARN:
        case LOG_ERROR:
          break;
      }

      R2MeshTool.LOG.error("{}: {}", p, message);
      R2MeshTool.this.exit_code = 1;
    }
  }

  @Parameters(commandDescription = "Convert meshes")
  private final class CommandConvert extends CommandRoot
  {
    @Parameter(
      names = "-in",
      description = "Input file",
      required = true)
    private String in;

    @Parameter(
      names = "-in-format",
      description = "Input mesh format",
      converter = R2MeshFileFormatNameConverter.class)
    private R2MeshFileFormat in_format;

    @Parameter(
      names = "-out",
      description = "Output file",
      required = true)
    private String out;

    @Parameter(
      names = "-out-format",
      description = "Output mesh format",
      converter = R2MeshFileFormatNameConverter.class)
    private R2MeshFileFormat out_format;

    @Parameter(
      names = "-mesh",
      description = "Mesh name",
      required = true)
    private String name;

    CommandConvert()
    {

    }

    @Override
    public Unit call()
      throws Exception
    {
      super.call();

      final R2MeshConverterType conv = R2MeshConverter.newConverter(this);

      final Path in_name = Paths.get(this.in);
      if (this.in_format != null) {
        conv.loadMeshesFromFile(in_name, this.in_format);
      } else {
        conv.loadMeshesFromFileInferred(in_name);
      }

      final Path out_name = Paths.get(this.out);
      R2MeshTool.LOG.info("writing mesh {} to {}", this.name, out_name);

      if (this.out_format != null) {
        conv.writeMeshToFile(out_name, this.name, this.out_format);
      } else {
        conv.writeMeshToFileInferred(out_name, this.name);
      }

      return Unit.unit();
    }
  }


  @Parameters(commandDescription = "Check the data in a given file")
  private final class CommandCheck extends CommandRoot
  {
    @Parameter(
      names = "-file",
      description = "Input file",
      required = true)
    private String file;

    @Parameter(
      names = "-format",
      description = "Mesh format",
      converter = R2MeshFileFormatNameConverter.class)
    private R2MeshFileFormat format;

    CommandCheck()
    {

    }

    @Override
    public Unit call()
      throws Exception
    {
      super.call();

      final R2MeshConverterType conv = R2MeshConverter.newConverter(this);

      final Path file_name = Paths.get(this.file);
      if (this.format != null) {
        conv.loadMeshesFromFile(file_name, this.format);
      } else {
        conv.loadMeshesFromFileInferred(file_name);
      }

      final Map<String, R2MeshType> ms = conv.getMeshes();
      R2MeshTool.LOG.info("loaded {} meshes", Integer.valueOf(ms.size()));
      return Unit.unit();
    }
  }

  @Parameters(commandDescription = "Dump the data in a given file")
  private final class CommandDump extends CommandRoot
  {
    @Parameter(
      names = "-file",
      description = "Input file",
      required = true)
    private String file;

    @Parameter(
      names = "-format",
      description = "Mesh format",
      converter = R2MeshFileFormatNameConverter.class)
    private R2MeshFileFormat format;

    @Parameter(
      names = "-mesh",
      description = "Mesh name",
      required = true)
    private String name;

    CommandDump()
    {

    }

    @Override
    public Unit call()
      throws Exception
    {
      super.call();

      final R2MeshConverterType conv = R2MeshConverter.newConverter(this);

      final Path file_name = Paths.get(this.file);
      if (this.format != null) {
        conv.loadMeshesFromFile(file_name, this.format);
      } else {
        conv.loadMeshesFromFileInferred(file_name);
      }

      final Map<String, R2MeshType> ms = conv.getMeshes();
      R2MeshTool.LOG.info("loaded {} meshes", Integer.valueOf(ms.size()));

      if (ms.containsKey(this.name)) {
        final R2MeshType m = ms.get(this.name);
        m.matchMesh(
          this::dumpBasic,
          this::dumpTangents
        );
      } else {
        this.onError(Optional.empty(), file_name, "No such mesh");
      }

      return Unit.unit();
    }

    private Void dumpTangents(final R2MeshTangentsType m)
    {
      final BigList<PVectorI3D<R2SpaceObjectType>> p = m.getPositions();
      final BigList<PVectorI3D<R2SpaceObjectType>> n = m.getNormals();
      final BigList<PVectorI2D<R2SpaceTextureType>> u = m.getUVs();
      final BigList<PVectorI4D<R2SpaceObjectType>> t = m.getTangents();
      final BigList<PVectorI3D<R2SpaceObjectType>> b = m.getBitangents();
      final BigList<R2MeshTangentsVertexType> vv = m.getVertices();
      final BigList<R2MeshTriangleType> tt = m.getTriangles();

      for (long index = 0L; index < p.size64(); ++index) {
        final PVectorI3D<R2SpaceObjectType> v = p.get(index);
        System.out.printf(
          "[%d] position: %.6f %.6f %.6f\n",
          Long.valueOf(index),
          Double.valueOf(v.getXD()),
          Double.valueOf(v.getYD()),
          Double.valueOf(v.getZD()));
      }

      for (long index = 0L; index < n.size64(); ++index) {
        final PVectorI3D<R2SpaceObjectType> v = n.get(index);
        System.out.printf(
          "[%d] normal: %.6f %.6f %.6f\n",
          Long.valueOf(index),
          Double.valueOf(v.getXD()),
          Double.valueOf(v.getYD()),
          Double.valueOf(v.getZD()));
      }

      for (long index = 0L; index < u.size64(); ++index) {
        final PVectorI2D<R2SpaceTextureType> v = u.get(index);
        System.out.printf(
          "[%d] uv: %.6f %.6f\n",
          Long.valueOf(index),
          Double.valueOf(v.getXD()),
          Double.valueOf(v.getYD()));
      }

      for (long index = 0L; index < t.size64(); ++index) {
        final PVectorI4D<R2SpaceObjectType> v = t.get(index);
        System.out.printf(
          "[%d] tangent: %.6f %.6f %.6f %.6f\n",
          Long.valueOf(index),
          Double.valueOf(v.getXD()),
          Double.valueOf(v.getYD()),
          Double.valueOf(v.getZD()),
          Double.valueOf(v.getWD()));
      }

      for (long index = 0L; index < b.size64(); ++index) {
        final PVectorI3D<R2SpaceObjectType> v = b.get(index);
        System.out.printf(
          "[%d] bitangent: %.6f %.6f %.6f\n",
          Long.valueOf(index),
          Double.valueOf(v.getXD()),
          Double.valueOf(v.getYD()),
          Double.valueOf(v.getZD()));
      }

      for (long index = 0L; index < vv.size64(); ++index) {
        final R2MeshTangentsVertexType v = vv.get(index);
        System.out.printf(
          "[%d] vertex: pos: %d norm: %d uv: %d tan: %d bi: %d\n",
          Long.valueOf(index),
          Long.valueOf(v.getPositionIndex()),
          Long.valueOf(v.getNormalIndex()),
          Long.valueOf(v.getUVIndex()),
          Long.valueOf(v.getTangentIndex()),
          Long.valueOf(v.getBitangentIndex()));
      }

      for (long index = 0L; index < tt.size64(); ++index) {
        final R2MeshTriangleType v = tt.get(index);
        System.out.printf(
          "[%d] triangle: %d %d %d\n",
          Long.valueOf(index),
          Long.valueOf(v.getV0()),
          Long.valueOf(v.getV1()),
          Long.valueOf(v.getV2()));
      }

      return null;
    }

    private Void dumpBasic(final R2MeshBasicType m)
    {
      final BigList<PVectorI3D<R2SpaceObjectType>> p = m.getPositions();
      final BigList<PVectorI3D<R2SpaceObjectType>> n = m.getNormals();
      final BigList<PVectorI2D<R2SpaceTextureType>> u = m.getUVs();
      final BigList<R2MeshBasicVertexType> vv = m.getVertices();
      final BigList<R2MeshTriangleType> tt = m.getTriangles();

      for (long index = 0L; index < p.size64(); ++index) {
        final PVectorI3D<R2SpaceObjectType> v = p.get(index);
        System.out.printf(
          "[%d] position: %.6f %.6f %.6f\n",
          Long.valueOf(index),
          Double.valueOf(v.getXD()),
          Double.valueOf(v.getYD()),
          Double.valueOf(v.getZD()));
      }

      for (long index = 0L; index < n.size64(); ++index) {
        final PVectorI3D<R2SpaceObjectType> v = n.get(index);
        System.out.printf(
          "[%d] normal: %.6f %.6f %.6f\n",
          Long.valueOf(index),
          Double.valueOf(v.getXD()),
          Double.valueOf(v.getYD()),
          Double.valueOf(v.getZD()));
      }

      for (long index = 0L; index < u.size64(); ++index) {
        final PVectorI2D<R2SpaceTextureType> v = u.get(index);
        System.out.printf(
          "[%d] uv: %.6f %.6f\n",
          Long.valueOf(index),
          Double.valueOf(v.getXD()),
          Double.valueOf(v.getYD()));
      }

      for (long index = 0L; index < vv.size64(); ++index) {
        final R2MeshBasicVertexType v = vv.get(index);
        System.out.printf(
          "[%d] vertex: pos: %d norm: %d uv: %d\n",
          Long.valueOf(index),
          Long.valueOf(v.getPositionIndex()),
          Long.valueOf(v.getNormalIndex()),
          Long.valueOf(v.getUVIndex()));
      }

      for (long index = 0L; index < tt.size64(); ++index) {
        final R2MeshTriangleType v = tt.get(index);
        System.out.printf(
          "[%d] triangle: %d %d %d\n",
          Long.valueOf(index),
          Long.valueOf(v.getV0()),
          Long.valueOf(v.getV1()),
          Long.valueOf(v.getV2()));
      }

      return null;
    }
  }
}
