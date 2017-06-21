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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.smfj.core.SMFErrorType;
import com.io7m.smfj.core.SMFFormatDescription;
import com.io7m.smfj.core.SMFFormatVersion;
import com.io7m.smfj.core.SMFHeader;
import com.io7m.smfj.core.SMFTriangles;
import com.io7m.smfj.core.SMFWarningType;
import com.io7m.smfj.frontend.SMFFilterCommandFile;
import com.io7m.smfj.frontend.SMFParserProviders;
import com.io7m.smfj.frontend.SMFSerializerProviders;
import com.io7m.smfj.parser.api.SMFParseError;
import com.io7m.smfj.parser.api.SMFParserEventsBodyType;
import com.io7m.smfj.parser.api.SMFParserEventsHeaderType;
import com.io7m.smfj.parser.api.SMFParserEventsType;
import com.io7m.smfj.parser.api.SMFParserProviderType;
import com.io7m.smfj.parser.api.SMFParserSequentialType;
import com.io7m.smfj.probe.api.SMFVersionProbeControllerServiceLoader;
import com.io7m.smfj.probe.api.SMFVersionProbeControllerType;
import com.io7m.smfj.probe.api.SMFVersionProbed;
import com.io7m.smfj.processing.api.SMFFilterCommandContext;
import com.io7m.smfj.processing.api.SMFFilterCommandModuleResolver;
import com.io7m.smfj.processing.api.SMFFilterCommandModuleResolverType;
import com.io7m.smfj.processing.api.SMFFilterCommandModuleType;
import com.io7m.smfj.processing.api.SMFMemoryMesh;
import com.io7m.smfj.processing.api.SMFMemoryMeshFilterType;
import com.io7m.smfj.processing.api.SMFMemoryMeshProducer;
import com.io7m.smfj.processing.api.SMFMemoryMeshProducerType;
import com.io7m.smfj.processing.api.SMFMemoryMeshSerializer;
import com.io7m.smfj.processing.api.SMFProcessingError;
import com.io7m.smfj.serializer.api.SMFSerializerProviderType;
import com.io7m.smfj.serializer.api.SMFSerializerType;
import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.collection.SortedSet;
import javaslang.control.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;

import static com.io7m.jfunctional.Unit.unit;

/**
 * The main command line program.
 */

