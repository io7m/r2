#ifndef R2_DEFERRED_SURFACE_OUTPUT_H
#define R2_DEFERRED_SURFACE_OUTPUT_H

//
// The type of surface details that all deferred surface shaders calculate.
//

struct R2_deferred_surface_output_t {
  vec3  albedo;       // 8-bit unsigned normalized RGB color
  float emission;     // 8-bit unsigned normalized emission level
  vec3  specular;     // 8-bit unsigned normalized RGB specular color
  float specular_exp; // Specular exponent in the range `[0, 256]`
};

#endif // R2_DEFERRED_SURFACE_OUTPUT_H
