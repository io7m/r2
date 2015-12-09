#ifndef R2_DEFERRED_SURFACE_SHADER_H
#define R2_DEFERRED_SURFACE_SHADER_H

#include "R2DeferredSurfaceOutput.h"

//
// Interpolated surface data that all deferred shaders will receive.
//

struct R2_deferred_surface_data_t {
  vec4 position_eye;    // Eye-space surface position
  vec4 position_clip;   // Clip-space surface position
  float positive_eye_z; // Positive eye-space Z position
  vec2 uv;              // Object-space UV
  vec3 normal;          // Object-space normal
  vec3 tangent;         // Tangent vector
  vec3 bitangent;       // Bitangent vector
};

//
// Constant surface parameters that all deferred shaders will receive.
//

struct R2_deferred_surface_parameters_t {
  float depth_coefficient; // Logarithmic depth coefficient
  mat3x3 normal;           // Object-space to Eye-space normal matrix
};

//
// Input textures that all deferred shaders will receive.
//

struct R2_deferred_surface_textures_t {
  sampler2D albedo;    // RGBA albedo texture
  sampler2D normal;    // RGB normal map texture
  sampler2D specular;  // RGB specular map texture
  sampler2D emission;  // R emission level texture
};

#endif // R2_DEFERRED_SURFACE_SHADER_H