public final class R2MeshToolMain implements Runnable
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(R2MeshToolMain.class);
  }

  private final Map<String, CommandType> commands;
  private final JCommander commander;
  private final String[] args;
  private int exit_code;

  private R2MeshToolMain(
    final String[] in_args)
  {
    this.args = NullCheck.notNull(in_args, "Arguments");

    final CommandRoot r = new CommandRoot();
    final CommandFormats formats = new CommandFormats();
    final CommandFilter filter = new CommandFilter();
    final CommandListFilters list_filters = new CommandListFilters();
    final CommandProbe probe = new CommandProbe();

    this.commands = new HashMap<>(8);
    this.commands.put("filter", filter);
    this.commands.put("list-formats", formats);
    this.commands.put("list-filters", list_filters);
    this.commands.put("probe", probe);

    this.commander = new JCommander(r);
    this.commander.setProgramName("smf");
    this.commander.addCommand("filter", filter);
    this.commander.addCommand("list-formats", formats);
    this.commander.addCommand("list-filters", list_filters);
    this.commander.addCommand("probe", probe);
  }

  /**
   * The main entry point.
   *
   * @param args Command line arguments
   */

  public static void main(final String[] args)
  {
    final R2MeshToolMain cm = new R2MeshToolMain(args);
    cm.run();
    System.exit(cm.exitCode());
  }

  /**
   * @return The program exit code
   */

  public int exitCode()
  {
    return this.exit_code;
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
        LOG.info("Arguments required.\n{}", sb.toString());
        return;
      }

      final CommandType command = this.commands.get(cmd);
      command.call();

    } catch (final ParameterException e) {
      final StringBuilder sb = new StringBuilder(128);
      this.commander.usage(sb);
      LOG.error("{}\n{}", e.getMessage(), sb.toString());
      this.exit_code = 1;
    } catch (final Exception e) {
      LOG.error("{}", e.getMessage(), e);
      this.exit_code = 1;
    }
  }

  private interface CommandType extends Callable<Unit>
  {

  }

  private class CommandRoot implements CommandType
  {
    @Parameter(
      names = "-verbose",
      converter = R2MeshToolLogLevelConverter.class,
      description = "Set the minimum logging verbosity level")
    private R2MeshToolLogLevel verbose = R2MeshToolLogLevel.LOG_INFO;

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
      root.setLevel(this.verbose.toLevel());
      return unit();
    }
  }

  @Parameters(commandDescription = "List available filters")
  private final class CommandListFilters extends CommandRoot
  {
    CommandListFilters()
    {

    }

    @Override
    public Unit call()
      throws Exception
    {
      super.call();

      final SMFFilterCommandModuleResolverType r =
        SMFFilterCommandModuleResolver.create();

      for (final String module_name : r.available().keySet()) {
        final SMFFilterCommandModuleType module =
          r.available().get(module_name).get();
        for (final String command_name : module.parsers().keySet()) {
          System.out.print(module_name);
          System.out.print(":");
          System.out.print(command_name);
          System.out.println();
        }
      }

      return unit();
    }
  }

  @Parameters(commandDescription = "List supported formats")
  private final class CommandFormats extends CommandRoot
  {
    CommandFormats()
    {

    }

    @Override
    public Unit call()
      throws Exception
    {
      super.call();

      final String fmt_string = "%-6s : %-6s : %-32s : %-10s : %-6s : %s\n";

      System.out.printf(
        fmt_string,
        "# Name",
        "Suffix",
        "Mime type",
        "Version",
        "R/W",
        "Description");

      final ServiceLoader<SMFParserProviderType> parser_loader =
        ServiceLoader.load(SMFParserProviderType.class);
      final Iterator<SMFParserProviderType> parser_providers =
        parser_loader.iterator();

      while (parser_providers.hasNext()) {
        final SMFParserProviderType provider = parser_providers.next();
        final SMFFormatDescription format = provider.parserFormat();
        final SortedSet<SMFFormatVersion> versions = provider.parserSupportedVersions();
        versions.forEach(
          version ->
            System.out.printf(
              fmt_string,
              format.name(),
              format.suffix(),
              format.mimeType(),
              String.format(
                "%d.%d",
                Integer.valueOf(version.major()),
                Integer.valueOf(version.minor())),
              "read",
              format.description()));
      }

      final ServiceLoader<SMFSerializerProviderType> serializer_loader =
        ServiceLoader.load(SMFSerializerProviderType.class);
      final Iterator<SMFSerializerProviderType> serializer_providers =
        serializer_loader.iterator();

      while (serializer_providers.hasNext()) {
        final SMFSerializerProviderType provider = serializer_providers.next();
        final SMFFormatDescription format = provider.serializerFormat();
        final SortedSet<SMFFormatVersion> versions = provider.serializerSupportedVersions();
        versions.forEach(
          version ->
            System.out.printf(
              fmt_string,
              format.name(),
              format.suffix(),
              format.mimeType(),
              String.format(
                "%d.%d",
                Integer.valueOf(version.major()),
                Integer.valueOf(version.minor())),
              "write",
              format.description()));
      }

      return unit();
    }
  }

  @Parameters(commandDescription = "Filter mesh data")
  private final class CommandFilter extends CommandRoot
  {
    @Parameter(
      names = "-file-in",
      required = true,
      description = "The input file")
    private String file_in;

    @Parameter(
      names = "-format-in",
      description = "The input file format")
    private String format_in;

    @Parameter(
      names = "-file-out",
      description = "The output file")
    private String file_out;

    @Parameter(
      names = "-format-out",
      description = "The output file format")
    private String format_out;

    @Parameter(
      names = "-commands",
      required = true,
      description = "The filter commands")
    private String file_commands;

    @Parameter(
      names = "-source-directory",
      description = "The source directory")
    private String source_directory = System.getProperty("user.dir");

    CommandFilter()
    {

    }

    @Override
    public Unit call()
      throws Exception
    {
      super.call();

      final Optional<List<SMFMemoryMeshFilterType>> filters_opt =
        this.parseFilterCommands();

      if (!filters_opt.isPresent()) {
        R2MeshToolMain.this.exit_code = 1;
        return unit();
      }

      final List<SMFMemoryMeshFilterType> filters = filters_opt.get();

      final Optional<SMFParserProviderType> provider_parser_opt =
        SMFParserProviders.findParserProvider(
          Optional.ofNullable(this.format_in),
          this.file_in);

      if (!provider_parser_opt.isPresent()) {
        R2MeshToolMain.this.exit_code = 1;
        return unit();
      }

      final SMFParserProviderType provider_parser = provider_parser_opt.get();
      final Path path_in = Paths.get(this.file_in);

      final Optional<SMFMemoryMesh> mesh_opt =
        this.loadMemoryMesh(provider_parser, path_in);

      if (!mesh_opt.isPresent()) {
        R2MeshToolMain.this.exit_code = 1;
        return unit();
      }

      final SMFFilterCommandContext context =
        SMFFilterCommandContext.of(
          Paths.get(this.source_directory).toAbsolutePath(),
          Paths.get(this.file_commands).toAbsolutePath());

      final Optional<SMFMemoryMesh> filtered_opt =
        this.runFilters(context, filters, mesh_opt.get());

      if (!filtered_opt.isPresent()) {
        R2MeshToolMain.this.exit_code = 1;
        return unit();
      }

      final SMFMemoryMesh filtered = filtered_opt.get();

      if (this.file_out != null) {
        final Optional<SMFSerializerProviderType> provider_serializer_opt =
          SMFSerializerProviders.findSerializerProvider(
            Optional.ofNullable(this.format_out), this.file_out);

        if (!provider_serializer_opt.isPresent()) {
          R2MeshToolMain.this.exit_code = 1;
          return unit();
        }

        final SMFSerializerProviderType provider_serializer =
          provider_serializer_opt.get();
        final Path path_out = Paths.get(this.file_out);

        LOG.debug("serializing to {}", path_out);
        try (final OutputStream os = Files.newOutputStream(path_out)) {
          try (final SMFSerializerType serializer =
                 provider_serializer.serializerCreate(
                   provider_serializer.serializerSupportedVersions().last(),
                   path_out.toUri(),
                   os)) {
            SMFMemoryMeshSerializer.serialize(filtered, serializer);
          }
        } catch (final IOException e) {
          R2MeshToolMain.this.exit_code = 1;
          LOG.error("could not serialize mesh: {}", e.getMessage());
          LOG.debug("i/o error: ", e);
        }
      }

      return unit();
    }

    private Optional<SMFMemoryMesh> runFilters(
      final SMFFilterCommandContext context,
      final Seq<SMFMemoryMeshFilterType> filters,
      final SMFMemoryMesh mesh)
    {
      SMFMemoryMesh mesh_current = mesh;
      for (int index = 0; index < filters.size(); ++index) {
        final SMFMemoryMeshFilterType filter = filters.get(index);
        LOG.debug("evaluating filter: {}", filter.name());

        final Validation<List<SMFProcessingError>, SMFMemoryMesh> result =
          filter.filter(context, mesh_current);
        if (result.isValid()) {
          mesh_current = result.get();
        } else {
          result.getError().map(e -> {
            LOG.error("filter: {}: {}", filter.name(), e.message());
            return unit();
          });
          return Optional.empty();
        }
      }

      return Optional.of(mesh_current);
    }

    private Optional<SMFMemoryMesh> loadMemoryMesh(
      final SMFParserProviderType provider_parser,
      final Path path_in)
      throws IOException
    {
      final SMFMemoryMeshProducerType loader =
        SMFMemoryMeshProducer.create();

      try (final InputStream is = Files.newInputStream(path_in)) {
        try (final SMFParserSequentialType parser =
               provider_parser.parserCreateSequential(
                 loader, path_in.toUri(), is)) {
          parser.parse();
        }
        if (!loader.errors().isEmpty()) {
          loader.errors().forEach(e -> LOG.error(e.fullMessage()));
          R2MeshToolMain.this.exit_code = 1;
          return Optional.empty();
        }
      }
      return Optional.of(loader.mesh());
    }

    private Optional<List<SMFMemoryMeshFilterType>> parseFilterCommands()
      throws IOException
    {
      final Path path_commands = Paths.get(this.file_commands);
      final SMFFilterCommandModuleResolverType resolver =
        SMFFilterCommandModuleResolver.create();

      try (final InputStream stream = Files.newInputStream(path_commands)) {
        final Validation<List<SMFParseError>, List<SMFMemoryMeshFilterType>> r =
          SMFFilterCommandFile.parseFromStream(
            resolver,
            Optional.of(path_commands.toUri()),
            stream);
        if (r.isValid()) {
          return Optional.of(r.get());
        }

        r.getError().forEach(e -> LOG.error(e.fullMessage()));
        return Optional.empty();
      }
    }
  }

  @Parameters(commandDescription = "Probe a mesh file and display information")
  private final class CommandProbe extends CommandRoot
    implements SMFParserEventsType, SMFParserEventsHeaderType
  {
    @Parameter(
      names = "-file-in",
      required = true,
      description = "The input file")
    private String file_in;

    CommandProbe()
    {

    }

    @Override
    public Unit call()
      throws Exception
    {
      super.call();

      final Path path_in = Paths.get(this.file_in);

      final SMFVersionProbeControllerType controller =
        new SMFVersionProbeControllerServiceLoader();

      final Validation<Seq<SMFParseError>, SMFVersionProbed> r =
        controller.probe(() -> {
          try {
            return Files.newInputStream(path_in);
          } catch (final IOException e) {
            throw new UncheckedIOException(e);
          }
        });

      if (r.isInvalid()) {
        r.getError().forEach(e -> LOG.error(e.fullMessage()));
        R2MeshToolMain.this.exit_code = 1;
        return unit();
      }

      final SMFVersionProbed version = r.get();
      final SMFFormatDescription format = version.provider().parserFormat();
      System.out.printf(
        "Format: %s (%s) %s\n",
        format.name(),
        format.mimeType(),
        version.version().toHumanString());

      try (final InputStream stream = Files.newInputStream(path_in)) {
        try (final SMFParserSequentialType p = version.provider()
          .parserCreateSequential(this, path_in.toUri(), stream)) {
          p.parse();
        }
      }

      return unit();
    }

    @Override
    public void onStart()
    {

    }

    @Override
    public Optional<SMFParserEventsHeaderType> onVersionReceived(
      final SMFFormatVersion version)
    {
      return Optional.of(this);
    }

    @Override
    public void onFinish()
    {

    }

    @Override
    public void onError(
      final SMFErrorType e)
    {
      LOG.error(e.fullMessage());
    }

    @Override
    public void onWarning(
      final SMFWarningType w)
    {
      LOG.warn(w.fullMessage());
    }

    @Override
    public Optional<SMFParserEventsBodyType> onHeaderParsed(
      final SMFHeader header)
    {
      header.schemaIdentifier().ifPresent(
        schema -> System.out.printf("Schema: %s\n", schema.toHumanString()));

      System.out.printf(
        "Vertices: %s\n",
        Long.toUnsignedString(header.vertexCount()));

      final SMFTriangles triangles = header.triangles();
      System.out.printf(
        "Triangles: %s (size %s)\n",
        Long.toUnsignedString(triangles.triangleCount()),
        Integer.toUnsignedString(triangles.triangleIndexSizeBits()));

      System.out.printf("Attributes:\n");

      header.attributesInOrder().forEach(
        attr -> System.out.printf(
          "  %-32s %s %s %s\n",
          attr.name().value(),
          attr.componentType().getName(),
          Integer.toUnsignedString(attr.componentCount()),
          Integer.toUnsignedString(attr.componentSizeBits())));

      return Optional.empty();
    }
  }
}
