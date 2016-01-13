#ifndef R2_DEFERRED_LIGHT_OUTPUT_H
#define R2_DEFERRED_LIGHT_OUTPUT_H

/// \file R2DeferredLightOutput.h
/// \brief Types describing calculated light contributions

/// The type of light values that all deferred light shaders calculate.

struct R2_deferred_light_output_t {
  /// The diffuse color/intensity (8-bit unsigned)
  vec3 diffuse;
  /// The specular color/intensity (8-bit unsigned)
  vec3 specular;
};

#endif // R2_DEFERRED_LIGHT_OUTPUT_H
