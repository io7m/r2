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

package com.io7m.r2.shaders.maven_plugin;

// CHECKSTYLE:OFF

import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessor;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorBatchReader;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorBatchReaderType;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorBatchRunner;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorBatchRunnerType;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorProgramType;
import com.io7m.r2.shaders.preprocessor.R2ShaderPreprocessorType;
import org.anarres.cpp.LexerException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Map;

@Mojo(name = "preprocess", requiresProject = true)
public final class R2PreprocessMojo extends AbstractMojo
{
  @Parameter(defaultValue = "${project}") private MavenProject project;

  /**
   * The directory that will contain source files.
   */

  @Parameter(defaultValue = "${project.basedir}/src/main/glsl")
  private File sourceDirectory;

  /**
   * The batch file.
   */

  @Parameter(defaultValue = "${project.basedir}/src/main/glsl/programs.json")
  private File batchFile;

  /**
   * The directory that will contain generated GLSL files.
   */

  @Parameter(defaultValue = "${project.build.directory}/generated-glsl")
  private File targetDirectory;

  /**
   * Construct a plugin.
   */

  public R2PreprocessMojo()
  {

  }

  @Override public void execute()
    throws MojoExecutionException, MojoFailureException
  {
    final Log logger = this.getLog();

    final R2ShaderPreprocessorType proc =
      R2ShaderPreprocessor.newPreprocessor(this.sourceDirectory);
    final R2ShaderPreprocessorBatchRunnerType runner =
      R2ShaderPreprocessorBatchRunner.newRunner(proc);
    final R2ShaderPreprocessorBatchReaderType reader =
      R2ShaderPreprocessorBatchReader.newReader();

    try (final InputStream is = Files.newInputStream(this.batchFile.toPath())) {
      final Map<String, R2ShaderPreprocessorProgramType> sources =
        reader.readFromStream(is);
      runner.process(this.targetDirectory.toPath(), sources);
    } catch (final NoSuchFileException e) {
      logger.error("File not found", e);
      throw new MojoFailureException("File not found", e);
    } catch (final IOException e) {
      logger.error("I/O error", e);
      throw new MojoExecutionException("I/O error", e);
    } catch (final LexerException e) {
      logger.error("Lexical error", e);
      throw new MojoFailureException("Lexical error", e);
    }
  }
}
