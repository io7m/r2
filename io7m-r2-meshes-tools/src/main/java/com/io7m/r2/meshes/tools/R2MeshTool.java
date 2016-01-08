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
import com.io7m.r2.meshes.R2MeshType;
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

    this.commands = new HashMap<>();
    this.commands.put("check", check);
    this.commands.put("convert", convert);

    this.commander = new JCommander(r);
    this.commander.setProgramName("meshtool");
    this.commander.addCommand("check", check);
    this.commander.addCommand("convert", convert);
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
      description = "Enable debugging")
    protected boolean debug;

    CommandRoot()
    {

    }

    @Override
    public Unit call()
      throws Exception
    {
      return Unit.unit();
    }

    @Override
    public final void onMeshLoaded(
      final Path p,
      final String name,
      final R2MeshType m)
    {
      R2MeshTool.LOG.info("{}: loaded {}", p, name);
    }

    @Override
    public final void onError(
      final Optional<Throwable> e,
      final Path p,
      final String message)
    {
      if (this.debug) {
        if (e.isPresent()) {
          R2MeshTool.LOG.error("{}: {}: ", p, message, e.get());
        }
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
}
