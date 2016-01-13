#ifndef R2_GBUFFER_OUTPUT_H
#define R2_GBUFFER_OUTPUT_H

/// \file R2GBufferOutput.h
/// \brief The G-Buffer format.

/// A type representing the values written to the G-Buffer.

struct R2_gbuffer_output_t {
  /// 8-bit unsigned normalized RGB color
  vec3  albedo;
  /// 8-bit unsigned normalized emission level
  float emission;
  /// Compressed 16-bit half-precision normals
  vec2  normal;
  /// 8-bit unsigned normalized RGB specular color
  vec3  specular;
  /// 8-bit unsigned normalized specular exponent
  float specular_exp;
  /// Logarithmic depth value
  float depth;
};

#endif // R2_GBUFFER_OUTPUT_H
