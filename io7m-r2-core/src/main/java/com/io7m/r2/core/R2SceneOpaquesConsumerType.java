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

package com.io7m.r2.core;

public interface R2SceneOpaquesConsumerType
{
  void onStart();

  void onShaderStart(
    R2ShaderType<?> s);

  void onMaterialStart(
    R2ShaderType<?> s,
    R2MaterialType<?> material);

  void onInstancesStartArray(
    R2InstanceType i);

  void onInstance(
    R2ShaderType<?> s,
    R2MaterialType<?> material,
    R2InstanceType i);

  void onMaterialFinish(
    R2ShaderType<?> s,
    R2MaterialType<?> material);

  void onShaderFinish(
    R2ShaderType<?> s);

  void onFinish();

}
