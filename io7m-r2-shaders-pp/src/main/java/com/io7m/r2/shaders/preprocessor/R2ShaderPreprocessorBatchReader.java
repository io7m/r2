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

package com.io7m.r2.shaders.preprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * The default implementation of the {@link R2ShaderPreprocessorBatchReaderType}
 * interface.
 */

public final class R2ShaderPreprocessorBatchReader
  implements R2ShaderPreprocessorBatchReaderType
{
  private R2ShaderPreprocessorBatchReader()
  {

  }

  /**
   * @return A new batch reader
   */

  public static R2ShaderPreprocessorBatchReaderType newReader()
  {
    return new R2ShaderPreprocessorBatchReader();
  }

  @Override
  public Map<String, R2ShaderPreprocessorProgramType> readFromStream(
    final InputStream s)
    throws IOException
  {
    final Map<String, R2ShaderPreprocessorProgramType> rm = new HashMap<>(128);
    final ObjectMapper om = new ObjectMapper();
    final JsonNode root = om.readTree(s);

    if (root.isObject()) {
      final ObjectNode o = (ObjectNode) root;
      final Iterator<String> iter = o.fieldNames();
      while (iter.hasNext()) {
        final String name = iter.next();
        final JsonNode raw = o.get(name);
        if (raw.isObject()) {
          final ObjectNode po = (ObjectNode) raw;
          final String v = po.get("vertex").textValue();
          final String f = po.get("fragment").textValue();
          final Optional<String> g;

          if (po.has("geometry")) {
            g = Optional.of(po.get("geometry").textValue());
          } else {
            g = Optional.empty();
          }

          final R2ShaderPreprocessorProgram p =
            R2ShaderPreprocessorProgram.of(name, v, g, f);
          rm.put(name, p);
        } else {
          final StringBuilder sb = new StringBuilder(128);
          sb.append("Expected: ");
          sb.append("A JSON object\n");
          sb.append("Got: ");
          sb.append(root.getNodeType());
          sb.append("\n");
          throw new IOException(sb.toString());
        }
      }
    } else {
      final StringBuilder sb = new StringBuilder(128);
      sb.append("Expected: ");
      sb.append("A JSON object\n");
      sb.append("Got: ");
      sb.append(root.getNodeType());
      sb.append("\n");
      throw new IOException(sb.toString());
    }

    return rm;
  }
}
