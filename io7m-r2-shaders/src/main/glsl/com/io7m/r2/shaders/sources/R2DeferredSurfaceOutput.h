#ifndef R2_DEFERRED_SURFACE_OUTPUT_H
#define R2_DEFERRED_SURFACE_OUTPUT_H

/// \file R2DeferredSurfaceOutput.h
/// \brief Types describing calculated surfaces

/// The type of surface details that all deferred surface shaders are required to calculate.

struct R2_deferred_surface_output_t {
  /// 8-bit unsigned normalized RGB color
  vec3  albedo;
  /// 8-bit unsigned normalized emission level
  float emission;
  /// 8-bit unsigned normalized RGB specular color
  vec3  specular;
  /// Specular exponent in the range [0, 256]
  float specular_exp;
};

#endif // R2_DEFERRED_SURFACE_OUTPUT_H
