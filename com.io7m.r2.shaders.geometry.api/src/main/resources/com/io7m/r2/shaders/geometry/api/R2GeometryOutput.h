#ifndef R2_GEOMETRY_OUTPUT_H
#define R2_GEOMETRY_OUTPUT_H

/// \file R2GeometryOutput.h
/// \brief Types describing calculated surfaces

/// The type of surface details that all deferred geometry shaders are required to calculate.

struct R2_geometry_output_t {
  /// 8-bit unsigned normalized RGB color
  vec3  albedo;
  /// 8-bit unsigned normalized emission level
  float emission;
  /// Eye-space normal vector
  vec3 normal;
  /// 8-bit unsigned normalized RGB specular color
  vec3  specular;
  /// Specular exponent in the range `[0, 256]`
  float specular_exp;
  /// `True` if this particular surface fragment should be discarded
  bool discarded;
};

#endif // R2_GEOMETRY_OUTPUT_H

