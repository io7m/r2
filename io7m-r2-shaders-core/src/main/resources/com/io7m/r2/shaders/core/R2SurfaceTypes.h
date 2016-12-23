#ifndef R2_SURFACE_TYPES_H
#define R2_SURFACE_TYPES_H

/// \file R2SurfaceTypes.h
/// \brief Types for deferred surface shading

#include "R2SurfaceOutput.h"

/// Interpolated vertex data that all deferred surface shaders will receive.

struct R2_vertex_data_t {
  /// Eye-space surface position
  vec4 position_eye;
  /// Clip-space surface position
  vec4 position_clip;
  /// Positive eye-space Z position
  float positive_eye_z;
  /// Object-space UV
  vec2 uv;
  /// Object-space vertex normal
  vec3 normal_vertex;
  /// Tangent vector
  vec3 tangent;
  /// Bitangent vector
  vec3 bitangent;
};

/// Derived surface data that all deferred surface shaders will receive.

struct R2_surface_derived_t {
  /// Final uncompressed eye-space normal produced by bump/normal mapping
  vec3 normal_bumped;
};

/// Textures that are required by all surfaces

struct R2_surface_textures_t {
  /// RGB normal map texture
  sampler2D normal;
};

/// Matrices related to the rendered instance that all deferred surface shaders will receive.

struct R2_surface_matrices_instance_t {
  /// Object-space to Eye-space matrix
  mat4x4 transform_modelview;
  /// Object-space to Eye-space normal matrix
  mat3x3 transform_normal;
  /// UV matrix
  mat3x3 transform_uv;
};

#endif // R2_SURFACE_TYPES_H
